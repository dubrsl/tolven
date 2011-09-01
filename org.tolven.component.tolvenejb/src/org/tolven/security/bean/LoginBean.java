package org.tolven.security.bean;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.admin.ActivateInvitation;
import org.tolven.core.InvitationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.bean.InvitationException;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.Sponsorship;
import org.tolven.core.entity.Status;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.entity.Invitation;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.CertificateHelper;
import org.tolven.security.LDAPLocal;
import org.tolven.security.LoginLocal;
import org.tolven.security.LoginRemote;
import org.tolven.security.TolvenPerson;
import org.tolven.session.TolvenSessionWrapperFactory;
@Stateless
@Local(LoginLocal.class)
@Remote(LoginRemote.class)
public class LoginBean implements LoginLocal, LoginRemote {
	@PersistenceContext private EntityManager em;
	
	@Resource EJBContext ejbContext;
	
    @EJB private InvitationLocal invitationBean;
    @EJB private LDAPLocal ldapBean;
    @EJB private TolvenPropertiesLocal propertiesBean;
    
    public LDAPLocal getLdapBean() {
    	if (ldapBean==null) throw new IllegalStateException( "LDAPBean must not be null" );
        return ldapBean;
    }

    /**
     * Given the principal's name, get the TolvenUser object. the parameter must be converted to lower case to ensure we find a match.
     */
     public TolvenUser findUser( String principal ) {
    	 //Support both types of active status;    	
  	   String activeStatus = Status.ACTIVE.value();
	   String oldActiveStatus = Status.OLD_ACTIVE.value(); 
  		//Activating should be replaced by New
 		String activatingStatus = Status.fromValue("ACTIVATING").value();
 		String newStatus = Status.NEW.value();
 		String select = "SELECT DISTINCT u FROM TolvenUser u WHERE u.ldapUID = :principal " +
        "and ( u.status = '";
 		select += oldActiveStatus + "' or u.status = '" + activeStatus + "' or u.status = '" + newStatus + "' or u.status = '" + activatingStatus + "' or u.status = '" + "') ";
    	 Query query = em.createQuery(select);
        query.setParameter("principal", principal.toLowerCase());
        query.setMaxResults(2);
        List<TolvenUser> items = query.getResultList();
        if (items.size()!=1) return null;
        return items.get(0);
     }

    public AccountUser findAccountUser(long accountUserId) {
        return em.find(AccountUser.class, accountUserId);
    }
    
    /**
     * Register a new user with an activation step that validates the userId as a valid eMail address.
     * <ol>
     * <li>Persist the new TolvenUser object</li>
     * <li>Create an LDAP entry</li>
     * <li>Create a TolvenUser Object</li>
     * <li>Create an invitation</li>
     * <li>Create an email referencing the invitation</li>
     * </ol>
     */
    public void register(  TolvenPerson tp, Date now ) throws Exception {
    	register( tp, now, null);
    }
    
    public void createActivateInvitation( ExpressionEvaluator ee ) throws Exception {
    	TolvenPerson tp = (TolvenPerson) ee.get("tp");
    	Date now = (Date) ee.get("now");
        ActivateInvitation detail = new ActivateInvitation( );
    	String rc = (String) ee.get("referenceCode");
//      detail.setExpirationTime( DatatypeFactory.newInstance().newXMLGregorianCalendar( t));
        detail.setPrincipal( tp.getUid());
        // Create Credentials in LDAP
        CertificateHelper helper = new CertificateHelper();
        helper.createCredentials(tp);
        detail.setReferenceCode( rc );
    	Invitation invitation = invitationBean.createInvitation(tp.getUid(), detail);
    	// Create an invitation
        invitation.setTitle( "New Account Activation");
        invitation.setDispatchAction("activate");
        invitation.setTemplate("/invitation/activate.jsf");
        invitation.setEmailFormat(tp.getEmailFormat());
        invitation.setAccount( null );	// No owner for registrations (could be sponsor)
        String expiration = propertiesBean.getProperty("tolven.register.expiration");
        if (expiration!=null) {
        	long elapsed = Long.parseLong(expiration)*1000;
        	if (elapsed > 0) {
        		invitation.setExpiration( new Date( now.getTime()+ (Long.parseLong(expiration)*1000) ) );
        	}
        }
        TolvenLogger.info( "Queueing invitation to " + invitation.getTargetEmail(), LoginBean.class);
        invitationBean.followup(invitation);
        // Once sent, the invitation state will be updated to reflect completion. Workflow, man. Workflow.
        invitationBean.queueInvitation( invitation );
    }
    
