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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * An argument-passing bean specifying query range, sort criteria and direction, and filter criteria.
 * @author John Churin
 *
 */
public class QueryControl implements Serializable {
	private static final long serialVersionUID = 1L;
	private Date now;
	private int limit = 0;
	private int offset = 0;
	private String sortOrder;
	private String sortDirection;
	private String filter;
	private Map<String, Object> filters;
	public QueryControl() {
		super();
	}
	/**
	 * Return the maximum number of rows to return. The actual number may be less. A value of zero means to return all rows.
	 * @return Number of rows to return
	 */
	public int getLimit() {
		return limit;
	}
	
	/**
	 * Set the maximum number of rows to return. A value of zero means to return all rows.
	 * @param limit
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	/**
	 * Queries are sometimes based on knowing the current time. In order to avoid problems with different
	 * queries using different values for now (usually only varying by milliseconds but nevertheless causing 
	 * strange results when items fall though the cracks). At the beginning of a transaction, a single transaction-wide "now" is
	 * established and this everything occurs either before or after this one timestamp.
	 * @return A Date (timestamp)
	 */
	public Date getNow() {
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}

	/**
	 * The offset into the result set to start returning results. An offset of zero causes the query to start at the begining. 
	 * @return 
	 */
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	/**
	 * ASC or DESC to specify direction of the sort. May be null in which case the result set is not sorted.
	 * @return sort direction indicator (ascending or descending)
	 */
	public String getSortDirection() {
		return sortDirection;
	}
	
	public void setSortDirection(String sortDirection) {
        if (sortDirection==null || sortDirection.length()==0) {
        	sortDirection = "ASC";
		} else {
			this.sortDirection = sortDirection;
		}
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer( 500 );
		sb.append( " Sort: "); sb.append(getSortOrder()); sb.append(" " );sb.append(getSortDirection());
		sb.append( " Now: "); sb.append(getNow());
		sb.append( " Offset: "); sb.append(getOffset());
		sb.append( " Limit: "); sb.append(getLimit());
		sb.append( " Filters: "); sb.append( getFilters() );
		return sb.toString();
	}

	/**
	 * The field to sort on.
	 * @return
	 */
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		if(sortOrder!=null && sortOrder.length()==0) this.sortOrder = null;
		else this.sortOrder = sortOrder;
	}
	/**
	 * Return the collection of filters. This method always returns a collection, even if it has to first create one.
	 * @return Return a reference to the actual collection of filters, not a copy
	 */
	public Map<String, Object> getFilters() {
		if (filters==null) {
			filters = new HashMap<String, Object>(10);
		}
		return filters;
	}
	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}
	/**
	 * Add an entry to the collection of filters. You cannot set the same key twice.
	 */
	public void addFilter( String key, Object value ) {
		getFilters().put(key, value);
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
}
