package org.tolven.client.examples.ws.trim;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;

import javax.xml.namespace.QName;

import org.tolven.client.examples.ws.common.HeaderHandlerResolver;
import org.tolven.client.examples.ws.trim.jaxws.Trim;
import org.tolven.client.examples.ws.trim.jaxws.TrimService;
import org.tolven.client.examples.ws.trim.jaxws.WebServiceField;
import org.tolven.client.examples.ws.trim.jaxws.WebServiceTrim;

/**
 * Submit a Trim via a WebService
 * 
 * This is an example class, and when first checked out of cvs will have compiler errors until the generated
 * classes it imports have been generated. These are generated using the generate-ws-clients ant target in
 * tolvenWSClient/build.xml.
 * 
 * @author John Churin
 *
 */
public class TrimWebServiceClient {

    private final String[][] trimData = {
            { "mrn", "M00000123" },
            { "firstName", "Able" },
            { "lastName", "Baker" },
            { "homeAddr1", "123 Elm Street" },
            { "homeAddr2", null },
            { "homeCity", "Anywhere" },
            { "homeState", "NY" },
            { "homeZip", "98765 - 4321" },
            { "homeCountry", "US" },
            { "homeTelecom", "707-123-4567" } };

    private String username;
    private char[] password;
    private String wsdl;
    private int expiresInSeconds;

    public TrimWebServiceClient() {
    }

    public TrimWebServiceClient(String username, char[] password, String wsdl, int expiresInSeconds) {
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

    public void submit(String trimName, long accountId) {
        WebServiceTrim webServiceTrim = new WebServiceTrim();
        webServiceTrim.setName(trimName);
        webServiceTrim.setAccountId(accountId);
        WebServiceField field = new WebServiceField();

        for (String[] data : trimData) {
            field = new WebServiceField();
            field.setName(data[0]);
            field.setValue(data[1]);
            webServiceTrim.getFields().add(field);
        }
        /*
         * Date of Birth
         */
        field = new WebServiceField();
        field.setName("dob");
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1957, 07, 23, 0, 0, 0);
        field.setValue(cal.getTime());
        webServiceTrim.getFields().add(field);
        URL url = null;
        try {
            url = new URL(getWsdl());
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not convert to URL: " + getWsdl(), ex);
        }
        TrimService service = new TrimService(url, new QName("http://tolven.org/trim", "TrimService"));
        HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver(getUsername(), getPassword(), getExpiresInSeconds());
        service.setHandlerResolver(handlerResolver);
        Trim port = service.getTrimPort();
        port.submitTrim(webServiceTrim);
    }

    /**
     * Generate a CCR file based on a randomly generated person
     * A typical invokation of this class would be:
     * 
     * java -Djavax.net.ssl.keyStore=keyStoreFile -Djavax.net.ssl.trustStore=trustStoreFile org.tolven.client.examples.ws.trim.TrimWebServiceClient http://dev.able.com:8080/ws/trim?wsdl
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
            System.out.println("Usage: java -Djavax.net.ssl.keyStore=keyStoreFile -Djavax.net.ssl.keyStorePassword=tolven -Djavax.net.ssl.trustStore=trustStoreFile org.tolven.client.examples.ws.trim.TrimWebServiceClient https://dev.able.com:8443/ws/trim?wsdl trimName accountId");
            System.exit(1);
        }
        String wsdl = args[0];
        String trimName = args[1];
        long accountId = Long.parseLong(args[2]);
        System.out.print("Please enter username: ");
        String username = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.print("Please enter user password: ");
        char[] password = System.console().readPassword();
        TrimWebServiceClient submitTrim = new TrimWebServiceClient(username, password, wsdl, 60);
        submitTrim.submit(trimName, accountId);
    }

}
