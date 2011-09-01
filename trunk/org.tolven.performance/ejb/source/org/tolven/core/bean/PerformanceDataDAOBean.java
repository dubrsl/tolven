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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.core.PerformanceDataDAO;
import org.tolven.core.entity.PerformanceData;
/**
 * The session bean for keeping track of performance measures for all request calls.
 * 
 * @author Scott DuVall
 */
@Stateless
@Local(PerformanceDataDAO.class)
public class PerformanceDataDAOBean implements PerformanceDataDAO, Serializable {

	@PersistenceContext
	private EntityManager em;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param eventTime
	 * @param sessionID
	 * @param remoteUserName
	 * @param accountUserID
	 * @param remoteIP
	 * @param requestURL
	 * @param queryParams
	 * @param elapsed
	 * @param exceptions
	 * @see org.tolven.core.PerformanceDataDAO#addPerformanceData(Date, String, String, long, String, String, String, long, String)
	 * @return
	 */
	public PerformanceData addPerformanceData(Date eventTime, String sessionID, String remoteUserName, Long accountUserID, String remoteIP, String requestURI, String queryParams, double elapsed, String exceptions) {
		PerformanceData performanceData = new PerformanceData();
		performanceData.setEventTime(eventTime);
		performanceData.setSessionID(sessionID);
		performanceData.setAccountUserID(accountUserID);
		performanceData.setElapsed(elapsed);
		performanceData.setExceptions(exceptions);
		performanceData.setQueryParams(queryParams);
		performanceData.setRemoteIP(remoteIP);
		performanceData.setRemoteUserName(remoteUserName);
		performanceData.setRequestURI(requestURI);
		em.persist(performanceData);
		return performanceData;
	}
	
	/**
	 * Add performance data
	 * @param performanceData
	 */
	public void addPerformanceData(PerformanceData performanceData) {
		em.persist(performanceData);
	}

	
	/* 
	 * @param sessionID the current session ID
	 * @param maxLength the maximum length of list to return
	 * @see org.tolven.core.PerformanceDataDAO#getPerformanceData()
	 */
	public List<PerformanceData> getPerformanceData(String sessionID, int maxLength) {
		Query query = em.createQuery( "SELECT p from PerformanceData p where p.sessionID = :sessionID order by p.eventTime desc").setParameter("sessionID", sessionID);    
		query.setMaxResults(maxLength);
		return (List<PerformanceData>) query.getResultList();
	}
	
	/* 
	 * @param accountUserID the current AccountUser ID
	 * @param maxLength the maximum length of list to return
	 * @see org.tolven.core.PerformanceDataDAO#getPerformanceData()
	 */
	public List<PerformanceData> getPerformanceData(Long accountUserID, int maxLength) {
		Query query = em.createQuery( "SELECT p from PerformanceData p where p.accountUserID = :accountUserID order by p.eventTime desc").setParameter("accountUserID", accountUserID);    
		query.setMaxResults(maxLength);
		return (List<PerformanceData>) query.getResultList();
	}
	
	 
	/*
	 * Added by Sabu Antony 09-13-2007 for task 1791295 
	 * return a list of unique Request_URI and its elapsed time summary for a single account user.
	 *   
	 * @param accountUserID the AccountUser ID 
	 * @param maxLength the maximum length of list to return
	 * 
	 * @see org.tolven.core.PerformanceDataDAO#getAccountUserSummary()
	 */
	
	public List<Object[]> getAccountUserSummary(Long accountUserId, int maxLength) {
			
		Query query = em.createQuery( "SELECT p.requestURI, COUNT(p.elapsed), MIN(p.elapsed), MAX(p.elapsed), AVG(p.elapsed) " +
				"FROM PerformanceData p " +
				"WHERE p.accountUserID = :accountUser  " +
				"AND length(p.method)>7 " +
				"GROUP BY p.requestURI  ORDER BY COUNT(p.elapsed) DESC");
		query.setParameter("accountUser", accountUserId);    
	
		query.setMaxResults(maxLength);
		
		return  query.getResultList();
	}
	
