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
package org.tolven.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

import javax.naming.InvalidNameException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.tolven.security.key.UserPrivateKey;

/**
 * This class provides functionality for creating certificates
 * 
 * @author Joseph Isaac
 *
 */
public class CertificateHelper {

    public static final String TOLVEN_CREDENTIAL_FORMAT_PKCS12 = "pkcs12";

    private SecureRandom secureRandom;

    public CertificateHelper() {
        //TODO This is not the right place to add a provider. It should be a one time initialization for the EJB tier
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public void createCredentials(TolvenPerson tolvenPerson) {
        KeyStore keyStore = createPKCS12KeyStore(tolvenPerson.getUid(), tolvenPerson.getCn(), tolvenPerson.getOrganizationUnitName(), tolvenPerson.getOrganizationName(), tolvenPerson.getStateOrProvince(), tolvenPerson.getCountryName(), tolvenPerson.getUserPassword().toCharArray());

        byte[] userCertificate = getX509CertificateByteArray(keyStore);
        tolvenPerson.setUserCertificate(userCertificate);
        byte[] userPKCS12 = CertificateHelper.toByteArray(keyStore, tolvenPerson.getUserPassword().toCharArray());
        tolvenPerson.setUserPKCS12(userPKCS12);
    }

    /**
     * Return the X509Certificate of the first alias in the keyStore
     * 
     * @param keyStore
     * @return
     */
    public X509Certificate getX509Certificate(KeyStore keyStore) {
        String alias = null;
        try {
            Enumeration<String> aliases = keyStore.aliases();
            if (!aliases.hasMoreElements()) {
                throw new RuntimeException("KeyStore contains no aliases");
            }
            alias = aliases.nextElement();
        } catch (KeyStoreException ex) {
            throw new RuntimeException("Could obtain alias: " + alias + " in the userPKCS12 keystore", ex);
        }
        try {
            Certificate[] certificateChain = keyStore.getCertificateChain(alias);
            if (certificateChain == null || certificateChain.length == 0) {
                throw new RuntimeException("KeyStore contains no certificate with alias " + alias);
            }
            return (X509Certificate) certificateChain[0];
        } catch (KeyStoreException ex) {
            throw new RuntimeException("Could not obtain X509Certificate from userPKCS12 keystore using alias: " + alias, ex);
        }
    }

    public PrivateKey getPrivateKey(KeyStore keyStore, char[] password) {
        String alias = null;
        try {
            Enumeration<String> aliases = keyStore.aliases();
            if (!aliases.hasMoreElements()) {
                throw new RuntimeException("KeyStore contains no aliases");
            }
            alias = aliases.nextElement();
        } catch (KeyStoreException ex) {
            throw new RuntimeException("Could obtain alias: " + alias + " in the userPKCS12 keystore", ex);
        }
        try {
            return (PrivateKey) keyStore.getKey(alias, password);
        } catch (Exception ex) {
            throw new RuntimeException("Could not get PrivateKey from KeyStore using alias: " + alias, ex);
        }
    }

    /**
     * Return the X509Certificate as a byte[] of the first alias in the keyStore
     * 
     * @param keyStore
     * @return
     */
    public byte[] getX509CertificateByteArray(KeyStore keyStore) {
        X509Certificate x509Certificate = getX509Certificate(keyStore);
        try {
            return x509Certificate.getEncoded();
        } catch (CertificateEncodingException ex) {
            throw new RuntimeException("Could not get encoded bytes from X509Certificate", ex);
        }
    }

    public KeyStore createPKCS12KeyStore(String uid, String cn, String organizationUnitName, String organizationName, String stateOrProvince, String countryName, char[] password) {
        X509CertificatePrivateKeyPair x509CertificatePrivateKeyPair = createX509CertificatePrivateKeyPair(uid, cn, organizationUnitName, organizationName, stateOrProvince, countryName);
        return x509CertificatePrivateKeyPair.createPKCS12KeyStore(uid, password);
    }

    private X509CertificatePrivateKeyPair createX509CertificatePrivateKeyPair(String email, String commonName, String organizationUnitName, String organizationName, String stateOrProvince, String countryName) {
        String privateKeyAlgorithm = System.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(privateKeyAlgorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Could not get KeyPairGenerator for algorithm: " + privateKeyAlgorithm, ex);
        }
        int keySize = Integer.parseInt(System.getProperty(UserPrivateKey.USER_PRIVATE_KEY_LENGTH_PROP));
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        X500Principal x500Principal = getX500Principal(email, commonName, organizationUnitName, organizationName, stateOrProvince, countryName);
        return createSelfSignedCertificate(x500Principal, keyPair.getPublic(), keyPair.getPrivate());
    }

    private X509CertificatePrivateKeyPair createSelfSignedCertificate(X500Principal subjectX500Principal, PublicKey publicKey, PrivateKey privateKey) {
        X509Certificate certificate = signCertificate(subjectX500Principal, publicKey, subjectX500Principal, privateKey);
        return new X509CertificatePrivateKeyPair(certificate, privateKey);
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

    private SecureRandom getSecureRandom() {
        //TODO: This may be better placed where it only gets created once for certain
        if (secureRandom == null)
            try {
                secureRandom = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException("Could not obtain a SecureRandom instance", ex);
            }
        return secureRandom;
    }

    public static X500Principal getX500Principal(String email, String commonName, String organizationUnitName, String organizationName, String stateOrProvince, String countryName) {
        if (null == email || null == commonName || null == organizationUnitName || null == organizationName || null == stateOrProvince || null == countryName) {
            throw new RuntimeException("Certificate requires EmailAddress, Common Name, organizationUnitName, organizationName, stateOrProvince, and countryName");
        }
        Attributes attributes = new BasicAttributes();
        attributes.put(X509Name.EmailAddress.toString(), email);
        attributes.put(X509Name.CN.toString(), commonName);
        attributes.put(X509Name.OU.toString(), organizationUnitName);
        attributes.put(X509Name.O.toString(), organizationName);
        attributes.put(X509Name.ST.toString(), stateOrProvince);
        attributes.put(X509Name.C.toString(), countryName);
        Rdn rdn;
        try {
            rdn = new Rdn(attributes);
        } catch (InvalidNameException ex) {
            throw new RuntimeException("Failed to obtain a Relative Distinguised Name", ex);
        }
        return new X500Principal(rdn.toString());
    }

    private X509Certificate signCertificate(X500Principal subjectX500Principal, PublicKey subjectPublicKey, X500Principal issuerX500Principal, PrivateKey issuerPrivateKey) {
        X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
        gen.setSignatureAlgorithm("SHA1withRSA");
        gen.setSubjectDN(subjectX500Principal);
        gen.setSerialNumber(getNextSerialNumber());
        gen.setIssuerDN(issuerX500Principal);
        gen.setNotBefore(new Date());
        gen.setNotAfter(new Date(new Date().getTime() + 1000000000000L));
        gen.setPublicKey(subjectPublicKey);
        try {
            return gen.generate(issuerPrivateKey);
        } catch (Exception e) {
            throw new RuntimeException("Could not sign cerfificate for: " + subjectX500Principal.getName(), e);
        }
    }

    public static byte[] toByteArray(KeyStore keyStore, char[] password) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            try {
                keyStore.store(baos, password);
            } catch (Exception ex) {
                throw new RuntimeException("Could not store keystore", ex);
            }
            byte[] byteArr = baos.toByteArray();
            return byteArr;
        } finally {
            if (baos != null)
                try {
                    baos.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Could not close bytearrayoutputstream for keystore", ex);
                }
        }
    }

