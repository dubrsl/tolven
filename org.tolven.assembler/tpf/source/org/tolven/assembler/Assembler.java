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
package org.tolven.assembler;

import org.apache.log4j.Logger;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;
import org.tolven.plugin.TolvenCommandPlugin;

/**
 * This plugin starts the plugin assemblers
 * 
 * @author Joseph Isaac
 *
 */
public class Assembler extends TolvenCommandPlugin {

    public static final String EXTENSIONPOINT_EXECUTE = "execute";

    private Logger logger = Logger.getLogger(Assembler.class);

    @Override
    protected void doStart() throws Exception {
        logger.debug("*** start ***");
    }

    public void execute(String[] args) throws Exception {
        if (args.length > 0) {
            throw new RuntimeException(getDescriptor() + " does not take any arguments");
        }
        logger.debug("*** execute ***");
        ExtensionPoint extensionPoint = getDescriptor().getExtensionPoint(EXTENSIONPOINT_EXECUTE);
        for (Extension extension : extensionPoint.getConnectedExtensions()) {
            PluginDescriptor pluginDesriptor = extension.getDeclaringPluginDescriptor();
            Parameter argsParameter = extension.getParameter("args");
            if (argsParameter.valueAsString() == null) {
                throw new RuntimeException(extension.getUniqueId() + " must supply a value for the parameter args");
            }
                String[] extensionArgs = argsParameter.valueAsString().split(" ");
            execute(pluginDesriptor, extensionArgs);
        }
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("*** stop ***");
    }

}
