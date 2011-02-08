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

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.tolven.core.SimpleProperty;
import org.tolven.security.key.AccountProcessingPrivateKey;
import org.tolven.security.key.AccountProcessingPublicKey;
import org.tolven.security.key.AccountPublicKey;

/**
 * Maintains a collection of HealthRecords and related information. An account has one or more account administrators that generally 
 * control the users of the account and what they can do. Accounts are Sponsored either by the users, the user's employers, an insurance company, or other entity.
 * @see AccountSponsor
 * @author John Churin
 */
@Entity
@Table
public class Account implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CORE_SEQ_GEN")
    private long id;

    @Column
    private String title;

    @Column
    private boolean subsetAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountType accountType;

    @Column
    private String timeZone;

    @Column
    private String locale;

    @Column
    private String emailFormat;
    
    @Column
    private Boolean enableBackButton;

    @Column
    private Boolean disableAutoRefresh;

    @Column
    private Boolean manualMetadataUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account parent;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account accountTemplate;

    @OneToMany(mappedBy = "account", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AccountUser> accountUsers = null;
    
    @OneToMany(mappedBy = "account", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AccountRole> accountRoles = null;

    @OneToMany(mappedBy = "account", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Sponsorship> sponsorships = null;
    
    @OneToMany(mappedBy = "account", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AccountProperty> accountProperties = null;

    @Embedded
    private AccountPublicKey accountPublicKey;
    
    @Embedded
    private AccountProcessingPublicKey accountProcessingPublicKey;

    @Embedded
    private AccountProcessingPrivateKey accountProcessingPrivateKey;
    
    /**
     * Construct an empty Account or Sub-account. A sub-account should have a non-null Parent Account. Account trees cannot have circular references. 
     * @see #getParent
     */
    public Account() {
    }
    
    /**
     * The unique internal Tolven ID of the account. This ID has no meaning other than uniqueness. 
     * Leave Id null for a new record. The EntityManager will assign a unique Id when it is persisted.
     */
    public long getId() {
        return id;
    }

    public void setId(long val) {
        this.id = val;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Account)) return false;
        if (this.getId()==((Account)obj).getId()) return true;
        return false;
    }

    public String toString() {
        return "Account: " + getId();
    }

    public int hashCode() {
    	if (getId()==0) throw new IllegalStateException( "id not yet established in Account object");
        return new Long( getId()).hashCode();
    }
    /**
     * A String containing the title for this account. The Title is set by an account administrator and has no meaning to Tolven.
     * The title will be used to refer to the account in web pages and communications regarding the account. It is limited to 
     * 50 characters in length and may not contain HTML mark-up.
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>An account with a non-null parent is a sub-account. Sub-accounts can have users and records just like the parent (or top-level) Account.
     * However, Sub-account users are always constrained to a sub-set of users of the parent account. Thus, the top-level Account must have all users in 
     * any sub-account.</p>
     * <p>Personal accounts usually don't need sub-accounts.</p>
     * <p>The administrator of an account may grant administrator permission to any users of the sub-account. In so doing, the sub-account 
     * Administrator can do the same with any sub-sub-account. Also, any Administrator of a parent account can administer any sub-account below it, 
     * no matter how deeply nested. </p>
     * <p>Note: Administering an account does not, on it's own, allow the administrator to see health data. This is because 
     * administrators (or anyone else other than the owner of the data) do not know the password protecting the data itself. This also means that an 
     * administrator will be unable to recover protected data.</p>
     *<p>While the number of users in an Account is restricted as one goes further down an Account tree, this is not necessarily true for HealthRecords. 
     * </p>
     * <p>It would be common for a top-level administrator to create a minimum of two sub-accounts: one for records he/she manages and one for 
     * records managed by sub-accounts. For example, the top-level administrator may have records in one sub-account they maintain for testing and training purposes 
     * which most users can't access. The second sub-account, with many more users, contains all (real) patients of the provider.</p>
     * @see #isSubsetAccount()
     */
    public Account getParent() {
        return parent;
    }

    public void setParent(Account parent) {
        this.parent = parent;
    }

    /**
     * <p>When a sub-account is created, an irreversible decision is made to make it a subset sub-account or an independent sub-account. A subset account means the account
     * must contain a sub-set of the records in the parent account. For example, a nursing unit would always be constrained to patients of the hospital.
     * A practice or clinic may have sub-accounts with specific kinds of patients selected from the clinic's patient roster. In any case, removing a record from a sub-account 
     * will require it's removal from nested sub-accounts. </p>
     * <p>In all cases, the root account contains an implied list of all records that exist anywhere within the account. Only top-level administrators 
     * have access to these patients.</p>
     * <p>An independent account can have new HealthRecords created by the administrator(s) of that SubAccount.
     * Examples of independent accounts include ReferralAccounts, clinics within an organization where each clinic maintains separate record. Or a psych hospital that maintains independent records.</p>
     * <p>Users of a parent account can be allowed access to all records in the sub-account. Or the sub-account can have a separate list of users.</p>
     *<p>The subset setting is meaningless for the top-level account.</p>
     */
    public boolean isSubsetAccount() {
        return subsetAccount;
    }

    public void setSubsetAccount(boolean subsetAccount) {
        this.subsetAccount = subsetAccount;
    }

    public AccountType getAccountType() {
         return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    
    public Set<AccountUser> getAccountUsers() {
        return accountUsers;
    }
    
    public void setAccountUsers(Set<AccountUser> accountUsers) {
        this.accountUsers = accountUsers;
    }
    
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public Set<Sponsorship> getSponsorships() {
		return sponsorships;
	}
	public void setSponsorships(Set<Sponsorship> sponsorships) {
		this.sponsorships = sponsorships;
	}

    public AccountPublicKey getAccountPublicKey() {
        return accountPublicKey;
    }

    public PublicKey getPublicKey() throws GeneralSecurityException {
        return accountPublicKey == null ? null : accountPublicKey.getPublicKey();
    }

    public void setPublicKey(PublicKey aPublicKey) {
        accountPublicKey = AccountPublicKey.getInstance();
        accountPublicKey.init(aPublicKey);
    }
    
    public boolean hasPublicKey() {
        return accountPublicKey != null;
    }
    
    public AccountProcessingPublicKey getAccountProcessingPublicKey() {
        return accountProcessingPublicKey;
    }

    public AccountProcessingPrivateKey getAccountProcessingPrivateKey() {
        return accountProcessingPrivateKey;
    }

    public void setAccountProcessingPublicKey(PublicKey aPublicKey) {
        accountProcessingPublicKey = AccountProcessingPublicKey.getInstance();
        accountProcessingPublicKey.init(aPublicKey);
    }

    public void setAccountProcessingPrivateKey(AccountProcessingPrivateKey anAccountProcessingPrivateKey) {
        accountProcessingPrivateKey = anAccountProcessingPrivateKey;
    }

    public boolean hasAccountProcessingPrivateKey() {
        return accountProcessingPrivateKey != null;
    }
    
	public String getEmailFormat() {
		return emailFormat;
	}
	public void setEmailFormat(String emailFormat) {
		this.emailFormat = emailFormat;
	}
	
	public String getEnableBackButton() {
		if( null == enableBackButton ) return "false";
		return enableBackButton.toString();
	}

	public Boolean isEnableBackButton() {
		if( null == enableBackButton ) return false;
		return enableBackButton;
	}
	public void setEnableBackButton(Boolean enableBackButton) {
		this.enableBackButton = enableBackButton;
	}
	public Account getAccountTemplate() {
		return accountTemplate;
	}
	public void setAccountTemplate(Account accountTemplate) {
		this.accountTemplate = accountTemplate;
	}
	public Boolean isDisableAutoRefresh() {
		return disableAutoRefresh;
	}
	
	public Boolean getDisableAutoRefresh() {
		return disableAutoRefresh;
	}
	
	public void setDisableAutoRefresh(Boolean disableAutoRefresh) {
		this.disableAutoRefresh = disableAutoRefresh;
	}

	public Set<AccountRole> getAccountRoles() {
		if (accountRoles==null) accountRoles = new HashSet<AccountRole>(10);
		return accountRoles;
	}

	public void setAccountRoles(Set<AccountRole> accountRoles) {
		this.accountRoles = accountRoles;
	}

	public Boolean getManualMetadataUpdate() {
		return manualMetadataUpdate;
	}
	public Set<AccountProperty> getAccountProperties() {
		if (accountProperties==null) {
			accountProperties = new HashSet<AccountProperty>();
		}
		return accountProperties;
	}

	public void setAccountProperties(Set<AccountProperty> accountProperties) {
		this.accountProperties = accountProperties;
	}

	public void setManualMetadataUpdate(Boolean manualMetadataUpdate) {
		this.manualMetadataUpdate = manualMetadataUpdate;
	}
	/**
	 * Get a property from an account. This will look at the account, then the templateAccount, if any, and finally, in system properties.
	 * @return
	 */
	public Map<String,String> getProperty() {
		return new AccountPropertyMap( this );
	}
	
	public Map<String,String> getBrandedProperty(String brand) {
		return new AccountPropertyMap( this, brand );
	}
	
	/**
	 * Return a Properties collection containing all properties where the property name matches a supplied regular expression
	 * @param regex
	 * @return Matching properties
	 */
	public Set<SimpleProperty> findMatchingProperties( String regex ) {
		Set<SimpleProperty> properties = new HashSet<SimpleProperty>();
		// COmpile the pattern we will be using
		Pattern pattern = Pattern.compile(regex);
		// First, get system properties
		for (Map.Entry<Object, Object> entry: System.getProperties().entrySet()) {
			 Matcher m = pattern.matcher(entry.getKey().toString());
			 if (m.matches()) {
				 SimpleProperty property = new SimpleProperty();
				 property.setName(entry.getKey().toString());
				 if (entry.getValue()!=null) {
					 property.setValue(entry.getValue().toString());
				 }
				 properties.add(property);
			 }
		}
		// Next, get accountTemplate properties
		Account accountTemplate = getAccountTemplate();
		if (accountTemplate!=null && accountTemplate!=this) {
			for (AccountProperty ap : accountTemplate.getAccountProperties()) {
				 Matcher m = pattern.matcher(ap.getPropertyName());
				 if (m.matches()) {
					 SimpleProperty property = new SimpleProperty();
					 property.setName(ap.getPropertyName());
					 property.setValue(ap.getPropertyValue());
					 properties.add(property);
				 }
			}
		}
		// Now layer on the account Properties
		for (AccountProperty ap : getAccountProperties()) {
			 Matcher m = pattern.matcher(ap.getPropertyName());
			 if (m.matches()) {
				 SimpleProperty property = new SimpleProperty();
				 property.setName(ap.getPropertyName());
				 property.setValue(ap.getPropertyValue());
				 properties.add(property);
			 }
		}
		// Return the collection of properties matching the specified criteria
		return properties;
	}
	
	/**
	 * A Map that wraps the list of properties for an account.
	 * @author John Churin
	 *
	 */
	class AccountPropertyMap implements Map<String, String>{
		Account account;
		String brand;
		
		public AccountPropertyMap( Account account ) {
			this.account = account; 
		}
		
		public AccountPropertyMap( Account account, String brand ) {
			this.account = account; 
			this.brand = brand;
		}

		public String getProperty(Object key) {
			for (AccountProperty property : getAccountProperties()) {
				if (key.equals(property.getPropertyName())) {
					return property.getPropertyValue();
				}
			}
			Account accountTemplate = getAccountTemplate();
			if (accountTemplate!=null && accountTemplate!=account) {
				return accountTemplate.getProperty().get(key);
			}
			// Finally, try to get it from the system properties
			return System.getProperty(key.toString());
			
		}
		@Override
		public String get(Object key) {
			if (brand!=null) {
				String rslt = getProperty(key+"."+brand);
				if (rslt==null) {
					rslt = getProperty(key);
				}
				return rslt;
			}
			return getProperty(key);
		}

		@Override
		public boolean containsKey(Object key) {
			for (AccountProperty property : getAccountProperties()) {
				if (key.equals(property.getPropertyName())) {
					return true;
				}
			}
			Account accountTemplate = getAccountTemplate();
			if (accountTemplate!=null && accountTemplate!=account) {
				return accountTemplate.getProperty().containsKey(key);
			}
			return false;
		}
		
		@Override
		public int size() {
			return getAccountProperties().size();
		}
		/**
		 * Put or update a property in this account's list of properties.
		 */
		@Override
		public String put(String key, String value) {
			for (AccountProperty property : getAccountProperties()) {
				if (key.equals(property.getPropertyName())) {
					String oldValue = property.getPropertyValue();
					property.setPropertyValue(value);
					return oldValue;
				}
			}
			// Create a new property
			AccountProperty property = new AccountProperty();
			property.setAccount(account);
			property.setPropertyName(key);
			property.setPropertyValue(value);
			account.getAccountProperties().add(property);
			return null;
		}
		
		@Override
		public void clear() {
			
		}
		
		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<java.util.Map.Entry<String, String>> entrySet() {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<String> keySet() {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public void putAll(Map<? extends String, ? extends String> m) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String remove(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<String> values() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
