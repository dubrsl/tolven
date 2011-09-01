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
 * @version $Id: CohortSnapshotLocal.java 486 2011-03-29 00:51:12Z kanag.kuttiannan $
 */
package org.tolven.analysis;

import java.util.Date;

import org.jfree.chart.JFreeChart;
import org.tolven.core.entity.AccountUser;

public interface CohortSnapshotLocal {

    public JFreeChart getChart(String cohortType, String snapshotType, String chartType, AccountUser accountUser, Date now);
}
