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
 * @version $Id: AnalysisLocal.java 486 2011-03-29 00:51:12Z kanag.kuttiannan $
 */
package org.tolven.analysis;

import java.util.Date;
import java.util.Map;

import org.tolven.core.entity.Account;

public interface AnalysisLocal {

    public void scheduleAnalysis(Map<String, Object> messageProperties, Date queueOnDate, Account account);

}
