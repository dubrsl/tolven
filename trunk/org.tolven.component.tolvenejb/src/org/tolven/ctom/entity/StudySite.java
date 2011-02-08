package org.tolven.ctom.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


/**
 * A health care site in which clinical trial activities are conducted.
 * @version 1.0
 * @created 27-Sep-2006 9:51:51 AM
 */
@Entity
@Table
public class StudySite implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * The role the site is playing in that study.  CTEP roles -- Lead organization,
	 * participating organization, etc.
	 */
	@Column private String siteRole;
	/**
	 * A code specifying whether the state of participation of a site in the given
	 * Study is pending, active, complete, or cancelled.
	 */
	@Column private String status;
	@ManyToOne private HealthCareSite healthCareSite;
	@ManyToOne private Study study;

	@OneToMany(mappedBy = "studySite", cascade=CascadeType.ALL, fetch = FetchType.LAZY) 
	private Set<SubjectAssignment> subjectAssignments;

	public Set<SubjectAssignment> getSubjectAssignments() {
		if (subjectAssignments==null) subjectAssignments = new HashSet<SubjectAssignment>();
	return subjectAssignments;
	}

	public void addSubjectAssignment( SubjectAssignment subjectAssignment ) {
		getSubjectAssignments().add(subjectAssignment);
		subjectAssignment.setStudySite(this);
	}
	
	public void setSubjectAssignments(Set<SubjectAssignment> subjectAssignments) {
		this.subjectAssignments = subjectAssignments;
	}

	
	public StudySite(){

	}

	public void finalize() throws Throwable {

	}

	public HealthCareSite getHealthCareSite() {
		return healthCareSite;
	}

	public void setHealthCareSite(HealthCareSite healthCareSite) {
		this.healthCareSite = healthCareSite;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSiteRole() {
		return siteRole;
	}

	public void setSiteRole(String siteRole) {
		this.siteRole = siteRole;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

}