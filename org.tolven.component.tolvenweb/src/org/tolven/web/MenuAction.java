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

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Iterator;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.tolven.ajax.GridBuilder;
import org.tolven.app.MenuLocal;
import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuDataVersion;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.app.entity.MenuLocator;
import org.tolven.ccr.ContinuityOfCareRecord;
import org.tolven.core.entity.AccountExchange;
import org.tolven.core.entity.AccountRole;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.entity.DocAttachment;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocCCR;
import org.tolven.doc.entity.DocXML;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.key.DocumentSecretKey;

/**
 * Faces Action Bean concerned with menu configuration and display. Note that menus include both metadata (MenuStructure) and
 * actual instance data (MenuData) sometimes called index data.
 * @author John Churin
 *
 */
public class MenuAction extends TolvenAction {
    
    private String graphURL = null; 
    private String element; 
    private String role;
    private String visible;
    private MenuPath targetMenuPath;
    private long accountId = 0;
    private MenuDataVersion menuDataVersion;
    private MenuStructure ms = null;
    private List<MenuStructure> prefChildren = null;
    private MenuData menuDataItem = null;
    private Long menuDataCount = null;
    private GridBuilder grid;
    private List<String> childPaths;
    private List<AccountExchange> providers;
    private DataModel providersModel;
    private String attachmentDescription;
    private String attachmentType;
    
	private String givenName;
    private String value;
	static final String TRIM_NS = "urn:tolven-org:trim:4.0"; 
	static final String CCR_NS = "urn:astm-org:CCR"; 

    private List<MenuStructure> menus = null;
    private DataModel menuModel;
	private AccountMenuStructure menuToEdit;
	private List<String> menuAllowRoles=null;
	private List<String> menuDenyRoles=null;
    private String menuToEditPath;
    private UIInput menuToEditPathCtrl;
	private List<MSColumn> menuToEditColumns;
	private List<MenuStructure> visibleChildren;
    // The CCR document for this menuDataItem, if any
    private ContinuityOfCareRecord ccr;
  
    public MenuAction() {
        super();
    }
    
    /**
	 * Get the object for the requested element
	 */
	public MenuStructure getThisMenu( ) {
//		TolvenLogger.info( "******** [MenuAction] Fetch MenuStructure for account: " + getAccountId() + " path: " + getTargetMenuPath().getPath() + " element: " + getElement(), MenuAction.class);
//		MenuData md = getMenuLocal().findMenuDataItem(getAccountId(), getElement());

		if (ms==null) ms =  getMenuLocal().findMenuStructure( getTop().getAccountUser(), getTargetMenuPath().getPath() );
		return ms;
	}
	
	/*
	 * Get children 
	 */
	public List<MenuStructure> getPreferredSortedChildren() {
//		TolvenLogger.info(" getPreferredSortedChildren " + getTargetMenuPath().getPath(), MenuAction.class);
		MenuStructure ms = getThisMenu();
		if( null == prefChildren ) prefChildren = getMenuLocal().findSortedChildren( getTop().getAccountUser(), ms.getAccountMenuStructure()); //new ArrayList<MenuStructure>( amsList.size() );
		return prefChildren;
	}
	
	/*
	 * List of Menu items for setting user preferences.
	 */
	public List<MenuStructure> getTabsAndLists() {
		List<MenuStructure> tabsAndLists = new ArrayList<MenuStructure>();
		getPreferredSortedChildren();
		
		if("tabs".equalsIgnoreCase( getRole() )){
			for(MenuStructure item : prefChildren){
				if(("tab".equalsIgnoreCase( item.getRole() ) || "list".equalsIgnoreCase( item.getRole() ) ) && item.getSequence() > 0 ){
					tabsAndLists.add(item);
				}
			}
		}
		return tabsAndLists;
	}
		
	/*
	 * List of portlets for the summary preferences popup menu
	 */
	public List<MenuStructure> getPortlets() {
		List<MenuStructure> portlets = new ArrayList<MenuStructure>();
		getPreferredSortedChildren();

		if("summary".equals( getRole())){
			for(MenuStructure ms : prefChildren){
				if("portlet".equals( ms.getRole()) && ms.getSequence() > 0 && (!"true".equals(ms.getVisible())) ){
					portlets.add(ms);
				}
			}
		}
		return portlets;
	}
	
	public List<String> getPreferredChildPaths( ) {
		if (childPaths==null ) {
			childPaths = new ArrayList<String>( 10 );
			MenuStructure msParent = getThisMenu();
			List<MenuStructure> children = getMenuLocal().findSortedChildren(getTop().getAccountUser(), msParent.getAccountMenuStructure() );
			for ( MenuStructure msChild : children) {
				if ("true".equals(msChild.getVisible())) {
					MenuPath msPath = new MenuPath(msChild.getPath(), getTargetMenuPath() );
					childPaths.add( msPath.getPathString() );
				}
			}
//			TolvenLogger.info( getElement() + " children: " + childPaths, MenuAction.class);
		}
		return childPaths;
	}

