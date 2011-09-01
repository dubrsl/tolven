package org.tolven.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.tolven.app.CreatorRemote;
import org.tolven.app.MenuRemote;
import org.tolven.app.TrimRemote;
import org.tolven.client.TolvenClient;
import org.tolven.core.AccountDAORemote;
import org.tolven.core.ActivationRemote;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.DocumentRemote;
import org.tolven.doc.RulesRemote;
import org.tolven.gen.GeneratorRemote;
import org.tolven.queue.JMSRemote;
import org.tolven.security.LoginRemote;



public  class TolvenClientEx extends TolvenClient {
	
	//private Properties contextProperties;
	private InitialContext ctx;
	
	public void setContextProperties() {
        this.setContextProperties(new Properties());       
        this.getContextProperties().put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory"); 
        this.getContextProperties().put(Context.PROVIDER_URL, "jnp://localhost:1099"); 
        this.getContextProperties().put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");

    }
	
	public InitialContext getCtx() {
        if (ctx == null) {
            if (getContextProperties() == null) {
            	//setContextProperties(loadProperties(new File(DEFAULT_PROPERTIES)));
                setContextProperties();
                if (getContextProperties() == null) {
                	throw new RuntimeException(getClass() + " does not have initial context properties");
                }
            }
            try {
            	ctx = new InitialContext(getContextProperties());
            } catch (NamingException ex) {
                throw new RuntimeException("Could not create initial context using supplied context properties", ex);
            }
        }
        return ctx;
    }
}