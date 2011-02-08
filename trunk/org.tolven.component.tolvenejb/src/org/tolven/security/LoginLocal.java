package org.tolven.security;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Date;

import org.tolven.core.bean.InvitationException;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.TolvenUser;
/**
 * Services that provide LDAP-related user management, including registration.
 * @author John Churin
 *
 */
public interface LoginLocal {

    /**
     * Given the principal's name, get the TolvenUser.
     */
     public TolvenUser findUser( String principal );

     /**
      * Return an AccountUser given an AccountUserId
      * @param anAccountUserId
      * @return
      */
     public AccountUser findAccountUser(long accountUserId);
     
     /**
      * Used for test, demo only. Register and immediately activate the user without sending an email. The user id does not need to be a valid email address.
      * The demoUser flag is set in the user account.
      * @param tp A TolvenPerson object representing the LDAP attributes of this user (A TolvenPerson is a transient object)
      * @param now A transactional now timestamp
      * @return A new TolvenUser object
      */
     @Deprecated
     public TolvenUser registerAndActivate(  TolvenPerson tp, Date now ) throws Exception;
     
     public TolvenUser activate(TolvenPerson tp, Date now);

     /**
      * Register a new user with an activation step that validates the userId as a valid eMail addresss.
      * <ol>
      * <li>Persist the new TolvenUser object</li>
      * <li>Create an LDAP entry</li>
      * <li>Create a TolvenUser Object</li>
      * <li>Create an invitation</li>
      * <li>Create an email referencing the invitation</li>
      * </ol>
      */
     public void register(  TolvenPerson tp, Date now ) throws Exception;
     public void register(  TolvenPerson tp, Date now, String brand ) throws Exception;
     
     /**
      * Activate a user. At this point, a user will have an entry in LDAP, they will have received an invitation
      * via email and now this method will create their Tolven user account.
      */
     public boolean activate( String principal, long invitationId, Date now, PublicKey userPublicKey) throws InvitationException;
     
     /**
      * Create a TolvenUser only, because the associated LDAP user is created independently
      * @param principal
      * @param now
      * @return
      */
     public TolvenUser createTolvenUser( String principal, Date now );

     /**
      * Persist a modified TolvenUser
      * @return
      */
     public void persistModifiedTolvenUser(TolvenUser user);
    
     /**
      * Reserve a new userId in LDAP. This user will not be able to login until activated with {@link #activateReservedUser(String, Date)}
      * This method is an alternate to {@link #register(TolvenPerson, Date)} which creates an invitation
      * for full self-service registration.
      * @param tp A TolvenPerson object containing attributes used to populate LDAP
      * @param now The current time
      * @throws GeneralSecurityException
      */
     public void reserveUser(TolvenPerson tp, Date now) throws GeneralSecurityException;
     
     /**
      * Activate a reserved user. The user must first be
      * reserved using {@link #reserveUser(TolvenPerson, Date)}. The effect of calling this method
      * is to create a new TolvenUSer entity in the database.
      * <p>Note: This function requires that the logged in user have the tolvenAdmin role.
      * In other words, the logged-in user cannot self-activate using this method.</p>
 	 * <p>This process does not require an invitation.</p>
      * @param principal User's ID.
      * @param now The current time
      * @return The newly create (or updated) TolvenUser entity
      * @throws GeneralSecurityException
      */
     public TolvenUser activateReservedUser( String principal, Date now) throws GeneralSecurityException;
     
     /**
      * Cancel Reservation for a new user. The user must first be
      * reserved using {@link #reserveUser(TolvenPerson, Date)}. The effect of calling this method
      * is to remove this user from LDAP. The method fails if the user has already been activated (added to
      * the database). In other words, the user must be in LDAP and not in TolvenUser.
      * Note: This function requires that the logged in user have the tolvenAdmin role.
      * This method should be use to cancel an invitation-based registrations.
      * @param principal User's ID.
      * @param now The current time
      * @throws GeneralSecurityException
      */
     public void cancelReservation( String principal, Date now) throws GeneralSecurityException;
     
}
