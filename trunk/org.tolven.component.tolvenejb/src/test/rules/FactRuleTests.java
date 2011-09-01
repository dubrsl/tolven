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
 * @version $Id: FactRuleTests.java,v 1.2 2009/12/07 23:42:59 jchurin Exp $
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
import org.drools.facttemplates.Fact;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateImpl;
import org.drools.facttemplates.FieldTemplate;
import org.drools.facttemplates.FieldTemplateImpl;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.Extractor;
import org.tolven.app.entity.AccountMenuStructure;
import org.tolven.app.entity.MSColumn;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;
import org.tolven.rules.PackageCompiler;
import org.tolven.rules.PlaceholderFact;
import org.tolven.rules.PlaceholderFactTemplate;
import org.tolven.rules.SimpleFieldTemplate;
public class FactRuleTests extends TestCase {
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
	public void testFieldTemplateSerialization() throws Exception{
		FieldTemplate fieldTemplate = new SimpleFieldTemplate("aName", 234, String.class);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos = new ObjectOutputStream(bos); 
		oos.writeObject(fieldTemplate);
		// Now read the stream back
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray()); 
		ObjectInputStream ois = new ObjectInputStream(bis); 
		FieldTemplate fieldTemplate2 = (FieldTemplate) ois.readObject();
		assertEquals("aName", fieldTemplate2.getName());
	}
	
	public void test1() throws Exception {
		
		RuleBase ruleBase = openRuleBase("test/rules/facts.drl", AssertBehaviour.EQUALITY);
	   	//load up the rulebase
		StatefulSession workingMemory = ruleBase.newStatefulSession();
	    workingMemory.setGlobal("out",System.out);
	    workingMemory.setGlobal("frt",this);
	    WMLogger logger = new WMLogger(workingMemory);
	    logger.addFilter(new WMLogFilter());
	    // an event.log file is created in the log dir (which must exist)
	    // in the working directory
		FactTemplate patientFactTemplate = pkg.getFactTemplate("patient");
		PlaceholderFact patientFact = (PlaceholderFact) patientFactTemplate.createFact(0);
		MenuData mdPatient = new MenuData();
		mdPatient.setId(123);	// Fake id
		mdPatient.setMenuStructure(msPatient);
		mdPatient.setString01("Thompson");
		mdPatient.setDate01(new Date());
		patientFact.setPlaceholder(mdPatient);
		workingMemory.insert(patientFact);
		FactTemplate obsFactTemplate = pkg.getFactTemplate("observation");
		PlaceholderFact obsFact = (PlaceholderFact) obsFactTemplate.createFact(1);
		MenuData mdObs = new MenuData();
		mdObs.setId(456);	// Fake id
		mdObs.setMenuStructure(msObs);
		mdObs.setString01("Weight");
		mdObs.setParent01(mdPatient);
		obsFact.setPlaceholder(mdObs);
		workingMemory.insert(obsFact);
	    workingMemory.fireAllRules();   
	    workingMemory.dispose();
	}
	
	public FactTemplate getPatientFT(Package pkg ) {
		msPatient = new AccountMenuStructure();
		msPatient.setNode("patient");
		MSColumn firstNameColumn = new MSColumn();
		firstNameColumn.setInternal("string01");
		firstNameColumn.setHeading("lastName");
		msPatient.getColumns().add(firstNameColumn);
		MSColumn dobColumn = new MSColumn();
		dobColumn.setInternal("date01");
		dobColumn.setHeading("dob");
		msPatient.getColumns().add(dobColumn);
		FactTemplate ft = new PlaceholderFactTemplate(pkg, msPatient); 
		return ft;
	}
	
	public FactTemplate getObservationFT(Package pkg ) {
		msObs = new AccountMenuStructure();
		msObs.setNode("observation");
		MSColumn testColumn = new MSColumn();
		testColumn.setInternal("string01");
		testColumn.setHeading("test");
		msObs.getColumns().add(testColumn);
		MSColumn patientColumn = new MSColumn();
		patientColumn.setInternal("parent01");
		patientColumn.setHeading("patient");
		msObs.getColumns().add(patientColumn);
		FactTemplate ft = new PlaceholderFactTemplate(pkg, msObs); 
		return ft;
	}
	
	public Package getFactTemplates(String packageName) {
		Package pkg = new Package(packageName);
		pkg.addFactTemplate(getPatientFT(pkg));
		pkg.addFactTemplate(getObservationFT(pkg));
		return pkg;
	}
	/**
	 * We actually return the package that has been serialized to ensure that everything serialized properly.
	 * @param pkg
	 * @return
	 * @throws Exception
	 */
	public Package checkPackageSerialization( Package pkg ) throws Exception{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos = new ObjectOutputStream(bos); 
		oos.writeObject(pkg);
		// Now read the stream back
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray()); 
		ObjectInputStream ois = new ObjectInputStream(bis); 
		Package pkg2 = (Package) ois.readObject();
		FactTemplate ft = pkg2.getFactTemplate("patient");
		logger.info(ft);
		for (FieldTemplate fieldTemplate: ft.getAllFieldTemplates()) {
			logger.info(fieldTemplate);
		}
		FactTemplate ft2 = pkg2.getFactTemplate("observation");
		logger.info(ft2);
		for (FieldTemplate fieldTemplate: ft2.getAllFieldTemplates()) {
			logger.info(fieldTemplate);
		}
		listPackageContents(pkg2);
		return pkg2;
	}
	/**
	 * Display package fact templates to log
	 * @param pkg
	 * @return
	 * @throws Exception
	 */
	public void listPackageContents( Package pkg ) throws Exception{
		for (Rule rule : pkg.getRules()) {
			logger.info( "Rule: " + rule.getName());
			
			for (Declaration declaration :rule.getDeclarations()) {
				logger.info( "declaration: " + declaration);
				logger.info( "pattern: " + declaration.getPattern());
				Extractor extractor = declaration.getExtractor();
				logger.info( "extractor: " + extractor);
			}
		}
//		FactTemplate ft = pkg2.getFactTemplate("patient");
//		logger.info(ft);
//		for (FieldTemplate fieldTemplate: ft.getAllFieldTemplates()) {
//			logger.info(fieldTemplate);
//		}
//		FactTemplate ft2 = pkg2.getFactTemplate("observation");
//		logger.info(ft2);
//		for (FieldTemplate fieldTemplate: ft2.getAllFieldTemplates()) {
//			logger.info(fieldTemplate);
//		}
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
		Package initialPackage = getFactTemplates(packageName);
//		pkg = compiler.compile(packageBody, null);
		pkg = compiler.compile(packageBody, initialPackage);
		pkg = checkPackageSerialization( pkg);

		RuleBaseConfiguration confRuleBase = new RuleBaseConfiguration();
		confRuleBase.setAssertBehaviour(assertBehaviour);

		//add the package to a rulebase (deploy the rule package).
		RuleBase ruleBase = RuleBaseFactory.newRuleBase(confRuleBase);
		
		ruleBase.addPackage( pkg );
		return ruleBase;
	}
}
