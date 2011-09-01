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

package org.tolven.web;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.naming.NamingException;

import org.tolven.core.entity.AccountUser;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocImage;
import org.tolven.doc.entity.DocXML;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.key.DocumentSecretKey;

/**
 *
 * @author John Churin
 */
public class DocAction  extends TolvenAction {

	private long documentId = 0;
	private String path;
	private String pathValue;
	private DocBase doc = null; 
	
    private String content;
    private DataModel photosModel;
    
    
    /** Creates a new instance of DocAction 
     * @throws NamingException 
     */
    public DocAction() throws NamingException {
//        setContent("This is some more content in B64 - We'll see how big it can be and if it can handle non-printable characters in a while" );
    }

 
    /**
     * Create a test document and persist it.
     * @return "success" if successful
     */
    public String create() throws NamingException {
    	doc = new DocBase();
    	doc.setMediaType( "text/plain" );
        String kbeKeyAlgorithm = getTolvenPropertiesBean().getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
        int kbeKeyLength = Integer.parseInt(getTolvenPropertiesBean().getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
        doc.setAsEncryptedContentString(content, kbeKeyAlgorithm, kbeKeyLength);
    	getDocBean().createDocument( doc, getSessionTolvenUserId(), getSessionAccountId() );
    	return "success";
    }

    /**
     * Set the current document. This requires the document to exist.
     * @param doc
     */
    public void setDoc( DocBase doc ) {
    	this.doc = doc;
    	documentId = doc.getId();
    }

    /**
     * Get the current document, if any
     * @return
     */
    public DocBase getDoc() throws NamingException {
		if (doc==null) {
			doc = getDocBean().findDocument(getDocumentId());
			TolvenLogger.info( "[getDocXML] id=" + getDocumentId(), DocAction.class);
			if (doc==null) {
				TolvenLogger.info( "No Document Found", DocAction.class);
				return null;
			}
			// Has to be for this account or it's not found.
			if (getSessionAccountId()!=doc.getAccount().getId()) {
				TolvenLogger.info( "Document not owned by this account", DocAction.class);
				return null;
			}
		}
		return doc;
	}

    /**
     * Type-safe method to return the current XML-based document, if any.
     * @return
     * @throws Exception
     */
    public DocXML getDocXML( ) throws Exception {
        DocBase d = getDoc();
        if (d==null) return null;
        if (!(d instanceof DocXML)) {
            TolvenLogger.info( "Document is not CCR " + d.getId() + " Class: " + d.getClass().getName(), DocAction.class);
            return null;
        }
        return (DocXML) d;
    }

    /**
     * Type-safe method to return the current XML-based document, if any.
     * @return
     * @throws Exception
     */
    public String getDocXMLContentString( ) throws Exception {
        DocXML d = getDocXML();
        if (d==null) return null;
        AccountUser activeAccountUser = getActivationBean().findAccountUser(this.getSessionAccountUserId());
        return getDocProtectionBean().getDecryptedContentString(d, activeAccountUser, getUserPrivateKey());
    }

	public long getDocumentId() {
    	if (documentId==00) {
    		TolvenLogger.info( "Get documentId from request: ", DocAction.class);
	    	Map<String,Object> reqMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
	    	MenuAction menuAction = (MenuAction) reqMap.get( "menu");
//	    	if (menuAction==null) menuAction = new MenuAction();
	    	Map<String, Long> keys = menuAction.getTargetMenuPath().getNodeValues();
	    	documentId = keys.get("detail");
    	}
    	return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

    /**
     * Return the specified photo
     */
    public DocImage getPhoto() throws NamingException {
    	// Get the familt we're looking for
    	long id = getDocumentId();
    	return getDocBean().findImage( id, getSessionAccountId() );
    }
    
    public List<DocImage> getPhotos() throws NamingException {
    	List<DocImage> rslt = getDocBean().findImages(getSessionAccountId(), 100, 0, "id", "ASC");
        return rslt;
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getAuthor() {
        return getSessionTolvenUserId();
	}

	/**
	 * Evaluate a path expression against the currently selected document, if present.
	 * @return
	 * @throws Exception 
	 */
	public String evaluatePath( ) throws Exception {
		DocXML doc = getDocXML( );
		if (doc != null) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			Application app = ctx.getApplication();
			setPathValue((String) app.evaluateExpressionGet(ctx, getPath(), String.class ));
		}
		return "success";
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPathValue() {
		return pathValue;
	}

	public void setPathValue(String pathValue) {
		this.pathValue = pathValue;
	}

	public long getDocumentCount( ) throws NamingException {
		return getDocBean().countDocuments( getSessionAccountId());
	}

	public long getXMLDocumentCount( ) throws NamingException {
		return getDocBean().countXMLDocuments( getSessionTolvenUserId());
	}

	public DataModel getPhotosModel() throws NamingException {
		if (photosModel==null) {
			photosModel = new ListDataModel();
			photosModel.setWrappedData(getDocBean().findImages(getTop().getAccountId(), 100, 0, "id", "ASC"));
		}
		return photosModel;
	}

	public void setPhotosModel(DataModel photosModel) {
		this.photosModel = photosModel;
	}
	
	public String selectUserLikeness( ) throws NamingException {
		DataModel model = getPhotosModel();
		DocImage doc = (DocImage) model.getRowData();
		getTop().getAccountUser().getUser().setLikeness(doc);
		return "success";
	}
	
	public String selectNoUserLikeness( ) {
		getTop().getAccountUser().getUser().setLikeness(null);
		return "success";
	}

	public int getPhotoCount( ) throws NamingException {
		List<Object> obj = (List<Object>) getPhotosModel().getWrappedData();
		return obj.size();
	}
}
