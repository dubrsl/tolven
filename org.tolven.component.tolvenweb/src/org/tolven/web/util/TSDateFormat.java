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
 * @author Anil
 * @version $Id: TSDateFormat.java,v 1.3 2009/10/27 08:35:14 jchurin Exp $
 */
package org.tolven.web.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolven.logging.TolvenLogger;

/**
 * Returns DateFormat pattern for Client script and for server side components, based on Locale and other date parameters.
 * 
 * 
 * Time-SHORT,Date-SHORT 		: 1/17/09 3:21 PM
 * Time-MEDIUM,Date-MEDIUM  	: Jan 17, 2009 3:23:14 PM
 * Time-LONG,Date-LONG			: January 17, 2009 3:25:11 PM EST
 * Time-FULL,Date-FULL  		: Saturday, January 17, 2009 3:23:55 PM EST
 * 
 * 		
 */
public class TSDateFormat  {
	
	private Locale locale = null;
	private int dateStyleType = -1;
	private int timeStyleType = -1;
	private String pattern = null;
	
	
	private String clientScriptPattern;
	private String simpleDatePattern;
	private String hl7DatePattern;
	
	/**
	 * Possible Expression matches from SimpleDateFormat
	 */
	private String dateRegEx = "yyyy|yy|MMMMM|MMM|MM|M|EEEE|EEE|E|dd|d|hh|h|HH|H|mm|ss|a";

	/**
	 * ClientScriptPattern and SimpleDatePattern will be computed based on DateStyle and TimeStyle.
	 * 
	 * - First Evaluates Java SimpleDateFormat pattern based on arguments.
	 * - Next based on simpleDateFormat pattern it computes that pattern for Client Calendar script
	 * 
	 * @param aLocale
	 * @param aDateStypeType
	 * @param aTimeStypeType
	 */
	public TSDateFormat(Locale aLocale, String aDateStypeType, String aTimeStypeType)
	{
		locale = aLocale;
		
		Map<String,Integer> dateTypeMappings = buildFormatTypeMappings(); 
		if (aDateStypeType != null && aDateStypeType.length() > 0) 
		{
			dateStyleType =   dateTypeMappings.get(aDateStypeType.toLowerCase());
		}
		else
		{
			throw new IllegalArgumentException("dateStyleType cannot be null");
		}
		
		if (aTimeStypeType != null && aTimeStypeType.length() > 0)
		{
			timeStyleType = dateTypeMappings.get(aTimeStypeType.toLowerCase());
		}
		setTSPattern();
	}
	
	
	private void setTSPattern()
	{
		SimpleDateFormat lFormat;
		if (timeStyleType == -1)
		{
			lFormat = (SimpleDateFormat)SimpleDateFormat.getDateInstance(dateStyleType, locale);
		}
		else
		{
			lFormat = (SimpleDateFormat)SimpleDateFormat.getDateTimeInstance(dateStyleType,timeStyleType, locale);
		}
		
		simpleDatePattern = lFormat.toPattern();
		// However, in Tolven, all years are 4-digit
		if (!simpleDatePattern.contains("yyyy")) {
			simpleDatePattern = simpleDatePattern.replace("yy", "yyyy");
		}

		// Evaluate Calendar Script pattern.
		Pattern lPattern = Pattern.compile(dateRegEx);
		Matcher lMatcher = lPattern.matcher(simpleDatePattern);
		
		Map<String,String> lReplaceChars  =  buildReplaceCharSet();
		StringBuffer lClientScriptPattern = new StringBuffer();
		while (lMatcher.find())
		{
			lMatcher.appendReplacement(lClientScriptPattern, lReplaceChars.get(lMatcher.group()));
		}

		clientScriptPattern = lClientScriptPattern.toString();
		
		Map<String,String> lHl7FormatMaps = buildSimpleFormatHl7Mappings();
		// Evaluate Hl7 Format.
		StringBuffer lHl7Format = new StringBuffer();
		for(String lHl7RegEx : buildSimpleFormatHl7RegEx())
		{
			Pattern lPattern2 = Pattern.compile(lHl7RegEx);
			Matcher lMatcher2 = lPattern2.matcher(simpleDatePattern);
			if (lMatcher2.find())
			{
				String lval = lHl7FormatMaps.get(lHl7RegEx);
				if ( lval != null && lval.length() > 0)
				{
					lHl7Format.append(lval);	
				}
			}
		}
		
		hl7DatePattern = lHl7Format.toString();
		
		//TolvenLogger.info("SimpleDateFormat - " + simpleDatePattern, TSDateFormat.class);
		//TolvenLogger.info("Calendar Format  - " + clientScriptPattern, TSDateFormat.class);
		//TolvenLogger.info("Hl7Fomat 		 - " + hl7DatePattern, TSDateFormat.class);		
	}
	
