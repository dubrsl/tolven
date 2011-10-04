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
 * This file contains CCHITBean.
 *
 * @package org.tolven.app.bean
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app.bean;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.security.PrivateKey;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.tolven.app.CCHITLocal;
import org.tolven.app.CCHITQueryBuilderLocal;
import org.tolven.app.CCHITRemote;
import org.tolven.app.MenuLocal;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.Status;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.XMLProtectedLocal;
import org.tolven.doc.entity.DocXML;
import org.tolven.trim.ex.TrimEx;

/**
 * This bean handles data retrieval operations.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	| 12/12/2009           	| Valsaraj Viswanathan 	| Initial Version.
2		| 03/16/2011			| Valsaraj Viswanathan  | Removed unused methods and added trim related methods.
==============================================================================================================================================
*/
@Stateless()
@Local(CCHITLocal.class)

public class CCHITBean implements CCHITLocal {
	@EJB XMLProtectedLocal xmlProtectedBean;
    
	@EJB MenuLocal menuBean;
	@EJB DocumentLocal docBean;
	
	@PersistenceContext
	private EntityManager em;
	
	private Set<String> queryBuilders;
	
	Logger logger = Logger.getLogger(this.getClass());
	
	public final static String QUERY_BUILDER_NAME = "queryBuilderJNDI";
	private final static String DEFAULT_QUERY_BUILDER = "java:global/tolven/tolvenEJB/CCHITQueryBuilder!org.tolven.app.CCHITQueryBuilderLocal";
	static final String TRIM_NS = "urn:tolven-org:trim:4.0"; 
	
    /**
	 * This function retrieve latest trim object corresponding to a menu.
	 * @author Suja
	 * added on 11/8/09
	 * @param params - Map<String, Object>
	 *        fields - menuPath 	=> Menu Path
	 *        		 - contextPath	=> Context Path
	 *        		 - conditions	=> Filtering and sorting conditions
	 *        						   Format - <fieldName>Filter=<fieldValue>::fromDate=<FROMDATE>:toDate=<TODATE>
	 *        									:<fieldName>Sort=<ASC/DESC>:limit=<value>
	 *        		 - accountUser	=> Account User object		
	 * @return TrimEx
	 * @throws ParseException
	 * @throws JAXBException
	 */
	public List<TrimEx> findTrimDataList( Map<String, Object> params, PrivateKey userPrivateKey) throws ParseException, JAXBException{
		Long id = new Long(0);
		String path = params.get("menuPath").toString();
		String contextPath = params.get("contextPath").toString();
		String condition = params.get("conditions").toString();
		AccountUser accountUser=(AccountUser)params.get("accountUser");
		List<TrimEx> trimList = new ArrayList<TrimEx>();
		List<Map<String, Object>> list = findAllMenuDataList(path, contextPath, condition, accountUser);
		MenuData md = null;
		
		if (list != null ){
			for (int i = 0;i < list.size(); i++) {
				Map<String, Object> map=list.get(i);
				id = new Long(map.get("id").toString());
				md = menuBean.findMenuDataItem(id);
				
				if (md != null){
					DocXML docXMLFrom = (DocXML) docBean.findDocument(md.getDocumentId());
					Object obj = xmlProtectedBean.unmarshal(docXMLFrom, accountUser,userPrivateKey);
					
					if (obj instanceof TrimEx) {
						trimList.add((TrimEx)obj);
					}
				}
			}
			
			return trimList;
		} else
			return null;
	}
	
