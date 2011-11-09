package org.societies.css_serregistry.db.model;

public class Service {
	
	public long getServiceID() {
		return serviceID;
	}
	public void setServiceID(long serviceID) {
		this.serviceID = serviceID;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getCSS_ID() {
		return CSS_ID;
	}
	public void setCSS_ID(String cSS_ID) {
		CSS_ID = cSS_ID;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	private long serviceID;
	private String serviceName;
	private String CSS_ID;
	private String serviceType;
	
	@Override
	public String toString() {
	return new StringBuffer().append("id=" + serviceID).append(",cssid=" + CSS_ID)
	.append(",serviceName=" + serviceName).append(",serviceType" + serviceType).toString();
	}
}
