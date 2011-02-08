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
 * You should have received a copy of the GNU Lesser General Public License along with this 
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA 02111-1307 USA 
 * 
 * Contact: info@tolvenhealth.com
 */

package org.tolven.app;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

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
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
/**
 * API for querying and manipulating menu and index structures.
 * @author John Churin
 *
 */
public interface MenuLocal {

	/**
	 * Given a Tolven accountId and the path to a menu item, return the corresponding menustructure object.
	 * @param accountId Tolven account id
	 * @param path The path is colon-separated, without identifiers. For example, "echr:activity"
	 * @return The MenuStructure object (its parents will also be accessible from this object)
	 */
	
	public MenuStructure findMenuStructure( long accountId, String path );
	/**
	 * Find the specified menuStructure within the specified account. If the path begins with ":", then
	 * it means the known-type is prefixed. For example, ":patient" would find "echr:patient" in an echr account.
	 * @param account
	 * @param pathSuffix
	 * @return The MenuStructure
	 */
	public MenuStructure findMenuStructure( Account account, String pathSuffix);

	public AccountMenuStructure findAccountMenuStructure( long accountId, String path );

	public MenuStructure findMenuStructure( AccountUser accountUser, String path );
	public MenuStructure findMenuStructure( AccountUser accountUser, AccountMenuStructure ams );

	public List<MenuStructure> findMenuChildren( AccountUser accountUser, MenuStructure ms );	

	public List<MenuStructure> findSortedChildren( AccountUser accountUser, MenuStructure ms );	

	/**
	 * Find the contents of a list.
	 * @param account The account containing the list
	 * @param msList MenuStructure defining the list
	 * @param mdParent The parent of the list, if any
	 * @return MenuData contents of the list
	 */
    public List<MenuData> findListContents(Account account, MenuStructure msList, MenuData mdParent);

	/**
	 * Prepare a MenuQueryControl for use on the server.
	 * If we get MenuQueryControl from a remote source, we first need to find 
	 * the MenuStructure based on path and accountId. 
	 * We also consider the query parameter which may direct the query itself to 
	 * another MenuStructure. This results in an "actualMenuStructure" which may not be the same
	 * as the menuStructure item being displayed.
	 */
	public void prepareMQC( MenuQueryControl ctrl);
	
	/**
	 * Store a resource into the database. The resource primary key is Account+name. In other words, resources are specific to an account.
	 * @param resource
	 */
	public void persistResource( MSResource resource);
	
	/**
	 * Find a resource by account and name. The resource primary key is Account+name. In other words, resources are specific to an account.
	 * @param id of the account
	 * @param path Path name of the resource
	 */
	public MSResource findMSResource( long templateAccountId, String path );

	public List<MSResource> findAccountResources( long templateAccountId );

	// Update UMS using DTO
	public void updateUserMenuStructure( AccountUser accountUser, String path, UMSDataTransferObject dto );
//	public void updateUserMenuStructure( AccountUser accountUser, String path, String visibility, Integer sequence, Integer columnNumber, String windowstyle );	

	public void setToDefaultMenuStructure( AccountUser accountUser, AccountMenuStructure parent );

	/**
	 * Given a Tolven accountId, a menustructure and a partial path to a menu item within that menu structure, 
	 * return the corresponsing menustructure object.
	 * @param userId tolven user id
	 * @param path The path is colon-separated, without identifiers. For example, if the path of the supplied menu structure is "echr:patient" then this paramater
	 * might contain "results:lab"
	 * @return The MenuStructure object (its parents will also be accessible from this object)
	 */
	public AccountMenuStructure findDescendentMenuStructure( long accountId, AccountMenuStructure parent, String path);
	
	/**
	 * Return the nearest ancestor that is a placeholder, or null if no placeholder above this ms.
	 * @param ms
	 * @return MenuStructure of owner or null
	 */
	public MenuStructure findOwner(MenuStructure ms);

	/**
	 * <p>Given a specific menuStructure and corresponding keys, return the data for that structure.
	 * Some MenuData results are specified by metadata to be single results. However, this method
	 * does nothing different except that the caller can expect a List result with one value in it 
	 * in that case.
	 * This query could be fairly complex if filters and such are used so we may use some helper methods to 
	 * modularize the query.</p>
	 * <ul>
	 * <li>Find only MenuData matching the specified MenuStructure instance. This significantly and 
	 * sometimes completely narrows the result set. The MenuStructure is a surrogate for the account 
	 * and/or user and the specific path in the menu hierarchy. this criteria also implicitly enforces 
	 * security by preventing access to any account data other than that of the user's account.</li>
	 * <li>The metadata tells us how to sort by default but the user may modify the sort requirements.</li>
	 * <li>Menu data can have an expiration timestamp. The <i>now</i> parameter in query control tells us the 
	 * definitive timestamp against which the query can compare. Note: Expired menuData rows can and will be 
	 * removed by a background task. Don't depend on expired menuData being present after it has expired.</li>
	 * <li>QueryControl tells us how many rows to return and which row to start with. This allows paging.</li>
	 * <li>Sort criteria may be specified.</li>
	 * <li></li>
	 * </ul>
	 * @param A query control structure carrying the parameters needed for this query including the Metadata 
	 * corresponding to the data we want to query, an array of long values corresponding to the path of this 
	 * menuStructure NodeKeys corresponding to static menuStructure nodes are zero (but they are not skipped). 
	 * Thus, the number of items in this array exactly matches the path specified in the 
	 * MenuStructure (which is different from how MenuData stores keyValues.
	 * @return A list of MenuData values matching the criteria
	 */
	public List<MenuData> findMenuData( MenuQueryControl ctrl );

