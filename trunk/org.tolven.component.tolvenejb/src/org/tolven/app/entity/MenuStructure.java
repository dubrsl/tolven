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
package org.tolven.app.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.tolven.app.AppLocaleText;
import org.tolven.app.el.AppLocaleMap;
import org.tolven.core.entity.Account;
import org.tolven.locale.ResourceBundleHelper;

/**
 * <p>An occurrence of this object defines an item in an application structure. MenuStructure 
 * objects are defined within an account.
 * Therefore, different accounts can have completely different structures.</p>
 * <p>MenuStructure objects are arranged in a hierarchy. Each object, except the root object, 
 * has a parent MenuStructure. This hierarchy is confined to a single account.
 * Thus, a hierarchy cannot be used to span accounts and thus violate security partitioning. 
 * That said, the hierarchy can span sub-accounts within the top-level account.</p>
 * <p>A MenuStructure object not only defines how the menu looks on the screen, it can also 
 * define how the data for dynamic structures is acquired, generally in the form of rules.
 * </p>
 * <p>The MenuData class defines occurrences of menu structures when repeating is specified and/or 
 * if there is a rule that creates menu data. By definition, a MenuStructure for a repeating entity is
 * a prototype and therefore only represents a usually invisible placeholder in the menu hierarchy. For example, 
 * defining a patient MenuStructure does not imply that there is a visible object on the screen unless
 * there is actually a specific patient to be displayed. Conversely, a MenuStructure called Patients might
 * very well be static, visible and represent a tab one can go to to see a list of patients. Therefore, 
 * the static "Patients" MenuStructure makes a good parent for the dynamic "patient placeholder" MenuStructure.</p>
 * <p>Dynamic (repeating) MenuStructures are represented by a key value. Nested structures may have more than one
 * key value. </p>
 * <p>A Menustructure can also be static but not have a key value. For example, a "new activity"  tab might 
 * carry an indication if there are any items to view or not thus saving the user the trouble of having to click the tab to
 * find out of there is anything to do or not. And since saving time is so critical, it is usually better to maintain the
 * indicator on such menus or tabs by rules that run in the background. Thus, the rules for a MenuStructure item can be run 
 * asynchronously. Later, the presentation of the pre-computed indicator is simply a matter of looking up the indicator, 
 * rather than running a potentially complex query while the user waits.</p>
 * <p>While each rule in a MenuStructure controls the MenuData associated with that MenuStructure, the individual rules are 
 * rolled up to the account. When any data associated with an account changes, the data element is asserted into the 
 * working memory of all the rules for that Account. Thus, a rule can potentially see every possible change to an Account.
 * To organize this process, it may be appropriate to modularize the rule logic. For example, say an account gets lab 
 * results from a variety of sources in different formats. A rule collecting data from lab results could be quite tedious if
 * it had to handle each lab format. Instead, two different kinds of rules should be created. The first kind simply reads a 
 * document and creates a new document in a normalized form. Each separate interface then had a rule that maps to it to this 
 * canonical form. The MenuStructure for such an object could be invisible if no one needs to see the raw messages. Then, 
 * one much simpler rule reacts the the creation of this new document type and populates the appropriate visible menu 
 * structure.</p>
 * @author John Churin
 */
public abstract class MenuStructure implements Serializable, AppLocaleText {

	static final int MAX_DEPTH = 50;
    public static final String VISIBLE_NEVER = "never";
    public static final String VISIBLE_TRUE = "true";
    public static final String VISIBLE_FALSE = "false";
    public static final String MENUPATH_SEPARATOR = ":";
    public static final String LOCALEPATH_SEPARATOR = ".";
    public static final String LOCALEPATH_COLUMN_PREFIX = "Column";
    
    public static final String ACTION = "action";
    public static final String BAND = "band";
    public static final String CALENDAR = "calendar";
    public static final String ENTRY = "entry";
    public static final String EVENT = "event";
    public static final String LIST = "list";
    public static final String PLACEHOLDER = "placeholder";
    public static final String PORTLET = "portlet";
    public static final String REPORT = "report";
    public static final String TAB = "tab";
    public static final String TIMELINE = "timeline";
    public static final String TRIMLIST = "trimlist";
    
    /**
     * The path string is computed, not set. This is where we do it by climbing up the path to the root.
     * We also compute the level based on how many nodes are in the path.
     */
    /**
     * Get the root of this MenuStructure tree
     * @return The root MenuStructure
     */
	public abstract AccountMenuStructure getRoot();

    public abstract String toString();

    public abstract int hashCode();

	public abstract long getId();

