package test.org.tolven.app.el;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.EN;
import org.tolven.trim.ENSlot;
import org.tolven.trim.ENXPSlot;
import org.tolven.trim.EntityNamePartType;
import org.tolven.trim.EntityNameUse;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;

public class ExpressionTest extends TestCase {
	private static final String TEST_STRING = "This is a string";
	private static final String MY_NAME_IS = "My name is";
		TrimExpressionEvaluator te;
		@Override
		protected void setUp() throws Exception {
			super.setUp();
			te = new TrimExpressionEvaluator();
		}

		@Override
		protected void tearDown() throws Exception {
			super.tearDown();
		}
		
		/**
		 * Get the contents of a string variable that we add to the context
		 *
		 */
		public void testExpressionString() {
			te.addVariable( "MyString", TEST_STRING);
			Object result = te.evaluate("#{MyString} #{1+2} And some additional text");
			TolvenLogger.info("MyString=" + result, ExpressionTest.class);
		}

		/**
		 * Get the contents of a trim field
		 *
		 */
		public void testTrimNameField() {
			Trim trim = new TrimEx();
			trim.setName( MY_NAME_IS );
			te.addVariable( "trim", trim );
		//	 resolve a top-level property
			Object result1 = te.evaluate("#{trim.name}");
			TolvenLogger.info("trim.name=" + result1, ExpressionTest.class);
			assertTrue( MY_NAME_IS.equals(result1) );
			Boolean result2 = (Boolean) te.evaluate("#{trim.drilldown==null}");
			assertTrue( result2);
			TolvenLogger.info("trim.drilldown=" + result2, ExpressionTest.class);
		}
		
		/**
		 * Get the conditional access to a trim field
		 * Notice requires space after the colon ":"
		 *
		 */
		public void testConditionalAccess() {
			Trim trim = new TrimEx();
			trim.setName( MY_NAME_IS );
			te.addVariable( "trim", trim );
		//	 resolve a top-level property
			Object result1 = te.evaluate("#{'a'!='a'?trim.xxx: trim.name}");
			TolvenLogger.info("trim.name=" + result1, ExpressionTest.class);
			assertTrue( MY_NAME_IS.equals(result1) );
			Boolean result2 = (Boolean) te.evaluate("#{trim.drilldown==null}");
			assertTrue( result2);
			TolvenLogger.info("trim.drilldown=" + result2, ExpressionTest.class);
		}

		/**
		 * Get the contents of a trim field
		 *
		 */
		public void testTrimString() {
			Trim trim = new TrimEx();
			trim.setName( MY_NAME_IS );
			trim.setDrilldown("DrilldownFieldContents");
			te.addVariable( "trim", trim );
		//	 resolve a top-level property
			Object result1 = te.evaluate("[trim]Name=#{trim.name}, Drilldown=#{trim.drilldown}");
			TolvenLogger.info(result1, ExpressionTest.class);
		}

		/**
		 * Test EN formatting
		 *
		 */
		public void testENString() {
			TrimFactory factory = new TrimFactory();
			EN en = factory.createEN();
			// Set the use code to L (legal)
			en.getUses().add(EntityNameUse.L);
			ENXPSlot family = factory.createENXPSlot();
			family.setType(EntityNamePartType.FAM);
			family.setST(factory.createNewST("Family") );
			en.getParts().add(family);
			ENXPSlot given1 = factory.createENXPSlot();
			given1.setType(EntityNamePartType.GIV);
			given1.setST(factory.createNewST("Given1") );
			en.getParts().add(given1);
			ENXPSlot given2 = factory.createENXPSlot();
			given2.setType(EntityNamePartType.GIV);
			given2.setST(factory.createNewST("Given2") );
			en.getParts().add(given2);
			ENSlot enSlot = factory.createENSlot();
			enSlot.getENS().add(en);
			te.addVariable( "enSlot", enSlot );
			Object result1 = te.evaluate("#{enSlot.EN['L'].formattedParts['GIV']}");
			TolvenLogger.info(result1, ExpressionTest.class);
			assertTrue( "Given1 Given2".equals(result1) );
			Object result2 = te.evaluate("#{enSlot.EN['L'].formattedParts['GIV FAM']}");
			TolvenLogger.info(result2, ExpressionTest.class);
			assertTrue( "Given1 Given2 Family".equals(result2) );
			Object result3 = te.evaluate("#{enSlot.EN['L'].formattedParts['GIV[0] FAM']}");
			TolvenLogger.info(result3, ExpressionTest.class);
			assertTrue( "Given1 Family".equals(result3) );
			Object result4 = te.evaluate("#{enSlot.EN['L'].formattedParts['GIV[0] GIV[1] FAM']}");
			TolvenLogger.info(result4, ExpressionTest.class);
			assertTrue( "Given1 Given2 Family".equals(result4) );
			assertTrue( "Given1 Family".equals(result3) );
		}

