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
 * @version $Id: AppEvalAdaptor.java,v 1.27.2.3 2010/11/06 12:34:56 jchurin Exp $
 */   

package org.tolven.app;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.facttemplates.Fact;
import org.tolven.app.bean.Mode;
import org.tolven.app.entity.MenuData;
import org.tolven.app.entity.MenuDataVersionMessage;
import org.tolven.app.entity.MenuStructure;
import org.tolven.app.entity.Touch;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.Status;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.RulesLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.bean.TolvenMessageAttachment;
import org.tolven.doc.bean.TolvenMessageWithAttachments;
import org.tolven.doc.entity.DocAttachment;
import org.tolven.doc.entity.DocBase;
import org.tolven.el.ExpressionEvaluator;
import org.tolven.provider.ProviderLocal;
import org.tolven.rules.PlaceholderFact;
import org.tolven.rules.WMLogger;
import org.tolven.security.AccountProcessingProtectionLocal;
import org.tolven.security.DocProtectionLocal;
import org.tolven.security.key.DocumentSecretKey;
import org.tolven.trim.CE;
import org.tolven.trim.ex.TrimFactory;

/**
 * Provide general callback functions from rules.
 * @author John Churin
 *
 */
public abstract class AppEvalAdaptor implements MessageProcessorLocal {
    protected @Resource EJBContext ejbContext;
    protected @Resource SessionContext sessionContext;   
	protected @EJB AccountDAOLocal accountBean;
	protected @EJB AccountProcessingProtectionLocal accountProcessingProctectionBean;
	protected @EJB CreatorLocal creatorBean;
	protected @EJB DocumentLocal documentBean;
	protected @EJB DocProtectionLocal docProtectionBean;
	protected @EJB MenuLocal menuBean;
	protected @EJB RulesLocal rulesBean;
	protected @EJB ShareLocal shareBean;
	protected @EJB TolvenPropertiesLocal propertyBean;
	protected @EJB XMLLocal xmlBean;
	protected @EJB ProviderLocal providerBean;
	protected @EJB TouchLocal touchPlaceholderBean;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	// Callback variables
	protected TolvenMessage tm;
	private DocBase docBase;
	private Account account;
	private Account sourceAccount;
	private Date now;
	protected RuleBase ruleBase;
	private StatefulSession workingMemory;
	private Map<String, MenuDataVersionMessage> mdvs;
	private Set<Touch> suspendedTouches;
	private Set<Touch> activeTouches;
	private List<MenuData> suspendedMenuData;
	
	protected static final TrimFactory trimFactory = new TrimFactory( );

		
	protected CE sexToGender( String sex ) {
		CE gender = trimFactory.createCE();
		if ("female".equalsIgnoreCase(sex)) {
			gender.setDisplayName("Female");
			gender.setCode("C0015780"); // Female
		} else {
			gender.setDisplayName("Male");
			gender.setCode("C0024554"); // Male
		}
		gender.setCodeSystem("2.16.840.1.113883.6.56");
		gender.setCodeSystemVersion("2007AA");
		return gender;
	}
	
	public Object lookupResource( String resourceName) {
		Object rslt =  sessionContext.lookup(resourceName);
		return rslt;
	}
	protected void assertPlaceholder( MenuData mdPlaceholder) {
		logger.info( "Assert Placeholder: " + mdPlaceholder);
		// Temporarily remove touches for this placeholder
		suspendTouches(mdPlaceholder);
		// remember the placeholder in the variables list, in case we need it later for populating
//[JMC, 3/1/2010] Is this needed???     		ee.put(mdPlaceholder.getMenuStructure().getNode(), mdPlaceholder);
		// If it's a changed placeholder
//		if (mdPlaceholder.getDocumentId()==getDocument().getId()) {
			suspendedMenuData.addAll(menuBean.findReferencingMenuData(mdPlaceholder));
    		mdPlaceholder.setStatus(Status.ACTIVE);
    		workingMemory.insert(mdPlaceholder);
//    		if ("act/evn/treatmentPlan".equals(trim.getName()) && 
//    				mdPlaceholder.getMenuStructure().getNode().equals("plan")) {
//				Plan plan = findEmptyPlan(mdPlaceholder);
//				if (plan!=null) {
//					workingMemory.insert(plan);
//				}
//			}
//		}
		// If this placeholder has placeholders that need to be touched, re-process them
		// Note that this results in a recursive call back to this method
		touched( mdPlaceholder);
	}
	
