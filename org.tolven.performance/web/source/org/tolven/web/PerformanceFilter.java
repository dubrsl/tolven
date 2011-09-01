/*
 * Copyright (C) 2006 Tolven Inc This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. Contact: info@tolvenhealth.com
 */
package org.tolven.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.tolven.core.entity.AccountUser;
import org.tolven.web.security.GeneralSecurityFilter;
import org.apache.log4j.Logger;
import org.tolven.app.MenuLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.core.PerformanceDataDAO;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.PerformanceData;

/**
 * Wraps all requests for jsp (xhtml) and ajax in a filter that measures
 * performance.
 * 
 * @author Scott DuVall
 */
public class PerformanceFilter implements Filter {

    public static String ACCOUNTUSER_ID = "accountUserId";
    public static String EXCLUDED_REQUEST_PARAM = "exclude-request-param";
    /**
     * PERFORMANCE_STATS represents a System property called tolven.performance.stats which can be on/off/undefined/unknown
     * undefined, unknown and on are all interpreted as the default performance stats on. Setting the property to off switches
     * off performance.
     */
    private final String PERFORMANCE_STATS_FALSE = "false";
    
    @EJB private PerformanceDataDAO perfDAO;
    
    @EJB private TolvenPropertiesLocal propertiesBean;
    @EJB private MenuLocal menuBean;
    
    FilterConfig config = null;
    private static List<String> excludedRequestParams;
    
    private Logger logger = Logger.getLogger(PerformanceFilter.class);

    public void init(FilterConfig config) throws ServletException {
		this.config=config;
	}

	public void destroy() {
	}
	
    public TolvenPropertiesLocal getPropertiesBean() throws ServletException {
    	try {
    		InitialContext ctx = new InitialContext();
			if (propertiesBean==null) {
		        //TODO Injection does not work for JBoss (v4.0.4GA) web tier (tomcat), but does for GlassFish
				propertiesBean = (TolvenPropertiesLocal) ctx.lookup("java:global/tolven/tolvenEJB/TolvenPropertiesBean!org.tolven.core.TolvenPropertiesLocal");
			}
			if (menuBean == null) {
				menuBean = (MenuLocal) ctx.lookup("java:global/tolven/tolvenEJB/MenuBean!org.tolven.app.bean.MenuLocal");
			}
		} catch (NamingException e) {
			throw new ServletException( "Unable to resolve PropertiesBean in Performance Filter", e);
		}
    	return propertiesBean;
    }

    public PerformanceDataDAO getPerformanceDataBean() throws ServletException {
    	try {
			if (perfDAO==null) {
				InitialContext ctx = new InitialContext();
			        //TODO Injection does not work for JBoss (v4.0.4GA) web tier (tomcat), but does for GlassFish
				perfDAO = (PerformanceDataDAO) ctx.lookup("java:global/tolven/tolvenEJB/PerformanceDataDAOBean!org.tolven.core.bean.TolvenPropertiesLocal");
				}
		} catch (NamingException e) {
			throw new ServletException( "Unable to resolve PerformanceDataBean in Performance Filter", e);
		}
    	return perfDAO;
    }

	/**
	 * If the PERFORMANCE_STATS property is set to anything other than off, then performance stats is collected. Performance stats are collected
	 * by default i.e. if PERFORMANCE_STATS is not defined or if PERFORMANCE_STATS is on etc, and since there is no desire to throw an exception
	 * if it's not off or on, then other values are interpreted as not switching off performance.
	 */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException {
        String performance_stats_value = getPropertiesBean().getProperty(PerformanceDataDAO.PERFORMANCE_STATS_PROPERTY);
        if (PERFORMANCE_STATS_FALSE.equals(performance_stats_value)) {
            try {
                chain.doFilter(request, response);
            } catch (IOException ex) {
                throw new ServletException(ex);
            }
        } else {
            doPerformance(request, response, chain);
        }
    }
    
