package test.org.tolven.security.key;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import junit.framework.TestCase;

import org.tolven.security.CertificateHelper;
import org.tolven.security.key.AccountPrivateKey;
import org.tolven.security.key.AccountSecretKey;

public class DocumentEncryptionTestCase extends TestCase {

    /*
     * Test method for 'test.org.tolven.security.key.TestCaseDocument.init(byte[], PublicKey, PrivateKey, String)'
     */
    public void testInit() throws GeneralSecurityException, IOException {
        SecurityTestSuite.initProperties();
        byte[] unencryptedDocument = "The quick brown fox jumps over a lazy dog.".getBytes();
        char[] password = "somepassword".toCharArray();
        AccountPrivateKey accountPrivateKey = AccountPrivateKey.getInstance();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, password);
        CertificateHelper certHelper = new CertificateHelper();
        PublicKey theUserPublicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        PrivateKey theUserPrivateKey = certHelper.getPrivateKey(keyStore, password);
        String privateKeyAlgorithm = System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
        int privateKeyLength = Integer.parseInt(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
        String kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        PublicKey accountPublicKey = accountPrivateKey.init(theUserPublicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
        TestCaseDocument testCaseDocument = new TestCaseDocument();
        testCaseDocument.init(unencryptedDocument, accountPublicKey, theUserPrivateKey);
    }

    /*
     * Test method for 'test.org.tolven.security.key.TestCaseDocument.verify(PublicKey, PrivateKey)'
     */
    public void testVerify() throws GeneralSecurityException, IOException {
        SecurityTestSuite.initProperties();
        byte[] unencryptedDocument = "The quick brown fox jumps over a lazy dog.".getBytes();
        char[] password = "somepassword".toCharArray();
        AccountPrivateKey accountPrivateKey = AccountPrivateKey.getInstance();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, password);
        CertificateHelper certHelper = new CertificateHelper();
        PublicKey theUserPublicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        PrivateKey theUserPrivateKey = certHelper.getPrivateKey(keyStore, password);
        String privateKeyAlgorithm = System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
        int privateKeyLength = Integer.parseInt(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
        String kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        PublicKey accountPublicKey = accountPrivateKey.init(theUserPublicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
        TestCaseDocument testCaseDocument = new TestCaseDocument();
        testCaseDocument.init(unencryptedDocument, accountPublicKey, theUserPrivateKey);
        boolean verified = testCaseDocument.verify(theUserPublicKey, accountPrivateKey.getPrivateKey(theUserPrivateKey));
        assertTrue(verified);
    }

    /*
     * Test method for 'test.org.tolven.security.key.TestCaseDocument.decryptDocument(PrivateKey)'
     */
    public void testDecryptDocument() throws GeneralSecurityException, IOException {
        SecurityTestSuite.initProperties();
        byte[] unencryptedDocument = "The quick brown fox jumps over a lazy dog.".getBytes();
        char[] password = "somepassword".toCharArray();
        AccountPrivateKey accountPrivateKey = AccountPrivateKey.getInstance();
        KeyStore keyStore = CertificateHelperTestCase.getUserPKCS12(CertificateHelperTestCase.UID, password);
        CertificateHelper certHelper = new CertificateHelper();
        PublicKey theUserPublicKey = certHelper.getX509Certificate(keyStore).getPublicKey();
        PrivateKey theUserPrivateKey = certHelper.getPrivateKey(keyStore, password);
        String privateKeyAlgorithm = System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_ALGORITHM_PROP);
        int privateKeyLength = Integer.parseInt(System.getProperty(AccountPrivateKey.ACCOUNT_PRIVATE_KEY_LENGTH_PROP));
        String kbeKeyAlgorithm = System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(System.getProperty(AccountSecretKey.ACCOUNT_USER_KBE_KEY_LENGTH));
        PublicKey accountPublicKey = accountPrivateKey.init(theUserPublicKey, privateKeyAlgorithm, privateKeyLength, kbeKeyAlgorithm, kbeKeyLength);
        TestCaseDocument testCaseDocument = new TestCaseDocument();
        testCaseDocument.init(unencryptedDocument, accountPublicKey, theUserPrivateKey);
        byte[] decryptedDocument = testCaseDocument.decryptDocument(accountPrivateKey.getPrivateKey(theUserPrivateKey));
        assertTrue(new String(unencryptedDocument).equals(new String(decryptedDocument)));
    }

}
