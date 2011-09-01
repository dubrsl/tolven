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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.tolven.config.model.config.bean.AdminDetail;
import org.tolven.config.model.config.bean.AppServerDetail;
import org.tolven.config.model.config.bean.ApplicationDetail;
import org.tolven.config.model.config.bean.DBServerDetail;
import org.tolven.config.model.config.bean.LDAPServerDetail;
import org.tolven.config.model.config.bean.MDBUserDetail;
import org.tolven.config.model.config.bean.PasswordServerDetail;
import org.tolven.config.model.config.bean.TolvenConfig;
import org.tolven.config.model.config.bean.WebServerDetail;
import org.tolven.config.model.credential.bean.CertificateDetail;
import org.tolven.config.model.credential.bean.CertificateGroupDetail;
import org.tolven.config.model.credential.bean.CertificateInfoDetail;
import org.tolven.config.model.credential.bean.CertificateKeyDetail;
import org.tolven.config.model.credential.bean.CertificateKeyStoreDetail;
import org.tolven.config.model.credential.bean.Credentials;
import org.tolven.config.model.credential.bean.TrustStoreCertificateDetail;
import org.tolven.config.model.credential.bean.TrustStoreDetail;
import org.tolven.security.password.PasswordHolder;

/**
 * This class acts a wrapper for the credential.xml, tolven-config.xml and tolven-config.properties.xml files.
 * This class can load its details when provided with the configuration directory.
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenConfigWrapper {

    public static final String TOLVEN_CONFIG_XML = "tolven-config.xml";
    public static final String CREDENTIAL_XML = "credential.xml";
    public static final String SERVER_DEFAULT_CONFIG_PROPERTIES_XML = "server-default-config.properties.xml";

    public static final String BUILD_DIR = "build";

    public static final String CREDENTIAL_XSD = "credential.xsd";
    public static final String TOLVEN_CONFIG_XSD = "tolven-config.xsd";
    public static final String CREDENTIALS_PACKAGE = "org.tolven.config.model.credential.bean";
    public static final String TOLVENCONFIG_PACKAGE = "org.tolven.config.model.config.bean";

    public static final String ARCHIVE_DIR = "archive";
    public static final String LAST_INSTALLED = "last_installed";
    public static final String INSTALLED_LOG = "installed.log";

    public static final String TOLVEN_CREDENTIAL_FORMAT_PEM = "pem";
    public static final String TOLVEN_CREDENTIAL_FORMAT_JKS = "jks";
    public static final String TOLVEN_CREDENTIAL_FORMAT_PKCS12 = "pkcs12";

    private File configDir;

    private Credentials credentials;
    private TolvenConfig tolvenConfig;
    private PasswordHolder passwordHolder;
    private Logger logger = Logger.getLogger(TolvenConfigWrapper.class);

    public TolvenConfigWrapper() {
        setPasswordHolder(new PasswordHolder());
    }

    public PasswordHolder getPasswordHolder() {
        return passwordHolder;
    }

    public void setPasswordHolder(PasswordHolder passwordHolder) {
        this.passwordHolder = passwordHolder;
    }

    public File getConfigDir() {
        return configDir;
    }

    private void setConfigDir(File configDir) {
        this.configDir = configDir;
    }

    public File getCredentialFile() {
        return new File(getConfigDir(), CREDENTIAL_XML);
    }

    public File getTolvenConfigFile() {
        return new File(getConfigDir(), TOLVEN_CONFIG_XML);
    }

    public Credentials getCredentials() {
        return credentials;
    }

    private void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    private TolvenConfig getTolvenConfig() {
        return tolvenConfig;
    }

    private void setTolvenConfig(TolvenConfig tolvenConfig) {
        this.tolvenConfig = tolvenConfig;
    }

    public File getBuildDir() {
        return new File(getConfigDir(), BUILD_DIR);
    }

    public File getAdminKeyStoreFile() {
        AdminDetail admin = getAdmin();
        if (admin == null) {
            return null;
        }
        String groupId = admin.getId();
        CertificateKeyStoreDetail keyStore = getKeyStoreDetail(groupId);
        if (keyStore == null) {
            return null;
        } else {
            return new File(keyStore.getSource());
        }
    }

    public File getAdminSecretKeyFile() {
        AdminDetail admin = getAdmin();
        if (admin == null) {
            return null;
        } else {
            return new File(admin.getSecretKeySource());
        }
    }

    public File getAdminPasswordFile() {
        AdminDetail admin = getAdmin();
        if (admin == null) {
            return null;
        } else {
            return new File(admin.getPasswordStoreSource());
        }
    }

    public String getActiveAppServerSSLPasswordAlias() {
        return getTolvenConfig().getAppServer().getId();
    }

    public String getActiveDBUser() {
        DBServerDetail activeDB = getDBServer();
        if (activeDB == null) {
            return null;
        } else {
            return activeDB.getUser();
        }
    }

    public String getActiveDBConnectionString() {
        DBServerDetail activeDB = getDBServer();
        if (activeDB == null) {
            return null;
        } else {
            return activeDB.getConnectionString();
        }
    }

    public boolean isValidServerConfiguration(String appserverId, String databaseId, String ldapId) {
        AppServerDetail appServerDetail = getAppServer();
        return (appServerDetail.getId().equals(appserverId) && appServerDetail.getDbId().equals(databaseId) && appServerDetail.getLdapId().equals(ldapId));
    }

    public void loadConfigDir(File configDir) {
        loadConfigDir(configDir, true);
    }

    public char[] getPassword(String refId) {
        return getPasswordHolder().getPassword(refId);
    }

    /**
     * Load the credential.xml, tolven-config.xml and tolven-config.properties.xml files. Optionally expand all of 
     * the properties (which is the normal for a runtime environment). For the purposes of editing only, the properties
     * do not have to be expanded.
     * 
     * @param configDir
     * @param expandProperties
     * @throws IOException
     * @throws JAXBException
     */
    public void loadConfigDir(File configDir, boolean expandProperties) {
        setConfigDir(configDir);
        setCredentials(initializeCredentials());
        setTolvenConfig(initializeTolvenConfig());
    }

    public CertificateInfoDetail getCertificateInfoDetail() {
        return getCredentials().getCertificateInfo();
    }

    public CertificateDetail getCertificateDetail(String id) {
        for (CertificateGroupDetail certGroupDetail : getCredentialGroupDetails()) {
            if (certGroupDetail.getId().equals(id)) {
                return certGroupDetail.getCertificate();
            }
        }
        return null;
    }

    public CertificateKeyDetail getKeyDetail(String id) {
        for (CertificateGroupDetail certGroupDetail : getCredentialGroupDetails()) {
            if (certGroupDetail.getId().equals(id)) {
                return certGroupDetail.getKey();
            }
        }
        return null;
    }

    public CertificateKeyStoreDetail getKeyStoreDetail(String id) {
        for (CertificateGroupDetail certGroup : getCredentialGroupDetails()) {
            if (certGroup.getId().equals(id)) {
                return certGroup.getKeyStore();
            }
        }
        return null;
    }

    public List<CertificateGroupDetail> getCredentialGroupDetails() {
        return getCredentials().getCertificateInfo().getGroup();
    }

    public List<AppServerDetail> getAppServerDetails() {
        List<AppServerDetail> appServerDetails = new ArrayList<AppServerDetail>();
        appServerDetails.add(getAppServer());
        return appServerDetails;
    }

    public AdminDetail getAdmin() {
        if (getTolvenConfig().getAdmin() == null) {
            getTolvenConfig().setAdmin(new org.tolven.config.model.config.bean.ObjectFactory().createAdminDetail());
        }
        return getTolvenConfig().getAdmin();
    }

    public String getAdminId() {
        return getAdmin().getId();
    }

    public char[] getAdminPassword() {
        if (getAdmin().getPassword() == null) {
            return null;
        } else {
            return getAdmin().getPassword().toCharArray();
        }
    }

    public void setAdminId(String groupId) {
        getAdmin().setId(groupId);
    }

    public LDAPServerDetail getLDAPServer() {
        if (getTolvenConfig().getLdap() == null) {
            getTolvenConfig().setLdap(new org.tolven.config.model.config.bean.ObjectFactory().createLDAPServerDetail());
        }
        return getTolvenConfig().getLdap();
    }

    public String getLDAPServerId() {
        return getLDAPServer().getId();
    }

    public void setLdapServerId(String groupId) {
        getLDAPServer().setId(groupId);
    }

    public String getLDAPServerRootUser() {
        return getLDAPServer().getUser();
    }

    public void setLDAPServerRootUser(String userId) {
        getLDAPServer().setUser(userId);
    }

    public String getLDAPServerRootPasswordId() {
        return getLDAPServer().getRootPassId();
    }

    public void setLDAPServerRootPasswordId(String passwordId) {
        getLDAPServer().setRootPassId(passwordId);
    }

    public DBServerDetail getDBServer() {
        if (getTolvenConfig().getDb() == null) {
            getTolvenConfig().setDb(new org.tolven.config.model.config.bean.ObjectFactory().createDBServerDetail());
        }
        return getTolvenConfig().getDb();
    }

    public String getDBServerId() {
        return getDBServer().getId();
    }

    public void setDBServerId(String groupId) {
        getDBServer().setId(groupId);
    }

    public void setDBServerRootUser(String userId) {
        getDBServer().setUser(userId);
    }

    public String getDBServerRootPasswordId() {
        return getDBServer().getRootPassId();
    }

    public void setDBServerRootPasswordId(String passwordId) {
        getDBServer().setRootPassId(passwordId);
    }

    public AppServerDetail getAppServer() {
        if (getTolvenConfig().getAppServer() == null) {
            getTolvenConfig().setAppServer(new org.tolven.config.model.config.bean.ObjectFactory().createAppServerDetail());
        }
        return getTolvenConfig().getAppServer();
    }

    public String getAppServerId() {
        return getAppServer().getId();
    }

    public void setAppServerId(String groupId) {
        getAppServer().setId(groupId);
    }

    public ApplicationDetail getApplication() {
        if (getTolvenConfig().getApplication() == null) {
            getTolvenConfig().setApplication(new org.tolven.config.model.config.bean.ObjectFactory().createApplicationDetail());
        }
        return getTolvenConfig().getApplication();
    }

    public WebServerDetail getWebServer() {
        if (getTolvenConfig().getWebServer() == null) {
            getTolvenConfig().setWebServer(new org.tolven.config.model.config.bean.ObjectFactory().createWebServerDetail());
        }
        return getTolvenConfig().getWebServer();
    }

    public String getWebServerId() {
        return getWebServer().getId();
    }

    public void setWebServerId(String groupId) {
        getWebServer().setId(groupId);
    }

    public MDBUserDetail getMDBUser() {
        if (getLDAPServer().getMdbuser() == null) {
            getLDAPServer().setMdbuser(new org.tolven.config.model.config.bean.ObjectFactory().createMDBUserDetail());
        }
        return getLDAPServer().getMdbuser();
    }

    public String getMDBUserId() {
        return getMDBUser().getId();
    }

    public void setMDBUserId(String groupId) {
        getMDBUser().setId(groupId);
    }

    public PasswordServerDetail getPasswordServer() {
        if (getTolvenConfig().getPasswordServer() == null) {
            getTolvenConfig().setPasswordServer(new org.tolven.config.model.config.bean.ObjectFactory().createPasswordServerDetail());
        }
        return getTolvenConfig().getPasswordServer();
    }

    public String getPasswordServerId() {
        return getPasswordServer().getId();
    }

    public void setPasswordServerId(String groupId) {
        getPasswordServer().setId(groupId);
    }

    public File getPrivateKeyFile(String credentialGroupId) {
        CertificateGroupDetail certGroup = getCredentialGroup(credentialGroupId);
        return new File(certGroup.getKey().getSource());
    }

    public File getKeyStoreFile(String credentialGroupId) {
        CertificateGroupDetail certGroup = getCredentialGroup(credentialGroupId);
        if (certGroup == null || certGroup.getKeyStore() == null) {
            return null;
        } else {
            return new File(certGroup.getKeyStore().getSource());
        }
    }

    public String getKeyStoreType(String credentialGroupId) {
        CertificateGroupDetail certGroup = getCredentialGroup(credentialGroupId);
        if (certGroup.getKeyStore() == null) {
            return null;
        } else {
            return certGroup.getKeyStore().getFormat();
        }
    }

    public KeyStore getKeyStore(String groupId, char[] password) {
        CertificateGroupDetail certGroup = getCredentialGroup(groupId);
        File keyStoreFile = getKeyStoreFile(groupId);
        if (keyStoreFile == null) {
            return null;
        } else {
            try {
                URL keyStoreURL = keyStoreFile.toURI().toURL();
                KeyStore keyStore = KeyStore.getInstance(certGroup.getKeyStore().getFormat());
                keyStore.load(keyStoreURL.openStream(), password);
                return keyStore;
            } catch (Exception ex) {
                throw new RuntimeException("Could not load the keystore for group Id " + groupId, ex);
            }
        }
    }

    public byte[] getKeyStoreByteArray(String credentialGroupId, char[] password) throws IOException, GeneralSecurityException {
        KeyStore keyStore = getKeyStore(credentialGroupId, password);
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

    public File getTrustStoreFile(String credentialGroupId) {
        TrustStoreDetail trustStoreDetail = getTrustStoreDetail(credentialGroupId);
        if (trustStoreDetail == null) {
            return null;
        } else {
            return new File(trustStoreDetail.getSource());
        }
    }

    public String getTrustStoreType(String credentialGroupId) {
        TrustStoreDetail trustStoreDetail = getTrustStoreDetail(credentialGroupId);
        if (trustStoreDetail == null) {
            return null;
        } else {
            return trustStoreDetail.getFormat();
        }
    }

    public KeyStore getTrustStore(String credentialGroupId, char[] password) throws GeneralSecurityException, IOException {
        TrustStoreDetail trustStoreDetail = getTrustStoreDetail(credentialGroupId);
        if (trustStoreDetail == null) {
            return null;
        } else {
            File trustStoreFile = getTrustStoreFile(credentialGroupId);
            URL trustStoreURL = trustStoreFile.toURI().toURL();
            KeyStore trustStore = KeyStore.getInstance(getTrustStoreType(credentialGroupId));
            trustStore.load(trustStoreURL.openStream(), password);
            return trustStore;
        }
    }

    /**
     * Returns a list of the credential groups from the credential.xml file. Active credential groups are those which
     * are referenced by the credentialGroupId of a server in the tolven-config.xml, which is active.
     * 
     * @return
     */
    public List<CertificateGroupDetail> getActiveCredentialGroupDetails() {
        Set<CertificateGroupDetail> credentialGroupDetails = new HashSet<CertificateGroupDetail>();
        CertificateGroupDetail credentialGroupDetail = null;
        //AdminClient
        credentialGroupDetail = getCredentialGroup(getAdmin().getId());
        credentialGroupDetails.add(credentialGroupDetail);
        //AdminClient TrustStores
        TrustStoreDetail adminTrustStoreDetail = getTrustStoreDetail(credentialGroupDetail.getId());
        credentialGroupDetails.addAll(getTrustedCredentialGroupDetails(adminTrustStoreDetail));
        if (getAppServer() != null) {
            //AppServer
            credentialGroupDetail = getCredentialGroup(getAppServer().getId());
            credentialGroupDetails.add(credentialGroupDetail);
            // AppServer TrustStores
            TrustStoreDetail jbossTrustStoreDetail = getTrustStoreDetail(credentialGroupDetail.getId());
            credentialGroupDetails.addAll(getTrustedCredentialGroupDetails(jbossTrustStoreDetail));
            //PasswordServer
            credentialGroupDetail = getCredentialGroup(getPasswordServer().getId());
            credentialGroupDetails.add(credentialGroupDetail);
            //DBServer
            credentialGroupDetail = getCredentialGroup(getDBServer().getId());
            credentialGroupDetails.add(credentialGroupDetail);
            //DBServer TrustStores
            TrustStoreDetail dbTrustStoreDetail = getTrustStoreDetail(credentialGroupDetail.getId());
            credentialGroupDetails.addAll(getTrustedCredentialGroupDetails(dbTrustStoreDetail));
            //LDAPServer
            credentialGroupDetail = getCredentialGroup(getLDAPServer().getId());
            credentialGroupDetails.add(credentialGroupDetail);
            //LDAPServer TrustStores
            TrustStoreDetail ldapTrustStoreDetail = getTrustStoreDetail(credentialGroupDetail.getId());
            credentialGroupDetails.addAll(getTrustedCredentialGroupDetails(ldapTrustStoreDetail));
        }
        return new ArrayList<CertificateGroupDetail>(credentialGroupDetails);
    }

    /**
     * Return each CredentialGroupDetail which is trusted by trustStoreDetail
     * 
     * @param trustStoreDetail
     * @return
     */
    private Set<CertificateGroupDetail> getTrustedCredentialGroupDetails(TrustStoreDetail trustStoreDetail) {
        Set<CertificateGroupDetail> credentialGroupDetails = new HashSet<CertificateGroupDetail>();
        CertificateGroupDetail credentialGroupDetail = null;
        for (TrustStoreCertificateDetail trustStoreCertificateDetail : trustStoreDetail.getCertificate()) {
            credentialGroupDetail = getCredentialGroup(trustStoreCertificateDetail.getRefId());
            credentialGroupDetails.add(credentialGroupDetail);
        }
        return credentialGroupDetails;
    }

    /**
     * Returns a list of the truststores in the credential.xml file which are referenced by an active server or client in tolven-config.xml
     * @return
     */
    public List<TrustStoreDetail> getActiveTrustStoreDetails() {
        Set<TrustStoreDetail> trustStoreDetails = new HashSet<TrustStoreDetail>();
        TrustStoreDetail trustStoreDetail = null;
        //AdminClient
        trustStoreDetail = getTrustStoreDetail(getAdmin().getId());
        if (trustStoreDetail != null) {
            trustStoreDetails.add(trustStoreDetail);
        }
        if (getAppServer() != null) {
            //AppServer
            trustStoreDetail = getTrustStoreDetail(getAppServer().getId());
            if (trustStoreDetail != null) {
                trustStoreDetails.add(trustStoreDetail);
            }
            //DBServer
            trustStoreDetail = getTrustStoreDetail(getDBServer().getId());
            if (trustStoreDetail != null) {
                trustStoreDetails.add(trustStoreDetail);
            }
            //LDAPServer
            trustStoreDetail = getTrustStoreDetail(getLDAPServer().getId());
            if (trustStoreDetail != null) {
                trustStoreDetails.add(trustStoreDetail);
            }
        }
        return new ArrayList<TrustStoreDetail>(trustStoreDetails);
    }

    public X509Certificate getX509Certificate(CertificateGroupDetail certGroup) {
        if (certGroup.getCertificate() != null) {
            try {
                CertificateFactory certFactory = CertificateFactory.getInstance("X509");
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(new File(certGroup.getCertificate().getSource()));
                    return (X509Certificate) certFactory.generateCertificate(fis);
                } finally {
                    if (fis != null)
                        fis.close();
                }
            } catch (Exception ex) {
                throw new RuntimeException("Could not get X509 Certificate for group: " + certGroup.getId(), ex);
            }
        } else if (certGroup.getKeyStore() != null) {
            try {
                CertificateKeyStoreDetail keyStoreDetail = certGroup.getKeyStore();
                char[] password = getPasswordHolder().getPassword(certGroup.getId());
                KeyStore keyStore = getKeyStore(password, keyStoreDetail);
                /*
                 * Assuming that although there may potentially be multiple certificates in the keyStore,
                 * there is only one key, and thus one certificate associated with that key
                 */
                Enumeration<String> enumeration = keyStore.aliases();
                String keyEntryAlias = null;
                while (enumeration.hasMoreElements()) {
                    String alias = enumeration.nextElement();
                    if (keyStore.isKeyEntry(alias)) {
                        keyEntryAlias = alias;
                        break;
                    }
                }
                if (keyEntryAlias == null) {
                    throw new RuntimeException("Group " + certGroup.getId() + "contains a keystore with no certificates");
                }
                return (X509Certificate) keyStore.getCertificate(keyEntryAlias);
            } catch (Exception ex) {
                throw new RuntimeException("Could not get X509 Certificate for group: " + certGroup.getId(), ex);
            }
        } else {
            return null;
        }
    }

    public KeyStore getKeyStore(CertificateGroupDetail certGroupDetail) {
        char[] keystorepass = getPasswordHolder().getPassword(certGroupDetail.getId());
        return getKeyStore(keystorepass, certGroupDetail.getKeyStore());
    }

    public KeyStore getKeyStore(char[] keystorepass, CertificateKeyStoreDetail certKeyStoreDetail) {
        File keyStoreFile = new File(certKeyStoreDetail.getSource());
        if (!keyStoreFile.exists()) {
            throw new RuntimeException("Cannot find KeyStore file: " + keyStoreFile.getPath());
        }
        return getKeyStore(keystorepass, keyStoreFile, certKeyStoreDetail.getFormat());
    }

    public KeyStore getKeyStore(char[] keystorepass, File keyStoreFile, String keyStoreType) {
        if (!keyStoreFile.exists()) {
            throw new RuntimeException("Cannot find KeyStore file: " + keyStoreFile.getPath());
        }
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            FileInputStream in = null;
            try {
                in = new FileInputStream(keyStoreFile);
                keyStore.load(in, keystorepass);
            } finally {
                if (in != null)
                    in.close();
            }
            return keyStore;
        } catch (Exception ex) {
            String message = "";
            if (keystorepass == null) {
                message = " (no password supplied)";
            }
            throw new RuntimeException("Could not load keystore from: " + keyStoreFile.getPath() + message, ex);
        }
    }

    public Set<X509Certificate> getX509Certificates(File trustStoreFile) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X509");
            FileInputStream in = null;
            try {
                in = new FileInputStream(trustStoreFile);
                Set<X509Certificate> certs = new HashSet<X509Certificate>();
                for (Certificate cert : certFactory.generateCertificates(in)) {
                    certs.add((X509Certificate) cert);
                }
                return certs;
            } finally {
                if (in != null)
                    in.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not load certificate from: " + trustStoreFile.getPath(), ex);
        }
    }

    public KeyStore getTrustStore(char[] truststorepass, TrustStoreDetail trustStoreDetail) throws IOException, GeneralSecurityException {
        KeyStore keyStore = KeyStore.getInstance(trustStoreDetail.getFormat());
        FileInputStream in = null;
        try {
            in = new FileInputStream(new File(trustStoreDetail.getSource()));
            keyStore.load(in, truststorepass);
        } finally {
            if (in != null)
                in.close();
        }
        return keyStore;
    }

    public TrustStoreDetail getTrustStoreDetail(String credentialGroupId) {
        for (TrustStoreDetail trustStore : getTrustStoreDetails()) {
            if (trustStore.getId().equals(credentialGroupId)) {
                return trustStore;
            }
        }
        return null;
    }

    private List<TrustStoreDetail> getTrustStoreDetails() {
        return getCertificateInfoDetail().getTrustStore();
    }

    public CertificateGroupDetail getCredentialGroup(String groupId) {
        for (CertificateGroupDetail certGroupDetail : getCredentialGroupDetails()) {
            if (certGroupDetail.getId().equals(groupId))
                return certGroupDetail;
        }
        return null;
    }

    private Credentials initializeCredentials() {
        try {
            InputStream fis = null;
            try {
                fis = getClass().getResourceAsStream(CREDENTIAL_XML);
                JAXBContext jc = JAXBContext.newInstance(CREDENTIALS_PACKAGE, getClass().getClassLoader());
                Unmarshaller u = jc.createUnmarshaller();
                Credentials credentials = (Credentials) u.unmarshal(fis);
                return credentials;
            } catch (JAXBException ex) {
                throw new RuntimeException("Could not initialize credentials from " + CREDENTIAL_XML, ex);
            } finally {
                if (fis != null)
                    fis.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not initialize credentials from " + CREDENTIAL_XML, ex);
        }
    }

    private TolvenConfig initializeTolvenConfig() {
        try {
            InputStream fis = null;
            try {
                fis = getClass().getResourceAsStream(TOLVEN_CONFIG_XML);
                JAXBContext jc = JAXBContext.newInstance(TOLVENCONFIG_PACKAGE, getClass().getClassLoader());
                Unmarshaller u = jc.createUnmarshaller();
                TolvenConfig tolvenConfig = (TolvenConfig) u.unmarshal(fis);
                return tolvenConfig;
            } catch (JAXBException ex) {
                throw new RuntimeException("Could not initialize configuration from " + TOLVEN_CONFIG_XML, ex);
            } finally {
                if (fis != null)
                    fis.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not initialize configuration from " + TOLVEN_CONFIG_XML, ex);
        }
    }

    public Map<String, String> getDeprecatedCredentialIdMap() {
        Map<String, String> deprecatedCredentialIdMap = new HashMap<String, String>();
        for (CertificateGroupDetail certGroupDetail : getCredentialGroupDetails()) {
            //deprecatedCredentialIdMap.put(certGroupDetail.getKey().getId(), certGroupDetail.getId());
            if (certGroupDetail.getKeyStore() != null) {
                deprecatedCredentialIdMap.put(certGroupDetail.getKeyStore().getId(), certGroupDetail.getId());
            }
        }
        for (TrustStoreDetail trustStoreDetail : getCertificateInfoDetail().getTrustStore()) {
            deprecatedCredentialIdMap.put(trustStoreDetail.getId(), trustStoreDetail.getId());
        }
        return deprecatedCredentialIdMap;
    }

    public void addCredentialGroup(String groupId, File keyFile, String keyType, File certFile) {
        addCredentialGroup(groupId, keyFile, keyType, certFile, null, null, null, null);
    }

    public void addCredentialGroup(String groupId, File keyStoreFile, String keyStoreType, File trustStoreFile, String trustStoreType) {
        addCredentialGroup(groupId, null, null, null, keyStoreFile, keyStoreType, trustStoreFile, trustStoreType);
    }

    public void addCredentialGroup(String groupId, File keyFile, String keyType, File certFile, File keyStoreFile, String keyStoreType, File trustStoreFile, String trustStoreType) {
        CertificateGroupDetail group = createCredentialGroup(groupId, keyFile, certFile, keyStoreFile);
        if (keyFile != null) {
            CertificateKeyDetail key = group.getKey();
            key.setSource(keyFile.getPath());
            key.setFormat(keyType);
        }
        if (certFile != null) {
            CertificateDetail cert = group.getCertificate();
            cert.setSource(certFile.getPath());
            cert.setFormat(keyType);
        }
        if (keyStoreFile != null) {
            CertificateKeyStoreDetail keyStore = group.getKeyStore();
            keyStore.setSource(keyStoreFile.getPath());
            keyStore.setFormat(keyStoreType);
        }
        if (trustStoreFile != null) {
            TrustStoreDetail trustStore = createTrustStore(groupId);
            trustStore.setSource(trustStoreFile.getPath());
            trustStore.setFormat(trustStoreType);
        }
    }

    public void setTrustedGroups(String groupId, String[] trustedGroups) {
        TrustStoreDetail trustStore = getTrustStoreDetail(groupId);
        if (trustStore == null) {
            throw new RuntimeException("Could not find trustore for group: " + groupId);
        }
        for (String trustedGroup : trustedGroups) {
            addTrustedGroup(trustedGroup, trustStore);
        }
    }

    private void addTrustedGroup(String groupId, TrustStoreDetail trustStore) {
        for (TrustStoreCertificateDetail cert : trustStore.getCertificate()) {
            if (cert.getRefId().equals(groupId)) {
                throw new RuntimeException("Certificate already exists in trustStore " + trustStore.getId() + "with groupId: " + groupId);
            }
        }
        TrustStoreCertificateDetail trustStoreCertificate = new org.tolven.config.model.credential.bean.ObjectFactory().createTrustStoreCertificateDetail();
        trustStoreCertificate.setRefId(groupId);
        trustStore.getCertificate().add(trustStoreCertificate);
    }

    private CertificateGroupDetail createCredentialGroup(String groupId, File keyFile, File certFile, File keyStoreFile) {
        if (getCredentialGroup(groupId) != null) {
            throw new RuntimeException("Group Id '" + groupId + "' already exists in the configuration");
        }
        org.tolven.config.model.credential.bean.ObjectFactory objFactory = new org.tolven.config.model.credential.bean.ObjectFactory();
        CertificateGroupDetail certGroup = objFactory.createCertificateGroupDetail();
        certGroup.setId(groupId);
        certGroup.setCommonName("tolven-" + groupId + "-commonName");
        certGroup.setOrganizationUnitName("Tolven Demo");
        certGroup.setOrganizationName("Tolven Healthcare Innovations");
        certGroup.setStateOrProvince("CA");
        certGroup.setCountryName("US");
        certGroup.setEmail("tolven-" + groupId + "@tolvendev.com");
        if (certFile != null) {
            CertificateDetail cert = objFactory.createCertificateDetail();
            cert.setId(groupId + "." + groupId + "-password-cert");
            cert.setCaRefId(groupId + "." + groupId + "-password-cert");
            cert.setFormat(TOLVEN_CREDENTIAL_FORMAT_PEM);
            certGroup.setCertificate(cert);
        }
        if (keyFile != null) {
            CertificateKeyDetail key = objFactory.createCertificateKeyDetail();
            key.setId(groupId + "." + groupId + "-password-key");
            key.setFormat(TOLVEN_CREDENTIAL_FORMAT_PEM);
            certGroup.setKey(key);
        }
        if (keyStoreFile != null) {
            CertificateKeyStoreDetail keyStore = objFactory.createCertificateKeyStoreDetail();
            keyStore.setId(groupId + "." + groupId + "-keystore");
            keyStore.setFormat(TOLVEN_CREDENTIAL_FORMAT_JKS);
            certGroup.setKeyStore(keyStore);
        }
        getCertificateInfoDetail().getGroup().add(certGroup);
        return certGroup;
    }

    private TrustStoreDetail createTrustStore(String groupId) {
        if (getTrustStoreDetail(groupId) != null) {
            throw new RuntimeException("TrustStore with credential group Id '" + groupId + "' already exists in the configuration");
        }
        org.tolven.config.model.credential.bean.ObjectFactory objFactory = new org.tolven.config.model.credential.bean.ObjectFactory();
        TrustStoreDetail trustStore = objFactory.createTrustStoreDetail();
        trustStore.setId(groupId);
        trustStore.setFormat(TOLVEN_CREDENTIAL_FORMAT_JKS);
        getCertificateInfoDetail().getTrustStore().add(trustStore);
        return trustStore;
    }

    /**
     * The passwordHolder can only be loaded if the admin keystore already exists
     * 
     * @param adminGroupId
     * @param adminPassword
     */
    public PasswordHolder loadPasswordHolder(String adminGroupId, char[] adminPassword) {
        try {
            File keyStoreFile = getAdminKeyStoreFile();
            if (!keyStoreFile.exists()) {
                throw new RuntimeException("KeyStore file does not exist '" + keyStoreFile.getPath() + "'");
            }
            File secretKeyFile = getAdminSecretKeyFile();
            File passwordStoreFile = getAdminPasswordFile();
            if (!secretKeyFile.exists() && passwordStoreFile.exists()) {
                throw new RuntimeException("The password store '" + passwordStoreFile.getPath() + "' exists with no corresponding secret key: '" + secretKeyFile.getPath() + "'");
            }
            PasswordHolder passwordHolder = new PasswordHolder();
            passwordHolder.init(adminGroupId, adminPassword, keyStoreFile, secretKeyFile, passwordStoreFile);
            passwordHolder.setDeprecatedCredentialIdMap(getDeprecatedCredentialIdMap());
            passwordHolder.loadPasswordStore();
            setPasswordHolder(passwordHolder);
            return getPasswordHolder();
        } catch (Exception ex) {
            getPasswordHolder().clearAllPasswords();
            throw new RuntimeException("Could not load password holder for groupId: '" + adminGroupId + "'", ex);
        }
    }

    public void initializeJSSE() {
        initializeJSSE(getAdminId(), getPasswordHolder().getPassword(getAdminId()));
    }

    public void initializeJSSE(String userId, char[] password) {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            logger.info("Load JSSE keystore for " + userId);
            KeyStore keyStore = getKeyStore(userId, password);
            if (keyStore == null) {
                throw new RuntimeException("A keystore is required by groupId " + userId + " for SSL");
            } else {
                keyManagerFactory.init(keyStore, password);
                KeyManager[] keyManager = keyManagerFactory.getKeyManagers();
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                logger.info("Load JSSE truststore for " + userId);
                KeyStore trustStore = getTrustStore(userId, null);
                if (trustStore == null) {
                    throw new RuntimeException("A truststore is required by groupId " + userId + " for SSL");
                }
                trustManagerFactory.init(trustStore);
                TrustManager[] trustManager = trustManagerFactory.getTrustManagers();
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(keyManager, trustManager, null);
                SSLContext.setDefault(sslContext);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not initialize JSSE for user " + userId, ex);
        }
    }

}