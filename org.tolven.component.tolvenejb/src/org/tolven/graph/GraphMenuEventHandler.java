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
 * @version $Id$
 */
package org.tolven.graph;

import java.awt.Color;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.NamingException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.tolven.app.MenuEventHandler;
import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MSAction;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.logging.TolvenLogger;

public class GraphMenuEventHandler extends MenuEventHandler {

    @EJB
    private MenuLocal menuBean;

    public GraphMenuEventHandler(MSAction action) {
        super(action);
    }

    protected MenuLocal getMenuBean() throws NamingException {
        if (menuBean == null) {
            setMenuBean((MenuLocal) getContext().lookup("java:global/tolven/tolvenEJB/MenuBean!org.tolven.app.MenuLocal"));
        }
        return menuBean;
    }

    private void setMenuBean(MenuLocal menuBean) {
        this.menuBean = menuBean;
    }

    public void initializeAction() throws Exception {
        getWriter().write("<input type=\"button\" value=\"" + getAction().getText() + "\" onclick=\"javascript:displayDatasets('displayDatasets', '" + getAction().getPath() + "','" + getElement() + "');\"/>");
    }

    public void displayDatasets() throws Exception {
        StringBuffer sbuffer = new StringBuffer();
        sbuffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
        sbuffer.append("xmlns:ui=\"http://java.sun.com/jsf/facelets\"");
        sbuffer.append("xmlns:f=\"http://java.sun.com/jsf/core\"");
        sbuffer.append("xmlns:h=\"http://java.sun.com/jsf/html\"");
        sbuffer.append("xmlns:c=\"http://java.sun.com/jsp/jstl/core\">");
        sbuffer.append("<span style='font-size:1.6em;font-weight:bold;'>Select Datasets</span>");
        sbuffer.append("<table cellspacing='5' cellpadding='5'>");
        sbuffer.append("<tr><td>");
        Map<String, String[]> datasetUnitMap = createDatasetUnitsMap();
        sbuffer.append("<div id=\"checkboxinputs\" style=\"text-align:left\">");
        for (String[] datasetUnitArray : datasetUnitMap.values()) {
            String dataset = datasetUnitArray[0];
            String unit = datasetUnitArray[1];
            String datasetUnit = dataset + unit;
            sbuffer.append("<input type=\"checkbox\" value=\"" + datasetUnit + "\" />" + dataset + " (" + unit + ")");
            sbuffer.append("<br/>");
        }
        sbuffer.append("</div>");
        sbuffer.append("</td></tr>");
        sbuffer.append("<tr><td>");
        sbuffer.append("<br/>");
        sbuffer.append("<div style=\"text-align:left\">");
        sbuffer.append("<input type=\"button\" value=\"Graph\" onclick=\"displayGraph('displayGraph', '" + getAction().getPath() + "','" + getElement() + "');\"/> ");
        sbuffer.append("<input type=\"button\" value=\"Cancel\" onclick=\"closeGraphDiv();\" /> ");
        sbuffer.append("</div>");
        sbuffer.append("</td></tr>");
        sbuffer.append("</table>");
        sbuffer.append("</html>");
        getWriter().write(sbuffer.toString());
    }

    public void displayGraph() throws Exception {
        JFreeChart chart = createChart();
        int width = 600;
        int height = 400;
        String filename = ServletUtilities.saveChartAsPNG(chart, width, height, getRequest().getSession());
        String graphURL = "my.graph?filename=" + URLEncoder.encode(filename, "UTF-8");
        StringBuffer sbuffer = new StringBuffer();
        sbuffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
        sbuffer.append("xmlns:ui=\"http://java.sun.com/jsf/facelets\"");
        sbuffer.append("xmlns:f=\"http://java.sun.com/jsf/core\"");
        sbuffer.append("xmlns:h=\"http://java.sun.com/jsf/html\"");
        sbuffer.append("xmlns:c=\"http://java.sun.com/jsp/jstl/core\">");
        sbuffer.append("<img width=\"" + width + "\" height=\"" + height + "\" src=\"" + graphURL + "\"/>");
        sbuffer.append("<input type=\"button\" value=\"Close\" onclick=\"closeGraphDiv();\" /> ");
        sbuffer.append("</html>");
        getWriter().write(sbuffer.toString());
    }