    /**
     * Register a new user with an activation step that validates the userId as a valid eMail address.
     * <ol>
     * <li>Persist the new TolvenUser object</li>
     * <li>Create an LDAP entry</li>
     * <li>Create a TolvenUser Object</li>
     * <li>Create an invitation</li>
     * <li>Create an email referencing the invitation</li>
     * </ol>
     * @param tp An object describing the person to receive the invitation
     * @param now The current time
     * @param localaddr The IP address addressed by the end user (if any). Used to assist in resolving properties.
     */
    public void register( ExpressionEvaluator ee ) throws Exception {
    	TolvenPerson tp = (TolvenPerson) ee.get("tp");
    	String rc = (String) ee.get("referenceCode");
    	if (rc!=null) {
			ee.addVariable("sponsor", findSponsorship(rc));
    	}
    	createActivateInvitation(ee);
        // Make the LDAP entry
        ldapBean.addPerson( tp );
    }
    
    /**
     * Register a new user with an activation step that validates the userId as a valid eMail address.
     * <ol>
     * <li>Persist the new TolvenUser object</li>
     * <li>Create an LDAP entry</li>
     * <li>Create a TolvenUser Object</li>
     * <li>Create an invitation</li>
     * <li>Create an email referencing the invitation</li>
     * </ol>
     * @param tp An object describing the person to receive the invitation
     * @param now The current time
     * @param localaddr The IP address addressed by the end user (if any). Used to assist in resolving properties.
     */
    public void register(  TolvenPerson tp, Date now, String localAddr ) throws Exception {
//		ExpressionEvaluator ee = new ExpressionEvaluator();
//    	Account account = accountBean.findAccountTemplate("global");
//		ee.addVariable("account", account);
//		ee.addVariable("tp", tp);
//		ee.addVariable("referenceCode", tp.getReferenceCode());
//		ee.addVariable("to", tp.getUid());
//    	ee.addVariable("bodyProperty", "org.tolven.message.activate");
//		ee.addVariable("subject", "New Account Activation");
//		ee.addVariable("now", now);
//		ee.addVariable("brand", localAddr);
//		register( ee );
//		invitationBean.sendMessage( ee );
    	String rc = tp.getReferenceCode();
    	if (rc!=null) {
			findSponsorship(rc);
    	}
        ActivateInvitation detail = new ActivateInvitation( );
//      detail.setExpirationTime( DatatypeFactory.newInstance().newXMLGregorianCalendar( t));
        detail.setPrincipal( tp.getUid());
        // Create Credentials in LDAP
        CertificateHelper helper = new CertificateHelper();
        helper.createCredentials(tp);
        detail.setReferenceCode( rc );
    	Invitation invitation = invitationBean.createInvitation(tp.getUid(), tp.getPrimaryMail(), detail);
    	// Create an invitation
        invitation.setTitle( "New Account Activation");
        invitation.setDispatchAction("activate");
        invitation.setTemplate("/invitation/activate.jsf");
        invitation.setEmailFormat(tp.getEmailFormat());
        invitation.setBrand(localAddr);
        invitation.setAccount( null );	// No owner for registrations (could be sponsor)
        String expiration = propertiesBean.getProperty("tolven.register.expiration");
        if (expiration!=null) {
        	long elapsed = Long.parseLong(expiration)*1000;
        	if (elapsed > 0) {
        		invitation.setExpiration( new Date( now.getTime()+ (Long.parseLong(expiration)*1000) ) );
        	}
        }
        TolvenLogger.info( "Queueing invitation to " + invitation.getTargetEmail(), LoginBean.class);
        // Once sent, the invitation state will be updated to reflect completion. Workflow, man. Workflow.
        invitationBean.queueInvitation( invitation );
        // Make the LDAP entry
        ldapBean.addPerson( tp );
    }
    
    /**
     * Activate a user. Return false if invitation has expired or logged in user does not match target user of invitation.
     * While an invitation is marked as used, there is no harm in supplying a used invitation id. 
     * This can happen if the user decides to bookmark the URL from an invitation eMail.
     * @param user An existing Tolven user to be activated
     * @param invitation the invitation boolean indicating if this user has account administration permission
     * @param now A transactional now timestamp
     * @return A new AccountUser object
     */
//    @WebMethod(operationName="activate", action="urn:Activate")
    public boolean activate( String principal, long invitationId, Date now, PublicKey userPublicKey) {
    	Invitation invitation = invitationBean.findInvitation( invitationId );
        if (invitation==null) return false;
        // If the expiration date is specified but it has past, then fail.
        if (invitation.getExpiration()!=null && invitation.getExpiration().before(now)) {
            return false;
        }
        try {
            invitationBean.executeInvitation(invitation, now, userPublicKey);
        } catch (InvitationException e) {
            throw new RuntimeException("Could not activate invitation", e);
        }
        return true;
    }
    
