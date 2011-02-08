package org.tolven.client.examples.ws.generator;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.tolven.client.examples.ws.common.HeaderHandlerResolver;
import org.tolven.client.examples.ws.generator.jaxws.Generator;
import org.tolven.client.examples.ws.generator.jaxws.GeneratorService;

/**
 * Generate a CCR files using Tolven's data generator.
 * The tolven application server must be running and accessible.
 * The file will be placed in the location specified by the command line parameter
 * 
 * This is an example class, and when first checked out of cvs will have compiler errors until the generated
 * classes it imports have been generated. These are generated using the generate-ws-clients ant target in
 * tolvenWSClient/build.xml.
 * 
 * @author John Churin
 *
 */
public class GeneratorWebServiceClient {

    private String username;
    private char[] password;
    private String wsdl;
    private int expiresInSeconds;

    public GeneratorWebServiceClient(String username, char[] password, String wsdl, int expiresInSeconds) {
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

    public void generate(String file) {
        try {
            byte[] payload = generate().getBytes("UTF-8");
            OutputStream output = new BufferedOutputStream(new FileOutputStream(file));
            output.write(payload);
            output.close();
        } catch (Exception ex) {
            throw new RuntimeException("Could not write payload to: " + file, ex);
        }
    }

    public String generate() {
        // Establish a connection to tolven's data generator
        URL url = null;
        try {
            url = new URL(getWsdl());
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Could not convert to URL: " + getWsdl(), ex);
        }
        GeneratorService service = new GeneratorService(url, new QName("http://tolven.org/generator", "GeneratorService"));
        HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver(getUsername(), getPassword(), getExpiresInSeconds());
        service.setHandlerResolver(handlerResolver);
        Generator port = service.getGeneratorPort();
        // Use the Tolven data generator to create a random CCR Message.
        byte[] payload = port.generateCCRXML(1995);
        return new String(payload);
    }

    /**
     * Generate a CCR file based on a randomly generated person
     * A typical invokation of this class would be:
     * 
     * java -Djavax.net.ssl.keyStore=keyStoreFile -Djavax.net.ssl.trustStore=trustStoreFile org.tolven.client.examples.ws.generator.GeneratorWebServiceClient http://dev.able.com:8080/ws/GeneratorService?wsdl
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
        if (args.length != 2) {
            System.out.println("Usage: java -Djavax.net.ssl.keyStore=keyStoreFile -Djavax.net.ssl.keyStorePassword=tolven -Djavax.net.ssl.trustStore=trustStoreFile org.tolven.client.examples.ws.generator.GeneratorWebServiceClient https://dev.able.com:8443/ws/GeneratorService?wsdl outFile");
            System.exit(1);
        }
        String wsdl = args[0];
        String outFile = args[1];
        System.out.print("Please enter username: ");
        String username = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.print("Please enter user password: ");
        char[] password = System.console().readPassword();
        GeneratorWebServiceClient g = new GeneratorWebServiceClient(username, password, wsdl, 60);
        g.generate(outFile);
        System.out.println("CCR output to: " + outFile);
    }

}
