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
import java.util.Map;

import org.tolven.core.entity.PerformanceData;
/**
 * The interface for the session bean for keeping track of performance measures for all request calls.
 * 
 * @author Scott DuVall
 */
public interface PerformanceDataDAO {
    public final String PERFORMANCE_STATS_PROPERTY = "tolven.performance.stats";
    
	public PerformanceData addPerformanceData(Date now, String sessionID, String remoteUserName, Long accountUserID, String remoteIP, String requestURI, String queryParams, double elapsed, String exceptions);
	public void addPerformanceData(PerformanceData performanceData);
	public List<PerformanceData> getPerformanceData(String sessionID, int maxLength);
	public List<PerformanceData> getPerformanceData(Long accountUserID, int maxLength);
	
	/**
	 * return a list of unique Request_URI and its elapsed time summary for a single account user.
	 * @param accountUserID the AccountUser ID 
	 * @param maxLength the maximum length of list to return
	 * @return requestURI, COUNT(elapsed), MIN(elapsed), MAX(elapsed), AVG(elapsed)
	 */
	
	public List getAccountUserSummary(Long accountUserId, int maxLength);
	
	/**
	 * Return a summary of requests for an entire account
	 * @param accountId
	 * @param maxLength
	 * @return requestURI, COUNT(elapsed), MIN(elapsed), MAX(elapsed), AVG(elapsed)
	 */
	public List getAccountSummary(Long accountId, int maxLength);
	
	/**
	 * Function used to get count of logs
	 * 
	 * @author Suja Sundaresan
	 * added on 01/12/2011
	 * @param map
	 * @return long
	 */
	public long countPerformanceData(Map<String, Object> map);
	
	/**
	 * This function used to retrive all logs corressponding to the account user
	 * 
	 * @author Suja Sundaresan
	 * added on 01/12/2011
	 * @param map
	 * @return List<PerformanceData>
	 */
	public List<PerformanceData> getPerformanceData(Map<String, Object> map);
}
