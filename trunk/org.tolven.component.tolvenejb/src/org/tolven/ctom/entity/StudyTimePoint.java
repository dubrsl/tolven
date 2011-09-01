package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Describes the point in time of the study schedule when an activity has taken
 * place.
 * @version 1.0
 * @created 27-Sep-2006 9:53:05 AM
 */
@Entity
@Table
public class StudyTimePoint implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Number of the course (cycle) of treatment the patient received.
	 */
	@Column private int courseNumber;
	/**
	 * The begin java.util.Date for a course (cycle) of a protocol.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date courseStartDate;
	/**
	 * The java.util.Date that a course (cycle) as defined by a protocol is completed.
	 */
	@Temporal(TemporalType.TIMESTAMP) @Column private java.util.Date courseStopDate;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * The proposed purpose for a visit as determined by the study.  
	 */
	@Column private String visitName;
	@ManyToOne private Activity activity;

	public StudyTimePoint(){

	}

	public void finalize() throws Throwable {

	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public int getCourseNumber() {
		return courseNumber;
	}

	public void setCourseNumber(int courseNumber) {
		this.courseNumber = courseNumber;
	}

	public java.util.Date getCourseStartDate() {
		return courseStartDate;
	}

	public void setCourseStartDate(java.util.Date courseStartDate) {
		this.courseStartDate = courseStartDate;
	}

	public java.util.Date getCourseStopDate() {
		return courseStopDate;
	}

	public void setCourseStopDate(java.util.Date courseStopDate) {
		this.courseStopDate = courseStopDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getVisitName() {
		return visitName;
	}

	public void setVisitName(String visitName) {
		this.visitName = visitName;
	}

}