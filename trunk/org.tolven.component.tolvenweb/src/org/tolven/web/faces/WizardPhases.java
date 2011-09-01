package org.tolven.web.faces;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WizardPhases implements PhaseListener {

	public void afterPhase(PhaseEvent event) {
//		TolvenLogger.info( "After Phase ", WizardPhases.class );
	}
	/**
	 * Escape the supplied string so it works in javascript (JSON).
	 * @param clientId - Remove client id from the string
	 * @param str
	 * @return
	 */
	public static String jsquote( String clientId, String str ) {
		String str1;
		if (clientId!=null) {
			str1 = str.replace(clientId + ": ", "");
		} else {
			str1 = str;
		}
		StringBuffer sb = new StringBuffer( str1.length());
		for (char c : str1.toCharArray()) {
			if (c=='\'' || c=='\"' ||  c=='\\') sb.append('\\');
			sb.append(c);
		}
		return sb.toString();
	}

	public void beforePhase(PhaseEvent event) {
		FacesContext ctx = event.getFacesContext();
		HttpServletRequest req = (HttpServletRequest) ctx.getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
//		TolvenLogger.info( "RenderResponse to: " + req.getRequestURI() + req.getParameter("submit3"), WizardPhases.class );
		String submit3 = req.getParameter("submit3");
		if (submit3!=null) {
			try {
			    Writer writer=response.getWriter();
			    // Note JSON format
				writer.write("{ element : '" + req.getParameter("element") + "',\n");
				writer.write(" root : '" + submit3 + "',\n");
				writer.write(" globalErrors : [ \n");
				boolean firstGlobalMsg = true;
				Iterator<FacesMessage> messageiter = event.getFacesContext().getMessages(null);
				while( messageiter.hasNext() ) {
					if (firstGlobalMsg) firstGlobalMsg = false;
					else writer.write(",");
					FacesMessage msg = messageiter.next();
					writer.write( "{ summary : '" + jsquote(null, msg.getSummary()) + 
						"', detail : '" + jsquote(null, msg.getDetail()) + 
						"'}\n" );
				}
				writer.write(" ],\n");
				writer.write(" errors : [ \n");
				boolean firstTime = true;
				Iterator<String> clientIditer = event.getFacesContext().getClientIdsWithMessages();
				while( clientIditer.hasNext() ) {
					String clientId = clientIditer.next();
					Iterator<FacesMessage> msgiter = event.getFacesContext().getMessages(clientId);
					if (clientId!=null) {
						while( msgiter.hasNext() ) {
							FacesMessage msg = msgiter.next();
							if (firstTime) firstTime = false;
							else writer.write(",\n");
							writer.write( "{ clientId : '" + clientId + 
									"', summary : '" + jsquote(clientId, msg.getSummary()) + 
									"', detail : '" + jsquote(clientId, msg.getDetail()) + 
									"'}" );
						}
					}
				}
				writer.write(" ]\n");
				writer.write("}\n");
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			event.getFacesContext().responseComplete();
		}
	}
	/**
	 * We're interested in renderResponse phase
	 */
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

}
