package test.org.tolven.trim;

import org.tolven.trim.Act;
import org.tolven.trim.ActClass;
import org.tolven.trim.ActMood;
import org.tolven.trim.ObjectFactory;

import test.org.tolven.trim.xml.XMLTestBase;

public class ActTests extends XMLTestBase {

	public void testAct1() {
		ObjectFactory factory = new ObjectFactory();
		Act act = factory.createAct();
		act.setClassCode(ActClass.OBS);
		act.setMoodCode(ActMood.EVN);
		
	}
}
