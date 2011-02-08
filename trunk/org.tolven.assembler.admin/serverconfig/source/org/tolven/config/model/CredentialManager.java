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
package org.tolven.config.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PasswordFinder;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.tolven.config.model.credential.bean.CertificateDetail;
import org.tolven.config.model.credential.bean.CertificateGroupDetail;
import org.tolven.config.model.credential.bean.CertificateKeyDetail;
import org.tolven.config.model.credential.bean.CertificateKeyStoreDetail;
import org.tolven.config.model.credential.bean.TrustStoreCertificateDetail;
import org.tolven.config.model.credential.bean.TrustStoreDetail;
import org.tolven.security.password.PasswordHolder;
import org.tolven.security.password.PasswordInfo;

/**
 * This class contains the main API for generating credentials.
 * It relies heavily for functionality on CredentialHelper and CredentialsValidator
 *
 * @author Joseph Isaac
 *
 */
public class CredentialManager {

    private TolvenConfigWrapper tolvenConfigWrapper;
    private SecureRandom secureRandom;
    private Logger logger = Logger.getLogger(CredentialManager.class);

    public CredentialManager(TolvenConfigWrapper tolvenConfigWrapper) {
        setTolvenConfigWrapper(tolvenConfigWrapper);
        //TODO Is this the best place for loading the provider
        Security.addProvider(new BouncyCastleProvider());
    }

    private TolvenConfigWrapper getTolvenConfigWrapper() {
        return tolvenConfigWrapper;
    }

    private void setTolvenConfigWrapper(TolvenConfigWrapper tolvenConfigWrapper) {
        this.tolvenConfigWrapper = tolvenConfigWrapper;
    }

    private PasswordHolder getPasswordHolder() {
        return getTolvenConfigWrapper().getPasswordHolder();
    }

    public void changeGroupCredentialPassword(PasswordInfo passwordInfo, char[] oldPassword, char[] newPassword) throws IOException, GeneralSecurityException {
        if (oldPassword == null)
            throw new RuntimeException("Old password '" + passwordInfo.getRefId() + "' is null");
        if (!getPasswordHolder().verify(passwordInfo, oldPassword))
            throw new RuntimeException("Old Password is invalid for '" + passwordInfo.getRefId() + "'");
        if (newPassword == null)
            throw new RuntimeException("New password '" + passwordInfo.getRefId() + "' is null");
        CertificateGroupDetail certGroup = getTolvenConfigWrapper().getCredentialGroup(passwordInfo.getRefId());
        CertificateKeyDetail keyDetail = certGroup.getKey();
        PrivateKey privateKey = getPrivateKey(keyDetail, oldPassword);
        File keyFile = new File(keyDetail.getSource());
        KeyStore keyStore = null;
        File keyStoreFile = null;
        CertificateKeyStoreDetail certKeyStoreDetail = certGroup.getKeyStore();
        if (certKeyStoreDetail != null) {
            keyStore = getTolvenConfigWrapper().getKeyStore(oldPassword, certKeyStoreDetail);
            keyStoreFile = new File(certKeyStoreDetail.getSource());
        }
        TrustStoreDetail trustStoreDetail = getTolvenConfigWrapper().getTrustStoreDetail(passwordInfo.getRefId());
        KeyStore trustStore = null;
        File trustStoreFile = null;
        if (trustStore != null) {
            trustStore = getTolvenConfigWrapper().getTrustStore(oldPassword, trustStoreDetail);
            trustStoreFile = new File(trustStoreDetail.getSource());
        }
        File tmpKey = null;
        File tmpKeyStore = null;
        File tmpTrustStore = null;
        boolean success = false;
        try {
            getTolvenConfigWrapper().getBuildDir().mkdirs();
            tmpKey = new File(getTolvenConfigWrapper().getBuildDir(), keyFile.getName());
            write(privateKey, keyDetail.getFormat(), tmpKey, newPassword);
            if (keyStoreFile != null) {
                tmpKeyStore = new File(getTolvenConfigWrapper().getBuildDir(), keyStoreFile.getName());
                String alias = keyStore.aliases().nextElement();
                Key key = keyStore.getKey(alias, oldPassword);
                Certificate[] chain = keyStore.getCertificateChain(alias);
                keyStore.setKeyEntry(alias, key, newPassword, chain);
                write(keyStore, tmpKeyStore, newPassword);
            }
            if (trustStoreFile != null) {
                tmpTrustStore = new File(getTolvenConfigWrapper().getBuildDir(), trustStoreFile.getName());
                write(trustStore, tmpTrustStore, newPassword);
            }
            FileUtils.copyFile(tmpKey, keyFile);
            if (keyStoreFile != null) {
                FileUtils.copyFile(tmpKeyStore, keyStoreFile);
            }
            if (trustStoreFile != null) {
                FileUtils.copyFile(tmpTrustStore, trustStoreFile);
            }
            success = true;
        } finally {
            if (success) {
                if (tmpKey != null) {
                    tmpKey.delete();
                }
                if (tmpKeyStore != null) {
                    tmpKeyStore.delete();
                }
                if (tmpKeyStore != null) {
                    tmpKeyStore.delete();
                }
                getPasswordHolder().changePassword(passwordInfo, oldPassword, newPassword);
            }
        }
    }

