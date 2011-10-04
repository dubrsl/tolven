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
package org.tolven.app.bean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.tolven.app.MDVersionDTO;
import org.tolven.app.MenuLocal;
import org.tolven.app.MenuRemote;
import org.tolven.app.PersisterLocal;
import org.tolven.app.QueryBuilderLocal;
import org.tolven.app.el.GeneralExpressionEvaluator;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MDQueryResults;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MSResource;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuDataVersion;
import org.tolven.app.entity.MenuDataVersionMessage;
import org.tolven.app.entity.MenuLocator;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.app.entity.PlaceholderID;
import org.tolven.app.entity.UMSDataTransferObject;
import org.tolven.app.entity.UserMenuStructure;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.AccountUserRole;
import org.tolven.core.entity.Status;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.RuleQueueLocal;
import org.tolven.doc.entity.DocBase;
import org.tolven.doctype.DocumentType;
import org.tolven.trim.CD;
import org.tolven.trim.II;
import org.tolven.trim.SETIISlot;


/**
 * <p>Query and manipulate menu and index structures. In general, menu/index structures provide efficient yet flexible
 * data (MenuData) and meta-data (MenuStructure). The document schema and in particular the DocBase table holds
 * full, complex, and potentially deeply-nested structured documents. The menu structure attempts to "flatten"
 * data extracted from documents in such a way that the data is "squared up" into rows and columns for efficient
 * relational access for transactional and analytic applications.</p>
 * <p>The term Menu Data is used because it is more visually comprehensible than document index but in 
 * fact, one could think of menu data as document indexes. Unlike most general-purpose document indexes,
 * menu-data is always purpose-built. Further, the configuration is generally user or account-specific. It is 
 * NOT installation-specific. That is, Tolven does not require a system administrator to "configure the system" for
 * most menu and index behavior. A single document can contribute to zero one or many different MenuData indexes. Therefore, 
 * depending on how the index was populated, the the same document could show up in the query result more than once. For example,
 * a single lab result "document" or "message" like a CBC with several components may create separate MenuData instances for
 * each of the result components. And each one would point back to the same document as its source.</p>
 * <p></p>    
 * @author John Churin
 *
 */
@Local(MenuLocal.class)
@Remote(MenuRemote.class)
@Stateless
public class MenuBean implements MenuLocal, MenuRemote {
    @PersistenceContext
    private EntityManager em;
    @EJB AccountDAOLocal accountBean;
    @EJB DocumentLocal docBean;
    @EJB RuleQueueLocal ruleQueueBean;
    
    Logger logger = Logger.getLogger(this.getClass());
    public final static String QUERY_BUILDER_NAME = "queryBuilderJNDI";
    public final static String PERSISTER_NAME = "persisterJNDI";
    
    /*
     * A set of methods to call when persisting a menuData item.
     */
    private Set<String> persisters;
    
    // A list of queryBuilders to be called when we create a query. The builder will determine
    // if and how it changes the query criteria.
    private Set<String> queryBuilders;

    private final static String DEFAULT_QUERY_BUILDER = "java:global/tolven/tolvenEJB/DefaultQueryBuilder!org.tolven.app.QueryBuilderLocal";
    private final static String DEFAULT_PERSISTER = "java:global/tolven/tolvenEJB/DefaultPersister!org.tolven.app.PersisterLocal";

    public List<MenuData> findListContents(Account account, MenuStructure msList, MenuData mdParent) {
        Query query = em.createQuery("SELECT md FROM MenuData md " +
                "WHERE md.account = :account " +
                "AND md.menuStructure = :ms " +
                "AND (md.deleted is null OR md.deleted = false) " +
                "AND md.parent01 = :parent " );
        query.setParameter("account", account );
        query.setParameter("ms", msList );
        query.setParameter("parent", mdParent );
        List<MenuData> results = query.getResultList();
        return results;
    }
    /**
     * Prepare a MenuQueryControl for use on the server.
     * If we get MenuQueryControl from a remote source, we first need to find 
     * the MenuStructure based on path and accountId. 
     * We also consider the query parameter which may direct the query itself to 
     * another MenuStructure. This results in an "actualMenuStructure" which may not be the same
     * as the menuStructure item being displayed.
     */
    public void prepareMQC( MenuQueryControl ctrl) {
        // If the menuStructure is already known, then we're done.
        if (ctrl.getMenuStructure()!=null) {
            if (!(ctrl.getMenuStructure() instanceof AccountMenuStructure)) {
                ctrl.setMenuStructure(ctrl.getMenuStructure().getAccountMenuStructure());
            }
        } else {
            if (ctrl.getAccountId()==0) throw new IllegalStateException( "AccountId required in MenuQueryControl" );
            if (ctrl.getMenuStructurePath()==null) throw new IllegalStateException( "MenuStructure path required in MenuQueryControl" );
            // Get the actual menuStructure - it must be valid or an error is thrown.
            ctrl.setMenuStructure( findAccountMenuStructure(ctrl.getAccountId(), ctrl.getMenuStructurePath()) );
        }
        // Start with the easy option (actual = requested menustructure)
        ctrl.setActualMenuStructure(ctrl.getMenuStructure());
        // Look for query.
        String queryPath = ctrl.getMenuStructure().getAccountMenuStructure().getQuery();
        if (queryPath!=null) {
            if (queryPath.startsWith("global:")) {
                MenuLocator menuLocator = findMenuLocator(queryPath);
                ctrl.setActualMenuStructure( menuLocator.getMenuStructure());
            } else {
                ctrl.setActualMenuStructure( findMenuStructure(ctrl.getAccountId(), queryPath));
            }
        }
    }
    
    private UserMenuStructure getUMS(AccountUser accountUser, MenuStructure ms){
        UserMenuStructure ums = null;
        try{
            Query query = em.createQuery("SELECT m FROM UserMenuStructure m WHERE m.accountuser = :accountuser and m.underlyingMS = :menustructure");
            query.setParameter("accountuser", accountUser );
            query.setParameter("menustructure", ms.getAccountMenuStructure());
            List<UserMenuStructure> list = query.getResultList();
            if( !list.isEmpty() ){
                ums = (UserMenuStructure) query.getSingleResult();
            }
            
        }catch( NonUniqueResultException nure ){
            throw new RuntimeException(" Multiple entries in UserMenustructure for account " + accountUser + " menustructure :" + ms , nure);
        }catch (RuntimeException e) {
            logger.info(" User Menu Structure not found for account user:" + accountUser + " menustructure :" + ms);
//          throw new RuntimeException( "User Menu Structure not found for account user:" + accountUser + " menustructure :" + ms, e);
//          e.printStackTrace();
            
//          throw new IllegalArgumentException( "Error finding menustructure :" + ms.toString() + " in account " + accountUser, e);
        }
    
        return ums;
    }

    public MenuStructure findMenuStructure( AccountUser accountUser, String path ){
        AccountMenuStructure ams = findAccountMenuStructure( accountUser.getAccount().getId(), path);
        MenuStructure ms = findMenuStructure(accountUser, ams);
        return ms;
    }
    
    /**
     * Special case of findMenuDataItem where we also check the AccountTemplate if the requested account
     * does not have the item. Good for menuData that is static relative to an account such as TRIM.
     */
    public MenuData findDefaultedMenuDataItem( Account account, String path) {
        MenuPath menuPath = new MenuPath( path );
//      MenuData md = findMenuDataItem( account.getId(), path ); 
        MenuData md = findMenuDataItem( menuPath.getLeafNodeKey() );    
        return md;
    }
//  /**
//   * Find an active event referencing the specified placeholder. This is the
//   * event currently responsible for the state of the placeholder.
//   * @param mdPlaceholder
//   * @return
//   */
//  public MenuData findReferencingEvent( MenuData mdPlaceholder) {
//      
//  }

    public MenuStructure findMenuStructure( AccountUser accountUser, AccountMenuStructure msAccount ){
        
        MenuStructure ms = null;
        try{
            ms = getUMS( accountUser, msAccount );
            if(ms == null){
                ms = msAccount;
            }
        }catch(RuntimeException re){
            logger.info(" Run time exception at UserMenuStructure findMenuStructure, creating new ums");
            UserMenuStructure ums = new UserMenuStructure( accountUser, msAccount, null, null, null, null, null, null, null );
            em.persist( ums );
            ms = ums;
        }catch(Exception e){
            logger.info(" usermenustructure findmenustructure");
            e.printStackTrace();
        }
        if( ms == null ) ms = msAccount;
        return ms;
    }
    
    /**
     * @see MenuLocal
     */
    public MenuStructure findMenuStructure( Account account, String pathSuffix) {
        String path;
        if (pathSuffix.startsWith(":")) {
            path = account.getAccountType().getKnownType() + pathSuffix;
        } else {
            path = pathSuffix;
        }
        try {
            MenuStructure ms;
            Query query = em.createQuery("SELECT am FROM AccountMenuStructure am WHERE am.account = :account and am.path = :p");
            query.setParameter("account", account );
            query.setParameter("p", path);
            ms = (AccountMenuStructure)query.getSingleResult();
//          testAndSetBackButton(account, (AccountMenuStructure)ms );
            return ms;
        } catch (Exception e) {
            throw new IllegalArgumentException( "Error finding menustructure path " + path + " in account " + account.getId(), e);
        }
    }

    /**
     * @see MenuLocal
     */
    public MenuStructure findMenuStructure( long accountId, String path) {
        Account account = accountBean.findAccount(accountId);
            return findMenuStructure( account, path);
    }
    
    // Get original MenuStructure
    public AccountMenuStructure findAccountMenuStructure( long accountId, String path ){
        return (AccountMenuStructure) findMenuStructure( accountId, path);
    }
    
    /**
     * @see MenuLocal
     */
    public MenuStructure findUserMenuStructure( AccountUser accountUser, String pathSuffix) {
        String path;
        if (pathSuffix.startsWith(":")) {
            path = accountUser.getAccount().getAccountType().getKnownType() + ":" + pathSuffix;
        } else {
            path = pathSuffix;
        }
        try {
            Query query = em.createQuery("SELECT m FROM AccountMenuStructure m WHERE m.account = :account and m.path = :p");
            query.setParameter("account", accountUser.getAccount() );
            query.setParameter("p", path);
            AccountMenuStructure item = (AccountMenuStructure)query.getSingleResult();
//          testAndSetBackButton(accountUser.getAccount(), item );
            return item;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException( "Error finding menustructure path " + path + " in account user " + accountUser, e);
        }
    }

