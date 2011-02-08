package org.tolven.provider.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.Account;
import org.tolven.logging.TolvenLogger;
import org.tolven.provider.ProviderLocal;
import org.tolven.provider.entity.MyProvider;
import org.tolven.provider.entity.PatientLink;
import org.tolven.provider.entity.Provider;
import org.tolven.provider.entity.ProviderLocation;
import org.tolven.provider.entity.ProviderSpecialty;
import org.tolven.provider.entity.Specialty;


@Stateless()
@Local(ProviderLocal.class)        
public class ProviderBean implements ProviderLocal {

	@PersistenceContext
	private EntityManager em;

    /**
	 * Get a provider by its internal ID
	 * @param providerId the id of the provider (this is the internal id)
	 * @return the provider object
	 */
	public Provider findProvider( long providerId ) {
    	Provider provider = em.find( Provider.class, providerId );
        return provider;
    }

    /**
	 * Get a provider by its internal ID and ensure that any lazy attributes are also
	 * fetched at this time. This method is necessary for remote interfaces which don't have
	 * the luxury of working with the object during a single transaction.
	 * This method could be slower than findProvider. 
	 * @param providerId the id of the provider (this is the internal id)
	 * @return the provider object
	 */
	public Provider findFullProvider( long providerId ) {
    	Provider provider = em.find( Provider.class, providerId );
    	int locationCount = 0;
    	for ( ProviderLocation providerLocation : provider.getLocations() ) {
    		locationCount++;
    	}
    	
    	int specialtyCount = 0;
    	for ( ProviderSpecialty providerSpecialty : provider.getSpecialties() ) {
    		Specialty specialty = providerSpecialty.getSpecialty();
    		specialtyCount++;
    	}
    	
        return provider;
    }

	/**
	 * Get a Specialty by its internal ID
	 * @param specialtyId the id of the Specialty (this is the internal id)
	 * @return the Specialty object
	 */
	public Specialty findSpecialty( long specialtyId ) {
    	Specialty specialty = em.find( Specialty.class, specialtyId );
        return specialty;
    }

	/**
	 * Return a sorted list of all specialties
	 */
	public List<Specialty> findAllSpecialties() {
		Query query = em.createQuery("SELECT s FROM Specialty s ORDER BY s.name");
		return query.getResultList();
	}
	
	/**
	 * Add and remove provider specialties from a provider as appropriate to yield the list of specialties supplied.
	 */
	public void updateProviderSpecialties(Provider provider, Set<Long> selectedSpecialties) {
		// Create a set of existing specialties for this provider
		Set<Long> existingSpecialties = new HashSet<Long>(provider.getSpecialties().size());
		for (ProviderSpecialty providerSpecialty: provider.getSpecialties()) {
			existingSpecialties.add(providerSpecialty.getSpecialty().getId());
		}
		// Any specialties missing from new set get removed from existing list
		Set<ProviderSpecialty> removeProviderSpecialties = new HashSet<ProviderSpecialty>(provider.getSpecialties().size());
		for (ProviderSpecialty providerSpecialty : provider.getSpecialties() ) {
			if (!selectedSpecialties.contains(providerSpecialty.getSpecialty().getId())) {
				removeProviderSpecialties.add(providerSpecialty);
			}
		}
		// Selected specialties not in existing set get added
		for (Long selectedId : selectedSpecialties) {
			if (!existingSpecialties.contains(selectedId)) {
				Specialty specialty = findSpecialty(selectedId);
				ProviderSpecialty newPS = new ProviderSpecialty();
				newPS.setProvider(provider);
				newPS.setSpecialty(specialty);
				provider.addSpecialty(newPS);
			}
		}
		// Remove the ones we don't want
		provider.getSpecialties().removeAll(removeProviderSpecialties);
		for (ProviderSpecialty providerSpecialty : removeProviderSpecialties) {
			em.remove(providerSpecialty);
		}
	}

	/**
	 * Find all providers for an account.
	 * @param accountId
	 * @return The list of provider entries owned by the requested account
	 */
	public List<Provider> findAccountProviders( long accountId, boolean includeInactive ) {
		Query query;
		if (includeInactive) {
			query = em.createQuery("SELECT p FROM Provider p WHERE p.ownerAccount.id = :id ORDER BY p.name");
		} else {
			query = em.createQuery("SELECT p FROM Provider p WHERE p.ownerAccount.id = :id AND p.status = 'active' ORDER BY p.name");
		}
		query.setParameter("id", accountId);
		return query.getResultList();
	}
	
