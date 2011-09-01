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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.tolven.app.GlobalLocaleText;
import org.tolven.app.el.GlobalLocaleMap;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.core.util.TolvenPropertiesMap;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.locale.TolvenResourceBundle;
import org.tolven.security.TolvenPerson;
import org.tolven.util.ExceptionFormatter;
import org.tolven.web.security.GeneralSecurityFilter;
import org.tolven.web.util.TSDateFormat;

/**
 * Backing bean for the logged in user. Thus, this bean hangs around for most of the session.
 * @author John Churin
 */
public class TopAction extends TolvenAction implements GlobalLocaleText {

	private String startPage;
	private TolvenPerson tp;
	private boolean showProviderInactive = false;
    private TolvenPropertiesMap tolvenPropertiesMap;
	private PermissionMap PermissionMap;

	private TSDateFormatList tsFormatList;
	
//	private long accountUserId;


	// One-time ignore default account
	private boolean ignoreDefault;

	private String info;

	private int dummyCount;
	
	/** Creates a new instance of TopAction */
    public TopAction() {
        dummyCount = 0;

        ignoreDefault = false;
    }

	/**
	 * This should only be used in the demo to simulate a simple database activity.
	 * @return
	 */
	public long getAccountCount() {
		return getActivationBean().countUserAccounts(getAccountUser().getUser());
	}
	

	public String getServerTime( ) {
		return getNow().toString();
	}

    /**
     * A simple method that returns the map of system properties to make them available to
     * EL expressions.
     * @return
     */
    public TolvenPropertiesMap getProperties( ) {
        FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
        return getProperties( ((ServletRequest)fc.getExternalContext().getRequest()) );
    }

    public TolvenPropertiesMap getProperties( ServletRequest req) {
        if (tolvenPropertiesMap==null) {
            String localAddr = req.getLocalAddr();
            String remoteAddr = req.getRemoteAddr();
            Logger.getLogger(TopAction.class).info("Contact: local=" + localAddr + " remote=" + remoteAddr);
            tolvenPropertiesMap = new TolvenPropertiesMap(localAddr, getTolvenPropertiesBean());
        }
        return tolvenPropertiesMap;
    }
	public ExceptionFormatterMap getExceptionFormatter() {
		return new ExceptionFormatterMap();
	}
	/**
	 * Return the file name of the application logo.
	 * @return
	 */
    public String getApplicationLogo() {
        // If no AccountType known yet, return the default logo.
        AccountUser accountUser = getAccountUser();
        if (accountUser == null) {
            return getProperties().get("tolven.web.defaultLogo");
        }
        // See if there is an override in properties
        String logo = getProperties().get("tolven.application." + getAccountType() + ".logo");
        if (logo == null) {
            // Otherwise, the pre-configured logo should be used.
            logo = accountUser.getLogo();
        }
        return logo;
    }

	/**
	 * Delete TolvenUser. We just change the state on the user object but actually remove the LDAP entry (which may not be desired). We also need to log the user out.
	 */
	public String deleteUser( ) throws Exception {
	    getLDAPLocal().deletePerson( getTp());
	    getActivationBean().deactivateUser( getTp().getUid());
		return logout();
	}

	public boolean isIgnoreDefault() {
//		TolvenLogger.info( "Getting default", TopAction.class);
		return ignoreDefault;
	}

	public void setIgnoreDefault(boolean ignoreDefault) {
		this.ignoreDefault = ignoreDefault;
	}

