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
 * @author <your name>
 * @version $Id: ApplicationMetadata.java,v 1.17.2.7 2010/11/26 20:27:42 joseph_isaac Exp $
 */  

package org.tolven.app.bean;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.tolven.app.ApplicationMetadataLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuLocator;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.Status;
import org.tolven.doc.DocumentLocal;
import org.tolven.menuStructure.AccountProperty;
import org.tolven.menuStructure.Action;
import org.tolven.menuStructure.Application;
import org.tolven.menuStructure.Band;
import org.tolven.menuStructure.Calendar;
import org.tolven.menuStructure.Column;
import org.tolven.menuStructure.Entry;
import org.tolven.menuStructure.Extends;
import org.tolven.menuStructure.Instance;
import org.tolven.menuStructure.List;
import org.tolven.menuStructure.Menu;
import org.tolven.menuStructure.MenuBase;
import org.tolven.menuStructure.MenuEventHandler.Data;
import org.tolven.menuStructure.Placeholder;
import org.tolven.menuStructure.PlaceholderField;
import org.tolven.menuStructure.Portal;
import org.tolven.menuStructure.Portlet;
import org.tolven.menuStructure.PortletColumn;
import org.tolven.menuStructure.Timeline;
import org.tolven.menuStructure.TrimList;
import org.tolven.menuStructure.parse.ParseMenuStructure;
import org.w3c.dom.Element;

/**
 * Manage application metadata such as accountTypes, menustructure, placeholders, etc.
 * @author John Churin
 *
 */
@Local(ApplicationMetadataLocal.class)
@Stateless
public class ApplicationMetadata implements ApplicationMetadataLocal {
	public static final int SEQUENCE_MULTIPLIER = 100;
    public static final String APPLICATION_EXTENSION = ".application.xml";
	public static String[] reservedPlaceholderNames = {"now","account","trim","accountUser"};
	private @PersistenceContext EntityManager em;
	@EJB MenuLocal menuBean;
	@EJB AccountDAOLocal accountBean;
	@EJB DocumentLocal docBean;
    @Resource EJBContext ctx;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Create or update columns
	 * @param ms
	 * @param columns
	 */
	public void processColumns( AccountMenuStructure ms, java.util.List<Column> columns) {
		// Create or update each of the columns
		for (Column col : columns) {
			processColumn( col, ms );
		}
	}
	
