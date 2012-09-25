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

package org.societies.android.api.internal.servicelifecycle;

import org.societies.android.api.servicelifecycle.AService;
import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;

/**
 *  Each method requires a callback to receive the result

 * @author aleckey
 *
 */
public interface IServiceDiscovery {
	
    //SERVICE LIFECYCLE INTENTS
	public static final String INTENT_RETURN_VALUE = "org.societies.android.platform.servicediscovery.ReturnValue";
	public static final String GET_SERVICE     = "org.societies.android.platform.servicediscovery.GET_SERVICE";
	public static final String GET_SERVICES    = "org.societies.android.platform.servicediscovery.GET_SERVICES";
	public static final String GET_MY_SERVICES     = "org.societies.android.platform.servicediscovery.GET_MY_SERVICES";
	public static final String SEARCH_SERVICES = "org.societies.android.platform.servicediscovery.SEARCH_SERVICES";


	public String methodsArray[] = {"getServices(String client, String identity)",
							 		"getService(String client, ServiceResourceIdentifier serviceId, String identity)",
							 		"searchService(String client, Service filter, String identity)",
							 		"getMyServices(String client)"
							};
	
	/**
	 * Gets list of 3rd party services available from this users cloud node
	 * @param client component package calling this method
	 */
    public AService[] getMyServices(String client);
    
	/**
	 * Gets list of 3rd party services available
	 * @param client component package calling this method
	 * @param identity The target node where search is to occur
	 */
    public AService[] getServices(String client, String identity);
    
    /**
	 * Gets details of a 3rd party service
	 * @param client component package calling this method
	 * @param identity The target node where search is to occur
	 */
    public AService getService(String client, AServiceResourceIdentifier serviceId, String identity);
    
    /**
	 * Searches list of 3rd party services available based on a filter
	 * @param client component package calling this method
	 * @param identity The target node where search is to occur
	 */
    public AService[] searchService(String client, AService filter, String identity);	
}
