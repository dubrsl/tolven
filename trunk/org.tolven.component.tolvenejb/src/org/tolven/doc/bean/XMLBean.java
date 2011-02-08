package org.tolven.doc.bean;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.tolven.ccr.ContinuityOfCareRecord;
import org.tolven.ccr.ex.CCRFactory;
import org.tolven.ccr.ex.ContinuityOfCareRecordEx;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.doc.entity.CCRException;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TRIMException;
import org.tolven.trim.ex.TrimFactory;

@Stateless()
@Local(XMLLocal.class)
public class XMLBean implements XMLLocal {
	static final String CCR_NS = "urn:astm-org:CCR"; 
	static final String TRIM_NS = "urn:tolven-org:trim:4.0"; 
	private static final TrimFactory trimFactory = new TrimFactory();  
	private static final CCRFactory ccrFactory = new CCRFactory();  
	private Map<String, JAXBContext> jaxbContexts;
	@EJB TolvenPropertiesLocal propertyBean;
    /**
     * Create or use a JAXB context. We keep a map of already-used bindings in a static variable. 
     * @return A JAXB context.
     * @throws JAXBException
     */
    protected JAXBContext setupJAXBContext(String bindingContext) throws JAXBException {
		if (bindingContext==null) throw new IllegalArgumentException( "JAXB binding context missing" ); 
	    JAXBContext jc;
	    if (jaxbContexts==null) jaxbContexts = new HashMap<String, JAXBContext>( 4 );
	    if( jaxbContexts.containsKey(bindingContext) ) {
	    	jc = jaxbContexts.get(bindingContext);
	    } else {
			jc = JAXBContext.newInstance( bindingContext, XMLBean.class.getClassLoader() );
			jaxbContexts.put(bindingContext, jc);
	    }
        return jc;
	}

    /**
     * Given a namespace, return the binding context that understands it, if any
     * @param ns
     * @return
     */
    public String decodeBindingContext( String ns) {
		if (CCR_NS.equals(ns)) return "org.tolven.ccr";
		if (TRIM_NS.equals(ns)) return "org.tolven.trim";
		return null;
    }
	public void marshalCCR( ContinuityOfCareRecordEx ccr, OutputStream output ) throws JAXBException, CCRException {
		// Reset the actors list with the actors we now have in the transient map
		ContinuityOfCareRecordEx.Actors actors = new ContinuityOfCareRecordEx.Actors();
		actors.getActor().addAll(ccr.getActorMap().values());
		ccr.setActors(actors);
		if (ccr.getPatient().size()>2) throw new CCRException( "No more than two patients allowed A2.5.2.6(3) ");
		if (ccr.getPatient().size()==0) throw new CCRException("Patient required A2.5.2.6(1)" );
	    JAXBContext jc = setupJAXBContext( decodeBindingContext(CCR_NS));
        JAXBElement<ContinuityOfCareRecord> root = new JAXBElement<ContinuityOfCareRecord>(new QName( CCR_NS, "ContinuityOfCareRecord"), ContinuityOfCareRecord.class, ccr);
        Marshaller m = jc.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        m.marshal( root, output );
//        StringWriter result = new StringWriter( 10000 ); 
//        return result.toString();
	}

	public void marshalTRIM( Trim trim, OutputStream output ) throws JAXBException, TRIMException {
	    JAXBContext jc = setupJAXBContext( decodeBindingContext(TRIM_NS));
        Marshaller m = jc.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
//        m.setProperty(  "com.sun.xml.bind.namespacePrefixMapper",new NamespacePrefixMapperImpl());
        m.marshal( trim, output );
	}

	/**
	 * <p>This method will unmarshal the XML into an object graph and return the head of that graph. </p>
	 * @return the object graph
	 * @throws JAXBException 
	 */
	public Object unmarshal(String ns, InputStream input) throws JAXBException {
//		TolvenLogger.info( "XMLBean.unmarshal ns=" + ns, XMLBean.class);
		JAXBContext jc = setupJAXBContext(decodeBindingContext(ns));
        Unmarshaller u = jc.createUnmarshaller();
        if (ns.equals(TRIM_NS)) {
        	u.setProperty("com.sun.xml.bind.ObjectFactory", trimFactory);
        } else if (ns.equals(CCR_NS)) {
        	u.setProperty("com.sun.xml.bind.ObjectFactory", ccrFactory);
        }

	    return u.unmarshal( new StreamSource( input ) );
	}
	
	public Object unmarshal(String ns, Reader reader) throws JAXBException {
//		TolvenLogger.info( "XMLBean.unmarshal ns=" + ns, XMLBean.class);
		JAXBContext jc = setupJAXBContext(decodeBindingContext(ns));
        Unmarshaller u = jc.createUnmarshaller();
        if (ns.equals(TRIM_NS)) {
        	u.setProperty("com.sun.xml.bind.ObjectFactory", trimFactory);
        } else if (ns.equals(CCR_NS)) {
        	u.setProperty("com.sun.xml.bind.ObjectFactory", ccrFactory);
        }
	    return u.unmarshal( new StreamSource( reader ) );
	}

	public Unmarshaller getUnmarshaller(String ns) throws JAXBException {
		JAXBContext jc = setupJAXBContext(decodeBindingContext(ns));
        Unmarshaller u = jc.createUnmarshaller();
	    return u;
	}

}
