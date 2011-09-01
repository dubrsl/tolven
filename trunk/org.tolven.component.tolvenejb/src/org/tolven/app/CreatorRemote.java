package org.tolven.app;

import org.tolven.core.entity.AccountUser;

/**
 * APIs in this module are used to create new instances of TRIM placeholders.
 * @author John Churin
 *
 */
public interface CreatorRemote {

   /**
	* Submit the specified document for processing (remote-friendly).
	*/
	public void submit( long documentId, AccountUser activeAccountUser) throws Exception;

}
