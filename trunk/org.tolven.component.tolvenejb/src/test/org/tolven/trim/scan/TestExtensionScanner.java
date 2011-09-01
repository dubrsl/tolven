package test.org.tolven.trim.scan;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.tolven.conceptgroup.ObjectFactory;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;
import org.tolven.trim.scan.ExtensionScanner;

import test.org.tolven.trim.xml.XMLTestBase;

public class TestExtensionScanner extends XMLTestBase {
	private Logger logger = Logger.getLogger(this.getClass());
	protected static final TrimFactory factory = new TrimFactory( );
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 * Detect and prevent cycles in extends
	 * @throws JAXBException
	 * @throws IOException
	 */
	public void testBaseExtension() throws JAXBException, IOException {
		JAXBContext jc = setupJAXBContext("org.tolven.trim");
		InputStream extensionStream = getClass().getResourceAsStream("extension.trim.xml");
		Trim extensionTrim = (Trim) unmarshalStream(jc, extensionStream, factory);
		Set<String> cycles = new HashSet<String>( );
		String extend = extensionTrim.getExtends();
		while (extend!=null) {
			if (cycles.contains(extend)) {
				throw new RuntimeException( "Cycle detected in trim hierarchy: " +  cycles);
			}
			cycles.add(extend);
			InputStream baseStream = getClass().getResourceAsStream(extend);
			Trim baseTrim = (Trim) unmarshalStream(jc, baseStream, factory);
			extend = baseTrim.getExtends();
			ExtensionScanner scanner = new ExtensionScanner();
			scanner.setTrim( baseTrim );
			scanner.setTargetTrim( extensionTrim );
			scanner.scan();
		}
		logger.info( this.marshal(jc, extensionTrim) );
	}
	
}
