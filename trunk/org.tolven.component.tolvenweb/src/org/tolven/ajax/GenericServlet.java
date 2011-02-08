package org.tolven.ajax;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.tolven.app.MenuEventHandler;
import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MSAction;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuStructure;
import org.tolven.app.entity.UMSDataTransferObject;
import org.tolven.core.entity.AccountUser;
import org.tolven.locale.TolvenResourceBundle;
import org.tolven.logging.TolvenLogger;
import org.tolven.provider.ProviderLocal;
import org.tolven.provider.entity.Provider;
import org.tolven.web.security.GeneralSecurityFilter;
import org.tolven.web.util.CalendarEventsBuilder;
import org.tolven.web.util.MiscUtils;
import org.tolven.web.util.SimileDataBuilder;

public class GenericServlet extends HttpServlet {
    
    @EJB
    private MenuLocal menuBean;
   
    @EJB
    private ProviderLocal providerBean;
    
    protected void getProviderOptions(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Writer writer=resp.getWriter();
            String prompt = req.getParameter( "prompt" );
            String providerIdString = req.getParameter( "providerId" );
            String selectedValue = req.getParameter( "selectedValue" );
            long providerId;
            if (providerIdString==null || providerIdString.length()==0) {
                        providerId = 0L;
            } else {
                        providerId = Long.parseLong(providerIdString);
            }
                List<Provider> providers = providerBean.findEndorsedProviders(providerId);
                writer.write("[{value:'',lable:'"+prompt+"'}");
                for (Provider provider : providers) {
                        if(selectedValue != null && StringUtils.isNumeric(selectedValue) && provider.getId() == Long.parseLong(selectedValue))
                                writer.write(",{selected:'selected',value:'"+Long.toString(provider.getId())+"',lable:'"+provider.getName()+"'}");
                        else
                        writer.write(",{value:'"+Long.toString(provider.getId())+"',lable:'"+provider.getName()+"'}");
                }
                writer.write("]");              
                
        } catch (Exception e) {
                throw new RuntimeException( "Error in getProviderOptions", e);
        }
 }
    

@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	try {
		      
		String uri = req.getRequestURI();
		AccountUser activeAccountUser = (AccountUser) req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
//		TolvenLogger.info(" Account User:  " + activeAccountUser, GenericServlet.class );
		resp.setContentType("text/xml");
	    resp.setHeader("Cache-Control", "no-cache");
	    Writer writer=resp.getWriter();
	    //	    writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
    	
	    if (uri.endsWith("getprovideroptions.ajaxc")) {
	    	getProviderOptions(req, resp);
    		
	    } else if (uri.endsWith("getuserpreferences.ajaxc")) {
	    	// Get path for the parent menu structure
	    	String path = req.getParameter( "element" );
	    	String rolename = req.getParameter("role");
	    	// get menu structure
	    	AccountMenuStructure ams = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), path );
	    	// get its preferred children
	    	List<MenuStructure> children = menuBean.findSortedChildren(activeAccountUser, ams);
            TolvenResourceBundle tolvenResourceBundle = (TolvenResourceBundle) req.getSession(false).getAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE);


    		writer.write("<div id=\"prefBox\" class=\"\" style=\"width:100%;display:block;\">");
    		writer.write("<div class=\"innerUserPrefBox\" style=\"width:100%;display:block\">");
    		writer.write("<ul id=\"thelist2\" class=\"userpreful\">");
	    	// prepare html
	    	for( MenuStructure item : children ){
	    		if( "tabs".equalsIgnoreCase( rolename ) && ("tab".equalsIgnoreCase( item.getRole() ) || "list".equalsIgnoreCase( item.getRole() ) ) && item.getSequence() > 0 ){
		    		writer.write("<li id=\"" + item.getPath() + "\">");
		    		String checked = ("true".equalsIgnoreCase( item.getVisible()))? "checked=\"true\"": "";
		    		writer.write("<input type=\"checkbox\" " + checked  + " value=\"" + item.getPath() + "\" />" + item.getText());
		    		writer.write("</li>");
		    		
	    		}else if( "summary".equalsIgnoreCase( rolename ) && "portlet".equalsIgnoreCase( item.getRole() ) && item.getSequence() > 0 ){
		    		writer.write("<li id=\"" + item.getPath() + "\">");
		    		String checked = ("true".equalsIgnoreCase( item.getVisible()))? "checked=\"true\"": "";
		    		writer.write("<input type=\"checkbox\" " + checked  + " value=\"" + item.getPath() + "\" />" + item.getText());
		    		writer.write("</li>");
	    		}
	    	}
    		writer.write("</ul>");
    		writer.write("<div style=\"text-align:center\">");
    		writer.write(" <input type=\"button\" value=\"" + tolvenResourceBundle.getString("Default") +  "\" onclick=\"setDefaultPreferences('" + path + "');\"/> ");
    		writer.write(" <input type=\"button\" value=\"" + tolvenResourceBundle.getString("Save") +  "\" onclick=\"savePreferences();\"/> ");
    		writer.write(" <input type=\"button\" value=\"" + tolvenResourceBundle.getString("Cancel") +  "\" onclick=\"closePrefDiv();\" /> ");
    		writer.write("</div></div></div>");
	    	// flush
	    	
