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
 * @version $Id: RulesLocal.java,v 1.2 2010/03/17 20:10:24 jchurin Exp $
 */

package org.tolven.doc;

import java.util.Date;
import java.util.List;

import org.drools.RuleBase;
import org.tolven.doc.entity.RulePackage;

/**
 * APIs that support rule loading and queries.
 * @author John Churin
 *
 */
public interface RulesLocal {

	/**
	 * A remote-friendly method that creates a new Rule package from source and requires no special classes on the remote-end
	 * @param packageBody
	 */
	public void loadRulePackage(String packageBody);
	/**
	 * Create or update a Rule package. If the content is unchanged from the previous version, then
	 * a new package is not created.
	 * @param packageBody The rule package body
	 * @return The RulePackage entity
	 */
	public RulePackage createRulePackage(String packageBody);
	
	/**
	 * Get the rule base for the specified account. In general, this pulls together all active rule packages applicable to the specified account 
	 * @param account
	 * @return A RuleBase
	 */
	public RuleBase getRuleBase( String knownType );
	
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
	
	/**CCHIT merge
	 * Added to set time from out side class.
	 * @author valsaraj
	 * added on 08/03/2010
	 */
	public void setSavedTime(Date savedTime);
}
