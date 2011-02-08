package org.tolven.appserverproperties.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.tolven.restful.client.RESTfulClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class LoadServerProperties extends RESTfulClient {

    private Logger logger = Logger.getLogger(LoadServerProperties.class);

    public LoadServerProperties(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }

    private void setProperties(Properties properties) {
        WebResource webResource = getAppWebResource().path("tolvenproperties/set");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        for (String name : properties.stringPropertyNames()) {
            formData.putSingle(name, (String) properties.get(name));
        }
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error: " + response.getStatus() + " POST " + getUserId() + " " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        List<String> names = new ArrayList<String>(properties.stringPropertyNames());
        Collections.sort(names);
        for (String name : names) {
            // Cannot distinguish security properties like passwords, so log only property name
            logger.info("Set property: " + name);
        }
    }

    private void removeProperties(List<String> propertyNames) {
        WebResource webResource = getAppWebResource().path("tolvenproperties/remove");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.put("propertyNames", propertyNames);
        formData.putSingle("silentIfMissing", String.valueOf(false));
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error: " + response.getStatus() + " POST " + getUserId() + " " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        Collections.sort(propertyNames);
        for (String propertyName : propertyNames) {
            logger.info("Removed property: " + propertyName);
        }
    }

    public void setProperty(String propertyName, String propertyValue) {
        Properties properties = new Properties();
        properties.setProperty(propertyName, propertyValue);
        setProperties(properties);
    }

    public void removeProperty(String propertyName) {
        List<String> propertyNames = new ArrayList<String>();
        propertyNames.add(propertyName);
        removeProperties(propertyNames);
    }

    public String displayProperties() {
        Properties properties = findProperties();
        List<String> names = new ArrayList<String>(properties.stringPropertyNames());
        Collections.sort(names);
        StringBuffer buff = new StringBuffer();
        for (String name : names) {
            // Cannot distinguish security properties like passwords, so log only property name
            String string = "Set property: " + name;
            logger.info(string);
            buff.append(string);
        }
        return buff.toString();
    }

    public void uploadPropertiesFiles(List<File> propertiesFiles) {
        for (File propertiesFile : propertiesFiles) {
            uploadPropertiesFile(propertiesFile);
        }
    }

    public void uploadPropertiesFile(File propertiesFile) {
        Properties properties = new Properties();
        loadProperties(propertiesFile, properties);
        String xml = null;
        try {
            xml = FileUtils.readFileToString(propertiesFile);
        } catch (Exception ex) {
            throw new RuntimeException("Could not read properties files as String from: " + propertiesFile.getPath(), ex);
        }
        WebResource webResource = getAppWebResource().path("tolvenproperties/set");
        ClientResponse response = webResource.type(MediaType.APPLICATION_XML).entity(xml).cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error: " + response.getStatus() + " POST " + getUserId() + " " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        List<String> names = new ArrayList<String>(properties.stringPropertyNames());
        Collections.sort(names);
        for (String name : names) {
            // Cannot distinguish security properties like passwords, so log only property name
            logger.info("Set property: " + name);
        }
    }

    public String findProperty(String propertyName) {
        List<String> propertyNames = new ArrayList<String>();
        propertyNames.add(propertyName);
        Properties properties = findProperties(propertyNames);
        return properties.getProperty(propertyName);
    }

    private Properties findProperties() {
        return findProperties(new ArrayList<String>());
    }

    private Properties findProperties(List<String> propertyNames) {
        WebResource webResource = getAppWebResource().path("tolvenproperties/find");
        StringBuffer buff = new StringBuffer();
        Iterator<String> it = propertyNames.iterator();
        while (it.hasNext()) {
            buff.append(it.next());
            if (it.hasNext()) {
                buff.append(",");
            }
        }
        if (!propertyNames.isEmpty()) {
            webResource.queryParam("propertyNames", buff.toString());
        }
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).get(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error: " + response.getStatus() + " GET " + getUserId() + " " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        MultivaluedMap<String, String> propertiesMap = response.getEntity(MultivaluedMap.class);
        Properties properties = new Properties();
        for (String name : propertiesMap.keySet()) {
            properties.setProperty(name, propertiesMap.getFirst(name));
        }
        return properties;
    }

    private Properties loadProperties(File propertiesFile, Properties properties) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(propertiesFile);
            properties.loadFromXML(in);
            in.close();
            return properties;
        } catch (IOException ex) {
            throw new RuntimeException("Could not load properties file: " + propertiesFile.getPath(), ex);
        }
    }

}
