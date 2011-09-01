package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_medcond database table.
 * 
 */
@Entity
@Table(name="fdb_medcond")
public class FdbMedcond implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String fdbdx;

	private String acutechroniccode;

	private String description1;

	private String description2;

	private String description3;

	private String description4;

	private String description5;

	private String description6;

	private String diseasedurcode;

	private Integer drugsthatcauseind;

	private Integer drugstoavoidind;

	private Integer drugstotreatind;

	private String dxid;

	private String dxidstatuscode;

	private Integer hasdrugsthatcauseind;

	private Integer hasdrugstoavoidind;

	private Integer hasdrugstotreatind;

	private Integer lowestlevelind;

	private Integer toplevelind;

    public FdbMedcond() {
    }

	public String getFdbdx() {
		return this.fdbdx;
	}

	public void setFdbdx(String fdbdx) {
		this.fdbdx = fdbdx;
	}

	public String getAcutechroniccode() {
		return this.acutechroniccode;
	}

	public void setAcutechroniccode(String acutechroniccode) {
		this.acutechroniccode = acutechroniccode;
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

	public String getDiseasedurcode() {
		return this.diseasedurcode;
	}

	public void setDiseasedurcode(String diseasedurcode) {
		this.diseasedurcode = diseasedurcode;
	}

	public Integer getDrugsthatcauseind() {
		return this.drugsthatcauseind;
	}

	public void setDrugsthatcauseind(Integer drugsthatcauseind) {
		this.drugsthatcauseind = drugsthatcauseind;
	}

	public Integer getDrugstoavoidind() {
		return this.drugstoavoidind;
	}

	public void setDrugstoavoidind(Integer drugstoavoidind) {
		this.drugstoavoidind = drugstoavoidind;
	}

	public Integer getDrugstotreatind() {
		return this.drugstotreatind;
	}

	public void setDrugstotreatind(Integer drugstotreatind) {
		this.drugstotreatind = drugstotreatind;
	}

	public String getDxid() {
		return this.dxid;
	}

	public void setDxid(String dxid) {
		this.dxid = dxid;
	}

	public String getDxidstatuscode() {
		return this.dxidstatuscode;
	}

	public void setDxidstatuscode(String dxidstatuscode) {
		this.dxidstatuscode = dxidstatuscode;
	}

	public Integer getHasdrugsthatcauseind() {
		return this.hasdrugsthatcauseind;
	}

	public void setHasdrugsthatcauseind(Integer hasdrugsthatcauseind) {
		this.hasdrugsthatcauseind = hasdrugsthatcauseind;
	}

	public Integer getHasdrugstoavoidind() {
		return this.hasdrugstoavoidind;
	}

	public void setHasdrugstoavoidind(Integer hasdrugstoavoidind) {
		this.hasdrugstoavoidind = hasdrugstoavoidind;
	}

	public Integer getHasdrugstotreatind() {
		return this.hasdrugstotreatind;
	}

	public void setHasdrugstotreatind(Integer hasdrugstotreatind) {
		this.hasdrugstotreatind = hasdrugstotreatind;
	}

	public Integer getLowestlevelind() {
		return this.lowestlevelind;
	}

	public void setLowestlevelind(Integer lowestlevelind) {
		this.lowestlevelind = lowestlevelind;
	}

	public Integer getToplevelind() {
		return this.toplevelind;
	}

	public void setToplevelind(Integer toplevelind) {
		this.toplevelind = toplevelind;
	}

}