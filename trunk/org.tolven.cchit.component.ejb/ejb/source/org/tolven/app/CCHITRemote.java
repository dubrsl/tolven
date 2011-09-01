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
 * This file contains CCHITRemote interface.
 *
 * @package org.tolven.app
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.security.PrivateKey;

import javax.xml.bind.JAXBException;

import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.trim.ex.TrimEx;

/**
 * This interface is used to for data retrieval process.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
public interface CCHITRemote {
	/**
	 * Function to retrieve menu data items corresponding to a path.
	 * 
	 * added on 18/03/2010
	 * @param path
	 * @param contextPath
	 * @param condition
	 * @param accountUser
	 * @return List<Map<String, Object>>
	 * @throws ParseException
	 */
	public List<Map<String, Object>> findAllMenuDataList(String path, String contextPath, String condition, AccountUser accountUser) throws ParseException;
	
	/**
	 * This function retrieve latest trim object corresponding to a menu.
	 * addedon 18/03/20109
	 * @param params - Map<String, Object>
	 *        fields - menuPath 	=> Menu Path
	 *        		 - contextPath	=> Context Path
	 *        		 - conditions	=> Filtering and sorting conditions
	 *        						   Format - <fieldName>Filter=<fieldValue>::fromDate=<FROMDATE>:toDate=<TODATE>
	 *        									:<fieldName>Sort=<ASC/DESC>:limit=<value>
	 *        		 - accountUser	=> Account User object		
	 * @return TrimEx
	 * @throws ParseException
	 * @throws JAXBException
	 */
	public TrimEx findTrimData( Map<String, Object> params, PrivateKey userPrivateKey) throws ParseException, JAXBException;
	/**
	 * This function retrieve all trim objects Listcorresponding to conditions and menupath.
	 * 
	 * added on 09/29/09
	 * @author Valsaraj
	 * @param Map<String, Object> params - parameters
	 * @return List<TrimEx> trimList - trim list
	 * @throws ParseException, JAXBException
	 */
	public List<TrimEx> findTrimDataList(Map<String, Object> params, PrivateKey userPrivateKey) throws ParseException, JAXBException;
	/**
	 * This function retrieve latest trim object corresponding to a menudata id.
	 * added on 18/03/2010
	 * @param Long id
	 * @param AccountUser accountUser
	 * @return TrimEx
	 * @throws JAXBException
	 */
	public TrimEx findTrimData(Long id, AccountUser accountUser, PrivateKey userPrivateKey) throws JAXBException;
	
	/**
	 * Finds the reference id using string01 value and account id.
	 * 
	 * added on 05/20/2010
	 * @author valsaraj
	 * @param string01 - string01 field value
	 * @param accountId - account id
	 * @return  referenceId - reference id
	 */
	public long findReferenceId(String string01, long accountId) throws Exception;
	
	/**
	 * Finds the reference id map based on conditions.
	 * 
	 * added on 06/08/2010
	 * @author valsaraj
	 * @param conditions - conditions
	 * @return referenceIdMap - reference id
	 */
	public Map<String, String> findReferenceIds(String conditions) throws Exception;
	
	 /**
	 * This function retrieves document of given id.
	 * 
	 * added on 09/30/09
	 * @author valsaraj
	 * @param id - id of document
	 * @param accountUser - account user
	 * @return Object - document
	 * @throws JAXBException
	 */
    public Object findDoc(Long id, AccountUser accountUser, PrivateKey userPrivateKey) throws JAXBException;
    
    public List<TolvenUser> getTolvenUser(String accountId);
    
    /**
	 * Returns account email.
	 * 
	 * added on 02/02/2011
	 * @author valsaraj
	 * @param accountId - Tolven account id
	 * @param propertyName - property name
	 * @return email - account email
	 */
    public String getAccountEmail(long accountId, String propertyName);
    
    /**
	 * Returns the latest menudata corresponding to the given menudata_path.
	 * @author Pinky S
	 * added on 02/02/11
	 * @param Account account, String snapshotType
	 * @return List<MenuData>
	 */
    public List<MenuData> findLatestSnapshotDate(Account account, String snapshotType);

    /**
     * Returns account list that matches title and user.
     * 
     * added on 06/16/2010
     * @author valsaraj
     * @param accountName - account name
     * @param user - Tolven user
     * @param accountTypeId - account type id
     * @return accountLists - account lists
     */
	public List<Account> getAccountsByTitleAndUser(String accountName, TolvenUser user, long accountTypeId);
	
	/**
     * Returns account list that matches user.
     * 
     * added on 07/12/2010
     * @author valsaraj
     * @param user - Tolven user
     * @param accountTypeId - account type id
     * @return accountLists - account lists
     */
	public List<Account> getAccountsByUser(TolvenUser user, long accountTypeId);
	
	//CCHITMerge
	public List<MenuData> findMenuChildren(Account account,long menuDataId);
}