package org.tolven.gen.bean;

import java.io.Serializable;
import java.util.Date;

public abstract class GenControlBase implements Serializable {

	private static final long serialVersionUID = 1L;
	private long userId;
	private long chrAccountId;
	private Date now;
	private int startYear;

	public long getChrAccountId() {
		return chrAccountId;
	}
	public void setChrAccountId(long chrAccountId) {
		this.chrAccountId = chrAccountId;
	}
	public Date getNow() {
		return now;
	}
	public void setNow(Date now) {
		this.now = now;
	}
	public int getStartYear() {
		return startYear;
	}
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}

}
