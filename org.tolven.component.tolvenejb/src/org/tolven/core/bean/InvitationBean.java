/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.core.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.jms.JMSException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.tolven.admin.ActivateInvitation;
import org.tolven.admin.AdministrativeDetail;
import org.tolven.admin.Details;
import org.tolven.admin.InvitationDetail;
import org.tolven.admin.JoinAccountInvitation;
import org.tolven.admin.JoinNewAccountInvitation;
import org.tolven.app.AdminAppQueueLocal;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.InvitationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.Sponsorship;
import org.tolven.core.entity.Status;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.bean.XMLBean;
import org.tolven.doc.entity.Invitation;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.security.LDAPLocal;
import org.tolven.session.TolvenSessionWrapperFactory;

/**
 * <p>Provide the capability to manage notifications backed by an internal structure called an
 * invitation. This mechanism provides the capability for a secure messaging capability over a public network which means that
 * the details of an email communication remain in Tolven's secure document storage with only a reference to the
 * details contained in an email message. Therefore, the user must be logged into Tolven in order to see the actual subject of an invitation.</p>
 * <p>Technically, the author of a particular notification (invitation) is free to include any content 
 * but in general, private information should not be sent but rather referenced via invitation id.
 * The invitation ID itself is not considered private since it conveys no meaning and has no intrinsic value
 * without the user being authenticated (logged in).</p> 
 * <p>A notification can and often does reference another object in Tolven. This reference is maintained at somewhat of an arms length. 
 * That is, the invitation can contain an identifier but there is no specific referential integrity maintained by the database.
 * Consider the notification </p> 
 * @author John Churin
 *
 */
/* Future:
 *  <p>Certain types of notifications should exhibit hysteresis. For example, say a physician is supposed to receive a notification when critical
 *  test results are received. If two or more critical lab results arrived within, say, 60 seconds, it would be inappropriate to notify the user of each individual event but rather,
 *  the notifications can be combined into a single notification message. To accomplish this, a small delay is introduced and at the end of that delay, all pending notifications are sent
 *  to that destination. Therefore, one cannot predict if a notification will be sent immediately or later. This delay is specified by the lag parameter.</p>
 */
@Stateless
@Local(InvitationLocal.class)
public class InvitationBean implements InvitationLocal {
	
	@PersistenceContext
	private EntityManager em;

	@Resource private EJBContext ejbContext;

	private TolvenAuthenticator authenticator;

    @EJB private LDAPLocal ldapBean;
    
    @EJB private ActivationLocal activationBean;
    
    @EJB private AccountDAOLocal accountBean;
    @EJB private TolvenPropertiesLocal propertiesBean;
    
    @EJB
    private AdminAppQueueLocal adminAppQueueBean;

    private TemplateGen templateGen = null;
    private static JAXBContext jc;
    
	private Logger logger = Logger.getLogger(this.getClass());
    /**
     * Create or use a JAXB context. We keep a map of already-used bindings in a static variable. 
     * @return A JAXB context.
     * @throws JAXBException
     */
    protected JAXBContext setupJAXBContext() throws JAXBException {
    	if (jc==null) {
			jc = JAXBContext.newInstance( "org.tolven.admin", XMLBean.class.getClassLoader() );
	    }
        return jc;
	}

    // We just use this class locally
    class TolvenAuthenticator extends Authenticator {
    	private String brand;
    	TolvenAuthenticator(String brand) {
    		this.brand = brand;
    	}
    	protected PasswordAuthentication getPasswordAuthentication(){
    		String user = propertiesBean.getBrandedProperty("tolven.mail.user", brand);
    		String password = propertiesBean.getBrandedProperty("tolven.mail.password", brand);
    		return new PasswordAuthentication(user, password);
    	}
    }

    public InvitationBean() {
		super();
	}
    public String getAccountProperty( ExpressionEvaluator ee, String name ) {
    	return (String) ee.evaluate("#{property['"+name + "']}");
    }
    
