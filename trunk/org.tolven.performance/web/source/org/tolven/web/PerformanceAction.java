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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import org.tolven.core.PerformanceDataDAO;
import org.tolven.core.entity.PerformanceData;

/**
 * Backing bean for actions related to Performance measures of request calls.
 * 
 * @author Sabu Antony
 * 
 */
public class PerformanceAction extends TolvenAction {

    public static String ACCOUNTUSER_ID = "accountUserId";

    @EJB
    private PerformanceDataDAO performanceBean;

	public PerformanceAction() {
		// J2EE 1.5 has not yet defined exact XML <ejb-ref> syntax for EJB3
        //TODO Injection does not work for JBoss (v4.0.4GA) web tier (tomcat), but does for GlassFish
		
	}
	
	/**
	 * Return a list of the last 100 server transaction elapsed times for this
	 * session
	 * 
	 * @return
	 */

    protected PerformanceDataDAO getPerformanceBean() {
        
        return performanceBean;
    }

	public List<PerformanceData> getPerfList() {
		ExternalContext ectx = FacesContext.getCurrentInstance()
				.getExternalContext();

		return getPerformanceBean().getPerformanceData((Long) ((HttpSession) ectx
				.getSession(false))
				.getAttribute(ACCOUNTUSER_ID), 100);

	}

	/**
	 * Return a list of the Summary of Server interaction for each Unique
	 * Request URI . Summary includes Count of Request ,Average,Minimum and
	 * Maximum of elapsed times
	 * 
	 * @return
	 */
	public List<Object[]> getAccountUserSummary() {
		return getPerformanceBean().getAccountUserSummary(getTop().getAccountUserId(), 100);
	}

	/**
	 * Return a list of the Summary of Server interaction for each Unique
	 * Request URI . Summary includes Count of Request ,Average,Minimum and
	 * Maximum of elapsed times
	 * 
	 * @return
	 */
	public List<Object[]> getAccountSummary() {
		return getPerformanceBean().getAccountSummary(getTop().getAccountId(),100);
	}
	
	/**
	 * Function used to get total performance log count.
	 * 
	 * @author Suja Sundaresan
	 * added on 01/12/2011
	 * @param map
	 * @return long
	 */
	public long getTotalPerformanceData(){
		Map<String, Object> params = new HashMap<String, Object>();
		if (getTop().isAccountAdmin())
			params.put("accountUserID", "");
		else
			params.put("accountUserID", getTop().getAccountUserId());
		params.put("accountID", getTop().getAccountId());
		return performanceBean.countPerformanceData(params);
	}
}
