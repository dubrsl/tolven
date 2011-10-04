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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.exeption.GatekeeperAuthenticationException;
import org.tolven.exeption.GatekeeperAuthorizationException;
import org.tolven.exeption.GatekeeperSecurityException;
import org.tolven.gatekeeper.LdapLocal;
import org.tolven.gatekeeper.LoginPasswordLocal;
import org.tolven.gatekeeper.SecurityQuestionLocal;
import org.tolven.gatekeeper.entity.DefaultPasswordBackup;
import org.tolven.gatekeeper.entity.PasswordBackup;
import org.tolven.gatekeeper.entity.SecurityQuestion;
import org.tolven.gatekeeper.entity.SecurityQuestionPurpose;

/**
 * 
 * @author Joseph Isaac
 * 
 * This class provides methods to manage user passwords
 *
 */
@Stateless
@Local(LoginPasswordLocal.class)
public class LoginPasswordBean implements LoginPasswordLocal {

    private static final String PBE_KEY_ALGORITHM = "PBEWithMD5AndDES";
    private static SecureRandom rng = new SecureRandom();

    private static final String USER_PASSWORD_ITERATION_COUNT = "20";
    private static final String USER_PASSWORD_SALT_LENGTH = "8";

    @PersistenceContext
    private EntityManager em;

    @EJB
    private LdapLocal ldapBean;
    
    @EJB
    private SecurityQuestionLocal securityQuestionBean;

    //TODO PassowrdMgrBean now works with gatekeeper, properties need to be loaded to gatekeeper
    //@EJB
    //private TolvenPropertiesLocal propertiesBean;

