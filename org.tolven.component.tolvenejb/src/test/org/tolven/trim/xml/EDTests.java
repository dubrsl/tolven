package test.org.tolven.trim.xml;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.ActClass;
import org.tolven.trim.ActMood;
import org.tolven.trim.ED;
import org.tolven.trim.EDSlot;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;


public class EDTests extends XMLTestBase {
	private Logger logger = Logger.getLogger(this.getClass());
	protected static final TrimFactory factory = new TrimFactory( );

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testEDValue() throws JAXBException, IOException, XMLStreamException {
		JAXBContext jc = setupJAXBContext("org.tolven.trim");
		// Create an object graph
		Trim trim = factory.createTrim();
		Act act = factory.createNewAct(ActClass.OBS, ActMood.EVN);
		
		EDSlot text = factory.createEDSlot();
		ED textED = factory.createED();
		textED.setValue("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa12345678".getBytes());
		text.setED(textED);
		act.setText(text);
		trim.setAct(act);

		// Marshal to XML.
		String xml = marshal(jc, trim );
		logger.info( xml);
		
		// Now unmarshal back to objects
		Trim trim2 = (Trim) unmarshal(jc, xml, factory);
		Act act2 = trim2.getAct();
		if (act2 instanceof Act ) { 
			logger.info( "TRIM Act: " + ((Act)act).getClassCode() + "-" + ((Act)act).getMoodCode());
			logger.info(new String(((Act)act2).getText().getED().getValue()));
		}
	}

}
