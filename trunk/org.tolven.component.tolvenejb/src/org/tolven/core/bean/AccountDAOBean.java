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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.AccountDAORemote;
import org.tolven.core.SponsoredUser;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountExchange;
import org.tolven.core.entity.AccountProperty;
import org.tolven.core.entity.AccountRole;
import org.tolven.core.entity.AccountTemplate;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.AccountUserProperty;
import org.tolven.core.entity.AccountUserRole;
import org.tolven.core.entity.Sponsorship;
import org.tolven.core.entity.Status;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.entity.Invitation;
import org.tolven.key.UserKeyLocal;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.key.AccountPrivateKey;
import org.tolven.security.key.AccountSecretKey;
import org.tolven.session.TolvenSessionWrapperFactory;

/**
 * This is the bean class for the AccountDAOBean enterprise bean.
 * Created Apr 3, 2006 1:27:18 PM
 * @author John Churin
 */
@Stateless
@Local(AccountDAOLocal.class)
public class AccountDAOBean implements AccountDAOLocal,AccountDAORemote {
    
    public static final String USER_KEYS_OPTIONAL = "tolven.security.user.keysOptional";
    
    @PersistenceContext
    private EntityManager em;

    @Resource
    private EJBContext ejbContext;
    
    @EJB
    private TolvenPropertiesLocal propertyBean;
    
    @EJB
    private UserKeyLocal userKeyBean;
    
    private Logger logger = Logger.getLogger(AccountDAOBean.class);

    public static String alphabet = "ab2c3d4e5f6g7h8j9kmnprstuvwxy";

    public static String generateSponsorReferenceCode(long n) {
        StringBuffer sb = new StringBuffer(20);
        n = ((n + 1) * 17) + (n * 3) + n;
        while (n > 0) {
            long r = n % (long) alphabet.length();
            sb.append(alphabet.charAt((int) r));
            n = n / alphabet.length();
        }
        return sb.toString();
    }

    /**
     * Create a new account. No users are associated with this account.
     * @param accountType
     * @return A new Account object
     */
    @Deprecated
    public Account createAccount(String knownType, String title, String timeZone) {
        Account account = new Account();
        AccountType accountType = findAccountTypebyKnownType(knownType);
        account.setAccountType(accountType);
        account.setTitle(title);
        account.setTimeZone(timeZone);
        account.setEnableBackButton(true);// by default enable backbutton for now.
        em.persist(account);
        return account;
    }

    /**
     * Stick the updated account back in the database.
     * @param account
     */
    public void updateAccount(Account account) {
        //      TolvenLogger.info( "disableAutoRefresh: " + account.getDisableAutoRefresh(), AccountDAOBean.class );
        em.merge(account);
    }

    /**
     * Create a new account. No users are associated with this account.
     * @param accountType
     * @return A new Account object
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    @Deprecated
    public Account createAccount2(String title, String timeZone, AccountType accountType) throws GeneralSecurityException, IOException {
        Account account = new Account();
        account.setAccountType(accountType);
        account.setTitle(title);
        account.setTimeZone(timeZone); //account.setAccountTypeId(accountTypeId);
        //TODO: This method is called by the generate patients functionality which generates patients
        //for an account in which the user is no logged and has not set up AccountProcessingKeys.
        //So it must be done here
        account.setEnableBackButton(true);
        em.persist(account);
        return account;
    }

    /**
     * Create an Account for TolvenUsers with a root MenuStructure.
     * @param accountType
     * @param user
     * @param userPublicKey
     * @return
     */
    @Override
    public AccountUser createAccount(AccountType accountType, TolvenUser user, PublicKey userPublicKey, Date timeNow) {
        if(!accountType.isCreatable()) {
            throw new RuntimeException("AccountType: " + accountType.getKnownType() + " is not creatable");
        }
        Account templateAccount = findAccountTemplate(accountType.getKnownType());
        if(templateAccount == null) {
            throw new RuntimeException("Could not find AccountTemplate for AccountType: " + accountType.getKnownType());
        }
        Account account = createAccount(accountType);
        account.setAccountTemplate(templateAccount);
        AccountUser accountUser = addAccountUser(account, user, timeNow, true, userPublicKey);
        return accountUser;
    }
    
    /**
     * Create a template Account with a root MenuStructure. Template accounts have no users and no accountTemplate of their own.
     * @param accountType
     * @return
     */
    @Override
    public Account createTemplateAccount(AccountType accountType) {
        Account account = createAccount(accountType);
        return account;
    }

    /**
     * Create an Account with a root MenuStructure, which can be used as a basis for a template Account or
     * an Account for TolvenUsers.
     * @param accountType
     * @return
     */
    private Account createAccount(AccountType accountType) {
        Account account = new Account();
        account.setAccountType(accountType);
        account.setTitle(accountType.getLongDesc());
        account.setEnableBackButton(true);
        em.persist(account);
        AccountMenuStructure ms = new AccountMenuStructure();
        ms.setAccount(account);
        ms.setNode(accountType.getKnownType());
        ms.setRole(MenuStructure.TAB);
        ms.setSequence(1);
        em.persist(ms);
        return account;
    }
    