	/**
	 * Any placeholder that we assert should have its referenced items 
	 * @param placeholders
	 */
	protected void assertPlaceholders( List<MenuData> placeholders) {
    	for (MenuData mdPlaceholder : placeholders) {
    		assertPlaceholder(mdPlaceholder);
    	}
	}
	/**
	 * Associate the document and attachments in the message with documents in the database either by
	 * finding document(s) or creating document(s).
	 * As a side effect of this method, the document id will be set in the message header if not already set.
	 * @param tm The message header containing the payload
	 * @param now The "transaction time"
	 */
	public void associateDocument( TolvenMessage tm, Date now ) throws Exception {
		// setup callback variables
		this.tm = tm;
		this.now = now;
		
		mdvs = new HashMap<String, MenuDataVersionMessage>();
		account = accountBean.findAccount(tm.getAccountId());
		try {
            accountProcessingProctectionBean.decryptTolvenMessage(tm);
        } catch (Exception ex) {
            throw new RuntimeException("Could not decrypt TolvenMessage: " + tm.getId(), ex);
        }
		
//		System.out.println(new String(getDocumentContent( tm.getDocumentId() )));
		
		ruleBase = rulesBean.getRuleBase(account.getAccountType().getKnownType());
		workingMemory = ruleBase.newStatefulSession();
		this.workingMemory.setGlobal("app", this);
		this.workingMemory.setGlobal("now", getNow());
	    new WMLogger(workingMemory);
		// If not present in document DB, add it now.
		if (0==tm.getDocumentId() && tm.getPayload()!=null) {
			docBase = documentBean.createXMLDocument( tm.getXmlNS(), tm.getAuthorId(), tm.getAccountId() );
			logger.info( "Document created, id: " + docBase.getId() + " Account: " + docBase.getAccount().getId());
	        String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
	        int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
			docBase.setAsEncryptedContent(tm.getPayload(), kbeKeyAlgorithm, kbeKeyLength);
		} else  if (0!=tm.getDocumentId()){
			// Document already exists, so get it.
			docBase = documentBean.findDocument(tm.getDocumentId() );
		    logger.info( "Document, id: " + docBase.getId() + " already exists" );
			}
		// Check for attachments
		if (tm instanceof TolvenMessageWithAttachments) {
			logger.info( "Attachments... ");
			for (TolvenMessageAttachment attachment : ((TolvenMessageWithAttachments)tm).getAttachments()) {
				if (0==attachment.getDocumentId()) {
					DocBase doc = new DocBase();
			        doc.setAccount(account);
			        doc.setMediaType( attachment.getMediaType() );
			        doc.setStatus(Status.NEW.value());
			        String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
			        int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
			        doc.setAsEncryptedContent(attachment.getPayload(), kbeKeyAlgorithm, kbeKeyLength);
			        documentBean.createFinalDocument(doc );
			        documentBean.createAttachment( docBase, doc, attachment.getDescription(), null, now);
			        logger.info( "Attachment: " + doc.getId() + " Length=" + attachment.getPayload().length);
				}
			}
		}
		// Special case if inbound (from another account)
		// If the message is inbound, we will be modifying the message so save the original as an attachment
		if (tm.getFromAccountId() !=0 && tm.getAccountId()!=tm.getFromAccountId()) {
			DocBase doc = documentBean.createXMLDocument( tm.getXmlNS(), tm.getAuthorId(), getAccount().getId() );
		    doc.setMediaType( getDocument().getMediaType() );
		    doc.setStatus(Status.NEW.value());
		    String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
		    int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
		    doc.setAsEncryptedContent(tm.getPayload(), kbeKeyAlgorithm, kbeKeyLength);
		    documentBean.createAttachment( doc, getDocument(), "Original message", null, now);
			logger.info( "Saving original inbound message as attachment: " + doc.getId() + " Length=" + tm.getPayload().length);
			scanInboundDocument(doc);
			// We actually mess with the copy, not the original, now an attachment.
			docBase = doc;
		}
		// Finally, make sure that the document id is set in the message header
		if (tm.getDocumentId()==0) {
			tm.setDocumentId(docBase.getId());
		}
	}
	
