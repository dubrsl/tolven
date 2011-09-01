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
package org.tolven.security.password;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.CharBuffer;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.tolven.security.password.bean.*;

/**
 * This class encapsulates different types of passwords for use by client objects.
 * 
 * @author Joseph Isaac
 *
 */
public final class PasswordHolder {

    public static final String CREDENTIAL_PASSWORD = "Credential";
    public static final String PRIVATE_KEY_PASSWORD = "PrivateKey";
    public static final String KEYSTORE_PASSWORD = "KeyStore";
    public static final String TRUSTSTORE_PASSWORD = "TrustStore";
    public static final String SERVER_PASSWORD = "Server";
    public static final String KEYSTORE_FORMAT = "jks";

    private String adminGroupId;
    private File keyStoreFile;
    private File secretKeyFile;
    private File passwordStoreFile;

    transient private KeyStore keyStore;
    transient private SecretKey secretKey;
    transient private Map<String, PasswordInfo> passwordMap;
    transient private Map<String, String> deprecatedCredentialIdMap;

    public PasswordHolder() {
        setPasswordMap(new HashMap<String, PasswordInfo>());
    }

    public void init(String adminGroupId, char[] adminPassword, File keyStoreFile, File secretKeyFile, File passwordStoreFile) {
        setAdminGroupId(adminGroupId);
        setKeyStoreFile(keyStoreFile);
        setSecretKeyFile(secretKeyFile);
        setPasswordStoreFile(passwordStoreFile);
        setAdminPassword(adminPassword);
    }

    public String getAdminGroupId() {
        return adminGroupId;
    }

    public void setAdminGroupId(String adminGroupId) {
        this.adminGroupId = adminGroupId;
    }

    public File getKeyStoreFile() {
        return keyStoreFile;
    }

