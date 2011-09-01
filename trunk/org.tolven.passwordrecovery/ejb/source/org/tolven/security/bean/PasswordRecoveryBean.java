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
package org.tolven.security.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Status;
import org.tolven.core.entity.TolvenUser;
import org.tolven.security.LDAPLocal;
import org.tolven.security.PasswordRecoveryLocal;
import org.tolven.security.TolvenPerson;
import org.tolven.security.entity.PasswordRecovery;
import org.tolven.security.entity.SecurityQuestion;
import org.tolven.security.entity.SecurityQuestionPurpose;

/**
 * 
 * @author Joseph Isaac
 * 
 * This class provides methods to manage user passwords
 *
 */
@Stateless(mappedName = "tolven/PasswordRecoveryBean/remote")
@Local(PasswordRecoveryLocal.class)
public class PasswordRecoveryBean implements PasswordRecoveryLocal {

    public static final String PBE_KEY_ALGORITHM_PROP = "tolven.security.user.pbeKeyAlgorithm";
    public static final String USER_PASSWORD_SALT_LENGTH_PROP = "tolven.security.user.passwordSaltLength";
    public static final String USER_PASSWORD_ITERATION_COUNT_PROP = "tolven.security.user.passwordIterationCount";

    public static SecureRandom rng = new SecureRandom();

    @PersistenceContext
    private EntityManager em;

    @EJB
    private LDAPLocal ldapBean;
    @EJB
    private TolvenPropertiesLocal propertiesBean;

    private LDAPLocal getLDAPBean() {
        return ldapBean;
    }

