package test.rules;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.java.JavaDialectConfiguration;
import org.tolven.ccr.ContinuityOfCareRecord;
import org.tolven.ccr.ex.CCRFactory;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.ActClass;
import org.tolven.trim.ActMood;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.GTSSlot;
import org.tolven.trim.ParticipationType;
import org.tolven.trim.Role;
import org.tolven.trim.RoleClass;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;
/**
 * Test JBoss Rule 4.0
 * @author John Churin
 *
 */
public class RuleBaseTests extends TestCase {
	private static final TrimFactory factory = new TrimFactory();
	private static final CCRFactory ccrFactory = new CCRFactory();  

	public void testRuleBase1( ) throws Exception {
		TolvenLogger.info( "testRuleBase1 - display Hello from rule", RuleBaseTests.class);
		RuleBase ruleBase = openRuleBase("test/rules/unitTest1.drl", AssertBehaviour.EQUALITY);
       	//load up the rulebase
		StatefulSession workingMemory = ruleBase.newStatefulSession();
        workingMemory.setGlobal("out",System.out);
		workingMemory.insert("Hello");
        workingMemory.fireAllRules();   
        workingMemory.dispose();
	}

	public void testRuleBase2a( ) throws Exception {
		TolvenLogger.info( "testRuleBase2a - assert XXX twice with identity: two XXX display", RuleBaseTests.class);
		RuleBase ruleBase = openRuleBase("test/rules/unitTest2.drl", AssertBehaviour.IDENTITY);
       	//load up the rulebase
		StatefulSession workingMemory = ruleBase.newStatefulSession();
        // With identity, this is two separate facts, with equality, only one
        workingMemory.insert(new String("XXX"));
        workingMemory.insert(new String("XXX"));
        workingMemory.setGlobal("out",System.out);
        workingMemory.fireAllRules();
        workingMemory.dispose();
	}

	public Role createRole1() {
		Role role = factory.createRole();
		role.setClassCode(RoleClass.PAT);
		return role;
	}
	public Role createRole2() {
		Role role = factory.createRole();
		role.setClassCode(RoleClass.ASSIGNED);
		return role;
	}

	public ActParticipation createPart2( ) {
		ActParticipation part = factory.createActParticipation();
		part.setTypeCode(ParticipationType.AUT);
		part.setRole(createRole2());
		return part;
	}
	
	public ActParticipation createPart1( ) {
		ActParticipation part = factory.createActParticipation();
		part.setTypeCode(ParticipationType.SBJ);
		part.setRole(createRole1());
		return part;
	}
	public Act createAct1( ) {
		Act act = factory.createNewAct(ActClass.OBS, ActMood.EVN);
//		act.setInternalId("92994566");
		factory.setCodeAsCD( act, "CBC", "MyRADSystem");
		act.setId(factory.createSETIISlot());
		act.getId().getIIS().add( factory.createNewII( "4.5.6.7", "id12345"));
		act.getId().getIIS().add( factory.createNewII( "4.5.6.8", "xxxx9998-123"));
		GTSSlot effectiveTime = factory.createGTSSlot();
		effectiveTime.setTS(factory.createNewTS(new Date()));
		act.setEffectiveTime(effectiveTime);
		act.getParticipations().add(createPart1());
		act.getParticipations().add(createPart2());
		return act;
	}

	public Trim createTrim1( ) {
		Trim trim = factory.createTrim();
		trim.setAct(createAct1());
		return trim;
	}

