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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Maintains a list of notices that can be shown on the login page. 
 * Only one notice is active at any point of time. 
 * @author Sashikanth Vema
 */
@Entity
@Table
public class Notice implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column
    private String notice;

    @Column
    private boolean noticeActive;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date showFrom;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date showTo;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date effectiveDate;
    
    /**
     * Construct an empty Notice. 
     */
    public Notice() {
    }
    /**
     * The unique internal Tolven ID of the account. This ID has no meaning other than uniqueness. 
     * Leave Id null for a new record. The EntityManager will assign a unique Id when it is persisted.
     */
    public long getId() {
        return id;
    }

    public void setId(long val) {
        this.id = val;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Notice)) return false;
        if (this.getId()==((Notice)obj).getId()) return true;
        return false;
    }

    public String toString() {
        return "Notice: " + getId();
    }

    public int hashCode() {
    	if (getId()==0) throw new IllegalStateException( "id not yet established in Notice object");
        return new Long( getId()).hashCode();
    }
    /**
     * A String containing the notice.
     */
    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    /**
     * Displays whether the notice is active or not.
     */
    public boolean isNoticeActive() {
        return noticeActive;
    }

    public void setNoticeActive(boolean noticeActive) {
        this.noticeActive = noticeActive;
    }

    public void setShowFrom(Date dateShowFrom) {
    	this.showFrom = dateShowFrom;
    }
    
    public Date getShowFrom() {
    	return this.showFrom;
    }

    public void setShowTo(Date dateShowTo) {
    	this.showTo = dateShowTo;
    }
    
    public Date getShowTo() {
    	return this.showTo;
    }

    public void setEffectiveDate(Date dateEffectiveDate) {
    	this.effectiveDate = dateEffectiveDate;
    }
    
    public Date getEffectiveDate() {
    	return this.effectiveDate;
    }
} // class Notice
