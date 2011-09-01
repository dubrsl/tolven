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
 * @version $Id: ApplicationMetadataLocal.java,v 1.2.2.1 2010/11/26 20:27:42 joseph_isaac Exp $
 */  

package org.tolven.app;

import java.util.Map;

import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.entity.Account;
import org.tolven.menuStructure.Action;
import org.tolven.menuStructure.Application;
import org.tolven.menuStructure.Band;
import org.tolven.menuStructure.Calendar;
import org.tolven.menuStructure.Column;
import org.tolven.menuStructure.Instance;
import org.tolven.menuStructure.Menu;
import org.tolven.menuStructure.MenuBase;
import org.tolven.menuStructure.Placeholder;
import org.tolven.menuStructure.PlaceholderField;
import org.tolven.menuStructure.Portal;
import org.tolven.menuStructure.Portlet;
import org.tolven.menuStructure.Timeline;
import org.tolven.menuStructure.TrimList;
/**
 * Application metadata is loaded to database. 
 * @author John Churin
 *
 */
public interface ApplicationMetadataLocal {
	/**
	 * Load all supplied application metadata. The files have been read to memory on the client and passed here as
	 * a list of zero or more mapped string with the key being the name of the file.
	 * @param appFiles
	 */
	public void loadApplications( Map<String, String> appFiles );

    /**
     * Create a placeholder.
     * Placeholders represent the "entities" in the 
     * application such as patient, medications, etc.
     * Within an account(type), placeholder names must be unique, regardless of the hierarchy.
     * @param name
     * @param parent
     * @param account
     * @return
     */
    public AccountMenuStructure createPlaceholder(String name, String parent, Account account);
    
    public void processExtends( java.util.List<Application> extendApplications);
    public void uploadProperties( Application app, Map<String, String> appFiles );
    public AccountMenuStructure processBand( Account account, Band band, AccountMenuStructure msParent, MenuStructure msPlaceholder );
    public AccountMenuStructure  processMenu( Account account, Menu menu, AccountMenuStructure msParent, AccountMenuStructure msPlaceholder);
    public AccountMenuStructure processCalendar( Account account, Calendar calendar, AccountMenuStructure msParent, AccountMenuStructure msPlaceholder );
    public AccountMenuStructure processInstance( Account account, Instance instance, AccountMenuStructure msParent);
    public AccountMenuStructure processPlaceholder( AccountMenuStructure msMenuRoot, Placeholder placeholder, MenuStructure msParent);
    public AccountMenuStructure processPortal( Account account, Portal portal, AccountMenuStructure msParent,AccountMenuStructure msPlaceholder);
    public AccountMenuStructure processPortlet( Account account, Portlet portlet, AccountMenuStructure msParent, AccountMenuStructure msPlaceholder);
    public AccountMenuStructure processTimeline( Account account, Timeline timeline, AccountMenuStructure msParent,AccountMenuStructure msPlaceholder);
    public AccountMenuStructure processTrimList( Account account, TrimList list, AccountMenuStructure msParent );
    public MSColumn processPlaceholderField( AccountMenuStructure ms, PlaceholderField field ) ;
    public MSColumn processColumn( Column col, MenuStructure ms);
    public AccountMenuStructure resolveMenuStructure( Account account, MenuBase menu, String role, AccountMenuStructure msParent );
    public void processColumns( AccountMenuStructure ms, java.util.List<Column> columns);
    public void processActions( Account account, AccountMenuStructure msParent, java.util.List<Action> actions );
    public void nominateDefaultSuffix( AccountMenuStructure ms );
    public String fullPath( MenuStructure ms );
    public void processTrimMenus( java.util.List<Placeholder> trimMenus );
    public java.util.List<AccountMenuStructure> getMatchingMenuStructures( String path);
}