		/**
		 * Filtering a trim based on name (event)
		 *
		 */
		public void testTrimFilter() {
			Trim trim = new TrimEx();
			trim.setName( "event1" );
			te.addVariable( "trim", trim );
		//	 resolve a top-level property
			Object result1 = te.evaluate("#{trim.isName['event'].name}");
			assertTrue( result1==null );
			TolvenLogger.info("Should be null: " + result1, ExpressionTest.class);
			Object result2 = te.evaluate("#{trim.isName['event1'].name}");
			assertTrue( result2!=null );
			TolvenLogger.info("Should be event1: " + result2, ExpressionTest.class);
			Object result3 = te.evaluate("#{trim.isName['event.*'].name}");
			assertTrue( result3!=null );
			TolvenLogger.info("Should be event1: " + result3, ExpressionTest.class);
		}

		/**
		 * Get the contents of a string variable that we add to the context
		 *
		 */
		public void testGetString() {
			te.addVariable( "MyString", TEST_STRING);
			Object result = te.evaluate("#{MyString}");
			TolvenLogger.info("MyString=" + result, ExpressionTest.class);
			assertTrue( TEST_STRING.equals(result) );
		}

		public static String fun1( ) {
			return "fun1value";
		}

		public static String fun2(String value ) {
			return "fun2value=" + value;
		}

		public void testFunction() throws SecurityException, NoSuchMethodException {
			te.addFunction("", "fun1", this.getClass().getMethod("fun1"));
			Object result = te.evaluate( "#{fun1()}");
			assertTrue( ("fun1value".equals( (String)result )));
		}

		public void testFunctions() throws SecurityException, ClassNotFoundException {
			te.addFunctions("", this.getClass().getName());
			Object result = te.evaluate( "#{fun1()}");
			assertTrue( ("fun1value".equals( (String)result )));
		}

		public void testFunctionsWithArg() throws SecurityException, ClassNotFoundException {
			te.addFunctions("", this.getClass().getName());
			Object result = te.evaluate( "#{fun2('hi')}", String.class);
			assertTrue( ("fun2value=hi".equals( (String)result )));
		}

		/**
		 * Create a variable containing a double.
		 *
		 */
		public void testRealValue() {
		//	 resolve top-level property
			te.setValue( "#{val}", 12345.6, Double.class);
			Object result0 = te.evaluate("#{val}");
			assertTrue( result0 instanceof Double );
			assertTrue( ((Double)result0 == 12345.6) );
			TolvenLogger.info("testRealValue: " + result0, ExpressionTest.class);
		}

		/**
		 * Test resolving a bean property. In this case, use Date(). Then return the time attribute (using getTime()).
		 */
		public void testDateValue() {
			Date now = new Date();
			te.setValue( "#{current}", now, Date.class);
			Object result = te.evaluate( "#{current.time}", long.class);
			long msTime = (Long) result;
			TolvenLogger.info("testDateValue: current.time = " + msTime, ExpressionTest.class);
			assertTrue( msTime==now.getTime());
		}
		
		public void testNegativeValue() {
			Object bool1 = te.evaluate( "#{-1<0}", Object.class);
			TolvenLogger.info("testNegativeValue (-1 < 0) = " + bool1, ExpressionTest.class);
			assertTrue( ((Boolean)bool1) );
		}

		public void testNegativeVariable() {
			te.setValue( "#{val}", -1, int.class);
			Object result = te.evaluate( "#{val<0}", Boolean.class);
			TolvenLogger.info("testNegativeValue val=-1; #(val < 0) = " + result, ExpressionTest.class);
			assertTrue( ((Boolean)result) );
		}
		
		public void testEqualVariables() {
			te.setValue( "#{valA}",  -3, int.class);
			te.setValue( "#{valB}",  -3, int.class);
			Object bool1 = te.evaluate( "#{valA==valB}", boolean.class);
			TolvenLogger.info("testEqualVariables valA=-3; valB=-3; #(valA==valB} = " + bool1, ExpressionTest.class);
			assertTrue( ((Boolean)bool1) );
		}
		
		public void testUnequalVariables() {
			te.setValue( "#{valA}", -3, int.class);
			te.setValue( "#{valB}", 3, int.class);
			Object bool1 = te.evaluate( "#{valA==valB}", boolean.class);
			TolvenLogger.info("testUnequalVariables valA=-3; valB=3; #(valA==valB} = " + bool1, ExpressionTest.class);
			assertFalse( ((Boolean)bool1) );
		}

