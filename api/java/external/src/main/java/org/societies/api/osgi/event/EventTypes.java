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
package org.societies.api.osgi.event;

/**
 * @author pkuppuud
 *
 */
public class EventTypes {

	
	public static final String CSS_ADV_EVENT = "org/societies/css/advert";	
	public static final String CSS_JOINED_EVENT = "eu/persist/css/joined";
	public static final String CSS_LEFT_EVENT = "org/societies/css/left";
	public static final String CSS_NOTIFICATION_EVENT = "org/societies/notification";
	public static final String CONTEXT_EVENT = "org/societies/context";
	public static final String SERVICE_LIFECYCLE_EVENT = "org/societies/service/lifecycle";
	public static final String SERVICE_FAULT_EVENT = "org/societies/service/fault";	
	public static final String PSS_ADV_EVENT = "eu/persist/onm/pss/advert";	
	
	/**
	 * Used by Service Management (Session Manager) to publish events concerning the status
	 * of sessions 
	 */
        public static final String SERVICE_SESSION_EVENT = "org/societies//servicemgmt/session/status";
        /**
         * Used by Service Management (Service Discovery) to notify peers of a new service
         */
        public static final String NEW_SERVICE_EVENT = "org/societies/servicemgmt/service/newservice";
        /**
         * Used by Service Management (Service Discovery) to notify peers of a removed service
         */
        public static final String REMOVED_SERVICE_EVENT = "org/societies/servicemgmt/service/removeservice";
        /**
         * Used by Service Management (Service Discovery) to notify a local peer of a failed service
         */
        public static final String FAILED_SERVICE_EVENT = "org/societies/servicemgmt/service/failedservice";
        

        
}
