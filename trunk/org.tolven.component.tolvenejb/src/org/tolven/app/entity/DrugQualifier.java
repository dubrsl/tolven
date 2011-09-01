/**
 * 
 */
package org.tolven.app.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author mohammed
 *
 */
@Entity
@Table(name="drug_qualifiers" , schema="app")
public class DrugQualifier implements Serializable{

	/**
	 * Auto generated Serial Version ID
	 */
	private static final long serialVersionUID = 4845578410348616639L;
	/**
	 * Default Constructor
	 */
	public DrugQualifier(){
		super();
	}
	/**
	 * Parameterised Constructor
	 * @param qualifierCode
	 */
	public DrugQualifier(String qualifierCode){
		this.qualcode = qualifierCode;
	}
	/**
	 * Variable to map to qualcode column in the drug_qualifiers table
	 */
	@Id
	private String qualcode;
	/**
	 * @return the qualcode
	 */
	public String getQualcode() {
		return qualcode;
	}
	/**
	 * @param qualcode the qualcode to set
	 */
	public void setQualcode(String qualcode) {
		this.qualcode = qualcode;
	}
	
	@Column(name="qualdesc")
	private String qualDesc;
	/**
	 * @return the qualDesc
	 */
	public String getQualDesc() {
		return qualDesc;
	}
	/**
	 * @param qualDesc the qualDesc to set
	 */
	public void setQualDesc(String qualDesc) {
		this.qualDesc = qualDesc;
	}
	
	
}
