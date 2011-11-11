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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * This class initializes the Tolven Gatekeeper.
 * 
 * @author Joseph Isaac
 *
 */
public class GatekeeperServletContextListener implements ServletContextListener {

    private Logger logger = Logger.getLogger(GatekeeperServletContextListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("#0#: WEB tier listening: " + servletContextEvent.getServletContext().getContextPath());
    }

}
