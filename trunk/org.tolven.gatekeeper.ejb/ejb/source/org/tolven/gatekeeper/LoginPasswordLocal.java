package org.tolven.gatekeeper;

import java.util.Collection;

import javax.naming.AuthenticationException;

import org.tolven.gatekeeper.entity.SecurityQuestion;

public interface LoginPasswordLocal {

    /**
     * Backup a login password.
     * 
     * @param uid
     * @param password
     * @param realm
     * @param securityQuestion
     * @param securityQuestionAnswer
     * @return
     * @throws AuthenticationException 
     */
    public byte[] backupPassword(String uid, char[] password, String realm, String securityQuestion, char[] securityQuestionAnswer) throws AuthenticationException;
    
    /**
     * Change a login password.
     * 
     * @param uid
     * @param oldPassword
     * @param realm
     * @param newPassword
     * @param securityQuestion
     * @param securityAnswer
     * @throws AuthenticationException
     */
    public void changePassword(String uid, char[] oldPassword, String realm, char[] newPassword, String securityQuestion, char[] securityAnswer) throws AuthenticationException;

    /**
     * Find a login password security question.
     * 
     * @param uid
     * @param realm
     * @return
     */
    public String findActiveSecurityQuestion(String uid, String realm) throws AuthenticationException;;

    /**
     * Find all login password security questions
     * 
     * @param purpose
     * @return
     */
    public Collection<SecurityQuestion> findAllSecurityQuestions();

    /**
     * Recover a password.
     * 
     * @param uid
     * @param newPassword
     * @param realm
     * @param securityQuestion
     * @param securityQuestionAnswer
     * @throws AuthenticationException
     */
    public void recoverPassword(String uid, char[] newPassword, String realm, String securityQuestion, char[] securityQuestionAnswer) throws AuthenticationException;

    /**
     * Reset a password.
     * 
     * @param uid
     * @param realm
     * @param admin
     * @param adminPassword
     * @throws AuthenticationException
     */
    public char[] resetPassword(String uid, String realm, String admin, char[] adminPassword) throws AuthenticationException;
    
}
