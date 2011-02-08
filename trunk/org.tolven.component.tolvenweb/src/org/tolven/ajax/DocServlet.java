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
package org.tolven.ajax;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.security.PrivateKey;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.tolven.core.ActivationLocal;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.DocumentLocal;
import org.tolven.doc.entity.DocBase;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.DocProtectionLocal;
import org.tolven.security.key.DocumentSecretKey;
import org.tolven.security.key.UserPrivateKey;
import org.tolven.sso.TolvenSSO;
import org.tolven.web.security.GeneralSecurityFilter;
import org.tolven.web.servlet.TolvenServlet;
import org.tolven.xml.Transformer;

import com.sun.image.codec.jpeg.ImageFormatException;

public class DocServlet extends TolvenServlet {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @EJB
    private DocumentLocal docBean;
    
    @EJB
    private TolvenPropertiesLocal propertyBean;
    
    @EJB
    private DocProtectionLocal docProtectionBean;
    
    @EJB
    protected ActivationLocal activationBean;
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(4096);
		//  Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
	    Writer writer=response.getWriter();
	    // Parse the request
		String returnTo = null;
		try {
			List<FileItem> items = upload.parseRequest(request);
			long id = 0;
			for (FileItem item : items) {
			    if (item.isFormField()) {
			        String name = item.getFieldName();
			        String value = item.getString();
			        if ("returnTo".equals(name)) returnTo = value;
			    } else {
			        String contentType = item.getContentType();
			        boolean isInMemory = item.isInMemory();
			        // TODO less than int bytes 
			        int sizeInBytes = (int)item.getSize();
			        AccountUser accountUser = (AccountUser) request.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
			        DocBase doc = docBean.createNewDocument( contentType, "", accountUser);
			        // Get the logged in user and set as the author
                    Object obj = request.getSession().getAttribute(GeneralSecurityFilter.ACCOUNT_ID);
                    if (obj == null)
                        throw new IllegalStateException(getClass() + ": Session ACCOUNT_ID is null");
					long accountId = (Long)obj;
                    obj = request.getSession().getAttribute(GeneralSecurityFilter.TOLVENUSER_ID);
                    if (obj == null)
                        throw new IllegalStateException(getClass() + ": Session TOLVENUSER_ID is null");
					long userId = (Long)obj;
				    String kbeKeyAlgorithm = propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
		            int kbeKeyLength = Integer.parseInt(propertyBean.getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));

			        if (isInMemory) {
			            doc.setAsEncryptedContent(item.get(), kbeKeyAlgorithm, kbeKeyLength);
			        } else {
			            InputStream uploadedStream = item.getInputStream();
			            byte[] b = new byte[sizeInBytes];
			            uploadedStream.read( b );
			            doc.setAsEncryptedContent(b, kbeKeyAlgorithm, kbeKeyLength);
			            uploadedStream.close();
			        }
				    docBean.finalizeDocument(doc);
			     }
//			    writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<html>\n" +
			    writer.write( "<html>\n" +
			    		"<head>" + 
			    			(returnTo==null? " " : 
			    				"<meta http-equiv=\"refresh\" content=\"0; url=" +
			    				returnTo +
			    				"\"/>"
			    			) + 
			    		"</head><body>\n" + id + "\n</body>\n</html>\n" );
			}
		} catch (FileUploadException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			e.printStackTrace();
		} finally {
    		request.setAttribute("activeWriter", writer);
//			writer.close();
		}
	}
	class StringInputStream extends InputStream {
		char[] string;
		int index = 0;
		public StringInputStream( String string ) {
			this.string = string.toCharArray();
		}
		
		@Override
		public int read() throws IOException {
			if (index < string.length) {
				return string[index++];
			}
			return -1;
		}
		
	}