    /**
     * Given a menuStructure list, remove the items that are not allowed
     * in the list due to permission. If the allowRoles and denyRoles columns are null, 
     * then all roles are allowed. If a roles is explicitly denied, then it is denies
     * even if there is an allowed role match. If allowedRoles is non-null, then
     * the AccountUser must have at least one matching role.
     */
    public void filterByRole(List<MenuStructure> msList, AccountUser accountUser) {
//      logger.debug("Role check for accountUser: " + accountUser.getId());
        Set<AccountUserRole> auRoles = accountUser.getRoles();
        AccountUserRole matchRoleKey = new AccountUserRole();
        matchRoleKey.setAccountUser(accountUser);
        List<MenuStructure> msRemove = new ArrayList<MenuStructure>(10);
        for (MenuStructure ms : msList ){
            // No allow or deny means all
            if ((ms.getAllowRoles()==null || ms.getAllowRoles().length()==0) && 
                    (ms.getDenyRoles()==null || ms.getDenyRoles().length()==0)) {
                continue;
            }
            // A matching denial means the menu item will be removed
            if (ms.getDenyRoles()!=null) {
                String denyRoles[] = ms.getDenyRoles().split(",");
//              logger.debug("Denying: " + ms.getDenyRoles());
                for( String denyRole : denyRoles) {
                    matchRoleKey.setRole(denyRole);
                    if (auRoles.contains(matchRoleKey)) {
//                      logger.debug("Deny - Removing: " + ms.getPath());
                        msRemove.add(ms);
                        continue;
                    }
                }
            }
            // A matching "allow" means this item is OK
            if (ms.getAllowRoles()!=null) {
//              logger.debug("Allowing: " + ms.getAllowRoles());
                String allowRoles[] = ms.getAllowRoles().split(",");
                boolean allow = false;
                for( String allowRole : allowRoles) {
                    matchRoleKey.setRole(allowRole);
                    if (auRoles.contains(matchRoleKey)) {
                        // Allowed role so let them in
                        allow = true;
                        continue;
                    }
                }
                if (!allow) {
                    // Not an allowed role so must be removed
//                  logger.debug("Allow - Removing: " + ms.getPath());
                    msRemove.add(ms);
                }
            }
        }
        // Remove the ms items we identified as needing to be removed
        msList.removeAll(msRemove);
    }
    
    /**
     * This method returns child MenuStructure objects, filtered by roles allowed by the account user
     * and sorted by sequence number.
     */
    public List<MenuStructure> findMenuChildren( AccountUser accountUser, MenuStructure ms ){
        List<UserMenuStructure> userList = null;
        List<AccountMenuStructure> accList = null;
        List<MenuStructure> msList = new ArrayList<MenuStructure>( 50 );
        try{
            StringBuffer queryA = new StringBuffer( 350 );
            queryA.append( "SELECT um FROM UserMenuStructure um WHERE um.accountuser = :accountuser and um.underlyingMS.id IN " );
            queryA.append( "(SELECT am.id FROM AccountMenuStructure am WHERE am.account = :account AND am.parent = :parent )" );
            
            Query query = em.createQuery( queryA.toString() );
            query.setParameter("accountuser", accountUser);
            query.setParameter("parent", ms.getAccountMenuStructure());
            query.setParameter("account", accountUser.getAccount());
            userList = query.getResultList();
//          logger.debug(" User MenuStructure List " + userList.size());
            for( UserMenuStructure m : userList ){
//              logger.debug(" User MS:" + m );
                msList.add( m );
            }
            
            StringBuffer queryB = new StringBuffer( 350 );
            queryB.append( "SELECT m from AccountMenuStructure m WHERE m.parent = :menustructure " );
            queryB.append("AND (m.visible IS NULL OR m.visible <> 'never') ");
            queryB.append("AND (m.account = :account) ");
            queryB.append( " AND m.id NOT IN ( SELECT um.underlyingMS.id FROM UserMenuStructure um WHERE um.accountuser = :accountuser)" );

            Query query2 = em.createQuery( queryB.toString() );
            query2.setParameter("menustructure", ms.getAccountMenuStructure() );
            query2.setParameter("accountuser", accountUser);
            query2.setParameter("account", accountUser.getAccount());
            accList = query2.getResultList();
            
//          logger.debug(" Account Menu Structure : " + accList.size());
            for( AccountMenuStructure m : accList ){
                msList.add( m );
//              logger.debug(" Account MS:" + m);
            }
            
            // Reduce the list to only what the user is allowed to see
            filterByRole(msList, accountUser);
        }catch( RuntimeException re ){
            throw new IllegalArgumentException( "Error finding Children of menustructure " + ms.getAccountMenuStructure() + " for account user " + accountUser, re);
        }
        return msList;
    }
    
    /**
     * This method returns child MenuStructure objects, filtered by roles allowed by the account user
     * and sorted by sequence number.
     */
    public List<MenuStructure> findSortedChildren( AccountUser accountUser, MenuStructure ms ){
        return ms.getSortedList(findMenuChildren(accountUser, ms));
    }
    
    /**
     * Find the most recent menuData item in the specified list using the specified context
     * @param ms
     * @param context
     * @param nothingOlderThan
     * @param concepts
     * @return
     */
    public MenuData findMostRecent( MenuStructure msList, MenuData mdContext, Date nothingOlderThan, List<CD> concepts)  {
        Query query = em.createQuery("SELECT md FROM MenuData md " +
                "WHERE md.account.id = :account " +
                "AND md.menuStructure > :ms " +
                "AND md.date01 > :oldestDate " +
                "AND md.string01 LIKE :concept");
        query.setParameter("account", mdContext.getAccount() );
        query.setParameter("ms", msList );
        query.setParameter("oldestDate", nothingOlderThan);
        query.setParameter("concept", "%" + concepts.get(0).getDisplayName() + "%");
        List<MenuData> list = query.getResultList();
        if (list.size()>0) {
            return list.get(0);
        }
        return null;
    }
    /**
     * This method returns child MenuStructure objects, filtered by roles allowed by the account user
     * and has data available for them.
     */
    public List<MenuStructure> findChildrenHasData( AccountUser accountUser, String pathSuffix ){
        MenuStructure ms = findMenuStructure(accountUser,pathSuffix);
        List<MenuStructure> children = findMenuChildren(accountUser,ms );
        MenuPath menuPath = new MenuPath(ms.getPath());
        MenuQueryControl ctrl = new MenuQueryControl();
        List<MenuStructure> childrenHasData = new ArrayList<MenuStructure>();
        for(MenuStructure child : children){             
            ctrl.setMenuStructure(child.getAccountMenuStructure());
            ctrl.setOffset(0);
            ctrl.setOriginalTargetPath(menuPath);
            ctrl.setRequestedPath(menuPath);
            if(countMenuData(ctrl)>0)
                childrenHasData.add(child);
        }
        return childrenHasData;
    }
    
    /**
     * @see MenuLocal
     */
    public AccountMenuStructure findDescendentMenuStructure( long accountId, AccountMenuStructure parent, String path) {
        try {
            Account account = em.getReference( Account.class, accountId);
            Query query = em.createQuery("SELECT m FROM AccountMenuStructure m WHERE m.account = :account and m.path = :p");
            query.setParameter("account", account );
            query.setParameter("p", parent.getPath()+ ":" + path);
    //      logger.debug( "[MenuBean:findDescendentMenuStructure] Looking for path: " +  parent.getPath()+ ":" + path );
            AccountMenuStructure item = (AccountMenuStructure)query.getSingleResult();
    //        for (MenuStructure child : item.getChildren()) {  // Make sure children are instantiated in the structure we return
    //          long id = child.getId();
    //        }
//          testAndSetBackButton(account, item );
            return item;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException( "Error finding descendent menustructure path " + parent.getPath()+ ":" + path + " in account " + accountId, e);
        }
    }
    
    public void updateUserMenuStructure( AccountUser accountUser, String path, UMSDataTransferObject dto ){
        try{
            AccountMenuStructure msAccount = findAccountMenuStructure(accountUser.getAccount().getId(), path);
            UserMenuStructure ums = getUMS(accountUser, msAccount);
            if( ums != null ){ //If UMS exists, update existing entry
//              logger.debug(" existign UMS ");
                if( dto.getVisible() != null && !ums.getVisible().equalsIgnoreCase( dto.getVisible() ) ){
//                  logger.debug("visible is modified");
                    ums.setVisible( dto.getVisible() );
                }
                
                if( dto.getSequence() != null && ums.getSequence() != dto.getSequence().intValue()){
//                  logger.debug("sequence is changed");
                    ums.setSequence( dto.getSequence() );
                }
                if( dto.getColumnNumber() != null && ums.getColumnNumber() != dto.getColumnNumber() ){
//                  logger.debug("col is changed");
                    ums.setColumnNumber( dto.getColumnNumber() );
                }
                if( dto.getWindowstyle() != null ){
                    ums.setWindowstyle( dto.getWindowstyle() );
                }
                if( dto.getNumItems() != null ){
                    ums.setNumSummaryItems( dto.getNumItems() );
                }
                if( dto.getDefaultPathSuffix() != null ){
                    ums.setDefaultPathSuffix( dto.getDefaultPathSuffix());
                }
                if( dto.getStyle()!= null ){
                    ums.setStyle(dto.getStyle());
                }
                em.merge( ums );
            }else { // else create new entry;
//              logger.debug(" new entry");
                String visible = ( dto.getVisible() == null || msAccount.getVisible().equalsIgnoreCase( dto.getVisible() ))? null: dto.getVisible();
                Integer sequence = (dto.getSequence() != null && msAccount.getSequence() == dto.getSequence().intValue() )? null: dto.getSequence();

                if( visible != null || sequence != null || dto.getDefaultPathSuffix() != null || dto.getStyle() != null ||
                        dto.getColumnNumber() != null || dto.getWindowstyle() != null || dto.getNumItems() != null ){
                    ums = new UserMenuStructure( accountUser, msAccount, visible, sequence, dto.getColumnNumber(),
                            dto.getWindowstyle(), dto.getNumItems(), dto.getDefaultPathSuffix(), dto.getStyle() );
                    em.persist( ums );
//                  logger.debug(" new entry is created");
                }
            }
        } catch (RuntimeException e) {
            throw new IllegalArgumentException( "Error updating user menustructure path " + path + " in account User" + accountUser, e);
        }
    }
    
    public void setToDefaultMenuStructure( AccountUser accountUser, AccountMenuStructure parent ){
        try{
            StringBuffer queryA = new StringBuffer( 350 );
            queryA.append( "DELETE FROM UserMenuStructure um WHERE um.accountuser = :accountuser and um.underlyingMS IN " );
            queryA.append( "(SELECT am FROM AccountMenuStructure am WHERE am.parent = :parent )" );
            
            Query query = em.createQuery( queryA.toString() );
            query.setParameter("accountuser", accountUser);
            query.setParameter("parent", parent);
            query.executeUpdate();
            
        } catch (RuntimeException e) {
            throw new IllegalArgumentException( "Error set to default user menustructure for parent:"  + parent + " in account User" + accountUser, e);
        }
        
    }

    /**
     * @see MenuLocal
     */
    public MenuData findMenuDataItem( long id ) {
        return em.find(MenuData.class, id);
    }

    /**
     * @see MenuLocal
     */
    public MenuData findMenuDataItem( long accountId, String path ) {
        Query query = em.createQuery( "SELECT md FROM MenuData md WHERE md.account.id = :accountId and md.path = :p" );
        query.setParameter( "accountId", accountId );
        query.setParameter( "p", path );
        List<MenuData> items = query.getResultList();
//        // Copy the columns list to a transient attribute on the trim  
//      Map<String, MSColumn> columnMap = rslt.getMenuStructure().getColumnMap();
//      rslt.setColumnMap(columnMap);
        if (items.size()==1) return items.get(0);
        else return null;
    }
    

