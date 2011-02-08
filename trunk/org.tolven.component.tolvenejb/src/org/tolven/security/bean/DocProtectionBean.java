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
package org.tolven.security.bean;

import static org.apache.commons.codec.binary.Base64.encodeBase64Chunked;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Base64;
import org.tolven.core.KeyUtility;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.entity.DocBase;
import org.tolven.doc.entity.DocumentSignature;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.CertificateHelper;
import org.tolven.security.DocContentSecurity;
import org.tolven.security.DocProtectionLocal;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * This class protects the DocBase content by handling its encryption and decryption.
 * 
 * @author Joseph Isaac
 * 
 */
@Stateless()
@Local(DocProtectionLocal.class)
//TODO This class should probably be in the same package as DocBase in order to protect the DocBase methods from public view
public class DocProtectionBean implements DocProtectionLocal {

    public static String VERIFIED = "VERIFIED";
    public static String VERIFICATION_FAILED = "WARNING: VERIFICATION FAILED";

    @PersistenceContext
    private EntityManager em;

    @EJB private TolvenPropertiesLocal propertiesBean;
    
    /**
     * Currently assumes all content is encrypted and only the authorized loggedInUser will succeed in getting the readable content
     * This method calls decryption each time it is called.
     * Decryption takes CPU time and it requires access to security policy which means
     * the caller must have permission to call this method.
     * @param encryptedContent
     * @return
     */
    public byte[] getDecryptedContent(DocContentSecurity doc, AccountUser activeAccountUser, PrivateKey userPrivateKey) {
        //        TolvenLogger.info("DocProtectedBean.getDecryptedContent", DocProtectionBean.class);
        if (doc.getContent() == null)
            return doc.getContent();
        try {
            PrivateKey accountPrivateKey = KeyUtility.getAccountPrivateKey(activeAccountUser, userPrivateKey);
            //            TolvenLogger.info(getClass() + " Decryption AccountPrivateKey=" + activeAccountPrivateKey, DocProtectionBean.class);
            if (doc.getDocumentSecretKey() == null) {
                //TODO: For backward compatibility, we no longer throw an exception here, since older accounts never had a documenSecretKey and
                // were thus never encrypted
                //throw new RuntimeException("Content cannot be decrypted without a documentSecretKey");
                TolvenLogger.info(getClass() + " No DocumentSecretKey found for doc id=" + doc.getId(), DocProtectionBean.class);
                return doc.getContent();
            }
            SecretKey docSecretKey = doc.getDocumentSecretKey().getSecretKey(accountPrivateKey);
            Cipher cipher = Cipher.getInstance(docSecretKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, docSecretKey);
            return cipher.doFinal(doc.getContent());
        } catch (Exception ex) {
            ex.printStackTrace();
            return "THIS DOCUMENT CANNOT BE DECRYPTED".getBytes();
        }
    }

    /**
     * Return the contents of the document as base64 encoded.
     * This method calls decryption each time it is called.
     * Decryption takes CPU time and it requires access to security policy which means
     * the caller must have permission to call this method.
     */
    public String getDecryptedContentB64(DocContentSecurity doc, AccountUser activeAccountUser, PrivateKey userPrivateKey) {
        return new String(Base64.encodeBase64(getDecryptedContent(doc, activeAccountUser, userPrivateKey)));
    }

    /**
     * Return the content as a string. This method calls decryption each time it is called.
     * Decryption takes CPU time and it requires access to security policy which means
     * the caller must have permission to call this method.
     * @return
     */
    public String getDecryptedContentString(DocContentSecurity doc, AccountUser activeAccountUser, PrivateKey userPrivateKey) {
        byte[] c = getDecryptedContent(doc, activeAccountUser, userPrivateKey);
        if (c == null)
            return null;
        return new String(c);
    }

    /**
     * Sign the clear text content of DocContentSecurity and return a DocumentSignatute
     * @param doc
     * @param activeAccountUser
     * @return
     */
    public DocumentSignature sign(DocBase doc, AccountUser activeAccountUser, PrivateKey privateKey, X509Certificate x509Certificate) {
        if (doc.getContent() == null) {
            return null;
        }
        if(privateKey == null) {
            throw new RuntimeException("A private key is required to sign a document");
        }
        if(x509Certificate == null) {
            throw new RuntimeException("An X509 Certificate is required to sign a document");
        }
        String signatureAlgorithm = propertiesBean.getProperty(DocumentSignature.DOC_SIGNATURE_ALGORITHM_PROP);
        try {
            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initSign(privateKey);
            byte[] document = getDecryptedContent(doc, activeAccountUser, privateKey);
            signature.update(document);
            DocumentSignature documentSignature = new DocumentSignature();
            documentSignature.setDocBase(doc);
            documentSignature.setSignature(signature.sign());
            documentSignature.setSignatureAlgorithm(signatureAlgorithm);
            documentSignature.setCertificate(x509Certificate.getEncoded());
            documentSignature.setUser(activeAccountUser.getUser());
            documentSignature.setTimstamp(new Date());
            em.persist(documentSignature);
            return documentSignature;
        } catch (Exception ex) {
            throw new RuntimeException("Could not sign documentId: " + doc.getId());
        }
    }
    
