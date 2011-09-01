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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;

import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MDQueryResults;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;

/**
 * This datasource is used by Jasper to obtain data from reports, based on a tolvenQuery (e.g. echr:patient)
 * and a tolvenQueryParameter (e.g. echr:patient-NNNN). The accountUser is also required to ensure that data
 * is restricted to a particular account and user.
 * 
 * @author Pinky S <pinky.sindhu@cyrusxp.com>
 * @author Vineetha George 
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	|     07/29/2010        |         Pinky S	    |      Initial Version 
==============================================================================================================================================
2    	|     03/16/2011        |     Vineetha George 	|      Class extends TolvenReportDataSource & Overrides fetchTolvenReportMaps()
==============================================================================================================================================
*/
public class CCHITReportDatasource extends TolvenReportDataSource {
	private List<Map<String, Object>> cchitReportMaps = null;
	private int index = -1;

	public CCHITReportDatasource(String tolvenQueryString,
			String tolvenQueryParameter, AccountUser accountUser) {
		this(tolvenQueryString, tolvenQueryParameter, "date01", "desc",
				accountUser);
	}

	public CCHITReportDatasource(String tolvenQueryString,
			String tolvenQueryParameter, String sortOrder,
			String sortDirection, AccountUser accountUser) {
		super(tolvenQueryString, tolvenQueryParameter, sortOrder,
				sortDirection, accountUser);
	}

	/**
	 * Move to the next TolvenReportMap
	 */
	@Override
	public boolean next() {
		if (cchitReportMaps == null) {
			try {
				fetchTolvenReportMaps();
			} catch (NamingException ex) {
				throw new RuntimeException(ex);
			}
		}
		index++;
		return (index < cchitReportMaps.size());
	}

	/**
	 * Return a field from from the datasource
	 */
	@Override
	public Object getFieldValue(JRField field) {
		Object value = null;
		Map currentReportMap = cchitReportMaps.get(index);
		if (currentReportMap != null) {
			value = currentReportMap.get(field.getName());
		}
		return value;
	}

	/**
	 * 
	 * @throws NamingException
	 */
	@Override
	protected void fetchTolvenReportMaps() throws NamingException {
		InitialContext ctx = new InitialContext();
		MenuQueryControl ctrl = new MenuQueryControl();
		MenuLocal menuBean = (MenuLocal) ctx.lookup("java:global/tolven/tolvenEJB/MenuBean!org.tolven.app.MenuLocal");
		MenuPath path = new MenuPath(getTolvenQueryParameter());

		long accountId = getAccountUser().getAccount().getId();
		MenuStructure ms = menuBean.findMenuStructure(accountId,
				getTolvenQuery());
		ctrl = new MenuQueryControl();
		ctrl.setAccountUser(getAccountUser());
		ctrl.setMenuStructure(ms);
		ctrl.setNow(new Date());
		ctrl.setOriginalTargetPath(path);
		ctrl.setRequestedPath(path);

		// Do the query
		MDQueryResults mdQueryResults = menuBean.findMenuDataByColumns(ctrl);
		cchitReportMaps = mdQueryResults.getResults();

	}

	/**
	 * Create a new TolveReportMap which wraps the MenuData. This will allow
	 * other data to be associated with a MenuData, without contaminating the
	 * MenuData object itself
	 * 
	 * @param menuData
	 * @return
	 */
	public TolvenReportMap newTolvenReportMap(MenuData menuData) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		TolvenReportMap reportMap = new TolvenReportMap(menuData, parameterMap);
		return reportMap;
	}

	/**
	 * This is where subreports get their compiled version of the jrxml that
	 * they require. This method will be in all reports which need to fetch
	 * their report via ReportLocal
	 * 
	 * @param sourceFileName
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	public static JasperReport getJasperReport(String name) throws JRException {
		try {
			InitialContext ctx = new InitialContext();
			ReportLocal reportLocal = (ReportLocal) ctx
					.lookup("java:global/tolven/tolvenEJB/ReportBean!!org.tolven.report.ReportLocal");			
			JasperReport report = reportLocal.getJasperReport(name);
			return report;
		} catch (NamingException ex) {
			throw new JRException(ex);
		}
	}
}