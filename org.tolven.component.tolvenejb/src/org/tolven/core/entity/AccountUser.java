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
package org.tolven.core.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tolven.security.key.AccountPrivateKey;

/**
 * <p>Specifies which users can participate in a given account. An AccountAdministrator can add and remove Users from an Account. This ability may be limited 
 * by a Sponsor. Users and accounts can be associated more than once, but only one at a time.</p>
 * 
 * @author John Churin
 */
@Entity
@Table
public class AccountUser implements Serializable, Cloneable {
    
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Account account;

    @ManyToOne
    private TolvenUser user;
    
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

    private boolean defaultAccount;
    
    @OneToMany(mappedBy = "accountUser", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AccountUserRole> roles = null;

    //    /**
//     * References the document authorizing the current state of this setting.
//     */
//    @ManyToOne
//    private DocBase authority;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date effectiveDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date expirationDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date lastLoginTime;

    @Column
    private boolean accountPermission;
    
    @Column
    private String status;

    @Column
    private String openMeFirst;

    @Embedded
    private AccountPrivateKey accountPrivateKey;

    @OneToMany(mappedBy = "accountUser", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AccountUserProperty> accountUserProperties = null;
    
    /**
     * Construct an empty AccountUser. 
     */
    public AccountUser() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AccountUser clone = (AccountUser) super.clone();
        return clone;
    }

    /**
     * Get the appropriate logo for this account user (by default, derived from the AccountType).
     * @return
     */
    public String getLogo() {
        return getAccount().getAccountType().getLogo();
    }
    /**
     * Get the appropriate CSS for this account user (by default, derived from the AccountType).
     * @return
     */
    public String getCSS() {
        return getAccount().getAccountType().getCSS();
    }

    /**
     * Get the timezone. Timezone is not stored in this entity. User and account may have timezones and
     * if they don't, we look for a system default.
     * @return
     */
    public String getTimeZone( ) {
        String timeZone;
        timeZone = getUser().getTimeZone();
        if (timeZone!=null) return timeZone;
        timeZone = getAccount().getTimeZone();
        if (timeZone!=null) return timeZone;
        timeZone = System.getProperty("tolven.timezone");
        if (timeZone!=null) return timeZone;
        timeZone = java.util.TimeZone.getDefault().getID();
        return timeZone;
    }
    
    public TimeZone getTimeZoneObject() {
        return TimeZone.getTimeZone(getTimeZone());
    }

    /**
     * Get the default locale for this user, The value contained here is based on the following precedence:
     * <ol>
     * <li>The user's locale if not null</li>
     * <li>The account's locale if not null</li>
     * <li>From Java Locale.getDefault()</li>
     * </ol>
     * @return
     * @throws IOException
     */
    public String getLocale() {
        String locale = getUser().getLocale();
        if (locale==null) {
            getAccount().getLocale();
        }
        if(locale == null) {
            locale = Locale.getDefault().toString();
        }
        return locale;
    }
    
    /**
     * Return a Locale Object based on the locale returned by getLocale
     * @return
     */
    public Locale getLocaleObject() {
        Locale locale = new Locale(getLocale());
        return locale;
    }
    
    /**
     * The account to which this user is associated. By this means, a TolvenUser can be associated with any number of Accounts and will normally be 
     * associated with only one at a time while logged in. A TolvenUser with more than one account will be provided a selection of which account they
     * want to be connected to as they login. 
     * 
     * @see #isDefaultAccount().
     */
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account val) {
        this.account = val;
    }

    /**
     * The TolvenUser to be granted access to the specified Account. Although allowed, the Author of the assignement is usually not the same TolvenUser as 
     * the TolvenUser being granted access.
     */
    public TolvenUser getUser() {
        return user;
    }

    public void setUser(TolvenUser val) {
        this.user = val;
    }
    /**
     * The meaningless, unique ID for this association. Leave Id null for a new record. The EntityManager will assign a unique Id when it is persisted. There may be 
     * more than one AccountUser association for a single combination of Account and TolvenUser. Typically only one is valid at any given point in time.
     */
    public long getId() {
        return id;
    }

    public void setId(long val) {
        this.id = val;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date val) {
        this.effectiveDate = val;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date val) {
        this.expirationDate = val;
    }

    public boolean isAccountPermission() {
        return accountPermission;
    }
 
    public void setAccountPermission(boolean accountPermission) {
        this.accountPermission = accountPermission;
    }
    
