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
package org.tolven.gen.entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author John Churin
 */

@Entity
@Table
public class VirtualPerson implements Serializable{
    
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="GEN_SEQ_GEN")
    private long id;
    @Column
    private String first;
    @Column
    private String last;
    @Column
    private String maiden;
    @Column
    private String middle;
    @Column
    private String gender;
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date dob = null;
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date dod = null;
    @Column
    private String causeOfDeath;
    private int ageAtDeath = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    private VirtualPerson mother;
    @ManyToOne(fetch = FetchType.LAZY)
    private VirtualPerson father;
    @Column
    private String ssn;
    @Column
    private String mobilePhone;

    
    public VirtualPerson getFather() {
        return father;
    }
    public void setFather(VirtualPerson father) {
        this.father = father;
    }
    public VirtualPerson getMother() {
        return mother;
    }
    public void setMother(VirtualPerson mother) {
        this.mother = mother;
    }

     public String getFirst() {
        return first;
    }
    public void setFirst(String first) {
        this.first = first;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getLast() {
        return last;
    }
    public void setLast(String last) {
        this.last = last;
    }
    public String getMiddle() {
        return middle;
    }
    public void setMiddle(String middle) {
        this.middle = middle;
    }
    
    transient static String hl7Format = "yyyyMMddhhmmss";
    transient SimpleDateFormat hl7sdf = new SimpleDateFormat( hl7Format );
    public String getHL7Dob()
    {
        return hl7sdf.format(dod);
    }

    transient static String birthdayFormat = "MMdd";
    transient SimpleDateFormat birthdaysdf = new SimpleDateFormat( birthdayFormat );
    public String getBirthday()
    {
        return birthdaysdf.format(dod);
    }

    public Date getDob() {
        return dob;
    }

    public String getFormattedDob( )
    {
    	DateFormat df = DateFormat.getDateInstance( DateFormat.MEDIUM );
    	return df.format( dob );
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }
    public Date getDod() {
        return dod;
    }
    public void setDod(Date dod) {
        this.dod = dod;
    }

    public String getAgeNow( ) {
    	return getAge( new Date() );
    }

    /**
     *  Compute the age.
     *  Applications may need more control than this generic method provides. But it does include unborn, dead, and in between.
     */
    
    public String getAge(Date now) {
        // If person is not born or will be born in the future, 
        // don't compute.
        if (dob==null) return "Unborn";
        GregorianCalendar n = new GregorianCalendar();
        n.setTime( now );
        GregorianCalendar b = new GregorianCalendar();
        b.setTime( dob );
        if (b.after(n)) return "Unborn";
        // If person has died, then just say it rather than the age
        GregorianCalendar d = new GregorianCalendar();
        d.setTime( dod );
        if (dod != null)
        {
	        if (n.after(d))
	        {
	            return "deceased";
	        }
        }
        // Get age in years
        int years = n.get(Calendar.YEAR)- b.get(Calendar.YEAR);
        int days = n.get( Calendar.DAY_OF_YEAR ) - b.get( Calendar.DAY_OF_YEAR );
        if (days < 0) 
          {
            years--;
            days = days + 365;
          }
        if (years > 1) return Integer.toString( years )+ " years";
        if (years == 0 && days < 30) 
         {
            return Integer.toString( days )+ " days";
         }
        return Integer.toString( years * 12 + (days/30) )+ " months";
    }

    /**
     * Compute the age in years. For unborn people return zero.
     * For people that are dead, return their age as if they had been alive.
     * Under one year old returns zero.
     */ 
    public int getAgeInYears(Date now) {
        // If person is not born or will be born in the future, 
        // don't compute.
        if (dob==null) return 0;
        GregorianCalendar n = new GregorianCalendar();
        n.setTime( now );
        GregorianCalendar b = new GregorianCalendar();
        b.setTime( dob );
        if (b.after(n)) return 0;
        // Get age in years
        int years = n.get(Calendar.YEAR)- b.get(Calendar.YEAR);
        return years;
    }

    public int getAgeAtDeath() {
        return ageAtDeath;
    }
    public void setAgeAtDeath(int ageAtDeath) {
        this.ageAtDeath = ageAtDeath;
    }
    public String getCauseOfDeath() {
        return causeOfDeath;
    }
    
    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }
    
    transient static String dobFormat = "yyyy-MM-dd";
    transient SimpleDateFormat sdf = new SimpleDateFormat( dobFormat );

    public String toString()
    {
        StringBuffer sb = new StringBuffer(100);
        sb.append( getAge( new Date()) ); sb.append( " ");
        sb.append( gender ); sb.append( " ");
        sb.append( first ); sb.append( " ");
        sb.append( middle ); sb.append( " ");
        sb.append( last );
        if (dob!=null)
        {
            sb.append( " born "); sb.append(sdf.format(dob));
        }
        if (dod!=null)
        {
            sb.append( " died "); sb.append(sdf.format(dod));
            sb.append(" at "); sb.append(ageAtDeath);
            sb.append(" of "); sb.append(causeOfDeath);
        }
        else sb.append(" ");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof VirtualPerson ) {
            if (getId()==((VirtualPerson)obj).getId()) return true;
        }
        return false;
    }

    public int hashCode() {
        return (int) (getId() % Integer.MAX_VALUE);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
	public String getMaiden() {
		return maiden;
	}
	public void setMaiden(String maiden) {
		this.maiden = maiden;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
    
}
