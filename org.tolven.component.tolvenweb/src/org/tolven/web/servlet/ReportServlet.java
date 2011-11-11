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
package org.tolven.web.servlet;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenRequest;
import org.tolven.core.entity.AccountUser;
import org.tolven.report.ReportLocal;

/**
 * The main task of the ReportServlet is to write a report to the servlet output stream.
 * 
 * @author Joseph Isaac
 *
 */
public class ReportServlet extends HttpServlet {

    public static final String JRXML = "jrxml";
    
    private static String REPORT_NAME = "reportName";
    private static String REPORT_TYPE = "reportType";
    private static String REPORT_FORMAT = "reportFormat";
    private static String TOLVEN_QUERY = "tolvenQuery";
    private static String TOLVEN_QUERY_PARAMETER = "tolvenQueryParameter";
    private static String TOLVEN_QUERY_SORT_ORDER = "sortOrder";
    private static String TOLVEN_QUERY_SORT_DIRECTION = "sortDirection";

    @EJB
    private ReportLocal reportLocal;

    @EJB
    private ActivationLocal activation;

    /**
     * Based on the supplied reportName, write the report to the servlet outputstream where tolvenQuery is a menu path
     * eg echr:patient and tolvenQueryParameter is also a menu path for a specific query e.g. echr:patient-NNNN, representing
     * a particular patient.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        HttpSession session = request.getSession(false);
        /*
         * Supply a report name of the form reportName.jrxml
         */
        String reportName = request.getParameter(REPORT_NAME);
        /*
         * Supply an optional report format, which currently must be pdf
         */
        String reportFormat = request.getParameter(REPORT_FORMAT);
        if (reportFormat == null) {
            reportFormat = "pdf";
        } else if ("xls".toLowerCase().equals(reportFormat.toLowerCase())) { //added for CCHIT merge
            /*
             * Let it through
             */
        	
        } else if ("pdf".toLowerCase().equals(reportFormat.toLowerCase())) {
            /*
             * Let it through
             */
        } else {
            throw new RuntimeException("Report format unrecognized: " + reportName);
        }
        String reportType = request.getParameter(REPORT_TYPE);
        if (reportType == null) {
            reportType = JRXML;
        } else if (reportType.toLowerCase().equals(JRXML.toLowerCase())) {
            reportType = JRXML;
        } else {
            throw new RuntimeException("Only Jasper jrxml report types are supported");
        }
        /*
         * Supply a query of the form echr:patient
         */
        String tolvenQuery = request.getParameter(TOLVEN_QUERY);
        /*
         * Supply a query parameter of the form echr:patient-NNNN
         */
        String tolvenQueryParameter = request.getParameter(TOLVEN_QUERY_PARAMETER);
        String sortOrder = request.getParameter(TOLVEN_QUERY_SORT_ORDER);
        String sortDirection = request.getParameter(TOLVEN_QUERY_SORT_DIRECTION);
        AccountUser accountUser = TolvenRequest.getInstance().getAccountUser();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Content-Disposition", "attachment; filename=" + tolvenQueryParameter + "." + reportFormat);
        try {
            /*
             * Reqeuest for the report to be written
             */
            reportLocal.writeReport(reportName, reportType, tolvenQuery, tolvenQueryParameter, sortOrder, sortDirection, accountUser, response.getOutputStream(), reportFormat);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        doGet(request, response);
    }

}
