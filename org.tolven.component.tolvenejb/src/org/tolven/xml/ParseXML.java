package org.tolven.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public abstract class ParseXML {
	private static JAXBContext jc;
	
	/**
	 * Override this method to return the package
	 * @return
	 */
	protected abstract String getParsePackageName();
	
	protected JAXBContext getJAXBContext() throws JAXBException {
		if (jc==null) {
			jc = JAXBContext.newInstance(getParsePackageName(), getClass().getClassLoader());
		}
		return jc;
	}
	
	protected Unmarshaller getUnmarshaller() throws JAXBException {
        return getJAXBContext().createUnmarshaller();
	}

	protected Marshaller getMarshaller() throws JAXBException {
        Marshaller m = getJAXBContext().createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        m.setProperty(  "com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
        return m;

	}
}
