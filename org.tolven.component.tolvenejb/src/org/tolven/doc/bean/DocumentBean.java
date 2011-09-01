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
package org.tolven.doc.bean;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.tolven.admin.AdministrativeDetail;
import org.tolven.admin.Details;
import org.tolven.app.el.TrimExpressionEvaluator;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.Account;
import org.tolven.core.entity.AccountUser;
import org.tolven.core.entity.Status;
import org.tolven.core.entity.TolvenUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.DocumentRemote;
import org.tolven.doc.TolvenMessageSchedulerLocal;
import org.tolven.doc.entity.CCRException;
import org.tolven.doc.entity.DocAttachment;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocCCR;
import org.tolven.doc.entity.DocImage;
import org.tolven.doc.entity.DocXML;
import org.tolven.doctype.DocTypeFactory;
import org.tolven.doctype.DocumentType;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.AccountProcessingProtectionLocal;
import org.tolven.security.DocContentSecurity;
import org.tolven.security.DocProtectionLocal;
import org.tolven.security.key.DocumentSecretKey;

/**
 * This is the bean class for the DocumentBean enterprise bean.
 * Most document work can be done indirectly through other beans but we provide these methods
 * for explicit document manipulation.
 * In particular, the details of most documents are stored in XML form. This session bean provides methods to marshall and unmarshall
 * that XML into and out of java object trees.
 * Created Apr 19, 2006 11:41:52 AM
 * @author John Churin
 */
@Stateless
@Local(DocumentLocal.class)
@Remote(DocumentRemote.class)
public class DocumentBean implements DocumentLocal, DocumentRemote {
    private static final String CCRns = "urn:astm-org:CCR";

    @PersistenceContext
    private EntityManager em;

    private static JAXBContext jc = null;

    @EJB private DocProtectionLocal docProtectionBean;
    @EJB private AccountProcessingProtectionLocal accountProcessingProctectionLocal;
    @EJB TolvenPropertiesLocal propertyBean;
    @EJB TolvenMessageSchedulerLocal tmSchedulerBean;

    private DocTypeFactory docTypeFactory;
    public DocumentBean() {
        super();
    }

    public byte[] getDecryptedContent(DocContentSecurity doc, AccountUser activeAccountUser, PrivateKey userPrivateKey) {
        return docProtectionBean.getDecryptedContent(doc, activeAccountUser, userPrivateKey);
    }

    /**
     * Get an instance of the DocumentType factory
     * @return
     */
    public DocTypeFactory getDocTypeFactory() {
        if (docTypeFactory == null) {
            docTypeFactory = new DocTypeFactory();
        }
        return docTypeFactory;
    }

    //	public DocumentType getDocumentType (DocBase doc) {
    //		DocumentType documentType = findDocumentType(doc);
    //		documentType.prepareEvaluator( ee );
    //	}

    /**
     * Prior to evaluating an expression, this method is called to ensure that if a document is mentioned, it is parsed (or can be parsed when needed)
     * and presented as an object graph accessible from EL.
     * The documentType needs to get involved since it is what knows how to parse the document contents.
     * @param ee
     */
    public void prepareEvaluator(TrimExpressionEvaluator ee) {
        ee.addVariable(DocumentType.DOCUMENT_BEAN, this);
    }

    /**
     * Get a document by its internal ID
     * @param docId the id of the document
     * @return the document object
     */
    public DocBase findDocument(long docId) {
        DocBase doc = em.find(DocBase.class, docId);
        getDocTypeFactory().associateDocumentType(doc);
        return doc;
    }

    /**
     * The document is immediately persisted (with no XML in it). This gives us the ID we'll need
     * to create actual CCR object graph. 
     * @param userId
     * @param accountId
     * @return
     * @throws CCRException 
     * @throws IOException 
     * @throws CCRException 
     */
    public DocCCR createCCRDocument(long userId, long accountId) throws CCRException {
        DocBase doc = getDocTypeFactory().createNewDocument("text/xml", CCRns);
        createDocument(doc, userId, accountId);
        return (DocCCR) doc;
    }

    public DocXML createXMLDocument(String xmlNS, long userId, long accountId) {
        try {
			if (CCRns.equals(xmlNS)) return createCCRDocument( userId, accountId);
            DocBase doc = getDocTypeFactory().createNewDocument("text/xml", xmlNS);
            createDocument(doc, userId, accountId);
            return (DocXML) doc;
        } catch (CCRException e) {
            throw new RuntimeException("Excption creating XML Document", e);
        }
    }

    /**
     * Create a new document of the given media type. The document is persisted (though not committed).
     * Therefore, it's ID has been established. 
     * @return The document object
     */
    public DocBase createNewDocument(String mediaType, String namespace, AccountUser accountUser) {
        DocBase doc = getDocTypeFactory().createNewDocument(mediaType, namespace);
        doc.setAccount(accountUser.getAccount());
        doc.setAuthor(accountUser.getUser());
        doc.setStatus(Status.NEW.value());
        em.persist(doc);
        return doc;
    }

