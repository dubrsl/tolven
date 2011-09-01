package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ddiminteraction database table.
 * 
 */
@Entity
@Table(name="fdb_ddiminteraction")
public class FdbDdiminteraction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer interactionid;

	private String clinicaleffectcode1;

	private String clinicaleffectcode2;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private Integer docanimalstudyind;

	private Integer doccaserptind;

	private Integer dochumantrialind;

	private Integer docmfgind;

	private Integer docmtgabstractind;

	private Integer docrvwarticleind;

	private String edipageref;

	private Integer monographid;

	private String severitylevelcode;

    public FdbDdiminteraction() {
    }

	public Integer getInteractionid() {
		return this.interactionid;
	}

	public void setInteractionid(Integer interactionid) {
		this.interactionid = interactionid;
	}

	public String getClinicaleffectcode1() {
		return this.clinicaleffectcode1;
	}

	public void setClinicaleffectcode1(String clinicaleffectcode1) {
		this.clinicaleffectcode1 = clinicaleffectcode1;
	}

	public String getClinicaleffectcode2() {
		return this.clinicaleffectcode2;
	}

	public void setClinicaleffectcode2(String clinicaleffectcode2) {
		this.clinicaleffectcode2 = clinicaleffectcode2;
	}

	public String getDescription1() {
		return this.description1;
	}

	public void setDescription1(String description1) {
		this.description1 = description1;
	}

	public String getDescription2() {
		return this.description2;
	}

	public void setDescription2(String description2) {
		this.description2 = description2;
	}

	public String getDescription3() {
		return this.description3;
	}

	public void setDescription3(String description3) {
		this.description3 = description3;
	}

	public String getDescription4() {
		return this.description4;
	}

	public void setDescription4(String description4) {
		this.description4 = description4;
	}

	public String getDescription5() {
		return this.description5;
	}

	public void setDescription5(String description5) {
		this.description5 = description5;
	}

	public String getDescription6() {
		return this.description6;
	}

	public void setDescription6(String description6) {
		this.description6 = description6;
	}

	public Integer getDocanimalstudyind() {
		return this.docanimalstudyind;
	}

	public void setDocanimalstudyind(Integer docanimalstudyind) {
		this.docanimalstudyind = docanimalstudyind;
	}

	public Integer getDoccaserptind() {
		return this.doccaserptind;
	}

	public void setDoccaserptind(Integer doccaserptind) {
		this.doccaserptind = doccaserptind;
	}

	public Integer getDochumantrialind() {
		return this.dochumantrialind;
	}

	public void setDochumantrialind(Integer dochumantrialind) {
		this.dochumantrialind = dochumantrialind;
	}

	public Integer getDocmfgind() {
		return this.docmfgind;
	}

	public void setDocmfgind(Integer docmfgind) {
		this.docmfgind = docmfgind;
	}

	public Integer getDocmtgabstractind() {
		return this.docmtgabstractind;
	}

	public void setDocmtgabstractind(Integer docmtgabstractind) {
		this.docmtgabstractind = docmtgabstractind;
	}

	public Integer getDocrvwarticleind() {
		return this.docrvwarticleind;
	}

	public void setDocrvwarticleind(Integer docrvwarticleind) {
		this.docrvwarticleind = docrvwarticleind;
	}

	public String getEdipageref() {
		return this.edipageref;
	}

	public void setEdipageref(String edipageref) {
		this.edipageref = edipageref;
	}

	public Integer getMonographid() {
		return this.monographid;
	}

	public void setMonographid(Integer monographid) {
		this.monographid = monographid;
	}

	public String getSeveritylevelcode() {
		return this.severitylevelcode;
	}

	public void setSeveritylevelcode(String severitylevelcode) {
		this.severitylevelcode = severitylevelcode;
	}

}