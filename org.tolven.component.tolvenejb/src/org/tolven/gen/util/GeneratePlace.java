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
package org.tolven.gen.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.tolven.gen.entity.FamilyUnit;
import org.tolven.logging.TolvenLogger;

/**
 * <p>We'll use a file from the U.S Census Bureau to generate places (where families live). Our denominator will be the total number of housing units in the US. Each row in this table tells
 * us how many housing units in each zip code. For efficient lookup, we'll invert this to an array of integers, each element containing a numeric encoding of a zip code. We'll create one entry per
 * 100 households per zip. Thus, a random entry in the array will yield a zip code in proportion to US population. From this zip code, we can work backwards to a state and address.</p>
 * <p>The ZCTA file contains data for all 5 digit ZCTAs in the 50 states, District of Columbia and Puerto Rico as of Census 2000. The file is plain ASCII text, one line per record. 
* <ul>
* <li>Columns 1-2: United States Postal Service State Abbreviation</li> 
* <li>Columns 3-66: Name (e.g. 35004 5-Digit ZCTA - there are no post office names)</li> 
* <li>Columns 67-75: Total Population (2000) </li>
* <li>Columns 76-84: Total Housing Units (2000) </li>
* <li>Columns 85-98: Land Area (square meters) - Created for statistical purposes only.</li> 
* <li>Columns 99-112: Water Area (square meters) - Created for statistical purposes only. </li>
* <li>Columns 113-124: Land Area (square miles) - Created for statistical purposes only. </li>
* <li>Columns 125-136: Water Area (square miles) - Created for statistical purposes only. </li>
* <li>Columns 137-146: Latitude (decimal degrees) First character is blank or "-" denoting North or South latitude respectively</li> 
* <li>Columns 147-157: Longitude (decimal degrees) First character is blank or "-" denoting East or West longitude respectively </li>
* </ul>
 * @author John Churin
 *
 */
public class GeneratePlace extends GeneratorBase {
    boolean placesLoaded = false;
	String zctaFile = "zcta5.txt";
	String zipCityFileName = "zipCities.txt";
	int [] zipCodes;
	String streetSuffix[] = {"Drive", "Road", "Street", "Road", "Way", "Lane", "Blvd", "Ave", "Drive", "Place"};
	String streetNames[];
	

    /**
     * Read needed files if we haven't already done so.
    */
   public void setupIfNeeded() throws IOException {
       if (!placesLoaded) {
    	   placesLoaded = true;
           // We'll need a random number generator
           rng = new Random();
           // How much space do we need to store names
           zipCodes = new int[analyzePlaces( zctaFile )];
           // Actually load up the zipcodes
           loadZipCodes(zctaFile );
       }
   }

   /**
    * Generate a random address (it has no corelation to a valid city/state/zip). We'll use random City names as street names.
    */
   public void generateAddress( FamilyUnit family ) throws IOException {
	   if (zipCityEntries==null) initializeZipCity();
	   if (streetNames==null) {
		   streetNames = new String[1000];
		   int nameNdx = 0;
		   for (String city : zipCityEntries.values()) {
			   if( nameNdx>=streetNames.length ) break;
			   int end = city.indexOf(' ');
			   if (end<0) end = city.length();
			   streetNames[nameNdx] = city.substring(0, 1) + city.substring(1, end).toLowerCase();
			   nameNdx++;
		   }
	   }
	   String streetName = streetNames[rng.nextInt(streetNames.length)];
	   family.setAddress( String.format(Locale.US, "%d %s %s", rng.nextInt(19999)+1, streetName, streetSuffix[rng.nextInt(streetSuffix.length)]));
   }
   
   /**
    * Count how many household in the US (ignoring zip codes with less than 100 households) divided by 100. In other words, 
    * one count equaly 100 households.
 * @throws IOException 
    */
   public int analyzePlaces( String zctaFile ) throws IOException {
       BufferedReader f = getFileHandle( zctaFile );
       
	    int zipCodeCount = 0;
	    int rejectCount = 0;
	    int zipCodeFreqCount = 0;
	    while (f.ready())
	    {
	        String line = f.readLine();
	        zipCodeCount++;
	        String houseHoldCountAsString = line.substring(75, 84).trim();
	        int householdCount = Integer.parseInt(houseHoldCountAsString);
	        String zipCode = line.substring(2, 7);
	        if (zipCode.matches( "\\d*") && householdCount >= 100) {
	        	zipCodeFreqCount = zipCodeFreqCount + (householdCount/100);
	        } else {
	            	rejectCount++;
	        }
	    }
	    f.close();
	    TolvenLogger.info( "Zipcodes: " + zipCodeCount + " Distribution: " + zipCodeFreqCount + " Rejects: " + rejectCount, GeneratePlace.class);
	    return zipCodeFreqCount; 
	   
   }
   
