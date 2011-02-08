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
package org.tolven.gen;

import javax.jms.JMSException;

import org.tolven.gen.bean.GenControlBase;

/**
 * Services to generate patients using the data generator.
 * @author John Churin
 *
 */
public interface GeneratorLocal {
       

   /**
    * Queue the generation of one or more clinical data events
    * @param invitation
    * @throws JMSException
    */
   public void queueGeneration(GenControlBase control) throws JMSException;

   public byte[] generateCCRXML(  int startYear );
}
