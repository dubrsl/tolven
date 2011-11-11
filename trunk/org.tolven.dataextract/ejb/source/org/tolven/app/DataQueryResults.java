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
 * @version $Id: DataQueryResults.java,v 1.5 2010/04/11 17:58:09 jchurin Exp $
 */  

package org.tolven.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.Account;

public class DataQueryResults {
	
	private List<DataField> fields;
	private List<DataField> parentFilterFields;	// Added a new attribute to retrieve the specified parent fields
	private AccountMenuStructure ms;
	private Iterator<Object> iterator;
	private Date now;
	private int offset;
	private int limit;
	private String order[];
	private String filter;
	private MenuPath menuPath;
	private boolean itemQuery = false;
	private boolean returnTotalCount = false;
	private boolean returnFilterCount = false;
	private int count = 0;
	private int level;	
	/**
	 * Construct a Data Query structure
	 * @param ms
	 */
	public DataQueryResults( AccountMenuStructure ms) {
		this.ms = ms;
	}
	
	/**
	 * Create the crude set of possible fields, later to be pruned/selected as needed.
	 * We create the field array by looking at the following sources:
	 * 1. The ID/primary key
	 * 2. parent placeholder (owner) when present (yields id/foreign key)
	 * 3. Referenced fields such as when a list references a placeholder. A list and
	 * the placeholder it references can have the same fields. However, the fields have different names:
	 * the referenced fields will be prefixed by the placeholder name.
	 * 4. Explicit column definitions
	 * These are processed from back to front (and explicit field wins over an implicit field)
	 * Each field that references a parent internal field will then examine all it's fields, etc.
	 */
	public void populateFieldSet(AccountMenuStructure ms, String externalPrefix, String internalPrefix, Set<DataField> dataFieldSet ) {
		if (ms==null) {
			throw new RuntimeException( "Missing AccountMenuStructure in " + DataQueryResults.class.getSimpleName());
		}
		for (MSColumn col : ms.getColumns()) {
//			Set<String> columnNames = new HashSet<String>();
//			// Get the raw column names needed for this column
//			col.extractColumnNames(columnNames);
			String displayFunction = col.getDisplayFunction();
			if (col.getDisplayFunctionArguments()!=null) continue;
			boolean enabled = internalPrefix.length()==0;
			String external = externalPrefix + col.getHeading();
			if (col.getInternal()!=null && col.getInternal().matches("parent\\d\\d")) {
				String internal = internalPrefix + col.getInternal();
				// Prepare for future expansion
//				MenuStructure msPlaceholder = col.getPlaceholder();
//				populateFieldSet( msPlaceholder, msPlaceholder.getNode()+".", col.getInternal(), dataFieldSet);
				dataFieldSet.add(new DataField( external+".id", internal+".id", enabled, displayFunction));
			} else if (col.getInternal()!=null && col.getInternal().matches("code\\d\\d")) {
				String internal = internalPrefix + col.getInternal();
				dataFieldSet.add(new DataField( external+".code", internal+"Code", enabled, displayFunction));
				dataFieldSet.add(new DataField( external+".codeSystem", internal+"CodeSystem", enabled, displayFunction));
			} else {
				if (!col.isComputed() && !col.isReference()) {
					String internal;
					if(col.getInternal()!=null)
						internal = internalPrefix + col.getInternal();
					else
						internal = internalPrefix + "_extended";
					dataFieldSet.add(new DataField( external, internal, enabled, displayFunction));
				}				
				if (col.isReference()) {
					String internal = internalPrefix + displayFunction;
					dataFieldSet.add(new DataField( external, internal, enabled, displayFunction));
				}
			}
		}
		// Drill down to placeholder
		AccountMenuStructure msReferenced = ms.getReferenced(); 
		if (msReferenced!=null) {
			populateFieldSet( msReferenced, msReferenced.getNode()+".", "referenced.", dataFieldSet);
		}
		// If we refer to a drilldown placeholder, get it, too. Note: Placeholders never have a drilldown, thought they may have parents.
		if (!MenuStructure.PLACEHOLDER.equals(ms.getRole()) && ms.getRepeating()!=null) {
			dataFieldSet.add(new DataField( externalPrefix+"drilldown", internalPrefix+"referencePath", true, null));
		}
		dataFieldSet.add(new DataField( externalPrefix+"path", internalPrefix+"path", true, null));
		// Get the id (primaryKey) for this item
		if (externalPrefix.length()==0) {
			dataFieldSet.add(new DataField( externalPrefix + "id", internalPrefix + "id", true, null));
		}		
		if(externalPrefix.equalsIgnoreCase("")) {
			dataFieldSet.add(new DataField("placeholderIds", "placeholderIds", true, null));  //ignore this in md query
		}
		dataFieldSet.add(new DataField( externalPrefix + "documentId", internalPrefix + "documentId", true, null));
		dataFieldSet.add(new DataField( externalPrefix + "actStatus", internalPrefix + "actStatus", true, null));
		// Get owners
		AccountMenuStructure msParent = ms.getParent();
		StringBuffer sbInternal = new StringBuffer();
		StringBuffer sbExternal = new StringBuffer();
		sbExternal.append(externalPrefix);
		sbInternal.append(internalPrefix);
		while (msParent!=null) {
			if (MenuStructure.PLACEHOLDER.equals(msParent.getRole())) {
//				dataFieldSet.add(new DataField( sbExternal.toString() + msParent.getNode() + ".id", sbInternal.toString()+ ".id"));
				sbExternal.append(msParent.getNode());
				sbExternal.append(".");
				String element = "p"+level+".";
				sbInternal.replace(0,sbInternal.length(),element);
				level++;
				populateFieldSet( msParent, sbExternal.toString(), sbInternal.toString(), dataFieldSet);
				break;
			}
			msParent = msParent.getParent();
		}
	}
	
