package org.tolven.client.example;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.naming.NamingException;

import org.tolven.client.TolvenClient;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.logging.TolvenLogger;

public class SubmitCCRMessage extends TolvenClient {
	
	private TolvenUser tolvenUser;
	private static final String CCRns = "urn:astm-org:CCR";	

	public SubmitCCRMessage(String configDir) throws IOException {
		super(configDir);
		TolvenLogger.initialize();
	}
	
	/**
	 * Log in as the Tolven user that will be what HL7 calls the dataEnterer. 
	 * @param uid The user name (principal)
	 * @param password The user password
	 * @throws NamingException
	 * @throws GeneralSecurityException 
	 */
	public void setupUser( String uid, String password ) throws NamingException, GeneralSecurityException {
		tolvenUser = login(uid, password);
	}
	
	/**
	 * <p>Specify the account that the already-logged-in user will be submitting to.
	 * The user must already be a member of the specified account.
	 * This method actually finds the a Tolven accountUser record which represents this
	 * user's membership in an account.</p> 
	 * <p>A method is provided to access the AccountUser object
	 * (getAccountUser( )) and from this object, the Account and TolvenUser objects can be acquired.</p>
	 * @param accountId
	 * @throws NamingException
	 */
	public void setupAccount(long accountId) throws NamingException {
		loginToAccount(accountId);
	}	
	
	/**
	 * Create a TolvenMessage payload wrapper. Notice that the accountId and user id must be supplied in the wrapper.
	 * Tolven does not accept anonymous messages.
	 * @param ns The namespace that defines the payload 
	 * @return
	 */
	public TolvenMessageWithAttachments createTolvenMessage ( String ns ) throws Exception {
		TolvenMessageWithAttachments tm = new TolvenMessageWithAttachments();
		tm.setAccountId(getAccountUser().getAccount().getId());
		tm.setAuthorId(tolvenUser.getId());
		tm.setXmlNS( ns );
		// Use the Tolven data generator to create a random CCR Message
		// although this step could have just as easily opened an XML 
		// file containing the CCR.	
		byte[] payload = getGeneratorBean().generateCCRXML(1995);
		// Display the CCR
		TolvenLogger.info( new String(payload), SubmitCCRMessage.class );
		tm.setPayload(payload);

		return tm;
	}
	
	
	/**
	 * Put a message onto the processing queue
	 * @param tm
	 * @throws Exception
	 */
	public void submitMessage( TolvenMessageWithAttachments tm ) throws Exception {
		getDocBean().queueTolvenMessage( tm );
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		if (args.length < 4 ) {
			System.out.println( "Arguments: UserId password accountId configDirectory");
			return;
		}
		SubmitCCRMessage s = new SubmitCCRMessage(args[3]);
		s.setupUser(args[0], args[1] );
		s.setupAccount(Long.parseLong(args[2]));
		s.beginTransaction();
		TolvenMessageWithAttachments tm = s.createTolvenMessage( CCRns );
		s.submitMessage( tm );
		s.commitTransaction();
	}
}