	protected abstract DocBase scanInboundDocument(DocBase doc) throws Exception;
	
	public void addMyProvider( Object providerId ) {
		providerBean.addMyProvider(getAccount(), (Long)providerId);
	}

	/**
	 * After processing, this method will actually remove any remaining MenuData
	 * that was not reused after rule processing.
	 */
	protected void removeSuspendedMenuData( ) {
		for (MenuData md : suspendedMenuData) {
			menuBean.deleteMenuData( md );
			logger.info( "Removed " + md );
		}
		suspendedMenuData.removeAll(suspendedMenuData);
	}
	
	/*
	 * Any menu data referencing a placeholder is initially added to the suspended list.
	 * If during rule processing, a menudata item is found to be reusable, this method is
	 * called to restore it (remove it from the suspended list).
	 */
	protected void unsuspendMenuData( MenuData mdSuspended ) {
		logger.info( "Unsuspend " + mdSuspended );
		suspendedMenuData.remove(mdSuspended);
	}
	
	protected void runRules( ) throws Exception {
		if (workingMemory==null) throw new IllegalArgumentException("Missing working memory argument");
		suspendedTouches = new HashSet<Touch>();
		activeTouches = new HashSet<Touch>();
		suspendedMenuData = new LinkedList<MenuData>();
    	List<MenuStructure> menus = menuBean.findFullMenuStructure( getAccount().getId());
    	for (MenuStructure ms : menus) {
    		workingMemory.insert(ms);
    	}
    	
		// We don't assert the message, 
    	// instead we assert a surrogate that describes the general mode of the message.
		Mode mode = new Mode( getAccount(), tm.getFromAccountId(), tm.getAccountId() );
	    workingMemory.insert(mode);
	    if ( !(mode.getDirection()==Mode.INBOUND)  && getDocument() != null) {
	    	// Now we need to remove any list items that reference this event. 
	    	// These will be WIP entries for the new Event.
			int count = menuBean.removeReferencingMenuData( getAccount().getId(), getDocument().getId(), false );
			logger.info( "Removed " + count + " non-placeholder menuData references to document " + getDocument().getId());
	    }
	    
	    // Assert the document itself.
	    workingMemory.insert(getDocument());
	    // Load working memory with the placeholders
	    loadWorkingMemory( workingMemory );
	    // Run the rule
	    workingMemory.fireAllRules();   
	    workingMemory.dispose();
	    // Any lists that we touched need a version update (for auto-update)
	    menuBean.queueDeferredMDVs(mdvs);
	    // Any placeholder left in the suspended list need to be marked as removed
	    removeSuspendedTouches();
	    // Actually remove any remaining suspended menu data
	    removeSuspendedMenuData();
	}
	/**
	 * Persist a MenuData which includes keeping track of versions
	 */
	public void persistMenuData( MenuData md ) {
		menuBean.persistMenuDataDeferred(md, mdvs);
	}
	
	protected abstract void loadWorkingMemory( StatefulSession workingMemory ) throws Exception;

