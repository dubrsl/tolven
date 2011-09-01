package org.tolven.doc.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.tolven.security.DocContentSecurity;

/**
 * Storage of a non-diagnostic image such as fax, jpeg, etc.
 * @author John Churin
 *
 */
@Entity
@DiscriminatorValue("IMG")
public class DocImage extends DocBase implements DocContentSecurity {

	/**
	 * Version number used for serialization
	 */
	private static final long serialVersionUID = 2L;

}
