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
/**
 * This file contains AutomationBean.
 *
 * @package org.tolven.app.bean
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app.bean;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.security.PublicKey;

import org.apache.log4j.Logger;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.AutomationLocal;
import org.tolven.app.AutomationRemote;
import org.tolven.app.CCHITLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.ActivationLocal;
import org.tolven.core.InvitationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.RulesLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.rules.WMLogger;
import org.tolven.security.LoginLocal;
import org.tolven.security.TolvenPerson;
import org.tolven.security.key.DocumentSecretKey;
import org.tolven.trim.AD;
import org.tolven.trim.Act;
import org.tolven.trim.CE;
import org.tolven.trim.CESlot;
import org.tolven.trim.DataType;
import org.tolven.trim.EN;
import org.tolven.trim.Entity;
import org.tolven.trim.LivingSubject;
import org.tolven.trim.ObservationValueSlot;
import org.tolven.trim.Role;
import org.tolven.trim.TSSlot;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TELEx;
import org.tolven.trim.ex.TSEx;

/**
 * To create a tolven user for patient and to create an account and patient inside that account.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	| 06/14/2010           	| Valsaraj Viswanathan 	| Initial Version.
2		| 03/15/2011			| Valsaraj Viswanathan 	| Replaced CCHITActivationBean reference with ActivationBean.
==============================================================================================================================================
*/
@Stateless
@Local(AutomationLocal.class)
@Remote(AutomationRemote.class)
public class AutomationBean implements AutomationLocal, AutomationRemote {
	@EJB private AccountDAOLocal accountBean;
	@EJB private CCHITLocal cchitBean;
	@EJB private MenuLocal menuBean;
    @EJB private LoginLocal loginBean;
    @EJB private InvitationLocal mailBean;
    @EJB private XMLLocal xmlBean;
    @EJB private TolvenPropertiesLocal propertyBean;
    @EJB private RulesLocal rulesBean;
    @EJB private ActivationLocal activationBean;
    
	private Logger logger = Logger.getLogger(this.getClass());
	private String newAccountTitle;
	private String newTimeZone;
	private String newAccountTypeStr;
	private long accountId;
	private TolvenPerson tp;
	private Trim trim;
	private String uId;
	private AppEvalAdaptor app;
	
