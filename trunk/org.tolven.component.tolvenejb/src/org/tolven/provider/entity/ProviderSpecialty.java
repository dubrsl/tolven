package org.tolven.provider.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class ProviderSpecialty  implements Serializable  {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="PROVIDER_SEQ_GEN")
    private long id;

    /**
     * The provider that owns this provider location entry.
     */
    @ManyToOne
    private Provider provider;

    /**
     * The specialty.
     */
    @ManyToOne
    private Specialty specialty;

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

	public Specialty getSpecialty() {
		return specialty;
	}

	public void setSpecialty(Specialty specialty) {
		this.specialty = specialty;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProviderSpecialty)) return false;
		return ((ProviderSpecialty)obj).getSpecialty().getId()== getSpecialty().getId();
	}

	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

}