	public void testRuleBase2b( ) throws Exception {
		TolvenLogger.info( "testRuleBase2b - assert XXX twice with equality: one XXX display", RuleBaseTests.class);
		RuleBase ruleBase = openRuleBase("test/rules/unitTest2.drl", AssertBehaviour.EQUALITY);
       	//load up the rulebase
		StatefulSession workingMemory = ruleBase.newStatefulSession();
        // With identity, this is two separate facts, with equality, only one
        workingMemory.insert(new String("XXX"));
        workingMemory.insert(new String("XXX"));
        workingMemory.setGlobal("out",System.out);
        workingMemory.fireAllRules();   
        workingMemory.dispose();
	}
public static class Counter {
	int count = 0;
	public void bump() {
		count++;
	}
}
	public void testRuleBase3( ) throws Exception {
		TolvenLogger.info( "testRuleBase3 - assert a TRIM object", RuleBaseTests.class);
		RuleBase ruleBase = openRuleBase("test/rules/unitTest3.drl", AssertBehaviour.EQUALITY);
       	//load up the rulebase
		StatefulSession workingMemory = ruleBase.newStatefulSession();
        // Assert a trim object and see what the rules do with it
        Counter counter = new Counter();
        workingMemory.setGlobal("counter", counter);
        workingMemory.setGlobal("out",System.out);
        workingMemory.insert(createTrim1());
        workingMemory.fireAllRules();   
        workingMemory.dispose();
        TolvenLogger.info( "Had: " + counter.count + " rule firings", RuleBaseTests.class);
        assertTrue( counter.count==8);
	}

	public void testRuleBase4t( ) throws Exception {
		TolvenLogger.info( "testRuleBase4t - assert TRIM one act with two participations", RuleBaseTests.class);
		RuleBase ruleBase = openRuleBase("test/rules/unitTest4t.drl", AssertBehaviour.EQUALITY);
       	//load up the rulebase
		StatefulSession workingMemory = ruleBase.newStatefulSession();
        // Assert a trim object and see what the rules do with it
        Counter counter = new Counter();
        workingMemory.setGlobal("counter", counter);
        workingMemory.setGlobal("out",System.out);
        workingMemory.insert(createTrim1());
        workingMemory.fireAllRules();   
        workingMemory.dispose();
        TolvenLogger.info( "Had: " + counter.count + " rule firings", RuleBaseTests.class);
        assertTrue( counter.count==8);
	}

	public ContinuityOfCareRecord unmarshalCCR( String fileName ) throws Exception {
//		String CCR_NS = "urn:astm-org:CCR"; 
		ClassLoader classLoader = RuleBaseTests.class.getClassLoader();
		assertFalse(classLoader==null );
		InputStream inputStream = classLoader.getResourceAsStream( fileName );
		
	    JAXBContext jc = JAXBContext.newInstance( "org.tolven.ccr", classLoader  );
        Unmarshaller u = jc.createUnmarshaller();
//    	u.setProperty("com.sun.xml.internal.bind.ObjectFactory", ccrFactory);
    	u.setProperty("com.sun.xml.bind.ObjectFactory", ccrFactory);
        ContinuityOfCareRecord ccr = (ContinuityOfCareRecord) u.unmarshal( inputStream );
        inputStream.close();
        return ccr;
	}

	public void testRuleBase4( ) throws Exception {
		TolvenLogger.info( "testRuleBase4 - assert a CCR object", RuleBaseTests.class);
		// Read and unmarshal an XML file containing ccr
		ContinuityOfCareRecord ccr = unmarshalCCR( "test/rules/testdata.ccr");
		RuleBase ruleBase = openRuleBase("test/rules/ccrexplode.drl", AssertBehaviour.EQUALITY);
       	//load up the rulebase
		StatefulSession workingMemory = ruleBase.newStatefulSession();
        workingMemory.setGlobal("out",System.out);
        // Assert a trim object and see what the rules do with it
        workingMemory.insert(ccr);
        workingMemory.fireAllRules();   
        workingMemory.dispose();
	}
	
	public void testRuleBase5( ) throws Exception {
		TolvenLogger.info( "testRuleBase5 - MVEL test", RuleBaseTests.class);
       	//load up the rulebase
		RuleBase ruleBase = openRuleBase("test/rules/unitTest5.drl", AssertBehaviour.EQUALITY);
		StatefulSession workingMemory = ruleBase.newStatefulSession();
        workingMemory.setGlobal("out",System.out);
        // Assert a trim object and see what the rules do with it

        MockParent parent = new MockParent();
        parent.setField1("I'm in field2");
        MockChild child = new MockChild();
        child.setField2("Field2 contents");
        child.setParent(parent);
        
        workingMemory.insert(child);
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
		
		//optionally read in the DSL (if you are using it).
		//Reader dsl = new InputStreamReader( DroolsTest.class.getResourceAsStream( "/mylang.dsl" ) );

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