    /**
     * Persist a new document and return it's ID. This is a final submission. the document is now
     * immutable. Account and author should be provided in the document. Also, mediaType should
     * be present.
     * @param doc
     * @return the id assigned to the new document.
     */
    public long createFinalDocument(DocBase doc) {
        doc.setStatus(Status.ACTIVE.value());
        em.persist(doc);
        return doc.getId();
    }

    /**
     * Persist a new document and return it's ID. This is not a final submission.
     * At this point the document is persisted but not actionable. It can still be edited.
     * When the document is ready for submission, the finalizeDocument method should be called.
     * TODO: Prior to finalization, the document can also be digitally signed.
     * @param doc
     * @param userId of the author
     * @return the id assigned to the new document.
     */
    public long createDocument(DocBase doc, long userId, long accountId) {
        doc.setStatus(Status.NEW.value());
        doc.setAuthor(em.find(TolvenUser.class, userId));
        doc.setAccount(em.find(Account.class, accountId));
        em.persist(doc);
        return doc.getId();
    }

    /**
     * Complete the document submission process by rendering the document immutable. A merge will be done
     * in case the finalization occurs in a different transaction as the creation.
     * @param doc
     */
    public void finalizeDocument(DocBase doc) {
        doc.setStatus(Status.ACTIVE.value());
        saveDocument(doc);
    }

    /**
     * Save the document without finalizing. A merge will be done
     * in case the save occurs in a different transaction from the creation.
     * Note: Withholding a "Save" does not imply a rollback. For example, a document
     * that has been created or fetched and then modified within the same local VM and same transaction
     * will "automatically" be saved. So this method simply ensures that documents in other states will also be saved.
     * @param doc
     */
    public void saveDocument(DocBase doc) {
        em.merge(doc);
    }

    /**
     * Not a very practical method but we'll use it for testing.
     * @return
     */
    public List<DocBase> findAllDocuments() {
        Query query = em.createQuery("SELECT d FROM DocBase d");
        query.setMaxResults(100);
        List<DocBase> items = query.getResultList();
        return items;
    }

    /**
     * Return documents for the specified author
     */
    public List<DocBase> findDocuments(long author, int pageSize, int offset, String sortAttribute, String sortDir) {
        Query query = em.createQuery("SELECT d FROM DocBase d where author.id = :author ORDER BY d." + sortAttribute + " " + sortDir);
        query.setMaxResults(pageSize);
        query.setFirstResult(offset);
        query.setParameter("author", author);
        List<DocBase> items = query.getResultList();
        return items;
    }

    /**
     * Return all XML documents owned by any account the specified user is a member of (for demo use only). 
     * This is otherwise a security violation but is used to demonstrate that even if documents are returned
     * that the user doesn't currently have access to, they will remain encrypted.
     */
    public List<DocXML> findAllXMLDocuments(long userId, int pageSize, int offset, String sortAttribute, String sortDir) {
        Query query = em.createQuery("SELECT d FROM DocXML d, AccountUser au WHERE au.user.id = :user AND d.account.id = au.account.id ORDER BY d." + sortAttribute + " " + sortDir);
        query.setMaxResults(pageSize);
        query.setFirstResult(offset);
        query.setParameter("user", userId);
        List<DocXML> items = query.getResultList();
        return items;
    }

    /**
     * Count of all XML documents owned by any account the specified user is a member of (for demo use only). This is otherwise a security violation.
     */
    public long countXMLDocuments(long userId) {
        Query query = em.createQuery("SELECT COUNT(d) FROM DocXML d, AccountUser au WHERE au.user.id = :userId AND d.account.id = au.account.id");
        query.setParameter("userId", userId);
        Long rslt = (Long) query.getSingleResult();
        return rslt.longValue();
    }

    /**
     * @see DocumentLocal
     */
    public DocImage findImage(long docId, long accountId) {
        DocImage doc = em.find(DocImage.class, docId);
        if (doc.getAccount().getId()!=accountId) throw new IllegalArgumentException( "Document is not owned by the " +
                "current user's account.");
        return doc;
    }

    /**
     * Count of all documents owned by this account.
     */
    public long countDocuments(long accountId) {
        Query query = em.createQuery("SELECT COUNT(d) FROM DocBase d where account.id=:account");
        query.setParameter("account", accountId);
        Long rslt = (Long) query.getSingleResult();
        return rslt.longValue();
    }

