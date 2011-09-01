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

import org.tolven.gen.entity.VirtualPerson;
import org.tolven.logging.TolvenLogger;

/**
 * Generate cause of death for a virtual person based on age at death and gender.
 * @author John Churin
 */
public class GenerateCauseOfDeath extends GeneratorBase {
	String maleFile = "MaleMortality.csv";
	String femaleFile = "FemaleMortality.csv";
	int [][] maleCauses = null;
	int [][] femaleCauses = null;
	int [] allCauses = null;
	int totalDeathCount = 0;
	String descriptions[] = new String[113];

    /**
     * Determine appropriate causes of death by age and sex. 
     * @throws IOException 
     */
    public void analyzeCauses( String causeFile,int [][] causes ) throws IOException {
        BufferedReader f = getFileHandle( causeFile );
 	    int causeCount = 0;
 	    int fileDeathCount = 0;
 	    for (int a = 0; a < causes.length; a++) {
 	    	for (int c = 0; c < causes[a].length; c++) {
 	    		causes[a][c] = 0;
 	    	}
 	    }
 	    while (f.ready())
 	    {
 	        String line = f.readLine();
			String fields[] = line.split("\\,");
 	        causeCount++;
 	        int deathCount = Integer.parseInt(fields[3]);
 	        // Make sure we have the description
 	        int diseaseId = Integer.parseInt(fields[0].substring(2,5))-1;
 	        if (descriptions[diseaseId]==null) {
 	        	descriptions[diseaseId] = fields[1];
 	        }
 	        // Figure out age
 	        if ("Under 1 Year".equals(fields[2])) {
 	        	causes[0][diseaseId] = deathCount;
 	        	allCauses[0] += deathCount;
 	        }
 	        if ("1- 4 years".equals(fields[2])) {
 	 	       	causes[1][diseaseId] = deathCount;
 	        	allCauses[1] += deathCount;
 	        }
 	        if ("5- 9 years".equals(fields[2])) {
 	 	       	causes[2][diseaseId] = deathCount;
 	        	allCauses[2] += deathCount;
 	        }
 	        if ("10-14 years".equals(fields[2])) {
 	 	        causes[3][diseaseId] = deathCount;
 	        	allCauses[3] += deathCount;
 	        }
 	        if ("15-19 years".equals(fields[2])) {
	 	        causes[4][diseaseId] = deathCount;
 	        	allCauses[4] += deathCount;
 	        }
 	        if ("20-24 years".equals(fields[2])) {
	 	        causes[5][diseaseId] = deathCount;
 	        	allCauses[5] += deathCount;
 	        }
 	        if ("25-34 years".equals(fields[2])) {
	 	        causes[6][diseaseId] = deathCount;
 	        	allCauses[6] += deathCount;
 	        }
 	        if ("35-44 years".equals(fields[2])) {
	 	        causes[7][diseaseId] = deathCount;
 	        	allCauses[7] += deathCount;
 	        }
 	        if ("45-54 years".equals(fields[2])) {
	 	        causes[8][diseaseId] = deathCount;
 	        	allCauses[8] += deathCount;
 	        }
 	        if ("55-64 years".equals(fields[2])) {
	 	        causes[9][diseaseId] = deathCount;
 	        	allCauses[9] += deathCount;
 	        }
 	        if ("65-74 years".equals(fields[2])) {
	 	        causes[10][diseaseId] = deathCount;
 	        	allCauses[10] += deathCount;
 	        }
 	        if ("75-84 years".equals(fields[2])) {
	 	        causes[11][diseaseId] = deathCount;
 	        	allCauses[11] += deathCount;
	        }
 	        if ("85 years and over".equals(fields[2])) {
	 	        causes[12][diseaseId] = deathCount;
 	        	allCauses[12] += deathCount;
 	        }
 	        totalDeathCount += deathCount;
 	        fileDeathCount += deathCount;
 	    }
 	    f.close();
 	    TolvenLogger.info( causeFile + ": deaths= " + fileDeathCount, GenerateCauseOfDeath.class);
    }
 
    /**
     * Calculate a cause of death for a specific virtualPerson.
     * @param person
     * @throws IOException
     */
	public void generateCauseOfDeath( VirtualPerson person ) throws IOException {
		if (maleCauses == null) {
	        maleCauses = new int[13][113];
	        femaleCauses = new int[13][113];
	        allCauses = new int [13];
			analyzeCauses( maleFile, maleCauses );
			analyzeCauses( femaleFile, femaleCauses );
		}
		int localCause[][];
		// Now pick one
		if ("F".equals(person.getGender())) {
			localCause = femaleCauses;
		} else {
			localCause = maleCauses;
		}
		// How old will they be when they die?
		int mark = rng.nextInt( totalDeathCount );
		int accum = 0;
		int age = 0;
		int ageGroup = 12;
		for (int x = 0; x < allCauses.length; x++) {
			accum += allCauses[x];
			if (accum > mark) {
				ageGroup = x;
				if (x==0) {
					age = 0;
				} else if (x==1) {
					age = 1 + rng.nextInt(5);
				} else if (x==2) {
					age = 5 + rng.nextInt(5);
				} else if (x==3) {
					age = 10 + rng.nextInt(5);
				} else if (x==4) {
					age = 15 + rng.nextInt(5);
				} else if (x==5) {
					age = 20 + rng.nextInt(5);
				} else if (x==6) {
					age = 25 + rng.nextInt(10);
				} else if (x==7) {
					age = 35 + rng.nextInt(10);
				} else if (x==8) {
					age = 45 + rng.nextInt(10);
				} else if (x==9) {
					age = 55 + rng.nextInt(10);
				} else if (x==10) {
					age = 65 + rng.nextInt(10);
				} else if (x==11) {
					age = 75 + rng.nextInt(10);
				} else {
					age = 85 + rng.nextInt(15);
				}
				break;
			}
		}
		person.setAgeAtDeath(age);
		int localAge [] = localCause[ageGroup];
		// How many deaths in this age group?
		int totalDeaths = 0;
		for (int c = 0; c < localAge.length; c++ ) {
			totalDeaths += localAge[c];
		}
		if (totalDeaths > 0) {
			// Now pick a random number
			int cmark = rng.nextInt( totalDeaths );
			// Now see where we land
			int caccum = 0;
			for (int d = 0; d < 113;d++) {
				 caccum += localAge[d];
				 if (caccum > cmark) {
					 person.setCauseOfDeath(descriptions[d]);
					 return;
				 }
			}
		}
		person.setCauseOfDeath("Unable to determine");
	}
}
