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
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.MDQueryResults;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.entity.DocImage;
import org.tolven.doc.entity.DocXML;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.gen.PersonGenerator;
import org.tolven.gen.entity.FamilyUnit;
import org.tolven.gen.entity.VirtualPerson;
import org.tolven.web.security.GeneralSecurityFilter;
import org.tolven.web.util.FormattedOutputWriter;
/**
 * A Servlet that responds to Live Grid data requests. See http://openrico.org/docs/RicoLiveGrid.pdf for details of
 * how LiveGrid works. This must be very fast: We're processing database queries as the user scrolls down a table.
 * Since these are all read-only requests, we don't establish a UserTransaction as we do with other web interactions.
 * @author John Churin
 *
 */
public class AjaxServlet extends HttpServlet {

    @EJB 
    private DocumentLocal documentLocal;

    @EJB 
    private PersonGenerator personDAO;

    @EJB 
    private MenuLocal menuLocal;

    @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    resp.setContentType("text/xml");
	    Writer writer=resp.getWriter();
	    writer.write( "<html><body><hr/></body></html>");
	    writer.close();
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public class HTMLOutputWriter extends FormattedOutputWriter {
		public HTMLOutputWriter(Writer writer, String type, AccountUser accountUser, Date now) {
			super(writer, type, accountUser, now);
		}

	/**
	 * Write a character adding escape characters for things like < >
	 * @param writer
	 * @param val
	 * @throws IOException 
	 */
	@Override
	public void writeEscape( char c ) throws IOException {
	    	switch ( c ) {
	    	case '<' : writer.write( "&lt;");break;
	    	case '>' : writer.write( "&gt;");break;
	    	case '&' : writer.write( "&amp;");break;
	    	case '"' : writer.write( "&quot;");break;
	    	default : writer.write(c);
		}
	}


	}
	
	private static void writeFormattedColumn(Map<String, Object> rowData, MSColumn msColumn, FormattedOutputWriter fow, MenuLocal menuLocal, HttpServletRequest req, String outputExpression ) {
		try {
			fow.writeExpression(outputExpression);
		} catch (Exception e) {
			throw new RuntimeException( "Error formatting column " + msColumn.getHeading(),e);
		}
	}

