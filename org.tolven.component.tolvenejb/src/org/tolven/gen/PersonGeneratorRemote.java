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
package org.tolven.gen;

import org.tolven.gen.entity.VirtualPerson;

/**
 * Remote services to generate people and related attributes.
 * @author John Churin
 */
public interface PersonGeneratorRemote {
    /**
     * Create a simple person object with name, gender, and dob.
     * Do not persist the object.
     * 
     * @return a new VirtualPerson
     * @throws Exception
     */
    VirtualPerson generatePerson() throws Exception;

}
