package test.org.tolven.trim.scan;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.tolven.trim.ObjectFactory;
import org.tolven.trim.BindPhase;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;
import org.tolven.trim.scan.TraceScanner;

import test.org.tolven.trim.xml.XMLTestBase;

public class TestScanner extends XMLTestBase {
	private Logger logger = Logger.getLogger(this.getClass());
	protected static final ObjectFactory factory = new TrimFactory( );
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testPatientXML( ) throws JAXBException, IOException {
		JAXBContext jc = setupJAXBContext("org.tolven.trim");
		InputStream trimStream = getClass().getResourceAsStream("testPatient.trim.xml");
		Trim trim = (Trim) unmarshalStream(jc, trimStream, factory);
		TraceScanner scanner = new TraceScanner();
		scanner.setTrim( trim );
		scanner.setPhase(BindPhase.REQUEST);
		scanner.scan();
		scanner.printNodePaths();
	}
	
}