    private Map<String, String[]> createDatasetUnitsMap() throws Exception {
        MenuStructure dataMS = getAction().getParent();
        Properties eventHandlerData = getAction().getMenuEventHandlerDataMap();
        String unitsColumn = eventHandlerData.getProperty("unitsColumn");
        String datasetColumn = eventHandlerData.getProperty("datasetColumn");
        Map<String, String[]> datasetUnitMap = new HashMap<String, String[]>();
        MenuPath elementPath = new MenuPath(getElement());
        MenuPath mdPath = new MenuPath(dataMS.getPath(), elementPath);
        MenuQueryControl ctrl = new MenuQueryControl();
        ctrl.setLimit(5000);
        ctrl.setMenuStructure(dataMS);
        ctrl.setAccountUser(getAccountUser());
        ctrl.setNow(getTolvenNow());
        ctrl.setOffset(0);
        ctrl.setOriginalTargetPath(mdPath);
        ctrl.setRequestedPath(mdPath);
        List<MenuData> menuData = getMenuBean().findMenuData(ctrl);
        for (MenuData md : menuData) {
            String dataset = md.getStringField(datasetColumn);
            String unit = md.getStringField(unitsColumn);
            datasetUnitMap.put(dataset + unit, new String[] { dataset, unit });
        }
        return datasetUnitMap;
    }

    /**
     * Creates a chart based on MenuData
     * @param dataset  a dataset
     * @return A chart suitable for rendering
     * @throws Exception 
     */
    private JFreeChart createChart() throws Exception {
        XYDataset xyDataset = createDataset();
        Properties menuEventHandlerData = getAction().getMenuEventHandlerDataMap();
        String title = menuEventHandlerData.getProperty("title");
        String xAxisLabel = menuEventHandlerData.getProperty("xAxisLabel");
        String yAxisLabel = menuEventHandlerData.getProperty("yAxisLabel");
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, // title
        xAxisLabel, // x-axis label
        yAxisLabel, // y-axis label
        xyDataset, // data
        true, // create legend?
        true, // generate tooltips?
        false // generate URLs?
        );
        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
        }
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        //CCHIT merge
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
       // rangeAxis.setStandardTickUnits(NumberAxis.cr);
        NumberFormat formatter = new DecimalFormat("#0.00");
        rangeAxis.setAutoRangeIncludesZero(false);        
        rangeAxis.setNumberFormatOverride(formatter);
        
        return chart;
    }

    private XYDataset createDataset() throws Exception {
        MenuPath elementPath = new MenuPath(getElement());
        MenuStructure dataMS = getAction().getParent();
        MenuPath mdPath = new MenuPath(dataMS.getPath(), elementPath);
        MenuQueryControl ctrl = new MenuQueryControl();
        ctrl.setLimit(5000); // TODO: This is a hard coded hard query limit that should be in a property or something
        ctrl.setMenuStructure(dataMS);
        ctrl.setAccountUser(getAccountUser());
        ctrl.setNow(getTolvenNow());
        ctrl.setOffset(0);
        ctrl.setOriginalTargetPath(mdPath);
        ctrl.setRequestedPath(mdPath);
        List<MenuData> menuData = getMenuBean().findMenuData(ctrl);
        Properties menuEventHandlerData = getAction().getMenuEventHandlerDataMap();
        String timeColumn = menuEventHandlerData.getProperty("timeColumn");
        String valueColumn = menuEventHandlerData.getProperty("valueColumn");
        String unitsColumn = menuEventHandlerData.getProperty("unitsColumn");
        String datasetColumn = menuEventHandlerData.getProperty("datasetColumn");
        String[] datasetUnitsArray = getRequest().getParameter("datasetunits").split(",");
        List<String> datasetUnits = new ArrayList<String>();
        for (String string : datasetUnitsArray) {
            datasetUnits.add(string);
        }
        Map<String, String[]> datasetUnitMap = createDatasetUnitsMap();
        Map<String, TimeSeries> timeSeriesMap = new HashMap<String, TimeSeries>();
        for (String[] datasetUnitArray : datasetUnitMap.values()) {
            String dataset = datasetUnitArray[0];
            String unit = datasetUnitArray[1];
            String datasetUnit = dataset + unit;
            if (datasetUnits.contains(datasetUnit)) {
                timeSeriesMap.put(datasetUnit, new TimeSeries(dataset + " (" + unit + ")", Hour.class));
            }
        }
        for (MenuData md : menuData) {
            String dataset = md.getStringField(datasetColumn);
            String units = md.getStringField(unitsColumn);
            String datasetUnit = dataset + units;
            TimeSeries currentTimeSeries = timeSeriesMap.get(datasetUnit);
            if (currentTimeSeries != null) {
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(md.getDateField(timeColumn));
                Hour hour = new Hour(cal.getTime());
                currentTimeSeries.addOrUpdate(hour,md.getInternalPQValueField(md.getColumn(valueColumn).getInternal()));
                
            }
        }
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        for (TimeSeries timeSeries : timeSeriesMap.values()) {
            timeSeriesCollection.addSeries(timeSeries);
        }
        timeSeriesCollection.setDomainIsPointsInTime(true);
        TolvenLogger.info("Done preparing Dataset", GraphMenuEventHandler.class);
        return timeSeriesCollection;
    }

}
