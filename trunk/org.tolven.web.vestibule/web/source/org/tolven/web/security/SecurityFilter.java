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
package org.tolven.web.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/**
 * An abstract class to handle common security filtering operations
 * 
 * @author Joseph Isaac
 */
public abstract class SecurityFilter implements Filter {

    protected static String TOP = "top";
    protected static String TOLVEN_NOW = "tolvenNow";
    
    private Logger logger = Logger.getLogger(SecurityFilter.class);
    
    public void logout(String reason, HttpServletRequest request, HttpServletResponse response) {
    	logout( "/loggedOut.jsf", reason, request, response );
    }

    public void logout(String page, String reason, HttpServletRequest request, HttpServletResponse response) {
        logger.debug(":INVALIDATE SESSION for REASON: " + reason + " : LOGGING OUT TO PAGE: " + page);
        try {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + page);
        } catch (IOException ex) {
            throw new RuntimeException("Could not redirect to: " + page, ex);
        }
    }

}
