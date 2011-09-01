package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ddcm database table.
 * 
 */
@Entity
@Table(name="fdb_ddcm")
public class FdbDdcm implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDdcmPK id;

	private String descdisease1;

	private String descdisease2;

	private String descdisease3;

	private String descdisease4;

	private String descdisease5;

	private String descdisease6;

	private String descdrug1;

	private String descdrug2;

	private String descdrug3;

	private String descdrug4;

	private String descdrug5;

	private String descdrug6;

	private String dxid;

	private String fdbdx;

	private String pagereference;

	private String sevlevelcode;

    public FdbDdcm() {
    }

	public FdbDdcmPK getId() {
		return this.id;
	}

	public void setId(FdbDdcmPK id) {
		this.id = id;
	}
	
	public String getDescdisease1() {
		return this.descdisease1;
	}

	public void setDescdisease1(String descdisease1) {
		this.descdisease1 = descdisease1;
	}

	public String getDescdisease2() {
		return this.descdisease2;
	}

	public void setDescdisease2(String descdisease2) {
		this.descdisease2 = descdisease2;
	}

	public String getDescdisease3() {
		return this.descdisease3;
	}

	public void setDescdisease3(String descdisease3) {
		this.descdisease3 = descdisease3;
	}

	public String getDescdisease4() {
		return this.descdisease4;
	}

	public void setDescdisease4(String descdisease4) {
		this.descdisease4 = descdisease4;
	}

	public String getDescdisease5() {
		return this.descdisease5;
	}

	public void setDescdisease5(String descdisease5) {
		this.descdisease5 = descdisease5;
	}

	public String getDescdisease6() {
		return this.descdisease6;
	}

	public void setDescdisease6(String descdisease6) {
		this.descdisease6 = descdisease6;
	}

	public String getDescdrug1() {
		return this.descdrug1;
	}

	public void setDescdrug1(String descdrug1) {
		this.descdrug1 = descdrug1;
	}

	public String getDescdrug2() {
		return this.descdrug2;
	}

	public void setDescdrug2(String descdrug2) {
		this.descdrug2 = descdrug2;
	}

	public String getDescdrug3() {
		return this.descdrug3;
	}

	public void setDescdrug3(String descdrug3) {
		this.descdrug3 = descdrug3;
	}

	public String getDescdrug4() {
		return this.descdrug4;
	}

	public void setDescdrug4(String descdrug4) {
		this.descdrug4 = descdrug4;
	}

	public String getDescdrug5() {
		return this.descdrug5;
	}

	public void setDescdrug5(String descdrug5) {
		this.descdrug5 = descdrug5;
	}

	public String getDescdrug6() {
		return this.descdrug6;
	}

	public void setDescdrug6(String descdrug6) {
		this.descdrug6 = descdrug6;
	}

	public String getDxid() {
		return this.dxid;
	}

	public void setDxid(String dxid) {
		this.dxid = dxid;
	}

	public String getFdbdx() {
		return this.fdbdx;
	}

	public void setFdbdx(String fdbdx) {
		this.fdbdx = fdbdx;
	}

	public String getPagereference() {
		return this.pagereference;
	}

	public void setPagereference(String pagereference) {
		this.pagereference = pagereference;
	}

	public String getSevlevelcode() {
		return this.sevlevelcode;
	}

	public void setSevlevelcode(String sevlevelcode) {
		this.sevlevelcode = sevlevelcode;
	}

}