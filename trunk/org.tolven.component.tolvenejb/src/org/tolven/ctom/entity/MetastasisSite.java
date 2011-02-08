package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


/**
 * Sites of metastatic neoplasm.
 * 
 * From CAP Breast model's metastasisAnatomicSite
 * @version 1.0
 * @created 27-Sep-2006 9:58:02 AM
 */
@Entity
@Table
public class MetastasisSite implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Name of site(s) within the body targeted for procedures or interventions;
	 * multiple contiguous sites within the same organ system may be referenced.
	 */
	@Column private String bodySiteName;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	@ManyToOne private CancerStaging cancerStaging;

	public MetastasisSite(){

	}

	public void finalize() throws Throwable {

	}

	public String getBodySiteName() {
		return bodySiteName;
	}

	public void setBodySiteName(String bodySiteName) {
		this.bodySiteName = bodySiteName;
	}

	public CancerStaging getCancerStaging() {
		return cancerStaging;
	}

	public void setCancerStaging(CancerStaging cancerStaging) {
		this.cancerStaging = cancerStaging;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}