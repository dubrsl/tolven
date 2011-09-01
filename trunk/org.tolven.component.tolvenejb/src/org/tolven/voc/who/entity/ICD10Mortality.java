/*
 * ICD10Mortality.java
 *
 * Created on March 25, 2006, 12:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tolven.voc.who.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author John Churin
 */

/*Entity Table(name = "ICD10MORTALITY", schema="who") */
public class ICD10Mortality {
    
/*    @ManyToOne */
    private CountryCode country;
    private String admin1;
    private String subdiv;
    private int year;
    private String list;
    private String cause;
    private int sex;
    private String frmat;
    private String im_frmat;
    private int deaths1;
    private int deaths2;
    private int deaths3;
    private int deaths4;
    private int deaths5;
    private int deaths6;
    private int deaths7;
    private int deaths8;
    private int deaths9;
    private int deaths10;
    private int deaths11;
    private int deaths12;
    private int deaths13;
    private int deaths14;
    private int deaths15;
    private int deaths16;
    private int deaths17;
    private int deaths18;
    private int deaths19;
    private int deaths20;
    private int deaths21;
    private int deaths22;
    private int deaths23;
    private int deaths24;
    private int deaths25;
    private int deaths26;
    private int IM_Deaths1;
    private int IM_Deaths2;
    private int IM_Deaths3;
    private int IM_Deaths4;

    /** Creates a new instance of ICD10Mortality */
    public ICD10Mortality() {
    }
    
}
