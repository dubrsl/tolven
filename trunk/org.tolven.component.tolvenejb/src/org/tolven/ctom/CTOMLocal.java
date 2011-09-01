package org.tolven.ctom;

import java.util.List;

import org.tolven.ctom.entity.Study;
import org.tolven.ctom.entity.Subject;

public interface CTOMLocal {

    /**
     * Persist a new study
     * @param study
     * @return the id of the new Study
     */
    public long persist(Study study);

    /**
     * Persist a new subject
     * @param subject
     */
    public long persist(Subject subject);

    /**
     * Return a list of all subject known to the system (limited to 100)
     * @return
     */
    public List<Subject> findSubjects();

}
