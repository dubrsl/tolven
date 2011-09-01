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
 * This file contains EmergencyAccountBean.
 *
 * @package org.tolven.app.bean
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.tolven.app.EmergencyAccountLocal;
import org.tolven.app.EmergencyAccountRemote;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountProperty;
import org.tolven.core.entity.Status;

/**
 * This bean is used to process emergency access account related operations.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	| 01/25/2011           	| Valsaraj Viswanathan 	| Initial Version 	
2		| 01/31/2011			| Valsaraj Viswanathan	| Added setEmergencyAccount(), getAccountProperty() to set an account emergency account.
3		| 02/01/2011			| Valsaraj Viswanathan	| Added isEmergencyAccount() to check whether the account is emergency account or not.
==============================================================================================================================================
*/
@Stateless
@Local(EmergencyAccountLocal.class)
@Remote(EmergencyAccountRemote.class)
public class EmergencyAccountBean {
	@PersistenceContext
	EntityManager em;
	@EJB
	AccountDAOLocal accountBean;
	Logger logger = Logger.getLogger(this.getClass());
	public static final String EMERGENCY_ACCOUNT_PROPERTY = 
		"tolven.cchit.emergencyAccount";
	
	/**
	 * Retrieves all study ids.
	 *
	 * added on 01/25/2011
	 * @author valsaraj
	 */
	public List<String> getAllStudyIds(String study) {
		List<String> studyIds = null;
		
		try {
			String queryStatement = "SELECT DISTINCT string01 FROM MenuData WHERE string02 = '" + study + "' and string01 is NOT NULL ";
			Query query = em.createQuery(queryStatement);
			studyIds = query.getResultList();
		} catch (Exception e) {
			logger.info("Exception in finding all study ids :" + e.getMessage());
			e.printStackTrace();
		}
		
		return studyIds;
	}
	
	/**
	 * Identifies patient with the given studyId and studyName.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj
	 * @param studyId - study id
	 * @param studyName - study name
	 * @return patients - matiching patient list
	 */
	public List<Object[]> getPatientIdByStudyId (String studyId, String studyName) {
		List<Object[]> patients = null;
		
		try {
			String queryStatement = "SELECT DISTINCT parentPath01, account FROM MenuData  WHERE string01 = '"+studyId+"' AND string02 = '"+studyName +"'";
			Query query = em.createQuery(queryStatement);
			patients = query.getResultList();
		} catch (Exception e) {
			logger.info("Exception in finding patient by study id :>>>>>>>>>>>.." + e.getMessage());
		}
		
		return patients;
	}
	
	/**
	 * Sets an account as emergency account.
	 * 
	 * added on 01/31/2011
	 * @author valsaraj
	 * @param accountIdStr - account id
	 */
	public void setEmergencyAccount(String accountIdStr) {
		setEmergencyAccount(Long.parseLong(accountIdStr));
	}
    
	/**
	 * Sets an account as emergency account.
	 * 
	 * added on 02/08/2011
	 * @author valsaraj
	 * @param accountId - account id
	 */
	public void setEmergencyAccount(long accountId) {
    	AccountProperty accountProperty = getAccountProperty("tolven.cchit.emergencyAccount", accountId);
		
    	if (accountProperty == null) {
    		accountProperty = new AccountProperty();
    		accountProperty.setPropertyName("tolven.cchit.emergencyAccount");
    	}
    	
		Account account = accountBean.findAccount(accountId);
		accountProperty.setAccount(account);
		em.persist(accountProperty);
		logger.info("Account [" + accountId + "] set as emergency account");
	}
	
	/**
     * Returns an account property with the given name.
     * 
     * added on 01/31/2011
     * @author valsaraj
     * @param propertyName - property name
     * @param accountId - account id
     * @return accountProperty - account property
     */
    public AccountProperty getAccountProperty(String propertyName, long accountId) {
    	String strQuery = "SELECT ap " +
							"FROM AccountProperty ap " +
							"WHERE " +
							"ap.propertyName = '" + propertyName + "' AND " +
							"ap.account.id = " + accountId;
    	Query query = em.createQuery(strQuery);
    	List<AccountProperty> lists = query.getResultList();
    	AccountProperty accountProperty = null;
		
    	if (lists.size() > 0 && lists.get(0) != null){
			accountProperty = (AccountProperty) lists.get(0);
		}
    	
    	return accountProperty;
	}
    
    /**
     * Checks whether the account is emergency account or not.
     * 
     * added on 02/01/2011
     * @author valsaraj
     * @param accountId - account id
     * @return status of whether the account is emergency account or not
     */
    public boolean isEmergencyAccount(long accountId) {
		return getAccountProperty("tolven.cchit.emergencyAccount",
				accountId) == null ? false : true;
	}
    
    /**
	 * Unsets an account as emergency account.
	 * 
	 * added on 02/08/2011
	 * @author valsaraj
	 * @param accountId - account id
	 */
	public void unsetEmergencyAccount(long accountId) {
    	AccountProperty accountProperty = getAccountProperty("tolven.cchit.emergencyAccount", accountId);
		
    	if (accountProperty != null) {
    		em.remove(accountProperty);
    		logger.info("Account [" + accountId + "] unset as emergency account");
    	}
	}
	
	/**
	 * Returns all emergency account details.
	 * 
	 * added on 02/08/2011
	 * @author valsaraj
	 * @return lists - emergency account details
	 */
	public List getAllEmergencyAccountIds() {
       List<Object[]> lists = null;
       
       try {
           String activeStatus = Status.ACTIVE.value();
           String oldActiveStatus = Status.OLD_ACTIVE.value();
           String uniqueIDs = "SELECT DISTINCT au.account.id FROM AccountUser au, AccountProperty ap WHERE ap.propertyName = 'tolven.cchit.emergencyAccount' AND ap.account.id = au.account.id and (au.status = '" + activeStatus + "' or au.status = '" + oldActiveStatus + "')";
           String accountTypes = "SELECT DISTINCT id FROM AccountType WHERE known_type = 'echr'";
           Query query = em.createQuery("SELECT DISTINCT id,title FROM Account WHERE id IN ("+uniqueIDs+") AND accounttype_id IN ("+accountTypes+")" );
           lists = query.getResultList();
       } catch (Exception e) {
           logger.info("Exception in getAllEmergencyAccountIDs :" + e.getMessage());
       }
       
       return lists;
   }
}
