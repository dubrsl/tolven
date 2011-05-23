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
package org.tolven.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.acl.Group;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.tolven.core.SponsoredUser;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.Sponsorship;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.entity.Invitation;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.gen.bean.GenControlCHRAccount;
import org.tolven.gen.bean.GenControlPHRAccount;
import org.tolven.security.TolvenPerson;
import org.tolven.security.hash.SSHA;
import org.tolven.sso.TolvenSSO;
import org.tolven.util.ExceptionFormatter;
import org.tolven.web.security.GeneralSecurityFilter;
import org.tolven.web.util.AccountProperties;
import org.tolven.web.util.AccountUserProperties;

/**
 * Faces Action bean concerned with the user and account registration process.
 * @author John Churin
 */
public class RegisterAction extends TolvenAction {

    public static final String UNFORMATTED = "Unformatted";
    public static final String FORMATTED = "Formatted";

    protected TolvenPerson tp;
    private String uid;
    private String repeatUid;
    private String repeatUserPassword;
    private String oldUserPassword;
    private String testResult;

    private Account account;
    
    private AccountType accountType;

    private List<SelectItem> accountDescs;
	private List<AccountType> accountTypes;
    private String newAccountTypeStr;
    //selected account type id for new account 
    private String knownType;
    
    private String accountTitle;
    private String accountTimeZone;
    private String accountLocale;
    private String newAccountTitle;
    private Boolean enableBackButton;
    private Boolean disableAutoRefresh;
    private Boolean manualMetadataUpdate;
    
    private String newTimeZone;
    private boolean genDemoData;
    
    private boolean clearRememberDefault;
    private boolean noActivation;
    private Boolean referenceRequired;
    private String sponsorshipTitle;
    private List<Sponsorship> sponsorships;
    private List<SponsoredUser> sponsoredUsers;
    
	private FileItem likenessFile;
	
	private String newDemoUser;
	
	private List<SelectItem> timeZones;
	public List<SelectItem> localesList;
				
    // Search Criteria
    private String searchCriteria;
    private String searchField;
    private int maxResults;
    private List<TolvenPerson> searchResults;
    private int timeLimit; // In milliseconds
    private String elapsedTime = null;
    
    private TolvenUser user;
    // A list of all accounts this user is a member of
    private List<AccountUser> userAccounts;
    // A list of all users for an account
    private List<AccountUser> accountUsers;
    
    private String userLevelPref;
    private String sendErrorEmailTo;
    private String userCertificateUpload;
    private boolean formattedUserCertificate = true;
    private static CertificateFactory certificateFactory;
    private Logger logger = Logger.getLogger(RegisterAction.class);
    
    
    /** Creates a new instance of RegisterAction 
     * @throws NamingException */
    public RegisterAction() {
        maxResults = 100;
        timeLimit = 1000;
        searchField = "uid=";
        searchCriteria = "*";
    }


	public String createNewAccount() throws Exception {
        Account account = null;
        try {
            account = getAccountBean().createAccount2(getNewAccountTitle(), getNewTimeZone(), getNewAccountType());
            logger.info("Created account: " + account.getId() + ", acct type " + getNewAccountType().getKnownType());
            // Note, the user automatically gets administrator permission because they are the only user on that new account.
            getAccountBean().addAccountUser(account, getUser(), getNow(), true, getUserPublicKey());
            logger.info("Added first admin to: " + account.getId() + ", acct type " + getNewAccountType().getKnownType());
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage("newAccountForm:createAccount", new FacesMessage("Failed to create Account: " + ExceptionFormatter.toSimpleStringMessage(ex, ",", true)));
            return "fail";
        }
		accountUsers = null;
//        menu.createDefaultMenuStructure( account );
		getMenuLocal().updateMenuStructure(account);
        if (this.isGenDemoData()) {
        	// Clinical account
        	if ("echr".equals(getNewAccountType().getKnownType())) {
	        	GenControlCHRAccount control = new GenControlCHRAccount();
	        	control.setUserId( getSessionTolvenUserId());
	        	control.setChrAccountId(account.getId());
	        	control.setNow(getNow());
	        	control.setStartYear( 2007 );
            	control.setCount( 10 );
            	getGeneratorBean().queueGeneration(control);
        	}
        	// Personal account
        	if ("ephr".equals(getNewAccountType().getKnownType())) {
	        	GenControlPHRAccount control = new GenControlPHRAccount();
	        	control.setUserId( getSessionTolvenUserId());
	        	control.setFamilyName(getTop().getTp().getSn());
	        	control.setChrAccountId(account.getId());
	        	control.setNow(getNow());
	        	control.setStartYear( 2007 );
	        	getGeneratorBean().queueGeneration(control);
        	}
        }
        TolvenSSO.getInstance().updateAccountUserTimestamp(getRequest());
		return "success";
	}

