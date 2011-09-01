/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author Joseph Isaac
 */
package org.tolven.shiro.web.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.tolven.session.ShiroSessionWrapper;
import org.tolven.session.TolvenSessionWrapperThreadLocal;

/**
 * A Filter which places a TolvenSessionWrapper into TolvenSessionWrapperThreadLocal
 * 
 * @author Joseph Isaac
 *
 */
public class ShiroSessionWrapperFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        /*
         * TolvenSessionWrapperThreadLocal must be set in a filter which appears after the main TolvenShiroFilter since only after that
         * filter, is the session guaranteed to have been created and ready for use in this thread local
         */
        try {
            TolvenSessionWrapperThreadLocal.set(new ShiroSessionWrapper());
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            TolvenSessionWrapperThreadLocal.remove();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

}
