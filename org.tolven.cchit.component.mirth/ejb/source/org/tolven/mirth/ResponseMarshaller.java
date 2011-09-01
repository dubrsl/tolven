package org.tolven.mirth;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;

import org.tolven.xml.ParseXML;
//import org.tolven.xml.TranscendNameSpacePrefixMapper;

/**
	 * Class for marshalling Trim objects
	 * @author syed
	 * added on 10/28/2009
	 */
	public class ResponseMarshaller extends ParseXML {
		@Override
		protected String getParsePackageName() {
			return "org.tolven.mirth";
		}
		
		/**
		 * Method to convert Trim to a payload
		 * @author syed
		 * added on 10/28/2009
		 */
		public OutputStream marshalToStream(Response response,OutputStream output) throws JAXBException {
	        Marshaller m = getMarshaller();
	        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        //m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new TranscendNameSpacePrefixMapper());
	        m.marshal(response, output );
	        
	        return output;
		}
		
		public  JAXBElement<Response> UnmarshalFromStream(Source is) throws JAXBException{
			return  (JAXBElement<Response>)getUnmarshaller().unmarshal(is);
		}
	}