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
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterSet;

/**
 * Use ant task classes for copying files using filters for variable substitution.
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenCopy {

    private static Logger logger = Logger.getLogger(TolvenCopy.class);

    public static void copyDirectoryToDirectory(File srcDir, File destDir, String pattern, Properties filterProperties) {
        Project project = new Project();
        project.init();
        Copy copy = new Copy();
        copy.setProject(project);
        copy.init();
        FileSet fileSet = new FileSet();
        fileSet.setDir(srcDir);
        if (pattern != null) {
            fileSet.setIncludes(pattern);
        }
        copy.add(fileSet);
        FilterSet filterSet = copy.createFilterSet();
        for (Object obj : filterProperties.keySet()) {
            String key = (String) obj;
            filterSet.addFilter(key, filterProperties.getProperty(key));
        }
        copy.setTodir(destDir);
        logger.debug("copy " + srcDir.getPath() + " to: " + destDir.getPath());
        copy.execute();
        logger.debug("copy complete");
    }

    public static void copyFile(File srcFile, File destFile) {
        copyFile(srcFile, destFile, null);
    }

    public static void copyFile(File srcFile, File destFile, Properties filterProperties) {
        Project project = new Project();
        project.init();
        Copy copy = new Copy();
        copy.setProject(project);
        copy.init();
        if (filterProperties != null) {
            FilterSet filterSet = copy.createFilterSet();
            for (Object obj : filterProperties.keySet()) {
                String key = (String) obj;
                filterSet.addFilter(key, filterProperties.getProperty(key));
            }
        }
        copy.setFile(srcFile);
        copy.setTofile(destFile);
        //TODO overwrite should not be used, but at this time, copy does not occur, even if the source is older than the destination?
        copy.setOverwrite(true);
        logger.debug("copy " + srcFile.getPath() + " to: " + destFile.getPath());
        copy.execute();
        logger.debug("copy complete");
    }

}
