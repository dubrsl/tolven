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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ImageCacheFilter implements Filter {
	GregorianCalendar lastModified = new GregorianCalendar();

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// Note: Locale must be en_US - this date is not visiable to end-users.
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US );
		
//		HttpServletRequest hRequest = (HttpServletRequest)request;
		HttpServletResponse hResponse = (HttpServletResponse)response;
		GregorianCalendar expires = new GregorianCalendar();
		expires.add(GregorianCalendar.HOUR, 24);
		
		hResponse.setHeader("Pragma", "private");
		hResponse.setHeader("Expires", sdf.format(expires.getTime()));
		hResponse.setHeader("Cache-Control", "private");
		hResponse.setHeader("Last-Modified", sdf.format(lastModified.getTime()));
//		hResponse.setHeader("Expires", "Thu, 24 Jan 2008 18:00:00 GMT");
//		hResponse.setHeader("Cache-Control", "max-age=86400,private");
//		hResponse.setHeader("Last-Modified", "Sat, 21 Apr 2007 01:09:11 GMT");
		chain.doFilter(request,response);
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

}
