package org.societies.platform.servicelifecycle.serviceRegistry.model;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.societies.api.internal.servicelifecycle.model.Service;
import org.societies.api.internal.servicelifecycle.model.ServiceLocation;
import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.servicelifecycle.model.ServiceType;

/**
 * This is the Class accepted by the ServiceRegistry when a service wants to
 * register. This Object contains attributes used to retrieve services shared
 * from/to a CSS/CIS and also information to retrieve organization that has
 * developed the service.
 * 
 * @author apanazzolo
 * @version 1.0
 * @created 06-dic-2011 12.12.57
 */

@Entity
@Table(name = "RegistryEntry")
public class RegistryEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9064750069927104572L;

	// private String serviceIdentifier;
	// private ServiceResourceIdentifier serviceIdentifier;

	private ServiceResourceIdentiferDAO serviceIdentifier;

	/**
	 * CSS where the service is installed.
	 */
	private String CSSIDInstalled;

	/**
	 * The version of the service, it must be updated by developer
	 */
	private String version;

	/**
	 * An alias name for the service
	 */
	private String serviceName;
	/**
	 * A "long" description of the service
	 */
	private String serviceDescription;
	/**
	 * The signature of the author
	 */
	private String authorSignature;

	private String serviceType;
	
	private String serviceLocation;
	
	public RegistryEntry() {

	}

	/**
	 * @param serviceEndpointURI
	 * @param cSSIDInstalled
	 * @param version
	 * @param hash
	 * @param lifetime
	 * @param serviceName
	 * @param serviceDescription
	 * @param authorSignature
	 */
	public RegistryEntry(ServiceResourceIdentifier serviceIdentifier,
			String cSSIDInstalled, String version, String serviceName,
			String serviceDescription, String authorSignature, ServiceType type, ServiceLocation location) {

		super();

		this.serviceIdentifier = new ServiceResourceIdentiferDAO(
				serviceIdentifier.getIdentifier().toString());

		this.version = version;

		this.serviceName = serviceName;
		this.serviceDescription = serviceDescription;
		this.authorSignature = authorSignature;
		this.CSSIDInstalled = cSSIDInstalled;
		
		this.serviceType = type.toString();
		this.serviceLocation = location.toString();
	}

	@Column(name = "ServiceName")
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Column(name = "Version")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name = "ServiceDescription")
	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	@Column(name = "AuthorSignature")
	public String getAuthorSignature() {
		return authorSignature;
	}

	public void setAuthorSignature(String authorSignature) {
		this.authorSignature = authorSignature;
	}

	@Column(name = "CSSIDInstalled")
	public String getCSSIDInstalled() {
		return CSSIDInstalled;
	}

	public void setCSSIDInstalled(String cSSIDInstalled) {
		CSSIDInstalled = cSSIDInstalled;
	}

	@EmbeddedId
	// @Column(name = "ServiceIdentifier")
	public ServiceResourceIdentiferDAO getServiceIdentifier() {
		return this.serviceIdentifier;
	}

	public void setServiceIdentifier(
			ServiceResourceIdentiferDAO serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}

	public Service createServiceFromRegistryEntry() {
		Service returnedService = null;
		try {
			ServiceType tmpServiceType = null;
			ServiceLocation tmpServiceLocation = null;
			
			
			/* Retrieve the service type from the service and
			 * create the appropriate enumeration type
			 */
			if (serviceType == "ThirdPartyService") {
				tmpServiceType = ServiceType.ThirdPartyService;
			} else {
				if (serviceType == "CoreService") {
					tmpServiceType = ServiceType.CoreService;
				}
			}
			
			/* Same as before but for service location */
			if (serviceLocation == "Local") {
				tmpServiceLocation = ServiceLocation.Local;
			} else {
				if (serviceLocation == "Remote" ) {
					tmpServiceLocation = ServiceLocation.Remote;
				}
			}

			returnedService = new Service(new ServiceResourceIdentifier(
					new URI(this.serviceIdentifier.getIdentifier())),
					this.CSSIDInstalled, this.version, this.serviceName,
					this.serviceDescription, this.authorSignature,
					tmpServiceType, tmpServiceLocation);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return returnedService;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceLocation() {
		return serviceLocation;
	}

	public void setServiceLocation(String serviceLocation) {
		this.serviceLocation = serviceLocation;
	}

}