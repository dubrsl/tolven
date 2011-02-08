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
import javax.servlet.http.HttpServletRequest;

public class UploadMultipartFilter implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
	throws IOException, ServletException {

		HttpServletRequest hRequest = (HttpServletRequest)request;

		//Check whether we're dealing with a multipart request
		boolean isMultipart = (hRequest.getHeader("content-type") != null && 
				hRequest.getHeader("content-type").indexOf("multipart/form-data") != -1); 

		if(isMultipart){
			//We're dealing with a multipart request - we have to wrap the request.
			UploadMultipartRequestWrapper wrapper = new UploadMultipartRequestWrapper(hRequest);
			chain.doFilter(wrapper,response);
		} else {
			chain.doFilter(request,response);
		}
}

public void destroy() {}
public void init(FilterConfig config) throws ServletException {}

}
