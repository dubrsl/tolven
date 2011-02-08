package org.tolven.trim.ex;

import org.apache.commons.lang.StringUtils;
import org.tolven.trim.Party;

public class PartyEx extends Party {
	 @Override
	    public String toString(){
	    	return getAccountPath()+"|"+getAccountId()+"|"+getProviderId();
	    }
	    @Override
	    public boolean equals(Object obj) {
	    	if(obj instanceof Party) {
	    		Party p = (Party) obj;
				return (p.getAccountPath()+p.getAccountId()+p.getProviderId()).equals(
						this.getAccountPath()+this.getAccountId()+this.getProviderId());
			}
	    	return false;
	    }
	    
	    /** Constructor with parameters
	     * @param args
	     */
	    public PartyEx(String args[]){
	    	if(args.length >= 3){
		    	this.setAccountPath(args[0]);
				this.setAccountId(args[1]);
				if(!StringUtils.isBlank(args[2]) && !"null".equals(args[2]))
					this.setProviderId(Long.parseLong(args[2]));
	    	}
	    }
		public PartyEx(String accountPath, String accountId, String providerId) {
			this.setAccountPath(accountPath);
			this.setAccountId(accountId);
			if(!StringUtils.isBlank(providerId) && !"null".equals(providerId))
				this.setProviderId(Long.parseLong(providerId));
    	
		}

}
