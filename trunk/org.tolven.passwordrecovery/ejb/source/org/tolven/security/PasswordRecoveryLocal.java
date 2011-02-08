package org.tolven.security;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.naming.NamingException;

import org.tolven.security.entity.SecurityQuestion;
import org.tolven.security.entity.PasswordRecovery;

public interface PasswordRecoveryLocal {

    /**
     * Get SecurityQuestions by purpose
     * 
     * @param purpose
     * @return
     */
    public List<SecurityQuestion> getSecurityQuestions(String purpose);

    /**
     * Get the SecurityQuestion with question and purpose
     * 
     * @param question
     * @param purpose
     * @return
     */
    public List<SecurityQuestion> getSecurityQuestions(String question, String purpose);

    /**
     * Add a List of SecurityQuestions
     * 
     * @param securityQuestions
     */
    public void addSecurityQuestions(List<SecurityQuestion> securityQuestions);

    /**
     * Change a SecurityQuestion which already exists. Useful to typos, but care should be used if the question is known to already be in use
     * 
     * @param securityQuestion
     */
    public void changeSecurityQuestion(SecurityQuestion securityQuestion, String newQuestion);
    
    /**
     * Remove a List of SecurityQuestions
     * 
     * @param securityQuestions
     */
    public void removeSecurityQuestions(List<SecurityQuestion> securityQuestions);
    
    public List<PasswordRecovery> findPasswordRecovery(String userLdapUID, String passwordPurpose);

    public List<PasswordRecovery> findPasswordRecovery(String userLdapUID, String passwordPurpose, String securityQuestion);

    public void resetLoginPassword(String userLdapUID, String securityQuestion, char[] securityQuestionAnswer, char[] newPassword) throws GeneralSecurityException, IOException, CharacterCodingException, NamingException;

    public void changeUserLoginPassword(TolvenPerson tolvenPerson, char[] oldPassword, char[] newPassword);
    
    public void changeUserLoginPassword(TolvenPerson tolvenPerson, char[] oldPassword, char[] newPassword, String securityQuestion, char[] securityAnswer);

    public byte[] backupLoginPassword(TolvenPerson tolvenPerson, char[] password, String securityQuestion, char[] securityQuestionAnswer);
}
