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
package org.societies.api.android.internal;

import java.util.List;

/**
 * Interface to access device status
 * 
 * @author Olivier (Trialog)
 */
public interface IDeviceStatus {
	String methodsArray [] = {"isInternetConnectivityOn(String callerPackageName)", "getConnectivityProvidersStatus(String callerPackageName)", "getLocationProvidersStatus(String callerPackageName)"};
	
	public static final String CONNECTIVITY_STATUS = "org.societies.android.platform.devicestatus.CONNECTIVITY_STATUS";
	public static final String CONNECTIVITY_INTERNET_ON = "org.societies.android.platform.devicestatus.CONNECTIVITYINTERNETON";
	public static final String CONNECTIVITY_PROVIDER_LIST = "org.societies.android.platform.devicestatus.CONNECTIVITYPROVIDERLIST";
	public static final String LOCATION_STATUS = "org.societies.android.platform.devicestatus.LOCATIONSTATUS";
	public static final String LOCATION_PROVIDER_LIST = "org.societies.android.platform.devicestatus.LOCATIONPROVIDERLIST";
	
	/**
	 * To know if Internet connectivity is available
	 * Send intent on CONNECTIVITY_STATUS value CONNECTIVITY_INTERNET_ON
	 * @param client Package name of service caller
	 * @return true if this connectivity is available
	 * @return otherwise false
	 */
	public boolean isInternetConnectivityOn(String callerPackageName);
	
	/**
	 * Retrieve the current status of connectivity providers
	 * Send intent on CONNECTIVITY_STATUS values CONNECTIVITY_INTERNET_ON, CONNECTIVITY_PROVIDER_LIST (List<org.societies.android.api.internal.ProviderStatus>)
	 * @param client Package name of service caller
	 * @return List of connectivity providers and their status
	 */
	public List<?> getConnectivityProvidersStatus(String callerPackageName);
	
	/**
	 * Retrieve the current status of location providers
	 * Send intent on LOCATION_STATUS value LOCATION_PROVIDER_LIST (List<org.societies.android.api.internal.ProviderStatus>)
	 * @param client Package name of service caller
	 * @return List of location providers and their status
	 */
	public List<?> getLocationProvidersStatus(String callerPackageName);
}
