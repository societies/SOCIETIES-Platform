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

package org.societies.android.platform.useragent.uam;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.personalisation.model.AAction;
import org.societies.android.api.useragent.IAndroidUserActionMonitor;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.useragent.monitoring.MonitoringMethodType;
import org.societies.api.schema.useragent.monitoring.UserActionMonitorBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;

import android.content.Context;
import android.util.Log;

public class AndroidUserActionMonitorBase implements IAndroidUserActionMonitor{

	//Logging tag
	private static final String LOG_TAG = AndroidUserActionMonitorBase.class.getName();
	private static final List<String> ELEMENT_NAMES = Arrays.asList("userActionMonitorBean");
	private static final List<String> NAME_SPACES = Arrays.asList(
			"http://societies.org/api/schema/useragent/monitoring");
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.useragent.monitoring");

	//private String cloudCommsDestination = null;
	//private String domainCommsDestination = null;
	private IIdentity cloudNodeIdentity = null;
	//private IIdentity domainNodeIdentity = null;

	private Context androidContext;
	private ClientCommunicationMgr ccm;
	private boolean restrictBroadcast;

	public AndroidUserActionMonitorBase(Context androidContext, ClientCommunicationMgr ccm, boolean restrictBroadcast){
		this.androidContext = androidContext;
		this.ccm = ccm;
		this.restrictBroadcast = restrictBroadcast;
		
		this.assignConnectionParameters();
	}

	public void monitor(String client, String identity, AAction aaction) {
		Log.d(LOG_TAG, "AndroidUserActionMonitor received monitored user action from client "+client+" with identity " +identity+ 
				": "+aaction.getparameterName()+" = "+aaction.getvalue());

		//CREATE MESSAGE BEAN
		UserActionMonitorBean uamBean = new UserActionMonitorBean();
		Log.d(LOG_TAG, "Creating message to send to virgo user agent");
		uamBean.setIdentity(identity);
		uamBean.setServiceResourceIdentifier(aaction.getServiceID());
		uamBean.setServiceType(aaction.getServiceType());
		uamBean.setParameterName(aaction.getparameterName());
		uamBean.setValue(aaction.getvalue());
		uamBean.setMethod(MonitoringMethodType.MONITOR);

		Stanza stanza = new Stanza(cloudNodeIdentity);

		ICommCallback callback = new UserAgentCallback();

		try {
			Log.d(LOG_TAG, "registering info with comms FW:");
			List<String> nameSpaces = callback.getXMLNamespaces();
			List<String> jPackages = callback.getJavaPackages();
			for(String nextNameSpace: nameSpaces){
				Log.d(LOG_TAG, nextNameSpace);
			}
			for(String nextPackage: jPackages){
				Log.d(LOG_TAG, nextPackage);
			}
			ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.SET, uamBean, callback);
			Log.d(LOG_TAG, "Stanza sent!");
		} catch (Exception e) {
			Log.e(LOG_TAG, Log.getStackTraceString(e));
		} 
	}

	/**
	 * Assign connection parameters (must happen after successful XMPP login)
	 */
	private void assignConnectionParameters() {
		//Get the Cloud destination
		String cloudCommsDestination = this.ccm.getIdManager().getCloudNode().getJid();
		Log.d(LOG_TAG, "Cloud Node: " + cloudCommsDestination);

		//String domainCommsDestination = this.ccm.getIdManager().getDomainAuthorityNode().getJid();
		//Log.d(LOG_TAG, "Domain Authority Node: " + domainCommsDestination);

		try {
			this.cloudNodeIdentity = IdentityManagerImpl.staticfromJid(cloudCommsDestination);
			Log.d(LOG_TAG, "Cloud node identity: " + this.cloudNodeIdentity);

			//this.domainNodeIdentity = IdentityManagerImpl.staticfromJid(domainCommsDestination);
			//Log.d(LOG_TAG, "Domain node identity: " + this.cloudNodeIdentity);

		} catch (InvalidFormatException e) {
			Log.e(LOG_TAG, "Unable to get cloud node identity", e);
			throw new RuntimeException(e);
		}     
	}
	
	/*
	 * Callback - not needed
	 */
	private class UserAgentCallback implements ICommCallback{

		public List<String> getJavaPackages() {
			return PACKAGES;
		}

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public void receiveError(Stanza arg0, XMPPError arg1) {
			//null
		}

		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			//null
		}

		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			//null
		}

		public void receiveMessage(Stanza arg0, Object arg1) {
			//null
		}

		public void receiveResult(Stanza arg0, Object arg1) {
			//null
		}
		
	}
}
