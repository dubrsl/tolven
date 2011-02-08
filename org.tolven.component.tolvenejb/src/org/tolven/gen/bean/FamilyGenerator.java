package org.tolven.gen.bean;

import java.util.Date;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.tolven.gen.FamilyGeneratorLocal;
import org.tolven.gen.FamilyGeneratorRemote;
import org.tolven.gen.entity.FamilyMember;
import org.tolven.gen.entity.FamilyUnit;
import org.tolven.gen.entity.VirtualPerson;
import org.tolven.gen.entity.FamilyMember.FamilyMemberRole;
import org.tolven.gen.util.GenerateCauseOfDeath;
import org.tolven.gen.util.GeneratePlace;
import org.tolven.gen.util.LifeSpan;
import org.tolven.gen.util.RandomNameGenerator;
import org.tolven.logging.TolvenLogger;

@Stateless
@Local(FamilyGeneratorLocal.class)
@Remote(FamilyGeneratorRemote.class)
public class FamilyGenerator  implements FamilyGeneratorLocal, FamilyGeneratorRemote {
	private RandomNameGenerator nameGenerator;
	private LifeSpan lifeSpan;
	private GeneratePlace placeGenerator;
	private GenerateCauseOfDeath grimReaper;

    public FamilyGenerator() {
    	nameGenerator = new RandomNameGenerator();
    	lifeSpan = new LifeSpan();
    	placeGenerator = new GeneratePlace();
    	grimReaper = new  GenerateCauseOfDeath();
    }
    
    public VirtualPerson generatePerson( ) throws Exception
    {
//        if (!ctx.isCallerInRole("gen")) { 
//            throw new SecurityException( "User [" + ctx.getCallerPrincipal() + "] must be in gen role to call GeneratePerson");
//        }
        // A virtual person object
        VirtualPerson vPerson = new VirtualPerson();
        nameGenerator.generateNewName(vPerson);
        grimReaper.generateCauseOfDeath( vPerson );
        lifeSpan.setLifeSpan( vPerson );
        
        return vPerson;
    }
    
	 /**
	  * Create a family.
	  * If family name is non-null, then we'll take it as the family name of the family otherwise we'll
	  * use a random name.
	  */
	 public FamilyUnit generateFamily( String familyName,  Date now ) throws Exception {
		 // Start a family
		 FamilyUnit family = new FamilyUnit();
		 placeGenerator.generateZipCode(family);
		 placeGenerator.generateAddress(family);
		 String primaryGender = null;
		 int primaryAge = 0;
	     int genKids = 0;
	     
	   	 // Start with a primary person (we should find a suitable mom or dad in 500 tries)
	   	 for (int c = 0; c < 500; c++) {
	   		VirtualPerson person = generatePerson();
	        primaryAge = person.getAgeInYears(now);
	        TolvenLogger.info( "[FamilyGen] Candidate: " + primaryAge + " year" + " " + person.getGender(), FamilyGenerator.class);
	        if ( primaryAge < 21 ) continue;
        	if (primaryAge < 50) genKids = 3; 
        	if (primaryAge < 30) genKids = 2; 
        	if (primaryAge < 20) genKids = 1; 
        	if (null!=familyName) {
	        	family.setFamilyName(familyName);
	        } else {
	        	family.setFamilyName(person.getLast());
	        }
      		person.setLast(family.getFamilyName());
	        primaryGender = person.getGender();
	        FamilyMemberRole role;
	        if ("F".equals(person.getGender())) {
	        	role = FamilyMember.FamilyMemberRole.MOTHER;
	        } else {
	        	role = FamilyMember.FamilyMemberRole.FATHER;
	        }
        	family.addMember( person, role );
        	if (c > 70 ) TolvenLogger.info( "[FamilyGen] Person candidates skipped: " + c, FamilyGenerator.class);
			break;
	   	}
	   	 // Look for a second person (we may not find one in 10 tries - meaning a single person)
	   	 for (int c = 0; c < 20; c++) {
    		VirtualPerson person = generatePerson();
    		int age = person.getAgeInYears( now );
    		if ( age < 21 ) {
//	    			TolvenLogger.info( "Rejecting spouse - too young", FamilyGenerator.class);
    			continue;
    		}
    		if (primaryGender.equals(person.getGender())) {
//	    			TolvenLogger.info( "Rejecting spouse - gender", FamilyGenerator.class);
    			continue;
    		}
      		int ageDifference = person.getAgeInYears( now )-primaryAge;
      		// If too far apart in age, forget it
      		if (Math.abs( ageDifference) > 10 ) {
//	    			TolvenLogger.info( "Rejecting spouse - Difference of " + ageDifference + " years", FamilyGenerator.class);
      			continue;
      		}
      		// Same last name, add to family
      		person.setLast(family.getFamilyName());
	        FamilyMemberRole role;
	        if ("F".equals(person.getGender())) {
	        	role = FamilyMember.FamilyMemberRole.MOTHER;
	        } else {
	        	role = FamilyMember.FamilyMemberRole.FATHER;
	        }
        	family.addMember( person, role );
//				TolvenLogger.info( "Found suitable spouse (" + family.getFamilyName() + ") after " + c + " failed attempts", FamilyGenerator.class);
  			break;
    	}
	   	 
	   	 for (int k = 0; k < 100; k++) {
    		if (genKids <= 0) break;
	   		VirtualPerson person = generatePerson();
   			// Ignore people not yet born
   			if (person.getDob().after(now)) continue;
    		int age = person.getAgeInYears( now );
    		// Assume kids over 18 take care of their own health care
    		if (age > 16) continue;
    		// put at least 18 years between mom and oldest kid
    		if ((primaryAge-age) < 18 ) continue;
    		genKids--;
      		person.setLast(family.getFamilyName());
        	family.addMember( person, FamilyMemberRole.CHILD );
	   	 }
	   	 return family;
	    }


}
