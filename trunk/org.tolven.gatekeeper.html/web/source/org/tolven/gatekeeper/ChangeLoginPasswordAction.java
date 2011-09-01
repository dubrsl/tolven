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
 * Faces Action for changing a login password.
 * 
 * @author Joseph Isaac
 */
public class ChangeLoginPasswordAction {

    private String activeSecurityQuestion;
    private String confirmSecurityQuestionAnswer;

    @EJB
    private LoginPasswordLocal loginPasswordBean;

    private List<SelectItem> loginSecurityQuestions;
    private String newUserPassword;
    private String newUserPassword2;
    private String oldUserPassword;
    private boolean removeSecurityQuestion;
    private String securityQuestion;
    private String securityQuestionAnswer;

    public ChangeLoginPasswordAction() {
    }

    public String changeLoginPassword() {
        boolean error = false;
        if (!getNewUserPassword().equals(getNewUserPassword2())) {
            FacesContext.getCurrentInstance().addMessage("changepasswdForm:newUserPassword", new FacesMessage("Both passwords must match"));
            FacesContext.getCurrentInstance().addMessage("changepasswdForm:newUserPassword2", new FacesMessage("Both passwords must match"));
            error = true;
        }
        String sq = getSecurityQuestion();
        if (sq != null && sq.trim().length() == 0 || isRemoveSecurityQuestion()) {
            sq = null;
        }
        String sqa = getSecurityQuestionAnswer();
        if (sqa != null && sqa.trim().length() == 0) {
            sqa = null;
        }
        String csqa = getConfirmSecurityQuestionAnswer();
        if (csqa != null && csqa.trim().length() == 0) {
            csqa = null;
        }
        if (sq == null && (sqa != null || csqa != null)) {
            FacesContext.getCurrentInstance().addMessage("changepasswdForm:securityQuestion", new FacesMessage("A security question must be selected when an answer is provided"));
            error = true;
        }
        if (sq != null) {
            if (sqa == null) {
                FacesContext.getCurrentInstance().addMessage("changepasswdForm:securityQuestionAnswer", new FacesMessage("A answer must be provided"));
                error = true;
            }
            if (csqa == null) {
                FacesContext.getCurrentInstance().addMessage("changepasswdForm:confirmSecurityQuestionAnswer", new FacesMessage("A answer must be provided"));
                error = true;
            }
            if (sqa != null && !sqa.equals(csqa)) {
                FacesContext.getCurrentInstance().addMessage("changepasswdForm:securityQuestionAnswer", new FacesMessage("The answer and its confirmation do not match"));
                FacesContext.getCurrentInstance().addMessage("changepasswdForm:confirmSecurityQuestionAnswer", new FacesMessage("The answer and its confirmation do not match"));
                error = true;
            }
        }
        if (error) {
            return "error";
        }
        try {
            char[] sqap = null;
            if (sqa != null) {
                sqap = sqa.toCharArray();
            }
            getLoginPasswordBean().changePassword(getUid(), getRealm(), getOldUserPassword().toCharArray(), getNewUserPassword().toCharArray(), sq, sqap);
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage("changepasswd", new FacesMessage(ex.getMessage()));
            return "error";
        } catch (Exception ex) {
            throw new RuntimeException("Could not change login password for user " + getUid() + " in realm: " + getRealm(), ex);
        }
        FacesContext.getCurrentInstance().addMessage("password", new FacesMessage("Password updated. You need to Sign Out and Login in again for the change to have effect"));
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

    public boolean isRemoveSecurityQuestion() {
        return removeSecurityQuestion;
    }

    public void setConfirmSecurityQuestionAnswer(String confirmSecurityQuestionAnswer) {
        this.confirmSecurityQuestionAnswer = confirmSecurityQuestionAnswer;
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

    public void setRemoveSecurityQuestion(boolean removeSecurityQuestion) {
        this.removeSecurityQuestion = removeSecurityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

}
