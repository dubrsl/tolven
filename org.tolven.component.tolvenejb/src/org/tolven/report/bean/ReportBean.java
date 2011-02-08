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

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import org.tolven.core.entity.AccountUser;
import org.tolven.logging.TolvenLogger;
import org.tolven.report.ReportLocal;
import org.tolven.report.TolvenReportDataSource;
import org.tolven.report.entity.TolvenReport;

/**
 * 
 * @author Joseph Isaac
 *
 */
@Stateless
@Local(ReportLocal.class)
public class ReportBean implements ReportLocal {

    public static final String JRXML = "jrxml";
    
    @PersistenceContext
    private EntityManager em;

    @Resource
    EJBContext ejbContext;

    private EntityManager getEm() {
        return em;
    }

    public EJBContext getEjbContext() {
        return ejbContext;
    }

    /**
    * Find an accountType by knownType
    * @return AccountType
    */
    public TolvenReport findTolvenReport(String name, String type) {
        Query q = getEm().createQuery("SELECT tolvenReport FROM TolvenReport tolvenReport WHERE tolvenReport.name = :name and tolvenReport.type = :type");
        q.setParameter("name", name);
        q.setParameter("type", type);
        List<TolvenReport> reports = q.getResultList();
        if (reports.size() == 1) {
            return reports.get(0);
        } else if (reports.size() == 0) {
            return null;
        } else {
            throw new RuntimeException("More than one TolvenReport found with name: " + name + " and type: " + type);
        }
    }

    /**
     * Store the report as a String
     */
    public void storeReport(String reportAsString, String reportType, Date uploadTime) {
        storeReport(null, reportAsString, uploadTime);
    }
    
    /**
     * Store the report as a String. An exception will be thrown if the externalRepotName is supplied and not found to
     * be the same as that within the report itself.
     * 
     * @param externalReportName
     * @param reportAsString
     * @param reportType
     */
    public void storeReport(String externalReportName, String reportAsString, String reportType, Date uploadTime) {
        try {
            if (!JRXML.equals(reportType)) {
                throw new RuntimeException("Only Jasper jrxml report types are supported");
            }
            if (getEjbContext().getCallerPrincipal() == null) {
                /*
                 * This can be removed once roles are operating
                 */
                throw new RuntimeException("A null principal cannot store or modify JRXML reports");
            }
            String principalName = getEjbContext().getCallerPrincipal().getName();
            ByteArrayInputStream bis = new ByteArrayInputStream(reportAsString.getBytes());
            JasperReport report = JasperCompileManager.compileReport(bis);
            /*
             * Jasper reports have an internal report name. If the external one is specified, throw an exception to alert
             * the caller that the names are out of synch
             */
            String internalReportName = report.getName();
            if(externalReportName != null && !externalReportName.equals(internalReportName)) {
                throw new RuntimeException("The external report name is not the same as that found in the Jasper report");
            }
            String validatedReportAsString = JRXmlWriter.writeReport(report, Charset.defaultCharset().name());
            TolvenReport tolvenReport = findTolvenReport(internalReportName, JRXML);
            if (tolvenReport == null) {
                tolvenReport = new TolvenReport();
                tolvenReport.setName(internalReportName);
                tolvenReport.setReport(validatedReportAsString);
                tolvenReport.setType(JRXML);
                tolvenReport.setUserId(principalName);
                tolvenReport.setDate(uploadTime);
                getEm().persist(tolvenReport);
            } else {
                tolvenReport.setReport(validatedReportAsString);
                tolvenReport.setUserId(principalName);
                tolvenReport.setDate(uploadTime);
                getEm().merge(tolvenReport);
            }
            TolvenLogger.info("Storing " + reportType + " report: " + internalReportName, ReportBean.class);
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Based on the supplied reportName, write the report to the servlet outputstream where tolvenQuery is a menu path
     * eg echr:patient and tolvenQueryParameter is also a menu path for a specific query e.g. echr:patient-NNNN, representing
     * a particular patient. The accountUser is supplied to ensure that information is restricted to a particular account for
     * a given user. The reportFormat defaults to pdf, but can be other formats supported by JasperReports
     */
    public void writeReport(String reportName, String reportType, String tolvenQuery, String tolvenQueryParameter, String sortOrder, String sortDirection, AccountUser accountUser, OutputStream out, String reportFormat) throws Exception {
        if(!JRXML.equals(reportType)) {
            throw new RuntimeException("Only Jasper jrxml report types are supported");
        }
        TolvenReportDataSource dataSource = new TolvenReportDataSource(tolvenQuery, tolvenQueryParameter, sortOrder, sortDirection, accountUser);
        JasperReport report = getJasperReport(reportName);
        java.util.Map<String, Object> parameterMap = new java.util.HashMap<String, Object>();
        parameterMap.put("accountUser", accountUser);
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameterMap, dataSource);
        if (reportFormat == null || "pdf".toLowerCase().equals(reportFormat.toLowerCase())) {
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            exporter.exportReport();
        } else {
            throw new RuntimeException("Report format unrecognized: " + reportName);
        }
    }

    /**
     * Find JasperReport by name
     * @param name
     * @return
     * @throws Exception
     */
    public JasperReport getJasperReport(String name) throws JRException {
        TolvenReport tolvenReport = findTolvenReport(name, JRXML);
        if (tolvenReport == null) {
            throw new RuntimeException("No TolvenReport found called: " + name);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(tolvenReport.getReport().getBytes());
        JasperReport report = JasperCompileManager.compileReport(bis);
        return report;
    }

}