    /**
     * Get an attribute from expression evaluator
     * @param ee
     * @param attribute
     * @return
     */
    private String getAttribute( ExpressionEvaluator ee, String attribute ) {
    	try {
			String property = (String)ee.get(attribute + "Property");
			String value;
			if (property==null) {
				value = (String)ee.get(attribute);
			} else {
				value = getAccountProperty(ee, property);
			}
			if (value!=null) {
				// in any case, we always evaluate the value
				String valueFinal = (String) ee.evaluate(value);
				return valueFinal;
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException( "Error evaluating email attribute " + attribute, e);
		}
    }
    
    /**
     * Construct a mime message using the supplied properties
     * @param ee
     * @return The finished MIME message
     */
    public MimeMessage composeMessage( ExpressionEvaluator ee, Session session ) {
        try {
        	ee.pushContext();
	        String brand = (String)ee.get("brand");
        	
        	// Setup property variable, it not already present
        	// Try accountUser then account
        	if (ee.get("property")==null) {
            	AccountUser accountUser = (AccountUser) ee.get("accountUser");
            	Account account = (Account) ee.get("account");
            	if (accountUser!=null) {
            		ee.addVariable("property", accountUser.getBrandedProperty(brand));
            	} else if (account!=null) {
            		ee.addVariable("property", account.getBrandedProperty(brand));
            	} else {
            		ee.addVariable("property", propertiesBean.getBrandedProperties(brand));
            	}
        	}
        	MimeMessage msg = new MimeMessage(session);
        	// Sent date
        	Date now = (Date)ee.get("now");
        	if (now==null) {
        		now = new Date();
        	}
	        msg.setSentDate(now);
        	// Get the subject
			msg.setSubject(getAttribute( ee, "subject"));
	        String fromString = getAttribute(ee, "from");
	        if (fromString==null) {
	        	fromString = "\"" + propertiesBean.getBrandedProperty("tolven.mail.fromName", brand) + "\" <" + propertiesBean.getBrandedProperty("tolven.mail.from", brand) + ">";
	        }
	        // Only take the first address in the string.
	        msg.setFrom(InternetAddress.parse(fromString)[0]);
	        // To
	        String toString = getAttribute(ee, "to");
            if (toString == null) {
                toString = (String) TolvenSessionWrapperFactory.getInstance().getAttribute("mail");
            }
	        if (toString!=null) {
		        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toString));
	        }
	        // CC
	        String ccString = getAttribute(ee, "cc");
	        if (ccString!=null) {
		        msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ccString));
	        }
	        // BCC
	        String bccString = getAttribute(ee, "bcc");
	        if (bccString!=null) {
		        msg.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccString));
	        }
	        // Body
	        String mime = getAttribute(ee, "mime");
	        String body = getAttribute(ee, "body");
	        // Need to guess at the mime type if not provided
	        if (mime==null) {
	        	// If the body contains 
	        	if (body!=null && body.contains("<html")) {
		        	mime="text/html";
	        	} else {
		        	mime="text/plain";
	        	}
			}
	        if (body!=null) {
		        msg.setContent(body, mime);
	        }
	        ee.popContext();
			return msg;
		} catch (Exception e) {
			throw new RuntimeException( "Error composing mail message", e);
		}
    }
    /**
     * Send a message to the specified addressee.
     * Variables extracted from the supplied EE: from, to, cc, bcc, subject, and body.
     * First, the name+"Property" is looked up. For example, if bodyProperty is supplied, then it 
     * is the name of an accountProperty containing the body, not the body itself.
     * In either case, the resulting variable is then evaluated using the same expression evaluator.
     * If the xxxProperty form is used, then an account or an accountUser variable must be present in the ee.
     * If the to (or toProperty) variable yields a null, then to is assumed to be the email address of the logged in user. 
     * If the from (or fromProperty) variable yields a null, then the from is taken from system properties <code>tolven.mail.fromName</code> and  <code>tolven.mail.from</code>
     * @param ExpressionEvaluator containing attributes needed to construct and send the message
     */
    public void sendMessage( ExpressionEvaluator ee ) {
        try {
        	String brand = (String)ee.get("brand");
			authenticator = new TolvenAuthenticator(brand);
			/* TextConverter conv = */new TextConverter();
			Session session = Session.getInstance(getBrandedMailProperties(brand), authenticator);
			// session.setDebug(true);
			MimeMessage msg = composeMessage(ee, session);
			Transport transport = null;
			try {
				transport = session.getTransport();
				transport.connect();
				transport.sendMessage(msg, msg.getAllRecipients());
			} finally {
				if (transport != null) {
					transport.close();
				}
			}
			logger.info("Message sent to " + msg.getAllRecipients()[0].toString());
		} catch (Exception e) {
			throw new RuntimeException("Exception while sending message", e);
		}
    }
    
    /**
	 * Initialize our template instance generator
	 * 
	 * @throws IOException
	 */
    public void initialize(String brand) {
    	if (templateGen==null) {
		    templateGen = new TemplateGen( 
		    		propertiesBean.getBrandedProperty("tolven.invitation.host", brand), 
		    		Integer.parseInt(propertiesBean.getBrandedProperty("tolven.invitation.port", brand)), 
		    		propertiesBean.getBrandedProperty("tolven.invitation.root", brand));
    	}
    }

    //  @WebMethod(operationName="sendTestMessage", action="urn:SendTestMessage")
    /* (non-Javadoc)
	 * @see org.tolven.core.InvitationLocal#sendTestMessage(java.lang.String)
	 */
    public void sendTestMessage( String addressee,String emailFormat) throws Exception {
    	ExpressionEvaluator ee = new ExpressionEvaluator();
    	ee.addVariable("now", new Date());
    	ee.addVariable("subject", "Test Message");
    	ee.addVariable("body", "Test body");
    	sendMessage( ee );
//    	initialize();
//    	// We call back to run the template
//			String body = templateGen.expandTemplate( "/invitation/testMessage.jsf");
//	        sendNotifyMessage( "Test Message", new InternetAddress(addressee), body, emailFormat);
    }

    public void sendNotifyMessage( String subject, InternetAddress addressee, String message, String emailFormat) throws Exception {
    	   sendNotifyMessage( subject, addressee, message, emailFormat, null);
    		      	
    }
    /* (non-Javadoc)
	 * @see org.tolven.core.InvitationLocal#sendNotifyMessage(java.lang.String, javax.mail.internet.InternetAddress, java.lang.String)
	 */
    public void sendNotifyMessage( String subject, InternetAddress addressee, String message, String emailFormat, String brand) throws Exception {
    	initialize(brand);
    	authenticator = new TolvenAuthenticator(brand);
    	/*TextConverter conv = */ new TextConverter();
    	Session session = Session.getInstance(getBrandedMailProperties(brand), authenticator);
    	//	session.setDebug(true);
        MimeMessage msg = new MimeMessage(session);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setFrom(new InternetAddress(propertiesBean.getBrandedProperty("tolven.mail.from", brand), propertiesBean.getBrandedProperty("tolven.mail.fromName", brand)));
//		msg.setContent(message, "text/html");
        if (emailFormat.equals("html")) {
			msg.setContent(message, "text/html");
		}
		else {
			msg.setContent(new TextConverter().html2text(message), "text/plain");
		}
        Transport transport = null;
        try {
            transport = session.getTransport();
            transport.connect();
            transport.sendMessage(msg, new InternetAddress[] { addressee });
        } finally {
            if(transport != null) {
                transport.close();
            }
        }
        logger.info( "Notification sent to " + addressee.toString());
    }

    /**
     * Return a list of all active invitations.
     * @return
     */
    public List<Invitation> activeInvitations( ) {
    	return null;
    }

    public void invitationQueueTest(int count) throws JMSException {
        for (int c = 0; c < count; c++) {
            Invitation invitation = new Invitation();
            invitation.setDispatchAction("test");
            invitation.setTitle("This is a test #" + (c + 1));
            Date now = new Date();
            invitation.setCreated(now);
            queueInvitation(invitation);
        }
    }

    public void queueInvitation(Invitation invitation) throws JMSException {
        adminAppQueueBean.send(invitation);
    }

    protected Properties getBrandedMailProperties(String brand) {
        Properties properties = new Properties();
        String suffix = null;
        if (brand!=null) { 
            suffix = "." + brand;
        }
        for (String key : System.getProperties().stringPropertyNames()) {
            if (key.startsWith("mail.") || key.startsWith("tolven.mail.")) {
                if (suffix!=null && key.endsWith(suffix)) {
                    String shortKey = key.substring(0, key.length()-suffix.length());
                    properties.setProperty(shortKey, System.getProperty(key));
                } else {
                    if (null==properties.getProperty(key)) {
                        properties.setProperty(key, System.getProperty(key));
                    }
                }
            }
        }
        return properties;
    }
    
    /* (non-Javadoc)
	 * @see org.tolven.core.InvitationLocal#sendInvitation(org.tolven.doc.entity.Invitation)
	 */
    public void sendInvitation(Invitation invitation) throws Exception {
    	Account account = invitation.getAccount();
    	if (account==null) {
    		account = accountBean.findAccountTemplate("global");
    	}
    	String bodyProperty = "org.tolven.message." + invitation.getDispatchAction();
    	// If new-style is configured - which does not require calling the web tier - do it instead
    	if (account!=null && account.getProperty().get(bodyProperty)!=null) {
    		ExpressionEvaluator ee = new ExpressionEvaluator();
    		ee.addVariable("invitation", invitation);
    		ee.addVariable("account", account);
    		ee.addVariable("to", invitation.getTargetEmail());
    		ee.addVariable("subject", invitation.getTitle());
    		ee.addVariable("now", invitation.getCreated());
    		ee.addVariable("bodyProperty", bodyProperty);
    		ee.addVariable("brand", invitation.getBrand());
    		sendMessage( ee );
    		return;
    	} 
    	initialize(invitation.getBrand());
    	// We call back to run the template
    	String emailFormat = "";
	    String body = templateGen.expandTemplate( invitation.getTemplate()+"?invitationId="+invitation.getId());
	//    sb.append(invitation.getId());
	    InternetAddress addressee;
	    // If we have a target user, get that address, otherwise, use the addressee string
	    	addressee = new InternetAddress( invitation.getTargetEmail());
	    	emailFormat = invitation.getEmailFormat();
	    {	// Debugging
	    	StringBuffer sb = new StringBuffer( 300 );
			sb.append("[InvitationBean.sendInvitation] Invitation id " );
			sb.append( invitation.getId() );
			sb.append( " message body formatted: [" );
			if (body.length()> 400) {sb.append(body.substring(0, 400 )); sb.append("...");}
			else sb.append( body );
			sb.append( "]");
			logger.info( sb.toString());
	    }
	    sendNotifyMessage( invitation.getTitle(), addressee, body, emailFormat, invitation.getBrand());
    }

    public void sendfollowupInvitation(Invitation invitation) throws Exception {
    	initialize(invitation.getBrand());
    	String emailFormat="";
    	String body = templateGen.expandTemplate( "/invitation/activationExpired.jsf");
        InternetAddress addressee;
			addressee = new InternetAddress( invitation.getTargetEmail());
			emailFormat = invitation.getEmailFormat();
		sendNotifyMessage( "New Account Activation", addressee, body, emailFormat, invitation.getBrand());
	    }
    /**
     * @deprecated
     */
    public Invitation createInvitation( String email, InvitationDetail detail ) throws JAXBException, IOException {
    	return createInvitation( null, email, detail);
    }
    
    /**
     * Create a new invitation using the supplied details directed to the specified user
     * @throws IOException 
     * @throws JAXBException 
     */
    public Invitation createInvitation( String userName, String email, InvitationDetail detail ) throws JAXBException, IOException {
   	 	String activeStatus = Status.ACTIVE.value(); 
    	Invitation invite = new Invitation( );
    	if (userName==null) {
        	invite.setTargetUserName(email);
    	} else {
        	invite.setTargetUserName(userName);
    	}
    	invite.setTargetEmail(email);
        invite.setCreated( new Date() );
        invite.setStatus(activeStatus);
        JAXBContext jc = setupJAXBContext();
        Details topDetail = new Details();
        topDetail.getDetail().add( detail );
        JAXBElement<Details> top = (new org.tolven.admin.ObjectFactory()).createDetails(topDetail);
        Marshaller m = jc.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		ByteArrayOutputStream output = new ByteArrayOutputStream( );
        m.marshal( top, output );
        invite.setDetailContent( output.toByteArray() );
        em.persist( invite );
        logger.info( "Invitation detail: " + invite.getId() + new String(invite.getDetailContent()));
        return invite;
    }


	/**
	 * Get a Invitation by its internal ID
	 * @param invitationId the id of the Invitation
	 * @return the Invitation object
	 */
	public Invitation findInvitation( long invitationId ) {
		Invitation invitation = em.find( Invitation.class, invitationId );
        return invitation;
    }
	
	public List<Invitation> findOpenInvitations( TolvenUser user, Date now) {
		if (null==user) throw new IllegalArgumentException("findOpenInvitations: user must not be null" );
		String activeStatus = Status.fromValue("active").value(); 
		 String oldActiveStatus = Status.fromValue("ACTIVE").value(); 

        Query query = em.createQuery("SELECT i FROM Invitation i WHERE i.targetEmail = :uid  " +
        		"AND (i.expiration > :now or i.expiration IS NULL) " +
        		"AND (i.status = '" + activeStatus + "' or i.status = '" + oldActiveStatus + "') ");
        query.setParameter("uid", user.getLdapUID());
        query.setParameter("now", now);
		List<Invitation> items = query.getResultList();
		return items;
	}

	/**
	 * Execute an invitation and if the invitation is chained, carry out all chained invitations as well.
	 * we stop either when we're done or if there's an error thrown.
	 * This is a special bearer invitation process. We don't know intrinsically who the
	 * user is so we'll use the specified principal as the user. 
	 */
	public void executeBearerInvitation( long invitationId, Date now, String principal ) throws InvitationException {
		// Get the invitation
		Invitation invitation = em.find( Invitation.class, invitationId);
		if (invitation == null ) throw new InvitationException( "Invalid invitation", invitation );
	}
	
	/**
	 * Execute an invitation.
	 * @throws InvitationException
	 */
	public void executeInvitation( long invitationId, Date now, PublicKey userPublicKey ) throws InvitationException {
		Invitation invitation = findInvitation( invitationId );
		executeInvitation( invitation, now, userPublicKey );
	}
	
	/**
	 * Extract the details as a list of object trees from the invitation XML
	 * @param invitation
	 * @return
	 */
	public List<AdministrativeDetail>extractDetails( Invitation invitation ) {
        JAXBElement<Details> element = null;
        try {
            JAXBContext jc = setupJAXBContext();
            Unmarshaller u = jc.createUnmarshaller();
            element = (JAXBElement<Details>)u.unmarshal( new StreamSource( new ByteArrayInputStream( invitation.getDetailContent()) ) );
        } catch (JAXBException e) {
            throw new RuntimeException("Could not obtain invitation details", e);
        }
//        logger.info("Unmarshal: " + element.getClass().getName() + ", " + element.getValue().getClass().getName());
        Details detailGraph = (Details) element.getValue();
		List<AdministrativeDetail> details = detailGraph.getDetail();
		return details;
	}
	
	/**
	 * Given an invitation id, return the activation details contained within, if any, otherwise
	 * return null. This method is pretty special purpose for the UI and is intended for display only,
	 * not for executing the invitation.
	 * @param invitationId
	 * @return
	 * @throws JAXBException 
	 */
	public ActivateInvitation extractActivationDetails( Invitation invitation ) throws JAXBException {
		if (invitation==null) return null;
		List<AdministrativeDetail> details = extractDetails(invitation);
        for (AdministrativeDetail detail : details) {
			if (detail instanceof ActivateInvitation) {
				return (ActivateInvitation) detail;
			}
        }
        return null;
	}

	/**
	 * Execute an invitation.
	 * @throws InvitationException 
	 */
	public void executeInvitation( Invitation invitation, Date now, PublicKey userPublicKey ) throws InvitationException {
//		Invitation invitation = findInvitation( invitationId );
		logger.info( "Executing invitation id: " +  invitation.getId() + " Title: " + invitation.getTitle() + " targetEmail: " + invitation.getTargetEmail());
        logger.info( " detail: " + new String(invitation.getDetailContent()));
        if (invitation.getTargetUserName()==null) {
        	invitation.setTargetUserName(invitation.getTargetEmail());
        }
		// Get the details
        List<AdministrativeDetail> details = extractDetails(invitation);
        for (AdministrativeDetail detail : details) {
			if (detail instanceof JoinNewAccountInvitation)
			{
				JoinNewAccountInvitation jna = (JoinNewAccountInvitation)detail;
				// TODO: Localization issue - Account title is seeded with English
				Account account = accountBean.createAccount( jna.getAccountType(), "New Account", null );
				logger.info( "Created account: " + account.getId());
				// Note, the user automatically gets account permission since they are the only user on that new account.
				accountBean.addAccountUser( account, activationBean.findUser( invitation.getTargetUserName()), now, true, userPublicKey);
				invitation.setStatus(Status.COMPLETED.value());
			}
			if (detail instanceof JoinAccountInvitation)
			{
				JoinAccountInvitation ja = (JoinAccountInvitation)detail;
				Account account = accountBean.findAccount( ja.getAccountId() );
				// Note, the user may or may not be given account permission depending on the wishes of the inviter.
				accountBean.addAccountUser( account, activationBean.findUser( invitation.getTargetUserName()), now, ja.isAccountPermission(), userPublicKey);
				invitation.setStatus(Status.COMPLETED.value());
			}
			if (detail instanceof ActivateInvitation)
			{
				ActivateInvitation activate = (ActivateInvitation) detail; 
				// If the invitation is now obsolete, we need to delete the LDAP entry.
				if (invitation.getStatus().equals(Status.OBSOLETE.value())) {
					invitation.setStatus(Status.OBSOLETE.value());
					ldapBean.deleteUser(activate.getPrincipal());
				} else {
					String principal =(String)TolvenSessionWrapperFactory.getInstance().getPrincipal();
			        if (!principal.equals(activate.getPrincipal())) {
			        	throw new InvitationException( "Invitation does not match logged in user");
			        }
			        // OK, we're good to go. Create the user and mark the invitation as used
			        TolvenUser user = activationBean.findUser(principal);
			        //TODO: If TolvenUser is found, the expected status is NEW_LOGIN. Should we check here, and if not what expected?
			        if(user == null)
			            user = activationBean.createTolvenUser( principal, now );
			    	user.setDemoUser(false);
			    	if (activate.getReferenceCode()!=null) {
						Sponsorship sponsorship = activationBean.findSponsorship(activate.getReferenceCode());
				    	user.setSponsorship(sponsorship);
			    	}
					invitation.setStatus(Status.COMPLETED.value());
				}
			}
		}
		em.merge( invitation );
	}
	
    /**
     * If the userID isn't registered within an hour, cancel the activation
     */ 
    public void followup( Invitation invitation ) {
    	if (invitation.getExpiration()!=null) {
    		ejbContext.getTimerService().createTimer( invitation.getExpiration(), new Long(invitation.getId()));
    	}
    }

    /**
     * Cancel an activation that has not been responded to within an hour or so.
     * @param UID
     * @throws IOException 
     * @throws JAXBException 
     * @throws NamingException 
     * @throws InvitationException 
     * @throws GeneralSecurityException 
     */
    @Timeout
    public void timeout( Timer timer ) throws JAXBException, IOException, NamingException, InvitationException, GeneralSecurityException {
    	long invitationId = (Long) timer.getInfo();
    	Invitation invitation = findInvitation(invitationId);
    	logger.info( "Checking " + invitation.getId() + " for activation");
    	// If this invitation is still active, we'll need to obsolete it.
		if (invitation.getStatus().equals(Status.ACTIVE.value())) {
			invitation.setStatus(Status.OBSOLETE.value());
			executeInvitation(invitation, new Date(), null);
			try
			   {
			    sendfollowupInvitation(invitation);
			   }
			   catch(Exception e)
			   {
				   logger.error("----------DELETED USER--------------------");
			   }
		}
    }



}