    public void update(){
    setAccountPermission(isAccountPermission());
    }
    
        public boolean equals(Object obj) {
        if (!(obj instanceof AccountUser)) return false;
        if (this.getId()==((AccountUser)obj).getId()) return true;
        return false;
    }

    public String toString() {
        return "AccountUser: " + getId();
    }

    public int hashCode() {
        if (getId()==0) throw new IllegalStateException( "id not yet established in Account object");
        return Long.valueOf( getId()).hashCode();
    }
//    /**
//     * This indicates the document, and thus the user, authorizing the assignment of this user to this account. This 
//     * information is visible to all AccountAdministrators and cannot be erased or hidden from their view.
//     */
//    public DocBase getAuthority() {
//        return authority;
//    }
//
//    public void setAuthority(DocBase authority) {
//        this.authority = authority;
//    }

    /**
     * The user can provide a default account in order to shorten the sign-on process when they select the "default-login" 
     * button instead of the normal Login button. If the user only has one account, both the default-login and regular login buttons
     * take the user to the Account.
     */
    public boolean isDefaultAccount() {
        return defaultAccount;
    }

    public void setDefaultAccount(boolean defaultAccount) {
        this.defaultAccount = defaultAccount;
    }

    /**
     * The status of this account-user relationship.
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AccountPrivateKey getAccountPrivateKey() {
        return accountPrivateKey;
    }
    
    public void setAccountPrivateKey(AccountPrivateKey privateKey) {
        this.accountPrivateKey = privateKey;
    }
    
    public boolean hasAccountPrivateKey() {
        return accountPrivateKey != null;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Set<AccountUserRole> getRoles() {
        if (roles==null) roles = new HashSet<AccountUserRole>( 5 );
        return roles;
    }

    public void setRoles(Set<AccountUserRole> roles) {
        this.roles = roles;
    }

    public List<AccountUserRole> getRoleList( ) {
        List<AccountUserRole> list = new ArrayList<AccountUserRole>();
        list.addAll(getRoles());
        return list;
    }
    
    public String getRoleListString( ) {
        StringBuffer sb = new StringBuffer();
        for (AccountUserRole role : getRoleList()) {
            if (sb.length()!=0) sb.append(",");
            sb.append(role.getRole());
        }
        return sb.toString();
    }

    public Map<String,String> getProperty() {
        return new AccountUserPropertyMap( this );
    }
    
    public Map<String,String> getBrandedProperty(String brand) {
        return new AccountUserPropertyMap( this, brand );
    }
    
    /**
     * A Map that wraps the properties available to an accountUser. 
     * Brand (when available), and Locale are also considered in the search:
     * <code>property-name[.brand][_locale]</code>
     * A total of twelve complete searches are possible - the first one to return a value ends the search. 
     * <ol>
     * <li>property-name.brand_locale</li>
     * <li>property-name.brand</li>
     * <li>property-name_locale</li>
     * <li>property-name</li>
     * </ol>
     * If brand is not provided, then half of the searches are not attempted.
     * Each search in the above list proceeds as follows:
     * <ol>
     * <li>Account - A property defined in the user's account</li>
     * <li>AccountType - A property defined in the template account for the accountType.</li>
     * <li>System property - These properties are defined in the TolvenProperties table in the database</li>
     * </ol>
     * All possible search combinations are enumerated:
     * <ol>
     * <li>Look for a property named <i>property-name.brand_locale</i> in the account</li>
     * <li>Look for a property named <i>property-name.brand_locale</i> in the accountType</li>
     * <li>Look for a property named <i>property-name.brand_locale</i> in the system properties</li>
     * <li>Look for a property named <i>property-name.brand</i> in the account</li>
     * <li>Look for a property named <i>property-name.brand</i> in the accountType</li>
     * <li>Look for a property named <i>property-name.brand</i> in the system properties</li>
     * <li>Look for a property named <i>property-name_locale</i> in the account</li>
     * <li>Look for a property named <i>property-name_locale</i> in the accountType</li>
     * <li>Look for a property named <i>property-name_locale</i> in the system properties</li>
     * <li>Look for a property named <i>property-name</i> in the account</li>
     * <li>Look for a property named <i>property-name</i> in the accountType</li>
     * <li>Look for a property named <i>property-name</i> in the system properties</li>
     * </ol>
     * @author John Churin
     * 
     */
    static class AccountUserPropertyMap implements Map<String, String>{
        AccountUser accountUser;
        String brand;
        String locale;
        public AccountUserPropertyMap( AccountUser accountUser ) {
            this.accountUser = accountUser; 
            this.brand = null;
            this.locale = accountUser.getLocale();
        }
        public AccountUserPropertyMap( AccountUser accountUser, String brand ) {
            this.accountUser = accountUser; 
            this.brand = brand;
            this.locale = accountUser.getLocale();
        }
        
