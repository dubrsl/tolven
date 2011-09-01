package org.tolven.gatekeeper;

import java.util.Collection;

import javax.naming.AuthenticationException;

import org.tolven.gatekeeper.entity.SecurityQuestion;

public interface LoginPasswordLocal {

    /**
     * Backup a login password.
     * 
     * @param uid
     * @param realm
     * @param password
     * @param securityQuestion
     * @param securityQuestionAnswer
     * @return
     * @throws AuthenticationException 
     */
    public byte[] backupPassword(String uid, String realm, char[] password, String securityQuestion, char[] securityQuestionAnswer) throws AuthenticationException;
    
    /**
     * Change a login password.
     * 
     * @param uid
     * @param realm
     * @param oldPassword
     * @param newPassword
     * @param securityQuestion
     * @param securityAnswer
     * @throws AuthenticationException
     */
    public void changePassword(String uid, String realm, char[] oldPassword, char[] newPassword, String securityQuestion, char[] securityAnswer) throws AuthenticationException;

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
     * @param realm
     * @param newPassword
     * @param securityQuestion
     * @param securityQuestionAnswer
     * @throws AuthenticationException
     */
    public void recoverPassword(String uid, String realm, char[] newPassword, String securityQuestion, char[] securityQuestionAnswer) throws AuthenticationException;

    /**
     * Reset a password.
     * 
     * @param uid
     * @param realm
     * @param newPassword
     * @param admin
     * @param adminPassword
     * @throws AuthenticationException
     */
    public void resetPassword(String uid, String realm, char[] newPassword, String admin, char[] adminPassword) throws AuthenticationException;
}
