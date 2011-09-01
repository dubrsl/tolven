/*
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
 * @author <your name>
 * @version $Id: TouchMessage.java,v 1.1 2010/03/06 05:34:11 jchurin Exp $
 */  

package org.tolven.app.entity;

import java.io.Serializable;

/**
 * @author John Churin
 * A message indicating that a placeholder in MenuData needs to be reprocessed.
 */
public class TouchMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private long accountId;

    private String path;
    
    /**
     * The normal constructor takes a MenuData entity and extracts what it needs.
     * @param md MenuData 
     */
    public TouchMessage( MenuData md ) {
    	path = md.getPath();
		setAccountId(md.getAccount().getId());
    }
    
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
