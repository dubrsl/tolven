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
package org.tolven.gen.bean;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.tolven.ccr.ex.ContinuityOfCareRecordEx;
import org.tolven.doc.XMLLocal;
import org.tolven.gen.GeneratorLocal;
import org.tolven.gen.GeneratorQueueLocal;
import org.tolven.gen.GeneratorRemote;
import org.tolven.gen.PersonGenerator;
import org.tolven.gen.model.GenMedicalCCR;

/**
* Generate patients for a CHR account.
* @author John Churin
*/
@Stateless
@Local(GeneratorLocal.class)
@Remote(GeneratorRemote.class)
public class GeneratorBean implements GeneratorLocal, GeneratorRemote {

    @EJB
    private XMLLocal xmlBean;

    @EJB
    private PersonGenerator personGenBean;

    @EJB
    private GeneratorQueueLocal generatorQueueBean;
    
    public byte[] generateCCRXML(int startYear) {
        try {
            Date now = new Date();
            GenMedicalCCR generator = new GenMedicalCCR(now, startYear);
            generator.setVp(personGenBean.generatePerson());
            ContinuityOfCareRecordEx ccr = generator.generate();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            xmlBean.marshalCCR(ccr, output);
            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public GeneratorBean() {
    }

    /**
     * Queue the generation of one or more clinical data events
     * @param control
     */
    public void queueGeneration(GenControlBase control) {
        generatorQueueBean.send(control);
    }

}
