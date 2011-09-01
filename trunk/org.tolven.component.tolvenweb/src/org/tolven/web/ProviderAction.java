package org.tolven.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIInput;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.tolven.provider.entity.MyProvider;
import org.tolven.provider.entity.Provider;
import org.tolven.provider.entity.ProviderSpecialty;
import org.tolven.provider.entity.Specialty;

public class ProviderAction extends TolvenAction {

	private DataModel providers;
	private DataModel myProviders;
	private DataModel matchingProviders;
	private Provider provider;
	private MyProvider myProvider;
	private long providerId;
	private UIInput providerIdCtrl;
	private String zipCode;
	private List<Specialty> specialties;
	private List<SelectItem> allProviders;
	private boolean showResults = false;
	private Map<Long, Provider> providerMap = null;
	private List<Long> selectedSpecialties;
	private SelectMap selectMap;


	public ProviderAction() {
		super();
	}
	
	/**
	 * Action method that prepares to edit a single provider.
	 * @return
	 */
	public String editProvider() {
		setProvider( (Provider) providers.getRowData());
		setProviderId( getProvider().getId());
//		TolvenLogger.info( "Editing: " + provider.getId() + " " + provider.getName(), ProviderAction.class );
		return "edit";
	}

	/**
	 * Toggle showing or hiding inactive providers.
	 * @return "Success"
	 */
	public String toggleShowProvider() {
		getTop().setShowProviderInactive(!getTop().isShowProviderInactive());
		providers = null;
		provider = null;
		providerId = 0;
		return "success";
	}
	
	/**
	 * Toggle status of a provider entry (active-inactive). This currently shows in the ui 
	 * as Delete/UnDelete
	 * @return "success"
	 */
	public String toggleStatus() {
		setProvider( (Provider) providers.getRowData());
		setProviderId( getProvider().getId());
		if (provider.getStatus().equals(org.tolven.core.entity.Status.ACTIVE.value())) {
			provider.setStatus(org.tolven.core.entity.Status.INACTIVE.value());
		} else {
			provider.setStatus(org.tolven.core.entity.Status.ACTIVE.value());
		}
		getProviderBean().updateProvider(provider);
		return "success";
	}

	public String updateProvider() {
//		TolvenLogger.info( "updateProvider--id: " + providerId, ProviderAction.class  );
		if (provider.getId()==0) {
			getProviderBean().createProvider(provider);
		} else {
			getProviderBean().updateProvider(provider);
		}
//		TolvenLogger.info( "Provider: " + provider.getId() + " updated", ProviderAction.class );
		return "success";
	}

	/**
	 * Action method that gets a list of the patient's providers.
	 * @return
	 */
	public DataModel getMyProviders() {
		if (myProviders==null) {
			myProviders = new ListDataModel();
			myProviders.setWrappedData(getProviderBean().findMyActiveProviders( this.getSessionAccountId()));
//			TolvenLogger.info( "Show Inactive=" + getTop().isShowProviderInactive(), ProviderAction.class);
		}
		return myProviders;
	}
	
	/**
	 * Action to create a list of potential providers for selection as MyProvider
	 * @return
	 */
	public String findAProvider( ) {
		showResults = true;
		return "success";
	}
	
	public DataModel getMatchingProviders() {
		if (matchingProviders==null) {
			matchingProviders = new ListDataModel();
			matchingProviders.setWrappedData(getProviderBean().findProviders( null, null, 500));
//			TolvenLogger.info( "FindaProvider count=" + matchingProviders.getRowCount(), ProviderAction.class);
		}
		return matchingProviders;
	}

