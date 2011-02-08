package org.tolven.client.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.tolven.client.TolvenClient;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageAttachment;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.Act;
import org.tolven.trim.ActClass;
import org.tolven.trim.ActMood;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimFactory;

/**
 * Submit a message for Tolven processing. Note: This module can submit a message but
 * security prevents it from reading back the message from the server.
 * @author John Churin
 */
public class SendTolvenMessage extends TolvenClient{

	private long accountId;
	private TolvenUser tolvenUser;
	static final String TRIM_NS = "urn:tolven-org:trim:4.0"; 
	static final String TRIM_Package = "org.tolven.trim"; 
    static JAXBContext jc;
	static TrimFactory factory = new TrimFactory();

	public SendTolvenMessage(String configDir) throws IOException {
		super(configDir);
		TolvenLogger.initialize();
	}
	
	/**
	 * Find the user that is the author. Then get all accounts known to the user.
	 * @param uid The user name (principal)
	 * @throws NamingException
	 * @throws GeneralSecurityException 
	 */
	public void setupUser( String uid, String password ) throws NamingException, GeneralSecurityException {
		tolvenUser = login(uid, password);
	}
	

	static JAXBContext setupJAXBContext() throws JAXBException {
	    if( jc==null) {
			jc = JAXBContext.newInstance( TRIM_Package );
	    }
        return jc;
	}
	public Act createAct() {
		Act act = factory.createNewAct(ActClass.DOC, ActMood.EVN);
		factory.setCodeAsCD( act, "CBC", "MyRADSystem");
		act.setId(factory.createSETIISlot());
		act.getId().getIIS().add( factory.createNewII( "4.5.6.7", "id12345"));
		act.getId().getIIS().add( factory.createNewII( "hospital2", "9998-123"));
		return act;
	}
	
	public Trim createTrim1( ) {
//		addPatient( act, "echr:patient-659122" );
		Trim trim = factory.createTrim();
		trim.setPage("document.xhtml");
		trim.setDrilldown("documentDD.xhtml");
		trim.setDescription("A generic document");
		trim.setName("document");
		trim.setAct(createAct());
		return trim;
	}
	
	public TolvenMessageWithAttachments createTolvenMessage(  ) {
		TolvenMessageWithAttachments tm = new TolvenMessageWithAttachments();
		tm.setAccountId(this.getAccountId());
		tm.setAuthorId(tolvenUser.getId());
		tm.setXmlNS( TRIM_NS );
		return tm;
	}
	
	public void addTrimAsPayload( Trim trim, TolvenMessage tm ) throws JAXBException {
	    JAXBContext jc = setupJAXBContext( );
		ByteArrayOutputStream output = new ByteArrayOutputStream( );
        Marshaller m = jc.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        m.marshal( trim, output );
        tm.setPayload(output.toByteArray());
	}
	
	public void addAttachment( TolvenMessageWithAttachments tm, File attachment) throws IOException {
		FileInputStream fi = new FileInputStream( attachment );
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buf[] = new byte[100];
		while ( true ) {
			int count = fi.read(buf);
			if (count < 0) break;
			baos.write(buf, 0, count);
		}
		fi.close();
		TolvenMessageAttachment tma = new TolvenMessageAttachment();
		tma.setPayload(baos.toByteArray());
		tma.setDescription("Attachment");
		if (attachment.getName().endsWith(".gif")) {
			tma.setMediaType("image/gif");
		}
		if (attachment.getName().endsWith(".jpg") || attachment.getName().endsWith(".jpeg")) {
			tma.setMediaType("image/jpeg");
		}
		if (attachment.getName().endsWith(".png") || attachment.getName().endsWith(".png")) {
			tma.setMediaType("image/png");
		}
		if (attachment.getName().endsWith(".tif") || attachment.getName().endsWith(".tiff")) {
			tma.setMediaType("image/tiff");
		}
		tm.getAttachments().add(tma);
	}
	
	public void sendMessage( Trim trim, File attachment ) throws Exception {
		TolvenMessageWithAttachments tm = createTolvenMessage();
		addTrimAsPayload( trim, tm );
		if (attachment!=null) {
			addAttachment( tm, attachment );
		}
		getDocBean().queueTolvenMessage( tm );
	}

	public void createAndSendMessage( File attachment ) throws Exception {
		TolvenMessageWithAttachments tm = createTolvenMessage();
		Trim trim = createTrim1();
		addTrimAsPayload( trim, tm );
		if (attachment!=null) {
			addAttachment( tm, attachment );
		}
		getDocBean().queueTolvenMessage( tm );
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 4 ) {
			System.out.println( "Arguments: UserId password accountId tolvenConfigDirectory");
			return;
		}
        SendTolvenMessage stm = new SendTolvenMessage(args[3]);
		stm.beginTransaction();
		stm.setupUser(args[0], args[1] );
		stm.setAccountId(Long.parseLong(args[2]));
		stm.createAndSendMessage(null);
		stm.commitTransaction();
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
}
