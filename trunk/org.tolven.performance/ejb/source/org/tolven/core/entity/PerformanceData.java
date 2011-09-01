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
package org.tolven.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * Keeps track of performance measures for all request calls.
 * 
 * @author Scott DuVall
 */
@Entity
@Table
public class PerformanceData implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

	@Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date eventTime ;

    @Column
    private String sessionID;
    
    @Column
    private String remoteUserName;
    
    @Column
    private Long accountUserID;

    @Column
    private String remoteIP ;
    
    @Column
    private String localIP ;

    @Column
    private String requestURI;

    @Column
    private String queryParams;

    @Column
    private String facesViewState;

    @Column
    private String method;

    @Column
    private String element;
    
    @Column
    private double elapsed ;
   
    @Column
    @Lob
    private String exceptions ;

    @Column
    private String patientName;
    
    /**
     * Construct an empty PerformanceData record. 
     */
    public PerformanceData() {
    }
    /**
     * The unique internal Tolven ID of the PerformanceData. This ID has no meaning other than uniqueness. 
     * Leave Id null for a new record. The EntityManager will assign a unique Id when it is persisted.
     */
    public long getId() {
        return id;
    }

    public void setId(long val) {
        this.id = val;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PerformanceData)) return false;
        if (this.getId()==((PerformanceData)obj).getId()) return true;
        return false;
    }

    public String toString() {
        return "PerformanceData: " + getId();
    }

    public int hashCode() {
    	if (getId()==0) throw new IllegalStateException( "id not yet established in PerformanceData object");
        return new Long( getId()).hashCode();
    }
	/**
	 * @return the accountUserID
	 */
	public Long getAccountUserID() {
		return accountUserID;
	}
	/**
	 * @param accountUserID the accountUserID to set
	 */
	public void setAccountUserID(Long accountUserID) {
		this.accountUserID = accountUserID;
	}
	/**
	 * @return the elapsed
	 */
	public double getElapsed() {
		return elapsed;
	}
	/**
	 * @param elapsed the elapsed to set
	 */
	public void setElapsed(double elapsed) {
		this.elapsed = elapsed;
	}
	/**
	 * @return the exceptions
	 */
	public String getExceptions() {
		return exceptions;
	}
	/**
	 * @param exceptions the exceptions to set
	 */
	public void setExceptions(String exceptions) {
		this.exceptions = exceptions;
	}
	/**
	 * @return the eventTime
	 */
	public Date getEventTime() {
		return eventTime;
	}
	/**
	 * @param eventTime the eventTime to set
	 */
	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}
	/**
	 * @return the queryParams
	 */
	public String getQueryParams() {
		return queryParams;
	}
	/**
	 * @param queryParams the queryParams to set
	 */
	public void setQueryParams(String queryParams) {
		this.queryParams = queryParams;
	}
	/**
	 * @return the remoteIP
	 */
	public String getRemoteIP() {
		return remoteIP;
	}
	/**
	 * @param remoteIP the remoteIP to set
	 */
	public void setRemoteIP(String remoteIP) {
		this.remoteIP = remoteIP;
	}
	/**
	 * @return the remoteUserName
	 */
	public String getRemoteUserName() {
		return remoteUserName;
	}
	/**
	 * @param remoteUserName the remoteUserName to set
	 */
	public void setRemoteUserName(String remoteUserName) {
		this.remoteUserName = remoteUserName;
	}
	/**
	 * @return the requestURI
	 */
	public String getRequestURI() {
		return requestURI;
	}
	/**
	 * @param requestURI the requestURI to set
	 */
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}
	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}
	/**
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getFacesViewState() {
		return facesViewState;
	}
	public void setFacesViewState(String facesViewState) {
		this.facesViewState = facesViewState;
	}
	public String getLocalIP() {
		return localIP;
	}
	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}
	public String getElement() {
		return element;
	}
	public void setElement(String element) {
		this.element = element;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
        
}
