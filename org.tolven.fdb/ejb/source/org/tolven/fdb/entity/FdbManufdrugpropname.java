package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_manufdrugpropname database table.
 * 
 */
@Entity
@Table(name="fdb_manufdrugpropname")
public class FdbManufdrugpropname implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String propertyname;

	private String description;

	private String regionaldescription;

    public FdbManufdrugpropname() {
    }

	public String getPropertyname() {
		return this.propertyname;
	}

	public void setPropertyname(String propertyname) {
		this.propertyname = propertyname;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRegionaldescription() {
		return this.regionaldescription;
	}

	public void setRegionaldescription(String regionaldescription) {
		this.regionaldescription = regionaldescription;
	}

}