	public List<SelectItem> getAllProviders() {
		if (allProviders==null) {
			List<Provider> providers = getProviderBean().findProviders( null, null, 500);
			allProviders = new ArrayList<SelectItem>(providers.size());
			allProviders.add(new SelectItem(0L, ""));
			for (Provider provider : providers) {
				SelectItem item = new SelectItem();
				item.setValue((Long)provider.getId());
				String providerName = provider.getName() +
					" (" +
					provider.getOwnerAccount().getId() +
					" " +
					provider.getOwnerAccount().getTitle() +
					")";
				item.setLabel(providerName);
				allProviders.add(item);
			}
		}
		return allProviders;
	}
	/**
	 * Return a list of "root" providers as specified by the endorsed list.
	 * If none are found, then the automatically returns all providers as a fallback. 
	 * @return
	 */
	public List<SelectItem> getRootProviders() {
		if (allProviders==null) {
			List<Provider> providers = getProviderBean().findRootProviders();
			if (providers.size()==0) {
				return getAllProviders();
			}
			allProviders = new ArrayList<SelectItem>(providers.size());
			for (Provider provider : providers) {
				SelectItem item = new SelectItem();
				item.setValue((Long)provider.getId());
				item.setLabel(provider.getName());
				allProviders.add(item);
			}
		}
		return allProviders;
	}


	/**
	 * Action method that prepares to view a single MyProvider.
	 * @return
	 */
	public String viewMyProvider() {
		setMyProvider( (MyProvider) myProviders.getRowData());
		setProviderId( getProvider().getId());
//		TolvenLogger.info( "Editing: " + provider.getId() + " " + provider.getName(), ProviderAction.class );
		return "edit";
	}

	public DataModel getProviders() {
		if (providers==null) {
			providers = new ListDataModel();
			providers.setWrappedData(getProviderBean().findAccountProviders( this.getSessionAccountId(), getTop().isShowProviderInactive()));
//			TolvenLogger.info( "Show Inactive=" + getTop().isShowProviderInactive(), ProviderAction.class);
		}
		return providers;
	}

	public void setProviders(DataModel providers) {
		this.providers = providers;
	}
	
	public void setMyProviders(DataModel myProviders) {
		this.myProviders = myProviders;
	}

	/**
	 * Either lookup an existing provider or create a new one. Later we'll persist or update it.
	 * @return Provider object
	 */
	public Provider getProvider() {
		if (providerIdCtrl!=null) {
			setProviderId((Long)providerIdCtrl.getValue());
		}
		if (provider==null) {
			if (providerId > 0) {
//				TolvenLogger.info( "Finding Provider: " + providerId, ProviderAction.class);
				provider = getProviderBean().findProvider(providerId);
			} else {
//				TolvenLogger.info( "New Provider", ProviderAction.class);
				provider = new Provider();
				provider.setOwnerAccount(getTop().getAccountUser().getAccount());
				provider.setShowFrom(getNow());
				provider.setShowTo(new Date(getNow().getTime()+ (100*365*24*60*1000)));
				provider.setStatus(org.tolven.core.entity.Status.ACTIVE.value());
			}
		}
		return provider;
	}
	/**
	 * Return a list of providers for the given list of specialties
	 * @author John Churin
	 *
	 */
	class SelectMap implements Map<List<Long>, List<SelectItem>> {

