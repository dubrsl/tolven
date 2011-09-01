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
package org.tolven.doc.bean;

import java.io.Serializable;

/**
 * Holds information about a TolvenMessage e.g. its id in the TolvenMessage table. It can be sent as a queue payload,
 * in order to refer to an already existing TolvenMessage, without having to send the whole message.
 * 
 * @author Joseph Isaac
 *
 */
public class TolvenMessageInfo implements Serializable {

    private long id;

    public TolvenMessageInfo(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