	/**
	 * Given an external instance identifier (root+extension) and an account, find the associated
	 * MenuData.
	 * @param account
	 * @param root
	 * @param extension
	 * @return zero or more menuData items matching by account, root and extension
	 */
	public List<MenuData> findMenuDataById( Account account, String root, String extension );
	
	/**
     * Lookup an ID that was not assigned by us
     * @param account
     * @param root
     * @param extension
     * @return placeholderID or null if other than exactly one result was found
     */
    public PlaceholderID findPlaceholderID( Account account, String root, String extension );
    
	/**
	 * Temporary: This is slow and very specific. But give us an insight into functions needed for real.
	 * @param accountId
	 * @param path
	 * @return
	 */
	public List<MenuData> findMenuDataByString04( long accountId, String path );
	
	/**
	 * A slower but more complete version of findMenuDataByColumns
	 * @param ctrl
	 * @return An array of columns either corresponding to the columns specified in the MenuQueryControl parameters
	 * or the full list of columns specified in the application.xml for this list.
	 */
	public List<Object[]> findMenuDataRows( MenuQueryControl ctrl );
	
	/*
	 * findMenuData returning columns defined in application.
	 */
	public MDQueryResults findMenuDataByColumns( MenuQueryControl ctrl );
	
    /**
     * Return a count of the number of menuData items specified in the criteria. Offset and limit are ignored.
     */
    public long countMenuData(MenuQueryControl ctrl  );
    
   /**
     * Return the date range of menuData items specified in the criteria. Offset and limit are ignored.
     */
    public List<Object> findMenuDataDateRange(MenuQueryControl ctrl  ) ;

	/**
	 * Update the version number of any lists we maintain. Supports efficient autoRefresh from the browser.
	 * "Portlet", timeline band, and calendar entry are just kinds of lists so we keep a version on those as well. 
	 * Since this call is very likely to cause contention in the database resulting in user delays, or failed transactions, 
	 * the actual update occurs behind a queue. 
	 * The messages are implicitly idempotent: The queued actions achieve the same result regardless of the order they are delivered. 
	 * @param mdvm
	 */
	public void updateMenuDataVersion( MenuDataVersionMessage mdvm );
	
    /** Updates min and max dates for the data items in this menu
	 * @param mdv
	 * @param role
	 */
	public void updateMenuDataVersion( MenuDataVersion mdv,String role);
	/**
	 * Direct access to a menuData item by id. An exception is thrown if not found.
	 */
	public MenuData findMenuDataItem( long id );
	/**
	 * <p>Find a menu data item - this is an exact-match although we just return null if not found (EJB spec says to thrown an error for a singleResult query). 
	 * For security purposes, we include
	 * accountId in case someone tries to phish for a menudata item they don't own.</p>
	 */
	public MenuData findMenuDataItem( MenuQueryControl ctrl );

	/**
	 * Special case of findMenuDataItem where we also check the AccountTemplate if the requested account
	 * does not have the item. Good for menuData that is static relative to an account such as TRIM.
	 */
	public MenuData findDefaultedMenuDataItem( Account account, String path);
	
	/**
	 * <p>Find a menu data item - this is an exact-match although we just return null if not found (EJB spec says to thrown an error for a singleResult query). 
	 * For security purposes, we include
	 * accountId in case someone tries to phish for a menudata item they don't own.</p>
	 * @param accountId The account id to search
	 * @param path The unique path of the menuData item
	 */
	public MenuData findMenuDataItem( long accountId, String path );

	/**
	 * Return a list of list items referencing this menuData item from a specific list.
	 * @param md the placeholder possibly occuring on a list
	 * @param msList The MenuStructure defining the list
	 * @return List of MD items on the list referencing the placeholder. Usually only one item returned but since it's not an enforced limitiation, the caller should expect zero or more.
	 */
	public List<MenuData> findReferencingMDs( MenuData md, MenuStructure msList );
	
    /**
     * Return a list of menudata that references the specified placeholder
     * @param mdPlaceholder
     * @return The list of menu data referencing the placeholder
     */
    public List<MenuData> findReferencingMenuData(MenuData mdPlaceholder);
    
