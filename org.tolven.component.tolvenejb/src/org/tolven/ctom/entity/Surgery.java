package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * A procedure to remove or repair a part of the body or to establish whether or
 * not disease is present.
 * @version 1.0
 * @created 27-Sep-2006 9:54:25 AM
 */
@Entity
@DiscriminatorValue("SURG")
public class Surgery extends Procedure implements Serializable {

	private static final long serialVersionUID = 1L;


	public Surgery(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}