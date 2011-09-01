package org.tolven.doc;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.tolven.ccr.ex.ContinuityOfCareRecordEx;
import org.tolven.doc.entity.CCRException;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TRIMException;

/**
 * Services for XML parsing into an object graph in memory.
 * @author John Churin
 *
 */
public interface XMLLocal {


	/**
	 * <p>This method will unmarshal the XML content of the specified document
	 * into an object graph and return the head of that graph. Subsequent calls to get the graph will
	 * not unmarshal the XML again.</p>
	 * @return the object graph
	 * @throws JAXBException 
	 */
	public Object unmarshal(String bindingContext, InputStream input) throws JAXBException;
	public Object unmarshal(String ns, Reader reader) throws JAXBException;
	public Unmarshaller getUnmarshaller(String ns) throws JAXBException;

	public void marshalCCR( ContinuityOfCareRecordEx ccr, OutputStream output ) throws JAXBException, CCRException;

	public void marshalTRIM( Trim trim, OutputStream output ) throws JAXBException, TRIMException;

}
