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

package org.societies.android.platform.useragent.decisionmaker;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.useragent.monitoring.MonitoringMethodType;
import org.societies.api.schema.useragent.monitoring.UserActionMonitorBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.decisionmaking.IDecisionMaker;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.useragent.decisionmaking.DecisionMakingBean;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class AndroidDecisionMaker extends Service implements IAndroidDecisionMaker{

	private static final String LOG_TAG = AndroidDecisionMaker.class.getName();
	private static final List<String> ELEMENT_NAMES = Arrays.asList("decisionMakingBean");
	private static final List<String> NAME_SPACES = Arrays.asList(
			"http://societies.org/api/schema/useragent/decisionmaking");
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.useragent.decisionmaking");

	//currently hard coded but should be injected
	private static final String DESTINATION = "xcmanager.societies.local";

	private IIdentity toXCManager = null;
	private ClientCommunicationMgr ccm;

	private IBinder binder = null;

	@Override
	public void onCreate () {

		this.binder = new LocalBinder();

		this.ccm = new ClientCommunicationMgr(this);

		try {
			toXCManager = IdentityManagerImpl.staticfromJid(DESTINATION);
		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new RuntimeException(e);
		}     
		Log.d(LOG_TAG, "User Agent service starting");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "User Agent service terminating");
	}

	/**
	 * Create Binder object for local service invocation
	 */
	public class LocalBinder extends Binder {
		public AndroidDecisionMaker getService() {
			return AndroidDecisionMaker.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}

	public static DecisionMakingBean packageBean(List<IOutcome> intents,List<IOutcome> preferences){
		DecisionMakingBean result=new DecisionMakingBean();
		result.setIntentSize(intents.size());
		result.setPreferenceSize(preferences.size());
		result.getIntentServiceIds();
		result.getIntentParameterNames();
		result.getIntentServiceTypes();
		result.getPreferenceServiceIds();
		result.getPreferenceParameterNames();
		result.getPreferenceServiceTypes();
		for(int i=0;i<result.getIntentSize();i++){
			result.getIntentServiceIds().add(intents.get(i).getServiceID());
			result.getIntentServiceTypes().add(intents.get(i).getServiceType());
			result.getIntentParameterNames().add(intents.get(i).getparameterName());
			result.getIntentConfidenceLevel().add(intents.get(i).getConfidenceLevel());
		}
		for(int i=0;i<result.getPreferenceSize();i++){
			result.getPreferenceServiceIds().add(preferences.get(i).getServiceID());
			result.getPreferenceServiceTypes().add(preferences.get(i).getServiceType());
			result.getPreferenceParameterNames().add(preferences.get(i).getparameterName());
			result.getPreferenceConfidenceLevel().add(preferences.get(i).getConfidenceLevel());
		}
		return result;
	}
	
	@Override
	public void makeDecision(List<IOutcome> intents, List<IOutcome> preferences){
		Log.d(LOG_TAG, "Decision Maker received intentes " +intents);
		Log.d(LOG_TAG, "Decision Maker received preferences " +preferences);
		//CREATE MESSAGE BEAN
		Log.d(LOG_TAG, "Creating message to send to virgo user agent");
		DecisionMakingBean bean=packageBean(intents,preferences);
		Stanza stanza = new Stanza(toXCManager);
						
		try {
			Log.d(LOG_TAG, "No Call Back for DM:");
			ccm.sendMessage(stanza, bean);
			Log.d(LOG_TAG, "Stanza sent!");
		} catch (Exception e) {
			Log.e(LOG_TAG, Log.getStackTraceString(e));
        } 
	}
	
}
