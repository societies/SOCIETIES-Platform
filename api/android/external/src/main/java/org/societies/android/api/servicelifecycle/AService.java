/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.api.servicelifecycle;

import org.json.JSONException;
import org.json.JSONObject;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class AService extends Service implements Parcelable {

	private static final long serialVersionUID = 7945457983909141117L;
	
	public AServiceInstance getServiceInstance() {
		return (AServiceInstance)super.getServiceInstance();
	}
	public void setServiceInstance(AServiceInstance aserviceInstance) {
		super.setServiceInstance(aserviceInstance);
	}
	
	public AServiceResourceIdentifier getServiceIdentifier() {
		return (AServiceResourceIdentifier)super.getServiceIdentifier();
	}

	public void setServiceIdentifier(AServiceResourceIdentifier aServiceResourceId) {
		super.setServiceIdentifier(aServiceResourceId);
	}
	
	public AService() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()*/
	public int describeContents() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)*/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getAuthorSignature());
	    dest.writeString(this.getPrivacyPolicy());
	    dest.writeString(this.getServiceDescription());
	    dest.writeString(this.getServiceEndpoint());
	    dest.writeParcelable(this.getServiceIdentifier(), flags);
	    dest.writeParcelable(this.getServiceInstance(), flags);
	    dest.writeString(this.getServiceLocation());
	    dest.writeString(this.getServiceName());
		dest.writeString(this.getServiceCategory());
		dest.writeString(this.getServiceStatus().toString());
		dest.writeString(this.getServiceType().toString());
	}

	private AService(Parcel in) {
		super();
		this.setAuthorSignature(in.readString());
	    this.setPrivacyPolicy(in.readString());
	    this.setServiceDescription(in.readString());
	    this.setServiceEndpoint(in.readString());
	    this.setServiceIdentifier((AServiceResourceIdentifier) in.readParcelable(this.getClass().getClassLoader()) );
	    this.setServiceInstance((AServiceInstance) in.readParcelable(this.getClass().getClassLoader()) );
	    this.setServiceLocation(in.readString());
	    this.setServiceName(in.readString());
		this.setServiceCategory(in.readString());
		this.setServiceStatus(ServiceStatus.fromValue(in.readString()));
		this.setServiceType(ServiceType.fromValue(in.readString()));
	}
	
	public static final Parcelable.Creator<AService> CREATOR = new Parcelable.Creator<AService>() {
		public AService createFromParcel(Parcel in) {
			return new AService(in);
		}

		public AService[] newArray(int size) {
			return new AService[size];
		}
	};
	
	public static AService convertService(Service service) {
		AService aservice = new AService();
		aservice.setAuthorSignature(service.getAuthorSignature());
		aservice.setContextSource(service.getContextSource());
		aservice.setPrivacyPolicy(service.getPrivacyPolicy());
		aservice.setSecurityPolicy(service.getSecurityPolicy());
		aservice.setServiceCategory(service.getServiceCategory());
		aservice.setServiceDescription(service.getServiceDescription());
		aservice.setServiceEndpoint(service.getServiceEndpoint());
		aservice.setServiceIdentifier(AServiceResourceIdentifier.convertServiceResourceIdentifier(service.getServiceIdentifier()));
		aservice.setServiceInstance(AServiceInstance.convertServiceInstance(service.getServiceInstance()));
		aservice.setServiceLocation(service.getServiceLocation());
		aservice.setServiceName(service.getServiceName());
		aservice.setServiceStatus(service.getServiceStatus());
		aservice.setServiceType(service.getServiceType());
		
		return aservice;
	}
	
	public static Service convertAService(AService aservice) {
		Service service = new Service();
		
		service.setAuthorSignature(aservice.getAuthorSignature());
		service.setContextSource(aservice.getContextSource());
		service.setPrivacyPolicy(aservice.getPrivacyPolicy());
		service.setSecurityPolicy(aservice.getSecurityPolicy());
		service.setServiceCategory(aservice.getServiceCategory());
		service.setServiceDescription(aservice.getServiceDescription());
		service.setServiceEndpoint(aservice.getServiceEndpoint());
		service.setServiceIdentifier(AServiceResourceIdentifier.convertAServiceResourceIdentifier(aservice.getServiceIdentifier()));
		service.setServiceInstance(AServiceInstance.convertAServiceInstance(aservice.getServiceInstance()));
		service.setServiceLocation(aservice.getServiceLocation());
		service.setServiceName(aservice.getServiceName());
		service.setServiceStatus(aservice.getServiceStatus());
		service.setServiceType(aservice.getServiceType());
		
		return service;
	}
	
	/**
	 * Creates a AService from a JSON Object
	 * @param jObj
	 * @return
	 * @throws JSONException
	 */
	public static AService createFromJSON(JSONObject jObj) throws JSONException {
		AService aservice = new AService();
		aservice.setAuthorSignature(jObj.getString("authorSignature"));
		aservice.setContextSource(jObj.getString("contextSource"));
		aservice.setPrivacyPolicy(jObj.getString("privacyPolicy"));
		aservice.setSecurityPolicy(jObj.getString("securityPolicy"));
		aservice.setServiceCategory(jObj.getString("serviceCategory"));
		aservice.setServiceDescription(jObj.getString("serviceDescription"));
		aservice.setServiceEndpoint(jObj.getString("serviceEndpoint"));
		aservice.setServiceIdentifier(AServiceResourceIdentifier.createFromJSON(jObj.getJSONObject("serviceIdentifier")));
		aservice.setServiceInstance(AServiceInstance.createFromJSON(jObj.getJSONObject("serviceInstance")));
		aservice.setServiceLocation(jObj.getString("serviceLocation"));
		aservice.setServiceName(jObj.getString("serviceName"));
		aservice.setServiceStatus(ServiceStatus.fromValue(jObj.getString("serviceStatus")));
		aservice.setServiceType(ServiceType.fromValue(jObj.getString("serviceType")));
		
		return aservice;
	}
}
