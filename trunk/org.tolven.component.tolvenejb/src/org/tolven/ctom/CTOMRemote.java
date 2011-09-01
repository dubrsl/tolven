package org.tolven.ctom;

import java.util.List;

import javax.jws.WebMethod;

import org.tolven.ctom.entity.Study;
import org.tolven.ctom.entity.Subject;

public interface CTOMRemote {

	/**
	 * Persist a new study
	 * @param study
	 * @return the id of the new Study
	 */
	public long persist( Study study );

	/**
	 * Return a list of all subject known to the system (limited to 100)
	 * @return
	 */
	public List<Subject> findSubjects();

}
