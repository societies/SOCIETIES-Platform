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
package org.societies.personalisation.UserPreferenceManagement.impl.monitoring;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.UserPreferenceManagement;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.management.IPersonalisationInternalCallback;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.IPreferenceConditionIOutcomeName;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class UserPreferenceConditionMonitor implements IUserPreferenceConditionMonitor{
	
	private MonitoringTable mt;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private List<CtxAttributeIdentifier> registered; 
	private ICtxBroker broker;
	private UserPreferenceManagement prefMgr;
	private IInternalPersonalisationManager persoMgr;

	public UserPreferenceConditionMonitor(ICtxBroker broker, UserPreferenceManagement prefMgr, IInternalPersonalisationManager persoMgr){
		this.broker = broker;
		this.prefMgr = prefMgr;
		this.persoMgr = persoMgr;
		mt = new MonitoringTable();
		registered = new ArrayList<CtxAttributeIdentifier>();
		
	}
	/**
	 * 
	 * @param ownerId
	 * @param attribute
	 * @param callback
	 */
	@Override
	public void getOutcome(Identity ownerId, CtxAttribute attribute, IPersonalisationInternalCallback callback){
		/*
		 * in this method, we need to check what preferences are affected, request re-evaluation of them, compare last ioutcome with new and send it to 
		 * the proactivity decision maker component
		 */
		logging.info("Processing context event : "+attribute.getType());
		List<PreferenceDetails> affectedPreferences = this.mt.getAffectedPreferences(attribute.getId());
		if (affectedPreferences.size()==0){
			//JOptionPane.showMessageDialog(null, "no affected preferences found for ctxID: "+ctxAttr.getCtxIdentifier().toUriString()+",\n ignoring event");
			this.logging.debug("no affected preferences found for ctxID: "+attribute.getId().toString()+", ignoring event");
		}else{
			this.logging.debug("found affected preferences");
			if (null == this.prefMgr){
				this.logging.debug(UserPreferenceManagement.class.getName()+" not found");
				return;
			}else{
				this.logging.debug(UserPreferenceManagement.class.getName()+" Found");
			}
			List<IPreferenceOutcome> outcomes = prefMgr.reEvaluatePreferences(ownerId,attribute, affectedPreferences);
			logging.info("requested re-evaluation of preferences");
			callback.sendPrefOutcome(ownerId, outcomes);
		}
	}
	

	/**
	 * 
	 * @param ownerId
	 * @param action
	 * @param callback
	 */
	@Override
	public void getOutcome(Identity ownerId, IAction action, IPersonalisationInternalCallback callback){
		/*
		 * an action describes a personalisable parameter that the user (manually) or the User Agent (proactively) changed.
		 * An action does not describe a change in the state of the service. i.e. starting or stopping a service. Therefore,
		 * PCM returns the value of the personalisable parameter that was last applied or is currently applicable. 
		 * 
		 * The PCM is notified of changes in the personalisable parameters of a service using context. the User Action Monitor 
		 * populates the context database with this information as soon as it receives an action from a service. 
		 */
		List<IPreferenceOutcome> outcomes = new ArrayList<IPreferenceOutcome>();
		IPreferenceOutcome outcome = new PreferenceOutcome(action.getparameterName(), action.getvalue());
		outcome.setServiceID(action.getServiceID());
		outcome.setServiceType(action.getServiceType());
		outcomes.add(outcome);
		callback.sendPrefOutcome(ownerId, outcomes);
		
		
	}

	
	public void processServiceStarted(Identity userId, String serviceType, IServiceResourceIdentifier serviceID){

		
		//JOptionPaneshowMessageDialog(null, "Processing service started event: "+serviceID.toUriString());
		this.logging.debug("Adding "+serviceID.toString()+" preference details to tables");
		List<IPreferenceConditionIOutcomeName> conditionIOutcomeName = this.prefMgr.getPreferenceConditions(userId, serviceType, serviceID);
		//JOptionPaneshowMessageDialog(null, this.myUSERDPI.toUriString()+" received "+conditionIOutcomeName.size()+" preferenceConditions from PrefMgr");
		for (IPreferenceConditionIOutcomeName info : conditionIOutcomeName){
			this.mt.addInfo(info.getICtxIdentifier(), serviceID, serviceType, info.getPreferenceName());
			this.logging.debug("Added: "+info.getICtxIdentifier().toString()+" to"+serviceID.toString()+" affecting preference: "+info.getPreferenceName());
			//JOptionPaneshowMessageDialog(null, this.myUSERDPI.toUriString()+"Added: "+info.getCtxIdentifier().toUriString()+" to"+serviceID.toUriString()+" affecting preference: "+info.getPreferenceName());
			if (this.registered.contains(info.getICtxIdentifier())){
				this.logging.debug("Already subscribed for: "+info.getICtxIdentifier().toUriString());
			}else{
				this.persoMgr.registerForContextUpdate(userId, PersonalisationTypes.UserPreference, info.getICtxIdentifier());
				//this.registerForContextEvent((CtxAttributeIdentifier) info.getICtxIdentifier());
				this.registered.add((CtxAttributeIdentifier) info.getICtxIdentifier());
				this.logging.info(userId.toString()+" Registered for :"+info.getICtxIdentifier().toUriString());
			}
		}
	}


	public void processServiceStopped(Identity userId, String serviceType, IServiceResourceIdentifier serviceID){
		if (this.mt.isServiceRunning(serviceType, serviceID)){
			mt.removeServiceInfo(serviceType, serviceID);
		}else{
			logging.info("The details of this service were not properly loaded. Nothing to do!");
		}
	}
	
	public void processPreferenceChangedEvent(Identity userID, IServiceResourceIdentifier serviceId, String serviceType, String preferenceName){
		List<CtxIdentifier> ctxIDs = this.prefMgr.getPreferenceConditions(userID, serviceType, serviceId, preferenceName);
		for (CtxIdentifier id : ctxIDs){
			this.mt.addInfo(id, serviceId, serviceType, preferenceName);
			if (this.registered.contains(id)){
				this.logging.debug("Already subscribed for: "+id.toUriString());
			}else{
				//this.registerForContextEvent((CtxAttributeIdentifier) id);
				this.registered.add((CtxAttributeIdentifier) id);
			}
		}
		/*IOutcome out = this.prefMgr.getPreference(this.getMyUSERDPI(), serviceType, serviceId, prefName);
		if (out==null){
			this.logging.debug("Preference Manager returned no new outcomes for serviceType:"+serviceType+" and serviceID: "+serviceId);
		}else{
			this.sendToDM(serviceType, serviceId, prefName, out);
			
		}*/
	

	}
}
