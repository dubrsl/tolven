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
 * @version $Id$
 */
package org.tolven.assembler.javamodule;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.tolven.plugin.TolvenCommandPlugin;

/**
 * This plugin assembles java modules for an ear file
 * 
 * @author Joseph Isaac
 *
 */
public class JavaModuleAssembler extends TolvenCommandPlugin {

    public static final String EXTENSIONPOINT_COMPONENT = "component";

    private Logger logger = Logger.getLogger(JavaModuleAssembler.class);

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
    }

    @Override
    public void execute(String[] args) throws Exception {
        logger.debug("*** execute ***");
        ExtensionPoint javaModuleExtensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_COMPONENT);
        for (Extension extension : javaModuleExtensionPoint.getConnectedExtensions()) {
            File sourceJarFile = getFilePath(extension.getDeclaringPluginDescriptor(), extension.getParameter("jar").valueAsString());
            File myPluginDataDir = getPluginTmpDir(extension.getDeclaringPluginDescriptor());
            File destinationJarFile = new File(myPluginDataDir, sourceJarFile.getName());
            destinationJarFile.getParentFile().mkdirs();
            logger.debug("Copy " + sourceJarFile.getPath() + " to " + destinationJarFile);
            FileUtils.copyFile(sourceJarFile, destinationJarFile);
        }
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