    /**
     * Count of all photo-documents owned by this account.
     */
    public long countImages(long accountId) {
        Query query = em.createQuery("SELECT COUNT(d) FROM DocImage d where account.id=:account");
        query.setParameter("account", accountId);
        Long rslt = (Long) query.getSingleResult();
        return rslt.longValue();
    }

    /**
     * Return images for the specified account
     */
    public List<DocImage> findImages(long accountId, int pageSize, int offset, String sortAttribute, String sortDir) {
        Query query = em.createQuery("SELECT d FROM DocImage d where account.id = :account ORDER BY d." + sortAttribute + " " + sortDir);
        query.setMaxResults(pageSize);
        query.setFirstResult(offset);
        query.setParameter("account", accountId);
        List<DocImage> items = query.getResultList();
        return items;
    }

    /**
     * Return XML documents for the specified account
     */
    public List<DocXML> findXMLDocuments(long accountId, int pageSize, int offset, String sortAttribute, String sortDir) {
        Query query = em.createQuery("SELECT d FROM DocCCR d where account.id = :account ORDER BY d." + sortAttribute + " " + sortDir);
        query.setMaxResults(pageSize);
        query.setFirstResult(offset);
        query.setParameter("account", accountId);
        List<DocXML> items = query.getResultList();
        //        TolvenLogger.info( "Query XML Docs for account " + accountId + " count is " + items.size(), DocumentBean.class);
        return items;
    }

    /**
     * Persist a new image and return it's ID
     * @param doc
     * @param accountId of the author
     */
    public long createImage(DocImage doc, long userId, long accountId, byte[] content) {
        long id = createDocument(doc, userId, accountId);
        String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        doc.setAsEncryptedContent(content, kbeKeyAlgorithm, kbeKeyLength);
        return id;
    }