	/**
	 * Look for a suspended MenuData item that matches the one we are looking for based on
	 * referenced placeholder and MenuStructure
	 * @param mdPlaceholder
	 * @param ms
	 * @return The matching menudata or null
	 */
	protected MenuData findSuspendedMenuData(MenuData mdPlaceholder, MenuStructure ms) {
		for (MenuData md : suspendedMenuData) {
			if (md.getReference()==mdPlaceholder && md.getMenuStructure()==ms) {
				unsuspendMenuData( md );
				return md;
			}
		}
		return null;
	}
	
	/**
	 * Create a reference using an overridden parent context
	 * @param mdPlaceholder
	 * @param ms
	 * @param mdParent
	 * @return
	 */
	public MenuData createReferenceMD( MenuData mdPlaceholder, MenuStructure ms, MenuData mdParent, boolean ignoreDuplicates ) {
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		if (mdParent!=null) {
			sourceMap.put("_parent", mdParent);
			sourceMap.put(mdParent.getMenuStructure().getNode(), mdParent);
		}
		return createReferenceMD( mdPlaceholder, ms, sourceMap, ignoreDuplicates);
	}	

	/**
	 * Create a new reference to the specified placeholder on the specified list (ms)
	 * @param mdPlaceholder The menu Item to be referenced
	 * @param ms MenuStructure of the item (usually a list) that will reference this placeholder 
	 * @param act If not null, make an "act" variable available in the expression language.
	 * @param ignoreDuplicates If there's already an item in the specified path referencing this placeholder, don't add it again.
	 * @return The new MenuData item, usually a list item referencing the placeholder
	 */
	public MenuData createReferenceMD( MenuData mdPlaceholder, MenuStructure ms, Map<String, Object> sourceMap, boolean ignoreDuplicates ) {
		try {
			ExpressionEvaluator ee = getExpressionEvaluator();
			MenuData md = null;
			// If the menuStructure says this should be unique by a certain key, then avoid duplicates.
			if (ignoreDuplicates) {
				List<MenuData> references = menuBean.findReferencingMDs(mdPlaceholder, ms);
				if (references.size() > 0) {
					md = references.get(0);
					unsuspendMenuData( md );
					return md;
				}
			}
			if (md==null) {
				md = findSuspendedMenuData( mdPlaceholder, ms);
			}
			if (md==null) {
				md = new MenuData();
			}
			md.setMenuStructure(ms.getAccountMenuStructure());
			md.setAccount(mdPlaceholder.getAccount());
			md.setReference(mdPlaceholder);
			md.setDocumentId(mdPlaceholder.getDocumentId());
			md.setSourceAccountId(mdPlaceholder.getSourceAccountId());
			if (sourceMap!=null) {
				MenuData mdParent = (MenuData) sourceMap.get("_parent"); 
				if (mdParent!=null) {
					md.setParent01(mdParent);
				}
			}
			ee.pushContext();
			// Populate from the act
			ee.addVariables(sourceMap);
			ee.addVariable("_placeholder", mdPlaceholder);
			menuBean.populateMenuData(ee, md);
			ee.popContext();
			// Now see if it's already there and if not, create the new one.
			persistMenuData(md);
			logger.info( "Reference: " + md.getPath() + " to " + md.getReference().getPath());
			return md;
		} catch (Exception e) {
			throw new RuntimeException( "Error creating reference from " + mdPlaceholder.getPath() + " to " + ms.getPath() + " with context " + sourceMap, e);
		}
	}
	
	public MenuData createReferenceMD( MenuData mdPlaceholder, MenuStructure ms ) {
		return createReferenceMD( mdPlaceholder, ms, new HashMap<String, Object>(), true );
	}
	