	/**
	 * Find an existing column by name
	 * @param col
	 * @param ms
	 * @return
	 */
	public MSColumn findColumn( Column col, MenuStructure ms ) {
		for (MSColumn msColumn : ms.getColumns()) {
			if  (msColumn.getHeading().equals(col.getName())) {
				return msColumn;
			}
		}
		return null;
	}
	public void loadOutputFormats( MSColumn msColumn, java.util.List<Column.Output> outputFormats ) {
		// Load up the "output" modifiers
		StringBuffer sb = new StringBuffer( 128 );
		if (msColumn.getOutputFormat()!=null) {
			sb.append(msColumn.getOutputFormat());
		}
		for ( Column.Output outputFormat : outputFormats) {
			if (sb.length() > 0 ) sb.append('|');
			String gridType = outputFormat.getType();
			sb.append(gridType);
			sb.append(':');
			Element element = (Element) outputFormat.getAny().cloneNode(true);
			element = (Element) element.getOwnerDocument().renameNode(element, "", element.getNodeName());
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "no");
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult( writer);
				DOMSource source = new DOMSource( element ); 
				transformer.transform(source, result);
				sb.append(writer.toString());
			} catch (Exception e) {
				throw new RuntimeException( "Error parsing output XML in column " + msColumn.getHeading(), e);
			}

		}
		if (sb.length() > 0) {
			msColumn.setOutputFormat(sb.toString());
		}
	}
	
	public void loadFroms( MSColumn msColumn, java.util.List<String> froms ) {
		// Load up the "from" modifiers
		StringBuffer fsb = new StringBuffer( 128 );
//		if (msColumn.getFrom()!=null) {
//			fsb.append(msColumn.getFrom());
//		}
		for ( String from : froms) {
			if (fsb.length() > 0 ) fsb.append('|');
			fsb.append(from);
		}
		if (fsb.length() > 0) {
			msColumn.setFrom(fsb.toString());
		}
	}
	/**
	 * Create or update a MenuStructure column definition from the supplied XML column definition
	 * @param account
	 * @param col
	 * @param msParent
	 * @return
	 */
	public MSColumn processColumn( Column col, MenuStructure ms) {
		MSColumn msColumn = findColumn( col, ms );
		// Create a new columns
		if (msColumn==null) {
			msColumn = new MSColumn();
			msColumn.setMenuStructure(ms);
			msColumn.setSequence(ms.getColumns().size());
	        msColumn.setHeading(col.getName());
			ms.getColumns().add(msColumn);
		}
		if (col.getAlign()!=null) {
			msColumn.setAlign(col.getAlign().value());
		}
		if (col.getWidth()!=null) {
			msColumn.setWidth(col.getWidth());
		}
		if (col.getVisible()!=null) {
			msColumn.setVisible(col.getVisible());
		}
//		msColumn.setSupress(col.getSupress());
        if(col.getTitle() != null) {
            msColumn.setText(col.getTitle());
        }
        if(col.getType() != null) {
            msColumn.setDatatype(col.getType().name());
        }
        if (msColumn.getText()==null) {
            msColumn.setText(msColumn.getHeading());
        }
        if(col.getInternal() != null) {
        	String internal = col.getInternal();
    		if (col.isReference()) {
    			msColumn.setInternal("reference");
    			if (col.getFormat()!=null) {
    				msColumn.setDisplayFunction(col.getFormat());
    				msColumn.setDisplayFunctionArguments(internal);
    			} else {
    				msColumn.setDisplayFunction(internal);
    			}
    		} else if (col.isInstantiate()) {
    			msColumn.setInternal("instantiate");
    			msColumn.setDisplayFunction(col.getInternal());
    		} else {
    			msColumn.setInternal(internal);
    			msColumn.setDisplayFunction(col.getFormat());
    		}
        }
        loadFroms( msColumn, col.getFroms());
        loadOutputFormats( msColumn, col.getOutputs());
		return msColumn;
	}
	
	/**
	 * Find an existing placeholder field by name
	 * @param col
	 * @param ms
	 * @return
	 */
	public MSColumn findPlaceholderField( PlaceholderField field, MenuStructure ms ) {
		for (MSColumn msColumn : ms.getColumns()) {
			if  (msColumn.getHeading().equals(field.getName())) {
				return msColumn;
			}
		}
		return null;
	}
	
	/**
	 * Create or update a MenuStructure column definition from the supplied XML column definition
	 * @param account
	 * @param col
	 * @param msParent
	 * @return
	 */
	public MSColumn processPlaceholderField( AccountMenuStructure ms, PlaceholderField field ) {
		MSColumn msColumn = findPlaceholderField( field, ms );
		boolean isNew = false;
        if (msColumn==null) {
            isNew = true;
            msColumn = new MSColumn();
            msColumn.setMenuStructure(ms);
            msColumn.setSequence(ms.getColumns().size());
        }
        msColumn.setHeading(field.getName());
        msColumn.setText(msColumn.getHeading());
		String internal = field.getInternal();
		msColumn.setInternal(internal);
		// Load up the "from" modifiers
        loadFroms( msColumn, field.getFroms());
        if (isNew) {
            ms.getColumns().add(msColumn);
            em.persist(msColumn);
        }
//      logger.info( "Placeholder field Internal=" + msColumn.getInternal() + ", from=" + msColumn.getFrom());
        return msColumn;
	}
	
	public void processPlaceholderFields(AccountMenuStructure ms, java.util.List<PlaceholderField> fields ) {
		// Create each of the columns
		for (PlaceholderField field : fields) {
			processPlaceholderField( ms, field);
		}
	}
	
	/**
	 * Create an instance placeholder from the supplied XML sub-tree 
	 * @param account
	 * @param menu
	 * @param msParent
	 * @return
	 */
	public AccountMenuStructure processInstance( Account account, Instance instance, AccountMenuStructure msParent ) {
		AccountMenuStructure ms = resolveMenuStructure(account, instance, MenuStructure.EVENT, msParent);
		ms.setSequence(-1);
		if (instance.getPage()!=null) {
			ms.setTemplate(instance.getPage());
		}
		if (instance.getHeading()!=null) {
			ms.setMenuTemplate(instance.getHeading());
		}
		if (instance.getTitle()!=null) {
			ms.setText(instance.getTitle());
		}
		ms.setRepeating(instance.getName());
		// Visibility probably makes no sense for WIP
		if (instance.getVisible()!=null) {
			ms.setVisible(instance.getVisible());
		}
		// Don't consider this for default suffix
//		nominateDefaultSuffix( ms );
		return ms;
	}
	
	/**
	 * Process a list pane from the supplied XML sub-tree 
	 * @param account
	 * @param menu
	 * @param msParent
	 * @return The created or update MenuStructure object
	 *  
	 */
	public AccountMenuStructure processList( Account account, List list, AccountMenuStructure msParent, AccountMenuStructure msPlaceholder ) {
		AccountMenuStructure ms = resolveMenuStructure(account, list, MenuStructure.LIST, msParent);
		if (list.getPage()!=null) {
			ms.setTemplate(list.getPage());
		}
		if (list.getQuery()!=null) {
			ms.setQuery( list.getQuery() );
		}
		if (list.getFilter()!=null) {
			ms.setFilter( list.getFilter() );
		}
		if (list.getUniqueKey()!=null) {
			ms.setUniqueKey( list.getUniqueKey());
		}
		if (list.getInitialSort()!=null) {
			ms.setInitialSort( list.getInitialSort() );
		}
		if (list.getTitle()!=null) {
			ms.setText(list.getTitle());
		}
		if (list.getDrilldown()!=null) {
			if (list.getDrilldown().startsWith(":")) {
				if (msPlaceholder!=null) {
					ms.setRepeating(fullPath(msPlaceholder) + list.getDrilldown());
				} else {
					throw new IllegalStateException( "Missing parent placeholder for relative drilldown path" + list.getDrilldown());
				}
			} else {
				ms.setRepeating(list.getDrilldown());
			}
		}
		if (list.getVisible()!=null) {
			ms.setVisible(list.getVisible());
		}
		processColumns( ms, list.getColumns());
		processActions( account, ms, list.getActions() );
		nominateDefaultSuffix( ms );
		return ms;
	}
	
	/**
	 * Create the special placeholder used by Trim Menus. If the placeholder already exists,
	 * then just return it.
	 * @param placeholder name of placeholder node
	 * @param root
	 * @return
	 */
	public AccountMenuStructure createTrimPlaceholder(String placeholder, AccountMenuStructure root ) {
		for (AccountMenuStructure ms : root.getChildren()) {
			if (ms.getNode().equals(placeholder)) return ms;
		}
		throw new RuntimeException( "Missing Trim placeholder for Trim list: " + placeholder );
	}
	
	/**
	 * Create a TrimList pane from the supplied XML sub-tree 
	 * @param account
	 * @param menu
	 * @param msParent
	 * @return
	 */
	public AccountMenuStructure processTrimList( Account account, TrimList list, AccountMenuStructure msParent ) {
		AccountMenuStructure ms = resolveMenuStructure(account, list, MenuStructure.LIST, msParent);
		if (list.getPage()!=null) {
			ms.setTemplate(list.getPage());
		}
		if (list.getTitle()!=null) {
			ms.setText(list.getTitle());
		}

		if (list.getInitialSort()!=null) {
			ms.setInitialSort( list.getInitialSort() );
		}
		
		if (list.getPlaceholder()!=null) {
			ms.setQuery(list.getPlaceholder());
		}
		if (ms.getQuery()==null) throw new RuntimeException( "Placeholder path missing in " + ms.getPath());
		if (list.getVisible()!=null) {
			ms.setVisible(list.getVisible());
		}
		processColumns( ms, list.getColumns());
		nominateDefaultSuffix( ms );
		return ms;
	}

	/**
	 * Create an Action button backed by a trim list.
	 * This has some improvements over createTrimList so we'll try to obsolete trimList at some point. 
	 * @param account
	 * @param menu
	 * @param msParent
	 * @return
	 *  
	 */
	public AccountMenuStructure processAction( Account account, Action action, AccountMenuStructure msParent ) {
		AccountMenuStructure ms = resolveMenuStructure(account, action, MenuStructure.ACTION, msParent);
		if (action.getPage()!=null) {
			ms.setTemplate(action.getPage());
		}
		if (action.getTitle()!=null) {
			ms.setText(action.getTitle());
		}
		if (action.getVisible()!=null) {
			ms.setVisible(action.getVisible());
		}
		if (action.getQuery()!=null) {
			ms.setQuery(action.getQuery());
		}
		if (ms.getQuery()==null) throw new IllegalStateException( "Query string required on action");

		try {
	        if(action.getMenuEventHandler() == null) {
	            ms.setMenuEventHandlerFactory(null);
	            ms.setMenuEventHandlerDataMap(null);
	        } else {
	            ms.setMenuEventHandlerFactory(action.getMenuEventHandler().getFactory());
	            Properties properties = new Properties();
	            for(Data data : action.getMenuEventHandler().getDatas()) {
	                properties.put(data.getName(), data.getValue());
	            }
	            ms.setMenuEventHandlerDataMap(properties);
	        }
		} catch (IOException e) {
			throw new RuntimeException("Error setting up Action " + action.getName(), e);
		}
		processColumns( ms, action.getColumns());
		nominateDefaultSuffix( ms );
		return ms;
	}

	/**
	 * Recursively build the path string from the MS hierarchy above us
	 * @param ms
	 * @return
	 */
	public static String fullPath( MenuStructure ms ) {
		StringBuffer path = new StringBuffer();
		if (ms.getParent()!=null) {
			path.append(fullPath( ms.getParent()));
			path.append(":");
		}
		path.append(ms.getNode());
		return path.toString();
	}
	
	/**
	 * Create a portlet from the supplied XML sub-tree 
	 * @param portal Parent of the portlet
	 * @param msParent
	 * @return
	 */
	public AccountMenuStructure processPortlet( Account account, Portlet portlet, AccountMenuStructure msParent, AccountMenuStructure msPlaceholder ) {
		if (msParent==null || !MenuStructure.TAB.equals(msParent.getRole())) {
			throw new RuntimeException( "Portlet (" + portlet.getName() + ") parent must be a portal" );
		}
		AccountMenuStructure ms = resolveMenuStructure(account, portlet, MenuStructure.PORTLET, msParent);
		if (portlet.getPage()!=null) {
			ms.setTemplate(portlet.getPage());
		}
		if (portlet.getTitle()!=null) {
			ms.setText(portlet.getTitle());
		}
		if (portlet.getFilter()!=null) {
			ms.setFilter( portlet.getFilter() );
		}
		if (portlet.getInitialSort()!=null) {
			ms.setInitialSort( portlet.getInitialSort() );
		}
		if (portlet.getQuery()!=null) {
			ms.setQuery( portlet.getQuery());
		}
		if (msPlaceholder!=null && portlet.getDrilldown()!=null) {
			if (portlet.getDrilldown().startsWith(":")) {
				if (msPlaceholder!=null) {
					ms.setRepeating(fullPath(msPlaceholder) + portlet.getDrilldown());
				} else {
					throw new IllegalStateException( "Missing parent placeholder for relative drilldown path" + portlet.getDrilldown());
				}
			} else {
				ms.setRepeating(portlet.getDrilldown());
			}
		}
		if (portlet.getVisible()!=null) {
			ms.setVisible(portlet.getVisible());
		}
		if (portlet.getPortalColumn()!=null) {
			ms.setColumnNumber(portlet.getPortalColumn().intValue());
		}
		processColumns( ms, portlet.getColumns());
		processActions( account, ms, portlet.getActions() );
		nominateDefaultSuffix( ms );
		return ms;
	}
	
	public void processActions( Account account, AccountMenuStructure msParent, java.util.List<Action> actions ) {
		int actionSeq = 0;
		// Create actions, if any
		for (Action action : actions) {
			processAction( account, action, msParent);
		}
		
	}

	/**
	 * Create a portal pane from the supplied XML subtree 
	 * @param account
	 * @param menu
	 * @param msParent
	 * @return
	 */
	public AccountMenuStructure processPortal( Account account, Portal portal, AccountMenuStructure msParent,AccountMenuStructure msPlaceholder ) {
		AccountMenuStructure ms = resolveMenuStructure(account, portal, MenuStructure.TAB, msParent);
		if (portal.getPage()!=null) {
			ms.setTemplate(portal.getPage());
		}
		if (portal.getTitle()!=null) {
			ms.setText(portal.getTitle());
		}
		if (portal.getVisible()!=null) {
			ms.setVisible(portal.getVisible());
		}
		int columnNumber = 0;
		// Create each of the portlets in this portal
		for (Object child : portal.getPortletColumns()) {
			if (child instanceof PortletColumn) {
				columnNumber++;
				for (Portlet p : ((PortletColumn)child).getPortlets()) {
					AccountMenuStructure msPortlet = processPortlet( account, p, ms, msPlaceholder);
					// Portlet can specify columnNumber but if not, then specify it here.
					if (msPortlet.getColumnNumber()==null) {
						msPortlet.setColumnNumber(columnNumber);
					}
				}
			}
		}
		nominateDefaultSuffix( ms );
		return ms;
	}
	
	/**
	 * Create a portal pane from the supplied XML subtree 
	 * @param account
	 * @param menu
	 * @param msParent
	 * @return
	 */
	public AccountMenuStructure processTimeline( Account account, Timeline timeline, AccountMenuStructure msParent,AccountMenuStructure msPlaceholder ) {
		AccountMenuStructure ms = resolveMenuStructure(account, timeline, MenuStructure.TIMELINE, msParent);
		if (timeline.getPage()!=null) {
			ms.setTemplate(timeline.getPage());
		}
		if (timeline.getTitle()!=null) {
			ms.setText(timeline.getTitle());
		}
		if (timeline.getVisible()!=null) {
			ms.setVisible(timeline.getVisible());
		}
		// Create each of the Band in this Timeline
		for (Band b : timeline.getBands()) {
			processBand( account, b, ms, msPlaceholder);
		}
		nominateDefaultSuffix( ms );
		return ms;
	}

	/**
	 * Create a Calendar pane from the supplied XML subtree 
	 * @param account
	 * @param menu
	 * @param msParent
	 * @return
	 *  
	 */
	public AccountMenuStructure processCalendar( Account account, Calendar calendar, AccountMenuStructure msParent, AccountMenuStructure msPlaceholder ) {
		AccountMenuStructure ms = resolveMenuStructure(account, calendar, MenuStructure.CALENDAR, msParent);
		if (calendar.getPage()!=null) {
			ms.setTemplate(calendar.getPage());
		}
		if (calendar.getTitle()!=null) {
			ms.setText(calendar.getTitle());
		}
		if (calendar.getVisible()!=null) {
			ms.setVisible(calendar.getVisible());
		}
		// Create each of the Band in this Timeline
		for (Entry e : calendar.getEntries()) {
			processEntry( account, e, ms, msPlaceholder);
		}
		nominateDefaultSuffix( ms );
		return ms;
	}

	/**
	 * Create a Calendar entry from the supplied XML sub-tree 
	 * @param entry The entry
	 * @param msParent Parent of the entry
	 * @param Placeolder context
	 * @return
	 */
	public AccountMenuStructure processEntry( Account account, Entry entry, AccountMenuStructure msParent, AccountMenuStructure msPlaceholder ) {
		if (msParent==null || !MenuStructure.CALENDAR.equals(msParent.getRole())) {
			throw new RuntimeException( "Entry (" + entry.getName() + ") parent must be a calendar" );
		}
		AccountMenuStructure ms = resolveMenuStructure(account, entry, MenuStructure.ENTRY, msParent);
		if (entry.getPage()!=null) {
			ms.setTemplate(entry.getPage());
		}
		if (entry.getTitle()!=null) {
			ms.setText(entry.getTitle());
		}
		if (entry.getQuery()!=null) {
			ms.setQuery( entry.getQuery());
		}
		if (msPlaceholder!=null && entry.getDrilldown()!=null) {
			if (entry.getDrilldown().startsWith(":")) {
				if (msPlaceholder!=null) {
					ms.setRepeating(fullPath(msPlaceholder) + entry.getDrilldown());
				} else {
					throw new IllegalStateException( "Missing parent placeholder for relative drilldown path" + entry.getDrilldown());
				}
			} else {
				ms.setRepeating(entry.getDrilldown());
			}
		}
		if (entry.getVisible()!=null) {
			ms.setVisible(entry.getVisible());
		}
		processColumns( ms, entry.getColumns());
		processActions( account, ms, entry.getActions() );
		nominateDefaultSuffix( ms );
		return ms;
	}
		
	/**
	 * Create a Timeline Band from the supplied XML sub-tree 
	 * @param band The band
	 * @param msParent Parent of the band
	 * @param Placeolder context
	 * @return
	 *  
	 */
	public AccountMenuStructure processBand( Account account, Band band, AccountMenuStructure msParent, MenuStructure msPlaceholder ) {
		if (msParent==null || !MenuStructure.TIMELINE.equals(msParent.getRole())) {
			throw new RuntimeException( "Band (" + band.getName() + ") parent must be a timeline" );
		}
		AccountMenuStructure ms = resolveMenuStructure(account, band, MenuStructure.BAND, msParent);
		if (band.getPage()!=null) {
			ms.setTemplate(band.getPage());
		}
		if (band.getTitle()!=null) {
			ms.setText(band.getTitle());
		}
		if (band.getQuery()!=null) {
			ms.setQuery( band.getQuery());
		}
		if (band.getInterval()!=null) {
			ms.setInterval( band.getInterval().toString());
		}
		if (band.getStyle()!=null) {
			ms.setStyle(band.getStyle());
		}
		if (msPlaceholder!=null && band.getDrilldown()!=null) {
			if (band.getDrilldown().startsWith(":")) {
				if (msPlaceholder!=null) {
					ms.setRepeating(fullPath(msPlaceholder) + band.getDrilldown());
				} else {
					throw new IllegalStateException( "Missing parent placeholder for relative drilldown path" + band.getDrilldown());
				}
			} else {
				ms.setRepeating(band.getDrilldown());
			}
		}
		if (band.getVisible()!=null) {
			ms.setVisible(band.getVisible());
		}
		processColumns( ms, band.getColumns());
		processActions( account, ms, band.getActions() );
		nominateDefaultSuffix( ms );
		return ms;
	}

	/**
	 * Determine if a node is eligible to participate in a default display path 
	 * @param ms
	 * @return true if node is eligible
	 */
	protected boolean elibibleForDefault( AccountMenuStructure ms ) {
		if (MenuStructure.PLACEHOLDER.equals(ms.getRole())) return false;
		if (MenuStructure.VISIBLE_NEVER.equals(ms.getVisible())) return false;
		if (MenuStructure.VISIBLE_FALSE.equals(ms.getVisible())) return false;
		return true;
	}
	/**
	  * If this ancestor is already setup but we have a higher sequence than the one already nominated,
	  * then we need to override.
	 * @return
	 */
	protected boolean replaceableSuffix( AccountMenuStructure msParent, String candidate) {
		if (msParent.getDefaultPathSuffix()!=null) {
			String parentPaths[] = msParent.getDefaultPathSuffix().split("\\:");
			String siblingPath = msParent.getPath()+":"+parentPaths[1];
			MenuStructure msSibling = menuBean.findAccountMenuStructure(msParent.getAccount().getId(), siblingPath);
			String candidatePaths[] = candidate.split("\\:");
			StringBuffer candidateSB = new StringBuffer();
			for (int x = 0; x < msSibling.getLevel();x++) {
				if (candidateSB.length()>0) {
					candidateSB.append(':');
				}
				candidateSB.append(candidatePaths[x]);
			}
			MenuStructure msCandidate = menuBean.findAccountMenuStructure(msParent.getAccount().getId(), candidateSB.toString());
			if (msSibling.getSequence() > msCandidate.getSequence()) {
				return true;
			}
		}
		return false;
	}
		
	/**
	 * A leaf node will usually nominate itself as the default for its ancestors unless
	 * one has already been nominated or if the one already nominated is sequenced after this one. 
	 * Portlets and Actions are not considered.
	 * A suffix applies to interior nodes. For example, if a leaf node is <code>echr:patients:all</code> 
	 * and its ancestor is <code>echr</code>, then suffix is <code>:patients:all</code>.
	 * While a placeholder will receive a default suffix, it will not be considered as a default for it's ancestors
	 * because it's display is controlled at runtime.
	 */
	public void nominateDefaultSuffix( AccountMenuStructure ms ) {
		if (MenuStructure.PORTLET.equals(ms.getRole())) return;
		if (MenuStructure.ACTION.equals(ms.getRole())) return;
		if (!elibibleForDefault( ms)) return;
		AccountMenuStructure msParent = ms.getParent();
		while (msParent!=null) {
			// Get candidate suffix
			String suffix = ms.getPath().substring(msParent.getPath().length());
			if (replaceableSuffix( msParent, ms.getPath() )) {
				msParent.setDefaultPathSuffix(null);
			}
			// See if ancestor is something we can improve on
			if (msParent.getDefaultPathSuffix()==null || suffix.startsWith(msParent.getDefaultPathSuffix())) {
				msParent.setDefaultPathSuffix(suffix);
			}
			// Stop after we see a node which would never be a default for whatever it's parent is
			if (!elibibleForDefault( msParent)) break;
			// Move up the chain
			msParent = msParent.getParent();
		}
	}
	
	/**
	 * Find or create a menuStructure entry so that we can add attributes and children to it
	 * @param account
	 * @param name
	 * @param role
	 * @param msParent
	 * @return
	 */
	public AccountMenuStructure resolveMenuStructure( Account account, MenuBase menu, String role, AccountMenuStructure msParent ) {
		String path;
		if (msParent!=null) {
			path = msParent.getPath() + ":" + menu.getName();
		} else {
			path = menu.getName();
		}
		// See if it already exists
		AccountMenuStructure ms = findAccountMenuStructure( account, path );
		// If not, create it
		if (ms==null) {
			ms = new AccountMenuStructure( );
			ms.setAccount(account);
			ms.setNode(menu.getName());
			ms.setRole(role);
			// Make us part of our parent
			if (msParent!=null) {
				msParent.getChildren().add(ms);
				if (menu.getSequence()!=null) {
					ms.setSequence(menu.getSequence().intValue());
				} else {
					ms.setSequence(SEQUENCE_MULTIPLIER*msParent.getChildren().size());
				}
				ms.setParent(msParent);
			} else {
				ms.setSequence(SEQUENCE_MULTIPLIER*1);
			}
			em.persist(ms);
		} else {
			// For an update, verify that the Role has not changed
			if (!role.equals(ms.getRole())) {
				throw new RuntimeException( role + " (" + path + ") already exists as " + ms.getRole());
			}
		}
		logger.info( "account " + account.getId() + " (" + account.getAccountType().getKnownType() + ") path " + path );
		return ms;
	}
	
	/**
	 * Add information from menu definition
	 * @param account
	 * @param msParent
	 * @return
	 */
	public AccountMenuStructure  processMenu( Account account, Menu menu, AccountMenuStructure msParent, AccountMenuStructure msPlaceholder ) {
		AccountMenuStructure ms = resolveMenuStructure( account, menu, MenuStructure.TAB, msParent );
		if (menu.getPage()!=null) {
			ms.setTemplate(menu.getPage());
		}
		if (menu.getTitle()!=null) {
			ms.setText(menu.getTitle());
		}
		if (menu.getVisible()!=null) {
			ms.setVisible(menu.getVisible());
		}
		for (MenuBase menuChild : menu.getMenusAndPortalsAndTimelines()) {
			if (menuChild instanceof Portal) {
				processPortal( ms.getAccount(), (Portal)menuChild, ms, msPlaceholder);
			} else if (menuChild instanceof Menu) {
				processMenu( ms.getAccount(), (Menu) menuChild, ms, msPlaceholder);
			} else if (menuChild instanceof List) {
				processList( ms.getAccount(), (List)menuChild, ms, msPlaceholder);
			} else if (menuChild instanceof Timeline) {
				processTimeline( ms.getAccount(), (Timeline)menuChild, ms, msPlaceholder );
			} else if (menuChild instanceof Calendar) {
				processCalendar( ms.getAccount(), (Calendar)menuChild, ms, msPlaceholder );
			} else if (menuChild instanceof TrimList) {
				processTrimList( ms.getAccount(), (TrimList)menuChild, ms);
			} else if (menuChild instanceof Instance) {
				processInstance( ms.getAccount(), (Instance)menuChild, ms );
			} else {
				throw new RuntimeException( "Invalid subclass of MenuBase");
			}
		}
		nominateDefaultSuffix( ms );
		return ms;
	}
	/**
	 * Create a new Account menuStructure, special purpose for TrimMenus.
	 * @param path
	 * @return The new accountMenuStructure
	 */
	public AccountMenuStructure createAccountMenuStructure(String path, String msRole ) {
		MenuPath menuPath = new MenuPath( path );
		Account account = null;
		AccountMenuStructure msParent = null;
		AccountMenuStructure ms = null;
		for ( String node : menuPath.getNodes()) {
			// First node must be accountType, create a new account
			// under that accountType
			if (account==null) {
				account = accountBean.createAccount(node, path, null);
				msParent = ms;
			}
			ms = new AccountMenuStructure();
			ms.setAccount(account);
			ms.setParent(msParent);
			ms.setNode(node);
			if (msParent==null) {
				ms.setRole(MenuStructure.TAB);
			} else {
				ms.setRole(msRole);
			}
			ms.setVisible(MenuStructure.VISIBLE_NEVER);
			em.persist(ms);
		}
		return ms;
	}
	
	/**
	 * Find a MenuLocation. This is not the same as the method in MenuBean. This method will return
	 * null if the MenuLocator is not found.
	 * @param name
	 * @return MenuLocator or null
	 */
    public MenuLocator findMenuLocator( String name ) {
        Query query = em.createQuery("SELECT md FROM MenuLocator md " + "WHERE md.path = :p");
        query.setParameter("p", name);
        java.util.List<MenuLocator> menuLocators = query.getResultList();
        if (menuLocators.size()==1) return menuLocators.get(0);
        return null;
    }
	
	/**
	 * Create or update TRIM menu (placeholders)
	 * Notice that trim menus are not associated with a specific account (although they are created within an invisible account)
	 * @param trimMenus
	 */
	public void processTrimMenus( java.util.List<Placeholder> trimMenus ) {
		for (Placeholder placeholder : trimMenus) {
			String menuName = placeholder.getName();
			AccountMenuStructure ams;
			MenuLocator menuLocator = findMenuLocator(menuName);
			if (menuLocator==null) {
				menuLocator = new MenuLocator();
				menuLocator.setPath(menuName);
				ams = createAccountMenuStructure(menuName, MenuStructure.TRIMLIST);
				menuLocator.setMenuStructure(ams);
				em.persist(menuLocator);
			} else {
				ams = menuLocator.getMenuStructure();
			}
			// Update TrimMenu attributes
			if (placeholder.getInitialSort()!=null) {
				ams.setInitialSort(placeholder.getInitialSort());
			}
			processPlaceholderFields( menuLocator.getMenuStructure(), placeholder.getFields());
		}
	}
	
	/**
	 * Find a menuStructure entry or return a null if not found. This is a special-purpose query used 
	 * when loading metadata.
	 * @param msParent
	 * @param node
	 * @return The AccountMenuStructure
	 */
	public AccountMenuStructure findAccountMenuStructure( Account account, String placeholderPath ){
		Query query = em.createQuery("SELECT am FROM AccountMenuStructure am WHERE am.account = :account and am.path = :p");
		query.setParameter("account", account );
		query.setParameter("p", placeholderPath);
		java.util.List<AccountMenuStructure> msList =  query.getResultList();
		if (msList.size()==0) {
			return null;
		} else {
			return msList.get(0);
		}
	}
	
	/**
	 * See if a placeholder node name is unique within an account. For example, echr:patient and echr:physician:patient would be a duplication since
	 * patient must identify a placeholder regardless of where it exists in the hierarchy.
	 * @param ms
	 * @return true if more than on 
	 */
	public boolean placeholderDuplicate( MenuStructure ms ) {
		em.flush();
		Query query = em.createQuery("SELECT am FROM AccountMenuStructure am " +
				"WHERE am.account = :account " +
				"AND am.role = :role " +
				"AND am.node = :node");
		query.setParameter("account", ms.getAccount() );
		query.setParameter("role", MenuStructure.PLACEHOLDER);
		query.setParameter("node", ms.getNode());
		java.util.List<AccountMenuStructure> msList =  query.getResultList();
		if (msList.size()==1) {
			return false;
		} else {
			return true;
		}
	}

    /**
     * Create a placeholder.
     * Placeholders represent the "entities" in the 
     * application such as patient, medications, etc.
     * Within an account(type), placeholder names must be unique, regardless of the hierarchy.
     * @param msMenuRoot root of menu hierarchy
     * @param placeholder The placeholder from XML
     * @param msParent the parent placeholder, if any.
     *  
     */
    public AccountMenuStructure processPlaceholder( AccountMenuStructure msMenuRoot, Placeholder placeholder, MenuStructure msParent ) {
        String placeholderName = placeholder.getName();
        if (placeholderName==null || placeholderName.length()==0) {
            throw new RuntimeException( "Missing placeholder name");
        }
        // Name cannot be a reserved word
        for (String reserved : reservedPlaceholderNames) {
            if (reserved.equals(placeholderName)) {
                throw new RuntimeException( "Placeholder name is reserved: " + placeholderName);
            }
        }
        // Look for an existing placeholder
        String placeholderPath;
        if (msParent==null) {
            placeholderPath = msMenuRoot.getNode() + ":" + placeholderName;
        } else {
            placeholderPath = msParent.getPath() + ":" + placeholderName;
        }
        AccountMenuStructure ms = findAccountMenuStructure( msMenuRoot.getAccount(), placeholderPath );
        if (ms==null) {
            ms = new AccountMenuStructure( );
            ms.setAccount(msMenuRoot.getAccount());
            ms.setNode(placeholderName);
            ms.setRole(MenuStructure.PLACEHOLDER);
            if (msParent==null) {
                ms.setParent(msMenuRoot);
            } else {
                ms.setParent(msParent);
            }
            // Persist before doing dupe check
            em.persist(ms);
        } else {
            if (!MenuStructure.PLACEHOLDER.equals(ms.getRole())) {
                throw new RuntimeException( "Path to placeholder " + msMenuRoot.getPath() + ":" + placeholderName + " is already a " + ms.getRole());
            }
        }
        if (msParent!=null) {
            // This must occur after node (name) is set
            msParent.getChildren().add(ms);
            ms.setParent(msParent);
        } else {
            // Top-level placeholders actually fall right under the root menu - they are not freestanding.
            // This allows placeholder to have a root path node that is similar to the menu structure.
            msMenuRoot.getChildren().add(ms);
            ms.setParent(msMenuRoot);
        }
        if (placeholder.getSequence()!=null) {
            ms.setSequence(placeholder.getSequence().intValue());
        } else {
            if( msParent!=null) {
                ms.setSequence(SEQUENCE_MULTIPLIER*msParent.getChildren().size());
            } else {
                ms.setSequence(SEQUENCE_MULTIPLIER*1);
            }
        }
        // See if there is more than one placeholder with this name
        if (placeholderDuplicate( ms )) {
            throw new RuntimeException( "Placeholder name must be unique with an account type: " + placeholderName );
        }
        if (placeholder.getPage()!=null) {
            ms.setTemplate(placeholder.getPage());
        }
        if (placeholder.getHeading()!=null) {
            ms.setMenuTemplate(placeholder.getHeading());
        }
        if (placeholder.getTitle()!=null) {
            ms.setText(placeholder.getTitle());
        }
        if (placeholder.getName()!=null) {
            ms.setRepeating(placeholder.getName());
        }
        if (placeholder.getEventInstance()!=null) {
            ms.setEventPath(placeholder.getEventInstance());
        }
        // Visibility probably makes no sense for a placeholder
        if (placeholder.getVisible()!=null) {
            ms.setVisible(placeholder.getVisible());
        }
        for (MenuBase menuChild : placeholder.getMenusAndPortalsAndTimelines()) {
            if (menuChild instanceof Portal) {
                processPortal( msMenuRoot.getAccount(), (Portal)menuChild, ms, ms);
            } else if (menuChild instanceof Menu) {
                processMenu( msMenuRoot.getAccount(), (Menu) menuChild, ms, ms);
            } else if (menuChild instanceof List) {
                processList( msMenuRoot.getAccount(), (List)menuChild, ms, ms);
            } else if (menuChild instanceof Timeline) {
                processTimeline( ms.getAccount(), (Timeline)menuChild, ms, ms );
            } else if (menuChild instanceof TrimList) {
                processTrimList( msMenuRoot.getAccount(), (TrimList)menuChild, ms );
            } else if (menuChild instanceof Instance) {
                processInstance( msMenuRoot.getAccount(), (Instance)menuChild, ms );
            } else {
                throw new RuntimeException( "Invalid subclass of MenuBase");
            }
        }
        processPlaceholderFields( ms, placeholder.getFields());
        // Create the placeholder hierarchy, if any
        for ( Placeholder child : placeholder.getPlaceholders()) {
            AccountMenuStructure childPlaceholder = processPlaceholder( msMenuRoot, child, ms );
//          childPlaceholder.setSequence(-1);
        }
        nominateDefaultSuffix( ms );
        return ms;
    }

    /**
     * Create a placeholder.
     * Placeholders represent the "entities" in the 
     * application such as patient, medications, etc.
     * Within an account(type), placeholder names must be unique, regardless of the hierarchy.
     * @param name
     * @param parent
     * @param account
     * @return
     */
    public AccountMenuStructure createPlaceholder(String name, String parent, Account account) {
        if (parent == null || parent.length() == 0) {
            throw new RuntimeException("Missing placeholder parent name");
        }
        if (name == null || name.length() == 0) {
            throw new RuntimeException("Missing placeholder name");
        }
        // Name cannot be a reserved word
        for (String reserved : reservedPlaceholderNames) {
            if (reserved.equals(name)) {
                throw new RuntimeException("Placeholder name is reserved: " + name);
            }
        }
        MenuStructure placeholderParent = getMenuBean().findMenuStructure(account, parent);
        // Look for an existing placeholder
        String placeholderPath = placeholderParent.getNode() + ":" + name;
        AccountMenuStructure ms = findAccountMenuStructure(account, placeholderPath);
        if (ms == null) {
            ms = new AccountMenuStructure();
            ms.setAccount(account);
            ms.setNode(name);
            ms.setRole(MenuStructure.PLACEHOLDER);
            ms.setParent(placeholderParent);
            // Persist before doing dupe check
            em.persist(ms);
        } else {
            if (!MenuStructure.PLACEHOLDER.equals(ms.getRole())) {
                throw new RuntimeException("Path to placeholder " + placeholderParent.getPath() + ":" + name + " is already a " + ms.getRole());
            }
        }
        // Top-level placeholders actually fall right under the root menu - they are not freestanding.
        // This allows placeholder to have a root path node that is similar to the menu structure.
        placeholderParent.getChildren().add(ms);
        ms.setParent(placeholderParent);
        // See if there is more than one placeholder with this name
        if (placeholderDuplicate(ms)) {
            throw new RuntimeException("Placeholder name must be unique with an account type: " + name);
        }
        return ms;
    }
    
	/**
	 * Using the root element (Application) as a start, build a new
	 * or update an existing account type and return the accountTemplate under which this metadata will be stored.
	 * This method should not be called if no name attribute is included in the root element. 
	 * @param app
	 * @return the TemplateAccount 
	 */
	public Account updateAccountType( Application app) {
        Query q = em.createQuery("SELECT at FROM AccountType at WHERE at.knownType = :knownType "); 
        q.setParameter("knownType", app.getName());
        java.util.List<AccountType> accountTypes = q.getResultList();
		AccountType accountType;
        if (accountTypes.size()==0) {
			logger.info( "Creating new AccountType: " + app.getName());
			accountType = getAccountBean().createAccountType(app.getName());
			accountType.setKnownType(app.getName());
			accountType.setReadOnly(false);
			accountType.setCreatable(true);
		} else {
			logger.info( "Updating existing AccountType: " + app.getName());
			accountType = accountTypes.get(0);
		}
		accountType.setCSS(app.getCss());
		accountType.setHomePage(app.getHomePage());
		accountType.setLogo(app.getLogo());
		accountType.setLongDesc(app.getTitle());
		accountType.setCreatable(app.isCreatable());
		accountType.setCreateAccountPage(app.getCreateAccountPage());
		if (!Status.ACTIVE.value().equalsIgnoreCase(accountType.getStatus())) {
			accountType.setStatus(Status.ACTIVE.value());
			logger.info("Reactivating AccountType " + accountType.getKnownType());
		}
		getAccountBean().updateAccountType(accountType);
		Account templateAccount = newTemplateAccount( accountType );
		// Now make sure the account type points to this new templateAccount
		getAccountBean().setAccountTemplate(accountType.getKnownType(), templateAccount);
		
		return templateAccount;
	}
	
	/**
	 * When loading new menu structure, a new "template" account is always created, even for a minor change.
	 * This new account is set in the AccountType as a template. Users then have some control
	 * over when they accept metadata updates. (The templateAccount is used to update individual
	 * real accounts). New accounts immediately use the current template Account. 
	 * Existing accounts either update upon login or on request by calling menuBean.updateMenuStructure(). 
	 * @return a new Account
	 */
	public Account newTemplateAccount( AccountType accountType ) {
		try {
			Account account = getAccountBean().createAccount2("Template for " + accountType.getKnownType() + " AccountType", null, accountType);
			logger.info( "Created new Template Account: " + account.getId());
			return account;
		} catch (Exception e) {
			throw new RuntimeException( "Error creating template account", e);
		}
	}
	/**
	 * Process one resolved extend element. The account type and path are known and valid.
	 * @param ext
	 * @param knownType
	 * @param templateAccount
	 */
	protected void processExtend( Extends ext, String knownType, String path ) {
		Account templateAccount;
		try {
			templateAccount = getAccountBean().findAccountTemplate( knownType );
		} catch (Exception e1) {
			throw new RuntimeException( "Unknown Account Type (" + knownType+ ") in extends path " + path, e1);
		}
		AccountMenuStructure msParent;
		try {
			MenuStructure ms = getMenuBean().findMenuStructure(templateAccount, path);
			msParent = ms.getAccountMenuStructure();
		} catch (Exception e1) {
			throw new RuntimeException( "Unable to extend " + path, e1);
		}
		AccountMenuStructure msPlaceholder = msParent;
		// Find immediate placeholder ancestor, if any
		while (msPlaceholder!=null) {
			if (MenuStructure.PLACEHOLDER.equals(msPlaceholder.getRole())) {
				break;
			}
			msPlaceholder = msPlaceholder.getParent();
		}
		if ( ext.getBand()!=null ) {
			processBand(templateAccount, ext.getBand(), msParent, msPlaceholder);
		} else if ( ext.getCalendar()!=null ) {
			processCalendar(templateAccount, ext.getCalendar(), msParent, msPlaceholder);
		} else if ( ext.getInstance()!=null ) {
			processInstance(templateAccount, ext.getInstance(), msParent);
		} else if ( ext.getList()!=null ) {
			processList(templateAccount, ext.getList(), msParent, msPlaceholder);
		} else if ( ext.getMenu()!=null ) {
			processMenu(templateAccount, ext.getMenu(), msParent, msPlaceholder);
		} else if ( ext.getPlaceholder()!=null ) {
			try {
				MenuStructure msMenuRoot = getMenuBean().findMenuStructure(templateAccount, knownType);
				processPlaceholder(msMenuRoot.getAccountMenuStructure(), ext.getPlaceholder(), msParent);
			} catch (Exception e) {
				String placeholderName = ext.getPlaceholder().getName();
				throw new RuntimeException( "Unable to create placeholder " + placeholderName + " at " + path /* + " at "  + ext.sourceLocation()*/, e);
			}
		} else if ( ext.getPortal()!=null ) {
			processPortal(templateAccount, ext.getPortal(), msParent, msPlaceholder);
		} else if ( ext.getPortlet()!=null ) {
			processPortlet(templateAccount, ext.getPortlet(), msParent, msPlaceholder);
		} else if ( ext.getTimeline()!=null ) {
			processTimeline(templateAccount, ext.getTimeline(), msParent, msPlaceholder);
		} else if ( ext.getTrimList()!=null ) {
			processTrimList(templateAccount, ext.getTrimList(), msParent);
		} else if ( ext.getField()!=null ) {
			if (msParent.getRole().equals(MenuStructure.PLACEHOLDER)) {
				processPlaceholderField(msParent, ext.getField());
			} else {
				throw new RuntimeException("Field extension ("+ path + ") must be for a placeholder");
			}
		} else if ( ext.getColumn()!=null ) {
			if (msParent.getRole().equals(MenuStructure.LIST) ||
					msParent.getRole().equals(MenuStructure.PORTLET) ||
					msParent.getRole().equals(MenuStructure.ACTION) ||
					msParent.getRole().equals(MenuStructure.TRIMLIST) ||
					msParent.getRole().equals(MenuStructure.BAND) ||
					msParent.getRole().equals(MenuStructure.ENTRY)) {
				processColumn(ext.getColumn(), msParent );
			} else {
				throw new RuntimeException("Column extension ("+ path + ") must to a list, portlet, action, trimlist, ban, or entry");
			}
		}
		
	}
	/**
	 * Return a list of menu structure entries matching the provided path, including wildcards.
	 * @param path
	 * @return
	 */
	protected java.util.List<AccountMenuStructure> getMatchingMenuStructures( String path) {
	    Query query = em.createQuery("SELECT ams FROM AccountMenuStructure ams " +
                "WHERE ams.account.id in (SELECT at.account.id FROM AccountTemplate at) " +
                "AND ams.path like :p " +
                "AND ams.account.accountType.status in ('ACTIVE', 'active')");
		query.setParameter("p", path.replace("*", "%"));
		java.util.List<AccountMenuStructure> rslt = query.getResultList();
		return rslt;
	}
	
	/**
	 * Process the extend element
	 * @param ext
	 */
	public void processExtend( Extends ext ) {
		if (ext.getPath()==null | ext.getPath().length()==0) {
			throw new RuntimeException( "Missing path attribute in application extension at " /* + ext.sourceLocation()*/);
		}
		String paths[] = ext.getPath().split("\\,");
		for (String path : paths) {
			// Look up this path
			java.util.List<AccountMenuStructure> amss = getMatchingMenuStructures( path );
			// If not found but required, error
			if (amss.size()==0 && !ext.isOptional()) {
				throw new RuntimeException( "Required path (" + path + ") in extends element not found at " /* + ext.sourceLocation()*/);
			}
			// Process each matching item
			for (AccountMenuStructure ams : amss) {
				processExtend( ext, ams.getAccount().getAccountType().getKnownType(), ams.getPath());
			}
		}
	}
	
	/**
	 * Individual updates to an account type begin with an extends node which specifies the path
	 * being updated.
	 * @param app
	 */
	public void processExtends( java.util.List<Application> extendApplications ) {
		java.util.List<Extends> extensions = new ArrayList<Extends>();
		// Pull all extensions from each application file into one list
        // Now pick up the extensions to established applications
        for (Application application : extendApplications) {
    		if (application.getMenu()!=null) {
    			throw new RuntimeException( "<menu> element not allowed in unnamed application " + application.getName() );
    		}
    		if (application.getDepends().size()>0) {
    			throw new RuntimeException( "<depends> element not allowed in unnamed application " + application.getName() );
    		}
    		if (application.getLogo()!=null) {
    			throw new RuntimeException( "logo attribute not allowed in unnamed application " + application.getName() );
    		}
    		if (application.getTitle()!=null) {
    			throw new RuntimeException( "title attribute not allowed in unnamed application " + application.getName() );
    		}
    		processTrimMenus( application.getTrimMenus() );
    		for (Extends ext : application.getExtends()) {
    			extensions.add( ext );
    		}
        }
        // Part 2: Sort the applications
        Collections.sort(extensions, new AppExtendsCompare());
        
		// This entire application.xml contains extensions to an existing templateAccount
		for (Extends ext : extensions) {
			processExtend( ext );
		}
	}
	
	/**
	 * Given a single application tree from xml, create or update an accountType if needed
	 * and all Menu structure under it. 
	 * There are two syntax variations: If name is specified in the root application element, then 
	 * create a "template account" which holds the updated metadata. This might also cause an account type to be create if it doesn't already exist.
	 * If the root application element does not contain the name attribute, then the entire application.xml file
	 * will contain extensions to existing accountTypes.
	 * @param app - the parsed application structure
	 */
    public void processNewApplication(Application app) {
        if (app.getExtends().size() > 0) {
            throw new RuntimeException("Extends element not allowed in named application " + app.getName());
        }
        Account templateAccount;
        // If application name is specified, then we are creating a new or updating an account type 
        // and creating a new templateAccount.
        templateAccount = updateAccountType(app);
        processTrimMenus(app.getTrimMenus());
        if (app.getMenu() == null) {
            // Create default root
            AccountMenuStructure ms = new AccountMenuStructure();
            ms.setAccount(templateAccount);
            ms.setNode(templateAccount.getAccountType().getKnownType());
            ms.setRole(MenuStructure.TAB);
            ms.setSequence(1);
            em.persist(ms);
        } else {
            AccountMenuStructure msRoot = processMenu(templateAccount, app.getMenu(), null, null);
            logger.info("Create Placeholders ");
            int seq = msRoot.getChildren().size();
            // Create the placeholder hierarchy
            for (Placeholder placeholder : app.getPlaceholders()) {
                AccountMenuStructure childPlaceholder = processPlaceholder(msRoot, placeholder, null);
                seq++;
                childPlaceholder.setSequence(SEQUENCE_MULTIPLIER * seq);
            }
        }
    }
	
	class AppDependCompare implements Comparator<Object> {

		public int compare(Object o1, Object o2) {
			Application app1 = (Application) o1;
			Application app2 = (Application) o2;
			// Anything without a name goes at the end
			if (app1.getName()==null && app2.getName()!=null) return 1;
			if (app2.getName()==null && app1.getName()!=null) return -1;
			// The application with fewer dependencies goes first
			if (app1.getDepends().size() < app2.getDepends().size()) return -1;
			if (app1.getDepends().size() > app2.getDepends().size()) return 1;
			// Compare dependency names
			for (int x = 0; x < app1.getDepends().size(); x++) {
				int comp = app1.getDepends().get(x).compareTo(app2.getDepends().get(x));
				if (comp != 0) return comp;
			}
			// Finally, compare the application name
			return  app1.getName().compareTo(app2.getName());
		}
	}
	
	class AppExtendsCompare implements Comparator<Object> {

		public int compare(Object o1, Object o2) {
			Extends ext1 = (Extends) o1;
			Extends ext2 = (Extends) o2;
			String ext1Path[] = ext1.getPath().split("\\:");
			String ext2Path[] = ext2.getPath().split("\\:");
			int pathDiff = ext1Path.length - ext2Path.length;
			// Shortest path goes first
			if (pathDiff != 0) {
				return pathDiff;
			}
			int nodeDiff = 0;
			for (int x = 0; x < ext1Path.length; x++) {
				nodeDiff = ext1Path[x].compareTo(ext2Path[x]);
				if (nodeDiff!=0) break;
			}
			return nodeDiff;
		}
	}
	
	protected void deactivateObsoleteAccountTypes( java.util.List<Application> newApplications ) {
		for (AccountType accountType : getAccountBean().findActiveAccountTypes()) {
			boolean keepActive = false;
	        for (Application application : newApplications) {
	        	if (accountType.getKnownType().equals(application.getName())) {
	        		keepActive=true;
	        		break;
	        	}
	        }
	        if (!keepActive) {
	        	logger.info("Deavtivating accountType " + accountType);
	        	accountType.setStatus(Status.OBSOLETE.value());
	        }
		}
		
	}
	/**
	 * Process the list of new applications
	 * @param newApplications
	 */
	public void processApplications( java.util.List<Application> newApplications ) {
        // Do a dependency sort before we get started
        // Part 1: Sort the depends list in each application 
        for (Application application : newApplications) {
            Collections.sort(application.getDepends());
        }
        // Part 2: Sort the applications
        Collections.sort(newApplications, new AppDependCompare());
        
        // Deactivate any accounts types no longer in the list.
        //deactivateObsoleteAccountTypes(newApplications);
        // First do the new applications
        for (Application application : newApplications) {
        	processNewApplication(application);
        }
		
	}
	/**
	 * Load all supplied application metadata. The files have been read to memory on the client and passed here as
	 * a list of zero or more mapped strings with the key being the name of the file.
	 * @param appFiles
	 */
	public void loadApplications( Map<String, String> appFiles ) {
        java.util.List<Application> newApplications = new ArrayList<Application>();
        java.util.List<Application> extendApplications = new ArrayList<Application>();
        ParseMenuStructure pa = new ParseMenuStructure();
		for (Map.Entry<String, String> entry : appFiles.entrySet()) {
			if (entry.getKey().endsWith(APPLICATION_EXTENSION)) {
				try {
					StringReader input = new StringReader( entry.getValue());
					Application application = pa.parseReader(input);
		    		if (application.getName()==null) {
		    			logger.info("Extending application(s) from " + entry.getKey());
		    			extendApplications.add(application);
		    		} else {
		    			logger.info("Adding application " + application.getName() +  " from " + entry.getKey());
		    			newApplications.add(application);
		    		}
				} catch (Exception e) {
					throw new RuntimeException( "Error parsing file " + entry.getKey(), e);
				}
			}
		}
		processApplications( newApplications );
        // Extensions
        processExtends( extendApplications );
        
        // Now go back for properties
        for (Application application : newApplications) {
            uploadProperties(application, appFiles);
        }
        for (Application application : extendApplications) {
            uploadProperties(application, appFiles);
        }
		logger.info( "Finished creating application metadata" );

	}
	
	/**
	 * Look for a file name by comparing the tail of the key string (endsWith). Verify that there is only one match.
	 * An error is thrown if the value is not found or a duplicate is found.
	 * @param key
	 * @param appFiles
	 * @return The value associated with the string
	 */
	public String findResource( String key, Map<String, String> appFiles ) {
		String value = null;
		for (Map.Entry<String, String> entry : appFiles.entrySet()) {
			if (entry.getKey().endsWith(key)) {
				if (value!=null) {
					throw new RuntimeException( "Ambiguous resource requested: " + key );
				}
				value = entry.getValue();
			}
		}
		if (value==null) {
			throw new RuntimeException( "Resource not found: " + key );
		}
		return value;
	}
	
	/**
	 * Given a single application tree from xml, upload the properties for this accountType
	 * @param app
	 */
	public void uploadProperties( Application app, Map<String, String> appFiles ) {
		try {
			for ( AccountProperty property : app.getProperties()) {
				String appName = app.getName();
				if (appName==null || appName.length()==0) {
					appName = property.getAccountType();
				}
				Account account;
				if (appName!=null) {
					account = getAccountBean().findAccountTemplate(appName);
				} else {
					account = getAccountBean().findAccount(property.getAccount());
				}
				String value = property.getValue();
				if ((value==null || value.length()==0) && property.getFile()!=null) {
					String resourceName = property.getFile();
					value = findResource( resourceName, appFiles );
				}
				getAccountBean().putAccountProperty(account.getId(), property.getName(), value );
			}
		} catch (Exception e) {
			throw new RuntimeException( "Error uploading properties for " + app.getName(), e);
		}
	}
	
	public MenuLocal getMenuBean() {
		return menuBean;
	}

	public void setMenuBean(MenuLocal menuBean) {
		this.menuBean = menuBean;
	}

	public AccountDAOLocal getAccountBean() {
		return accountBean;
	}

	public void setAccountBean(AccountDAOLocal accountBean) {
		this.accountBean = accountBean;
	}

	
}
