package org.tolven.client.example;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.core.ActivationRemote;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.DocumentRemote;
import org.tolven.logging.TolvenLogger;

public class Test3 {
	public static final String uid = "bob";
	public static final String password = "bob";
	public static final long accountId = 11602;

	/**
	 * @param args
	 * @throws NamingException 
	 */
	public static void main(String[] args) throws NamingException {
		TolvenLogger.initialize();
		Properties prop = new Properties(); 
		prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory"); 
		prop.put(Context.PROVIDER_URL, "jnp://localhost:1099"); 
		prop.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces"); 
		prop.put("java.naming.security.principal", uid); 
		prop.put("java.naming.security.credentials", password); 
		Context ctx = new InitialContext(prop);
		DocumentRemote docBean = (DocumentRemote) ctx.lookup("tolven/DocumentBean/remote");
		ActivationRemote activationBean = (ActivationRemote) ctx.lookup("tolven/ActivationBean/remote");
		long docCount = docBean.countDocuments(accountId);
		TolvenLogger.info("Account " + accountId + " has " + docCount + " documents", Test3.class);
		TolvenLogger.info("Attempting to get TolvenUser: " + uid, Test3.class);
		TolvenUser user = activationBean.findUser( uid );
		TolvenLogger.info("User " + user.getId() + " Last login: " + user.getLastLogin(), Test3.class);
	}

}
