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
 * @version $Id: AnalysisChartBean.java 486 2011-03-29 00:51:12Z kanag.kuttiannan $
 */
package org.tolven.analysis.bean;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.jfree.chart.JFreeChart;
import org.tolven.analysis.CohortSnapshotLocal;
import org.tolven.analysis.PercentTimeSeriesLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;

@Stateless
@Local(CohortSnapshotLocal.class)
public class AnalysisChartBean implements CohortSnapshotLocal {

    @EJB
    private MenuLocal menuBean;

    @EJB
    private PercentTimeSeriesLocal timeSeriesBean;

    @Override
    public JFreeChart getChart(String cohortType, String snapshotType, String chartType, AccountUser accountUser, Date now) {
        if ("percenttimeseries".equals(chartType)) {
            return getPercentTimeSeriesChart(cohortType, snapshotType, accountUser, now);
        } else {
            throw new RuntimeException("Unrecognized chart type: '" + chartType + "'");
        }
    }

    private JFreeChart getPercentTimeSeriesChart(String cohortType, String snapshotType, AccountUser accountUser, Date now) {
        MenuStructure snapshotListMS = menuBean.findMenuStructure(accountUser, "echr:cohort:snapshots:" + snapshotType);
        MenuPath snapshotPH = new MenuPath(menuBean.findMenuStructure(accountUser, "echr:cohort:" + snapshotType).getPath());
        String intervalUnitClassName = accountUser.getAccount().getProperty().get(cohortType + "." + snapshotType + ".intervalUnit");
        Class<?> intervalUnitClass = null;
        try {
            intervalUnitClass = Class.forName("org.jfree.data.time." + intervalUnitClassName);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Could not find the percentTimeSeries interval class: " + "org.jfree.data.time." + intervalUnitClassName + " using property: " + cohortType + "." + snapshotType + ".intervalUnit", ex);
        }
        Long intervalValue = Long.parseLong(accountUser.getAccount().getProperty().get(cohortType + "." + snapshotType + ".intervalValue"));
        Long chartRange = intervalValue * Long.parseLong(accountUser.getAccount().getProperty().get(cohortType + "." + snapshotType + ".chartRange"));
        String chartDataTitle = accountUser.getAccount().getProperty().get(cohortType + "." + snapshotType + ".chartDataTitle");
        String chartTargetTitle = accountUser.getAccount().getProperty().get(cohortType + "." + snapshotType + ".chartTargetTitle");
        return timeSeriesBean.getChart(snapshotListMS, snapshotPH, chartRange, intervalUnitClass, chartDataTitle, chartTargetTitle, accountUser, now);
    }

}
