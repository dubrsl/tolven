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
package org.tolven.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.context.ResponseWriter;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <b>No longer used.</b> The body of email invitations are processed as normal "front-door" web pages that we just
 * capture and stick in the message body instead of returning to a browser.  
 * @see InvitationAction
 * @author John Churin
 *
 */
public class AsyncServlet extends HttpServlet {
	private String testResult;
	
    class MyResponseWrapper extends ServletResponseWrapper{
    	StringWriter stringWriter = new StringWriter( 2000 );
    	PrintWriter printWriter = new PrintWriter( stringWriter, true);
    	MyResponseWrapper( ServletResponse response ) {
    		super( response );
    	}

    	
    	@Override
		public PrintWriter getWriter() throws IOException {
			// TODO Auto-generated method stub
			return printWriter;
		}

		public String getResult() {
    		return stringWriter.getBuffer().toString();
    	}
    }

    public void facesTest(ServletContext ctx, ServletRequest req, ServletResponse resp) throws IOException {
		LifecycleFactory lcFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle lifecycle = lcFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		FacesContextFactory fcFactory = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);

		MyResponseWrapper wrappedResponse = new MyResponseWrapper( resp );

		RenderKitFactory renderKitFactory = (RenderKitFactory)FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
		RenderKit renderKit = renderKitFactory.getRenderKit( null, RenderKitFactory.HTML_BASIC_RENDER_KIT );

//		ResponseWriter responseWriter = renderKit.createResponseWriter(stringWriter, "text/html", "UTF-8");
		FacesContext facesContext = fcFactory.getFacesContext(ctx, req, wrappedResponse, lifecycle);
		
//	    MailResponseWriter newWriter = new MailResponseWriter( new StringWriter( 2000 ) );

//		facesContext.setResponseWriter(responseWriter );
		
		Application application = facesContext.getApplication();
		ViewHandler viewHandler = application.getViewHandler();
		UIViewRoot view = viewHandler.createView( facesContext, "/invitation/activate.xhtml");
		facesContext.setViewRoot( view );
		lifecycle.render( facesContext );
		facesContext.release();
		// Look at what we got
		testResult = wrappedResponse.getResult();
    }
	public AsyncServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		facesTest(this.getServletContext(), request, response);
	}

	@Override
	public void init(ServletConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		super.init(arg0);
	}
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}
}
