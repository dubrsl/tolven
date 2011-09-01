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

package org.tolven.core;

import java.util.List;

import org.tolven.core.entity.Question;
import org.tolven.core.entity.QuestionAnswer;
import org.tolven.core.entity.TolvenUser;
import org.tolven.core.entity.TolvenUserAnswer;


/**
 * Services used to manage questions given during initial activation and the answers provided by individual users.
 */
public interface QuestionAnswerDAOLocal {
	
	/**
     * Create a new question.
     * @param strQuestion, type
     * @return A new Question object
     */
	public Question createQuestion( String strQuestion, String type );
    
	/**
     * Create a new answer for an existing question.
     * @param question, strAnswer
     * @return A new QuestionAnswer object
     */
	public QuestionAnswer createAnswer( Question question, String strAnswer );

    /**
     * Stick the updated question back in the database.
     * @param account
     */
	public void updateQuestion( Question question );
    
	/**
     * Find a question given the question id.
     * @param questionId
     * @return the question object
     */
    public Question findQuestion(long questionId );

	/**
     * Find a answer given the answer id.
     * @param answer Id
     * @return the answer object
     */
    public QuestionAnswer findAnswer(long answerId );
    
    /**
     * Get all the questions
     * @param 
     * @return list of question objects
     */
    public List<Question> getQuestions();
    
    /**
     * Find a question by question string
     * @param 
     * @return list of question objects
     */
    public Question findQuestionByString(String strQuestion);
    
    /**
     * Delete TolvenUserAnswer entities for this Tolven User
     * @param tolven user id
     * @return question object
     */
    public int deleteTolvenUserAnswers( TolvenUser tolvenUser );
    
	/**
     * Create a new answer for a tolven user.
     * @param tolven user, answer
     * @return A new TolvenUserAnswer object
     */
    public TolvenUserAnswer createTolvenUserAnswer( TolvenUser tolvenUser, QuestionAnswer answer );

	/**
     * Find a tolven user answer by question
     * @param tolvenUser, question
     * @return A list of TolvenUserAnswer objects
     */
    public List<TolvenUserAnswer> findTolvenUserAnswerByQuestion( TolvenUser tolvenUser, Question question );

	/**
     * Find if there are any unanswered questions by the user
     * Returns a count of unanswered questions
     * @param tolvenUser
     * @return int
     */
    public int findUnansweredQuestions( TolvenUser tolvenUser );

	/**
     * Get a list of unanswered questions
     * @param tolvenUser
     * @return boolean
     */
    public List<Question> getUnansweredQuestions( TolvenUser tolvenUser );
    
} // class QuestionAnswerDAOLocal