	/**
	 * Construct a string suitable for insertion directly into the EJBQuery select clause
	 * @return
	 */
	public String getSelectString () {
		StringBuffer sb = new StringBuffer();
		for (DataField df : getFields()) {
			if (df.isEnabled() && !Arrays.asList(df.getInternalSegments()).contains("_extended") && !Arrays.asList(df.getInternalSegments()).contains("placeholderIds")) {
				if (sb.length()!=0) {
					sb.append(", ");
				}
				// If the dfSegments has only one element (say path, name) need to append with md
				// Otherwise, for the elements of parent (say parent01.path), no need to append md.
				String dfSegments[] = df.getInternalSegments();
				if(dfSegments.length<=1 || dfSegments[0].startsWith("parent"))
				{
					sb.append("md.");
				}
				sb.append(df.getInternal());
			}
		}
		return sb.toString();
	}
	
	/**
	 * Construct a string suitable for use in a an EJBQuery order by clause
	 * @return
	 */
	public void getSortString (StringBuffer sb) {
		String orderStrings[] = getOrder();
		if (orderStrings==null) {
			return;
		}
        sb.append("ORDER BY ");
        boolean first = true;
		for (String order : orderStrings) {
			String orderParts[] = order.split(" ");
			DataField dataField = findExternalField( orderParts[0] );
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append( "md.");
			sb.append(dataField.getInternal());
			if (orderParts.length>1) {
				sb.append(" ");
				sb.append(orderParts[1]);
			}
		}
    	sb.append(" ");
		return;
	}
	
	/**
	 * Get a list of all possible fields
	 * @return
	 */
	public List<DataField> getFields() {
		if (fields==null) {
			level = 1;
			Set<DataField> dataFieldSet = new HashSet<DataField>();
			populateFieldSet( ms, "", "", dataFieldSet);
			fields = new ArrayList<DataField>(dataFieldSet);
			Collections.sort(fields);
		}
		return fields;
	}
	
	/**
	 * Set the enabled flag in the list of fields
	 */
	public void enableAllFields( ) {
		for (DataField field : getFields()) {
			field.setEnabled(true);
		}
	}
	
	/**
	 * Reset the enabled flag in the list of fields
	 */
	public void disableAllFields( ) {
		for (DataField field : getFields()) {
			field.setEnabled(false);
		}
	}
	
	public List<DataField> getSelectedFields() {
		List<DataField> selectedFields = new ArrayList<DataField>(getFields().size());
		for (DataField field : getFields()) {
			if (field.isEnabled()) {
				selectedFields.add(field);
			}
		}
		return selectedFields;
	}
	
	public DataField findExternalField( String external ) {
		for (DataField field : getFields() ) {
			if (external.equals(field.getExternal())) {
				return field;
			}
		}
		return null;
	}
	
	/**
	 * Find a field name by its label
	 * @param label
	 * @return The field
	 */
	public DataField findField( String label ) {
		for (DataField field : getFields() ) {
			if (label.equals(field.getLabel())) {
				return field;
			}
		}
		throw new RuntimeException( "Field " +label+ " not found in " + ms);
	}
	
	/**
	 * Set the fields to be used for the query. If the list is null or contains no fields, then all
	 * fields will be selected.
	 * @param list of selected fields (external field names)
	 */
	public void setSelectedFields( List<String> selected) {
		if (selected==null || selected.size()==0) {
			for (DataField field : getFields()) {
				field.setEnabled(true);
			}
		} else {
			// Clear previous settings
			for (DataField field : getFields()) {
				field.setEnabled(false);
			}
			for (String label : selected) {
				DataField selectedField = findField( label );
				selectedField.setEnabled(true);
			}
		}
	}
	
	public MenuStructure getMenuStructure() {
		return ms;
	}
	public Account getAccount() {
		return ms.getAccount();
	}
	
	public String getPath() {
		return ms.getPath();
	}

	public Iterator<Object> getIterator() {
		return iterator;
	}

	public void setIterator(Iterator<Object> iterator) {
		this.iterator = iterator;
	}
	
	public boolean hasNext() {
		return getIterator().hasNext();
	}
	
	public Object next() {
		return getIterator().next();
	}

	public Date getNow() {
		if (now==null) {
			now = new Date();
		}
		return now;
	}

	public void setNow(Date now) {
		this.now = now;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String[] getOrder() {
		return order;
	}

	public void setOrder(String[] order) {
		this.order = order;
	}

	public void setFields(List<DataField> fields) {
		this.fields = fields;
	}

	public MenuPath getMenuPath() {
		return menuPath;
	}

	public void setMenuPath(MenuPath menuPath) {
		this.menuPath = menuPath;
	}
	/**
	 * True if this is an item query, otherwise, it is a list query
	 * @return
	 */
	public boolean isItemQuery() {
		return itemQuery;
	}

	public void setItemQuery(boolean itemQuery) {
		this.itemQuery = itemQuery;
	}

	public boolean isReturnTotalCount() {
		return returnTotalCount;
	}

	public void setReturnTotalCount(boolean returnTotalCount) {
		this.returnTotalCount = returnTotalCount;
	}

	public boolean isReturnFilterCount() {
		return returnFilterCount;
	}

	public void setReturnFilterCount(boolean returnFilterCount) {
		this.returnFilterCount = returnFilterCount;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setParentFilterFields(List<DataField> parentFilterFields) {
		this.parentFilterFields = parentFilterFields;
	}

	public List<DataField> getParentFilterFields() {
		return parentFilterFields;
	}

	
}
