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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import org.tolven.core.entity.Question;
import org.tolven.core.entity.QuestionAnswer;
import org.tolven.core.entity.QuestionType;
import org.tolven.core.entity.TolvenUser;

/**
 * Faces Action bean concerned with questions-answer process.
 * @author Sashikanth Vema
 */
public class QuestionsAction extends TolvenAction {


    private List<UserAnswers> listUIUnansweredQuestions;
    
    /** Creates a new instance of QuestionsAction 
     * @throws NamingException */
    public QuestionsAction() throws NamingException {
        // J2EE 1.5 has not yet defined exact XML <ejb-ref> syntax for EJB3
        //TODO Injection does not work for JBoss (v4.0.4GA) web tier (tomcat), but does for GlassFish

    } // constructor

    /*
     * This method returns a list of unanswered questions, corresponding answers, and user selected answers (dummy placeholders).
     * The list returned from this method is a list of UserAnswers instances.
     * Each UserAnswers instance will hold a question object, corresponding list of valid answers as SelectItem(s),
     * and a placeholder list for selected answers
     */
	public List<UserAnswers> getUnansweredQuestions() throws NamingException {
		
		if(listUIUnansweredQuestions == null) {

			listUIUnansweredQuestions = new ArrayList<UserAnswers>();
			TolvenUser user = getActivationBean().findTolvenUser(getSessionTolvenUserId());
			
			for(Question q : getQuestionAnswerBean().getUnansweredQuestions(user)) {
				
			   	List<SelectItem> listAnswers = new ArrayList<SelectItem>();
			   	
			   	if(q.getType().equalsIgnoreCase(QuestionType.ONELIST.value()))
			   		listAnswers.add(new SelectItem("NA", "Select One"));
			   	
		    	for(QuestionAnswer a : q.getAnswers())
		    		listAnswers.add(new SelectItem("" + a.getId(), a.getAnswer()));
		    	
		    	List<String> listUserAnswers = new ArrayList<String>();
		    	// Dummy default value
	    		listUserAnswers.add("");
	
		    	// User Answers stored in UserAnswers bean (Inner class)
		    	listUIUnansweredQuestions.add(new UserAnswers(q, listAnswers, listUserAnswers));
		    
			} // for - questions
			
		} // if (listUIQuestions == null)
	
    	return listUIUnansweredQuestions;
    	
	} // getUnansweredQuestions()
	
    /**
     * <p>Updates the selected answers for the current user.</p> 
     * <p>First a check is made to make sure that user has answered all the unanswered questions, 
     * if not returning with an error message so that the same page is displayed for the user.</p>
     * <p>If user has answered all the unanswered questions, they are stored in TolvenUserAnswer entity.</p>
     */
	public String updateAnswers() throws NamingException {
		
		//TolvenLogger.info("##### Entered updateAnswers()...", QuestionsAction.class);
		Map<String, String[]> requestParamsMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
		Set<String> keySet = requestParamsMap.keySet();
		int i = 0;
		for(String strKey : keySet) {
			if(strKey.indexOf("questionsForm:questions") != -1 && !requestParamsMap.get(strKey)[0].equals("NA")) {
				//TolvenLogger.info(strKey + " = " + requestParamsMap.get(strKey), QuestionsAction.class);
				i++;
			}
		}

		TolvenUser user = getActivationBean().findTolvenUser(getSessionTolvenUserId());
		
		if(i != getQuestionAnswerBean().findUnansweredQuestions(user)) {
			
			FacesMessage facesMessage = new FacesMessage();
			facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			facesMessage.setSummary("All the questions need to be answered");
			facesMessage.setDetail("Before proceeding further, all the questions need to be answered.  " +
					"If any of the answers need to be changed later, that can be done by updating the preferences");
			FacesContext.getCurrentInstance().addMessage( "questionsForm:questions", facesMessage);
			
			//TolvenLogger.info("##### Exiting updateAnswers() with error...", QuestionsAction.class);
			return "error";
			
		} else {
			
			for(String strKey : keySet) {
				if(strKey.indexOf("questionsForm:questions") != -1) {
					String[] answers = requestParamsMap.get(strKey);
					for(String strAnswer : answers) {
						if(strAnswer.equalsIgnoreCase("NA"))
							continue;
						long answerId = Long.valueOf( strAnswer );
						QuestionAnswer answer = getQuestionAnswerBean().findAnswer( answerId );
						getQuestionAnswerBean().createTolvenUserAnswer( user, answer );
					} // for answers
				} // if answers found in request
			} // for each key
			
		} // if all questions are answered or not
		
		//TolvenLogger.info("##### Exiting updateAnswers() with success...", QuestionsAction.class);
		return "success";
		
	} // updateAnswers()

	// Inner class to hold the values sent to the UI.
	// Stores user answers (used in /vestibule/askQuestions.xhtml)
	public class UserAnswers {
		List<String> userAnswers;
		Question question;
		List<SelectItem> answers;
		public UserAnswers(Question question, List<SelectItem> answers, List<String> userAnswers) {
			this.question = question;
			this.answers = answers;
			setUserAnswers(userAnswers);
		}
		public void setUserAnswers(List<String> userAnswers) {
			this.userAnswers = userAnswers;
		}
		public List<String> getUserAnswers() {
			return userAnswers;
		}
		public String getType() {
			return this.question.getType();
		}
		public String getQuestion() {
			return this.question.getQuestion();
		}
		public void setAnswers(List<SelectItem> answers) {
			this.answers = answers;
		}
		public List<SelectItem> getAnswers() {
			return this.answers;
		}
	} // class UserAnswers

} // class QuestionsAction
