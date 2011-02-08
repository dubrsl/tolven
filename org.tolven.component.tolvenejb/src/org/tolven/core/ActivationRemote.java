package org.tolven.core;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;

/**
 * Remote services used to create, activate, and query Tolven users.
 */
public interface ActivationRemote {

    /**
     * Given the principal's name, get the TolvenUser.
     */
     public TolvenUser findUser( String principal );
     
     /**
      * Find the user object and mark the last update date in the user object. Technically, 
      * this is really a post-login step since the container has already authenticated the user.
      */
     public TolvenUser loginUser( String principal, Date now) throws GeneralSecurityException, NamingException;

     /**
      * Given the TolvenUser, find a list of all Accounts that that TolvenUser is associated with.
      */
      public List<AccountUser> findUserAccounts( TolvenUser user );

}
