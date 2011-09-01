package org.tolven.fdb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the fdb_settings database table.
 * 
 */
@Entity
@Table(name="fdb_settings")
public class FdbSetting implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private String settingname;

	private String settingvalue;

    public FdbSetting() {
    }

	public String getSettingname() {
		return this.settingname;
	}

	public void setSettingname(String settingname) {
		this.settingname = settingname;
	}

	public String getSettingvalue() {
		return this.settingvalue;
	}

	public void setSettingvalue(String settingvalue) {
		this.settingvalue = settingvalue;
	}

}