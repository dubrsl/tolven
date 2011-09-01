package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_dfiminteraction database table.
 * 
 */
@Entity
@Table(name="fdb_dfiminteraction")
public class FdbDfiminteraction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer interactionid;

	private String interactionmessage1;

	private String interactionmessage2;

	private String interactionmessage3;

	private String interactionmessage4;

	private String interactionmessage5;

	private String interactionmessage6;

	private String interactionresult1;

	private String interactionresult2;

	private String interactionresult3;

	private String interactionresult4;

	private String interactionresult5;

	private String interactionresult6;

	private String interactor;

	private Integer monographid;

	private String significancelevelcode;

    public FdbDfiminteraction() {
    }

	public Integer getInteractionid() {
		return this.interactionid;
	}

	public void setInteractionid(Integer interactionid) {
		this.interactionid = interactionid;
	}

	public String getInteractionmessage1() {
		return this.interactionmessage1;
	}

	public void setInteractionmessage1(String interactionmessage1) {
		this.interactionmessage1 = interactionmessage1;
	}

	public String getInteractionmessage2() {
		return this.interactionmessage2;
	}

	public void setInteractionmessage2(String interactionmessage2) {
		this.interactionmessage2 = interactionmessage2;
	}

	public String getInteractionmessage3() {
		return this.interactionmessage3;
	}

	public void setInteractionmessage3(String interactionmessage3) {
		this.interactionmessage3 = interactionmessage3;
	}

	public String getInteractionmessage4() {
		return this.interactionmessage4;
	}

	public void setInteractionmessage4(String interactionmessage4) {
		this.interactionmessage4 = interactionmessage4;
	}

	public String getInteractionmessage5() {
		return this.interactionmessage5;
	}

	public void setInteractionmessage5(String interactionmessage5) {
		this.interactionmessage5 = interactionmessage5;
	}

	public String getInteractionmessage6() {
		return this.interactionmessage6;
	}

	public void setInteractionmessage6(String interactionmessage6) {
		this.interactionmessage6 = interactionmessage6;
	}

	public String getInteractionresult1() {
		return this.interactionresult1;
	}

	public void setInteractionresult1(String interactionresult1) {
		this.interactionresult1 = interactionresult1;
	}

	public String getInteractionresult2() {
		return this.interactionresult2;
	}

	public void setInteractionresult2(String interactionresult2) {
		this.interactionresult2 = interactionresult2;
	}

	public String getInteractionresult3() {
		return this.interactionresult3;
	}

	public void setInteractionresult3(String interactionresult3) {
		this.interactionresult3 = interactionresult3;
	}

	public String getInteractionresult4() {
		return this.interactionresult4;
	}

	public void setInteractionresult4(String interactionresult4) {
		this.interactionresult4 = interactionresult4;
	}

	public String getInteractionresult5() {
		return this.interactionresult5;
	}

	public void setInteractionresult5(String interactionresult5) {
		this.interactionresult5 = interactionresult5;
	}

	public String getInteractionresult6() {
		return this.interactionresult6;
	}

	public void setInteractionresult6(String interactionresult6) {
		this.interactionresult6 = interactionresult6;
	}

	public String getInteractor() {
		return this.interactor;
	}

	public void setInteractor(String interactor) {
		this.interactor = interactor;
	}

	public Integer getMonographid() {
		return this.monographid;
	}

	public void setMonographid(Integer monographid) {
		this.monographid = monographid;
	}

	public String getSignificancelevelcode() {
		return this.significancelevelcode;
	}

	public void setSignificancelevelcode(String significancelevelcode) {
		this.significancelevelcode = significancelevelcode;
	}

}