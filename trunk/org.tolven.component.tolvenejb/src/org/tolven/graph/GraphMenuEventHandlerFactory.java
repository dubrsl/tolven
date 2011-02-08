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
package org.tolven.graph;

import org.tolven.app.MenuEventHandler;
import org.tolven.app.MenuEventHandlerFactory;
import org.tolven.app.entity.MSAction;

public class GraphMenuEventHandlerFactory implements MenuEventHandlerFactory {

    public MenuEventHandler createMenuEventHandler(MSAction action) {
        return new GraphMenuEventHandler(action);
    }

}
