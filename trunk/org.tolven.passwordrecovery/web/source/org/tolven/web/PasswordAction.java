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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import org.tolven.security.PasswordRecoveryLocal;
import org.tolven.security.TolvenPerson;
import org.tolven.security.entity.PasswordRecovery;
import org.tolven.security.entity.SecurityQuestion;
import org.tolven.security.entity.SecurityQuestionPurpose;
import org.tolven.web.security.GeneralSecurityFilter;

/**
 * Faces Action bean concerned with the user and account registration process.
 * @author John Churin
 */
public class PasswordAction extends TolvenAction {

    protected TolvenPerson tp;
    private String uid;
    private String repeatUid;
    private String oldUserPassword;
    private String newUserPassword;
    private String newUserPassword2;

    private String securityQuestion;
    private String securityQuestionAnswer;
    private String confirmSecurityQuestionAnswer;
    private List<SelectItem> loginSecurityQuestions;
    private List<SelectItem> inativeUserSecurityQuestions;
    private List<SelectItem> activeUserSecurityQuestions;

    // Search Criteria
    private String searchCriteria;
    private String searchField;
    private int maxResults;
    private List<TolvenPerson> searchResults;
    private int timeLimit; // In milliseconds
    private String elapsedTime = null;

    @EJB(name = "tolven/PasswordRecoveryBean/local")
    private PasswordRecoveryLocal passwordRecoveryBean;

    /** Creates a new instance of PasswordAction */
    public PasswordAction() {
        maxResults = 100;
        timeLimit = 1000;
        searchField = "uid=";
        searchCriteria = "*";
    }

    public void setTp(TolvenPerson tp) {
        this.tp = tp;
    }

    public String getRepeatUid() {
        return repeatUid;
    }

    public void setRepeatUid(String repeatUid) {
        this.repeatUid = repeatUid;
    }

    public String getOldUserPassword() {
        return oldUserPassword;
    }

    public void setOldUserPassword(String oldUserPassword) {
        this.oldUserPassword = oldUserPassword;
    }

    public String getNewUserPassword2() {
        return newUserPassword2;
    }

