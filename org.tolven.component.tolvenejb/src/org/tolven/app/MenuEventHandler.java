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
 * @author Joseph Isaac
 * @version $Id$
 */
package org.tolven.app;

import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.ResourceBundle;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.tolven.app.entity.MSAction;
import org.tolven.core.entity.AccountUser;
/**
 * An abstract class used to handle custom menu events.
 * @author John Churin
 *
 */
public abstract class MenuEventHandler {

    private HttpServletRequest request;
    private MSAction action;
    private String element;
    private AccountUser accountUser;
    private ResourceBundle resourceBundle;
    private Date tolvenNow;
    private Writer writer;
    private InitialContext context;

    public MenuEventHandler(MSAction action) {
        this.action = action;
    }

    public static MenuEventHandler createMenuEventHandler(Class<?> menuEventHandlerFactoryClass, MSAction action) throws Exception {
        Method method = menuEventHandlerFactoryClass.getDeclaredMethod("createMenuEventHandler", new Class[] { MSAction.class });
        return (MenuEventHandler) method.invoke(menuEventHandlerFactoryClass.newInstance(), new Object[] { action });
    }

    public MSAction getAction() {
        return action;
    }

    public void setAction(MSAction action) {
        this.action = action;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public AccountUser getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(AccountUser accountUser) {
        this.accountUser = accountUser;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public Date getTolvenNow() {
        return tolvenNow;
    }

    public void setTolvenNow(Date tolvenNow) {
        this.tolvenNow = tolvenNow;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    protected InitialContext getContext() throws NamingException {
        if (context == null) {
            context = new InitialContext();
        }
        return context;
    }

    public abstract void initializeAction() throws Exception;

}
