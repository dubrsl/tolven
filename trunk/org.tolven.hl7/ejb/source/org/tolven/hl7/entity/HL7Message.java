package org.tolven.hl7.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table
public class HL7Message {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "APP_SEQ_GEN")
	private long id;

	@Column
	private String placeholderPath;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column
	private byte[] xml01;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPlaceholderPath() {
		return placeholderPath;
	}

	public void setPlaceholderPath(String placeholderPath) {
		this.placeholderPath = placeholderPath;
	}

	public byte[] getXml01() {
		return xml01;
	}

	public void setXml01(byte[] xml01) {
		this.xml01 = xml01;
	}
}
