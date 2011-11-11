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
 * @author anil
 * @version $Id: HL7DateFormatUtility.java,v 1.1 2009/04/06 00:50:26 jchurin Exp $
 */

package org.tolven.trim.ex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Purpose of this class is to provide a centralized Date formatting in HL7 Standards. 
 * 
 * @author Anil
 *
 */
public class HL7DateFormatUtility {

    private static final String HL7TSFormat4 = "yyyy";
    private static final String HL7TSFormatL12 = "yyyyMMddHHmm";
    private static final String HL7TSFormatL14 = "yyyyMMddHHmmss";
    private static final String HL7TSFormatL16 = "yyyyMMddHHmmssZZ";
    private static final String HL7TSFormatL8 = "yyyyMMdd";
    
    private static final String TSFormatL12 = "MM/dd/yyyy kk:mm aa";
    
    public static String formatHL7TSFormat4Date(Date time) {
        return getSimpleDateFormat(HL7TSFormat4).format(time);
    }
    public static String formatTSFormatL12(Date time) {
        return getSimpleDateFormat(TSFormatL12).format(time);
    }

    public static String formatHL7TSFormatL12Date(Date time) {
        return getSimpleDateFormat(HL7TSFormatL12).format(time);
    }

    public static String formatHL7TSFormatL14Date(Date time) {
        return getSimpleDateFormat(HL7TSFormatL14).format(time);
    }

    public static String formatHL7TSFormatL16Date(Date time) {
        return getSimpleDateFormat(HL7TSFormatL16).format(time);
    }

    public static String formatHL7TSFormatL8Date(Date time) {
        return getSimpleDateFormat(HL7TSFormatL8).format(time);
    }

    private static SimpleDateFormat getSimpleDateFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public static Date parseDate(String value) throws ParseException {
        if (value == null || value.length() == 0) {
            return null;
        } else if (value.length() == 4) {
            return getSimpleDateFormat(HL7TSFormat4).parse(value);
        } else if (value.length() == 8) {
            return getSimpleDateFormat(HL7TSFormatL8).parse(value);
        } else if (value.length() == 12) {
            return getSimpleDateFormat(HL7TSFormatL12).parse(value);
        } else if (value.length() == 14) {
            return getSimpleDateFormat(HL7TSFormatL14).parse(value);
        } else if (value.length() == 19) {
            return getSimpleDateFormat(HL7TSFormatL16).parse(value);
        } else {
            throw new RuntimeException("Unrecognized format: " + value);
        }

    }

}
