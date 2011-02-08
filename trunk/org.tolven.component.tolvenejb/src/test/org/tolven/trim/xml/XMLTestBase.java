package test.org.tolven.trim.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

//import org.tolven.doc.bean.NamespacePrefixMapperImpl;
import org.apache.log4j.Logger;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;

public abstract class XMLTestBase extends TestCase{
	private Logger logger = Logger.getLogger(this.getClass());
	
	public JAXBContext setupJAXBContext(String binding) throws JAXBException {
		JAXBContext jc;
		logger.info( "loading JAXB binding context: " + binding );
		jc = JAXBContext.newInstance( binding );
        return jc;
	}

	public String marshal( JAXBContext jc, Object top, boolean fragment) throws JAXBException, IOException {
	    Marshaller m = jc.createMarshaller();
	    m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	   m.setProperty( Marshaller.JAXB_FRAGMENT, fragment );
//	    m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.tolven.org/schema/trim4.xsd" );
//        m.setProperty(  "com.sun.xml.bind.namespacePrefixMapper",new NamespacePrefixMapperImpl());
	    StringWriter result = new StringWriter( 1000 ); 
	    m.marshal( top, result );
	    result.close();
	    return result.toString();
	}
	public String marshal( JAXBContext jc, Object top) throws JAXBException, IOException {
		return marshal( jc, top, Boolean.FALSE );
	}

	public Object unmarshal( JAXBContext jc, String xml, Object factory ) throws JAXBException, XMLStreamException {
		XMLInputFactory xmlfactory = XMLInputFactory.newInstance();
		StringReader stringReader = new StringReader( xml );
		XMLStreamReader xmlStreamReader = xmlfactory.createXMLStreamReader( stringReader );
        return unmarshalStream( jc, xmlStreamReader, factory );
	}

	public Object unmarshalStream( JAXBContext jc, XMLStreamReader input, Object factory ) throws JAXBException {
        Unmarshaller u = jc.createUnmarshaller();
    	u.setProperty("com.sun.xml.bind.ObjectFactory", factory);
        Object o = u.unmarshal(  input );
        return o;
	}
	
	public Trim unmarshalStream( JAXBContext jc, InputStream input, Object factory ) throws JAXBException {
        Unmarshaller u = jc.createUnmarshaller();
    	u.setProperty("com.sun.xml.bind.ObjectFactory", factory);
        Object o = u.unmarshal(  input );
        return (Trim) o;
	}

}
