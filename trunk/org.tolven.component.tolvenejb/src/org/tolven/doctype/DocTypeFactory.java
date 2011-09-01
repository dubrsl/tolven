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
 * @version $Id: DocTypeFactory.java,v 1.6 2009/06/02 03:13:59 jchurin Exp $
 */  

package org.tolven.doctype;

import java.beans.Beans;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocXML;
/**
 * Create and associate a DocumentType with a Document. A DocumentType is created for each Document
 * and accompanies that document while in memory - thus it only contains transient properties.
 * @author John Churin
 */
public class DocTypeFactory {
	TolvenPropertiesLocal propertyBean;
	public static final String  DOCUMENT_TYPE_PROPERTY = "documentTypeClassName";
	
    private List<DocumentType> documentTypes;
    
    public List<DocumentType> getDocumentTypes() {
		return documentTypes;
	}
    
	public void addDocumentType(DocumentType documentType) {
		if (this.documentTypes==null) {
			this.documentTypes = new ArrayList<DocumentType>();
		}
		this.documentTypes.add(documentType);
	}
	
	/**
	 * Initialize the list of DocumentTypes. We also add a default DocumentType to handle
	 * generic documents (images, etc) that require no special processing.
	 */
	public void initDocumentTypes() {
    	try {
			if (documentTypes==null) {
				Properties properties = new Properties();
				String propertyFileName = this.getClass().getSimpleName()+".properties"; 
				properties.load(this.getClass().getResourceAsStream(propertyFileName));
				String docTypeNames = properties.getProperty(DOCUMENT_TYPE_PROPERTY);
				// Clear previous DocType list
				documentTypes = null;
				// Now parse and process each DocType name
				for (String documentTypeName : docTypeNames.split("\\,")) {
					DocumentType instance = (DocumentType) Beans.instantiate(this.getClass().getClassLoader(), documentTypeName);
					if (!Beans.isInstanceOf(instance, DocumentType.class)) {
						throw new RuntimeException( "DocType class must be a subclass of DocumentType");
					}
					addDocumentType( instance );
				}
				// Add a generic processor at the end
				addDocumentType( new GenericDocumentType());
			}
		} catch (Exception e) {
			throw new RuntimeException( "Error initializing DocTypeFactory", e);
		}
	}
	
	/**
	 * Ask each document type to determine if the supplied document is a document it can handle.
	 * When found, set the documentType in the document. This method should be called whenever a document is 
	 * retrieved from the database.
	 * @param doc
	 * @return DocumentType applicable for the document or null if no DocumentType is found.
	 */
    public DocumentType associateDocumentType( DocBase doc ) {
    	DocumentType documentType;
    	if (doc.getDocumentType()==null) {
    		String mediaType = doc.getMediaType();
    		String namespace = null;
    		if (doc instanceof DocXML ) {
    			namespace = ((DocXML)doc).getXmlNS();
    		} else {
    			namespace = doc.getSchemaURI();
    		}
    		documentType = createDocumentType( mediaType, namespace);
    		doc.setDocumentType(documentType);
    	} else {
    		documentType = doc.getDocumentType();
    	}
    	documentType.setDocument(doc);
		return documentType;
    }
    
	/**
	 * Ask each document type to determine if the supplied document is a document it can handle.
	 * @param mediaType - media type of document
	 * @param namespace - namespace applicable to mediaType. For example, for text/xml mediaType, XML namespace url.
	 * @return DocumentType applicable for the document or null if no DocumentType is found.
	 */
    public DocumentType createDocumentType( String mediaType, String namespace ) {
		initDocumentTypes();
        for (DocumentType documentType : documentTypes) {
        	if (documentType.matchDocumentType( mediaType, namespace )) {
        		DocumentType instance;
        		try {
					instance = (DocumentType) Beans.instantiate(this.getClass().getClassLoader(), documentType.getClass().getName());
					} catch ( Exception e) {
						throw new RuntimeException( "Unable to instantiate DocumentType " + documentType.getClass().getName() , e);
					}
        		return instance;
        	}
        }
        return null;
    }
    
    /**
     * Create a document type instance, then ask it to create a new document. At that point, the
     * documentType and document are one to one (a documentType instance is not reused for other documents).
     * @param mediaType
     * @param namespace
     * @return
     */
    public DocBase createNewDocument( String mediaType, String namespace ) {
    	DocumentType documentType = createDocumentType( mediaType, namespace );
    	DocBase doc = documentType.createNewDocument(mediaType, namespace);
    	return doc;
    }

}
