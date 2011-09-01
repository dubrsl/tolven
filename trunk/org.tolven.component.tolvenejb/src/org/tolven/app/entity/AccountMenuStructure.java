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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.tolven.core.entity.Account;

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
public class AccountMenuStructure extends MenuStructure implements Serializable, MSAction {

    private static final long serialVersionUID = 1L;

    static final int MAX_DEPTH = 50;
    
    public AccountMenuStructure() {
        super();
    }

    public AccountMenuStructure(Account account, AccountMenuStructure parent, String template, String menuTemplate, int seq, String node, String text, String visible, String repeat, String role) {
        this.account = account;
        this.parent = parent;
        this.template = template;
        this.menuTemplate = menuTemplate;
        this.sequence = seq;
        this.node = node;
        this.text = text;
        this.visible = visible;
        this.repeating = repeat;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="APP_SEQ_GEN")
    private long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountMenuStructure parent;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountMenuStructure referenced;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected Collection<AccountMenuStructure> children = new HashSet<AccountMenuStructure>( 10 );
    
    @OneToMany(mappedBy = "menuStructure", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	protected Collection<MSColumn> columns = new HashSet<MSColumn>( 10 );

    @Transient
    private transient Map<String, MSColumn> columnMap;
    
    @Column
    private String path;

    @Column
    private String eventPath;

    @Column
    private String node;
 
    @Column
    private String role;

    // Text actually displayed in node
    @Column
    private String text;

    // Override text which would normally be pulled from message bundles
    @Column
    private String textOverride;
 
    @Column
    private String query;

    @Column
    private String filter;

    @Column
    private String uniqueKey;

    @Column
    private String initialSort;

    // Could be deduced from path. If path is empty, then level is 0. A path of "echr:chart" would be level 2.
    @Column
    private int level;
 
    @Column
    private int sequence;
    
    @Column
    private Integer columnNumber;
 
    @Column
    public String allowRoles;

    @Column
    public String denyRoles;

    @Column
    private String template;

    @Column
    private String menuTemplate;

    // Visibility rule. true/false for simple, static.
    // Rule can also be based on patient demographics or other settings
    @Column
    private String visible;
    
    // The entity that repeats, if any, otherwise, null. For example, patient-123 would repeat per "patient"
    @Column
    private String repeating;

    @Column
    private String defaultPathSuffix;
    
    // This maps to the value in Account table
    @Column
    private Boolean enableBackButton;

    @Column
    private String interval;
    
    @Column
    private String style;

    @Column
    private String idFrom;

    @Column
    private String menuEventHandlerFactory;

    @Lob
    @Column
    private String menuEventHandlerData;
    
    @Transient
    private Properties menuEventHandlerDataMap;
    
    //As the name suggests, Get the first child( order by sequence number and which is not a portlet) of the given node. 
    private MenuStructure getFirstChild( MenuStructure ms ){
        MenuStructure firstChild = null;
        if( ms != null){
            for( MenuStructure item : ms.getSortedChildren() ){
                if( item.getSequence() > 0 && !(VISIBLE_NEVER.equalsIgnoreCase(item.getVisible())) && !( PORTLET.equalsIgnoreCase( item.getRole() ) ) ){
                    firstChild = item;
    //              TolvenLogger.info(" first child:" + item.getPath(), AccountMenuStructure.class );
                    break;
                }
            }
        }
        return firstChild;
    }

    /**
     * The path string is computed, not set. This is where we do it by climbing up the path to the root.
     * We also compute the level based on how many nodes are in the path.
     * @throws IOException 
     */
    @PrePersist
    protected void prePersist() throws IOException {
        calculatePath();
        marshallMenuEventHandlerData();
    }
    
    @PreUpdate
    protected void preUpdate() throws IOException {
        marshallMenuEventHandlerData();
    }

    protected void calculatePath() {
        Stack<MenuStructure> parents = new Stack<MenuStructure>();
        MenuStructure p = this;
        // Climb to the top  of the hierarchy
        // If we go too deep, consider it a loop and throw an exception
        while (p!=null) {
            if (parents.size()>MAX_DEPTH) throw new RuntimeException( "Menu Structure hierarchy exceeded, probably due to a cycle in the parent links" );
            parents.push( p );
            p = p.getParent();
        }
        this.setLevel( parents.size() );
        // Build the path string by popping and getting the node name
        StringBuffer sb = new StringBuffer(150);
        while (parents.size() > 0) {
            sb.append( parents.pop().getNode() );
            if (parents.size()>0) sb.append( ":" );
        }
        path = sb.toString();
        // Also while we're here, add us to our parent
        if (getParent()!=null && !getParent().getChildren().contains(this)) {
            getParent().getChildren().add( this ); 
        }

        // By default, set enable back button to true; 
/*      this.setEnableBackButton( new Boolean( true ) );
        //Calculate and set the defaultPath of ancestors
        if( this.getDefaultPathSuffix() == null ){
            TolvenLogger.info(" UpdateDPSOfParent", AccountMenuStructure.class);
            UpdateDPSOfParent( this );
        }
*/      
    }
    
    public Map<String, MSColumn> getColumnMap() {
        if (columnMap==null) {
            // Copy the columns list to a transient attribute on the trim  
            columnMap = new HashMap<String, MSColumn>(getColumns().size());
            for (MSColumn col : getColumns()) {
                columnMap.put(col.getHeading(), col);
            }
        }
        return columnMap;
    }
    /**
     * Get the root of this MenuStructure tree
     * @return The root MenuStructure
     */
    public AccountMenuStructure getRoot() {
        if (parent==null) return this;
        return parent.getRoot();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AccountMenuStructure)) return false;
        MenuStructure ms = (MenuStructure) obj;
        if (getAccount()==null) return false;
        if (ms.getAccount()==null) return false;
        if (getAccount().getId()!=ms.getAccount().getId()) return false;
        if (getPath()==null) return false;
        if (ms.getPath()==null) return false;
        return getPath().equals(ms.getPath());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("MenuStructure: ");
        sb.append(id);
        sb.append(" Path: ");
        sb.append( path );
        return sb.toString();
    }

    public int hashCode() {
        return getNode().hashCode();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getPath() {
        return path;
    }
    /**
     * Let the infrastructure set this attribute by following the parent/child hierarchy. In fact, it will be reset at persist time.
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getRepeating() {
        return repeating;
    }

    public void setRepeating(String repeating) {
        this.repeating = repeating;
    }
    /**
     * Return true if this metadata item is marked as visible
     * @return
     */
    public boolean isVisible() {
        if (visible==null || VISIBLE_TRUE.equals(visible)) return true;
        return false;
    }
    /**
     * Return visible string (if actual value is null, then true is returned).
     */
    public String getVisible() {
        if (visible==null) {
            return VISIBLE_TRUE;
        }
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * For customization, override text for display, which would normally be pulled and translated via message bundles
     * @return
     */
    public String getTextOverride() {
        return textOverride;
    }

    public void setTextOverride(String textOverride) {
        this.textOverride = textOverride;
    }

    public AccountMenuStructure getParent() {
        return parent;
    }

    public void setParent(AccountMenuStructure parent) {
        this.parent = parent;
    }
    public void setParent(MenuStructure parent){
        this.parent = (AccountMenuStructure)parent;
    }

    public Collection<AccountMenuStructure> getChildren() {
        return children;
    }

    /**
     * Return a list of actions, sorted per sequence
     */
    public List<MSAction> getActions() {
        List<MSAction> actions = new ArrayList<MSAction>(5);
        for (AccountMenuStructure child: getChildren()) {
            if (ACTION.equals(child.getRole()) && VISIBLE_TRUE.equals(child.getVisible())) {
                actions.add(child);
            }
        }
        Collections.sort( actions, new MenuSeqSort());
        return actions;
    }
    
    /**
      * Find the first child containing the specified node name.
     * @param node Parent node to search
     * @return MenuStructure or null
     */
    public AccountMenuStructure findChild( String node ) {
        for ( AccountMenuStructure c : getChildren()) {
            if (c.getNode().equals( node )) return c;
        }
        return null;
    }
    
    /**
     * Find the first child or child of child, etc containing the specified node name.
     * @param node Node from which to begin search
     * @return MenuStructure or null
     */
    public AccountMenuStructure findDescendent( String node ) {
        for ( AccountMenuStructure c : getChildren()) {
            if (c.getNode().equals( node )) return c;
            AccountMenuStructure cms = c.findDescendent( node );
            if (cms != null) return cms;
        }
        return null;
    }

    /**
     * Local comparator used to sort menu items by sequence number. The sort only occurs within a
     * single menu level and thus the list is usually small, say two up to about ten items.
     * @author John Churin
     *
     */
    public List<AccountMenuStructure> getSortedChildren( ) {
        List<AccountMenuStructure> sorted = new ArrayList<AccountMenuStructure>( getChildren());
        Collections.sort( sorted, new MenuSeqSort());
        return sorted;
    }

    public void setChildren(Collection<AccountMenuStructure> children) {
        this.children = children;
    }
    /**
     * Depending on the template, this role helps determine where in the page this menu item occurs.
     * For example, menuItem is a common role used for the main menu structure. Portlet could also be a role,
     * context, etc. 
     * @return
     */
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMenuTemplate() {
        return menuTemplate;
    }

    public void setMenuTemplate(String menuTemplate) {
        this.menuTemplate = menuTemplate;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Collection<MSColumn> getColumns() {
        return columns;
    }

    /**
     * Local comparator used to sort column items by sequence number. 
     */
    class ColSeqSort implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            MSColumn mcol1 = (MSColumn) o1;
            MSColumn mcol2 = (MSColumn) o2;
            if (mcol1.getSequence() == mcol2.getSequence()) return 0;
            if (mcol1.getSequence() < mcol2.getSequence()) return -1;
            return 1;
        }
    }

    public List<MSColumn> getSortedColumns( ) {
        List<MSColumn> sortedColumns = new ArrayList<MSColumn>(getColumns());
        Collections.sort( sortedColumns, new ColSeqSort());
        return sortedColumns;
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
        return referenced;
    }

    public void setReferenced(AccountMenuStructure referenced) {
        this.referenced = referenced;
    }
    public void setReferenced(MenuStructure referenced){
        this.referenced = (AccountMenuStructure)referenced;
    }

    public String getDefaultPathSuffix() {
        return defaultPathSuffix;
    }

    public void setDefaultPathSuffix(String defaultPathSuffix) {
        this.defaultPathSuffix = defaultPathSuffix;
    }

    public Boolean getEnableBackButton() {
        return enableBackButton;
    }

    public void setEnableBackButton(Boolean enableBackButton) {
        this.enableBackButton = enableBackButton;
    }

    @Override
    public AccountMenuStructure getAccountMenuStructure() {
        return this;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(Integer columnNumber) {
        this.columnNumber = columnNumber;
    }

    @Override
    public String getWindowstyle() {
        return "true";
    }

    @Override
    public Integer getNumSummaryItems() {
        return 6;
    }

    public String getAllowRoles() {
        return allowRoles;
    }

    public void setAllowRoles(String allowRoles) {
        this.allowRoles = allowRoles;
    }

    public String getDenyRoles() {
        return denyRoles;
    }

    public void setDenyRoles(String denyRoles) {
        this.denyRoles = denyRoles;
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getInitialSort() {
        return initialSort;
    }

    public void setInitialSort(String initialSort) {
        this.initialSort = initialSort;
    }

    public String getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(String idFrom) {
        this.idFrom = idFrom;
    }
    
    /**
     * EventPath is used in a placeholder and is used when the even has a different
     * "event" placeholder than the primary placeholder.
     * @return
     */
    public String getEventPath() {
        return eventPath;
    }

    public void setEventPath(String eventPath) {
        this.eventPath = eventPath;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    @Override
    public String getMenuEventHandlerFactory() {
        return menuEventHandlerFactory;
    }

    @Override
    public void setMenuEventHandlerFactory(String menuEventHandlerFactory) {
        this.menuEventHandlerFactory = menuEventHandlerFactory;
    }

    @Override
    public String getMenuEventHandlerData() {
        return menuEventHandlerData;
    }

    @Override
    public void setMenuEventHandlerData(String menuEventHandlerData) {
        this.menuEventHandlerData = menuEventHandlerData;
    }

    protected void marshallMenuEventHandlerData() throws IOException {
        if(menuEventHandlerDataMap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            menuEventHandlerDataMap.storeToXML(baos, null, "UTF-8");
            menuEventHandlerData = new String(baos.toByteArray(), "UTF-8");
        }
    }

    protected void unmarshallMenuEventHandlerData() throws IOException {
        menuEventHandlerDataMap = new Properties();
        menuEventHandlerDataMap.loadFromXML(new ByteArrayInputStream(getMenuEventHandlerData().getBytes("UTF-8")));
    }
    
    @Override
    public Properties getMenuEventHandlerDataMap() throws IOException {
        if(menuEventHandlerDataMap == null && getMenuEventHandlerData() != null) {
            unmarshallMenuEventHandlerData();
        }
        return menuEventHandlerDataMap;
    }

    @Override
    public void setMenuEventHandlerDataMap(Properties properties) throws IOException {
        this.menuEventHandlerDataMap = properties;
        marshallMenuEventHandlerData();
    }
    
}