    public String getDocumentSignaturesString(DocBase docBase, AccountUser activeAccountUser, PrivateKey userPrivateKey) {
        String select = "SELECT sig FROM DocumentSignature sig WHERE sig.docBase = :docBase";
        Query query = em.createQuery(select);
        query.setParameter("docBase", docBase);
        List<DocumentSignature> documentSignatures = query.getResultList();
        StringBuffer buff = new StringBuffer();
        X509Certificate x509Certificate = null;
        for (DocumentSignature documentSignature : documentSignatures) {
            x509Certificate = CertificateHelper.getX509Certificate(documentSignature.getCertificate());
            boolean verified = false;
            verified = verify(documentSignature, x509Certificate, activeAccountUser, userPrivateKey);
            if (verified) {
                buff.append(VERIFIED);
            } else {
                buff.append(VERIFICATION_FAILED);
            }
            buff.append("\nDate signed: ");
            buff.append(documentSignature.getTimestamp());
            buff.append("\n  -- Signature -- \n");
            buff.append(new String(encodeBase64Chunked(documentSignature.getSignature())));
            buff.append("\n  -- End Signature --\n");
            buff.append("\n");
            buff.append("Signed by: ");
            buff.append(x509Certificate.toString());
            buff.append("\n");
        }
        return buff.toString();
    }

    /**
     * Verify the document signature belongs to aPublicKey using aDecryptionKey
     * to decrypt the document
     * @param aPublicKey
     * @param aDecryptionKey
     * @return
     */
    public boolean verify(DocumentSignature documentSignature, X509Certificate x509Certificate, AccountUser activeAccountUser, PrivateKey userPrivateKey) {
        try {
            Signature signature = Signature.getInstance(documentSignature.getSignatureAlgorithm());
            signature.initVerify(x509Certificate.getPublicKey());
            byte[] document = getDecryptedContent(documentSignature.getDocBase(), activeAccountUser, userPrivateKey);
            signature.update(document);
            return signature.verify(documentSignature.getSignature());
        } catch (Exception ex) {
            throw new RuntimeException("Could not verify the signature", ex);
        }
    }

    /**
     * Stream content to the specified output stream
     * @return
     */
    public void streamContent(DocContentSecurity doc, OutputStream stream, AccountUser activeAccountUser, PrivateKey userPrivateKey) {
        byte[] b = getDecryptedContent(doc, activeAccountUser, userPrivateKey);
        try {
            stream.write(b);
        } catch (IOException ex) {
            throw new RuntimeException("Could not write to stream", ex);
        }
    }
 
	/**
	 * Create a JPEG thumbnail of the underlying image and encode it to the output stream provided.
	 * The aspect ratio of the underlying image is retained. As a result, the thumbnail is scaled to fit in
	 * the specified rectangle. Whitespace is added if the image does not match the aspect ratio of the rectangle. 
	 * @param targetWidth
	 * @param targetHeight
	 * @param stream
	 */
	static public void streamJPEGThumbnail(byte[] unencryptedContent, int targetWidth, int targetHeight, OutputStream stream) {
		Image sourceImage = new ImageIcon(Toolkit.getDefaultToolkit().createImage(unencryptedContent)).getImage();
		float hscale = ((float)targetWidth)/((float)sourceImage.getWidth(null));
		float vscale = ((float)targetHeight)/((float)sourceImage.getHeight(null));
		float scale = 1.0f;
		if ( hscale < scale) scale = hscale;
		if (vscale < scale) scale = vscale;
		int newWidth = (int)(sourceImage.getWidth(null)*scale);
		int newHeight = (int)(sourceImage.getHeight(null)*scale);
		Image resizedImage = scaleImage(sourceImage,newWidth,newHeight);
	    BufferedImage bufferedImage = toBufferedImage(resizedImage, targetWidth, targetHeight);
		
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(stream);
		try {
            encoder.encode(bufferedImage);
        } catch (Exception ex) {
            throw new RuntimeException("Could not encode bufferedImage", ex);
        }
	}
	
	/**
	 * Create a new scaled image using a filter.
	 * @param sourceImage
	 * @param width The width of the resulting picture
	 * @param height The height of the resulting picture
	 * @return The Image
	 */
	static public Image scaleImage(Image sourceImage, int width, int height) {
	    ImageFilter filter = new ReplicateScaleFilter(width,height);
	    ImageProducer producer = new FilteredImageSource(sourceImage.getSource(),filter);
	    return Toolkit.getDefaultToolkit().createImage(producer);
	}
	/**
	 * Place the resulting scaled image into the output buffer
	 * @param image The scaled image
	 * @param windowWidth The desired window width to return
	 * @param windowHeight The desired window hight to return
	 * @return A Buffered image, ready for output
	 */
	static public BufferedImage toBufferedImage(Image image, int windowWidth, int windowHeight) {
	    image = new ImageIcon(image).getImage();
	    BufferedImage bufferedImage = new BufferedImage(windowWidth, windowHeight,BufferedImage.TYPE_INT_RGB);
	    Graphics g = bufferedImage.createGraphics();
	    g.setColor(Color.WHITE);
	    g.fillRect(0,0,windowWidth,windowHeight);
	    // Center image in window
	    int hOffset = (windowWidth-image.getWidth(null))/2;
	    int vOffset = (windowHeight-image.getHeight(null))/2;
	    g.drawImage(image,hOffset,vOffset,null);
	    g.dispose();
	    return bufferedImage;
	}

    /**
     * Create a JPEG thumbnail of the underlying image and encode it to the output stream provided.
     * The aspect ratio of the underlying image is retained. As a result, the thumbnail is scaled to fit in
     * the specified rectangle. Whitespace is added if the image does not match the aspect ratio of the rectangle. 
     * @param targetWidth
     * @param targetHeight
     * @param stream
     */
    public void streamJPEGThumbnail(DocContentSecurity doc, int targetWidth, int targetHeight, OutputStream stream, AccountUser activeAccountUser, PrivateKey userPrivateKey) {
        streamJPEGThumbnail(getDecryptedContent(doc, activeAccountUser, userPrivateKey), targetWidth, targetHeight, stream);
    }

}