	/**
	 * Function to retrieve menu data items corresponding to a path.
	 * @param path
	 * @param contextPath
	 * @param condition
	 * @param accountUser
	 * @return
	 * @throws ParseException
	 */
	public List<Map<String, Object>> findAllMenuDataList(String path, String contextPath, String condition, AccountUser accountUser) throws ParseException{
		List<Map<String, Object>> list = null;
		MenuQueryControl ctrl = new MenuQueryControl();
		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		String splitChar = ":";
		MenuStructure ms = menuBean.findMenuStructure(accountUser.getAccount().getId(), path );
		Map<String, Long> nodeValues = new HashMap<String, Long>(10);
		nodeValues.putAll(new MenuPath( contextPath ).getNodeValues());
		ctrl.setMenuStructure( ms );
		ctrl.setNow(new Date());
		ctrl.setAccountUser(accountUser);
		ctrl.setOriginalTargetPath( new MenuPath(ms.instancePathFromContext(nodeValues, true)));
		ctrl.setRequestedPath( ctrl.getOriginalTargetPath() );
		
		if (condition != null && condition != "") {
			String[] condnParams=condition.split(splitChar);
			String[] param;
			String paramStr;
			long t;
			
			for (int i = 0; i < condnParams.length; i++) {
				param=condnParams[i].split("=");
				
				if (param.length > 1){
					if (param[0].equals("filter"))
						ctrl.setFilter(param[1]);
					else if (param[0].equals("fromDate"))
						ctrl.setFromDate(df.parse(param[1]));
					else if (param[0].equals("toDate"))
						ctrl.setToDate(df.parse(param[1]));
					else if (param[0].equals("DateFilter")){
						if (param[1].contains("/")){
							ctrl.setFromDate(df.parse(param[1]));
							t = df.parse(param[1]).getTime();
							t += 24 * 60 * 60 * 1000;
							ctrl.setToDate(new Date(t));
						} else if (param[1].length()==8){
							paramStr=param[1].substring(4, 6)+"/"+param[1].substring(6, 8)+"/"+param[1].substring(2, 4);
							ctrl.setFromDate(df.parse(paramStr));
							t = df.parse(paramStr).getTime();
							t += 24 * 60 * 60 * 1000;
							ctrl.setToDate(new Date(t));
						}						
					} else if (param[0].endsWith("Filter")) {
						ctrl.getFilters().put(param[0].substring(0, param[0].length()-6), param[1]);
					} else if (param[0].endsWith("Sort")) {
						ctrl.setSortOrder(param[0].substring(0, param[0].length()-4));
						ctrl.setSortDirection(param[1]);
					} else if (param[0].toLowerCase().equals("limit"))
						ctrl.setLimit(Integer.parseInt(param[1]));
				}
			}
		}
		
		list =  menuBean.findMenuDataByColumns(ctrl).getResults();
		
		return list;
	}
	
	/**
	 * This function retrieve latest trim object corresponding to a menu.
	 * @author Suja
	 * added on 11/8/09
	 * @param params - Map<String, Object>
	 *        fields - menuPath 	=> Menu Path
	 *        		 - contextPath	=> Context Path
	 *        		 - conditions	=> Filtering and sorting conditions
	 *        						   Format - <fieldName>Filter=<fieldValue>::fromDate=<FROMDATE>:toDate=<TODATE>
	 *        									:<fieldName>Sort=<ASC/DESC>:limit=<value>
	 *        		 - accountUser	=> Account User object		
	 * @return TrimEx
	 * @throws ParseException
	 * @throws JAXBException
	 */
	public TrimEx findTrimData(Map<String, Object> params, PrivateKey userPrivateKey) throws ParseException, JAXBException{
		Long id = new Long(0);
		String path = params.get("menuPath").toString();
		String contextPath = params.get("contextPath").toString();
		String condition = params.get("conditions").toString();
		AccountUser accountUser=(AccountUser)params.get("accountUser");
		
		List<Map<String, Object>> list = findAllMenuDataList(path, contextPath, condition, accountUser);
		
		if (list != null && list.size() > 0) {
			Map<String, Object> map=list.get(0);
			id = new Long(map.get("id").toString());
		}		
		
		MenuData md = menuBean.findMenuDataItem(id);
		
		if (md != null){
			DocXML docXMLFrom = (DocXML) docBean.findDocument(md.getDocumentId());
			TrimEx trim = (TrimEx) xmlProtectedBean.unmarshal(docXMLFrom, accountUser, userPrivateKey);
			
			return trim;
		} else
			return null;
	}
	
	/**
	 * This function retrieve latest trim object corresponding to a menudata id.
	 * @addedon 18/03/2010
	 * @param Long id
	 * @param AccountUser accountUser
	 * @return TrimEx
	 * @throws JAXBException
	 */
	public TrimEx findTrimData(Long id, AccountUser accountUser, PrivateKey userPrivateKey) throws JAXBException{
		MenuData md = menuBean.findMenuDataItem(id);
		
		if (md != null){
			DocXML docXMLFrom = (DocXML) docBean.findDocument(md.getDocumentId());
			TrimEx trim = (TrimEx) xmlProtectedBean.unmarshal(docXMLFrom, accountUser,userPrivateKey);
			
			return trim;
		} else
			return null;
	}
	
	/**
	 * Modified by adding sorting of TRIMs by id when sort by date is with same date
	 * @param ctrl
	 * @param init
	 * @return
	 */

