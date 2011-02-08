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

import java.io.IOException;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.tolven.analysis.SnapshotLocal;
import org.tolven.core.entity.AccountUser;

/**
 * This class handles charts for portlets
 * 
 * @author Joseph Isaac
 *
 */
public class PortletChartServlet extends HttpServlet {

    @EJB
    private SnapshotLocal snapshotBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String cohortType = request.getParameter("cohortType");
        if (cohortType == null) {
            throw new RuntimeException("A request cohortType cannot be null");
        }
        String snapshotType = request.getParameter("snapshotType");
        if (snapshotType == null) {
            throw new RuntimeException("A request snapshotType cannot be null");
        }
        String chartType = request.getParameter("chartType");
        if (chartType == null) {
            throw new RuntimeException("A request chartType cannot be null");
        }
        AccountUser accountUser = (AccountUser) request.getAttribute("accountUser");
        Date now = (Date) request.getAttribute("tolvenNow");
        JFreeChart chart = snapshotBean.getChart(cohortType, snapshotType, chartType, accountUser, now);
        int chartWidth = Integer.parseInt(accountUser.getAccount().getProperty().get("org.tolven.analysis.bean.percenttimeseries.portlet.width"));
        int chartHeight = Integer.parseInt(accountUser.getAccount().getProperty().get("org.tolven.analysis.bean.percenttimeseries.portlet.height"));
        try {
            ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, chartWidth, chartHeight);
        } catch (IOException ex) {
            throw new RuntimeException("Could not write PNG chart", ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        doGet(request, response);
    }

}
