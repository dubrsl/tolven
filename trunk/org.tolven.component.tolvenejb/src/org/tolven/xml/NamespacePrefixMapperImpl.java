package org.tolven.xml;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class NamespacePrefixMapperImpl extends NamespacePrefixMapper  {

	public static final String NS_XS = "http://www.w3.org/2001/XMLSchema";
	public static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";

	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
//		TolvenLogger.info( "Namespace mapper: " + namespaceUri + " Suggestion:" + suggestion, NamespacePrefixMapperImpl.class );
		if( NS_XSI.equals(namespaceUri) ) return "xsi";
        if( NS_XS.equals(namespaceUri) ) return "xs";
		return suggestion;
	}
	/**
	 * Get Namespaces to include in root element
	 */
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { NS_XSI, NS_XS };
    }

}
