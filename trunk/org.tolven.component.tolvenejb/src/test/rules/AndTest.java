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
 * @author <your name>
 * @version $Id: AndTest.java,v 1.2 2009/11/16 07:03:32 jchurin Exp $
 */  

package test.rules;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemoryEventManager;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.audit.event.ActivationLogEvent;
import org.drools.audit.event.ILogEventFilter;
import org.drools.audit.event.LogEvent;
import org.drools.rule.Package;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.Account;
import org.tolven.rules.PackageCompiler;
import org.tolven.rules.PlaceholderFact;
public class AndTest extends TestCase {
	private Logger logger = Logger.getLogger(this.getClass());
	private Package pkg;
	private PackageCompiler compiler = new PackageCompiler();
	private AccountMenuStructure msPatient;
	private AccountMenuStructure msObs;
	class WMLogger extends WorkingMemoryLogger {

		public WMLogger(WorkingMemoryEventManager workingMemoryEventManager) {
			super(workingMemoryEventManager);
		}

		@Override
		public void logEventCreated(LogEvent event) {
			if (event instanceof ActivationLogEvent) {
				ActivationLogEvent ale = (ActivationLogEvent) event;
				System.out.println( "Fire rule '" + ale.getRule() + "' using: " + ale.getDeclarations() );
			} else {
				System.out.println( event.toString());
			}
		}
	}
	
	class WMLogFilter implements ILogEventFilter {

		@Override
		public boolean acceptEvent(LogEvent event) {
			if (event.getType()==LogEvent.AFTER_ACTIVATION_FIRE) return true;
//			if (event.getType()==LogEvent.ACTIVATION_CREATED) return true;
			return false;
		}
	}
	public void addToList(PlaceholderFact fact, String list) {
		logger.info(list);
	}
	
	public void testAnd() throws Exception {
		
		RuleBase ruleBase = openRuleBase("test/rules/andTest.drl", AssertBehaviour.EQUALITY);
	   	//load up the rulebase
		StatefulSession workingMemory = ruleBase.newStatefulSession();
	    workingMemory.setGlobal("out",System.out);
	    WMLogger logger = new WMLogger(workingMemory);
	    logger.addFilter(new WMLogFilter());
	    Account account = new Account();
	    account.setId(111);
	    // Patient MS
		msPatient = new AccountMenuStructure();
		msPatient.setAccount(account);
		msPatient.setRole("placeholder");
		msPatient.setNode("patient");
		msPatient.setPath("echr:patient");
		MSColumn firstNameColumn = new MSColumn();
		firstNameColumn.setInternal("string01");
		firstNameColumn.setHeading("lastName");
		msPatient.getColumns().add(firstNameColumn);
		MSColumn dobColumn = new MSColumn();
		dobColumn.setInternal("date01");
		dobColumn.setHeading("dob");
		msPatient.getColumns().add(dobColumn);
		workingMemory.insert(msPatient);
		// Observation MS
		msObs = new AccountMenuStructure();
		msObs.setAccount(account);
		msObs.setRole("placeholder");
		msObs.setPath("echr:patient:observation");
		msObs.setNode("observation");
		MSColumn testColumn = new MSColumn();
		testColumn.setInternal("string01");
		testColumn.setHeading("test");
		msObs.getColumns().add(testColumn);
		MSColumn patientColumn = new MSColumn();
		patientColumn.setInternal("parent01");
		patientColumn.setHeading("patient");
		msObs.getColumns().add(patientColumn);
		workingMemory.insert(msObs);
		// Patient MD
		MenuData mdPatient = new MenuData();
		mdPatient.setId(123);	// Fake id
		mdPatient.setMenuStructure(msPatient);
		mdPatient.setPath("Patient: Thompson");
		mdPatient.setString01("Thompson");
		mdPatient.setDate01(new Date());
		workingMemory.insert(mdPatient);
		// Observation MD
		MenuData mdObs = new MenuData();
		mdObs.setId(456);	// Fake id
		mdObs.setMenuStructure(msObs);
		mdObs.setString01("Weight");
		mdObs.setPath("Observation: Weight");
		mdObs.setParent01(mdPatient);
		workingMemory.insert(mdObs);
		// Go
	    workingMemory.fireAllRules();   
	    workingMemory.dispose();
	}
	
	public void checkPackageSerialization( Package pkg ) throws Exception{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos = new ObjectOutputStream(bos); 
		oos.writeObject(pkg);
		// Now read the stream back
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray()); 
		ObjectInputStream ois = new ObjectInputStream(bis); 
		Package pkg2 = (Package) ois.readObject();
	}
	
	public RuleBase openRuleBase( String ruleFile, AssertBehaviour assertBehaviour ) throws Exception {
		//read in the source
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assertFalse(classLoader==null );
		InputStream inputStream = classLoader.getResourceAsStream( ruleFile );
		assertFalse(inputStream==null );
		
		Reader reader = new InputStreamReader( inputStream );
		StringWriter sw = new StringWriter();
		int c;
		while ((c=reader.read())!=-1) {sw.write(c);}
		String packageBody = sw.toString();
		String packageName = compiler.extractPackageName(packageBody);
		logger.info("Package name: " + packageName);
		for (String knownType : compiler.extractPlaholderAccountType( packageBody )) {
			logger.info("accountType: " + knownType );
		}

		inputStream = classLoader.getResourceAsStream( ruleFile );
		pkg = compiler.compile(packageBody, null);
//		pkg = compiler.compile(packageBody, initialPackage);
		checkPackageSerialization( pkg);

		RuleBaseConfiguration confRuleBase = new RuleBaseConfiguration();
		confRuleBase.setAssertBehaviour(assertBehaviour);

		//add the package to a rulebase (deploy the rule package).
		RuleBase ruleBase = RuleBaseFactory.newRuleBase(confRuleBase);
		
		ruleBase.addPackage( pkg );
		return ruleBase;
	}
}
