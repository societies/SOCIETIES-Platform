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
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.IFeedbackEvent;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.mock.EntityIdentifier;
import org.societies.api.mock.ServiceResourceIdentifier;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.UserPreferenceManagement.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.model.IPreferenceConditionIOutcomeName;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;

import sun.awt.PeerEvent;

public class PCM /*extends EventListener*/ implements IUserPreferenceConditionMonitor {

	private MonitoringTable mt;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private List<CtxAttributeIdentifier> registered; 
	private String myLocalID = null;
	private IServiceResourceIdentifier my_FW_SERVICE_ID;
	private ICtxBroker broker;
	private IUserPreferenceManagement prefMgr;
	private boolean isPeerUID = false;

	private IInternalPersonalisationManager persoMgr;

	private boolean enabled = true;

	public PCM(IInternalPersonalisationManager internalPersoMgr, ICtxBroker broker, IUserPreferenceManagement prefManager){
		
		this.persoMgr = internalPersoMgr;
		this.broker = broker;
		this.prefMgr = prefManager;

		this.mt = new MonitoringTable();
		this.registered = new ArrayList<CtxAttributeIdentifier>();
		this.logging.debug("initialised");

	}

	/**
	 * Method that allows the Proactivity component to request explicitly a re-evaluation of the preferences
	 * @param dpi				The Identity of the user
	 * @param serviceType		The serviceType of the service for which the component is requesting evaluation of preferences
	 * @param serviceID			The identifier of the service for which the component is requesting evaluation of preferences in the form of IServiceResourceIdentifier
	 * @param preferenceName	The name of the preference for which the component is requesting evaluation of
	 * @return 					The result of the re-evaluation
	 */
	public IOutcome requestOutcomeWithCurrentContext(
			Identity dpi, 
			String serviceType, 
			IServiceResourceIdentifier serviceID, 
			String preferenceName) {
		this.logging.debug("requestOutcomeWithCurrentContext(\n"+dpi.getAnnotation()+",\n"+serviceType+",\n"+serviceID.toUriString()+",\n"+preferenceName+")");
		IOutcome outcome = this.mt.getLastAction(serviceType, serviceID, preferenceName);

		if (null==outcome){

			outcome = this.prefMgr.reEvaluatePreferences(dpi, null, serviceType, serviceID, preferenceName);
			if (null!=outcome){
				this.mt.updateLastAction(serviceType, serviceID, preferenceName, outcome);
			}
		}


		return outcome;
	}


	private void ContextEventReceived(CtxAttribute ctxAttr){
		/*
		 * in this method, we need to check what preferences are affected, request re-evaluation of them, compare last ioutcome with new and send it to 
		 * the proactivity decision maker component
		 */
		logging.info("Processing context event : "+ctxAttr.getType());
		List<PreferenceDetails> affectedPreferences = this.mt.getAffectedPreferences(ctxAttr.getId());
		if (affectedPreferences.size()==0){
			//JOptionPane.showMessageDialog(null, this.myUSERDPI.toUriString()+"\nno affected preferences found for ctxID: "+ctxAttr.getCtxIdentifier().toUriString()+",\n ignoring event");
			this.logging.debug("no affected preferences found for ctxID: "+ctxAttr.getId().toString()+", ignoring event");
		}else{
			this.logging.debug("found affected preferences");
			if (null == this.prefMgr){
				this.logging.debug(IUserPreferenceManagement.class.getName()+" not found");
				return;
			}else{
				this.logging.debug(IUserPreferenceManagement.class.getName()+" Found");
			}
			List<IOutcome> outcomes = prefMgr.reEvaluatePreferences(this.getMyUSERDPI(),ctxAttr, affectedPreferences);
			logging.info("requested re-evaluation of preferences");
			for (int i=0; i<outcomes.size(); i++){
				IOutcome o = outcomes.get(i);
				//update tables
				this.sendToDM(o.getServiceType(), o.getServiceID(), o.getparameterName(), o);

			}
		}


	}