	public void doPerformance(ServletRequest request, ServletResponse response, FilterChain chain)throws ServletException {

		Date eventTime = new Date(System.currentTimeMillis());
		double beginNanoTime = beginNanoTime = System.nanoTime();

		HttpSession sess = ((HttpServletRequest)request).getSession(false);
		Exception caught = null;
		
		// catch session variables before chain
		Long accountUserID = null ;
		String sessionID = null ;
		//UserPrincipal is not found for the first request coming in. Hence it can be ignored
		String remoteUser = ((HttpServletRequest)request).getUserPrincipal()!= null?
					((HttpServletRequest)request).getUserPrincipal().getName():"";
		String remoteAddress = request.getRemoteAddr();
		String localAddress = request.getLocalAddr();
		String requestURI = ((HttpServletRequest)request).getRequestURL().toString();

		
		AccountUser activeAccountUser = (AccountUser)  ((HttpServletRequest)request).getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
		//accountuser will not found until the user selects an account.
		//ignore this parameter value for audit log.
		if(activeAccountUser != null)
			accountUserID=(Long)activeAccountUser.getId();
		
		if(sess!=null)
		{
			sessionID = sess.getId();
		}
		
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			// save for now. Throw after persisting performance data
			caught = e;
		}

		// moved outside of try / catch so it will always record, even when an
		// exception is thrown
		double elapsed = System.nanoTime() - beginNanoTime;

		String errorString = null;
		if(caught != null && Boolean.parseBoolean(getPropertiesBean().getProperty("tolven.transaction.log"))) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			caught.printStackTrace(pw);
			errorString = sw.toString();				
		}
		PerformanceData performanceData = new PerformanceData();
		performanceData.setEventTime(eventTime);
		performanceData.setSessionID(sessionID);
		performanceData.setAccountUserID(accountUserID);
		performanceData.setElapsed(elapsed/1000000);
		performanceData.setExceptions(errorString);
		java.util.Enumeration<String> paramNames = request.getParameterNames();
		StringBuffer sb = new StringBuffer( 1000 );
		while ( paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (!isExcludedRequestParam(paramName)) {
                String val = request.getParameter(paramName);
                if (paramName.equals("element") ) {
            		performanceData.setElement(val);
            		if (val!=null && !val.equals("") && val.split("echr:patient-").length>1) {
						MenuData patMD = menuBean.findMenuDataItem(Long.parseLong(val.split("echr:patient-")[1].split(":")[0]));
						performanceData.setPatientName(
								String.format("%s, %s %s", (patMD.getString01()!=null? patMD.getString01(): ""),
										(patMD.getString02()!=null? patMD.getString02(): ""),
										(patMD.getString03()!=null? patMD.getString03():"")));
								 
					}
                }
                else if (paramName.equals("javax.faces.ViewState") && val.length() > 255) {
                    performanceData.setFacesViewState(val);
                } else {
                    if (sb.length() > 0) {
                    	sb.append(String.format("|%s(%s)%s", paramName,Integer.toString(val.length()),val));
                    }else
                    	sb.append(String.format("%s(%s)%s", paramName,Integer.toString(val.length()),val));                    
                }
            }
		}
		performanceData.setQueryParams(sb.toString());
		performanceData.setRemoteIP(remoteAddress);
		performanceData.setLocalIP(localAddress);
		performanceData.setRemoteUserName(remoteUser);
		performanceData.setRequestURI(requestURI);
		performanceData.setMethod(((HttpServletRequest)request).getMethod());
		getPerformanceDataBean().addPerformanceData(performanceData);
		if(caught != null) {
			if (caught instanceof ServletException) {
				throw (ServletException)caught;
			} else {
				throw new ServletException(caught);
			}
		}
	}
	
	private boolean isExcludedRequestParam(String paramName) {
        for (String expr : excludedRequestParams) {
            if (paramName.matches(expr)) {
                return true;
            }
        }
        return false;
    }
	
	static {
        String propertyFileName = PerformanceFilter.class.getSimpleName() + ".properties";
        try {
            Properties filterProperties = new Properties();
            InputStream in = PerformanceFilter.class.getResourceAsStream(propertyFileName);
            if (in == null) {
                excludedRequestParams = new ArrayList<String>();
            } else {
                filterProperties.load(in);
                String value = filterProperties.getProperty(EXCLUDED_REQUEST_PARAM);
                if (value == null) {
                    excludedRequestParams = new ArrayList<String>();
                } else {
                    excludedRequestParams = Arrays.asList(value.split(","));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load filter properties from: " + propertyFileName, e);
        }
    }
}
