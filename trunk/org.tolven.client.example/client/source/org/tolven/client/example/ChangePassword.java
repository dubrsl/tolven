package org.tolven.client.example;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.naming.NamingException;

import org.tolven.client.TolvenClient;

/**
 * An example of how to change a user password from a remote client.
 * If called from the command line, it is not secure.
 * Note that the user will be logged in as part of the password change. Therefore, the user will have been authenticated
 * before a password change occurs. In any case, the user's current password must be supplied in order to change the password.
 * This remote client program depends on the tolvenRemoteClient.jar (located in tolvenEJB/build)
 * @author John Churin
 *
 */
public class ChangePassword extends TolvenClient {

	public ChangePassword(String configDir) throws IOException {
		super(configDir);
	}
	
	/**
	 * Method to change the logged-in user's password
	 * @param password
	 * @param newPassword
	 * @throws GeneralSecurityException
	 * @throws NamingException
	 */
	public void changeUserPassword( char[] password, char[] newPassword) throws GeneralSecurityException, NamingException {
        this.getLoginBean().changeUserPassword( password, newPassword);
	}
	
	/**
	 * Change user password. Pass in the user name and currentPassword and new password.
	 * @param args [0] = username, [1] = oldPassword, [2] newPassword, [3] tolven-config directory
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String username = args[0];	// eg "sam.snap"
		String password = args[1];	// eg "sam"
		String newPassword = args[2]; // eg sammy
        String configDir = args[3]; // location of the tolven-config directory eg "c:\tolven-config"
        ChangePassword cp = new ChangePassword(configDir);
		// Login the user who's password will change. 
		cp.login( username, password);
		// The rest should be done within a transaction
		cp.beginTransaction();
		// Now change the password
        cp.changeUserPassword(password.toCharArray(), newPassword.toCharArray());
        // Commit
		cp.commitTransaction();
	}

}
