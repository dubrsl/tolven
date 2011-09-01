package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import javax.persistence.Column;


/**
 * The semantic link between activities.
 * 
 * BRIDG: In the particular case where the activities are analysis tasks, this is
 * a verb phrase that specifies the semantic link between two AnalysisTasks.
 * Examples:
 * (1) specification that a particular value in the response to one AnalysisTask
 * dictates navigation to another AnalysisTask (e.g., if analysis of the
 * distribution of the data shows that it is not normally distributed, you would
 * navigate to the non-parametric version of the statistical test)
 * (2) the value of a response may be determined from the value of a set of other
 * fields (e.g., the standard error of the mean is derived from the mean, the
 * standard deviation and the sample size).
 * (3) the behavior of a field for another AnalysisTask is determined by the value
 * of a response (e.g., the choice of prior distribution changes your Bayesian
 * model).
 * @version 1.0
 * @created 27-Sep-2006 9:55:09 AM
 */
@Entity
@Table
public class ActivityRelationship implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * A description of the relationship between two activities. 
	 */
	@Column private String comments;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	@ManyToOne private Activity activity;

	public ActivityRelationship(){

	}

	public void finalize() throws Throwable {

	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}