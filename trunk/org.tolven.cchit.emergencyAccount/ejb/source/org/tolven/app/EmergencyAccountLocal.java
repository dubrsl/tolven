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
 * This file contains EmergencyAccountLocal interface.
 *
 * @package org.tolven.app
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app;

import java.util.List;

import org.tolven.core.entity.AccountProperty;

/**
 * This interface is used to process emergency access account related operations.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
public interface EmergencyAccountLocal {
	/**
	 * Retrieves all study ids.
	 *
	 * added on 01/25/2011
	 * @author valsaraj
	 */
	public List<String> getAllStudyIds(String study);
	
	/**
	 * Identifies patient with the given studyId and studyName.
	 * 
	 * added on 01/25/2011
	 * @author valsaraj
	 * @param studyId - study id
	 * @param studyName - study name
	 * @return patients - matiching patient list
	 */
	public List<Object[]> getPatientIdByStudyId (String studyId, String studyName);
	
	/**
	 * Sets an account as emergency account.
	 * 
	 * added on 01/31/2011
	 * @author valsaraj
	 * @param accountIdStr - account id
	 */
	public void setEmergencyAccount(String accountIdStr);
	
	/**
	 * Sets an account as emergency account.
	 * 
	 * added on02/08/2011
	 * @author valsaraj
	 * @param accountId - account id
	 */
	public void setEmergencyAccount(long accountId);
	
	/**
     * Returns an account property with the given name.
     * 
     * added on 01/31/2011
     * @author valsaraj
     * @param propertyName - property name
     * @param accountId - account id
     * @return accountProperty - account property
     */
	 public AccountProperty getAccountProperty(String propertyName, int accountId);
	 
	 /**
      * Checks whether the account is emergency account or not.
      * 
      * added on 02/01/2011
      * @author valsaraj
      * @param accountId - account id
      * @return status of whether the account is emergency account or not
      */
	 public boolean isEmergencyAccount(long accountId);
	 
	 /**
	  * Unsets an account as emergency account.
	  * 
	  * added on 02/08/2011
	  * @author valsaraj
	  * @param accountId - account id
	  */
	public void unsetEmergencyAccount(long accountId);
	
	/**
	 * Returns all emergency account details.
	 * 
	 * added on 02/08/2011
	 * @author valsaraj
	 * @return lists - emergency account details
	 */
	public List getAllEmergencyAccountIds();
}