    public void setKeyStoreFile(File keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    public File getSecretKeyFile() {
        return secretKeyFile;
    }

    public void setSecretKeyFile(File secretKeyFile) {
        this.secretKeyFile = secretKeyFile;
    }

    public File getPasswordStoreFile() {
        return passwordStoreFile;
    }

    public void setPasswordStoreFile(File passwordStoreFile) {
        this.passwordStoreFile = passwordStoreFile;
    }

    private KeyStore getKeyStore() {
        if (keyStore == null) {
            try {
                keyStore = KeyStore.getInstance(KEYSTORE_FORMAT);
                FileInputStream in = null;
                try {
                    in = new FileInputStream(getKeyStoreFile());
                    getKeyStore().load(in, getAdminPassword());
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException("Could not load keystore from " + getKeyStoreFile().getPath());
            }
        }
        return keyStore;
    }

    private Map<String, PasswordInfo> getPasswordMap() {
        return passwordMap;
    }

    private void setPasswordMap(HashMap<String, PasswordInfo> passwordMap) {
        this.passwordMap = passwordMap;
    }

    public void addPassword(PasswordInfo passwordInfo, char[] password, boolean replace) {
        if (password == null) {
            throw new RuntimeException("Password required for password Ref ID: " + passwordInfo.getRefId());
        }
        if (!replace) {
            PasswordInfo existingPasswordInfo = getPasswordInfo(passwordInfo.getRefId());
            if (existingPasswordInfo != null) {
                if (!CharBuffer.wrap(existingPasswordInfo.getPassword()).equals(CharBuffer.wrap(password))) {
                    throw new RuntimeException("PasswordInfo with id '" + passwordInfo.getRefId() + "' already exists.\nIt can be changed by supplying the old password.");
                } else {
                    /*
                     * No need to add, since the password has not changed
                     */
                    return;
                }
            }
        }
        passwordInfo.setPassword(password);
        getPasswordMap().put(passwordInfo.getRefId(), passwordInfo);
    }

    public void addPassword(String refId, char[] password) {
        addPassword(refId, CREDENTIAL_PASSWORD, password);
    }

    public void addPassword(String refId, String type, char[] password) {
        PasswordInfo passwordInfo = new PasswordInfo(refId, type);
        addPassword(passwordInfo, password, false);
    }

    public void addPasswords(Map<PasswordInfo, char[]> inputPasswordInfoMap) {
        for (PasswordInfo passwordInfo : inputPasswordInfoMap.keySet()) {
            addPassword(passwordInfo, inputPasswordInfoMap.get(passwordInfo), false);
        }
    }

    public void clearAllPasswords() {
        getPasswordMap().clear();
    }

    public void clearPasswords(List<PasswordInfo> passwordInfos) {
        String refId = null;
        for (PasswordInfo passwordInfo : passwordInfos) {
            refId = passwordInfo.getRefId();
            if (!refId.equals(getAdminGroupId())) {
                clearPassword(refId);
            }
        }
    }

    public void clearPassword(String refId) {
        getPasswordMap().remove(refId);
    }

    public void changePassword(PasswordInfo passwordInfo, char[] oldPassword, char[] newPassword) {
        char[] storedPassword = getPassword(passwordInfo.getRefId());
        if (storedPassword != null) {
            if (oldPassword == null || !CharBuffer.wrap(oldPassword).equals(CharBuffer.wrap(storedPassword))) {
                throw new RuntimeException("Old password does not match new password for: " + passwordInfo.getRefId());
            }
        }
        addPassword(passwordInfo, newPassword, true);
    }

    public char[] getAdminPassword() {
        PasswordInfo passwordInfo = getPasswordInfo(getAdminGroupId());
        if (passwordInfo == null) {
            return null;
        } else {
            return getPassword(passwordInfo.getRefId());
        }
    }

    private Passwords getCurrentPasswords(boolean includeAdmin) {
        ObjectFactory objectFactory = new ObjectFactory();
        Passwords passwords = objectFactory.createPasswords();
        PasswordDetail passwordDetail = null;
        PasswordInfo passwordInfo = null;
        if (includeAdmin) {
            passwordInfo = getPasswordInfo(getAdminGroupId());
            passwordDetail = objectFactory.createPasswordDetail();
            updatePasswordDetail(passwordDetail, passwordInfo);
            passwords.getPasswordInfo().add(passwordDetail);
        }
        for (String key : getPasswordMap().keySet()) {
            if (!getAdminGroupId().equals(key)) {
                passwordInfo = getPasswordInfo(key);
                passwordDetail = objectFactory.createPasswordDetail();
                updatePasswordDetail(passwordDetail, passwordInfo);
                passwords.getPasswordInfo().add(passwordDetail);
            }
        }
        return passwords;
    }

    /**
     * Return the Password, if the caller has the access token
     * @param passwordId
     * @param token
     * @return
     */
    public char[] getPassword(String refId) {
        PasswordInfo passwordInfo = getPasswordInfo(refId);
        if (passwordInfo != null) {
            return passwordInfo.getPassword();
        } else {
            return null;
        }
    }

    public List<String> getRefIds() {
        return new ArrayList<String>(getPasswordMap().keySet());
    }

    /**
     * Return the Password, if the caller has the access token
     * @param passwordId
     * @param token
     * @return
     */
    public PasswordInfo getPasswordInfo(String refId) {
        return (PasswordInfo) getPasswordMap().get(refId);
    }

    private SecretKey getSecretKey() {
        return secretKey;
    }

    public boolean hasPassword(String refId) {
        return getPasswordInfo(refId) != null;
    }

    private void updatePasswordDetail(PasswordDetail passwordDetail, PasswordInfo passwordInfo) {
        passwordDetail.setRefId(passwordInfo.getRefId());
        passwordDetail.setType(passwordInfo.getType());
        //TODO Passwords should not be converted to strings?
        passwordDetail.setPassword(new String(getPassword(passwordInfo.getRefId())));
    }

    public boolean verify(PasswordInfo passwordInfo, char[] password) {
        if (password == null) {
            return getPassword(passwordInfo.getRefId()) == null;
        } else {
            return getPassword(passwordInfo.getRefId()) != null && CharBuffer.wrap(password).equals(CharBuffer.wrap(getPassword(passwordInfo.getRefId())));
        }
    }

    public void createPasswordStore() {
        generateSecretKey(getSecretKeyFile());
        store();
    }

    public void loadPasswordStore() {
        if (!getKeyStoreFile().exists()) {
            throw new RuntimeException("Could not find admin keystore file: " + getKeyStoreFile().getPath());
        }
        if (!getSecretKeyFile().exists()) {
            throw new RuntimeException("Could not find admin secretkey file: " + getSecretKeyFile().getPath());
        }
        if (!getPasswordStoreFile().exists()) {
            throw new RuntimeException("Could not find admin passwordstore file: " + getPasswordStoreFile().getPath());
        }
        setPasswordStoreFile(getPasswordStoreFile());
        try {
            loadSecretKey(getSecretKeyFile(), getAdminPassword());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not load encrypted SecretKey " + getSecretKeyFile().getPath() + "\n" + ex.getMessage());
        }
        try {
            Passwords passwords = loadPasswords();
            PasswordInfo loadedPasswordInfo = null;
            boolean passwordUpdateRequired = false;
            for (PasswordDetail passwordDetail : passwords.getPasswordInfo()) {
                String passwordKey = passwordDetail.getRefId();
                String passwordType = passwordDetail.getType();
                if (PRIVATE_KEY_PASSWORD.equals(passwordType) || KEYSTORE_PASSWORD.equals(passwordType)) {
                    passwordKey = getCredentialGroupId(passwordKey);
                    passwordType = CREDENTIAL_PASSWORD;
                    passwordUpdateRequired = true;
                } else if (TRUSTSTORE_PASSWORD.equals(passwordType)) {
                    passwordKey = getCredentialGroupId(passwordKey);
                    passwordType = CREDENTIAL_PASSWORD;
                    passwordUpdateRequired = true;
                }
                loadedPasswordInfo = new PasswordInfo(passwordKey, passwordType);
                addPassword(loadedPasswordInfo, passwordDetail.getPassword().toCharArray(), true);
            }
            if (passwordUpdateRequired) {
                store();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not load password store " + getPasswordStoreFile().getPath());
        }
    }

    public void setDeprecatedCredentialIdMap(Map<String, String> map) {
        deprecatedCredentialIdMap = map;
    }

    private String getCredentialGroupId(String deprecatedCredentialId) {
        if (deprecatedCredentialIdMap == null) {
            return deprecatedCredentialId;
        }
        String credentialId = deprecatedCredentialIdMap.get(deprecatedCredentialId);
        if (credentialId == null) {
            return deprecatedCredentialId;
        } else {
            return credentialId;
        }
    }

    private void setAdminPassword(char[] password) {
        PasswordInfo passwordInfo = new PasswordInfo(getAdminGroupId(), CREDENTIAL_PASSWORD);
        addPassword(passwordInfo, password, false);
    }

    private Passwords loadPasswords() {
        FileInputStream in = null;
        ByteArrayInputStream bais = null;
        try {
            try {
                Passwords passwords = null;
                if (getPasswordStoreFile().exists()) {
                    in = new FileInputStream(getPasswordStoreFile());
                    byte[] encryptedPasswords = new byte[in.available()];
                    in.read(encryptedPasswords);
                    Cipher cipher = Cipher.getInstance(getSecretKey().getAlgorithm());
                    cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
                    byte[] decryptedPasswords = cipher.doFinal(encryptedPasswords);
                    bais = new ByteArrayInputStream(decryptedPasswords);
                    JAXBContext jc = JAXBContext.newInstance("org.tolven.security.password.bean", getClass().getClassLoader());
                    Unmarshaller unmarshaller = jc.createUnmarshaller();
                    passwords = (Passwords) unmarshaller.unmarshal(bais);
                } else {
                    passwords = new ObjectFactory().createPasswords();
                }
                return passwords;
            } finally {
                if (bais != null)
                    bais.close();
                if (in != null)
                    in.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not load passwords from: " + getPasswordStoreFile());
        }
    }

    private void loadSecretKey(File encryptedSecretKeyFile, char[] password) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream in = null;
            try {
                in = new FileInputStream(encryptedSecretKeyFile);
                byte[] bytes = new byte[1024];
                int n = 0;
                while ((n = in.read(bytes)) != -1) {
                    baos.write(bytes, 0, n);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            byte[] encryptedSecretKey = Base64.decodeBase64(baos.toByteArray());
            String alias = getKeyStore().aliases().nextElement();
            Key key = getKeyStore().getKey(alias, password);
            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.UNWRAP_MODE, key);
            secretKey = (SecretKey) cipher.unwrap(encryptedSecretKey, "DESede", Cipher.SECRET_KEY);
        } catch (Exception ex) {
            throw new RuntimeException("Could not load secret key from " + encryptedSecretKeyFile.getPath(), ex);
        }
    }

    private void generateSecretKey(File secretKeyFile) {
        if (getSecretKeyFile().exists()) {
            throw new RuntimeException("A secretkey file already exists at: " + getSecretKeyFile().getPath());
        }
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
            keyGenerator.init(112);
            secretKey = keyGenerator.generateKey();
            String alias = getKeyStore().aliases().nextElement();
            Certificate adminCert = getKeyStore().getCertificate(alias);
            PublicKey publicKey = adminCert.getPublicKey();
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.WRAP_MODE, publicKey);
            byte[] encryptedSecretKey = cipher.wrap(secretKey);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(secretKeyFile);
                out.write(Base64.encodeBase64(encryptedSecretKey));
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not generate secret key for file: " + secretKeyFile.getPath(), ex);
        }
    }

    public boolean isPasswordStoreUpToDate() {
        Passwords initialPasswords = loadPasswords();
        HashMap<String, PasswordInfo> passwordMapCopy = new HashMap<String, PasswordInfo>(getPasswordMap());
        passwordMapCopy.remove(getAdminGroupId());
        if (passwordMapCopy.size() != initialPasswords.getPasswordInfo().size()) {
            return false;
        }
        char[] password = null;
        PasswordInfo passwordInfo = null;
        for (PasswordDetail passwordDetail : initialPasswords.getPasswordInfo()) {
            passwordInfo = passwordMapCopy.get(passwordDetail.getRefId());
            if (passwordInfo == null) {
                return false;
            }
            password = getPassword(passwordInfo.getRefId());
            CharBuffer passwordBuff = CharBuffer.wrap(password);
            if (!passwordBuff.toString().equals(passwordDetail.getPassword()))
                return false;
        }
        return true;
    }

    public void store() {
        FileOutputStream out = null;
        ByteArrayOutputStream baos = null;
        try {
            try {
                JAXBContext jc = JAXBContext.newInstance("org.tolven.security.password.bean", getClass().getClassLoader());
                Marshaller marshaller = jc.createMarshaller();
                baos = new ByteArrayOutputStream();
                marshaller.marshal(getCurrentPasswords(false), baos);
                Cipher cipher = Cipher.getInstance(getSecretKey().getAlgorithm());
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
                byte[] encryptedPasswords = cipher.doFinal(baos.toByteArray());
                out = new FileOutputStream(getPasswordStoreFile());
                out.write(encryptedPasswords);
            } finally {
                if (baos != null)
                    baos.close();
                if (out != null)
                    out.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not update the password store " + getPasswordStoreFile().getPath(), ex);
        }
    }

}
