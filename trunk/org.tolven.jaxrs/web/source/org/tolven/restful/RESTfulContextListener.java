package org.tolven.restful;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * A class to initialize a Jersey client (an expensive step) and place the Client in the ServletContext
 * @author Joseph Isaac
 *
 */
public class RESTfulContextListener implements ServletContextListener {

    private boolean initialized = false;

    public void contextInitialized(ServletContextEvent event) {
        ClientConfig config = new DefaultClientConfig();
        try {
            config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(new DefaultHostnameVerifier(), SSLContext.getDefault()));
        } catch (Exception ex) {
            throw new RuntimeException("Could not include DefaultHostnameVerifier", ex);
        }
        Client client = Client.create(config);
        client.setFollowRedirects(true);
        event.getServletContext().setAttribute("restfulClient", client);
    }

    /**
     * Return true if the system appears to not have been initiallized
     * @return
     */
    public boolean isInitialized() {
        return initialized;
    }

    public void contextDestroyed(ServletContextEvent event) {
    }

    class DefaultHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostName, SSLSession session) {
            return true;
        }

    }

}
