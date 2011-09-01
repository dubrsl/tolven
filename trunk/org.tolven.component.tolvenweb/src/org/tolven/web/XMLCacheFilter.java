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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class XMLCacheFilter implements Filter {

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
//			HttpServletRequest hRequest = (HttpServletRequest)request;
//			TolvenLogger.info( "Req: " + hRequest.getServletPath(), XMLCacheFilter.class);
			HttpServletResponse hResponse = (HttpServletResponse)response;
			hResponse.setHeader("Cache-Control", "no-cache");
			
		chain.doFilter(request,response);
		hResponse.setContentType("text/xml; charset=UTF-8");
//		TolvenLogger.info( " After Content Type: " + hResponse.getContentType(), XMLCacheFilter.class);
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

}
