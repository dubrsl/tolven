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
/**
 * This file contains CCHITQueryBuilder.
 *
 * @package org.tolven.app.bean
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */  
package org.tolven.app.bean;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.tolven.app.CCHITQueryBuilderLocal;
import org.tolven.app.QueryBuilderLocal;
import org.tolven.app.QueryBuilderLocal.Participation;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;

/**
 * This bean extends DefaultQueryBuilder to allow reference columnwise searching.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	| 03/24/2010           	| Valsaraj Viswanathan 	| Initial Version.
==============================================================================================================================================
*/
@Local(CCHITQueryBuilderLocal.class)
public @Stateless class CCHITQueryBuilder implements CCHITQueryBuilderLocal {
	@EJB QueryBuilderLocal queryBuilderLocal;
	
	@Override
	public Participation getParticipation(MenuQueryControl ctrl) {
		return queryBuilderLocal.getParticipation(ctrl);
	}
	@Override
	public void prepareCriteria(MenuQueryControl ctrl, StringBuffer sbFrom, StringBuffer sbWhere, StringBuffer sbOrder,
			Map<String, Object> params) {
        MenuStructure msActual = ctrl.getActualMenuStructure( );
		// If the MenuStructure we're looking at is a reference, then we're really going after the referenced items but
		// using the reference as a selector.
		sbWhere.append(" (md.deleted is null OR md.deleted = false) " );
		sbWhere.append(" AND md.menuStructure = :m"); 
        params.put("m", msActual);
		// Add any parent keys we may have unless we're looking for a placeholder in which case the
        // we just need the if of the node-name.
        if (MenuStructure.PLACEHOLDER.equals( msActual.getRole())) {
        	  sbWhere.append(" AND md.id = :id");
        	  Long placeholderId = ctrl.getRequestedPath().getNodeValues().get(msActual.getNode());
        	  params.put("id", placeholderId);
       	} else {
       		MenuData owner = queryBuilderLocal.getOwner(ctrl);
       		
       		if (owner != null) {
        		sbWhere.append( " AND md.parent01 = :owner" );
        		params.put("owner", owner);
       		}
       	}
        
		// See if there's a global filter specified in (original) MenuStructure
        queryBuilderLocal.addGlobalFilter( ctrl, sbWhere);
		// First filter type is based on MenuDataWord table
        queryBuilderLocal.addWordFilter2(ctrl.getFilter(), sbWhere, params );
		int filterNo = 0;
		// This is the older explicit column filter
		// See if any of the columns are mentioned in the filter list.
		for (MSColumn col : msActual.getColumns()) {
			Object value = ctrl.getFilters().get(col.getHeading());
			String colName;
			colName = col.getInternal();
			
			if (col.isInstantiate() || col.isReference()) {
				colName = col.getDisplayFunction();
			}

			// In the case of combination fields, the column name comes from DisplayFunctionArgs
			if (col.getDisplayFunctionArguments() != null) {
				String dfas[] = col.getDisplayFunctionArguments().split("\\,");
				colName = dfas[0];
			}
			
			if (value != null) {
				if (! (value instanceof String)) 
					throw new IllegalArgumentException( "Filter value of " + col.getHeading() + " must be a string");
				
				if (((String)value).length() > 0) {
					filterNo += 2;
					sbWhere.append( " AND lower(md." + colName + ") LIKE :fltr" + filterNo   );
					params.put( "fltr"+filterNo, "%"+((String)value).trim().toLowerCase()+"%" );
				}
			}
		}
		
		if (ctrl.getFromDate() != null) {
		   if (ctrl.getToDate() != null) {
			   sbWhere.append(" and md.date01 >= :fromDate and md.date01 <:toDate");
			   params.put( "fromDate", ctrl.getFromDate() );
			   params.put( "toDate", ctrl.getToDate() );			   
		   }
		}

		// See about sort criteria. Similar to filters: For safety, we pull from the request list rather 
		// than letting the request list drive our behavior.
		if (ctrl.getSortOrder() != null) {
			for (MSColumn col : msActual.getColumns()) {
				// Ignore computed and invisible fields
				if (col.isComputed()) 
					continue;
				
				if (! col.isVisible()) 
					continue;
				// Internal is required for sorting.
				// Look for a match on internal name or a heading name
				if (! col.isExtended() &&
						(ctrl.getSortOrder().equalsIgnoreCase(col.getHeading()) ||
						 ctrl.getSortOrder().equalsIgnoreCase(col.getInternal()))) {
					sbOrder.append( " ORDER BY ");
					String colName = col.getInternal();
					
					if (col.isInstantiate() || col.isReference()) {
						colName = col.getDisplayFunction();
					}
					
					String sortCols[];
					
					if (col.getDisplayFunctionArguments()==null) {
						sortCols = new String[1];
						sortCols[0] = colName;
					} else {
						sortCols = col.getDisplayFunctionArguments().split("\\,");
					}
					
					boolean firstTime = true;
					
					for (String sortCol : sortCols) {
						if (firstTime) {
							firstTime = false;
						} else {
							sbOrder.append(", ");
						}
						
						if (sortCol.startsWith("string")) {
							sbOrder.append("lower(");
							sbOrder.append("md.");
							sbOrder.append(sortCol);
							sbOrder.append(")");
						} else {
							sbOrder.append("md.");
							sbOrder.append(sortCol);
						}
						
						sbOrder.append(" ");
						
						if (ctrl.getSortDirection() != null) {
						    sbOrder.append(ctrl.getSortDirection());
						}
					}
					
					break;
				}
			}
		}
	}
}