    /**
     * This method checks if the AccountType of an account has a newer version of the template or not.
     * @return true 
     */
    public boolean isAccountTemplateCurrent(Account account) {
        AccountTemplate accountTemplate = em.find(AccountTemplate.class, account.getAccountType().getKnownType());
        if (accountTemplate == null)
            return false; // Legacy
        if (account.getAccountTemplate() == null)
            return false; // Legacy
        return (accountTemplate.getAccount().getId() == account.getAccountTemplate().getId());
    }

    /**
     * Set this account as being up to date with the account type
     */
    public void markAccountAsUpToDate(Account account) {
        AccountTemplate accountTemplate = em.find(AccountTemplate.class, account.getAccountType().getKnownType());
        if(accountTemplate == null) {
            throw new RuntimeException("Could not find accountType: " + account.getAccountType().getKnownType());
        }
        account.setAccountTemplate(accountTemplate.getAccount());
        em.merge(account);
    }

    /**
     * Given an account, return its AccountTemplate, the account containing
     * metadata used to populate the account.
     * @param account
     * @return
     */
    public Account findAccountTemplate(Account account) {
        Account templateAccount = account.getAccountTemplate();
        if (templateAccount == null) {
            AccountTemplate accountTemplate = em.find(AccountTemplate.class, account.getAccountType().getKnownType());
            templateAccount = accountTemplate.getAccount();
        }
        return templateAccount;
    }

    /**
     * Given an knownType of accountType, return its AccountTemplate.
     * @param knownType
     * @return
     */
    public Account findAccountTemplate(String knownType) {
        AccountTemplate accountTemplate = em.find(AccountTemplate.class, knownType);
        return accountTemplate.getAccount();
    }

    /**
     * Specifies the account to use as a template when creating new accounts
     * @param knownType
     * @param account
     */
    public void setAccountTemplate(String knownType, Account account) {
        AccountType accountType = findAccountTypebyKnownType(knownType);
        if (accountType == null) {
            throw new RuntimeException("Could not find AccountType: " + knownType);
        }
        AccountTemplate accountTemplate = em.find(AccountTemplate.class, knownType);
        if (accountTemplate == null) {
            accountTemplate = new AccountTemplate();
            accountTemplate.setKnownType(knownType);
        }
        accountTemplate.setAccount(account);
        em.merge(accountTemplate);
    }

    /**
     * Create a new account. No users are associated with this account.
     * @param AccountType accountType
     * @return A new Account object
     */
    @Deprecated
    public Account createAccount(String knownType) {
        return createAccount(knownType, null, null);
    }

    /**
     * Find an account given the account id.
     * @param accountId
     * @return the account object
     */
    public Account findAccount(long accountId) {
        return em.find(Account.class, accountId);
    }
    /** CCHIT merge
     * Return a list of all accounts of a particular accountType
     * @param accountType
     * @return
     */
    public List<Account> findAccounts(AccountType accountType) {
        Query q = em.createQuery("SELECT a FROM Account a WHERE a.accountType = :accountType");
        q.setParameter("accountType", accountType);
        List<Account> accounts = q.getResultList();
    	return accounts;
    }
    
    /**
     * Return a template Account, which is defined as an Account with no AccountTemplate associated (and technically, no users)
     * @param accountId
     * @return
     */
    @Override
    public Account findTemplateAccount(long accountId) {
        Query q = em.createQuery("SELECT a FROM Account a " + 
                "WHERE a.id = :accountId " + 
                "AND a.accountTemplate IS NULL");
        q.setParameter("accountId", accountId);
        List<Account> rslt = q.getResultList();
        if (rslt.size() == 1) {
            return rslt.get(0);
        } else if (rslt.size() == 0) {
            return null;
        } else {
            throw new RuntimeException("More than one template account found with accountId: " + accountId);
        }
    }

    /**
     * Associate a user with an account without sending using invitation
     * @param account The existing (although possibly very recent) Account object
     * @param user the existing (although possibly very recent) TolvenUser object
     * @param now Transactional "now" time
     * @param accountPermission boolean indicating if this user has account administration permission
     */
    public AccountUser addAccountUser(Account account, TolvenUser user, Date now, boolean accountPermission, PublicKey userPublicKey) {
        return addAccountUser(null, account, user, null, now, accountPermission, userPublicKey);
    }

    /**
    * Associate a user with an account by invitation
    * @param account The existing (although possibly very recent) Account object
    * @param inviterAccountUser The AccountUser of the inviter
    * @param invitedUser the existing (although possibly very recent) TolvenUser object
    * @param anInviterUserPrivateKey the PrivateKey of the inviter of the Account
    * @param now Transactional "now" time
    * @param accountPermission boolean indicating if this user has account administration permission
    */
    @Override
    public AccountUser inviteAccountUser(Account account, AccountUser inviterAccountUser, TolvenUser invitedUser, String invitedUserRealm, PrivateKey anInviterUserPrivateKey, Date now, boolean accountPermission) {
        return inviteAccountUser(account, inviterAccountUser, invitedUser, invitedUserRealm, anInviterUserPrivateKey, now, accountPermission, false);
    }