/**
 * Open the appropriate XSLT for a document. The XSLT can be specific to an Account, account type, or
 * or system-wide. The schema-url controls which XSLT to fetch as does the locale of the AcountUser and and brand.
 * If the url-schema of the document is cda-medication, then the property name is <code>tolven.xslt.cda-medication</code>
 * 
 * @param doc
 * @param brand
 * @param servletContext
 * @return
 */	
protected InputStream openXSLT(AccountUser accountUser, DocBase doc, String brand,  ServletContext servletContext) {
	String key = "tolven.xslt/" + doc.getSchemaURI();
	String value = accountUser.getBrandedProperty(brand).get(key);
	if (value==null) return null;
	InputStream xsltStream = new StringInputStream( value );
	return xsltStream;
}
/**
 * Return XML content, optionally transformed.
 * @throws IOException 
 * @throws IOException 
 * @throws ImageFormatException 
 */	
protected void returnXML(DocBase doc, HttpServletRequest req, HttpServletResponse res) throws IOException {
	AccountUser accountUser = (AccountUser) req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
//	res.setContentType("text/xml");
//	res.setCharacterEncoding("UTF-8");
	try {
		InputStream xsltStream = openXSLT(accountUser, doc, req.getLocalAddr(), req.getSession().getServletContext());
		if (xsltStream==null) {
			returnText( doc, req, res );
			return;
		}
		Transformer transformer = new Transformer( xsltStream );
		String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
		Reader reader = new StringReader(docProtectionBean.getDecryptedContentString(doc, accountUser, TolvenSSO.getInstance().getUserPrivateKey(req, keyAlgorithm)));
		transformer.transform(reader, res.getWriter());
	} catch (Exception e) {
		throw new RuntimeException("Unable to render XML document " + doc.getId(), e);
	}
}

/**
 * Return XML content, optionally transformed.
 * @throws IOException 
 * @throws IOException 
 * @throws ImageFormatException 
 */	
protected void returnText(DocBase doc, HttpServletRequest req, HttpServletResponse res) throws IOException {
	TolvenLogger.debug( "Returning text", DocServlet.class );
	AccountUser accountUser = (AccountUser) req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
	res.setContentType("text/html");
	res.setCharacterEncoding("UTF-8");
    String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
	String content = docProtectionBean.getDecryptedContentString(doc, accountUser, TolvenSSO.getInstance().getUserPrivateKey(req, keyAlgorithm));
	Writer writer=res.getWriter();
//	writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\" >\n");
//	writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
//	writer.write("<body>\n");
	writer.write("<pre>\n");
	for (char c : content.toCharArray()) {
		if ('<'==c ) { 
			writer.write("&lt;");
		} else if ('>'==c) {
			writer.write("&gt;");
		} else {
			writer.write( c );
		}
	}
	writer.write("</pre>\n");
//	writer.write("</body>\n");
//	writer.write("</html>\n");
	writer.flush();
}
	
	/**
	 * Return an image scaled to fit the viewport while maintaining the aspect ratio and without clipping.
	 * The image will be centered in the window.
	 * @throws IOException 
	 * @throws ImageFormatException 
	 */	
protected void returnImage(DocBase doc, HttpServletRequest req, HttpServletResponse res) throws ImageFormatException, IOException {
	AccountUser accountUser = (AccountUser) req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
	// Look for dimension settings
	String widthString = req.getParameter("width");
	String heightString = req.getParameter("height");
	int targetWidth = (widthString==null ? 0 : Integer.parseInt(widthString));
	int targetHeight = (heightString==null ? 0 : Integer.parseInt(heightString));
    String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
    PrivateKey userPrivateKey = TolvenSSO.getInstance().getUserPrivateKey(req, keyAlgorithm);
	if (targetWidth==0 || targetHeight==0) {
		TolvenLogger.info( "Returning image", DocServlet.class );
		res.setContentType(doc.getMediaType());
		docProtectionBean.streamContent(doc, res.getOutputStream(), accountUser, userPrivateKey);
	} else {
		TolvenLogger.info( "Returning image as JPEG thumbnail", DocServlet.class );
		res.setContentType("image/jpeg");
		docProtectionBean.streamJPEGThumbnail(doc, targetWidth, targetHeight, res.getOutputStream(), accountUser, userPrivateKey);
	}
}