    public void setNewUserPassword2(String newUserPassword2) {
        this.newUserPassword2 = newUserPassword2;
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<TolvenPerson> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<TolvenPerson> searchResults) {
        this.searchResults = searchResults;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    public String getConfirmSecurityQuestionAnswer() {
        return confirmSecurityQuestionAnswer;
    }

    public void setConfirmSecurityQuestionAnswer(String confirmSecurityQuestionAnswer) {
        this.confirmSecurityQuestionAnswer = confirmSecurityQuestionAnswer;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNewUserPassword() {
        return newUserPassword;
    }

    public void setNewUserPassword(String newPassword) {
        this.newUserPassword = newPassword;
    }

    public void setActiveUserSecurityQuestions(List<SelectItem> activeUserSecurityQuestions) {
        this.activeUserSecurityQuestions = activeUserSecurityQuestions;
    }

    public PasswordRecoveryLocal getPasswordRecoveryBean() {
        if (passwordRecoveryBean == null) {
            try {
                passwordRecoveryBean = (PasswordRecoveryLocal) getContext().lookup("tolven/PasswordRecoveryBean/local");
            } catch (NamingException ex) {
                throw new RuntimeException("Could not lookup tolven/PasswordRecoveryBean/local", ex);
            }
        }
        return passwordRecoveryBean;
    }

    public void setPasswordRecoveryBean(PasswordRecoveryLocal passwordRecoveryBean) {
        this.passwordRecoveryBean = passwordRecoveryBean;
    }

    public List<SelectItem> getLoginSecurityQuestions() {
        List<SecurityQuestion> securityQuestions = getPasswordRecoveryBean().getSecurityQuestions(SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
        loginSecurityQuestions = new ArrayList<SelectItem>();
        loginSecurityQuestions.add(new SelectItem("", ""));
        for (SecurityQuestion securityQuestion : securityQuestions) {
            loginSecurityQuestions.add(new SelectItem(securityQuestion.getQuestion(), securityQuestion.getQuestion()));
        }
        return loginSecurityQuestions;
    }

    public void setLoginSecurityQuestions(List<SelectItem> loginSecurityQuestions) {
        this.loginSecurityQuestions = loginSecurityQuestions;
    }

    public List<SelectItem> getInactiveUserSecurityQuestions() {
        List<SecurityQuestion> securityQuestions = getPasswordRecoveryBean().getSecurityQuestions(SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
        inativeUserSecurityQuestions = new ArrayList<SelectItem>(1);
        inativeUserSecurityQuestions.add(new SelectItem("", ""));
        List<String> activeUserSecurityQuestions = new ArrayList<String>();
        String userLdapUID = getTp().getUid();
        List<PasswordRecovery> passwordRecoverys = getPasswordRecoveryBean().findPasswordRecovery(userLdapUID, SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
        for (PasswordRecovery passwordRecovery : passwordRecoverys) {
            activeUserSecurityQuestions.add(passwordRecovery.getSecurityQuestion());
        }
        for (SecurityQuestion securityQuestion : securityQuestions) {
            if (!activeUserSecurityQuestions.contains(securityQuestion.getQuestion())) {
                inativeUserSecurityQuestions.add(new SelectItem(securityQuestion.getQuestion(), securityQuestion.getQuestion()));
            }
        }
        inativeUserSecurityQuestions.removeAll(getActiveUserSecurityQuestions());
        return inativeUserSecurityQuestions;
    }

    public int getInactiveUserSecurityQuestionsCount() {
        return getInactiveUserSecurityQuestions().size() - 1;
    }

    public void setInactiveUserSecurityQuestions(List<SelectItem> inactiveUserSecurityQuestions) {
        this.inativeUserSecurityQuestions = inactiveUserSecurityQuestions;
    }

    public List<SelectItem> getActiveUserSecurityQuestions() {
        String userLdapUID = getTp().getUid();
        List<PasswordRecovery> passwordRecoverys = getPasswordRecoveryBean().findPasswordRecovery(userLdapUID, SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
        activeUserSecurityQuestions = new ArrayList<SelectItem>(passwordRecoverys.size());
        activeUserSecurityQuestions.add(new SelectItem("", ""));
        for (PasswordRecovery passwordRecovery : passwordRecoverys) {
            activeUserSecurityQuestions.add(new SelectItem(passwordRecovery.getSecurityQuestion(), passwordRecovery.getSecurityQuestion()));
        }
        return activeUserSecurityQuestions;
    }

    public int getActiveUserSecurityQuestionsCount() {
        return getActiveUserSecurityQuestions().size() - 1;
    }

    public String submitPasswordRequest() {
        boolean errorDetected = false;
        if (getOldUserPassword() == null || getOldUserPassword().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("password:oldUserPassword", new FacesMessage("The old password must be supplied"));
            errorDetected = true;
        }
        boolean changePassword = false;
        if ((getNewUserPassword() != null && getNewUserPassword().trim().length() > 0) || (getNewUserPassword2() != null && getNewUserPassword2().trim().length() > 0)) {
            if (getNewUserPassword() == null || getNewUserPassword().trim().length() == 0 || getNewUserPassword2() == null || getNewUserPassword2().trim().length() == 0 || !getNewUserPassword().equals(getNewUserPassword2())) {
                FacesContext.getCurrentInstance().addMessage("password:newUserPassword", new FacesMessage("Both passwords must match"));
                FacesContext.getCurrentInstance().addMessage("password:newUserPassword2", new FacesMessage("Both passwords must match"));
                errorDetected = true;
            }
            changePassword = true;
        }
        if (getSecurityQuestion() == null || getSecurityQuestion().trim().length() == 0) {
            if ((getSecurityQuestionAnswer() != null && getSecurityQuestionAnswer().trim().length() > 0) || (getConfirmSecurityQuestionAnswer() != null && getConfirmSecurityQuestionAnswer().trim().length() > 0)) {
                FacesContext.getCurrentInstance().addMessage("password:securityQuestion", new FacesMessage("A security question must be selected"));
                errorDetected = true;
            }
        } else {
            if (getSecurityQuestionAnswer() == null || getSecurityQuestionAnswer().trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage("password:securityQuestionAnswer", new FacesMessage("An answer must be supplied"));
                errorDetected = true;
            }
            if (getConfirmSecurityQuestionAnswer() == null || getConfirmSecurityQuestionAnswer().trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage("password:confirmSecurityQuestionAnswer", new FacesMessage("An answer must be confirmed"));
                errorDetected = true;
            }
            if (!getSecurityQuestionAnswer().equals(getConfirmSecurityQuestionAnswer())) {
                FacesContext.getCurrentInstance().addMessage("password:securityQuestionAnswer", new FacesMessage("The answer and its confirmation do not match"));
                FacesContext.getCurrentInstance().addMessage("password:confirmSecurityQuestionAnswer", new FacesMessage("The answer and its confirmation do not match"));
                errorDetected = true;
            }
        }
        if (!getLDAPLocal().verifyPassword(getTp().getUid(), getOldUserPassword().toCharArray())) {
            FacesContext.getCurrentInstance().addMessage("password:oldUserPassword", new FacesMessage("Incorrect password"));
            errorDetected = true;
        }
        if (errorDetected) {
            return "error";
        }
        if (changePassword) {
            if (getSecurityQuestion() == null || getSecurityQuestion().trim().length() == 0) {
                getPasswordRecoveryBean().changeUserLoginPassword(getTp(), getOldUserPassword().toCharArray(), getNewUserPassword().toCharArray());
            } else {
                getPasswordRecoveryBean().changeUserLoginPassword(getTp(), getOldUserPassword().toCharArray(), getNewUserPassword().toCharArray(), getSecurityQuestion(), getSecurityQuestionAnswer().toCharArray());
            }
        } else {
            getPasswordRecoveryBean().backupLoginPassword(getTp(), getOldUserPassword().toCharArray(), getSecurityQuestion(), getSecurityQuestionAnswer().toCharArray());
        }
        // Reset tolven person (so it picks up the new LDAP changes)
        setTp(null);
        if (changePassword) {
            FacesContext.getCurrentInstance().addMessage("password", new FacesMessage("Password updated. You need to Sign Out and Login in again for the change to have effect"));
        } else {
            if(getSecurityQuestion() == null) {
                FacesContext.getCurrentInstance().addMessage("password", new FacesMessage("Password recovery disabled"));
            } else {
                FacesContext.getCurrentInstance().addMessage("password", new FacesMessage("Password backed up"));
            }
        }
        return "success";
    }

    public String resetLoginPassword() {
        if (getUid() == null || getUid().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("resetLoginPassword:uid", new FacesMessage("A user id must be supplied"));
            return "error";
        }
        if (getSecurityQuestion() == null || getSecurityQuestion().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("resetLoginPassword:securityQuestion", new FacesMessage("A security question must be supplied"));
            return "error";
        }
        String securityQuestion = getSecurityQuestion();
        List<PasswordRecovery> passwordRecoverys = getPasswordRecoveryBean().findPasswordRecovery(getUid(), SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value(), getSecurityQuestion());
        if (passwordRecoverys.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("resetLoginPassword", new FacesMessage("Password recovery is not available for: " + getUid()));
            return "error";
        }
        if (getSecurityQuestionAnswer() == null || getSecurityQuestionAnswer().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("resetLoginPassword:securityQuestionAnswer", new FacesMessage("A security question answer must be supplied"));
            return "error";
        }
        String passwordAnswer = getSecurityQuestionAnswer();
        if (getNewUserPassword() == null || getNewUserPassword().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("resetLoginPassword:newUserPassword", new FacesMessage("A new password must be supplied"));
            return "error";
        }
        if (getNewUserPassword2() == null || getNewUserPassword2().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("resetLoginPassword:newUserPassword2", new FacesMessage("A new password must be confirmed"));
            return "error";
        }
        if (!getNewUserPassword().equals(getNewUserPassword2())) {
            FacesContext.getCurrentInstance().addMessage("resetLoginPassword:newUserPassword", new FacesMessage("The new password and its confirmation do not match"));
            FacesContext.getCurrentInstance().addMessage("resetLoginPassword:newUserPassword2", new FacesMessage("The new password and its confirmation do not match"));
            return "error";
        }
        String newPassword = getNewUserPassword();
        try {
            getPasswordRecoveryBean().resetLoginPassword(getUid(), securityQuestion, passwordAnswer.toCharArray(), newPassword.toCharArray());
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage("resetLoginPassword", new FacesMessage("Failed to reset password"));
            ex.printStackTrace(); // Create named exception so that the GUI can be informed correctly of the error
            return "error";
        }
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        return "success";
    }

    public String verifyPassword() {
        boolean error = false;

        if (getOldUserPassword() == null || getOldUserPassword().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("check:password", new FacesMessage("Password must be supplied"));
            error = true;
        }
        if (error)
            return "error";
        if (!getLDAPLocal().verifyPassword(getTp().getUid(), getOldUserPassword().toCharArray())) {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage("Invalid Password");
            context.addMessage("check:password", message);
            return "error";
        }
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        //	TolvenLogger.info(getClass() + " REGISTER ACTION :VESTIBULE_PASS=" + session.getAttribute(VestibuleSecurityFilter.VESTIBULE_PASS));
        session.setAttribute(GeneralSecurityFilter.VESTIBULE_PASS, "true");
        return "success";
    }

    /**
    * Search for matching names in LDAP. 
    */
    public String search() {
        long startTime = System.currentTimeMillis();
        setSearchResults(getLDAPLocal().search(getSearchField() + getSearchCriteria(), getMaxResults(), getTimeLimit()));
        double elapsed = (System.currentTimeMillis() - startTime);
        setElapsedTime(String.format("Elapsed: %.3f sec ", elapsed / 1000));
        return "success";
    }

    /*
     * Get a TolvenPerson. The first time called, we just create an empty TolvenPerson if no one is logged in, otherwise,
     * get a copy of the logged in person.
     */
    public TolvenPerson getTp() {
        if (getLDAPLocal() == null) {
            throw new NullPointerException("@EJB ldap = null");
        }
        if (tp == null) {
            Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
            if (principal != null) {
                tp = getLDAPLocal().createTolvenPerson(principal.getName());
                List<PasswordRecovery> passwordRecoverys = getPasswordRecoveryBean().findPasswordRecovery(tp.getUid(), SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
                if (!passwordRecoverys.isEmpty()) {
                    PasswordRecovery passwordRecovery = passwordRecoverys.get(0);
                    setSecurityQuestion(passwordRecovery.getSecurityQuestion());
                }
            } else {
                tp = new TolvenPerson();
            }
        }
        return tp;
    }

}