    /**
     * Return a list of list items referencing this menuData item from a specific list.
     * @param md the placeholder possibly occurring on a list
     * @param msList The MenuStructure defining the list
     * @return List of MD items on the list referencing the placeholder. Usually only one item returned but since it's not an enforced limitiation, the caller should expect zero or more.
     */
    public List<MenuData> findReferencingMDs( MenuData mdRef, MenuStructure msList ) {
        if (!MenuStructure.PLACEHOLDER.equals(mdRef.getMenuStructure().getRole())) throw new IllegalStateException( "The referenced item must be a placeholder");
        if (MenuStructure.PLACEHOLDER.equals(msList.getRole())) throw new IllegalStateException( "The referencing place must not be be a placeholder");
        StringBuffer sb = new StringBuffer(256);
        sb.append("SELECT md FROM MenuData md WHERE md.reference = :ref AND md.menuStructure = :msList ");
        sb.append("AND (md.deleted is null OR md.deleted = false) " );
        Query query = em.createQuery( sb.toString());
        query.setParameter("ref", mdRef);
        query.setParameter("msList", msList);
        return query.getResultList();
    }
    /**
     * Temporary: This is slow and very specific. But give us an insight into functions needed for real.
     * @param accountId
     * @param path
     * @return
     */
    public List<MenuData> findMenuDataByString04( long accountId, String path ) {
        Query query = em.createQuery("SELECT md FROM MenuData md " +
                "WHERE md.account.id = :account AND md.string04 = :p " +
                "AND md.menuStructure.role = 'placeholder'" );
        query.setParameter("account", accountId);
		query.setParameter("p", path);
        return query.getResultList();
    }
    
    /**
     * @see MenuLocal
     */
    public MenuData findMenuDataItem( MenuQueryControl ctrl ) {
        try {
            prepareMQC( ctrl );
//          return findMenuDataItem(ctrl.getActualMenuStructure().getAccount().getId(), ctrl.getOriginalTargetPath().getPathString());
            Query query = em.createQuery( "SELECT md FROM MenuData md WHERE md.account.id = :accountId and md.id = :id" );
            long id = ctrl.getRequestedPath().getLeafNodeKey();
            query.setParameter( "id", id );
            query.setParameter( "accountId", ctrl.getActualMenuStructure().getAccount().getId() );
            query.setMaxResults(2);
            List<MenuData> items = query.getResultList();
            if ( items.size()!=1) return null;
            MenuData rslt = items.get(0);
            return rslt;
    } catch (RuntimeException e) {
        throw new RuntimeException( "Error in findMenuDataItem for path " +  ctrl.getRequestedPath(), e); 
    }
    }
    
    protected void addWordFilter( String filter, StringBuffer sbFrom, StringBuffer sbWhere, Map<String, Object>params ) {
        if (filter==null) return;
        String words[] = filter.split("\\W");
        int wordNo = 0;
        for ( String rawWord : words ) {
            String word = rawWord.trim().toLowerCase();
            if (word.length()>0 && !StopList.ignore( word, "en_US" )) {
                wordNo++;
                sbFrom.append(", MenuDataWord mdw");
                sbFrom.append(wordNo);
                sbWhere.append( String.format(Locale.US, " AND (md = mdw%d.menuData AND mdw%d.word BETWEEN :wfl%d AND :wfh%d) ", wordNo, wordNo, wordNo, wordNo));
                params.put("wfl" + wordNo, word );
                params.put("wfh" + wordNo, word + "zzzzzzzzzzzzzzzzzzzz");
            }
        }
    }
    