    /**
     * Reserve a new userId in LDAP. This user will not be able to login until activated with {@link #activateReservedUser(String, Date)}
     * @param tp
     * @param now
     * 
     */
    public void reserveUser(TolvenPerson tp, Date now) {
        // Make the LDAP entry
        try {
			addToLDAP(tp);
		} catch (Exception e) {
			throw new RuntimeException( "Unable to reserve new user " + tp, e);
		}
    }
    
    /**
     * Activate a reserved user. The user must first be
     * reserved using {@link #reserveUser(TolvenPerson, Date)}. The effect of calling this method
     * is to create a new TolvenUSer entity in the database.
     * <p>Note: This function requires that the logged in user have the tolvenAdmin role.
     * In other words, the logged-in user cannot self-activate using this method.</p>
	 * <p>This process does not require an invitation.</p>
     * @param principal User's ID.
     * @param now The current time
     * @return The newly create (or updated) TolvenUser entity
     */
    public TolvenUser activateReservedUser( String principal, Date now) {
        TolvenUser user = findUser(principal);
        if(user == null) {
            user = createTolvenUser( principal, now );
        }
        user.setStatus(Status.ACTIVE.value());
        return user;
    }
    
    /**
     * Cancel Reservation for a new user. The user must first be
     * reserved using {@link #reserveUser(TolvenPerson, Date)}. The effect of calling this method
     * is to remove this user from LDAP. The method fails if the user has already been activated (added to
     * the database). In other words, the user must be in LDAP and not in TolvenUser.
     * Note: This function requires that the logged in user have the tolvenAdmin role.
     * This method should be use to cancel an invitation-based registrations.
     * @param principal User's ID.
     * @param now The current time
     */
    public void cancelReservation( String principal, Date now) {
        TolvenUser user = findUser(principal);
        if (user!=null) {
        	throw new RuntimeException( "User reservation for " + principal + " cannot be cancelled: User has already been activated");
        }
    	ldapBean.deleteUser(principal);
    }

    /**
     * Used for test, demo only. Register and immediately activate the user without sending an email. The user id does not need to be a valid email address.
     * The demoUser flag is set in the user account. If inLDAP is false, then it is assumed that the user already exists in LDAP
     * @param tp A TolvenPerson object representing the LDAP attributes of this user (A TolvenPerson is a transient object)
     * @param now A transactional now timestamp
     * @param inLDAP
     * @return A new TolvenUser object
     * @throws NamingException 
     */
    @Deprecated
    public TolvenUser registerAndActivate(TolvenPerson tp, Date now) throws IOException, GeneralSecurityException, NamingException {
        // Make the LDAP entry if it does not already exist
        if(!ldapBean.entryExists(tp.getUid())) {
            addToLDAP(tp);
        }
        // Create a new user object
        TolvenUser user = findUser(tp.getUid());
        if (user == null) {
            user = createTolvenUser(tp.getUid(), now);
            String rc = tp.getReferenceCode();
            if (rc != null) {
                Sponsorship sponsorship = findSponsorship(rc);
                user.setSponsorship(sponsorship);
            }
            user.setDemoUser(true);
            user.setEmailFormat(tp.getEmailFormat());
            return user;
        } else {
            throw new RuntimeException(tp.getUid() + " is already registered");
        }
    }

    /**
     * Used for test, demo only. Activate the user without sending an email. The user id does not need to be a valid email address.
     * The demoUser flag is set in the user account.
     * @param tp A TolvenPerson object representing the LDAP attributes of this user (A TolvenPerson is a transient object)
     * @param now A transactional now timestamp
     * @return A new TolvenUser object
     */
    public TolvenUser activate(TolvenPerson tp, Date now) {
        TolvenUser user = findUser(tp.getUid());
        if (user == null) {
            user = createTolvenUser(tp.getUid(), now);
            String rc = tp.getReferenceCode();
            if (rc != null) {
                Sponsorship sponsorship = findSponsorship(rc);
                user.setSponsorship(sponsorship);
            }
            user.setDemoUser(true);
            user.setEmailFormat(tp.getEmailFormat());
            return user;
        } else {
            throw new RuntimeException(tp.getUid() + " is already activated");
        }
    }