	public List<String> getPreferredChildPathsByColumn( int colNumber ) {
//		TolvenLogger.info(" getPreferredChildPaths " + colNumber, MenuAction.class);
		List<String> childrenPaths = new ArrayList<String>( getPreferredSortedChildren().size() );
		try{
			for ( MenuStructure ms : getPreferredSortedChildren() ){
				if("true".equals( ms.getVisible() ) ){
					if( ms.getColumnNumber() != null ){
//						TolvenLogger.info(" col number not null ", MenuAction.class);
						if( ms.getColumnNumber() == colNumber ){
							MenuPath msPath = new MenuPath(ms.getPath(), getTargetMenuPath() ); 
//							TolvenLogger.info(msPath.getPathString() + " :- ADDED ", MenuAction.class);
							childrenPaths.add( msPath.getPathString());
						}					
					}else{
//						TolvenLogger.info(" col number is null ", MenuAction.class);
						if( ms.getSequence() >= ( ( colNumber * 3 ) - 2 ) && ms.getSequence() <= ( colNumber * 3 ) ){
							MenuPath msPath = new MenuPath(ms.getPath(), getTargetMenuPath() );
//							TolvenLogger.info(msPath.getPathString() + " :- ADDED .. col number is null ", MenuAction.class);
							int originalIndex = ms.getSequence() - (( colNumber * 3 ) - 2);
							int index = (childrenPaths.size() < originalIndex )?childrenPaths.size():originalIndex;
							childrenPaths.add( index, msPath.getPathString() );
						}
					}
				}
			}
		}catch( Exception e){
			e.printStackTrace();
		}

		return childrenPaths;
	}
	
	// One for each column.
	public List<String> getPreferredChildPathsColumn1() {
		return getPreferredChildPathsByColumn( 1 );
	}
	
	public List<String> getPreferredChildPathsColumn2() {
		return getPreferredChildPathsByColumn( 2 );
	}
	
	public List<String> getPreferredChildPathsColumn3() {
		return getPreferredChildPathsByColumn( 3 );
	}
	
	/*
	 * Get Default Path Suffix
	 */
	public String getDefaultPathSuffix() {
		return getThisMenu().getDefaultPathSuffix();
	}
	
	/**
	 * Return the path to the drilldown element, if any. for example, from the problem summary (heading) to the detailed problem list.
	 */
	public String getDrilldown( ) {
		MenuStructure ms = getThisMenu();
		if (ms==null || ms.getRepeating()==null) return null;
		// Merge ids in the current path with the repeating path, if any.
		MenuPath mp = new MenuPath(ms.getRepeating(), getTargetMenuPath());
		return mp.getPathString();
	}
	
	/**
	 * Get the parent of the requested element
	 */
	public MenuStructure getThisParentMenu( ) {
//		return getMenuLocal().findMenuStructure( getAccountId(), getTargetMenuPath().getParentPath() ); //commented by Arun
		return getMenuLocal().findMenuStructure(getTop().getAccountUser(), getTargetMenuPath().getParentPath() );
	}

	public MenuPath getTargetMenuPath( ) {
		if (targetMenuPath==null) {
			targetMenuPath = new MenuPath( getElement() );
		}
		return targetMenuPath;
	}

	public DataModel getMenuModel() {
		if(menuModel == null){
			menuModel = new ListDataModel();
			menuModel.setWrappedData(getMenus());
		}
		return menuModel;
	}
	
	/**
	 * Return the entire menu structure list for the logged in user.
	 */
	public List<MenuStructure> getMenus( ) {
		if (menus==null) {
			menus = getMenuLocal().findFullMenuStructure( getAccountId() );
		}
		return menus;
	}

	public void setMenus(List<MenuStructure> menus) {
		this.menus = menus;
	}
	
	/*
	 * All roles associated with the Account User.
	 */
	public List<String> getRoles() {
		Set<AccountRole> roleSet = getAccountBean().findAccount(getAccountId()).getAccountRoles();

		List<String> roles = new ArrayList<String>();
		for(AccountRole role : roleSet){
			roles.add( role.getRole());
		}
		return roles;
	}
	/**
	 * Action to update the menus for this account to that specified in the accountType.
	 */
	public String refreshMetaData() {
		getMenuLocal().updateMenuStructure(getTop().getAccountUser().getAccount());
		return "success";
	}
	
	/**
	 * Action to update the menus for this user.
	 */
	public String updateMenus() {
//		getMenuLocal().updateMenus( getMenus());
		
		return "success";
	}

