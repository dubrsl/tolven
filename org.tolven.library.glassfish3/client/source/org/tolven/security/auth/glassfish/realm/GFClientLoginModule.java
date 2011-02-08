package org.tolven.security.auth.glassfish.realm;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.glassfish.security.common.PrincipalImpl;

import com.sun.enterprise.security.auth.login.common.PasswordCredential;
import com.sun.enterprise.security.common.ClientSecurityContext;

public class GFClientLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;
    private String principalName;
    private char[] password;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        try {
            if (callbackHandler == null) {
                throw new LoginException("No CallbackHandler");
            }
            NameCallback nc = new NameCallback("User name: ");
            PasswordCallback pc = new PasswordCallback("Password Alias: ", false);
            Callback[] callback = { nc, pc };
            callbackHandler.handle(callback);
            principalName = nc.getName();
            password = pc.getPassword();
            /*
             * Safety Check
             */
            if (principalName == null) {
                throw new LoginException("null principalName not permitted");
            }
            if (password == null) {
                throw new LoginException("null password not permitted");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LoginException le = new LoginException("Authentication failed for '" + principalName + "': " + ex.getMessage());
            throw le;
        }
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        PrincipalImpl userPrincipal = new PrincipalImpl(principalName);
        if (!subject.getPrincipals().contains(userPrincipal)) {
            subject.getPrincipals().add(userPrincipal);
        }
        PasswordCredential pc = new PasswordCredential(principalName, new String(password), "tolvenLDAP");
        //PasswordCredential pc = new PasswordCredential(principalName, password, "tolvenLDAP");
        if (!subject.getPrivateCredentials().contains(pc)) {
            subject.getPrivateCredentials().add(pc);
        }
        ClientSecurityContext securityContext = new ClientSecurityContext(principalName, subject);
        ClientSecurityContext.setCurrent(securityContext);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        removeAllCredentials();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        removeAllCredentials();
        return true;
    }

    private void removeAllCredentials() {
        principalName = null;
        password = null;
    }
}
