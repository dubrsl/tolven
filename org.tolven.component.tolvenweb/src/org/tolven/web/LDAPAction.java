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
package org.tolven.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.tolven.security.TolvenPerson;

/**
 * OBSOLETE MODULE SEE org.tolven.security.bean.LDAPBean.java.
 * @author John Churin
 */
public class LDAPAction {
    // Connection info
    private String url;
    private String baseDN;
    // User needed for updates
    // Criteria
    private String criteria;
    private String field;
    private int maxResults;
    private int timeLimit; // In milliseconds
    private String ctxF = "com.sun.jndi.ldap.LdapCtxFactory";
    private LdapContext ctx = null;
    private int numberToCreate;
    private List<TolvenPerson> searchResults;
    private String elapsedTime = null;
    private List<SelectItem> baseDNs = null;
    
   
    /** Creates a new instance of LDAPAction */
    public LDAPAction() {
    }


    /**
     * Connect to LDAP server
     * @exception NamingException
     */
    void connectLDAP( ) throws NamingException {
        if (ctx!=null) return;
        InitialContext iniCtx = new InitialContext();
        ctx = (LdapContext)iniCtx.lookup("tolven/ldap");
    }

    /**
     * Close the connection (if open)
     * TODO: LDAP is a resource that could be pooled - look into it.
     */
     void closeLDAP( ) throws NamingException {
         if (ctx!=null) {
             ctx.close();
             ctx = null;
         }
     }

     /**
     * Search for matching names. If not connected yet, we'll connect to LDAP now.
     */
    public String search( ) throws NamingException {
        long startTime = System.currentTimeMillis();
        connectLDAP();
        NamingEnumeration<SearchResult> namingEnum = null;
        SearchControls ctls = new SearchControls();
//        ctls.setReturningAttributes(_dnOnly );
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setCountLimit(maxResults);
        ctls.setTimeLimit(getTimeLimit());
        namingEnum = ctx.search(baseDN, field+criteria, ctls);
        searchResults = new ArrayList<TolvenPerson>( 10 );
        while (namingEnum.hasMore()) {
            SearchResult rslt = namingEnum.next();
            searchResults.add( new TolvenPerson( rslt ) );
        }
//        TolvenLogger.info( searchResults, LDAPAction.class );
        closeLDAP();
        double elapsed = (System.currentTimeMillis() - startTime);
        elapsedTime = String.format("Elapsed: %.3f sec ", elapsed/1000);
        return "success";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    public String getCtxF() {
        return ctxF;
    }

    public DirContext getCtx() {
        return ctx;
    }

    public void setCtx(LdapContext ctx) {
        this.ctx = ctx;
    }

    public List<TolvenPerson> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<TolvenPerson> searchResults) {
        this.searchResults = searchResults;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getNumberToCreate() {
        return numberToCreate;
    }

    public void setNumberToCreate(int numberToCreate) {
        this.numberToCreate = numberToCreate;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Collection<SelectItem> getBaseDNs() {
        return baseDNs;
    }

}