    private void addToLDAP(TolvenPerson tp) {
    	propertiesBean.setAllProperties();
    	if (tp.getUserCertificate() == null && tp.getUserPKCS12() == null) {
            getLdapBean().createCredentials(tp);
        } else if ((tp.getUserCertificate() == null && tp.getUserPKCS12() != null)) {
            throw new RuntimeException("TolvenPerson with ID: " + tp.getUid() + " has a missing user certificate");
        } else if ((tp.getUserCertificate() != null && tp.getUserPKCS12() == null)) {
            throw new RuntimeException("TolvenPerson with ID: " + tp.getUid() + " has a missing user PKCS12 keystore");
        }
        getLdapBean().addPerson(tp);
    }

    /**
     * Given what is suspected to be a valid sponsorship reference code, return the Sponsorship 
     * This method fails loudly (throws an object not found exception) if the reference code is not found.
     * This should be sufficient to rollback a transaction intended to create a new user 
     * with an invalid reference code.
     * @throws NoResultException if the referenceCode is not found
     * @param referenceCode
     * @return
     */
    public Sponsorship findSponsorship( String referenceCode ) {
    	Query q = em.createQuery("SELECT s FROM Sponsorship s WHERE s.referenceCode = :rc");
    	q.setParameter("rc", referenceCode);
    	Sponsorship s = (Sponsorship) q.getSingleResult();
    	return s;
    }

    /**
     * Create a new Tolven User
     * @param principal
     * @return new TolvenUser object properly initialized and persisted
     */
    public TolvenUser createTolvenUser( String principal, Date now ) {
        TolvenLogger.info("createTolvenUser: " + principal, LoginBean.class);
        TolvenUser user = new TolvenUser();
        user.setLdapUID( principal );
 		String activeStatus = Status.ACTIVE.value();
        user.setStatus( activeStatus);
        user.setLastLogin( null );    // Last login is null, never logged in before this
        user.setCreation( now ); 
        em.persist( user );
        return user;
    }

    public void persistModifiedTolvenUser(TolvenUser user) {
        em.merge(user);
    }

    /**
     * Add or update TolvenPerson to LDAP. If the user does not already exist in LDAP, then create it.
     */
    public void createOrUpdateTolvenPerson(TolvenPerson tp) {
        /*
         * Create or update user in LDAP
         */
        TolvenPerson currentTolvenPerson = getLdapBean().findTolvenPerson(tp.getUid());
        if(currentTolvenPerson == null) {
            /*
             * Create LDAP entry and leave DB entry alone
             */
            addToLDAP(tp);
        } else {
            if((tp.getUserCertificate() == null && tp.getUserPKCS12() == null)) {
                getLdapBean().updatePerson(tp);
            } else if ((tp.getUserCertificate() == null && tp.getUserPKCS12() != null)) {
                throw new RuntimeException("TolvenPerson with ID: " + tp.getUid() + " has a missing user certificate");
            } else if ((tp.getUserCertificate() != null && tp.getUserPKCS12() == null)) {
                throw new RuntimeException("TolvenPerson with ID: " + tp.getUid() + " has a missing user PKCS12 keystore");
            }
        }
    }

    /**
     * Return true if the user with uid exists in the database
     */
    public boolean userExistsInDB(String uid) {
        return findUser(uid) != null;
    }

    /**
     * Find TolvenPerson in LDAP by its principal name, which matches the LDAP UID.
     */
    public TolvenPerson findTolvenPerson(String principalName) {
        return getLdapBean().findTolvenPerson(principalName);
    }
    
    /**
     * Remote-friendly method to verify the user's password. The user must be logged in.
     * @param password Must match existing password
     */
    public void verifyUserPassword(char[] password) {
    	String principal = null;
    	try {
    		principal = (String)TolvenSessionWrapperFactory.getInstance().getPrincipal();
			this.ldapBean.verifyPassword(principal, password);
		} catch (Exception e) {
			throw new RuntimeException("Error changing password for " + principal, e );
		}
    }
    
    /**
     * Remote-friendly method to change the user's password
     */
    public void changeUserPassword(char[] oldPassword, char[] newPassword) {
    	String principal = null;
    	try {
    		principal = (String)TolvenSessionWrapperFactory.getInstance().getPrincipal();
			this.ldapBean.changeUserPassword(principal, oldPassword, newPassword);
		} catch (Exception e) {
			throw new RuntimeException("Error changing password for " + principal, e );
		}
    }

}
