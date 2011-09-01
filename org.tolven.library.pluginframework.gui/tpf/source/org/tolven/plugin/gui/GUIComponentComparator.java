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
package org.tolven.plugin.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GUIComponentComparator {

    public static List<GUIComponent> sort(List<GUIComponent> guiComponents, final List<String> sortOrder) {
        List<GUIComponent> sortedGUIComponents = new ArrayList<GUIComponent>();
        sortedGUIComponents.addAll(guiComponents);
        Comparator<GUIComponent> comparator = new Comparator<GUIComponent>() {
            public int compare(GUIComponent guiComponent1, GUIComponent guiComponent2) {
                int index1 = sortOrder.indexOf(guiComponent1.getComponentId());
                int index2 = sortOrder.indexOf(guiComponent2.getComponentId());
                //unspecifed componentIds should go to the end
                if (index1 == -1 && index2 != -1) {
                    index1 = index2 + 1;
                } else if(index2 == -1 && index1 != -1) {
                    index2 = index1 + 1;
                }
                return new Integer(index1).compareTo(index2);
            };
        };
        Collections.sort(sortedGUIComponents, comparator);
        return sortedGUIComponents;
    }
}
