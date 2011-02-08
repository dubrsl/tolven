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
 * @version $Id: ConceptGroupRuleTests.java,v 1.1 2009/11/11 19:43:02 jchurin Exp $
 */  

package test.rules;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemoryEventManager;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.audit.WorkingMemoryConsoleLogger;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.audit.event.ActivationLogEvent;
import org.drools.audit.event.ILogEventFilter;
import org.drools.audit.event.LogEvent;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;
import org.tolven.conceptgroup.ObjectFactory;

import junit.framework.TestCase;

public class ConceptGroupRuleTests extends TestCase {
	
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
	
	public void test1() throws Exception {
		RuleBase ruleBase = openRuleBase("test/rules/conceptGroupRules.drl", AssertBehaviour.EQUALITY);
	   	//load up the rulebase
		StatefulSession workingMemory = ruleBase.newStatefulSession();
	    workingMemory.setGlobal("out",System.out);
	    workingMemory.setGlobal("cgf",new ObjectFactory());
	    WMLogger logger = new WMLogger(workingMemory);
	    logger.addFilter(new WMLogFilter());
	    // an event.log file is created in the log dir (which must exist)
	    // in the working directory
	    workingMemory.fireAllRules();   
	    workingMemory.dispose();
	}
	
	public RuleBase openRuleBase( String ruleFile, AssertBehaviour assertBehaviour ) throws Exception {
		//read in the source
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assertFalse(classLoader==null );
		InputStream inputStream = classLoader.getResourceAsStream( ruleFile );
		assertFalse(inputStream==null );
		Reader source = new InputStreamReader( inputStream );
		
		//Use package builder to build up a rule package.
		//An alternative lower level class called "DrlParser" can also be used...
		PackageBuilderConfiguration conf = new PackageBuilderConfiguration();
		JavaDialectConfiguration javaConf = (JavaDialectConfiguration) conf.getDialectConfiguration( "java" );
		javaConf.setJavaLanguageLevel( "1.5" );
		PackageBuilder builder = new PackageBuilder(conf);

		//this wil parse and compile in one step
		//NOTE: There are 2 methods here, the one argument one is for normal DRL.
		builder.addPackageFromDrl( source );

		//Use the following instead of above if you are using a DSL:
		//builder.addPackageFromDrl( source, dsl );
		
		//get the compiled package (which is serializable)
		Package pkg = builder.getPackage();

		RuleBaseConfiguration confRuleBase = new RuleBaseConfiguration();
		confRuleBase.setAssertBehaviour(assertBehaviour);

		//add the package to a rulebase (deploy the rule package).
		RuleBase ruleBase = RuleBaseFactory.newRuleBase(confRuleBase);
		
		ruleBase.addPackage( pkg );
		return ruleBase;
	}
}
