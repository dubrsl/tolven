package org.tolven.web;

import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.tolven.admin.ActivateInvitation;
import org.tolven.doc.entity.Invitation;

public class ActivateAction extends TolvenAction {

	private long invitationId = 0;
	
	private Invitation invitation;
    private ActivateInvitation activateInvitation;

    /**
     * Get the invitation associated with this login (typically an activation but could be a referral or similar invitation).
     * @return
     */
    public long getInvitationId() {
    	if (invitationId==0) setInvitationId(getRequestParameterAsLong( "invitationId"));
    	return invitationId;
    }

    public void setInvitationId(long invitationId) {
    	this.invitationId = invitationId;
    	setSessionAttribute("invitationId", new Long(invitationId));
    }

    /**
     * Return the ActivationInvitation details for this invitation.
     * @return
     * @throws JAXBException
     */
    public ActivateInvitation getInvitationDetail() throws JAXBException, NamingException {
    	if (activateInvitation==null) {
    		Invitation invitation = getInvitation();
    		if (invitation==null) return null;
    		activateInvitation = getInvitationLocal().extractActivationDetails( invitation);
    	}
    	return activateInvitation;
    }

    public Invitation getInvitation() throws NamingException {
    	if (invitation==null) {
    		long invitationId = getInvitationId();
    		if (invitationId==0) return null;
    		invitation = getInvitationLocal().findInvitation( invitationId);
    	}
    	return invitation;
    }
    
}