    /**
     * Backup a login password.
     * 
     * @param uid
     * @param password
     * @param realm
     * @param securityQuestion
     * @param securityQuestionAnswer
     * @return
     */
    @Override
    public byte[] backupPassword(String uid, char[] password, String realm, String securityQuestion, char[] securityQuestionAnswer) {
        boolean passwordVerified = getLdapBean().verifyPassword(uid, password, realm);
        if (!passwordVerified) {
            throw new GatekeeperAuthenticationException("Backup password for: " + uid, uid, realm);
        }
        if (securityQuestion == null && securityQuestionAnswer != null) {
            throw new RuntimeException("Failed to backup password for: " + uid + " in realm: " + realm + ". A security question is provided without an answer");
        }
        PasswordBackup passwordBackup = findPasswordBackup(uid, realm);
        if (passwordBackup != null) {
            em.remove(passwordBackup);
        }
        if (securityQuestion == null) {
            return null;
        }
        try {
            //String pbeKeyAlgorithm = propertiesBean.getProperty(PBE_KEY_ALGORITHM_PROP);
            String pbeKeyAlgorithm = PBE_KEY_ALGORITHM;
            byte[] salt = getRandomSalt();
            //int iterationCount = Integer.parseInt(propertiesBean.getProperty(USER_PASSWORD_ITERATION_COUNT_PROP));
            int iterationCount = Integer.parseInt(USER_PASSWORD_ITERATION_COUNT);
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
            } catch (Exception ex) {
                throw new RuntimeException("Could not not convert password from char[] to bytes", ex);
            }
            byte[] encryptedPassword = cipher.doFinal(baos.toByteArray());
            storeEncryptedPassword(encryptedPassword, salt, iterationCount, uid, realm, securityQuestion);
            return encryptedPassword;
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException("Could not backup password for: " + uid, ex);
        }
    }
    
    /**
     * Change a login password.
     * 
     * @param uid
     * @param oldPassword
     * @param realm
     * @param newPassword
     * @param securityQuestion
     * @param securityAnswer
     */
    @Override
    public void changePassword(String uid, char[] oldPassword, String realm, char[] newPassword, String securityQuestion, char[] securityAnswer) {
        if (securityQuestion == null && securityAnswer != null || securityQuestion != null && securityAnswer == null) {
            throw new RuntimeException("A securtiy question needs to be paired with a security answer");
        }
        try {
            getLdapBean().changeUserPassword(uid, oldPassword, realm, newPassword);
        } catch (GatekeeperSecurityException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to change login password for: " + uid + " in realm: " + realm, ex);
        }
        try {
            backupPassword(uid, newPassword, realm, securityQuestion, securityAnswer);
        } catch (Exception ex) {
            if (securityQuestion == null) {
                throw new RuntimeException("Changed password, but failed to remove backup password for: " + uid + " in realm: " + realm, ex);
            } else {
                throw new RuntimeException("Changed password, but failed to backup password for: " + uid + " in realm: " + realm, ex);
            }
        }
    }

    private PasswordBackup findPasswordBackup(String uid, String realm) {
        String select = "SELECT p FROM DefaultPasswordBackup p WHERE p.passwordPurpose = :passwordPurpose and p.userId = :userId and p.realm = :realm";
        Query query = em.createQuery(select);
        query.setParameter("passwordPurpose", SecurityQuestionPurpose.LOGIN_PASSWORD_BACKUP.value());
        query.setParameter("userId", uid);
        query.setParameter("realm", realm);
        List<PasswordBackup> passwordBackups = query.getResultList();
        if (passwordBackups.size() == 0) {
            return null;
        } else if (passwordBackups.size() == 1) {
            return passwordBackups.get(0);
        } else {
            throw new RuntimeException("Found more than one backup login password for user: " + uid + " in realm: " + realm);
        }
    }

    /**
     * Find a login password security question.
     * 
     * @param uid
     * @param realm
     * @return
     */
    @Override
    public String findActiveSecurityQuestion(String uid, String realm) {
        String select = "SELECT p FROM DefaultPasswordBackup p WHERE p.passwordPurpose = :passwordPurpose and p.userId = :userId and p.realm = :realm";
        Query query = em.createQuery(select);
        query.setParameter("passwordPurpose", SecurityQuestionPurpose.LOGIN_PASSWORD_BACKUP.value());
        query.setParameter("userId", uid);
        query.setParameter("realm", realm);
        List<PasswordBackup> passwordBackups = query.getResultList();
        if (passwordBackups.size() == 0) {
            return null;
        } else if (passwordBackups.size() == 1) {
            return passwordBackups.get(0).getSecurityQuestion();
        } else {
            throw new RuntimeException("Found more than one backup login password for user: " + uid + " in realm: " + realm);
        }
    }

    /**
     * Find all login password security questions
     * 
     * @param purpose
     * @return
     */
    @Override
    public Collection<SecurityQuestion> findAllSecurityQuestions() {
        return getSecurityQuestionBean().findSecurityQuestions(SecurityQuestionPurpose.LOGIN_PASSWORD_BACKUP.value());
    }

    public LdapLocal getLdapBean() {
        return ldapBean;
    }

    /**
     * Return a randome salt byte[]
     * 
     * @return
     */
    private byte[] getRandomSalt() {
        //byte[] salt = new byte[Integer.parseInt(propertiesBean.getProperty(USER_PASSWORD_SALT_LENGTH_PROP))];
        byte[] salt = new byte[Integer.parseInt(USER_PASSWORD_SALT_LENGTH)];
        rng.nextBytes(salt);
        return salt;
    }

    public SecurityQuestionLocal getSecurityQuestionBean() {
        return securityQuestionBean;
    }

    private char[] recoverLoginPassword(String uid, String realm, String securityQuestion, char[] securityQuestionAnswer, String passwordPurpose) {
        try {
            PasswordBackup passwordBackup = findPasswordBackup(uid, realm);
            if (passwordBackup == null) {
                throw new GatekeeperAuthorizationException("Recover login password for: " + passwordPurpose + " using security question: '" + securityQuestion + "'", uid, realm);
            }
            //String pbeKeyAlgorithm = propertiesBean.getProperty(PBE_KEY_ALGORITHM_PROP);
            String pbeKeyAlgorithm = PBE_KEY_ALGORITHM;
            byte[] salt = passwordBackup.getSalt();
            int iterationCount = passwordBackup.getIterationCount();
            PBEKeySpec pbeKeySpec = new PBEKeySpec(securityQuestionAnswer);
            SecretKey secretKey = SecretKeyFactory.getInstance(pbeKeyAlgorithm).generateSecret(pbeKeySpec);
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, iterationCount);
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParamSpec);
            byte[] password = cipher.doFinal(passwordBackup.getEncryptedPassword());
            ByteBuffer byteBuffer = ByteBuffer.wrap(password);
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            return decoder.decode(byteBuffer).array();
        } catch (GatekeeperSecurityException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to recover login password");
        }
    }

    /**
     * Recover a password.
     * 
     * @param uid
     * @param newPassword
     * @param realm
     * @param securityQuestion
     * @param securityQuestionAnswer
     */
    @Override
    public void recoverPassword(String uid, char[] newPassword, String realm, String securityQuestion, char[] securityQuestionAnswer) {
        char[] recoveredPassword = recoverLoginPassword(uid, realm, securityQuestion, securityQuestionAnswer, SecurityQuestionPurpose.LOGIN_PASSWORD_BACKUP.value());
        getLdapBean().changeUserPassword(uid, recoveredPassword, realm, newPassword);
        backupPassword(uid, newPassword, realm, securityQuestion, securityQuestionAnswer);
    }

    /**
     * Reset a password.
     * 
     * @param uid
     * @param realm
     * @param admin
     * @param adminPassword
     */
    @Override
    public char[] resetPassword(String uid, String realm, String admin, char[] adminPassword) {
        return getLdapBean().resetUserPassword(uid, realm, admin, adminPassword);
    }

    private void storeEncryptedPassword(byte[] encryptedPassword, byte[] salt, int iterationCount, String uid, String realm, String securityQuestion) {
        PasswordBackup currentPasswordBackup = findPasswordBackup(uid, realm);
        if (currentPasswordBackup != null) {
            em.remove(currentPasswordBackup);
        }
        PasswordBackup passwordBackup = new DefaultPasswordBackup();
        passwordBackup.setEncryptedPassword(encryptedPassword);
        passwordBackup.setSalt(salt);
        passwordBackup.setIterationCount(iterationCount);
        passwordBackup.setUserId(uid);
        passwordBackup.setRealm(realm);
        passwordBackup.setSecurityQuestion(securityQuestion);
        passwordBackup.setPasswordPurpose(SecurityQuestionPurpose.LOGIN_PASSWORD_BACKUP.value());
        passwordBackup.setCreation(new Date());
        em.persist(passwordBackup);
    }

}