    /**
    * Update the AccountUser with AccountPrivateKey encrypted with the provided invitedUserPublicKey (required when 
    * a user obtains a new user public key). The invited user must exist on the Account, and already have an AccountPrivateKey.
    * The invited user's public key (certificate) must also be supplied
    * 
    * @param account The existing (although possibly very recent) Account object
    * @param inviterAccountUser The AccountUser of the inviter
    * @param invitedUser the existing (although possibly very recent) TolvenUser object
    * @param anInviterUserPrivateKey the PrivateKey of the inviter of the Account
    * @param now Transactional "now" time
    * @param accountPermission boolean indicating if this user has account administration permission
    */
    @Override
    public AccountUser inviteAccountUser(Account account, AccountUser inviterAccountUser, TolvenUser invitedUser, String invitedUserRealm, PrivateKey anInviterUserPrivateKey, Date now, boolean accountPermission, boolean isReinvite) {
        if (invitedUser==null) throw new NullPointerException("Missing TolvenUser object");
        if (inviterAccountUser==null) throw new NullPointerException("Missing AccountUser object");
        if (account==null) throw new NullPointerException("Missing Account object");
        AccountUser invitedAccountUser = findAccountUser(invitedUser.getLdapUID(), account.getId());
        try {
            if(isReinvite) {
                if(invitedAccountUser == null) {
                    throw new RuntimeException("Invited user " + invitedUser.getLdapUID() + " is not currently a user of account: " + account.getId());
                }
                String principal = (String) TolvenSessionWrapperFactory.getInstance().getPrincipal();
                logger.info("User: " + principal + " is reinviting user: " + invitedUser.getLdapUID() + " to Account: " + account.getId());
            } else {
                if(invitedAccountUser != null) {
                    throw new RuntimeException("Invited user " + invitedUser.getLdapUID() + " is already a user of account: " + account.getId());
                }
            }
            AccountPrivateKey inviterAccountPrivateKey = inviterAccountUser.getAccountPrivateKey();
            if (inviterAccountPrivateKey == null) {
                throw new RuntimeException("No AccountPrivateKey found for " + ejbContext.getCallerPrincipal() + " for account: " + account.getId());
            }
            PublicKey invitedUserPublicKey = userKeyBean.getUserPublicKey(invitedUser.getLdapUID(), invitedUserRealm);
            AccountPrivateKey invitedAccountPrivateKey = AccountPrivateKey.getInstance();
            String kbeKeyAlgorithm = propertyBean.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
            int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
            invitedAccountPrivateKey.init(inviterAccountPrivateKey, anInviterUserPrivateKey, invitedUserPublicKey, kbeKeyAlgorithm, kbeKeyLength);
            Invitation invitation = new Invitation();
            invitation.setStatus(Status.INACTIVE.value());
            invitation.setAccountPrivateKey(invitedAccountPrivateKey);
            return addAccountUser(invitedAccountUser, account, invitedUser, invitation, now, accountPermission, invitedUserPublicKey);
        } catch (Exception ex) {
            throw new RuntimeException("Problem with inviteAccountUser for " + invitedUser.getLdapUID(), ex);
        }
    }
    
    public TolvenUser findSystemUser( String principal, String status) {
        Query query = em.createQuery("SELECT u FROM TolvenUser u WHERE u.ldapUID = :principal " +
        "AND u.status = :status");
        query.setParameter("principal", principal.toLowerCase());
        query.setParameter("status", status);
        List<TolvenUser> results = query.getResultList();
        if (results.size()>0) {
            return results.get(0);
        } else {
            return null;
        }
    }
    
    public TolvenUser createSystemUser( String principal, String status, Date now) {
        TolvenUser tolvenUser = new TolvenUser();
        tolvenUser.setCreation(now);
        tolvenUser.setDemoUser(false);
        tolvenUser.setLdapUID(principal.toLowerCase());
        tolvenUser.setStatus(status);
        em.persist(tolvenUser);
        return tolvenUser;
    }
    
    public AccountUser findSystemAccountUser(TolvenUser user, Account account, String status) {
        Query query = em.createQuery("SELECT au FROM AccountUser au WHERE au.user = :user " +
        "AND au.account = :account AND au.status = :status");
        query.setParameter("user", user);
        query.setParameter("account", account);
        query.setParameter("status", status);
        List<AccountUser> results = query.getResultList();
        if (results.size()>0) {
            return results.get(0);
        } else {
            return null;
        }
    }
    /**
     * Create an accountUser with the status specified. Note: This accountUser can never log into the account
     * because no credentials are provided to do so.
     * @param user 
     * @param account
     * @param status Typically should contain "system"
     * @param now Transaction now time
     * @return
     */
    public AccountUser createSystemAccountUser(TolvenUser user, Account account, String status, Date now) {
        AccountUser accountUser = new AccountUser();
        accountUser.setAccount(account);
        accountUser.setUser(user);
        accountUser.setEffectiveDate(now);
        accountUser.setStatus(status);
        // Don't make this the default account until asked, even if we only have one account for the user.
        accountUser.setDefaultAccount(false);
        accountUser.setAccountPermission(false);
        em.persist(accountUser);
        return accountUser;
    }

