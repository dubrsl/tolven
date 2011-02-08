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
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.DependSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;

/**
 * A class providing functionality of Ant's DependSet task
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenDependSet {

    public static void process(File srcFile, File targetFile) {
        Set<File> srcFiles = new HashSet<File>();
        srcFiles.add(srcFile);
        process(srcFiles, targetFile);
    }

    public static void process(Set<File> srcFiles, File targetFile) {
        Set<File> targetFiles = new HashSet<File>();
        targetFiles.add(targetFile);
        process(srcFiles, targetFiles);
    }

    public static void process(File srcFile, Set<File> targetFiles) {
        Set<File> srcFiles = new HashSet<File>();
        srcFiles.add(srcFile);
        process(srcFiles, targetFiles);
    }

    public static void process(Set<File> srcFiles, Set<File> targetFiles) {
        Project project = new Project();
        project.init();
        DependSet dependSet = new DependSet();
        dependSet.setProject(project);
        dependSet.init();
        for (File src : srcFiles) {
            FileSet fileSet = new FileSet();
            if (src.isDirectory()) {
                fileSet.setDir(src);
                fileSet.setIncludes("**/*");
            } else {
                fileSet.setFile(src);
            }
            //Adding the project to each and every fileSet is dumb, but if you don't Ant throws a null pointer exception
            fileSet.setProject(project);
            dependSet.addSrcfileset(fileSet);
        }
        FileList targetFileList = new FileList();
        for (File file : targetFiles) {
            FileList.FileName filename = new FileList.FileName();
            filename.setName(file.getPath());
            targetFileList.addConfiguredFile(filename);
        }
        dependSet.addTargetfilelist(targetFileList);
        dependSet.execute();
    }

}