		@Override
		public List<SelectItem> get(Object key) {
			Object[] specialtyIds = (Object[]) key;
			List<Specialty> specialists = new ArrayList<Specialty>(specialtyIds.length);
			for (Object specialtyId : specialtyIds) {
				long id;
				if (specialtyId instanceof String) {
					id = Long.parseLong(specialtyId.toString());
				} else if (specialtyId instanceof Long) {
					id = (Long) specialtyId;
				} else {
					throw new RuntimeException( "Invalid datatype of Specialty id in Provider selection");
				}
				specialists.add(getProviderBean().findSpecialty(id));
			}
			List<Provider> providers = getProviderBean().findProviders( specialists, null, 500);
			List<SelectItem> items = new ArrayList<SelectItem>(providers.size());
			for (Provider provider : providers) {
				SelectItem item = new SelectItem();
				StringBuffer sb = new StringBuffer();
				sb.append(provider.getName());
				if (provider.getSpecialties().size() > 0) {
					sb.append(" - ");
					boolean firstTime = true;
					for (ProviderSpecialty ps : provider.getSpecialties()) {
						if (firstTime) {
							firstTime=false;
						} else {
							sb.append(", ");
						}
						sb.append(ps.getSpecialty().getName());
					}
				}
				item.setLabel(sb.toString());
				item.setValue(provider.getId());
				items.add(item);
			}
			return items;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<java.util.Map.Entry<List<Long>, List<SelectItem>>> entrySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<List<Long>> keySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<SelectItem> put(List<Long> key, List<SelectItem> value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void putAll(Map<? extends List<Long>, ? extends List<SelectItem>> m) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public List<SelectItem> remove(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Collection<List<SelectItem>> values() {
			// TODO Auto-generated method stub
			return null;
		}


	}
	class ProviderMap implements Map<Long, Provider> {

		@Override
		public Provider get(Object key) {
            Provider provider = getProviderBean().findProvider((Long) key);
            return provider;
        }

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean containsKey(Object key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<Entry<Long, Provider>> entrySet() {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<Long> keySet() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Provider put(Long key, Provider value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void putAll(Map<? extends Long, ? extends Provider> m) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Provider remove(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Collection<Provider> values() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	/**
	 * Return a map capable of looking up a provider by id
	 * @return
	 */
	public Map<Long, Provider> getProviderMap( ) {
		if (providerMap==null) {
			providerMap = new ProviderMap( );
		}
		return providerMap;
	}
	
	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public long getProviderId() {
		return providerId;
	}

	public void setProviderId(long providerId) {
//		TolvenLogger.info( "setProviderId: " + providerId, ProviderAction.class);
		this.providerId = providerId;
	}

	public UIInput getProviderIdCtrl() {
		return providerIdCtrl;
	}

	public void setProviderIdCtrl(UIInput providerIdCtrl) {
//		TolvenLogger.info( "setProviderIdCtrl: " + providerIdCtrl.getValue(), ProviderAction.class);
		this.providerIdCtrl = providerIdCtrl;
	}

	public MyProvider getMyProvider() {
		return myProvider;
	}

	public void setMyProvider(MyProvider myProvider) {
		this.myProvider = myProvider;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public List<Specialty> getSpecialties() {
		if (specialties==null) {
			specialties = getProviderBean().findAllSpecialties();
		}
		return specialties;
	}

	public SelectMap getSpecialtyProviderItems() {
		if (selectMap==null) {
			selectMap = new SelectMap();
		}
		return selectMap;
	}
	
	/**
	 * Return a list of all roles supported by this account
	 * @return List of accountRoles
	 */
	public List<SelectItem> getSpecialtyItems() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Specialty specialty : getSpecialties()) {
			SelectItem item = new SelectItem();
			item.setLabel(specialty.getName());
			item.setValue(Long.toString(specialty.getId()));
			items.add(item);
		}
		return items;
	}
	
	public List<Long> getSelectedSpecialtyItems() {
		if (selectedSpecialties==null) {
			selectedSpecialties = new ArrayList<Long>();
		}
		return selectedSpecialties;
	}
	
	public void setSelectedSpecialtyItems(List<Long> items) {
		// Create a set of selected specialties
		selectedSpecialties = items;
	}
	
	public List<String> getSelectedProviderSpecialtyItems() {
		List<String> items = new ArrayList<String>();
		if (getProvider()!=null) {
			for (ProviderSpecialty providerSpecialty : getProvider().getSpecialties()) {
				items.add(Long.toString(providerSpecialty.getSpecialty().getId()));
			}
	}
		return items;
	}
	
	public void setSelectedProviderSpecialtyItems(List<String> items) {
		// Create a set of selected specialties
		Set<Long> selectedSpecialties = new HashSet<Long>(items.size());
		for (String item : items) {
			selectedSpecialties.add(Long.parseLong(item));
		}
		getProviderBean().updateProviderSpecialties(getProvider(),selectedSpecialties);
	}

	public void setSpecialties(List<Specialty> specialties) {
		this.specialties = specialties;
	}

	public void setMatchingProviders(DataModel matchingProviders) {
		this.matchingProviders = matchingProviders;
	}

	public boolean isShowResults() {
		return showResults;
	}

	public void setShowResults(boolean showResults) {
		this.showResults = showResults;
	}

}
