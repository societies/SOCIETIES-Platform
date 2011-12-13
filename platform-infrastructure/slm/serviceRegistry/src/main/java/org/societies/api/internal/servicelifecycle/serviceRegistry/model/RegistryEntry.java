package org.societies.api.internal.servicelifecycle.serviceRegistry.model;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.societies.comm.xmpp.Endpoint;

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

	private long id;

	private String serviceName;
	private String version;
	private String serviceDescription;
	private List<Endpoint> sharedWithCSS;
	private List<Endpoint> sharedWithCIS;
	private Endpoint CSSInstalled;
	private Endpoint CSSNodeInstalled;
	private URI serviceURI;
	private String organizationId;
	private ServiceResourceIdentifier serviceIdentifier;
	private String authorSignature;

	public RegistryEntry() {

	}

	public void finalize() throws Throwable {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RegistryEntryId")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Column (name="Version")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "SharedWithCSSJoin", joinColumns = { @JoinColumn(name = "RegistryEntryId") }, inverseJoinColumns = { @JoinColumn(name = "EndpointId") })
	public List<Endpoint> getSharedWithCSS() {
		return sharedWithCSS;
	}

	public void setSharedWithCSS(
			List<Endpoint> sharedWithCSS) {
		this.sharedWithCSS = sharedWithCSS;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "SharedWithCISJoin", joinColumns = { @JoinColumn(name = "RegistryEntryId") }, inverseJoinColumns = { @JoinColumn(name = "EndpointId") })
	public List<Endpoint> getSharedWithCIS() {
		return sharedWithCIS;
	}

	public void setSharedWithCIS(
			List<Endpoint> sharedWithCIS) {
		this.sharedWithCIS = sharedWithCIS;
	}

	public Endpoint getCSSInstalled() {
		return CSSInstalled;
	}

	public void setCSSInstalled(Endpoint cSSInstalled) {
		CSSInstalled = cSSInstalled;
	}

	public Endpoint getCSSNodeInstalled() {
		return CSSNodeInstalled;
	}

	public void setCSSNodeInstalled(
			Endpoint cSSNodeInstalled) {
		CSSNodeInstalled = cSSNodeInstalled;
	}

	public URI getServiceURI() {
		return serviceURI;
	}

	public void setServiceURI(URI serviceURI) {
		this.serviceURI = serviceURI;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public ServiceResourceIdentifier getServiceIdentifier() {
		return serviceIdentifier;
	}

	public void setServiceIdentifier(ServiceResourceIdentifier serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}

	public String getAuthorSignature() {
		return authorSignature;
	}

	public void setAuthorSignature(String authorSignature) {
		this.authorSignature = authorSignature;
	}

}