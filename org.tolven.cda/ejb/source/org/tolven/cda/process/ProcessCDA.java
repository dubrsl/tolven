/*
 * Copyright (C) 2009 Tolven Inc

 * This library is free software; you can redistribute it and/or modify it under the terms of 
 * the GNU Lesser General Public License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;  
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * Contact: info@tolvenhealth.com 
 *
 * @author <your name>
 * @version $Id: ProcessCDA.java,v 1.4 2010/01/21 06:57:22 jchurin Exp $
 */  

package org.tolven.cda.process;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.drools.StatefulSession;
import org.tolven.app.AppEvalAdaptor;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.core.entity.Account;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuStructure;
import org.tolven.app.entity.PlaceholderID;
import org.tolven.cda.api.CDASubProcessLocal;
import org.tolven.cda.api.ProcessCDALocal;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.doc.entity.DocBase;
import org.tolven.doctype.DocumentType;
import org.tolven.el.ExpressionEvaluator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Process CDA R2 messages. Unlike Trim-based messages which define binding requirements in the message itself, CDA messages
 * are bound to placeholders in a "pull mode", that is, from the outside. For example, the patient ID is assumed to exist in the
 * message and will be extracted by this module and then posted
 * @author John Churin
 *
 */
@Stateless
@Local( ProcessCDALocal.class )
public class ProcessCDA extends AppEvalAdaptor {
	
    private static final String CDAns = "urn:hl7-org:v3";	
    private ExpressionEvaluator cdaee;
	private Logger logger = Logger.getLogger(this.getClass());
	private  XPath xpath = XPathFactory.newInstance().newXPath();
	
    public final static String CDA_SUB_PROCESSOR_NAME = "processorJNDI";

    // A list of cdaSubProcessors to be called when we process a CDA message. 
    // Each processor determines if it is interested in the message.
    private Set<String> cdaSubProcessors;

	private final XPathExpression templateIdPath = path("/ClinicalDocument/templateId/@root");
	private final XPathExpression titlePath = path("/ClinicalDocument/title/text()");
	private final XPathExpression sourcePath = path("/ClinicalDocument/author/assignedAuthor/representedOrganization/name");
	private final XPathExpression effectiveTimePath = path("/ClinicalDocument/effectiveTime/@value");
	private final XPathExpression patientRolePath = path("/ClinicalDocument/recordTarget/patientRole");
	private final XPathExpression patientNamePath = path("/ClinicalDocument/recordTarget/patientRole/patient/name");
	private final XPathExpression givenNamePath = path("given/text()");
	private final XPathExpression familyNamePath = path("family/text()");
	private final XPathExpression idPath = path("id");
	private final XPathExpression rootPath = path("@root");
	private final XPathExpression extensionPath = path("@extension");
	private final XPathExpression assigningAuthorityPath = path("@assigningAuthorityName");
	protected Document document;
	private MenuData mdPatientDoc;
	
	protected XPathExpression path( String expression ) {
		try {
			return xpath.compile(expression);
		} catch (XPathExpressionException e) {
			throw new RuntimeException( "Invalid XPath Expression", e );
		}
	}

	protected void openDocument() {
		// parse the XML as a W3C Document
	}
	
	@Override
	protected ExpressionEvaluator getExpressionEvaluator() {
		if (cdaee==null) {
			cdaee = new TrimExpressionEvaluator();
			cdaee.addVariable( "now", getNow());
			cdaee.addVariable( "doc", getDocument());
			cdaee.addVariable(TrimExpressionEvaluator.ACCOUNT, getAccount());
			cdaee.addVariable(DocumentType.DOCUMENT, getDocument());
		}
		return cdaee;
	}
	
