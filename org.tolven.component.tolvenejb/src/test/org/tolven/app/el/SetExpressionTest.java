package test.org.tolven.app.el;

import junit.framework.TestCase;

import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.trim.Act;
import org.tolven.trim.ActStatus;
import org.tolven.trim.II;
import org.tolven.trim.Transition;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TransitionsEx;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;

public class SetExpressionTest extends TestCase {
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
		 * Set the value of a string in trim that already has a value
		 */
		public void testSetTrimName() {
			Trim trim = new TrimEx();
			trim.setName( "****" );
			te.addVariable( "trim", trim );
			te.setValue("#{trim.name}", MY_NAME_IS);
			System.out.println( trim.getName() );
			assertEquals( MY_NAME_IS, trim.getName() ) ;
		}

		/**
		 * Set the value of a string in trim that does not exist
		 */
		public void testSetTrimName2() {
			Trim trim = new TrimEx();
			te.addVariable( "trim", trim );
			te.setValue("#{trim.name}", MY_NAME_IS);
			System.out.println( trim.getName() );
			assertEquals( MY_NAME_IS, trim.getName() ) ;
		}
		
		/**
		 * Set the value of a string in a component of trim (Act in this case)
		 * This test depends on the act already existing in the trim.
		 */
		public void testSetTrimActInternalId() {
			Trim trim = new TrimEx();
			te.addVariable( "trim", trim );
			Act act = new Act();
			act.setInternalId(MY_NAME_IS);
			trim.setAct(act);
			te.setValue("#{trim.act.internalId}", MY_NAME_IS);
			System.out.println( trim.getAct().getInternalId() );
			assertEquals( MY_NAME_IS, trim.getAct().getInternalId() ) ;
		}
		
		/**
		 * Set the value of a string in a component of trim (Act in this case)
		 * While the trim must already exist, the act does not exist.  In this example
		 * <pre>#{trim.act.internalId}</pre>
		 * the act node is created and then the internalId attribute is set.
		 */
		public void testSetTrimActInternalId2() {
			Trim trim = new TrimEx();
			te.addVariable( "trim", trim );
			// Intentionally do not set act
//			Act act = new Act();
//			act.setInternalId(MY_NAME_IS);
//			trim.setAct(act);
			te.setValue("#{trim.act.internalId}", MY_NAME_IS);
			System.out.println( trim.getAct().getInternalId() );
			assertEquals( MY_NAME_IS, trim.getAct().getInternalId() ) ;
		}

		/**
		 * More complex test to set the value of a string in a component of trim (Act in this case)
		 * <pre>#{trim.act.label.internalId}</pre>
		 * the act node and label node are created and then the value attribute is set.
		 */
		public void testSetTrimActLabel() {
			Trim trim = new TrimEx();
			te.addVariable( "trim", trim );
			te.setValue("#{trim.act.label.value}", MY_NAME_IS);
			System.out.println( trim.getAct().getLabel().getValue() );
			assertEquals( MY_NAME_IS, trim.getAct().getLabel().getValue() ) ;
		}
		/**
		 * More complex test setting with two values in one datatype (a CD in this case)
		 */
		public void testSetCode() {
			Trim trim = new TrimEx();
			te.addVariable( "trim", trim );
			te.setValue("#{trim.act.code.CD.code}", "code");
			te.setValue("#{trim.act.code.CD.codeSystem}", "codeSystem");
			System.out.println( "code: " + trim.getAct().getCode().getCD().getCode() + " codesystem: " + trim.getAct().getCode().getCD().getCodeSystem());
			assertEquals( "code", trim.getAct().getCode().getCD().getCode() ) ;
			assertEquals( "codeSystem", trim.getAct().getCode().getCD().getCodeSystem() ) ;
		}

		/**
		 * Test setting an enumerated value - ActStatus
		 */
		public void testSetActStatus1() {
			Trim trim = new TrimEx();
			te.addVariable( "trim", trim );
			te.setValue("#{trim.act.statusCode}", ActStatus.ACTIVE);
			System.out.println( "statucCode: " + trim.getAct().getStatusCode());
			assertEquals( ActStatus.ACTIVE, trim.getAct().getStatusCode() ) ;
		}
		
		/**
		 * Test setting an enumerated value - ActStatus, using strings rather than enums
		 */
		public void testSetActStatus2() {
			Trim trim = new TrimEx();
			te.addVariable( "trim", trim );
			te.setValue("#{trim.act.statusCodeValue}", "active");
			System.out.println( "statucCode: " + trim.getAct().getStatusCode());
			assertEquals( ActStatus.ACTIVE, trim.getAct().getStatusCode() ) ;
			assertEquals( "active", te.evaluate("#{trim.act.statusCodeValue}") ) ;
		}