	/**
	 * Create a reference to the specified menuData item
	 * @param mdReferenced The menu Item to be referenced
	 * @param path The path of the new item 
	 * @return
	 */
	public MenuData createReferenceMD( MenuData mdReferenced, String path ) {
		MenuStructure ms = menuBean.findMenuStructure(getAccount().getId(), path);
		return createReferenceMD(mdReferenced, ms);
	}
	/**
	 * Accept a rule fact, which must be a PlaceholderFact, extract MenuData from it and create a reference to
	 * the placeholder from the named list (path).
	 * @param fact
	 * @param path
	 * @return
	 */
	public MenuData createReferenceMD( Fact fact, String path ) {
		if (!(fact instanceof PlaceholderFact)) {
			throw new RuntimeException( "Fact " + fact + " must be a PlaceholderFact to be referenced by " + path);
		}
		PlaceholderFact mdReferencedFact = (PlaceholderFact)fact;
		MenuStructure ms = menuBean.findMenuStructure(getAccount().getId(), path);
		return createReferenceMD(mdReferencedFact.getPlaceholder(), ms);
	}
	
	/**
	 * Create a reference using an overridden parent context
	 * @param mdPlaceholder
	 * @param ms
	 * @param mdParent
	 * @return
     */
    public MenuData createReferenceMD(MenuData mdPlaceholder, MenuStructure ms, MenuData mdParent) {
        return createReferenceMD(mdPlaceholder, ms, mdParent, true);
    }
	
    /**
     * Callback to create and populate a placeholder
     * @param trim
     * @param ms
     * @return
     */
    public MenuData createPlaceholder(MenuStructure ms) {
        return createPlaceholder(ms , null);
    }

    /**
     * Create placeholder with MenuStructure ms, and set one of its fields to menuData
     * 
     * @param ms
     * @param field
     * @param menuData
     * @return
     */
    public MenuData createPlaceholder(MenuStructure ms, String field, MenuData menuData) {
        Map<String, Object> menuDataMap = new HashMap<String, Object>();
        menuDataMap.put(field, menuData);
        return createPlaceholder(ms, menuDataMap);
    }

    /**
     * Create placeholder with MenuStructure ms, and set its fields to menuData objects in menuDataMap
     * 
     * @param ms
     * @param menuDataMap
     * @return
     */
    public MenuData createPlaceholder(MenuStructure ms, Map<String, Object> menuDataMap) {
        ExpressionEvaluator ee = getExpressionEvaluator();
        MenuData mdPlaceholder = new MenuData();
        mdPlaceholder.setMenuStructure(ms.getAccountMenuStructure());
        mdPlaceholder.setAccount(ms.getAccount());
        ee.pushContext();
        if (menuDataMap != null) {
            for (String key : menuDataMap.keySet()) {
                ee.addVariable(key, menuDataMap.get(key));
            }
        }
        menuBean.populateMenuData(ee, mdPlaceholder);
        ee.popContext();
        mdPlaceholder.setStatus(Status.NEW);
        if (getDocument() != null) {
            mdPlaceholder.setDocumentId(getDocument().getId());
        }
        persistMenuData(mdPlaceholder);
        return mdPlaceholder;
    }

	/**
	 * Return a list of zero or more attachments. 
	 * @return List of DocAttachment objects which reference the attached documents. 
	 */
	public List<DocAttachment> getAttachments() {
		return documentBean.findAttachments(getDocument());
	}
	
	/**
	 * Given a placeholder and a path to a list, find the item(s) in the list pointing to the placeholder.
	 * @param ms
	 * @param path
	 * @return
	 */
	public List<MenuData> findReferencingMDs( MenuData mdPlaceholder, String path ) {
		MenuStructure msList = menuBean.findMenuStructure(mdPlaceholder.getAccount().getId(), path);
		return menuBean.findReferencingMDs( mdPlaceholder, msList );
	}	
   
