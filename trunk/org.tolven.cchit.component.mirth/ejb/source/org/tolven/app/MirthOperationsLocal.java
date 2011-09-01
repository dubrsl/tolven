/**
 * 
 */
package org.tolven.app;

import java.security.PrivateKey;
import org.tolven.trim.ex.TRIMException;
import org.tolven.trim.ex.TrimEx;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
import org.tolven.mirth.Response;

/**
 * Methods in this interface are used for Mirth related operations.
 * @author syed
 * added on 6/3/2010
 */

public interface MirthOperationsLocal {

	/**
	 * This method sends the HL7 trim message to Mirth
	 * @param trim
	 * @author Syed
	 * added on 6/4/2010
	 * @throws Exception 
	 */
	public void sendMessage(TrimEx trim) throws Exception;	
	
	/**
	 * Gets the user account specified in the response message 
	 * and updates the lab orders.
	 * @author Syed
	 * @param user - TolvenUser
	 * @param resp - the response from Mirth
	 * added on 6/24/2010
	 */
	public void updateDocument(TolvenUser user, Response response,PrivateKey userPrivateKey);
	
}