	public abstract void setId(long id);

	public abstract int getLevel();

	public abstract void setLevel(int level);

	public abstract String getNode();

	public abstract String getAllowRoles();

	public abstract String getDenyRoles();

	public abstract void setNode(String node);

	public abstract String getPath();
	/**
	 * Let the infrastructure set this attribute by following the parent/child hierarchy. In fact, it will be reset at persist time.
	 * @param path
	 */
	public abstract void setPath(String path);

    
	public abstract String getQuery();
	public abstract void setQuery(String query);

    public abstract String getMenuEventHandlerFactory();
    public abstract void setMenuEventHandlerFactory(String menuEventHandlerFactory);

    public abstract String getMenuEventHandlerData();
    public abstract Properties getMenuEventHandlerDataMap() throws IOException;
    public abstract void setMenuEventHandlerDataMap(Properties properties) throws IOException;
    public abstract void setMenuEventHandlerData(String menuEventHandlerData);
	/**
	 * Filter is raw EJBQL - however, the string is pre-processed for EL substitutions.
	 * @return
	 */
	public abstract String getFilter();
	public abstract void setFilter(String filter);

	public abstract String getUniqueKey();
	public abstract void setUniqueKey(String uniqueKey);

	public abstract String getInitialSort();
	public abstract void setInitialSort(String initialSort);

	public abstract int getSequence();

	public abstract void setSequence(int sequence);

	public abstract Integer getColumnNumber();

	public abstract void setColumnNumber(Integer columnNumber);
	
	/**
	 * Compute a full menu path using the supplied context path to harvest
	 * identifiers. For example, if the menuStructure path is echr:patient:encounters:all
	 * and the supplied context is echr:patient-123:observation-456, then the resulting path will
	 * be echr:patient-123:encounters:all (the 123 of the patient placeholder was copied into 
	 * the resulting path.
	 * @param context zero or more context path elements. Can be null.
	 * @param required If true, throw an exception if any required ids are missing
	 * @return The resulting instancePath or null if the context supplied was incomplete
	 */
	public String instancePathFromContext( Map<String, Long> context, boolean required ) {
		// We now have a map of all possible placeholder instance values.
		MenuStructure ms = this;
		StringBuffer sb = new StringBuffer( 100 );
		// Build the string from last to first (while climbing the hierarchy)
		while ( ms != null ) {
			if (sb.length()>0) sb.insert(0, ':');
			if (PLACEHOLDER.equals(ms.getRole())) {
				Long nodeValue = context.get(ms.getNode());
				if (nodeValue==null) {
					if (required) {
						throw new RuntimeException( "Missing node value for " + ms.getNode() + " in " + context + " for menustructure " + getPath());
					} else {
						return null;
					}
				}
				sb.insert( 0, nodeValue );
				sb.insert( 0, '-' );
			} 
			sb.insert( 0, ms.getNode() );
			ms = ms.getParent();
		}
		// Return the final path
		return sb.toString();
	}

	public abstract String getInterval();

	public abstract void setInterval(String interval);

	
	public abstract String getTemplate();
	/**
	 * Return the type of template files, or null if we're not sure.
	 *  
	 * @return A string containing jsf, jsp, xhtml, image (such as for jpeg, jpg, png, etc)
	 * 
	 */
	public String getTemplateType() {
		String template = getTemplate();
		if (template==null) return null;
		if (template.endsWith(".xhtml")) return "xhtml";
		if (template.endsWith(".jsf")) return "jsf";
		if (template.endsWith(".jsp")) return "jsp";
		if (template.endsWith(".html")) return "html";
		if (template.endsWith(".htm")) return "html";
		if (template.endsWith(".jpeg")) return "image";
		if (template.endsWith(".jpg")) return "image";
		if (template.endsWith(".png")) return "image";
		if (template.endsWith(".gif")) return "image";
		return null;
	}
	public abstract void setTemplate(String template);

	public abstract String getRepeating();

	public abstract void setRepeating(String repeating);

	public abstract String getVisible();

	public abstract void setVisible(String visible);

    public abstract String getText();
    public abstract String getTextOverride();

	public abstract void setText(String text);
    public abstract void setTextOverride(String text);

	public abstract AccountMenuStructure getParent();

	public abstract void setParent(AccountMenuStructure parent);
	public abstract void setParent(MenuStructure parent);

	public abstract Collection<AccountMenuStructure> getChildren();

