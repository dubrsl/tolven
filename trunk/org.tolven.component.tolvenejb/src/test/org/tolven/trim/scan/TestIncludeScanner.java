package test.org.tolven.trim.scan;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.tolven.conceptgroup.ObjectFactory;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;

import test.org.tolven.trim.xml.XMLTestBase;

public class TestIncludeScanner extends XMLTestBase {
	protected static final TrimFactory factory = new TrimFactory( );
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 * Test inclusion
	 * @throws JAXBException
	 * @throws IOException
	 */
	public void testInclude() throws JAXBException, IOException {
		JAXBContext jc = setupJAXBContext("org.tolven.trim");
		InputStream extensionStream = getClass().getResourceAsStream("extension.trim.xml");
		Trim trim = unmarshalStream(jc, extensionStream, factory);
		SpecialIncludeScanner scanner = new SpecialIncludeScanner();
		scanner.setTrim( trim );
		scanner.setJc(jc);
		scanner.scan();
		logger.info( marshal(jc, trim) );
	}
	
	/**
	 * Test Valueset Binds.
	 * 
	 * @throws JAXBException
	 * @throws IOException
	 */
	public void testIncludeValueset() throws JAXBException, IOException {
		JAXBContext jc = setupJAXBContext("org.tolven.trim");
		InputStream prescriptionMedStream = getClass().getResourceAsStream("PrescriptionMedications.trim.xml");
		Trim trim = (Trim) unmarshalStream(jc, prescriptionMedStream, factory);
		SpecialIncludeScanner scanner = new SpecialIncludeScanner();
		scanner.setTrim( trim );
		scanner.setJc(jc);
		scanner.scan();
		logger.info( marshal(jc, trim ) );
		
	}
	
	/**
	 * Test Valueset Binds with duplicates.
	 * 
	 * @throws JAXBException
	 * @throws IOException
	 */
	public void testIncludeDuplicateValueset() throws JAXBException, IOException {
		JAXBContext jc = setupJAXBContext("org.tolven.trim");
		InputStream prescriptionMedStream = getClass().getResourceAsStream("PrescriptionMedications.trim.xml");
		Trim trim = (Trim) unmarshalStream(jc, prescriptionMedStream, factory);
		SpecialIncludeScanner scanner = new SpecialIncludeScanner();
		scanner.setTrim( trim );
		scanner.setJc(jc);
		scanner.scan();
		logger.info( marshal(jc, trim ) );
		
	}
}
