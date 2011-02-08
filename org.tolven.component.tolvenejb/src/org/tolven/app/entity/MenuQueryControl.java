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
package org.tolven.app.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.tolven.app.bean.MenuPath;
import org.tolven.core.entity.AccountUser;

/**
 * Specific QueryControl used for MenuData queries. Most of the important attributes are in the base class, QueryControl.
 * @author John Churin
 *
 */
public class MenuQueryControl extends QueryControl implements Serializable  {

	private static final long serialVersionUID = 1L;
	private String menuStructurePath;
	private AccountUser accountUser;
	private transient MenuStructure menuStructure;
	private String actualMenuStructurePath;
	private transient MenuStructure actualMenuStructure;
	private transient MenuPath resolvedPath;
	private Date fromDate = null;
	private Date toDate = null;
	private List<String> columns = new ArrayList<String>();
	private TimeZone timeZone;
	private Locale locale;
	
	public MenuQueryControl() {
		super();
	}

	/**
	 * Prepare a menuData query
	 * @param accountId
	 * @param menuStructurePath
	 */
	public MenuQueryControl(AccountUser accountUser, String menuStructurePath) {
		super();
		this.accountUser = accountUser;
		this.menuStructurePath = menuStructurePath;
	}
	
	public MenuPath originalTargetPath;

	public MenuPath requestedPath;
	
	public MenuStructure getMenuStructure() {
		return menuStructure;
	}
	

	/**
	 * Set the menuStructure to query - This is not the preferred way to specify MenuStructure. 
	 * @see #setMenuStructurePath(String)
	 * @param menuStructure
	 */
	public void setMenuStructure(MenuStructure menuStructure) {
		this.menuStructure = menuStructure;
		
	}

	/**
	 * The path to the pane the user actually wants to see. The requested pane could be a menu above this target. 
	 * @return The path to the original target
	 */
	public MenuPath getOriginalTargetPath() {
		return originalTargetPath;
	}


	public void setOriginalTargetPath(MenuPath originalTargetPath) {
		this.originalTargetPath = originalTargetPath;
	}

	/**
	 * return the path to the pane the browser is asking for.
	 */
	public MenuPath getRequestedPath() {
		return requestedPath;
	}

	public void setRequestedPath(MenuPath requestedPath) {
		this.requestedPath = requestedPath;
	}

	/**
	 * An application should reference fields by the application's field name. 
	 * This method translates those field name to the underlying database name. For example, if the 
	 * Patient's last name attribute is mapped to String01, then calling this method with lastName will return string01.
	 */
	public String getInternalName( String externalName) {
		return null;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer( 500 );
		sb.append( " AccountId:"); sb.append(getAccountId());
		sb.append( " MenuStructure:"); sb.append(getMenuStructurePath());
		if (getRequestedPath()!=null) {
			sb.append( " Requested Path: ["); sb.append(getRequestedPath().getPathString());sb.append( "]");
		}
		if (getFromDate()!=null) {
			sb.append( " From : ["); sb.append(getFromDate());sb.append( "]");
			if (getToDate()!=null) {
				sb.append( " To : ["); sb.append(getToDate());sb.append( "]");
			}				
		}
		
		sb.append( " Original Path: ["); 
			if (getOriginalTargetPath()!=null) {
				sb.append(getOriginalTargetPath().getPathString());
			} else {sb.append("null");}
			sb.append( "]");
		sb.append( " Resolved Path: ["); sb.append(getResolvedPath().getPathString());sb.append( "]");
		sb.append(super.toString());
		return sb.toString();
	}

	/**
	 * When a query is setup, this contains the requested Menustructure. If the requested MenuStructure
	 * references another MenuStructure, then the actual menustructure will be set accordingly.
	 * @return
	 */
	public MenuStructure getActualMenuStructure() {
		if (actualMenuStructure==null) return getMenuStructure();
		return actualMenuStructure;
	}

	public void setActualMenuStructure(MenuStructure actualMenuStructure) {
		this.actualMenuStructure = actualMenuStructure;
	}

	public MenuPath getResolvedPath( ) {
		if (resolvedPath==null) {
			// We need to start with a new path based on the referenced list, not the original list
			resolvedPath = new MenuPath( getActualMenuStructure().getPath(), getRequestedPath() );
		}
		return resolvedPath;
	}

	public long getParentId( int level) {
        long[] snk = getResolvedPath().getSignificantNodeKeys( );
		if (level < snk.length ) {
			return snk[level];
		} else {
			return 0;
		}
	}


	public Date getFromDate() {
		return fromDate;
	}


	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}


	public Date getToDate() {
		return toDate;
	}


	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}


	public String getMenuStructurePath() {
		return menuStructurePath;
	}

	/**
	 * When calling a query from a remote client, or when the MenuStructure is not yet known,
	 * set the path to the menuStructure here.
	 * @param menuStructurePath
	 */
	public void setMenuStructurePath(String menuStructurePath) {
		this.menuStructurePath = menuStructurePath;
	}
	
	/**
	 * Get the resolved list of columns to use for this query
	 * @return Sorted list of columns
	 */
	public List<MSColumn> getSortedColumns() {
		List<MSColumn> mdSortedColumns = getMenuStructure().getSortedColumns();
		if (mdSortedColumns.size()==0) {
			mdSortedColumns = getActualMenuStructure().getSortedColumns();
		}
		return mdSortedColumns;
	}

	public String getActualMenuStructurePath() {
		return actualMenuStructurePath;
	}


	public void setActualMenuStructurePath(String actualMenuStructurePath) {
		this.actualMenuStructurePath = actualMenuStructurePath;
	}


	public long getAccountId() {
		return getAccountUser().getAccount().getId();
	}

	/**
	 * A list of columns to be returned when calling findMenuDataRows
	 * If this list is empty, then all columns are returned in the order specified in the application.xml.
	 * @return
	 */
	public List<String> getColumns() {
		return columns;
	}

	public AccountUser getAccountUser() {
		return accountUser;
	}

	public void setAccountUser(AccountUser accountUser) {
		this.accountUser = accountUser;
	}

	public Locale getLocale() {
		if (locale!=null) return locale;
		if (locale==null) {
			String userLocale = getAccountUser().getUser().getLocale();
			if (userLocale!=null) {
				locale= new Locale(userLocale); 
			}
		}
		if (locale==null) {
			String accountLocale = getAccountUser().getAccount().getLocale();
			if (accountLocale!=null) {
				locale = new Locale(accountLocale); 
			}
		}
		if (locale==null) {
			locale = Locale.getDefault();
		}
		return locale;
	}

	public TimeZone getTimeZone() {
		if (timeZone==null) {
			timeZone = TimeZone.getTimeZone(accountUser.getTimeZone() );
		}
		return timeZone;
		
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}


}