    public static X509Certificate getX509Certificate(byte[] bytes) {
        //return (X509Certificate) getPEMObject(bytes);
        X509Certificate x509Certificate = null;
        ByteArrayInputStream bis = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                x509Certificate = (X509Certificate) certificateFactory.generateCertificate(bis);
            } catch (CertificateException ex) {
                throw new RuntimeException("Could not generate an X509 certificate", ex);
            }
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Could not close bytearrayinputstream after generating an X509 certificate", ex);
                }
            }
        }
        return x509Certificate;
    }

    public static KeyStore getKeyStore(byte[] bytes, char[] password) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            try {
                KeyStore keyStore = KeyStore.getInstance(TOLVEN_CREDENTIAL_FORMAT_PKCS12);
                keyStore.load(bais, password);
                return keyStore;
            } catch (Exception ex) {
                throw new RuntimeException("Could not load keystore", ex);
            }
        } finally {
            if (bais != null)
                try {
                    bais.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Could not close bytearrayinputstream after loading keystore", ex);
                }
        }
    }

    public static void changeKeyStorePassword(KeyStore keyStore, String alias, char[] oldPassword, char[] newPassword) {
        try {
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, oldPassword);
            keyStore.setKeyEntry(alias, privateKey, newPassword, keyStore.getCertificateChain(alias));
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException("Could not change the keystore password for with alias: " + alias, ex);
        }
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

        public byte[] getX509CertificateByteArray() {
            try {
                return getX509Certificate().getEncoded();
            } catch (CertificateEncodingException ex) {
                throw new RuntimeException("Could not get encoded byte[] from an X509 certificate", ex);
            }
        }

        public KeyStore createPKCS12KeyStore(String alias, char[] password) {
            // We can't use Bouncy Castle here because it requires unrestricted strength
            // which Tolven does not require (although it is allowed). 
            try {
                KeyStore keyStore = KeyStore.getInstance(CertificateHelper.TOLVEN_CREDENTIAL_FORMAT_PKCS12);
                try {
                    keyStore.load(null, password);
                } catch (IOException ex) {
                    throw new RuntimeException("Could not load keystore for alias: " + alias, ex);
                }
                X509Certificate[] certificateChain = new X509Certificate[1];
                certificateChain[0] = getX509Certificate();
                keyStore.setKeyEntry(alias, getPrivateKey(), password, certificateChain);
                return keyStore;
            } catch (GeneralSecurityException ex) {
                throw new RuntimeException("Could not create a PKCS12 keystore for alias: " + alias, ex);
            }
        }

        public byte[] createPKCS12KeyStoreByteArray(String alias, char[] password) {
            KeyStore keyStore = createPKCS12KeyStore(alias, password);
            return CertificateHelper.toByteArray(keyStore, password);
        }

    }

}
