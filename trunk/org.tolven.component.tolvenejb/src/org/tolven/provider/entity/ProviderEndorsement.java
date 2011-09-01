package org.tolven.provider.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tolven.core.entity.Account;

/**
 * Each row represents an endorsement by an account for a provider record.
 * For example, account HilltopClinic might endorse ValleyClinic. In this way, a
 * user may wish to see a filtered list of providers rather than anyone posing as a provider.  
 * @author John Churin
 *
 */
@Entity
@Table
public class ProviderEndorsement implements Serializable {

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="PROVIDER_SEQ_GEN")
    private long id;

    @ManyToOne
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account endorsingAccount;

    @Column
    private String comment;

	@Column
    private String status;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date effectiveTimeFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date effectiveTimeTo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public Account getEndorsingAccount() {
		return endorsingAccount;
	}

	public void setEndorsingAccount(Account endorsingAccount) {
		this.endorsingAccount = endorsingAccount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEffectiveTimeFrom() {
		return effectiveTimeFrom;
	}

	public void setEffectiveTimeFrom(Date effectiveTimeFrom) {
		this.effectiveTimeFrom = effectiveTimeFrom;
	}

	public Date getEffectiveTimeTo() {
		return effectiveTimeTo;
	}

	public void setEffectiveTimeTo(Date effectiveTimeTo) {
		this.effectiveTimeTo = effectiveTimeTo;
	}
    
    
}