		/**
		 * Test setting transitions
		 */
		public void testSetTransitions() {
			TrimFactory factory = new TrimFactory();
			Trim trim = new TrimEx();
			TransitionsEx trans = (TransitionsEx) factory.createTransitions();
			trim.setTransitions(trans);
			Transition t = new Transition();
			t.setName("transname");
			trans.getTransitions().add(t);
			te.addVariable( "trim", trim );
			te.setValue("#{trim.transitions.transition['transname'].from}", "fromStatus");
			System.out.println( "TransitionName: " + trans.getTransition().get("transname").getFrom());
		}

		/**
		 * Even more complex test to set the value in a list component
		 * <pre>#{trim.act.id.for['xxx']}</pre>
		 */
		public void testSetId() {
			Trim trim = new TrimEx();
			te.addVariable( "trim", trim );
			te.setValue("#{trim.act.id.for['rootxxx'].extension}", "id1234");
			te.setValue("#{trim.act.id.for['rootyy'].extension}", "id56789");
			for (II ii : trim.getAct().getId().getIIS()) {
				System.out.println( "root: " + ii.getRoot() + " extension: " + ii.getExtension() );
			}
			Object extension1 = te.evaluate("#{trim.act.id.for['rootxxx'].extension}");
			assertEquals( "id1234", extension1 ) ;
			Object extension2 = te.evaluate("#{trim.act.id.for['rootyy'].extension}");
			assertEquals( "id56789", extension2 ) ;
		}

		/**
		 * Test setting a long value in a field
		 */
		public void testSetLongField() {
			ExpressionEvaluator ee = new ExpressionEvaluator();
			TrimEx trim = new TrimEx();
			ee.addVariable( "trim", trim );
			Long testValue = 12345L; 
			ee.setValue("#{trim.field['longValue']}", testValue);
			Long result = (Long) trim.getField().get("longValue");
			System.out.println( "Value: " + result);
			assertEquals( testValue, result ) ;
		}
		
		/**
		 * Test setting a long value in a field that already has a field value.
		 */
		public void testResetLongField() {
			ExpressionEvaluator ee = new ExpressionEvaluator();
			TrimEx trim = new TrimEx();
			// Set the initial value - to zero
			trim.getField().put("longValue", 0L);
			Long testValue = 12345L; 
			ee.addVariable( "trim", trim );
			// Use EL to change the value
			ee.setValue("#{trim.field['longValue']}", testValue);
			Long result = (Long) trim.getField().get("longValue");
			System.out.println( "Value: " + result);
			assertEquals( testValue, result ) ;
		}
		
		/**
		 * When just evaluating, do not create intermediate nodes
		 */
		public void testNoNewForEvaluate() {
			Trim trim = new TrimEx();
			te.addVariable( "trim", trim );
			Object result = te.evaluate("#{trim.act.internalId}");
			// The act node should NOT be created in the case of reading
			assertNull( result ) ;
			assertNull( trim.getAct() ) ;
		}

		/**
		 * Make sure this resolver is silent on non-trim classes
		 */
		public void testSetNonTrim() {
			SubBean subBean = new SubBean();
//			subBean.setString1("duh");
			te.addVariable( "subBean", subBean );
			// This should succeed
			System.out.println( "subBean before: " + te.evaluate("#{subBean.string1}") );
			te.setValue("#{subBean.string1}", MY_NAME_IS);
			System.out.println( "subBean after: " + te.evaluate("#{subBean.string1}") );
			// This should fail because trim.subBean is not filled in and we don't have a factory method for it.
			try {
				te.setValue("#{subBean.subBean.string1}", MY_NAME_IS);
				fail("Non-trim beans should fail to fill in intermediate nodes");
			} catch (Exception e) {
//				e.printStackTrace();
				System.out.println( "#{subBean.subBean.string1} failed as expected" );
			}
			// This should work
			subBean.setSubBean(new SubBean());
			te.setValue("#{subBean.subBean.string1}", MY_NAME_IS);
			assertEquals( te.evaluate("#{subBean.subBean.string1}"), MY_NAME_IS ) ;
			System.out.println( "subBean.subBean.string1 after: " + te.evaluate("#{subBean.subBean.string1}") );
		}
		
		 public class SubBean {
			private String string1;
			private SubBean subBean;
			public SubBean() {}
			
			public String getString1() {
				return string1;
			}

			public void setString1(String string1) {
				this.string1 = string1;
			}

			public SubBean getSubBean() {
				return subBean;
			}

			public void setSubBean(SubBean subBean) {
				this.subBean = subBean;
			}
		}

}
