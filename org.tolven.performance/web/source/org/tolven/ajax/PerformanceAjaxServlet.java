package org.tolven.ajax;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tolven.core.PerformanceDataDAO;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.PerformanceData;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.web.util.FormattedOutputWriter;

/**
 * This servlet added for processing performance log.
 * 
 * @author Suja Sundaresan
 * added on 01/12/2011
 */
public class PerformanceAjaxServlet extends HttpServlet {

	@EJB private PerformanceDataDAO performanceBean;
	
	/**
	 * Init Method
	 */
	public void init(ServletConfig config) throws ServletException {
	}
	
	/**
	 * doGet Method
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String uri = req.getRequestURI();
			AccountUser activeAccountUser = (AccountUser) req
					.getAttribute("accountUser");
			resp.setContentType("text/xml");
			resp.setCharacterEncoding("UTF-8");
			resp.setHeader("Cache-Control", "no-cache");
			Writer writer = resp.getWriter();
			Date now = (Date) req.getAttribute("tolvenNow");
			/**
			 * Used to get count of performance log
			 * 
			 * @author Suja Sundaresan
			 * added on 01/12/2011
			 */
			if (uri.endsWith("countPerformanceData.ajaxper")) {
		    	FormattedOutputWriter fow = new HTMLOutputWriter(writer, "_default", activeAccountUser, now);
		    	ExpressionEvaluator ee = fow.getExpressionEvaluator();
				ee.pushContext();
				Enumeration names = req.getParameterNames();
				while (names.hasMoreElements()) {
					String key = (String) names.nextElement();
					ee.addVariable(key, req.getParameter(key));
				}
				
				// setting parameters to HashMap
				Map<String, Object> params = new HashMap<String, Object>();
				if (activeAccountUser.isAccountPermission())
					params.put("accountUserID", "");
				else
					params.put("accountUserID", activeAccountUser.getId());
				params.put("accountID", activeAccountUser.getAccount().getId());
				if (ee.get("offset")!=null)
					params.put("offset", ee.get("offset").toString());
				if (ee.get("page_size")!=null)
					params.put("page_size", ee.get("page_size").toString());
				if (ee.get("filter")!=null && !ee.get("filter").toString().equals(""))
					params.put("filter", ee.get("filter").toString().trim());
				
				// to get performance count
				long count = performanceBean.countPerformanceData(params);
				
				writer.write(Long.toString(count));
	    		req.setAttribute("activeWriter", writer);
				return;
		    }
			/**
			 * Used to get performance data
			 * 
			 * @author Suja Sundaresan
			 * added on 01/12/2011
			 */
			else if (uri.endsWith("getPerformanceData.ajaxper")) {
				String listPath = req.getParameter("path");
				DateFormat dtFormat = new SimpleDateFormat("MM/dd/yyyy");
				DateFormat tmFormat = new SimpleDateFormat("HH:mm:ss.F");
				
				FormattedOutputWriter fow = new HTMLOutputWriter(writer, "_default", activeAccountUser, now);
				ExpressionEvaluator ee = fow.getExpressionEvaluator();
				ee.pushContext();
				Enumeration names = req.getParameterNames();
				while (names.hasMoreElements()) {
					String key = (String) names.nextElement();
					ee.addVariable(key, req.getParameter(key));
				}
				
				// preparing xml response
				writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
				writer.write( "<ajax-response>\n" );
				writer.write( "<response type=\"object\" id=\"" + listPath + "-LG" + "_updater\">\n" );
				writer.write( "<rows update_ui='true'>\n" );
				
				// setting parameters to HashMap
				Map<String, Object> params = new HashMap<String, Object>();
				if (activeAccountUser.isAccountPermission())
					params.put("accountUserID", "");
				else
					params.put("accountUserID", activeAccountUser.getId());
				params.put("accountID", activeAccountUser.getAccount().getId());
				if (ee.get("offset")!=null)
					params.put("offset", ee.get("offset").toString());
				
				if (ee.get("page_size")!=null)
					params.put("page_size", ee.get("page_size").toString());
				if (ee.get("sort_col")!=null && !ee.get("sort_col").toString().equals("")){
					if(ee.get("sort_col").toString().equals("date") || ee.get("sort_col").toString().equals("time"))
						params.put("sort_col", "eventTime");
					else if(ee.get("sort_col").toString().equals("user"))
						params.put("sort_col", "remoteUserName");
					else if(ee.get("sort_col").toString().equals("patient"))
						params.put("sort_col", "patientName");
					else if(ee.get("sort_col").toString().equals("remote_ip"))
						params.put("sort_col", "remoteIP");
					else if(ee.get("sort_col").toString().equals("uri"))
						params.put("sort_col", "requestURI");
					else if(ee.get("sort_col").toString().equals("method"))
						params.put("sort_col", "method");
					else
						params.put("sort_col", "eventTime");
				}
				if (ee.get("sort_dir")!=null && !ee.get("sort_dir").toString().equals(""))
					params.put("sort_dir", ee.get("sort_dir").toString());
				if (ee.get("filter")!=null && !ee.get("filter").toString().equals(""))
					params.put("filter", ee.get("filter").toString().trim());
				
				// to get performance list
				List<PerformanceData> performances = performanceBean.getPerformanceData(params);
				
				for (PerformanceData performanceData : performances) {
					fow.writeVerbatim( "<tr>");
					// event date
					fow.writeVerbatim( "<td style='text-align:left;width:6.0em'>" + dtFormat.format(performanceData.getEventTime()) + "</td>");
					// event time
					fow.writeVerbatim( "<td style='text-align:left;width:6.0em'>" + tmFormat.format(performanceData.getEventTime()) + "</td>");
					//user
					fow.writeVerbatim( "<td style='text-align:left;width:9.5em'>" + performanceData.getRemoteUserName() + "</td>");
					// patient name
					fow.writeVerbatim( "<td style='text-align:left;width:11.5em'>" + (performanceData.getPatientName()!=null? performanceData.getPatientName() : "") + "</td>");
					// uri
					fow.writeVerbatim( "<td style='text-align:left;width:20.5em'>" + performanceData.getRequestURI() + "</td>");
					// query parameter
					fow.writeVerbatim( "<td style='text-align:left;width:29.5em'>" + getMethodDisplayName(performanceData.getRequestURI(),performanceData.getMethod())+"</td>");
					fow.writeVerbatim( "</tr>\n");
				}
				ee.popContext();
				writer.write( "</rows>\n");
				writer.write( "</response>\n");
				writer.write( "</ajax-response>\n");
				req.setAttribute("activeWriter", writer);
				
			}
		} catch (Exception e) {
			throw new ServletException(
					"New Exception thrown in SurescriptsAjaxServlet", e);
		}
	}
	
	private String getMethodDisplayName(String URI, String method)
	{
		if(method==null) return "";
		else if(method.trim().indexOf("GET")==0) return "GET (read)"+method.substring(3);
		else if(method.trim().indexOf("POST")==0) return "POST (add)"+method.substring(4);
		else if(method.trim().indexOf("PUT")==0) return "PUT (update)"+method.substring(3);
		else if(method.trim().indexOf("DELETE")==0) return "DELETE (delete)"+method.substring(6);
		
		return "";
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
	
}
