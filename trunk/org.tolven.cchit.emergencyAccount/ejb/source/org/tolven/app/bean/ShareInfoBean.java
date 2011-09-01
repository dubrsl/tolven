/*
 *  Copyright (C) 2006 Tolven Inc 
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 * 
 * Contact: info@tolvenhealth.com
 */
/**
 * This file contains ShareInfoBean to handle sharing.
 *
 * @package org.tolven.app.bean
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @copyright Tolven Inc 
 * @since File available since Release 0.0.1
 */
package org.tolven.app.bean;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.ShareInfoLocal;
import org.tolven.app.ShareInfoRemote;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageAttachment;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.doc.entity.DocAttachment;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.Compute;
import org.tolven.trim.ENSlot;
import org.tolven.trim.LabelFacet;
import org.tolven.trim.Message;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.ActEx;
import org.tolven.trim.ex.TrimFactory;
import org.tolven.xml.ParseXML;

/**
 * Manages sharing of trims.
 * 
 * @author Valsaraj Viswanathan <valsa.v@cyrusxp.com>
 * @since File available since Release 0.0.1
 */
/*
=============================================================================================================================================
No:  	|  Created/Updated Date |    Created/Updated By |     Method name/Comments            
==============================================================================================================================================
1    	| 01/25/2011           	| Valsaraj Viswanathan 	| Initial Version 	
==============================================================================================================================================
*/
@Stateless
@Local(ShareInfoLocal.class)
@Remote(ShareInfoRemote.class)
public class ShareInfoBean extends CreatorBean implements ShareInfoLocal, ShareInfoRemote {
	private static String MESSAGE_TRIM = "docclin/evn/shareInfoIn";
	private static TrimFactory factory = new TrimFactory();
	
	private Logger logger = Logger.getLogger(this.getClass());
	private TrimMarshaller marshal = new TrimMarshaller();
	
	/**
	 * Shares information with emergency account.
	 * 
	 * added on 01/31/2011
	 * @author Valsaraj
	 * @param trim - trim
	 * @param app - AppEvalAdaptor object
	 * @param tm - TolvenMessage object
	 */
	public void startSharing(Trim trim, AppEvalAdaptor app, TolvenMessage tm) {
		logger.info("Sharing " + trim.getName() + "..............");
		
		try {
			ActEx trimAct = (ActEx) trim.getAct();
			Trim shareTrim = trimBean.findTrim("docclin/evn/ShareInfo");
			
			if (setReceiverDetails(trim, app, tm, shareTrim, MESSAGE_TRIM)) {
				setPatientDetails(trim);
				setSharingStatus(trimAct, "1");
				linkPatient(trim);
				sendMessage(trim, app, tm, Long.parseLong(shareTrim.getMessage()
						.getReceiver().getAccountId()));
			}
		} catch (Exception e) {
			logger.info("Failed to share information: " + e.getMessage());
		}
	}

	/**
	 * Links the patient in the shared TRIM.
	 * 
	 * added on 01/31/2011
	 * @author Valsaraj
	 * @param trim - trim
	 */
	public void linkPatient(Trim trim) {
		Compute linkPat = new  Compute();
		linkPat.setType("org.tolven.process.CreatePatient");
		List<Compute.Property> properties = linkPat.getProperties();
		Compute.Property p1 = new Compute.Property();
		p1.setName("enabled");
		p1.setValue(Boolean.TRUE);
		properties.add(p1);
		((ActEx) trim.getAct()).getComputes().add(linkPat);
	}
	
	/**
	 * Sets receiver details.
	 * 
	 * added on 01/31/2011
	 * @author Valsaraj
	 * @param trim - trim
	 * @param app - AppEvalAdaptor object
	 * @param tm - TolvenMessage object
	 * @param shareTrim - share trim template
	 * @param messageTrim - message trim
	 * @return boolean status of operation
	 */
	public boolean setReceiverDetails(Trim trim, AppEvalAdaptor app,
			TolvenMessage tm, Trim shareTrim, String messageTrim)
			throws Exception {
		try {
			ActEx trimAct = (ActEx) trim.getAct();
			String receiverId = trimAct.getRelationship().get(
					"emergencyAccount").getAct().getObservation().getValues()
					.get(0).getST().toString();
			String receiverName = trimAct.getRelationship().get(
					"emergencyAccount").getAct().getObservation().getValues()
					.get(1).getST().toString();
			
			if (!"0".equals(receiverId) && !"0".equals(receiverName)) {
				Message msg = shareTrim.getMessage();
				msg.getSender().setAccountId(tm.getFromAccountId() + "");
				msg.getSender().setAccountName(app.getAccount().getTitle());
				msg.getSender().setTrim(messageTrim);
				msg.getReceiver().setAccountId(receiverId);
				msg.getReceiver().setAccountName(receiverName);
				trim.setMessage(msg);
				
				return true;
			}
		} catch (Exception e) {
			throw (new Exception("Failed to set receiver details"));
		}
		
		return false;
	}

