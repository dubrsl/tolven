package org.tolven.app.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.app.MenuLocal;
import org.tolven.app.ShareLocal;
import org.tolven.app.ShareRemote;
import org.tolven.app.el.ELFunctions;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.PlaceholderID;
import org.tolven.core.entity.Account;
import org.tolven.provider.ProviderLocal;
import org.tolven.trim.Act;
import org.tolven.trim.ActParticipation;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.Entity;
import org.tolven.trim.II;
import org.tolven.trim.Role;
import org.tolven.trim.SETIISlot;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.SETIISlotEx;
import org.tolven.trim.ex.TrimFactory;
/**
 * <p>Provide functions related to sharing.</p>
 * <p>When a trim is assembled for the the purpose of sharing, it will have one or more Acts, Roles, and Entities that
 * have Ids. Ids can be categories as follows:</p>
 * <ul>
 * <li>An internal id is one that the sender or receiver of a share has a placeholder for.</li>
 * <li>An external id is one that the sender or receiver has no authoritative knowledge of. External ids
 * fall into two loose sub-categories. Some ids are external to one account but internal to some other Tolven account.
 * Other ids are completely unknown to any Tolven account. An account cannot usually tell the difference between these two sub-types
 * however, it doesn't actually matter.</li>
 * </ul>
 * <p>When an exchange occurs, the sender should identify each object that it sends. This will allow the sender to update
 * the objects in the future. HL7 provides a mechanism to globally identify such items. However, in Tolven, each account actually 
 * defines its own "reality" and thus the unique object will have a second identifier that is local to the other account.</p>
 * <p>To make this even more complex, each of two accounts may have independently identified the same object (patient is a typical example)
 * each with its own unique id without either account being aware of the fact. That is until a share occurs. In this case,
 * an account receiving an object has to make a decision: Is the inbound object the same as a known object or is it in fact a new
 * object to the receiving account.</p>
 * <p>Regardless of the outcome of the decisions above, once a connection is made between an inbound object and an internal object,
 * it is reasonable to keep a permanent cross-reference between the two so that the next time an inbound object matches an internally-defined object,
 * there is no need to ask the question again.</p>
 * <table cellspacing="10px">
 * <tr><td>Line#</td><td>Source<br/>Account</td><td>Local<br/>Account</td><td>Local<br/>Placeholder</td><td>Root</td><td>Extension</td></tr>
 * <tr><td>1</td><td>-</td><td>100</td><td>patient-123</td><td>ssan</td><td>123-456-7890</td></tr>
 * <tr><td>2</td><td>-</td><td>100</td><td>patient-123</td><td>...100</td><td>patient-123</td></tr>
 * <tr><td>3</td><td>100</td><td>200</td><td>patient-456</td><td>...100</td><td>patient-123</td></tr>
 * <tr><td>4</td><td>100?</td><td>200</td><td>patient-456</td><td>ssan</td><td>123-456-7890</td></tr>
 * <tr><td>5</td><td>200</td><td>100</td><td>patient-123</td><td>...200</td><td>patient-456</td></tr>
 * <tr><td>6</td><td>-</td><td>100</td><td>patient-123</td><td>...100.pseudo</td><td>xxx-123-xxx-456</td></tr>
 * <tr><td>7</td><td>100</td><td>200</td><td>patient-456</td><td>...100.pseudo</td><td>xxx-123-xxx-456</td></tr>
 * <tr><td>8</td><td>-</td><td>100</td><td>patient-123:allergy-789</td><td>...100</td><td>patient-123:allergy-789</td></tr>
 * <tr><td>9</td><td>100</td><td>200</td><td>patient-456:allergy-010</td><td>...100</td><td>patient-123:allergy-789</td></tr>
 * </table>
 * <p>In the table above, Line #1 might be present for a patient in account 100. This Social security number is an example of a benign id which would 
 * not assigned by any Tolven account. This type of placeholderID has nothing to do with sharing so it is not put there by this method.</p>
 * <p>Line #2 would be added for account 100 indicating that, without being specific, patient-123 has or will shortly be shared with someone outside of this account.</p>
 * <p>Upon receipt of an inbound message, Line #3 is added to account 200 indicating that patient-123 from account 100 is the same patient as patient-456 in account 200. 
 * This determination is made by some human interaction or perhaps the actions of an MPI which does automatic or
 * semi-automatic matching based on demographics.</p>
 * <p>Line #4 is optional but generally, if the sender wants to send a benign id like an SSAN, then the receiver is likely to want to
 * save it.</p>
 * <p>Line #5 is optional. This entry provides an explicit cross-reference from the receiver's internal ID back to the sender's internal ID. 
 * However, it isn't actually needed because the sender is going to get back the internal ID for account 100. the advantage is that the
 * next time a message is sent to account 200, the id for 200 (patient-456) will be sent thus eliminating the need for
 * account 200 to figure out which internal id matches the received id.</p>
 * <p>When a pseudo-id is used for privacy reasons, lines #6 and #7 come into play. 
 * In this case, a benign id is sent rather than the real id. Line #6 provides the cross-reference from the pseudo id to the real placeholder in account 100.
 * the actual ID. Line #7 is showing what #3 would look like if the pseudo id were sent.</p>
 * <p>Line #9 is functionally similar to #3 with the difference that it describes a component item, that is, subordinate to a container such as patient.</p>
 * <p>It is important to remember that an outbound TRIM is always immutable so nothing in this process can (effectively) 
 * change the trim itself. Actions therefore are limited to populating the cross-reference 
 * tables in preparation from the item being sent outbound.</p>
 * <p>I DON'T THINK THIS IS NEEDED: Context preprocessing is needed when we process nested components such as allergies for a patient. eg:
 * patient-123:allergy-678 which is an allergy for patient-123. When creating a new inbound placeholder and cross reference based on
 * a supplied id, we don't (usually) burden the user with having to identify the patient for each allergy. Instead, 
 * </p>
 * <p></p>
 * @author John Churin
 */