    /**
     * Backup password using securityQuestionAnswer for userLdapUID
     */
    private byte[] backupPassword(String userLdapUID, char[] password, String passwordPurpose, String securityQuestion, char[] securityQuestionAnswer) {
        try {
            String pbeKeyAlgorithm = propertiesBean.getProperty(PBE_KEY_ALGORITHM_PROP);
            byte[] salt = getRandomSalt();
            int iterationCount = Integer.parseInt(propertiesBean.getProperty(USER_PASSWORD_ITERATION_COUNT_PROP));
            PBEKeySpec pbeKeySpec = new PBEKeySpec(securityQuestionAnswer);
            SecretKey secretKey = SecretKeyFactory.getInstance(pbeKeyAlgorithm).generateSecret(pbeKeySpec);
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, iterationCount);
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParamSpec);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter outputStreamWriter = null;
            try {
                try {
                    outputStreamWriter = new OutputStreamWriter(baos, Charset.forName("UTF-8"));
                    outputStreamWriter.write(password, 0, password.length);
                } finally {
                    outputStreamWriter.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException("Could not not convert password from char[] to bytes", ex);
            }
            byte[] encryptedPassword = cipher.doFinal(baos.toByteArray());
            storeEncryptedPassword(encryptedPassword, salt, iterationCount, userLdapUID, securityQuestion, passwordPurpose);
            return encryptedPassword;
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException("Could not backup password for: " + userLdapUID, ex);
        }
    }

    /**
     * Get SecurityQuestions by purpose
     * 
     * @param purpose
     * @return
     */
    @Override
    public List<SecurityQuestion> getSecurityQuestions(String purpose) {
        String select = "SELECT sq FROM SecurityQuestion sq where sq.purpose = :purpose";
        Query query = em.createQuery(select);
        query.setParameter("purpose", purpose);
        return query.getResultList();
    }

    /**
     * Get the SecurityQuestion with question and purpose
     * 
     * @param question
     * @param purpose
     * @return
     */
    @Override
    public List<SecurityQuestion> getSecurityQuestions(String question, String purpose) {
        String select = "SELECT sq FROM SecurityQuestion sq where sq.purpose = :purpose and sq.question = :question";
        Query query = em.createQuery(select);
        query.setParameter("question", question);
        query.setParameter("purpose", purpose);
        return query.getResultList();
    }

    public List<PasswordRecovery> findPasswordRecovery(String userLdapUID, String passwordPurpose) {
        return findPasswordRecovery(userLdapUID, passwordPurpose, null);
    }

    public List<PasswordRecovery> findPasswordRecovery(String userLdapUID, String passwordPurpose, String securityQuestion) {
        TolvenUser user = findUser(userLdapUID);
        if (user == null) {
            return new ArrayList<PasswordRecovery>();
        }
        String select = null;
        if (securityQuestion == null) {
            select = "SELECT p FROM PasswordRecovery p WHERE p.passwordPurpose = :passwordPurpose and p.userId = :userId and p.status = :status";
        } else {
            select = "SELECT p FROM PasswordRecovery p WHERE p.securityQuestion = :securityQuestion and p.passwordPurpose = :passwordPurpose and p.userId = :userId and p.status = :status";
        }
        Query query = em.createQuery(select);
        if (securityQuestion != null) {
            query.setParameter("securityQuestion", securityQuestion);
        }
        query.setParameter("passwordPurpose", passwordPurpose);
        query.setParameter("userId", user.getId());
        query.setParameter("status", Status.ACTIVE.value());
        return (List<PasswordRecovery>) query.getResultList();
    }

    /**
     * Given the principal's name, get the TolvenUser object. the parameter must be converted to lower case to ensure we find a match.
     */
    private TolvenUser findUser(String principal) {
        //Support both types of active status;     
        String activeStatus = Status.ACTIVE.value();
        String oldActiveStatus = Status.OLD_ACTIVE.value();
        //Activating should be replaced by New
        String activatingStatus = Status.fromValue("ACTIVATING").value();
        String newStatus = Status.NEW.value();
        String select = "SELECT  u FROM TolvenUser u WHERE u.ldapUID = :principal " + "and ( u.status = '";
        select += oldActiveStatus + "' or u.status = '" + activeStatus + "' or u.status = '" + newStatus + "' or u.status = '" + activatingStatus + "' or u.status = '" + "') ";
        Query query = em.createQuery(select);
        query.setParameter("principal", principal.toLowerCase());
        query.setMaxResults(2);
        List<TolvenUser> items = query.getResultList();
        if (items.size() != 1)
            return null;
        return items.get(0);
    }

    /**
     * Return a randome salt byte[]
     * 
     * @return
     */
    private byte[] getRandomSalt() {
        byte[] salt = new byte[Integer.parseInt(propertiesBean.getProperty(USER_PASSWORD_SALT_LENGTH_PROP))];
        rng.nextBytes(salt);
        return salt;
    }

    public void resetLoginPassword(String userLdapUID, String securityQuestion, char[] securityQuestionAnswer, char[] newPassword) throws GeneralSecurityException, IOException, CharacterCodingException, NamingException {
        TolvenUser user = findUser(userLdapUID);
        if (user == null) {
            // It is intentional not to let users know exactly which is incorrect
            throw new RuntimeException("Invalid user name or security answer.");
        }
        char[] recoveredPassword = recoverPassword(SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value(), userLdapUID, securityQuestion, securityQuestionAnswer);
        if (recoveredPassword == null) {
            // It is intentional not to let users know exactly which is incorrect
            throw new RuntimeException("Invalid user name or security answer.");
        }
        /*
         * Authenticate the user. Failure results in an authentication exception.
         */
        getLDAPBean().authenticate(user.getLdapUID(), recoveredPassword);
        getLDAPBean().changeUserPassword(userLdapUID, recoveredPassword, newPassword);
        backupPassword(userLdapUID, newPassword, SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value(), securityQuestion, securityQuestionAnswer);
    }

    /**
     * Change the user's login password and remove any previous backup for that password
     * 
     * @param tolvenPerson
     * @param oldPassword
     * @param newPassword
     */
    public void changeUserLoginPassword(TolvenPerson tolvenPerson, char[] oldPassword, char[] newPassword) {
        changeUserLoginPassword(tolvenPerson, oldPassword, newPassword, null, null);
    }

    /**\
     * Change the user's password and backup the password using the securityAnswer, if it is supplied
     * 
     * @param tolvenPerson
     * @param oldPassword
     * @param newPassword
     * @param securityQuestion
     * @param securityAnswer
     */
    public void changeUserLoginPassword(TolvenPerson tolvenPerson, char[] oldPassword, char[] newPassword, String securityQuestion, char[] securityAnswer) {
        if (securityQuestion == null && securityAnswer != null || securityQuestion != null && securityAnswer == null) {
            throw new RuntimeException("A securtiy question needs to be paired with a security answer");
        }
        try {
            getLDAPBean().changeUserPassword(tolvenPerson, oldPassword, newPassword);
        } catch (Exception ex) {
            throw new RuntimeException("Failed changing password for: " + tolvenPerson.getUid(), ex);
        }
        try {
            backupLoginPassword(tolvenPerson, newPassword, securityQuestion, securityAnswer);
        } catch (Exception ex) {
            if (securityQuestion == null) {
                throw new RuntimeException("Failed to remove backup password for: " + tolvenPerson.getUid(), ex);
            } else {
                throw new RuntimeException("Failed to backup password for: " + tolvenPerson.getUid(), ex);
            }
        }
    }

    public byte[] backupLoginPassword(TolvenPerson tolvenPerson, char[] password, String securityQuestion, char[] securityQuestionAnswer) {
        List<PasswordRecovery> passwordRecoverys = findPasswordRecovery(tolvenPerson.getUid(), SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
        for (PasswordRecovery passwordRecovery : passwordRecoverys) {
            if ("active".equals(passwordRecovery.getStatus())) {
                try {
                    deactivatePasswordRecovery(tolvenPerson.getUid(), passwordRecovery.getSecurityQuestion(), SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value());
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to deactivate password recovery for: " + tolvenPerson.getUid(), ex);
                }
            }
        }
        if (securityQuestion == null) {
            return null;
        } else {
            return backupPassword(tolvenPerson.getUid(), password, SecurityQuestionPurpose.LOGIN_PASSWORD_RECOVERY.value(), securityQuestion, securityQuestionAnswer);
        }
    }

    private char[] recoverPassword(String passwordPurpose, String userLdapUID, String securityQuestion, char[] securityQuestionAnswer) throws GeneralSecurityException, CharacterCodingException {
        List<PasswordRecovery> passwordRecoverys = findPasswordRecovery(userLdapUID, passwordPurpose, securityQuestion);
        if (passwordRecoverys.isEmpty()) {
            throw new RuntimeException("No password recovery available for '" + passwordPurpose + "' using security question: '" + securityQuestion + "'");
        }
        PasswordRecovery passwordRecovery = passwordRecoverys.get(0);
        String pbeKeyAlgorithm = propertiesBean.getProperty(PBE_KEY_ALGORITHM_PROP);
        byte[] salt = passwordRecovery.getSalt();
        int iterationCount = passwordRecovery.getIterationCount();
        PBEKeySpec pbeKeySpec = new PBEKeySpec(securityQuestionAnswer);
        SecretKey secretKey = SecretKeyFactory.getInstance(pbeKeyAlgorithm).generateSecret(pbeKeySpec);
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, iterationCount);
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParamSpec);
        byte[] password = cipher.doFinal(passwordRecovery.getEncryptedPassword());
        ByteBuffer byteBuffer = ByteBuffer.wrap(password);
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        return decoder.decode(byteBuffer).array();
    }

    public void deactivatePasswordRecovery(String userLdapUID, String securityQuestion, String passwordPurpose) {
        List<PasswordRecovery> passwordRecoverys = findPasswordRecovery(userLdapUID, passwordPurpose, securityQuestion);
        for (PasswordRecovery passwordRecovery : passwordRecoverys) {
            passwordRecovery.setStatus(Status.INACTIVE.value());
            em.persist(passwordRecovery);
        }
    }

    private void storeEncryptedPassword(byte[] encryptedPassword, byte[] salt, int iterationCount, String userLdapUID, String securityQuestion, String passwordPurpose) {
        TolvenUser user = findUser(userLdapUID);
        List<PasswordRecovery> passwordRecoverys = findPasswordRecovery(userLdapUID, passwordPurpose, securityQuestion);
        for (PasswordRecovery passwordRecovery : passwordRecoverys) {
            passwordRecovery.setStatus(Status.INACTIVE.value());
            em.persist(passwordRecovery);
        }
        PasswordRecovery passwordRecovery = new PasswordRecovery();
        passwordRecovery.setEncryptedPassword(encryptedPassword);
        passwordRecovery.setSalt(salt);
        passwordRecovery.setIterationCount(iterationCount);
        passwordRecovery.setUserId(user.getId());
        passwordRecovery.setSecurityQuestion(securityQuestion);
        passwordRecovery.setPasswordPurpose(passwordPurpose);
        passwordRecovery.setStatus(Status.ACTIVE.value());
        passwordRecovery.setCreation(new Date());
        em.persist(passwordRecovery);
    }

    /**
     * Add a List of SecurityQuestions
     * 
     * @param securityQuestions
     */
    @Override
    //@RolesAllowed("tolvenAdmin")
    public void addSecurityQuestions(List<SecurityQuestion> securityQuestions) {
        for (SecurityQuestion securityQuestion : securityQuestions) {
            if (getSecurityQuestions(securityQuestion.getQuestion(), securityQuestion.getPurpose()).isEmpty()) {
                em.merge(securityQuestion);
            }
        }
    }

    /**
     * Change a SecurityQuestion
     * 
     * @param securityQuestion
     */
    @Override
    //@RolesAllowed("tolvenAdmin")
    public void changeSecurityQuestion(SecurityQuestion securityQuestion, String newQuestion) {
        List<SecurityQuestion> securityQuestions = getSecurityQuestions(securityQuestion.getQuestion(), securityQuestion.getPurpose());
        if (securityQuestions.isEmpty()) {
            throw new RuntimeException("Could not find question: " + securityQuestion.getQuestion() + " with purpose: " + securityQuestion.getPurpose());
        } else if (securityQuestions.size() > 1) {
            throw new RuntimeException("Found more than one question: " + securityQuestion.getQuestion() + " with purpose: " + securityQuestion.getPurpose());
        } else {
            securityQuestions.get(0).setQuestion(newQuestion);
            em.merge(securityQuestions.get(0));
        }
    }

    /**
     * Remove a List of SecurityQuestions
     * 
     * @param securityQuestions
     */
    @Override
    //@RolesAllowed("tolvenAdmin")
    public void removeSecurityQuestions(List<SecurityQuestion> securityQuestions) {
        for (SecurityQuestion securityQuestion : securityQuestions) {
            if (getSecurityQuestions(securityQuestion.getQuestion(), securityQuestion.getPurpose()).isEmpty()) {
                em.remove(securityQuestion);
            }
        }
    }

}
