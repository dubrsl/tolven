package org.tolven.doc.bean;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

import org.tolven.core.entity.AccountUser;
import org.tolven.doc.XMLProtectedLocal;
import org.tolven.doc.entity.DocXML;
import org.tolven.security.DocProtectionLocal;

@Stateless()
@Local(XMLProtectedLocal.class)
public class XMLProtectedBean extends XMLBean implements XMLProtectedLocal {

    @EJB private DocProtectionLocal docProtectionBean;
	/**
	 * <p>This method will unmarshal the XML content of the specified document
	 * into an object graph and return the head of that graph. Subsequent calls to get the graph will
	 * not unmarshal the XML again.</p>
	 * @return the object graph
	 * @throws JAXBException 
	 */
	public Object unmarshal(DocXML doc, AccountUser activeAccountUser, PrivateKey userPrivateKey) throws JAXBException {
//		TolvenLogger.info( "XMLProtected.unmarshal doc id=" + doc.getId(), XMLProtectedBean.class);
		byte[] c = docProtectionBean.getDecryptedContent(doc, activeAccountUser, userPrivateKey);
		if (c==null) return null;
		return unmarshal( doc.getXmlNS(), new ByteArrayInputStream( c ));
	}

}
