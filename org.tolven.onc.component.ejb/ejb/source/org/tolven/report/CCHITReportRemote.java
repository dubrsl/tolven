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
 * You should have received a copy of the GNU Lesser General Public License along with this 
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA 02111-1307 USA 
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.report;

import java.util.Date;

/**
 * 
 * @author Joseph Isaac
 *
 */
public interface CCHITReportRemote {
    /**
     * Store the jrxml
     * @param jrxml
     * @param user
     * @param comment
     */
    public void storeReport(String reportAsString, String reportType, Date time);

    /**
     * Store the report as a String. An exception will be thrown if the externalRepotName is supplied and not found to
     * be the same as that within the report itself.
     * 
     * @param externalReportName
     * @param reportAsString
     * @param reportType
     */
    public void storeReport(String externalReportName, String reportAsString, String reportType, Date time);

}