//			writer.close();
    	}else if( uri.endsWith( "setuserpreferences.ajaxc" ) ) {

	    	String userpreference = req.getParameter( "userpreference" );
//	    	TolvenLogger.info(" user pref array = " + userpreference, GenericServlet.class );
	    	String[] userPrefArray = userpreference.split( ";" );
	    	for( int seq = 0; seq < userPrefArray.length; seq++){
	    		String[] onerow = userPrefArray[ seq ].split( "," );
	    		if( onerow.length == 2){
	    			String path = onerow[ 0 ];
	    			String visibility = onerow[ 1 ];
	    			int sequence = seq  + 1;
//	    			TolvenLogger.info(activeAccountUser + path + visibility + sequence + columnNumber, GenericServlet.class );
	    			UMSDataTransferObject dto = new UMSDataTransferObject();
	    			dto.setSequence( new Integer( sequence ) );
	    			dto.setVisible( visibility );
	    			menuBean.updateUserMenuStructure(activeAccountUser, path, dto );
	    		}
	    	}
	    	
	    	// Set default path
	    	String parentPath = req.getParameter("parentPath");
	    	String defaultView = req.getParameter("defaultView" );
	    	if( parentPath != null && defaultView != null ){
	    		UMSDataTransferObject dto = new UMSDataTransferObject();
	    		// Extract the suffix
	    		defaultView = defaultView.replaceFirst(parentPath, "");
	    		dto.setDefaultPathSuffix( defaultView);
	    		menuBean.updateUserMenuStructure(activeAccountUser, parentPath, dto);
	    		
	    	}
	    	
	    	// get menu structure
//	    	AccountMenuStructure ams = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), path );
    		
    	}else if (uri.endsWith("getmenuitems.ajaxc")) {
    		// Get path for the parent menu structure
	    	String path = req.getParameter( "element" );
	    	String rolename = req.getParameter("role");
	    	// get menu structure
	    	AccountMenuStructure ams = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), path );
	    	
	    	MenuStructure ms = menuBean.findMenuStructure(activeAccountUser, ams);
	    	String defPathSuffix = ms.getDefaultPathSuffix();
	    	
	    	// get its preferred children
	    	List<MenuStructure> children = menuBean.findSortedChildren(activeAccountUser, ams);
            TolvenResourceBundle tolvenResourceBundle = (TolvenResourceBundle) req.getSession(false).getAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE);
	    	String title = tolvenResourceBundle.getString("UserPreferencesTitle");
	    	
	    	// prepare xml
	    	writer.write("<ajax-response>");
	    	writer.write("<response path=\"" + path + "\" role=\"" + rolename +"\" defpath=\"" + defPathSuffix + "\" " +
	    			" title=\"" + title + "\">" );
	    	for(int i = 1; i <= 3; i++){
	    		writer.write("<UserPreferencesHelp>");
	    		writer.write( tolvenResourceBundle.getString("UserPreferencesHelp" + i ));
	    		writer.write("</UserPreferencesHelp>");
	    	}
	    	
	    	for( MenuStructure item : children ){
	    		if(!"placeholder".equalsIgnoreCase(item.getRole()) && item.getSequence() > 0 ){
	    			String checked = ("true".equalsIgnoreCase( item.getVisible()))? "true": "false";
	    			writer.write("<element path=\"" + item.getPath() + "\" visible=\"" + checked + "\">");
//		    		writer.write(item.getText());
		             writer.write(StringEscapeUtils.escapeHtml(item.getLocaleText(tolvenResourceBundle))); 
		    		writer.write("</element>");
	    		}
	    	}
	    	writer.write("<VisibilityColumnHeader>" + tolvenResourceBundle.getString("UserPreferencesVisibilityColumnHeader") + "</VisibilityColumnHeader>");
	    	writer.write("<DefaultColumnHeader>" + tolvenResourceBundle.getString("UserPreferencesDefaultColumnHeader") + "</DefaultColumnHeader>");
	    	writer.write("<MenuColumnHeader>" + tolvenResourceBundle.getString("UserPreferencesMenuColumnHeader") + "</MenuColumnHeader>");

	    	writer.write("<DefaultButton>" + tolvenResourceBundle.getString("Default") + "</DefaultButton>");
	    	writer.write("<SaveButton>" + tolvenResourceBundle.getString("Save") + "</SaveButton>");
	    	writer.write("<CancelButton>" + tolvenResourceBundle.getString("Cancel") + "</CancelButton>");
	    	
	    	writer.write("</response>");
	    	writer.write("</ajax-response>");
	    	
	    	// flush
	    	
