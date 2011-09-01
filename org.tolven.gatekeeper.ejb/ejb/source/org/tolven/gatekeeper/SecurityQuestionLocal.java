package org.tolven.gatekeeper;

import java.util.Collection;

import org.tolven.gatekeeper.entity.SecurityQuestion;

public interface SecurityQuestionLocal {

    /**
     * Add a SecurityQuestion
     * 
     * @param securityQuestion
     */
    public void addSecurityQuestion(SecurityQuestion securityQuestion);

    /**
     * Add a collection of SecurityQuestions
     * 
     * @param securityQuestions
     */
    public void addSecurityQuestions(Collection<SecurityQuestion> securityQuestions);

    /**
     * Find the SecurityQuestion for a given purpose
     * 
     * @param question
     * @param purpose
     * @return
     */
    public SecurityQuestion findSecurityQuestion(String question, String purpose);

    /**
     * Find SecurityQuestions by purpose
     * 
     * @param purpose
     * @return
     */
    public Collection<SecurityQuestion> findSecurityQuestions(String purpose);

    /**
     * Remove a SecurityQuestion for a given purpose
     * 
     * @param question
     * @param purpose
     */
    public void removeSecurityQuestion(String question, String purpose);

    /**
     * Remove a collection of questions for a given purpose
     * 
     * @param questions
     * @param purpose
     */
    public void removeSecurityQuestions(Collection<String> questions, String purpose);

}
