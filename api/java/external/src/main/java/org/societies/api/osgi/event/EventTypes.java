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
 * The Class EventTypes.
 *
 *
 * @author pkuppuud
 */


public class EventTypes {

	/** Used to notify peers that the CSS friend request has been accepted */
	public static final String CSS_FRIENDED_EVENT = "org/societies/css/friended";

	/** Used to notify peers that the CSS Record has been modified */
	public static final String CSS_RECORD_EVENT = "org/societies/css/record";

	/** User to notify peers of a css event */
	public static final String CSS_ADV_EVENT = "org/societies/css/advert";

	/** User to notify peers of a css event */
	public static final String CSS_JOINED_EVENT = "eu/persist/css/joined";

	/** User to notify peers of a css event */
	public static final String CSS_LEFT_EVENT = "org/societies/css/left";

	/** User to notify peers of a css event */
	public static final String CSS_NOTIFICATION_EVENT = "org/societies/notification";

	/** User to notify peers of a context event */
	public static final String CONTEXT_EVENT = "org/societies/context";

	/** User to notify peers of a service lifelycle event */
	public static final String SERVICE_LIFECYCLE_EVENT = "org/societies/service/lifecycle";

	/** User to notify peers of a service fault */
	public static final String SERVICE_FAULT_EVENT = "org/societies/service/fault";

	/** Used by Service Management (Session Manager) to publish events concerning the status of sessions. */
    public static final String SERVICE_SESSION_EVENT = "org/societies/servicemgmt/session/status";

    /** Used by Service Management (Service Discovery) to notify peers of a new service. */
	public static final String NEW_SERVICE_EVENT = "org/societies/servicemgmt/service/newservice";

	/** Used by Service Management (Service Discovery) to notify peers of a removed service. */
	public static final String REMOVED_SERVICE_EVENT = "org/societies/servicemgmt/service/removeservice";

	/** Used by Service Management (Service Discovery) to notify a local peer of a failed service. */
	public static final String FAILED_SERVICE_EVENT = "org/societies/servicemgmt/service/failedservice";

	/** Used by User Agent Montior to publish event relating to use actions. */
	public static final String UIM_EVENT = "org/societies/useragent/monitoring/newaction";
	public static final String UIM_STATIC_ACTION = "org/societies/useragent/monitoring/staticaction";


	/** Used to notify peers of a failed negotiation */
	public static final String FAILED_NEGOTIATION_EVENT = "org/societies/privacyprotection/negotiation/failednegotiation";

	/** Used to notify peers of the result of privacy policy negotiation */
	public static final String PRIVACY_POLICY_NEGOTIATION_EVENT = "org/societies/privacyprotection/negotiation/negotiationResult";

	/** Used by the device drivers to publish events about device (sensors/actuators) data change*/
	public static final String DEVICE_MANAGEMENT_EVENT = "org/societies/css/device";

	/**	Published by the RFIDDriver to notify a change in the location of an RFID tag */
	public static final String RFID_UPDATE_EVENT = "org/societies/css/device/rfidUpdate";

	/** Used by CSS DataCollector to forward messages  	 */
	public static final String CSSDC_EVENT = "org/societies/orchestration/CSSDC";

	/** Used by ICO for recommendation messages  	 */
	public static final String ICO_RECOMMENDTION_EVENT = "org/societies/orchestration/ICO";

	// CIS RELATED EVENTS

	/** CIS creation */
	public static final String CIS_CREATION = "org/societies/cis/create";
	/** CIS deletion */
	public static final String CIS_DELETION = "org/societies/cis/delete";
	/** CIS subscription */
	public static final String CIS_SUBS = "org/societies/cis/subscription";
	/** CIS unsubscription */
	public static final String CIS_UNSUBS = "org/societies/cis/unsubscription";

	/** CIS restore - for the case when a CIS is restored from the database */
	public static final String CIS_RESTORE = "org/societies/cis/restore";


	/** Published by the DisplayDriver service to notify when a display is available to use */
	public static final String DISPLAY_EVENT = "org/societies/css/device/displayUpdate";

    /** Published by Userfeedback - consumed by webapp and android user feedback*/
	public static final String UF_PRIVACY_NEGOTIATION = "org/societies/useragent/feedback/privacyNegotiation";
	/** Published by webapp and android user feeedback - consumed by Userfeedback */
    public static final String UF_PRIVACY_NEGOTIATION_RESPONSE = "org/societies/useragent/feedback/privacyNegotiationResponse";
    /** Published by UserFeedback - consumed by webapp and android user feedback */
    public static final String UF_PRIVACY_NEGOTIATION_REMOVE_POPUP = "org/societies/useragent/feedback/privacyNegotiationRemovePopup";
    /** Published by Userfeedback - consumed by webapp and android user feedback*/
    public static final String UF_PRIVACY_ACCESS_CONTROL = "org/societies/useragent/feedback/privacyAccessControl";
    /** Published by webapp and android user feeedback - consumed by Userfeedback */
    public static final String UF_PRIVACY_ACCESS_CONTROL_RESPONSE = "org/societies/useragent/feedback/privacyAccessControlResponse";
    /** Published by UserFeedback - consumed by webapp and android user feedback */
    public static final String UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP = "org/societies/useragent/feedback/privacyAccessControlRemovePopup";
    
    
}
