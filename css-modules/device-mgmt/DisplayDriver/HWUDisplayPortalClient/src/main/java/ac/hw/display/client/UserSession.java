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
package ac.hw.display.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class UserSession {

	private String userIdentity;
	private List<ServiceInfo> services;
	private final int serviceRuntimeSocketPort;
	
	public UserSession(String userIdentity, int port){
		this.setUserIdentity(userIdentity);
		services = new ArrayList<ServiceInfo>();
		this.serviceRuntimeSocketPort = port;
	}
	
	public void addService(ServiceInfo sInfo){
		this.services.add(sInfo);
	}

	/**
	 * @return the userIdentity
	 */
	public String getUserIdentity() {
		return userIdentity;
	}

	/**
	 * @param userIdentity the userIdentity to set
	 */
	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public List<ServiceInfo> getServices() {
		
		return this.services;
	}

	public boolean containsService(String serviceName) {
		for (ServiceInfo sInfo : services){
			if (sInfo.getServiceName().equalsIgnoreCase(serviceName)){
				return true;
			}
		}
		return false;
	}
	
	public ServiceInfo getService(String serviceName){
		for (ServiceInfo sInfo: services){
			if (sInfo.getServiceName().equalsIgnoreCase(serviceName)){
				return sInfo;
			}
		}
		return null;
	}

	public int getServiceRuntimeSocketPort() {
		return serviceRuntimeSocketPort;
	}

}
