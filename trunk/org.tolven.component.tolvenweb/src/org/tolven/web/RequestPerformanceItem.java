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
package org.tolven.web;

import java.util.Date;
/**
 * Simple bean that holds one response timing entry. 
 * @author John Churin
 *
 */
public class RequestPerformanceItem {
	Date timestamp;
	double msElapsed;
	String uri;
	String query;
	public RequestPerformanceItem(Date timestamp, double msElapsed, String uri, String query ) {
		this.timestamp = timestamp;
		this.msElapsed = msElapsed;
		this.uri = uri;
		this.query = query;
	}
	public double getMsElapsed() {
		return msElapsed;
	}
	public void setMsElapsed(double msElapsed) {
		this.msElapsed = msElapsed;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
}
