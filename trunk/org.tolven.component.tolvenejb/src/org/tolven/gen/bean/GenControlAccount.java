/*
 *  Copyright (C) 2007 Tolven Inc 
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
package org.tolven.gen.bean;

/**
 * Command object directing an MDB to generate patients for an account. Create a number of families and add them to 
 * the specified account. This does not cause the direct creation of the families but instead creates
 * the virtual family and then passes that to the data generator.
 * @author John Churin
 *
 */
public abstract class GenControlAccount extends GenControlBase {

	private static final long serialVersionUID = 1L;

}