    public void processPasswords(Properties inputPasswordProperties) throws IOException, GeneralSecurityException {
        File credentialFile = null;
        String credentialId = null;
        char[] privateKeyPassword = null;
        for (CertificateGroupDetail certGroup : getTolvenConfigWrapper().getCertificateInfoDetail().getGroup()) {
            credentialId = certGroup.getId();
            if (inputPasswordProperties.containsKey(credentialId)) {
                credentialFile = new File(certGroup.getKey().getSource());
                privateKeyPassword = (char[]) inputPasswordProperties.get(credentialId);
                if (credentialFile.exists()) {
                    getPrivateKey(certGroup.getKey(), privateKeyPassword);
                }
                if (getPasswordHolder().getPasswordInfo(credentialId) == null) {
                    getPasswordHolder().addPassword(certGroup.getId(), PasswordHolder.CREDENTIAL_PASSWORD, privateKeyPassword);
                }
            }
        }
    }

    public boolean verifyKeyStorePassword(PasswordInfo passwordInfo) {
        char[] password = getPasswordHolder().getPassword(passwordInfo.getRefId());
        if (password == null) {
            throw new RuntimeException("Password '" + passwordInfo.getRefId() + "' is does not exist.");
        }
        CertificateGroupDetail certGroup = getTolvenConfigWrapper().getCredentialGroup(passwordInfo.getRefId());
        CertificateKeyStoreDetail certKeyStoreDetail = certGroup.getKeyStore();
        getTolvenConfigWrapper().getKeyStore(password, certKeyStoreDetail);
        return true;
    }

    public boolean verifyPrivateKeyPassword(PasswordInfo passwordInfo) throws IOException, GeneralSecurityException {
        char[] password = getPasswordHolder().getPassword(passwordInfo.getRefId());
        if (password == null) {
            throw new RuntimeException("Password '" + passwordInfo.getRefId() + "' is does not exist.");
        }
        CertificateGroupDetail certGroup = getTolvenConfigWrapper().getCredentialGroup(passwordInfo.getRefId());
        CertificateKeyDetail keyDetail = certGroup.getKey();
        try {
            getPrivateKey(keyDetail, password);
        } catch (IOException ex) {
            throw new IOException("PrivateKey password verified, but failed with corresponding credential '" + passwordInfo.getRefId() + "'");
        }
        return true;
    }

    public boolean verifyTrustStorePassword(PasswordInfo passwordInfo) throws IOException, GeneralSecurityException {
        char[] password = getPasswordHolder().getPassword(passwordInfo.getRefId());
        if (password == null) {
            throw new RuntimeException("Password '" + passwordInfo.getRefId() + "' is does not exist.");
        }
        TrustStoreDetail trustStoreDetail = getTolvenConfigWrapper().getTrustStoreDetail(passwordInfo.getRefId());
        try {
            getTolvenConfigWrapper().getTrustStore(password, trustStoreDetail);
        } catch (IOException ex) {
            throw new IOException("TrustStore password verified, but failed with corresponding credential '" + passwordInfo.getRefId() + "'");
        }
        return true;
    }

