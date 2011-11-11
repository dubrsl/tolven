package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_dispensable_image database table.
 * 
 */
@Entity
@Table(name="fdb_dispensable_image")
public class FdbDispensableImage implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FdbDispensableImagePK id;

	private String refrepackagercode;

    public FdbDispensableImage() {
    }

	public FdbDispensableImagePK getId() {
		return this.id;
	}

	public void setId(FdbDispensableImagePK id) {
		this.id = id;
	}
	
	public String getRefrepackagercode() {
		return this.refrepackagercode;
	}

	public void setRefrepackagercode(String refrepackagercode) {
		this.refrepackagercode = refrepackagercode;
	}

}