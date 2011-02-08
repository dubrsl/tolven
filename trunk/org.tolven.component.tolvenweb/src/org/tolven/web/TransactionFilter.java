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
import java.io.Writer;
import java.util.Date;

import javax.naming.InitialContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.tolven.logging.TolvenLogger;

/**
 * <p>Wrap all web interactions involving jsp (xhtml) pages in a user transaction. Thus, page composition is free to 
 * interact with local session beans without worrying about transaction demarcation. </p>
 * <p>Another very important function provided by this filter is to establish the definitive point in time known as "now".
 * This time, in the form of a Date object, should be used within the transaction when "now" is needed. Do not just call new Date() again
 * because that can cause queries to return strange results and mismatched timestamps (off by milliseconds yet suspicious looking nontheless.) 
 * Otherwise, there is nothing special about this timestamp. For example, it is not synchronized with the database or NIST (unless the system clock is).
 * This makes "now" consistent within a transaction but not necessarily accurate. Accuracy is almost impossible to establish - reduction of error is
 * the best one could hope for. Tolven limits its dependency on date accuracy to those places where no other alternative exists.
 * For example, Tolven never uses a date like "now" to establish uniqueness, record a transaction processing sequence, or to coordinate distributed transaction processing. 
 * Instead, a transactionally incremented integer meets the need more accurately.</p>
 *  
 * @author John Churin
 *
 */
public class TransactionFilter implements Filter {

    private FilterConfig config = null;

    private Logger logger = Logger.getLogger(TransactionFilter.class);

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }

    public void destroy() {
    }

		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
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
	         if(ut.getStatus()==Status.STATUS_NO_TRANSACTION) {
	            ut.begin();
	    		request.setAttribute( "tolvenNow", now );
	            break;
	         } else if (ut.getStatus()==Status.STATUS_ACTIVE) {
		        TolvenLogger.info( "***** Transaction already active *******", TransactionFilter.class);
		        break;
	         } else {
		        TolvenLogger.info( "***** Transaction not in a good state [" + ut.getStatus() + "] trying again *******", TransactionFilter.class);
	        	 ut.setRollbackOnly();
	        	 ut.rollback();
	         }
    	  }
	      } catch (Exception e) {
	         config.getServletContext().log("Error setting up UserTransaction or starting a transaction", e);
	      }

	      try {
            if (logger.isDebugEnabled()) {
                logger.debug("TOLVEN_PERF: " + (System.currentTimeMillis() - start));
                start = System.currentTimeMillis();
            }
            chain.doFilter(request, response);
            if (logger.isDebugEnabled()) {
                logger.debug("TOLVEN_PERF: downstream: " + (System.currentTimeMillis() - start));
            }
	         if(ut != null) {
                if (ut.getStatus() == Status.STATUS_ACTIVE) {
                    if (logger.isDebugEnabled()) {
                        start = System.currentTimeMillis();
                    }
                    ut.commit();
                    if (logger.isDebugEnabled()) {
                        logger.debug("TOLVEN_PERF: commit: " + (System.currentTimeMillis() - start));
                    }
                }
	        	 else ut.rollback();
	        	 // We need to put the web page commit *after* the database commit
	        	 // otherwise it is possible that the web page can query for uncommitted work in the next interaction.
	        	 // The situation actually occurs in underpowered configurations.
	        	 Writer writer = (Writer) request.getAttribute("activeWriter");
	        	 if (writer!=null) writer.close();
		         ut = null;
	          }
	      } catch (Exception e) {
			      try {
			    	  if(ut != null   && (ut.getStatus()==Status.STATUS_ACTIVE)) {
			           ut.rollback();
			           ut = null;
				      }
				      } catch (Exception e2 ) {
				    	  TolvenLogger.info( "*************** !!!!!TRANSACTION ROLLBACK EXCEPTION!!!! *******************", TransactionFilter.class );
				    	  throw new ServletException( e2 );
	              }
    			  throw new ServletException( "Exception caught in Transaction", e );
	      } finally {
		      try {
		    	  if(ut != null  ) {// && (ut.getStatus()==Status.STATUS_ACTIVE)
		           ut.rollback();
		           ut = null;
	              }
	           HttpSession session = ((HttpServletRequest) request).getSession(false);
	           if (session!=null) {
	        	   Double elapsed = (Double) session.getAttribute("elapsedMilli");
	        	   if (elapsed==null) elapsed = 0.0;
		           elapsed += (System.nanoTime() - beginNanoTime)/1000000;
		           session.setAttribute("elapsedMilli", elapsed);
	           }
		      } catch (Exception e ) {
		    	  TolvenLogger.info( "*************** !!!!!TRANSACTION ROLLBACK EXCEPTION!!!! *******************", TransactionFilter.class );
		    	  throw new ServletException( e );
		      }
	      }
	   }


}
