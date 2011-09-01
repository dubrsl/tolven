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

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.core.ActivationLocal;
import org.tolven.core.ActivationRemote;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.AccountUserRole;
import org.tolven.core.entity.Sponsorship;
import org.tolven.core.entity.Status;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.LDAPLocal;
import org.tolven.security.TolvenPerson;

//import javax.jws.WebService;
/**
 * ActivationBean provides functions associated with activating users and accounts.
 * Created Apr 6, 2006 10:39:34 AM
 * 
 * @author John Churin
 */

//@WebService( name="Activation", serviceName="ActivationService", targetNamespace="http://tolven.org/activation")
@Stateless
@Local(ActivationLocal.class)    
@Remote(ActivationRemote.class)  
public class ActivationBean implements ActivationLocal, ActivationRemote {
    @PersistenceContext private EntityManager em;
    
    @Resource EJBContext ejbContext;

    @EJB private LDAPLocal ldapBean;
    
	@EJB TolvenPropertiesLocal propertyBean;

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
        String select = "SELECT  u FROM TolvenUser u WHERE u.ldapUID = :principal " +
        "and ( u.status = '";
        select += oldActiveStatus + "' or u.status = '" + activeStatus + "' or u.status = '" + newStatus + "' or u.status = '" + activatingStatus + "' or u.status = '" + "') ";
         Query query = em.createQuery(select);
        query.setParameter("principal", principal.toLowerCase());
        query.setMaxResults(2);
        List<TolvenUser> items = query.getResultList();
        if (items.size()!=1) return null;
        return items.get(0);
     }
    /**
     * Create a new Tolven User
     * @param principal
     * @return new TolvenUser object properly initialized and persisted
     */
    public TolvenUser createTolvenUser( String principal, Date now ) {
        TolvenUser user = new TolvenUser();
        user.setLdapUID( principal );
        String activeStatus = Status.fromValue("active").value();
        user.setStatus( activeStatus);
        user.setLastLogin( null );    // Last login is null, never logged in before this
        user.setCreation( now ); 
        em.persist( user );
        return user;
    }
    
    /**
     * Return the TolvenUser object given it's uinque id
     * @param tolvenUserId
     * @return
     */
    public TolvenUser findTolvenUser( long tolvenUserId ) {
        return em.find(TolvenUser.class, tolvenUserId);
    }


    /**
     * Register a new user with an activation step that validates the userId as a valid eMail addresss.
     * <ol>
     * <li>Persist the new TolvenUser object</li>
     * <li>Create an LDAP entry</li>
     * <li>Create a TolvenUser Object</li>
     * <li>Create an invitation</li>
     * <li>Create an email referencing the invitation</li>
     * </ol>
     */
    public void register(  TolvenPerson tp, Date now ) throws Exception {
//      String rc = tp.getReferenceCode();
//      if (rc!=null) {
//          findSponsorship(rc);
//      }
//        ActivateInvitation detail = new ActivateInvitation( );
////      detail.setExpirationTime( DatatypeFactory.newInstance().newXMLGregorianCalendar( t));
//        detail.setPrincipal( tp.getUid());
//        detail.setReferenceCode( rc );
//      Invitation invitation = invitationBean.createInvitation(tp.getUid(), detail);
//      // Create an invitation
//        invitation.setTitle( "Finish new user activation");
//        invitation.setDispatchAction("activate");
//        invitation.setTemplate("/invitation/activate.jsf");
//        invitation.setAccount( null );    // No owner for registrations (could be sponsor)
//        String expiration = System.getProperty("tolven.register.expiration");
//        if (expiration!=null) {
//          long elapsed = Long.parseLong(expiration)*1000;
//          if (elapsed > 0) {
//              invitation.setExpiration( new Date( now.getTime()+ (Long.parseLong(expiration)*1000) ) );
//          }
//        }
//        // Once sent, the invitation state will be updated to reflect completion. Workflow, man. Workflow.
//        invitationBean.queueInvitation( invitation );
//        // Make the LDAP entry
//        ldapBean.addPerson( tp );
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

    public boolean hasUserCredentials(TolvenUser aTolvenUser) {
        return ldapBean.hasUserCredentials(aTolvenUser.getLdapUID());
    }
    
    /**
      * Find the user object and mark the last update date in the user object. Technically, 
      * this is really a post-login step since the container has already authenticated the user.
      */
     public TolvenUser loginUser( String principal, Date now) {
         TolvenUser user = findUser( principal );
         if (user!=null) {
            user.setOldLastLogin(user.getLastLogin());
            user.setLastLogin( now );    // Now - current time. Update last login to now.
            //The following should no longer be required
            /*
            if (!hasUserCredentials(user)) {
                Subject subject;
                try {
                    subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
                } catch (PolicyContextException e) {
                    throw new IllegalStateException("[EJB]PolicyContext exception", e);
                }
                if (subject == null)
                    throw new IllegalStateException("[EJB]No Subject found in PolicyContext");
            }
            */
        }
         return user;
     }

     /**
      * Find the user object and mark it as inactive. This is a terminal state. If the user is recreated at some time
      * in the future, a new user object is created.
      * We'll return the user object but it's probably not much good any more.
      */
     public TolvenUser deactivateUser( String principal) {
        TolvenUser user = findUser( principal );
        String inactiveStatus = Status.INACTIVE.value();
        
         if (user!=null) {
            user.setStatus(inactiveStatus);
         }
         return user;
     }

     /**
      * Given the user, find the default account for that user. There may be none.
      */
     public AccountUser findDefaultAccountUser( TolvenUser user )
     {
       String activeStatus = Status.ACTIVE.value();
       String oldActiveStatus = Status.OLD_ACTIVE.value(); 
        String select = "SELECT au FROM AccountUser au WHERE au.user = :user ";
        select += "and ( au.status = '" + activeStatus + "' or au.status = '" + oldActiveStatus;
        select += "' ) and au.defaultAccount = true";
        Query query = em.createQuery(select);
        query.setParameter("user", user);
        List<AccountUser> accountUsers = query.getResultList();
        if (accountUsers.size()==1) return accountUsers.get(0);
        return null;
     }
     /**
      * Remove the listed AccountUserRules
      * @param removeList
      */
     public void removeAccountUserRoles( List<AccountUserRole> removeList) {
    	 for( AccountUserRole aur : removeList ) {
    		 em.remove(aur);
    	 }
     }

     public void setDefaultAccountUser(AccountUser accountUser) {
        for (AccountUser au : findUserAccounts(accountUser.getUser())) {
            if (au.isDefaultAccount() && au.getId() != accountUser.getId()) {
                au.setDefaultAccount(false);
                em.merge(au);
            }
        }
        if (!accountUser.isDefaultAccount()) {
            accountUser.setDefaultAccount(true);
            em.merge(accountUser);
        }
    }
     
     /**
      * Update the user object. This is a fairly lightweight object with most of the work being handled by
      * TolvenPerson (LDAP) and/or AccountUser.
      */
     public void updateUser( TolvenUser user ) {
         em.merge( user );
     }

     /**
      * Update an AccountUser record, usually to set the default flag. We don't check if there's already another 
      * accountUser with the default flag. But the application should insure this doesn't happen (the only harm is 
      * that the user would be required to specify which account they want to log into.
      */
     public void updateAccountUser(AccountUser accountUser ) {
         em.merge( accountUser );
     }
     
     /**
      * Return an accountUser given its id
      */
     public AccountUser findAccountUser( long accountUserId )
     {
     	propertyBean.setAllProperties();
        return em.find(AccountUser.class, accountUserId);
     }

     /**
      * Given the TolvenUser, find a list of all Accounts that that TolvenUser is associated with.
      */
      public List<AccountUser> findUserAccounts( TolvenUser user ) {
       String activeStatus = Status.ACTIVE.value();
       String oldActiveStatus = Status.OLD_ACTIVE.value(); 
         Query query = em.createQuery("SELECT au FROM AccountUser au WHERE au.user = :user " +
                 "and ( au.status = '" + activeStatus + "' or au.status = '" + oldActiveStatus + "') " +
                        "ORDER BY au.account.title, au.id");
         query.setParameter("user", user);
         List<AccountUser> items = query.getResultList();
          return items;
      }
      
      /**
       * Count the number of accounts a user could log into
       */
      public long countUserAccounts( TolvenUser user )
      {
        Query query = em.createQuery("SELECT COUNT(au) FROM AccountUser au WHERE au.user = :user AND au.status IN ('" + Status.ACTIVE.value() + "','" + Status.OLD_ACTIVE.value() + "')");
        query.setParameter("user", user);
        Long rslt = (Long) query.getSingleResult();
        return rslt.longValue();
     }

      /**
       * Given the Tolven Account, find a list of all TolvenUsers that are users of the account.
       * We ignore users that are inactive.
       */
       public List<AccountUser> findAccountUsers( Account account ) {
           String activeStatus = Status.ACTIVE.value();
           String oldActiveStatus = Status.OLD_ACTIVE.value(); 
           Query query = em.createQuery("SELECT au FROM AccountUser au WHERE au.account = :account " +
                  "and (au.user.status = '" + activeStatus + "' or au.user.status = '" + oldActiveStatus + "') order by au.user.ldapUID");       
          query.setParameter("account", account);
          List<AccountUser> items = query.getResultList();
           return items;
       }
       
     /**
      * Return a list of all active Tolven Users. This method has limited usefulness. Good for initial setup activities.
      */
      public List<TolvenUser> findAllActiveUsers( ) {
            String activeStatus = Status.ACTIVE.value();
            String oldActiveStatus = Status.OLD_ACTIVE.value();
         Query query = em.createQuery("SELECT u FROM TolvenUser u WHERE u.status = '" + activeStatus + "' or u.status = '" + oldActiveStatus + "'");
         List<TolvenUser> items = query.getResultList();
         return items;
      }
     
    /**
     * Count the number of users knows to the DB (not LDAP). No actual purpose for this function.
     */
    public long countUsers(  )
    {
        Query query = em.createQuery("SELECT COUNT(u) FROM TolvenUser u");
        Long rslt = (Long) query.getSingleResult();
        return rslt.longValue();
    }    
   
 }
