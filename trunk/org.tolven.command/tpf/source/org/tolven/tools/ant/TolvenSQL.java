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
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.listener.Log4jListener;
import org.apache.tools.ant.taskdefs.SQLExec;

/**
 * A class providing functionality of Ant's SQL task
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenSQL {

    private static Logger logger = Logger.getLogger(TolvenSQL.class);

    public static void sql(File srcFile, String url, String driver, String userId, char[] password) {
        File tmpFile = null;
        try {
            try {
                tmpFile = File.createTempFile("bug_in_apache_sqlExec_", "txt");
            } catch (IOException ex) {
                throw new RuntimeException("Could not create temp file to prevent Apache code from closing the System.out", ex);
            }
            Project project = new Project();
            project.init();
            SQLExec sqlExec = new SQLExec();
            sqlExec.setProject(project);
            sqlExec.init();
            sqlExec.setSrc(srcFile);
            sqlExec.setUrl(url);
            sqlExec.setDriver(driver);
            sqlExec.setAutocommit(true);
            sqlExec.setPrint(true);
            SQLExec.OnError onerror = new SQLExec.OnError();
            onerror.setValue("continue");
            sqlExec.setOnerror(onerror);
            sqlExec.setUserid(userId);
            sqlExec.setShowheaders(true);
            sqlExec.setShowtrailers(true);
            sqlExec.setPassword(new String(password));
            //sqlExec closes the System.out !!! So here it's given something else to close
            sqlExec.setOutput(tmpFile);
            project.addBuildListener(new Log4jListener());
            project.addBuildListener(new BuildListener() {
                @Override
                public void buildFinished(BuildEvent event) {
                }

                @Override
                public void buildStarted(BuildEvent event) {
                }

                @Override
                public void messageLogged(BuildEvent event) {
                    if (event.getPriority() < 3) {
                        logger.debug(event.getMessage());
                    }
                }

                @Override
                public void targetFinished(BuildEvent event) {
                }

                @Override
                public void targetStarted(BuildEvent event) {
                }

                @Override
                public void taskFinished(BuildEvent event) {
                }

                @Override
                public void taskStarted(BuildEvent event) {
                }

            });
            logger.debug("About to execute sql " + srcFile.getPath());
            sqlExec.execute();
            logger.debug("sql complete");
        } finally {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
    }
}