    public String getKeySource(String groupId) {
        CertificateGroupDetail certGroupDetail = getTolvenConfigWrapper().getCredentialGroup(groupId);
        if (certGroupDetail != null) {
            CertificateKeyDetail certKeyDetail = certGroupDetail.getKey();
            File keyFile = new File(certKeyDetail.getSource());
            return keyFile.getPath();
        }
        return null;
    }

    public boolean keyExists(String groupId) {
        CertificateGroupDetail certGroupDetail = getTolvenConfigWrapper().getCredentialGroup(groupId);
        if (certGroupDetail != null) {
            CertificateKeyDetail certKeyDetail = certGroupDetail.getKey();
            File keyFile = new File(certKeyDetail.getSource());
            return keyFile.exists();
        }
        return false;
    }

    private byte[] convertToPEMBytes(char[] pass, Object object) throws IOException {
        OutputStreamWriter osw = null;
        PEMWriter pemWriter = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            try {
                osw = new OutputStreamWriter(baos);
                pemWriter = new PEMWriter(osw, "BC");
                if (pass == null) {
                    pemWriter.writeObject(object);
                } else {
                    pemWriter.writeObject(object, "DESEDE", pass, getSecureRandom());
                }
            } finally {
                if (pemWriter != null)
                    pemWriter.close();
                if (osw != null)
                    osw.close();
            }
            return baos.toByteArray();
        } finally {
            if (baos != null)
                baos.close();
        }
    }

    private byte[] convertToPEMBytes(Object object) throws IOException {
        return convertToPEMBytes(null, object);
    }

    private void createKeyStore(CertificateGroupDetail certGroup, char[] password) throws IOException, GeneralSecurityException {
        File file = null;
        try {
            CertificateKeyStoreDetail certificateKeyStore = certGroup.getKeyStore();
            KeyStore keyStore = KeyStore.getInstance(certificateKeyStore.getFormat());
            char[] keystorepass = password;
            if (keystorepass == null) {
                keystorepass = getPasswordHolder().getPassword(certGroup.getId());
                if (keystorepass == null) {
                    throw new RuntimeException("Password for keystore in group " + certGroup.getId() + " cannot be null");
                }
            }
            if (getPasswordHolder().getPassword(certGroup.getId()) == null) {
                getPasswordHolder().addPassword(certGroup.getId(), PasswordHolder.CREDENTIAL_PASSWORD, keystorepass);
            }
            keyStore.load(null, keystorepass);
            CertificateKeyDetail keyDetail = certGroup.getKey();
            X509Certificate[] certificates = getX509CertificateChain(certGroup);
            char[] keypass = getPasswordHolder().getPassword(certGroup.getId());
            PrivateKey privateKey = getPrivateKey(keyDetail, keypass);
            String alias = certificates[0].getSubjectDN().getName();
            keyStore.setKeyEntry(alias, privateKey, keypass, certificates);
            file = new File(certificateKeyStore.getSource());
            write(keyStore, file, keystorepass);
        } catch (IOException ex) {
            ex.printStackTrace();
            if (file != null)
                file.delete();
            throw ex;
        } catch (GeneralSecurityException ex) {
            ex.printStackTrace();
            if (file != null)
                file.delete();
            throw ex;
        }
    }

    private X509CertificatePrivateKeyPair createSelfSignedCertificate(X500Principal subjectX500Principal) throws GeneralSecurityException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        X509Certificate certificate = signCertificate(subjectX500Principal, keyPair.getPublic(), subjectX500Principal, keyPair.getPrivate());
        return new X509CertificatePrivateKeyPair(certificate, keyPair.getPrivate());
    }

    private PrivateKey getDERPrivateKey(CertificateKeyDetail keyDetail, char[] password) throws IOException, GeneralSecurityException {
        File privateKeyFile = new File(keyDetail.getSource());
        if (!privateKeyFile.exists()) {
            throw new RuntimeException("Cannot find PrivateKey file: " + privateKeyFile.getPath());
        }
        byte[] privateKey = FileUtils.readFileToByteArray(privateKeyFile);
        EncryptedPrivateKeyInfo encryptedKeyInfo = new EncryptedPrivateKeyInfo(privateKey);
        AlgorithmParameters params = encryptedKeyInfo.getAlgParameters();
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(encryptedKeyInfo.getAlgName());
        PBEKeySpec passwordSpec = new PBEKeySpec(password);
        SecretKey secretKey = secretKeyFactory.generateSecret(passwordSpec);
        Cipher cipher = Cipher.getInstance(encryptedKeyInfo.getAlgName());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, params);
        PKCS8EncodedKeySpec keySpec = encryptedKeyInfo.getKeySpec(cipher);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private BigInteger getNextSerialNumber() {
        byte[] randomLong = new byte[8];
        getSecureRandom().nextBytes(randomLong);
        BigInteger serialNumber = new BigInteger(randomLong);
        if (serialNumber.compareTo(BigInteger.valueOf(0)) > 0) {
            return serialNumber;
        } else if (serialNumber.compareTo(BigInteger.valueOf(0)) < 0) {
            return BigInteger.valueOf(0).subtract(serialNumber);
        } else {
            return BigInteger.valueOf(1).add(serialNumber);
        }
    }

    private PrivateKey getPEMPrivateKey(CertificateKeyDetail keyDetail, final char[] password) throws IOException {
        FileInputStream fis = null;
        InputStreamReader inputStreamReader = null;
        PEMReader pemReader = null;
        File file = new File(keyDetail.getSource());
        if (!file.exists()) {
            throw new RuntimeException("Cannot find PrivateKey file: " + file.getPath());
        }
        try {
            fis = new FileInputStream(new File(keyDetail.getSource()));
            inputStreamReader = new InputStreamReader(fis, Charset.forName("UTF-8"));
            if (keyDetail.isPasswordProtected()) {
                pemReader = new PEMReader(inputStreamReader, new PasswordFinder() {
                    public char[] getPassword() {
                        return password;
                    }
                }, "BC");
            } else {
                pemReader = new PEMReader(inputStreamReader);
            }
            KeyPair keyPair = (KeyPair) pemReader.readObject();
            return keyPair.getPrivate();
        } finally {
            if (fis != null)
                fis.close();
            if (inputStreamReader != null)
                inputStreamReader.close();
            if (pemReader != null)
                pemReader.close();
        }
    }

    private PrivateKey getPrivateKey(CertificateKeyDetail keyDetail, char[] password) throws IOException, GeneralSecurityException {
        if (password == null && keyDetail.isPasswordProtected())
            throw new RuntimeException("The key " + keyDetail.getId() + " requires a password");
        if (TolvenConfigWrapper.TOLVEN_CREDENTIAL_FORMAT_PEM.equals(keyDetail.getFormat())) {
            return getPEMPrivateKey(keyDetail, password);
        } else {
            return getDERPrivateKey(keyDetail, password);
        }
    }

    private SecureRandom getSecureRandom() {
        //TODO: This may be better placed where it only gets created once for certain
        String algorithm = "SHA1PRNG";
        if (secureRandom == null) {
            try {
                secureRandom = SecureRandom.getInstance(algorithm);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException("Could not get an instance of SecureRandom using algorithm" + algorithm, ex);
            }
        }
        return secureRandom;
    }

    private X500Principal getX500Principal(CertificateGroupDetail certGroupDetail) {
        return new X500Principal(X509Name.EmailAddress + "=" + certGroupDetail.getEmail() + "," + X509Name.CN + "=" + certGroupDetail.getCommonName() + "," + X509Name.OU + "=" + certGroupDetail.getOrganizationUnitName() + "," + X509Name.O + "=" + certGroupDetail.getOrganizationName() + "," + X509Name.ST + "=" + certGroupDetail.getStateOrProvince() + "," + X509Name.C + "=" + certGroupDetail.getCountryName());
    }

    private X509Certificate[] getX509CertificateChain(CertificateGroupDetail certGroup) {
        List<X509Certificate> certificates = new ArrayList<X509Certificate>();
        X509Certificate certificate = getTolvenConfigWrapper().getX509Certificate(certGroup);
        certificates.add(certificate);
        if (!certificate.getIssuerDN().equals(certificate.getSubjectDN())) {
            X509Certificate issuingCertificate = null;
            do {
                CertificateGroupDetail issuingCertGroup = getTolvenConfigWrapper().getCredentialGroup(certGroup.getCertificate().getCaRefId());
                issuingCertificate = getTolvenConfigWrapper().getX509Certificate(issuingCertGroup);
                if (!certificates.contains(issuingCertificate))
                    certificates.add(issuingCertificate);

            } while ((!issuingCertificate.getIssuerDN().equals(issuingCertificate.getSubjectDN())));
        }
        X509Certificate[] certArr = new X509Certificate[certificates.size()];
        for (int i = 0; i < certificates.size(); i++)
            certArr[i] = certificates.get(i);
        return certArr;
    }

    public void processCredentialGroup(CertificateGroupDetail certGroup) throws IOException, GeneralSecurityException {
        processCredentialGroup(certGroup, null);
    }

    public void processCredentialGroup(CertificateGroupDetail certGroup, char[] password) throws IOException, GeneralSecurityException {
        CertificateDetail cert = certGroup.getCertificate();
        CertificateKeyDetail key = certGroup.getKey();
        if ((key == null && cert != null) || (key != null && cert == null)) {
            throw new RuntimeException("Group: " + certGroup.getId() + " must either have a both key and certificate definition or neither");
        }
        if (key != null && cert != null) {
            File certFile = new File(cert.getSource());
            File keyFile = new File(key.getSource());
            if (key.isCommercial()) {
                if (!(certFile.exists() && keyFile.exists()))
                    throw new RuntimeException(certGroup.getId() + " is a commercial group. " + certFile.getPath() + " and " + keyFile.getPath() + " must exist");
            } else {
                if (!certFile.exists() && !keyFile.exists()) {
                    logger.info("New credentials will be created for certificate " + cert.getId());
                    getTolvenConfigWrapper().getBuildDir().mkdirs();
                    X500Principal x500Principal = getX500Principal(certGroup);
                    X509CertificatePrivateKeyPair certPrivateKeyPair = createSelfSignedCertificate(x500Principal);
                    X509Certificate certificate = null;
                    if (cert.getId().equals(cert.getCaRefId())) {
                        certificate = certPrivateKeyPair.getX509Certificate();
                    } else {
                        certificate = signCertificate(cert, x500Principal, certPrivateKeyPair.getX509Certificate().getPublicKey());
                    }
                    File tmpCertFile = null;
                    File tmpKeyFile = null;
                    try {
                        tmpCertFile = File.createTempFile("cert", ".tmp", getTolvenConfigWrapper().getBuildDir());
                        // Just require a path to a temporary file
                        tmpCertFile.delete();
                        tmpKeyFile = File.createTempFile("key", ".tmp", getTolvenConfigWrapper().getBuildDir());
                        // Just require a path to a temporary file
                        tmpKeyFile.delete();
                        char[] keypass = password;
                        if (key.isPasswordProtected()) {
                            if (keypass == null) {
                                keypass = getPasswordHolder().getPassword(certGroup.getId());
                                if (keypass == null) {
                                    throw new RuntimeException("Password for key in group " + certGroup.getId() + " cannot be null");
                                }
                            }
                            if (getPasswordHolder().getPassword(certGroup.getId()) == null) {
                                getPasswordHolder().addPassword(certGroup.getId(), PasswordHolder.CREDENTIAL_PASSWORD, keypass);
                            }
                        } else {
                            if (password != null) {
                                throw new RuntimeException("The key for group " + certGroup.getId() + " is not protected, yet a password was supplied");
                            }
                        }
                        PrivateKey privateKey = certPrivateKeyPair.getPrivateKey();
                        if (TolvenConfigWrapper.TOLVEN_CREDENTIAL_FORMAT_PEM.equals(key.getFormat())) {
                            writePEMObject(certificate, tmpCertFile);
                            writePEMObject(keypass, privateKey, tmpKeyFile);
                        } else {
                            writeDER(certificate, tmpCertFile);
                            writeDER(keypass, privateKey, tmpKeyFile);
                        }
                        FileUtils.copyFile(tmpCertFile, certFile);
                        FileUtils.copyFile(tmpKeyFile, keyFile);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (certFile != null)
                            certFile.delete();
                        if (keyFile != null)
                            keyFile.delete();
                        throw new RuntimeException("Could not create key and certificate for group: " + certGroup.getId(), ex);
                    } finally {
                        if (tmpCertFile != null)
                            tmpCertFile.delete();
                        if (tmpKeyFile != null)
                            tmpKeyFile.delete();
                    }
                } else {
                    if (!(certFile.exists() && keyFile.exists()))
                        throw new RuntimeException(certGroup.getId() + " is a non-commercial group with existing credentials. " + certFile.getPath() + " and " + keyFile.getPath() + " must exist");
                }
            }
        }
        CertificateKeyStoreDetail certificateKeyStoreDetail = certGroup.getKeyStore();
        if (certificateKeyStoreDetail != null && !new File(certificateKeyStoreDetail.getSource()).exists()) {
            logger.info("New keyStore will be created: " + certificateKeyStoreDetail.getId() + " at " + certificateKeyStoreDetail.getSource());
            createKeyStore(certGroup, password);
        }
    }

    public void processCertificateInfo(List<CertificateGroupDetail> certGroupDetails) throws IOException, GeneralSecurityException {
        for (CertificateGroupDetail certGroupDetail : certGroupDetails) {
            CertificateDetail certDetail = certGroupDetail.getCertificate();
            if (certDetail != null) {
                processCredentialGroup(certGroupDetail);
            }
        }
    }

    public void processTrustStores(List<TrustStoreDetail> trustStores) {
        for (TrustStoreDetail trustStore : trustStores) {
            processTrustStore(trustStore);
        }
    }

    public void processTrustStore(String groupId) {
        TrustStoreDetail trustStoreDetail = getTolvenConfigWrapper().getTrustStoreDetail(groupId);
        if (trustStoreDetail == null) {
            throw new RuntimeException("Could not locate truststore for group: " + groupId);
        }
        processTrustStore(trustStoreDetail);
    }

    public void processTrustStore(TrustStoreDetail trustStoreDetail) {
        try {
            Set<X509Certificate> newTrustStoreCerts = new HashSet<X509Certificate>();
            Set<X509Certificate> previousTrustStoreCerts = new HashSet<X509Certificate>();
            Set<X509Certificate> resultingTrustStoreCerts = new HashSet<X509Certificate>();
            for (TrustStoreCertificateDetail trustStoreCertificateDetail : trustStoreDetail.getCertificate()) {
                CertificateGroupDetail certGroup = getTolvenConfigWrapper().getCredentialGroup(trustStoreCertificateDetail.getRefId());
                if (certGroup == null) {
                    throw new RuntimeException("The trusted group " + trustStoreCertificateDetail.getRefId() + " in truststore " + trustStoreDetail.getId() + " does not exist");
                }
                X509Certificate trustStoreX509Certificate = getTolvenConfigWrapper().getX509Certificate(certGroup);
                newTrustStoreCerts.add(trustStoreX509Certificate);
            }
            File trustStoreFile = new File(trustStoreDetail.getSource());
            if (TolvenConfigWrapper.TOLVEN_CREDENTIAL_FORMAT_PEM.equals(trustStoreDetail.getFormat())) {
                if (trustStoreFile.exists()) {
                    previousTrustStoreCerts = getTolvenConfigWrapper().getX509Certificates(trustStoreFile);
                    for (X509Certificate cert : previousTrustStoreCerts) {
                        resultingTrustStoreCerts.add(cert);
                    }
                }
                // And now for what Java calls a Set intersection
                resultingTrustStoreCerts.retainAll(newTrustStoreCerts);
                if (resultingTrustStoreCerts.size() != newTrustStoreCerts.size() || !resultingTrustStoreCerts.containsAll(newTrustStoreCerts)) {
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(trustStoreFile);
                        for (X509Certificate x509Certificate : newTrustStoreCerts) {
                            out.write(convertToPEMBytes(x509Certificate));
                        }
                    } finally {
                        if (out != null) {
                            out.close();
                        }
                    }
                    logger.info("Created truststore: " + trustStoreDetail.getId());
                }
            } else if (TolvenConfigWrapper.TOLVEN_CREDENTIAL_FORMAT_JKS.equals(trustStoreDetail.getFormat()) || TolvenConfigWrapper.TOLVEN_CREDENTIAL_FORMAT_PKCS12.equals(trustStoreDetail.getFormat())) {
                char[] truststorepass = getPasswordHolder().getPassword(trustStoreDetail.getId());
                if (trustStoreFile.exists()) {
                    KeyStore trustStore = getTolvenConfigWrapper().getKeyStore(truststorepass, trustStoreFile, trustStoreDetail.getFormat());
                    Enumeration<String> enumeration = trustStore.aliases();
                    while (enumeration.hasMoreElements()) {
                        String alias = enumeration.nextElement();
                        X509Certificate cert = (X509Certificate) trustStore.getCertificate(alias);
                        previousTrustStoreCerts.add(cert);
                        resultingTrustStoreCerts.add(cert);
                    }
                }
                // And now for what Java calls a Set intersection
                resultingTrustStoreCerts.retainAll(newTrustStoreCerts);
                if (resultingTrustStoreCerts.size() != newTrustStoreCerts.size() || !resultingTrustStoreCerts.containsAll(newTrustStoreCerts)) {
                    KeyStore trustStore = KeyStore.getInstance(trustStoreDetail.getFormat());
                    trustStore.load(null, truststorepass);
                    for (X509Certificate newCert : newTrustStoreCerts) {
                        String alias = newCert.getSubjectDN().getName();
                        trustStore.setCertificateEntry(alias, newCert);
                    }
                    trustStoreFile.getParentFile().mkdirs();
                    write(trustStore, trustStoreFile, truststorepass);
                    logger.info("Created truststore: " + trustStoreDetail.getId());
                }
            } else {
                throw new RuntimeException("Unrecognized keystore format: " + trustStoreDetail.getFormat());
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to process truststore: " + trustStoreDetail.getId(), ex);
        }
    }

    private X509Certificate signCertificate(CertificateDetail certDetail, X500Principal subjectX500Principal, PublicKey subjectPublicKey) throws IOException, GeneralSecurityException {
        CertificateGroupDetail caCertGroupDetail = getTolvenConfigWrapper().getCredentialGroup(certDetail.getCaRefId());
        CertificateKeyDetail caKeyDetail = caCertGroupDetail.getKey();
        char[] caKeyPass = getPasswordHolder().getPassword(caCertGroupDetail.getId());
        PrivateKey caPrivateKey = getPrivateKey(caKeyDetail, caKeyPass);
        X509Certificate caCertificate = getTolvenConfigWrapper().getX509Certificate(caCertGroupDetail);
        return signCertificate(subjectX500Principal, subjectPublicKey, caCertificate.getIssuerX500Principal(), caPrivateKey);
    }

    private X509Certificate signCertificate(X500Principal subjectX500Principal, PublicKey subjectPublicKey, X500Principal issuerX500Principal, PrivateKey issuerPrivateKey) throws GeneralSecurityException {
        X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
        gen.setSignatureAlgorithm("SHA1withRSA");
        gen.setSubjectDN(subjectX500Principal);
        gen.setSerialNumber(getNextSerialNumber());
        gen.setIssuerDN(issuerX500Principal);
        gen.setNotBefore(new Date());
        gen.setNotAfter(new Date(new Date().getTime() + 1000000000000L));
        gen.setPublicKey(subjectPublicKey);
        return gen.generate(issuerPrivateKey);
    }

    private void write(KeyStore keyStore, File keyStoreFile, char[] password) {
        File file = null;
        FileOutputStream out = null;
        try {
            try {
                keyStoreFile.getParentFile().mkdirs();
                out = new FileOutputStream(keyStoreFile);
                keyStore.store(out, password);
            } catch (Exception ex) {
                if (file != null) {
                    file.delete();
                }
                throw new RuntimeException("Could not create keystore file: " + keyStoreFile.getPath(), ex);
            } finally {
                if (out != null)
                    out.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not store keystore file " + keyStoreFile.getPath(), ex);
        }

    }

    private void write(PrivateKey privateKey, String privateKeyFormat, File file, char[] password) throws IOException, GeneralSecurityException {
        if (TolvenConfigWrapper.TOLVEN_CREDENTIAL_FORMAT_PEM.equals(privateKeyFormat)) {
            writePEMObject(password, privateKey, file);
        } else {
            writeDER(password, privateKey, file);
        }
    }

    private void writeDER(char[] password, PrivateKey privateKey, File file) throws IOException, GeneralSecurityException {
        byte[] bytes = null;
        if (password == null) {
            bytes = privateKey.getEncoded();
        } else {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            PBEKeySpec passwordSpec = new PBEKeySpec(password);
            SecretKey secretKey = secretKeyFactory.generateSecret(passwordSpec);
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedPrivateKey = cipher.doFinal(privateKey.getEncoded());
            EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(cipher.getParameters(), encryptedPrivateKey);
            bytes = encryptedPrivateKeyInfo.getEncoded();
        }
        FileUtils.writeByteArrayToFile(file, bytes);
    }

    private void writeDER(X509Certificate certificate, File file) throws IOException, GeneralSecurityException {
        FileUtils.writeByteArrayToFile(file, certificate.getEncoded());
    }

    private void writePEMObject(char[] password, Object object, File file) {
        file.getParentFile().mkdirs();
        FileWriter fileWriter = null;
        PEMWriter pemWriter = null;
        try {
            try {
                fileWriter = new FileWriter(file);
                pemWriter = new PEMWriter(fileWriter, "BC");
                if (password == null) {
                    pemWriter.writeObject(object);
                } else {
                    pemWriter.writeObject(object, "DESEDE", password, getSecureRandom());
                }
            } finally {
                if (pemWriter != null)
                    pemWriter.close();
                if (fileWriter != null)
                    fileWriter.close();
            }
        } catch (IOException ex) {
            new RuntimeException("Failed to write pem to file: " + file.getPath(), ex);
        }
    }

    private void writePEMObject(Object object, File file) {
        writePEMObject(null, object, file);
    }

    private byte[] toByteArray(KeyStore keyStore, char[] password) throws IOException, GeneralSecurityException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            keyStore.store(baos, password);
            byte[] byteArr = baos.toByteArray();
            return byteArr;
        } finally {
            if (baos != null)
                baos.close();
        }
    }

    /**
     * Given an adminGroupId and an adminPassword, create the admin credentials, if they do not already exist. Attempt to load the password store.
     * 
     * @param adminGroupId
     * @param adminPassword
     * @throws Exception
     */
    public void processAdminPassword(String adminGroupId, char[] adminPassword) throws Exception {
        if (!getTolvenConfigWrapper().getAdminKeyStoreFile().exists()) {
            CertificateGroupDetail certGroup = getTolvenConfigWrapper().getCredentialGroup(adminGroupId);
            processCredentialGroup(certGroup, adminPassword);
        }
        getTolvenConfigWrapper().loadPasswordHolder(adminGroupId, adminPassword);
    }

    /**
     * During generation of a PrivateKey and Certificate pair, both components are often created simultaneously, so
     * this class is a wrapper which allows both to be returned together
     * @author Joseph Isaac
     *
     */
    private class X509CertificatePrivateKeyPair {

        private PrivateKey privateKey;
        private X509Certificate certificate;

        public X509CertificatePrivateKeyPair(X509Certificate certificate, PrivateKey privateKey) {
            this.certificate = certificate;
            this.privateKey = privateKey;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public X509Certificate getX509Certificate() {
            return certificate;
        }

        public byte[] getX509CertificateByteArray() throws GeneralSecurityException {
            return getX509Certificate().getEncoded();
        }

        public KeyStore createPKCS12KeyStore(String alias, char[] password) throws GeneralSecurityException, IOException {
            // We can't use Bouncy Castle here because it requires unrestricted strength
            // which Tolven does not require (although it is allowed). 
            KeyStore keyStore = KeyStore.getInstance(TolvenConfigWrapper.TOLVEN_CREDENTIAL_FORMAT_PKCS12);
            keyStore.load(null, password);
            X509Certificate[] certificateChain = new X509Certificate[1];
            certificateChain[0] = getX509Certificate();
            keyStore.setKeyEntry(alias, getPrivateKey(), password, certificateChain);
            return keyStore;
        }

        public byte[] createPKCS12KeyStoreByteArray(String alias, char[] password) throws GeneralSecurityException, IOException {
            KeyStore keyStore = createPKCS12KeyStore(alias, password);
            return toByteArray(keyStore, password);
        }

    }

}
