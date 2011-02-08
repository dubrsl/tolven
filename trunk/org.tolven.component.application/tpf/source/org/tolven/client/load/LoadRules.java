/**
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author Kul Bhushan
 * @version $Id: LoadRules.java,v 1.1.2.9 2010/10/23 08:53:37 joseph_isaac Exp $
 */

package org.tolven.client.load;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.tolven.logging.TolvenLogger;
import org.tolven.restful.client.RESTfulClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This class uploads the rules to the database (RulePackage).
 */
public class LoadRules extends RESTfulClient {

	Logger logger = Logger.getLogger(this.getClass());

	public LoadRules(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        init(userId, password, appRestfulURL, authRestfulURL);
	}
	/**
	 * Upload a single rule file
	 * @param ruleFile
	 * @return
	 * @throws Exception
	 */
	public boolean uploadFile(File ruleFile) throws Exception {
        try {
            String xml = null;
            try {
                xml = FileUtils.readFileToString(ruleFile);
            } catch (Exception ex) {
                throw new RuntimeException("Could not read properties files as String", ex);
            }
            logger.info("Loading file: " + ruleFile);
            WebResource webResource = getAppWebResource().path("loader/packageBody");
            ClientResponse response = webResource.queryParam("packageBodyName", ruleFile.getName()).cookie(getTokenCookie()).type(MediaType.APPLICATION_XML).entity(xml).accept(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Status: " + response.getStatus() + " " + getUserId() + " POST " + webResource.getURI() + " " + response.getEntity(String.class));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error encountered in rule file " + ruleFile, e);
        }
        return true;
    }
	
	public void uploadFromDirectory(String directory) throws Exception {
        java.util.List<File> listDir = new ArrayList<File>();
        getRuleFiles(new File(directory), listDir);

        File files[] = (File[]) listDir.toArray(new File[listDir.size()]);
        if (files != null) {
            for (File file : files) {
                uploadFile(file);
            }
        }
    }
	
    protected static String EXTENSION = ".drl";
    private static class RuleFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            if (name.toLowerCase().endsWith(EXTENSION))
                return true;
            return false;
        }
    }

    private static class DirFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return dir.isDirectory();
        }
    }
    
    private void getRuleFiles(File ruledir, java.util.List<File> filesList) {
        File files[] = ruledir.listFiles(new RuleFilter());
        if (files != null) {
            for (File file : files) {
                filesList.add(file);
            }
        }
        File dirs[] = ruledir.listFiles(new DirFilter());
        if (dirs != null) {
            for (File dir : dirs) {
                getRuleFiles(dir, filesList);
            }
        }
    }
	
	public static void main(String[] args) throws Exception {
		TolvenLogger.defaultInitialize();
		if(args.length < 2) {
			System.out.println("Arguments: configDirectory RuleFileDirectory");
			return;
		}
		//LoadRules rules = new LoadRules(args[0], args[1].toCharArray(), args[2], args[3]);
		//rules.uploadFromDirectory(args[1]);
	}
}
