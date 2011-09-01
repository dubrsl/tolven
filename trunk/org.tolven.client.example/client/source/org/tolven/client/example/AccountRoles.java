package org.tolven.client.example;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.tolven.client.TolvenClient;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountRole;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.TolvenPerson;

/**
 * An example of how to get/set the Roles allowed in an account.
 * If called from the command line, this example is not secure.
 * The user will be logged in and an account selected. 
 * This remote client program depends on the tolvenRemoteClient.jar (located in tolvenEJB/build)
 * @author John Churin
 *
 */
public class AccountRoles extends TolvenClient {
	private Logger logger = Logger.getLogger(this.getClass());
	private Account account;
	private AccountUser accountUser;
	public AccountRoles(String configDir) throws IOException {
		super(configDir);
	}
	/**
	 * Setup the account we're interested in
	 * @param accountId
	 * @throws NamingException
	 */
	public void selectAccount( Long accountId  ) throws NamingException {
		accountUser = loginToAccount(accountId);
		account = getAccountBean().findAccount(accountId);
	}
	
	public void displayAccountRoles() throws NamingException {
		Map<String, String> roles = getAccountBean().findAccountRoles(account.getId());
		logger.info("Account Roles for account: " + account.getId());
		for (String accountRole : roles.keySet()) {
			logger.info(" Role: " + accountRole + " (" + roles.get(accountRole) + ")");
		}
	}
	public void displayAccountUserRoles() throws NamingException {
		logger.info("Account User Roles for accountUser " + accountUser.getId());
		Set<String> accountUserRoles = getAccountBean().findAccountUserRoles(accountUser.getId());
		for (String role : accountUserRoles) {
			logger.info(" Role: " + role );
		}
	}
	
	public void addAccountRole(String role, String title) throws NamingException {
		Map<String, String> roles = getAccountBean().findAccountRoles(account.getId());
		roles.put(role, title);
		getAccountBean().updateAccountRoles(account.getId(), roles);
	}
	
	public void addAccountUserRole(String role) throws NamingException {
		Set<String> roles = getAccountBean().findAccountUserRoles(accountUser.getId());
		roles.add(role);
		getAccountBean().updateAccountUserRoles(accountUser.getId(), roles);
	}
	
	/**
	 * Login with username[0] and password[1]
	 * Select Account[2]
	 * 
	 * List roles for that account
	 * 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		TolvenLogger.initialize();
        String configDir = args[3]; // location of the tolven-config directory eg "c:\tolven-config"
		AccountRoles ar = new AccountRoles(configDir);
		ar.login(args[0], args[1]);
		ar.selectAccount(Long.parseLong(args[2])); 
		ar.displayAccountRoles();
		ar.displayAccountUserRoles();
		ar.addAccountRole("anewone", "A new one-");
		ar.addAccountUserRole("anewone");
	}

}
