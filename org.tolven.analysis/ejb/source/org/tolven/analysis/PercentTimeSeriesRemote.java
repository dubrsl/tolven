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
package org.tolven.analysis;

import java.util.Date;

import org.jfree.chart.JFreeChart;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;

/**
 * @author Joseph Isaac
 */

public interface PercentTimeSeriesRemote {

    public JFreeChart getChart(MenuStructure snapshotListMS, MenuPath snapshotPH, Long chartRange, Class<?> intervalUnitClass, String chartDataTitle, String chartTargetTitle, AccountUser accountUser, Date now);
    
}
