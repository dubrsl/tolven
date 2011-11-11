package org.tolven.ccd.process;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.tolven.app.AppResolverLocal;
import org.tolven.core.AccountDAOLocal;
import org.tolven.core.entity.AccountUser;
import org.tolven.doc.entity.DocBase;
import org.tolven.logging.TolvenLogger;
import org.tolven.process.ComputeBase;
import org.tolven.security.key.DocumentSecretKey;
import org.tolven.trim.Act;
import org.tolven.trim.Compute.Property;
import org.tolven.trim.ex.TrimFactory;
import org.w3c.dom.Element;

public class CCDRouting  extends ComputeBase {
	private static final String HL7_NAMESPACE = "urn:hl7-org:v3";
	private static final String CCD_XSLT_WEBVIEW = "2.16.840.1.113883.3.27.1776";
	private static final String CCD_XSLT = "org.tolven.xslt.ccd";
	//private Logger logger = Logger.getLogger(this.getClass());
	private static final TrimFactory trimFactory = new TrimFactory();
	private Element extract;
	private boolean enabled;	

	@EJB
    private AccountDAOLocal accountBean;
	
	 @EJB
	private AppResolverLocal appResolver;
	   
	
	private String retrieveCCDXSLAccountProperty(String propertyName) {
		
		AccountUser accountUser = getAccountUser();		
		String pvalue = accountUser.getProperty().get(propertyName);		 
		return pvalue;
		
	}
		
	private String retrieveCCDXSLT(String xslt, String context) {
		AccountUser accountUser = getAccountUser();
		//Long accountUserId = accountUser.getId();
		Writer writer = new StringWriter();
		 try {
		        Source source = new StreamSource( new StringReader(xslt) );
				writer = new StringWriter();
				Result outputTarget = new StreamResult( writer);
				//logger.info(outputTarget.toString());
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				getAppResolver().setAccountUser(accountUser);
			    transformerFactory.setURIResolver(getAppResolver());
			    //transformerFactory.setAttribute("indent-number", 2);
			    Transformer transformer = transformerFactory.newTransformer(source);
				//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				//transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				DOMSource domsrc = new DOMSource(db.newDocument());
				if(context != null) {
					transformer.setParameter("context", context);
				}
				transformer.transform(domsrc, outputTarget);
				
				//transformer.transform(new DOMSource(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()), outputTarget);
		       // Response response = Response.ok().type(MediaType.APPLICATION_XML).entity(writer.toString()).build(); 
		        //return response;
	        } catch (Exception ex) {
	           // return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN).entity(ExceptionFormatter.toSimpleString(ex, "\\n")).build();
	        }	
		 return writer.toString();
	}
	
	private void submitDocument(String document) {
		AccountUser accountUser = getAccountUser();		
       
        long accountId = accountUser.getAccount().getId();
		long accountUserId = accountUser.getUser().getId();
		getDocumentBean().queueWSMessage(document.getBytes(), HL7_NAMESPACE, accountId, accountUserId);        
	}
	
	@Override
	public void compute() throws Exception {
		TolvenLogger.info( "Compute enabled=" + isEnabled(), CCDRouting.class);
		super.checkProperties();		
		if (isEnabled()) {
			//Generate the CCD
			//1. Set account properties
			// a. retrieve stored value ccd/xslt/WebViewLayout_CDA.xsl from property:tolven.xslt/2.16.840.1.113883.3.27.1776 .. for some reason for this it needs to be in: tolven.xslt/urn:hl7-org:v3
			////This just had to be set ahead of time, which has been taken care of
			//retrieveCCDXSLAccountProperty(CCD_XSLT_WEBVIEW);
			// b. retrieve stored value ccd/xslt/ccd.xsl from property: org.tolven.xslt.ccd
			//This just had to be set ahead of time, which has been taken care of
			String xslt = retrieveCCDXSLAccountProperty(CCD_XSLT);
			//2. Grab current patient for context
			//String context = getPatientPath(); //i.e. echr:patient-1129000
			//String context = "echr:patient-1129000";
			Act act = getAct();
			String context = act.getParticipations().get(1).getRole().getId().getIIS().get(0).getExtension();
			//3. getXSLT and create a document out of this
			String xmloutput = retrieveCCDXSLT(xslt, context);
			
			AccountUser accountUser = getAccountUser();		       
			DocBase xmlDoc = getDocumentBean().createNewDocument("text/xml", HL7_NAMESPACE, accountUser);
			String kbeKeyAlgorithm = getPropertyBean().getProperty(DocumentSecretKey.DOC_KBE_KEY_ALGORITHM_PROP);
		    int kbeKeyLength = Integer.parseInt(getPropertyBean().getProperty(DocumentSecretKey.DOC_KBE_KEY_LENGTH));
			xmlDoc.setAsEncryptedContentString(xmloutput, kbeKeyAlgorithm, kbeKeyLength);
			//4. attach this to the trim document
			getDocumentBean().createAttachment(getDocumentType().getDocument(), xmlDoc, "CCD", accountUser, getNow());
			
			//with xml output, Submit as a document with namespace urn:hl7-org:v3
			//submitDocument(xmloutput);
			
			// Disable the Compute since its job is done.
	    	for (Property property : getComputeElement().getProperties()) {
				if (property.getName().equals("enabled")) {
					property.setValue(Boolean.FALSE);
					break;
				}
			}
		}
		
		/*List<DocAttachment> attachments = getDocumentType().getAttachments();
		StringBuffer sb = new StringBuffer(200);
		if (attachments!=null) {
			for (DocAttachment attachment : attachments) {
				String text = "Attachment (" + attachment.getId() + ") " + attachment.getDescription();
				logger.info(text);
				sb.append(text);
				DocBase doc = attachment.getAttachedDocument();
				DocumentType documentType = doc.getDocumentType();
				documentType.setAccountUser(getAccountUser()); 
				Document dom = (Document) documentType.getParsed();
//				NodeList nodeList = getExtract().getChildNodes();
//				for ( int n = 0; n < nodeList.getLength(); n++) {
//					Node child = nodeList.item(n);
//					sb.append(" Node: [");
//					sb.append(child.getNodeName());
//					sb.append("]");
//				}
			}
		}
		ST value = trimFactory.createST();
		value.setValue(sb.toString());
		Act act = getAct();
		act.getText().setST(value);*/
	}
	
	public Element getExtract() {
		return extract;
	}
	public void setExtract(Element extract) {
		this.extract = extract;
	}

	 protected AccountDAOLocal getAccountBean() {
        if (accountBean == null) {
            String jndiName = "java:app/tolvenEJB/AccountDAOBean!org.tolven.core.AccountDAOLocal";
            try {
                InitialContext ctx = new InitialContext();
                accountBean = (AccountDAOLocal) ctx.lookup(jndiName);
            } catch (Exception ex) {
                throw new RuntimeException("Could not lookup " + jndiName);
            }
        }
        return accountBean;
    }
	 
	 protected AppResolverLocal  getAppResolver() {
	        if (appResolver == null) {
	            String jndiName = "java:app/tolvenEJB/AppResolverBean!org.tolven.app.AppResolverLocal";
	            try {
	                InitialContext ctx = new InitialContext();
	                appResolver = (AppResolverLocal) ctx.lookup(jndiName);
	            } catch (Exception ex) {
	                throw new RuntimeException("Could not lookup " + jndiName);
	            }
	        }
	        return appResolver;
	    }

	 public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
}