    /**
     * Remove the listed menudata 
     * @param List<MenuData> list of menuData items to mark deleted 
     */
	public void removeMenuData( List<MenuData> menuData );

	
	/**
	 * Simple lookup of a menuDataVersion entry
	 * @param accountId
	 * @param element
	 * @return
	 */
	public MenuDataVersion findMenuDataVersion( long accountId, String element);
	/**
	 * Lookup a list of menuDataVersions, and return them
	 * @param account
	 * @param a set of elements
	 * @return
	 */
	public List<MDVersionDTO> findMenuDataVersions( Account account, List<String> elements);

	/**
	 * Create an initial, debugging menu structure for the user
	 * @param account
	 * @return The root node of the MenuStructure
	 */
	public MenuStructure createDefaultMenuStructure( Account account );
	/**
	 * Return the full account menu structure for a user.
	 */
	public List<AccountMenuStructure> findFullAccountMenuStructure( long accountId);
	/**
	 * Return the full menu structure for a user.
	 */
	public List<MenuStructure> findFullMenuStructure( long accountId);
	
	/**
	 * Given a list of menus, update them in the database
	 * @param menus
	 */
	public void updateMenus( List<MenuStructure> menus);
	
	/*
	 *Given a list of columns, update them in the database
	 * @param columns
	 */
	public void updateColumns( List<MSColumn> columns);

	/**
	 * Simple method to persist a single menu data object. In general, this method should result in a new row being
	 * added to the database. However, if the controlling menustructure is non-null, then it may actually
	 * cause an update of an existing MenuData item. 
	 * @param md
	 * @return true if the item was actually persisted. Returns false if it was a duplicate
	 * and duplicate checking was enabled for this item (uniqueKey)
	 */
	public boolean persistMenuData( MenuData md );
	
	/**
	 * Delete a menuData item. Normally, this means setting the deleted flag)
	 * @param md
	 */
	public void deleteMenuData(MenuData md);
	/**
	 * Update a MenuData entity
	 * @param md
	 */
	public void updateMenuData(MenuData md);

	public boolean persistMenuDataDeferred( MenuData md, Map<String, MenuDataVersionMessage> mdvs );
	public void queueDeferredMDVs(Map<String, MenuDataVersionMessage> mdvs );

	/**
	 * Prior to evaluating an expression, this method is called to ensure that if a placeholder is mentioned that we
	 * find the appropriate placeholder values, document contents (trim, ccr, etc)
	 * @param ee The expression evaluator
	 */
	public void prepareEvaluator( TrimExpressionEvaluator ee );

	/**
	 * Prepare the where part of a query
	 */
	public Query prepareCriteria( MenuQueryControl ctrl, String init );
	
	/**
     * Remove any menuData item that references the specified document. This is usually done when a new document is submitted to
     * remove it from "to do" lists. The rules will put the document back on any lists it should be on now that the
     * document is "actionable".
	 * @param accountId
	 * @param documentId
	 * @param removePlaceholders If true, placeholders are also removed
     * @return the number of rows actually deleted.
     */
    public int removeReferencingMenuData( long accountId, long documentId, boolean removePlaceholders);

    /**
     * Remove MenuData (lists, etc) referencing this placeholder. This method does not remove the placeholder itself or
     * any events, past or current.
     * @param mdPlaceholder
     * @return The number of items removed (marked as deleted)
     */
	public int removeReferencingMenuData( MenuData mdPlaceholder );

	/**
	 * See if there's a menu structure available for the specified account. 
	 * @param user
	 * @return true if at least one item found for this account, otherwise false
	 */
	public AccountMenuStructure getRootMenuStructure( Account account );
	
	/**
	 * Update the target account's menuStructure with that of the template account for this account.
	 * The accountTypes must match.
	 * @param targetAccountId
	 */
	public void updateMenuStructure( Account targetAccount );
	

	/**
	 * Using the object graphs supplied, we find the columns requested to be populated and populate them.
	 * But only those with an internal attribute. If that is missing, then the field is acquired later, upon demand.
	 */
	public void populateMenuData( Map<String, Object> sourceMap, MenuData destination );

	/**
	 * Persist a MenuStructure to the database (recursively if we have to). Including columns.
	 * @param ms
	 */
	public void persistMenuStructure( MenuStructure ms );

	/**
	 * Given a path to a global menu, such as TrimList, return
	 * the menuLocator that tells us where the list actually is.
	 * A RuntimeException is thrown if the item is not found.
	 * @param path
	 * @return MenuLocator for the given path
	 */
	public MenuLocator findMenuLocator( String path);

	/**
	 * Given a path to a global menu, such as TrimList, find, or 
	 * create the MenuLocator. In either case, return the menuLocator.
	 * @param path
	 * @param columns A list of column definitions 
	 * @return MenuLocator for the given path
	 */
	public MenuLocator createOrUpdateMenuLocator( String path, List<MSColumn> columns);
}
