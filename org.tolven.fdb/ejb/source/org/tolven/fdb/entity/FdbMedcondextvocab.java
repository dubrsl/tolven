package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_medcondextvocab database table.
 * 
 */
@Entity
@Table(name="fdb_medcondextvocab")
public class FdbMedcondextvocab implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbMedcondextvocabPK id;

	private Integer clinicallinkind;

	private String descabbrev1;

	private String descabbrev2;

	private String descabbrev3;

	private String descabbrev4;

	private String descabbrev5;

	private String descabbrev6;

	private String descprimary1;

	private String descprimary2;

	private String descprimary3;

	private String descprimary4;

	private String descprimary5;

	private String descprimary6;

	private String descsecondary1;

	private String descsecondary2;

	private String descsecondary3;

	private String descsecondary4;

	private String descsecondary5;

	private String descsecondary6;

	private String desctertiary1;

	private String desctertiary2;

	private String desctertiary3;

	private String desctertiary4;

	private String desctertiary5;

	private String desctertiary6;

	private Integer druglinkind;

	private Integer drugsthatcauseind;

	private Integer drugstoavoidind;

	private Integer drugstotreatind;

	private String extvocabid;

	private String extvocabnametypecode;

	private Integer hasdrugsthatcauseind;

	private Integer hasdrugstoavoidind;

	private Integer hasdrugstotreatind;

	private Integer lowestlevelind;

	private Integer parentind;

	private Integer toplevelind;

    public FdbMedcondextvocab() {
    }

	public FdbMedcondextvocabPK getId() {
		return this.id;
	}

	public void setId(FdbMedcondextvocabPK id) {
		this.id = id;
	}
	
	public Integer getClinicallinkind() {
		return this.clinicallinkind;
	}

	public void setClinicallinkind(Integer clinicallinkind) {
		this.clinicallinkind = clinicallinkind;
	}

	public String getDescabbrev1() {
		return this.descabbrev1;
	}

	public void setDescabbrev1(String descabbrev1) {
		this.descabbrev1 = descabbrev1;
	}

	public String getDescabbrev2() {
		return this.descabbrev2;
	}

	public void setDescabbrev2(String descabbrev2) {
		this.descabbrev2 = descabbrev2;
	}

	public String getDescabbrev3() {
		return this.descabbrev3;
	}

	public void setDescabbrev3(String descabbrev3) {
		this.descabbrev3 = descabbrev3;
	}

	public String getDescabbrev4() {
		return this.descabbrev4;
	}

	public void setDescabbrev4(String descabbrev4) {
		this.descabbrev4 = descabbrev4;
	}

	public String getDescabbrev5() {
		return this.descabbrev5;
	}

	public void setDescabbrev5(String descabbrev5) {
		this.descabbrev5 = descabbrev5;
	}

	public String getDescabbrev6() {
		return this.descabbrev6;
	}

	public void setDescabbrev6(String descabbrev6) {
		this.descabbrev6 = descabbrev6;
	}

	public String getDescprimary1() {
		return this.descprimary1;
	}

	public void setDescprimary1(String descprimary1) {
		this.descprimary1 = descprimary1;
	}

	public String getDescprimary2() {
		return this.descprimary2;
	}

	public void setDescprimary2(String descprimary2) {
		this.descprimary2 = descprimary2;
	}

	public String getDescprimary3() {
		return this.descprimary3;
	}

	public void setDescprimary3(String descprimary3) {
		this.descprimary3 = descprimary3;
	}

	public String getDescprimary4() {
		return this.descprimary4;
	}

	public void setDescprimary4(String descprimary4) {
		this.descprimary4 = descprimary4;
	}

	public String getDescprimary5() {
		return this.descprimary5;
	}

	public void setDescprimary5(String descprimary5) {
		this.descprimary5 = descprimary5;
	}

	public String getDescprimary6() {
		return this.descprimary6;
	}

	public void setDescprimary6(String descprimary6) {
		this.descprimary6 = descprimary6;
	}

	public String getDescsecondary1() {
		return this.descsecondary1;
	}

	public void setDescsecondary1(String descsecondary1) {
		this.descsecondary1 = descsecondary1;
	}

	public String getDescsecondary2() {
		return this.descsecondary2;
	}

	public void setDescsecondary2(String descsecondary2) {
		this.descsecondary2 = descsecondary2;
	}

	public String getDescsecondary3() {
		return this.descsecondary3;
	}

	public void setDescsecondary3(String descsecondary3) {
		this.descsecondary3 = descsecondary3;
	}

	public String getDescsecondary4() {
		return this.descsecondary4;
	}

	public void setDescsecondary4(String descsecondary4) {
		this.descsecondary4 = descsecondary4;
	}

	public String getDescsecondary5() {
		return this.descsecondary5;
	}

	public void setDescsecondary5(String descsecondary5) {
		this.descsecondary5 = descsecondary5;
	}

	public String getDescsecondary6() {
		return this.descsecondary6;
	}

	public void setDescsecondary6(String descsecondary6) {
		this.descsecondary6 = descsecondary6;
	}

	public String getDesctertiary1() {
		return this.desctertiary1;
	}

	public void setDesctertiary1(String desctertiary1) {
		this.desctertiary1 = desctertiary1;
	}

	public String getDesctertiary2() {
		return this.desctertiary2;
	}

	public void setDesctertiary2(String desctertiary2) {
		this.desctertiary2 = desctertiary2;
	}

	public String getDesctertiary3() {
		return this.desctertiary3;
	}

	public void setDesctertiary3(String desctertiary3) {
		this.desctertiary3 = desctertiary3;
	}

	public String getDesctertiary4() {
		return this.desctertiary4;
	}

	public void setDesctertiary4(String desctertiary4) {
		this.desctertiary4 = desctertiary4;
	}

	public String getDesctertiary5() {
		return this.desctertiary5;
	}

	public void setDesctertiary5(String desctertiary5) {
		this.desctertiary5 = desctertiary5;
	}

	public String getDesctertiary6() {
		return this.desctertiary6;
	}

	public void setDesctertiary6(String desctertiary6) {
		this.desctertiary6 = desctertiary6;
	}

	public Integer getDruglinkind() {
		return this.druglinkind;
	}

	public void setDruglinkind(Integer druglinkind) {
		this.druglinkind = druglinkind;
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

	public String getExtvocabid() {
		return this.extvocabid;
	}

	public void setExtvocabid(String extvocabid) {
		this.extvocabid = extvocabid;
	}

	public String getExtvocabnametypecode() {
		return this.extvocabnametypecode;
	}

	public void setExtvocabnametypecode(String extvocabnametypecode) {
		this.extvocabnametypecode = extvocabnametypecode;
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

	public Integer getParentind() {
		return this.parentind;
	}

	public void setParentind(Integer parentind) {
		this.parentind = parentind;
	}

	public Integer getToplevelind() {
		return this.toplevelind;
	}

	public void setToplevelind(Integer toplevelind) {
		this.toplevelind = toplevelind;
	}

}