    /**
     * Read-only attribute to get the account associated with this user
     * @return
     */
    public Account getAccount() {
    	if (account==null) {
    		account = getAccountBean().findAccount(getSessionAccountId());
    	}
    	return account;
    }
    
	/**
	 * Return true if the current account has new configuration/metadata available.
	 * @return
	 */
	public boolean getNewMetadataAvailable() {
		return !getAccountBean().isAccountTemplateCurrent(getAccount());
	}

    /**
     * Action to update the account
     * @return "success"
     */
    public String updateAccount() {
    	Account account = getAccountUser().getAccount();
    	account.setTitle(accountTitle);// Don't use accessor, doesn't work right when resetting locale to null
    	account.setTimeZone(accountTimeZone);// Don't use accessor, doesn't work right when resetting locale to null
    	account.setLocale(accountLocale); // Don't use accessor, doesn't work right when resetting locale to null
    	account.setEnableBackButton( enableBackButton );
    	account.setDisableAutoRefresh( disableAutoRefresh );
    	account.setManualMetadataUpdate(manualMetadataUpdate);
    	getTop().getAccountUser().getAccount().getProperty().put(AccountProperties.ERROR_EMAIL_TO,sendErrorEmailTo);
//    	TolvenLogger.info( "disableAutoRefresh: " + disableAutoRefresh, RegisterAction.class );
    	getAccountBean().updateAccount(account);
//    	getTop().setAccountTitle(getAccountTitle());
//    	getTop().setAccountTimeZone(getAccount().getTimeZone());
    	return "success";
    }
    public String saveUserPreferences(){
    	getTop().getAccountUser().getProperty().put(AccountUserProperties.USER_LEVEL_PREF,userLevelPref);
    	return "success";
    }
    
    public String setFormat()
    	{
    		TolvenUser user = getTop().getAccountUser().getUser();
    		Invitation invitation = new Invitation();
    		invitation.setEmailFormat(user.getEmailFormat());
    		String email = invitation.getEmailFormat();
    		return email;
    	}

    public String sendTestMessage( ) throws Exception {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	ExpressionEvaluator ee = new ExpressionEvaluator();
    	ee.addVariable("now", getNow());
    	ee.addVariable("subject", "Test Message");
    	ee.addVariable("bodyProperty", "org.tolven.message.testMessage");
    	ee.addVariable("accountUser", getAccountUser());
    	ee.addVariable("brand", request.getLocalAddr());
        getInvitationLocal().sendMessage( ee );

        FacesContext.getCurrentInstance().addMessage( "register:uid", new FacesMessage("Test message sent"));
        return "success";
    }