	private void sendToDM(String serviceType, IServiceResourceIdentifier serviceID, String parameterName, IOutcome outcome){
		if (serviceType==null){
			this.logging.debug("serviceType is null");

		}
		if (serviceID == null){
			this.logging.debug("serviceID is null");
		}

		if (parameterName==null){
			this.logging.debug("parameterName is null");

		}
		if (outcome==null){
			this.logging.debug("outcome is null");
		}
		if (this.mt == null){
			this.logging.debug("monitoring table is null");
		}
		IOutcome last = this.mt.getLastAction(serviceType, serviceID, parameterName);
		if (this.isPeerUID){
			if (last==null){
				this.stopMilliseconds = System.currentTimeMillis();
				if (this.enabled){
					this.persoMgr.sendIOutcome(getMyUSERDPI(), outcome, this.my_FW_SERVICE_ID);
				}else{
					this.logging.debug("PCM disabled by user. Use the PCM Utility GUI to enable it. ");
				}
			}else{ 
				if (!outcome.equals(last)){
					this.stopMilliseconds = System.currentTimeMillis();
					if (this.enabled){
						this.persoMgr.sendIOutcome(getMyUSERDPI(), outcome, this.my_FW_SERVICE_ID);
					}else{
						this.logging.debug("PCM disabled by user. Use the PCM Utility GUI to enable it. ");
					}
				}
			}
		}else{
			logging.info("This peer is not the current user interaction device. Not sending to DM");
		}
		this.mt.updateLastAction(serviceType, serviceID, parameterName, outcome);
	}
	




	public void processServiceStarted(PssService service){
		IServiceResourceIdentifier serviceID = service.getServiceId();
		String serviceType = service.getServiceType();
		//JOptionPaneshowMessageDialog(null, "Processing service started event: "+serviceID.toUriString());
		this.logging.debug("Adding "+serviceID.toUriString()+" preference details to tables");
		List<IPreferenceConditionIOutcomeName> conditionIOutcomeName = this.prefMgr.getPreferenceConditions(this.getMyUSERDPI(), serviceType, serviceID);
		//JOptionPaneshowMessageDialog(null, this.myUSERDPI.toUriString()+" received "+conditionIOutcomeName.size()+" preferenceConditions from PrefMgr");
		for (IPreferenceConditionIOutcomeName info : conditionIOutcomeName){
			this.mt.addInfo(info.getCtxIdentifier(), serviceID, serviceType, info.getPreferenceName());
			this.logging.debug("Added: "+info.getCtxIdentifier().toUriString()+" to"+serviceID.toUriString()+" affecting preference: "+info.getPreferenceName());
			//JOptionPaneshowMessageDialog(null, this.myUSERDPI.toUriString()+"Added: "+info.getCtxIdentifier().toUriString()+" to"+serviceID.toUriString()+" affecting preference: "+info.getPreferenceName());
			if (this.registered.contains(info.getCtxIdentifier())){
				this.logging.debug("Already subscribed for: "+info.getCtxIdentifier().toUriString());
			}else{
				this.registerForContextEvent((CtxAttributeIdentifier) info.getCtxIdentifier());
				this.registered.add((CtxAttributeIdentifier) info.getCtxIdentifier());
				this.logging.info(this.getMyUSERDPI().toUriString()+" Registered for :"+info.getCtxIdentifier().toUriString());
			}
		}

	}

	public void processServiceStopped(PssService service){
		IServiceResourceIdentifier serviceID = service.getServiceId();
		String serviceType = service.getServiceType();
		if (this.mt.isServiceRunning(serviceType, serviceID)){
			mt.removeServiceInfo(serviceType, serviceID);
		}else{
			logging.info("The details of this service were not properly loaded. Nothing to do!");
		}
	}

	public void processNewPreferences(PreferenceChangedEvent event){
		//JOptionPaneshowMessageDialog(null, this.myUSERDPI.toUriString()+"Processing PreferenceChangedEvent: ");
		this.logging.info(this.getMyUSERDPI().toUriString()+" Processing PreferenceChangedEvent");
		IServiceResourceIdentifier serviceID = event.getServiceID();

		if (serviceID!=null){
			String serviceType = event.getServiceType();
	
			String prefName = event.getPreferenceName();
			List<CtxIdentifier> ctxIDs = this.prefMgr.getPreferenceConditions(getMyUSERDPI(), serviceType, serviceID, prefName);
			//JOptionPaneshowMessageDialog(null, this.myUSERDPI.toUriString()+" received "+ctxIDs.size()+"preference conditions from prefMgr");
			for (CtxIdentifier id : ctxIDs){
				this.mt.addInfo(id, serviceID, serviceType, prefName);
				if (this.registered.contains(id)){
					this.logging.debug("Already subscribed for: "+id.toUriString());
				}else{
					this.registerForContextEvent((CtxAttributeIdentifier) id);
					this.registered.add((CtxAttributeIdentifier) id);
				}
			}
			IOutcome out = this.prefMgr.getPreference(this.getMyUSERDPI(), serviceType, serviceID, prefName);
			if (out==null){
				this.logging.debug("Preference Manager returned no new outcomes for serviceType:"+serviceType+" and serviceID: "+serviceID);
			}else{
				this.sendToDM(serviceType, serviceID, prefName, out);
				
			}
		}


	}
	/**
	 * method that allows the Proactivity component to request a re-evaluation of the preferences using future context as input
	 * 
	 * @param dpi				The Identity of the user
	 * @param serviceType 		The serviceType of the service for which the component is requesting evaluation of preferences
	 * @param serviceID 		The identifier of the service for which the component is requesting evaluation of preferences in the form of IServiceResourceIdentifier
	 * @param preferenceName 	The name of the preference for which the component is requesting evaluation of
	 * 
	 */
	public IOutcome requestOutcomeWithFutureContext(Identity dpi,
			String serviceType, IServiceResourceIdentifier serviceID, String preferenceName) {
		this.logging.debug("This method will not be implemented");
		return null;
	}


