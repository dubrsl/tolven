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
 * @version $Id$
 */
package org.tolven.analysis;

import java.util.Date;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;

public interface SnapshotLocal {

    public void snapshotNow(String cohortType, String snapshotType, Account account);
    
    public void scheduleSnapshot(String cohortType, String snapshotType, Account account);
    
    public Date getNextScheduledDate(String cohortType, String snapshotType, Date now, Account account);

    public void cancelSchedule(String cohortType, String snapshotType, Account account);
    
    public void cancelScheduleNow(String cohortType, String snapshotType, Account account, Date now);

    public MenuData createSnapshot(MenuData cohort, Account account);

    public MenuData findMenuData(long id, Account account);

    public void addCohortPlaceholderID(String cohortName, MenuData cohort);
    
    public MenuData findCohort(String cohortName, Account account);

    public void addPatientcohortPlaceholderID(String cohortName, MenuData patientcohort);
    
    public MenuData findPatientcohort(String cohortName, long patientId, Account account);

    public MenuData findPatientcohort(String cohortName, MenuData patient, Account account);

    public MenuData findPatientcohort(MenuData cohort, MenuData patient, Account account);

    public List<MenuData> findCohortPatients(String cohortType, Account account);

    public List<MenuData> findCohortPatients(MenuData cohort, Account account);

    public JFreeChart getChart(String cohortType, String snapshotType, String chartType, AccountUser accountUser, Date now);

    public void upateMenuDataVersion(String path, Account account, Date now);
}