	public Query prepareCriteria(MenuQueryControl ctrl, String init) {
		menuBean.prepareMQC(ctrl);
		StringBuffer sbFrom = new StringBuffer(350);
		StringBuffer sbWhere = new StringBuffer(350);
		StringBuffer sbOrder = new StringBuffer(350);
		Map<String, Object> params = new HashMap<String, Object>(5);
		sbFrom.append("MenuData md");
		
		try {
	    	initializeQueryBuilders();
	    	InitialContext ctx = new InitialContext();
	    	List<CCHITQueryBuilderLocal> qbList = new ArrayList<CCHITQueryBuilderLocal>();
	    	CCHITQueryBuilderLocal qbExclusive = null;
	    	
	    	for (String queryBuilder : queryBuilders) {
	    		Object qbo = ctx.lookup(queryBuilder);
	    		
	    		if (qbo instanceof CCHITQueryBuilderLocal) {
	    			CCHITQueryBuilderLocal qbBean = (CCHITQueryBuilderLocal) qbo;
	    			
		    		switch (qbBean.getParticipation(ctrl)) {
	    				case NONE: 
	    					break;
		    			case EXCLUSIVE:
		    				if (qbExclusive!=null) 
		    					throw new RuntimeException("Multiple QueryBuilders requesting exclusive control " + queryBuilder);
		    					qbExclusive = qbBean;
		    					
		    					break;
		    			case FILTER: 
		    				qbList.add(qbBean);
		    				
		    				break;
		    		}
	    		} else if (qbo != null){
	    			throw new RuntimeException( "For JNDI name " + queryBuilder + ", object " + qbo.getClass().getName() + " must implement " + CCHITQueryBuilderLocal.class.getName());
	    		} else {
	    			throw new RuntimeException( "JNDI name " + queryBuilder + " name not found");
	    		}
	    	}
	    	if (qbExclusive != null) {
	    		qbExclusive.prepareCriteria( ctrl, sbFrom, sbWhere, sbOrder, params );
	    	} else {
		    	for (CCHITQueryBuilderLocal qbBean : qbList) {
		    		qbBean.prepareCriteria( ctrl, sbFrom, sbWhere, sbOrder, params );
		    	}
	    	}
		} catch ( Exception e) {
			throw new RuntimeException( "Error building query " + ctrl.toString(), e);
		}

		if (sbOrder != null && sbOrder.length() > 0) {
			sbOrder.append(",md.id DESC");
		}
		
		String qs = String.format(Locale.US, "SELECT %s FROM %s WHERE %s %s", init, sbFrom, sbWhere, sbOrder);
		logger.debug( "MenuData Query: " + qs.toString());
		Query query = em.createQuery( qs );
        logger.debug( "Query params: ");
        
		for (Map.Entry<String, Object> e : params.entrySet()) {
	        query.setParameter( e.getKey(), e.getValue() );
	        logger.debug("   key:" + e.getKey() + ", value:" + e.getValue() );
		}
		
        return query;
	}
	
	/**
     * Find the list of query builders to use.
     */

