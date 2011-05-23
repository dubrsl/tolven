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
 * @version $Id: RulesBean.java,v 1.6.8.2 2010/05/03 20:38:18 joseph_isaac Exp $
 */

package org.tolven.doc.bean;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.facttemplates.FactTemplate;
import org.drools.rule.Package;
import org.tolven.app.MenuLocal;
import org.tolven.app.entity.MenuStructure;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.Account;
import org.tolven.doc.RulesLocal;
import org.tolven.doc.RulesRemote;
import org.tolven.doc.entity.RulePackage;
import org.tolven.rules.PackageCompiler;
import org.tolven.rules.PlaceholderFactTemplate;
import org.tolven.util.ExceptionFormatter;


@Stateless
@Local(RulesLocal.class)
@Remote(RulesRemote.class)
public class RulesBean implements RulesLocal, RulesRemote {
	@PersistenceContext private EntityManager em;
	private static final String ACTIVE_STATUS = "active";
	private static final String OBSOLETE_STATUS = "obsolete";
	Logger logger = Logger.getLogger(this.getClass());
	private PackageCompiler compiler = new PackageCompiler();
	private String savedType;
	private Date savedTime;
	private RuleBase savedBase;
	private static final long MAX_AGE = 1000*60*2;
	@EJB private MenuLocal menuBean;
	@EJB private AccountDAOLocal accountBean;
	
	/**
	 * A remote-friendly method that creates a new Rule package from source and requires no special classes on the remote-end
	 * @param packageBody
	 */
	public void loadRulePackage(String packageBody) {
		try {
			createRulePackage( packageBody );
		} catch (Exception e) {
			String details = ExceptionFormatter.toSimpleString(e, "\n");
			throw new RuntimeException("Error loading rule package\n" + details, e);
		}
	}
	
	public void addKnownTypeToPackage(Package pkg, String knownType) {
		Account account = accountBean.findAccountTemplate( knownType );		
    	List<MenuStructure> menus = menuBean.findFullMenuStructure( account.getId());
    	for (MenuStructure ms : menus) {
    		if (MenuStructure.PLACEHOLDER.equals(ms.getRole())) {
    			FactTemplate ft = new PlaceholderFactTemplate(pkg,  ms); 
        		pkg.addFactTemplate(ft);
    		}
    	}
	}

	/**
	 * Create or update a Rule package. If the content is unchanged from the previous version, then
	 * a new package is not created.
	 * @param packageBody The rule package body
	 * @return The RulePackage entity
	 */
	public RulePackage createRulePackage(String packageBody) {
		String packageName = compiler.extractPackageName(packageBody);
		logger.info("Compile rule package: " + packageName);
		Package initialPackage = new Package(packageName);
		for (String knownType : compiler.extractPlaholderAccountType( packageBody )) {
			logger.info("Add " + knownType + " placeholders to " + packageName + " package");
			addKnownTypeToPackage(initialPackage, knownType);
		}
		// Compile the package
		Package pkg = compiler.compile( packageBody, initialPackage );
		byte[] serialized = compiler.serializePackage( pkg );
		// See about a previous version
		int version = 1;
		RulePackage previousRulePackage = findActivePackage( pkg.getName() );
		if (previousRulePackage!=null) {
			if (packageBody.equals(previousRulePackage.getPackageBody()) &&
					Arrays.equals(serialized, previousRulePackage.getCompiledPackage())	) {
				return previousRulePackage;
			}
			previousRulePackage.setPackageStatus( OBSOLETE_STATUS );
			version = previousRulePackage.getPackageVersion()+1;
		}
		RulePackage rulePackage = new RulePackage();
		rulePackage.setPackageName(pkg.getName());
//		rulePackage.setDescription();
		rulePackage.setPackageBody(packageBody);
		rulePackage.setPackageVersion(version);
		rulePackage.setPackageStatus( ACTIVE_STATUS );
		rulePackage.setTimestamp(new Date());
		rulePackage.setCompiledPackage(serialized);
		// Add it to database
		em.persist(rulePackage);
		return rulePackage;
	}
	
	/**
	 * Recompile a rule package even though the source code has not changed. This may happen when an underlying
	 * serialized object used by rules may have changed. The new compiled rules are updated in the rule package.
	 * This is sufficient to create a version of the rule package even though the rule source itself has not changed.
	 * @param p the Rule package to recompile
	 * @return The new rule package is returned
	 */
	public RulePackage recompile( RulePackage p ) {
		return createRulePackage( p.getPackageBody());
	}
	
