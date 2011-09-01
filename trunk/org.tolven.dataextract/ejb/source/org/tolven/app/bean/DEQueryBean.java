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
 * @version $Id: DEQueryBean.java,v 1.1 2009/08/19 07:08:38 jchurin Exp $
 */  

package org.tolven.app.bean;


//@Local(DEQueryLocal.class)
//@Stateless
public class DEQueryBean  {
//	@EJB MenuLocal menuBean;
//	
//	/**
//	 * Map a segment of a field name to the internal column name
//	 * @param ms
//	 * @param segment
//	 * @return
//	 */
//	protected String mapToInternal( MenuStructure ms, String segment ) {
//		// Internal and we're done
//		if (MenuData.isInternalField(segment)) {
//			return segment;
//		}
//		
//		// Internal match means it's already internal
//		Iterator<MSColumn> columns = ms.getColumns().iterator();
//		while (columns.hasNext()) {
//			MSColumn col = columns.next();
//			if( segment.equals(col.getHeading())) {
//				if (col.getInternal()!=null) return col.getInternal();
//			}
//		}
//		
//		// If not a placeholder, do some more checking to see if we need access to the placeholder in the result.
//		if (!MenuStructure.PLACEHOLDER.equals(ms.getRole())) {
//			MenuStructure placeholder = ms.getReferenced();
//			if (placeholder!=null && fieldNameParts[0].equals(placeholder.getNode())) {
//				return "referenced";
//			}
//		}
//		// If we have a placeholder parent, then we need to make these available, too.
//		
//		MenuStructure owner = menuBean.getOwner( ms );
//			if (owner!=null && fieldName.equals(owner.getNode())) {
//				return "parent01";
//			}
//		}
//	}
//	/**
//	 * Look for one of several possible patterns: 
//	 * 1) fieldName matches a column name known to the menuStructure.
//	 * 2) fieldName prefix (for example, patient in patient.firstName) matches a columnName known to the menuStructure.
//	 * 3) fieldName prefix matches the name of the underlying placeholder. For example, in a list of allergies, the
//	 * underlying placeholder would probably be allergy. The internal field in this case is "referenced".
//	 * 4) fieldName prefix is an internal field 
//	 * @param ms
//	 * @param fieldName Name of the field to acquire
//	 * @return The matching internal column
//	 */
//	protected String getInternalColumnName( MenuStructure ms, String fieldName) {
//		int firstDot = fieldName.indexOf('.');
//		String segment;
//		String remainder;
//		if (firstDot < 0) {
//			segment = fieldName;
//			remainder = null;
//		} else {
//			segment = fieldName.substring(0, firstDot-1);
//			remainder = fieldName.substring(firstDot+1);
//		}
//		String segmentInternal = mapToInternal( ms, segment);
//		return null;
//	}
//	
//	/**
//	 * Build a set of unique internal fields to query for
//	 * @param ms
//	 * @param fieldNames
//	 * @return
//	 */
//	protected List<String> convertToInternalColumnNames( MenuStructure ms, List<String> fieldNames) {
//		List<String> internalNames = new ArrayList<String>(fieldNames.size());
//		for (String fieldName : fieldNames) {
//			String internalName = getInternalColumnName( ms, fieldName);
//			if (internalName!=null) internalNames.add(internalName);
//		}
//		return internalNames;
//	}
//	
//	/**
//	 * Find data using the usual query criteria but with more control over the columns
//	 * than provided by findMenuDataByColumns.
//	 * @param ctrl A structure specifying account, MenuStructure, and query limits.
//	 * @param columnNames A list of column names to extract.
//	 * @return A structure containing the results of the query
//	 */
//	public MDQueryResults findData( MenuQueryControl ctrl, List<String> fieldNames ){
//		DataQueryResults results = new MDQueryResults(ctrl);
//		try{
//			Set<String> internalNames = getInternalColumnNames( ctrl.getMenuStructure(), fieldNames);
//			StringBuffer selectClause = new StringBuffer( 350 );
//			// Build the query string
//			for (String internalName : internalNames) {
//				if (selectClause.length()!=0) selectClause.append(", ");
//				selectClause.append("md.");
//				selectClause.append(internalName);
//			}
//			Query query = prepareCriteria(ctrl, selectClause.toString() );
//			if (ctrl.getLimit()>0) {
//				query.setMaxResults(ctrl.getLimit());
//			}
//			query.setFirstResult(ctrl.getOffset());
//		    // Get the results of the query
//			List<Object[]> results = query.getResultList(); 
//			for( Object[] row : results){
//			    Map<String, Object> rowMap = new HashMap<String, Object>(columnNames.length+columns.size());
//			    int c1 = 0;
//			    // Load the raw internal fields into the map
//			    for (String field : columnNames) {
//			    	rowMap.put(field, row[ c1 ]);
//			    	c1++;
//			    }
//			    // Expand extended columns into the map
//			    Object xml01 = rowMap.get("xml01");
//			    Map<String, Object> extendedFields = new HashMap<String, Object>();
//			    if (xml01!=null) {
//			    	MenuData.unmarshalExtendedFields( (byte[]) xml01, extendedFields );
//			    }
//			    // Make extended fields available to the fieldGetter
//			    fieldGetter.setExtendedFields( extendedFields );
//			    // Also expand underlying placeholder
//			    Object referenceId = rowMap.get("reference.id");
//			    // Only query placeholder when we will for-certain reference it.
//			    if (referenceId!=null) {
//				    MenuData placeholder = this.findMenuDataItem((Long)referenceId);
//				    rowMap.put("_placeholder", placeholder);
//				    //expandSourceMap(evaluator, rowMap);
//				    expandSourceMap(rowMap);
//					for (Map.Entry<String, Object> entry : rowMap.entrySet() ) {
//						String variable = entry.getKey();
//						// Initial lowercase and camelCase otherwise
//						if (Character.isUpperCase(variable.charAt(0))) {
//							variable = Character.toLowerCase(variable.charAt(0)) + variable.substring(1);
//						}
//						evaluator.addVariable(variable, entry.getValue());
//					}
//			    }
//			    // Make the rowMap available to fieldGetter (this will collect the formatted field values) 
//		    	fieldGetter.setRowMap( rowMap );
//			    // Now create a separate entry for each of the column entries, including the formatting functions
//			    // performed by those columns. If the column is not an internal field, then the only thing we can do is formatting.
//			    for (MSColumn col : columns) {
//			    	rowMap.put(col.getHeading(), col.getFormattedColumn( fieldGetter ));
//			    }
//				mdQueryResults.addRow( rowMap );
//			}
//			
//		}catch(Exception e){
//			throw new RuntimeException("Error in findMenuDataByColumn for path " + ctrl.getActualMenuStructurePath(), e );
//		}
//		return mdQueryResults;
//	}
//	

}
