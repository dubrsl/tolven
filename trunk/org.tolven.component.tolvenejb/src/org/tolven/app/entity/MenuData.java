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
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.tolven.app.bean.MenuPath;
import org.tolven.app.bean.StopList;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.Status;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.trim.CD;
import org.tolven.trim.ex.TrimFactory;

/**
 * <p>Where MenuStructure represents metadata about an account's menus, MenuData contains the actual data.
 * for each MenuStructure, there will be zero or more corresponding MenuData records. A MenuStructure entry
 * defines the rules that control the creation of menu data.</p> 
 * <p>A MenuData entity is often, but not required 
 * to be, associated with a single DocBase entity which provides the detailed backing for the MenuData entity. 
 * For example, a CBC lab result could be represented by a single MenuData entity with the individual results
 * represented in a RIM-based DocBase entity. This makes chronological results displays convenient. That same
 * lab result document could also be represented by a number of MenuData items, one for each numeric value in the test
 * which would be convenient for graphing, aggregation, or simply sorting by type of test. </p>
 * <p>The MenuData entity has a number of generic columns, the purpose of which changes by MenuStructure definition. </p>
 * <p>MenuData is often derived by summarizing activity from various source documents. However, at least in one case, 
 * an entire menu structure may be based on a single document. for example, a CCR document is specifically intended as a summary and so
 * it's display may be nothing but a number of MenuStructures that define how to display the CCR document located in this document.
 * As such, there may be little purpose in creating separate MenuData items that essentially just mirror the CCR document.
 * To accomplish a display such as this, the MenuStructure rules for a CCR document should not create MenuData 
 * items but rather navigate the CCR structure directly. There must be a MenuData item representing the document 
 * itself along with a corresponding MenuStructure covering the display requirements for the document. Beyond root level in the MenuHierarchy, expression 
 * language (EL) in the display template, can access the document directly.</p>
 * @author John Churin
 */
