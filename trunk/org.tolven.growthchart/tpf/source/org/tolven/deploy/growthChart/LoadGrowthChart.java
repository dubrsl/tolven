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
 * @author <your name>
 * @version $Id: LoadGrowthChart.java 1058 2011-05-23 01:26:07Z kanag.kuttiannan $
 */

package org.tolven.deploy.growthChart;


import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import javax.naming.Context;

import org.apache.log4j.Logger;
import org.tolven.plugin.TolvenCommandPlugin;




/**
 * To load Growth Chart values
 * 
 * @author Nevin
 * added on 02/04/2011
 */
public class LoadGrowthChart extends TolvenCommandPlugin {
    protected Logger logger = Logger.getLogger(getClass());
    public static final String LENGTH = "length";
    public static final String WEIGHT = "weight";
    	
    @Override
    public void execute(String[] args) throws Exception 
    {
	    LoaderGrowthChart loader = new LoaderGrowthChart(getAdminId(), getAdminPassword(), getAppRestfulRootURL(), getAuthRestfulRootURL());
		
		File sourceFile = null;
		try {
			
//			loader.testGrowthChartAction();
			logger.info("********* Loading Length *********");
			sourceFile = getFilePath("tpf/voc/lenageinf.xls");
			logger.info("********* Loading from Source = " + sourceFile.getPath() + " ********");
			loader.loadData(sourceFile, "clearLenageinf","loadLenageinf");
			logger.info("********* Load Completed = " + sourceFile.getPath() + " ********");

        	sourceFile = getFilePath("tpf/voc/statage.xls");
        	logger.info("********* Loading from Source = " + sourceFile.getPath() + " ********");
        	loader.loadData(sourceFile, "clearStatage","loadStatage");
        	logger.info("********* Load Completed = " + sourceFile.getPath() + " ********");

        	
			logger.info("********* Loading Weight *********");
			
			sourceFile = getFilePath("tpf/voc/wtage.xls");
			logger.info("********* Loading from Source = " + sourceFile.getPath() + " ********");
			loader.loadData(sourceFile, "clearWtage","loadWtage");
			logger.info("********* Load Completed = " + sourceFile.getPath() + " ********");

			
        	sourceFile = getFilePath("tpf/voc/wtageinf.xls");
        	logger.info("********* Loading from Source = " + sourceFile.getPath() + " ********");
        	loader.loadData(sourceFile, "clearWtageinf","loadWtageinf");
        	logger.info("********* Load Completed = " + sourceFile.getPath() + " ********");
            	
		} catch (Exception e) {
			e.printStackTrace();
		}
		 logger.info("********* Loading Growth Chart - end *********");
    }	
    
    private String getAdminId() {
        return getTolvenConfigWrapper().getAdminId();
    }

    private char[] getAdminPassword() {
        return getTolvenConfigWrapper().getAdminPassword();
    }

    private String getAppRestfulRootURL() {
        return getTolvenConfigWrapper().getApplication().getAppRestfulURL();
    }

    private String getAuthRestfulRootURL() {
        return getTolvenConfigWrapper().getApplication().getAuthRestfulURL();
    }
    
    @Override
    protected void doStart() throws Exception {
    }
    
    @Override
    protected void doStop() throws Exception {
    }

}
