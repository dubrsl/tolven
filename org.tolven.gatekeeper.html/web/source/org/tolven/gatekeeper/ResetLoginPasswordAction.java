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
package org.tolven.gatekeeper;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.AuthenticationException;

import org.tolven.naming.TolvenPerson;
import org.tolven.session.TolvenSessionWrapperFactory;

/**
 * Faces Action for reset a login password.
 * 
 * @author Joseph Isaac
 */
public class ResetLoginPasswordAction {

    private String admin;
    private String adminPassword;
    private String cn;

    @EJB
    private LDAPLocal ldapBean;

    @EJB
    private LoginPasswordLocal loginPasswordBean;

    private String newUserPassword;
    private String newUserPassword2;
    private String searchUid;
    private String sn;
    private String uid;

    public ResetLoginPasswordAction() {
    }

    public String findUser() {
        TolvenPerson tp = null;
        try {
            tp = getLdapBean().findTolvenPerson(getSearchUid(), getRealm());
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("findUserForm", new FacesMessage("Access denied"));
            return "error";
        } catch (Exception ex) {
            throw new RuntimeException("Could not find user " + getUid() + " in realm: " + getRealm(), ex);
        }
        if (tp == null) {
            FacesContext.getCurrentInstance().addMessage("findUserForm", new FacesMessage("No result found for user: " + getUid() + " in realm: " + getRealm()));
            setUid(null);
            setCn(null);
            setSn(null);
        } else {
            FacesContext.getCurrentInstance().addMessage("findUserForm", new FacesMessage("Found user " + tp.getUid() + " in realm: " + getRealm()));
            setUid(tp.getUid());
            setCn(tp.getCn());
            setSn(tp.getSn());
        }
        return "success";
    }

    public String getAdmin() {
        return admin;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public String getCn() {
        return cn;
    }

    public LDAPLocal getLdapBean() {
        return ldapBean;
    }

    public LoginPasswordLocal getLoginPasswordBean() {
        return loginPasswordBean;
    }

    public String getNewUserPassword() {
        return newUserPassword;
    }

    public String getNewUserPassword2() {
        return newUserPassword2;
    }

    public String getRealm() {
        return TolvenSessionWrapperFactory.getInstance().getRealm();
    }

    public String getSearchUid() {
        return searchUid;
    }

    public String getSn() {
        return sn;
    }

    public String getUid() {
        return uid;
    }

    public String navigateHome() {
        return "navigateHome";
    }

    public String resetLoginPassword() {
        boolean error = false;
        if (!getNewUserPassword().equals(getNewUserPassword2())) {
            FacesContext.getCurrentInstance().addMessage("resetpasswd:newUserPassword", new FacesMessage("Both passwords must match"));
            FacesContext.getCurrentInstance().addMessage("resetpasswd:newUserPassword2", new FacesMessage("Both passwords must match"));
            error = true;
        }
        if (error) {
            return "error";
        }
        try {
            getLoginPasswordBean().resetPassword(getUid(), getRealm(), getNewUserPassword().toCharArray(), getAdmin(), getAdminPassword().toCharArray());
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("resetpasswdForm", new FacesMessage("Access denied"));
            return "error";
        } catch (Exception ex) {
            throw new RuntimeException("Could not reset login password for user " + getUid() + " in realm: " + getRealm(), ex);
        }
        FacesContext.getCurrentInstance().addMessage("resetpasswdForm", new FacesMessage("Password reset for user " + getUid() + " in realm: " + getRealm()));
        setUid(null);
        setCn(null);
        setSn(null);
        return "success";
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public void setNewUserPassword(String newUserPassword) {
        this.newUserPassword = newUserPassword;
    }

    public void setNewUserPassword2(String newUserPassword2) {
        this.newUserPassword2 = newUserPassword2;
    }

    public void setSearchUid(String searchUid) {
        this.searchUid = searchUid;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
