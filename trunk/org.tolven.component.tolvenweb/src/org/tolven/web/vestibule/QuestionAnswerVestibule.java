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
 * @version $Id: QuestionAnswerVestibule.java 1009 2011-05-18 22:37:37Z joe.isaac $
 */
package org.tolven.web.vestibule;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.ServletRequest;

import org.tolven.web.security.AbstractVestibule;
import org.tolven.core.QuestionAnswerDAOLocal;
import org.tolven.core.entity.TolvenUser;

@ManagedBean(value = "org.tolven.web.vestibule.QuestionAnswerVestibule")
public class QuestionAnswerVestibule extends AbstractVestibule {

    public static String VESTIBULE_NAME = "org.tolven.vestibule.questionanswer";

    @EJB
    private QuestionAnswerDAOLocal questionAnswerBean = null;

    @Override
    public void abort(ServletRequest servletRequest) {
    }

    @Override
    public void enter(ServletRequest servletRequest) {
    }

    @Override
    public void exit(ServletRequest servletRequest) {
    }

    @Override
    public String getName() {
        return VESTIBULE_NAME;
    }

    @Override
    public String validate(ServletRequest servletRequest) {
        TolvenUser user = findPrincipalTolvenUser(servletRequest);
        if (questionAnswerBean.findUnansweredQuestions(user) > 0) {
            return "/vestibule/askQuestions.jsf";
        } else {
            return null;
        }
    }
}