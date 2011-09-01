package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_labeler database table.
 * 
 */
@Entity
@Table(name="fdb_labeler")
public class FdbLabeler implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String labelerid;

	private String mfgname;

	private String ndcmfgcode;

    public FdbLabeler() {
    }

	public String getLabelerid() {
		return this.labelerid;
	}

	public void setLabelerid(String labelerid) {
		this.labelerid = labelerid;
	}

	public String getMfgname() {
		return this.mfgname;
	}

	public void setMfgname(String mfgname) {
		this.mfgname = mfgname;
	}

	public String getNdcmfgcode() {
		return this.ndcmfgcode;
	}

	public void setNdcmfgcode(String ndcmfgcode) {
		this.ndcmfgcode = ndcmfgcode;
	}

}