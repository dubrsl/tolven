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
package org.tolven.analysis.bean;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.tolven.analysis.PercentTimeSeriesLocal;
import org.tolven.analysis.PercentTimeSeriesRemote;
import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;

/**
 * @author Joseph Isaac
 */
@Stateless
@Local(PercentTimeSeriesLocal.class)
@Remote(PercentTimeSeriesRemote.class)
public class PercentTimeSeriesBean implements PercentTimeSeriesLocal, PercentTimeSeriesRemote, Serializable {

    @EJB
    private MenuLocal menuBean;

    @Override
    public JFreeChart getChart(MenuStructure snapshotListMS, MenuPath snapshotPH, Long chartRange, Class<?> intervalUnitClass, String chartDataTitle, String chartTargetTitle, AccountUser accountUser, Date now) {
        MenuQueryControl ctrl = new MenuQueryControl();
        ctrl.setMenuStructure(snapshotListMS);
        ctrl.setAccountUser(accountUser);
        ctrl.setNow(now);
        ctrl.setOriginalTargetPath(snapshotPH);
        ctrl.setRequestedPath(snapshotPH);
        ctrl.setSortOrder("Date");
        ctrl.setSortDirection("DESC");
        ctrl.setLimit(1);
        List<MenuData> singleItemList = menuBean.findMenuData(ctrl);
        Date lastSnapshotDate = null;
        if (singleItemList.isEmpty()) {
            lastSnapshotDate = now;
        } else {
            MenuData snapshotListItem = singleItemList.get(0);
            MenuData snapshot = snapshotListItem.getReference();
            lastSnapshotDate = new Date(snapshot.getDate01().getTime());
        }
        RegularTimePeriod endTimePeriod = RegularTimePeriod.createInstance(intervalUnitClass, lastSnapshotDate, TimeZone.getDefault());
        long milliseconds = Math.max(1, chartRange - 1) * (endTimePeriod.getEnd().getTime() - endTimePeriod.getStart().getTime());
        Date fDate = new Date(endTimePeriod.getEnd().getTime() - milliseconds);
        RegularTimePeriod startTimePeriod = RegularTimePeriod.createInstance(intervalUnitClass, fDate, TimeZone.getDefault());
        Date fromDate = startTimePeriod.getStart();
        Date toDate = endTimePeriod.getEnd();
        ctrl.setSortDirection("ASC");
        ctrl.setLimit(0);
        ctrl.setFromDate(fromDate);
        ctrl.setToDate(toDate);
        List<MenuData> snapshotListItems = menuBean.findMenuData(ctrl);
        List<MenuData> snapshots = new ArrayList<MenuData>();
        for(MenuData snapshotListItem : snapshotListItems) {
            snapshots.add(snapshotListItem.getReference());
        }
        return getChart(chartDataTitle, chartTargetTitle, snapshots, fromDate, toDate, intervalUnitClass);
    }

    private JFreeChart getChart(String dataSeriesTitle, String targetSeriesTitle, List<MenuData> snapshots, Date fromDate, Date toDate, Class<?> intervalUnitClass) {
        TimeSeries dataTimeSeries = new TimeSeries(dataSeriesTitle);
        TimeSeries targetTimeSeries = null;
        if (targetSeriesTitle != null) {
            targetTimeSeries = new TimeSeries(targetSeriesTitle);
        }
        for (MenuData snapshot : snapshots) {
            Date snapshotDate = snapshot.getDate01();
            long nSnapshotresultsNumerator = snapshot.getLongField("normCount");
            long nSnapshotresultsDenominator = snapshot.getLongField("allCount");
            Double value = null;
            if (nSnapshotresultsDenominator == 0) {
                value = 0d;
            } else {
                value = 1d * nSnapshotresultsNumerator / nSnapshotresultsDenominator;
            }
            RegularTimePeriod regTimePeriod = RegularTimePeriod.createInstance(intervalUnitClass, snapshotDate, TimeZone.getDefault());
            dataTimeSeries.addOrUpdate(regTimePeriod, value);
            if (targetTimeSeries != null) {
                Double targetPercent = snapshot.getDoubleField("targetPercent") / 100;
                targetTimeSeries.addOrUpdate(regTimePeriod, targetPercent);
            }
        }
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        timeSeriesCollection.addSeries(dataTimeSeries);
        if (targetTimeSeries != null) {
            timeSeriesCollection.addSeries(targetTimeSeries);
        }
        XYDataset xyDataset = (XYDataset) timeSeriesCollection;
        JFreeChart chart = ChartFactory.createTimeSeriesChart(null, // title
        null, // x-axis label
        null, // y-axis label
        xyDataset, // data
        true, // create legend?
        false, // generate tooltips?
        false // generate URLs?
        );
        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(false);
        XYItemRenderer r = plot.getRenderer();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6));
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesShape(1, new Rectangle2D.Double(-3, -3, 6, 6));
        renderer.setSeriesPaint(1, Color.RED);
        NumberAxis vaxis = (NumberAxis) plot.getRangeAxis();
        vaxis.setAutoRange(true);
        vaxis.setAxisLineVisible(true);
        vaxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
        vaxis.setTickMarksVisible(true);
        DateAxis daxis = (DateAxis) plot.getDomainAxis();
        daxis.setRange(fromDate, toDate);
        if (intervalUnitClass == Month.class) {
            DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
            dateFormatSymbols.setShortMonths(new String[] {
                    "J",
                    "F",
                    "M",
                    "A",
                    "M",
                    "J",
                    "J",
                    "A",
                    "S",
                    "O",
                    "N",
                    "D" });
            daxis.setDateFormatOverride(new SimpleDateFormat("MMM", dateFormatSymbols));
        }
        return chart;
    }

}
