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
package org.tolven.passwordrecovery.model;

/**
 * This class wraps tolven system question information
 * 
 * @author Joseph Isaac
 *
 */
public class PasswordRecoveryQuestionInfo {


    public static final int REQUIRES_REFRESH = 0;
    public static final int UPTODATE = 1;
    public static final int OUTOFDATE = 2;
    public static final int ADDPENDING = 3;
    public static final int UPDATEPENDING = 4;
    public static final int REMOVEPENDING = 5;
    public static final int ERROR = 6;

    private String info;
    private SecurityQuestion securityQuestion;
    private int status = REQUIRES_REFRESH;

    public PasswordRecoveryQuestionInfo(SecurityQuestion securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getDefaultStatusString() {
        if (REQUIRES_REFRESH == getStatus()) {
            return "Requires refresh";
        } else if (UPTODATE == getStatus()) {
            return "Up to date";
        } else if (OUTOFDATE == getStatus()) {
            return "Out of date";
        } else if (ADDPENDING == getStatus()) {
            return "Add pending";
        } else if (UPDATEPENDING == getStatus()) {
            return "Modified, awaiting update";
        } else if (REMOVEPENDING == status) {
            return "delete pending";
        } else if (ERROR == status) {
            return getInfo();
        } else {
            throw new RuntimeException("Unknown state: " + getStatus());
        }
    }

    public String getInfo() {
        return info;
    }

    public String getPurposeAndQuestion() {
        return getSecurityQuestion().getPurposeAndQuestion();
    }

    public String getQuestion() {
        if(getSecurityQuestion() == null) {
            return null;
        } else {
            return getSecurityQuestion().getQuestion();
        }
    }

    public SecurityQuestion getSecurityQuestion() {
        return securityQuestion;
    }

    public int getStatus() {
        return status;
    }

    public String getValue() {
        if (securityQuestion == null) {
            return null;
        } else {
            return securityQuestion.getQuestion();
        }
    }

    public boolean isNew() {
        return getSecurityQuestion().getId() == null;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setSecurityQuestion(SecurityQuestion securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setValue(String value) {
        if (securityQuestion != null) {
            securityQuestion.setQuestion(value);
        }
    }

}
