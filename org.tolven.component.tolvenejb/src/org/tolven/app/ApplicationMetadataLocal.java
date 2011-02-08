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
import org.tolven.core.entity.Account;
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
}
