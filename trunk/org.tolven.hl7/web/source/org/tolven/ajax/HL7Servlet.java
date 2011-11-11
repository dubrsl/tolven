package org.tolven.ajax;

import java.io.IOException;
import java.io.Writer;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.tolven.hl7.HL7Local;

public class HL7Servlet extends HttpServlet{
	
	@EJB
	private HL7Local hl7Local;
	  
    public void init(ServletConfig config) throws ServletException {
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI();
    	Writer writer = resp.getWriter();
    	StringBuffer response = new StringBuffer("");
    	if (uri.endsWith("getHL7.ajaxhl7")) {
			String path = req.getParameter("path");
			response.append(hl7Local.findHL7Message(path));
			writer.write(StringEscapeUtils.escapeXml(response.toString()));
			req.setAttribute("activeWriter", writer);
    	}
   	}
}