package org.tolven.doc.bean;

import java.util.Date;
import java.util.Properties;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.tolven.app.MessageProcessorLocal;
import org.tolven.doc.ProcessLocal;

@Stateless()
@Local(ProcessLocal.class)
public class ProcessBean implements ProcessLocal {
    private String messageProcessorNames[];
    private MessageProcessorLocal messageProcessors[];
    
    public final static String PROCESSOR_NAME = "processorJNDI";
	public final static String PROPERTY_FILE_NAME = "Evaluator.properties"; 
    
	private Logger logger = Logger.getLogger(this.getClass());

	/**
     * Find the list of message evaluators we are using.
     */
    public void initializeMessageProcessors() {
    	try {
			if (messageProcessors==null) {
				Properties properties = new Properties();
				properties.load(this.getClass().getResourceAsStream(PROPERTY_FILE_NAME));
				String processorNames = properties.getProperty(PROCESSOR_NAME);
				logger.info("Message processors: " + processorNames);
				messageProcessorNames = processorNames.split(",");
				messageProcessors = new MessageProcessorLocal[messageProcessorNames.length];
		        InitialContext ctx = new InitialContext();
		        if (messageProcessorNames.length == 0) {
					logger.warn("No message processors installed");
		        }
		    	for (int i = 0; i < messageProcessorNames.length; i++ ) {
		    		String messageProcessor = messageProcessorNames[i];
		    	    Object obj = ctx.lookup(messageProcessor);
		            messageProcessors[i] = (MessageProcessorLocal) obj;
		    	}
			}
		} catch (Exception e) {
			throw new RuntimeException( "Error initializing message processors", e);
		}
    }
    
    @Override
    public long processMessage(byte[] payload, String mediaType, String xmlns, long accountId, long userId, Date now) {
        TolvenMessage tm = new TolvenMessage();
        tm.setAccountId(accountId);
        tm.setAuthorId(userId);
        tm.setXmlNS(xmlns);
        tm.setPayload(payload);
        tm.setMediaType(mediaType);
        tm.setDecrypted(true);
        dispatchToMessageProcessors(tm, now);
        return tm.getDocumentId();
    }

    public void dispatchToMessageProcessors(TolvenMessage tm, Date now) {
    	initializeMessageProcessors();
    	for (MessageProcessorLocal messageProcessor : messageProcessors ) {
			logger.info("dispatching to: " + messageProcessor);
    		messageProcessor.process( tm, now );
    	}
    }
}
