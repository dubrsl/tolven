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
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;

/**
 * A class providing functionality of Ant's Jar task
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenJar {

    private static Logger logger = Logger.getLogger(TolvenJar.class);

    public static void jar(File dir, File destFile) {
        jarDir(dir, destFile, false);
    }

    public static void jarFile(File file, File destFile) {
        Project project = new Project();
        project.init();
        Jar jar = new Jar();
        jar.setProject(project);
        jar.init();
        FileSet fileSet = new FileSet();
        fileSet.setFile(file);
        jar.add(fileSet);
        jar.setDestFile(destFile);
        jar.setUpdate(false);
        logger.debug("jar " + file.getPath() + " to: " + destFile.getPath());
        jar.execute();
    }

    public static void jarFile(File file, File destFile, boolean update) {
        Project project = new Project();
        project.init();
        Jar jar = new Jar();
        jar.setProject(project);
        jar.init();
        FileSet fileSet = new FileSet();
        fileSet.setFile(file);
        jar.add(fileSet);
        jar.setDestFile(destFile);
        jar.setUpdate(update);
        logger.debug("jar " + file.getPath() + " to: " + destFile.getPath());
        jar.execute();
    }

    public static void jarDir(File dir, File destFile, boolean update) {
        jarDir(dir, destFile, null, update);
    }

    public static void jarDir(File dir, File destFile, String includes, boolean update) {
        jarDir(dir, destFile, includes, null, update);
    }

    public static void jarDir(File dir, File destFile, String includes, String excludes, boolean update) {
        jarDir(dir, destFile, includes, null, excludes, null, update);
    }

    public static void jarDir(File dir, File destFile, String includes, List<String> include, String excludes, List<String> exclude, boolean update) {
        Project project = new Project();
        project.init();
        Jar jar = new Jar();
        jar.setProject(project);
        jar.init();
        FileSet fileSet = new FileSet();
        fileSet.setDir(dir);
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
        jar.add(fileSet);
        jar.setDestFile(destFile);
        jar.setUpdate(update);
        logger.debug("jar " + dir.getPath() + " to: " + destFile.getPath());
        jar.execute();
    }

    public static void unjar(File file, File dir) {
        unjar(file, dir, "**");
    }

    public static void unjar(File file, File dir, String pattern) {
        Project project = new Project();
        project.init();
        Expand expand = new Expand();
        expand.setProject(project);
        expand.init();
        expand.setSrc(file);
        PatternSet patternSet = new PatternSet();
        patternSet.setIncludes(pattern);
        expand.addPatternset(patternSet);
        expand.setDest(dir);
        logger.debug("unjar " + file.getPath() + " to: " + dir.getPath());
        expand.execute();
    }

}
