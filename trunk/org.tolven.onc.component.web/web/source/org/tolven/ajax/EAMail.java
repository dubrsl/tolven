package org.tolven.ajax;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.tolven.app.CCHITLocal;
import org.tolven.core.entity.TolvenUser;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.TolvenPerson;
import org.tolven.security.bean.LDAPBean;

public final class EAMail {

	private CCHITLocal cchitBean;
	private static Properties ldapProperties;
	private LdapContext ctx = null;
	private static final List<String> emptyList = Collections.emptyList();
	
	private static final String ROLE_ATTRIBUTE_ID_PROPERTY = "roleAttributeID";
    private static final String PRINCIPAL_DN_PREFIX_PROPERTY = "principalDNPrefix";
    private static final String PRINCIPAL_DN_SUFFIX_PROPERTY = "principalDNSuffix";
    private static final String ROLES_CTX_DN_PROPERTY = "rolesCtxDN";
	
    /**
     * Initialize ldapProperties
     */
    static {
        String propertyFileName = LDAPBean.class.getSimpleName() + ".properties";
        try {
            ldapProperties = new Properties();
            InputStream in = LDAPBean.class.getResourceAsStream(propertyFileName);
            if (in != null) {
                ldapProperties.load(in);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not load ldap properties from: " + propertyFileName, ex);
        }
        if (ldapProperties.getProperty(ROLE_ATTRIBUTE_ID_PROPERTY) == null) {
            throw new RuntimeException("Could not find property: " + ROLE_ATTRIBUTE_ID_PROPERTY + " in " + propertyFileName);
        }
        if (ldapProperties.getProperty(ROLES_CTX_DN_PROPERTY) == null) {
            throw new RuntimeException("Could not find property: " + ROLES_CTX_DN_PROPERTY + " in " + propertyFileName);
        }
        if (ldapProperties.getProperty(PRINCIPAL_DN_PREFIX_PROPERTY) == null) {
            throw new RuntimeException("Could not find property: " + PRINCIPAL_DN_PREFIX_PROPERTY + " in " + propertyFileName);
        }
        if (ldapProperties.getProperty(PRINCIPAL_DN_SUFFIX_PROPERTY) == null) {
            throw new RuntimeException("Could not find property: " + PRINCIPAL_DN_SUFFIX_PROPERTY + " in " + propertyFileName);
        }
    }
    
    /**
     * Public method to get email id of the TolvenUser with the given account id
     * @param accountID
     * @return
     */
    public String getEaEmail(String accountID) {
		String email = "emailId=";
		List<String> emails = retrieveEmail(accountID);
		if(emails.isEmpty()) {
			TolvenLogger.info("ERROR:--------> No emails in list from retrieveEmail method",EAMail.class);
			return email;
		}
		email += emails.get(0);
		return email;
	}
    
    /**
     * Private method to retrieve emails. The account may have more than  one email stored but we use only the first email id.
     * @param ID
     * @return
     */
    private List<String> retrieveEmail(String ID) {
    	List<TolvenUser> list = getCCHITBean().getTolvenUser(ID);
		String ldapUid = null;
		if(list.isEmpty()) {
			TolvenLogger.info("ERROR:--------> No TolvenUsers in list from CCHITBean method",EAMail.class);
			return emptyList;
		}
		ldapUid = list.get(0).getLdapUID();
		String uidPath = getUIDField() + ldapUid;
		List<TolvenPerson> tolvenPersons = search(uidPath, 1, 1000);
		if(tolvenPersons.isEmpty()) {
			TolvenLogger.info("ERROR:--------> No TolvenPerson in list from search method",EAMail.class);
			return emptyList;
		}
		return tolvenPersons.get(0).getMail();
    }
    
    /**
     * Search for matching names. If not connected yet, we'll connect to LDAP now.
     */
    private List<TolvenPerson> search(String criteria, int maxResults, int timeLimit) {
        try {
			connectLDAP();
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
        NamingEnumeration<SearchResult> namingEnum = null;
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setCountLimit(maxResults);
        ctls.setTimeLimit(timeLimit);
        ArrayList<TolvenPerson> searchResults = new ArrayList<TolvenPerson>(10);
        try {
            namingEnum = ctx.search(getBaseDN(), criteria, ctls);
            while (namingEnum.hasMore()) {
                SearchResult rslt = namingEnum.next();
                searchResults.add(new TolvenPerson(rslt));
            }
        } catch (NamingException e) {
            TolvenLogger.info(e.getMessage(), EAMail.class);
            RuntimeException runtimException = new RuntimeException(e.getMessage());
            runtimException.setStackTrace(e.getStackTrace());
            throw runtimException;
        }
        try {
			closeLDAP();
		} catch (NamingException e) {
			e.printStackTrace();
		}
        return searchResults;

    }
 
    /**
     * CRFBean lookup
     * @return CRFLocal
     */
    private CCHITLocal getCCHITBean() {
		if(cchitBean == null) {
			try {
				InitialContext ctx  = new InitialContext();
				cchitBean  = (CCHITLocal ) ctx.lookup("java:global/tolven/tolvenEJB/CCHITBean!!org.tolven.app.CCHITLocal");				
			} catch (NamingException e) {
				throw new RuntimeException("java:global/tolven/tolvenEJB/CCHITBean!!org.tolven.app.CCHITLocal", e);
			}
		}
		return cchitBean;
	}
    
    private String getUIDField() {
        return ldapProperties.getProperty(PRINCIPAL_DN_PREFIX_PROPERTY) + "=";
    }
	
	private String getBaseDN() {
        return ldapProperties.getProperty(PRINCIPAL_DN_SUFFIX_PROPERTY);
    }
    /**
     * Connect to LDAP server
     * @exception NamingException
     */
    void connectLDAP( ) throws NamingException {
        if (ctx!=null) return;
        InitialContext iniCtx = new InitialContext();
        ctx = (LdapContext)iniCtx.lookup("tolven/ldap");
    }

    /**
     * Close the connection (if open)
     * TODO: LDAP is a resource that could be pooled - look into it.
     */
     void closeLDAP( ) throws NamingException {
         if (ctx!=null) {
             ctx.close();
             ctx = null;
         }
     }
	
}
