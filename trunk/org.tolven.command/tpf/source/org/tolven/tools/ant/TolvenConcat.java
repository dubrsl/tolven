/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
package org.tolven.tools.ant;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.ExpandProperties;
import org.apache.tools.ant.taskdefs.Concat;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;

/**
 * A class providing functionality of Ant's Concat task
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenConcat {

    private static Logger logger = Logger.getLogger(TolvenConcat.class);

    public static void concat(File src, File dest, Properties properties) {
        Project project = new Project();
        project.init();
        for (String key : properties.stringPropertyNames()) {
            project.setUserProperty(key, properties.getProperty(key));
        }
        Concat concat = new Concat();
        concat.setProject(project);
        concat.init();
        Path path = concat.createPath();
        path.setLocation(src);
        FilterChain filterChain = new FilterChain();
        ExpandProperties expandProperties = new ExpandProperties();
        expandProperties.setProject(project);
        filterChain.addExpandProperties(expandProperties);
        concat.addFilterChain(filterChain);
        concat.setDestfile(dest);
        logger.debug("concat " + src.getPath() + " to: " + dest.getPath());
        concat.execute();
    }

}