    /**
     * Return a list of all properties applicable to a user. This includes system with duplicates eliminated.
     * @param user The TolvenUser to resolve 
     * @return A map of name-value pairs (properies)
     */
    @Override
    public Properties resolveTolvenUserProperties(TolvenUser user) {
        Properties properties = new Properties();
        // System Properties
        propertyBean.setAllProperties();
        Properties systemProperties = propertyBean.findProperties();
        for (Object obj : propertyBean.findProperties().keySet()) {
            String key = (String) obj;
            String value = systemProperties.getProperty(key);
            properties.setProperty(key, value);
        }
        Locale locale = ResourceBundleHelper.getLocale(user.getLocale());
        ResourceBundle globalBundle = ResourceBundle.getBundle(ResourceBundleHelper.GLOBALBUNDLENAME, locale);
        for (String key : globalBundle.keySet()) {
            String value = globalBundle.getString(key);
            properties.setProperty(key, value);
        }
        return properties;
    }
    
    /**
     * Return a list of all properties applicable to an account user. This includes system, accountType, account, and accountUser properties
     * with duplicates eliminated.
     * @param accountUser The AccountUser to resolve 
     * @return A map of name-value pairs (properies)
     */
    @Override
    public Properties resolveAccountUserProperties(AccountUser accountUser) {
        Properties properties = new Properties();
        // System Properties
        propertyBean.setAllProperties();
        Properties systemProperties = propertyBean.findProperties();
        for (Object obj : propertyBean.findProperties().keySet()) {
            String key = (String) obj;
            String value = systemProperties.getProperty(key);
            properties.setProperty(key, value);
        }
        if (accountUser != null) {
            Locale locale = ResourceBundleHelper.getLocale(accountUser.getUser().getLocale(), accountUser.getLocale());
            ResourceBundle globalBundle = ResourceBundle.getBundle(ResourceBundleHelper.GLOBALBUNDLENAME, locale);
            for (String key : globalBundle.keySet()) {
                String value = globalBundle.getString(key);
                properties.setProperty(key, value);
            }
            Account account = accountUser.getAccount();
            if (account != null) {
                String accountType = account.getAccountType().getKnownType();
                try {
                    ResourceBundle appGlobalBundle = ResourceBundle.getBundle(ResourceBundleHelper.getAppGlobalBundleName(accountType), locale);
                    for (String key : appGlobalBundle.keySet()) {
                        String value = appGlobalBundle.getString(key);
                        properties.setProperty(key, value);
                    }
                } catch (MissingResourceException ex) {
                    logger.warn("Could not find AppGlobalBundleName ResourceBundle for AccountType: " + accountType);
                }
                try {
                    ResourceBundle appBundle = ResourceBundle.getBundle(ResourceBundleHelper.getAppBundleName(accountType), locale);
                    for (String key : appBundle.keySet()) {
                        String value = appBundle.getString(key);
                        properties.setProperty(key, value);
                    }
                } catch (MissingResourceException ex) {
                    logger.warn("Could not find AppBundleName ResourceBundle for AccountType: " + accountType);
                }
                // Account Type Properties
                Account templateAccount = account.getAccountTemplate();
                if (templateAccount != null) {
                    for (AccountProperty accountProperty : templateAccount.getAccountProperties()) {
                        if (accountProperty.getPropertyValue() != null) {
                            properties.setProperty(accountProperty.getPropertyName(), accountProperty.getPropertyValue());
                        }
                    }
                }
                // Account Properties
                for (AccountProperty accountProperty : account.getAccountProperties()) {
                    if (accountProperty.getPropertyValue() != null) {
                        properties.setProperty(accountProperty.getPropertyName(), accountProperty.getPropertyValue());
                    }
                }
            }
            // AccountUser properties
            for (AccountUserProperty accountUserProperty : accountUser.getAccountUserProperties()) {
                if (accountUserProperty.getPropertyValue() != null) {
                    properties.setProperty(accountUserProperty.getPropertyName(), accountUserProperty.getPropertyValue());
                }
            }
        }
        return properties;
    }
    
