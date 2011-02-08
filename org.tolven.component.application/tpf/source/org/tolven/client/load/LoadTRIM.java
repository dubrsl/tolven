package org.tolven.client.load;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.tolven.logging.TolvenLogger;
import org.tolven.restful.client.RESTfulClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class LoadTRIM extends RESTfulClient {

    protected String trimDir;
    
    /**
     * Prepare to load Trims to the server
     * @param configDir
     */
    public LoadTRIM(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
    }
    
    protected static String EXTENSION = ".trim.xml";
    protected static String XSL_EXTENSION = ".trim.xsl";

    private static class TrimFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            if (name.toLowerCase().endsWith(EXTENSION))
                return true;
            if (name.toLowerCase().endsWith(XSL_EXTENSION))
                return true;
            return false;
        }
    }

    private static class DirFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return dir.isDirectory();
        }
    }
    /**
     * Now activate any new trims we've created.
     */
    public void activate() {
        try {
            WebResource webResource = getAppWebResource().path("loader/queueActivateNewTrimHeaders");
            ClientResponse response = webResource.cookie(getTokenCookie()).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Status: " + response.getStatus() + " " + getUserId() + " POST " + webResource.getURI() + " " + response.getEntity(String.class));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed while activating trims", ex);
        }
    }

    public void uploadTrim(File file) {
        String xml = null;
        try {
            xml = FileUtils.readFileToString(file);
        } catch (Exception ex) {
            throw new RuntimeException("Could not read properties files as String", ex);
        }
        TolvenLogger.info("Load TRIM: " + file.getPath(), LoadTRIM.class);
        WebResource webResource = getAppWebResource().path("loader/createTrimHeader");
        ClientResponse response = webResource.queryParam("trimName", file.getPath()).cookie(getTokenCookie()).type(MediaType.APPLICATION_XML).entity(xml).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Status: " + response.getStatus() + " " + getUserId() + " POST " + webResource.getURI() + " " + response.getEntity(String.class));
        }
        //		getTrimBean().loadTrimXML(sb.toString(), user, comment, autogenerated);
    }

    /**
     * Return the directory containing Trim instances
     * @return
     */
    public String getTrimDir() {
        return trimDir;
    }

    /**
     * Trim instances in same directory as trim
     * @return
     */
    public String getTrimInstanceDir() {
        return trimDir;
    }

    /**
     * Return a File handle pointing to a trimInstance file
     * @param forEach
     * @return
     */
    public File getTrimInstanceFile(String forEach) {
        return new File(trimDir + File.separator + forEach);
    }

    /**
     * Upload all available trim in the named directory to server
     * @param directory from which trim is uploaded
     * @throws IOException 
     * @throws NamingException 
     * @throws JMSException 
     * @throws Exception
     * @deprecated
     */
    public void uploadFromDirectory() {
        java.util.List<File> listDir = new ArrayList<File>();
        getTrimFiles(new File(getTrimDir()), listDir);

        File files[] = (File[]) listDir.toArray(new File[listDir.size()]);
        if (files != null) {
            for (File file : files) {
                try {
                    uploadTrim(file);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed while uploading trim: " + file.getPath(), ex);
                }
            }
        }
    }
    
    /**
     * Upload all available trim in the named directory to server as specified in the constructor
     * Be sure to call the activate method before finishing.
     * A user must be logged in and had permission to write to the admin Queue
     * @param directory from which trim is uploaded
     */
    public void uploadFromDirectory(String trimDir) {
        java.util.List<File> listDir = new ArrayList<File>();
        getTrimFiles(new File(trimDir), listDir);

        File files[] = (File[]) listDir.toArray(new File[listDir.size()]);
        if (files != null) {
            for (File file : files) {
                try {
                    uploadTrim(file);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed while uploading trim: " + file.getPath(), ex);
                }
            }
        }
    }

    private void getTrimFiles(File trimdir, java.util.List<File> filesList) {
        File files[] = trimdir.listFiles(new TrimFilter());
        if (files != null) {
            for (File file : files) {
                filesList.add(file);
            }
        }
        File dirs[] = trimdir.listFiles(new DirFilter());
        if (dirs != null) {
            for (File dir : dirs) {
                getTrimFiles(dir, filesList);
            }
        }
    }

    /*
     * Check the local trim dir for the trim and return it.
     * todo: check version on server.
     */
    public File findTrimFile(String trimName) {
        File trmdir = new File(trimDir);
        java.util.List<File> trims = new ArrayList<File>();
        getTrimFiles(trmdir, trims);

        if (!trimName.endsWith(".trim.xml")) {
            trimName += ".trim.xml";
        }
        for (File t : trims) {
            URI uri = t.toURI();
            String path = uri.getPath();
            if (path.endsWith(trimName)) {
                return t;
            }
        }

        return null;
    }

}