    @PostConstruct
    public void init() {
        try {
            setupJAXBContext();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public JAXBContext setupJAXBContext() throws JAXBException {
        if (jc == null) {
            //          TolvenLogger.info( " **************** [DocumentBean]setupJAXBContext **********", DocumentBean.class);
            jc = JAXBContext.newInstance("org.tolven.admin", DocBase.class.getClassLoader());
        }
        return jc;
    }

    /**
     * Store document details in XML form. A valid XML document must contain just one root element and we create
     * that element here, hiding it from the Java programmer who can just deal with the collection.
     * @param details A structure ready for marshalling into XML
     * @return
     * @throws JAXBException
     * @throws IOException
     */
    public void setDetails(DocBase doc, List<AdministrativeDetail> details) throws JAXBException, IOException {
        JAXBContext jc = setupJAXBContext();
        Details topDetail = new Details();
        topDetail.getDetail().addAll(details);
        JAXBElement<Details> top = (new org.tolven.admin.ObjectFactory()).createDetails(topDetail);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter result = new StringWriter(2000);
        m.marshal(top, result);
        result.close();
        String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        doc.setAsEncryptedContentString(result.toString(), kbeKeyAlgorithm, kbeKeyLength);
        doc.setMediaType("text/xml");
    }

    /**
     * Return the content of this structured xml document as a list of administrative detail objects  
     * @return List<AdministrativeDetail> 
     * @throws JAXBException
     * @throws IOException
     */
    public List<AdministrativeDetail> getDetails(DocBase doc, AccountUser activeAccountUser, PrivateKey userPrivateKey) throws JAXBException, IOException {
        //      TolvenLogger.info( "[DocumentBean.getDetails]: " + doc.getContentString(), DocumentBean.class);
        // Now unmarshal back to an object graph
        JAXBContext jc = setupJAXBContext();
        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement<Details> o = (JAXBElement<Details>) u.unmarshal(new StreamSource(new StringReader(docProtectionBean.getDecryptedContentString(doc, activeAccountUser, userPrivateKey))));
        Details details = o.getValue();
        return details.getDetail();
    }

    public String queueWSMessage(byte[] payload, String xmlns, long accountId, long userId) {
        try {
            queueMessage(payload, xmlns, accountId, userId);
            return "success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String testWS() {
        String rslt = "***********Test*********";
        TolvenLogger.info(rslt, DocumentBean.class);
        return rslt;
    }

    /**
     * Queue a message to be processed by Tolven
     */
    public void queueMessage(byte[] payload, String xmlns, long accountId, long userId) throws Exception {
        TolvenMessage tm = new TolvenMessage();
        tm.setAccountId(accountId);
        tm.setAuthorId(userId);
        //      tm.setXmlName("trim");
        tm.setXmlNS(xmlns);
        accountProcessingProctectionLocal.setAsEncryptedContent(payload, tm);
        tmSchedulerBean.queue(tm);
    }

    /**
     * Queue a document to be processed by Tolven. The payload should be in cleartext and will
     * be encrypted prior to being queued.
     */
    public void queueTolvenMessage(TolvenMessage tm) throws Exception {
        queueTolvenMessage(tm, null);
    }
    
    /**
     * Queue a document to be processed by Tolven on the queueOnDate. The payload should be in cleartext and will
     * be encrypted prior to being queued.
     */
    public void queueTolvenMessage(TolvenMessage tm, Date queueOnDate) throws Exception {
        // Encrypt the message
        accountProcessingProctectionLocal.encryptTolvenMessage(tm);
        tmSchedulerBean.schedule(tm, queueOnDate);
    }

    /**
     * Create a new document attachment
     * @param parentDocument The account of the parent document and attachment must be the same.
     * @param attachedDocument The document to attach
     * @param description
     * @param tolvenAuthor
     * @param now
     * @return A new attachment object which has already been persisted 
     */
    public DocAttachment createAttachment( DocBase parentDocument, DocBase attachedDocument, 
            String description, AccountUser tolvenAuthor, Date now ) {
        DocAttachment da = new DocAttachment();
        da.setAccount(parentDocument.getAccount());
        da.setParentDocument(parentDocument);
        da.setAttachedDocument(attachedDocument);
        da.setDescription(description);
        // Denormalize media type (from document)
        da.setMediaType(attachedDocument.getMediaType());
        da.setTolvenAuthor(tolvenAuthor);
        da.setUploadTime(now);
        em.persist(da);
        return da;
    }

    /**
     * Copy all attachments to the from document to be attachments to the to document.
     * The attachment is copied but the attached document is not. Therefore, an attached document to the 
     * from document the same exact document as attached to the to document. The reason that this works is
     * because all attachments are by definition immutable. Thus, if one were to edit an attached document, it
     * would actually be a different document.
     * @param fromDoc
     * @param toDoc
     */
    public void copyAttachments(DocBase fromDoc, DocBase toDoc) {
        Set<DocBase> copyAttachments = new HashSet<DocBase>();
        List<DocAttachment> fromAttachments = findAttachments(fromDoc);
        // Add the list of attachments of the from document to the list we need to copy
        for (DocAttachment att : fromAttachments) {
            copyAttachments.add(att.getAttachedDocument());
        }
        // But subtract any attachments already in the to list (if any)
        for (DocAttachment att : findAttachments(toDoc)) {
            copyAttachments.remove(att.getAttachedDocument());
        }
        // Now the copy list contains the list of attachments we need to add to the toDoc.
        // Run back through the attachment list and create the new attachments
        for (DocAttachment att : fromAttachments) {
            if (copyAttachments.contains(att.getAttachedDocument())) {
    			DocAttachment newAtt = createAttachment( toDoc,att.getAttachedDocument(), 
    					att.getDescription(), att.getTolvenAuthor(), att.getUploadTime() );
                newAtt.setMediaType(att.getMediaType());
            }
        }
    }

    /**
     * Find the attachments to the specified document 
     * @param parentDocument The document for which attachments are sought
     * @return The list of attachments. The list may be empty
     */
    public List<DocAttachment> findAttachments(DocBase parentDocument) {
    	Query query = em.createQuery("SELECT da FROM DocAttachment da WHERE da.parentDocument = :parent");
        query.setParameter("parent", parentDocument);
        List<DocAttachment> attachments = query.getResultList();
        for (DocAttachment attachment : attachments) {
            getDocTypeFactory().associateDocumentType(attachment.getAttachedDocument());
        }
        return attachments;
    }

    /**
     * Find the attachments to the specified document 
     * @param parentDocumentId The id of the document for which attachments are sought
     * @return The list of attachments. The list may be empty
     */
    public List<DocAttachment> findAttachments(long parentDocumentId) {
        DocBase parentDoc = em.find(DocBase.class, parentDocumentId);
        List<DocAttachment> attachments = findAttachments(parentDoc);
        // Touch each attachment just to make sure lazy attributes are fetched
        for (DocAttachment attachment : attachments) {
            getDocTypeFactory().associateDocumentType(attachment.getAttachedDocument());
            // Don't remove this...
            attachment.getAttachedDocument().getContent();
        }
        return attachments;
    }

    /**
     * Delete an attachment. the attachment must is part of an editable parent document.
     * @param attId
     * @param accountUser
     */
    public void deleteAttachment(long attId, AccountUser accountUser) {
        DocAttachment attachment = em.find(DocAttachment.class, attId);
        if (attachment.getAccount().getId() != accountUser.getAccount().getId()) {
            throw new IllegalArgumentException("Attachment cannot be deleted by this user");
        }
        DocBase parentDoc = attachment.getParentDocument();
        if (!parentDoc.isEditable()) {
            throw new IllegalArgumentException("Attachment cannot be deleted from an immutable parent");
        }
        em.remove(attachment);
        // Document is gone too (this is forever)
        em.remove(attachment.getAttachedDocument());
    }

}
