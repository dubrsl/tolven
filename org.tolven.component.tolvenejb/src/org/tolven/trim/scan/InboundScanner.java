package org.tolven.trim.scan;

import java.util.HashMap;
import java.util.Map;

import org.tolven.app.CreatorLocal;
import org.tolven.app.MenuLocal;
import org.tolven.app.el.ELFunctions;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.PlaceholderID;
import org.tolven.logging.TolvenLogger;
import org.tolven.trim.II;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.SETIISlotEx;
import org.tolven.trim.ex.TrimEx;
/**
 * This "Inbound" scanner will populate placeholders according to bind instructions. 
 * <ol>
 * <li>If the trim does not reference a local placeholder, then create or find a placeholder for the trim now.</li>
 * <li>finding a placeholder at this point may involve using the placeholderID cross-reference which gives us a mapping between an external id
 * and an internal id.</li>
 * <li>If the trim does not reference an Event, then create an event that references the placeholder.</li>
 * <li>The placeholder and event should now contain the Id of the document</li>
 * <li>The placeholder document id must be updated with that of the event.</li>
 * <li>Change the status of both the placeholder and event to active.</li>
 * <li>If the trim references a previous event, then change the status of that (old) event to obsolete.</li>
 * <li>If a placeholder is created, then it is also populated from the trim document, according to the metaData for that placeholder.</li>
 * <li>In any case, now is the time to apply changes from the event (document) to the placeholder.</li>
 * </ol>
 * 
 * @author John Churin
 *
 */
public class InboundScanner extends BindScanner {
	private MenuLocal menuBean;
	private CreatorLocal creatorBean;
	private long documentId;
    private MenuData mdEvent;

	public MenuLocal getMenuBean() {
		return menuBean;
	}

	public void setMenuBean(MenuLocal menuBean) {
		this.menuBean = menuBean;
	}
	
	public MenuData getInstanceEvent() {
		return mdEvent;
	}
	
	@Override
	protected void postProcessTrim(Trim trim) {
		super.postProcessTrim(trim);
		// Create an event
		Map<String, Object> variables = new HashMap<String, Object>(10);
		variables.put("trim", trim);
		mdEvent = creatorBean.establishEvent( getAccount(), (TrimEx) trim, getNow(), variables);
		mdEvent.setDocumentId(documentId);
		// Make sure this item shows up on the activity list
		creatorBean.addToWIP(mdEvent, (TrimEx) trim, getNow(), variables );

	}

	/**
	 * Populate MenuData from trim when we find an element with placeholder bind information.
	 * @return The created or found placeholder, or null if none is applicable
	 */
	public MenuData bindToPlaceholder( BindRequest request ) {
		TolvenLogger.info( "Inbound scan of placeholder at: " + request.location, InboundScanner.class);
		MenuData md = null;
		// Get the id slot setup for our work
		SETIISlotEx slot = computeSlot( request.rimObject );
		// This is the root of the ID we're looking for
		String idRoot = ELFunctions.computeIDRoot( getAccount() );
		MenuData mdPlaceholder = null;
		{
			// Look for an existing placeholder for this account
			II ii = slot.findII( idRoot );
			if ( ii!=null && ii.getExtension()!=null) {
				mdPlaceholder = menuBean.findMenuDataItem(getAccount().getId(), ii.getExtension());
			}
		}
		// If not found, see if we already know of a placeholder by external reference
    	if ( mdPlaceholder == null ) {
        	for( II ii : slot.getIIS()) {
            	PlaceholderID phid = menuBean.findPlaceholderID( getAccount(), ii.getRoot(), ii.getExtension() );
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
        			slot.addUniqueII(newII);
        		}
        	}
    	}
		return md;
	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public CreatorLocal getCreatorBean() {
		return creatorBean;
	}

	public void setCreatorBean(CreatorLocal creatorBean) {
		this.creatorBean = creatorBean;
	}
	

	
}
