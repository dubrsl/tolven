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
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;

/**
 * A class providing functionality of Ant's Zip task
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenZip {

    private static Logger logger = Logger.getLogger(TolvenZip.class);

    public static void zip(File dir, File destFile) {
        zip(dir, destFile, "**", null);
    }

    public static void zip(File dir, File destFile, String includes, String excludes) {
        zip(dir, destFile, includes, null, excludes, null);
    }

    public static void zip(File dir, File destFile, String includes, List<String> include, String excludes, List<String> exclude) {
        Project project = new Project();
        project.init();
        Zip zip = new Zip();
        zip.setProject(project);
        zip.init();
        FileSet fileSet = new FileSet();
        if (includes != null) {
            fileSet.setIncludes(includes);
        }
        if (include != null) {
            for (String inc : include) {
                fileSet.createInclude().setName(inc);
            }
        }
        if (excludes != null) {
            fileSet.setExcludes(excludes);
        }
        if (exclude != null) {
            for (String exc : exclude) {
                fileSet.createExclude().setName(exc);
            }
        }
        fileSet.setDir(dir);
        zip.add(fileSet);
        zip.setDestFile(destFile);
        logger.debug("zip " + dir.getPath() + " to: " + destFile.getPath());
        zip.execute();
    }

    public static void unzip(File file, File dir) {
        unzip(file, dir, "**", null);
    }

    public static void unzip(File file, File dest, String includes, String excludes) {
        unzip(file, dest, includes, null, excludes, null);
    }

    public static void unzip(File file, File dest, String includes, List<String> include, String excludes, List<String> exclude) {
        Project project = new Project();
        project.init();
        Expand expand = new Expand();
        expand.setProject(project);
        expand.init();
        expand.setSrc(file);
        if (includes != null || include != null || excludes != null || exclude != null) {
            PatternSet patternSet = new PatternSet();
            if (includes != null) {
                patternSet.setIncludes(includes);
            }
            if (include != null) {
                for (String inc : include) {
                    patternSet.createInclude().setName(inc);
                }
            }
            if (excludes != null) {
                patternSet.setExcludes(excludes);
            }
            if (exclude != null) {
                for (String exc : exclude) {
                    patternSet.createExclude().setName(exc);
                }
            }
            expand.addPatternset(patternSet);
        }
        expand.setDest(dest);
        logger.debug("unzip " + file.getPath() + " to: " + dest.getPath());
        expand.execute();
    }

}
