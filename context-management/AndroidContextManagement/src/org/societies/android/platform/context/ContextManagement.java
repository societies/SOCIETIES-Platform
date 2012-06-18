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

package org.societies.android.platform.context;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.jivesoftware.smack.packet.IQ;
//import org.societies.android.api.useragent.IAndroidUserAgent;
import org.societies.android.api.context.broker.ICtxClientBroker;

/// allaksa se kanoniko api
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.useragent.monitoring.MethodType;
import org.societies.api.schema.useragent.monitoring.UserActionMonitorBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ContextManagement extends Service implements ICtxClientBroker{

	private static final String LOG_TAG = ContextManagement.class.getName();
	private static final List<String> ELEMENT_NAMES = Arrays.asList("userActionMonitorBean");
	private static final List<String> NAME_SPACES = Arrays.asList(
			"http://societies.org/api/schema/useragent/monitoring");
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.useragent.monitoring");

	private final ConcurrentHashMap<CtxIdentifier, CtxModelObject> modelObjects = null;
	private static Map<CtxIdentifier, CtxModelObject> contextCache = new ConcurrentHashMap<CtxIdentifier, CtxModelObject>();
	
	// TODO Remove and instantiate privateId properly so that privateId.toString() can be used instead
	private final String privateIdtoString = "myFooIIdentity@societies.local";
	
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
		Log.d(LOG_TAG, "Context Management service starting");
	}

	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "Context Management service terminating");
	}

	/**
	 * Create Binder object for local service invocation
	 */
	public class LocalBinder extends Binder {
		public ContextManagement getService() {
			return ContextManagement.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}
/*
	@Override
	public void monitor(IIdentity identity, IAction action){
		Log.d(LOG_TAG, "Context Management received monitored user action from identity " +identity.getJid()+ 
				": "+action.getparameterName()+" = "+action.getvalue());
		
		//CREATE MESSAGE BEAN
		UserActionMonitorBean uamBean = new UserActionMonitorBean();
		Log.d(LOG_TAG, "Creating message to send to virgo user agent");
		uamBean.setIdentity(identity.getJid());
		uamBean.setServiceResourceIdentifier(action.getServiceID());
		uamBean.setServiceType(action.getServiceType());
		uamBean.setParameterName(action.getparameterName());
		uamBean.setValue(action.getvalue());
		uamBean.setMethod(MethodType.MONITOR);
		
		Stanza stanza = new Stanza(toXCManager);
		
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
*/

	public Future<CtxAssociation> createAssociation(String arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<CtxAttribute> createAttribute(CtxEntityIdentifier scope,
			String type) throws CtxException {

		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

		final CtxEntity entity = (CtxEntity) modelObjects.get(scope);
		
		if (entity == null)	
			throw new NullPointerException("Scope not found: " + scope);
		
		CtxAttributeIdentifier attrIdentifier = new CtxAttributeIdentifier(scope, type, CtxModelObjectNumberGenerator.getNextValue());
		final CtxAttribute attribute = new CtxAttribute(attrIdentifier);

		this.modelObjects.put(attribute.getId(), attribute);
		entity.addAttribute(attribute);
		
		return (Future<CtxAttribute>) attribute;
		// on internalctxbroker
		//		return new AsyncResult<CtxAttribute>(attribute);
	}

	public CtxEntity createEntity(String type) throws CtxException {

		final CtxEntityIdentifier identifier;
		
		identifier = new CtxEntityIdentifier(this.privateIdtoString, 
					type, CtxModelObjectNumberGenerator.getNextValue());

		final CtxEntity entity = new  CtxEntity(identifier);
		if (entity.getId()!=null)
			Log.d(LOG_TAG, "Maps key is OK!!");
		else
			Log.d(LOG_TAG, "Problem with maps key!!");
	//	modelObjects.put(entity.getId(), entity);
		contextCache.put(entity.getId(), entity);
		
		return entity;
	}

	public void disableCtxMonitoring(CtxAttributeValueType arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void disableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void enableCtxMonitoring(CtxAttributeValueType arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void enableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public Future<List<CtxAttributeIdentifier>> getHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<List<CtxIdentifier>> lookup(CtxModelType arg0, String arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<List<CtxEntityIdentifier>> lookupEntities(String arg0,
			String arg1, Serializable arg2, Serializable arg3)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerForChanges(CtxChangeEventListener arg0,
			CtxIdentifier arg1) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void registerForChanges(CtxChangeEventListener arg0,
			CtxEntityIdentifier arg1, String arg2) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public Future<CtxModelObject> remove(CtxIdentifier arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<Boolean> removeHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<CtxModelObject> retrieve(CtxIdentifier arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<IndividualCtxEntity> retrieveAdministratingCSS(
			CtxEntityIdentifier arg0) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<Set<CtxBond>> retrieveBonds(CtxEntityIdentifier arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			CtxEntityIdentifier arg0) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(
			CtxEntityIdentifier arg0) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<Boolean> setHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public void unregisterFromChanges(CtxChangeEventListener arg0,
			CtxIdentifier arg1) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void unregisterFromChanges(CtxChangeEventListener arg0,
			CtxEntityIdentifier arg1, String arg2) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public Future<CtxModelObject> update(CtxModelObject arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<CtxAttribute> updateAttribute(CtxAttributeIdentifier arg0,
			Serializable arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<CtxAttribute> updateAttribute(CtxAttributeIdentifier arg0,
			Serializable arg1, String arg2) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Future<List<CtxAttributeIdentifier>> updateHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}	
	
	
	/*
	 * Callback - not needed
	 */
/*	private class UserAgentCallback implements ICommCallback{

		public List<String> getJavaPackages() {
			return PACKAGES;
		}

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public void receiveError(Stanza arg0, XMPPError arg1) {
			// TODO Auto-generated method stub
			
		}

		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			// TODO Auto-generated method stub
			
		}

		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			// TODO Auto-generated method stub
			
		}

		public void receiveMessage(Stanza arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}

		public void receiveResult(Stanza arg0, Object arg1) {
			// TODO Auto-generated method stub
			
		}
		
	} */
}