@Stateless()
@Local(ShareLocal.class)
@Remote(ShareRemote.class)
public class ShareBean implements ShareLocal, ShareRemote {
	@PersistenceContext
	private EntityManager em;
    @EJB ProviderLocal providerBean;
    @EJB MenuLocal menuBean;
	private static final TrimFactory trimFactory = new TrimFactory();

    public enum Direction{ outbound, inbound };
    
    

    /**
     * Local: For each ID that we find that is not assigned by this account, create a local cross reference to it.
     * Outbound: If an id is assigned by this account, create a cross reference to it.
     * Inbound: For each ID not assigned to our account that we find in our cross-reference, add the cross-referenced ID.
     * @param id ID Slot containing one or more instance identifiers
     * @param account
     */
    public void processId( SETIISlot id, Direction direction, Account account ) {
    	// See if we have a placeholder in this account - in other words, do we know about this
    	// placeholder in this account.
    	MenuData mdPlaceholder = null;//findInternalPlaceholder( account, id );
    	// For an outbound message, we need to add placeholders to the placeholder cross-reference.
    	// This only needs to be done once per placeholder, regardless of the number of places
    	// the placeholder is shared.
    	if ( mdPlaceholder != null && direction==Direction.outbound ) {
    		String root = ELFunctions.computeIDRoot(account);
    		mdPlaceholder.addPlaceholderID(root, mdPlaceholder.getPath(), "tolven-xref");
    	}
    	// For inbound messages, look to see if an ID already exists in the placeholder id table. But it may not.
    	// If it does, then add that id to the trim.
    	if ( mdPlaceholder == null && direction==Direction.inbound ) {
        	for( II ii : id.getIIS()) {
            	PlaceholderID phid = menuBean.findPlaceholderID( account, ii.getRoot(), ii.getExtension() );
            	// This is likely a remote id which we've found in our cross-reference. It means we've seen this
            	// object before and determined the cross reference at that time.
            	if (phid!=null) {
            		mdPlaceholder = phid.getPlaceholder();
            		break;
            	}
        	}
        	// The inbound trim should now have all the ids of this placeholder. Copy them in if not already there. 
        	if (mdPlaceholder!=null) {
        		for (PlaceholderID newID : mdPlaceholder.getPlaceholderIDs()) {
        			II newII = trimFactory.createII();
        			newII.setExtension(newID.getExtension());
        			newII.setRoot(newID.getRoot());
        			newII.setAssigningAuthorityName("tolven-xref");
        			// Add it to trim (if unique)
        			((SETIISlotEx)id).addUniqueII(newII);
        		}
        	}
    	}
//    	// If still no placeholder, then we'll put a special II in the trim that tells downstream
//    	// processes to acquire the id.
//    	if ( mdPlaceholder == null && direction==Direction.inbound ) {
//    		II needII = trimFactory.createII();
//    		needII.setRoot( ELFunctions.computeIDRoot(account) );
//    		id.getIIS().add(needII);
//    	}
    }
    
    public void scanAct( Act act, Direction direction, Account account ) {
    	if (act.getId()!=null) {
    		processId( act.getId(), direction, account );
    	}
    	for ( ActParticipation participation : act.getParticipations() ) {
    		if (participation.getRole() != null ) {
    			scanRole(participation.getRole(), direction, account );
    		}
    	}
    	for ( ActRelationship relationship : act.getRelationships() ) {
    		if ( relationship.getAct() != null ) {
        		scanAct( relationship.getAct(), direction, account );
    		}
    	}
    }
    
    public void scanRole( Role role, Direction direction, Account account ) {
    	if (role.getId()!=null) {
    		processId( role.getId(), direction, account );
    	}
    	if ( role.getScoper() != null ) {
    		scanEntity( role.getScoper(), direction, account );
    	}
    	if ( role.getPlayer() != null ) {
    		scanEntity( role.getPlayer(), direction, account );
    	}
    }

    public void scanEntity( Entity entity, Direction direction, Account account ) {
    	if (entity.getId()!=null) {
    		processId( entity.getId(), direction, account );
    	}
    	for ( Role role : entity.getPlayedRoles() ) {
    		scanRole( role, direction, account );
    	}
    	for ( Role role : entity.getScopedRoles() ) {
    		scanRole( role, direction, account );
    	}
    }

    public void scanTrim( Trim trim, Direction direction, Account account ) {
    	if ( trim.getAct() != null ) {
    		scanAct( trim.getAct(), direction, account );
    	}
    }
    
    /**
     * Scan an outbound trim for embedded IDs and create a cross-reference 
     * to that ID if it doesn't already exists.
     * @param trim
     * @param account The account sending the TRIM
     */
    public void outboundScan( Trim trim, Account account ) {
    	scanTrim( trim, Direction.outbound, account );
    }

    /**
     * Scan an inbound trim for embedded IDs and resolve cross-references 
     * @param trim
     * @param account The account receiving the TRIM
     */
    public void inboundScan( Trim trim, Account account ) {
    	scanTrim( trim, Direction.inbound, account );
    }
    
}