    /**
     * Insert any references to a specific placeholder in a specified list into working memory.
     * Items on the list do not need to be new.
     * @param mdPlaceholder
     * @param msList
     */
    public void insertReferencingItems(MenuData mdPlaceholder, MenuStructure msList ) {
        List<MenuData> items = menuBean.findReferencingMDs( mdPlaceholder, msList );
        for (MenuData item : items) {
            workingMemory.insert(item);
        }
    }

    /**
     * Insert a MenuData into working memory.
     * @param id
     */
    public void insertMenuDataItem(long id) {
        MenuData menuData = menuBean.findMenuDataItem(id);
        workingMemory.insert(menuData);
    }
    
	public void queueAttachments( String ns ) {
		if (!(tm instanceof TolvenMessageWithAttachments)) return;
		long destinationAccountId = getAccount().getId();
		try {
			TolvenMessageWithAttachments tma = (TolvenMessageWithAttachments) tm;
			for (TolvenMessageAttachment attachment : tma.getAttachments() ) {
				TolvenMessageWithAttachments tm = new TolvenMessageWithAttachments();
				tm.setAccountId(destinationAccountId);
				tm.setFromAccountId(getSourceAccount().getId());
				tm.setAuthorId(tm.getAuthorId());
				tm.setXmlNS( ns );
				tm.setMediaType(attachment.getMediaType());
				tm.setPayload(attachment.getPayload());
				documentBean.queueTolvenMessage(tm);
			}
		} catch (Exception e) {
			throw new RuntimeException( "Error queueing attachment to account " + destinationAccountId, e );
		}
	}
	
	public void createPlaceholdersForAttachments( String ns,String trimName,String menuPath,String status ) {
		if (!(tm instanceof TolvenMessageWithAttachments)) return;
		long destinationAccountId = getAccount().getId();
		try {
			TolvenMessageWithAttachments tma = (TolvenMessageWithAttachments) tm;
			for (TolvenMessageAttachment attachment : tma.getAttachments() ) {
				MenuData md = creatorBean.createTRIMPlaceholder(getAccountUser(), trimName, menuPath, getNow(), null);
				DocBase parentDoc = documentBean.findDocument(md.getDocumentId());
				DocBase attachmentDoc = documentBean.findDocument(attachment.getDocumentId());
				documentBean.createAttachment( parentDoc, attachmentDoc, attachment.getDescription(), getAccountUser(), getNow());				
			}
		} catch (Exception e) {
			throw new RuntimeException( "Error queueing attachment to account " + destinationAccountId, e );
		}
	}
	
	
	/**
	 * Return the date of n years ago
	 * @param years
	 * @return
	 */
	public Date yearsAgo( int years ) {
		GregorianCalendar cal = new GregorianCalendar( );
		cal.setTime(getNow());
		cal.add(GregorianCalendar.YEAR, -years);
		return cal.getTime();
	}
	
	/**
	 * Return the date of n years from now
	 * @param years
	 * @return
	 */
	public Date yearsFromNow( int years ) {
		GregorianCalendar cal = new GregorianCalendar( );
		cal.setTime(getNow());
		cal.add(GregorianCalendar.YEAR, years);
		return cal.getTime();
	}
	
	/**
	 * Return an appropriate expression evaluator. Subclasses must implement this method 
	 * @return
	 */
	protected abstract ExpressionEvaluator getExpressionEvaluator();
	
	public void finalize( DocBase doc) {
		documentBean.finalizeDocument(doc);
		logger.info( "Finalized Document, id: " + doc.getId());
	}

	public DocBase getDocument() {
		return docBase;
	}

	public Account getSourceAccount() {
		if (sourceAccount==null) {
			sourceAccount = accountBean.findAccount(tm.getFromAccountId());
		}
		return sourceAccount;
	}

    public void info( String message ) {
        logger.info( "AccountId: " + getAccount().getId() + " " + message);
    }

	public void debug( String message ) {
		logger.debug( "AccountId: " + getAccount().getId() + " " + message);
	}

	public Account getAccount() {
		return account;
	}

