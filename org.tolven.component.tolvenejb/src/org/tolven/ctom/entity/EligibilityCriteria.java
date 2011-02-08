package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


/**
 * Requirements that must be met for subjects to be included in a study. These
 * requirements ensure that subjects on a study are similar to each other in terms
 * of specific factors such as age, type and stage of cancer, general health and
 * treatment history.
 * @version 1.0
 * @created 27-Sep-2006 9:50:08 AM
 */
@Entity
@Table
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("ELIG")
public class EligibilityCriteria implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * Indicates the expected answer to the eligibility question. Values include: Yes,
	 * No
	 */
	@Column private String expectedAnswer;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	/**
	 * The complete text of an individual question/criterion on the eligibility
	 * checklist of a protocol.
	 */
	@Column private String question;
	@ManyToOne private Study study;

	public EligibilityCriteria(){

	}

	public void finalize() throws Throwable {

	}

	public String getExpectedAnswer() {
		return expectedAnswer;
	}

	public void setExpectedAnswer(String expectedAnswer) {
		this.expectedAnswer = expectedAnswer;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

}