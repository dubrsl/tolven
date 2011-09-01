package org.tolven.gen.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

import org.tolven.gen.entity.VirtualPerson;
import org.tolven.logging.TolvenLogger;

/**
 * Generate random names using combinations of common first and last names.
 * @author John Churin
 */
public class RandomNameGenerator extends GeneratorBase{
    
	String lastNameFile = "lastnames.txt";
	String femaleNameFile = "femalenames.txt";
	String maleNameFile = "malenames.txt";
	long [] lastNames;
    long [] femaleNames;
    long [] maleNames;
    boolean namesLoaded = false;
    
     /**
     * Read needed files if we haven't already done so.
     */
    public void setupIfNeeded() throws IOException {
        if (!namesLoaded) {
        	namesLoaded = true;
            // We'll need a random number generator
            rng = new Random();
            // How much space do we need to store names
            lastNames = new long[analyzeFile( lastNameFile )];
            femaleNames = new long[analyzeFile( femaleNameFile )];
            maleNames = new long[analyzeFile( maleNameFile )];
            // Actually load up the names
            loadNames(lastNameFile, lastNames );
            loadNames(femaleNameFile, femaleNames );
            loadNames(maleNameFile, maleNames );
        }

    }

    int analyzeFile(String fileName) throws IOException
    {
        BufferedReader f = getFileHandle( fileName );
        
	    int nameCount = 0;
	    int rejectCount = 0;
	    int nameFreqCount = 0;
	    while (f.ready())
	    {
	        String line = f.readLine();
	        nameCount++;
	        String name = line.substring(0, 15).toUpperCase().trim();
	        long cname = compressName(name);
	        // Verify that it uncompresses correctly
	        String ucName = uncompressName(cname, false);
	        if (!name.equals(ucName)) 
	            {
	            	rejectCount++;
	            	break;
	            }
	        // Now populate enough slots in the array according to the frequency
	        // which is a decimal number, eg 1.006 = 1,006 occurances
	        float freq = new Float(line.substring(15, 19)).floatValue()*1000.0f+1.0f;
	        nameFreqCount = nameFreqCount + Math.round(freq); 
	    }
	    f.close();
	    TolvenLogger.info( "Names: " + nameCount + " Distribution: " + nameFreqCount + " Rejects: " + rejectCount, RandomNameGenerator.class);
	    return nameFreqCount; 
    }

    void loadNames( String fileName, long[]nameArray) throws IOException
    {
        BufferedReader f = getFileHandle( fileName );
        int nameFreqCount = 0;
        while (f.ready())
        {
            String line = f.readLine();
            String name = line.substring(0, 15).toUpperCase().trim();
            long cname = compressName(name);
            // Now populate enough slots in the array according to the frequency
            // which is a decimal number, eg 1.006
            float freq = new Float(line.substring(15, 19)).floatValue()*1000.0f+1.0f;
            for (int fx = 0; fx < Math.round(freq); fx++)
            {
                nameArray[nameFreqCount++] = cname;
            }
        }
        f.close();
    }

    /**
     * Compress a name as tight as possible
     * Ignore case and any special characters
     * A-Z. Fits in a single long.
     * @param name (all caps, no spaces)
     * @return a long containing the name
     */
    static long compressName( String name )
    {
        long x = 0;
        for (int i = 0; i < name.length(); i++)
        {
            char c = name.charAt(i);
            if (c > 'Z') break;
            if (c < 'A') break;
            if (c == ' ') break;
            x = x * 27 + (c-'A'+1);
        }
        return x;
    }
    /**
     * Turn a compessed name back into a string, all upper case
     * @param cn compressed name
     * @param ulcase If true, return as upper and lower case
     * @return name
     */
    static String uncompressName(long cn, boolean ulcase)
    {
        long cnx = cn;
        StringBuffer sb = new StringBuffer(15);
        char base;
        if (ulcase) base = 'a'-1;
        else base = 'A'-1;
        for (int i = 0; i < 15;i++)
        {
            long cnc = cnx%27;
            cnx = cnx / 27;
            if (cnc!=0) sb.insert(0, (char)(cnc+base));
        }
        // Initial capital
        if (ulcase) {
            int x = sb.charAt(0);
            x = x - 'a' ;
            x = x + 'A' ;
            sb.setCharAt( 0, (char)x);
        }
        return sb.toString();
    }

    /**
     * Select a name at random from the supplied list of names
     */ 
    String chooseName( long[]nameList)
    {
        int ndx = rng.nextInt(nameList.length);
        
        return uncompressName( nameList[ndx], true);
    }

    /**
     * Generate a name and add it to the virtual person supplied.
     * This creates a completely new person, arriving out of the blue.
     * Other methods create relatives of persons. As a practical matter,
     * we generate a lot of these starting persons to get things rolling.
     * Then we call more specialized name generators to create offspring
     * etc.
     * @throws IOException 
     */
    public void generateNewName(VirtualPerson person ) throws IOException
    {
    	setupIfNeeded();
    	// Choose a gender
        if (rng.nextBoolean())
        {
	        // Choose a first and middle name
            person.setFirst( chooseName(femaleNames));
            person.setMiddle( chooseName(femaleNames));
            person.setGender( "F");
        }
        else
        {
	        // Choose a first and middle name
            person.setFirst( chooseName(maleNames));
            person.setMiddle( chooseName(maleNames));
            person.setGender( "M");
        }
        // Last Name
        person.setLast( chooseName(lastNames) );
        
    }


    /**
     * Return the number of times this person is likely to be married
     * for the purposes of having a baby (we don't care if they are legally
     * married).
     */
//    public int spouseProbablility( )
//    {
//        
//    }
    /**
     * Based on a furtility rate of 64.8 births per 1000 women between 15 and 44.
     * 1st child  25.8
2d child  21.1 
3d child . . . . . . . . . . . . . . . . . . . 10.9 * 1.2 0.2 2.8 14.5 21.4 18.8 9.6 1.7 0.1
4th child . . . . . . . . . . . . . . . . . . . 4.3 * 0.1 0.0 0.4 4.2 8.3 7.8 4.7 1.0 0.1
5th child . . . . . . . . . . . . . . . . . . . 1.5 * 0.0 * 0.0 1.0 2.9 2.9 2.0 0.6 0.0
6th and 7th child . . . . . . . . . . . . . . 0.9 * 0.0 * 0.0 0.3 1.3 1.8 1.5 0.5 0.0
8th child and over . . . . . . . . . . . . . 0.3
	 * We need to find a father for children born to women. 
	 * To do this reliably and fairly, we'll create 
     * @return the number of chidren born to a female of childbearing age.
     */
//    public int childProbability( )
//    {
//        
//    }

}
