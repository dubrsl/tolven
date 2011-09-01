package org.tolven.doc;

import java.util.Date;
import java.util.List;

import org.tolven.doc.bean.TolvenMessage;
import org.tolven.doc.entity.DocAttachment;
import org.tolven.doc.entity.DocBase;

/**
 * This is the business interface for Document enterprise bean. Store, encrypt, and fetch whole 
 * documents. Also store transformed documents. For instance, each sharing of a doc is a new document.
 */
public interface DocumentRemote {
	/**
	 * Get a document by its internal ID
	 * @param docId the id of the document
	 * @return the document object
	 */
	public DocBase findDocument( long docId );

	/**
	 * Persist a new document and return it's ID. This is a final submission. the document is now
	 * immutable. Account and author should be provided in the document. Also, mediaType should
	 * be present.
	 * @param doc
	 * @param userId of the author
	 * @return the id assigned to the new document.
	 */
	public long createFinalDocument( DocBase doc );

   	public long countDocuments( long accountId );

	//	public DocBase findDocument( long docId, long AccountId );
   	
	public void queueMessage( byte[] payload, String xmlns, long accountId, long userId) throws Exception;

    /**
     * Queue a document to be processed by Tolven. The payload should be in cleartext and will
     * be encrypted prior to being added to the queue.
     */
    public void queueTolvenMessage(TolvenMessage tm) throws Exception;

    /**
     * Queue a document to be processed by Tolven on the queueOnDate. The payload should be in cleartext and will
     * be encrypted prior to being queued.
     */
    public void queueTolvenMessage(TolvenMessage tm, Date queueOnDate) throws Exception;

	/**
	 * Find the attachments to the specified document 
	 * @param parentDocumentId The id of the document for which attachments are sought
	 * @return The list of attachments. The list may be empty
	 */
	public List<DocAttachment> findAttachments( long parentDocumentId );

}
