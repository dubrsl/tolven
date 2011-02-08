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
package org.tolven.plugin;

import org.java.plugin.Plugin;
import org.java.plugin.registry.PluginDescriptor;
import org.tolven.command.TolvenApplication;

public abstract class TolvenCommandPlugin extends TolvenPlugin {

    public abstract void execute(String[] args) throws Exception;

    protected void execute(String pluginId, String[] args) {
        TolvenApplication.execute(pluginId, getManager(), args);
    }

    protected void execute(PluginDescriptor pluginDescriptor, String[] args) {
        TolvenApplication.execute(pluginDescriptor, getManager(), args);
    }

    protected Plugin getPlugin(PluginDescriptor pluginDescriptor) {
        return TolvenApplication.getPlugin(pluginDescriptor, getManager());
    }

}
