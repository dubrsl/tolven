package test.org.tolven.app.el;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.tolven.el.ExpressionEvaluator;

public class DotExpressionTest extends TestCase {
	private static final String PROPERTY_NAME1 = "my.property.value";
	private static final String PROPERTY_VALUE1 = "THIS IS an unimportant value";
	private static final String PROPERTY_NAME2 = "my.property.name";
	private static final String PROPERTY_VALUE2 = "THIS IS an unimportant value2";
	private static final String PROPERTY_NAME3 = "my";
	private static final String PROPERTY_VALUE3 = "THIS IS an unimportant value3";
	
		ExpressionEvaluator ee;

		@Override
		protected void setUp() throws Exception {
			super.setUp();
			ee = new ExpressionEvaluator();
		}

		@Override
		protected void tearDown() throws Exception {
			super.tearDown();
		}
		
		/**
		 * Test the basic approach without doing anything special: each segment of the string, except the last, is
		 * a map containing a map. The last item is a map entry pointing to the value.
		 */
		public void testBasicApproach() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("property", PROPERTY_VALUE1);
			ee.addVariable("my", map);
			Object result1 = ee.evaluate("#{my.property}", String.class);
			// Pie and zoom should be available
			assertEquals(PROPERTY_VALUE1, result1  );
		}
		/**
		 * Add property names containing one dot, not just simple variable names.
		 */
		public void testPropertyName1() {
			ee.addProperty(PROPERTY_NAME1, PROPERTY_VALUE1);
			Object result1 = ee.evaluate("#{" + PROPERTY_NAME1 + "}");
			// Pie and zoom should be available
			assertEquals(PROPERTY_VALUE1, result1  );
		}

		/**
		 * Add property names containing two dots, not just simple variable names.
		 */
		public void testPropertyName2() {
			ee.addProperty(PROPERTY_NAME2, PROPERTY_VALUE2);
			Object result1 = ee.evaluate("#{" + PROPERTY_NAME2 + "}");
			// Pie and zoom should be available
			assertEquals(PROPERTY_VALUE2, result1  );
		}
		
		/**
		 * Add property names containing no dots, just a simple variable namem but calling addProperty.
		 */
		public void testPropertyName3() {
			ee.addProperty(PROPERTY_NAME3, PROPERTY_VALUE3);
			Object result1 = ee.evaluate("#{" + PROPERTY_NAME3 + "}");
			// Pie and zoom should be available
			assertEquals(PROPERTY_VALUE3, result1  );
		}

		/**
		 * Add several properties
		 */
		public void testPropertyName4() {
			ee.addProperty(PROPERTY_NAME1, PROPERTY_VALUE1);
			ee.addProperty(PROPERTY_NAME2, PROPERTY_VALUE2);
//			ee.addProperty(PROPERTY_NAME3, PROPERTY_VALUE3);
			String result1 = (String) ee.evaluate("${" + PROPERTY_NAME1 + "}", String.class);
			// Pie and zoom should be available
			assertEquals(PROPERTY_VALUE1, result1  );
			Object result2 = ee.evaluate("${" + PROPERTY_NAME2 + "}", String.class);
			// Pie and zoom should be available
			assertEquals(PROPERTY_VALUE2, result2  );
//			Object result3 = ee.evaluate("${" + PROPERTY_NAME3 + "}", String.class);
//			// Pie and zoom should be available
//			assertEquals(PROPERTY_VALUE3, result3  );
		}
}