//			writer.close();
    	}else if( uri.endsWith("setdefaultpreferences.ajaxc" ) ){
	    	String path = req.getParameter( "element" );
	    	AccountMenuStructure ams = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), path );
    		menuBean.setToDefaultMenuStructure(activeAccountUser, ams);

    	}else if( uri.endsWith( "setuserpreferencesforsummary.ajaxc" ) ){
    		String summaryorder = req.getParameter( "summaryorder" );
    		TolvenLogger.info(" Summary Order: " + summaryorder, GenericServlet.class );
    		String[] summaryNodes = summaryorder.split(";");
    		for( String item : summaryNodes ){
    			String[] node = item.split( "," );
    			if( node.length > 1 ){
        			String path = node[ 0 ];
        			String type = node[ 1 ];
        			if( type.startsWith( "c" ) ){ // type is column number 
        				// Get Colummn number;
        				type = type.substring( type.indexOf( "c_" ) + 2 );
        				Integer colnumber = Integer.parseInt( type );

        				// get sequence number
        				String seq = node[ 2 ];   // sequence
        				seq = seq.substring( seq.indexOf( "s_" ) + 2 );
        				Integer sequence = Integer.parseInt( seq );
    	    			TolvenLogger.info(activeAccountUser + path + "S=" + sequence + "C="+ colnumber, GenericServlet.class );
    	    			UMSDataTransferObject dto = new UMSDataTransferObject();
    	    			dto.setColumnNumber( colnumber );
    	    			dto.setSequence( sequence );
        				menuBean.updateUserMenuStructure(activeAccountUser, path, dto );

        			}else if( type.startsWith( "v" )){ // Set visibility of the component; [x] and undo on the screen;
        				String visibility = type.substring( type.indexOf( "v_" ) + 2 );
        				if( "true".equalsIgnoreCase( visibility ) || "false".equalsIgnoreCase( visibility ) ){
        					UMSDataTransferObject dto = new UMSDataTransferObject();
        					dto.setVisible( visibility );
                			menuBean.updateUserMenuStructure(activeAccountUser, path, dto );
                            TolvenResourceBundle tolvenResourceBundle = (TolvenResourceBundle) req.getSession(false).getAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE);
                	    	
                			writer.write( "<undo>" + tolvenResourceBundle.getString( "Undo") +  "</undo>");
        				}

        			}else if( type.startsWith( "w") ){ // Set Window Style
        				String windowStyle = type.substring( type.indexOf( "w_" ) + 2 );
        				if( "true".equalsIgnoreCase( windowStyle ) || "false".equalsIgnoreCase( windowStyle ) ){
        					UMSDataTransferObject dto = new UMSDataTransferObject();
        					dto.setWindowstyle( windowStyle );
                			menuBean.updateUserMenuStructure(activeAccountUser, path, dto );
        				}
        			}
        			else if( type.startsWith( "n") ){ // Number of items to show in each portal
        				String num = type.substring( type.indexOf( "n_" ) + 2 );
        				TolvenLogger.info(" number of summary items:  " + type, GenericServlet.class);
        				Integer numItems = Integer.parseInt( num );
        				UMSDataTransferObject dto = new UMSDataTransferObject();
        				dto.setNumItems( numItems );
        				menuBean.updateUserMenuStructure(activeAccountUser, path, dto);
        			}
    			}
    		}
    	}else if (uri.endsWith("getmoresummaries.ajaxc")) {
	    	// Get path for the parent menu structure
	    	String path = req.getParameter( "element" );
	    	String rolename = req.getParameter("role");
//	    	TolvenLogger.info(" Element, Path = " + path, GenericServlet.class );
//	    	TolvenLogger.info(" node type");
	    	// get menu structure
	    	AccountMenuStructure ams = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), path );
