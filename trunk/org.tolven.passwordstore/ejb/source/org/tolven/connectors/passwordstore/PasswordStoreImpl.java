/*
 * Copyright 2003-2006 Sun Microsystems, Inc.  All Rights Reserved.

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author Joseph Isaac
 * @version $Id$
 */
package org.tolven.connectors.passwordstore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * An interface to a password based on an alias for a password, where the alias is associated with an encrypted password.
 * Security is currently based on providing access only to classes running in the same JVM.
 * 
 * @author Joseph Isaac
 *
 */
class PasswordStoreImpl implements PasswordStore {

    private EncryptedPasswordStore encryptedPasswordStore;

    private transient KeyStore keyStore;
    private transient char[] keyStorePassword;
    private transient Map<String, char[]> passwordStore;

    PasswordStoreImpl(KeyStore keyStore, char[] keyStorePassword) {
        this(keyStore, keyStorePassword, new Properties());
    }

    PasswordStoreImpl(KeyStore keyStore, char[] keyStorePassword, Properties encryptedPasswords) {
        setKeyStore(keyStore);
        setKeyStorePassword(keyStorePassword);
        setEncryptedPasswordStore(new EncryptedPasswordStoreImpl(encryptedPasswords));
        setPasswordStore(new HashMap<String, char[]>());
    }

    private KeyStore getKeyStore() {
        return keyStore;
    }

    private void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    private char[] getKeyStorePassword() {
        return keyStorePassword;
    }

    private void setKeyStorePassword(char[] keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public EncryptedPasswordStore getEncryptedPasswordStore() {
        return encryptedPasswordStore;
    }

    public void setEncryptedPasswordStore(EncryptedPasswordStore encryptedPasswordStore) {
        this.encryptedPasswordStore = encryptedPasswordStore;
    }

    private Map<String, char[]> getPasswordStore() {
        return passwordStore;
    }

    private void setPasswordStore(Map<String, char[]> passwordStore) {
        this.passwordStore = passwordStore;
    }

    public Properties getEncryptedPasswords() {
        return getEncryptedPasswordStore().getEncryptedPasswords();
    }

    public String getEncryptedPassword(String alias) {
        return getEncryptedPasswordStore().getEncryptedPassword(alias);
    }

    public char[] getPassword(char[] alias) {
        try {
            return getPassword(new String(getBytes(alias), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Could not convert alias to byte[] in order to access password", ex);
        }
    }

    /**
     * Returns a password.
     */
    public char[] getPassword(String alias) {
        char[] password = getPasswordStore().get(alias);
        if (password == null) {
            String encryptedPassword = getEncryptedPassword(alias);
            if (encryptedPassword == null) {
                return null;
            }
            try {
                String keyStoreAlias = getKeyStore().aliases().nextElement();
                PrivateKey privateKey = (PrivateKey) getKeyStore().getKey(keyStoreAlias, getKeyStorePassword());
                Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] encryptedBytes = Base64.decodeBase64(encryptedPassword.getBytes(Charset.forName("UTF-8")));
                byte[] unencryptedBytes = cipher.doFinal(encryptedBytes);
                password = toCharArray(unencryptedBytes);
                getPasswordStore().put(alias, password);
            } catch (Exception ex) {
                throw new RuntimeException("Could not get password for alias: " + alias, ex);
            }
        }
        return password;
    }

    /**
     * Set password to have this alias
     */
    public String setPassword(String alias, char[] password) {
        String encryptedPassword;
        try {
            String keyStoreAlias = getKeyStore().aliases().nextElement();
            X509Certificate x509Certificate = (X509Certificate) getKeyStore().getCertificate(keyStoreAlias);
            PublicKey publicKey = x509Certificate.getPublicKey();
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(getBytes(password));
            encryptedPassword = new String(Base64.encodeBase64(encryptedBytes), Charset.forName("UTF-8"));
            getEncryptedPasswordStore().getEncryptedPasswords().put(alias, encryptedPassword);
        } catch (Exception ex) {
            throw new RuntimeException("Could not set password for alias: " + alias, ex);
        }
        return encryptedPassword;
    }

    private static byte[] getBytes(char[] charArr) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter outputStreamWriter = null;
            try {
                outputStreamWriter = new OutputStreamWriter(baos);
                outputStreamWriter.write(charArr, 0, charArr.length);
            } finally {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
            }
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Could not convert char[] to byte[]", ex);
        }
    }

    private static char[] toCharArray(byte[] passwordBytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(passwordBytes);
            InputStreamReader inputStreamReader = null;
            List<Character> characters = new ArrayList<Character>();
            try {
                inputStreamReader = new InputStreamReader(bais, Charset.forName("UTF-8"));
                int c;
                while ((c = inputStreamReader.read()) > -1) {
                    characters.add(new Character((char) c));
                }
            } finally {
                inputStreamReader.close();
            }
            char[] passwordAsCharArray = new char[characters.size()];
            for (int i = 0; i < characters.size(); i++) {
                passwordAsCharArray[i] = characters.get(i);
            }
            return passwordAsCharArray;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private interface EncryptedPasswordStore {

        /**
         * Returns a base64 encoded, encrypted password.
         */
        public String getEncryptedPassword(String alias);

        /**
         * Return the encrypted properties
         */
        public Properties getEncryptedPasswords();

    }

    private class EncryptedPasswordStoreImpl implements EncryptedPasswordStore {

        private Properties encryptedPasswordStore;

        public EncryptedPasswordStoreImpl(Properties encryptedPasswords) {
            setEncryptedPasswordStore(encryptedPasswords);
        }

        public Properties getEncryptedPasswordStore() {
            return encryptedPasswordStore;
        }

        public void setEncryptedPasswordStore(Properties encryptedPasswordStore) {
            this.encryptedPasswordStore = encryptedPasswordStore;
        }

        /**
         * Return the encrypted properties
         */
        public Properties getEncryptedPasswords() {
            return encryptedPasswordStore;
        }

        /**
         * Returns a base64 encoded, encrypted password.
         */
        public String getEncryptedPassword(String alias) {
            return getEncryptedPasswordStore().getProperty(alias);
        }
    }

}