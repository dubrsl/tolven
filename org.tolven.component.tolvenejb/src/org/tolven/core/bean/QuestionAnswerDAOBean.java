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
package org.tolven.core.bean;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.core.QuestionAnswerDAOLocal;
import org.tolven.core.entity.Question;
import org.tolven.core.entity.QuestionAnswer;
import org.tolven.core.entity.TolvenUser;
import org.tolven.core.entity.TolvenUserAnswer;

/**
 * This is the bean class for the QuestionAnswerDAOBean enterprise bean.
 * Created May 23, 2007
 * @author Sashikanth Vema
 */
@Stateless()
@Local(QuestionAnswerDAOLocal.class)
public class QuestionAnswerDAOBean implements QuestionAnswerDAOLocal {
	
	@PersistenceContext private EntityManager em;

	/**
     * Create a new question.
     * @param strQuestion, type
     * @return A new Question object
     */
    public Question createQuestion( String strQuestion, String type ) {
    	
        Question question = new Question();
        question.setQuestion( strQuestion );
        question.setType( type );
        em.persist( question );
        
        return question;
        
    } // createQuestion()

    /**
     * Stick the updated question back in the database.
     * @param account
     */
    public void updateQuestion( Question question ) {
    	
    	em.merge(question);
    	
    } // updateQuestion()

	/**
     * Create a new answer for an existing question.
     * @param question, strAnswer
     * @return A new QuestionAnswer object
     */
    public QuestionAnswer createAnswer( Question question, String strAnswer ) {
    	
        QuestionAnswer answer = new QuestionAnswer();
        answer.setQuestion( question );
        answer.setAnswer( strAnswer );
        em.persist( answer );
        
        return answer;
        
    } // createAnswer()

    /**
     * Find a question given the question id.
     * @param questionId
     * @return the question object
     */
    public Question findQuestion(long questionId ) {
    	
    	return em.find( Question.class, questionId );
    	
    } // findQuestion()

	/**
     * Find a answer given the answer id.
     * @param answer Id
     * @return the answer object
     */
    public QuestionAnswer findAnswer(long answerId ) {
    	
    	return em.find( QuestionAnswer.class, answerId ); 
    	
    } // findAnswer()

    /**
     * Get all the questions
     * @param 
     * @return list of question objects
     */
    public List<Question> getQuestions() {
    	
       	Query q = em.createQuery("FROM Question");
     	List<Question> rslt = q.getResultList();
     	
     	return rslt;

    } // getQuestions()

    /**
     * Find a question by question string
     * @param 
     * @return question object
     */
    public Question findQuestionByString(String strQuestion) {
    	
       	Query q = em.createQuery("FROM Question where question = :question");
       	q.setParameter("question", strQuestion);
       	List<Question> questionList = q.getResultList();
       	if(questionList.size() == 1)
       		return questionList.get(0);
       	else
       		return null;

    } // findQuestionByString()

    /**
     * Find a question id by a given answer id
     * @param answer id
     * @return question object
     */
    public Question findQuestionByAnswerId(String strAnswerId) {

       	Query q = em.createQuery("FROM Answer where answer_id = :answer_id");
       	q.setParameter("answer_id", strAnswerId);
       	List<Question> questionList = q.getResultList();
       	if(questionList.size() == 1)
       		return questionList.get(0);
       	else
       		return null;

    } // findQuestionByAnswerId()
    
    /**
     * Delete TolvenUserAnswer entities for this Tolven User
     * @param tolven user id
     * @return question object
     */
    public int deleteTolvenUserAnswers( TolvenUser tolvenUser ) {

    	Query query = em.createQuery("DELETE FROM TolvenUserAnswer WHERE tolvenUser = :tolvenUser");
    	query.setParameter( "tolvenUser", tolvenUser );
    	return query.executeUpdate();
    	
    } // deleteTolvenUserAnswers()
    
	/**
     * Create a new answer for a tolven user.
     * @param tolven user, question, answer
     * @return A new TolvenUserAnswer object
     */
    public TolvenUserAnswer createTolvenUserAnswer( TolvenUser tolvenUser, QuestionAnswer answer ) {
    	
        TolvenUserAnswer tolvenUserAnswer = new TolvenUserAnswer();
        tolvenUserAnswer.setTolvenUser( tolvenUser );
        tolvenUserAnswer.setQuestion( answer.getQuestion() );
        tolvenUserAnswer.setAnswer( answer );
        em.persist( tolvenUserAnswer );
        return tolvenUserAnswer;
        
    } // createTolvenUserAnswer()

	/**
     * Find a tolven user answer by question
     * @param tolvenUser, question
     * @return A List of TolvenUserAnswer objects
     */
    public List<TolvenUserAnswer> findTolvenUserAnswerByQuestion( TolvenUser tolvenUser, Question question ) {
    	
       	Query q = em.createQuery("FROM TolvenUserAnswer where tolvenUser = :tolvenUser and question = :question");
       	q.setParameter("tolvenUser", tolvenUser);
       	q.setParameter("question", question);
       	return q.getResultList();
    	    	
    } // findTolvenUserAnswerByQuestion()

	/**
     * Find if there are any unanswered questions by the user
     * Returns a count of unanswered questions
     * @param tolvenUser
     * @return int
     */
    public int findUnansweredQuestions( TolvenUser tolvenUser ) {
    	
    	Query q1 = em.createQuery("SELECT count(q) FROM Question q WHERE q.id NOT IN " +
    			"(SELECT DISTINCT tua.question.id FROM TolvenUserAnswer tua WHERE tua.tolvenUser = :tolvenUser)");
    	q1.setParameter("tolvenUser", tolvenUser);
    	
    	return ((Long) q1.getSingleResult()).intValue();
    	
    } // findUnansweredQuestions()

	/**
     * Get a list of unanswered questions
     * @param tolvenUser
     * @return boolean
     */
    public List<Question> getUnansweredQuestions( TolvenUser tolvenUser ) {
    	
    	Query q = em.createQuery("SELECT q FROM Question q WHERE q.id NOT IN " +
    			"(SELECT DISTINCT tua.question.id FROM TolvenUserAnswer tua WHERE tua.tolvenUser = :tolvenUser)");
    	q.setParameter("tolvenUser", tolvenUser);
    	
    	List<Question> listUnansweredQuestions = q.getResultList();
    	return listUnansweredQuestions;
    	
    } // getUnansweredQuestions()
    
 } // class QuestionAnswerDAOBean
 	

   