    /**
     * <p>Register a new user. 
     * After the registration form has collected initial information such as username and password,
     * begin the registration process. We'll generate and send an eMail to validate the user's email address.
     * The message will contain a link to an invitation, that we create, that allows us to finish the
     * process.</p> 
     * <p> In this method, we take the UID and add it as an email address to LDAP. Thus, user id is email address of user. </p>
     */
    /*
    public String register() throws Exception {
    	if (!Boolean.parseBoolean(getTolvenPropertiesBean().getProperty("tolven.login.create.activatedUser"))) {
    		throw new SecurityException( "tolven property tolven.login.create.activatedUser must be true");
    	}
        boolean error = false;
        // Make the UID the email address
        String separateUID = getTolvenPropertiesBean().getProperty("tolven.register.separateUID");
        if (separateUID==null || !Boolean.parseBoolean(separateUID)) {
       		getTp().addMail( getTp().getUid() );
        }
        if (!getRepeatUid().equals(getTp().getPrimaryMail())) {
            FacesContext.getCurrentInstance().addMessage( "register:email", new FacesMessage("Both email addresses must match"));
            error = true;
        }
        if (!getTp().getPrimaryMail().matches("[\\w\\.\\_\\-\\@]+") ) {
            FacesContext.getCurrentInstance().addMessage( "register:email", new FacesMessage("Does not appear to be a valid email address"));
            error = true;
        }
        if (getTp().getOrganizationUnitName() == null || getTp().getOrganizationUnitName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "register:organizationUnitName", new FacesMessage("An Organization Unit Name is required"));
            error = true;
        }
        if (getTp().getOrganizationName() == null || getTp().getOrganizationName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "register:organizationName", new FacesMessage("An Organization Name is required"));
            error = true;
        }
        if (getTp().getStateOrProvince() == null || getTp().getStateOrProvince().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "register:stateOrProvince", new FacesMessage("A State Or Province is required"));
            error = true;
        }
        if (getTp().getCountryName() == null || getTp().getCountryName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "register:countryName", new FacesMessage("A Country Name is required"));
            error = true;
        }
        if (!getRepeatUserPassword().equals(getTp().getUserPassword())) {
            FacesContext.getCurrentInstance().addMessage( "register:userPassword2", new FacesMessage("Both passwords must match"));
            error = true;
        }
        if (error) return "error";
        getTp().setCn( getTp().getGivenName() + " " + getTp().getSn());
        // Verify email address is unique in LDAP
        if (getLDAPLocal().entryExists( tp.getUid() )) {
           FacesContext.getCurrentInstance().addMessage( "register:uid", new FacesMessage("This id is already in use, please select another"));
           return "error";
        }
        String returnStatus = null;
        // Option to register with activation (or without - for demo)
        if (this.isNoActivation()) {
    		try {
    		    getLoginBean().registerAndActivate( tp, getNow() );
			} catch (Throwable e) {
				while (e.getCause()!=null) {
					if (e.getCause() instanceof NoResultException ) {
						FacesContext.getCurrentInstance().addMessage( "register:referenceCode", new FacesMessage("Invalid Reference Code"));
						return "error";
					}
					e = e.getCause();
				}
				throw new TolvenSecurityException( "Error Registering user: " + getTp().getUid(), e);
			}
			returnStatus = Status.ACTIVATED.value();
			// Make the user available to top (session)
			getTop().setTp(tp);
        } else {
    		try {
    	        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	        // Create the user and the invitation. The user will be invited to join a new, empty account.
    	        // Other invitations may reference an existing account or a referral account.
    		    getLoginBean().register( getTp(), getNow(), request.getLocalAddr());
			} catch (Throwable e) {
				while (e.getCause()!=null) {
					if (e.getCause() instanceof NoResultException ) {
						FacesContext.getCurrentInstance().addMessage( "register:referenceCode", new FacesMessage("Invalid Reference Code"));
						return "error";
					}
					e = e.getCause();
				}
				if (e instanceof Exception) throw (Exception)e;
			}
			returnStatus = Status.REGISTERED.value();

        }
        // Invite the user to join a new Tolven account (this could be optional if the user is expecting an invitation from someone else
        // inviting the user to join their account). but right now, we do it automatically.
        // Before we're done, we must invalidate session.
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
     
        return returnStatus;
    }
*/
    /*
     * TODO Note that this method is only here for backward compatibility. The only users who should be entering this information, should be
     * those we had the old-style DB UserPrivateKey, and are converting those into the new userPKCS12 key in LDAP
     */
    public String updateCertInfo() throws Exception {
        boolean error = false;
        if (getTp().getOrganizationUnitName() == null || getTp().getOrganizationUnitName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "createCertificate:organizationUnitName", new FacesMessage("An Organization Unit Name is required"));
            error = true;
        }
        if (getTp().getOrganizationName() == null || getTp().getOrganizationName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "createCertificate:organizationName", new FacesMessage("An Organization Name is required"));
            error = true;
        }
        if (getTp().getStateOrProvince() == null || getTp().getStateOrProvince().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "createCertificate:stateOrProvince", new FacesMessage("A State Or Province is required"));
            error = true;
        }
        if (getTp().getCountryName() == null || getTp().getCountryName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "createCertificate:countryName", new FacesMessage("A Country Name is required"));
            error = true;
        }
        if (getOldUserPassword() == null || getOldUserPassword().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "createCertificate:oldUserPassword", new FacesMessage("A password is required"));
            error = true;
        }
        if(!getLDAPLocal().verifyPassword(getTp().getUid(), getOldUserPassword().toCharArray())) {
            FacesContext.getCurrentInstance().addMessage("createCertificate:oldUserPassword", new FacesMessage("Incorrect password"));
            error = true;
        }
        if (error) return "error";
        /*
         * The user needs to logout in order for the credentials to be added to the Subject
         */
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        return "success";
    }
    /**
     * <p>Activate a new demo user (without email step). 
     * <p> In this method, UID and email address are kept separate in LDAP. </p>
     */
  /*
    public String activate() throws Exception {
    	if (!Boolean.parseBoolean(getTolvenPropertiesBean().getProperty("tolven.login.create.demoUser"))) {
    		throw new SecurityException( "tolven property tolven.login.create.demoUser must be true");
    	}
        boolean error = false;
        if (!getTp().getUid().matches("[\\w\\.\\_\\-]+")) {
            FacesContext.getCurrentInstance().addMessage( "register:uid", new FacesMessage("UserId can only contain letters, numbers, space, '.', '-', or '_'"));
            error = true;
        }
        if (!getRepeatUserPassword().equals(getTp().getUserPassword())) {
            FacesContext.getCurrentInstance().addMessage( "register:userPassword2", new FacesMessage("Both passwords must match"));
            error = true;
        }
        if (getTp().getOrganizationUnitName() == null || getTp().getOrganizationUnitName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "register:organizationUnitName", new FacesMessage("An Organization Unit Name is required"));
            error = true;
        }
        if (getTp().getOrganizationName() == null || getTp().getOrganizationName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "register:organizationName", new FacesMessage("An Organization Name is required"));
            error = true;
        }
        if (getTp().getStateOrProvince() == null || getTp().getStateOrProvince().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "register:stateOrProvince", new FacesMessage("A State Or Province is required"));
            error = true;
        }
        if (getTp().getCountryName() == null || getTp().getCountryName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage( "register:countryName", new FacesMessage("A Country Name is required"));
            error = true;
        }
        if (error) return "error";
        getTp().setCn(getTp().getGivenName() + " " + getTp().getSn());
        if (getLDAPLocal() == null) {
            throw new RuntimeException("getLDAPLocal() returned null");
        }
        // Verify uid is unique in LDAP
        //if (getLDAPLocal().entryExists(tp.getUid())) {
        //    FacesContext.getCurrentInstance().addMessage("register:uid", new FacesMessage("This id is already in use, please select another"));
        //    return "error";
        //}
        // Optional email address
//        getTp().addMail(getRepeatUid());
        String returnStatus = null;
        try {
            getLoginBean().registerAndActivate(tp, getNow());
        } catch (Exception e) {
            Throwable ex = e;
            while (ex.getCause() != null) {
                if (ex.getCause() instanceof NoResultException) {
                    FacesContext.getCurrentInstance().addMessage("register:referenceCode", new FacesMessage("Invalid Reference Code"));
                    return "error";
                }
                ex = ex.getCause();
            }
            throw e;
        }
        returnStatus = Status.ACTIVATED.value();
        return returnStatus;
    }
*/
    /**
     * Update user preferences. If password specified, both versions must match. If password not supplied, it is not changed.
     */
     public String updatePrefs( ) throws Exception {
        boolean error = false;
        /*
        if (getTp().getGivenName() == null || getTp().getGivenName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("register:givenName", new FacesMessage("A First Name must be supplied"));
            error = true;
        }
        if (getTp().getSn() == null || getTp().getSn().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("register:sn", new FacesMessage("A Surname must be supplied"));
            error = true;
        }
        */
        if (getOldUserPassword() == null || getOldUserPassword().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("register:oldUserPassword", new FacesMessage("Password must be supplied"));
            error = true;
        }
        String sshaUserPassword = getTolvenPersonString("userPassword");
        boolean passwordVerfied = SSHA.checkPassword(getOldUserPassword().toCharArray(), sshaUserPassword);
        if(!passwordVerfied) {
            FacesContext.getCurrentInstance().addMessage("register:oldUserPassword", new FacesMessage("Incorrect password"));
            error = true;
        }
        if (error) return "error";
        // Recalculate new CN
        //TODO XAccountSWAP the following line cannot be made in V2
        //getTp().setCn( getTp().getGivenName() + " " + getTp().getSn());
        // Send it to LDAP
        //getLDAPLocal().updatePerson( getTp() );
        // And update the user object now, too
        if (getUser() != null) {
            getActivationBean().updateUser(getUser());
        }
        // Reset tolven person (so it picks up the new LDAP changes)
        getTop().setTp(null);
        FacesContext.getCurrentInstance().addMessage( null, new FacesMessage("Preference update completed " + new Date()));
        // If there's a new likeness, load it also.
        if (likenessFile!=null) {
            logger.info( "Uploaded file: " + likenessFile);
        }
        // See if defaultAccount needs to be cleared.
        if (isClearRememberDefault()) {
            AccountUser au = getActivationBean().findDefaultAccountUser(getUser());
            if (au!=null) {
                au.setDefaultAccount(false);
            }
        }
        return "success";
     }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

     public String verifyPassword() throws Exception {
     boolean error = false;
     
     if (getOldUserPassword() == null || getOldUserPassword().trim().length() == 0) {
         FacesContext.getCurrentInstance().addMessage("check:password", new FacesMessage("Password must be supplied"));
         error=true;
     }
     if (error) return "error";
     if(!getLDAPLocal().verifyPassword(getTp().getUid(), getOldUserPassword().toCharArray())) {
         FacesContext context = FacesContext.getCurrentInstance(); 
         FacesMessage message = new FacesMessage("Invalid Password"); 
         context.addMessage("check:password", message); 
         return "error";
     }
	HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//	TolvenLogger.info(getClass() + " REGISTER ACTION :VESTIBULE_PASS=" + session.getAttribute(GeneralSecurityFilter.VESTIBULE_PASS));
	session.setAttribute(GeneralSecurityFilter.VESTIBULE_PASS, "true");
	return "success";
 }
     
     /**
      * Upload a photo
      * @return
      */
     public String upload( ) {
    	 return "success";
     }

     /**
      * Store the selected likeness in the current user object. If the likenessId (docId) is zero, the likeness setting is cleared.
      * @return "success"
     * @throws IOException 
     * @throws NamingException 
     * @throws GeneralSecurityException 
      */
    public String selectAsLikeness() throws NamingException, GeneralSecurityException {
        long docId = getRequestParameterAsLong("selectedLikenessId");
        if (docId != 0) {
            getUser().setLikeness(getDocumentLocal().findDocument(docId));
        } else {
            getUser().setLikeness(null);
        }
        return "success";
    }

     /**
     * Search for matching names in LDAP.
     * @throws GeneralSecurityException 
     */
    public String search( ) throws GeneralSecurityException {
        long startTime = System.currentTimeMillis();
        setSearchResults(getLDAPLocal().search( getSearchField() + getSearchCriteria(), getMaxResults(), getTimeLimit()));
        double elapsed = (System.currentTimeMillis() - startTime);
        setElapsedTime(String.format("Elapsed: %.3f sec ", elapsed/1000));
        return "success";
    }

    /**
     * This method is called after the user is logged in for the first time after clicking the
     * link in the verification email. The ID provided in the link should take the user here.
     * We'll lookup the supplied ID and ensure it is valid for this user. If it is, we'll activate the account.
     * If not, we'll advise the user to try again. 
     */
    public String verify() {
        return "verified";
    }

    /*
     * Get a TolvenPerson. The first time called, we just create an empty TolvenPerson if no one is logged in, otherwise,
     * get a copy of the logged in person.
     */
    public TolvenPerson getTp() {
        if (tp == null) {
            Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
            if (principal != null) {
                //tp=getLDAPLocal().createTolvenPerson( principal.getName() );
                //Attribute are now injected to the request before calling Tolven
                //These may in future be accessed directly rather than by TolvenPerson
                tp = new TolvenPerson();
                tp.setCn(getTolvenPersonString("cn"));
                tp.setDn(getTolvenPersonString("dn"));
                tp.setGivenName(getTolvenPersonString("givenName"));
                tp.setOrganizationName(getTolvenPersonString("o"));
                tp.setOrganizationUnitName(getTolvenPersonString("ou"));
                tp.setSn(getTolvenPersonString("sn"));
                tp.setStateOrProvince(getTolvenPersonString("st"));
                tp.setUid(getTolvenPersonString("uid"));
                tp.setMail(TolvenSSO.getInstance().getTolvenPersonList("mail", getRequest()));
            }
        }
        return tp;
    }
    
    protected void initializeTolvenPerson(TolvenPerson tolvenPerson) {
    }
    
    public void setTp(TolvenPerson tp) {
        this.tp = tp;
    }


    public String getRepeatUid() {
        return repeatUid;
    }

    public void setRepeatUid(String repeatUid) {
        this.repeatUid = repeatUid;
    }

    public String getOldUserPassword() {
        return oldUserPassword;
    }

    public void setOldUserPassword(String oldUserPassword) {
       this.oldUserPassword = oldUserPassword;
    }

    public String getRepeatUserPassword() {
        return repeatUserPassword;
    }

    public void setRepeatUserPassword(String repeatUserPassword) {
       this.repeatUserPassword = repeatUserPassword;
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<TolvenPerson> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<TolvenPerson> searchResults) {
        this.searchResults = searchResults;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

	public FileItem getLikenessFile() {
		return likenessFile;
	}

	public void setLikenessFile(FileItem likenessFile) {
		this.likenessFile = likenessFile;
	}

	public String getTestResult() {
		return testResult;
	}

	public void setTestResult(String testResult) {
		this.testResult = testResult;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public boolean isNoActivation() {
		return noActivation;
	}

	public void setNoActivation(boolean noActivation) {
		this.noActivation = noActivation;
	}

    public String getUserCertificateString() {
        if(isFormattedUserCertificate()) {
            X509Certificate x509Certificate = TolvenSSO.getInstance().getUserX509Certificate(getRequest());
            if(x509Certificate == null) {
                return null;
            } else {
                return x509Certificate.toString();
            }
        } else {
            return TolvenSSO.getInstance().getUserX509CertificateString(getRequest());
        }
    }

	public List<AccountUser> getAccountUsers() {
		if (accountUsers==null) accountUsers = getActivationBean().findAccountUsers( getAccount() );
			for ( AccountUser accUser : accountUsers ) {
				Account acc = accUser.getAccount();
				if ( acc != null ){
					acc.getAccountType();
					//force fetch of AccountType to get hold of KnownType
				}
			}
		return accountUsers;
	}

	public void setAccountUsers(List<AccountUser> accountUsers) {
		this.accountUsers = accountUsers;
	}

	public String getAccountTitle() {
		if (accountTitle==null) accountTitle = getAccount().getTitle();
		return accountTitle;
	}

	public void setAccountTimeZone(String accountTimeZone) {
		this.accountTimeZone = accountTimeZone;
	}

	public String getAccountTimeZone() {
		if (accountTimeZone==null) accountTimeZone = getAccount().getTimeZone();
		return accountTimeZone;
	}

	public void setAccountLocale(String accountLocale) {
		this.accountLocale = accountLocale;
	}

	public String getAccountLocale() {
		if (accountLocale==null) accountLocale = getAccount().getLocale();
		return accountLocale;
	}

	public void setAccountTitle(String accountTitle) {
		this.accountTitle = accountTitle;
	}

	public List<AccountUser> getUserAccounts() {
		if (userAccounts==null) userAccounts = getActivationBean().findUserAccounts(getUser());
			for ( AccountUser accountUser : userAccounts ) {
				accountUser.getAccount().getAccountType().getKnownType();
//				TolvenLogger.info("TYPE "+ atype);
			}
		return userAccounts;
	}

	public void setUserAccounts(List<AccountUser> userAccounts) {
		this.userAccounts = userAccounts;
	}
	
	
    public List<SelectItem> getAccountDescs() {
        if (accountDescs == null) {
            accountTypes = getAccountBean().findActiveAccountTypes();
            Comparator<AccountType> comparator = new Comparator<AccountType>() {
                public int compare(AccountType a1, AccountType a2) {
                    return (a1.getLongDesc().compareTo(a2.getLongDesc()));
                };
            };
            Collections.sort(accountTypes, comparator);
            accountDescs = new ArrayList<SelectItem>(accountTypes.size());
            for (AccountType accountType : accountTypes) {
                if (accountType.isCreatable()) {
                    accountDescs.add(new SelectItem(Long.toString(accountType.getId()), accountType.getLongDesc()));
                }
            }
        }
        return accountDescs;
    }
	
    public String addDemoUser() {
        return inviteUser(false);
    }

    public String updateAccountUserCertificate() {
        return inviteUser(true);
    }

    private String inviteUser(boolean isReinvite) {
        if (getNewDemoUser() == null) {
            return "success";
        }
        String uid = getNewDemoUser().toLowerCase().trim();
        TolvenUser invitedUser = getActivationBean().findUser(uid);
        if (invitedUser == null) {
            FacesContext.getCurrentInstance().addMessage("userAccess:uid", new FacesMessage("User " + uid + " not found"));
            return "fail";
        }
        AccountUser accountUser = getAccountBean().findAccountUser(uid, getAccount().getId());
        if (isReinvite) {
            if (accountUser == null) {
                FacesContext.getCurrentInstance().addMessage("userAccess:uid", new FacesMessage("User " + uid + " does not exist on this account"));
                return "fail";
            }
        } else {
            if (accountUser != null) {
                FacesContext.getCurrentInstance().addMessage("userAccess:uid", new FacesMessage("User " + uid + " already exists on this account"));
                return "fail";
            }
        }
        PublicKey publicKey = null;
        try {
            if (getUserCertificateUpload() != null && getUserCertificateUpload().length() > 0) {
                byte[] userX509CertificateBytes = getUserCertificateUpload().getBytes("UTF-8");
                X509Certificate x509Certificate = (X509Certificate) getCertificateFactory().generateCertificate(new ByteArrayInputStream(userX509CertificateBytes));
                publicKey = x509Certificate.getPublicKey();
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage("userAccess:uid", new FacesMessage("Failed to convert to X509Certificate: " + ExceptionFormatter.toSimpleStringMessage(ex, ",", true)));
            return "fail";
        }
        try {
            getAccountBean().inviteAccountUser(getAccount(), getAccountUser(), invitedUser, getUserPrivateKey(), getNow(), false, publicKey, isReinvite);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage("userAccess:uid", new FacesMessage("Failed to invite user to Account: " + ExceptionFormatter.toSimpleStringMessage(ex, ",", true)));
            return "fail";
        }
        FacesContext.getCurrentInstance().addMessage("userAccess:uid", new FacesMessage("User " + uid + " added to this account"));
        // force a refresh of the list
        accountUsers = null;
        uid = null;
        setUserCertificateUpload(null);
        return "success";
    }

	public List<Sponsorship> getSponsorships() {
		if (sponsorships==null) sponsorships = getAccountBean().findAccountSponsorships( getAccount() );
		return sponsorships;
	}

	/**
	 * Add a new sponsorship to this account.
	 * @return
	 */
	public String addSponsorship( ) {
	    getAccountBean().createSponsorship(getAccount(), getSponsorshipTitle());
		// force a requery of the sponsorships
		sponsorships = null;
		return "success";
	}

	public String getNewDemoUser() {
		return newDemoUser;
	}

	public void setNewDemoUser(String newDemoUser) {
		this.newDemoUser = newDemoUser;
	}

    public List<SelectItem> getTimeZones() {
        if (timeZones==null) {
            timeZones = new ArrayList<SelectItem>( 30 );
            String zones[] = TimeZone.getAvailableIDs();
            timeZones.add(new SelectItem( null, "Use Default"));
            timeZones.add(new SelectItem( "Europe/Paris", "Europe/Paris"));
            timeZones.add(new SelectItem( "Europe/London", "Europe/London"));
            timeZones.add(new SelectItem( "America/New_York", "America/New_York"));
            timeZones.add(new SelectItem( "America/Chicago", "America/Chicago"));
            timeZones.add(new SelectItem( "America/Denver", "America/Denver"));
            timeZones.add(new SelectItem( "America/Phoenix", "America/Phoenix"));
            timeZones.add(new SelectItem( "America/Los_Angeles", "America/Los_Angeles"));
            for (String zone : zones) {
                timeZones.add(new SelectItem( zone, zone));
            }
        }
        return timeZones;
    }
    
	public List<SelectItem> getLocalesList() {
		if (localesList==null) {
			Locale list[] = Locale.getAvailableLocales();
			localesList = new ArrayList<SelectItem>();
			
			//Add "default" as en_US at the top of the list
			//The leading space is used so that it doesn't get messed up in the sorting.
			localesList.add(new SelectItem(null, " Use Default"));
			for (Locale userLocale : list) {
				String localeDesc;
				String localeCode = userLocale.toString();
				if (userLocale.getCountry() == "") {
					localeDesc = userLocale.getDisplayLanguage() + " (" + userLocale.getLanguage() + ")";
				} else {
					localeDesc = userLocale.getDisplayLanguage() + " - " + userLocale.getDisplayCountry() + " (" + userLocale.getLanguage() + "_" + userLocale.getCountry() + ")";	
				}
	
				//This list is unsorted
				localesList.add(new SelectItem(localeCode, localeDesc));
			}
			
			//Create a comparator for locale display language-country pairs
			class LabelSort implements Comparator<Object> {
	
				public int compare(Object o1, Object o2) {
					SelectItem localeLabel1 = (SelectItem) o1;
					SelectItem localeLabel2 = (SelectItem) o2;
					if (localeLabel1.getLabel().equals(localeLabel2.getLabel())) return 0;
					if (localeLabel1.getLabel().compareTo(localeLabel2.getLabel()) < 0) return -1;
					return 1;
				}
			}
			
			//Sorted list
			java.util.Collections.sort(localesList, new LabelSort());
		}
		return localesList;
	}

	public boolean isReferenceRequired() {
        if (referenceRequired == null) {
            referenceRequired = Boolean.parseBoolean(getTolvenPropertiesBean().getProperty("tolven.register.referenceRequired"));
        }
        return referenceRequired;
    }

	public String getSponsorshipTitle() {
		return sponsorshipTitle;
	}

	public void setSponsorshipTitle(String sponsorshipTitle) {
		this.sponsorshipTitle = sponsorshipTitle;
	}

	public List<SponsoredUser> getSponsoredUsers() {
		if (sponsoredUsers==null) {
			sponsoredUsers = getAccountBean().findSponsoredUsersForAccount(getAccount());
		}
		return sponsoredUsers;
	}

	public void setSponsoredUsers(List<SponsoredUser> sponsoredUsers) {
		this.sponsoredUsers = sponsoredUsers;
	}

	public String getNewAccountTitle() {
		return newAccountTitle;
	}

	public void setNewAccountTitle(String newAccountTitle) {
		this.newAccountTitle = newAccountTitle;
	}

	public boolean isGenDemoData() {
		return genDemoData;
	}

	public void setGenDemoData(boolean genDemoData) {
		this.genDemoData = genDemoData;
	}

	public String getNewTimeZone() {
		return newTimeZone;
	}

	public void setNewTimeZone(String newTimeZone) {
		this.newTimeZone = newTimeZone;
	}

	public AccountType getNewAccountType() {
		return getAccountBean().findAccountType(getAccountTypeId());
	}
	
	public String getNewAccountTypeStr() {
		return newAccountTypeStr;
	}

	public long getAccountTypeId(){ 
		return Long.parseLong( newAccountTypeStr);
	}

	public void setNewAccountTypeStr(String newAccountTypeStr) {
		this.newAccountTypeStr = newAccountTypeStr;
	}
	public String getKnownType() {
		if (knownType==null) knownType = getAccount().getAccountType().getKnownType();
		return knownType;
	}

	public boolean getEnableBackButton() {
		if( enableBackButton == null ) enableBackButton = getAccount().isEnableBackButton();
		if( enableBackButton == null ) enableBackButton = false;
		return enableBackButton;
	}

	public boolean isEnableBackButton() {
		return getEnableBackButton();
	}

	public void setEnableBackButton(boolean enableBackButton) {
		if( this.enableBackButton == null ) this.enableBackButton = new Boolean( enableBackButton );
		else this.enableBackButton = enableBackButton;
	}

	public boolean isDisableAutoRefresh() {
		return getDisableAutoRefresh();
	}

	public boolean getDisableAutoRefresh()  {
		if( disableAutoRefresh == null ) disableAutoRefresh = getAccount().getDisableAutoRefresh();
		if( disableAutoRefresh == null ) disableAutoRefresh = false;
		return disableAutoRefresh;
	}

	public void setDisableAutoRefresh(boolean disableAutoRefresh) {
		this.disableAutoRefresh = disableAutoRefresh;
	}


	public boolean isManualMetadataUpdate() {
		if( manualMetadataUpdate == null ) manualMetadataUpdate = getAccount().getManualMetadataUpdate();
		if( manualMetadataUpdate == null ) manualMetadataUpdate = false;
		return manualMetadataUpdate;
	}


	public void setManualMetadataUpdate(boolean manualMetadataUpdate) {
		this.manualMetadataUpdate = manualMetadataUpdate;
	}
	
	public String getUserRoleString() throws PolicyContextException {
        Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
        StringBuffer buff = new StringBuffer();
        if (subject != null) {
            for (Principal principal : subject.getPrincipals()) {
                if (principal instanceof Group && "Roles".equals(principal.getName())) {
                    java.util.Enumeration<?> enumeration = ((Group) principal).members();
                    Principal rolePrincipal = null;
                    while (enumeration.hasMoreElements()) {
                        rolePrincipal = (Principal) enumeration.nextElement();
                        buff.append(rolePrincipal.getName());
                        if (enumeration.hasMoreElements()) {
                            buff.append(", ");
                        }
                    }
                    break;
                }
            }
        }
        return buff.toString();
    }
	
	public String getLoginPage() {
	    return "/private/login.html";
	}
	
	public AccountUser getDefaultAccount() {
		return getActivationBean().findDefaultAccountUser(getUser());
	}
	
	public boolean isClearRememberDefault() {
		return clearRememberDefault;
	}

	public void setClearRememberDefault(boolean clearRememberDefault) {
		this.clearRememberDefault = clearRememberDefault;
	}


	public String getUserLevelPref() {
		userLevelPref = getTop().getAccountUser().getProperty().get(AccountUserProperties.USER_LEVEL_PREF);
		// If there is no level set already default it to Standard
		if(userLevelPref == null){
			userLevelPref = "Standard";
			getTop().getAccountUser().getProperty().put(AccountUserProperties.USER_LEVEL_PREF,userLevelPref);
		}
		return userLevelPref;
	}


	public void setUserLevelPref(String userLevelPref) {
		this.userLevelPref = userLevelPref;
	}


	public String getSendErrorEmailTo() {
		return getTop().getAccountUser().getAccount().getProperty().get(AccountProperties.ERROR_EMAIL_TO);
	}


	public void setSendErrorEmailTo(String sendErrorEmailTo) {
		this.sendErrorEmailTo = sendErrorEmailTo;
	}


    public String getUserCertificateUpload() {
        return userCertificateUpload;
    }


    public void setUserCertificateUpload(String userCertificateUpload) {
        this.userCertificateUpload = userCertificateUpload;
    }

    public CertificateFactory getCertificateFactory() {
        if (certificateFactory == null) {
            try {
                certificateFactory = CertificateFactory.getInstance("X509");
            } catch (CertificateException ex) {
                throw new RuntimeException("Could not get instance of CertificateFactory", ex);
            }
        }
        return certificateFactory;
    }

    public boolean isFormattedUserCertificate() {
        return formattedUserCertificate;
    }


    public String getUserCertificateDisplay() {
        return getUserCertificateDisplay(isFormattedUserCertificate());
    }

    public String getUserCertificateAlternativeDisplay() {
        return getUserCertificateDisplay(!isFormattedUserCertificate());
    }

    public String toggleUserCertificate() {
        boolean formatted = Boolean.parseBoolean(getRequest().getParameter("formatted"));
        formattedUserCertificate = !formatted;
        return "success";
    }
    
    private String getUserCertificateDisplay(boolean formatted) {
        if (formatted) {
            return FORMATTED;
        } else {
            return UNFORMATTED;
        }
    }
    
}
