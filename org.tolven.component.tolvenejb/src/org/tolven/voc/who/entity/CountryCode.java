/*
 * CountryCode.java
 *
 * Created on March 25, 2006, 12:12 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tolven.voc.who.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * WHO Country codes
 * @author John Churin
 */
@Entity
@Table
public class CountryCode implements Serializable {
    
    /** Creates a new instance of CountryCode */
    public CountryCode() {
    }
    
    @Id
    @Column
    private String country;

    @Column
    private String name;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    /**
     * Compare two country codes for equality
     */
    public boolean equals(Object obj) {
        if (obj instanceof CountryCode ) {
            if (country.equals( ((CountryCode)obj).getCountry())) return true;
        }
        return false;
    }

    /**
     * Display string - for debugging
     */
    public String toString() {
        return country + " " + name;
    }

    /**
     * Return hash code based on country
     */
    public int hashCode() {
        return  country.hashCode();
    }
    
    
}