@EntityListeners(ExtendedMenuData.class)
@Entity
@Table
public class MenuData  implements Serializable {
	/**
	 * Version number for this entity object
	 */
	private static final long serialVersionUID = 1L;
	private static final TrimFactory trimFactory = new TrimFactory( );
	
	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="APP_SEQ_GEN")
    private long id;
    
    @Column
    private String path;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountMenuStructure menuStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Column
    private long documentId;

    @OneToMany(mappedBy = "placeholder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	protected Set<PlaceholderID> placeholderIDs;

    @OneToMany(mappedBy = "menuData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	protected Set<MenuDataWord> words;

    @ManyToOne(fetch = FetchType.LAZY)
    private TrimHeader trimHeader;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Transient
    private Map<String, MSColumn> columnMap;

    @Column
    private String actStatus;

    @Column
    private Boolean deleted;

    @Column
    private String parentPath01;

    @Column
    private String parentPath02;

    @Column
    private String parentPath03;

    @Column
    private String parentPath04;

    @ManyToOne(fetch = FetchType.LAZY)
    private MenuData parent01;

    @ManyToOne(fetch = FetchType.LAZY)
    private MenuData parent02;

    @ManyToOne(fetch = FetchType.LAZY)
    private MenuData parent03;

    @ManyToOne(fetch = FetchType.LAZY)
    private MenuData parent04;

    @ManyToOne(fetch = FetchType.LAZY)
    private MenuData reference;

    @Column
    private String referencePath;
    
    @Column
    private String dateType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date expirationTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date date01;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date date02;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date date03;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date date04;
    
    @Column
    private String date01String;
    
    @Column
    private String date02String;
        
    @Column
    private String date03String;

    @Column
    private String date04String;
    
    @Column
    private String code01CodeSystem;

    @Column
    private String code01Code;
//
//    @Column
//    private String code02Source;
//
//    @Column
//    private String code02;
//
//    @Column
//    private String code03Source;
//
//    @Column
//    private String code03;
//
//    @Column
//    private String code04Source;
//
//    @Column
//    private String code04;

    @Column
    private String string01;
    
    @Column
    private String string02;

    @Column
    private String string03;

    @Column
    private String string04;
    
    @Column
    private String string05;

    @Column
    private String string06;
    
    @Column
    private String string07;
 
    @Column
    private String string08;
 
    @Column
    private Long long01;
    
    @Column
    private Long long02;
    
    @Column
    private Long long03;
    
    @Column
    private Long long04;
    
    @Column
    private Double pqValue01;
    
    @Column
    private String pqStringVal01;
    
    @Column
    private String pqUnits01;
    
    @Column
    private Double pqValue02;
    
    @Column
    private String pqStringVal02;
    
    @Column
    private String pqUnits02;
    
    @Column
    private Double pqValue03;
    
    @Column
    private String pqStringVal03;
    
    @Column
    private String pqUnits03;
    
    @Column
    private Double pqValue04;
    
    @Column
    private String pqStringVal04;
    
    @Column
    private String pqUnits04;
    
    @Column
    private String sourceAccountId;
    
    @Column
    private String documentPath;

    @Column
    private String documentPathVariable;
    
    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column
    private byte[] xml01;

    @Transient
    Map<String, Object> extendedFields;

    @Transient
    transient boolean saveExtendedFields;
    
//    @Version
//    @Column
//    private Integer version;
//    
//    public Integer getVersion() {return version;}
    
    public MenuData() {
		super();
	}
    
//    /**
//     * Copy constructor
//     * @param md
//     */
//    public MenuData(MenuData md) {
//    	super();
//		setMenuStructure(md.getMenuStructure());
//		setAccount(md.getAccount());
//		setReference(md.getReference());
//		setParent01(md.getParent01());
//		setAccount(md.getAccount());
//		setDocumentId(md.getDocumentId());
//		setParent01(md.getParent01());
//		setParent02(md.getParent02());
//		setParent03(md.getParent03());
//		setParent04(md.getParent04());
//		setString01(md.getString01() );
//		setString02( md.getString02());
//		setString03( md.getString03());
//		setString04( md.getString04());
//		setString05( md.getString05());
//		setString06( md.getString06());
//		setDate01( md.getDate01());
//		setDate02( md.getDate02());
//		setDate03( md.getDate03());
//		setDate04( md.getDate04());
//		setPqStringVal01( md.getPqStringVal01());
//		setPqStringVal02( md.getPqStringVal02());
//		setPqStringVal03( md.getPqStringVal03());
//		setPqStringVal04( md.getPqStringVal04());
//		setPqValue01( md.getPqValue01());
//		setPqValue02( md.getPqValue02());
//		setPqValue03( md.getPqValue03());
//		setPqValue04( md.getPqValue04());
//		setPqUnits01( md.getPqUnits01());
//		setPqUnits02( md.getPqUnits02());
//		setPqUnits03( md.getPqUnits03());
//		setPqUnits04( md.getPqUnits04());
//		setLong01( md.getLong01());
//		setLong02( md.getLong02());
//		setLong03( md.getLong03());
//		setLong04( md.getLong04());
//		setXml01( md.getXml01());
//    }

    /**
     * Compare two MenuData objects for equality. This test is based on the surrogate ID, not the
     * data contents.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof MenuData)) return false;
        if (this.getId()==0 || ((MenuData)obj).getId()==0) {
        	throw new RuntimeException( "Menudata that has not been persisted cannot be compared for equality " + this);
        }
        if (this.getId()==((MenuData)obj).getId()) return true;
        return false;
    }

    /**
     * Provide a debug string representation for this object.
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer sb = new StringBuffer( );
    	sb.append("[MenuData] Account: ");
    	if (account==null) {
    		sb.append("null");
    	} else {
    		sb.append(getAccount().getId());
    	}
    	sb.append(" Path: ");
    	if (path==null) {
    		sb.append("null");
    	} else {
    		sb.append(path);
    	}
    	return sb.toString();
    }
    /**
     * Return a hash code for this object. Note: The hashCode is based on the id
     * and therefore cannot be determined until the ide is created which will be after a call to
     * em.persist().
     * @exception IllegalStateException if the id has not been established
     */
    public int hashCode() {
    	if (getId()==0) throw new IllegalStateException( "id not yet established in MenuData object");
        return new Long( getId()).hashCode();
    }

    /**
     * Get the named id from this MenuData item. For example, if looking for "patient", we'll have to look
     * at the MenuStructure to see what the node name of this item is. Logically, the MS might be: 
     * <pre>echr:patients:patient</pre>
     * If it is "patient", then we can return the
     * id of this object. However, if not, we'll look at the rest of the path in MenuStrcture for a match in which 
     * case we will use one of the parentKeys.
     * @param name the node name of the key value requested
     * @return The id of the key or zero if no match found.
     * @throws IllegalArgumentException if name is null or MenuStructure link is null
     */
    public long getKey( String name ) {
    	if (name==null) throw new IllegalArgumentException( "Name passed to getKey cannot be null");
    	if (getMenuStructure()==null ) throw new IllegalArgumentException( "MenuData must have a MenuStructure");
    	if (name.equals(menuStructure.getRepeating())) return this.getId();
    	int plevel = 0;
    	MenuStructure ms = getMenuStructure().getParent();
    	// Look up path
    	while ( ms != null ) {
    		if (ms.getRepeating()!=null) {
    			plevel++;
    			if (name.equals(ms.getRepeating())) break;
    		}
    		ms = ms.getParent();
    	}
    	if (plevel==1) return this.getParent01().getId();
    	if (plevel==2) return this.getParent02().getId();
    	if (plevel==3) return this.getParent03().getId();
    	if (plevel==4) return this.getParent04().getId();
    	return 0;
    }
//    
//    // This value never gets persisted. Used to indicate that this is a new entity prior to populating the real path
    private static final String newPath = "%%%new%%%";

    /**
     * Pre-persist denormalization
     */
    @PrePersist
    protected void denormalize() {
    	denormalizeAccount();
    	denormalizeParentPaths();
    	denormalizeReferencePath();
    	setPath(newPath);
    }
    
    public void initPath() {
    	setPath(calculatePath());
    }
    
//    @PostPersist
//    protected void setupPath(){
//    	if (path.equals(newPath)) {
//        	setPath(calculatePath());
//    	}
//    }
    
	/**
     * Account is denormalized from MenuStructure into MenuData. We need it here for performance and security.
     * For performance, we avoid a join with MenuStructure and for security, we can verify that the user
     * cannot query outside their own account. 
     */
    protected void denormalizeAccount() {
    	setAccount( getMenuStructure().getAccount());
    }

    /**
     * As a MenuData item is persisted, get the path specification from all referenced "parent" menuData items. 
     */
    protected void denormalizeParentPaths() {
    	if (this.getParent01()!=null) setParentPath01( getParent01().getPath());
    	if (this.getParent02()!=null) setParentPath02( getParent02().getPath());
    	if (this.getParent03()!=null) setParentPath03( getParent03().getPath());
    	if (this.getParent04()!=null) setParentPath04( getParent04().getPath());
    }

    protected void denormalizeReferencePath() {
    	if (this.getReference()!=null) setReferencePath( getReference().getPath());
    }

    /**
     * <p>As a MenuData item is persisted, a full path is created. For example, a MenuData item for a lab result 
     * for a patient might be: echr:patient-123:results:lab-456 which means the second and forth nodes in the
     * path should have corresponding ids either from a parent attribute (patient in this case) or the ID of this object (in this case, the lab result itself).</p> 
     * <p>This path should look similar to the path attribute in MenuStructure except that it has the keys inserted where appropriate.
     * So, in the previous example, the menuStructure.getPath(): echr:patient:lab</p>
     * <p>This method will not work at all unless this entity (and its parents) have been persisted so that they have Ids or if the menuStructure specifies a uniqueKey for the item.</p>
     * @Return calculated path 
     */
    public String calculatePath() {
    	MenuStructure ms = getMenuStructure();
    	if (ms==null) {
			throw new RuntimeException( "Null MenuStructure in MenuData not allowed");
    	}
//		if (getId()==0 && ms.getUniqueKey()==null) {
//			throw new RuntimeException( "MenuData instance of (" + ms + ") must have an Id or a uniqueKey");
//		}
		if (getAccount()==null) {
			throw new RuntimeException( "Missing account in MenuData instance of (" + ms + ")");
		}
		Object key;
    	// We either use the id of this item or the unique key field if applicable
    	if (ms.getUniqueKey()!=null) {
    		key = getField(ms.getUniqueKey() );
    		// If it's menuData, then just store the id of the menuData item
    		if (key instanceof MenuData) {
    			key = ((MenuData)key).getId();
    		}
    		// Can't allow a null unique key
    		if (key==null) {
    			throw new RuntimeException( "Null uniqueKey in MenuData instance of (" + ms + ")");
    		}
    	} else {
//    		key = Math.round(Integer.MAX_VALUE*Math.random());
    		key = getId();
    	}
    	StringBuffer sb = new StringBuffer(150);
    	MenuData owner = getParent01();
    	// Get the owner's path so we can grab placeholder id's from it
    	// Note: Use of the term placeholderID here is not the same as used in PlaceholderID entity. 
    	Map<String, Long> placeholderIds;
    	MenuPath ownerPath = null;
    	if (owner!=null) {
        	ownerPath = new MenuPath( owner.getPath());
        	placeholderIds = ownerPath.getNodeValues();
    	} else {
    		// Empty placeholder id map
    		placeholderIds = new HashMap<String, Long>(0);
    	}
    	// Climb up the menuStructure hierarchy
    	MenuStructure p = ms.getParent();
    	Stack<MenuStructure> parents = new Stack<MenuStructure>();
    	while (p!=null) {
    		parents.push( p );
    		p = p.getParent();
    	}
    	// Now construct the path from top to us
    	while (parents.size() > 0) {
    		MenuStructure pms = parents.pop();
    		sb.append( pms.getNode() );
    		// Placeholders need an ID
    		if (MenuStructure.PLACEHOLDER.equals(pms.getRole())) {
    			Long id = placeholderIds.get(pms.getNode());
    			if (id==null) {
    				throw new RuntimeException( "Missing placeholder " + pms.getNode() + " for new MenuData " + getId() );
    			}
    	    	sb.append( "-" );
    	    	sb.append(id);
    		}
    		// Add path separator
    		sb.append( ":" );
    	}
    	// Last node is always this data item
    	sb.append(ms.getNode());
    	// And it must have an ID
    	sb.append( "-" );
    	sb.append( key );
    	return sb.toString();
    }

    /**
     * return the unique id of this MenuData item
     * @return a unique id, greater than 0
     */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	/**
	 * <p>Optionally references the document which gave rise to this MenuData item.
	 * If the MenuData item is a summary, then this will be the last document contributing to its value.
	 * If the NenuData item is updateable, then this will be the last document affecting the value(s).
	 * One document may affect many MenuData items. For example, a single CBC lab result might have separate
	 * MenuData items for each test value (RBC, WBC, HCT, HGB, etc).</p>
	 * @return ID of document or zero if no document
	 */
	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}
	
	/**
	 * Return true if this menudata item is an item on a list.
	 * @return true if the item occurs on a list
	 */
	public boolean isListItem( ) {
		String role = getMenuStructure().getRole();
		return (MenuStructure.LIST.equals(role) 
			 || MenuStructure.TRIMLIST.equals(role) 
			 || MenuStructure.PORTLET.equals(role)
			 || MenuStructure.BAND.equals(role)
			 || MenuStructure.ENTRY.equals(role) );
	}
	
	/**
	 * <p>If this menuData item is in a list, then return a portion of the path that represents the list itself.
	 * For example, if the path is <code>echr:patient-123:problems:active-456</code>, then the list path returned will be
	 *  <code>echr:patient-123:problems:active</code></p>
	 *  <p>This path is used in MenuDataVersion.</p>
	 * @return The listPath
	 */
	public String getListPath( ) {
		if (!isListItem()) throw new IllegalStateException( toString() + " is not a list items");
		return getPath().substring(0, getPath().lastIndexOf('-'));
	}
	
	/**
	 * If this is an Event, return the underlying placeholder MenuStructure, if possible. 
	 * @return MenuStructure
	 */
    public AccountMenuStructure getPlaceholderMenuStructure() {
    	// If this is an event, then we'll use the metadata of the placeholder.
    	if ("event".equals(menuStructure.getRole()) && getReference() != null) {
    		return getReference().getMenuStructure();
    	}
		return menuStructure;
	}
    
	/**
	 * Required reference to the MenuStructure item that defines this MenuData item. 
	 * @return MenuStructure
	 */
    public AccountMenuStructure getMenuStructure() {
		return menuStructure;
	}

	public void setMenuStructure(AccountMenuStructure menuStructure) {
		this.menuStructure = menuStructure;
	}

	public MSColumn getColumn( String fieldName ) {
		if (getColumnMap()==null) return null;
		return getColumnMap().get(fieldName);
	}

	
	/**
	 * Get an extended field. Any column definition that doesn't mention an internal
	 * column comes here. Extended fields are stored in an XML structure.
	 * @param fieldName
	 * @return field value
	 */
	public Object getExtendedField( String fieldName ) {
		Object value = getExtendedFields().get(fieldName);
		return value;
	}
	
	private transient int modifyCount = 0;
	
	public void setExtendedField( String fieldName, Object fieldValue ) {
		getExtendedFields().put(fieldName, fieldValue);
		saveExtendedFields = true;
		// The following is a necessary hack to ensure that we update the extended fields during postPersist since these fields are
		// transient - they alone won't update the MenuData instance in the DB.
		byte bytes[] = new byte[4];
		int c = modifyCount++;
		bytes[0] = (byte)(c % 256);
		c = c / 256;
		bytes[1] = (byte)(c % 256);
		c = c / 256;
		bytes[2] = (byte)(c % 256);
		c = c / 256;
		bytes[3] = (byte)(c % 256);
		setXml01(bytes);
	}
	
	/**
	 * A map of additional attributes held by this MenuData item. The map itself is not persisted but 
	 * is populated by reading the contents from an xml representation of the fields.
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	Map<String, Object> getExtendedFields() {
		if (extendedFields==null) {
			extendedFields = new HashMap<String, Object>();
		}
		return extendedFields;
	}
	/**
	 * Set the value for a field using the name assigned by the placeholder column definition. 
	 * If the field is extended, then we always store the type of object provided. If it is an internal field, then we
	 * store it in the type of that internal field in which case we can provide some trivial type conversion such as string to long or long to string.
	 * @param fieldName
	 * @param fieldValue
	 */
	public void setField( String fieldName, Object value ) {
		Object fieldValue;
		// In general, empty strings are treated as null
		if (value instanceof String && ((String)value).length()==0) {
			fieldValue = null;
		} else {
			fieldValue = value;
		}
		String resolvedName;
		if (getMenuStructure()!=null) {
			MSColumn column = getColumn( fieldName );
			if ( column!=null ) {
				if (column.isExtended()) {
					setExtendedField( column.getHeading(), fieldValue );
					return;
				}
				resolvedName = column.getInternal();
			} else {
				resolvedName = fieldName;
			}
		} else {
			resolvedName = fieldName;
		}
		if (isInternalField(resolvedName)) {
			setInternalField( resolvedName, fieldValue);
			return;
		}
		throw new IllegalArgumentException( "Invalid menuData field name: " + fieldName + " (" + resolvedName + ")");
	}
	
	private static Long coerceLong( Object value ) {
		if (value == null) return null;
		if (value instanceof Long ) return (Long) value;
		if (value instanceof Integer) return (Long) value;
		if (value instanceof String) return Long.parseLong((String) value);
		throw new RuntimeException( "Cannot convert " + value + " to Long value");
	}

	/**
	 * Get a computed field, based on froms. A computed field is not the same as a formatted field as returned by getMenuDataByColumns.
	 * @param col
	 * @return The first non-null value returned from evaluating each from in turn
	 */
	public Object getComputedField( MSColumn col) {
		ExpressionEvaluator ee = new TrimExpressionEvaluator();
		ee.addVariable(getMenuStructure().getNode(), this);
		MenuData reference = getReference();
		if (reference!=null) {
			ee.addVariable(reference.getMenuStructure().getNode(), reference);
		}
		Object val = null;
		for (String from : col.getFroms()) {
			val = ee.evaluate(from);
			if (val!=null) break;
		}
		return val;
	}
	public Map<String, MenuData> getPlaceholder() {
		return new MDPlaceholderFieldMap(this);
	}
	
	public Map<String, String> getString() {
		return new MDStringFieldMap(this);
	}
	
	public Map<String, Long> getLong() {
		return new MDLongFieldMap(this);
	}
	public Map<String, Date> getDate() {
		return new MDDateFieldMap(this);
	}
	/**
	 * Return a field from MenuData by the name assigned by the Placeholder Columns in application.xml. 
	 * @param fieldName
	 * @return the column value (which may be null)
	 */
	public Object getField( String fieldName ) {
		if (getMenuStructure()==null) return getInternalField(fieldName);
		String resolvedName;
		MSColumn column = null;
		try {
			int dotIndex = fieldName.indexOf(".");
			String prefix;
			String suffix;
			if (dotIndex >= 0) {
				prefix = fieldName.substring(0,dotIndex);
				column = getColumn( prefix );
				String internal = column.getInternal();
				Object val = getInternalField(internal);
				if (!(val instanceof MenuData)) {
					throw new RuntimeException( "Field " + prefix + " must be a placeholder");
				}
				suffix = fieldName.substring(dotIndex+1);
				return ((MenuData)val).getField(suffix);
			}
		}
		catch( Exception e) {
			throw new RuntimeException( "Error decoding field " + fieldName);
		}
		
		if (getMenuStructure()!=null) {
			column = getColumn( fieldName );
			if ( column!=null ) {
				if (column.isExtended()) {
					return getExtendedField( column.getHeading() );
				}
				if (column.isComputed()) {
					return getComputedField( column );
				}
				resolvedName = column.getInternal();
			} else {
				resolvedName = fieldName;
			}
		} else {
			resolvedName = fieldName;
		}
		String internalNames[];
		String format;
		if ("reference".equals(resolvedName)) {
			if (column.getDisplayFunctionArguments()!=null) {
				internalNames = column.getDisplayFunctionArguments().split("\\,");
				format = column.getDisplayFunction();
			} else {
				internalNames = column.getDisplayFunction().split("\\,");
				format = null;
			}
		} else {
			internalNames = resolvedName.split("\\,");
			format = null;
		}
		if (internalNames.length==1) {
			return getInternalField( internalNames[0] );
		}
		// At this point, we're being asked to concatenate/format the field
		Object dfavals[] = new Object[internalNames.length];
		for ( int x = 0; x < dfavals.length; x++) {
			dfavals[x] = getInternalField( internalNames[x]);
		}
		if (format!=null) {
			// Notice that the locale MUST be hardcoded to US.
			return String.format(Locale.US, format, dfavals);
		} else {
			// Otherwise, just do a simple string concatenation
			StringBuffer sb = new StringBuffer( 256 );
			for (Object field : dfavals) {
				sb.append(field);
			}
			return sb.toString();
		}
	}

	private static Set<String> internalFields;
	public static Map<String, Method> internalSetters = new HashMap<String, Method>();
	public static Map<String, Method> internalGetters = new HashMap<String, Method>();
	static {
		internalFields = new HashSet<String>( );
		for (Method method : MenuData.class.getMethods()) {
			String methodName = method.getName();
			if (methodName.startsWith("set") 
					&& method.getReturnType()==void.class 
					&& method.getParameterTypes().length==1 
					&& !methodName.equals("setInternalField")) {
				String attribute = methodName.substring(3,4).toLowerCase() + methodName.substring(4);
				internalSetters.put(attribute, method);
				internalFields.add(attribute);
			}
			if (methodName.startsWith("get") 
					&& method.getReturnType()!=void.class 
					&& method.getParameterTypes().length==0 
					&& !methodName.equals("getInternalField")) {
				String attribute = methodName.substring(3,4).toLowerCase() + methodName.substring(4);
				internalGetters.put(attribute, method);
			}
		}
	}
	
	/**
	 * Return true if the named field is an internal field name
	 * @param fieldName
	 * @return
	 */
	public static boolean isInternalField( String fieldName ) {
		return internalFields.contains(fieldName);
	}
	
	public static String[] getInternalFields(){
		return internalFields.toArray(new String[internalFields.size()]) ;
		}
	
	public Object getInternalField( String fieldName ) {
		Method method = internalGetters.get(fieldName);
		if (method==null) {
			throw new IllegalArgumentException( "Invalid menuData field name: " + fieldName);
		}
		try {
			return method.invoke(this);
		} catch (Exception e) {
			throw new IllegalArgumentException( "Error getting menuData field: " + fieldName, e);
		}
	}
	/**
	 * Set the value of an internal field.
	 * @param fieldName
	 * @param value
	 */
	public void setInternalField( String fieldName, Object value ) {
		Method method = internalSetters.get(fieldName);
		if (method==null) {
			throw new IllegalArgumentException( "Invalid menuData field name: " + fieldName );
		}
		Object normalizedValue;
		if (value==null) {
			normalizedValue = null;
		} else {
			Class<?> paramClass = method.getParameterTypes()[0];
			if (paramClass.isInstance(value)) {
				normalizedValue = value;
			} else {
				// Cast to string?
				if (String.class.isAssignableFrom(paramClass) && value instanceof Number) {
					normalizedValue = value.toString();
				} else if (Long.class.isAssignableFrom(paramClass) && value instanceof String) {
					normalizedValue = Long.parseLong((String)value);
				} else if (Long.class.isAssignableFrom(paramClass) && value instanceof String) {
					normalizedValue = Long.parseLong((String)value);
				} else {
					throw new RuntimeException( "Type mismatch in setInternalField (" + value.getClass().getName() + ") requires: " + method.toString());
				}
			}
		}
		try {
			method.invoke(this, normalizedValue);
		} catch (Exception e) {
			throw new IllegalArgumentException( "Error setting menuData field name: " + fieldName + " value " + value, e);
		}
	}
	
	public Date getDateField( String fieldName ) {
		Object result = getField( fieldName );
		if ( result==null ) return null;
		if (result instanceof Date) return (Date) result;
		throw new IllegalArgumentException( "Field: " + fieldName + " is not a Date");
	}
	
	public String getStringField( String fieldName ) {
		Object result = getField( fieldName );
		if ( result==null ) return null;
		if (result instanceof String) return (String) result;
		throw new IllegalArgumentException( "Field: " + fieldName + " is not a String");
	}

	public Long getLongField( String fieldName ) {
		Object result = getField( fieldName );
		if ( result==null ) return null;
		if (result instanceof Long) return (Long) result;
		throw new IllegalArgumentException( "Field: " + fieldName + " is not a Long");
	}
	
	public Boolean getBooleanField( String fieldName ) {
		Object result = getField( fieldName );
		if ( result==null ) return null;
		if (result instanceof Boolean) return (Boolean) result;
		throw new IllegalArgumentException( "Field: " + fieldName + " is not a Boolean");
	}
	
	public Double getDoubleField( String fieldName ) {
		Object result = getField( fieldName );
		if ( result==null ) return null;
		if (result instanceof Double) return (Double) result;
		throw new IllegalArgumentException( "Field: " + fieldName + " is not a Double");
	}
	
	public Date getInternalDateField( String fieldName ) {
		Object value = getInternalField( fieldName );
		if (value instanceof Date) return (Date) value;
		return null;
	}
	
	public Long getInternalLongField( String fieldName ) {
		Object value = getInternalField( fieldName );
		if (value instanceof Long) return (Long) value;
		return null;
	}

	public String getInternalStringField( String fieldName ) {
		Object value = getInternalField( fieldName );
		if (value instanceof String) return (String) value;
		return null;
	}

	public Double getInternalPQValueField( String fieldName ) {
		Object value = getInternalField( fieldName );
		if (value instanceof Double) return (Double) value;
		return null;
	}

	public MenuData getParentPath( String fieldName ) {
		Object value = getInternalField( fieldName );
		if (value instanceof MenuData) return (MenuData) value;
		return null;
	}

	/**
     * An an arbitrary date, the contents and description of which is determined by a rule defined in MenuStructure
     * @return Date
     */
	public Date getDate01() {
		return date01;
	}

	public void setDate01(Date date01) {
		this.date01 = date01;
	}
	
    /**
     * An an arbitrary date, the contents and description of which is determined by a rule defined in MenuStructure
     * @return Date
     */
	public Date getDate02() {
		return date02;
	}

	public void setDate02(Date date02) {
		this.date02 = date02;
	}

    /**
     * An an arbitrary date, the contents and description of which is determined by a rule defined in MenuStructure
     * @return Date
     */
	public Date getDate03() {
		return date03;
	}

	public void setDate03(Date date03) {
		this.date03 = date03;
	}

    /**
     * An an arbitrary date, the contents and description of which is determined by a rule defined in MenuStructure
     * @return Date
     */
	public Date getDate04() {
		return date04;
	}

	public void setDate04(Date date04) {
		this.date04 = date04;
	}

    /**
     * An an arbitrary integer, the contents and description of which is determined by a rule defined in MenuStructure
     * @return long
     */
	public Long getLong01() {
		return long01;
	}

	public void setLong01(Long long01) {
		this.long01 = long01;
	}

    /**
     * An an arbitrary integer, the contents and description of which is determined by a rule defined in MenuStructure
     * @return long
     */
	public Long getLong02() {
		return long02;
	}

	public void setLong02(Long long02) {
		this.long02 = long02;
	}

    /**
     * An an arbitrary integer, the contents and description of which is determined by a rule defined in MenuStructure
     * @return long
     */
	public Long getLong03() {
		return long03;
	}

	public void setLong03(Long long03) {
		this.long03 = long03;
	}

    /**
     * An an arbitrary integer, the contents and description of which is determined by a rule defined in MenuStructure
     * @return long
     */
	public Long getLong04() {
		return long04;
	}

	public void setLong04(Long long04) {
		this.long04 = long04;
	}

	/**
	 * <p>An arbitrary Physical Quantity. PQs are in "value" and "unit of measure" pairs. The value is a real number, the unit of measure is
	 * defined by HL7 datatype PQ. However, the actual representation here may differ from the PQ in the document and thus what may 
	 * actually be displayed. For example, say a lab result has a PQ of 1.000 mcg/mL. When stored here, the precision of the value is lost because
	 * neither Java nor the database retain that information in a real datatype. So it may be that the display of the PQ occurs
	 * in a string field which this is used only for graphing and perhaps sorting purposes. </p>
	 * <p>A unit of measure should always be present in a PQ. If no other unit fits, then a "1" is implied.
	 * @return A String containing the unit of measure.
	 */
	public String getPqUnits01() {
		return pqUnits01;
	}

	public void setPqUnits01(String pqUnits01) {
		this.pqUnits01 = pqUnits01;
	}

	public String getPqUnits02() {
		return pqUnits02;
	}

	public void setPqUnits02(String pqUnits02) {
		this.pqUnits02 = pqUnits02;
	}

	public String getPqUnits03() {
		return pqUnits03;
	}

	public void setPqUnits03(String pqUnits03) {
		this.pqUnits03 = pqUnits03;
	}

	public String getPqUnits04() {
		return pqUnits04;
	}

	public void setPqUnits04(String pqUnits04) {
		this.pqUnits04 = pqUnits04;
	}

	public Double getPqValue01() {
		return pqValue01;
	}

	public void setPqValue01(Double pqValue01) {
		this.pqValue01 = pqValue01;
	}

	public Double getPqValue02() {
		return pqValue02;
	}

	public void setPqValue02(Double pqValue02) {
		this.pqValue02 = pqValue02;
	}

	public Double getPqValue03() {
		return pqValue03;
	}

	public void setPqValue03(Double pqValue03) {
		this.pqValue03 = pqValue03;
	}

	public Double getPqValue04() {
		return pqValue04;
	}

	public void setPqValue04(Double pqValue04) {
		this.pqValue04 = pqValue04;
	}

	/**
	 * An arbitrary string used to describe a MenuData item. The rule associated with the MenuStructure item creates and updates this string.
	 * @return
	 */
	public String getString01() {
		return string01;
	}

	public void setString01(String string01) {
		this.string01 = string01;
	}

	public String getString02() {
		return string02;
	}

	public void setString02(String string02) {
		this.string02 = string02;
	}

	public String getString03() {
		return string03;
	}

	public void setString03(String string03) {
		this.string03 = string03;
	}

	public String getString04() {
		return string04;
	}

	public void setString04(String string04) {
		this.string04 = string04;
	}

	/**
	 * <p>An arbitrary XML snippet associated with a MenuData item. This attribute is usually the last resort as a means of displaying information.
	 * the most efficient technique is usually to store each display attribute in one of the explicit datatypes in this entity.
	 * the most thorough method is to provide a drilldown to the actual underlying document using the document attribute. In these fields, an
	 * XML snippet, the schema of which is known to the menu structure, holds either a copy of part of the document or a completely separate snippet created for display purpose.</p>
	 * <p>Consider Patient name as an example: A rule can extract patient name information for a complete document and store it in a single String field.
	 * While most efficient, it means the name format is determined, forever more, by the rule, not by the display template.
	 * Instead, the display could navigate into the document to pull out the name field. But this could require coupling display logic to the possible many different document 
	 * formats containing name information. A compromise provides maximum decoupling: The rule knows where to find the name information and provides it to the application
	 * as an XML snippet (making no assumption about how it will be displayed). The display template can then decide how to display the name without having to know anything 
	 * about the original transaction.</p>
	 * <p>Performance note: One larger XML snippet is more efficient than two smaller ones, all else being equal.</p>
	 * @return XML Snippet
	 */
	public byte[] getXml01() {
		return xml01;
	}

	public void setXml01(byte[] xml01) {
		this.xml01 = xml01;
	}
	
//	public byte[] getXml02() {
//		return xml02;
//	}
//
//	public void setXml02(byte[] xml02) {
//		this.xml02 = xml02;
//	}
//
//	public byte[] getXml03() {
//		return xml03;
//	}
//
//	public void setXml03(byte[] xml03) {
//		this.xml03 = xml03;
//	}
//
//	public byte[] getXml04() {
//		return xml04;
//	}
//
//	public void setXml04(byte[] xml04) {
//		this.xml04 = xml04;
//	}
	
	/**
	 * <p>Expiration time, if non-null, defines when a particular MenuData item expires. The query for display will always
	 * ignore any items where the current time (now) is after expirationTime. For example, an appointment list item might 
	 * automatically be removed after a certain time.</p>
	 * <p>Once a MenuData item has expired, a utility program is free to delete the item from the database. In such cases, 
	 * the item should have no intrinsic value for any purpose. If that is NOT the case, use a different attribute to store the date.
	 * For example, if there is an expiration time associated with a medication product, that would have no 
	 * bearing on whether the item is displayed or not.</p>
	 * <p>In summary, this field means that the MenuData item has no value whatsoever after this expiration time. If the expirationTime is
	 * null, the item never exists.</p>
	 * @return a date and time value which may be null.
	 */
	public Date getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	/**
	 * <p>A MenuStructure item can define a repeating data item. For example, 
	 * a MenuStructure defining HealthRecord at the top level and then appointment (encounter) within that healthRecord
	 * will use parent01  (all Tolven surrogate keys are long value). These keys are typically not displayed.
	 * Instead, "real world" keys, such as medical record number, are extracted from the document and displayed, if needed.
	 * Therefore, these key are typically used in a hierarchy starting with whole and proceeding toward the parts.</p>
	 * <p>Account id should not be included in these keys since in most cases the account id is the overall security 
	 * partitioning value. Thus, including accountId as a key would never provide access to an account beyond the 
	 * account owning this MenuData item. (Sub-accounts would be reasonable to include in a key, if appropriate).
	 * </p>
	 * <p>If the MenuStructure defines only a single MenuData item, which is rare but possible, all key values would contain
	 * zero. Example, a clinic keeps a summary item showing the total number of active patients.</p>
	 * <p>These arbitrary keys represent an important architectural concept: Tolven makes no intrinsic assumption about the organization 
	 * structure of an account. A single Tolven installation can handle patients (clinical), subjects (research), and family members (personal). 
	 * Note, patients can be humans or animals without affecting the core structure. However, that distinction is due to HL7 RIM
	 * more than Tolven since the patient role itself is separate from the living subject playing that role.</p>
	 * @return Surrogate key value
	 */
	public MenuData getParent01() {
		return parent01;
	}
	public void setParent01(MenuData parent01) {
		this.parent01 = parent01;
	}
	public MenuData getParent02() {
		return parent02;
	}
	public void setParent02(MenuData parent02) {
		this.parent02 = parent02;
	}
	public MenuData getParent03() {
		return parent03;
	}
	public void setParent03(MenuData parent03) {
		this.parent03 = parent03;
	}
	public MenuData getParent04() {
		return parent04;
	}
	public void setParent04(MenuData parent04) {
		this.parent04 = parent04;
	}

	public void setParents( MenuData parents[]) {
		int count = 0; 
		for (MenuData parent : parents ) {
			count++;
			if (count==1) setParent01( parent );
			if (count==2) setParent02( parent );
			if (count==3) setParent03( parent );
			if (count==4) setParent04( parent );
		}
	}

	/**
	 * Account is denormalized here (from MenuStructure) for performance and security: A user cannot access MenuData beyond their account.
	 * @return
	 */
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	public String getParentPath01() {
		return parentPath01;
	}
	public void setParentPath01(String parentPath01) {
		this.parentPath01 = parentPath01;
	}
	public String getParentPath02() {
		return parentPath02;
	}
	public void setParentPath02(String parentPath02) {
		this.parentPath02 = parentPath02;
	}
	public String getParentPath03() {
		return parentPath03;
	}
	public void setParentPath03(String parentPath03) {
		this.parentPath03 = parentPath03;
	}
	public String getParentPath04() {
		return parentPath04;
	}
	public void setParentPath04(String parentPath04) {
		this.parentPath04 = parentPath04;
	}

	/**
	 * Path is a denormalization of the hierarchical nodes collected into a single string. example: "echr:patient".
	 * If called before the path is calculated, we will calculate it now, however, the patch could change before persistence.
	 * @return
	 */
	public String getPath() {
		if (path==null) return calculatePath();
//		if (path==null || path.equals(newPath)) {
//			setPath( calculatePath());
//		}
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * A MenuData reference provides a way of referencing another object such as on lists.
	 * For example, say patients are represented by the <i>echr:patient</i> path. Then there might be a patient list
	 * <i>echr:patients</i> with each entry being a reference to a echr:patient item.
	 * @return
	 */
	public MenuData getReference() {
		return reference;
	}
	public void setReference(MenuData reference) {
		this.reference = reference;
	}
	public String getReferencePath() {
		return referencePath;
	}
	public void setReferencePath(String referencePath) {
		this.referencePath = referencePath;
	}
	/**
	 * Return the string representation of the physical quantity value.
	 * If only the real (double) value is available, convert the real to
	 * a string representation.
	 * @return
	 */
	public String getPqStringVal01() {
		if (pqStringVal01==null && pqValue01!=null) {
			return Double.toString( getPqValue01());
		}
		return pqStringVal01;
	}
	public void setPqStringVal01(String pqStringVal01) {
		this.pqStringVal01 = pqStringVal01;
	}
	public String getPqStringVal02() {
		if (pqStringVal02==null && pqValue02!=null) {
			return Double.toString( getPqValue02());
		}
		return pqStringVal02;
	}
	public void setPqStringVal02(String pqStringVal02) {
		this.pqStringVal02 = pqStringVal02;
	}
	public String getPqStringVal03() {
		if (pqStringVal03==null && pqValue03!=null) {
			return Double.toString( getPqValue03());
		}
		return pqStringVal03;
	}
	public void setPqStringVal03(String pqStringVal03) {
		this.pqStringVal03 = pqStringVal03;
	}
	public String getPqStringVal04() {
		if (pqStringVal04==null && pqValue04!=null) {
			return Double.toString( getPqValue04());
		}
		return pqStringVal04;
	}
	public void setPqStringVal04(String pqStringVal04) {
		this.pqStringVal04 = pqStringVal04;
	}
	public TrimHeader getTrimHeader() {
		return trimHeader;
	}
	public void setTrimHeader(TrimHeader trimHeader) {
		this.trimHeader = trimHeader;
	}
	public Status getStatus() {
		return status;
	}
	public String getStatusName() {
		if (status==null) return null;
		else return status.name();
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public Set<PlaceholderID> getPlaceholderIDs() {
		if (placeholderIDs==null) placeholderIDs = new HashSet<PlaceholderID>( 10 );
		return placeholderIDs;
	}

	public PlaceholderID[] getPlaceholderIDArray() {
		final PlaceholderID[] prototype =  new PlaceholderID[0];
		return  getPlaceholderIDs().toArray(prototype);
	}
	
	/**
	 * Add a new id to the existing list of placeholders as long as the new
	 * id is unique. 
	 * @param root
	 * @param extension
	 * @param assigningAuthority
	 */
	public void addPlaceholderID( String root, String extension, String assigningAuthority ) {
		PlaceholderID id = new PlaceholderID( );
		id.setMenuStrucuture(getMenuStructure());
		id.setAccount(getAccount());
		id.setPlaceholder(this);
		id.setRoot(root);
		id.setExtension(extension);
		id.setAssigningAuthority(assigningAuthority);
		if (!getPlaceholderIDs().contains( id )) {
			getPlaceholderIDs().add(id); 
		}
	}
	
	/**
	 * Return the set of words associated with this MenuDataItem
	 * @return
	 */ 
	public Set<MenuDataWord> getWords() {
		if (words==null) words = new HashSet<MenuDataWord>( 10 );
		return words;
	}
	
	public Map<String, PlaceholderID> getPlaceholderID() {
		return new PlaceholderIDMap(getPlaceholderIDs() );
	}
	
	/**
	 * Add a word to this MenuData Item we quietly ignore duplicates.

	 * @param word - the word to be added
	 * @param position - the position of the word in the phrase it was extract from
	 * @param field - which field or semantic relationship the word is from
	 * @param language - which language the phrase is in
	 */
	public void addWord( String word, int position, String field, String language ) {
		MenuDataWord newWord = new MenuDataWord( );
		newWord.setField(field);
		newWord.setLanguage(language);
		newWord.setPosition(position);
		newWord.setWord(word);
		newWord.setMenuData(this);
		newWord.setMenuStructure(this.getMenuStructure());
		if (!getWords().contains( newWord )) {
			getWords().add(newWord); 
		}
	}
	
	/**
	 * Add a phrase to this MenuData Item's word list. 
	 * @param phrase - the phrase to be added
	 * @param position - the position of the word in the phrase it was extract from
	 * @param field - which field or semantic relationship the word is from
	 * @param language - which language the phrase is in
	 */
	public void addPhrase( String phrase, String field, String language ) {
		if( phrase==null) return;
		// Split by any non-word characters
		String words[] = phrase.split("\\W");
		int position = 0;
		for ( String rawWord : words ) {
			String word = rawWord.trim().toLowerCase();
			if (word.length()>0 && !StopList.ignore( word, language )) {
				position++;
				addWord( word, position, field, language);
			}
		}
	}
	public String getString05() {
		return string05;
	}
	public void setString05(String string05) {
		this.string05 = string05;
	}
	public String getString06() {
		return string06;
	}
	public void setString06(String string06) {
		this.string06 = string06;
	}
	
	/**
	 * The Act Status of the underlying document, not of the MenuData item itself.
	 * @return
	 */
	public String getActStatus() {
		return actStatus;
	}
	
	public void setActStatus(String actStatus) {
		this.actStatus = actStatus;
	}
	
	/**
	 * A Transient attribute holding a map of the column definitions from the MenuStructure.
	 * @return
	 */
	public Map<String, MSColumn> getColumnMap() {
		if (columnMap==null) {
			columnMap=getPlaceholderMenuStructure().getColumnMap();
		}
		return columnMap;
	}
	
	public void setColumnMap(Map<String, MSColumn> columnMap) {
		this.columnMap = columnMap;
	}
//	public String getCode01Source() {
//		return code01Source;
//	}
//	public void setCode01Source(String code01Source) {
//		this.code01Source = code01Source;
//	}
//	public String getCode01() {
//		return code01;
//	}
//	public void setCode01(String code01) {
//		this.code01 = code01;
//	}
//	public String getCode02Source() {
//		return code02Source;
//	}
//	public void setCode02Source(String code02Source) {
//		this.code02Source = code02Source;
//	}
//	public String getCode02() {
//		return code02;
//	}
//	public void setCode02(String code02) {
//		this.code02 = code02;
//	}
//	public String getCode03Source() {
//		return code03Source;
//	}
//	public void setCode03Source(String code03Source) {
//		this.code03Source = code03Source;
//	}
//	public String getCode03() {
//		return code03;
//	}
//	public void setCode03(String code03) {
//		this.code03 = code03;
//	}
//	public String getCode04Source() {
//		return code04Source;
//	}
//	public void setCode04Source(String code04Source) {
//		this.code04Source = code04Source;
//	}
//	public String getCode04() {
//		return code04;
//	}
//	public void setCode04(String code04) {
//		this.code04 = code04;
//	}

	public String getSourceAccountId() {
		return sourceAccountId;
	}

	public void setSourceAccountId(String sourceAccountId) {
		this.sourceAccountId = sourceAccountId;
	}
	
	/**
	 * This path, when specified, provides an EL path to the location in the document where the placeholder 
	 * was derived from. The specified node will invariably contain a placeholder bind instruction if it is a trim document.
	 * @return
	 */
	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	
	/**
	 * The EL variable name corresponding to the document path. For example:
	 * <code>role</code> might be the variable associated with a path such as <code>trim.act.participation["subject"].role</code>
	 * @return
	 */
	public String getDocumentPathVariable() {
		return documentPathVariable;
	}

	public void setDocumentPathVariable(String documentPathVariable) {
		this.documentPathVariable = documentPathVariable;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public String getDateType() {
		return dateType;
	}

	public String getDate01String() {
		return date01String;
	}

	public void setDate01String(String date01String) {
		this.date01String = date01String;
	}

	public String getDate02String() {
		return date02String;
	}

	public void setDate02String(String date02String) {
		this.date02String = date02String;
	}

	public String getDate03String() {
		return date03String;
	}

	public void setDate03String(String date03String) {
		this.date03String = date03String;
	}

	public String getDate04String() {
		return date04String;
	}

	public void setDate04String(String date04String) {
		this.date04String = date04String;
	}

	public String getString07() {
		return string07;
	}

	public void setString07(String string07) {
		this.string07 = string07;
	}

	public String getString08() {
		return string08;
	}

	public void setString08(String string08) {
		this.string08 = string08;
	}

	public String getCode01CodeSystem() {
		return code01CodeSystem;
	}

	public void setCode01CodeSystem(String code01CodeSystem) {
		this.code01CodeSystem = code01CodeSystem;
	}

	public String getCode01Code() {
		return code01Code;
	}

	public void setCode01Code(String code01Code) {
		this.code01Code = code01Code;
	}
	
	public CD getCode01( ) {
		if (getCode01Code()==null) {
			return null;
		}
		CD cd = trimFactory.createCD();
		cd.setCodeSystem(getCode01CodeSystem());
		cd.setCode(getCode01Code());
		return cd;
	}
	
	public void setCode01( CD cd ) {
		if (cd==null) {
			setCode01CodeSystem( null );
			setCode01Code( null );
		} else {
			setCode01CodeSystem( cd.getCodeSystem() );
			setCode01Code( cd.getCode() );
		}

	}
}