	private static void writeColumn(Map<String, Object> rowData, MSColumn msColumn, FormattedOutputWriter fow, MenuLocal menuLocal, HttpServletRequest req ) {
	    try {
			String lMethodArgs = req.getParameter("methodArgs");
			String lMethodName = req.getParameter("methodName");
			// Get the column value to display
			Object value = rowData.get(msColumn.getHeading());
			if (value==null) {
				fow.writeVerbatim(" "); // NB Space
			} else {
				String stringValue = value.toString().trim();
				if (stringValue.length()==0) {
					fow.writeVerbatim(" ");	// NB Space
				}else if (lMethodName.equals("patientPopupRefReq")) {	//For patient popup in refillReq wizard. Modified by uks.
					fow.writeVerbatim( "<a href=\"javascript:patientPopupRefReq('" + rowData.get("referencePath") +
							"','"+req.getParameter("element")+"')\">" );
					fow.writeEscape( stringValue );
					fow.writeVerbatim("</a>");
					if(null != rowData.get("sourceAccountId")) {
						AccountUser accountUser = (AccountUser) req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
						Long accountId = accountUser.getAccount().getId();
						if(Long.parseLong(rowData.get("sourceAccountId").toString()) != (accountId)) {
							fow.writeVerbatim(" <img src='../images/vcard.gif' class='shareInfo' title='Data shared from account:"+rowData.get("sourceAccountId")+"'/>");						
						}
					}
				}else if (msColumn.isReference()) {
					fow.writeVerbatim( "<a href=\"javascript:showPane('" + rowData.get("referencePath") +
							"',false,'"+req.getParameter("element")+"')\">" );
					fow.writeEscape( stringValue );
					fow.writeVerbatim("</a>");
					if(null != rowData.get("sourceAccountId")) {
						AccountUser accountUser = (AccountUser) req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
						Long accountId = accountUser.getAccount().getId();
						if(Long.parseLong(rowData.get("sourceAccountId").toString()) != (accountId)) {
							fow.writeVerbatim(" <img src='../images/vcard.gif' class='shareInfo' title='Data shared from account:"+rowData.get("sourceAccountId")+"'/>");						
						}
					}
				} else if (msColumn.isInstantiate() && !lMethodArgs.contains("enableCopy")) {
					String instantiateValue="";
					
					String path=(String)rowData.get( "path" );
					String element = req.getParameter("element");
					
					if (lMethodName != null && lMethodName.length() > 0)
					{
						long id;
						id = (Long)rowData.get( "id" );
						MenuData md1 = menuLocal.findMenuDataItem(id);
						if (lMethodArgs.contains("withCode")) 
							instantiateValue =  "<span style='float:left;width:100px;'>"+md1.getString02().split("-")[1]+"</span><span style='float:left;'>";
						instantiateValue +=  "<a href=\"#\" onclick=\"" + lMethodName + "('" + md1.getString02() + "','"+ element+ "','" + lMethodArgs + "')\">";
					}else{
						instantiateValue =  "<a href=\"#\" onclick=\"instantiate('" + path + "','"+ element+ "')\">";	
					}		
					fow.writeVerbatim(instantiateValue);
					fow.writeEscape( stringValue ); 
					fow.writeVerbatim("</a>");
					if (lMethodArgs.contains("withCode")) 
						fow.writeVerbatim("</span>");
				} else if(lMethodArgs != null && lMethodArgs.contains("enableCopy")){
					/* this to populate a grid which will be used to create favorites lists
					 basically on clicking an item in the grid a custom script function will be called */
					
					//chop off the argument 'enableCopy' and replace , with ','
					lMethodArgs = lMethodArgs.replaceAll("enableCopy,","").replaceAll(",","','");
					
					String path=(String)rowData.get( "path" );
					String instantiateValue =  "<a id='"+path+"' href=\"javascript:" + lMethodName + "('" + path + "','"+lMethodArgs+"')\">";
					fow.writeVerbatim(instantiateValue);
					fow.writeEscape( stringValue ); 
					fow.writeVerbatim("</a>");
				} else  if(lMethodName != null && lMethodName.length() > 0){ // may be this should be combined with the above if
					String instantiateValue="";
					String element = req.getParameter("element");
					long id = (Long)rowData.get( "id" );
					MenuData md1 = menuLocal.findMenuDataItem(id);
					instantiateValue =  "<a href=\"#\" onclick=\"" + lMethodName + "('" + md1.getString02() + "','"+ element+ "','" + lMethodArgs + "')\">";
					fow.writeVerbatim(instantiateValue);
					fow.writeEscape( stringValue ); 
					fow.writeVerbatim("</a>");
				}else{
					fow.writeEscape( stringValue );
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error writing column " + msColumn, e);
		}
	}
	
	public static void writeMenuDataRow( FormattedOutputWriter fow, MDQueryResults mdQueryResults,
			Map<String, Object> rowData, MenuLocal menuLocal, HttpServletRequest req ) throws IOException {
		List<MSColumn> msColumns = mdQueryResults.getSortedColumns();
		fow.writeVerbatim( "<tr "+(req.getParameter("methodArgs").contains("addId")?"id='"+(String)rowData.get( "path" )+"'":"")+">");
		ExpressionEvaluator ee = fow.getExpressionEvaluator();
		ee.pushContext();
		ee.addVariables(rowData);
		for (MSColumn msColumn : msColumns) {
			if (msColumn.isVisible()) {
				fow.writeVerbatim( "<td>");
				String outputExpression = null;
				if (fow.getType()!=null) {
					outputExpression = msColumn.getOutputFormatMap().get(fow.getType());
				}
				if (outputExpression!=null) {
					writeFormattedColumn(rowData, msColumn, fow, menuLocal, req, outputExpression);
				} else {
					writeColumn( rowData, msColumn, fow, menuLocal, req);
				}
				fow.writeVerbatim("</td>");
			}
		}
		fow.writeVerbatim( "</tr>\n");
		ee.popContext();
	}
	
	/**
	 * Respond to AJAX (Rico) LiveGrid requests from the browser.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		resp.setContentType("text/xml");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Cache-Control", "no-cache");
		Writer writer=resp.getWriter();
		writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
		writer.write( "<ajax-response>\n" );
		int offset = Integer.parseInt( req.getParameter( "offset"));
		int pageSize = Integer.parseInt( req.getParameter( "page_size"));
		String sortCol = req.getParameter( "sort_col");
		String sortDir = req.getParameter( "sort_dir");
		// Figure out timeZone
		AccountUser accountUser = (AccountUser) req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
		// Get now
		Date now = (Date) req.getAttribute( "tolvenNow" );
		String tableId = req.getParameter( "id");
		writer.write( "<response type=\"object\" id=\"" + tableId + "_updater\">\n" );
		writer.write( "<rows update_ui='true'>\n" );
		
		String element = req.getParameter( "element");
			try {
			// MenuData
			if (uri.endsWith("menuData.ajax")) {
				
				MenuPath path = new MenuPath( element );
				long accountId = accountUser.getAccount().getId();
				MenuStructure ms = menuLocal.findMenuStructure( accountId, path.getPath() );
				boolean addPath = false;
				MenuQueryControl ctrl = new MenuQueryControl();
				ctrl.setAccountUser(accountUser);
				ctrl.setLimit( pageSize );
				ctrl.setMenuStructure( ms );
				ctrl.setNow( new Date() );
				ctrl.setOffset( offset );
				ctrl.setOriginalTargetPath( path );
				ctrl.setRequestedPath( path );
				ctrl.setSortDirection( sortDir);
				ctrl.setSortOrder( sortCol );

				// Any attribute name that ends with "Filter" (exactly) is taken as a filter parameter (but we don't store the 'filter suffix')
				Enumeration<String> params = req.getParameterNames();
				
				while (params.hasMoreElements() ) {
					String param = params.nextElement();
					// Simple catch-all filter is just "filter"
					// named filter is name+Filter
					if (param.equals("filter")) {
						ctrl.setFilter(req.getParameter(param));
					} else if (param.endsWith("Filter")) {
						ctrl.getFilters().put(param.substring(0, param.length()-6), req.getParameter(param));
					}else if(param.equals("methodArgs")){
						
					}
				}
				String gridType = req.getParameter( "gridType");
				if (gridType==null || gridType.length()==0) {
					gridType = "_default";
				}
				FormattedOutputWriter fow = new HTMLOutputWriter(writer, gridType, accountUser, now);
				ExpressionEvaluator ee = fow.getExpressionEvaluator();
				ee.pushContext();
				// Make all request parameters available to the expression evaluator as variables.
				Enumeration names = req.getParameterNames();
				while (names.hasMoreElements()) {
					String key = (String) names.nextElement();
					ee.addVariable(key, req.getParameter(key));
				}
				 // Do the query
				MDQueryResults mdQueryResults = menuLocal.findMenuDataByColumns( ctrl );
			    int line = 0;
			    // Prepare the list of columns we're going to populate
				for( Map<String, Object>  rowData : mdQueryResults.getResults()) {
			    	line++;
			    	writeMenuDataRow( fow, mdQueryResults, rowData, menuLocal, req);
				}
				ee.popContext();
			}

			// Demo only and probably obsolete
			else if (uri.endsWith("activeInvitations.ajax")) {
				String sortAttribute = "id";
				if ("name".equals(sortCol)) {
					sortAttribute="familyName";
				}
				String nameFilter = req.getParameter( "nameFilter");
				List<FamilyUnit> rows = personDAO.findFamilies(pageSize, offset, sortAttribute, sortDir, nameFilter );
			    int line = 0;
			    for (FamilyUnit p : rows) {
			    	line++;
				    writer.write( "<tr>" );
					writer.write( "<td> " + p.getId() + "</td>");
					writer.write( "<td> <a href=\"javascript:showPane(containerId(this)+':detail-" + p.getId()+ "',false,'"+element+"')\">"+ p.getFamilyName() + "</a></td>");
					writer.write( "</tr>\n");
			    }
			}

			// Demo only and probably obsolete
			else if (uri.endsWith("familyList.ajax")) {
				String sortAttribute = "id";
				if ("name".equals(sortCol)) {
					sortAttribute="familyName";
				}
				String nameFilter = req.getParameter( "nameFilter");
				List<FamilyUnit> rows = personDAO.findFamilies(pageSize, offset, sortAttribute, sortDir, nameFilter );
			    int line = 0;
			    for (FamilyUnit p : rows) {
			    	line++;
				    writer.write( "<tr>" );
					writer.write( "<td> " + p.getId() + "</td>");
					writer.write( "<td> <a href=\"javascript:showPane(containerId(this)+':detail-" + p.getId()+ "',false,'"+element+"')\">"+ p.getFamilyName() + "</a></td>");
					writer.write( "</tr>\n");
			    }
			}
			// Demo only and probably obsolete
			else if (uri.endsWith("personList.ajax")) {
				String sortAttribute = "id";
				if ("first".equals(sortCol)) {
					sortAttribute="first";
				}
				if ("last".equals(sortCol)) {
					sortAttribute="last";
				}
				if ("dob".equals(sortCol) || "age".equals(sortCol)) {
					sortAttribute="dob";
				}
				if ("sex".equals(sortCol)) {
					sortAttribute="gender";
				}
				String nameFilter = req.getParameter( "nameFilter");
				
				List<VirtualPerson> rows = personDAO.findPersons(pageSize, offset, sortAttribute, sortDir, nameFilter );
			    int line = 0;
//		    Date now = new Date();
			    for (VirtualPerson p : rows) {
			    	line++;
				    writer.write( "<tr>" );
					writer.write( "<td> " + p.getId() + "</td>");
					writer.write( "<td> " + p.getLast() + "</td>");
					writer.write( "<td> " + p.getFirst() + " "+ p.getMiddle() + "</td>");
					writer.write( "<td> " + p.getFormattedDob() + "</td>");
					writer.write( "<td> " + p.getAge( now ) + "</td>");
					writer.write( "<td> " + p.getGender() + "</td>");
					writer.write( "</tr>\n");
			    }
			}
			// Demo only
			else if (uri.endsWith("xmlDocList.ajax")) {
			    String sortAttribute = "id";
			    Object obj = req.getSession().getAttribute(GeneralSecurityFilter.TOLVENUSER_ID);
			    if (obj == null)
			        throw new IllegalStateException(getClass() + ": Session TOLVENUSER_ID is null");
			    long userId = ((Long)obj).longValue();
			    List<DocXML> rows = documentLocal.findAllXMLDocuments(userId, pageSize, offset, sortAttribute, sortDir );
			    int line = 0;
			    for (DocXML d : rows) {
			        line++;
			        String uid = "-";
			        if (d.getAuthor()!=null) {
			        	uid = d.getAuthor().getLdapUID();
			        }
			        writer.write( "<tr>" );
			        writer.write( "<td> <a href=\"javascript:showPane(containerId(this)+':detail-" + d.getId()+ "',false,'"+element+"')\">"+ d.getId() + "</a> </td>");
//              writer.write( "<td> " + d.getId() + "</td>");
			        writer.write( "<td> " + d.getBindingContext() + "</td>");
			        writer.write( "<td> " + uid + "</td>");
			        writer.write( "<td> " + d.getStatus() + "</td>");
			        writer.write( "<td> " + d.getAccount().getId() + " " + d.getAccount().getTitle() + "</td>");
			        
			        writer.write( "</tr>\n");
			    }
			} 
			else if (uri.endsWith("photoList.ajax")) {
				long accountId = (Long) req.getSession(false).getAttribute("accountId");
				String sortAttribute = "id";
//			if ("description".equals(sortCol)) {
//				sortAttribute="preferredTerm";
//			}
				List<DocImage> rows = documentLocal.findImages(accountId, pageSize, offset, sortAttribute, sortDir );
			    int line = 0;
			    for (DocImage d : rows) {
			    	line++;
				    writer.write( "<tr>" );
					writer.write( "<td class=\"col1\"> <a href=\"javascript:showPane( 'echr:test:photos:detail-" + d.getId()+ "',false,'"+element+"')\">"+ d.getId() + "</a></td>");
					writer.write( "<td class=\"col2\"> " + d.getMediaType()+ "</td>");
					writer.write( "</tr>\n");
			    }
			}
			
			writer.write( "</rows>\n");
			writer.write( "</response>\n");
			writer.write( "</ajax-response>\n");
			req.setAttribute("activeWriter", writer);
		} catch (Exception e) {
			throw new RuntimeException("Error in LiveGrid request for element " + element, e);
		}
//		writer.close();
	}
}