	private void registerForContextEvent(CtxAttributeIdentifier ctxAttrIdentifier){
		//JOptionPaneshowMessageDialog(null, this.myUSERDPI.toUriString()+" registering for ctx attr events for: \n"+ctxAttrIdentifier.toUriString());
		this.logging.debug("attempting to register for context event");
		try {
			if (broker==null){
				this.logging.debug("broker is null");
			}
			if (ctxAttrIdentifier==null){
				this.logging.debug("ctxAttrIdentifier is null");
			}
			this.broker.registerUpdateNotification(this, ctxAttrIdentifier);
			//JOptionPaneshowMessageDialog(null, this.myUSERDPI.toUriString()+"registered for context event: "+ctxAttrIdentifier.toUriString());
			this.logging.debug("registered for context event: "+ctxAttrIdentifier.toUriString());
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void handlePSSEvent(PSSEvent event) {
		this.startMilliseconds = System.currentTimeMillis();
		this.logging.debug("PSS Event Received " + event);
		String value = (String) event.geteventInfoAsXML();

		if (event.geteventType().equals(PSSEventTypes.CONTEXT_UPDATE_EVENT)){
			try{
				CtxAttribute ctxAttr = (CtxAttribute) XMLConverter.xmlToObject(event.geteventInfoAsXML(),CtxAttribute.class);
				CtxIdentifier id = broker.parseIdentifier((String) event.geteventName());
				String type = id.getType();
				this.logging.debug("Context Event received: type: "+type+" value: "+value);
				this.ContextEventReceived(ctxAttr);
			}
			catch (CtxException e){
				this.logging.debug("Error while processing PSS context event");
			}
		}else{
			this.logging.debug("Received event of unknown type. Ignoring event: "+event.geteventType()); 
		}

	}




	public void handlePeerEvent(PeerEvent event) {
		this.startMilliseconds = System.currentTimeMillis();
		try{
			this.logging.debug("Peer Event Received " + event.geteventType());
			if (event.geteventType().equals(PSSEventTypes.CONTEXT_UPDATE_EVENT)){
				CtxAttribute ctxAttr = (CtxAttribute) event.geteventInfo();
				String type = ctxAttr.getType();
				String value = ctxAttr.getStringValue();
				CtxIdentifier id = ctxAttr.getCtxIdentifier();

				this.logging.debug("Event received: type: "+type+" value: "+value);
				this.ContextEventReceived( ctxAttr);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void testRegisterForEvent(){

		try {
			CtxEntity entity = (CtxEntity) broker.createEntity("Person");
			CtxAttribute ctxAttribute = (CtxAttribute) broker.createAttribute(entity.getCtxIdentifier(), "symLoc");

			//CtxAttributeIdentifier identifier = (CtxAttributeIdentifier) broker.parseIdentifier(id);
			this.registerForContextEvent(ctxAttribute.getCtxIdentifier());
			//CtxAttribute ctxAttribute = (CtxAttribute) broker.retrieve(identifier);
			ctxAttribute.setStringValue("Klanoxori Arkadias");
			broker.update(ctxAttribute);
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}




	/* (non-Javadoc)
	 * @see org.personalsmartspace.pro.common.api.platform.IIOutcomeProvider#getOutcome(org.personalsmartspace.sre.api.pss3p.Identity, org.personalsmartspace.sre.api.pss3p.IServiceResourceIdentifier, java.lang.String)
	 */

	public IOutcome getOutcome(Identity dpi,
			IServiceResourceIdentifier serviceID, String parameterName) {
		return mt.getLastAction("", serviceID, parameterName);
	}
	
	
	
	private void printTable(){
		this.mt.printTable();
	}
	
	private String getTableAsString(){
		return this.mt.toString();
	}


	@Override
	public IPreferenceOutcome getOutcome(CtxAttribute arg0,
			EntityIdentifier arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPreferenceOutcome requestOutcomeWithCurrentContext(
			EntityIdentifier arg0, String arg1, ServiceResourceIdentifier arg2,
			String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPreferenceOutcome requestOutcomeWithFutureContext(
			EntityIdentifier arg0, String arg1, ServiceResourceIdentifier arg2,
			String arg3) {
		// TODO Auto-generated method stub
		return null;
	}


}