   void loadZipCodes( String fileName) throws IOException
   {
       BufferedReader f = getFileHandle( fileName );
       int zipCodeFreqCount = 0;
       while (f.ready())
       {
           String line = f.readLine();
	        int householdCount = Integer.parseInt(line.substring(75, 84).trim());
	        String zipCode = line.substring(2, 7);
	        if (zipCode.matches( "\\d*") && householdCount >= 100) {
	        	int zipCodeInt = Integer.parseInt(zipCode);
	        	for (int fx = 0; fx < householdCount/100;fx++) {
	        		zipCodes[zipCodeFreqCount++] = zipCodeInt;
	        	}
	        }
       }
       f.close();
   }

   /**
    * Add a random but proportional zip code to the virtual person.
    * @throws IOException 
    */
   public void generateZipCode(FamilyUnit family ) throws IOException
   {
   	setupIfNeeded();
    int ndx = rng.nextInt(zipCodes.length);
    String zipCode = String.format(Locale.US, "%05d", zipCodes[ndx]);
    family.setZip( zipCode );
    family.setState( zipCodeToStateCode( zipCodes[ndx]));
    family.setCity(zipCodeToCity(zipCodes[ndx]) );
    // Now fill in city and state based on zipcode.
   }

   class ZipEntry {
	   int fromZip; 
	   int toZip; 
	   String state;
	   ZipEntry( int fromZip, int toZip, String state) {
		   this.fromZip = fromZip;
		   this.toZip = toZip;
		   this.state = state;
	   }
	   ZipEntry( int zip, String state) {
		   this.fromZip = zip;
		   this.toZip = zip;
		   this.state = state;
	   }
	   }
   ArrayList<ZipEntry> zipEntries = null;

   /**
    * Zip code to state code
    * 
    */
   public String zipCodeToStateCode( int zipCode) {
	   if (zipEntries==null) initializeZipState();
	   for (ZipEntry z : zipEntries) {
		   if (z.fromZip <= zipCode && zipCode <= z.toZip ) return z.state;
	   }
	   return null;
   }
   // Zip code maps to city name
   Map<Integer, String> zipCityEntries = null;
   
   /**
    * Zip code to City
 * @throws IOException 
    * 
    */
   public String zipCodeToCity( int zipCode) throws IOException {
	   if (zipCityEntries==null) initializeZipCity();
	   String city = zipCityEntries.get( new Integer( zipCode ));
	   return city;
   }
   
   public void initializeZipCity() throws IOException {
	   zipCityEntries = new HashMap<Integer, String>(10000 );
       BufferedReader f = getFileHandle( zipCityFileName );
       while (f.ready())
       {
           String line = f.readLine();
           // Get the city which is after the =
		   String fields[] = line.split("\\=");
		   String city = fields[1];
		   // See if a range of zips is present.
		   String zips[] = fields[0].split("\\-");
		   if (zips.length==1) {
			   int zip = Integer.parseInt( zips[0]);
			   zipCityEntries.put( new Integer(zip), city);
		   }
		   if (zips.length==2) {
			   int zipFrom = Integer.parseInt( zips[0]);
			   int zipTo = Integer.parseInt( zips[1]);
			   for (int z = zipFrom; z <=zipTo; z++) {
				   zipCityEntries.put( new Integer(z), city);
			   }
		   }
       }
       f.close();
   }