	public Date getNow() {
		return now;
	}

	public StatefulSession getWorkingMemory() {
		return workingMemory;
	}
	/**
	 * Get the account User for this message session. We first look for a conventional account user
	 * that is, a real user with real access to this account. If not found, then look for or create
	 * a "system" user and/or a "system" accountUser. The user is usually mdbuser. 
	 * @return AccountUser
	 */
	public AccountUser getAccountUser() {
			return accountBean.getSystemAccountUser(getAccount(),true, getNow());
	}
	
	/**
	 * Get the decrypted content of a document. This requires that the account user be 
	 * @param documentId The document to be decrypted
	 * @return The decrypted byte array 
	 */
	public byte[] getDocumentContent(long documentId, PrivateKey userPrivateKey) {
		String principal = ejbContext.getCallerPrincipal().getName();
		long accountId = getAccount().getId();
		AccountUser accountUser = accountBean.findAccountUser(principal, accountId);
		if (accountUser==null) {
			throw new RuntimeException( "Document decryption not possible; Account " + accountId + " has not added " + principal + " as a user of the account");
		}
		DocBase d = documentBean.findDocument(documentId);
		byte[] bytes = docProtectionBean.getDecryptedContent(d, accountUser, userPrivateKey);
		return bytes;
	}
	
	/**
	 * If the focalPlaceholder has changed, then cause each of the placeholders referencing it to be reprocessed.
	 * @param focalPlaceholder
	 */
	public void touched( MenuData focalPlaceholder ) {
		List<Touch> touched = touchPlaceholderBean.findTouched( focalPlaceholder );
		for (Touch touch : touched ) {
			assertPlaceholder(touch.getUpdatePlaceholder());
		}
	}
	
	/**
	 * Ensure that the "update placeholder" will (later) be reprocessed if the "focal placeholder" is 
	 * updated. For example, update encounter placeholder if focal patient placeholder is updated.
	 * There are two possible ways this operates. First, if there is an entry in the suspendedTouches list, then
	 * just remove it from that list. Otherwise, create a new entry in the touches list.
	 */
	public void touchIf( MenuData updatePlaceholder, MenuData focalPlaceholder ) {
		// If already touched, nothing to do.
		for (Touch touch : activeTouches) {
			if (touch.getFocalPlaceholder().getId()==focalPlaceholder.getId() && 
					touch.getUpdatePlaceholder().getId()==updatePlaceholder.getId()) {
				return;
			}
		}
		// If suspended, reactivate it
		for (Touch touch : suspendedTouches) {
			if (touch.getFocalPlaceholder().getId()==focalPlaceholder.getId() && 
					touch.getUpdatePlaceholder().getId()==updatePlaceholder.getId()) {
				suspendedTouches.remove(touch);
				activeTouches.add(touch);
				return;
			}
		}
		// Add a new touch object
		Touch touch = new Touch();
		touch.setAccount(updatePlaceholder.getAccount());
		touch.setFocalPlaceholder(focalPlaceholder);
		touch.setUpdatePlaceholder(updatePlaceholder);
		touchPlaceholderBean.persistTouch(touch);
		activeTouches.add(touch);
	}
	
	/**
	 * Prior to processing a placeholder, we remove all touchIf entries. The rules will add it back if
	 * desired. This is a provisional action. If a rule adds back this placeholder, then it will be restored
	 * and from a database perspective, no change occurs. If, on the other hand, the touch is not renewed, then
	 * it will be removed from the database.
	 */
	public void suspendTouches( MenuData mdPlaceholder ) {
		List<Touch> touches = touchPlaceholderBean.findTouches(mdPlaceholder);
		suspendedTouches.addAll(touches);
	}
	
	/**
	 * Any suspended touches remaining in the list should be removed.
	 */
	public void removeSuspendedTouches() {
		for (Touch touch : suspendedTouches) {
			touch.setDeleted(true);
		}
	}
}
