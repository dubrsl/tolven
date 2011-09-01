package org.tolven.app;

import java.util.List;

import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MDQueryResults;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MSResource;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuDataVersion;
import org.tolven.app.entity.MenuLocator;
import org.tolven.app.entity.MenuQueryControl;
import org.tolven.app.entity.MenuStructure;
import org.tolven.app.entity.PlaceholderID;
import org.tolven.app.entity.UMSDataTransferObject;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountType;
import org.tolven.core.entity.AccountUser;

/**
 * API for querying and manipulating menu and index structures.
 * @author John Churin
 *
 */
public interface MenuRemote {
	/**
	 * Given a Tolven accountId and the path to a menu item, return the corresponding menustructure object.
	 * @param userId tolven user id
	 * @param path The path is colon-separated, without identifiers. For example, "echr:activity"
	 * @return The MenuStructure object (its parents will also be accessible from this object)
	 */
	public MenuStructure findMenuStructure( long accountId, String path );
	public AccountMenuStructure findAccountMenuStructure( long accountId, String path );

	public MenuStructure findMenuStructure( AccountUser accountUser, String path );
	public MenuStructure findMenuStructure( AccountUser accountUser, AccountMenuStructure ams );

	public List<MenuStructure> findMenuChildren( AccountUser accountUser, MenuStructure ms );	

	public List<MenuStructure> findSortedChildren( AccountUser accountUser, MenuStructure ms );	
	
	//Update UMS using DTO
	public void updateUserMenuStructure( AccountUser accountUser, String path, UMSDataTransferObject dto );
//	public void updateUserMenuStructure( AccountUser accountUser, String path, String visibility, Integer sequence, Integer columnNumber, String windowstyle );	
	public void setToDefaultMenuStructure( AccountUser accountUser, AccountMenuStructure parent );

	/**
     * Lookup an ID that was not assigned by us
     * @param account
     * @param root
     * @param extension
     * @return placeholderID or null if other than exactly one result was found
     */
    public PlaceholderID findPlaceholderID( Account account, String root, String extension );
    
	/**
	 * Given a Tolven accountId, a menustructure and a partial path to a menu item within that menu structure, 
	 * return the corresponding menustructure object.
	 * @param userId tolven user id
	 * @param path The path is colon-separated, without identifiers. For example, if the path of the supplied menu structure is "echr:patient" then this paramater
	 * might contain "results:lab"
	 * @return The MenuStructure object (its parents will also be accessible from this object)
	 */
	public AccountMenuStructure findDescendentMenuStructure( long accountId, AccountMenuStructure parent, String path);

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
    
    /** Updates min and max dates for the data items in this menu
	 * @param mdv
	 * @param role
	 */
	public void updateMenuDataVersion( MenuDataVersion mdv,String role);
    
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

	/*
	 * Method to get MSColumn(s) by account id for remote users only.
	 */
	public List<MSColumn> findAllMSColumnsForAccount( long accountId);

	/**
	 * Remote-friendly version of updateMenuStructure( Account )
	 * @param accountId
	 */
	public void updateMenuStructure( long accountId );
	
	/**
	 * Update the target account's menuStructure with that of the template account for this account.
	 * The accountTypes must match.
	 * @param targetAccountId
	 */
	public void updateMenuStructure( Account targetAccount );
	
	/**
	 * Given a list of menus, update them in the database
	 * @param menus
	 */
	public void updateMenus( List<MenuStructure> menus);
	
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


	/**
	 * Persist a MenuStructure to the database (recursively if we have to). Including columns.
	 * @param ms
	 */
	public void persistMenuStructure( MenuStructure ms );
	
        //CCHIR merge
        public void persistResource( MSResource resource);
	/**
	 * Store a resource into the database. The resource primary key is Account+name
	 * @param resource
	 */
	public MSResource findMSResource( long templateAccountId, String path );
	
	/**
	 * See if there's a menu structure available for the specified account. 
	 * @param user
	 * @return true if at least one item found for this account, otherwise false
	 */
	public AccountMenuStructure getRootMenuStructure( Account account );

    public List<String> getLocaleProperties(AccountType accountType);

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
