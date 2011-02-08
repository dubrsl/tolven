package org.tolven.client.examples.ws.document;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.namespace.QName;

import org.tolven.client.examples.ws.common.HeaderHandlerResolver;
import org.tolven.client.examples.ws.document.jaxws.Document;
import org.tolven.client.examples.ws.document.jaxws.DocumentService;

/**
 * Submit a CCR document from the file name specified in the first command-line parameter
 * to the accountId specified in the second command line parameter from the userId specified
 * in the third command line parameter.
 * 
 * This is an example class, and when first checked out of cvs will have compiler errors until the generated
 * classes it imports have been generated. These are generated using the generate-ws-clients ant target in
 * tolvenWSClient/build.xml.
 * 
 * <pre>
 * SubmitCCR test3.ccr.xml 32700 11500
 * </pre>
 * @author John Churin
 *
 */
public class DocumentWebServiceClient {

    private static final String CCRns = "urn:astm-org:CCR";

    private String username;
    private char[] password;
    private String wsdl;
    private int expiresInSeconds;

    public DocumentWebServiceClient(String username, char[] password, String wsdl, int expiresInSeconds) {
        this.username = username;
        this.password = password;
        this.wsdl = wsdl;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getWsdl() {
        return wsdl;
    }

    public void setWsdl(String wsdl) {
        this.wsdl = wsdl;
    }

    public int getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(int expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    private String getXML(File file) {
        ByteArrayOutputStream xml = null;
        try {
            InputStream input = new BufferedInputStream(new FileInputStream(file));
            xml = new ByteArrayOutputStream();
            int b;
            while ((b = input.read()) >= 0) {
                xml.write(b);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not read file: " + file.getPath(), ex);
        }
        try {
            return new String(xml.toByteArray(), "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException("Could not convert file to UTF-8 bytes: " + file.getPath(), ex);
        }
    }

    private Document getDocumentPort() {
        URL url = null;
        try {
            url = new URL(getWsdl());
        } catch (Exception ex) {
            throw new RuntimeException("Could not convert to URL: " + getWsdl(), ex);
        }
        DocumentService service = new DocumentService(url, new QName("http://tolven.org/document", "DocumentService"));
        HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver(getUsername(), getPassword(), getExpiresInSeconds());
        service.setHandlerResolver(handlerResolver);
        return service.getDocumentPort();
    }

    public String testWS() {
        return getDocumentPort().test();
    }

    public String queueMessage(String filename, String accountIdString) {
        long accountId = Long.parseLong(accountIdString);
        return queueMessage(getXML(new File(filename)), accountId);
    }

    public long processDocument(String filename, String accountIdString) {
        long accountId = Long.parseLong(accountIdString);
        return processDocument(getXML(new File(filename)), accountId);
    }

    public String queueMessage(String xml, long accountId) {
        return getDocumentPort().queueMessage(xml.getBytes(), CCRns, accountId);
    }

    public long processDocument(String xml, long accountId) {
        return getDocumentPort().processDocument(xml.getBytes(), CCRns, accountId);
    }

    /**
     * 
     * A typical invokation of this class would be:
     * 
     * java -Djavax.net.ssl.keyStore=keyStoreFile -Djavax.net.ssl.trustStore=trustStoreFile org.tolven.client.examples.ws.document.DocumentWebServiceClient http://dev.able.com:8080/ws/DocumentService?wsdl filename accountIdNumber userIdNumber
     * Note that the common name in the wsdl is checked against that returned from the appserver's webserver certificate, and since there is no oppotunity to veto, like in a browser,
     * it must match. One way to do this is to temporarily add the following entry to your hosts file
     * <pre>
     * 127.0.0.1 dev.able.com
     * </pre>
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: java -Djavax.net.ssl.keyStore=keyStoreFile -Djavax.net.ssl.keyStorePassword=tolven -Djavax.net.ssl.trustStore=trustStoreFile org.tolven.client.examples.ws.document.DocumentWebServiceClient https://dev.able.com:8443/ws/DocumentService?wsdl filename accountIdNumber");
            System.exit(1);
        }
        String wsdl = args[0];
        String filename = args[1];
        String accountId = args[2];
        System.out.print("Please enter username: ");
        String username = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.print("Please enter user password: ");
        char[] password = System.console().readPassword();
        DocumentWebServiceClient client = new DocumentWebServiceClient(username, password, wsdl, 60);
        String result = client.queueMessage(filename, accountId);
        System.out.println("Message queue result: " + result);
    }
}
