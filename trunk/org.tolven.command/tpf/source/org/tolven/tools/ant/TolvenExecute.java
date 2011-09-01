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
 * @version $Id: TolvenExecute.java 1744 2011-07-18 04:24:44Z joe.isaac $
 */
package org.tolven.tools.ant;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;

public class TolvenExecute {

    public int execute(String[] commandLine, File workingDirectory) {
        Project project = new Project();
        project.init();
        Execute execute = new Execute();
        execute.setCommandline(commandLine);
        execute.setWorkingDirectory(workingDirectory);
        try {
            return execute.execute();
        } catch (Exception ex) {
            throw new RuntimeException("Could not execute command", ex);
        }
    }

}
