package org.tolven.process;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.app.CCHITLocal;
import org.tolven.core.AccountDAOLocal;

public abstract class CCHITComputeBase extends ComputeBase{
	private CCHITLocal cchitBean;	
	
	/**
	 * @return
	 */
	protected CCHITLocal getCCHITBean() {
		if (cchitBean == null) {
			try {
				InitialContext ctx = new InitialContext();
				cchitBean = (CCHITLocal) ctx.lookup("java:global/tolven/tolvenEJB/CCHITBean!org.tolven.app.CCHITLocal");
			} catch (NamingException ex) {
				throw new RuntimeException("Failed to look up java:global/tolven/tolvenEJB/CCHITBean!org.tolven.app.CCHITLocal", ex);
			}
		}
		return cchitBean;
	}

	protected static AccountDAOLocal getAccountBean() {
        try {
            InitialContext ctx = new InitialContext();
            return (AccountDAOLocal) ctx.lookup("java:global/tolven/tolvenEJB/AccountDAOBean!org.tolven.core.AccountDAOLocal");
        } catch (Exception ex) {
            throw new RuntimeException("Could not lookup java:global/tolven/tolvenEJB/AccountDAOBean!org.tolven.core.AccountDAOLocal", ex);
        }
    }

}
