package org.tolven.provider.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.tolven.app.entity.MenuData;
/**
 * <p>The PatientLink entity establishes a relationship between a provider's internal representation of a patient (in MenuData)
 * and one more external accounts that may communicate information related to that patient. From the provider's
 * perspective, PatientLinks enumerate each relationship between an internal patient (in MenuData) and an external
 * entity (any other clinical or personal account).</p>
 * <p>Tolven does not use this link to extend the view of patient data by either party. For example, 
 * this link can be revoked by either party at any time. Doing so has no effect on the data currently held
 * by either party. However, it does affect the ability of either party to communicate regarding this patient. 
 * This link is equally useful between a citizen and provider as it is between two providers.
 * While a PatientLink represents an implicitly bi-directional communication path, each of the roles is
 * unique: </p>
 * <ul>
 * <li>myPatient is the patient not owned by the provider. For example, if a citizen contacts a provider for the 
 * purpose of becoming a patient of the provider, this is the citizen's patient or medical record.</li>  
 * <li>providerPatient is the patient owned by the provider. the typical case is the physician that maintains
 * records about the patients they see.</li>  
 * </ul>
 * <p>A patientLink is by no means a mechanism for sharing a list of patients. This mechanism cannot be used to
 * allow one account to see all patients of another account. The linkage is very specific. However, in practice, the
 * patientLink is somewhat mis-named because it can be used to link any placeholder in one account to a placeholder in
 * another account. Specimen, research subject, provider (credentialing) etc.</p>
 * <p>In the typical flow, the MyPatient association is established first, then the provider "accepts" the patient by
 * assigning a providerPatient (which may be new or not).</p>
 * @author John Churin
 *
 */
@Entity
@Table
public class PatientLink implements Serializable {

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="PROVIDER_SEQ_GEN")
    private long id;

    @ManyToOne
    private Provider provider;

    @ManyToOne
    private MenuData myPatient;

    @ManyToOne
    private MenuData providerPatient;

    @Column
    private String request;

    @Column
    private String status;

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

	public MenuData getMyPatient() {
		return myPatient;
	}

	public void setMyPatient(MenuData myPatient) {
		this.myPatient = myPatient;
	}

	public MenuData getProviderPatient() {
		return providerPatient;
	}

	public void setProviderPatient(MenuData providerPatient) {
		this.providerPatient = providerPatient;
	}

	/**
	 * Status of the relationship active or obsolete. new may also be used. Only active links allow for communication.
	 * Revokable by either party at any time. Cannot be reinstated.
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * A brief description of the request to become a patient of the provider
	 * @return
	 */
	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}
    
}
