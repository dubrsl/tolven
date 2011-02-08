package org.tolven.doc;

import java.security.PrivateKey;

import javax.xml.bind.JAXBException;

import org.tolven.core.entity.AccountUser;
import org.tolven.doc.entity.DocXML;

/**
 * Services for XML parsing. These service require the AccountPrivateKey which generally means that
 * the caller must be logged in.
 * @author John Churin
 *
 */
public interface XMLProtectedLocal {

	/**
	 * <p>This method will unmarshal the XML content of the specified document
	 * into an object graph and return the head of that graph. Subsequent calls to get the graph will
	 * not unmarshal the XML again.</p>
	 * @return the object graph
	 * @throws JAXBException 
	 */
	public Object unmarshal(DocXML doc, AccountUser activeAccountUser, PrivateKey userPrivateKey) throws JAXBException;

}
