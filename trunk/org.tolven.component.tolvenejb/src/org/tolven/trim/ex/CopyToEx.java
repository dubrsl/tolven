package org.tolven.trim.ex;

import java.io.Serializable;

import org.tolven.provider.entity.PatientLink;
import org.tolven.provider.entity.Provider;
import org.tolven.trim.CopyTo;

@SuppressWarnings("serial")
public class CopyToEx extends CopyTo implements Serializable {

	private PatientLink patientLink;
	private Provider provider;
	
	public Boolean getCopy() {
        return copy;
    }

	public PatientLink getPatientLink() {
		return patientLink;
	}

	public void setPatientLink(PatientLink patientLink) {
		this.patientLink = patientLink;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CopyToEx)) return false;
		CopyToEx other = (CopyToEx) obj;
		if (this.accountId!=null 
			&& other.accountId!=null 
			&& this.accountId!=other.accountId) return false;
		if (this.providerId!=null 
			&& other.providerId!=null 
			&& this.providerId!=other.providerId) return false;
		if (this.patientLinkId!=null 
			&& other.patientLinkId!=null 
			&& this.patientLinkId!=other.patientLinkId) return false;
		return true;
	}

	@Override
	public int hashCode() {
		long hashBase = 0;
		if (providerId != null) hashBase += providerId;
		if (accountId != null) hashBase += accountId;
		if (patientLinkId != null) hashBase += patientLinkId;
		return new Long(hashBase).hashCode();
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
}