    /**
     * Return an account user with a status of "system" for the EJB caller principal.
     * If the accountUser does not exist, it will be created.
     * Users with this status are not able to login as normal users.
     * @param account The account for which this accountUser should apply
     * @param createIf If true, the user and/or account user will be created if they do not exist
     * @return The AccountUser
     */
    public AccountUser getSystemAccountUser(Account account, boolean createIf, Date now ) {
        String principal = (String)TolvenSessionWrapperFactory.getInstance().getPrincipal();
        String status = "system";
        propertyBean.setAllProperties();
        TolvenUser tolvenUser = findSystemUser( principal, status);
        if (tolvenUser==null) {
            if (createIf) {
                tolvenUser = createSystemUser( principal, status, now );
            } else {
                throw new RuntimeException( "No tolvenUser entry for " + principal + " in getSystemAccountUser");
            }
        }
        AccountUser accountUser = findSystemAccountUser(tolvenUser, account, status);
        if (accountUser==null) {
            if (createIf) {
                accountUser = createSystemAccountUser(tolvenUser, account, status, now);
            } else {
                throw new RuntimeException( "No accountUser entry for " + principal + " and account " + account.getId() + " in getSystemAccountUser");
            }
        }
        return accountUser;
        
    }

    /**
     * Find an accountUser given the username and account id. 
     */
    public AccountUser findAccountUser(String username, long accountId) {
        propertyBean.setAllProperties();
        // Example of using traditional-style SQL joins
        Query q = em.createQuery("SELECT au FROM AccountUser au, TolvenUser u, Account a " + 
                "WHERE au.account = a " + 
                "AND a.id = :accountId " + 
                "AND au.user = u " + 
                "AND u.ldapUID = :username " + 
                "AND u.status = 'active' " + 
                "AND au.status = 'active' ");
        q.setParameter("accountId", accountId);
        q.setParameter("username", username.toLowerCase());
        List<AccountUser> rslt = q.getResultList();
        AccountUser au = null;
        if (rslt.size() == 1) {
            au = rslt.get(0);
            // Touch the related objects so they are for sure in memory (in case of lazy fetch)
            au.getAccount().getId();
            au.getUser().getId();
        }
        return au;
    }

    /**
     * Associate a user with an account
     * @param account The existing (although possibly very recent) Account object
     * @param user the existing (although possibly very recent) TolvenUser object
     * @param invitation the invitation to join the account
     * @param now Transactional "now" time
     * @param accountPermission boolean indicating if this user has account administration permission
     */
    private AccountUser addAccountUser(AccountUser invitedAccountUser, Account account, TolvenUser user, Invitation invitation, Date now, boolean accountPermission, PublicKey invitedUserPublicKey) {
        // TODO: Note that the invitation supplied here by the method
        // inviteAccountUser, is not fully implemented, until this todo is
        // removed, and it may also be null.
        if(user == null) {
            throw new RuntimeException("The TolvenUser to add to Account is null");
        }
        AccountUser au = null;
        if(invitedAccountUser == null) {
            au = new AccountUser();
        } else {
            au = invitedAccountUser;
            logger.info(user.getLdapUID() + " is being reinvited to account: " + account.getId());
        }
        au.setAccount(account);
        au.setUser(user);
        au.setEffectiveDate(now);
        String activeStatus = Status.fromValue("active").value();
        au.setStatus(activeStatus);
        // Don't make this the default account until asked, even if we only have one account for the user.
        au.setDefaultAccount(false);
        au.setAccountPermission(accountPermission);
        // TODO: Note that keys are not ready for release and creating them
        // before they are tested could lead to problems for later
        // migration. But if you know what you are doing and the DB is
        // experimental, then developers are free to play by setting System
        // property tolven.security.keys.activate
        // TODO: At this point the AccountUser cannot have a PrivateKey
        setupAccountKeys(account, au, invitation, user, invitedUserPublicKey);
        em.persist(au);
        String principal = (String) TolvenSessionWrapperFactory.getInstance().getPrincipal();
        logger.info("User: " + principal + " has invited user: " + au.getUser().getLdapUID() + " to Account: " + au.getAccount().getId() + " (AccountUser Id: " + au.getId() + ")");
        if(invitedUserPublicKey == null) {
            logger.info("User: " + au.getUser().getLdapUID() + " has NO UserPublicKey to protect the AccountPrivateKey");
        } else {
            logger.info("AccountPrivateKey is protected by the UserPublicKey of user: " + au.getUser().getLdapUID());
        }
        return au;
    }

