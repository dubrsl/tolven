package org.tolven.process;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.tolven.app.AppEvalAdaptor;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.logging.TolvenLogger;
import org.tolven.process.InsertAct;
import org.tolven.security.key.DocumentSecretKey;
import org.tolven.trim.ActRelationship;
import org.tolven.trim.Trim;
import org.tolven.trim.ex.ActEx;

/**
 * Compute for FDB.
 * 
 * @author valsaraj
 * added on 07/22/2010
 */
public class FDBCompute extends InsertAct {
	private ActEx act;
	private long prescriberOrderNumber;
	@EJB private XMLLocal xmlBean;
	@EJB private TolvenPropertiesLocal propertyBean;
	
	public XMLLocal getXmlBean() {
		return xmlBean;
	}

	public void setXmlBean(XMLLocal xmlBean) {
		this.xmlBean = xmlBean;
	}

	public TolvenPropertiesLocal getPropertyBean() {
		return propertyBean;
	}

	public void setPropertyBean(TolvenPropertiesLocal propertyBean) {
		this.propertyBean = propertyBean;
	}

	
	
	
	
	/**
	 * Compute.
	 * 
	 * @author valsaraj
	 * added on 08/13/2010
	 * @return
	 */
	public void compute() {
		try {
			if (isEnabled()) {
				act = (ActEx) this.getAct();
				
				if ("medicationHistory".equals(getAction())) {
					List<ActRelationship> meds = act.getRelationship().get("medications").getAct().getRelationships();
					String extension = null;
					
					for (ActRelationship actRelationship : meds) {
						try {
							extension = actRelationship.getAct().getId().getIIS().get(0).getExtension();
							
							if (extension != null && ! extension.isEmpty()) {
								prescriberOrderNumber = Long.valueOf(extension.split("-")[2]);
								actRelationship.getAct().getObservation().getValues().get(10).getINT().setValue(prescriberOrderNumber);
								TolvenLogger.info("PrescriberOrderNumber: " + Long.toString(prescriberOrderNumber), FDBCompute.class);
							}
						}
						catch (Exception e) {
							
						}
					}
				}
			}
		}
		catch (Exception e) {
			
		}
	}
	
	/**
	 * Sets PON.
	 * 
	 * @author valsaraj
	 * added on 08/13/2010
	 * @return
	 */
	public static void setPON(AppEvalAdaptor app, Trim trim) {
		try {
			ActEx act = (ActEx) trim.getAct();
			List<ActRelationship> meds = act.getRelationship().get("medications").getAct().getRelationships();
			String extension = null;
				
			for (ActRelationship actRelationship : meds) {
				try {
					extension = actRelationship.getAct().getId().getIIS().get(0).getExtension();
					
					if (extension != null && ! extension.isEmpty()) {
						long prescriberOrderNumber = Long.valueOf(extension.split("-")[2]);
						actRelationship.getAct().getObservation().getValues().get(10).getINT().setValue(prescriberOrderNumber);
						TolvenLogger.info("PrescriberOrderNumber: " + Long.toString(prescriberOrderNumber), FDBCompute.class);
					}
				}
				catch (Exception e) {
					
				}
			}
			
			FDBCompute fdbc = new FDBCompute();
			ByteArrayOutputStream trimXML = new ByteArrayOutputStream();
			fdbc.getXmlBean().marshalTRIM(trim, trimXML);
	        String kbeKeyAlgorithm = fdbc.getPropertyBean().getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
	        int kbeKeyLength = Integer.parseInt(fdbc.getPropertyBean().getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
	        app.getDocument().setAsEncryptedContent(trimXML.toByteArray(), kbeKeyAlgorithm, kbeKeyLength);
		}
		catch (Exception e) {
			
		}
	}
}
