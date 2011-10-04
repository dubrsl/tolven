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
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.AuthenticationException;
import javax.naming.InitialContext;

import org.tolven.gatekeeper.entity.SecurityQuestion;
import org.tolven.naming.TolvenContext;
import org.tolven.session.TolvenSessionWrapperFactory;

/**
 * Faces Action for recovering a login password.
 * 
 * @author Joseph Isaac
 */
public class RecoverLoginPasswordAction {
    
    private String activeSecurityQuestion;

    @EJB
    private LoginPasswordLocal loginPasswordBean;

    private List<SelectItem> loginSecurityQuestions;
    private String newUserPassword;
    private String newUserPassword2;
    private String oldUserPassword;
    private String realm;
    private List<SelectItem> realms;
    private String searchRealm;
    private String searchUid;
    private String securityQuestionAnswer;
    private String uid;

    public RecoverLoginPasswordAction() {
    }

    public String findSecurityQuestion() {
        try {
            activeSecurityQuestion = getLoginPasswordBean().findActiveSecurityQuestion(getSearchUid(), getSearchRealm());
            setUid(getSearchUid());
            setRealm(getSearchRealm());
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("findQuestionForm", new FacesMessage("Access denied"));
            return "error";
        } catch (Exception ex) {
            throw new RuntimeException("Could not recover login password for user " + getSearchUid() + " in realm: " + getSearchRealm(), ex);
        }
        if(activeSecurityQuestion == null) {
            FacesContext.getCurrentInstance().addMessage("findQuestionForm", new FacesMessage("Found no active security question"));
        }
        return "success";
    }

    public String getActiveSecurityQuestion() {
        return activeSecurityQuestion;
    }

    public LoginPasswordLocal getLoginPasswordBean() {
        return loginPasswordBean;
    }

    public List<SelectItem> getLoginSecurityQuestions() {
        Collection<SecurityQuestion> securityQuestions = getLoginPasswordBean().findAllSecurityQuestions();
        loginSecurityQuestions = new ArrayList<SelectItem>();
        loginSecurityQuestions.add(new SelectItem(null));
        for (SecurityQuestion securityQuestion : securityQuestions) {
            loginSecurityQuestions.add(new SelectItem(securityQuestion.getQuestion()));
        }
        return loginSecurityQuestions;
    }

    public String getNewUserPassword() {
        return newUserPassword;
    }

    public String getNewUserPassword2() {
        return newUserPassword2;
    }

    public String getOldUserPassword() {
        return oldUserPassword;
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

    public String getSearchRealm() {
        return searchRealm;
    }

    public String getSearchUid() {
        return searchUid;
    }

    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    public String getUid() {
        return uid;
    }

    public String recoverLoginPassword() {
        boolean error = false;
        if (!getNewUserPassword().equals(getNewUserPassword2())) {
            FacesContext.getCurrentInstance().addMessage("recover:newUserPassword", new FacesMessage("Both passwords must match"));
            FacesContext.getCurrentInstance().addMessage("recover:newUserPassword2", new FacesMessage("Both passwords must match"));
            error = true;
        }
        if (getActiveSecurityQuestion() == null || getActiveSecurityQuestion().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("recover:securityQuestion", new FacesMessage("A security question must be available"));
            error = true;
        }
        if (error) {
            return "error";
        }
        try {
            char[] sqa = null;
            if (getSecurityQuestionAnswer() != null) {
                sqa = getSecurityQuestionAnswer().toCharArray();
            }
            getLoginPasswordBean().recoverPassword(getUid(), getNewUserPassword().toCharArray(), getRealm(), getActiveSecurityQuestion(), sqa);
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("recoverForm", new FacesMessage("Access denied"));
            return "error";
        } catch (Exception ex) {
            throw new RuntimeException("Could not recover login password for user " + getUid() + " in realm: " + getRealm(), ex);
        }
        TolvenSessionWrapperFactory.getInstance().logout();
        return "success";
    }

    public void setActiveSecurityQuestion(String activeSecurityQuestion) {
        this.activeSecurityQuestion = activeSecurityQuestion;
    }

    public void setLoginSecurityQuestions(List<SelectItem> loginSecurityQuestions) {
        this.loginSecurityQuestions = loginSecurityQuestions;
    }

    public void setNewUserPassword(String newPassword) {
        this.newUserPassword = newPassword;
    }

    public void setNewUserPassword2(String newUserPassword2) {
        this.newUserPassword2 = newUserPassword2;
    }

    public void setOldUserPassword(String oldUserPassword) {
        this.oldUserPassword = oldUserPassword;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setSearchRealm(String searchRealm) {
        this.searchRealm = searchRealm;
    }

    public void setSearchUid(String searchUid) {
        this.searchUid = searchUid;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
