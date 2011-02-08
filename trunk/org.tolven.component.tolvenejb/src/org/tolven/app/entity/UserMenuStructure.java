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
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;

/**
 * <p>An occurance of this object defines an item in an application menu structure. MenuStructure 
 * objects are defined within an account.
 * Therefore, different accounts can have completely different menu structures.</p>
 * <p>MenuStructure objects are arranged in a hierarchy. Each object, except the root object, 
 * has a parent MenuStructure. This hierarchy is confined to a single account.
 * Thus, a hierarchy cannot be used to span accounts and thus violate security partitioning. 
 * That said, the hierarchy can span sub-accounts within the top-level account.</p>
 * <p>A MenuStructure object not only defines how the menu looks on the screen, it can also 
 * define how the data for dynamic structures is acquired, generally in the form of rules.
 * </p>
 * <p>The MenuData class defines occurances of menu structures when repeating is specified and/or 
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
@Entity
@Table
public class UserMenuStructure extends MenuStructure implements Serializable {

	static final int MAX_DEPTH = 50;
	
	public UserMenuStructure() {
		super();
	}

	public UserMenuStructure(AccountUser accountuser, AccountMenuStructure underlytingMS, String visible, Integer seq, 
			Integer colnumber, String windowStyle, Integer numSummaryItems, String defaultpath, String interval ) {
		this.accountuser = accountuser;
		this.underlyingMS = underlytingMS;
		this.visible = visible;
		this.sequence = seq;
		this.columnNumber = colnumber;
		this.windowstyle = windowStyle;
		this.numSummaryItems = numSummaryItems;
		this.defaultPathSuffix = defaultpath;
		this.interval = interval;
	}

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="APP_SEQ_GEN")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountUser accountuser;

    @ManyToOne( fetch = FetchType.LAZY )
    private AccountMenuStructure underlyingMS;
    
    // Visibility rule. true/false for simple, static.
    // Rule can also be based on patient demographics or other settings
    @Column
    private String visible;

    @Column
    private Integer sequence;

    @Column
    private Integer columnNumber;
    
    @Column
    private String windowstyle;
    
    @Column
    private Integer numSummaryItems;
    
    @Column
    private String defaultPathSuffix;
    
    @Column
    private String interval;

    @Column
    private String style;
    
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id =id;
	}

	public AccountUser getAccountuser() {
		return accountuser;
	}

	public void setAccountuser(AccountUser accountuser) {
		this.accountuser = accountuser;
	}

	public MenuStructure getUnderlyingMS() {
		return underlyingMS;
	}

	public void setUnderlyingMS(AccountMenuStructure underlyingMS) {
		this.underlyingMS = underlyingMS;
	}

	public void setDefaultPathSuffix(String defaultPathSuffix) {
		this.defaultPathSuffix = defaultPathSuffix;
	}

	// Inserts
	
	// Rest is from MenuStructure ( just the getters )
	
    /**
     * Get the root of this MenuStructure tree
     * @return The root MenuStructure
     */
    public AccountMenuStructure getRoot() {
    	return underlyingMS.getRoot();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof UserMenuStructure) ) return false;
        MenuStructure ms = (MenuStructure) obj;
        if (getAccount()==null) return false;
        if (ms.getAccount()==null) return false;
        if (getAccount().getId()!=ms.getAccount().getId()) return false;
        if (getPath()==null) return false;
        if (ms.getPath()==null) return false;
        return getPath().equals(ms.getPath());
    }

    public String toString() {
        return "UMS: " + this.id + " AMS:" + this.underlyingMS.getId();
    }

    public int hashCode() {
        return underlyingMS.getNode().hashCode();
    }

	public int getLevel() {
		return underlyingMS.getLevel();
	}

	public String getNode() {
		return underlyingMS.getNode();
	}

	public String getPath() {
		return underlyingMS.getPath();
	}

    @Override
    public String getQuery() {
        return underlyingMS.getQuery();
    }

    @Override
    public void setQuery(String query) {
        underlyingMS.setQuery(query);
    }

	/**
	 * The user should never be allowed to override the filter criteria.
	 * End-user filter criteria is always on top of this filter.     
	 */
	public String getFilter() {
		return 	underlyingMS.getFilter();
	}

	public void setFilter(String filter) {
		underlyingMS.setFilter(filter);
	}

	public String getInitialSort() {
		return underlyingMS.getInitialSort();
	}

	public void setInitialSort(String initialSort) {
		underlyingMS.setInitialSort(initialSort);
	}

	/**
	 * Return a list of actions, sorted per sequence
	 */
	@Override
	public List<MSAction> getActions() {
		return underlyingMS.getActions();
	}

	public int getSequence() {
		if( sequence == null ) return underlyingMS.getSequence();
		else return sequence.intValue();
	}

	public String getTemplate() {
		return underlyingMS.getTemplate();
	}

	public String getRepeating() {
		return underlyingMS.getRepeating();
	}

	public String getVisible() {
		if( visible == null ) return underlyingMS.getVisible();
		else return visible;
	}

    public String getText() {
        return underlyingMS.getText();
    }

    public String getTextOverride() {
        return underlyingMS.getTextOverride();
    }

	public AccountMenuStructure getParent() {
		return underlyingMS.getParent();
	}

	public Collection<AccountMenuStructure> getChildren() {
		return underlyingMS.getChildren();
	}
	public List<AccountMenuStructure> getSortedChildren( ) {
		return underlyingMS.getSortedChildren();
	}
	
	/**
	 * Depending on the template, this role helps determine where in the page this menu item occurs.
	 * For example, menuItem is a common role used for the main menu structure. Portlet could also be a role,
	 * context, etc. 
	 * @return
	 */
	public String getRole() {
		return underlyingMS.getRole();
	}

	public String getMenuTemplate() {
		return underlyingMS.getMenuTemplate();
	}

	public Account getAccount() {
		return underlyingMS.getAccount();
	}

	public Collection<MSColumn> getColumns() {
		return underlyingMS.getColumns();
	}
	
	public List<MSColumn> getSortedColumns( ) {
		return underlyingMS.getSortedColumns();
	}

	/**
	 * <p>When non-null, a "list" item refers to another MenuStructure item that actually contains the data.
	 * This allows a list to be moved around in the menu structure while the rule that populates the list always
	 * does do in a consistent place.</p>
	 * <p>This also allows a list, such as a generic menu to be accessed from within, say, the patient context without
	 * requiring patient to be specified in the path</p>
	 * @return
	 */
	public AccountMenuStructure getReferenced() {
		return underlyingMS.getReferenced();
	}

	public String getDefaultPathSuffix() {
		if(defaultPathSuffix != null ) return defaultPathSuffix;
		else return underlyingMS.getDefaultPathSuffix();
	}

	public Boolean getEnableBackButton() {
		return underlyingMS.getEnableBackButton();
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	@Override
	public AccountMenuStructure findChild(String node) {
		return this.underlyingMS.findChild( node );
	}

	@Override
	public AccountMenuStructure findDescendent(String node) {
		return this.underlyingMS.findDescendent( node );
	}

	@Override
	public void setAccount(Account account) {
		this.underlyingMS.setAccount( account );
	}

	@Override
	public void setChildren(Collection<AccountMenuStructure> children) {
		this.underlyingMS.setChildren( children );
	}

	@Override
	public void setEnableBackButton(Boolean enableBackButton) {
		this.underlyingMS.setEnableBackButton( enableBackButton );
	}

	@Override
	public void setLevel(int level) {
		this.underlyingMS.setLevel( level );
	}

	@Override
	public void setMenuTemplate(String menuTemplate) {
		this.underlyingMS.setMenuTemplate( menuTemplate );
	}

	@Override
	public void setNode(String node) {
		this.underlyingMS.setNode( node );
	}

	@Override
	public void setParent(AccountMenuStructure parent) {
		this.underlyingMS.setParent( parent );
	}

	@Override
	public void setParent(MenuStructure parent) {
		this.underlyingMS.setParent( parent );
	}

	@Override
	public void setPath(String path) {
		this.underlyingMS.setPath( path );
	}

	@Override
	public void setReferenced(AccountMenuStructure referenced) {
		this.underlyingMS.setReferenced( referenced );
	}

	@Override
	public void setReferenced(MenuStructure referenced) {
		this.underlyingMS.setReferenced( referenced );
	}

	@Override
	public void setRepeating(String repeating) {
		this.underlyingMS.setRepeating( repeating );
	}

	@Override
	public void setRole(String role) {
		this.underlyingMS.setRole( role );
	}

	@Override
	public void setSequence(int sequence) {
		this.underlyingMS.setSequence( sequence );
	}

	@Override
	public void setTemplate(String template) {
		this.underlyingMS.setTemplate( template );
	}

    @Override
    public void setText(String text) {
        this.setText( text );
    }

    @Override
    public void setTextOverride(String text) {
        this.setTextOverride( text );
    }

	@Override
	public AccountMenuStructure getAccountMenuStructure() {
		return this.underlyingMS;
	}

	public Integer getColumnNumber() {
		if( columnNumber == null ) return this.underlyingMS.getColumnNumber();
		return columnNumber;
	}

	public void setColumnNumber(Integer columnNumber) {
		this.columnNumber = columnNumber;
	}

	public String getWindowstyle() {
		if( windowstyle == null ) return "true";
		return windowstyle;
	}

	public void setWindowstyle(String windowstyle) {
		this.windowstyle = windowstyle;
	}

	public Integer getNumSummaryItems() {
		if( numSummaryItems == null ) return 6;
		return numSummaryItems;
	}

	public void setNumSummaryItems(Integer numSummaryItems) {
		this.numSummaryItems = numSummaryItems;
	}

	@Override
	public String getAllowRoles() {
		return getAccountMenuStructure().getAllowRoles();
	}

	@Override
	public String getDenyRoles() {
		return getAccountMenuStructure().getDenyRoles();
	}
	public String getInterval() {
		return interval;
	}

	public void setInterval(String timelineInterval) {
		this.interval = timelineInterval;
	}
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public String getUniqueKey() {
		return underlyingMS.getUniqueKey();
	}

	@Override
	public void setUniqueKey(String uniqueKey) {
		underlyingMS.setUniqueKey(uniqueKey);
		
	}

    @Override
    public String getMenuEventHandlerFactory() {
        return underlyingMS.getMenuEventHandlerFactory();
    }

    @Override
    public void setMenuEventHandlerFactory(String menuEventHandlerFactory) {
        underlyingMS.setMenuEventHandlerFactory(menuEventHandlerFactory);
    }

    @Override
    public String getMenuEventHandlerData() {
        return underlyingMS.getMenuEventHandlerData();
    }

    @Override
    public void setMenuEventHandlerData(String menuEventHandlerData) {
        underlyingMS.setMenuEventHandlerData(menuEventHandlerData);
    }

    @Override
    public Properties getMenuEventHandlerDataMap() throws IOException {
        return underlyingMS.getMenuEventHandlerDataMap();
    }

    @Override
    public void setMenuEventHandlerDataMap(Properties properties) throws IOException {
        underlyingMS.setMenuEventHandlerDataMap(properties);
    }

    @PrePersist
    public void prePersist() throws IOException {
        underlyingMS.prePersist();
    }

}
