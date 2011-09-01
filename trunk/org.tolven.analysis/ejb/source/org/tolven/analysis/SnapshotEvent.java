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
 * @version $Id: SnapshotEvent.java 486 2011-03-29 00:51:12Z kanag.kuttiannan $
 */
package org.tolven.analysis;

import org.tolven.app.entity.MenuData;

public class SnapshotEvent {

    private MenuData cohort;
    private String cohortType;
    private String snapshotType;

    public MenuData getCohort() {
        return cohort;
    }

    public void setCohort(MenuData cohort) {
        this.cohort = cohort;
    }

    public String getCohortType() {
        return cohortType;
    }

    public void setCohortType(String cohortType) {
        this.cohortType = cohortType;
    }

    public String getSnapshotType() {
        return snapshotType;
    }

    public void setSnapshotType(String snapshotType) {
        this.snapshotType = snapshotType;
    }

}
