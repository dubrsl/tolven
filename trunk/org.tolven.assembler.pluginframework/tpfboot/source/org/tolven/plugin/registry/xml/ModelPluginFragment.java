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
package org.tolven.plugin.registry.xml;

import org.java.plugin.registry.MatchingRule;
import org.java.plugin.registry.Version;

/**
 * Class externalized from the ModelPluginManifest, since it is a useful interface to prerequisite information
 * 
 * @author Joseph Isaac
 *
 */
public class ModelPluginFragment extends ModelPluginManifest {
    private String pluginId;
    private Version pluginVersion;
    private MatchingRule matchingRule = MatchingRule.COMPATIBLE;

    public ModelPluginFragment() {
        // no-op
    }

    public MatchingRule getMatchingRule() {
        return matchingRule;
    }

    public void setMatchingRule(MatchingRule value) {
        matchingRule = value;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String value) {
        pluginId = value;
    }

    public Version getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String value) {
        pluginVersion = Version.parse(value);
    }
}
