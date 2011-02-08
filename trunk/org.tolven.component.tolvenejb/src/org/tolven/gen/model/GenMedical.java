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
package org.tolven.gen.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import org.tolven.app.entity.MenuData;
import org.tolven.gen.entity.FamilyUnit;
import org.tolven.gen.entity.VirtualPerson;
import org.tolven.gen.util.DateUtil;

/**
 * <p>Generate Medical data based on the patient's demographic data.
 * This is a callback class sent to the actual scenario generator. The scenarios determine
 * when a medical event occurs and then calls back to this class to actually create the data.</p> 
 * <p>Restated: genSource says the patient has a disease (and when) and GenMedical (this class) records 
 * that fact in the document.</p>
 * @author John Churin
 */
public abstract class GenMedical {
	private Date now;
	private int startYear;
    private RandomData rng;
    private List<GenSource> criteria = null;
    // The following vary by which patient we're doing
	private VirtualPerson vp;
	private FamilyUnit family;
	private boolean male;
	private String documentId;
	private MenuData mdPatient;
	

	public GenMedical(Date now, int startYear) {
		rng = new RandomDataImpl();
    	this.now = now;
    	this.startYear = startYear;
    }

    /**
     * Setup the various generators we have
     */
    public List<GenSource> getCriteria() {
   	 if (criteria==null) {
    	 criteria = new ArrayList<GenSource>( 100 );
    	 criteria.add(new PatientGen(rng));
    	 criteria.add(new DiabetesGen(rng));
    	 criteria.add(new HypertensionGen(rng));
    	 criteria.add(new AsthmaGen(rng));
    	 criteria.add(new CholesterolGen(rng));
    	 criteria.add(new TBGen(rng));
    	 criteria.add(new BreastCancerGen(rng));
    	 criteria.add(new ProstateCancerGen(rng));
    	 criteria.add(new PregnantGen(rng));
    	 criteria.add(new WellBabyGen(rng));
//    	 criteria.add(new WellChildGen(rng));
//    	 criteria.add(new WellAdultGen(rng));
   	 }
   	 return criteria;
    }

    /**
	 * Return a time relative to now
	 */
	public Date monthsAgo( int months ) {
		GregorianCalendar cal = new GregorianCalendar( );
		cal.setTime( getNow());
		cal.add(GregorianCalendar.MONTH, -months);
		return cal.getTime();
	}
	

	/**
	 * Mother has baby at the conclusion of pregnancy (or adoption process). There's nothing
	 * medical about this step - we simply create the new person, give them a name, etc. The major
	 * function is to then run this new person through the same disease criteria as anyone else.
	 * The dob will be in the past and it is possible that this "baby" will have children
	 * of her own by now. Thus, 2-3 levels of recursion can occur.
	 * @param count
	 * @param eventDate
	 */
	public void birth( int count, Date eventDate ) {
		for (int m = 0; m < count; m++) {
//	    	chrGen.registerBaby(this, eventDate);
		}
    }
	
	/**
   	 * A nominal callback function to create the patient details in our target document.
   	 * All patients generated here are new so we'll just make up a random number as a
   	 * patient medicalrecordNumber - there may even be duplicates which we'll chalk up to
   	 * reality.
   	 * @param startYear
   	 * @return
   	 */
   	public abstract void generateNewPatient( Date eventTime );
   	
   	/**
   	 * Generate one problem. CCR supports episodes, we could but don't take that into account. Herpes
   	 * began on a certain date and then episodes periodically from then on out.
   	 */
   	public abstract void generateProblem(  Date onset, String problem, String status );

   	/**
   	 * Generate one allergy.
   	 */
   	public abstract void generateAllergy(  Date onset, String allergy, String status );
   	
   	/**
   	 * Generate a lab result. We ignore requests before the start date presuming that if the practice wasn't in
   	 * business, then the results wouldn't be in this record.
   	 */
   	public abstract void generateLabResult( Date testDate, String battery, List<LabTest> tests);

   	/**
   	 * Generate one rad result, but nothing before we started the practice
   	 * @return
   	 */
   	public abstract void generateRadResult( Date effective, String test, String result, String interpretation );

   	/**
   	 * Generate one appointment (typically in the future)
   	 * @return
   	 */
   	public abstract void generateAppointment( Date effective, String purpose, String location, String practitioner, String instructions );
   	
   	/**
   	 * Generate a medication
   	 * @return
   	 */
   	public abstract void generateMedication( Date effective, String name, String dispense, String instuctions, String refills );

   	public boolean isMale() {
   		return male;
   	}

   	public boolean isFemale() {
   		return !male;
   	}

   	public void setMale(boolean male) {
		this.male = male;
	}

   	public void setFemale(boolean female) {
		this.male = !female;
	}

   	public Date getDOB( ) {
   		Date dob = null;
   		if (vp!=null) {
   			dob = vp.getDob();
   		} else if (mdPatient!=null) {
   			dob = mdPatient.getDate01();
   		}
   		return dob;
   	}
   	
   	public Date getDateOfDeath( ){
   		Date dod = null;
   		if (vp!=null) {
   			dod = vp.getDod();
   		} else if (mdPatient!=null) {
   			// We'll have to guess at dod
   			dod = new Date(mdPatient.getDate01().getTime() + 100*DateUtil.YEARS);
   		}
   		return dod;
   	}
   	
   	public int getAgeInYears( ) {
   		if (vp!=null) {
   			return vp.getAgeInYears(getNow());
   		}
   		if (mdPatient!=null) {
			return (int) ((mdPatient.getDate01().getTime()-getNow().getTime())/DateUtil.YEARS);
   		}
   		return 0;
   	}
   	
   	public Date getNow() {
		return now;
	}
	public void setNow(Date now) {
		this.now = now;
	}
	
	/**
	 * The year when this patient started with this practice/account.
	 * @return
	 */
	public int getStartYear() {
		return startYear;
	}
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public FamilyUnit getFamily() {
		return family;
	}

	public void setFamily(FamilyUnit family) {
		this.family = family;
	}

	public RandomData getRng() {
		return rng;
	}

	public void setRng(RandomData rng) {
		this.rng = rng;
	}

	public VirtualPerson getVp() {
		return vp;
	}

	public void setVp(VirtualPerson vp) {
		this.vp = vp;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public MenuData getPatient() {
		return mdPatient;
	}

	public void setPatient(MenuData mdPatient) {
		this.mdPatient = mdPatient;
	}
	
}
