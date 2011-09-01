package org.tolven.doc.entity;
/**
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author Kul Bhushan
 * @version $Id: RulePackage.java,v 1.2.8.1 2010/04/29 01:47:00 joseph_isaac Exp $
 */ 

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


@Entity
@Table
public class RulePackage implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE, generator="DOC_SEQ_GEN")
    private Long id;
	
	@Column
    private String packageName;
   
    @Column
    private String description;

    @Column
    private String packageBody;
    
    @Column
    private Integer packageVersion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date timestamp;
    
    @Column
    private String packageStatus;
    
    @Lob
    @Column
    private byte[] compiledPackage;
    
	public String getPackageBody() {
		return packageBody;
	}

	@Override
	public String toString() {
		return getPackageName() + " version " + getPackageVersion();
	}

	public void setPackageBody(String packageBody) {
		this.packageBody = packageBody;
	}

	public Integer getPackageVersion() {
		return packageVersion;
	}

	public void setPackageVersion(Integer packageVersion) {
		this.packageVersion = packageVersion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageStatus() {
		return packageStatus;
	}

	public void setPackageStatus(String packageStatus) {
		this.packageStatus = packageStatus;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public byte[] getCompiledPackage() {
		return compiledPackage;
	}

	public void setCompiledPackage(byte[] compiledPackage) {
		this.compiledPackage = compiledPackage;
	}

    
}

