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

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.tolven.app.bean.MenuPath;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;

/**
 * This bean provides functionality to extract data based on menupaths.
 * 
 * @author Joseph Isaac
 *
 */
public interface DataExtractLocal {

    public void streamResultsXML(Writer out, DataQueryResults dq) throws IOException;

    public void streamResultsCSV(Writer out, DataQueryResults dq) throws IOException;

    public void streamResultsSQL(Writer out, DataQueryResults dq, String table, String dialect) throws IOException;

    /**
     * Setup for a query. Can be used to get a list of available column headings for a particular menu path
     * @param menupath
     * @param accountUser
     * @return Query structure
     */
    public DataQueryResults setupQuery(String menupath, AccountUser accountUser);

    /**
     * This method can only be used with Accounts which are template Accounts
     * 
     * @param menupath
     * @param account
     * @return
     */
    public DataQueryResults setupQuery(String menupath, Account account);
    
    public List<String> getSQLDialects();
    
	public AccountMenuStructure findAccountMenuStructure( Account account, MenuPath mp);

}
