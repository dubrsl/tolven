package org.tolven.provider.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.tolven.core.entity.Account;
/**
 * This class provides a reverse path back to the account originating a provider link. 
 * While a MyProvider has a unique id, it also refers to the provider as well as the account
 * to which it applies. There is one MyProvider record per account that accesses a provider.
 * @author John Churin
 *
 */
@Entity
@Table
public class MyProvider implements Serializable {

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="PROVIDER_SEQ_GEN")
    private long id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Provider provider;

	@Column
    private String status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    

}