//	    	TolvenLogger.info(" account menu structure: " + ams, GenericServlet.class );
	    	// get its preferred children
	    	List<MenuStructure> children = menuBean.findSortedChildren(activeAccountUser, ams);

            TolvenResourceBundle tolvenResourceBundle = (TolvenResourceBundle) req.getSession(false).getAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE);
	    	
	    	writer.write("<ajax-response>");
	    	writer.write("<head>" + tolvenResourceBundle.getString("UserPreferencesTitle") + "</head>");
	    	writer.write("<body>");
	    	
    		writer.write("<div id=\"prefBox\" class=\"\" style=\"width:100%;display:block;\">");
    		writer.write("<div class=\"innerUserPrefBox\" style=\"width:100%;display:block\">");

    		StringBuffer sbuffer = new StringBuffer();
	    	// prepare html
	    	for( MenuStructure item : children ){
//	    		TolvenLogger.info(" Item " + item  + "role:" + item.getRole() + " : visibility: " + item.getVisible() + " seq: " + item.getSequence(), GenericServlet.class );
	    		if( "tabs".equalsIgnoreCase( rolename ) && ("tab".equalsIgnoreCase( item.getRole() ) || "list".equalsIgnoreCase( item.getRole() ) ) && item.getSequence() > 0 ){
	    			sbuffer.append("<li id=\"" + item.getPath() + "\">");
		    		String checked = ("true".equalsIgnoreCase( item.getVisible()))? "checked=\"true\"": "";
		    		sbuffer.append("<input type=\"checkbox\" " + checked  + " value=\"" + item.getPath() + "\" />" + item.getText());
		    		sbuffer.append("</li>");
		    		
	    		}else if( "summary".equalsIgnoreCase( rolename ) && "portlet".equalsIgnoreCase( item.getRole() ) && item.getSequence() > 0 && (!"true".equals(item.getVisible())) ){
	    			sbuffer.append("<li id=\"" + item.getPath() + "\">");
		    		String checked = ("true".equalsIgnoreCase( item.getVisible()))? "checked=\"true\"": "";
		    		sbuffer.append("<input sequence=\"" + item.getSequence() + "\" type=\"checkbox\" " + checked  + " value=\"" + item.getPath() + "\" />" + item.getText());
		    		sbuffer.append("</li>");
	    		}
	    	}
	    	if( sbuffer.length() > 0 ){
	    		writer.write("<ul id=\"thelist2\" class=\"userpreful\">");
	    		writer.write( sbuffer.toString() );
	    		writer.write("</ul>");	    		
	    	}else{
	    		writer.write("<span>No more components to add.</span>");
	    		writer.write("<BR/><hr/>");
	    	}
    		writer.write("<div style=\"text-align:center\">");
    		writer.write(" <input type=\"button\" value=\"" + tolvenResourceBundle.getString("Default") +  "\" onclick=\"setDefaultPreferences('" + path + "');\"/>");
	    	if( sbuffer.length() > 0 ){
	    		writer.write(" <input type=\"button\" value=\"" + tolvenResourceBundle.getString("Save") +  "\" onclick=\"saveAddMoreSummaries();\"/>");
	    	}
    		writer.write(" <input type=\"button\" value=\"" + tolvenResourceBundle.getString("Cancel") +  "\" onclick=\"closePrefDiv();\" />");
    		writer.write("</div></div></div>");

	    	writer.write("</body>");
	    	writer.write("</ajax-response>");
    		
    		// flush
	    	
    	} else if (uri.endsWith("menueventhandler.ajaxc")) {
                String command = req.getParameter("command");
                String actionPath = req.getParameter("path");
                String element = req.getParameter("element");
                Date tolvenNow = (Date) req.getAttribute("tolvenNow");
                MSAction action = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), actionPath);
                String menuEventHandlerFactoryClassname = action.getMenuEventHandlerFactory();
                Class<?> menuEventHandlerFactoryClass = Class.forName(menuEventHandlerFactoryClassname);
                MenuEventHandler menuEventHandler = MenuEventHandler.createMenuEventHandler(menuEventHandlerFactoryClass, action);
                menuEventHandler.setRequest(req);
                menuEventHandler.setAction(action);
                menuEventHandler.setElement(element);
                menuEventHandler.setAccountUser(activeAccountUser);
                menuEventHandler.setTolvenNow(tolvenNow);
                TolvenResourceBundle tolvenResourceBundle = (TolvenResourceBundle) req.getSession(false).getAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE);
                menuEventHandler.setResourceBundle(tolvenResourceBundle);
                menuEventHandler.setWriter(writer);
                Method method = menuEventHandler.getClass().getDeclaredMethod(command, new Class[] {});
                method.invoke(menuEventHandler, new Object[] {});
            }
    	else if (uri.endsWith("setUserTimelinePreferences.ajaxc")) { // TODO: this method can be improved if we add JSON libraries to project
    		String path = req.getParameter( "path" );
    		StringTokenizer settings = new StringTokenizer(req.getParameter("userpreferences"),":");
    		int i=0;
    		while(settings.hasMoreTokens()){  // bandName,color,visible
    		  String bandSettingsStr = settings.nextToken();
    		  TolvenLogger.info(activeAccountUser+" "+path+" "+bandSettingsStr, GenericServlet.class);
    		  StringTokenizer bandPreferences = new StringTokenizer(bandSettingsStr,",");    		  
    		  UMSDataTransferObject dto = new UMSDataTransferObject();
    		  String menuName = bandPreferences.nextToken();
    		  dto.setStyle("#"+bandPreferences.nextToken());  // hack 
    		  dto.setVisible(bandPreferences.nextToken());
    		  dto.setTimelineOrder(i++);
			  menuBean.updateUserMenuStructure(activeAccountUser, new MenuPath(path +":"+ menuName).getPath(), dto );
    		}    		
		}else if (uri.endsWith("getTimelineDataJSON.ajaxc")) {
			resp.setContentType("text/plain");
			writer = resp.getWriter();			
    		SimileDataBuilder jsonBuilder = new SimileDataBuilder(menuBean,req);
			jsonBuilder.buildJSONData(writer);	    		
		}else if (uri.endsWith("getBandEventDataJSON.ajaxc")) {
			resp.setContentType("text/plain");
			writer = resp.getWriter();	
    		SimileDataBuilder jsonBuilder = new SimileDataBuilder(menuBean,req);
			jsonBuilder.getBandEvents(writer);	    		
		}else if (uri.endsWith("getTimelinePreferences.ajaxc")) {
			resp.setContentType("text/plain");
			writer = resp.getWriter();	
    		SimileDataBuilder jsonBuilder = new SimileDataBuilder(menuBean,req);
			jsonBuilder.buildTimlePreferences(writer);	    		
		}else if (uri.endsWith("getCalendarEvents.ajaxc")) {
			resp.setContentType("text/plain");
			writer = resp.getWriter();	
    		CalendarEventsBuilder eventsBuilder = new CalendarEventsBuilder(menuBean,req);
			eventsBuilder.buildCalendarEvents(writer);	    		
		}else if (uri.endsWith("getActions.ajaxc")) {
			String element = req.getParameter( "element" );
			MenuPath path = new MenuPath( element );
			AccountMenuStructure ms = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), path.getPath() );
			List<MSAction> actions = ms.getActions();
            TolvenResourceBundle tolvenResourceBundle = (TolvenResourceBundle) req.getSession(false).getAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE);
			writer.write(MiscUtils.createActionButtons(actions, element, tolvenResourceBundle));
			
		}else if( uri.endsWith( "updateMenuColumnSequence.ajaxc" )) {
			// sequence=heading:2;heading:1;heading:3
			String mspath = req.getParameter("mspath");
			String params = req.getParameter("sequence");
			if(mspath != null && params != null){
				AccountMenuStructure ms = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), mspath );
				
				List<MSColumn> mscolumns = ms.getSortedColumns();
				HashMap<String, Integer> order = new HashMap<String, Integer>();
				
				String[] colsArray = params.split(";");
				for(String col : colsArray){
					String[] column = col.split("-");
					String heading = column[0];
					Integer seq = new Integer( column[1]);
					order.put(heading, seq);
				}
				for(MSColumn c : mscolumns){
					if( order.containsKey( c.getHeading()) ){
						Integer seq = order.get(c.getHeading());
						c.setSequence( seq.intValue());
						System.out.println("Change header: " + c.getHeading() + "  new sequence: " + seq);
					}
				}if(order.size() > 0){
					menuBean.updateColumns(mscolumns);
				}
			}
		}else if( uri.endsWith( "updateMenuStructure.ajaxc" )) {
			String type = req.getParameter("type");
			if(null != type){
				if("sequence".equals( type) ){
					String params = req.getParameter("sequence"); // path:seqno;path:seqno;
					if(params != null){
						String[] elements = params.split(";");
						List<MenuStructure> msToUpdate = new ArrayList<MenuStructure>();
						for(String node : elements){
							String[] item = node.split("-");
							String path = item[0];
							Integer sequence = new Integer(item[1]);
							AccountMenuStructure ms = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), path );
							ms.setSequence( sequence.intValue());
							msToUpdate.add(ms);
						}
						menuBean.updateMenus(msToUpdate);
					}
				}else if("translationoverride".equals( type) ){
					String toverride = req.getParameter("overridetext");
					String path = req.getParameter("path");
					path = path.replace('.', ':');
					AccountMenuStructure ms = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), path );
					if(toverride.isEmpty() ) 
						ms.setTextOverride(null);
					else
						ms.setTextOverride(toverride);
					List<MenuStructure> list = new ArrayList<MenuStructure>();
					list.add(ms);
					menuBean.updateMenus(list);
					writer.write(toverride);
				}else if("visibility".equals( type)){
					String visibility = req.getParameter("visible");
					String path = req.getParameter("path");
					path = path.replace('.', ':');
					AccountMenuStructure ms = menuBean.findAccountMenuStructure(activeAccountUser.getAccount().getId(), path );
					if(visibility.isEmpty()){
						ms.setVisible( null);
					}else{
						ms.setVisible( visibility);
					}
					List<MenuStructure> list = new ArrayList<MenuStructure>();
					list.add(ms);
					menuBean.updateMenus( list);
					writer.write(visibility);
				}
			}

	    }else if( uri.endsWith( "isserveravailable.ajaxc" )) {
    		writer.write( "<server><info>available</info></server>" );
    	}

	} catch (Exception e) {
		throw new ServletException( "Exception thrown in GenericServlet", e);
	}
}
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	doGet( request, response );
}

}
