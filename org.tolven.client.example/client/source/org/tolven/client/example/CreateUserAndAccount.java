package org.tolven.client.example;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Date;

import javax.naming.NamingException;

import org.tolven.client.TolvenClient;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.TolvenPerson;

/**
 * This example has limited real use but provides a good example of creating a 
 * user, an account, and adding the new user to the account. 
 * The program will run as a standalone client. However, since it has to login as the
 * new user, it may not work if called locally. 
 * If run under Java 1.6, be sure to include the jvm arge:
 *  -Dsun.lang.ClassLoader.allowArraySyntax=true
 * This remote client program depends on the tolvenRemoveClient.jar (located in tolvenEJB/build)
 * @author John Churin
 *
 */
public class CreateUserAndAccount extends TolvenClient {

    public CreateUserAndAccount(String configDir) throws IOException {
        super(configDir);
        TolvenLogger.initialize();
    }
    
    /**
     * Create a new user in LDAP and in Tolven schema. 
     * @param username - The unique username which will automatically be converted to lowercase.
     * @param password - Password strength is controlled by the underlying directory server.
     * @param firstName
     * @param lastName
     * @return The newly created user object.
     * @throws Exception - An error is thrown if the user already exists.
     */
    public TolvenUser createUser(String username, String password, String firstName, String lastName) throws Exception {
        TolvenPerson tp = new TolvenPerson();
        tp.setUid(username.toLowerCase());
        tp.setSn(lastName);
        tp.setCn(firstName + " " + lastName);
        tp.setGivenName(firstName);
        tp.setUserPassword(password);
        TolvenUser tolvenUser = getLoginBean().registerAndActivate(tp, new Date());
        return tolvenUser;
    }
    /**
     * Create a new account. Also, create the initial application metadata.
     * @param accountType
     * @return Account object.
     * @throws NamingException 
     */
    public Account createAccount(String accountType, String accountTitle) throws NamingException {
        Account account = getAccountBean().createAccount(accountType, accountTitle, null);
        getMenuBean().updateMenuStructure(account);
        return account;
    }
    
    /**
     * Associate a user with the specified account. This function must be 
     * called while the user is logged in.
     * @param user
     * @param account
     * @return The AccountUser object.
     * @throws NamingException 
     */
    public AccountUser addUserToAccount( TolvenUser user, Account account, PublicKey userPublicKey ) throws NamingException {
        return getAccountBean().addAccountUser(account, user, new Date(), true, userPublicKey);
    }

    /**
     * Pass in the user name and password. The AccountId will be echoed on standard out.
     * @param args [0] = username, [1] = [password]
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        String username = (args.length > 0) ? args[0] : null;   // eg "sam.snap"
        String password = args[1];  // eg "sam"
        String firstName = args[2];
        String lastName = args[3];
        String accountType = args[4]; // eg "ephr"
        String accountTitle = args[5]; // eg "Snap family"
        String configDir = args[6]; // location of the tolven-config directory eg "c:\tolven-config"
        CreateUserAndAccount cu = new CreateUserAndAccount(configDir);
        // Create a Tolven user (The TolvenUser object 
        // is returned but we'll ignore it for the moment) 
        cu.createUser( username, password, firstName, lastName );
        // Login the new user. This is needed in order to encrypt and store the account private key
        // when we link the user to the account (AccountUser).
        TolvenUser user = cu.login( username, password);
        // The rest should be done within a transaction
        cu.beginTransaction();
        // Create the account
        // This call also initializes the application metadata for the new account.
        Account account = cu.createAccount(accountType, accountTitle);
        // Associate user to account and make this user an administrator of the account.
        AccountUser accountUser = cu.addUserToAccount( user, account, null );
        // All or nothing (however, note that the createUser step, in this example,
        // is outside of the transaction.
        cu.commitTransaction();
        TolvenLogger.info( "TolvenUser id: "+ accountUser.getUser().getId(), CreateUserAndAccount.class);
        TolvenLogger.info( "Account id: "+ accountUser.getAccount().getId(), CreateUserAndAccount.class);
        TolvenLogger.info( "AccountUser id: "+ accountUser.getId(), CreateUserAndAccount.class);
    }

}