/**
 * Return an application file (pdf, zip, doc, etc) and let browser deal with it.
 * @throws IOException 
 * @throws ImageFormatException 
 */	
protected void returnApplication(DocBase doc, HttpServletRequest req, HttpServletResponse res) throws ImageFormatException, IOException {
	AccountUser accountUser = (AccountUser) req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
	TolvenLogger.info( "Returning application file", DocServlet.class );
	res.setHeader("Pragma", null);
	res.setHeader("Cache-Control", "max-age=1000");
	res.setContentType(doc.getMediaType());
    String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
	docProtectionBean.streamContent(doc, res.getOutputStream(), accountUser, TolvenSSO.getInstance().getUserPrivateKey(req, keyAlgorithm));
}

protected void returnContent(HttpServletRequest req, HttpServletResponse res) throws ImageFormatException, IOException {
	long docId = Long.parseLong( req.getParameter( "docId"));
	DocBase doc = docBean.findDocument(docId);
	TolvenLogger.info( "Download document id: " + docId + " Media type " + doc.getMediaType(), DocServlet.class);
    res.setHeader("Cache-Control", "no-cache");
	if (doc.getMediaType().startsWith("image/")) {
		returnImage( doc, req, res );
		return;
	}
	if (doc.getMediaType().startsWith("text/xml")) {
		returnXML( doc, req, res );
		return;
	}
	if (doc.getMediaType().startsWith("text/")) {
		returnText( doc, req, res );
		return;
	}
	if (doc.getMediaType().startsWith("application/")) {
		returnApplication( doc, req, res );
		return;
	}
}
	
protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    try
    {
    	TolvenLogger.info( "[DocServlet] doGet queryString: " + req.getQueryString(), DocServlet.class );
		String uri = req.getRequestURI();
    	TolvenLogger.info( "[DocServlet] doGet url: " + uri, DocServlet.class );
	    if (uri.endsWith("/document")) {
	    	returnContent(req, res);
	    	return;
	    }
    	// Get a path to the image to resize.
		// ImageIcon is a kludge to make sure the image is fully 
		// loaded before we proceed.
		long docId = Long.parseLong( req.getParameter( "docId"));
        Object obj = req.getSession().getAttribute(GeneralSecurityFilter.ACCOUNT_ID);
        if (obj == null)
            throw new IllegalStateException(getClass() + ": Session ACCOUNT_ID is null");
        long accountId = (Long)obj;
		// Calculate the target width and height based on scaling to the smallest of the two dimensions
		int targetWidth = Integer.parseInt(req.getParameter("width"));
		int targetHeight = Integer.parseInt(req.getParameter("height"));
		
		DocBase doc = docBean.findDocument(docId);
		if (doc.getAccount().getId()!=accountId) {
			throw new RuntimeException( "Permission denied to access document " + docId + " from account " + accountId);
		}
		// Output as JPEG, regardless of input format.
		res.setContentType("image/jpeg");
	    res.setHeader("Cache-Control", "no-cache");
        AccountUser activeAccountUser = (AccountUser) req.getAttribute(GeneralSecurityFilter.ACCOUNTUSER);
        String keyAlgorithm = propertyBean.getProperty(UserPrivateKey.USER_PRIVATE_KEY_ALGORITHM_PROP);
        docProtectionBean.streamJPEGThumbnail(doc, targetWidth, targetHeight, res.getOutputStream(), activeAccountUser, TolvenSSO.getInstance().getUserPrivateKey(req, keyAlgorithm));
	}
	catch(Exception e)
	{
		throw new ServletException( "Error in DocServlet", e);
	}
}

}
