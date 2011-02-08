package org.tolven.client.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.TrimHeader;
import org.tolven.client.TolvenClient;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.CE;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.TrimEx;
import org.tolven.trim.ex.TrimFactory;

/**
 * <p>Find a Patient trim, update the trim using EL, then submit the updated trim for Tolven processing.</p> 
 * <p>The example demonstrates the following:</p> 
 * @author John Churin
 */
public class UpdateTrim extends TolvenClient{

	private TolvenUser tolvenUser;
	private static final String TRIM_NS = "urn:tolven-org:trim:4.0"; 
	private static final String TRIM_Package = "org.tolven.trim"; 
	private static JAXBContext jc;
	private static TrimFactory factory = new TrimFactory();

	public UpdateTrim(String configDir) throws IOException {
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
	 * Get the (demographics) Trim for a patient for the purpose of updating. 
	 * Find the placeholder with the specified placeholderId. 
	 * @param placeholderId
	 * @return
	 */
	public MenuData findMenuData( String placeholderId ) {
		return null;
	}

	/**
	 * Get a new trim prepared for updating the specified placeholder.
	 * @param mdPlaceholder
	 * @return
	 */
	public Trim getTrimForUpdate( MenuData mdPlaceholder, String trimName) {
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public void updatePatient( Trim trim ) {
		
	}

	/**
	 * In order to create a trim, we ask the server to perform several operations that effectively populate
	 * a new trim.
	 * <ul>
	 * <li>The server will find the trim, by name, in the trim repository.</li>
	 * <li>Technically, trims are stored as XML blobs in the database.</li>
	 * <li>We will send a map of name-value pairs to the server along with our request so that the
	 * substitution variables in the TRIM will be replaced with values in supplied map. See getVariables( ) for
	 * more details.</li>
	 * <li></li>
	 * <li></li>
	 * </ul>
	 * For each trim or parsed trim, we need to substitute expressions such as
	 * #{patient.lastName} etc.
	 * 
	 * @return
	 * @throws NamingException 
	 * @throws JAXBException 
	 */
	public Trim createTrim( ) throws JAXBException, NamingException {
		TrimEx trim = getTrimBean().evaluateAndParseTrim( getTrimName(), getVariables() );
		return trim;
	}
	
	/**
	 * Get the name of the trim to create.
	 * This trim is almost identical to the regular reg/evn/patient trim
     * except that the substitution variables are simplified for this example. 
     * If that trim has not already been uploaded to Tolven, </li>

	 * @return The name of the Trim to create
	 */
	public String getTrimName() {
		return "reg/evn/patient";
	}
	
	/**
	 * As a convention, we use "source" in a trim document for a generic source for
	 * pre-populating the trim. For example, in the patient.trim.xml file, 
	 * <pre>
	 *	&lt;part&gt;
	 *  	&lt;label&gt;First Name&lt;/label&gt;
	 *  	&lt;type&gt;GIV&lt;/type&gt;
	 *  	&lt;ST&gt;#{source.firstName}&lt;/ST&gt;
	 *  &lt;/part&gt;
	 * </pre>
	 * @return a map of source variables.
	 */
	protected Map<String, Object> getSourceVariables() {
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put("mrn", "M00000123");
		sourceMap.put("firstName", "Able");
		sourceMap.put("lastName", "Baker");
		CE gender = factory.createCE();
		gender.setDisplayName("Male");
		gender.setCode("C0024554"); // Male
//		gender.setDisplayName("Female");
//		gender.setCode("C0015780"); // Female
		gender.setCodeSystem("2.16.840.1.113883.6.56");
		gender.setCodeSystemVersion("2007AA");
		sourceMap.put("gender", gender); 
		{ // Date of Birth
			GregorianCalendar cal = new GregorianCalendar();
			cal.set(1957, 07, 23, 0, 0, 0);
			sourceMap.put("dob", cal.getTime());
		}
		sourceMap.put("homeTelecom", "707-123-4567");
		sourceMap.put("homeAddr1", "123 Elm Street");
		sourceMap.put("homeAddr2", null);
		sourceMap.put("homeCity", "Anywhere");
		sourceMap.put("homeState", "NY");
		sourceMap.put("homeZip", "98765-4321");
		sourceMap.put("homeCountry", "US");
		return sourceMap;
	}
	
	/**
	 * To create a trim instance, we supply a map of name/value pairs to
	 * be used by EL expressions (substitution variables) embedded in the Trim XML.
	 * Simple datatypes (String, Date, etc) as well as complex objects can be provided
	 * as long as the objects correspond to Java Beans conventions and are serializable. 
	 * The Expression Language can navigate the contents of a bean's getXXX() methods and
	 * using a dot can access child objects as well. In this example, the Account object and TolvenUser
	 * objects are provided in the map.  
	 * @return A map of variables to be presented to the server for substitution into the trim
	 * being instantiated.
	 */
	protected Map<String, Object> getVariables() {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("account", getAccountUser().getAccount());
		variables.put("user", getAccountUser().getUser());
		variables.put("now", new Date());
		variables.put("source", getSourceVariables());
		return variables;
	}
	
	/**
	 * Create a TolvenMessage payload wrapper. Notice that the accountId and user id must be supplied in the wrapper.
	 * Tolven does not accept anonymous messages.
	 * @param ns The namespace that defines the payload 
	 * @return
	 */
	public TolvenMessageWithAttachments createTolvenMessage( String ns ) {
		TolvenMessageWithAttachments tm = new TolvenMessageWithAttachments();
		tm.setAccountId(getAccountUser().getAccount().getId());
		tm.setAuthorId(tolvenUser.getId());
		tm.setXmlNS( ns );
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

	public void submitMessage( TolvenMessageWithAttachments tm ) throws Exception {
		getDocBean().queueTolvenMessage( tm );
	}
	
	static JAXBContext setupJAXBContext() throws JAXBException {
	    if( jc==null) {
			jc = JAXBContext.newInstance( TRIM_Package );
	    }
        return jc;
	}
	/**
	 * This is a debugging method that makes an extra trim to the server to get the source, unaltered, trim.
	 * @throws NamingException 
	 */
	public void displayRawTrim(  ) throws NamingException {
		TrimHeader trimHeader = getTrimBean().findTrimHeader(getTrimName());
        TolvenLogger.info( "******************************* Raw Trim *******************************", UpdateTrim.class);
        TolvenLogger.info( "Name: " + trimHeader.getName(), UpdateTrim.class);
        TolvenLogger.info( "Date last updated: " + trimHeader.getLastUpdated(), UpdateTrim.class);
        TolvenLogger.info( "Comment: " + trimHeader.getComment(), UpdateTrim.class);
        TolvenLogger.info( "************************************************************************", UpdateTrim.class);
		TolvenLogger.info( new String( trimHeader.getTrim()), UpdateTrim.class);
        TolvenLogger.info( "************************************************************************", UpdateTrim.class);
	}
	
	public void displayInstantiatedTrim( TolvenMessageWithAttachments tm ) {
        String x = new String( tm.getPayload());
        TolvenLogger.info( "************************** Instantiated Trim ***************************", UpdateTrim.class);
        TolvenLogger.info( x, UpdateTrim.class );
        TolvenLogger.info( "************************************************************************", UpdateTrim.class);
	}
	
	/**
	 * Standalone program to update a patient 
	 * @param args userId password accountId patientId
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 5 ) {
			System.out.println( "Arguments: UserId password accountId patient configDirectory");
			return;
		}
        UpdateTrim stm = new UpdateTrim(args[4]);
		stm.setupUser(args[0], args[1] );
		stm.setupAccount(Long.parseLong(args[2]));
		stm.beginTransaction();
		MenuData mdPatient = stm.findMenuData(args[3]);
		Trim trim = stm.getTrimForUpdate(mdPatient, "reg/evn/patient");
		stm.updatePatient( trim );
		TolvenMessageWithAttachments tm = stm.createTolvenMessage( TRIM_NS );
		stm.addTrimAsPayload( trim, tm );
		stm.submitMessage( tm );
		stm.commitTransaction();
		stm.displayInstantiatedTrim(tm );
	}

}
