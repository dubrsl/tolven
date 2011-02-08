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
package org.tolven.gen.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Base class used by data generator classes.
 * @author John Churin
 *
 */
public abstract class GeneratorBase {

	/**
	 * Random number generator
	 */
	protected Random rng;

	public GeneratorBase() {
		super();
		rng = new Random();
	}

	BufferedReader getFileHandle( String fileName ) throws IOException {
    	InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("gen/"  + fileName);
    	if (resourceStream==null) throw new IOException( "[Generator] Resource " + fileName + " not found");
    	return new BufferedReader(new InputStreamReader(resourceStream));
    }

}
