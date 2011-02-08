package org.tolven.process;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.tolven.app.entity.MenuData;
import org.tolven.core.entity.Account;
import org.tolven.provider.ProviderLocal;
import org.tolven.provider.entity.Provider;
import org.tolven.trim.Party;

/**
 * Setup the receiver elements for this share.
 * .*:providers./ > echr:provider > provider > ownerAccount
 * e?hr:patient:share:accountShares > AccountId 
 * @author John Churin
 *
 */
public class ShareSetup extends ComputeBase {
	private ProviderLocal providerBean;
	private Logger logger = Logger.getLogger(this.getClass());

	public void compute( ) throws Exception {
		// The Account of this user is intrinsic (safer than getting it from Trim)
		Account account = getAccountUser().getAccount();
		// Get the selected provider, if any
		String path = getTrim().getMessage().getReceiver().getAccountPath();
		if (path==null || path.length()==0) {
			return;
 		}
		MenuData md = getMenuBean().findMenuDataItem(account.getId(), path);
		if (md==null) {
			throw new RuntimeException( "Receiver path [" + path + "] not found in ShareSetup for account " + getAccountUser().getAccount().getId() );
		}
		Account destinationAccount;
		Long providerId;
		Provider provider = null;
		if (md.getMenuStructure().getPath().endsWith("accountShares")) {
			Long accountId = (Long) md.getField("AccountId");
			if (accountId==null) {
				throw new RuntimeException( "Missing accountId in accountShares " + md.getPath());
			}
			destinationAccount = this.getTrimBean().findAccount(accountId);
			providerId = null;
		} else {
			providerId = (Long) md.getField("providerId");
			if (providerId==null) {
				throw new RuntimeException( "Missing providerId in provider placeholder " + md.getPath());
			}
			provider = getProviderBean().findProvider(providerId);
			destinationAccount = provider.getOwnerAccount();
		}
		// Set destination accountId in message block
		Party party = getTrim().getMessage().getReceiver();
		party.setAccountId(Long.toString(destinationAccount.getId()));
		party.setProviderId(providerId);
		if (provider != null)
			party.setProviderName(provider.getName());
		party.setAccountName(destinationAccount.getTitle());
		logger.info( "ShareSetup for account " + account.getId() + " Path " + path + " to account " + destinationAccount.getId() );
	}

	public ShareSetup() {
		super();
	}
	
	public ProviderLocal getProviderBean() throws NamingException {
		if (providerBean==null) {
		    providerBean = (ProviderLocal) getCtx().lookup("java:global/tolven/tolvenEJB/ProviderBean!org.tolven.provider.ProviderLocal");
		}
		return providerBean;
	}

}