	public String getSimpleDatePattern()
	{
		return simpleDatePattern;
	}
	public SimpleDateFormat getSimpleDateFormat()
	{
		return new SimpleDateFormat(simpleDatePattern);
	}
	
	public String getClientScriptPattern()
	{
		return clientScriptPattern;
	}
	
	public SimpleDateFormat getClientScriptFormat()
	{
		return new SimpleDateFormat(clientScriptPattern);
	}
	
	
	public String getHl7DatePattern()
	{
		return hl7DatePattern; 
	}
	public SimpleDateFormat getHl7DateFormat()
	{
		return new SimpleDateFormat(hl7DatePattern); 
	}
	
	public SimpleDateFormat getHl7BaseStandardFormat()
	{
		return new SimpleDateFormat("yyyyMMddHHmmssZZ");
	}
	
	

	
	private Map<String, String> buildReplaceCharSet()
	{
		Map<String,String> lReplaceChars = new HashMap();
		
		lReplaceChars.put("yyyy", "%Y");
		lReplaceChars.put("yy", "%Y");	// Even if Java says two-digit year, use 4 digits year (%Y) in calendar.
		
		// January
		lReplaceChars.put("MMMMM", "%B");
		// Jan
		lReplaceChars.put("MMM", "%b");
		// 01
		lReplaceChars.put("MM", "%m");
		// 01
		lReplaceChars.put("M", "%m");
		
		
		// Wednesday
		lReplaceChars.put("EEEE", "%A");
		// Wed
		lReplaceChars.put("EEE", "%a");		
		lReplaceChars.put("E", "%a");		

		// 01
		lReplaceChars.put("dd", "%d");
		//1
		lReplaceChars.put("d", "%e");

		
		lReplaceChars.put("hh", "%I");		
		lReplaceChars.put("h", "%l");
		lReplaceChars.put("HH", "%H");
		lReplaceChars.put("H", "%k");
		
		// Minutes
		lReplaceChars.put("mm", "%M");
		//Seconds
		lReplaceChars.put("ss", "%S");

		
		// APM/PM
		lReplaceChars.put("a", "%P");		

		// EST - Not found
		// lReplaceChars.put("z", "??");		

		
		return lReplaceChars;
		
	}
	
	private Map<String,Integer> buildFormatTypeMappings()
	{
		Map<String,Integer> lMap = new HashMap();
		
		lMap.put("short", DateFormat.SHORT);
		lMap.put("medium", DateFormat.MEDIUM);
		lMap.put("long", DateFormat.LONG);
		lMap.put("full", DateFormat.FULL);		
		
		return lMap;
		
	}
	
	private List<String> buildSimpleFormatHl7RegEx()
	{
		List<String> lHl7RegEx = new ArrayList<String>();
		
		lHl7RegEx.add("yyyy|yy");
		lHl7RegEx.add("MMMMM|MMM|MM|M");
		lHl7RegEx.add("dd|d");
		lHl7RegEx.add("hh|h|HH|H");		
		lHl7RegEx.add("mm");
		lHl7RegEx.add("ss");		
		lHl7RegEx.add("a");
		lHl7RegEx.add("z");		
		
		return lHl7RegEx;
		
	}
	
	private Map<String,String> buildSimpleFormatHl7Mappings()
	{
		Map<String,String> lMap = new HashMap<String,String>();
		
		lMap.put("yyyy|yy", "yyyy");
		lMap.put("MMMMM|MMM|MM|M", "MM");
		lMap.put("dd|d","dd");
		lMap.put("hh|h|HH|H", "HH");		
		lMap.put("mm", "mm");		
		lMap.put("ss", "ss");		
		lMap.put("z", "z");		
		
		return lMap;
		
	}	
	
	
	public static void main (String[] args)
	{
		TSDateFormat lFormat1 = new TSDateFormat(Locale.UK, "short", "medium");
		System.out.println(lFormat1.getClientScriptPattern());
		System.out.println(lFormat1.getSimpleDatePattern());
		System.out.println(lFormat1.getHl7DatePattern());

		TSDateFormat lFormat2 = new TSDateFormat(Locale.UK, "short", "long");
		System.out.println(lFormat2.getClientScriptPattern());
		System.out.println(lFormat2.getSimpleDatePattern());
		System.out.println(lFormat2.getHl7DatePattern());		
		
		TSDateFormat lFormat4 = new TSDateFormat(Locale.US, "long", "full");
		System.out.println(lFormat4.getClientScriptPattern());
		System.out.println(lFormat4.getSimpleDatePattern());
		System.out.println(lFormat4.getHl7DatePattern());		
		
	}
}
