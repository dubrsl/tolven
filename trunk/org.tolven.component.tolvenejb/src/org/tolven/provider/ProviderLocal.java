package org.tolven.provider;

import java.util.List;
import java.util.Set;

import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.Account;
import org.tolven.provider.entity.MyProvider;
import org.tolven.provider.entity.PatientLink;
import org.tolven.provider.entity.Provider;
import org.tolven.provider.entity.Specialty;
/**
 * Services for managing providers. A provider is associated with an account and is visible across
 * accounts.
 * @author John Churin
 *
 */
public interface ProviderLocal {

    /**
	 * Get a provider by its internal ID
	 * @param providerId the id of the provider (this is the internal id)
	 * @return the provider object
	 */
	public Provider findProvider( long providerId );
	
	/**
	 * Get a Specialty by its internal ID
	 * @param specialtyId the id of the Specialty (this is the internal id)
	 * @return the Specialty object
	 */
	public Specialty findSpecialty( long specialtyId );
	
	/**
	 * Persist a new or updated specialty
	 * @param specialty
	 */
	public void persistSpecialty( Specialty specialty );
	
	/**
	 * Find all providers for an account.
	 * @param accountId
	 * @return The list of provider entries owned by the requested account
	 */
	public List<Provider> findAccountProviders( long accountId, boolean includeInactive );

	/**
	 * Update an existing provider
	 * @param provider
	 */
	public void updateProvider( Provider provider );

	/**
	 * Persist a new provider
	 * @param provider
	 */
	public void createProvider( Provider provider );

	/**
	 * Return a list of providers endorsed by this provider. 
	 * @param provider with endorsements to it
	 * @return 
	 */
	public List<Provider> findEndorsedProviders( Provider provider );
	
	/**
	 * Return a list of providers endorsed by this provider. 
	 * @param providerId with endorsements to it
	 * @return The list of providers with endorsements
	 */
	public List<Provider> findEndorsedProviders( long providerId );
	
	/**
	 * Return a list of "root" providers, that is, providers endorsed by a null account. 
	 * @return 
	 */
	public List<Provider> findRootProviders( );

	/**
	 * Return a list of the providers selected by a personal account
	 * @param account
	 * @return List of MyProvider objects, which will point to providers.
	 */
	public List<MyProvider> findMyProviders( long personalAccountId );

	/**
	 * Return a list of the active providers selected by a personal account
	 * @param account
	 * @return List of MyProvider objects, which will point to providers.
	 */
	public List<MyProvider> findMyActiveProviders( long personalAccountId );

	/**
	 * Add a provider to the account's list of providers allows replies from.
	 * This provides a return path.
	 * @param account This account
	 * @param providerId The provider id that this account will now allow to send responses back.
	 */
	public void addMyProvider(Account account, long providerId );
	
	/**
	 * Return a list of matching providers given the criteria and limit.
	 * This method supports four variations. 
	 * <ul>
	 * <li>If the list of specialties is null or empty and zip is null or empty
	 * then it returns all providers up to the limit specified.</li>
	 * <li>If the list of specialties is null or empty then a list of providers in or near
	 * the zip/postal code are returned.</li>
	 * <li>If the zip code is null or empty, then a list of providers matching at least one of the specialties
	 * is returned.</li>
	 * <li>If both the specialties list and the zip code are specified, then a list of providers matching that
	 * criteria is returned.
	 * </ol>
	 * @param specialties
	 * @param zip
	 * @param limit
	 * @return
	 */
	public List<Provider> findProviders( List<Specialty> specialties, String zip, int limit);

	/**
	 * Create a new patientLink that relates a patient to a provider. This is only one half of a completed
	 * patient link as the provider must complete the link by providing the provider own patient record (providerPatient).
	 * @param provider
	 * @param menuData of the patient placeholder (typically ephr)
	 * @return
	 */
	public PatientLink createPatientLink( Provider provider, MenuData myPatient, String request);
	
	/**
	 * Find a patientLink by its internal ID
	 * @param specialtyId the id of the PatientLink (this is the internal id)
	 * @return the PatientLink object
	 */
	public PatientLink findPatientLink( long patientLinkId );

	/**
	 * Add (or change) a provider in a patient link
	 */
	public void addProviderToPatientLink(long patientLinkId, long providerId);
	
	/**
	 * Create a new patientLink that relates a patient to a provider. This is only one half of a completed
	 * patient link as the provider must complete the link by providing the provider own patient record (providerPatient).
	 * @param provider
	 * @param menuData of the patient placeholder (typically ephr)
	 * @return
	 */
	public PatientLink createPatientLink( long providerId, MenuData myPatient, String request);
	
	/**
	 * The providerAcceptPatientLink method is a provider-side method that indicates that the provider
	 * has accepted the patient link. Note: This method does not update status. For example, if the patient has
	 * subsequently declined the link, then having the provider accept the link is OK but it won't 
	 * re-activate the link.
	 * @param patientLink
	 * @param menuData of the patient placeholder (typically in echr) to add to the patient link
	 */
	public void providerAcceptPatientLink( PatientLink patientLink, MenuData providerPatient);
	
	/**
	 * The revokePatientLink method is initiated by either side of the link to indicate that the
	 * link should no longer be active.
	 * @param patientLink
	 */
	public void revokePatientLink( PatientLink patientLink, MenuData providerPatient);
	
	/**
	 * Return a list of PatientLinks where the supplied patient is MyPatient (and the link is active).
	 * This essentially returns a list of active providers for this patient.
	 * Note: Some patientLinks may not actually be usable yes: If the provider has not accepted the patient
	 * yet, then providerPatient will be null. If status is not = active, it means the provider has declined the
	 * patient-provider connection.
	 * @param menuData
	 * @return
	 */
	public List<PatientLink> findOutboundPatientLinks(MenuData menuData );

	/**
	 * Return a list of patient links appropriate for a provider: The list normally contains
	 * only one patient link which is the patient. This query does not reveal providers not
	 * owned by the account owning this patient record. For example, if the citizen is a patient of
	 * Valley and Hilltop clinics, and Hilltop initiates this query then only the one patient link is
	 * returned.
	 * This query may return 
	 * @param menuData
	 * @return
	 */
	public List<PatientLink> findInboundPatientLinks(MenuData menuData );
	
	/**
	 * Return a sorted list of all specialties
	 */
	public List<Specialty> findAllSpecialties();
	
	/**
	 * Add and remove provider specialties from a provider as appropriate to yield the list of specialties supplied.
	 */
	public void updateProviderSpecialties(Provider provider, Set<Long> selectedSpecialties);

	/**
	 * checks if provider is endorsed
	 */
	public boolean isProviderEndorsed(long providerId);
}