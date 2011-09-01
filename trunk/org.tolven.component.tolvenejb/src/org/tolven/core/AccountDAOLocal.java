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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.tolven.core.bean.AccountDAOBean;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountExchange;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.Sponsorship;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.entity.Invitation;


/**
 * Account-related services.
 */
public interface AccountDAOLocal {
    /**
     * Find an account given the account id.
     * @param accountId
     * @return the account object
     */
    public Account findAccount(long accountId );
   
    /** CCHIT merge
     * Return a list of all accounts of a particular accountType
     * @param accountType
     * @return
     */
    //public List<Account> findAccounts(AccountType accountType);
    
    
    /**
     * Create a new account. No users are associated with this account.
     * @param accountType
     * @return A new Account object
     */
    @Deprecated
    public Account createAccount( String knownType );

    /**
     * Stick the updated account back in the database.
     * @param account
     */
    public void updateAccount( Account account );
    
    /**
     * Create a new account. No users are associated with this account.
     * @param accountType
     * @param timeZone Java time zone, null means use tolven default
     * @return A new Account object
     */
    @Deprecated
    public Account createAccount( String knownType, String title, String timeZone );

    /**
     * Create a new account. No users are associated with this account.
     * @param accountType
     * @param timeZone Java time zone, null means use tolven default
     * @return A new Account object
     */
    @Deprecated
    public Account createAccount2( String title, String timeZone, AccountType accType ) throws LoginException, GeneralSecurityException, IOException;

    /**
	 * Mark this account as being up to date with the latest account type
	 */
	public void markAccountAsUpToDate(Account account );

	/**
	 * This method checks if the AccountType of an account has a newer version of the template or not.
	 * @return true 
	 */
	public boolean isAccountTemplateCurrent( Account account );
	

    /**
     * Given an account, return its AccountTemplate, the account containing
     * metadata used to populate the account.
     * @param account
     * @return
     */
    public Account findAccountTemplate( Account account );

    /**
     * Given an knownType of accountType, return its AccountTemplate.
     * @param knownType
     * @return
     */
    public Account findAccountTemplate( String knownType );
    
    /**
     * Specifies the account to use as a template when creating new accounts
     * @param knownType
     * @param account
     */
    public void setAccountTemplate( String knownType, Account account );
    
    /**
     * Find an accountUser given the username and account id. 
     */
    public AccountUser findAccountUser( String username, long accountId);

    /**
     * Return a list of all properties applicable to a user. This includes system with duplicates eliminated.
     * @param user The TolvenUser to resolve 
     * @return A map of name-value pairs (properies)
     */

    public Properties resolveTolvenUserProperties(TolvenUser user);
    
    /**
     * Return a list of all properties applicable to an account user. This includes system, accountType, account, and accountUser properties
     * with duplicates eliminated.
     * @param accountUser The AccountUser to resolve 
     * @return A map of name-value pairs (properies)
     */
    public Properties resolveAccountUserProperties(AccountUser accountUser);
    
    /**
     * Return an account user with a status of "system" for the EJB caller principal.
     * If the accountUser does not exist, it will be created.
     * @param account The account for which this accountUser should apply
     * @param createIf If true, the account user will be created if it does not exist
     * @return The AccountUser
     */
	public AccountUser getSystemAccountUser(Account account, boolean createIf, Date now );

    /**
      * @see AccountDAOBean
      */
     public AccountUser addAccountUser(Account account, TolvenUser user, Date now, boolean accountPermission, PublicKey userPublicKey );

     /**
      * @see AccountDAOBean
      */
     public AccountUser inviteAccountUser(Account account, AccountUser accountUser, TolvenUser invitedUser, String invitedUserRealm, PrivateKey anInviterPrivateKey, Date now, boolean accountPermission);
     
     /**
      * Invite or reinvite a user to an Account
      */
     public AccountUser inviteAccountUser(Account account, AccountUser accountUser, TolvenUser invitedUser, String invitedUserRealm, PrivateKey anInviterPrivateKey, Date now, boolean accountPermission, boolean isReinvite);

     /**
      * Find the list of Sponsorships owned by the specified account
      * @param account
      * @return
      */
     public List<Sponsorship> findAccountSponsorships( Account account );

     /**
      * Find the list of TolvenUsers referencing the specified Sponsorship
      * @param sponsorship
      * @return
      */
      public List<TolvenUser> findSponsoredUsers( Sponsorship sponsorship );

      /**
       * Create a new sponsorship for the given account.
       */
      public Sponsorship createSponsorship( Account account, String title);

      /**
    	* Find an accountType by id
    	* @return AccountType
    	*/
   	public AccountType findAccountType( long id );

   		/**
       * Return a list of users sponsored users by the specified account. This attributes returned are captive
       * in order to avoid cross-account peeping beyond the scope of sponsorship. 
       * @param account
       * @return
       */
      public List<SponsoredUser> findSponsoredUsersForAccount( Account account );
      /**
       *  Return a list of all AccountTypes
       */
      public List<AccountType> findAllAccountTypes ( );
      /**
       * Get active AccountTypes
       * @return
       */
      public List<AccountType> findActiveAccountTypes ( );
      
