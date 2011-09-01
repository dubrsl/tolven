package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_subset_site database table.
 * 
 */
@Entity
@Table(name="fdb_subset_site")
public class FdbSubsetSite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String siteid;

	private String descaltsearch;

	private String descdisplay;

	private String descsearch;

	private String sitecomment;

    public FdbSubsetSite() {
    }

	public String getSiteid() {
		return this.siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public String getDescaltsearch() {
		return this.descaltsearch;
	}

	public void setDescaltsearch(String descaltsearch) {
		this.descaltsearch = descaltsearch;
	}

	public String getDescdisplay() {
		return this.descdisplay;
	}

	public void setDescdisplay(String descdisplay) {
		this.descdisplay = descdisplay;
	}

	public String getDescsearch() {
		return this.descsearch;
	}

	public void setDescsearch(String descsearch) {
		this.descsearch = descsearch;
	}

	public String getSitecomment() {
		return this.sitecomment;
	}

	public void setSitecomment(String sitecomment) {
		this.sitecomment = sitecomment;
	}

}