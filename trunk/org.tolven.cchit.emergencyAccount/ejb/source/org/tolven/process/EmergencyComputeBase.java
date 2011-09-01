package org.tolven.process;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.app.CCHITLocal;
import org.tolven.app.EmergencyAccountLocal;
import org.tolven.core.AccountDAOLocal;

public abstract class EmergencyComputeBase extends ComputeBase{
	private CCHITLocal cchitBean;
	private EmergencyAccountLocal emergencyAccountBean;
	
	
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

	/**
	 * @return
	 */
	protected EmergencyAccountLocal getEmergencyAccountBean() {
		if (emergencyAccountBean == null) {
			try {
				InitialContext ctx = new InitialContext();
				emergencyAccountBean = (EmergencyAccountLocal) ctx.lookup("java:global/tolven/tolvenEJB/EmergencyAccountBean!org.tolven.app.EmergencyAccountLocal");
			} catch (NamingException ex) {
				throw new RuntimeException("Failed to look up java:global/tolven/tolvenEJB/EmergencyAccountBean!org.tolven.app.EmergencyAccountLocal", ex);
			}
		}
		return emergencyAccountBean;
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
