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
 * @author Joseph Isaac
 * @version $Id: TolvenLoadProperties.java 1757 2011-07-20 04:40:03Z joe.isaac $
 */
package org.tolven.tools.ant;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.LoadProperties;

public class TolvenLoadProperties {

    private static Logger logger = Logger.getLogger(TolvenLoadProperties.class);

    public static Properties load(File src, Properties properties) {
        Project project = new Project();
        project.init();
        for (String key : properties.stringPropertyNames()) {
            project.setUserProperty(key, properties.getProperty(key));
        }
        LoadProperties loadProperties = new LoadProperties();
        loadProperties.setProject(project);
        loadProperties.init();
        loadProperties.setSrcFile(src);
        logger.debug("loadproperties from " + src.getPath());
        loadProperties.execute();
        Properties loadedProperties = new Properties();
        for (Object obj : project.getProperties().keySet()) {
            String key = (String) obj;
            String value = project.getProperty(key);
            loadedProperties.setProperty(key, value);
        }
        return loadedProperties;
    }

}
