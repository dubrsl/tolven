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
 * @version $Id: DMA1cSnapshotEvent.java 292 2011-03-08 01:57:04Z kanag.kuttiannan $
 */
package org.tolven.analysis;

import org.tolven.app.entity.MenuData;

public class DMA1cSnapshotEvent {

    private MenuData cohort;
    private MenuData snapshot;
    private Double a1cLow;
    private Double a1cHigh;

    public DMA1cSnapshotEvent() {
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

    public Double getA1cLow() {
        return a1cLow;
    }

    public void setA1cLow(Double low) {
        a1cLow = low;
    }

    public Double getA1cHigh() {
        return a1cHigh;
    }

    public void setA1cHigh(Double high) {
        a1cHigh = high;
    }

}
