package org.tolven.provider.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tolven.core.entity.Account;

/**
 * Represents one provider. This is a cross-account table populated, usually, by members of eCHR accounts.
 * There is nothing "official" about this list. Any account can create an entry in this table.
 * Affiliations will most likely be used by people to filter the list to a useful subset.
 * @author John Churin
 *
 */
@Entity
@Table
public class Provider implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="PROVIDER_SEQ_GEN")
    private long id;

	@Column
    private String name;

	@Column
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProviderType providerType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Account ownerAccount;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date showFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date showTo;
    
    @OneToMany(mappedBy = "provider", cascade=CascadeType.ALL)
    private Set<ProviderLocation> locations = null;

    @OneToMany(mappedBy = "provider", cascade=CascadeType.ALL)
    private Set<ProviderSpecialty> specialties = null;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    /**
     * The account that owns this provider entry.
     * A single provider entry can have other account affiliates. 
     */
	public Account getOwnerAccount() {
		return ownerAccount;
	}

	public void setOwnerAccount(Account ownerAccount) {
		this.ownerAccount = ownerAccount;
	}

	public Date getShowFrom() {
		return showFrom;
	}

	public void setShowFrom(Date showFrom) {
		this.showFrom = showFrom;
	}

	public Date getShowTo() {
		return showTo;
	}

	public void setShowTo(Date showTo) {
		this.showTo = showTo;
	}

	public Set<ProviderLocation> getLocations() {
		return locations;
	}

	public void setLocations(Set<ProviderLocation> locations) {
		this.locations = locations;
	}

	public void addLocation( ProviderLocation location ) {
		if (locations==null) locations = new HashSet<ProviderLocation>();
		locations.add(location);
	}

	public Set<ProviderSpecialty> getSpecialties() {
		if (specialties==null) specialties = new HashSet<ProviderSpecialty>();
		return specialties;
	}

	public void addSpecialty( ProviderSpecialty specialty ) {
		getSpecialties().add(specialty);
	}

	public void setSpecialties(Set<ProviderSpecialty> specialties) {
		this.specialties = specialties;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Provider)) return false;
		return ((Provider)obj).id==id;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ProviderType getProviderType() {
		return providerType;
	}

	public void setProviderType(ProviderType providerType) {
		this.providerType = providerType;
	}
    
}