	/**
	 * Sets patient details.
	 * 
	 * added on 01/31/2011
	 * @author Valsaraj
	 * @param trim - trim
	 */
	public void setPatientDetails(Trim trim) throws Exception {
		try {
			ActEx trimAct = (ActEx) trim.getAct();
			ENSlot enSlot = trimAct.getParticipation().get("subject").getRole()
					.getPlayer().getName();
			enSlot.getENS().get(0).getParts().get(0).getST().setValue("");
			enSlot.getENS().get(0).getParts().get(1).getST().setValue("");
			enSlot.getENS().get(0).getParts().get(2).getST().setValue(
					trimAct.getRelationship().get("patientInfo").getAct()
							.getObservation().getValues().get(0).getST()
							.toString());
		} catch (Exception e) {
			throw (new Exception("Invalid patient information"));
		}
	}
		
	/**
	 * Sends a trim payload to target account.
	 * 
	 * added on 01/31/2011
	 * @author Valsaraj
	 * @param trim - trim
	 * @param app - AppEvalAdaptor object
	 * @param tm - TolvenMessage object
	 * @param destinationAccountId - destination account id
	 */
	public void sendMessage(Trim trim, AppEvalAdaptor app, TolvenMessage tm,
			Long destinationAccountId) {
		/* Don't forward to ourself */
		if (destinationAccountId != null
				&& destinationAccountId != app.getAccount().getId()) {
			try {
				logger.info("Forward to account" + destinationAccountId);
				TolvenMessage tmNew;

				if (tm instanceof TolvenMessageWithAttachments) {
					List<DocAttachment> docAttList = documentBean
							.findAttachments(app.getDocument());
					DocAttachment docAtt;
					tmNew = new TolvenMessageWithAttachments();
					((TolvenMessageWithAttachments) tmNew)
							.setAttachments(new ArrayList<TolvenMessageAttachment>());
					int index = -1;
					TolvenMessageAttachment tmAttNew;

					for (TolvenMessageAttachment tmAtt : ((TolvenMessageWithAttachments) tm)
							.getAttachments()) {
						index++;
						docAtt = docAttList.get(index);

						if (!docAtt.getDescription().equals("Original message")) {
							tmAttNew = new TolvenMessageAttachment();
							tmAttNew.setDescription(docAtt.getDescription());
							tmAttNew.setPayload(tmAtt.getPayload());
							tmAttNew.setMediaType(tmAtt.getMediaType());
							tmAttNew.setVersion(tmAtt.getVersion());
							((TolvenMessageWithAttachments) tmNew)
									.getAttachments().add(tmAttNew);
						}
					}
				} else {
					tmNew = new TolvenMessage();
				}

				tmNew.setAccountId(destinationAccountId);
				tmNew.setAuthorId(tm.getAuthorId());
				tmNew.setFromAccountId(tm.getAccountId());
				tmNew.setXmlNS(TRIM_NS);
				tmNew.setXmlName(tm.getXmlName());
				/* Creating new PayLoad to be added to the tolven message */
				tmNew.setPayload(marshal.addTrimAsPayload(trim));
				documentBean.queueTolvenMessage(tmNew);
			} catch (Exception e) {
				throw new RuntimeException("Error forwarding message", e);
			}
		}
	}
	
	/**
	 * Sets a status to differentiate between request and response
	 *
	 * added on 01/31/2011
	 * @author Valsaraj
	 * @param trimAct - act of trim
	 * @param statusVal - status value
	 */
	public void setSharingStatus(ActEx trimAct, String statusVal) {
		ActRelationship sharingRel = trimAct.getRelationship().get(
				"sharingStatus");

		if (sharingRel == null) {
			sharingRel = factory.createActRelationship();
			sharingRel.setName("sharingStatus");
			sharingRel.setLabel(new LabelFacet());
			trimAct.getRelationships().add(sharingRel);
		}

		trimAct.getRelationship().get("sharingStatus").getLabel().setValue(
				statusVal);
	}
	
	/**
	 * Class for marshalling Trim objects
	 * 
	 * added on 01/31/2011
	 * @author Valsaraj
	 */
	public class TrimMarshaller extends ParseXML {
		@Override
		protected String getParsePackageName() {
			return "org.tolven.trim";
		}
		
		/**
		 * Method to convert Trim to a payload.
		 * 
		 * added on 01/31/2011
		 * @author Valsaraj
		 */
		private byte[] addTrimAsPayload( Trim trim) throws JAXBException {
	        ByteArrayOutputStream output = new ByteArrayOutputStream( );
	        Marshaller m = getMarshaller();
	        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        m.marshal( trim, output );
	        
	        return output.toByteArray();
		}
	}
}