    /**
     * Find the list of query builders to use.
     */
    public void initializeQueryBuilders() {
        try {
            if (queryBuilders==null) {
                Properties properties = new Properties();
                String propertyFileName = this.getClass().getSimpleName()+".properties"; 
                InputStream is = this.getClass().getResourceAsStream(propertyFileName);
                String queryBuilderNames = null;
                if (is!=null) {
                    properties.load(is);
                    queryBuilderNames = properties.getProperty(QUERY_BUILDER_NAME);
                    is.close();
                }
                queryBuilders = new HashSet<String>();
                if (queryBuilderNames!=null && queryBuilderNames.length()>0) {
                    String names[] = queryBuilderNames.split(",");
                    // Ignore duplicates
                    for (String name : names) {
                        queryBuilders.add(name);
                    }
                } else {
                    // No query builder's named, so use the default.
                    queryBuilders.add(DEFAULT_QUERY_BUILDER);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException( "Error initializing Query Builders", e);
        }
    }
    
    /**
     * @see MenuLocal
     */
    public Query prepareCriteria( MenuQueryControl ctrl, String init ) {
        prepareMQC( ctrl );
        // Allow one level of "referenced" indirection. The effect is to return data from a different MenuStructure
        // than the one requested.
//      ctrl.setActualMenuStructure( ctrl.getMenuStructure().getAccountMenuStructure().getReferenced());
        StringBuffer sbFrom = new StringBuffer( 350 );
        StringBuffer sbWhere = new StringBuffer( 350 );
        StringBuffer sbOrder = new StringBuffer( 350 );
        Map<String, Object> params = new HashMap<String, Object>( 5 );
        sbFrom.append("MenuData md");
        // Get help setting up the query.
        try {
            initializeQueryBuilders();
            InitialContext ctx = new InitialContext();
            List<QueryBuilderLocal> qbList = new ArrayList<QueryBuilderLocal>();
            QueryBuilderLocal qbExclusive = null;
            // First pass: Find out what the build wants to do.
            for (String queryBuilder : queryBuilders ) {
	    		Object qbo = ctx.lookup(queryBuilder);
                if (qbo instanceof QueryBuilderLocal) {
                    QueryBuilderLocal qbBean = (QueryBuilderLocal) qbo;
                    switch (qbBean.getParticipation(ctrl)) {
                    case NONE: break;
                    case EXCLUSIVE:
                        if (qbExclusive!=null) throw new RuntimeException( "Multiple QueryBuilders requesting exclusive control " + queryBuilder );
                        qbExclusive = qbBean;
                        break;
                    case FILTER: 
                        qbList.add(qbBean);
                        break;
                    }
                } else if (qbo!=null){
                    throw new RuntimeException( "For JNDI name " + queryBuilder + ", object " + qbo.getClass().getName() + " must implement " + QueryBuilderLocal.class.getName());
                } else {
                    throw new RuntimeException( "JNDI name " + queryBuilder + " name not found");
                }
            }
            // If exclusive control, kick out the others.
            // Otherwise, call the ones interested in filtering
            if (qbExclusive!=null) {
                qbExclusive.prepareCriteria( ctrl, sbFrom, sbWhere, sbOrder, params );
            } else {
                for (QueryBuilderLocal qbBean : qbList) {
                    qbBean.prepareCriteria( ctrl, sbFrom, sbWhere, sbOrder, params );
                }
            }
        } catch ( Exception e) {
            throw new RuntimeException( "Error building query " + ctrl.toString(), e);
        }
        //SK: added from CCHIT plugin
	if (sbOrder != null && sbOrder.length() > 0) {
			sbOrder.append(",md.id DESC");
		}

        // Assemble the query string
        String qs = String.format(Locale.US, "SELECT %s FROM %s WHERE %s %s", init, sbFrom, sbWhere, sbOrder);
        logger.debug( "MenuData Query: " + qs.toString());
        // Load up parameters we've been accumulating
//      if (ctrl.getFilter()!=null && ctrl.getFilter().length()>0) logger.debug( qs );
        Query query = em.createQuery( qs );
        logger.debug( "Query params: ");
        for ( Map.Entry<String, Object> e : params.entrySet()) {
            query.setParameter( e.getKey(), e.getValue() );
            logger.debug("   key:" + e.getKey() + ", value:" + e.getValue() );
        }
        return query;
    }

    /**
     * @see MenuLocal
     */
    public List<MenuData> findMenuData( MenuQueryControl ctrl )  {
        try {
            prepareMQC( ctrl );
            Query query = prepareCriteria( ctrl, "md");
//          logger.debug( "[findMenuData]: " + ctrl );
            if (ctrl.getLimit()>0) {
                query.setMaxResults(ctrl.getLimit());
            }
            query.setFirstResult(ctrl.getOffset());
            List<MenuData> items = query.getResultList();
            // Copy the columns list to a transient attribute on the trim  
            Map<String, MSColumn> columnMap = (ctrl.getActualMenuStructure().getAccountMenuStructure()).getColumnMap();
            for (MenuData item : items ) {
                item.setColumnMap(columnMap);
            }
            return items;
        } catch (RuntimeException e) {
            throw new RuntimeException( "Error in findMenuData for path " +  ctrl.getRequestedPath(), e); 
        }
    }
    
    /**
     * Given an external instance identifier (root+extension) and an account, find the associated
     * MenuData.
     * @param account
     * @param root
     * @param extension
     * @return zero or more menuData items matching by account, root and extension
     */
    public List<MenuData> findMenuDataById( Account account, String root, String extension ) {
        Query query = em.createQuery( "SELECT md FROM MenuData md, IN(md.placeholderIDs) pid " +
        "WHERE pid.account = :account AND pid.root = :root AND pid.extension = :extension " );
        query.setParameter( "account", account );
        query.setParameter( "root", root );
        query.setParameter( "extension", extension );
        List<MenuData> items = query.getResultList();
        return items;
    }

    /**
     * Lookup an ID that was not assigned by us
     * @param account
     * @param root
     * @param extension
     * @return placeholderID or null if other than exactly one result was found
     */
    public PlaceholderID findPlaceholderID( Account account, String root, String extension ) {
        Query query = em.createQuery( "SELECT id FROM PlaceholderID id " +
                                      " WHERE id.account = :account " +
                                      "   AND id.root = :root " +
                                      "   AND id.extension = :extension");
        query.setParameter("account", account);
        query.setParameter("root", root);
        query.setParameter("extension", extension);
        query.setMaxResults(2);
        List<PlaceholderID> items = query.getResultList();
        if ( items.size()!=1) return null;
        PlaceholderID rslt = items.get(0);
        return rslt;
    }
    
    /**
     * Local comparator used to sort column items by sequence number. 
     *
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

    
    /**
     * @see MenuLocal
     */
    public List<Object[]> findMenuDataRows( MenuQueryControl ctrl )  {
        try {
            prepareMQC( ctrl );
            List<MenuData> initialResult = findMenuData( ctrl ); 
            List<Object[]> finalResult = new ArrayList<Object[]>( initialResult.size()); 
            // Decide on a list of column names
            List<String> colNames;
            if (ctrl.getColumns()==null || ctrl.getColumns().size()==0) {
                List<MSColumn> columns = new ArrayList<MSColumn>(ctrl.getSortedColumns());
                Collections.sort( columns, new ColSeqSort());
                colNames = new ArrayList<String>( columns.size());
                for (MSColumn col : columns ) {
                    colNames.add(col.getHeading());
                }
            } else {
                colNames = ctrl.getColumns();
            }
            // Now pick off the individual rows for the caller to see.
            // We do this the hard way because some fields may not be relational and some
            // may be indirect such as patient name from a parent object.
            for (MenuData row : initialResult) {
                List<Object> outCols = new ArrayList<Object>( colNames.size());
                for (String colName : colNames ) {
                    outCols.add(row.getField(colName));
                }
                finalResult.add( outCols.toArray());
            }
            return finalResult;
        } catch (RuntimeException e) {
            throw new RuntimeException( "Error in findMenuData for path " +  ctrl, e); 
        }
    }
    
    /**
     * Build a set of column names that will be needed in a query query. Some MSColumns refer to more than
     * one database column and, in some cases, multiple MSColumns use the same database column.
     * For the query, we don't care how many times or how something is used, we just 
     * need to acquire it at this point.
     * We're not interested in deferred columns at this time. We pick those up after the query.
     */
    public static String[] extractColumnNamesFromMSColumns( Collection<MSColumn> cols, Set<String> limitColumns ) {
        Set<String> columns = new HashSet<String>(20);
        // We always get the id,path and sourceAccountId of this item
        columns.add("id");
        columns.add("path");
        columns.add("sourceAccountId");
        
        // Get the column names (from clause)
        // But if limit columns specified, then only those columns
        for( MSColumn col : cols ){
            if (limitColumns!=null && !limitColumns.contains(col.getHeading())) continue; 
            // Ignore computed columns
            if (!col.isComputed()) {
                col.extractColumnNames(columns);
            }
        }
        return columns.toArray(new String[columns.size()]);
    }
    
    /**
     * Return a Map per row of MenuData. The Map contains each of the
     * columns in menuData. Each column appears twice in the Map: one time using the internal 
     * name of the field and another containing the "heading" of the column. Also, if
     * a column contains extended fields, then the raw XML string is returned in the Map 
     * (not very useful in its raw form) and then each of the extended fields.
     * All maps contain both the <i>id</i> and the <i>path</i> of the MenuData item and a <i>reference</i> to the underlying
     * placeholder. 
     */
    public MDQueryResults findMenuDataByColumns( MenuQueryControl ctrl ){
        MDQueryResults mdQueryResults = new MDQueryResults(ctrl);
        prepareMQC( ctrl );
        try{
            Collection<MSColumn> columns = ctrl.getSortedColumns();
            Set<String> limitColumns = null;
            if (ctrl.getColumns()!=null && ctrl.getColumns().size() > 0 ) {
                limitColumns = new HashSet<String>(ctrl.getColumns());
            }
            String[] columnNames = extractColumnNamesFromMSColumns( columns, limitColumns );
            StringBuffer selectClause = new StringBuffer( 350 );
            // Build the query string
            for (String column : columnNames) {
                if (selectClause.length()!=0) selectClause.append(", ");
                selectClause.append("md.");
                selectClause.append(column);
            }
            Query query = prepareCriteria(ctrl, selectClause.toString() );
            if (ctrl.getLimit()>0) {
                query.setMaxResults(ctrl.getLimit());
            }
            query.setFirstResult(ctrl.getOffset());
            // Do the query
//          findMenuDataRows( ctrl );
            // Setup a FieldGetter which holds common attributes used by the formatter.
            MSColumn.RowMapFieldGetter fieldGetter = new MSColumn.RowMapFieldGetter( );
            // Some common attributes for all rows and columns
            fieldGetter.setNow(  ctrl.getNow() );
            fieldGetter.setTimeZone( ctrl.getTimeZone() );
            fieldGetter.setLocale( ctrl.getLocale() );
            // Setup for fancy "from" behavior as well
            TrimExpressionEvaluator evaluator = new TrimExpressionEvaluator();
            evaluator.addVariable("account", ctrl.getAccountUser().getAccount());
            evaluator.addVariable("accountUser", ctrl.getAccountUser());
            // TODO: JMC - Add hook to underlying document here.
            fieldGetter.setEvaluator( evaluator );
            // Get the results of the query
            List<Object[]> results = query.getResultList(); 
            for( Object[] row : results){
                Map<String, Object> rowMap = new HashMap<String, Object>(columnNames.length+columns.size());
                int c1 = 0;
                // Load the raw internal fields into the map
                for (String field : columnNames) {
                    rowMap.put(field, row[ c1 ]);
                    c1++;
                }
                // Expand extended columns into the map
                Object xml01 = rowMap.get("xml01");
                Map<String, Object> extendedFields = new HashMap<String, Object>();
/* Looks like it's impossible to get extended fields from a list
 * because extended in a list means get it from the placeholder directly.
                if (xml01!=null) {
                    extendedMenuDataBean.unmarshalExtendedFields( (byte[]) xml01, extendedFields );
                }
*/              // Make extended fields available to the fieldGetter
                fieldGetter.setExtendedFields( extendedFields );
                MenuData placeholder = null;
                // Expand underlying placeholder if possible by considering both reference.id and referencePath.
                Long referenceId = (Long) rowMap.get("reference.id");
                if (referenceId!=null) {
                    // Only query placeholder when we will for-certain reference it.
                    placeholder = this.findMenuDataItem(referenceId);
                }
                if (placeholder==null) {
                    String referencePath = (String) rowMap.get("referencePath");
                    if (referencePath!=null) {
                        // Only query placeholder when we will for-certain reference it.
                        placeholder = this.findMenuDataItem(ctrl.getAccountUser().getAccount().getId(), referencePath);
                    }
                }
                if (placeholder!=null) {
                    rowMap.put("_placeholder", placeholder);
                    expandSourceMap(rowMap);
                    for (Map.Entry<String, Object> entry : rowMap.entrySet() ) {
                        String variable = entry.getKey();
                        // Initial lowercase and camelCase otherwise
                        if (Character.isUpperCase(variable.charAt(0))) {
                            variable = Character.toLowerCase(variable.charAt(0)) + variable.substring(1);
                        }
                        evaluator.addVariable(variable, entry.getValue());
                    }
                    {   // get computed fields 
                        evaluator.pushContext();
                        evaluator.addVariable(placeholder.getMenuStructure().getNode(), placeholder);
                        for (MSColumn col : columns) {
                            if (col.isComputed() || col.isExtended()) {
                                Object val = null;
                                for (String from : col.getFroms()) {
                                    val = evaluator.evaluate(from);
                                    if (val!=null) {
                                        rowMap.put(col.getHeading(), val);
                                        break;
                                    }
                                }
                            }
                        }
                        evaluator.popContext();
                    }
                }
                // Make the rowMap available to fieldGetter (this will collect the formatted field values) 
                fieldGetter.setRowMap( rowMap );
                // Now create a separate entry for each of the column entries, including the formatting functions
                // performed by those columns. If the column is not an internal field, then the only thing we can do is formatting.
                for (MSColumn col : columns) {
                    // Ignore computed columns
                    if (!col.isComputed()) {
                        rowMap.put(col.getHeading(), col.getFormattedColumn( fieldGetter ));
                    }
                }
                mdQueryResults.addRow( rowMap );
            }
            
        }catch(Exception e){
            throw new RuntimeException("Error in findMenuDataByColumn for path " + ctrl.getActualMenuStructurePath(), e );
        }
        return mdQueryResults;
    }
    

    /**
     * Return a count of the number of menuData items specified in the criteria. Offset and limit are ignored.
     */
    public long countMenuData(MenuQueryControl ctrl  ) {
        try {
            prepareMQC( ctrl );
            Query query = prepareCriteria( ctrl, "COUNT(md)");
            Long rslt = (Long) query.getSingleResult();
//          logger.debug( "[MenuLocal.countMenuData]: " + ctrl + " count=" + rslt.longValue() );
            return rslt.longValue();
        } catch (RuntimeException e) {
            throw new RuntimeException( "Error in countMenuData for path " +  ctrl.getRequestedPath(), e); 
        }
    }
    
    /**
     * Return the date range of menuData items specified in the criteria. Offset and limit are ignored.
     */
    public List<Object> findMenuDataDateRange(MenuQueryControl ctrl  ) {
        try {
            prepareMQC( ctrl );
            Query query = prepareCriteria( ctrl, "min(date01),max(date01)");
            Object dates[] = (Object[]) query.getSingleResult();
            List<Object> datesRange = Arrays.asList(dates);
            return datesRange;
        } catch (RuntimeException e) {
            throw new RuntimeException( "Error in findMenuDataDateRange for path " +  ctrl.getRequestedPath(), e); 
        }
    }
    
    /**
     * Return a list of menudata that references the specified placeholder
     * @param mdPlaceholder
     * @return The list of menu data referencing the placeholder
     */
    public List<MenuData> findReferencingMenuData(MenuData mdPlaceholder) {
        StringBuffer queryString = new StringBuffer(200);
        queryString.append("SELECT md FROM MenuData md " );
        queryString.append("WHERE md.account = :account ");
        queryString.append("AND md.reference = :reference ");
        queryString.append("AND (md.deleted is null OR md.deleted = false) ");
        queryString.append("AND md.menuStructure.role != 'placeholder'");
        Query query = em.createQuery( queryString.toString() );
        query.setParameter( "account", mdPlaceholder.getAccount() );
        query.setParameter( "reference", mdPlaceholder );
        List<MenuData> results = query.getResultList();
        return results;
    }

    /**
     * Remove the listed menudata 
     * @param placeholders
     */
    public void removeMenuData( List<MenuData> menuData ) {
        for (MenuData md : menuData) {
            deleteMenuData(md);
        }
    }
    
    /**
     * Remove MenuData (lists, etc) referencing this placeholder. This method does not remove the placeholder itself or
     * any events, past or current.
     * @param mdPlaceholder
     * @return The number of items removed (marked as deleted)
     */
    public int removeReferencingMenuData( MenuData mdPlaceholder ) {
        List<MenuData> results = findReferencingMenuData(mdPlaceholder);
        int count = results.size();
        removeMenuData( results);
        return count;
    }
    
    /**
     * Remove any menuData item that references the specified document. This is usually done when a new document is submitted to
     * remove it from "to do" lists. The rules will put the document back on any lists it should be on now that the
     * document is "actionable".
     * @param accountId
     * @param documentId
     * @param removePlaceholders If true, placeholders are also removed
     * @return the number of rows actually deleted.
     */
    public int removeReferencingMenuData( long accountId, long documentId, boolean removePlaceholders) {
        logger.debug( "***** Remove MD references to document " + documentId );
        if (0==documentId) return 0;
    	// This is a critical function in that updates the doc entity so as to synchronize (via optimistic locking on version field)
    	// this operation with the actual submit processing (AppEvalAdapter). In other words, either this operation succeeds or the 
    	// processing succeeds.
    	DocBase doc = this.docBean.findDocument(documentId);
		if (removePlaceholders) {
			doc.setFinalSubmitTime(null);
		}
    	// If we're asked to remove placeholders but the document is already immutable, don't allow it
    	if (removePlaceholders && doc.getStatus().equals(Status.ACTIVE.value())) {
    		throw new RuntimeException( "Document is immutable");
    	}
        int count = 0;
        StringBuffer queryString = new StringBuffer(200);
        queryString.append("select md FROM MenuData md WHERE md.account.id = :account AND md.documentId = :docId");
        queryString.append(" AND (md.deleted is null OR md.deleted = false) " );
        
        if (!removePlaceholders) {
            queryString.append(" AND md.menuStructure.role != 'placeholder'");
        }
        Query query = em.createQuery( queryString.toString() );
        query.setParameter( "account", accountId );
        query.setParameter( "docId", documentId );
        List<MenuData> results = query.getResultList();
        count = results.size();
        for (MenuData md : results) {
            deleteMenuData(md);
            logger.debug( "Remove " + md.getPath() );
        }
        return count;
    }
    

    /**
     * Given a list of columns, update them in the database. Typically called from the configuration page for a user.
     * @param columns
     */
    public void updateColumns( List<MSColumn> columns) {
        for (MSColumn col : columns ) {
            em.merge(col);
        }
    }
    
    /**
     * Given a list of menus, update them in the database. Typically called from the configuration page for a user.
     * This is a good example of "detachment" where a list of entity objects is edited outside of a transaction and then
     * resynchronized with the database when the user submits the changes.
     * @param menus
     */
    public void updateMenus( List<MenuStructure> menus) {
        for (MenuStructure m : menus ) {
            em.merge(m);
        }
    }
    class MSComparator implements Comparator<MenuStructure> {

        public int compare(MenuStructure ms1, MenuStructure ms2) {
            return ms1.getPath().compareTo(ms2.getPath());
//          MenuStructure ms1Parent = ms1.getParent();
//          MenuStructure ms2Parent = ms2.getParent();
//          if (ms1Parent==null) return -1;
//          if (ms2Parent==null) return 1;
//          int pathComp = ms1Parent.getPath().compareTo(ms2Parent.getPath());
//          if (pathComp < 0) return -1;
//          if (pathComp > 0) return 1;
//          if (ms1.getSequence() < ms2.getSequence()) return -1;
//          return 1;
        }
    }
    
    /**
     * Return the full menu structure for a user.
     */
    public List<MenuStructure> findFullMenuStructure( long accountId) {
        Account account  = em.getReference( Account.class, accountId);
        Query query = em.createQuery("SELECT m FROM AccountMenuStructure m WHERE m.account = :account");
        query.setParameter("account", account );
        List<AccountMenuStructure> items = query.getResultList();
        List <MenuStructure> msItems = new ArrayList<MenuStructure>(items.size());
        Collections.sort(items, new MSComparator());
        for( AccountMenuStructure item : items ){
//          logger.info( item.getPath() + " " + item.getSequence());
            msItems.add( item );
        }
        return msItems;
    }
    
    public List<AccountMenuStructure> findFullAccountMenuStructure( long accountId) {
        Account account  = em.getReference( Account.class, accountId);
        Query query = em.createQuery("SELECT m FROM AccountMenuStructure m WHERE m.account = :account");
        query.setParameter("account", account );
        List<AccountMenuStructure> items = query.getResultList();
        Collections.sort(items, new MSComparator());
        return items;
    }
    /*
     * Method to get MSColumn(s) by account id for remote users only.
     * Local users can use msnode.getColumns(); 
     * Remote users get org.hibernate.LazyInitializationException when they use node.getColumns();
     */
    public List<MSColumn> findAllMSColumnsForAccount( long accountId){
        Account account  = em.getReference( Account.class, accountId);
        Query query = em.createQuery("SELECT m FROM MSColumn m WHERE m.account = :account");
        query.setParameter("account", account );
        List<MSColumn> items = query.getResultList();
        return items;
        
    }
    
    /**
     * Update the version number of any lists we maintain. Supports efficient autoRefresh from the browser.
     * "Portlet", timeline band, and calendar entry are just kinds of lists so we keep a version on those as well. 
     * Since this call is very likely to cause contention in the database resulting in user delays, or failed transactions, 
     * the actual update occurs behind a queue. 
     * The messages are implicitly idempotent: The queued actions achieve the same result regardless of the order they are delivered. 
     * @param md
     */
    public void updateMenuDataVersion( MenuDataVersionMessage mdvm ) {
            Query query = em.createQuery("SELECT mdv FROM MenuDataVersion mdv WHERE mdv.account.id = :accountId AND mdv.element = :element");
            query.setParameter("accountId", mdvm.getAccountId());
            query.setParameter("element", mdvm.getElement());
            List<MenuDataVersion> results = query.getResultList();
            MenuDataVersion mdv;
            if (results.size() < 1) {
                mdv = new MenuDataVersion();
                mdv.setAccount(em.getReference(Account.class, mdvm.getAccountId()));
                mdv.setElement(mdvm.getElement());
                mdv.setVersion(1);
                mdv.setRole(mdvm.getRole());
                mdv.setMinMaxDate(mdvm.getMinDate());
                mdv.setMinMaxDate(mdvm.getMaxDate());
                em.persist(mdv);
            } else {
                mdv = results.get(0);
                mdv.setVersion(mdv.getVersion()+1);
                mdv.setMinMaxDate(mdvm.getMinDate());
                mdv.setMinMaxDate(mdvm.getMaxDate());
                em.merge(mdv);
            }
        //      logger.debug( "CollectionVersion: " + md.getPath() + " = " + element);
    }
    
    /** Updates min and max dates for the data items in this menu
     * @param mdv
     * @param role
     * @deprecated
     */
// TODO: This method and the caller from buildJSONData appear to be obsolete. See if Srini agrees and then remove, JMC. 
// In either case, this method is unnecessary for updating MDV.
    public void updateMenuDataVersion( MenuDataVersion mdv, String role) {
        if ( mdv.getAccount().getDisableAutoRefresh() !=null && mdv.getAccount().getDisableAutoRefresh()==true) return;
        try{
            if ("list".equals(role) 
                || "trimlist".equals(role) 
                || "portlet".equals(role)
                || "band".equals(role)
                || "entry".equals(role)
                ) {
                String element = mdv.getElement();
                Query query = em.createQuery("Update MenuDataVersion mdv set mdv.minDate =:minDate,mdv.maxDate=:maxDate,mdv.role=:role WHERE mdv.account = :account AND mdv.element = :element");
                query.setParameter("account", mdv.getAccount());
                query.setParameter("element", element);
                query.setParameter("minDate", mdv.getMinDate());
                query.setParameter("maxDate", mdv.getMaxDate());
                query.setParameter("role", role);
                /*int results = */ query.executeUpdate();
            }
        }catch (RuntimeException e) {
            throw new RuntimeException( "Error updating minDate and maxDate for MenuDataVersion "  + mdv, e);
        }
    }

    /**
     * Simple lookup of a menuDataVersion entry
     * @param accountId
     * @param element
     * @return
     */
    public MenuDataVersion findMenuDataVersion( long accountId, String element) {
        Query query = em.createQuery("SELECT mdv FROM MenuDataVersion mdv WHERE mdv.account.id = :account AND mdv.element = :element");
        query.setParameter("account", accountId);
        query.setParameter("element", dereferencePath(accountId, element));
        List<MenuDataVersion> results = query.getResultList();
        if (results.size() < 1) return null;
        return results.get(0);
    }
    
    /**
     * If the requested element is actually 
     * @param accountId
     * @param element
     * @return
     */
    public String dereferencePath(long accountId, String element ) {
        MenuPath menuPath = new MenuPath(element);
        MenuStructure ms = this.findMenuStructure(accountId, menuPath.getPath());
        String path = element;
        if (ms.getReferenced()!=null) {
            MenuPath pathMenuPath = new MenuPath(ms.getReferenced().getPath(), menuPath);
            path = pathMenuPath.getPathString();
        }
        return path;
    }
    
    /**
     * When given a set of menu paths, we determine the actual (referenced) lists which are the ones
     * with the version number we seek.
     * @param elements
     * @return a map from the referenced path to the supplied element
     */
    public Map<String,String> dereferencePaths(Account account, Collection<String> elements ) {
        Map<String,String> paths = new LinkedHashMap<String, String>();
        for (String element : elements ) {
            paths.put(element,dereferencePath( account.getId(), element));
        }
        return paths;
    }
    /**
     * Lookup a list of menuDataVersions, and return them. 
     * @param account
     * @param a set of elements
     * @return
     */
    public List<MDVersionDTO> findMenuDataVersions(Account account, List<String> elements) {
        if (account.getDisableAutoRefresh() != null && account.getDisableAutoRefresh())
            return new ArrayList<MDVersionDTO>(0); 
        Map<String,String> elementVsPathMap = dereferencePaths(account, elements);
        List<String> elementsLst = new ArrayList<String>(elementVsPathMap.keySet());
        List<String> pathsLst = new ArrayList<String>(elementVsPathMap.values());
        
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT mdv FROM MenuDataVersion mdv WHERE mdv.account = :account AND mdv.element IN ( ");
        boolean firstTime = true;
        for (String path : pathsLst ) {
            if (firstTime) firstTime = false;
            else sb.append(",");
            sb.append( '\'');
            sb.append(path);
            sb.append( '\'');
        }
        sb.append(" ) ");
//      logger.debug(sb.toString());
        Query query = em.createQuery( sb.toString() );
        query.setParameter("account", account);
        List<MenuDataVersion> results = query.getResultList();
        List<MDVersionDTO> versions = new ArrayList<MDVersionDTO>(20);
        // return MDVs for all the menus that are using the same references paths also. 
        for (MenuDataVersion mdv : results) {
            for(int i=0; i<pathsLst.size();i++){
                if(pathsLst.get(i).equalsIgnoreCase(mdv.getElement())){
                    MDVersionDTO mdVersionDTO = new MDVersionDTO();
                    mdVersionDTO.setElement(elementsLst.get(i));
                    mdVersionDTO.setPath(pathsLst.get(i));
                    mdVersionDTO.setVersion(mdv.getVersion());
                    mdVersionDTO.setRole(mdv.getRole());
                    versions.add(mdVersionDTO);
                }
            }           
        }
        return versions;
    }
    /**
     * Find the list of persisters to use.
     */
    public void initializePersisters() {
        try {
            if (persisters==null) {
                Properties properties = new Properties();
                String propertyFileName = this.getClass().getSimpleName()+".properties"; 
                InputStream is = this.getClass().getResourceAsStream(propertyFileName);
                String persisterNames = null;
                if (is!=null) {
                    properties.load(is);
                    persisterNames = properties.getProperty(PERSISTER_NAME);
                    is.close();
                }
                persisters = new HashSet<String>();
                if (persisterNames!=null && persisterNames.length()>0) {
                    String names[] = persisterNames.split(",");
                    // Ignore duplicates
                    for (String name : names) {
                        persisters.add(name);
                    }
                } else {
                    // No persisters named, so use the default.
                    persisters.add(DEFAULT_PERSISTER);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException( "Error initializing MenuData Persisters", e);
        }
    }
    
    protected void persistOperation( MenuData md, PersisterLocal.Operation operation ) {
        try {
            initializePersisters();
            InitialContext ctx = new InitialContext();
            List<PersisterLocal> persisterList = new ArrayList<PersisterLocal>();
            PersisterLocal poExclusive = null;
            // First pass: Find out what the build wants to do.
            for (String persister : persisters ) {
                Object po = ctx.lookup(persister);
                if (po instanceof PersisterLocal) {
                    PersisterLocal poBean = (PersisterLocal) po;
                    switch (poBean.getParticipation(md, operation)) {
                    case NONE: break;
                    case EXCLUSIVE:
                        if (poExclusive!=null) throw new RuntimeException( "Multiple Persisters requesting exclusive control " + persister );
                        poExclusive = poBean;
                        break;
                    case SHARED: 
                        persisterList.add(poBean);
                        break;
                    }
                } else if (po!=null){
                    throw new RuntimeException( "For JNDI name " + persister + ", object " + po.getClass().getName() + " must implement " + PersisterLocal.class.getName());
                } else {
                    throw new RuntimeException( "Persister JNDI name " + persister + " name not found");
                }
            }
            // If exclusive control, kick out the others.
            // Otherwise, call the ones interested in filtering
            if (poExclusive!=null) {
                callPersistOperation( md, poExclusive, operation);
            } else {
                for (PersisterLocal poBean : persisterList) {
                    callPersistOperation( md, poBean, operation);
                }
            }
//	    	em.flush();
        } catch (Exception e) {
            throw new RuntimeException( "Exception peristing (" + operation + ") MenuData item " + md, e);
        }

    }
    
    protected void callPersistOperation( MenuData md, PersisterLocal poBean, PersisterLocal.Operation operation ) {
        switch (operation) {
            case PERSIST: poBean.persist( md ); break;
            case DELETE: poBean.delete( md ); break;
            case UPDATE: poBean.update( md ); break;
        }
    }
    /**
     * Persist a single menu data object. Normally this method should result in a new row being
     * added to the database. However, if the controlling menustructure specifies a uniqueKey, 
     * then it may actually cause an update of an existing MenuData item.
     * This method does not queue a message to update MenuDataVersion but does populate the appropriate
     * MenuDataVersion message for later submission via queueDeferredMDVs
     * @param md The MenuData item to be persisted
     * @param mdvs A map of MenuDataVersionMessages
     * @return New database item inserted. True if the item was actually inserted. 
     * False a uniqueKey is specified in MS and if this was a duplicate 
     */
    public boolean persistMenuDataDeferred( MenuData md, Map<String, MenuDataVersionMessage> mdvs ) {
        // Special processing if uniqueKey is specified
        if (md.getMenuStructure().getUniqueKey()!=null) {
            String newPath = md.calculatePath();
            Query query = em.createQuery("SELECT md FROM MenuData md WHERE account = :account AND path = :p");
            query.setParameter("account", md.getAccount());
            query.setParameter("p", newPath);
            query.setMaxResults(1);
            List<MenuData> mdExisting = query.getResultList();
            if (mdExisting.size()==1) {
                return false;
            }
        }
        persistOperation( md, PersisterLocal.Operation.PERSIST);
        updateMDVs( md, mdvs);
        return true;
    }
    
    /**
     * Update the supplied map of MenuDataVersionMessages with this MenuData
     * @param md
     * @param mdvs
     */
    public void updateMDVs( MenuData md, Map<String,MenuDataVersionMessage> mdvs ) {
        // See if this item applies
        if ((md.getAccount().getDisableAutoRefresh() ==null || md.getAccount().getDisableAutoRefresh()==false) 
        && md.isListItem()) {
            String listPath = md.getListPath();
            MenuDataVersionMessage mdv = mdvs.get(listPath);
            if (mdv==null) {
                // Construct a prototype of the MDV entity
                mdv = new MenuDataVersionMessage(md);
                mdvs.put(listPath, mdv);
            }
            mdv.applyMenuData(md);
        }
    }

    /**
     * Send deferred MDV Updates to the queue for processing
     */
    public void queueDeferredMDVs(Map<String, MenuDataVersionMessage> mdvs) {
        // Don't bother if there's nothing in the list
        if (mdvs.size() == 0) {
            return;
        }
        try {
            for (Map.Entry<String, MenuDataVersionMessage> mdv : mdvs.entrySet()) {
                ruleQueueBean.send(mdv.getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error queuing MenuDataVersion update message to: " + ruleQueueBean.getQueueName(), e);
        }
    }

    /**
     * Persist a single menu data object. Normally this method should result in a new row being
     * added to the database. However, if the controlling menustructure specifies a uniqueKey, 
     * then it may actually cause an update of an existing MenuData item.
     * @param md
     * @return New database item inserted. True if the item was actually inserted. 
     * False a uniqueKey is specified in MS and if this was a duplicate 
     */
    public boolean persistMenuData( MenuData md ) {
        Map<String, MenuDataVersionMessage> mdvs = new HashMap<String,MenuDataVersionMessage>();
        boolean rslt = persistMenuDataDeferred( md, mdvs );
        queueDeferredMDVs( mdvs );
        return rslt;
    }

    public void updateMenuDataDeferred(MenuData md, Map<String, MenuDataVersionMessage> mdvs) {
        persistOperation( md, PersisterLocal.Operation.UPDATE);
        updateMDVs( md, mdvs);
    }
    
    public void deleteMenuDataDeferred(MenuData md, Map<String, MenuDataVersionMessage> mdvs) {
        persistOperation( md, PersisterLocal.Operation.DELETE);
        updateMDVs( md, mdvs);
    }

    /**
     * Delete a menuData item. Normally, this means setting the deleted flag)
     * @param md
     */
    public void deleteMenuData(MenuData md) {
        Map<String, MenuDataVersionMessage> mdvs = new HashMap<String,MenuDataVersionMessage>();
        deleteMenuDataDeferred( md, mdvs );
        queueDeferredMDVs( mdvs );
    }
    /**
     * Update a MenuData entity
     * @param md
     */
    public void updateMenuData(MenuData md) {
        Map<String, MenuDataVersionMessage> mdvs = new HashMap<String,MenuDataVersionMessage>();
        updateMenuDataDeferred( md, mdvs );
        queueDeferredMDVs( mdvs );
    }
    
    /**
     * Add a node to the targetAccount based on the source menuStructure entry.
     * Note: references, which may be forward references, are not done here.
     * @param targetAccount
     * @param sourceMS
     * @return
     */
    public AccountMenuStructure addCopyOfMenuStructure( Account targetAccount, 
            AccountMenuStructure sms ) {
        AccountMenuStructure msParent = null;
        AccountMenuStructure msNew = new AccountMenuStructure();
        if (sms.getParent()!=null) {
            msParent = findAccountMenuStructure( targetAccount.getId(), sms.getParent().getPath());
            msParent.getChildren().add(msNew);
        }
        msNew.setAccount(targetAccount);
        replaceColumns( msNew, sms );
        msNew.setLevel(sms.getLevel());
        msNew.setMenuTemplate(sms.getMenuTemplate());
        msNew.setNode(sms.getNode());
        msNew.setParent(msParent);
        msNew.setRepeating(sms.getRepeating());
        msNew.setRole(sms.getRole());
        msNew.setQuery(sms.getQuery());
        msNew.setMenuEventHandlerFactory(sms.getMenuEventHandlerFactory());
        msNew.setMenuEventHandlerData(sms.getMenuEventHandlerData());
        msNew.setRepeating(sms.getRepeating());
        msNew.setSequence(sms.getSequence());
        msNew.setColumnNumber(sms.getColumnNumber());
        msNew.setTemplate(sms.getTemplate());
        msNew.setText(sms.getText());
        msNew.setTextOverride(sms.getTextOverride());
        msNew.setFilter(sms.getFilter());
        msNew.setUniqueKey(sms.getUniqueKey());
        msNew.setInitialSort(sms.getInitialSort());
        msNew.setVisible(sms.getVisible());
        msNew.setReferenced(sms.getReferenced());
        msNew.setStyle( sms.getStyle());
        msNew.setEventPath(sms.getEventPath());
        msNew.setDefaultPathSuffix(sms.getDefaultPathSuffix());
        em.persist( msNew );
        
        if (msParent!=null) logger.info( "Adding " + msNew.getNode() + " to " + msParent.getPath());
        else logger.info( "Adding root: " + msNew.getNode());
        return msNew;
    }
    /**
     * See if a two fields have changed or not
     * @param oldValue
     * @param newValue
     * @param accountId
     * @param field
     * @return
     */
    public boolean diffCheck(Object oldValue, Object newValue, long accountId, String field ) {
//      if (oldValue !=null && (oldValue instanceof String && ((String)oldValue).length()<1)) {
//          logger.info("Updating account " + accountId + ": Zero length String in field " + field );
//      }
        if (oldValue == newValue) return false;
        if (oldValue!=null && oldValue.equals(newValue)) return false;
        logger.info( "Updated Account: " + accountId + 
                " field: " + field + " from " + oldValue + " to " + newValue);
        return true;
    }

    /**
     * Resolve differences in column definitions between two menuStructure entries.
     *
     */
    public void replaceColumns(MenuStructure tms, MenuStructure sms) {
        // Make a set of all columns already in the target
        Map<String, MSColumn> currentColumns = new HashMap<String, MSColumn>();

        for (MSColumn col : tms.getColumns()) {
            currentColumns.put(col.getHeading(), col);
        }

        // Now add or update the new column info
        for (MSColumn col : sms.getColumns()) {
            MSColumn newCol = currentColumns.get(col.getHeading());
            if (newCol == null) {
                newCol = new MSColumn();
                newCol.setHeading(col.getHeading());
                tms.getColumns().add(newCol);
            } else {
                // Remove this column from the current columns list
                // Anything left in the list must be deleted, later.
                currentColumns.remove(col.getHeading());
            }
            newCol.setAccount(tms.getAccount());
            newCol.setDisplayFunction(col.getDisplayFunction());
            newCol.setDisplayFunctionArguments(col.getDisplayFunctionArguments());
            newCol.setText(col.getText());
            newCol.setInternal(col.getInternal());
            newCol.setMenuStructure(tms.getAccountMenuStructure());
            newCol.setSequence(col.getSequence());
            newCol.setSupressColumns(col.getSupressColumns());
            newCol.setAlign(col.getAlign());
            newCol.setWidth(col.getWidth());
            newCol.setVisible(col.getVisible());
            newCol.setFrom(col.getFrom());
            newCol.setOutputFormat(col.getOutputFormat());
            newCol.setDatatype(col.getDatatype());
            em.persist(newCol);
        }
        // Any columns left in the map should be removed from DB.
        for (Entry<String, MSColumn> entry : currentColumns.entrySet()) {
            em.remove(entry.getValue());
            tms.getColumns().remove(entry.getValue());
        }
    }
    
    /**
     * Given a path to a global menu, such as TrimList, return
     * the menuLocator that tells us where the list actually is.
     * A RuntimeException is thrown if the item is not found.
     * @param path
     * @return MenuLocator for the given path
     */
    public MenuLocator findMenuLocator( String path) {
        Query query = em.createQuery("SELECT md FROM MenuLocator md " +
                "WHERE md.path = :p");
        query.setParameter("p", path);
        MenuLocator menuLocator = (MenuLocator)query.getSingleResult();
        return menuLocator;
    }
    
    /**
     * Create a new Account menuStructure
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
     * Given a path to a global menu, such as TrimList, find, or 
     * create the MenuLocator. In either case, return the menuLocator.
     * @param path
     * @return MenuLocator for the given path
     */
    public MenuLocator createOrUpdateMenuLocator( String path, List<MSColumn> columns) {
        Query query = em.createQuery("SELECT md FROM MenuLocator md " +
            "WHERE md.path = :p");
        query.setParameter("p", path);
        List<MenuLocator> menuLocators = query.getResultList();
        MenuLocator menuLocator;
        if (menuLocators.size()>0) {
            menuLocator = menuLocators.get(0);
            /*String unlazy = */ menuLocator.getMenuStructure().getPath();
            /*String unlazy2 = */ menuLocator.getMenuStructure().getAccount().getTitle();
        } else {
            menuLocator = new MenuLocator();
            menuLocator.setPath(path);
            menuLocator.setMenuStructure(createAccountMenuStructure(path, MenuStructure.TRIMLIST));
            em.persist(menuLocator);
        }
        // Update columns (remove previous column definitions, add new ones)
        for (MSColumn oldCol : menuLocator.getMenuStructure().getColumns()) {
            em.remove(oldCol);
        }
        menuLocator.getMenuStructure().getColumns().clear();
        if (columns !=null ) {
            for (MSColumn col : columns) { 
                col.setMenuStructure(menuLocator.getMenuStructure());
            }
            menuLocator.getMenuStructure().getColumns().addAll(columns);
        }
        return menuLocator;
    }
    /**
     * Remote-friendly version of updateMenuStructure
     * @param accountId
     */
    public void updateMenuStructure( long accountId ) {
        updateMenuStructure(accountBean.findAccount(accountId));
    }

    public void updateMenuStructure( Account targetAccount ) {
        if (accountBean.isAccountTemplateCurrent(targetAccount)) {
            return;
        }
        // This action sets the new Template Account based on the account type
        accountBean.markAccountAsUpToDate(targetAccount);
        Account sourceAccount = targetAccount.getAccountTemplate(); 
//      if (!knownType.equals(sourceAccount.getAccountType().getKnownType())) 
//          throw new IllegalStateException( "Account types must be equal in order to perform an update");
        Map<String, AccountMenuStructure> targetMap = new HashMap<String, AccountMenuStructure>();
//      Map<String, LinkedList<MenuStructure>> references = new HashMap<String, LinkedList<MenuStructure>>(); 
        // Create a map of each path we know about in the target
        for (AccountMenuStructure tms : findFullAccountMenuStructure( targetAccount.getId())) {
            targetMap.put(tms.getPath(), tms);
        }
        for (AccountMenuStructure sms : findFullAccountMenuStructure( sourceAccount.getId())) {
            // If this entry is not found, we need to add it
            // We can be certain that the parent will already exist because the source list
            // is sorted by path name. For example, echr:activity will occur before echr:activity:all.
            // If it is found, we'll see if there are any updates to apply
            AccountMenuStructure tms;
            if (!targetMap.containsKey(sms.getPath())) {
                tms = addCopyOfMenuStructure( targetAccount, sms );
                targetMap.put(sms.getPath(), tms);
            } else {
                tms = targetMap.get(sms.getPath());
                if (diffCheck(tms.getText(), sms.getText(), 
                        tms.getAccount().getId(), sms.getPath() + " (text)")) {
                    tms.setText(sms.getText());
                }
                if (diffCheck(tms.getTextOverride(), sms.getTextOverride(), 
                        tms.getAccount().getId(), sms.getPath() + " (textOverride)")) {
                    tms.setTextOverride(sms.getTextOverride());
                }
                if (diffCheck(tms.getTemplate(), sms.getTemplate(), 
                            tms.getAccount().getId(), sms.getPath() + " (template)")) {
                    tms.setTemplate(sms.getTemplate());
                }
                if (diffCheck(tms.getMenuTemplate(), sms.getMenuTemplate(), 
                        tms.getAccount().getId(), sms.getPath() + " (menuTemplate)")) {
                    tms.setMenuTemplate(sms.getMenuTemplate());
                }
                if (diffCheck(tms.getVisible(), sms.getVisible(), 
                        tms.getAccount().getId(), sms.getPath() + " (visible)")) {
                    tms.setVisible(sms.getVisible());
                }
                if (diffCheck(tms.getRepeating(), sms.getRepeating(), 
                        tms.getAccount().getId(), sms.getPath() + " (repeating)")) {
                    tms.setRepeating(sms.getRepeating());
                }
                if (diffCheck(tms.getSequence(), sms.getSequence(), 
                        tms.getAccount().getId(), sms.getPath() + " (sequence)")) {
                    tms.setSequence(sms.getSequence());
                }
                if (diffCheck(tms.getFilter(), sms.getFilter(), 
                        tms.getAccount().getId(), sms.getPath() + " (filter)")) {
                    tms.setFilter(sms.getFilter());
                }
                if (diffCheck(tms.getInitialSort(), sms.getInitialSort(), 
                        tms.getAccount().getId(), sms.getPath() + " (initialSort)")) {
                    tms.setInitialSort(sms.getInitialSort());
                }
                if (diffCheck(tms.getUniqueKey(), sms.getUniqueKey(), 
                        tms.getAccount().getId(), sms.getPath() + " (uniqueKey)")) {
                    tms.setUniqueKey(sms.getUniqueKey());
                }
                if (diffCheck(tms.getColumnNumber(), sms.getColumnNumber(), 
                        tms.getAccount().getId(), sms.getPath() + " (columnNumber)")) {
                    tms.setColumnNumber(sms.getColumnNumber());
                }
                if (diffCheck(tms.getStyle(), sms.getStyle(), 
                        tms.getAccount().getId(), sms.getPath() + " (style)")) {
                    tms.setStyle(sms.getStyle());
                }
                if (diffCheck(tms.getEventPath(), sms.getEventPath(), 
                        tms.getAccount().getId(), sms.getPath() + " (eventPath)")) {
                    tms.setEventPath(sms.getEventPath());
                }
                if (diffCheck(tms.getDefaultPathSuffix(), sms.getDefaultPathSuffix(), 
                        tms.getAccount().getId(), sms.getPath() + " (defaultPathSuffix)")) {
                    tms.setDefaultPathSuffix(sms.getDefaultPathSuffix());
                }
                // If the source contains a reference, just copy it exactly to the target.
                // **** This results in an efficient, but potentially cross-account reference to
                // existing menuData, used for trim menus
                // But we don't need to check difference since by definition most referenced 
                // fields change each time we update.
//              if (diffCheck(tms.getReferenced(), sms.getReferenced(),
//                  tms.getAccount().getId(), sms.getPath() + " (referenced)")) {
                    tms.setReferenced(sms.getReferenced());
//              }
                // A query spec also uses referenced but in a different way: The result will be a
                // reference to a menuStructure within this account, the one specified by the
                // name in query
                if (diffCheck(tms.getQuery(), sms.getQuery(), 
                        tms.getAccount().getId(), sms.getPath() + " (query)")) {
                    tms.setQuery(sms.getQuery());
                }
                    if (diffCheck(tms.getMenuEventHandlerFactory(), sms.getMenuEventHandlerFactory(), 
                            tms.getAccount().getId(), sms.getPath() + " (menuEventHandlerFactory)")) {
                        tms.setMenuEventHandlerFactory(sms.getMenuEventHandlerFactory());
                    }
                    if (diffCheck(tms.getMenuEventHandlerData(), sms.getMenuEventHandlerData(), 
                            tms.getAccount().getId(), sms.getPath() + " (menuEventHandlerData)")) {
                        tms.setMenuEventHandlerData(sms.getMenuEventHandlerData());
                    }
                // Update the columns as well
                replaceColumns( tms, sms );
            }
        }
        // Update referenced MenuStructures
        for (AccountMenuStructure tms : targetMap.values()) {
            if (tms.getQuery()!=null) {
                AccountMenuStructure referenced  = targetMap.get(tms.getQuery());
                if (referenced!=null) tms.setReferenced(referenced);
            }
        }
    }
/*
    private void debugPrint( MenuStructure ms ) {
        logger.info( "MS: " + ms.getNode() );
        for (MenuStructure msChild : ms.getChildren()) {
            debugPrint(msChild);
        }
        for (MSColumn col : ms.getColumns()) {
//          em.persist(col);
//          logger.debug( "    Col: " + col.getHeading() );
        }
    }
    */
    /**
     * Persist a menuStructure to the database (recursively if we have to)
     * @param ms
     */
    public void persistMenuStructure( MenuStructure ms ) {
//      debugPrint( ms );
        em.persist(ms);
//      logger.debug( "MS Persisted: " + ms.getNode() + " id=" + ms.getId() );
    }
    
    /**
     * Store a resource into the database. The resource primary key is Account+name
     * @param resource
     */
    public void persistResource( MSResource resource) {
        logger.info( "MSResource Persisted: " + resource.getName() + " account: " + resource.getAccount().getId());
        em.persist(resource);
    }
    
    public MSResource findMSResource( long templateAccountId, String path ) {
        Query query = em.createQuery("SELECT msr FROM MSResource msr WHERE msr.account.id = :accountId AND msr.name = :p" );
        query.setParameter("accountId", templateAccountId);
        query.setParameter("p", path);
        return (MSResource)query.getSingleResult();
    }

    public List<MSResource> findAccountResources( long templateAccountId ) {
        Query query = em.createQuery("SELECT msr FROM MSResource msr WHERE msr.account.id = :accountId" );
        query.setParameter("accountId", templateAccountId);
        return query.getResultList();
    }
    
    /**
     * See if there's a menu structure available for the specified account. 
     * @param user
     * @return true if at least one item found for this account, otherwise false
     */
    public AccountMenuStructure getRootMenuStructure( Account account ) {
        Query query = em.createQuery("SELECT m FROM AccountMenuStructure m WHERE m.account = :account and m.parent = null");
        query.setParameter("account", account);
        List<AccountMenuStructure> items = query.getResultList();
        if (items.size()>0) return items.get(0);
        return null;
    }

    /**
     * Called to create the metadata for a new account. The source will either be the account specified as the template
     * for that accountType or we will grind out a "factory preset" menu structure.
     */
    public MenuStructure createDefaultMenuStructure( Account account ) {
//      logger.debug( "createDefaultMenuStructure, if needed, for account: " + account.getId());
        MenuStructure root = getRootMenuStructure( account);
        if (root!=null) { 
//          logger.debug( "MenuStructure already exists");
            return root;
        }
        
        // If a template is specified in the accountType, then use it, otherwise, 
        Account accountTemplate = accountBean.findAccountTemplate(account);
        return getRootMenuStructure( accountTemplate );

    }
    /**
     * Return the nearest ancestor that is a placeholder, or null if no placeholder above this ms.
     * @param ms
     * @return MenuStructure of owner or null
     */
    public MenuStructure findOwner(MenuStructure ms) {
        MenuStructure msParent = ms.getParent();
        while (msParent!=null ) {
            if (MenuStructure.PLACEHOLDER.equals(msParent.getRole())) {
                return msParent;
            }
            msParent = msParent.getParent();
        }
        return null;
    }
    
    /**
     * Prior to evaluating an expression, this method is called to ensure that if a placeholder is mentioned that we
     * find the appropriate related contents.
     * @param ee
     */
    public void prepareEvaluator( TrimExpressionEvaluator ee ) {
        MenuData mdPlaceholder = (MenuData) ee.get(DocumentType.PLACEHOLDER);
        if (mdPlaceholder!=null) {
            MenuData mdOwner = mdPlaceholder;
            while (mdOwner!=null) {
                ee.addVariable(mdOwner.getMenuStructure().getNode(), mdOwner);
                mdOwner = mdOwner.getParent01();
            }
            if (mdPlaceholder.getDocumentId()!=0) {
                ee.addVariable( DocumentType.DOCUMENT_ID, mdPlaceholder.getDocumentId());
                docBean.prepareEvaluator(ee);
                if (mdPlaceholder.getDocumentPathVariable()!=null) {
                    ee.addVariable(mdPlaceholder.getDocumentPathVariable(), mdPlaceholder.getDocumentPath());
                }
            }
        }
    }
    
    /**
     * Add variables representing placeholders elsewhere in the graph of objects connected to the
     * "_placeholder" variable.
     * @param evaluator
     * @param sourceMap
     */
    //public static void expandSourceMap( TrimExpressionEvaluator evaluator, Map<String, Object> sourceMap ) {
    public static void expandSourceMap( Map<String, Object> sourceMap ) {
        MenuData mdPlaceholder = (MenuData) sourceMap.get("_placeholder");
        if (mdPlaceholder==null) return; 
        // Populate the list (or whatever) from the placeholder which we name based on the node name of the placeholder. eg patlist might reference patient.
        // For the owner, we need to visit the entire hierarchy via parent01
        MenuData mdOwner = mdPlaceholder;
        while (mdOwner!=null) {
            sourceMap.put(mdOwner.getMenuStructure().getNode(), mdOwner);
            mdOwner = mdOwner.getParent01();
        }
//      if (mdPlaceholder.getParent02()!=null) {
//          sourceMap.put(mdPlaceholder.getParent02().getMenuStructure().getNode(), mdPlaceholder.getParent02());
//      }
//      if (mdPlaceholder.getParent03()!=null) {
//          sourceMap.put(mdPlaceholder.getParent03().getMenuStructure().getNode(), mdPlaceholder.getParent03());
//      }
//      if (mdPlaceholder.getParent04()!=null) {
//          sourceMap.put(mdPlaceholder.getParent04().getMenuStructure().getNode(), mdPlaceholder.getParent04());
//      }
    }
    
    /**
     * Using the object graph supplied, we find the columns requested to be populated and populate them.
     * But only those with an internal attribute. If that is missing, then the field is acquired later, upon demand.
     */
    public void populateMenuData( Map<String, Object> sourceMap, MenuData destination ) {
//      logger.debug( "[populateMenuData] variable=" + variable + ", source=" + source.getClass().getSimpleName() + ", destination=" + destination.getId() + " path=" + destination.getMenuStructure().getPath());
        try {
            // If we are the placeholder, then add that indicator to the sourceMap. This allows self-references.
            if (!sourceMap.containsKey("_placeholder") && destination.getMenuStructure().getRole().equals(MenuStructure.PLACEHOLDER)) {
                sourceMap.put("_placeholder", destination);
            }
            expandSourceMap( sourceMap );
            GeneralExpressionEvaluator evaluator = null;
            if (sourceMap instanceof GeneralExpressionEvaluator) {
                evaluator = (GeneralExpressionEvaluator)sourceMap;
            } else {
                evaluator = new TrimExpressionEvaluator();
				if (sourceMap.entrySet()!=null) {
                for (Map.Entry<String, Object> entry : sourceMap.entrySet() ) {
                    String variable = entry.getKey();
                    // Initial lowercase and camelCase otherwise
                    if (Character.isUpperCase(variable.charAt(0))) {
                        variable = Character.toLowerCase(variable.charAt(0)) + variable.substring(1);
                    }
                    evaluator.addVariable(variable, entry.getValue());
                }
				}
                evaluator.addVariable("account", destination.getAccount());
            }
            // If this is an event, then we'll use the metadata of the placeholder.
            AccountMenuStructure msAccount = destination.getPlaceholderMenuStructure();
            // Pick up as many placeholder ids as we known about
            if (msAccount.getIdFrom()!=null) {
                String idFroms[] = msAccount.getIdFrom().split("\\|");
                Object idResult = null;
                for (String from : idFroms) {
                    idResult = evaluator.evaluate(from);
//              logger.debug( "[populateMenuData] value=" + result);
                    if (idResult!=null) break;
                }
                // Add all placeholder id's we find
                if (idResult != null) {
                    if (idResult instanceof SETIISlot) {
                        SETIISlot slot = (SETIISlot)idResult;
                        for (II ii : slot.getIIS()) {
                            if (ii.getExtension()!=null && ii.getExtension().length() > 0) {
                                destination.addPlaceholderID(ii.getRoot(), ii.getExtension(), ii.getAssigningAuthorityName());
                            }
                        }
                    }
                }
            }
            // Parent01 is special in that it must contain our "owning" parent, if any
            MenuStructure owner = findOwner(destination.getMenuStructure());
            if (owner!=null) {
                MenuData parent = destination.getParent01();
                // If parent01 is already set, just make sure it has the right menuStructure
                if (parent!=null) {
                    if (owner!=parent.getMenuStructure()) {
                        throw new RuntimeException( "Parent of " + destination.getPath() + " expected type is " + owner.getPath() + " actual type is " + parent.getMenuStructure().getPath());
                    }
                } else {
                    Object sourceItem = sourceMap.get(owner.getNode());
                    if (sourceItem instanceof MenuData) {
                        parent = (MenuData) sourceItem;
                    } else if (sourceItem instanceof Long ) {
                        parent = findMenuDataItem( (Long) sourceItem );
                    } else if (sourceItem instanceof String ){
                        parent = findMenuDataItem( destination.getAccount().getId(), sourceItem.toString() );
                    }
                    if (parent!=null) {
                        destination.setParent01( parent );
                    } else {
                        StringBuffer sb = new StringBuffer();
                        sb.append("You are trying to populate a placeholder (");
                        sb.append(destination.getPath());
                        sb.append(") that must have an 'owner' (");
                        sb.append(owner.getNode());
                        sb.append(") but the owner is not aupplied in the passed-in map: ");
                        sb.append(sourceMap);
                        throw new RuntimeException( sb.toString());
                    }
                }
            }
            // Run through each column and pick up what we can
            for ( MSColumn column : msAccount.getColumns()) {
                // Ignore columns (fields) with no from (nothing to pull from).
                if (column.getFrom()==null) continue;
                if (column.isComputed()) continue;
                    // If no internal field is specified in a list, then we'll grab the data later, when we query the data.
                // But placeholders are populated anyway.
                if (msAccount.getRole().equals(MenuStructure.LIST) && column.isExtended() ) continue;
//          logger.debug( "[populateMenuData] column=" + column.getHeading() + ", from=" + column.getFrom());
                String froms[] = column.getFrom().split("\\|");
                Object result = null;
                for (String from : froms) {
                    result = evaluator.evaluate(from);
//              logger.debug( "[populateMenuData] value=" + result);
                    if (result!=null) break;
                }
                if(result == null) {
                	continue;
                }
                if (result != null && result instanceof Enum) {
                    // Special handling for Enums
                    result = ((Enum<?>)result).toString();
                }
                // Put the value into the menuData row
                // If no internal name, then put it in extended
                if (column.isExtended()) {
                    destination.setExtendedField(column.getHeading(), result);
                } else if (result==null || (result instanceof String && ((String)result).length()==0)) {
                    if (column.isReference()) {
                        destination.setField( column.getDisplayFunction(), null);
                    } else {
                        destination.setField( column.getInternal(), null);
                    }
                // special handling for references to other placeholders
                } else if (column.getInternal().startsWith("parent")) {
                    MenuData parent = null;
                    if (result instanceof MenuData) {
                        parent = (MenuData) result;
                    } else if (result instanceof Long ) {
                        parent = findMenuDataItem( (Long) result );
                    } else {
                        parent = findMenuDataItem( destination.getAccount().getId(), result.toString() );
                    }
                    if (parent==null) {
                        throw new RuntimeException( "Missing placeholder for id:" + result);
                    }
                    // If the resulting parent is not a placeholder, then reference through to it.
                    if (!MenuStructure.PLACEHOLDER.equals(parent.getMenuStructure().getRole())) {
                        parent = parent.getReference();
                        if (parent==null) {
                            throw new RuntimeException( "Missing referenced placeholder for id:" + result);
                        }
                    }
                    destination.setField( column.getInternal(), parent);
                } else {
                    if (column.isReference()) {
                        destination.setField( column.getDisplayFunction(), result);
                    } else {
                        destination.setField( column.getInternal(), result);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException( "Error populating " + destination, e );
        }
    }
    
    public List<String> getLocaleProperties(AccountType accountType) {
        Account account = accountBean.findAccountTemplate(accountType.getKnownType());
        List<String> properties = new ArrayList<String>();
        for (AccountMenuStructure accountMenuStructure : findFullAccountMenuStructure(account.getId())) {
            properties.addAll(getLocaleProperties(accountMenuStructure));
        }
        return properties;
    }

    private static List<String> getLocaleProperties(AccountMenuStructure accountMenuStructure) {
        List<String> properties = new ArrayList<String>();
        if (!MenuStructure.VISIBLE_NEVER.equals(accountMenuStructure.getVisible())) {
            properties.add(accountMenuStructure.getLocaleTextKey() + "=" + accountMenuStructure.getText());
        }
        if (accountMenuStructure.getReferenced() != null && !MenuStructure.VISIBLE_NEVER.equals(accountMenuStructure.getReferenced().getVisible())) {
            properties.addAll(getLocaleProperties(accountMenuStructure.getReferenced()));
        }
        for (MSColumn column : accountMenuStructure.getSortedColumns()) {
            if (!MenuStructure.VISIBLE_NEVER.equals(column.getVisible())) {
                properties.add(column.getLocaleTextKey() + "=" + column.getHeading());
            }
        }
        return properties;
    }

    /**
	 * Finds the reference id map based on conditions.
	 * 
	 * @author valsaraj
	 * added on 06/08/2010
	 * @param conditions
	 * @return referenceIdMap
	 */
	public Map<String, String> findReferenceIds(String conditions) {
		Query query = em.createQuery("SELECT md FROM MenuData md " +
						"WHERE " + conditions);
		Map<String, String> referenceIdMap = new HashMap<String, String>();		
		List<MenuData> mdList = query.getResultList();
		
		for (MenuData menuData : mdList) {
			referenceIdMap.put(menuData.getString01().toLowerCase(), menuData.getPath());
		}
		
		return referenceIdMap;
	}
    
}
