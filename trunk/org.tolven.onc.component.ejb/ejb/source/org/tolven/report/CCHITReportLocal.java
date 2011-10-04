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

import java.io.OutputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;

import org.tolven.core.entity.AccountUser;

/**
 * 
 * @author Joseph Isaac
 *
 */
public interface CCHITReportLocal {
    
/**
 * Based on the supplied reportName, write the report to the servlet outputstream where tolvenQuery is a menu path
 * eg echr:patient and tolvenQueryParameter is also a menu path for a specific query e.g. echr:patient-NNNN, representing
 * a particular patient. The accountUser is supplied to ensure that information is restricted to a particular account for
 * a given user. The reportFormat defaults to pdf, but can be other formats supported by JasperReports
     * 
     * @param reportName
     * @param reportType
     * @param tolvenQuery
     * @param tolvenQueryParameter
     * @param sortOrder
     * @param sortDirection
     * @param accountUser
     * @param out
     * @param reportFormat
     * @throws Exception
     */
    public void writeReport(String reportName, String reportType, String tolvenQuery, String tolvenQueryParameter, String sortOrder, String sortDirection, AccountUser accountUser, OutputStream out, String reportFormat) throws Exception;

    /**
     * Return a JasperReport object by name
     * @param name
     * @return
     * @throws JRException
     */
    public JasperReport getJasperReport(String name) throws JRException;
}