	/**
	 * Create a new patientLink that relates a patient to a provider. This is only one half of a completed
	 * patient link as the provider must complete the link by providing the provider own patient record (providerPatient).
	 * @param provider
	 * @param menuData of the patient placeholder (typically ephr)
	 * @return
	 */
	public PatientLink createPatientLink( Provider provider, MenuData myPatient, String request) {
		PatientLink patientLink = new PatientLink();
		patientLink.setMyPatient(myPatient);
		patientLink.setProvider(provider);
		patientLink.setRequest(request);
		patientLink.setStatus("active");
		em.persist(patientLink);
		return patientLink;
	}

	/**
	 * Add (or change) a provider in a patient link
	 */
	public void addProviderToPatientLink(long patientLinkId, long providerId) {
		PatientLink patientLink = em.find(PatientLink.class, patientLinkId);
		Provider provider = em.find( Provider.class, providerId);
		patientLink.setProvider(provider);
		em.merge(patientLink);
	}

	/**
	 * Create a new patientLink that relates a patient to a provider. This is only one half of a completed
	 * patient link as the provider must complete the link by providing the provider own patient record (providerPatient).
	 * @param provider
	 * @param menuData of the patient placeholder (typically ephr)
	 * @return
	 */
	public PatientLink createPatientLink( long providerId, MenuData myPatient, String request) {
		Provider provider = em.find(Provider.class, providerId);
		return createPatientLink(  provider, myPatient, request);
	}
	
	/**
	 * Find a patientLink by its internal ID
	 * @param specialtyId the id of the PatientLink (this is the internal id)
	 * @return the PatientLink object
	 */
	public PatientLink findPatientLink( long patientLinkId ) {
		PatientLink patientLink = em.find( PatientLink.class, patientLinkId );
        return patientLink;
    }

	/**
	 * The providerAcceptPatientLink method is a provider-side method that indicates that the provider
	 * has accepted the patient link. Note: This method does not update status. For example, if the patient has
	 * subsequently declined the link, then having the provider accept the link is OK but it won't 
	 * re-activate the link.
	 * @param patientLink
	 * @param menuData of the patient placeholder (typically in echr) to add to the patient link
	 */
	public void providerAcceptPatientLink( PatientLink patientLink, MenuData providerPatient) {
		patientLink.setProviderPatient(providerPatient);
		em.merge(patientLink);
	}
	
	/**
	 * The revokePatientLink method is initiated by either side of the link to indicate that the
	 * link should no longer be active.
	 * @param patientLink
	 */
	public void revokePatientLink( PatientLink patientLink, MenuData providerPatient) {
		patientLink.setStatus("completed");
		em.merge(patientLink);
	}
	
	/**
	 * Return a list of PatientLinks where the supplied patient is MyPatient (and the link is active).
	 * This essentially returns a list of active providers for this patient.
	 * Note: Some patientLinks may not actually be usable yes: If the provider has not accepted the patient
	 * yet, then providerPatient will be null. If status is not = active, it means the provider has declined the
	 * patient-provider connection.
	 * @param menuData
	 * @return
	 */
	public List<PatientLink> findOutboundPatientLinks(MenuData menuData ) {
		Query query;
		query = em.createQuery("SELECT pat FROM PatientLink pat " +
				" WHERE pat.myPatient = :md" +
				" AND pat.status = 'active'");
		query.setParameter("md", menuData);
		return query.getResultList();
	}

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
	public List<PatientLink> findInboundPatientLinks(MenuData menuData ) {
		Query query;
		query = em.createQuery("SELECT pat FROM PatientLink pat " +
		" WHERE pat.providerPatient = :md" +
		" AND pat.status = 'active'");
		query.setParameter("md", menuData);
		return query.getResultList();
	}

	/**
	 * Return a list of the providers selected by a personal account
	 * @param account
	 * @return List of MyProvider objects, which will point to providers.
	 */
	public List<MyProvider> findMyProviders( long personalAccountId ) {
		Query query;
		query = em.createQuery("SELECT mp FROM MyProvider mp WHERE mp.account.id = :account");
		query.setParameter("account", personalAccountId);
		return query.getResultList();
	}

	/**
	 * Return a list of the active providers selected by a personal account
	 * @param account
	 * @return List of MyProvider objects, which will point to providers.
	 */
	public List<MyProvider> findMyActiveProviders( long personalAccountId ) {
		Query query;
		query = em.createQuery("SELECT mp FROM MyProvider mp WHERE mp.account.id = :account AND mp.status = 'active'");
		query.setParameter("account", personalAccountId);
		return query.getResultList();
	}