		public void testNotEqualVariables() {
			te.setValue( "#{valA}", -3, int.class);
			te.setValue( "#{valB}", 3, int.class);
			Object bool1 = te.evaluate( "#{valA!=valB}", boolean.class);
			TolvenLogger.info("testNotEqualVariables valA=-3; valB=3; #(valA!=valB} = " + bool1, ExpressionTest.class);
			assertTrue( (Boolean)bool1 );
		}
		
		public void testCoerceToBoolean1() {
			Object bool2 = te.evaluate( "#{false}", Boolean.class);
//			boolean rslt = (Boolean) te.factory.coerceToType(bool2.getValue(te.context), boolean.class);
//			TolvenLogger.info("testCoerceToBoolean1 #{false} = " + rslt, ExpressionTest.class);
			assertFalse( (Boolean)bool2 );
		}

		public void testCoerceToBoolean2() {
			Object bool2 = te.evaluate( "#{true}", Boolean.class);
//			boolean rslt = (Boolean) te.factory.coerceToType(bool2.getValue(te.context), boolean.class);
//			TolvenLogger.info("testCoerceToBoolean2 #{true} = " + rslt, ExpressionTest.class);
			assertTrue( (Boolean)bool2 );
		}

		public void testCoerceToBoolean3() {
			Object bool2 = te.evaluate( "#{null}", Boolean.class);
//			boolean rslt = (Boolean) te.factory.coerceToType(bool2.getValue(te.context), boolean.class);
//			TolvenLogger.info("testCoerceToBoolean3 #{null} = " + rslt, ExpressionTest.class);
			assertFalse( (Boolean)bool2 );
		}

		public void testCoerceToBoolean4() {
			te.setValue( "#{answer}", -1, int.class);
			Object bool2 = te.evaluate( "#{0 gt answer}", Boolean.class);
//			boolean rslt = (Boolean) te.factory.coerceToType(bool2.getValue(te.context), boolean.class);
			TolvenLogger.info("testCoerceToBoolean4 answer=-1; #{0 gt answer} = " + bool2, ExpressionTest.class);
			assertTrue( (Boolean)bool2 );
		}

		public void testMixedELValue() {
			Object string1 = te.evaluate( "Six=#{2*3}", Object.class);
			assertEquals( string1, "Six=6");
			TolvenLogger.info("testMixedELValue = " + string1, ExpressionTest.class);
		}
		
		public void testXML1() {
			Object xml = te.evaluate("<node>#{2*3}</node>");
			assertEquals("<node>6</node>", xml);
		}
			
		public void testXML2() {
			Object xml = te.evaluate("<node>#{'<sub></sub>'}</node>");
			assertEquals("<node><sub></sub></node>", xml);
		}

		/**
		 * Get the contents of an array
		 *
		 */
		public void testArrayList() {
			List<String> list = new ArrayList<String>();
			list.add("content of array element 0");
			list.add("content of array element 1");
			te.addVariable( "array", list);
			Object result = te.evaluate("#{array[0]}");
			TolvenLogger.info("array[0]=" + result, ExpressionTest.class);
			assertTrue( list.get(0).equals(result) );
		}
		
		/**
		 * Test an escaped expression
		 */
		public void testEscape() {
			final String escaped = "\\#{nothing}"; // In XML, this would be \#{nothing}
			final String escapedResult = "#{nothing}"; 
			Object result = te.evaluate(escaped);
			TolvenLogger.info("escaped=" + result, ExpressionTest.class);
			assertTrue( escapedResult.equals(result) );
		}
		
		/**
		 * Test variable context push pop
		 */
		public void testPush() {
			te.setValue("#{var}", "35");
			te.pushContext();
			te.setValue("#{var}", 96);
			Object pushedContextResult = te.evaluate("#{var}");
			assertEquals(pushedContextResult, 96 );
			te.popContext();
			Object poppedContextResult = te.evaluate("#{var}");
			assertEquals(poppedContextResult, "35" );
			
		}

		/**
		 * Clear variable test
		 */
		public void testClearVariables() {
			te.setValue("#{var}", "37");
			Object result = te.evaluate("#{var}");
			assertEquals(result, "37" );
			te.clearVariables();
			Object result2 = te.evaluate("#{var}");
			assertNull( result2 );
			
		}
		
		/**
		 * Clear pushed variable test (should only clear current context, not pushed context)
		 */
		public void testClearVariablesInPushedContext() {
			te.setValue("#{var1}", "pie");
			te.pushContext();
			te.setValue("#{var2}", "zoom");
			Object result1 = te.evaluate("#{var1}#{var2}");
			// Pie and zoom should be available
			assertEquals(result1, "piezoom" );
			// ClearVariables should only clear pie
			te.clearVariables();
			// Only pie is available now
			Object result2 = te.evaluate("#{var1}#{var2}");
			assertEquals(result2, "pie" );
		}

}
