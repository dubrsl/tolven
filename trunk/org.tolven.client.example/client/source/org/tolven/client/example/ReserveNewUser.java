package org.tolven.client.example;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.tolven.client.TolvenClient;
import org.tolven.core.entity.TolvenUser;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.TolvenPerson;

/**
 * An example of how to reserve a new user.
 * If called from the command line, this example is not secure.
 * The user will not normally be logged in. 
 * This remote client program depends on the tolvenRemoteClient.jar (located in tolvenEJB/build)
 * @author John Churin
 *
 */
public class ReserveNewUser extends TolvenClient {
	private Logger logger = Logger.getLogger(this.getClass());
	
	public ReserveNewUser(String configDir) throws IOException {
		super(configDir);
	}
	
	/**
	 * Method to change the logged-in user's password
	 * @param password
	 * @param newPassword
	 * @throws GeneralSecurityException
	 * @throws NamingException
	 */
	public void reserveNewUser( TolvenPerson tp ) throws GeneralSecurityException, NamingException {
        this.getLoginBean().reserveUser(tp, new Date());
	}
	
	public void activateReservedUser( String principal ) throws GeneralSecurityException, NamingException {
        TolvenUser tolvenUser = this.getLoginBean().activateReservedUser(principal, new Date());
        logger.info("New TolvenUser: " + tolvenUser);
	}
	
	/**
	 * Reserver a new user, then activate that new user.
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		TolvenLogger.initialize();
		TolvenPerson tp = new TolvenPerson();
		tp.setUid(args[0]);				// eg "sam.snap"
		tp.setUserPassword(args[1]);	// eg "secret"
		tp.setGivenName(args[2]);		// eg "sam"
		tp.setSn(args[3]);				// Sirname (LastName) eg "snap"
		List<String> email = new ArrayList<String>();
		email.add(args[4]);				// "my.name@sample.com"
		tp.setMail(email);
		// *** Very Important -- LDAP Common Name and a few other attributes must be set in order to create an X.509 certificate. ***
		tp.setCn( tp.getGivenName() + " " + tp.getSn());
		tp.setOrganizationUnitName("No Organization Unit");
		tp.setOrganizationName("No Organization");
		tp.setStateOrProvince("No StateOrProvince");
		tp.setCountryName("No CountryName");
        String configDir = args[5]; // location of the tolven-config directory eg "c:\tolven-config"
        ReserveNewUser cp = new ReserveNewUser(configDir);
		// Reserve the user
        cp.reserveNewUser(tp);
		// At this point, the user cannot login
        
        // So we're now pretend to be the admin who accepts (activates) this user
		cp.login( "admin", "sysadmin");
		// Start a transaction
		cp.beginTransaction();
		cp.activateReservedUser( args[0] );
        // Commit
		cp.commitTransaction();
	}

}