  	  /**
  	   * Return a single AccountType by known_type
  	   * @param kt
  	   * @return AccountType
  	   */
  	  public AccountType findAccountTypebyKnownType( String kt );
  	  
  	  /**
  	   * Create new AccountType record 
  	   * @param knownType, homePage, longDesc, readOnly
  	   * @return AccountType
  	   */
  	   public AccountType createAccountType( String knownType, String homePage, 
  			   String longDesc, boolean readOnly, String status, boolean userCanCreate );

  	 	/**
  	 	 * createAccountType - create a new row in AccountType
  	 	 * @param knownType
  	 	 * @return New AccountType
  	 	 */
  	    public AccountType createAccountType( String knownType );

    	/**
  	    * Update AccountType record
  	    * @param accountType
  	    * @return void
  	    */
  	   public void updateAccountType( AccountType accountType );

  	   /**
  	    * Get status type for input value
  	    */
  	    public String statusForValue( String value);

  	    /**
  	     * One-time migration 5-Nov-2006 for Accounts without an AccountType.
  	     */
  	    public void accountTypeConversion();
        

        /**
         * This method was added to handle pre-existing accounts which have no keys. An invitation does not have to be supplied but
         * if the account already has a public key, then the invitation cannot be null
         * @param account
         * @param accountUser
         * @param invitation
         * @param user
         */
        public void setupAccountKeys(Account account, AccountUser accountUser, Invitation invitation, TolvenUser user, PublicKey userPublicKey);

        /**
         * For a given account, return all valid accounts which this account can talk to.
         * @param account
         * @param direction
         * @param complete A boolean that when true indicates that both parties in the exchange are in agreement
         * about the exchange. False means that the "other party" need not have agreed to the exchange. For example,
         * if the account administrator wants to see a list of other accounts that one could 
         * potentially. communicate with.
         * @return
         */
        public List<AccountExchange> findActiveEndPoints( Account account, AccountExchange.Direction direction, boolean complete );
        
        public AccountExchange newAccountExchange( Account account, Account otherAccount, AccountExchange.Direction direction );

        /**
        * Create or update an account property
        * @param accountId The account to update
        * @param name Name of the property
        * @param value Property value, a string of any length
        * @return The previous value of the property or null if it is new
        */
       public String putAccountProperty( long accountId, String name, String value );

       /**
       * Create or update an account property
       * @param accountId The account to update
       * @param name Name of the property
       * @param Properties
       */
    public void putAccountProperties(long accountId, Properties properties);

    /**
     * Create an Account for TolvenUsers with a root MenuStructure.
     * @param accountType
     * @param user
     * @param userPublicKey
     * @return
     */
    public AccountUser createAccount(AccountType accountType, TolvenUser user, PublicKey userPublicKey, Date timeNow);

    /**
     * Create a template Account with a root MenuStructure. Template accounts have no users and no accountTemplate of their own.
     * @param accountType
     * @return
     */
    public Account createTemplateAccount(AccountType accountType);
    
    /**
     * Return a template Account, which is defined as an Account with no AccountTemplate associated (and technically, no users)
     * @param accountId
     * @return
     */
    public Account findTemplateAccount(long accountId);

    /**
     * Return the list of known accountRoles for an Account. 
     * If you have access to the Account entity locally, just access the roles directly
     * from the Account object.
     * @param accountId
     * @return A list of roles
     */
    public Map<String, String> findAccountRoles(long accountId);
    
    /**
     * Return the list of allowed roles for an AccountUser
     * If you have access to the AccountUser entity locally, just access the roles directly
     * from the AccountUser object.
     * @param accountId
     * @return A list of roles
     */
    public Set<String> findAccountUserRoles(long accountUserId);
    
    /**
     * Update the list of known roles for an Account. New roles in the supplied list are added.
     * missing roles in the list will be removed from the account. 
     * If you have access to the Account entity locally, just update the roles directly
     * from the Account object.
     * @param accountId
     * @param A list of roles
     */
    public void updateAccountRoles(long accountId, Map<String, String> roles);

    /**
     * Update the list of roles for an AccountUser. New roles in the supplied list are added.
     * Missing roles in the list will be removed from the accountUser. 
     * If you have access to the AccountUser entity locally, just update the roles directly
     * from the AccountUser object.
     * @param accountUserId
     * @param A list of roles for this AccountUser
     */
    public void updateAccountUserRoles(long accountUserId, Set<String> roles);

    /**
     * Is this account a template or not, which can affect the circumstances under which the user is allowed to retrive the Account
     * @paraam accountId
     * @return true 
     */
    public boolean isTemplateAccount(Long accountId);
    //CCHIT merge
    public List<AccountUser> getCurrentAccountUserList(TolvenUser tolvenUser);
    
}
