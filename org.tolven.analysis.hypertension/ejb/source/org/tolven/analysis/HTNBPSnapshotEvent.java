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
 * @version $Id: HTNBPSnapshotEvent.java 295 2011-03-08 01:59:31Z kanag.kuttiannan $
 */
package org.tolven.analysis;

import org.tolven.app.entity.MenuData;

public class HTNBPSnapshotEvent {

    private MenuData cohort;
    private MenuData snapshot;
    private Double lowSystolic;
    private Double highSystolic;
    private Double lowDiastolic;
    private Double highDiastolic;

    public HTNBPSnapshotEvent() {
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

    public Double getLowSystolic() {
        return lowSystolic;
    }

    public void setLowSystolic(Double lowSystolic) {
        this.lowSystolic = lowSystolic;
    }

    public Double getHighSystolic() {
        return highSystolic;
    }

    public void setHighSystolic(Double highSystolic) {
        this.highSystolic = highSystolic;
    }

    public Double getLowDiastolic() {
        return lowDiastolic;
    }

    public void setLowDiastolic(Double lowDiastolic) {
        this.lowDiastolic = lowDiastolic;
    }

    public Double getHighDiastolic() {
        return highDiastolic;
    }

    public void setHighDiastolic(Double highDiastolic) {
        this.highDiastolic = highDiastolic;
    }

}
