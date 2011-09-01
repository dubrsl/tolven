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
package org.tolven.glassfishmgr.api;

import org.tolven.glassfishmgr.model.GlassFishModelImpl;

public class GlassFishModel {

    private GlassFishModelImpl glassFishModelImpl;

    public GlassFishModel(String userId, char[] password, String appRestfulURL, String authRestfulURL) {
        setGlassFishModelImpl(new GlassFishModelImpl(userId, password, appRestfulURL, authRestfulURL));
    }

    private GlassFishModelImpl getGlassFishModelImpl() {
        return glassFishModelImpl;
    }

    private void setGlassFishModelImpl(GlassFishModelImpl glassFishModelImpl) {
        this.glassFishModelImpl = glassFishModelImpl;
    }

    public void test() {
        getGlassFishModelImpl().test();
    }

}
