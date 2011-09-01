package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_ind database table.
 * 
 */
@Entity
@Table(name="fdb_ind")
public class FdbInd implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbIndPK id;

	private String descdrug1;

	private String descdrug2;

	private String descdrug3;

	private String descdrug4;

	private String descdrug5;

	private String descdrug6;

	private String descindication1;

	private String descindication2;

	private String descindication3;

	private String descindication4;

	private String descindication5;

	private String descindication6;

	private String dxid;

	private String fdbdx;

	private String labeledcode;

	private String predictorcode;

    public FdbInd() {
    }

	public FdbIndPK getId() {
		return this.id;
	}

	public void setId(FdbIndPK id) {
		this.id = id;
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

	public String getDescindication1() {
		return this.descindication1;
	}

	public void setDescindication1(String descindication1) {
		this.descindication1 = descindication1;
	}

	public String getDescindication2() {
		return this.descindication2;
	}

	public void setDescindication2(String descindication2) {
		this.descindication2 = descindication2;
	}

	public String getDescindication3() {
		return this.descindication3;
	}

	public void setDescindication3(String descindication3) {
		this.descindication3 = descindication3;
	}

	public String getDescindication4() {
		return this.descindication4;
	}

	public void setDescindication4(String descindication4) {
		this.descindication4 = descindication4;
	}

	public String getDescindication5() {
		return this.descindication5;
	}

	public void setDescindication5(String descindication5) {
		this.descindication5 = descindication5;
	}

	public String getDescindication6() {
		return this.descindication6;
	}

	public void setDescindication6(String descindication6) {
		this.descindication6 = descindication6;
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

	public String getLabeledcode() {
		return this.labeledcode;
	}

	public void setLabeledcode(String labeledcode) {
		this.labeledcode = labeledcode;
	}

	public String getPredictorcode() {
		return this.predictorcode;
	}

	public void setPredictorcode(String predictorcode) {
		this.predictorcode = predictorcode;
	}

}