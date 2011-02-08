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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.NewCookie;

import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.locale.ResourceBundleHelper;
import org.tolven.locale.TolvenResourceBundle;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.sso.TolvenSSO;
import org.tolven.web.security.GeneralSecurityFilter;

import com.iplanet.sso.SSOToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
/**
 * An abstract class from which Tolven Faces backing beans inherit. Provides frequently used utilities such as getNow and getTop.
 * @author John Churin
 *
 */
public abstract class TolvenAction extends TolvenBean {

    @EJB
    private TolvenPropertiesLocal propertyBean;
    
    private Locale locale;
    
    public TolvenAction() {
        super();
        // J2EE 1.5 has not yet defined exact XML <ejb-ref> syntax for EJB3
        //TODO Injection does not work for JBoss (v4.0.4GA) web tier (tomcat), but does for GlassFish
    }

    protected Object getSessionAttribute(String name) {
        Map<String,Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return sessionMap.get( name );
    }

    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    protected Object getRequestAttribute( String name ) {
//      TolvenLogger.info( "Request attribute: " + name + "=" + req.getAttribute(name), TolvenAction.class);
        return getRequest().getAttribute( name );
    }

    protected void setRequestAttribute(String name, Object value) {
        getRequest().setAttribute(name, value);
    }

    protected void setSessionAttribute(String name, Object anObject) {
        Map<String,Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put(name, anObject);
    }

    protected void removeSessionAttribute(String name) {
        Map<String,Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.remove(name);
    }

    protected Object getRequestParameter( String name ) {
//      TolvenLogger.info( "Request parameter: " + name + "=" + req.getParameter(name), TolvenAction.class);
        return getRequest().getParameter( name );
    }

    protected long getRequestParameterAsLong( String name ) {
        String param = (String)getRequestParameter( name );
        if (param==null) return 0;
        return Long.parseLong(param);
    }

    protected Date getNow() {
        Date now = (Date) getRequestAttribute("tolvenNow");
        return now;
    }
    
    protected TopAction getTop() {
        TopAction top = (TopAction) getSessionAttribute( "top");
        return top;
    }

    protected long getSessionTolvenUserId() {
        String idString = getSessionProperty(GeneralSecurityFilter.TOLVENUSER_ID);
        if (idString == null || idString.length() == 0) {
            throw new IllegalStateException(getClass() + ": Session TOLVENUSER_ID is null");
        } else {
            return Long.parseLong(idString);
        }
    }

    protected long getSessionAccountId() {
        String idString = getSessionProperty(GeneralSecurityFilter.ACCOUNT_ID);
        if (idString == null || idString.length() == 0) {
            throw new IllegalStateException(getClass() + ": Session ACCOUNT_ID is null");
        } else {
            return Long.parseLong(idString);
        }
    }

    protected long getSessionAccountUserId() {
        String idString = getSessionProperty(GeneralSecurityFilter.ACCOUNTUSER_ID);
        if (idString == null || idString.length() == 0) {
            throw new IllegalStateException(getClass() + ": Session ACCOUNTUSER_ID is null");
        } else {
            return Long.parseLong(idString);
        }
    }

    /**
     * Get the default locale for this user, The value contained here is based on the following precedence:
     * <ol>
     * <li>The user's locale if not null</li>
     * <li>The account's locale if not null</li>
     * <li>From Java Locale.getDefault()</li>
     * </ol>
     * @return
     */
    public Locale getLocale() {
        if (locale == null) {
            String accountUserLocale = getSessionProperty(ResourceBundleHelper.USER_LOCALE);
            if (accountUserLocale != null && accountUserLocale.length() == 0) {
                accountUserLocale = null;
            }
            String accountLocale = getSessionProperty(ResourceBundleHelper.ACCOUNT_LOCALE);
            if (accountLocale != null && accountLocale.length() == 0) {
                accountLocale = null;
            }
            locale = ResourceBundleHelper.getLocale(accountUserLocale, accountLocale);
        }
        return locale;
    }

    public String logout() {
        return dispatchingLogout("loggedOut");
    }

    protected String dispatchingLogout(String outcome) {
        return outcome;
    }

    protected PrivateKey getUserPrivateKey() {
        String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        return TolvenSSO.getInstance().getUserPrivateKey(getRequest(), keyAlgorithm);
    }

    protected X509Certificate getUserX509Certificate() {
        return TolvenSSO.getInstance().getUserX509Certificate(getRequest());
    }

    protected PublicKey getUserPublicKey() {
        return TolvenSSO.getInstance().getUserPublicKey(getRequest());
    }
    
    protected SSOToken getSSOToken() {
        return TolvenSSO.getInstance().getSSOToken(getRequest());
    }

    protected String getSessionProperty(String name) {
        return TolvenSSO.getInstance().getSessionProperty(name, getSSOToken());
    }

    protected void setSessionProperty(String name, String value) {
        TolvenSSO.getInstance().setSessionProperty(name, value, getSSOToken());
    }

    protected void removeSessionProperty(String name) {
        TolvenSSO.getInstance().setSessionProperty(name, null, getSSOToken());
    }

    protected NewCookie getSSOCookie() {
        return TolvenSSO.getInstance().getSSOCookie(getSSOToken());
    }

    protected WebResource getAppWebResource() {
        String appRestfulURL = (String) getRequest().getSession().getServletContext().getInitParameter("appRestful.url");
        return getRESTfulClient().resource(appRestfulURL);
    }

    protected Client getRESTfulClient() {
        Client client = (Client)getRequest().getSession().getServletContext().getAttribute("restfulClient");
        return client;
    }

    /**
     * Return the accountUser from current request based on the selected accountUserId stored in session.
     * If the accountUser is not in request, query it and stick it there.
     * If we can't find accountUserId, then return null.
     * @return
     */
    public AccountUser getAccountUser() {
        AccountUser accountUser = (AccountUser) getRequestAttribute(GeneralSecurityFilter.ACCOUNTUSER);
        return accountUser;
    }

    /**
     * Return the TolvenUser from current request.
     * @return
     */
    public TolvenUser getUser() {
        TolvenUser user = (TolvenUser) getRequestAttribute(GeneralSecurityFilter.TOLVENUSER);
        return user;
    }

    /**
     * Return the accountUser from current request based on the selected accountUserId stored in session.
     * If the accountUser is not in request, query it and stick it there.
     * If we can't find accountUserId, then return null.
     * @return
     */
    public TolvenResourceBundle getTolvenResourceBundle() {
        return (TolvenResourceBundle) getSessionAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE);
    }

    public String getTolvenPersonString(String name) {
        return TolvenSSO.getInstance().getTolvenPersonString(name, getRequest());
    }

}
