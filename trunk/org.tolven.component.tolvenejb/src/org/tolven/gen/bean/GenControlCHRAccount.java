package org.tolven.gen.bean;

import java.io.Serializable;

public class GenControlCHRAccount extends GenControlAccount implements Serializable{

	private int count;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer( 200 );
		sb.append( "GenControl CHR Account: ");
		sb.append(getChrAccountId());
		sb.append(" Count: ");
		sb.append(getCount());
		sb.append( " Now: ");
		sb.append(getNow());
		return sb.toString();
	}

}
