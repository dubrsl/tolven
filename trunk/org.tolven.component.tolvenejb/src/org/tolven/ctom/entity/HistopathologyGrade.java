package org.tolven.ctom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;


/**
 * The microscopic classification of degree of abnormality of a neoplasm.
 * @version 1.0
 * @created 27-Sep-2006 9:55:31 AM
 */
@Entity
@Table
public class HistopathologyGrade implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * A description of the grade.
	 */
	@Column private String comments;
	/**
	 * Text grade for the degree of cellular differentiation in a tissue sample. 
	 */
	@Column private String grade;
	/**
	 * The name of system used in grading the findings.  Example: nottingham.
	 */
	@Column private String gradingSystemName;
	/**
	 * The system generated unique identifier.
	 */
	@Id @GeneratedValue(strategy=GenerationType.TABLE, generator="CTOM_SEQ_GEN") private long id;
	@ManyToOne private Histopathology histopathology;

	public HistopathologyGrade(){

	}

	public void finalize() throws Throwable {

	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getGradingSystemName() {
		return gradingSystemName;
	}

	public void setGradingSystemName(String gradingSystemName) {
		this.gradingSystemName = gradingSystemName;
	}

	public Histopathology getHistopathology() {
		return histopathology;
	}

	public void setHistopathology(Histopathology histopathology) {
		this.histopathology = histopathology;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}