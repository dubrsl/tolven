package org.tolven.client.load;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.tolven.logging.TolvenLogger;
import org.tolven.restful.client.RESTfulClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class LoadReports extends RESTfulClient {

    protected static String EXTENSION = ".jrxml";

    protected File jrxmlDir;

    public LoadReports(String userId, char[] password, String appRestfulURL, String authRestfulURL, String jrxmlDir) {
        this.jrxmlDir = new File(jrxmlDir);
        init(userId, password, appRestfulURL, authRestfulURL);
    }

    public void uploadJRXML(File file) {
        String xml = null;
        try {
            xml = FileUtils.readFileToString(file);
        } catch (Exception ex) {
            throw new RuntimeException("Could not read properties files as String", ex);
        }
        TolvenLogger.info("Load JRXML: " + file.getPath(), LoadReports.class);
        String baseFilenameWithNoExt = file.getName().substring(0, file.getName().indexOf('.'));
        WebResource webResource = getAppWebResource().path("loader/storeReport");
        ClientResponse response = webResource.queryParam("externalReportName", baseFilenameWithNoExt).queryParam("reportType", "jrxml").type(MediaType.APPLICATION_XML).cookie(getTokenCookie()).entity(xml).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Status: " + response.getStatus() + " " + getUserId() + " POST " + webResource.getURI() + " " + response.getEntity(String.class));
        }
    }

    /**
     * Return the directory containing JRXML instances
     * @return
     */
    public File getJRXMLDir() {
        return jrxmlDir;
    }

    /**
     * Upload all available jrxml in the named directory to server
     * @param directory from which jrxml is uploaded
     * @throws NamingException 
     * @throws IOException 
     * @throws Exception
     */
    public void uploadFromDirectory() throws IOException, NamingException {
        if (!getJRXMLDir().exists()) {
            throw new RuntimeException("The reports directory could not be found: " + getJRXMLDir().getPath());
        }
        List<File> listDir = new ArrayList<File>();
        getJRXMLFiles(getJRXMLDir(), listDir);
        File files[] = (File[]) listDir.toArray(new File[listDir.size()]);
        if (files != null) {
            //Date uploadTime = new Date();
            for (File file : files) {
                uploadJRXML(file);
            }
        }
    }

    private void getJRXMLFiles(File jrxmldir, List<File> filesList) {
        File files[] = jrxmldir.listFiles(new JRXMLFilter());
        if (files != null) {
            for (File file : files) {
                filesList.add(file);
            }
        }
        File dirs[] = jrxmldir.listFiles(new DirFilter());
        if (dirs != null) {
            for (File dir : dirs) {
                getJRXMLFiles(dir, filesList);
            }
        }
    }

    private static class JRXMLFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            if (name.endsWith(EXTENSION))
                return true;
            return false;
        }
    }

    private static class DirFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return dir.isDirectory();
        }
    }

}