	protected void initializeCDASubProcessors( ) {
		try {
			if (cdaSubProcessors==null) {
				Properties properties = new Properties();
				String propertyFileName = this.getClass().getSimpleName()+".properties"; 
				InputStream is = this.getClass().getResourceAsStream(propertyFileName);
				String cdaProcessorNames = null;
				if (is!=null) {
					properties.load(is);
					cdaProcessorNames = properties.getProperty(CDA_SUB_PROCESSOR_NAME);
					is.close();
				}
				cdaSubProcessors = new HashSet<String>();
				if (cdaProcessorNames!=null && cdaProcessorNames.length()>0) {
					String names[] = cdaProcessorNames.split(",");
					// Ignore duplicates
					for (String name : names) {
						cdaSubProcessors.add(name);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException( "Error initializing CDA Processors", e);
		}
	}
	
	@Override
	protected void loadWorkingMemory(StatefulSession workingMemory) throws Exception {
		try {
		    workingMemory.insert(this);
		    logger.info("From accountId " + tm.getFromAccountId());
		    workingMemory.insert(tm);		    

		    logger.info("doc accountId " + getDocument().getAccount().getId());
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream bis = new ByteArrayInputStream( tm.getPayload() );
			document = builder.parse(bis);
			bis.close();
			NodeList patientRoleList = (NodeList) patientRolePath.evaluate(document, XPathConstants.NODESET );
			if (patientRoleList.getLength()!=1) {
				throw new RuntimeException( "Missing Record Target/PatientRole element in CDA document" );
			}
			MenuData mdPatient = bindToPatient(patientRoleList.item(0));
			// CDA Contents
			initializeCDASubProcessors();
	    	InitialContext ctx = new InitialContext();
	    	for (String cdaProcessor : cdaSubProcessors ) {
	    		CDASubProcessLocal subProcessorBean = (CDASubProcessLocal) ctx.lookup(cdaProcessor);
	    		subProcessorBean.process(document, mdPatient, this );
	    	}
			workingMemory.insert(mdPatient);
			
			String templateId = templateIdPath.evaluate(document);
			getDocument().setSchemaURI(templateId);
			workingMemory.insert(bindToDocument(document, mdPatient));
		} catch (Exception e) {
			throw new RuntimeException( "Unable to process CDA document", e );
		}
	}
	
	/**
	 * Forward the underlying message to the list of accounts specified
	 * @param doc
	 * @param list
	 */
	public void forwardToList( MenuStructure list, MenuData mdPatient, long except ) {
		List<MenuData> results = menuBean.findListContents(getAccount(), list, mdPatient);
		logger.info("Except accountId " + except);
		for (MenuData md : results) {
			Long destinationAccountId = (Long) md.getField("AccountId");
			logger.info("destinationAccountId " + destinationAccountId);
			// Don't forward to ourself
			if (destinationAccountId!=null && destinationAccountId!=getAccount().getId() ) {
				if (destinationAccountId != except ) {
					// Get the list of accounts to send to 
					try {
						logger.info("Forward to account" + destinationAccountId);
						TolvenMessage tmNew = new TolvenMessage();
						tmNew.setAccountId(destinationAccountId);
						logger.info("From accountId " + this.getAccount().getId());
						tmNew.setFromAccountId(this.getAccount().getId());
						tmNew.setAuthorId(0);
						tmNew.setXmlNS(CDAns);
						tmNew.setPayload(tm.getPayload());
						documentBean.queueTolvenMessage(tmNew);						
						// get the outShare and insert it into working memory
						getWorkingMemory().insert(generateOutShare(document,mdPatient, md));
					} catch (Exception e) {
						throw new RuntimeException( "Error forwarding message", e);
					}					
				}
				else if (except > 0) { // generate an inShare for the from account to this account
					getWorkingMemory().insert(generateInShare(document,mdPatient, md));					
				}
			}
		}
		
	}

	/**
	 * Generate a placeholder for outShare 
	 * @param document
	 * @param patient
	 * @param menuData representing an accountShare
	 * @return outShare
	 */
	protected MenuData generateOutShare( Document document, MenuData mdPatient, MenuData accountShare ) {
		ExpressionEvaluator ee = getExpressionEvaluator();
		try {
			MenuStructure msOutShare = menuBean.findMenuStructure(getAccount(), ":patient:outShare");
			MenuData mdOutShare = new MenuData();
			mdOutShare.setMenuStructure(msOutShare.getAccountMenuStructure());
			mdOutShare.setDocumentId(getDocument().getId());
			mdOutShare.setAccount(getAccount());
			mdOutShare.setParent01(mdPatient);
			mdOutShare.setDate01(getNow());
			mdOutShare.setString02(titlePath.evaluate(document));
			mdOutShare.setString03((String)accountShare.getField("Name"));
			mdOutShare.setString08(mdPatientDoc.getPath());
			persistMenuData(mdOutShare);			
			return mdOutShare;
		} catch (Exception e) {
			throw new RuntimeException( "Unable to setup generateOutShare in account " + getAccount().getId() , e );
		}
	}
	
	/**
	 * Generate a placeholder for inShare 
	 * @param document
	 * @param patient
	 * @param menuData representing an accountShare
	 * @return inShare
	 */
	protected MenuData generateInShare( Document document, MenuData mdPatient, MenuData accountShare ) {
		ExpressionEvaluator ee = getExpressionEvaluator();
		try {
			MenuStructure msOutShare = menuBean.findMenuStructure(getAccount(), ":patient:inShare");
			MenuData mdOutShare = new MenuData();
			mdOutShare.setMenuStructure(msOutShare.getAccountMenuStructure());
			mdOutShare.setDocumentId(getDocument().getId());
			mdOutShare.setAccount(getAccount());
			mdOutShare.setParent01(mdPatient);
			mdOutShare.setDate01(getNow());
			mdOutShare.setString02(titlePath.evaluate(document));
			mdOutShare.setString01((String)accountShare.getField("Name"));
			mdOutShare.setString08(mdPatientDoc.getPath());
			persistMenuData(mdOutShare);			
			return mdOutShare;
		} catch (Exception e) {
			throw new RuntimeException( "Unable to setup generateInShare in account " + getAccount().getId() , e );
		}
	}
	
	/**
	 * Associate a document with the patient
	 * @param mdPatient
	 * @return The Document placeholder
	 */
	protected MenuData bindToDocument( Document document, MenuData mdPatient ) {
		ExpressionEvaluator ee = getExpressionEvaluator();
		try {
			MenuStructure msDocument = menuBean.findMenuStructure(getAccount(), ":patient:patDoc");
			MenuData mdPatDoc = new MenuData();
			mdPatDoc.setMenuStructure(msDocument.getAccountMenuStructure());
			mdPatDoc.setDocumentId(getDocument().getId());
			mdPatDoc.setAccount(getAccount());
			mdPatDoc.setParent01(mdPatient);
			String effectiveTimeString = effectiveTimePath.evaluate(document);
			if (effectiveTimeString!=null) {
				// Parse only the length we have - eg if 20090621 supplied, then only parse yyyymmdd. 
				String sdfString = "yyyyMMddhhmmss".substring(0, Math.min(effectiveTimeString.length(),14));
				SimpleDateFormat sdf = new SimpleDateFormat(sdfString);
				Date effectiveTime = sdf.parse(effectiveTimeString);
				mdPatDoc.setField("effectiveTime", effectiveTime);
			}
			mdPatDoc.setField("title", titlePath.evaluate(document));
			mdPatDoc.setField("source", sourcePath.evaluate(document));
			persistMenuData(mdPatDoc);
			ee.pushContext();
			XPathMap xpathMap = new XPathMap( document );
			ee.addVariable("xpath", xpathMap);
			// Now pull attributes into menuData from metadata
			menuBean.populateMenuData(ee, mdPatDoc);
			mdPatientDoc = mdPatDoc;
			ee.popContext();
			
			return mdPatDoc;
		} catch (Exception e) {
			throw new RuntimeException( "Unable to setup patDoc in account " + getAccount().getId() , e );
		}
	}
	
	/**
	 * CDA RecordTarget is the patient. Return the patient object (new or existing) with demographic data updated per
	 * the RecordTarget attributes pulled from the definition of patient in application.xml.
	 * @return Patient MenuData
	 */
	protected MenuData bindToPatient( Node patientRole ) {
		ExpressionEvaluator ee = getExpressionEvaluator();
		MenuStructure msPatient = menuBean.findMenuStructure(getAccount(), ":patient");
		try {
			MenuData mdPatient = null;
			// See if this patient is a current patient
			NodeList idList = (NodeList) idPath.evaluate(patientRole, XPathConstants.NODESET );
			for (int i = 0; i < idList.getLength(); i++) {
				String root = rootPath.evaluate(idList.item(i));
				String extension = extensionPath.evaluate(idList.item(i));
				System.out.println("ID Root: " + root + " extension: " + extension);
				List<MenuData> mdPatients = menuBean.findMenuDataById(getAccount(), root, extension);
				if (mdPatients.size() > 0) {
					mdPatient = mdPatients.get(0);
					// Since this is an update, clear out previous references, such as from lists) to this placeholder.
					menuBean.removeReferencingMenuData(mdPatient);
					break;
				}
			}
			if (mdPatient==null) {
				mdPatient = new MenuData();
				mdPatient.setMenuStructure(msPatient.getAccountMenuStructure());
				mdPatient.setDocumentId(getDocument().getId());
				mdPatient.setAccount(getAccount());
				// Add IDs to MenuData item
				for (int i = 0; i < idList.getLength(); i++) {
					String root = rootPath.evaluate(idList.item(i));
					String extension = extensionPath.evaluate(idList.item(i));
					String assigningAuthority = assigningAuthorityPath.evaluate(idList.item(i));
					mdPatient.addPlaceholderID(root, extension, assigningAuthority);
				}
			 } else {
				// add any IDs that do not exist yet
				PlaceholderID[] allIds = mdPatient.getPlaceholderIDArray();
				for (int j = 0; j < idList.getLength(); j++) {
					String root = rootPath.evaluate(idList.item(j));
					String extension = extensionPath.evaluate(idList.item(j));
					String assigningAuthority = assigningAuthorityPath.evaluate(idList.item(j));
					boolean idFound = false;
					if (root != null && extension != null && assigningAuthority != null) {
						for (int i = 0; i < allIds.length; i++) {
							if (extension.equalsIgnoreCase(allIds[i].getExtension())
									&& root.equalsIgnoreCase(allIds[i].getRoot())
									&& assigningAuthority.equalsIgnoreCase(allIds[i].getAssigningAuthority())) {
								idFound = true;
								break;
							}
						}
					}
					if (!idFound)
						mdPatient.addPlaceholderID(root, extension, assigningAuthority);
				}
			}

			ee.pushContext();
			XPathMap xpathMap = new XPathMap( patientRole );
			ee.addVariable("xpath", xpathMap);
			// Now pull attributes into menuData from metadata
			menuBean.populateMenuData(ee, mdPatient);
			persistMenuData(mdPatient);
			ee.popContext();
			return mdPatient;
		} catch (Exception e) {
			throw new RuntimeException( "Unable to setup patient in account " + getAccount().getId() , e );
		}
	}
	
	@Override
	protected DocBase scanInboundDocument(DocBase doc) throws Exception {
		return doc;
	}

	@Override
	public void process(Object message, Date now) {
		try {
			if (message instanceof TolvenMessage || 
				message instanceof TolvenMessageWithAttachments) { 
				TolvenMessage tm = (TolvenMessage) message;
				// We're only interested in CDA messages
				if (CDAns.equals(tm.getXmlNS())) {
					associateDocument( tm, now );		
					// Run the rules now
					runRules( );
					logger.info( "Processing CDA document " + getDocument().getId() + " for account: " + tm.getAccountId());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException( "Exception in CDA processor", e);
		}

	}
	
	/**
	 * Internal Map implementation that allows XPath expressions to be embedded in EL in field definitions.
	 * @author John Churin
	 *
	 */
	private static class XPathMap implements Map<String,Object> {
		private XPath xpath = XPathFactory.newInstance().newXPath();
		private Node root;
		
		public XPathMap( Node root ) {
			this.root = root;
		}
		
		@Override
		public Object get(Object key) {
			try {
				XPathExpression keyPath = xpath.compile((String)key);
				String result = keyPath.evaluate(root);
				if (result==null || result.length()==0) return null;
				return result.trim();
			} catch (Exception e) {
				throw new RuntimeException( "XPath error: " + key, e );
			}

		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<String> keySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object put(String key, Object value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> m) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object remove(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Collection<Object> values() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
}
