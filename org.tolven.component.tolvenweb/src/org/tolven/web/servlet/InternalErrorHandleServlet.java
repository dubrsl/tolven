/*
 *  Copyright (C) 2009 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this 
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA 02111-1307 USA 
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.web.servlet;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.tolven.core.entity.AccountUser;
import org.tolven.web.security.GeneralSecurityFilter;


/** This servlet will send an email when there is an error in the application(server side) 
 * if the email is configured in account preferences and then shows an error page.
 *   Note: This filter should be outside of the transaction filter scope - It uses an independent transaction context.
 * @author Srini Kandula
 */

public class InternalErrorHandleServlet extends HttpServlet {
	private ServletConfig config;
	private InitialContext ctx ; 
    
	@Override 
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//check if a transaction is already started, if not redirect to the servlet
		UserTransaction ut = null;
		try {
			ut = (UserTransaction) ctx.lookup("UserTransaction");
			if(ut.getStatus()!=Status.STATUS_ACTIVE) {
				ut.begin();				
	        }
			//check if the error.email is configured. If found send an email
			Object accountUser = req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
			if(accountUser != null){		 
				String errorEmail = ((AccountUser)accountUser).getAccount().getProperty().get("org.tolven.error.email.to");
				if(!StringUtils.isBlank(errorEmail)){
					System.out.println("Send an email to "+errorEmail);
				}
			}
			if(ut != null){
				ut.commit();
			}				
			RequestDispatcher dispatcher = this.config.getServletContext().getRequestDispatcher("/error.jsp");
		    if (dispatcher != null) 
		    	dispatcher.forward(req, resp);		
		} catch (Exception e) {
			throw new ServletException( "Exception thrown in InternalErrorHandleServlet", e);
		}finally{
			if(ut != null){
				try {
					ut.rollback();
				} catch (Exception e) {
					throw new ServletException( "Exception thrown in InternalErrorHandleServlet", e);
				}
			}
		}
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.config = config;
	}
}
