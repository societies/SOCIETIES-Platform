package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.io.Serializable;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

public class IDSPreferenceDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EntityIdentifier affectedDPI;
	private EntityIdentifier providerDPI;
	private ServiceResourceIdentifier serviceID;
	
	public IDSPreferenceDetails(EntityIdentifier affectedDPI){
		this.affectedDPI = affectedDPI;
	}
	
	public void setAffectedDPI(EntityIdentifier affectedDPI) {
		this.affectedDPI = affectedDPI;
	}

	public EntityIdentifier getAffectedDPI() {
		return affectedDPI;
	}

	public void setServiceID(ServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}

	public ServiceResourceIdentifier getServiceID() {
		return serviceID;
	}

	public void setProviderDPI(EntityIdentifier providerDPI) {
		this.providerDPI = providerDPI;
	}

	public EntityIdentifier getProviderDPI() {
		return providerDPI;
	}
	
	private boolean compareProviderDPI(EntityIdentifier requestorDPI){
		if (requestorDPI==null){
			if (this.providerDPI==null){
				return true;
			}else{
				return false;
			}
		}else{
			if (requestorDPI==null){
				return false;
			}else{
				if (requestorDPI.toUriString().equalsIgnoreCase(this.providerDPI.toUriString())){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	
	private boolean compareServiceID(ServiceResourceIdentifier serviceID2){
		if (serviceID2==null){
			if (this.serviceID == null){
				return true;
			}else{
				return false;
			}
		}else{
			if (serviceID2==null){
				return false;
			}else{
				if (serviceID2.toUriString().equalsIgnoreCase(this.serviceID.toUriString())){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	@Override
	public boolean equals(Object obj){
		if (obj instanceof IDSPreferenceDetails){
			IDSPreferenceDetails details = (IDSPreferenceDetails) obj;
			if (getAffectedDPI().toUriString().equalsIgnoreCase(details.getAffectedDPI().toUriString())){
				if (this.compareProviderDPI(details.getProviderDPI())){
					return this.compareServiceID(details.getServiceID());
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	@Override
	public String toString(){
		String str = "\n";
		str = str.concat("AffectedDPI: "+this.getAffectedDPI().toUriString());
		
		if (this.providerDPI!=null){
			str = str.concat("\nProvider DPI: "+this.providerDPI.toUriString());
		}
		if (this.serviceID!=null){
			str = str.concat("\nServiceID :"+this.serviceID.toUriString());
		}
		str = str.concat("\n");
		return str;
	}


	
}
