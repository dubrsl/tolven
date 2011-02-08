package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Requirements that, if met, determine the subject is eligible to participate in
 * a clinical trial.
 * @version 1.0
 * @created 26-Sep-2006 12:18:38 PM
 */
@Entity
@DiscriminatorValue("INC")
public class Inclusion extends EligibilityCriteria implements Serializable {

	private static final long serialVersionUID = 1L;


	public Inclusion(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}