	private static final String EPHR = "ephr";
	private static final String EMAIL_EXPRESSION =  "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public AutomationBean() {
		setNewTimeZone(null);
	}
	
	/**
	 * Creates PHR account for patient automatically.
	 * 
	 * @author valsaraj
	 * added on 06/14/2010
	 * @param trim - patient trim oject
	 * @param app - AppEvalAdaptor instance
	 */
	public void automateCreatePHR(Trim trim, AppEvalAdaptor app, PublicKey userPublicKey) {
		try {
			setTrim(trim);
			setApp(app);
			ActEx act = ((ActEx) ((ActEx) trim.getAct()).getRelationship().get("preference").getAct());
			setUId(((TELEx) trim.getAct().getParticipations().get(0).getRole()
					.getPlayer().getTelecom().getNullsAndURLSAndTELS().get(3)).getValue());
			
			if (act.getRelationship().get("createPHR").isEnabled() != false) {			
				if (validateEmail(getUId()) != false) {				
					createNewAccount(trim, app, userPublicKey);
						
					if (updateAccountInfo() != true) {
						List<ObservationValueSlot> obs = ((ActEx) getTrim()
								.getAct()).getRelationship().get("preference")
								.getAct().getRelationships().get(0).getAct()
								.getRelationships().get(2).getAct()
								.getObservation().getValues();
						obs.get(0).getST().setValue(
								String.valueOf(getAccountId()));
						obs.get(1).getST().setValue(getNewAccountTitle());
					}
					
					updateDoc();
				}
			} else {
				if (updateAccountInfo() != false) {
					updateDoc();
				}
			}
		} catch (Exception e) {
			logger.info("Invalid UID");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the account id and name if user selected one explicitly.
	 * 
	 * added on 07/12/2010
	 * @author valsaraj
	 * @return status - trim update status
	 */
	public boolean updateAccountInfo() {
		boolean status = false;
		List<ObservationValueSlot> obs = ((ActEx) getTrim().getAct())
				.getRelationship().get("preference").getAct()
				.getRelationships().get(0).getAct().getRelationships().get(2)
				.getAct().getObservation().getValues();
		String accountInfo = obs.get(0).getST().getValue();
		
		try {
			if (accountInfo != null && accountInfo.indexOf(" ") > 0) {
				String[] temp = accountInfo.split(" ");
				obs.get(0).getST().setValue(temp[temp.length - 1].replace(")", "").replace("(", ""));
				obs.get(1).getST().setValue(temp[0]);
				status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return status;
	}
	
	/**
	 * Updates submitted document.
	 * 
	 * added on 07/12/2010
	 * @author valsaraj
	 */
	public void updateDoc() {
		try {
			ByteArrayOutputStream trimXML = new ByteArrayOutputStream();
			xmlBean.marshalTRIM(getTrim(), trimXML);
			String kbeKeyAlgorithm = propertyBean
					.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
			int kbeKeyLength = Integer.parseInt(propertyBean
					.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
			getApp().getDocument().setAsEncryptedContent(trimXML.toByteArray(),
					kbeKeyAlgorithm, kbeKeyLength);
		} catch (Exception e) {
			logger.info("Failed to update TRIM");
		}
	}
	
	/**
	 * Creates new PHR account and tolven account user for patient automatically.
	 * 
	 * added on 06/14/2010
	 * @author valsaraj
	 * @param trim - patient trim oject
	 * @param app - AppEvalAdaptor instance
	 */
	public void createNewAccount(Trim trim, AppEvalAdaptor app, PublicKey userPublicKey) {
		try {
			TolvenUser user = getActivationBean().findUser(getUId());
						
			if (user == null) {
				user = getLoginBean().activate(getTp(), getApp().getNow());
			}
			
			setNewAccountTitle("ephr-" + user.getId());
			setNewAccountTypeStr(accountBean.findAccountTypebyKnownType(EPHR).getId().toString());
			List<Account> accountLists = cchitBean.getAccountsByTitleAndUser(getNewAccountTitle(), user, getAccountTypeId());
			
			if (accountLists.size() == 0) {		
				AccountUser accUser = getAccountBean().createAccount(getNewAccountType(), user,userPublicKey,new Date());
				Account phrAccount = accUser.getAccount();
				logger.info( "Created account: " + phrAccount.getId() + ", acct type " + getNewAccountType().getKnownType());
				//getAccountBean().addAccountUser( phrAccount, user, getApp().getNow(), true, userPublicKey);
				menuBean.updateMenuStructure(phrAccount);
		        setAccountId(phrAccount.getId());	
		        sendMail(getApp(), ePHRMesaage(getNewAccountTitle()));
		        createNewPatient(phrAccount.getId());
	        	
			} else {
				logger.info("Account exists");
				setAccountId(accountLists.get(0).getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates new patient inside PHR account.
	 * 
	 * added on 06/14/2010
	 * @author suja
	 * @param phrAccountId - PHR account id
	 */
	public void createNewPatient(long phrAccountId) {
		try {
    		Entity entity = (Entity) ((Role) ((Act) trim.getAct())
					.getParticipations().get(0).getRole()).getPlayer();
			EN en = entity.getName().getENS().get(0);
    		String firstName = en.getParts().get(0).getST().getValue();
    		String middleName = en.getParts().get(1).getST().getValue();
    		String lastName = en.getParts().get(2).getST().getValue();
    		String familyName = en.getParts().get(3).getST().getValue();
    		Date dob = null;
    		String gender = "";	        		
    		LivingSubject liv = entity.getLivingSubject();
    		CE genderCE= new CE();
    		
    		if (liv != null) {
    			TSSlot birthTimeSlot = liv.getBirthTime();
    			
    			if (birthTimeSlot != null) {
    				if (birthTimeSlot.getTS() != null) {
    					dob = ((TSEx)birthTimeSlot.getTS()).getDate();
    				}
    			}
    			
    			CESlot genderSlot = liv.getAdministrativeGenderCode();
    			
    			if (genderSlot != null) {
    				genderCE = genderSlot.getCE();
    				
    				if (genderCE != null) {
    					if ("C0015780".equals( genderCE.getCode() )) {
    						gender = "Female";
    					}
    					if ("C0024554".equals( genderCE.getCode() )) {
    						gender = "Male";
    					}
    				}
    			}
    		}
    		
    		AD ad = trim.getAct().getParticipations().get(0).getRole()
					.getPlayer().getPerson().getAddr().getADS().get(0);
			String homeAddr1 = ad.getParts().get(0).getST().getValue();
    		String homeAddr2 = ad.getParts().get(1).getST().getValue();
    		String homeCounty = ad.getParts().get(2).getST().getValue();
    		String homeState = ad.getParts().get(3).getST().getValue();
    		String homeCountry = ad.getParts().get(4).getST().getValue();
    		String homeZip = ad.getParts().get(5).getST().getValue();
    		List<DataType> tList = trim.getAct().getParticipations().get(0)
					.getRole().getPlayer().getTelecom().getNullsAndURLSAndTELS();
			String homeTelecom = ((TELEx) tList.get(0)).getValue();
    		String workTelecom = ((TELEx) tList.get(1)).getValue();
    		String cellTelecom = ((TELEx) tList.get(2)).getValue();
    		String eMail = ((TELEx) tList.get(3)).getValue();
    		String seMail = ((TELEx) tList.get(4)).getValue();
    		String race = "" ;
    		
    		if(trim.getAct().getParticipations().get(0).getRole().getPlayer().getPerson().getRaceCode().getCES() != null
    				&& trim.getAct().getParticipations().get(0).getRole().getPlayer().getPerson().getRaceCode().getCES().size() > 0) {
    			race = trim.getAct().getParticipations().get(0).getRole().getPlayer().getPerson().getRaceCode().getCES().get(0).getDisplayName();
    		}
    		
    		/*if (((ActEx) trim.getAct()).getRelationship().get(
					"patientOtherInfo").getAct().getObservation().getValues().get(1).getST() != null)
				race = ((ActEx) trim.getAct()).getRelationship().get("patientOtherInfo").getAct()
						.getObservation().getValues().get(1).getST().getValue();
    		*/
    		String ethnicity = "";
    		
    		if (trim.getAct().getParticipations().get(0).getRole().getPlayer().getPerson().getEthnicGroupCode().getCES() != null
					&& trim.getAct().getParticipations().get(0).getRole().getPlayer().getPerson().getEthnicGroupCode().getCES().size() > 0) {
    			for(CE anEthnicity : trim.getAct().getParticipations().get(0).getRole().getPlayer().getPerson().getEthnicGroupCode().getCES()) {
    				ethnicity += anEthnicity.getDisplayName()+",";
    			}
    			//remove trailing comma ,
    			ethnicity = ethnicity.substring(0, ethnicity.length()-1); 
    		}
    		
    		String language = "" ;
    		String languageCode = "" ;
    		
    		if(trim.getAct().getParticipations().get(0).getRole().getPlayer().getLanguageCommunications().size() > 0) {
    			language = trim.getAct().getParticipations().get(0).getRole().getPlayer().getLanguageCommunications().get(0).getLanguageCode().getCE().getDisplayName();
    			languageCode = trim.getAct().getParticipations().get(0).getRole().getPlayer().getLanguageCommunications().get(0).getLanguageCode().getCE().getCode();
    		}
    				
    				
    		/*if (((ActEx) trim.getAct()).getRelationship().get(
					"patientOtherInfo").getAct().getObservation().getValues().get(3).getCE() != null) {
				language = ((ActEx) trim.getAct()).getRelationship().get("patientOtherInfo").getAct()
							.getObservation().getValues().get(3).getCE().getDisplayName();
				languageCode = ((ActEx) trim.getAct()).getRelationship().get("patientOtherInfo").getAct()
								.getObservation().getValues().get(3).getCE().getCode();
			}*/
    		
    		/*String comments = "" ;
    		
    		if (((ActEx) trim.getAct()).getRelationship().get(
					"patientOtherInfo").getAct().getObservation().getValues().get(0).getST() != null)
				comments = ((ActEx) trim.getAct()).getRelationship().get("patientOtherInfo").getAct()
							.getObservation().getValues().get(0).getST().getValue();*/
    		
    		String mrn = "" ;
    		mrn = trim.getAct().getParticipations().get(0).getRole().getId().getIIS().get(0).getExtension();
    		
        	Account account = getAccountBean( ).findAccount(phrAccountId);
        	TolvenUser tolvenUser = activationBean.findUser( getTp().getUid() );
        	List <AccountUser> accountUsers= activationBean.findUserAccounts(tolvenUser);
        	
        	for(AccountUser au: accountUsers) {
				if (au != null && au.getAccount().getId() == account.getId()) {
					/************************ ephr:patient *******************************/
					MenuData mdPatient = new MenuData();
		        	MenuStructure msPatient = menuBean.findMenuStructure(au.getAccount().getId(), "ephr:patient");
		        	ExpressionEvaluator ee = new ExpressionEvaluator();
		        	ee.addVariable( "now", app.getNow());
		        	ee.addVariable(TrimExpressionEvaluator.ACCOUNT, au.getAccount());							
                    mdPatient.setMenuStructure(msPatient.getAccountMenuStructure());
                    mdPatient.setDocumentId(app.getDocument().getId());
                    mdPatient.setAccount(au.getAccount());
                    ee.addVariable("_placeholder", mdPatient);
                    ee.pushContext();	
                    menuBean.populateMenuData(ee, mdPatient);		                    
                    mdPatient.setString01(lastName); //lastName
                    mdPatient.setString02(firstName); //firstName
                    mdPatient.setString03(middleName); //middleName
                    mdPatient.setString08(familyName); //familyName
                    mdPatient.setString04(gender); //sex
                    
                    if (dob != null)
                    	mdPatient.setDate01(dob); //dob
                    
                    mdPatient.setExtendedField("gender", genderCE); //gender
                    mdPatient.setExtendedField("homeAddr1", homeAddr1); //homeAddr1
                    mdPatient.setExtendedField("homeAddr2", homeAddr2); //homeAddr2
                    mdPatient.setExtendedField("homeCounty", homeCounty); //homeCounty
                    mdPatient.setExtendedField("homeState", homeState); //homeState
                    mdPatient.setExtendedField("homeCountry", homeCountry); //homeCountry
                    mdPatient.setExtendedField("homeZip", homeZip); //homeZip
                    mdPatient.setExtendedField("homeTelecom", homeTelecom); //homeTelecom
                    mdPatient.setExtendedField("workTelecom", workTelecom); //workTelecom
                    mdPatient.setExtendedField("cellTelecom", cellTelecom); //cellTelecom
                    mdPatient.setExtendedField("eMail", eMail); //eMail
                    mdPatient.setExtendedField("seMail", seMail); //seMail
                    mdPatient.setExtendedField("race", race); //race
                    mdPatient.setString06(race); //race
                    mdPatient.setExtendedField("ethnicity", ethnicity); //ethnicity
                    mdPatient.setExtendedField("language", language); //language
                    mdPatient.setExtendedField("languageCode", languageCode); //languageCode
                    //mdPatient.setExtendedField("comments", comments); //comments
                    mdPatient.setString05(mrn); //mrn		                    
                    menuBean.persistMenuData(mdPatient);
                    ee.popContext();		                    
                    StatefulSession workingMemory;
                    RuleBase ruleBase;
                    ruleBase = rulesBean.getRuleBase(account.getAccountType().getKnownType());
            		workingMemory = ruleBase.newStatefulSession();
            		workingMemory.setGlobal("app", app);
            		workingMemory.setGlobal("now", app.getNow());
            		new WMLogger(workingMemory);
            		workingMemory.insert(mdPatient);
            		/************************ ephr:patients:all ***************************/
            		MenuData mdPatients = new MenuData();
            		MenuStructure msPatients = menuBean.findMenuStructure(au.getAccount().getId(), "ephr:patients:all");
            		ExpressionEvaluator ee1 = new ExpressionEvaluator();
		        	ee1.addVariable( "now", app.getNow());
		        	ee1.addVariable(TrimExpressionEvaluator.ACCOUNT, au.getAccount());							
		        	mdPatients.setMenuStructure(msPatients.getAccountMenuStructure());
		        	mdPatients.setDocumentId(app.getDocument().getId());
		        	mdPatients.setAccount(au.getAccount());
                    ee1.addVariable("_placeholder", mdPatients);
                    ee1.pushContext();	
                    menuBean.populateMenuData(ee1, mdPatients);	
                	mdPatients.setString01(lastName); //lastName
                	mdPatients.setString02(firstName); //firstName
                	mdPatients.setString03(middleName); //middleName
                	mdPatients.setString08(familyName); //familyName
                	mdPatients.setString04(gender); //sex
                	
                    if (dob!=null)
                    	mdPatients.setDate01(dob); //dob
                    
                    mdPatients.setExtendedField("gender", gender); //gender
                    mdPatients.setExtendedField("homeAddr1", homeAddr1); //homeAddr1
                    mdPatients.setExtendedField("homeAddr2", homeAddr2); //homeAddr2
                    mdPatients.setExtendedField("homeCounty", homeCounty); //homeCounty
                    mdPatients.setExtendedField("homeState", homeState); //homeState
                    mdPatients.setExtendedField("homeCountry", homeCountry); //homeCountry
                    mdPatients.setExtendedField("homeZip", homeZip); //homeZip
                    mdPatients.setExtendedField("homeTelecom", homeTelecom); //homeTelecom
                    mdPatients.setExtendedField("workTelecom", workTelecom); //workTelecom
                    mdPatients.setExtendedField("cellTelecom", cellTelecom); //cellTelecom
                    mdPatients.setExtendedField("eMail", eMail); //eMail
                    mdPatients.setExtendedField("seMail", seMail); //seMail
                    mdPatients.setExtendedField("race", race); //race
                    mdPatients.setString06(race); //race
                    mdPatients.setExtendedField("ethnicity", ethnicity); //ethnicity
                    mdPatients.setExtendedField("language", language); //language
                    mdPatients.setExtendedField("languageCode", languageCode); //languageCode
                   // mdPatients.setExtendedField("comments", comments); //comments
                    mdPatients.setString05(mrn); //mrn		                    
                    mdPatients.setReferencePath("ephr:patient-"+mdPatient.getId());
                    mdPatients.setReference(mdPatient);
                    menuBean.persistMenuData(mdPatients);
                    ee1.popContext();
                    workingMemory.insert(mdPatients);		                    
            	    workingMemory.fireAllRules();   
            	    workingMemory.dispose();
				}
			}
    	} catch (Exception e) {
    		e.printStackTrace();
		}
	}
	
	/**
	 * Creates tolven person with details from patient TRIM.
	 * 
	 * added on 06/14/2010
	 * @author valsaraj
	 * @return TolvenPerson
	 */
	public TolvenPerson getTp() {
		tp = new TolvenPerson();
		tp.setUid(getUId());
		String password = null;
		
		try {
			password = ((ActEx) ((ActEx) getTrim().getAct()).getRelationship()
					.get("preference").getAct()).getRelationship().get("createPHR").getAct()
					.getObservation().getValues().get(0).getST().getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tp.setUserPassword(password);
		String cn = "";
		String lastName = "";
		
		try {
			cn = getTrim().getAct().getParticipations().get(0).getRole()
					.getPlayer().getName().getENS().get(0).getParts().get(0).getST().getValue();
			tp.setGivenName(cn);
			lastName = getTrim().getAct().getParticipations().get(0).getRole()
					.getPlayer().getName().getENS().get(0).getParts().get(2).getST().getValue();
			cn += " " + lastName;
			tp.setSn(lastName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        tp.setCn(cn.isEmpty() ? "cn" : cn);        
        tp.setOrganizationUnitName("ou");
        tp.setOrganizationName("o");
        String state = "";
        
        try {
        	state = getTrim().getAct().getParticipations().get(0).getRole()
					.getPlayer().getPerson().getAddr().getADS().get(0)
					.getParts().get(3).getST().getValue();
        }
        catch (Exception e) {
        	logger.info("State field is not set");
		}
        
        tp.setStateOrProvince(state.isEmpty() ? "CA" : state);
        String country = "";
        
        try {
        	country = getTrim().getAct().getParticipations().get(0).getRole()
					.getPlayer().getPerson().getAddr().getADS().get(0)
					.getParts().get(4).getST().getValue();
        }
        catch (Exception e) {
			logger.info("Country field is not set");
		}
        
        tp.setCountryName(country.isEmpty() ? "us" : country);
        tp.setSn(lastName.isEmpty() ? "sn" : lastName);
        
		return tp;
	}

	/**
	 * Setup email to be send to patient.
	 * 
	 * added on 06/14/2010
	 * @author valsaraj
	 * @param accountName - account name
	 * @return Map<String,String> emailMap - email details
	 */
	public Map<String,String> ePHRMesaage(String accountName) {
		Map<String,String> emailMap = new HashMap<String,String>();
		emailMap.put("subject", "Electronic Personal Health Record account Information");
		emailMap.put("to", getUId());
		String body = "<html><body><p>Hi,</p><p>" +
						"We have created a PHR account (" + accountName + ") for you." +
						"<br/>You can access this account by login as " + getUId() +
						", PIN number as password. We recommend that you to change password after login." +
						"</p></body></html>";
		emailMap.put("body", body);
		
		return emailMap;
	}
	
	/**
	 * This method sends the mail.
	 * 
	 * added on 06/14/2010
	 * @author valsaraj
	 * @param AppEvalAdaptor - AppEvalAdaptor object
	 * @param Map<String,String> emailMap - email details
	 */
	public void sendMail(AppEvalAdaptor app, Map<String,String> emailMap) {
		try {
			ExpressionEvaluator ee = new ExpressionEvaluator();
			ee.addVariable("account", app.getAccount());
			ee.addVariable("to", emailMap.get("to"));
			ee.addVariable("subject", emailMap.get("subject"));
			ee.addVariable("body", emailMap.get("body"));
			ee.addVariable("now", app.getNow());
			mailBean.sendMessage(ee);
		} catch (Exception e) {
			logger.info("Failed to send message");
		}
	}

	/**
	 * Validates the email address
	 * @param email - the  email address to validate
	 * @return true or false
	 */
	public boolean validateEmail(String email){
		 Matcher matcher = Pattern.compile(EMAIL_EXPRESSION).matcher(email);
		  
		 return matcher.matches();
	}

	public void setTp(TolvenPerson tp) {
		this.tp = tp;
	}
	
	public AccountDAOLocal getAccountBean() {
		return accountBean;
	}

	public void setAccountBean(AccountDAOLocal accountBean) {
		this.accountBean = accountBean;
	}
	
	public String getNewAccountTitle() {
		return newAccountTitle;
	}

	public void setNewAccountTitle(String newAccountTitle) {
		this.newAccountTitle = newAccountTitle;
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

	public ActivationLocal getActivationBean() {
		return activationBean;
	}

	public void setActivationBean(ActivationLocal activationBean) {
		this.activationBean = activationBean;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public MenuLocal getMenuBean() {
		return menuBean;
	}

	public void setMenuBean(MenuLocal menuBean) {
		this.menuBean = menuBean;
	}

	public Trim getTrim() {
		return trim;
	}

	public void setTrim(Trim trim) {
		this.trim = trim;
	}

	public LoginLocal getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginLocal loginBean) {
		this.loginBean = loginBean;
	}

	public String getUId() {
		return uId;
	}

	public void setUId(String id) {
		uId = id;
	}

	public AppEvalAdaptor getApp() {
		return app;
	}

	public void setApp(AppEvalAdaptor app) {
		this.app = app;
	}
}