	/**
	 * We try to de-serialize a package twice. If we get a serialization error the first time, we will try recompiling
	 * the package.
	 * @param p
	 * @param ruleBase
	 * @throws Exception
	 */
	protected void addRulePackageToRuleBase(RulePackage p, RuleBase ruleBase, boolean firstTry ) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(p.getCompiledPackage());
		ObjectInputStream ois = new ObjectInputStream( bis );
		try {
			Package pkg = (Package) ois.readObject();
			ruleBase.addPackage( pkg );
			bis.close();
		} catch( Exception e ) {
			if (firstTry) {
				logger.info( "Error loading rule package " + p.getPackageName()+ ". Will recompile and try again");
				// Try one more time with a recompiled package
				addRulePackageToRuleBase( recompile( p ), ruleBase, false );
			} else {
				// Cannot be rescued
				throw e;
			}
		}
	}
	
	/**
	 * Compile rules and add to rule base without using serialized-compiled rules from DB. Just used for debugging.
	 * @param p
	 * @param ruleBase
	 * @throws Exception
	 */
	protected void addRulePackageToRuleBase2( RulePackage p, RuleBase ruleBase) throws Exception {
		String packageName = compiler.extractPackageName(p.getPackageBody());
		Package initialPackage = new Package(packageName);
		for (String knownType : compiler.extractPlaholderAccountType( p.getPackageBody() )) {
			logger.info("Add " + knownType + " placeholders to " + packageName + " package");
			addKnownTypeToPackage(initialPackage, knownType);
		}
		// Compile the package
		Package pkg = compiler.compile( p.getPackageBody(), initialPackage );
		ruleBase.addPackage( pkg );
	}
	
	/**
	 * Get the rule base for the specified account. In general, this pulls together all active rule packages applicable to the specified account 
	 * @param account
	 * @return A RuleBase
	 */
	public RuleBase getRuleBase( String knownType ) {
		try {
			Date now = new Date();
			if (savedTime!=null && savedType!=null) {
				long savedAge = now.getTime() - savedTime.getTime();
				if( knownType.equals(savedType) && savedAge < MAX_AGE) {
					return savedBase;
				}
			}
			savedType = null;
			savedBase = null;
			RuleBaseConfiguration confRuleBase = new RuleBaseConfiguration();
			// Be sure we use equality, not identity for facts.
			confRuleBase.setAssertBehaviour(AssertBehaviour.EQUALITY);
			confRuleBase.setShadowProxy( false );
			// Create a rule base
			RuleBase ruleBase = RuleBaseFactory.newRuleBase(confRuleBase);
			// Get the applicable packages
			List<RulePackage> packages = findActivePackages( knownType );
			for (RulePackage p : packages) {
//				addRulePackageToRuleBase(p, ruleBase, true);
				addRulePackageToRuleBase2(p, ruleBase);
			}
			savedType = knownType;
			savedBase = ruleBase;
			savedTime = now;
			return ruleBase;
		} catch (Exception e) {
			throw new RuntimeException( "Error building rule base" , e);
		}
	}
	
	/**
	 * Find the version of a named package that is active. 
	 * @param name Of the rule package
	 * @return The rule package or null if not found
	 */
	public RulePackage findActivePackage(String name) {
        String select = "SELECT p FROM RulePackage p WHERE p.packageName = :packageName AND p.packageStatus = :status";
        Query query = em.createQuery(select);
        query.setParameter("packageName", name);
        query.setParameter("status", ACTIVE_STATUS);
        List<RulePackage> items = query.getResultList();
        if (items.size()!=1) return null;
        return items.get(0);
	}
	
	/**
	 * Find all active rule packages applicable to an Account type. 
	 * NOTE: This method ignore AccountType and loads all active packages - The rules can limit their applicability as needed.
	 * @param accountType name (known type)
	 * @return A list of rule packages
	 */
	public List<RulePackage> findActivePackages( String knownType ) {
		Query query = em.createQuery("SELECT p FROM RulePackage p WHERE p.packageStatus = :status");
        query.setParameter("status", ACTIVE_STATUS);
		List<RulePackage> packages = query.getResultList();
		return packages;
	}

	
}
