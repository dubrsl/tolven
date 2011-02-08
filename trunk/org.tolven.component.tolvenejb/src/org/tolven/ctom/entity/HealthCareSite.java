package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Column;


/**
 * A facility or organization  that provides healthcare, which may be associated
 * with the execution of clinical trial(s).
 * @version 1.0
 * @created 27-Sep-2006 9:51:44 AM
 */
@Entity
@DiscriminatorValue("SITE")
public class HealthCareSite extends Organization implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * A unique alpha-numeric code assigned by NCI to institution where the
	 * patient/participant was originally registered on study (institution where the
	 * patient signed the informed consent form). The code is assigned to
	 * protocol/records to identify the institution(s) participating in NCI-sponsored
	 * clinical trials and investigator records to identify address location.
	 */
	@Column private String nciInstituteCode;

	public HealthCareSite(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getNciInstituteCode() {
		return nciInstituteCode;
	}

	public void setNciInstituteCode(String nciInstituteCode) {
		this.nciInstituteCode = nciInstituteCode;
	}

}