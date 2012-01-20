package org.societies.platform.servicelifecycle.serviceRegistry.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;

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
	
	private long id;

	// private String serviceIdentifier; 
	// private ServiceResourceIdentifier serviceIdentifier;
	
	private ServiceResourceIdentiferDAO serviceIdentifier;
	
	/**
	 * Unique identifier for a single instance of a service.
	 */
	private String serviceEndpointURI;

	/**
	 * The version of the service, it must be updated by developer
	 */
	private String version;
	/**
	 * <No description from WP3>
	 */
	private String hash;

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
			String serviceDescription, String authorSignature) {
		super();
		// this.setServiceIdentifier(serviceIdentifier);
		this.serviceIdentifier = new ServiceResourceIdentiferDAO(serviceIdentifier.getIdentifier().toString());

		this.version = version;

		this.serviceName = serviceName;
		this.serviceDescription = serviceDescription;
		this.authorSignature = authorSignature;
	}

	// @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RegistryEntryId")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	@Column(name = "ServiceEndPoint")
	public String getServiceEndpointURI() {
		return serviceEndpointURI;
	}

	public void setServiceEndpointURI(String serviceEndpointURI) {
		this.serviceEndpointURI = serviceEndpointURI;
	}

	@Column(name = "Hash")
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@EmbeddedId
	// @Column(name = "ServiceIdentifier")
	public ServiceResourceIdentiferDAO getServiceIdentifier() {
		return this.serviceIdentifier;
	}

	public void setServiceIdentifier(ServiceResourceIdentiferDAO serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}

}