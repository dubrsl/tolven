/*
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
 * @author unni
 * Class to download the SS flat file and upload into database.
 * @version $Id: LoaderAllergies.java,v 1.2 2009/08/26 07:45:58 jchurin Exp $
 * */
package org.tolven.deploy.pharmacies;

import java.io.File;
import java.util.Properties;

import javax.naming.Context;

import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;

public class LoaderPharmacies extends TolvenCommandPlugin {
    protected Logger logger = Logger.getLogger(getClass());

    @Override
    protected void doStart() throws Exception {
        logger.info("*** START ***");
    }

    @Override
    protected void doStop() throws Exception {
        logger.info("*** STOP ***");
    }

	@Override
	public void execute(String[] args) throws Exception {
        logger.info("*** EXECUTE ***");
        //LoadPharmacies loader = new LoadPharmacies(getTolvenConfigDir().getPath());
        UploadPharmacies loader = new UploadPharmacies(getTolvenConfigDir().getPath());
		String adminId = getTolvenConfigWrapper().getAdminId();
		char[] adminPassword = getTolvenConfigWrapper().getPasswordHolder().getPassword(adminId);
		String authFilename = getTolvenConfigWrapper().getAppServer().getAuthFile();
		File authFile = new File(authFilename);
		String jndiFilename = getTolvenConfigWrapper().getAppServer().getJndiPropertiesFile();
		File jndiFile = new File(jndiFilename);
		Properties jndiProperties = loadProperties(jndiFile);
		String providerURL = getTolvenConfigWrapper().getAppServer().getJavaNamingProviderURL();
		// Override the default providerURL in the jndiProperties file 
		jndiProperties.setProperty(Context.PROVIDER_URL, providerURL);
		loader.load(adminId, adminPassword, authFile, jndiProperties);
		logger.info("****LOAD PHARMACIES COMPLETE****");
	}
}
