package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_version database table.
 * 
 */
@Entity
@Table(name="fdb_version")
public class FdbVersion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer versionkey;

	private String buildversion;

	private String dbtype;

	private String dbversion;

	private String frequency;

	private String issuedate;

	private String versioncomment;

    public FdbVersion() {
    }

	public Integer getVersionkey() {
		return this.versionkey;
	}

	public void setVersionkey(Integer versionkey) {
		this.versionkey = versionkey;
	}

	public String getBuildversion() {
		return this.buildversion;
	}

	public void setBuildversion(String buildversion) {
		this.buildversion = buildversion;
	}

	public String getDbtype() {
		return this.dbtype;
	}

	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	public String getDbversion() {
		return this.dbversion;
	}

	public void setDbversion(String dbversion) {
		this.dbversion = dbversion;
	}

	public String getFrequency() {
		return this.frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getIssuedate() {
		return this.issuedate;
	}

	public void setIssuedate(String issuedate) {
		this.issuedate = issuedate;
	}

	public String getVersioncomment() {
		return this.versioncomment;
	}

	public void setVersioncomment(String versioncomment) {
		this.versioncomment = versioncomment;
	}

}