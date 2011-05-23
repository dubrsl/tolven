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
 * @version $Id: DefaultQueryBuilder.java,v 1.5 2010/04/24 17:43:40 jchurin Exp $
 */  

package org.tolven.app.bean;

import java.util.Locale;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.tolven.app.QueryBuilderLocal;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;

@Local(QueryBuilderLocal.class)
public @Stateless class DefaultQueryBuilder implements QueryBuilderLocal {

	@PersistenceContext private EntityManager em;

	@Override
	public Participation getParticipation(MenuQueryControl ctrl) {
		return Participation.FILTER;
	}
	
	@Override
	public void prepareCriteria(MenuQueryControl ctrl, StringBuffer sbFrom, StringBuffer sbWhere, StringBuffer sbOrder,
			Map<String, Object> params) {
        MenuStructure msActual = ctrl.getActualMenuStructure( );
//		// If the MenuStructure we're looking at is a reference, then we're really going after the referenced items but
//		// using the reference as a selector.
		sbWhere.append(" (md.deleted is null OR md.deleted = false) " );
		sbWhere.append(" AND md.menuStructure = :m"); 
        params.put( "m", msActual );
		// Add any parent keys we may have unless we're looking for a placeholder in which case the
        // we just need the if of the node-name.
        if (MenuStructure.PLACEHOLDER.equals( msActual.getRole())) {
        	  sbWhere.append( " AND md.id = :id" );
        	  Long placeholderId = ctrl.getRequestedPath().getNodeValues().get(msActual.getNode());
        	  params.put( "id", placeholderId );
       	} else {
       		MenuData owner = getOwner( ctrl);
       		if (owner!=null) {
        		sbWhere.append( " AND md.parent01 = :owner" );
        		params.put( "owner", owner );
       		}
       	}
		// See if there's a global filter specified in (original) MenuStructure
		addGlobalFilter( ctrl, sbWhere);
		// First filter type is based on MenuDataWord table
//		addWordFilter(ctrl.getFilter(), sbFrom, sbWhere, params );
		addWordFilter2(ctrl.getFilter(), sbWhere, params );
		int filterNo = 0;
		// This is the older explicit column filter
		// See if any of the columns are mentioned in the filter list.
		for (MSColumn col : msActual.getColumns()) {
			Object value = ctrl.getFilters().get(col.getHeading());
			String colName;
			colName = col.getInternal();
			// In the case of combination fields, the column name comes from DisplayFunctionArgs
			if (col.getDisplayFunctionArguments()!=null) {
				String dfas[] = col.getDisplayFunctionArguments().split("\\,");
				colName = dfas[0];
			}
			if (value!=null) {
				if ( !(value instanceof String) ) throw new IllegalArgumentException( "Filter value of " + col.getHeading() + " must be a string");
				if (((String)value).length() > 0) {
					filterNo += 2;
					sbWhere.append( " AND lower(md." + colName + ") BETWEEN :fltr" + filterNo + " AND :fltr" + (filterNo+1)  );
					params.put( "fltr"+filterNo, ((String)value).trim().toLowerCase() );
					params.put( "fltr"+(filterNo+1),  ((String)value).trim().toLowerCase() + "zzzzzzzzzzzzzzzzzzzzzzzzzzzz" );
				}
			}
		}
		if(ctrl.getFromDate() != null){
		   if(ctrl.getToDate() != null){
			   sbWhere.append(" and md.date01 >= :fromDate and md.date01 <:toDate");
			   params.put( "fromDate", ctrl.getFromDate() );
			   params.put( "toDate", ctrl.getToDate() );			   
		   }
		}

//		logger.debug( "Sort: " + ctrl.getSortOrder());
		// See about sort criteria. Similar to filters: For safety, we pull from the request list rather 
		// than letting the request list drive our behavior.
		if (ctrl.getSortOrder()!=null) {
			for (MSColumn col : msActual.getColumns()) {
//				logger.debug( "Sort - checking " + col);
				// Ignore computed and invisible fields
				if (col.isComputed()) continue;
				if (!col.isVisible()) continue;
				// Internal is required for sorting.
				// Look for a match on internal name or a heading name
				if ( !col.isExtended() &&
						(ctrl.getSortOrder().equalsIgnoreCase(col.getHeading()) ||
						 ctrl.getSortOrder().equalsIgnoreCase(col.getInternal()))
								) {
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
					for ( String sortCol : sortCols) {
						if (firstTime) {
							firstTime=false;
						} else {
							sbOrder.append(", ");
						}
						if (sortCol.startsWith("string")) {
							sbOrder.append("md.");sbOrder.append(sortCol);
						} else {
							sbOrder.append("md.");//that should do it. redeploy? yes.s
							sbOrder.append(sortCol);
						}
						sbOrder.append(" ");
						if(ctrl.getSortDirection() != null) {
						    sbOrder.append(ctrl.getSortDirection());
						}
					}
					break;
				}
			}
		}

	}

	/**
	 * See if there is a global filter defined in the menuStructure.
	 * @param ctrl
	 * @param sbWhere
	 * @param params
	 */
	protected void addGlobalFilter(  MenuQueryControl ctrl, StringBuffer sbWhere ) {
		String rawFilter = ctrl.getMenuStructure().getFilter();
		if (rawFilter==null) return;
		TrimExpressionEvaluator evaluator = new TrimExpressionEvaluator();
		if (ctrl.getAccountUser()!=null) {
			evaluator.addVariable("accountUser", ctrl.getAccountUser());
			evaluator.addVariable("account", ctrl.getAccountUser().getAccount());
			evaluator.addVariable("user", ctrl.getAccountUser().getUser());
		}
		evaluator.addVariable( "now", ctrl.getNow());
		evaluator.addVariable( "ctrl", ctrl);
		String filter = (String) evaluator.evaluate(rawFilter);
		sbWhere.append( " AND (" );
		sbWhere.append(filter);
		sbWhere.append( ") " );
	}
	
	/**
	 * Many lists have an "owner", that is the list is inside of a placeholder. For example, 
	 * an allergy list for a patient is "owned" by the patient. In effect, each patient has an allergy list.
	 * @param ctrl
	 * @return zero if no owner exists, making the list global (to the account). Otherwise, the id of the menuData item
	 * that is the owner of the requested list.
	 */
	public MenuData getOwner( MenuQueryControl ctrl ) {
        long[] snk = ctrl.getResolvedPath().getSignificantNodeKeys( );
        for (int x = snk.length-1; x >= 0; x--) {
        	if (snk[x]!=0) {
        		return em.getReference( MenuData.class, snk[x]);
        	}
        }
		return null;
//		String path = ctrl.getResolvedPath().getOwnerPath();
//		if (path==null) {
//			return null;
//		}
//		MenuData md;
//		try {
//			Query query = em.createQuery("SELECT md FROM MenuData md WHERE md.account.id = :account AND md.path = :path");
//			query.setParameter("account", ctrl.getAccountId());
//			query.setParameter("path", path);
//			md = (MenuData) query.getSingleResult();
//		} catch (Exception e) {
//			throw new RuntimeException( "Error encountered in query of path " + path, e);
//		}
//		return md;
	}
	public MenuData getParentOrNull( MenuQueryControl ctrl, int level) {
		long parentId = ctrl.getParentId(level);
		if (parentId > 0) {
			return em.getReference( MenuData.class, parentId);
		} else {
			return null;
		}
	}

	protected void addWordFilter2( String filter, StringBuffer sbWhere, Map<String, Object>params ) {
		if (filter==null) return;
		String words[] = filter.split("\\W");
		int wordNo = 0;
		for ( String rawWord : words ) {
			String word = rawWord.trim().toLowerCase();
			if (word.length()>0 && !StopList.ignore( word, "en_US" )) {
				wordNo++;
				sbWhere.append( String.format(Locale.US, " AND (md IN (SELECT mdw%d.menuData FROM MenuDataWord mdw%d WHERE mdw%d.menuStructure = :m and mdw%d.word BETWEEN :wfl%d AND :wfh%d)) ", wordNo, wordNo, wordNo, wordNo, wordNo, wordNo));
				params.put("wfl" + wordNo, word );
				params.put("wfh" + wordNo, word + "zzzzzzzzzzzzzzzzzzzz");
			}
		}
	}

}
