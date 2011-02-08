package org.tolven.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
public class AccountTemplate implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	private String knownType;
	
	@OneToOne()
	private Account account;
  
	/**
	 * Corresponds to knownType of AccountType. Primary key of this table.
	 * @return
	 */
	public String getKnownType() {
		return knownType;
	}
	
	public void setKnownType(String knownType) {
		this.knownType = knownType;
	}
	
	public Account getAccount() {
		return account;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
  
}
