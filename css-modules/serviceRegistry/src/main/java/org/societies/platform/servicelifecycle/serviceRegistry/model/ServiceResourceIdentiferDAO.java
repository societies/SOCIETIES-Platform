package org.societies.platform.servicelifecycle.serviceRegistry.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
@Embeddable
public class ServiceResourceIdentiferDAO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7982137477351378779L;
	@Basic
	private String identifier;
	@Basic
	private String instanceId;

	public ServiceResourceIdentiferDAO() {
		
	}

	public ServiceResourceIdentiferDAO(String identifier, String instanceIdentifier) {
		super();
		this.identifier = identifier;
		this.instanceId=instanceIdentifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	public boolean equals(Object object){
		if (object instanceof ServiceResourceIdentiferDAO) {
			ServiceResourceIdentiferDAO serviceIdentifier = (ServiceResourceIdentiferDAO)object;
            return this.identifier.equals(serviceIdentifier.getIdentifier()) && this.instanceId.equals(serviceIdentifier.getInstanceId()) ;
        } else {
            return false;
        }
	}
	
	public int hashCode() {
		StringBuilder builder = new StringBuilder();
	    builder.append(this.identifier);
	    builder.append(this.instanceId);
	    return builder.toString().hashCode();

    }
}
