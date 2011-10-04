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
package org.tolven.report.bean;

import java.io.OutputStream;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.tolven.core.entity.AccountUser;
import org.tolven.report.CCHITReportDatasource;
import org.tolven.report.CCHITReportLocal;
import org.tolven.report.CCHITReportRemote;

/**
 * To create reports in pdf or xls, depending on the reportFormat passed.
 * 
 * @author Vineetha George 
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	|     06/07/2010        |   Vineetha George 	|      Initial Version 
==============================================================================================================================================
*/
@Stateless
@Local(CCHITReportLocal.class)
@Remote(CCHITReportRemote.class)
public class CCHITReportBean extends ReportBean implements CCHITReportLocal,
		CCHITReportRemote {

	/**
	 * Based on the supplied reportName, write the report to the servlet outputstream where tolvenQuery is a menu path
	 * eg echr:patient and tolvenQueryParameter is also a menu path for a specific query e.g. echr:patient-NNNN, representing
	 * a particular patient. The accountUser is supplied to ensure that information is restricted to a particular account for
	 * a given user. The reportFormat defaults to pdf, but can be other formats supported by JasperReports
	 */
	@Override
	public void writeReport(String reportName, String reportType,
			String tolvenQuery, String tolvenQueryParameter, String sortOrder,
			String sortDirection, AccountUser accountUser, OutputStream out,
			String reportFormat) throws Exception {
		if (!JRXML.equals(reportType)) {
			throw new RuntimeException(
					"Only Jasper jrxml report types are supported");
		}
		CCHITReportDatasource dataSource = new CCHITReportDatasource(
				tolvenQuery, tolvenQueryParameter, sortOrder, sortDirection,
				accountUser);
		JasperReport report = getJasperReport(reportName);
		java.util.Map<String, Object> parameterMap = new java.util.HashMap<String, Object>();
		JasperPrint jasperPrint = JasperFillManager.fillReport(report,
				parameterMap, dataSource);
		/*
		 * To export data to xls format
		 */
		if ("xls".toLowerCase().equals(reportFormat.toLowerCase())) {
			JRXlsExporter exporterXLS = new JRXlsExporter();
			exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT,
					jasperPrint);
			exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, out);
			exporterXLS
					.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
							Boolean.FALSE);
			exporterXLS.setParameter(
					JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporterXLS.setParameter(
					JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
					Boolean.FALSE);
			exporterXLS.setParameter(
					JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
					Boolean.TRUE);
			exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME,
					reportName);
			exporterXLS.exportReport();
		}
		/*
		 * To export data to pdf format
		 */
		else if (reportFormat == null
				|| "pdf".toLowerCase().equals(reportFormat.toLowerCase())) {
			JRPdfExporter exporterPDF = new JRPdfExporter();
			exporterPDF.setParameter(JRXlsExporterParameter.JASPER_PRINT,
					jasperPrint);
			exporterPDF.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, out);
			exporterPDF.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME,
					reportName);
			exporterPDF.exportReport();
		} else {
			throw new RuntimeException("Report format unrecognized: "
					+ reportName);
		}

	}
}