	/**
	 * Return the start (home) page for this user.
	 * This getter method controls how the dispatch page works. If
	 * a session doesn't have a start page yet, it means we'll either need to find
	 * the default page or prompt the use to select which account they want to use.
	 */
	public String getStartPage() {
//		TolvenLogger.info( "Getting start page", TopAction.class);
		return startPage;
	}

	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}
	/**
	 * Return the TolvenPerson representation of an LDAP entry if there is a logged-in user.
	 * @return
	 * @throws GeneralSecurityException 
	 * @throws IOException
	 */
    public TolvenPerson getTp() throws GeneralSecurityException {
        if (tp == null) {
            Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
            if (principal != null) {
                //tp=getLDAPLocal().createTolvenPerson( principal.getName() );
                //Attribute are now injected to the request before calling Tolven
                //These may in future be accessed directly rather than by TolvenPerson
                tp = new TolvenPerson();
                tp.setCn(getTolvenPersonString("cn"));
                tp.setDn(getTolvenPersonString("dn"));
                tp.setGivenName(getTolvenPersonString("givenName"));
                tp.setOrganizationName(getTolvenPersonString("o"));
                tp.setOrganizationUnitName(getTolvenPersonString("ou"));
                tp.setSn(getTolvenPersonString("sn"));
                tp.setStateOrProvince(getTolvenPersonString("st"));
                tp.setUid(getTolvenPersonString("uid"));
                List<String> mail = null;
                Set<String> set = (Set<String>) getRequest().getAttribute("mail");
                if (set == null || set.isEmpty()) {
                    mail = new ArrayList<String>();
                } else {
                    mail = new ArrayList<String>(set);
                }
                tp.setMail(mail);
            }
        }
        return tp;
    }

	public void setTp(TolvenPerson tp) {
		this.tp = tp;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getDummyCount() {
		return ++dummyCount;
	}

	public void setDummyCount(int dummyCount) {
		this.dummyCount = dummyCount;
	}

    public long getAccountUserId() {
        String idString = getSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_ID);
        if (idString == null || idString.length() == 0) {
            //Not sure why this isn't an exception as per the superclass?
            return 0;
        } else {
            return Long.parseLong(idString);
        }
    }

	/**
	 * Only used for initial setup, impractical for anything else.
	 * @return
	 */
	public List<TolvenUser> getAllActiveUsers() {
		List<TolvenUser> allActiveUsers = getActivationBean().findAllActiveUsers();
		return allActiveUsers;
	}


	public long getAccountId() {
		if(getAccountUser() == null) {
			return 0;
		} else {
			return getAccountUser().getAccount().getId();
		}
	}

	public String getAccountTitle() {
		if(getAccountUser() == null) {
			return null;
		} else {
			return getAccountUser().getAccount().getTitle();
		}
	}

	/**
	 * Get Read-only user permission attributes. These are not account permissions (roles).
	 * @return
	 */
	public PermissionMap getPermission() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = ((HttpServletRequest)fc.getExternalContext().getRequest());
		return new PermissionMap(request);
	}
	/**
	 * Read-only attribute indicating if this user has permission to administer account or not.
	 * If the flag changes in the database, user must log out and log back in to get the new setting.
	 * @return
	 */
	public boolean isAccountAdmin() {
		if(getAccountUser() == null) {
			return false;
		} else {
			return getAccountUser().isAccountPermission();
		}
	}

	/**
	 * Get the default timezone for this user, the first non-null value is selected:
	 * <ol>
	 * <li>The user's timezone if not null</li>
	 * <li>The account's timezone if not null</li>
	 * <li>From tolven.properties: tolven.timezone</li>
	 * <li>From Java TimeZone.getDefault()</li>
	 * </ol>
	 * @return
	 */
	public String getTimeZone() {
		if (null!=getAccountUser()) return getAccountUser().getTimeZone();
		String timeZone = null;
		TolvenUser user = getActivationBean().findTolvenUser(getSessionTolvenUserId());
		if (timeZone==null && user!=null) timeZone = user.getTimeZone();
		if (timeZone==null) timeZone = getTolvenPropertiesBean().getProperty("tolven.timezone");
		if (timeZone==null) timeZone = java.util.TimeZone.getDefault().getID();
		return timeZone;
	}

	public TimeZone getTimeZoneObject() {
		return TimeZone.getTimeZone(getTimeZone());
	}

	public String getAccountTimeZone() {
		if(getAccountUser() == null) {
			return null;
		} else {
			return getAccountUser().getAccount().getTimeZone();
		}
	}

	public String getAccountLocale() {
		if(getAccountUser() == null) {
			return null;
		} else {
			return getAccountUser().getAccount().getLocale();
		}
	}

	public String getAccountType() {
		if(getAccountUser() == null) {
			return null;
		} else {
			return getAccountUser().getAccount().getAccountType().getKnownType();
		}
	}

	public boolean getCanSelectAccounts() {
		return getAccountUserId() != 0;
	}

	public boolean isShowProviderInactive() {
		return showProviderInactive;
	}

	public void setShowProviderInactive(boolean showProviderInactive) {
		this.showProviderInactive = showProviderInactive;
	}

    public TolvenResourceBundle getAppBundle() {
        return getTolvenResourceBundle();
    }

    public TolvenResourceBundle getGlobalBundle() {
        return getTolvenResourceBundle();
    }

    /**
     * This method exists to handle the fact that JSF's method binding is limited to Map property type behavior.
     * In JSF pages, this will be accessed as top.localeText[aString] for dynamic strings.
     * @return
     */
    public String getLocaleText(String key) {
        return ResourceBundleHelper.getString(getTolvenResourceBundle(), key);
    }
    
    /**
     * This method exists to handle the fact that JSF's method binding is limited to Map property type behavior.
     * In JSF pages, this will be accessed as top.localeText[aString] for dynamic strings.
     * @return
     */
    public GlobalLocaleMap getLocaleText() {
        return new GlobalLocaleMap(this);
    }
    
    public String navAction() {
        return (String) getRequestParameter("navAction");
    }

    class ExceptionFormatterMap implements Map<Exception, String> {

		@Override
		public String get(Object key) {
			Exception exception = (Exception) key;
			return ExceptionFormatter.toSimpleString(exception, "\n");
		}
		
		// The rest of the methods don't do anything
		@Override
		public void clear() {
			
		}

		@Override
		public boolean containsKey(Object arg0) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public Set<java.util.Map.Entry<Exception, String>> entrySet() {
			return null;
		}


		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public Set<Exception> keySet() {
			return null;
		}

		@Override
		public String put(Exception key, String value ) {
			return null;
		}

		@Override
		public void putAll(Map<? extends Exception, ? extends String> m) {
			
		}

		@Override
		public String remove(Object key) {
			return null;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public Collection<String> values() {
			return null;
		}
    	
    }
	/**
	 * Present a TSDateFormat object based on the Date Type Parameters
	 */
	class TSDateFormatList extends HashMap<Object,TSDateFormat > {
		public TSDateFormatList() {
    }
		
		// path should be of the format "dateStyle~timeStyle~pattern
		public void buildDateForamtList(Object path ) {
			// Parse the arguments.
			String lPath = (String)path;
			String[] lDateparams = lPath.split("~", 2);
			this.put(path, new TSDateFormat(getLocale(), lDateparams[0], lDateparams[1]));
			
		}
		
		public TSDateFormat get(Object path) {
			if (!this.containsKey(path)) buildDateForamtList(path);
			return super.get(path);
		}
	}
	
	public TSDateFormatList getTSFormatList() throws Exception {
		if ( tsFormatList == null )	{
			tsFormatList = new TSDateFormatList();
		}
		return tsFormatList;
	}

    
}// class TopAction
