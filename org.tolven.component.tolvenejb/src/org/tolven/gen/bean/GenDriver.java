/*
 *  Copyright (C) 2007 Tolven Inc 
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
package org.tolven.gen.bean;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.tolven.ccr.ex.ContinuityOfCareRecordEx;
import org.tolven.core.TolvenPropertiesLocal;
import org.tolven.doc.TolvenMessageSchedulerLocal;
import org.tolven.doc.XMLLocal;
import org.tolven.doc.bean.TolvenMessage;
import org.tolven.gen.FamilyGeneratorLocal;
import org.tolven.gen.GeneratorQueueLocal;
import org.tolven.gen.entity.FamilyMember;
import org.tolven.gen.entity.FamilyUnit;
import org.tolven.gen.model.GenMedicalCCR;
import org.tolven.logging.TolvenLogger;
import org.tolven.security.AccountProcessingProtectionLocal;

/**
 * Control the Data Generator process. This process starts when a client queues a message to the 
 * generator asking it to generate patients for an account. For each family created, the generator
 * then queues another message to generate data for that family. Thus, if 10 families are created,
 * then 10 separate activities are initiated. In any case, all processes return immediately and can
 * execute in parallel. 
 * @author John Churin
 */

@MessageDriven
public class GenDriver implements MessageListener {

    @EJB
    private XMLLocal xmlBean;

    @EJB
    private FamilyGeneratorLocal familyGen;

    @EJB
    private AccountProcessingProtectionLocal accountProcessingProctectionLocal;

    @EJB
    private TolvenPropertiesLocal propertiesBean;

    @EJB
    private TolvenMessageSchedulerLocal tmSchedulerBean;

    @EJB
    private GeneratorQueueLocal generatorQueueBean;

    @Resource
    private MessageDrivenContext ctx;

    private Logger logger = Logger.getLogger(GenDriver.class);

    //    private JAXBContext jc = null;
    //
    //  public String marshall( ContinuityOfCareRecord ccr ) throws JAXBException {
    //      if (jc==null) {
    //          jc = JAXBContext.newInstance( "org.tolven.ccr", GenDriver.class.getClassLoader() );
    //      }
    //        JAXBElement<ContinuityOfCareRecord> root = new JAXBElement<ContinuityOfCareRecord>(new QName( "urn:astm-org:CCR", "ContinuityOfCareRecord"), ContinuityOfCareRecord.class, ccr);
    //        Marshaller m = jc.createMarshaller();
    //        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
    //        StringWriter result = new StringWriter( 10000 ); 
    //        m.marshal( root, result );
    //        return result.toString();
    //  }

    public void onMessage(Message msg) {
        try {
            // Generate a CHR account with many families
            if (((ObjectMessage) msg).getObject() instanceof GenControlCHRAccount) {
                GenControlCHRAccount control = (GenControlCHRAccount) ((ObjectMessage) msg).getObject();
                //TolvenLogger.info("MsgID: " + msg.getJMSMessageID() +  "  "+ control + " Principal:" + ctx.getCallerPrincipal().getName(), GenDriver.class);
                int count = 0;
                List<Serializable> familyControls = new ArrayList<Serializable>();
                while (count < control.getCount()) {
                    FamilyUnit family = familyGen.generateFamily(null, control.getNow());
                    GenControlFamily familyControl = new GenControlFamily();
                    familyControl.setFamilyUnit(family);
                    familyControl.setChrAccountId(control.getChrAccountId());
                    familyControl.setNow(control.getNow());
                    familyControl.setUserId(control.getUserId());
                    familyControl.setStartYear(control.getStartYear());
                    familyControls.add(familyControl);
                    count = count + familyControl.getFamilyUnit().getMembers().size();
                }
                generatorQueueBean.send(familyControls);
                TolvenLogger.info("***Done generating families***", GenDriver.class);
                return;
            }
            // Generate a PHR account with one family
            if (((ObjectMessage) msg).getObject() instanceof GenControlPHRAccount) {
                GenControlPHRAccount control = (GenControlPHRAccount) ((ObjectMessage) msg).getObject();
                TolvenLogger.info("MsgID: " + msg.getJMSMessageID() + " " + control, GenDriver.class);
                FamilyGenerator familyGen = new FamilyGenerator();
                FamilyUnit family = familyGen.generateFamily(control.getFamilyName(), control.getNow());
                GenControlFamily familyControl = new GenControlFamily();
                familyControl.setFamilyUnit(family);
                familyControl.setChrAccountId(control.getChrAccountId());
                familyControl.setNow(control.getNow());
                familyControl.setUserId(control.getUserId());
                familyControl.setStartYear(control.getStartYear());
                generatorQueueBean.send(familyControl);
                TolvenLogger.info("***Done generating PHR family: " + family.getFamilyName(), GenDriver.class);
                return;
            }
            // Generate data for one family
            if (((ObjectMessage) msg).getObject() instanceof GenControlFamily) {
                String OID = propertiesBean.getProperty("tolven.repository.oid");
                GenControlFamily control = (GenControlFamily) ((ObjectMessage) msg).getObject();
                TolvenLogger.info("start: " + control, GenDriver.class);
                //              TolvenUser user = em.find(TolvenUser.class, control.getUserId());
                //              Account chrAccount = em.find(Account.class, control.getChrAccountId());
                GenMedicalCCR generator = new GenMedicalCCR(control.getNow(), control.getStartYear());
                generator.setFamily(control.getFamilyUnit());
                String baseSeq = "" + control.getNow().getTime();
                int seq = 0;
                List<TolvenMessage> tms = new ArrayList<TolvenMessage>();
                for (FamilyMember member : control.getFamilyUnit().getMembers()) {
                    generator.setVp(member.getPerson());
                    seq++;
                    generator.setDocumentId(OID + "." + baseSeq + "." + seq);
                    // Create a CCR document which will be the target for this operation
                    ContinuityOfCareRecordEx ccr = generator.generate();
                    // Send the resulting document to tolven for persistence and rule processing
                    TolvenMessage tm = new TolvenMessage();
                    tm.setAccountId(control.getChrAccountId());
                    tm.setAuthorId(control.getUserId());
                    tm.setXmlName("ContinuityOfCareRecord");
                    tm.setXmlNS("urn:astm-org:CCR");
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    xmlBean.marshalCCR(ccr, output);
                    accountProcessingProctectionLocal.setAsEncryptedContent(output.toByteArray(), tm);
                    tms.add(tm);
                    //                  TolvenLogger.info(tm.getPayload());
                }
                if(!tms.isEmpty()) {
                    tmSchedulerBean.queue(tms);
                }
                TolvenLogger.info("Finished generating: " + control, GenDriver.class);
            }
        } catch (JMSException e) {
            ctx.setRollbackOnly();
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (Exception e) {
            ctx.setRollbackOnly();
            logger.error(e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