        protected String getAccountProperty( Account account, Object key ) {
            if (account.getAccountProperties()!=null) {
                for (AccountProperty property : account.getAccountProperties()) {
                    if (key.equals(property.getPropertyName())) {
                        return property.getPropertyValue();
                    }
                }
            }
            return null;
        }
        
        protected String getAccountUserProperty( Object key ) {
            for (AccountUserProperty property : accountUser.getAccountUserProperties()) {
                if (key.equals(property.getPropertyName())) {
                    return property.getPropertyValue();
                }
            }
            return null;
        }
        
        protected String getProperty( String name ) {
            String result;
            // Try account user first
            result = getAccountUserProperty( name );
            if (result!=null) return result;
            // Look in account
            result = getAccountProperty( accountUser.getAccount(), name );
            if (result!=null) return result;
            // See about accountTemplate, if there is one
            Account accountTemplate = accountUser.getAccount().getAccountTemplate();
            if (accountTemplate!=null) {
                result = getAccountProperty( accountTemplate, name );
                if (result!=null) return result;
            }
            result = System.getProperty(name);
            if (result!=null) return result;
            return null;
        }
        
        @Override
        public String get(Object key) {
            String result;
            if (brand!=null && locale!=null) {
                result = getProperty( key+"."+brand+"_"+locale );
                if (result!=null) return result;
            }
            if (brand!=null ) {
                result = getProperty( key+"."+brand );
                if (result!=null) return result;
            }
            if (locale!=null) {
                result = getProperty( key+"_"+locale );
                if (result!=null) return result;
            }
            result = getProperty( (String)key );
            return result;
        }

        @Override
        public boolean containsKey(Object key) {
            throw new UnsupportedOperationException( "Property operation not supported in AccountUser" );
        }
        
        /**
         * Put or update a property in this account's list of properties.
         */
        @Override
        public int size() {
            return accountUser.getAccountUserProperties().size();
        }
        /**
         * Put or update a property in this accountUser's list of properties.
         */
        @Override
        public String put(String key, String value) {
            for (AccountUserProperty property : accountUser.getAccountUserProperties()) {
                if (key.equals(property.getPropertyName())) {
                    String oldValue = property.getPropertyValue();
                    property.setPropertyValue(value);
                    return oldValue;
                }
            }
            // Create a new property
            AccountUserProperty property = new AccountUserProperty();
            property.setAccountUser(accountUser);
            property.setPropertyName(key);
            property.setPropertyValue(value);
            accountUser.getAccountUserProperties().add(property);
            return null;
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException( "Property operation not supported in AccountUser" );
        }
        
        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException( "Property operation not supported in AccountUser" );
        }

        @Override
        public Set<java.util.Map.Entry<String, String>> entrySet() {
            throw new UnsupportedOperationException( "Property operation not supported in AccountUser" );
        }


        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException( "Property operation not supported in AccountUser" );
        }

        @Override
        public Set<String> keySet() {
            throw new UnsupportedOperationException( "Property operation not supported in AccountUser" );
        }


        @Override
        public void putAll(Map<? extends String, ? extends String> m) {
            throw new UnsupportedOperationException( "Property operation not supported in AccountUser" );
        }

        @Override
        public String remove(Object key) {
            throw new UnsupportedOperationException( "Property operation not supported in AccountUser" );
        }

        @Override
        public Collection<String> values() {
            throw new UnsupportedOperationException( "Property operation not supported in AccountUser" );
        }

    }

    public Set<AccountUserProperty> getAccountUserProperties() {
        if (accountUserProperties==null) {
            accountUserProperties = new HashSet<AccountUserProperty>();
        }
        return accountUserProperties;
    }

    public void setAccountUserProperties(Set<AccountUserProperty> accountUserProperties) {
        this.accountUserProperties = accountUserProperties;
    }

    public String getOpenMeFirst() {
        return openMeFirst;
    }

    public void setOpenMeFirst(String openMeFirst) {
        this.openMeFirst = openMeFirst;
    }

}
