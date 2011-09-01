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
 * @author Joseph Isaac
 * @version $Id: SmkProgSnapshotEvent.java 307 2011-03-08 02:07:22Z kanag.kuttiannan $
 */
package org.tolven.analysis;

import java.util.Date;

import org.tolven.app.entity.MenuData;

public class SmkProgSnapshotEvent {

    private MenuData cohort;
    private MenuData snapshot;
    private Date orderDate;

    public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public SmkProgSnapshotEvent() {
    }

    public MenuData getCohort() {
        return cohort;
    }

    public void setCohort(MenuData cohort) {
        this.cohort = cohort;
    }

    public MenuData getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(MenuData snapshot) {
        this.snapshot = snapshot;
    }
    

}