	/**
	 * Return a summary of requests for an entire account
	 * @param accountId
	 * @param maxLength
	 * @return
	 */
	public List<Object[]> getAccountSummary(Long accountId, int maxLength) {
		
		Query query = em.createQuery( "SELECT p.requestURI, COUNT(p.elapsed), MIN(p.elapsed), MAX(p.elapsed), AVG(p.elapsed) " +
				"FROM PerformanceData p, AccountUser au " +
				"WHERE p.accountUserID = au.id " +
				"AND au.account.id = :accountId " +
				"AND length(p.method)>7 " +
				"GROUP BY p.requestURI  ORDER BY COUNT(p.elapsed) DESC");
		query.setParameter("accountId", accountId);
		query.setMaxResults(maxLength);
		
		return  query.getResultList();
	}

	/**
	 * Prepare Query for performance log
	 * 
	 * @author Suja Sundaresan
	 * added on 01/12/2011
	 * @param map
	 * @param type
	 * @return Query
	 */
	public Query prepareQuery(Map<String, Object> map, int type) {
		try {
			String queryString = "";
			if (type == 0)
				queryString = "SELECT count(p) from PerformanceData p where p.accountUserID >0 ";
			else
				queryString = "SELECT p from PerformanceData p where p.accountUserID >0 AND length(p.method)>7 ";
			if (!(map.get("accountUserID")!=null && map.get("accountUserID").toString().equals("")))
				queryString += " AND p.accountUserID = :accountUserID ";
			if (map.get("accountID")!=null)
				queryString += " AND p.accountUserID in (SELECT au.id FROM AccountUser au WHERE au.account.id=" + map.get("accountID").toString() + ") ";
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			String fromDate = "";
			String toDate = "";
			
			if (map.get("filter") != null && !map.get("filter").toString().equals("")){
				String[] filters = map.get("filter").toString().split("@");
				for (String filter : filters) {
					String[] params = filter.split(":");
					if (params[0].equals("date")){
						fromDate = params[1].split("-")[0];
						if(params[1].split("-").length > 1)
							toDate = params[1].split("-")[1];
						if (!fromDate.equals(""))
							queryString += " AND p.eventTime>=:fromDate";
						if (!toDate.equals(""))
							queryString += " AND p.eventTime<:toDate";
					}
					else if (params[0].equals("user")){
						queryString += " AND p.remoteUserName='" + params[1].toString() + "'";
					}
					else if (params[0].equals("patient")){
						queryString += " AND trim(p.patientName)='" + params[1].toString().trim() + "'";
					}
					else if (params[0].equals("method")){
						queryString += " AND trim(p.method)='" + params[1].toString().trim() + "'";
					}
				}
			}
				
			if (type==1 && map.get("sort_col") != null && !map.get("sort_col").toString().equals("")) {
				String sortCol = map.get("sort_col").toString();
				queryString += " ORDER BY p." + sortCol;
				if (map.get("sort_dir") != null && !map.get("sort_dir").toString().equals("")) 
					queryString += " " + map.get("sort_dir").toString();
			}
			
			Query query = em.createQuery(queryString);
			if (!(map.get("accountUserID")!=null && map.get("accountUserID").toString().equals("")))
				query.setParameter("accountUserID", map.get("accountUserID"));
			if (!fromDate.equals(""))
				query.setParameter("fromDate", df.parse(fromDate));
			if (!toDate.equals("")){
				long toTime = df.parse(toDate).getTime();
				toTime += 1*24*60*60*1000;
				Date todate = new Date(toTime);
				query.setParameter("toDate", todate);
			}
			if (map.get("offset") != null && new Long(map.get("offset").toString()).intValue() > 0)
				query.setFirstResult(new Integer(map.get("offset").toString()).intValue());
			else
				query.setFirstResult(0);
			if (map.get("page_size") != null && new Long(map.get("page_size").toString()).intValue() > 0)
				query.setMaxResults(new Long(map.get("page_size").toString()).intValue());
			else
				query.setMaxResults(75);
			return query;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Function used to get count of logs
	 * 
	 * @author Suja Sundaresan
	 * added on 01/12/2011
	 * @param map
	 * @return long
	 */
	public long countPerformanceData(Map<String, Object> map) {
		long count = 0;
		try {
			Query query = prepareQuery(map, 0);
			Long rslt = (Long) query.getSingleResult();
			return rslt.longValue();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * This function used to retrive all logs corressponding to the account user
	 * 
	 * @author Suja Sundaresan
	 * added on 01/12/2011
	 * @param map
	 * @return List<PerformanceData>
	 */
	public List<PerformanceData> getPerformanceData(Map<String, Object> map) {
		try {
			Query query = prepareQuery(map, 1);
			List<PerformanceData> performances = query.getResultList();
			return performances;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
