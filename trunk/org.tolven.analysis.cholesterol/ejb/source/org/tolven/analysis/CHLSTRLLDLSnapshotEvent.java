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
 * @version $Id: CHLSTRLLDLSnapshotEvent.java 289 2011-03-08 01:53:22Z kanag.kuttiannan $
 */
package org.tolven.analysis;

import org.tolven.app.entity.MenuData;

public class CHLSTRLLDLSnapshotEvent {

    private MenuData cohort;
    private MenuData snapshot;
    private Double ldlLow;
    private Double ldlHigh;

    public CHLSTRLLDLSnapshotEvent() {
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

    public Double getLdlLow() {
        return ldlLow;
    }

    public void setLdlLow(Double ldlLow) {
        this.ldlLow = ldlLow;
    }

    public Double getLdlHigh() {
        return ldlHigh;
    }

    public void setLdlHigh(Double ldlHigh) {
        this.ldlHigh = ldlHigh;
    }

}