   public void initializeZipState() {
	   zipEntries = new ArrayList<ZipEntry>( 70 );
	    zipEntries.add(new ZipEntry(1000, 2799,"MA"));
	    zipEntries.add(new ZipEntry(2800, 2999, "RI"));
		zipEntries.add(new ZipEntry(3000, 3899, "NH"));                                  
		zipEntries.add(new ZipEntry(3900, 4999, "ME"));   
		zipEntries.add(new ZipEntry(5000, 5999, "VT"));                                  
		zipEntries.add(new ZipEntry(6000, 6999, "CT"));   
		zipEntries.add(new ZipEntry(7000, 8999, "NJ"));                                  
		zipEntries.add(new ZipEntry(9000, 14999, "NY"));   
		zipEntries.add(new ZipEntry(15000, 19699, "PA"));                                  
		zipEntries.add(new ZipEntry(19700, 19999, "DE"));   
		zipEntries.add(new ZipEntry(20000, 20330, "DC"));
		zipEntries.add(new ZipEntry(20332, 20599, "DC"));                  
		zipEntries.add(new ZipEntry(20600, 21999, "MD"));
		zipEntries.add(new ZipEntry(20331, "MD"));   
		zipEntries.add(new ZipEntry(22000, 24699, "VA"));                          
		zipEntries.add(new ZipEntry(24700, 26899, "WV"));   
		zipEntries.add(new ZipEntry(27000, 28999, "NC"));                                  
		zipEntries.add(new ZipEntry(29000, 29999, "SC"));   
		zipEntries.add(new ZipEntry(30000, 31999, "GA"));                                  
		zipEntries.add(new ZipEntry(32000, 34999, "FL"));
		zipEntries.add(new ZipEntry(35000, 36999, "AL"));                               
		zipEntries.add(new ZipEntry(37000, 38599, "TN"));
		zipEntries.add(new ZipEntry(38600, 39799, "MS"));                               
		zipEntries.add(new ZipEntry(40000, 42799, "KY"));
		zipEntries.add(new ZipEntry(43000, 45899, "OH"));                               
		zipEntries.add(new ZipEntry(46000, 47999, "IN"));
		zipEntries.add(new ZipEntry(48000, 49935, "MI" ));
		zipEntries.add(new ZipEntry(49937, 49999, "MI"));               
		zipEntries.add(new ZipEntry(50000, 52899, "IA"));
		zipEntries.add(new ZipEntry(53000, 54999, "WI"));
		zipEntries.add(new ZipEntry(49936, "WI"));                       
		zipEntries.add(new ZipEntry(55000, 56799, "MN"));
		zipEntries.add(new ZipEntry(57000, 57799, "SD"));                               
		zipEntries.add(new ZipEntry(58000, 58899, "ND"));
		zipEntries.add(new ZipEntry(59000, 59999, "MT"));                               
		zipEntries.add(new ZipEntry(60000, 62999, "IL"));
		zipEntries.add(new ZipEntry(63000, 65899, "MO"));                               
		zipEntries.add(new ZipEntry(66000, 67999, "KS"));
		zipEntries.add(new ZipEntry(68000, 69399, "NE"));                               
		zipEntries.add(new ZipEntry(70000, 71499, "LA"));
		zipEntries.add(new ZipEntry(71600, 72999, "AR"));
		zipEntries.add(new ZipEntry(75502, "AR"));                       
		zipEntries.add(new ZipEntry(73000, 74999, "OK"));
		zipEntries.add(new ZipEntry(75000, 75501, "TX"));
		zipEntries.add(new ZipEntry(75503, 79999, "TX"));               
		zipEntries.add(new ZipEntry(80000, 81699, "CO"));
		zipEntries.add(new ZipEntry(82000, 83199, "WY"));                               
		zipEntries.add(new ZipEntry(83200, 83899, "ID"));
		zipEntries.add(new ZipEntry(84000, 84799, "UT"));                               
		zipEntries.add(new ZipEntry(85000, 86599, "AZ"));
		zipEntries.add(new ZipEntry(87000, 88499, "NM"));                               
		zipEntries.add(new ZipEntry(89000, 89899, "NV"));
		zipEntries.add(new ZipEntry(90000, 96699, "CA"));                               
		zipEntries.add(new ZipEntry(96700, 96899, "HI"));
		zipEntries.add(new ZipEntry(97000, 97999, "OR"));                               
		zipEntries.add(new ZipEntry(98000, 98732, "WA"));
		zipEntries.add(new ZipEntry(98734, 98735, "WA"));                                             
		zipEntries.add(new ZipEntry(98737, 98776, "WA"));
		zipEntries.add(new ZipEntry(98778, 98790, "WA"));
		zipEntries.add(new ZipEntry(98792, 99499, "WA"));                     
		zipEntries.add(new ZipEntry(98733,"AK" ));
		zipEntries.add(new ZipEntry(98736, "AK"));
		zipEntries.add(new ZipEntry(98777,"AK"));
		zipEntries.add(new ZipEntry(98791,"AK"));
		zipEntries.add(new ZipEntry(99500, 99990, "AK")); 	   
   }
}
