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
package org.tolven.passwordrecovery.api;

import java.io.File;
import java.util.List;

import org.tolven.passwordrecovery.model.LoadSecurityQuestions;
import org.tolven.passwordrecovery.model.PasswordRecoveryQuestionInfo;

public class SecurityQuestionsImpl {

    private LoadSecurityQuestions loadSecurityQuestions;

    public SecurityQuestionsImpl(String appRestfulURL, String authRestfulURL, String userId, char[] password) {
        setLoadSecurityQuestions(new LoadSecurityQuestions(userId, password, appRestfulURL, authRestfulURL));
    }

    public LoadSecurityQuestions getLoadSecurityQuestions() {
        return loadSecurityQuestions;
    }

    public void setLoadSecurityQuestions(LoadSecurityQuestions loadSecurityQuestions) {
        this.loadSecurityQuestions = loadSecurityQuestions;
    }

    public void displaySecurityQuestions() {
        try {
            getLoadSecurityQuestions().displaySecurityQuestions();
        } catch (Exception ex) {
            throw new RuntimeException("Could not display security questions for: ", ex);
        } finally {
            getLoadSecurityQuestions().logout();
        }
    }

    public void importSecurityQuestions(File securityQuestionFile) {
        try {
            getLoadSecurityQuestions().importSecurityQuestions(securityQuestionFile);
        } catch (Exception ex) {
            throw new RuntimeException("Could not import security questions from " + securityQuestionFile.getPath() + " for: ", ex);
        } finally {
            getLoadSecurityQuestions().logout();
        }
    }

    public void addSecurityQuestion(String securityQuestionString) {
        try {
            getLoadSecurityQuestions().addSecurityQuestion(securityQuestionString);
        } catch (Exception ex) {
            throw new RuntimeException("Could not add security question: " + securityQuestionString + " for: ", ex);
        } finally {
            getLoadSecurityQuestions().logout();
        }
    }

    public void changeSecurityQuestion(String currentSecurityQuestionString, String newSecurityQuestionString) {
        try {
            getLoadSecurityQuestions().changeSecurityQuestion(currentSecurityQuestionString, newSecurityQuestionString);
        } catch (Exception ex) {
            throw new RuntimeException("Could not change security question: " + currentSecurityQuestionString + " for: ", ex);
        } finally {
            getLoadSecurityQuestions().logout();
        }
    }

    public PasswordRecoveryQuestionInfo addPasswordRecoveryQuestionInfo(String securityQuestionString) {
        try {
            PasswordRecoveryQuestionInfo passwordRecoveryQuestionInfo = getLoadSecurityQuestions().addPasswordRecoveryQuestionInfo(securityQuestionString);
            return passwordRecoveryQuestionInfo;
        } catch (Exception ex) {
            throw new RuntimeException("Could not add security question: " + securityQuestionString + " for: ", ex);
        } finally {
            getLoadSecurityQuestions().logout();
        }
    }

    public void removeSecurityQuestion(String securityQuestionString) {
        try {
            getLoadSecurityQuestions().removeSecurityQuestion(securityQuestionString);
        } catch (Exception ex) {
            throw new RuntimeException("Could not remove security question: " + securityQuestionString + " for: ", ex);
        } finally {
            getLoadSecurityQuestions().logout();
        }
    }

    public List<PasswordRecoveryQuestionInfo> getPasswordRecoveryQuestionInfos() {
        try {
            List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfos = getLoadSecurityQuestions().getPasswordRecoveryQuestionInfos();
            return passwordRecoveryQuestionInfos;
        } catch (Exception ex) {
            throw new RuntimeException("Could not get security question infos for: ", ex);
        } finally {
            getLoadSecurityQuestions().logout();
        }
    }

    public void update(List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfos) {
        try {
            getLoadSecurityQuestions().writePasswordRecoveryQuestionInfos(passwordRecoveryQuestionInfos);
        } catch (Exception ex) {
            throw new RuntimeException("Could not update security questions for: ", ex);
        } finally {
            getLoadSecurityQuestions().logout();
        }
    }

    public List<PasswordRecoveryQuestionInfo> importPasswordRecoveryQuestionInfos(File securityQuestionFile) {
        try {
            List<PasswordRecoveryQuestionInfo> passwordRecoveryQuestionInfos = getLoadSecurityQuestions().importPasswordRecoveryQuestionInfos(securityQuestionFile);
            return passwordRecoveryQuestionInfos;
        } catch (Exception ex) {
            throw new RuntimeException("Could not import security question infos from " + securityQuestionFile.getPath() + " for: ", ex);
        } finally {
            getLoadSecurityQuestions().logout();
        }
    }

    public void exportPasswordRecoveryQuestionInfos(File questionsFile) {
        try {
            getLoadSecurityQuestions().exportPasswordRecoveryQuestionInfos(questionsFile);
        } catch (Exception ex) {
            throw new RuntimeException("Could not export security question infos to " + questionsFile.getPath() + " for: ", ex);
        } finally {
            getLoadSecurityQuestions().logout();
        }
    }

}
