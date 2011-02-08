/**
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
 * @author Kul Bhushan
 * @version $Id: RulesRemote.java,v 1.2.2.1 2010/05/27 04:03:21 joseph_isaac Exp $
 */

package org.tolven.doc;

import java.util.List;

import org.tolven.doc.entity.RulePackage;

/**
 * Remote APIs that support rule loading and queries.
 * @author John Churin
 *
 */
public interface RulesRemote {

	/**
	 * Find the version of a named package that is active. 
	 * @param name Of the rule package
	 * @return The rule package or null if not found
	 */
	public RulePackage findActivePackage(String name);
	
	/**
	 * Find all active rule packages applicable to an Account type. 
	 * NOTE: This method ignore AccountType and loads all active packages - The rules can limit their applicability as needed.
	 * @param accountType name (known type)
	 * @return A list of rule packages
	 */
	public List<RulePackage> findActivePackages( String knownType );
}
