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
 * @version $Id: TolvenAgentFilter.java 1530 2011-06-30 09:43:03Z joe.isaac $
 */
package org.tolven.openamclient;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import javax.naming.InitialContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import com.sun.identity.agents.filter.AmAgentFilter;

public class TolvenAgentFilter extends AmAgentFilter {

    private Logger logger = Logger.getLogger(TolvenAgentFilter.class);

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, final FilterChain chain) throws IOException, ServletException {
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        double beginNanoTime = System.nanoTime();

        InitialContext ctx;
        UserTransaction ut = null;
        // Establish "now" for anything that happens during this interaction.
        Date now = new Date();

        try {
            ctx = new InitialContext();
            while (true) {
                ut = (UserTransaction) ctx.lookup("UserTransaction");
                if (ut.getStatus() == Status.STATUS_NO_TRANSACTION) {
                    ut.begin();
                    servletRequest.setAttribute("tolvenNow", now);
                    break;
                } else if (ut.getStatus() == Status.STATUS_ACTIVE) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("***** Transaction already active *******");
                    }
                    break;
                } else {
                    logger.error("***** Transaction not in a good state [" + ut.getStatus() + "] trying again *******");
                    ut.setRollbackOnly();
                    ut.rollback();
                }
            }
        } catch (Exception e) {
            getFilterConfig().getServletContext().log("Error setting up UserTransaction or starting a transaction", e);
        }

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("TOLVEN_PERF: " + (System.currentTimeMillis() - start));
                start = System.currentTimeMillis();
            }
            /*
             * Now let OpenAM do the downstream work
             */
            super.doFilter(servletRequest, servletResponse, chain);
            if (true) {
                if (logger.isDebugEnabled()) {
                    logger.debug("TOLVEN_PERF: downstream: " + (System.currentTimeMillis() - start));
                }
            }
            if (ut != null) {
                if (ut.getStatus() == Status.STATUS_ACTIVE) {
                    if (logger.isDebugEnabled()) {
                        start = System.currentTimeMillis();
                    }
                    ut.commit();
                    if (logger.isDebugEnabled()) {
                        logger.debug("TOLVEN_PERF: commit: " + (System.currentTimeMillis() - start));
                    }
                } else
                    ut.rollback();
                // We need to put the web page commit *after* the database commit
                // otherwise it is possible that the web page can query for uncommitted work in the next interaction.
                // The situation actually occurs in underpowered configurations.
                Writer writer = (Writer) servletRequest.getAttribute("activeWriter");
                if (writer != null)
                    writer.close();
                ut = null;
            }
        } catch (Exception e) {
            try {
                if (ut != null && (ut.getStatus() == Status.STATUS_ACTIVE)) {
                    ut.rollback();
                    ut = null;
                }
            } catch (Exception e2) {
                logger.error("*************** !!!!!TRANSACTION ROLLBACK EXCEPTION!!!! *******************");
                throw new ServletException(e2);
            }
            throw new ServletException("Exception caught in Transaction", e);
        } finally {
            try {
                if (ut != null) {// && (ut.getStatus()==Status.STATUS_ACTIVE)
                    ut.rollback();
                    ut = null;
                }
                HttpSession session = ((HttpServletRequest) servletRequest).getSession(false);
                if (session != null) {
                    Double elapsed = (Double) session.getAttribute("elapsedMilli");
                    if (elapsed == null)
                        elapsed = 0.0;
                    elapsed += (System.nanoTime() - beginNanoTime) / 1000000;
                    session.setAttribute("elapsedMilli", elapsed);
                }
            } catch (Exception e) {
                logger.error("*************** !!!!!TRANSACTION ROLLBACK EXCEPTION!!!! *******************");
                throw new ServletException(e);
            }
        }
    }

}
