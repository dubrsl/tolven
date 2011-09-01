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

import org.tolven.gatekeeper.entity.SecurityQuestion;
import org.tolven.session.TolvenSessionWrapperFactory;

/**
 * Faces Action for backuping a login password.
 * 
 * @author Joseph Isaac
 */
public class BackupLoginPasswordAction {

    private String activeSecurityQuestion;
    private String confirmSecurityQuestionAnswer;

    @EJB
    private LoginPasswordLocal loginPasswordBean;

    private List<SelectItem> loginSecurityQuestions;
    private String oldUserPassword;
    private String securityQuestion;
    private String securityQuestionAnswer;

    public BackupLoginPasswordAction() {
    }

    public String backupLoginPassword() {
        boolean error = false;
        if (getSecurityQuestion() == null || getSecurityQuestion().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("backupForm:securityQuestion", new FacesMessage("A security question must be selected"));
            error = true;
        }
        if (getSecurityQuestionAnswer() != null && !getSecurityQuestionAnswer().equals(getConfirmSecurityQuestionAnswer())) {
            FacesContext.getCurrentInstance().addMessage("backupForm:securityQuestionAnswer", new FacesMessage("The answer and its confirmation do not match"));
            FacesContext.getCurrentInstance().addMessage("backupForm:confirmSecurityQuestionAnswer", new FacesMessage("The answer and its confirmation do not match"));
            error = true;
        }
        if (error) {
            return "error";
        }
        try {
            getLoginPasswordBean().backupPassword(getUid(), getRealm(), getOldUserPassword().toCharArray(), getSecurityQuestion(), getSecurityQuestionAnswer().toCharArray());
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("backupForm", new FacesMessage(ex.getMessage()));
            return "error";
        } catch (Exception ex) {
            throw new RuntimeException("Could not backup login password for user " + getUid() + " in realm: " + getRealm(), ex);
        }
        return "success";
    }

    public String getActiveSecurityQuestion() {
        if (activeSecurityQuestion == null) {
            try {
                activeSecurityQuestion = getLoginPasswordBean().findActiveSecurityQuestion(getUid(), getRealm());
            } catch (AuthenticationException ex) {
                // Authentication will be denied on any submit, so no need to handle here
            }
        }
        return activeSecurityQuestion;
    }

    public String getConfirmSecurityQuestionAnswer() {
        return confirmSecurityQuestionAnswer;
    }

    public LoginPasswordLocal getLoginPasswordBean() {
        return loginPasswordBean;
    }

    public int getLoginSecurityQuestionCount() {
        return getLoginSecurityQuestions().size() - 1;
    }

    public List<SelectItem> getLoginSecurityQuestions() {
        if (loginSecurityQuestions == null) {
            Collection<SecurityQuestion> securityQuestions = getLoginPasswordBean().findAllSecurityQuestions();
            loginSecurityQuestions = new ArrayList<SelectItem>();
            loginSecurityQuestions.add(new SelectItem(null));
            for (SecurityQuestion securityQuestion : securityQuestions) {
                loginSecurityQuestions.add(new SelectItem(securityQuestion.getQuestion()));
            }
        }
        return loginSecurityQuestions;
    }

    public String getOldUserPassword() {
        return oldUserPassword;
    }

    public String getRealm() {
        return TolvenSessionWrapperFactory.getInstance().getRealm();
    }

    public String getSecurityQuestion() {
        if (securityQuestion == null) {
            securityQuestion = getActiveSecurityQuestion();
        }
        return securityQuestion;
    }

    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    public String getUid() {
        return (String) TolvenSessionWrapperFactory.getInstance().getPrincipal();
    }

    public void setConfirmSecurityQuestionAnswer(String confirmSecurityQuestionAnswer) {
        this.confirmSecurityQuestionAnswer = confirmSecurityQuestionAnswer;
    }

    public void setLoginSecurityQuestions(List<SelectItem> loginSecurityQuestions) {
        this.loginSecurityQuestions = loginSecurityQuestions;
    }

    public void setOldUserPassword(String oldUserPassword) {
        this.oldUserPassword = oldUserPassword;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

}
