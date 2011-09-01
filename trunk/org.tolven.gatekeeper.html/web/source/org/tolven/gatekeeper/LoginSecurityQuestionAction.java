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

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;

import org.tolven.gatekeeper.entity.SecurityQuestion;
import org.tolven.gatekeeper.entity.SecurityQuestionFactory;
import org.tolven.gatekeeper.entity.SecurityQuestionPurpose;

/**
 * Faces Action for manging security questions.
 * 
 * @author Joseph Isaac
 */
public class LoginSecurityQuestionAction {

    private List<SecurityQuestion> currentSecurityQuestions;
    private String questions;

    @EJB
    private SecurityQuestionLocal securityQuestionBean;

    public LoginSecurityQuestionAction() {
    }

    public String addLoginSecurityQuestions() {
        List<SecurityQuestion> securityQuestions = new ArrayList<SecurityQuestion>();
        for (String question : getQuestions(getQuestions())) {
            SecurityQuestion securityQuestion = SecurityQuestionFactory.getInstance();
            securityQuestion.setQuestion(question);
            securityQuestion.setPurpose(SecurityQuestionPurpose.LOGIN_PASSWORD_BACKUP.value());
            securityQuestions.add(securityQuestion);
        }
        if (!securityQuestions.isEmpty()) {
            getSecurityQuestionBean().addSecurityQuestions(securityQuestions);
        }
        return "success";
    }

    public List<SecurityQuestion> getCurrentSecurityQuestions() {
        if (currentSecurityQuestions == null) {
            currentSecurityQuestions = (List<SecurityQuestion>) getSecurityQuestionBean().findSecurityQuestions(SecurityQuestionPurpose.LOGIN_PASSWORD_BACKUP.value());
            Comparator<SecurityQuestion> comparator = new Comparator<SecurityQuestion>() {
                public int compare(SecurityQuestion sq1, SecurityQuestion sq2) {
                    return (sq1.getQuestion().compareTo(sq2.getQuestion()));
                };
            };
            Collections.sort(currentSecurityQuestions, comparator);
        }
        return currentSecurityQuestions;
    }

    public String getQuestions() {
        return questions;
    }

    public SecurityQuestionLocal getSecurityQuestionBean() {
        return securityQuestionBean;
    }

    private List<String> getQuestions(String questionsString) {
        BufferedReader reader = new BufferedReader(new StringReader(questionsString));
        List<String> questions = new ArrayList<String>();
        String question = null;
        do {
            try {
                question = reader.readLine();
            } catch (Exception ex) {
                throw new RuntimeException("Could not read secucrity question", ex);
            }
            if (question != null && question.trim().length() > 0) {
                questions.add(question);
            }
        } while (question != null);
        return questions;
    }

    public String navigateHome() {
        return "navigateHome";
    }

    public String removeLoginSecurityQuestions() {
        List<String> questionsToRemove = getQuestions(getQuestions());

        if (!questionsToRemove.isEmpty()) {
            getSecurityQuestionBean().removeSecurityQuestions(questionsToRemove, SecurityQuestionPurpose.LOGIN_PASSWORD_BACKUP.value());
        }
        return "success";
    }

    public void setCurrentSecurityQuestions(List<SecurityQuestion> currentSecurityQuestions) {
        this.currentSecurityQuestions = currentSecurityQuestions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

}
