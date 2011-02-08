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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;

/**
 * This datasource is used by Jasper to obtain data from reports, based on a tolvenQuery (e.g. echr:patient)
 * and a tolvenQueryParameter (e.g. echr:patient-NNNN). The accountUser is also required to ensure that data
 * is restricted to a particular account and user.
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenReportDataSource implements JRDataSource {

    private AccountUser accountUser;
    private String tolvenQuery;
    private String tolvenQueryParameter;
    private String sortOrder;
    private String sortDirection;

    private List<TolvenReportMap> reportMaps = null;
    private int index = -1;

    public TolvenReportDataSource(String tolvenQueryString, String tolvenQueryParameter, AccountUser accountUser) {
        this(tolvenQueryString, tolvenQueryParameter, "date01", "desc", accountUser);        
    }

    public TolvenReportDataSource(String tolvenQueryString, String tolvenQueryParameter, String sortOrder, String sortDirection, AccountUser accountUser) {
        this.accountUser = accountUser;
        this.tolvenQuery = tolvenQueryString;
        this.tolvenQueryParameter = tolvenQueryParameter;
        this.sortOrder = sortOrder;
        this.sortDirection = sortDirection;
    }

    public AccountUser getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(AccountUser accountUser) {
        this.accountUser = accountUser;
    }

    public String getMenuPath() {
        return tolvenQuery;
    }

    public void setMenuPath(String menuPath) {
        this.tolvenQuery = menuPath;
    }

    public List<TolvenReportMap> getReportMaps() {
        return reportMaps;
    }

    public void setReportMaps(List<TolvenReportMap> reportMaps) {
        this.reportMaps = reportMaps;
    }

    public String getTolvenQuery() {
        return tolvenQuery;
    }

    public void setTolvenQuery(String tolvenQuery) {
        this.tolvenQuery = tolvenQuery;
    }

    public String getTolvenQueryParameter() {
        return tolvenQueryParameter;
    }

    public void setTolvenQueryParameter(String tolvenQueryParameter) {
        this.tolvenQueryParameter = tolvenQueryParameter;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    /**
     * Move to the next TolvenReportMap
     */
    @Override
    public boolean next() {
        if (getReportMaps() == null) {
            try {
                fetchTolvenReportMaps();
            } catch (NamingException ex) {
                throw new RuntimeException(ex);
            }
        }
        index++;
        return (index < getReportMaps().size());
    }

    /**
     * Return a field from from the datasource
     */
    @Override
    public Object getFieldValue(JRField field) {
        Object value = null;
        TolvenReportMap currentReportMap = getReportMaps().get(index);
        if (currentReportMap != null) {
            value = currentReportMap.get(field.getName());
        }
        return value;
    }

    /**
     * 
     * @throws NamingException
     */
    protected void fetchTolvenReportMaps() throws NamingException {
        InitialContext ctx = new InitialContext();
        setReportMaps(new ArrayList<TolvenReportMap>());
        MenuLocal menuBean = (MenuLocal) ctx.lookup("java:global/tolven/tolvenEJB/MenuBean!org.tolven.app.MenuLocal");
        /*
         * Find the menustructure represented by the tolvenQuery and place it in a MenuQueryControl
         */
        MenuStructure menuStructure = menuBean.findMenuStructure(accountUser, getTolvenQuery());
        MenuPath menuPath = new MenuPath(getTolvenQueryParameter());
        MenuQueryControl ctrl = new MenuQueryControl();
        ctrl.setMenuStructure(menuStructure);
        ctrl.setAccountUser(accountUser);
        ctrl.setMenuStructure(menuStructure.getAccountMenuStructure());
        ctrl.setNow(new java.util.Date());
        /*
         * Supply the tolvenQueryParameter as a MenuPath to the MenuQueryControl
         */
        ctrl.setOriginalTargetPath(menuPath);
        ctrl.setRequestedPath(menuPath);
        /*
         * Retrieve the actual MenuData based on the qualifed query and wrap each MenuData with
         * a TolvenReportMap
         */
        ctrl.setSortOrder(getSortOrder());
        ctrl.setSortDirection(getSortDirection());
        List<MenuData> menuDatas = menuBean.findMenuData(ctrl);
        TolvenReportMap reportMap = null;
        for (MenuData menuData : menuDatas) {
            reportMap = newTolvenReportMap(menuData);
            getReportMaps().add(reportMap);
        }
    }

    /**
     * Create a new TolveReportMap which wraps the MenuData. This will allow other data to be associated
     * with a MenuData, without contaminating the MenuData object itself
     * @param menuData
     * @return
     */
    public TolvenReportMap newTolvenReportMap(MenuData menuData) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        TolvenReportMap reportMap = new TolvenReportMap(menuData, parameterMap);
        return reportMap;
    }

    /**
     * This is where subreports get their compiled version of the jrxml that they require. This method will be in
     * all reports which need to fetch their report via ReportLocal
     * 
     * @param sourceFileName
     * @return
     * @throws IOException
     * @throws JRException
     */
    public static JasperReport getJasperReport(String name) throws JRException {
        try {
            InitialContext ctx = new InitialContext();
            ReportLocal reportLocal = (ReportLocal) ctx.lookup("java:global/tolven/tolvenEJB/ReportBean!org.tolven.report.ReportLocal");
            JasperReport report = reportLocal.getJasperReport(name);
            return report;
        } catch (NamingException ex) {
            throw new JRException(ex);
        }
    }

}
