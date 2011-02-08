package test.org.tolven.trim.xml;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.tolven.trim.Act;
import org.tolven.trim.ActClass;
import org.tolven.trim.ActMood;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;


public class ActXMLTests extends XMLTestBase {
	protected static final TrimFactory factory = new TrimFactory( );
	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public Act createAct1( ) {
		Act act = factory.createNewAct(ActClass.OBS, ActMood.EVN);
//		act.setInternalId("92994566");
		factory.setCodeAsCD( act, "CBC", "MyRADSystem");
		act.setId(factory.createSETIISlot());
		act.getId().getIIS().add( factory.createNewII( "4.5.6.7", "id12345"));
		act.getId().getIIS().add( factory.createNewII( "4.5.6.8", "xxxx9998-123"));
		return act;
	}

	public void testAct1() throws JAXBException, IOException, XMLStreamException {
		JAXBContext jc = setupJAXBContext("org.tolven.trim");
		// Create an object graph
		Trim trim = factory.createTrim();
		trim.setAct(createAct1());
		// Marshal to XML.
		String xml = marshal(jc, trim );
		logger.info( xml );
		// Now unmarshal back to objects
		Trim trim2 = (Trim) unmarshal(jc, xml, factory);
		Act act = trim2.getAct();
		if (act instanceof Act ) { 
			logger.info( "TRIM Act: " + ((Act)act).getClassCode() + "-" + ((Act)act).getMoodCode());
		}
	}
	
}
