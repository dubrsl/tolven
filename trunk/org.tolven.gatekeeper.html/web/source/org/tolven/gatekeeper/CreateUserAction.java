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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import org.tolven.exeption.GatekeeperSecurityException;
import org.tolven.naming.TolvenContext;
import org.tolven.naming.TolvenPerson;

/**
 * Faces Action bean concerned with the user registration process.
 * @author John Churin
 */
public class CreateUserAction {

    private String admin;
    private String adminPassword;
    private String generatedPassword;

    @EJB
    private LdapLocal ldapBean;

    private String realm;
    private List<SelectItem> realms;
    private TolvenPerson tp;

    public CreateUserAction() {
    }

    public String activate() throws Exception {
        //TODO How do we control create demouser permission in gatekeeper? Probably by URL permissions as opposed to System property
        /*
        if (!Boolean.parseBoolean(getTolvenPropertiesBean().getProperty("tolven.login.create.demoUser"))) {
            throw new SecurityException( "tolven property tolven.login.create.demoUser must be true");
        }
        */
        boolean error = false;
        if (!getTp().getUid().matches("[\\w\\.\\_\\-]+")) {
            FacesContext.getCurrentInstance().addMessage("createUserForm:uid", new FacesMessage("UserId can only contain letters, numbers, space, '.', '-', or '_'"));
            error = true;
        }
        if (getLdapLocal().findTolvenPerson(tp.getUid(), getRealm()) != null) {
            FacesContext.getCurrentInstance().addMessage("createUserForm:uid", new FacesMessage("This id is already in use, please select another"));
            error = true;
        }
        if (error) {
            return "error";
        }
        getTp().setCn(getTp().getGivenName() + " " + getTp().getSn());
        try {
            generatedPassword = new String(getLdapLocal().createTolvenPerson(tp, getTp().getUid(), null, getRealm(), null, getAdmin(), getAdminPassword().toCharArray()));
        } catch (Exception ex) {
            GatekeeperSecurityException gex = GatekeeperSecurityException.getRootGatekeeperException(ex);
            if (gex == null) {
                throw new RuntimeException("Could not register and activate user " + getTp().getUid() + " for admin: " + getAdmin(), ex);
            } else {
                ex.printStackTrace();
                FacesContext.getCurrentInstance().addMessage("createUserForm:adminPassword", new FacesMessage(gex.getFormattedMessage()));
                return "error";
            }
        }
        FacesContext.getCurrentInstance().addMessage("createUserForm", new FacesMessage("Password generated for user " + getTp().getUid() + " in realm: " + getRealm() + ": " + getGeneratedPassword()));
        return "success";
    }

    public String getAdmin() {
        return admin;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public String getGeneratedPassword() {
        return generatedPassword;
    }

    private LdapLocal getLdapLocal() {
        return ldapBean;
    }

    public String getRealm() {
        return realm;
    }

    public List<SelectItem> getRealms() {
        if (realms == null) {
            TolvenContext tolvenContext;
            try {
                InitialContext ictx = new InitialContext();
                tolvenContext = (TolvenContext) ictx.lookup("tolvenContext");
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup tolvenContext", ex);
            }
            realms = new ArrayList<SelectItem>();
            for (String realmId : tolvenContext.getRealmIds()) {
                realms.add(new SelectItem(realmId));
            }
        }
        return realms;
    }

    public TolvenPerson getTp() {
        if (tp == null) {
            tp = new TolvenPerson();
        }
        return tp;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void setGeneratedPassword(String generatedPassword) {
        this.generatedPassword = generatedPassword;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setTp(TolvenPerson tp) {
        this.tp = tp;
    }

}