	public List<MyProvider> findMyActiveProviders( Account account, Provider provider ) {
		Query query;
		query = em.createQuery("SELECT mp FROM MyProvider mp " +
				"WHERE mp.account = :account " +
				"AND mp.provider = :provider " +
				"AND mp.status = 'active'");
		query.setParameter("account", account);
		query.setParameter("provider", provider);
		return query.getResultList();
	}
	
	/**
	 * Return a list of providers endorsed by this provider. 
	 * @param providerId with endorsements to it
	 * @return The list of providers with endorsements
	 */
	public List<Provider> findEndorsedProviders( long providerId ) {
		List<Provider> providers;
		if (providerId==0) {
			providers = findRootProviders();
		} else {
			Provider provider = em.getReference(Provider.class, providerId);
			providers = findEndorsedProviders(provider);
		}
		return providers; 
	}
	
	/**
	 * Return a list of providers endorsed by this provider. 
	 * @param provider with endorsements to it
	 * @return 
	 */
	public List<Provider> findEndorsedProviders( Provider provider ) {
		// Get the owner account for this provider
		Account account = provider.getOwnerAccount();
		// Now find which, if any, providers this account endorses
		Query query;
		query = em.createQuery("SELECT pe.provider " +
				"FROM ProviderEndorsement pe " +
				"WHERE pe.endorsingAccount = :account");
		query.setParameter("account", account);
		return query.getResultList();
	}
	
	/**
	 * Returns if a provider is endorsed 
	 * @param providerId for whom the status needs to be checked
	 * @return true if endorsed
	 */
	public boolean isProviderEndorsed( long providerId ) {
		Query query = em.createQuery("SELECT pe.provider FROM ProviderEndorsement pe WHERE pe.provider.id = :id and pe.endorsingAccount is not null");
		query.setParameter("id", providerId);
		if (query.getResultList().size() > 0)
		   return true;
		return false;
	}
	
	
	/**
	 * Return a list of "root" providers, that is, providers endorsed by a null account. 
	 * @return 
	 */
	public List<Provider> findRootProviders( ) {
		Query query;
		query = em.createQuery("SELECT pe.provider " +
				"FROM ProviderEndorsement pe " +
				"WHERE pe.endorsingAccount is null");
		return query.getResultList();
	}
	
	/**
	 * Add a provider to the account's list of providers to allow replies from.
	 * This provides a return path.
	 * @param account This account
	 * @param providerId The provider id that this account will now allow to send responses back.
	 */
	public void addMyProvider(Account account, long providerId ) {
		Provider provider = em.find(Provider.class, providerId);
		List<MyProvider> myProviders = findMyActiveProviders(account, provider );
		if (myProviders.size() == 0) {
			MyProvider myProvider = new MyProvider();
			myProvider.setAccount(account);
			myProvider.setProvider(provider);
			myProvider.setStatus("active");
			em.persist(myProvider);
			TolvenLogger.info( "Added myProvider " + myProvider.getProvider().getName() + " to account " + myProvider.getAccount().getId() + " as id" + myProvider.getId(), ProviderBean.class);
		}
	}

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
	public List<Provider> findProviders( List<Specialty> specialties, String zip, int limit) {
		Query query;
		if (specialties!=null && specialties.size() > 0) {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT p FROM Provider p LEFT OUTER JOIN p.specialties ps WHERE ps.specialty.id IN (");
			boolean firstTime = true;
			for (Specialty specialty : specialties) {
				if (firstTime) {
					firstTime = false;
				} else {
					queryString.append(",");
				}
				queryString.append(specialty.getId());
			}
			queryString.append( ") ORDER BY p.name");
			query = em.createQuery(queryString.toString());
		} else {
			query = em.createQuery("SELECT p FROM Provider p ORDER BY p.name");
		}
		query.setMaxResults(limit);
		return query.getResultList();
	}
	
	/**
	 * Persist a new or modified provider. In general, we expect that the specialty will already exist
	 * when this method is called since the user will have chosen it from a list rather than
	 * make one up on the fly.
	 * @param provider
	 */
	public void persistProvider( Provider provider ) {
		em.persist(provider);
	}

	/**
	 * Persist a new or updated specialty
	 * @param specialty
	 */
	public void persistSpecialty( Specialty specialty ) {
		em.merge(specialty);
	}

	/**
	 * Update an existing provider
	 * @param provider
	 */
	public void updateProvider( Provider provider ) {
		em.merge( provider );
	}

	/**
	 * Persist a new provider
	 * @param provider
	 */
	public void createProvider( Provider provider ) {
		em.persist( provider );
	}
	
}