	/**
	 * Get the accountId for this menu. 
	 * @return tolven accountId
	 */
	public long getAccountId() {
		if (accountId==0) {
			accountId = getSessionAccountId();
		}
		if (accountId==0) {
			throw new IllegalArgumentException( "[MenuAction] Missing AccountId");
		}
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	
	public String getParameterFromRequest(String name){
		String value = null;
		String referer = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("Referer");
		TolvenLogger.info( "Got referer: " + referer, MenuAction.class );
		int queryParamIndex = referer.indexOf("?");
		if (queryParamIndex >= 0) {
			String queryParams[] = referer.substring(queryParamIndex+1).split("&");
			for (int p = 0; p < queryParams.length;p++) {
				TolvenLogger.info( "Referer [" + p + "]: " + queryParams[p], MenuAction.class );
				String param[] = queryParams[p].split("=");
				if (name.equals(param[0])) {
					value = param[1];
					TolvenLogger.info( "Got element: " + value, MenuAction.class );
					break;
				}
			}
		}

		return value;
	}

	/**
	 * Get the element we're currently processing. The element comes from the browser as a parameter "element=xxx".
	 * In most cases, we will need to parse the element in order to do anything with it.
	 * @return
	 */
	public String getElement() {
		if (element==null) {
			Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			// Parse the element if specified
			element = params.get("element");
			if (element==null) {
				element = getParameterFromRequest("element");
			}
		}
//		TolvenLogger.info( "getElement: " + element, MenuAction.class);
		return element;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRole() {
		if (role==null) {
			Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			// Parse the element if specified
			role = params.get("role");
			if (role==null) {
				role = getParameterFromRequest("role");
			}
		}
		return role;
	}

	/**
	 * Get the element cleansed to be suitable for a javascript label. (Remove ":", "-" and other special characters)
	 * Update: Now just  return E and a big number
	 * @return
	 */
	public String getElementLabel() {
		return getTargetMenuPath().getLabel();
	}

	/**
	 * Get the original element leading to the current element. The original element comes from the browser as a 
	 * parameter "element=xxx". In most cases, we will need to parse the element in order to do anything with it.
	 * @return
	 */
	public String getOriginal() {
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		// Parse the element if specified
		return params.get("original");
	}

	public void setElement(String element) {
		this.element = element;
	}
	
    private MDItem mdItem;
	
	class MDItem extends HashMap<String, MenuData> {
		private MenuLocal menuLocal;
		private long accountId;
		
		public MDItem( MenuLocal menuLocal, long accountId) {
			this.menuLocal = menuLocal;
			this.accountId = accountId;
		}
		
		public MenuData get(Object path) {
			if (!this.containsKey(path)) {
				MenuData item = menuLocal.findMenuDataItem(accountId, (String)path);
				put( (String)path, item);
				return item;
			}
			return super.get(path);
		}
	}

	/**
	 * Shortcut to any supplied menu data item in the account.
	 * @param path (accountId is implied)
	 * @return
	 * @throws Exception
	 */
	public MDItem getItem() throws Exception {
		if (mdItem==null) {
			mdItem = new MDItem( this.getMenuLocal(), this.getAccountId());
		}
		return mdItem;
	}

	/**
	 * Get the menudata for the requested element
	 */
	public List<MenuData> getMenuData( ) {
		MenuQueryControl ctrl = new MenuQueryControl();
		ctrl.setLimit( 50000 );	// TODO: This is a hard coded hard query limit that should be in a property or something
		ctrl.setMenuStructure( getThisMenu().getAccountMenuStructure() );
		ctrl.setAccountUser(getTop().getAccountUser());
		ctrl.setNow( getNow());
		ctrl.setOffset( 0 );
		ctrl.setOriginalTargetPath( getTargetMenuPath() );
		ctrl.setRequestedPath( getTargetMenuPath() );
		ctrl.setSortDirection( "ASC");
		ctrl.setSortOrder( "id" );
		return getMenuLocal().findMenuData( ctrl );
	}
	
	/**
	 * From the current element, return a list of elements referencing each of the child
	 * nodes of the current MenuStructure item. Used to build summary pages from metaData.
	 * @return list of elements to include.
	 */
	public List<String> getChildPaths( ) {
		if (childPaths==null ) {
			childPaths = new ArrayList<String>( 10 );
			MenuStructure msParent = getThisMenu();
			for ( MenuStructure msChild : msParent.getSortedChildren()) {
				if ("true".equals(msChild.getVisible())) {
					MenuPath msPath = new MenuPath(msChild.getPath(), getTargetMenuPath() );
					childPaths.add( msPath.getPathString() );
				}
			}
//			TolvenLogger.info( getElement() + " children: " + childPaths, MenuAction.class);
		}
		return childPaths;
	}
	
	/**
	 * Get the menudata for the requested element
	 */
	public List<MenuData> getSummaryMenuData( ) {
		MenuStructure ms = getThisMenu().getAccountMenuStructure();
		MenuQueryControl ctrl = new MenuQueryControl();
		ctrl.setLimit( getThisMenu().getNumSummaryItems() ); 
		ctrl.setMenuStructure( ms );
		ctrl.setAccountUser(getTop().getAccountUser());
		ctrl.setNow( getNow());
		ctrl.setOffset( 0 );
		ctrl.setFilter(ms.getFilter());
		ctrl.setOriginalTargetPath( getTargetMenuPath() );
		ctrl.setRequestedPath( getTargetMenuPath() );
		ctrl.setSortDirection( "DESC"); // TODO: Hard-coded limit of default sort criteria should be in MenuStructure.
		ctrl.setSortOrder(ms.getInitialSort());
		if (ctrl.getSortOrder()==null) {
			ctrl.setSortOrder( "date01" );
		}
		return getMenuLocal().findMenuData( ctrl );
	}
	
	/**
	 * Add escape characters for things like < >
	 * @param writer
	 * @param val
	 * @throws IOException 
	 */
	public static void escapeString( StringBuffer sb, String val ) {
		for ( char c : val.toCharArray()) {
	    	switch ( c ) {
	    	case '<' : sb.append( "&lt;");break;
	    	case '>' : sb.append( "&gt;");break;
	    	case '&' : sb.append( "&amp;");break;
	    	case '"' : sb.append( "&quot;");break;
	    	default : sb.append(c);
	    	}
		}
	}
	/**
	 * Return summary data as a table
	 * @return
	 */
	public String getSummaryMenuDataRows( ) {
		StringBuffer sb = new StringBuffer( 512 );
		List<MenuData> mdList = getSummaryMenuData( );
		MenuStructure ms = getThisMenu().getAccountMenuStructure().getReferenced();
		List<MSColumn> cols = ms.getSortedColumns();
		TimeZone timeZone = TimeZone.getTimeZone(getTop().getTimeZone());
	    MSColumn.MDFieldGetter fieldGetter = new MSColumn.MDFieldGetter( );
	    fieldGetter.setNow(  getNow() );
	    fieldGetter.setTimeZone( timeZone );
	    fieldGetter.setLocale( getTop().getLocale() );
		for (MenuData md : mdList ) {
		    fieldGetter.setMenuData(md);
			sb.append("<tr>");
			for (MSColumn col : cols) {
				sb.append("<td>");
				escapeString(sb, col.getFormattedColumn( fieldGetter ));
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		return sb.toString();
	}
	/**
	 * Get the count of menudata for the requested element
	 */
	public long getMenuDataCount( ) {
		if (menuDataCount==null) {
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setLimit( 0 );
			ctrl.setAccountUser(getTop().getAccountUser());
			ctrl.setMenuStructure( getThisMenu().getAccountMenuStructure() );
			ctrl.setNow( getNow());
			ctrl.setOffset( 0 );
			ctrl.setOriginalTargetPath( getTargetMenuPath() );
			ctrl.setRequestedPath( getTargetMenuPath() );
			ctrl.setSortDirection( "ASC");
			ctrl.setSortOrder( "" );
			menuDataCount = new Long( getMenuLocal().countMenuData( ctrl ) );
		}
		return menuDataCount;
	}

	/**
	 * Get a single menudata item for the requested element. Throw an exception if more (or less) than one item is found. 
	 * @throws Exception 
	 */
	public MenuData getMenuDataItem( ) throws Exception {
		if (menuDataItem==null) { 
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setAccountUser(getTop().getAccountUser());
			ctrl.setMenuStructure( getThisMenu().getAccountMenuStructure() );
			ctrl.setNow( getNow());
			ctrl.setOriginalTargetPath( getTargetMenuPath() );
			ctrl.setRequestedPath( getTargetMenuPath() );
			menuDataItem =  getMenuLocal().findMenuDataItem( ctrl );
		}
		return menuDataItem;
	}
	
	/**
	 * get the repeating item if specified in MenuStructure or 
	 * @return
	 * @throws Exception
	 */
	public MenuData getDrilldownItem( ) throws Exception {
		if (menuDataItem==null) { 
			MenuQueryControl ctrl = new MenuQueryControl();
			String drilldownItem = this.getDrilldown();
			if (drilldownItem!=null) {
				ctrl.setRequestedPath( new MenuPath(drilldownItem) );
			} else {
				ctrl.setRequestedPath( getTargetMenuPath() );
			}
			ctrl.setAccountUser(getTop().getAccountUser());
			ctrl.setOriginalTargetPath( getTargetMenuPath() );
			ctrl.setMenuStructure( getThisMenu().getAccountMenuStructure() );
			ctrl.setNow( getNow());
			menuDataItem =  getMenuLocal().findMenuDataItem( ctrl );
		}
		return menuDataItem;
	}

	/**
	 * This method is needed because menuData only contains documentId, a separate query is needed
	 * to get the document itself.
	 * @return
	 * @throws Exception
	 */
	public DocBase getDrilldownItemDoc()  throws Exception {
		MenuData md = getDrilldownItem();
		if (md==null) return null;
		DocBase doc = getDocumentLocal().findDocument(md.getDocumentId());
		return doc;
	}
	
	/**
	 * Return the XML namespace of the document if it is an XML document.
	 * @return the XML namespace or null if the document is not XML
	 * @throws Exception 
	 */
	public String getxmlNS( ) throws Exception {
		DocBase docBase = getDrilldownItemDoc();
		if (docBase!=null && docBase instanceof DocXML) {
			return ((DocXML)docBase).getXmlNS();
		}
		return null;
	}
	
	/**
	 * Construct and return a string containing HTML for a grid control for the current item.
	 * @return
	 * @throws IOException 
	 */
	public String getGridControl() throws IOException {
		if (grid==null) {
			// java script method for grid rows on-click
			String scriptMethodName = (String)getRequestParameter("method");
			// Form id for .   
			String scriptMethodArgs = (String)getRequestParameter("methodArgs");
			
			// Setup a query control
			MenuQueryControl ctrl = new MenuQueryControl();
			ctrl.setLimit( 0 );
			ctrl.setAccountUser(getTop().getAccountUser());
			ctrl.setMenuStructure( getThisMenu().getAccountMenuStructure() );
			ctrl.setNow( getNow());
			ctrl.setOffset( 0 );
			ctrl.setOriginalTargetPath( getTargetMenuPath() );
			ctrl.setRequestedPath( getTargetMenuPath() );
			ctrl.setSortDirection( "ASC");
			ctrl.setSortOrder( "" );
			// Get the number of rows
			menuDataCount = new Long( getMenuLocal().countMenuData( ctrl ) );
			grid = new GridBuilder(ctrl, menuDataCount);
			grid.createGrid(scriptMethodName, scriptMethodArgs );			
//			TolvenLogger.info( grid.toString(), MenuAction.class);
		}
		return grid.toString();
	}
	
	/**
	 * This method is needed because menuData only contains documentId, a separate query is needed
	 * to get the document itself.
	 * @return
	 * @throws Exception
	 */
	public String getDrilldownItemDocContentString()  throws Exception {
        AccountUser activeAccountUser = getAccountUser();
		return getDocProtectionBean().getDecryptedContentString(getDrilldownItemDoc(), activeAccountUser, getUserPrivateKey());
	}

	public DocCCR getDocCCR( ) throws Exception {
		MenuData md = getDrilldownItem();
//		TolvenLogger.info( "[getDocCCR] MD.id=" + md.getId(), MenuAction.class);
		if (md.getDocumentId()==0) return null;
		DocBase doc = getDocumentLocal().findDocument(md.getDocumentId());
		if (doc==null) {
			throw new RuntimeException( "Document id invalid in MD " + md.getId());
		}
		if (!(doc instanceof DocXML)) {
			return null;
		}
		if (!((DocXML)doc).getXmlNS().equals(CCR_NS) ) {
			return null;
		}
		if (!(doc instanceof DocCCR)) {
			throw new RuntimeException( "Document is not CCR " + doc.getId() + " Class: " + doc.getClass().getName());
		}
		return (DocCCR) doc;
	}
	
	public ContinuityOfCareRecord getCcr( ) throws Exception {
		if (ccr==null && getDocCCR()!=null) {
			ccr = (ContinuityOfCareRecord) getXMLProtectedBean().unmarshal(getDocCCR(), getTop().getAccountUser(), getUserPrivateKey());
		}
		return ccr;
	}

	/**
     * Creates a chart based on MenuData
     * @param dataset  a dataset
     * @return A chart suitable for rendering
     */
    public JFreeChart createChart(String title, XYDataset dataset ) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title,  // title
            "Date",             // x-axis label
            "Value",   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
        }
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        return chart;
    }
    
    /**
     * Creates a dataset from menuData.
     *
     * @return An XY dataset
     */
    public XYDataset createDataset(String path) {
    	// Adjust the path to make this work
    	MenuStructure msLab = getMenuLocal().findMenuStructure(getAccountId(), path);
    	if (msLab==null) throw new IllegalArgumentException( "Path not valid for this account");
    	// Create a new path based on the matching id(s) from the called path
    	// for example, if patient were specified in the input nodeValues and the new path has a patient node, then
    	// it's pulled.
    	MenuPath mdPath = new MenuPath(msLab.getPath(), getTargetMenuPath() );
//        TolvenLogger.info("dataset: Query from " + msLab.getPath() + " for requested path: " + getTargetMenuPath(), MenuAction.class);
		MenuQueryControl ctrl = new MenuQueryControl();
		ctrl.setLimit( 5000 );	// TODO: This is a hard coded hard query limit that should be in a property or something
		ctrl.setMenuStructure( msLab );
		ctrl.setAccountUser(getTop().getAccountUser());
		ctrl.setNow( getNow());
		ctrl.setOffset( 0 );
		ctrl.setOriginalTargetPath( mdPath );
		ctrl.setRequestedPath( mdPath );
		List<MenuData> menuData = getMenuLocal().findMenuData( ctrl );
        TimeSeries s1 = new TimeSeries("triglycerides (mg/dL)", Month.class);
        TimeSeries s2 = new TimeSeries("low-density lipoprotein - LDL (mg/dL)", Month.class);
        for ( MenuData md : menuData ) {
        	TimeSeries sx;
//            TolvenLogger.info("MD Item: " + md.getId(), MenuAction.class);
            String result = md.getString02();
        	if ("triglycerides".equals(result)) {
        		sx = s1;
        	}
        	else if (result!=null && result.startsWith("low-density")) {
        		sx = s2;
        	} else continue;
        	GregorianCalendar cal = new GregorianCalendar();
        	cal.setTime( md.getDate01() );
        	Month m = new Month( cal.get(GregorianCalendar.MONTH)+1, cal.get(GregorianCalendar.YEAR));
//        	TolvenLogger.info( "Graph Data: " + m.getMonth() + "/" + m.getYear() + "=" + md.getPqValue01(), MenuAction.class);
        	sx.addOrUpdate(m, md.getPqValue01());
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);

        dataset.setDomainIsPointsInTime(true);
        TolvenLogger.info("Done preparing Dataset", MenuAction.class);

        return dataset;

    }
	
	public String getGraphURL()  throws IOException {
		if (graphURL==null) {
//	        TolvenLogger.info("Preparing Graph", MenuAction.class);
			MenuStructure ms = getThisMenu( );
			JFreeChart chart = createChart("Lipids", createDataset( ms.getAccount().getAccountType().getKnownType() + ":patient:results:lab"));
	        String filename = ServletUtilities.saveChartAsPNG(chart, 600, 400, (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false));
	        graphURL = "my.graph?filename=" + URLEncoder.encode(filename, "UTF-8");
	        TolvenLogger.info("Graph URL: " + graphURL, MenuAction.class);
		}
		return graphURL;
	}
	class TolvenHour extends Hour {
		private static final long serialVersionUID = 1L;
		SimpleDateFormat sdf = new SimpleDateFormat( "dd-MMM-yy hh");
		public TolvenHour( Date date ) {
			super( date );
		}
		public String toString() {
			String s = sdf.format(this.getStart());
			TolvenLogger.info( "[TolvenDate:toString]" + s, MenuAction.class);
			return s;
		}
	}
	
	/**
     * Creates a dataset from menuData.
     *
     * @return An XY dataset
     */
    public XYDataset createDataset2(String path) {
    	// Adjust the path to make this work
    	MenuStructure msLab = getMenuLocal().findMenuStructure(getAccountId(), path);
    	if (msLab==null) throw new IllegalArgumentException( "Path not valid for this account");
    	// Create a new path based on the matching id(s) from the called path
    	// for example, if patient were specified in the input nodeValues and the new path has a patient node, then
    	// it's pulled.
    	MenuPath mdPath = new MenuPath(msLab.getPath(), getTargetMenuPath() );
//        TolvenLogger.info("dataset: Query from " + msLab.getPath() + " for requested path: " + getTargetMenuPath(), MenuAction.class);
		MenuQueryControl ctrl = new MenuQueryControl();
		ctrl.setLimit( 5000 );	// TODO: This is a hard coded hard query limit that should be in a property or something
		ctrl.setMenuStructure( msLab );
		ctrl.setAccountUser(getTop().getAccountUser());
		ctrl.setNow( getNow());
		ctrl.setOffset( 0 );
		ctrl.setOriginalTargetPath( mdPath );
		ctrl.setRequestedPath( mdPath );
		List<MenuData> menuData = getMenuLocal().findMenuData( ctrl );
        TimeSeries s1 = new TimeSeries("glucose (mg/dL)", TolvenHour.class);
        TimeSeries s2 = new TimeSeries("Weight (lb)", TolvenHour.class);
        for ( MenuData md : menuData ) {
        	TimeSeries sx;
//            TolvenLogger.info("MD Item: " + md.getId(), MenuAction.class);
            String result = md.getString01();
        	if ("Blood Glucose".equals(result)) {
        		sx = s1;
        	}
        	else if ("Weight".equals(result)) {
        		sx = s2;
        	} else continue;
        	Hour d = new TolvenHour( md.getDate01());
//        	TolvenLogger.info( "Graph Data: " + m.getMonth() + "/" + m.getYear() + "=" + md.getPqValue01(), MenuAction.class);
        	TimeSeriesDataItem di = sx.getDataItem(d);
        	if (di!=null) TolvenLogger.info( "Date: " + di.getPeriod() + " replacing " + di.getValue() + " with " + md.getPqValue01(), MenuAction.class);
        	sx.addOrUpdate(d, md.getPqValue01());
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);
//        dataset.setDomainIsPointsInTime(true);
        TolvenLogger.info("Done preparing Dataset", MenuAction.class);

        return dataset;

    }
	
	public String getGraphURL2()  throws IOException {
		if (graphURL==null) {
//	        TolvenLogger.info("Preparing Graph", MenuAction.class);
			MenuStructure ms = getThisMenu( );
			JFreeChart chart = createChart("Diabetes", createDataset2( ms.getAccount().getAccountType().getKnownType() + ":patient:observations:all"));
			String filename = ServletUtilities.saveChartAsPNG(chart, 600, 400, (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false));
	        graphURL = "my.graph?filename=" + URLEncoder.encode(filename, "UTF-8");
	        TolvenLogger.info("Graph2 URL: " + graphURL, MenuAction.class);
		}
		return graphURL;
	}

	/**
     * Creates a dataset from menuData.
     *
     * @return An XY dataset
     */
    public XYDataset createDataset3(String path) {
    	// Adjust the path to make this work
    	MenuStructure msLab = getMenuLocal().findMenuStructure(getAccountId(), path);
    	if (msLab==null) throw new IllegalArgumentException( "Path not valid for this account");
    	// Create a new path based on the matching id(s) from the called path
    	// for example, if patient were specified in the input nodeValues and the new path has a patient node, then
    	// it's pulled.
    	MenuPath mdPath = new MenuPath(msLab.getPath(), getTargetMenuPath() );
//        TolvenLogger.info("dataset: Query from " + msLab.getPath() + " for requested path: " + getTargetMenuPath(), MenuAction.class);
		MenuQueryControl ctrl = new MenuQueryControl();
		ctrl.setLimit( 5000 );	// TODO: This is a hard coded hard query limit that should be in a property or something
		ctrl.setMenuStructure( msLab );
		ctrl.setAccountUser(getTop().getAccountUser());
		ctrl.setNow( getNow());
		ctrl.setOffset( 0 );
		ctrl.setOriginalTargetPath( mdPath );
		ctrl.setRequestedPath( mdPath );
		List<MenuData> menuData = getMenuLocal().findMenuData( ctrl );
        TimeSeries s1 = new TimeSeries("Peak Flow", TolvenHour.class);
        for ( MenuData md : menuData ) {
        	TimeSeries sx;
//            TolvenLogger.info("MD Item: " + md.getId(), MenuAction.class);
            String result = md.getString01().toLowerCase();
        	if ("peak flow".equals(result) || "peakflow".equals(result)) {
        		sx = s1;
        	} else continue;
        	Hour d = new TolvenHour( md.getDate01());
//        	TolvenLogger.info( "Graph Data: " + m.getMonth() + "/" + m.getYear() + "=" + md.getPqValue01(), MenuAction.class);
        	TimeSeriesDataItem di = sx.getDataItem(d);
        	if (di!=null) TolvenLogger.info( "Date: " + di.getPeriod() + " replacing " + di.getValue() + " with " + md.getPqValue01(), MenuAction.class);
        	sx.addOrUpdate(d, md.getPqValue01());
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
//        dataset.setDomainIsPointsInTime(true);
        TolvenLogger.info("Done preparing Dataset", MenuAction.class);

        return dataset;

    }
	
	public String getGraphURL3()  throws IOException {
		if (graphURL==null) {
//	        TolvenLogger.info("Preparing Graph", MenuAction.class);
			MenuStructure ms = getThisMenu( );
			JFreeChart chart = createChart("Peak Flow", createDataset3( ms.getAccount().getAccountType().getKnownType() + ":patient:observations:all"));
			String filename = ServletUtilities.saveChartAsPNG(chart, 600, 400, (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false));
	        graphURL = "my.graph?filename=" + URLEncoder.encode(filename, "UTF-8");
	        TolvenLogger.info("Graph2 URL: " + graphURL, MenuAction.class);
		}
		return graphURL;
	}
	
	
	/**
	 * Submit the current menu item, which must be a wizard
	 * @return
	 * @throws Exception 
	 */
	public String submit( ) {
		try {
			MenuData md = getMenuDataItem( );
			md.setString03(getValue());
			TolvenLogger.info("Submitted value: " + md.getString03(), MenuAction.class);
            AccountUser activeAccountUser = getAccountUser();
            getCreatorBean().submit(md, activeAccountUser, getUserPrivateKey());
		} catch (Exception e) {
			TolvenLogger.info( "Unable to submit" + e.getMessage(), MenuAction.class);
		}
		return "success";
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	
	public void submitPersonal( ) {
		String patientPath = getTargetMenuPath().getSubPathWithIds("patient");
		TolvenLogger.info( "Account: " + getAccountId() + " Path: " + patientPath +" Name is: " + getGivenName(), MenuAction.class );
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<AccountExchange> getAccountExchangeSend() {
		if (providers==null) {
			providers = getAccountBean().findActiveEndPoints(getTop().getAccountUser().getAccount(), AccountExchange.Direction.SEND, false);
        }
		return providers;
	}

	public DataModel getProvidersModel() {
		if (providersModel==null) {
			providersModel = new ListDataModel();
			providersModel.setWrappedData(getAccountExchangeSend());
		}
		return providersModel;
	}

	/**
	 * Get the version number of this element
	 * @return
	 */
	public MenuDataVersion getMenuDataVersion() {
		if (menuDataVersion ==null ) {
			menuDataVersion = getMenuLocal().findMenuDataVersion( getAccountId(), getElement());
		}
		return menuDataVersion;
	}

	public void setMenuDataVersion(MenuDataVersion menuDataVersion) {
		this.menuDataVersion = menuDataVersion;
	}
	
	/**
	 * Return the attachments for the current drilldown item
	 * @return
	 * @throws Exception
	 */
	public List<DocAttachment> getDrilldownAttachments( ) throws Exception {
		DocBase doc = getDrilldownItemDoc();
		if (doc == null ) throw new Exception( "Document missing, cannot fetch attachments" );
		List<DocAttachment> attachments = getDocumentLocal().findAttachments( doc );
		return attachments;
	}
	
	public String deleteAttachment() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		long attId = Long.parseLong(params.get("attId"));
        getDocumentLocal().deleteAttachment( attId, getTop().getAccountUser());
		return "success";
	}
	/**
	 * Add an attachment to the current menudataItem 
	 * @return success
	 * @throws Exception 
	 */
	public String addAttachment() throws Exception {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	    FileItem upfile = (FileItem) request.getAttribute("upfile");
		if (upfile==null) return "fail";
        String contentType = upfile.getContentType();
        TolvenLogger.info( "Upload, contentType: " + contentType, MenuAction.class );
        boolean isInMemory = upfile.isInMemory();
        int sizeInBytes = (int)upfile.getSize();
    	DocBase doc = getDocumentLocal().createNewDocument(contentType, getAttachmentType(), getTop().getAccountUser());
    	doc.setSchemaURI(getAttachmentType());
		byte[] b;
        if (isInMemory) {
	        b = upfile.get();
        } else {
            InputStream uploadedStream = upfile.getInputStream();
            b = new byte[sizeInBytes];
            uploadedStream.read( b );
            uploadedStream.close();
        }
        String kbeKeyAlgorithm = getTolvenPropertiesBean().getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(getTolvenPropertiesBean().getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        doc.setAsEncryptedContent(b, kbeKeyAlgorithm, kbeKeyLength);
        getDocBean().createFinalDocument(doc );
        getDocumentLocal().createAttachment( getDrilldownItemDoc(), doc, getAttachmentDescription(), getTop().getAccountUser(), getNow());
        
        setAttachmentDescription(null);
        return "success";
	}

	public String getAttachmentDescription() {
		return attachmentDescription;
	}

	public void setAttachmentDescription(String attachmentDescription) {
		this.attachmentDescription = attachmentDescription;
	}

	public void setMenuToEditPathCtrl(UIInput menuToEditPathCtrl) {
		this.menuToEditPathCtrl = menuToEditPathCtrl;
	}

	public UIInput getMenuToEditPathCtrl() {
		return menuToEditPathCtrl;
	}

	public void setMenuToEditPath(String menuToEditPath) {
		this.menuToEditPath = menuToEditPath;
	}

	public String getMenuToEditPath() {
		return menuToEditPath;
	}

	public void setMenuToEdit(AccountMenuStructure menuToEdit) {
		this.menuToEdit = menuToEdit;
	}
	
	protected String getPath(){
		String path = null;
		if(null != menuToEditPathCtrl){
			path = (String)menuToEditPathCtrl.getValue();
		}
		if(path == null){
			path = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("menuPath");
		}
		setMenuToEditPath(path);
		return path;
	}
	
	public AccountMenuStructure getMenuToEdit() {
		if(null == menuToEdit){
			String path = getPath();
			if(path != null){
				menuToEdit = getMenuLocal().findAccountMenuStructure(getTop().getAccountId(), path);
				List<MSColumn> allCols = (List<MSColumn>) menuToEdit.getSortedColumns();
				menuToEditColumns = new ArrayList<MSColumn>();
				for(MSColumn col : allCols){
					if(col.getSequence() >= 0){
						menuToEditColumns.add(col);
					}
				}
			}
			if(menuToEdit != null)
				setVisible( menuToEdit.getVisible());
		}
		return menuToEdit;
	}
	
	public String editMenu() {
		getMenuToEdit();
		return "edit";
	}
	
	public void setMenuToEditColumns(List<MSColumn> cols){
		menuToEditColumns = cols;
	}

	public List<MSColumn> getMenuToEditColumns() {
		getMenuToEdit();
		return menuToEditColumns;
	}
	
	public List<MenuStructure> getVisibleChildren() {
		if(null == visibleChildren){
			getMenuToEdit();
			visibleChildren = new ArrayList<MenuStructure>();
			if(menuToEdit != null){
				for(MenuStructure child : menuToEdit.getSortedChildren()){
					if(child.getSequence() >= 0 ){
						visibleChildren.add( child);
					}
				}
			}
		}
		
		return visibleChildren;
	}

	public void setVisibleChildren(List<MenuStructure> visibleChildren) {
		this.visibleChildren = visibleChildren;
	}

	public List<String> getMenuAllowRoles() {
		if(menuAllowRoles == null){
			getMenuToEdit();
			menuAllowRoles = new ArrayList<String>();
			if(menuToEdit != null && null != menuToEdit.getAllowRoles()){
				String ar[] = menuToEdit.getAllowRoles().split(",");
				for(String role : ar){
					menuAllowRoles.add( role);
				}
			}
		}
		return menuAllowRoles;
	}

	public List<String> getMenuDenyRoles() {
		if(menuDenyRoles == null){
			getMenuToEdit();
			menuDenyRoles = new ArrayList<String>();
			if( null != menuToEdit.getDenyRoles()){
				String ar[] = menuToEdit.getDenyRoles().split(",");
				for(String role : ar){
					menuDenyRoles.add( role);
				}
			}
		}
		return menuDenyRoles;
	}
	
	public void setMenuAllowRoles(List<String> menuAllowRoles) {
		this.menuAllowRoles = menuAllowRoles;
	}

	public void setMenuDenyRoles(List<String> menuDenyRoles) {
		this.menuDenyRoles = menuDenyRoles;
	}

	public String getListAsString(List<String> list){
		String roles = new String();
		if(list != null){
			for(String r : list){
				if(roles.isEmpty()) roles = r;
				else roles += "," + r;
			}
		}
		return roles;
	}

	/*
	 * update single AccountMenuStructure. 
	 * Use the method in MenuBean that updates list of MS.
	 * set values as null for empty fields. This will reduce errors and extra check in MenuBean or
	 * other classes where these fields are checked only for null value.
	 *  
	 */
	public String updateMenuItem() {
		//send update to server
		if(menuToEdit.getTextOverride() == null || menuToEdit.getTextOverride().trim().isEmpty()){
			menuToEdit.setTextOverride(null);
		}
		if(menuToEdit.getTemplate()== null || menuToEdit.getTemplate().trim().isEmpty()){
			menuToEdit.setTemplate(null);
		}
		if(menuToEdit.getMenuTemplate() == null || menuToEdit.getMenuTemplate().trim().isEmpty()){
			menuToEdit.setMenuTemplate(null);
		}
		if(menuToEdit.getRepeating() == null || menuToEdit.getRepeating().trim().isEmpty()){
			menuToEdit.setRepeating(null);
		}
		menuToEdit.setAllowRoles(getListAsString( menuAllowRoles));
		menuToEdit.setDenyRoles( getListAsString( menuDenyRoles));
		if (visible==null || visible.trim().isEmpty()) {
			menuToEdit.setVisible(null);
		} else {
			menuToEdit.setVisible(visible);
		}
		
		List<MenuStructure> list = new ArrayList<MenuStructure>();
		list.add(menuToEdit);
		
		getMenuLocal().updateMenus(list);
		
		for(MSColumn col : menuToEditColumns){
			if(col.getTextOverride() == null || col.getTextOverride().trim().isEmpty()){
				col.setTextOverride(null);
			}
		}
		
		getMenuLocal().updateColumns(menuToEditColumns);
		
		return "success";
	}

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}	

	public String getHl7Message(){
		String hl7Message ="";
		try {
				Object message = getMenuDataItem().getField("hl7Message");
				if(message !=null){
					hl7Message = message.toString();
				}else{
					hl7Message = "To be Generated";
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hl7Message;
	}

	public List<SelectItem> getMvx(){
		List<SelectItem> mvxList= new ArrayList<SelectItem>();
		String mvxPath = "global:manufacturerMenu";
		MenuQueryControl ctrl = new MenuQueryControl();
		MenuPath reqPath = new MenuPath(mvxPath);
		ctrl.setAccountUser(getAccountUser());
		ctrl.setMenuStructurePath(mvxPath);
		ctrl.setRequestedPath(reqPath);
		MenuLocator menuLocator = getMenuLocal().findMenuLocator(mvxPath);
		ctrl.setMenuStructure( menuLocator.getMenuStructure());
		ctrl.getMenuStructure().getAccountMenuStructure().setQuery(mvxPath);
		List<MenuData>selectedMvxList = getMenuLocal().findMenuData(ctrl);
		if(selectedMvxList.size() >0){
			Iterator<MenuData> itrMvx = selectedMvxList.iterator();
			while(itrMvx.hasNext()){
				MenuData md = itrMvx.next();
				Object manufacturerInfo = md.getString04()+"~"+md.getString03();
				SelectItem option = new SelectItem(manufacturerInfo,md.getString04());
				mvxList.add(option);
			}
		}
		return mvxList;
	}

}