    public void initializeQueryBuilders() {
    	try {
			if (queryBuilders == null) {
				Properties properties = new Properties();
				String propertyFileName = this.getClass().getSimpleName() + ".properties"; 
				InputStream is = this.getClass().getResourceAsStream(propertyFileName);
				String queryBuilderNames = null;
				
				if (is != null) {
					properties.load(is);
					queryBuilderNames = properties.getProperty(QUERY_BUILDER_NAME);
					is.close();
				}
				
				queryBuilders = new HashSet<String>();
				
				if (queryBuilderNames != null && queryBuilderNames.length() > 0) {
					String names[] = queryBuilderNames.split(",");
					
					for (String name : names) {
						queryBuilders.add(name);
					}
				} else {
					queryBuilders.add(DEFAULT_QUERY_BUILDER);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException( "Error initializing Query Builders", e);
		}
    }

	/**
	 * Finds the reference id using string01 value and account id.
	 * 
	 * @author valsaraj
	 * added on 05/20/2010
	 * @param string01
	 * @param accountId
	 * @return  referenceId
	 */
	public long findReferenceId(String string01, long accountId) throws Exception {
		long referenceId;
		Query query = em.createQuery("SELECT md FROM MenuData md " +
				"WHERE md.string01 = :string01 AND md.status='ACTIVE' AND md.account.id=:accountId");
		query.setParameter("string01", string01);	
		query.setParameter("accountId", accountId);
		List<MenuData> mdList = query.getResultList();
		referenceId = mdList.get(mdList.size() - 1).getId();
		
		return referenceId;
	}
	
	/**
	 * This function retrieve object corresponding to conditions and menupath.
	 * @author Valsaraj
	 * added on 09/30/09
	 * @param Map<String, Object> params
	 * @return List<TrimEx> trimList
	 * @throws ParseException, JAXBException
	 */
    public Object findDoc(Long id, AccountUser accountUser, PrivateKey userPrivateKey) throws JAXBException {
		MenuData md = menuBean.findMenuDataItem(id);
		
		if (md != null) {
			DocXML docXMLFrom = (DocXML) docBean.findDocument(md.getDocumentId());
			Object doc = xmlProtectedBean.unmarshal(docXMLFrom, accountUser, userPrivateKey);
			
			return doc;
		}
		else
			return null;
	}
    
    /**
     * Returns a list of TolvenUser with the given account id.
     */
    @SuppressWarnings("unchecked")
    public List<TolvenUser> getTolvenUser(String accountId) {
        List<TolvenUser> list = null;
        
        try {
        	Query query = em.createQuery("SELECT u FROM TolvenUser u WHERE u.id IN (SELECT au.user.id FROM AccountUser au WHERE au.account.id =" + accountId + ") " );
            list = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
	 * Returns account email.
	 * 
	 * added on 02/02/2011
	 * @author valsaraj
	 * @param accountId - Tolven account id
	 * @param propertyName - property name
	 * @return email - account email
	 */
	public String getAccountEmail(long accountId, String propertyName) {
		String email = null;
		
		try {
			Query query = em.createQuery("SELECT ap.shortStringValue FROM AccountProperty ap WHERE ap.account.id = :accountId and ap.propertyName = :property");
			query.setParameter("accountId", accountId );
			query.setParameter("property", propertyName);
			email = (String)query.getSingleResult();			
		} catch (Exception e) {
			logger.info("Error finding email in account " + accountId );
		}
		
		return email;
	}
	
	/**
     * Returns the latest menudata corresponding to the given menudata_path.
     * It is used to retrieve the latest snapshot date in analysis screens
     * @author Pinky S
	 * added on 02/02/11
	 * @param Account account,String snapshotType
	 * @return List<MenuData> 
     */
	public List<MenuData> findLatestSnapshotDate(Account account,String snapshotType) {
			String[] snapshotTypeArrays = snapshotType.split(":");
			String allignedSnaphotTypeString = snapshotTypeArrays[0]+":"+snapshotTypeArrays[1]+":"+snapshotTypeArrays[2]+":all-";
			Query query = em.createQuery("SELECT md FROM MenuData md WHERE "
					+ "md.account.id = :account AND "
					+ "md.path  LIKE '"+ allignedSnaphotTypeString + "%' AND "
					+ "(md.deleted IS NULL OR md.deleted = false)");
			
			query.setParameter("account", account.getId());
			List<MenuData> patientcohorts = query.getResultList();
			return patientcohorts;
	}
	
	/**
     * Returns account list that matches title and user.
     * 
     * added on 06/16/2010
     * @author valsaraj
     * @param accountName - account name
     * @param user - Tolven user
     * @param accountTypeId - account type id
     * @return accountLists - account lists
     */
    @SuppressWarnings("unchecked")
	public List<Account> getAccountsByTitleAndUser(String accountName, TolvenUser user, long accountTypeId) {
		List<Account> accountLists = null;
		 
		try {
			String activeStatus = Status.ACTIVE.value();
			String strQuery = "SELECT a " +
								 "FROM Account a, AccountUser au " +
								 "WHERE " +
								 	"a.accountType.id = :accountTypeId " +
								 "AND " +
								 	"a.id = au.account.id " +
								 "AND " +
								 	"au.status = :activeStatus " +
								 "AND " +
								 	"au.user = :user " +
								 "AND " +
								 "a.title = :accountName";			
			Query query = em.createQuery(strQuery);
			query.setParameter("accountTypeId", accountTypeId);
			query.setParameter("activeStatus", activeStatus);
			query.setParameter("user", user);
			query.setParameter("accountName", accountName);
			accountLists = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return accountLists;
	}
    
    /**
     * Returns account list that matches user.
     * 
     * added on 07/12/2010
     * @author valsaraj
     * @param user - Tolven user
     * @param accountTypeId - account type id
     * @return accountLists - account lists
     */
    @SuppressWarnings("unchecked")
	public List<Account> getAccountsByUser(TolvenUser user, long accountTypeId) {
		List<Account> accountLists = null;
		 
		try {
			String activeStatus = Status.ACTIVE.value();
			String strQuery = "SELECT a " +
								 "FROM Account a, AccountUser au " +
								 "WHERE " +
								 	"a.accountType.id = :accountTypeId " +
								 "AND " +
								 	"a.id = au.account.id " +
								 "AND " +
								 	"au.status = :activeStatus " +
								 "AND " +
								 	"au.user = :user ";			
			Query query = em.createQuery(strQuery);
			query.setParameter("accountTypeId", accountTypeId);
			query.setParameter("activeStatus", activeStatus);
			query.setParameter("user", user);
			accountLists = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return accountLists;
	}
    
    /**
	 * Method to retrieve all children for a menudata
	 */
	public List<MenuData> findMenuChildren(Account account,long menuDataId){
		MenuData md = menuBean.findMenuDataItem(menuDataId);
		Query query = em.createQuery(String.format(Locale.US, "SELECT md  FROM MenuData md WHERE md.parent01 = :parent " +
				"AND md.status = 'ACTIVE' and md.account = :account"));
		query.setParameter( "parent", md);
		query.setParameter( "account", account);		
		List<MenuData> children = query.getResultList();
		if(children == null || children.size() == 0)
			return new ArrayList<MenuData>();
		return children;
	}
    
}