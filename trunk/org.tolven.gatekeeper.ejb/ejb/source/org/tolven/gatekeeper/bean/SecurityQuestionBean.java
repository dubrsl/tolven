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
package org.tolven.gatekeeper.bean;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.gatekeeper.SecurityQuestionLocal;
import org.tolven.gatekeeper.entity.SecurityQuestion;

/**
 * 
 * @author Joseph Isaac
 * 
 * This class provides methods to manage user passwords
 *
 */
@Stateless
@Local(SecurityQuestionLocal.class)
public class SecurityQuestionBean implements SecurityQuestionLocal {

    @PersistenceContext
    private EntityManager em;

    /**
     * Add a SecurityQuestion
     * 
     * @param securityQuestion
     */
    @Override
    public void addSecurityQuestion(SecurityQuestion securtiyQuestion) {
        SecurityQuestion currentSecurityQuestion = findSecurityQuestion(securtiyQuestion.getQuestion(), securtiyQuestion.getPurpose());
        if (currentSecurityQuestion != null) {
            throw new RuntimeException("Question already exists with purpose: " + currentSecurityQuestion.getPurpose() + " and question: " + currentSecurityQuestion.getQuestion());
        }
        em.merge(securtiyQuestion);
    }

    /**
     * Add a collection of SecurityQuestions
     * 
     * @param securityQuestions
     */
    @Override
    public void addSecurityQuestions(Collection<SecurityQuestion> securtiyQuestions) {
        for (SecurityQuestion securityQuestion : securtiyQuestions) {
            addSecurityQuestion(securityQuestion);
        }
    }

    /**
     * Find the SecurityQuestion for a given purpose
     * 
     * @param question
     * @param purpose
     * @return
     */
    @Override
    public SecurityQuestion findSecurityQuestion(String question, String purpose) {
        String select = "SELECT sq FROM DefaultSecurityQuestion sq where sq.purpose = :purpose and sq.question = :question";
        Query query = em.createQuery(select);
        query.setParameter("question", question);
        query.setParameter("purpose", purpose);
        List<SecurityQuestion> sqs = query.getResultList();
        if (sqs.size() == 0) {
            return null;
        } else if (sqs.size() == 1) {
            return sqs.get(0);
        } else {
            throw new RuntimeException("More than one question found for purpose: " + purpose + " and question: " + question);
        }
    }

    /**
     * Find SecurityQuestions by purpose
     * 
     * @param purpose
     * @return
     */
    @Override
    public Collection<SecurityQuestion> findSecurityQuestions(String purpose) {
        String select = "SELECT sq FROM DefaultSecurityQuestion sq where sq.purpose = :purpose";
        Query query = em.createQuery(select);
        query.setParameter("purpose", purpose);
        return query.getResultList();
    }

    /**
     * Remove a SecurityQuestion for a given purpose
     * 
     * @param securityQuestion
     */
    @Override
    public void removeSecurityQuestion(String question, String purpose) {
        SecurityQuestion securityQuestion = findSecurityQuestion(question, purpose);
        if (securityQuestion == null) {
            throw new RuntimeException("Question does not exist with purpose: " + purpose + ": " + question);
        }
        em.remove(securityQuestion);
    }

    /**
     * Remove a collection of questions for a given purpose
     * 
     * @param securityQuestions
     */
    @Override
    public void removeSecurityQuestions(Collection<String> questions, String purpose) {
        for (String question : questions) {
            removeSecurityQuestion(question, purpose);
        }
    }

}
