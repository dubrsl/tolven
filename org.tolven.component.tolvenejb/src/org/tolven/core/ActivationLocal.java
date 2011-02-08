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

package org.tolven.core;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.AccountUserRole;
import org.tolven.core.entity.Sponsorship;
import org.tolven.core.entity.TolvenUser;


/**
 * Services used to create, activate, and query Tolven users.
 */
public interface ActivationLocal {
    
 
    /**
     * Return the TolvenUser object given it's uinque id
     * @param tolvenUserId
     * @return
     */
    public TolvenUser findTolvenUser( long tolvenUserId );
    
    /**
     * Given what is suspected to be a valid sponsorship reference code, return the Sponsorship 
     * This method fails loudly (throws an object not found exception) if the reference code is not found.
     * This should be sufficient to rollback a transaction intended to create a new user 
     * with an invalid reference code.
     * @throws NoResultException if the referenceCode is not found
     * @param referenceCode
     * @return
     */
    public Sponsorship findSponsorship( String referenceCode );

    /**
     * Given the principal's name, get the TolvenUser.
     */
     public TolvenUser findUser( String principal );
     
     /**
      * Remove the listed AccountUserRules
      * @param removeList
      */
     public void removeAccountUserRoles( List<AccountUserRole> removeList);


    /**
     * Create a new Tolven User
     * @param principal
     * @return new TolvenUser object properly initialized and persisted
     */
    public TolvenUser createTolvenUser( String principal, Date now );
    
      /**
      * Find the user object and mark the last update date in the user object. Technically, 
      * this is really a post-login step since the container has already authenticated the user.
      */
     public TolvenUser loginUser( String principal, Date now);
     
     /**
      * Find the user object and mark it as inactive. This is a terminal state. If the user is recreated at some time
      * in the future, a new user object is created.
      * We'll return the user object but it's probably not much good any more.
      */
     public TolvenUser deactivateUser( String principal);

     /**
      * Update the user object. This is a fairly lightweight object with most of the work being handled by
      * TolvenPerson (LDAP) and/or AccountUser.
      */
     public void updateUser( TolvenUser user );

      /**
      * Retun an accountUser given its id. Note: AccountUserId is not the same as accountId or userId.
      */
     public AccountUser findAccountUser( long accountUserId );

     /**
      * Given the user, find the default account for that user. It is possible that the default account for the user
      * will be created if none exists.
      */
     public AccountUser findDefaultAccountUser( TolvenUser user );
     

     /**
      * Make accountUser the default AccountUser for the associated TolvenUser
      * @param accountUser
      */
     public void setDefaultAccountUser(AccountUser accountUser);

     /**
      * Given the TolvenUser, find a list of all Accounts that that TolvenUser is associated with.
      */
      public List<AccountUser> findUserAccounts( TolvenUser user );
      
      /**
       * Count the number of accounts a user could log into
       */
      public long countUserAccounts( TolvenUser user );

      /**
       * Given the Tolven Account, find a list of all TolvenUsers that are users of the account.
       */
       public List<AccountUser> findAccountUsers( Account account );
       
     /**
      * Update an AccountUser record, usually to set the default flag. We don't check if there's already another 
      * accountUser with the default flag. But the application should insure this doesn't happen (the only harm is 
      * that the user would be required to specify which account they want to log into.
      */
     public void updateAccountUser(AccountUser accountUser );
     
     /**
     * Count the number of users knows to the DB
     */
    public long countUsers(  );
    
    /**
     * Return a list of all active Tolven Users. This method has limited usefulness. Good for initial setup activities.
     */
     public List<TolvenUser> findAllActiveUsers( );

     public boolean hasUserCredentials(TolvenUser aTolvenUser);
}
