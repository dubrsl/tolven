package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_generic_routeddrug_temp database table.
 * 
 */
@Entity
@Table(name="fdb_generic_routeddrug_temp")
public class FdbGenericRouteddrugTemp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Integer tempid;

	private Integer rtgenid;

    public FdbGenericRouteddrugTemp() {
    }

	public Integer getTempid() {
		return this.tempid;
	}

	public void setTempid(Integer tempid) {
		this.tempid = tempid;
	}

	public Integer getRtgenid() {
		return this.rtgenid;
	}

	public void setRtgenid(Integer rtgenid) {
		this.rtgenid = rtgenid;
	}

}