package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_image database table.
 * 
 */
@Entity
@Table(name="fdb_image")
public class FdbImage implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbImagePK id;

	private String imagefilename;

	private String stopdate;

    public FdbImage() {
    }

	public FdbImagePK getId() {
		return this.id;
	}

	public void setId(FdbImagePK id) {
		this.id = id;
	}
	
	public String getImagefilename() {
		return this.imagefilename;
	}

	public void setImagefilename(String imagefilename) {
		this.imagefilename = imagefilename;
	}

	public String getStopdate() {
		return this.stopdate;
	}

	public void setStopdate(String stopdate) {
		this.stopdate = stopdate;
	}

}