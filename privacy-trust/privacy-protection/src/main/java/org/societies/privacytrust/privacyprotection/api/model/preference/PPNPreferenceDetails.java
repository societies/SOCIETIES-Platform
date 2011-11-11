package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.io.Serializable;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

public class PPNPreferenceDetails implements Serializable{

	private String contextType;
	private ICtxAttributeIdentifier affectedCtxID;
	private EntityIdentifier requestorDPI;
	private ServiceResourceIdentifier serviceID;
	public PPNPreferenceDetails(String contextType){
		this.setContextType(contextType);
	}

	public void setAffectedCtxID(ICtxAttributeIdentifier affectedCtxID) {
		this.affectedCtxID = affectedCtxID;
	}

	public ICtxAttributeIdentifier getAffectedCtxID() {
		return affectedCtxID;
	}

	public void setRequestorDPI(EntityIdentifier requestorDPI) {
		this.requestorDPI = requestorDPI;
	}

	public EntityIdentifier getRequestorDPI() {
		return requestorDPI;
	}

	public void setContextType(String contextType) {
		this.contextType = contextType;
	}

	public String getContextType() {
		return contextType;
	}
	
	private boolean compareRequestorDPIs(EntityIdentifier dpi){
		if (dpi==null){
			if (this.requestorDPI==null){
				return true;
			}else{
				return false;
			}
		}else{
			if (this.requestorDPI==null){
				return false;
			}else{
				if (dpi.toUriString().equals(requestorDPI.toUriString())){
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
	private boolean compareCtxIDs(ICtxAttributeIdentifier ctxID){
		if (ctxID==null){
			if (this.affectedCtxID==null){
				return true;
			}else{
				return false;
			}
		}else{
			if (this.affectedCtxID==null){
				return false;
			}else{
				if (ctxID.toUriString().equals(this.affectedCtxID.toUriString())){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	@Override
	public boolean equals(Object obj){
		if (obj instanceof PPNPreferenceDetails){
			PPNPreferenceDetails det = (PPNPreferenceDetails) obj;
			if (det.getContextType().equalsIgnoreCase(contextType)){
				if (compareCtxIDs(det.getAffectedCtxID())){
					if (compareRequestorDPIs(det.getRequestorDPI())){
						return this.compareServiceID(det.getServiceID());
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
		return false;
	}
	
	@Override
	public String toString(){
		String str = "\n";
		str = str.concat("Context Type: "+this.contextType);
		if (this.affectedCtxID!=null){
			str = str.concat("\nAffected CtxID: "+this.affectedCtxID.toUriString());
		}
		
		if (this.requestorDPI!=null){
			str = str.concat("\nRequestor DPI: "+this.requestorDPI.toUriString());
		}
		str = str.concat("\n");
		return str;
	}

	public void setServiceID(ServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}

	public ServiceResourceIdentifier getServiceID() {
		return serviceID;
	}
	
}