	/**
	  * Find the first child containing the specified node name.
	 * @param node Parent node to search
	 * @return MenuStructure or null
	 */
	public abstract AccountMenuStructure findChild( String node );
	/**
	 * Find the first child or child of child, etc containing the specified node name.
	 * @param node Node from which to begin search
	 * @return MenuStructure or null
	 */
	public abstract AccountMenuStructure findDescendent( String node );

	/**
	 * Local comparator used to sort menu items by sequence number. The sort only occurs within a
	 * single menu level and thus the list is usually small, say two up to about ten items.
	 * @author John Churin
	 *
	 */
	class MenuSeqSort implements Comparator<Object> {

		public int compare(Object o1, Object o2) {
			MenuStructure ms1 = (MenuStructure) o1;
			MenuStructure ms2 = (MenuStructure) o2;
			if (ms1.getSequence() == ms2.getSequence()) return 0;
			if (ms1.getSequence() < ms2.getSequence()) return -1;
			return 1;
		}
		
	}
	
	public List<MenuStructure> getSortedList( List<MenuStructure> list ) {
		List<MenuStructure> sorted = new ArrayList<MenuStructure>( list );
		Collections.sort( sorted, new MenuSeqSort());
		return sorted;
	}
	
	/**
	 * Return the accountType name (knownType) for this menuStructure.
	 * @return A string containing the known type, for example: echr.
	 */
	public String getKnownType( ) {
		return getAccount().getAccountType().getKnownType();
	}
	public abstract List<AccountMenuStructure> getSortedChildren( );

	public abstract void setChildren(Collection<AccountMenuStructure> children);
	/**
	 * Depending on the template, this role helps determine where in the page this menu item occurs.
	 * For example, menuItem is a common role used for the main menu structure. Portlet could also be a role,
	 * context, etc. 
	 * @return
	 */
	public abstract String getRole();

	public abstract void setRole(String role);

	public abstract String getMenuTemplate();

	public abstract void setMenuTemplate(String menuTemplate);

	public abstract Account getAccount();

	public abstract void setAccount(Account account);

	public abstract Collection<MSColumn> getColumns();
	public abstract List<MSColumn> getSortedColumns( );


	/**
	 * <p>When non-null, a "list" item refers to another MenuStructure item that actually contains the data.
	 * This allows a list to be moved around in the menu structure while the rule that populates the list always
	 * does do in a consistent place.</p>
	 * <p>This also allows a list, such as a generic menu to be accessed from within, say, the patient context without
	 * requiring patient to be specified in the path</p>
	 * @return
	 */
	public abstract AccountMenuStructure getReferenced();

	public abstract void setReferenced(AccountMenuStructure referenced);
	public abstract void setReferenced(MenuStructure referenced);

	public abstract String getDefaultPathSuffix();

	public abstract void setDefaultPathSuffix(String defaultPathSuffix);

	public abstract Boolean getEnableBackButton();

	public abstract void setEnableBackButton(Boolean enableBackButton);
	
	public abstract AccountMenuStructure getAccountMenuStructure();
	
	public abstract String getStyle();
	public abstract void setStyle(String style);

	public abstract String getWindowstyle();
	public abstract Integer getNumSummaryItems();
	public abstract Collection<MSAction> getActions();
	
    public String getRootApplication() {
        return getRoot().getAccount().getAccountType().getKnownType();
    }

    public String getLocaleText(ResourceBundle bundle) {
        if(getTextOverride() == null || getTextOverride().length() == 0) {
            return ResourceBundleHelper.getString(bundle, getLocaleTextKey());
        } else {
            return getTextOverride();
        }
    }

    public String getDefaultLocaleText(ResourceBundle bundle) {
        return ResourceBundleHelper.getString(bundle, getLocaleTextKey());
    }

    /**
     * Return a key for entries in MessagesBundles
     * @param locale
     * @return
     */
    public String getLocaleTextKey() {
        return getPath().replace(MENUPATH_SEPARATOR, LOCALEPATH_SEPARATOR);
    }

    /**
     * This method exists to handle the fact that JSF's method binding is limited to Map property type behavior.
     * In JSF pages, this will be accessed as item.localeText[aResourceBundle] and returns the textOverride of the item,
     * if defined, or the value found in the resource bundle
     * @return
     */
    public AppLocaleMap getLocaleText() {
        return new AppLocaleMap(this);
    }

    /**
     * This method exists to handle the fact that JSF's method binding is limited to Map property type behavior.
     * In JSF pages, this will be accessed as item.localeText[aResourceBundle] and ignores the textOverride of the item,
     * in order to return the value found in the resource bundle
     * @return
     */
    public AppLocaleMap getDefaultLocaleText() {
        return new AppLocaleMap(this, true);
    }
    
}
