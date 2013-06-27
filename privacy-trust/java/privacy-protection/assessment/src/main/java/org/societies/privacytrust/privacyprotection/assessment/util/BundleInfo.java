package org.societies.privacytrust.privacyprotection.assessment.util;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class BundleInfo {

	private String className;
	private int bundleId;
	private String bundleSymbolicName;
	private ServiceResourceIdentifier serviceId;
	
	public BundleInfo() {
		this.bundleId = 0;
		this.bundleSymbolicName = null;
		this.serviceId = null;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the bundleId
	 */
	public int getBundleId() {
		return bundleId;
	}

	/**
	 * @param bundleId the bundleId to set
	 */
	public void setBundleId(int bundleId) {
		this.bundleId = bundleId;
	}

	/**
	 * @return the bundleSymbolicName
	 */
	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}

	/**
	 * @param bundleSymbolicName the bundleSymbolicName to set
	 */
	public void setBundleSymbolicName(String bundleSymbolicName) {
		this.bundleSymbolicName = bundleSymbolicName;
	}

	/**
	 * @return the serviceId
	 */
	public ServiceResourceIdentifier getServiceId() {
		return serviceId;
	}

	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(ServiceResourceIdentifier serviceId) {
		this.serviceId = serviceId;
	}
	
	
}
