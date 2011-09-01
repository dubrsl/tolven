package org.tolven.gen.bean;

import java.io.Serializable;

public class GenControlPHRAccount extends GenControlAccount  implements Serializable {

	String familyName;

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer( 200 );
		sb.append( "GenControl PHR Account: ");
		sb.append(getChrAccountId());
		sb.append(" Family Name: ");
		sb.append(getFamilyName());
		sb.append( " Now: ");
		sb.append(getNow());
		return sb.toString();
	}

}