    /**
     * If the account already has a PublicKey, then retrieve the AccountPrivateKey from the Invitation and pass it to the AccountUser.
     * If the account does not yet have a PublicKey, then create a key pair now...place the AccountPublicKey in the Account and protect.
     * the AccountPrivateKey with aUserPublicKey, and place it in the AccountUser.
     * @param account
     * @param accountUser should not yet have an AccountPrivateKey.
     * @param invitation contains an AccountPrivateKey, from the invitee, if this Account is not new.
     * @param user being invited to the Account (may be a self-invitation for a new Account)
     */
    public void setupAccountKeys(Account account, AccountUser accountUser, Invitation invitation, TolvenUser user, PublicKey invitedUserPublicKey) {
        try {
            if(invitedUserPublicKey == null) {
                String userKeysOptional = propertyBean.getProperty(USER_KEYS_OPTIONAL);
                if(!Boolean.parseBoolean(userKeysOptional)) {
                    String message = null;
                    if(account.getId() == 0) {
                        message = "A UserPublicKey (User Certificate) is required to create an account for: " + user.getLdapUID();
                    } else {
                        message = "A UserPublicKey (User Certificate) is required by users of account: " + account.getId();
                    }
                    throw new RuntimeException(message);
                }
            }
            if (account.hasPublicKey()) {
                if (invitation == null) {
                    throw new RuntimeException("An invitation is required to obtain an AccountPrivateKey for account: " + account.getId());
                } else {
                    accountUser.setAccountPrivateKey(invitation.getAccountPrivateKey());
                }
            } else {
                AccountPrivateKey accountPrivateKey = AccountPrivateKey.getInstance();
                String privateKeyAlgorithm = propertyBean.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
                int privateKeyLength = Integer.parseInt(propertyBean.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
                String kbeKeyAlgorithm = propertyBean.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
                int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
                PublicKey accountPublicKey = accountPrivateKey.init(invitedUserPublicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
                account.setPublicKey(accountPublicKey);
                accountUser.setAccountPrivateKey(accountPrivateKey);
            }
        } catch (Exception ex) {
            //TODO: consider declaring exceptions in the method declaration
            throw new RuntimeException("Problem while setting up Account Keys for " + user.getLdapUID(), ex);
        }
    }

    /**
     * Create a new sponsorship for the given account.
     */
    public Sponsorship createSponsorship(Account account, String title) {
        Sponsorship sponsorship = new Sponsorship();
        sponsorship.setAccount(account);
        sponsorship.setTitle(title);
        // ID needs to be generated before creating the reference code so persist THEN setReferenceCode
        em.persist(sponsorship);
        sponsorship.setReferenceCode(generateSponsorReferenceCode(sponsorship.getId()));
        return sponsorship;
    }

    public List<Sponsorship> findAccountSponsorships(Account account) {
        Query q = em.createQuery("SELECT s FROM Sponsorship s WHERE s.account = :account");
        q.setParameter("account", account);
        return q.getResultList();
    }

    public List<TolvenUser> findSponsoredUsers(Sponsorship sponsorship) {
        Query q = em.createQuery("SELECT u FROM TolvenUser u WHERE u.sponsorship = :sponsorship");
        q.setParameter("sponsorship", sponsorship);
        return q.getResultList();
    }

    /**
     * Return a list of users sponsored users by the specified account. This attributes returned are captive
     * in order to avoid cross-account peeping beyond the scope of sponsorship. 
     * @param account
     * @return
     */
    public List<SponsoredUser> findSponsoredUsersForAccount(Account account) {
        Query q = em.createQuery("SELECT new org.tolven.core.SponsoredUser(s.referenceCode, u.ldapUID, u.creation, u.lastLogin) " +
                "FROM TolvenUser u, Sponsorship s " +
                "WHERE u.sponsorship = s and s.account = :account");
        q.setParameter("account", account);
        return q.getResultList();
    }

    /**
     * Return a list of all account types
     * @return List
     */
    public List<AccountType> findAllAccountTypes() {
        Query query = em.createQuery("SELECT acty FROM AccountType acty");
        List<AccountType> items = query.getResultList();
        return items;
    }

    /**
     * Return a list of active account types
     * @return List
     */
    public List<AccountType> findActiveAccountTypes() {
        String activeStatus = Status.fromValue("active").value();
        String oldActiveStatus = Status.fromValue("ACTIVE").value();
        String select = "SELECT acty FROM AccountType acty WHERE (acty.status = '";
        select += activeStatus + "' or acty.status = '" + oldActiveStatus + "' ) ORDER BY acty.knownType";
        Query query = em.createQuery(select);
        List<AccountType> items = query.getResultList();
        return items;
    }

    /**
    * Find an accountType by knownType
    * @return AccountType
    */
    public AccountType findAccountTypebyKnownType(String knownType) {
        try {
            List<AccountType> accountTypes;
            Query q = em.createQuery("SELECT acty " +
                    "FROM AccountType acty " +
                    "WHERE acty.knownType = :kt " + 
                    "AND (acty.status = '" + Status.ACTIVE.value() + "' or acty.status = '" + Status.OLD_ACTIVE.value() + "' or acty.status = '" + Status.NEW.value() + "')");
            q.setParameter("kt", knownType);
            accountTypes = q.getResultList();
            if (accountTypes.size() == 1)
                return accountTypes.get(0);
            else
                return null;
        } catch (RuntimeException e) {
            TolvenLogger.info(" Caught error in [findAccountTypebyKnownType]", AccountDAOBean.class);
            throw e;
        }
    }

    /**
    * Find an accountType by id
    * @return AccountType
    */
    public AccountType findAccountType(long id) {
        return em.find(AccountType.class, id);
    }

    /**
     * createAccountType - create a new row in AccountType
     * @param knownType
     * @param homePage
     * @param longDesc
     * @param readOnly
     * @return
     */
    public AccountType createAccountType(String knownType, String homePage, String longDesc, boolean readOnly, String status, boolean userCanCreate) {
        AccountType accountType = new AccountType();
        accountType.setKnownType(knownType);
        accountType.setHomePage(homePage);
        accountType.setLongDesc(longDesc);
        accountType.setReadOnly(readOnly);
        accountType.setCreatable(userCanCreate);
        String encodedStatus = Status.fromValue(status).value();
        accountType.setStatus(encodedStatus);
        em.persist(accountType);
        return accountType;
    }

    /**
     * createAccountType - create a new row in AccountType
     * @param knownType
     * @return New AccountType
     */
    public AccountType createAccountType(String knownType) {
        AccountType accountType = new AccountType();
        accountType.setKnownType(knownType);
        em.persist(accountType);
        return accountType;
    }

    /**
     * updateAccountType - update the indicated accountType record
     * @param accountType
     */
    public void updateAccountType(AccountType accountType) {
        em.merge(accountType);
    }

    /**
     * statusForValue
     * @param value
     * @return String value of a status type
     */
    public String statusForValue(String value) {
        String statusStr = Status.fromValue(value).value();
        return statusStr;
    }

    /**
     * One-time migration 5-Nov-2006 for Accounts without an AccountType.
     */
    public void accountTypeConversion() {
        em.flush();
        // Make sure all account types have a "creatable" flag that is non-null
        Query accountTypeFix = em.createNativeQuery("update core.account_type set user_can_create = true where user_can_create is null;");
        accountTypeFix.executeUpdate();

        Query q = em.createQuery("SELECT a FROM Account a WHERE a.accountType = null");
        q.setMaxResults(1);
        List<Account> typelessAccouts = q.getResultList();
        if (typelessAccouts.size() > 0) {
            Query q1 = em.createNativeQuery("update core.account set accounttype_id = (select at.id from core.account_type at where at.known_type = 'echr') where known_type = 'echr';");
            int countQ1 = q1.executeUpdate();
            Query q2 = em.createNativeQuery("update core.account set accounttype_id = (select at.id from core.account_type at where at.known_type = 'ephr') where known_type = 'ephr';");
            int countQ2 = q2.executeUpdate();
            TolvenLogger.info("Upgraded " + (countQ1 + countQ2) + " accounts to new AccountTypes", AccountDAOBean.class);
        }
    }

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
    public List<AccountExchange> findActiveEndPoints(Account account, AccountExchange.Direction direction, boolean complete) {
        Query q;
        if (complete) {
            // Both ends must agree
            q = em.createQuery("SELECT ae " +
                    "FROM AccountExchange ae, AccountExchange aeo " + 
                    "WHERE ae.account = :account " + 
                    "AND aeo.account = ae.otherAccount " + 
                    "AND aeo.status = :status " + 
                    "AND aeo.direction = :otherDirection " + 
                    "AND ae.direction = :direction " + 
                    "AND ae.status = :status ");
            if (direction.equals(AccountExchange.Direction.SEND)) {
                q.setParameter("otherDirection", AccountExchange.Direction.RECEIVE);
            } else {
                q.setParameter("otherDirection", AccountExchange.Direction.SEND);
            }
        } else {
            q = em.createQuery("SELECT ae " +
                    "FROM AccountExchange ae " + 
                    "WHERE ae.account = :account " + 
                    "AND ae.direction = :direction " + 
                    "AND ae.status = :status");
        }
        q.setParameter("account", account);
        q.setParameter("direction", direction);
        q.setParameter("status", Status.ACTIVE.value());
        return q.getResultList();
    }

    public AccountExchange newAccountExchange(Account account, Account otherAccount, AccountExchange.Direction direction) {
        AccountExchange ae = new AccountExchange();
        ae.setAccount(account);
        ae.setOtherAccount(otherAccount);
        ae.setDirection(direction);
        ae.setStatus(Status.ACTIVE.value());
        em.persist(ae);
        return ae;
    }

    /**
     * Return the list of known accountRoles for an Account. 
     * If you have access to the Account entity locally, just access the roles directly
     * from the Account object.
     * @param accountId
     * @return A map of roles and titles
     */
    public Map<String, String> findAccountRoles(long accountId) {
        Account account = findAccount(accountId);
        Map<String, String> roles = new HashMap<String, String>();
        for (AccountRole role : account.getAccountRoles()) {
            roles.put(role.getRole(), role.getTitle());
        }
        return roles;
    }

    /**
     * Return the list of allowed roles for an AccountUser
     * If you have access to the AccountUser entity locally, just access the roles directly
     * from the AccountUser object.
     * @param accountId
     * @return A list of roles
     */
    public Set<String> findAccountUserRoles(long accountUserId) {
        AccountUser accountUser = em.find(AccountUser.class, accountUserId);
        Set<String> roles = new HashSet<String>(accountUser.getRoleList().size());
        for (AccountUserRole accountUserRole : accountUser.getRoleList()) {
            roles.add(accountUserRole.getRole());
        }
        return roles;

    }

    /**
     * Update the list of known roles for an Account. New roles in the supplied list are added.
     * missing roles in the list will be removed from the account. 
     * If you have access to the Account entity locally, just update the roles directly
     * from the Account object.
     * @param accountId
     * @param A list of roles
     */
    public void updateAccountRoles(long accountId, Map<String, String> roles) {
        Account account = findAccount(accountId);
        List<AccountRole> deleteAccountRoles = new ArrayList<AccountRole>();
        // Build a proposed list of roles to add
        Set<String> addAccountRoles = new HashSet<String>();
        for (String role : roles.keySet()) {
            addAccountRoles.add(role);
        }
        // Make list of the account roles to be deleted, or remove it from the list to be added if already there.
        for (AccountRole accountRole : account.getAccountRoles()) {
            if (!roles.containsKey(accountRole.getRole())) {
                deleteAccountRoles.add(accountRole);
            } else {
                accountRole.setTitle(roles.get(accountRole.getRole()));
                addAccountRoles.remove(accountRole.getRole());
            }
        }
        // Now remove them
        account.getAccountRoles().removeAll(deleteAccountRoles);
        // Anything left gets added
        for (String role : addAccountRoles) {
            AccountRole accountRole = new AccountRole();
            accountRole.setAccount(account);
            accountRole.setRole(role);
            accountRole.setTitle(roles.get(role));
            account.getAccountRoles().add(accountRole);
        }
    }

    /**
     * Update the list of roles for an AccountUser. New roles in the supplied list are added.
     * Missing roles in the list will be removed from the accountUser. 
     * If you have access to the AccountUser entity locally, just update the roles directly
     * from the AccountUser object.
     * @param accountUserId
     * @param A list of roles for this AccountUser
     */
    public void updateAccountUserRoles(long accountUserId, Set<String> roles) {
        AccountUser accountUser = em.find(AccountUser.class, accountUserId);
        List<AccountUserRole> deleteRoles = new ArrayList<AccountUserRole>();
        Set<String> addRoles = new HashSet<String>();
        for (String role : roles) {
            addRoles.add(role);
        }
        // Make list of the roles to be deleted
        for (AccountUserRole accountUserRole : accountUser.getRoleList()) {
            if (!roles.contains(accountUserRole.getRole())) {
                deleteRoles.add(accountUserRole);
            } else {
                addRoles.remove(accountUserRole.getRole());
            }
        }
        // Now remove them
        accountUser.getRoleList().removeAll(deleteRoles);
        // Anything left gets added
        Account account = findAccount(accountUser.getAccount().getId());
        Set<String> accountRoles = new HashSet<String>();
        for (AccountRole accountRole : account.getAccountRoles()) {
            accountRoles.add(accountRole.getRole());
        }
        for (String role : addRoles) {
            if (!accountRoles.contains(role)) {
                throw new RuntimeException("Invalid role " + role + " cannot be added to accountUser: " + accountUser.getId());
            }
            AccountUserRole newRole = new AccountUserRole();
            newRole.setAccountUser(accountUser);
            newRole.setRole(role);
            accountUser.getRoles().add(newRole);
        }
    }

    /**
     * Create or update an account property
     * @param accountId The account to update
     * @param name Name of the property
     * @param value Property value, a string of any length
     * @return The previous value of the property or null if it is new
     */
    public String putAccountProperty(long accountId, String name, String value) {
        Account account = findAccount(accountId);
        String oldValue = account.getProperty().put(name, value);
        return oldValue;
    }
    //CCHIT merge
    public List<AccountUser> getCurrentAccountUserList(TolvenUser tolvenUser) {
		String activeStatus = Status.ACTIVE.value();
		String oldActiveStatus = Status.OLD_ACTIVE.value(); 
	   Query query = em.createQuery("SELECT au FROM AccountUser au WHERE au.user = :user " +
			   "and ( au.status = '" + activeStatus + "' or au.status = '" + oldActiveStatus + "') " +
	   		   "ORDER BY au.account.title, au.id");
	   query.setParameter("user", tolvenUser);
	   List<AccountUser> items = query.getResultList();
	   return items;
    }
    /**
    * Create or update an account property
    * @param accountId The account to update
    * @param name Name of the property
    * @param Properties
    */
    public void putAccountProperties(long accountId, Properties properties) {
        for (Object obj : properties.keySet()) {
            String key = (String) obj;
            String value = properties.getProperty(key);
            putAccountProperty(accountId, key, value);
        }
    }

    /**
     * Is this account a template or not, which can affect the circumstances under which the user is allowed to retrive the Account
     * @paraam accountId
     * @return true 
     */
    public boolean isTemplateAccount(Long accountId) {
        Account account = findAccount(accountId);
        return account.getAccountTemplate() == null;
    }
} //endclass

