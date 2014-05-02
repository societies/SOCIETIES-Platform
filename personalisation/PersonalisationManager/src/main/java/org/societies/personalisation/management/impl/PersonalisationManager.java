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
package org.societies.personalisation.management.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.*;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.internal.personalisation.model.IFeedbackEvent;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.decisionmaking.IDecisionMaker;
import org.societies.api.internal.useragent.monitoring.UIMEvent;
import org.societies.api.osgi.event.*;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.personalisation.model.PersonalisablePreferenceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRIST.api.model.ICRISTUserAction;
import org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.ActionInformation;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PersonalisationManager extends EventListener implements IPersonalisationManager, IInternalPersonalisationManager, CtxChangeEventListener {

	private static final String CRIST_CONFIDENCE_LEVEL = "cristConfidenceLevel";
	private static final String CAUI_CONFIDENCE_LEVEL = "cauiConfidenceLevel";
	private static final String PREF_MGR_CONFIDENCE_LEVEL = "prefMgrConfidenceLevel";
	private static final String DIANNE_CONFIDENCE_LEVEL = "dianneConfidenceLevel";
	// services
	private ICtxBroker ctxBroker;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private IUserPreferenceConditionMonitor pcm;
	private IDIANNE dianne;
	private ICAUIPrediction cauiPrediction;
	private ICRISTUserIntentPrediction cristPrediction;
	private IIdentityManager idm;
	private IDecisionMaker decisionMaker;
	private ICommManager commsMgr;
	private IEventMgr eventMgr;

	// data structures
	ArrayList<CtxAttributeIdentifier> dianneList;
	ArrayList<CtxAttributeIdentifier> prefMgrList;
	ArrayList<CtxAttributeIdentifier> cauiList;
	ArrayList<CtxAttributeIdentifier> cristList;

	//internal confidence levels for each personalisation source
	private int dianneConfidenceLevel;
	private int prefMgrConfidenceLevel;
	private int cauiConfidenceLevel;
	private int cristConfidenceLevel;

	private Hashtable<String, ActionInformation> actionInformation;

	public PersonalisationManager() {
		this.actionInformation = new Hashtable<String, ActionInformation>();
		this.dianneList = new ArrayList<CtxAttributeIdentifier>();
		this.prefMgrList = new ArrayList<CtxAttributeIdentifier>();
		this.cauiList = new ArrayList<CtxAttributeIdentifier>();
		this.cristList = new ArrayList<CtxAttributeIdentifier>();
		if (this.logging.isDebugEnabled()){
			this.logging.debug("executed constructor");
		}
	}

	public void initialisePersonalisationManager(ICtxBroker broker, IUserPreferenceConditionMonitor pcm, IDIANNE dianne, ICAUIPrediction cauiPrediction, ICRISTUserIntentPrediction cristPrediction, ICommManager commsMgr, IDecisionMaker decisionMaker) {
		this.ctxBroker = broker;
		this.pcm = pcm;
		this.dianne = dianne;
		this.cauiPrediction = cauiPrediction;
		this.cristPrediction = cristPrediction;
		this.setCommsMgr(commsMgr);
		this.decisionMaker = decisionMaker;
		this.idm = this.getCommsMgr().getIdManager();
		retrieveConfidenceLevels();
		this.registerForEvents();
		this.dianne.registerContext();
		if (this.logging.isDebugEnabled()){
			this.logging.debug("initialisePersonalisationManager(ICtxBroker broker, IUserPreferenceConditionMonitor pcm, IDIANNE dianne, ICAUIPrediction cauiPrediction, ICRISTUserIntentPrediction cristPrediction, ICommManager commsMgr, IDecisionMaker decisionMaker)");
		}
	}

	public void initialisePersonalisationManager() {

		this.dianneList = new ArrayList<CtxAttributeIdentifier>();
		this.prefMgrList = new ArrayList<CtxAttributeIdentifier>();
		this.cauiList = new ArrayList<CtxAttributeIdentifier>();
		this.cristList = new ArrayList<CtxAttributeIdentifier>();
		this.registerForEvents();
		retrieveConfidenceLevels();
		this.dianne.registerContext();
		if (this.logging.isDebugEnabled()){
			this.logging.debug("initialisePersonalisationManager()");
		}

	}


	private void registerForEvents() {
		String eventFilter = "(&" +
				"(" + CSSEventConstants.EVENT_NAME + "=newaction)" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/useragent/monitoring)" +
				")";
		this.getEventMgr().subscribeInternalEvent(this, new String[]{EventTypes.UIM_EVENT}, eventFilter);

		if (this.logging.isDebugEnabled()){
			this.logging.debug("Subscribed to " + EventTypes.UIM_EVENT + " events");
		}

		String uiEventFilter = "(&" +
				"(" + CSSEventConstants.EVENT_NAME + "=feedback)" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/useragent/decisionmaker)" +
				")";

		this.getEventMgr().subscribeInternalEvent(this, new String[]{EventTypes.UI_EVENT}, uiEventFilter);
		if (this.logging.isDebugEnabled()){
			this.logging.debug("Subscribed to " + EventTypes.UI_EVENT + " events");
		}
	}

	private void retrieveConfidenceLevels() {

		IIdentity userId = this.idm.getThisNetworkNode();
		try {
			Future<List<CtxIdentifier>> futuredianneConf = this.ctxBroker.lookup(userId, CtxModelType.ATTRIBUTE, DIANNE_CONFIDENCE_LEVEL);
			List<CtxIdentifier> dianneConfs = futuredianneConf.get();
			if (dianneConfs.isEmpty()) {
				this.dianneConfidenceLevel = 50;
			} else {
				CtxAttribute tempAttr = (CtxAttribute) this.ctxBroker.retrieve(dianneConfs.get(0)).get();
				this.dianneConfidenceLevel = tempAttr.getIntegerValue();
			}

			Future<List<CtxIdentifier>> futureprefMgrConf = this.ctxBroker.lookup(userId, CtxModelType.ATTRIBUTE, PREF_MGR_CONFIDENCE_LEVEL);
			List<CtxIdentifier> prefMgrConf = futureprefMgrConf.get();
			if (prefMgrConf.isEmpty()) {
				this.prefMgrConfidenceLevel = 50;
			} else {
				CtxAttribute tempAttr = (CtxAttribute) this.ctxBroker.retrieve(prefMgrConf.get(0)).get();
				this.prefMgrConfidenceLevel = tempAttr.getIntegerValue();
			}

			Future<List<CtxIdentifier>> futurecauiConf = this.ctxBroker.lookup(userId, CtxModelType.ATTRIBUTE, CAUI_CONFIDENCE_LEVEL);
			List<CtxIdentifier> cauiConf = futurecauiConf.get();
			if (cauiConf.isEmpty()) {
				this.cauiConfidenceLevel = 50;
			} else {
				CtxAttribute tempAttr = (CtxAttribute) this.ctxBroker.retrieve(cauiConf.get(0)).get();
				this.cauiConfidenceLevel = tempAttr.getIntegerValue();
			}


			Future<List<CtxIdentifier>> futurecristConf = this.ctxBroker.lookup(userId, CtxModelType.ATTRIBUTE, CRIST_CONFIDENCE_LEVEL);
			List<CtxIdentifier> cristConf = futurecauiConf.get();
			if (cristConf.isEmpty()) {
				this.cristConfidenceLevel = 50;
			} else {
				CtxAttribute tempAttr = (CtxAttribute) this.ctxBroker.retrieve(cristConf.get(0)).get();
				this.cristConfidenceLevel = tempAttr.getIntegerValue();
			}
			if (this.logging.isDebugEnabled()){
				logging.debug("retrieved confidence levels");
			}
		} catch (CtxException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		} catch (InterruptedException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		} catch (ExecutionException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		}


	}


	private void storeConfidenceLevels(){
		IIdentity userId = this.idm.getThisNetworkNode();
		try {
			IndividualCtxEntity personEntity = this.ctxBroker.retrieveIndividualEntity(userId).get();
			if (personEntity!=null){
				Set<CtxAttribute> attributes = personEntity.getAttributes(DIANNE_CONFIDENCE_LEVEL);
				if (attributes.size()==0){
					CtxAttribute dianneConfAttr = this.ctxBroker.createAttribute(personEntity.getId(), DIANNE_CONFIDENCE_LEVEL).get();
					dianneConfAttr.setIntegerValue(this.dianneConfidenceLevel);
					this.ctxBroker.update(dianneConfAttr).get();
				}else{
					CtxAttribute dianneConfAttr = attributes.iterator().next();
					dianneConfAttr.setIntegerValue(this.dianneConfidenceLevel);
					this.ctxBroker.update(dianneConfAttr).get();				
				}

				Set<CtxAttribute> prefAttrs = personEntity.getAttributes(PREF_MGR_CONFIDENCE_LEVEL);
				if (prefAttrs.size()==0){
					CtxAttribute prefConfAttr = this.ctxBroker.createAttribute(personEntity.getId(), PREF_MGR_CONFIDENCE_LEVEL).get();
					prefConfAttr.setIntegerValue(this.prefMgrConfidenceLevel);
					this.ctxBroker.update(prefConfAttr).get();
				}else{
					CtxAttribute prefConfAttr = prefAttrs.iterator().next();
					prefConfAttr.setIntegerValue(this.prefMgrConfidenceLevel);
					this.ctxBroker.update(prefConfAttr).get();
				}

				Set<CtxAttribute> cauiAttrs = personEntity.getAttributes(CAUI_CONFIDENCE_LEVEL);
				if (cauiAttrs.size()==0){
					CtxAttribute cauiConfAttr = this.ctxBroker.createAttribute(personEntity.getId(), CAUI_CONFIDENCE_LEVEL).get();
					cauiConfAttr.setIntegerValue(this.cauiConfidenceLevel);
					this.ctxBroker.update(cauiConfAttr).get();
				}else{
					CtxAttribute cauiConfAttr = cauiAttrs.iterator().next();
					cauiConfAttr.setIntegerValue(this.cauiConfidenceLevel);
					this.ctxBroker.update(cauiConfAttr).get();
				}

				Set<CtxAttribute> cristAttrs = personEntity.getAttributes(CRIST_CONFIDENCE_LEVEL);
				if (cristAttrs.size()==0){
					CtxAttribute cristConfAttr = this.ctxBroker.createAttribute(personEntity.getId(), CRIST_CONFIDENCE_LEVEL).get();
					cristConfAttr.setIntegerValue(this.cristConfidenceLevel);
					this.ctxBroker.update(cristConfAttr).get();
				}

				if (this.logging.isDebugEnabled()){
					logging.debug("updated confidence levels");
				}
			}
			
		} catch (CtxException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		} catch (InterruptedException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		} catch (ExecutionException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		}

	}
	public ICAUIPrediction getCauiPrediction() {
		return cauiPrediction;
	}

	public void setCauiPrediction(ICAUIPrediction cauiPrediction) {
		this.cauiPrediction = cauiPrediction;
	}

	public ICRISTUserIntentPrediction getCristPrediction() {
		return cristPrediction;
	}

	public void setCristPrediction(ICRISTUserIntentPrediction cristPrediction) {
		this.cristPrediction = cristPrediction;
	}

	public IUserPreferenceConditionMonitor getPcm() {
		if (logging.isDebugEnabled()) logging.debug(this.getClass().getName() + "Return PCM");
		return pcm;
	}

	public void setPcm(IUserPreferenceConditionMonitor pcm) {
		if (logging.isDebugEnabled()) logging.debug(this.getClass().getName() + "GOT PCM");
		this.pcm = pcm;
	}

	public IDIANNE getDianne() {
		if (logging.isDebugEnabled()) logging.debug(this.getClass().getName() + "Return DIANNE");
		return dianne;
	}

	public void setDianne(IDIANNE dianne) {
		if (logging.isDebugEnabled()) logging.debug(this.getClass().getName() + "GOT DIANNE");
		this.dianne = dianne;
	}

	public ICtxBroker getCtxBroker() {
		if (logging.isDebugEnabled()) logging.debug(this.getClass().getName() + "Return CtxBroker");
		return this.ctxBroker;
	}

	public void setCtxBroker(ICtxBroker broker) {
		if (logging.isDebugEnabled()) logging.debug(this.getClass().getName() + "GOT CtxBroker");
		this.ctxBroker = broker;
	}

	public IDecisionMaker getDecisionMaker() {
		return decisionMaker;
	}

	public void setDecisionMaker(IDecisionMaker decisionMaker) {
		this.decisionMaker = decisionMaker;
	}

	/**
	 * @return the commsMgr
	 */
	public ICommManager getCommsMgr() {
		return commsMgr;
	}

	/**
	 * @param commsMgr the commsMgr to set
	 */
	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
		this.idm = commsMgr.getIdManager();
	}

	/**
	 * @return the eventMgr
	 */
	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	/**
	 * @param eventMgr the eventMgr to set
	 */
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.societies.personalisation.common.api.management.
	 * IInternalPersonalisationManager
	 * #returnFeedback(org.societies.api.internal.
	 * personalisation.model.IFeedbackEvent)
	 */
	@Override
	public void returnFeedback(IFeedbackEvent arg0) {


	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.societies.personalisation.common.api.management.
	 * IInternalPersonalisationManager
	 * #registerForContextUpdate(org.societies.api
	 * .comm.xmpp.datatypes.IIdentity,
	 * org.societies.personalisation.common.api.model.PersonalisationTypes,
	 * org.societies.api.context.model.CtxAttributeIdentifier)
	 */
	@Override
	public void registerForContextUpdate(IIdentity id,
			PersonalisationTypes type, CtxAttributeIdentifier ctxAttributeId) {
		try {
			if (isAlreadyRegistered(ctxAttributeId)) {
				if (this.logging.isDebugEnabled()){
					this.logging.debug(type.toString() + " requested event registration for: " + ctxAttributeId.getType() + " but I'm already registered for it.");
				}
			} else {
				this.ctxBroker.registerForChanges(this, ctxAttributeId);
				if (this.logging.isDebugEnabled()){
					this.logging.debug(type.toString() + " requested event registration for: " + ctxAttributeId.getType());
				}
			}

		} catch (CtxException e) {
			logging.error(e.getMessage());
			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		}

		switch (type) {
		case UserPreference:
			this.addtoList(ctxAttributeId, prefMgrList);
		case DIANNE:
			this.addtoList(ctxAttributeId, dianneList);
			break;
		case CAUIIntent:
			this.addtoList(ctxAttributeId, cauiList);
			break;
		case CRISTIntent:
			this.addtoList(ctxAttributeId, cristList);
			break;
		default:
			return;
		}

	}

	private boolean isAlreadyRegistered(CtxAttributeIdentifier id) {
		for (CtxAttributeIdentifier ctxAttrId : this.prefMgrList) {
			if (ctxAttrId.equals(id)) {
				return true;
			}
		}

		for (CtxAttributeIdentifier ctxAttrId : this.dianneList) {
			if (ctxAttrId.equals(id)) {
				return true;
			}
		}

		for (CtxAttributeIdentifier ctxAttrId : this.cauiList) {
			if (ctxAttrId.equals(id)) {
				return true;
			}
		}

		for (CtxAttributeIdentifier ctxAttrId : this.cristList) {
			if (ctxAttrId.equals(id)) {
				return true;
			}
		}
		return false;

	}

	private void addtoList(CtxAttributeIdentifier ctxId,
			List<CtxAttributeIdentifier> list) {
		for (CtxAttributeIdentifier Id : list) {
			if (ctxId.equals(Id)) {
				return;
			}
		}

		list.add(ctxId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.societies.personalisation.common.api.management.
	 * IInternalPersonalisationManager
	 * #getPreference(org.societies.api.comm.xmpp.datatypes.IIdentity,
	 * java.lang.String,
	 * org.societies.api.servicelifecycle.model.ServiceResourceIdentifier,
	 * java.lang.String,
	 * org.societies.api.personalisation.mgmt.IPersonalisationCallback)
	 */
	@Override
	public Future<IAction> getPreference(IIdentity ownerID, String serviceType,
			ServiceResourceIdentifier serviceID, String preferenceName) {
		if (this.logging.isDebugEnabled()){
			this.logging.debug("Processing request for preference: " + preferenceName + " for serviceType: " + serviceType + " and serviceID " + serviceID.getServiceInstanceIdentifier());
		}

		Future<List<IDIANNEOutcome>> futureDianneOuts;

		futureDianneOuts = this.dianne.getOutcome(ownerID, serviceID, preferenceName);

		if (futureDianneOuts == null) {
			futureDianneOuts = new AsyncResult<List<IDIANNEOutcome>>(new ArrayList<IDIANNEOutcome>());
			if (this.logging.isDebugEnabled()){
				this.logging.debug(".getPreference(...): DIANNE returned null list");
			}
		}
		Future<IOutcome> futurePrefOuts;

		futurePrefOuts = this.pcm.getOutcome(ownerID, serviceType, serviceID, preferenceName);
		


		if (futurePrefOuts == null) {
			futurePrefOuts = new AsyncResult<IOutcome>(null);
			if (this.logging.isDebugEnabled()){
				this.logging.debug(".getPreference(...): PCM returned null list");
			}
		}
		IAction action;
		try {
			List<IDIANNEOutcome> dianneOutList = futureDianneOuts.get();
			if (dianneOutList.size() > 0) {

				IDIANNEOutcome dianneOut = dianneOutList.get(0);
				if (this.logging.isDebugEnabled()){
					this.logging.debug(".getPreference(...): DIANNE returned an outcome: " + dianneOut.toString());
				}
				IPreferenceOutcome prefOut = (IPreferenceOutcome) futurePrefOuts.get();

				if (null == prefOut) {
					if (this.logging.isDebugEnabled()){
						this.logging.debug(".getPreference(...): PCM didn't return an outcome. Returning DIANNE's outcome: " + dianneOut.toString());
					}
					
					String str = "Request for preference: ";
					str = str.concat("\nDIANNE outcome:  ");
					
					str = str.concat(dianneOut.getparameterName()+" = "+dianneOut.getvalue());
					
					str = str.concat("\nPreference outcome is null, returning DIANNE outcome ");
				
					this.logging.info(str);
					
					return new AsyncResult<IAction>(dianneOut);
				}

				if (this.logging.isDebugEnabled()){
					this.logging.debug(".getPreference(...): PCM returned an outcome " + prefOut.toString());
				}
				if (dianneOut.getvalue().equalsIgnoreCase(prefOut.getvalue())) {
					action = new Action(serviceID, serviceType, preferenceName, prefOut.getvalue());
					action.setServiceID(serviceID);
					action.setServiceType(serviceType);
					if (this.logging.isDebugEnabled()){
						this.logging.debug(".getPreference(...): returning action: " + action.toString());
					}
					
					String str = "Request for preference: ";
					str = str.concat("\nBoth DIANNE and Preferences agree on the following outcome outcome:  ");
					
					str = str.concat(action.getparameterName()+" = "+action.getvalue());
									
					this.logging.info(str);
					return new AsyncResult<IAction>(action);
				} else {
					if (this.logging.isDebugEnabled()){
						this.logging.debug(".getPreference(...): conflict between pcm and dianne.");
					}
					IOutcome resolvePreferenceConflicts = this.resolvePreferenceConflicts(dianneOut, prefOut);
					
					if (null==resolvePreferenceConflicts){
						String str = "Request for preference: ";
						str = str.concat("\nPreference outcome:  ");
						
						str = str.concat(prefOut.getparameterName()+" = "+prefOut.getvalue());
						
						str = str.concat("\nDIANNE outcome: ");
						
						str = str.concat(dianneOut.getparameterName()+" = "+dianneOut.getvalue());
						
						str = str.concat("\nAfter conflict resolution, returning null action: ");
						this.logging.info(str);
					}else{
						String str = "Request for preference: ";
						str = str.concat("\nPreference outcome:  ");
						
						str = str.concat(prefOut.getparameterName()+" = "+prefOut.getvalue());
						
						str = str.concat("\nDIANNE outcome: ");
						
						str = str.concat(dianneOut.getparameterName()+" = "+dianneOut.getvalue());
						
						str = str.concat("\nAfter conflict resolution, returning action: "+resolvePreferenceConflicts.getparameterName()+" = "+resolvePreferenceConflicts.getvalue());
						this.logging.info(str);
					}
					
					return new AsyncResult<IAction>(resolvePreferenceConflicts);
				}

			} else {

				IPreferenceOutcome prefOut = (IPreferenceOutcome) futurePrefOuts.get();
				if (prefOut != null) {
					if (this.logging.isDebugEnabled()){
						this.logging.debug(".getPreference(...): DIANNE didn't return an outcome. Returning PCM's outcome: " + prefOut.toString());
					}
					String str = "Request for preference: ";
					str = str.concat("\nPreference outcome:  ");
					
					str = str.concat(prefOut.getparameterName()+" = "+prefOut.getvalue());
					
					str = str.concat("\nDIANNE outcome is null, returning preference outcome ");
				
					this.logging.info(str);
					return new AsyncResult<IAction>(prefOut);
				}
			}
		} catch (InterruptedException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		} catch (ExecutionException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		}

		if (this.logging.isDebugEnabled()){
			this.logging.debug(".getPreference(...): Returning null action");
		}
		return new AsyncResult<IAction>(null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.societies.api.personalisation.mgmt.IPersonalisationManager#
	 * getIntentAction(org.societies.api.comm.xmpp.datatypes.IIdentity,
	 * org.societies.api.comm.xmpp.datatypes.IIdentity,
	 * org.societies.api.servicelifecycle.model.ServiceResourceIdentifier,
	 * java.lang.String,
	 * org.societies.api.personalisation.mgmt.IPersonalisationCallback)
	 */
	@Override
	public Future<IAction> getIntentAction(Requestor requestor, IIdentity ownerID,
			ServiceResourceIdentifier serviceID, String preferenceName) {
		
		return this.getIntentAction(ownerID, serviceID, preferenceName);
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.api.personalisation.mgmt.IPersonalisationManager#getPreference(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, java.lang.String, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String)
	 */
	@Override
	public Future<IAction> getPreference(Requestor requestor, IIdentity ownerID,
			String serviceType, ServiceResourceIdentifier serviceID,
			String preferenceName) {
		//check with access control
		return this.getPreference(ownerID, serviceType, serviceID, preferenceName);

	}

	@Override
	public void registerPersonalisableService(IActionConsumer actionConsumer) {
		if (actionConsumer == null)
			throw new IllegalArgumentException("actionConsumer cannot be null");

		List<PersonalisablePreferenceIdentifier> preferenceIdentifiers = actionConsumer.getPersonalisablePreferences();

		if (preferenceIdentifiers == null) {
			throw new IllegalArgumentException("actionConsumer.getPersonalisablePreferences() must return a list of personalisable preferences");
		}

		for (PersonalisablePreferenceIdentifier pref : preferenceIdentifiers) {
			this.pcm.getPreferenceManager().registerPersonalisableService(actionConsumer, pref);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.common.api.management.IInternalPersonalisationManager#getIntentAction(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String)
	 */
	@Override
	public Future<IAction> getIntentAction(IIdentity ownerID,
			ServiceResourceIdentifier serviceID, String preferenceName) {
		
		Future<IUserIntentAction> futureCAUIOuts;
		try {
			futureCAUIOuts = this.cauiPrediction.getCurrentIntentAction(ownerID, serviceID, preferenceName);
		} catch (Exception e) {
			if (logging.isDebugEnabled()){ logging.error("Error", e); }
			futureCAUIOuts = new AsyncResult<IUserIntentAction>(null);
		}
		Future<CRISTUserAction> futureCRISTOuts;
		try {
			futureCRISTOuts = this.cristPrediction.getCurrentUserIntentAction(ownerID, serviceID, preferenceName);
		} catch (Exception e) {
			if (logging.isDebugEnabled()){ logging.error("Error", e); }
			futureCRISTOuts = new AsyncResult<CRISTUserAction>(null);
		}
		IAction action;

		try {
			IUserIntentAction cauiOut = futureCAUIOuts.get();
			CRISTUserAction cristOut = futureCRISTOuts.get();

			if (cauiOut == null) {
				if (cristOut == null) {
					return new AsyncResult<IAction>(null);
				} else {
					String str = "Request for user intent: ";
					str = str.concat("\nCRIST outcome:  ");
					
					str = str.concat(cristOut.getparameterName()+" = "+cristOut.getvalue());
					
					str = str.concat("\nCAUI outcome is null, returning CRIST outcome ");
					
			
					this.logging.info(str);
					return new AsyncResult<IAction>(cristOut);
				}
			} else {
				if (cristOut == null) {
					String str = "Request for user intent: ";
					str = str.concat("\nCAUI outcome:  ");
					
					str = str.concat(cauiOut.getparameterName()+" = "+cauiOut.getvalue());
					
					str = str.concat("\nCRIST outcome is null, returning CAUI outcome ");
					
					this.logging.info(str);
					return new AsyncResult<IAction>(cauiOut);
				}
			}


			if (cauiOut.getvalue().equalsIgnoreCase(cristOut.getvalue())) {
				action = new Action(serviceID, "", preferenceName, cauiOut.getvalue());
				String str = "Request for user intent: ";
				str = str.concat("\nCAUI and CRIST agree on the following action:  ");
												
				str = str.concat(action.getparameterName()+" = "+action.getvalue());
				
				this.logging.info(str);
				return new AsyncResult<IAction>(action);
			} else {
				
				IOutcome resolveIntentConflicts = this.resolveIntentConflicts(cristOut, cauiOut);
				if (null==resolveIntentConflicts){
					String str = "Request for user intent: ";
					str = str.concat("\nCRIST outcome:  ");
					
					str = str.concat(cristOut.getparameterName()+" = "+cristOut.getvalue());
					
					str = str.concat("\nCAUI outcome: ");
					
					str = str.concat(cauiOut.getparameterName()+" = "+cauiOut.getvalue());
					
					str = str.concat("\nAfter conflict resolution, returning null action: ");
					this.logging.info(str);
				}else{
					String str = "Request for user intent: ";
					str = str.concat("\nCRIST outcome:  ");
					
					str = str.concat(cristOut.getparameterName()+" = "+cristOut.getvalue());
					
					str = str.concat("\nCAUI outcome: ");
					
					str = str.concat(cauiOut.getparameterName()+" = "+cauiOut.getvalue());
					
					str = str.concat("\nAfter conflict resolution, returning action: "+resolveIntentConflicts.getparameterName()+" = "+resolveIntentConflicts.getvalue());
					this.logging.info(str);
				}
				return new AsyncResult<IAction>(resolveIntentConflicts);
			}
		} catch (InterruptedException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		} catch (ExecutionException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		}
		return new AsyncResult<IAction>(null);
	}


	private boolean containsCtxId(CtxAttributeIdentifier ctxId,
			List<CtxAttributeIdentifier> list) {
		for (CtxAttributeIdentifier id : list) {
			if (ctxId.equals(id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onCreation(CtxChangeEvent arg0) {


	}

	@Override
	public void onModification(final CtxChangeEvent event) {
		if (this.logging.isDebugEnabled()){
			this.logging.debug("Received context event: " + event.getId().getType());
		}

		new Thread() {

			public void run() {
				CtxIdentifier ctxIdentifier = event.getId();

				try {
					Thread.sleep(10);
					Future<CtxModelObject> futureAttribute = ctxBroker.retrieve(ctxIdentifier);

					CtxAttribute ctxAttribute = (CtxAttribute) futureAttribute.get();

					if (null != ctxAttribute) {
						
						
						if (ctxAttribute instanceof CtxAttribute) {
							if (logging.isDebugEnabled()){
								logging.debug("Received event and retrieved value " + ctxAttribute.getStringValue() + " for context attribute: " + ctxAttribute.getType());
							}
							String uuid = UUID.randomUUID().toString();
							ActionInformation info = new ActionInformation(uuid, ctxAttribute);
							
							CtxAttributeIdentifier ctxId = (CtxAttributeIdentifier) ctxAttribute.getId();
							IIdentity userId = idm.fromJid(ctxId.getOwnerId());
							List<IOutcome> preferenceOutcomes = processPreferences(userId, ctxAttribute, info);
							List<IOutcome> intentOutcomes = processIntent(userId, ctxAttribute, info);
							actionInformation.put(uuid, info);
							if (preferenceOutcomes.size() == 0 && intentOutcomes.size() == 0) {
								if (logging.isDebugEnabled()){
									logging.debug("Nothing to send to decisionMaker");
								}
								return;
							} else {
								for (int i = 0; i < preferenceOutcomes.size(); i++) {
									if (logging.isDebugEnabled()){
										logging.debug("Pref Outcome " + i + " :" + preferenceOutcomes.get(i));
									}
								}
								for (int i = 0; i < intentOutcomes.size(); i++) {
									if (logging.isDebugEnabled()){
										logging.debug("Intent Outcome " + i + " :" + intentOutcomes.get(i));
									}
								}
							}
							if (logging.isDebugEnabled()){
								logging.debug("Sending " + preferenceOutcomes.size() + " preference outcomes and " + intentOutcomes.size() + " intent outcomes to decisionMaker");
							}


							printInfo(intentOutcomes, preferenceOutcomes, info);
							//remove non implementable and non proactive actions:
							Iterator<IOutcome> intentIterator = intentOutcomes.iterator();
							while (intentIterator.hasNext()){
								IOutcome next = intentIterator.next();
								if ((!next.isImplementable()) || (!next.isProactive())){
									if (logging.isDebugEnabled()) logging.debug("Removing intent action: "+next.getparameterName()+":"+next.getvalue());
									intentIterator.remove();
								}
							}
							Iterator<IOutcome> prefIterator = preferenceOutcomes.iterator();
							while (prefIterator.hasNext()){
								IOutcome next = prefIterator.next();
								if ((!next.isImplementable()) || (!next.isProactive())){
									if (logging.isDebugEnabled()) logging.debug("Removing preference action: "+next.getparameterName()+":"+next.getvalue());
									prefIterator.remove();
								}
							}
							if (logging.isDebugEnabled()) logging.debug("Removed non implementable and non proactive outcomes:");
							printInfo(intentOutcomes, preferenceOutcomes, info);
							if ((intentOutcomes.size()!=0) && (preferenceOutcomes.size()!=0)){
								if (logging.isDebugEnabled()) logging.debug("Sending to decisionMaker");
								decisionMaker.makeDecision(intentOutcomes, preferenceOutcomes, uuid);
							}
						} else {
							if (logging.isDebugEnabled()){
								logging.debug("retrieved attribute but was not instanceof CtxAttribute");
							}
						}
					} else {
						if (logging.isDebugEnabled()){
							logging.debug("Tried to retrieve ctxAttribute from ctxDB onModification() but the attribute is null");
						}
					}
				} catch (CtxException e) {

					if (logging.isDebugEnabled()){ logging.error("Error", e); }
				} catch (InterruptedException e) {

					if (logging.isDebugEnabled()){ logging.error("Error", e); }
				} catch (ExecutionException e) {

					if (logging.isDebugEnabled()){ logging.error("Error", e); }
				} catch (InvalidFormatException e) {

					if (logging.isDebugEnabled()){ logging.error("Error", e); }
				}
			}
		}.start();
	}

	private List<IOutcome> processIntent(IIdentity userId, CtxAttribute ctxAttribute, ActionInformation info) {
		if (logging.isDebugEnabled()){
			this.logging.debug("Processing intent");
		}
		List<IOutcome> results = new ArrayList<IOutcome>();
		CtxAttributeIdentifier ctxId = ctxAttribute.getId();


		try {
			if (this.containsCtxId(ctxId, cauiList)) {
				if (logging.isDebugEnabled()){
					this.logging.debug("caui is registered for events of: " + ctxId.toUriString());
				}
				Future<List<IUserIntentAction>> futureCauiActions = this.cauiPrediction.getPrediction(userId, ctxAttribute);
				if (this.containsCtxId(ctxId, cristList)) {
					if (logging.isDebugEnabled()){
						this.logging.debug("crist is registered for events of: " + ctxId.toUriString());
					}
					Future<List<CRISTUserAction>> futureCristActions = this.cristPrediction.getCRISTPrediction(userId, ctxAttribute);
					return this.compareIntentConflicts(futureCauiActions.get(), futureCristActions.get(), info);
				} else {
					if (logging.isDebugEnabled()){
						this.logging.debug("crist is NOT registered for events of: " + ctxId.toUriString());
					}
					List<IUserIntentAction> cauiActions = futureCauiActions.get();
					info.setSentCAUIIntentOutcomes(cauiActions);
					for (IUserIntentAction cauiAction : cauiActions) {
						CRISTUserAction cristAction = this.cristPrediction.getCurrentUserIntentAction(userId, cauiAction.getServiceID(), cauiAction.getparameterName()).get();
						if (null == cristAction) {
							results.add(cauiAction);
						} else {
							info.getSentCRISTIntentOutcomes().add(cristAction);
							if (cristAction.getvalue().equalsIgnoreCase(cauiAction.getvalue())) {
								results.add(cauiAction);
							} else {
								results.add(this.resolveIntentConflicts(cristAction, cauiAction));
							}
						}
					}
				}
			} else if (this.containsCtxId(ctxId, cristList)) {
				if (logging.isDebugEnabled()){
					this.logging.debug("crist is registered for events of: " + ctxId.toUriString());
				}
				Future<List<CRISTUserAction>> futureCristActions = this.cristPrediction.getCRISTPrediction(userId, ctxAttribute);
				if (this.containsCtxId(ctxId, cauiList)) {
					if (logging.isDebugEnabled()){
						this.logging.debug("caui is registered for events of: " + ctxId.toUriString());
					}
					Future<List<IUserIntentAction>> futureCauiActions = this.cauiPrediction.getPrediction(userId, ctxAttribute);
					return this.compareIntentConflicts(futureCauiActions.get(), futureCristActions.get(), info);
				} else {
					if (logging.isDebugEnabled()){
						this.logging.debug("caui is NOT registered for events of: " + ctxId.toUriString());
					}
					List<CRISTUserAction> cristActions = futureCristActions.get();
					info.setSentCRISTIntentOutcomes(cristActions);
					for (CRISTUserAction cristAction : cristActions) {
						IUserIntentAction cauiAction = this.cauiPrediction.getCurrentIntentAction(userId, cristAction.getServiceID(), cristAction.getparameterName()).get();
						if (null == cauiAction) {
							results.add(cristAction);
						} else {
							info.getSentCAUIIntentOutcomes().add(cauiAction);
							if (cauiAction.getvalue().equalsIgnoreCase(cristAction.getvalue())) {
								results.add(cristAction);
							} else {
								results.add(this.resolveIntentConflicts(cristAction, cauiAction));
							}
						}
					}
				}
			} else {
				if (logging.isDebugEnabled()){
					this.logging.debug("Context attribute: " + ctxAttribute.getType() + " not affecting intent models");
				}
			}
		} catch (InterruptedException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		} catch (ExecutionException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		}

		return results;
	}


	private List<IOutcome> processPreferences(IIdentity userId, CtxAttribute ctxAttribute, ActionInformation info) {
		if (logging.isDebugEnabled()){
			this.logging.debug("Processing preferences after receiving context event");
		}
		List<IOutcome> results = new ArrayList<IOutcome>();
		/*
		 * List<IPreferenceOutcome> pcmResults = new
		 * ArrayList<IPreferenceOutcome>(); List<IDIANNEOutcome> dianneResults =
		 * new ArrayList<IDIANNEOutcome>();
		 */

		try {
			CtxAttributeIdentifier ctxId = (CtxAttributeIdentifier) ctxAttribute.getId();

			if (this.containsCtxId(ctxId, dianneList)) {
				if (logging.isDebugEnabled()){
					this.logging.debug("dianne is registered for events of: " + ctxId.toUriString());
				}
				Future<List<IDIANNEOutcome>> futureDianneOutcomes = this.dianne.getOutcome(userId, (CtxAttribute) ctxAttribute);
				
				if (this.containsCtxId(ctxId, prefMgrList)) {
					if (logging.isDebugEnabled()){
						this.logging.debug("pcm is registered for events of: " + ctxId.toUriString());
					}
					Future<List<IPreferenceOutcome>> futurePrefOutcomes = this.pcm.getOutcome(userId, ctxAttribute, info.getUuid());
					return this.comparePreferenceConflicts(futureDianneOutcomes.get(), futurePrefOutcomes.get(), info);
				} else {
					if (logging.isDebugEnabled()){
						this.logging.debug("pcm is NOT registered for events of: " + ctxId.toUriString());
					}
					List<IDIANNEOutcome> dianneOutcomes;

					dianneOutcomes = futureDianneOutcomes.get();
					info.setSentDianneOutcomes(dianneOutcomes);
					if (logging.isDebugEnabled()){
						this.logging.debug("Received " + dianneOutcomes.size() + " outcomes from dianne after receiving context event: " + ctxAttribute.getType());
					}
					for (IDIANNEOutcome dOut : dianneOutcomes) {
						IPreferenceOutcome pOut = (IPreferenceOutcome) this.pcm.getOutcome(userId, dOut.getServiceType(), dOut.getServiceID(), dOut.getparameterName()).get();
						if (null == pOut) {
							results.add(dOut);
						} else {
							info.getSentPreferenceOutcomes().add(pOut);
							if (pOut.getvalue().equalsIgnoreCase(dOut.getvalue())) {
								results.add(dOut);
							} else {
								results.add(this.resolvePreferenceConflicts(dOut, pOut));
							}
						}
					}
				}
			} else if (this.containsCtxId(ctxId, prefMgrList)) {
				if (logging.isDebugEnabled()){
					this.logging.debug("pcm is registered for events of: " + ctxId.toUriString());
				}
				Future<List<IPreferenceOutcome>> futurePcmOutcomes = this.pcm.getOutcome(userId, ctxAttribute, info.getUuid());
				if (this.containsCtxId(ctxId, dianneList)) {
					if (logging.isDebugEnabled()){
						this.logging.debug("dianne is registered for events of: " + ctxId.toUriString());
					}
					Future<List<IDIANNEOutcome>> futureDianneOutcomes = this.dianne.getOutcome(userId, (CtxAttribute) ctxAttribute);
					return this.comparePreferenceConflicts(futureDianneOutcomes.get(), futurePcmOutcomes.get(), info);
				} else {
					if (logging.isDebugEnabled()){
						this.logging.debug("dianne is NOT registered for events of: " + ctxId.toUriString());
					}
					List<IPreferenceOutcome> pcmOutcomes = futurePcmOutcomes.get();
					info.setSentPreferenceOutcomes(pcmOutcomes);
					if (logging.isDebugEnabled()){
						this.logging.debug("Received " + pcmOutcomes.size() + " outcomes from pcm after receiving context event: " + ctxAttribute.getType());
					}
					for (IPreferenceOutcome pOut : pcmOutcomes) {
						IDIANNEOutcome dOut = this.dianne.getOutcome(userId, pOut.getServiceID(), pOut.getparameterName()).get().get(0);
						
						if (null == dOut) {
							results.add(pOut);
						} else {
							info.getSentDianneOutcomes().add(dOut);
							if (dOut.getvalue().equalsIgnoreCase(pOut.getvalue())) {
								results.add(pOut);
							} else {
								results.add(this.resolvePreferenceConflicts(dOut, pOut));
							}
						}
					}
				}
			} else {
				if (logging.isDebugEnabled()){
					this.logging.debug("Context attribute: " + ctxAttribute.getType() + " not affecting any preferences");
				}
			}
		} catch (InterruptedException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		} catch (ExecutionException e) {

			if (logging.isDebugEnabled()){ logging.error("Error", e); }
		}
		return results;
	}


	private List<IOutcome> comparePreferenceConflicts(List<IDIANNEOutcome> dOuts, List<IPreferenceOutcome> pOuts, ActionInformation info) {
		
		info.setSentDianneOutcomes(dOuts);
		info.setSentPreferenceOutcomes(pOuts);
		
		if (logging.isDebugEnabled()){
			this.logging.debug("Finding conflicts between dianne and pcm");
		}

		List<IOutcome> result = new ArrayList<IOutcome>();
		for (IDIANNEOutcome dOut : dOuts) {
			boolean matches = false;
			IPreferenceOutcome matchedOutcome = null;
			for (IPreferenceOutcome pOut : pOuts) {
				if (logging.isDebugEnabled()) logging.debug("Comparing parameter names: (d):"+dOut.getparameterName()+" - (p): "+pOut.getparameterName());
				if (dOut.getparameterName().equalsIgnoreCase(pOut.getparameterName())) {
					matches = true;
					matchedOutcome = pOut;
					break;
				} else {
					matches = false;
				}
			}
			
			if (!matches) {
				if (logging.isDebugEnabled()) logging.debug("no conflict (d): "+dOut.getparameterName()+": "+dOut.getvalue());

				result.add(dOut);
			} else {
				if (logging.isDebugEnabled()) logging.debug("found a conflict: "+dOut.getparameterName()+" value A: "+dOut.getvalue()+" value B: "+matchedOutcome.getvalue());
				IOutcome resolved = this.resolvePreferenceConflicts(dOut, matchedOutcome);
				if (logging.isDebugEnabled()) logging.debug("Adding resolved: "+resolved.getparameterName()+":"+resolved.getvalue());
				result.add(resolved);
			}
		}


		for (IPreferenceOutcome pOut : pOuts) {
			IOutcome out = matches(pOut, result);


			if (out == null) {
				if (logging.isDebugEnabled()) logging.debug("no conflict (p) : "+pOut.getparameterName()+": "+pOut.getvalue());

				result.add(pOut);
			}
		}

		return result;
	}


	private IOutcome matches(IOutcome outcome, List<IOutcome> outcomes) {
		for (IOutcome out : outcomes) {
			if (outcome.getServiceID().equals(out.getServiceID())) {
				if (outcome.getparameterName().equalsIgnoreCase(out.getparameterName())) {
					return out;
				}
			}
		}

		return null;
	}

	private List<IOutcome> compareIntentConflicts(List<IUserIntentAction> cauiOuts, List<CRISTUserAction> cristOuts, ActionInformation info) {
		info.setSentCAUIIntentOutcomes(cauiOuts);
		info.setSentCRISTIntentOutcomes(cristOuts);
		if (logging.isDebugEnabled()){
			this.logging.debug("Finding conflicts between caui and crist");
		}
		List<IOutcome> result = new ArrayList<IOutcome>();
		for (IUserIntentAction cauiOut : cauiOuts) {
			boolean matches = false;
			CRISTUserAction matchedOutcome = null;
			for (CRISTUserAction cristOut : cristOuts) {
				if (cauiOut.getparameterName().equalsIgnoreCase(cristOut.getparameterName())) {
					matches = true;
					matchedOutcome = cristOut;
				} else {
					matches = false;
				}
			}
			if (!matches) {
				result.add(cauiOut);
			} else {
				result.add(this.resolveIntentConflicts(matchedOutcome, cauiOut));
			}
		}


		for (CRISTUserAction cristOut : cristOuts) {
			IOutcome out = matches(cristOut, result);


			if (out == null) {
				result.add(cristOut);
			}
		}

		return result;
	}

	private IOutcome resolvePreferenceConflicts(IDIANNEOutcome dOut, IPreferenceOutcome pOut) {
		if (logging.isDebugEnabled()){
			this.logging.debug("Resolving preference conflicts between dianne and pcm");
		}
		int dConf = this.dianneConfidenceLevel * dOut.getConfidenceLevel();

		int pConf = this.prefMgrConfidenceLevel * pOut.getConfidenceLevel();

		if (dConf > pConf) {
			if (logging.isDebugEnabled()){
				this.logging.debug("Conflict Resolved. Returning dianne's outcome: " + dOut.toString());
			}
			return dOut;
		} else {
			if (logging.isDebugEnabled()){
				this.logging.debug("Conflict Resolved. Returning pcm's outcome:" + pOut.toString());
			}
			return pOut;
		}


	}

	private IOutcome resolveIntentConflicts(CRISTUserAction cristAction, IUserIntentAction cauiAction) {
		if (logging.isDebugEnabled()){
			this.logging.debug("Resolving intent conflicts between crist and caui");
		}
		int cauiConf = this.cauiConfidenceLevel * cauiAction.getConfidenceLevel();

		int cristConf = this.cristConfidenceLevel * cristAction.getConfidenceLevel();

		if (cauiConf > cristConf) {
			if (logging.isDebugEnabled()){
				this.logging.debug("Conflict Resolved. Returning caui's outcome: " + cauiAction.toString());
			}

			return cauiAction;
		} else {
			if (logging.isDebugEnabled()){
				this.logging.debug("Conflict Resolved. Returning crist's outcome: " + cristAction.toString());
			}

			return cristAction;
		}

	}

	@Override
	public void onRemoval(CtxChangeEvent arg0) {


	}

	@Override
	public void onUpdate(CtxChangeEvent arg0) {


	}

	@Override
	public void handleInternalEvent(final InternalEvent event) {
		
		if (logging.isDebugEnabled()){
			this.logging.debug("Received event:");
			this.logging.debug("Event name " + event.geteventName() +

					"Event info: " + event.geteventInfo().toString() +
					"Event source: " + event.geteventSource() +
					"Event type: " + event.geteventType());
		}
		new Thread() {

			public void run() {
				try {

					if (event.geteventType().equals(EventTypes.UIM_EVENT)) {
						
						UIMEvent uimEvent = (UIMEvent) event.geteventInfo();
						
						String uuid = UUID.randomUUID().toString();
						ActionInformation info = new ActionInformation(uuid, uimEvent.getAction());
						
						Future<List<IUserIntentAction>> futureCauiActions = cauiPrediction.getPrediction(uimEvent.getUserId(), uimEvent.getAction());
						if (logging.isDebugEnabled()){
							logging.debug("Requested caui prediction");
						}
						List<IUserIntentAction> cauiActions = futureCauiActions.get();
						
						info.setSentCAUIIntentOutcomes(cauiActions);
						if (logging.isDebugEnabled()){
							logging.debug("cauiPrediction returned: " + cauiActions.size() + " outcomes");
						}

						Future<List<CRISTUserAction>> futureCristActions = cristPrediction.getCRISTPrediction(uimEvent.getUserId(), uimEvent.getAction());
						if (logging.isDebugEnabled()){
							logging.debug("Requested crist prediction");
						}
						/*
                        List<CRISTUserAction> cristActions = futureCristActions.get();
                        if (logging.isDebugEnabled()){
                        logging.debug("cristPrediction returned: " + cristActions.size() + " outcomes");
                        for (CRISTUserAction action : cristActions) {
                            if (logging.isDebugEnabled()){
                            logging.debug("Crist outcome - parameter: " + action.getparameterName() + " - value: " + action.getvalue());
                        }
						 */

						/**
						 * get intent outcomes
						 */


						Hashtable<IUserIntentAction, CRISTUserAction> overlapping = new Hashtable<IUserIntentAction, CRISTUserAction>();
						List<IOutcome> intentNonOverlapping = new ArrayList<IOutcome>();
						// first change
						intentNonOverlapping.addAll(cauiActions);
						/*

                        for (IUserIntentAction caui : cauiActions) {
                            CRISTUserAction crist = exists(cristActions, caui);
                            if (null == crist) {
                                intentNonOverlapping.add(caui);
                            } else {
                                overlapping.put(caui, crist);
                            }
                        }

                        for (CRISTUserAction crist : cristActions) {
                            IUserIntentAction caui = exists(cauiActions, crist);
                            if (null == caui) {
                                intentNonOverlapping.add(crist);
                            }
                        }

                        Enumeration<IUserIntentAction> cauiEnum = overlapping.keys();

                        while (cauiEnum.hasMoreElements()) {
                            IUserIntentAction caui = cauiEnum.nextElement();
                            CRISTUserAction crist = overlapping.get(caui);
                            intentNonOverlapping.add(resolveIntentConflicts(crist, caui));
                        }
						 */

						/**
						 * get preference outcomes
						 */

						Future<List<IDIANNEOutcome>> futureDianneActions = dianne.getOutcome(uimEvent.getUserId(), uimEvent.getAction());
						if (logging.isDebugEnabled()){
							logging.debug("Requested outcome from dianne");
						}
						List<IDIANNEOutcome> dianneActions = futureDianneActions.get();
						info.setSentDianneOutcomes(dianneActions);
						if (logging.isDebugEnabled()){
							logging.debug("DIANNE returned: " + dianneActions.size() + " outcomes");
						}


						Future<List<IPreferenceOutcome>> futurePreferenceActions = pcm.getOutcome(uimEvent.getUserId(), uimEvent.getAction(), info.getUuid());
						if (logging.isDebugEnabled()){
							logging.debug("Requested preference outcome");
						}
						List<IPreferenceOutcome> prefActions = futurePreferenceActions.get();
						info.setSentPreferenceOutcomes(prefActions);
						if (logging.isDebugEnabled()){
							logging.debug("PCM returned: " + prefActions.size() + " outcomes");
						}


						Hashtable<IPreferenceOutcome, IDIANNEOutcome> prefOverlapping = new Hashtable<IPreferenceOutcome, IDIANNEOutcome>();
						List<IOutcome> prefNonOverlapping = new ArrayList<IOutcome>();

						for (IDIANNEOutcome d : dianneActions) {
							IPreferenceOutcome p = exists(prefActions, d);
							if (null == p) {
								prefNonOverlapping.add(d);
							} else {
								prefOverlapping.put(p, d);
							}
						}

						for (IPreferenceOutcome p : prefActions) {
							IDIANNEOutcome d = exists(dianneActions, p);
							if (null == d) {
								prefNonOverlapping.add(p);
							}
						}

						Enumeration<IPreferenceOutcome> prefEnum = prefOverlapping.keys();

						while (prefEnum.hasMoreElements()) {
							IPreferenceOutcome pref = prefEnum.nextElement();
							IDIANNEOutcome dianne = prefOverlapping.get(pref);
							prefNonOverlapping.add(resolvePreferenceConflicts(dianne, pref));
						}

						if (intentNonOverlapping.size() == 0 & prefNonOverlapping.size() == 0) {
							if (logging.isDebugEnabled()){
								logging.debug("Action Event-> Nothing to send to decisionMaker");
							}
							return;
						} else {
							for (int i = 0; i < prefNonOverlapping.size(); i++) {
								if (logging.isDebugEnabled()){
									logging.debug("Preference Outcome " + i + " :" + prefNonOverlapping.get(i));
								}
							}
							for (int i = 0; i < intentNonOverlapping.size(); i++) {
								if (logging.isDebugEnabled()){
									logging.debug("Intent Outcome " + i + " :" + intentNonOverlapping.get(i));
								}
							}
						}
						// second change
						// set null preferences
						//prefNonOverlapping = new ArrayList<IOutcome>();

						
						actionInformation.put(uuid, info);
						
						printInfo(intentNonOverlapping, prefNonOverlapping, info);
						//remove non implementable and non proactive actions:
						Iterator<IOutcome> intentIterator = intentNonOverlapping.iterator();
						while (intentIterator.hasNext()){
							IOutcome next = intentIterator.next();
							if ((!next.isImplementable()) || (!next.isProactive())){
								intentIterator.remove();
							}
						}
						Iterator<IOutcome> prefIterator = prefNonOverlapping.iterator();
						while (prefIterator.hasNext()){
							IOutcome next = prefIterator.next();
							if ((!next.isImplementable()) || (!next.isProactive())){
								prefIterator.remove();
							}
						}
						if (logging.isDebugEnabled()) logging.debug("Removed non implementable and non proactive outcomes:");
						printInfo(intentNonOverlapping, prefNonOverlapping, info);
						if ((intentNonOverlapping.size()!=0) && (prefNonOverlapping.size()!=0)){
							decisionMaker.makeDecision(intentNonOverlapping, prefNonOverlapping, uuid);
						}
					} else if (event.geteventType().equalsIgnoreCase(EventTypes.UI_EVENT)){
						if (event.geteventInfo()!=null){
							if (event.geteventInfo() instanceof FeedbackEvent){
								FeedbackEvent fEvent = (FeedbackEvent) event.geteventInfo();
								String uuid = fEvent.getUuid();
								if (actionInformation.containsKey(uuid)){
									ActionInformation info = actionInformation.get(uuid);
									//commenting the confidence level updating for the review... throwing NPE at:
									/*
									 * Exception in thread "Thread-1983" java.lang.NullPointerException 
[2014-04-28 09:43:04.990] ERROR Thread-1983                  System.err                                                        	at org.societies.personalisation.management.impl.PersonalisationManager.outcomesMatch(PersonalisationManager.java:1760) 
[2014-04-28 09:43:04.991] ERROR Thread-1983                  System.err                                                        	at org.societies.personalisation.management.impl.PersonalisationManager.existsinCRISTList(PersonalisationManager.java:1748) 
[2014-04-28 09:43:04.991] ERROR Thread-1983                  System.err                                                        	at org.societies.personalisation.management.impl.PersonalisationManager.updateConfidenceLevels(PersonalisationManager.java:1541) 
[2014-04-28 09:43:04.991] ERROR Thread-1983                  System.err                                                        	at org.societies.personalisation.management.impl.PersonalisationManager$2.run(PersonalisationManager.java:1446) 
									 */
									//updateConfidenceLevels(info, fEvent);
									cauiPrediction.receivePredictionFeedback(fEvent);
									pcm.sendFeedback(fEvent, info);

								}

							}
						}
					}

				} catch (InterruptedException e) {

					if (logging.isDebugEnabled()){ logging.error("Error", e); }
				} catch (ExecutionException e) {

					if (logging.isDebugEnabled()){ logging.error("Error", e); }
				}

				if (logging.isDebugEnabled()){
					logging.debug("Thread of handleInternalEvent finished executing");
				}
			}
		}.start();

	}

	
	protected void printInfo(List<IOutcome> intentNonOverlapping,
			List<IOutcome> prefNonOverlapping, ActionInformation info) {
		String str = "";
		if (info.getActionTrigger()==null){
			str = str.concat("\nPersonalisation: Acquired the following actions after receiving context event : "+info.getContextTrigger().getType()+" with value: "+info.getContextTrigger().getStringValue());
		}else{
			str = str.concat("\nPersonalisation: Acquired the following actions after receiving UIM event : "+info.getActionTrigger().getparameterName()+" with value: "+info.getActionTrigger().getvalue());
		}
		
		List<IDIANNEOutcome> sentDianneOutcomes = info.getSentDianneOutcomes();
		str = str.concat("\n"+sentDianneOutcomes.size()+" dianne outcomes: ");
		
		for (int i=0; i<sentDianneOutcomes.size(); i++){
			str = str.concat("\n"+sentDianneOutcomes.get(i).getparameterName()+" = "+sentDianneOutcomes.get(i).getvalue());
		}
		List<IPreferenceOutcome> sentPreferenceOutcomes = info.getSentPreferenceOutcomes();
		str = str.concat("\n"+sentPreferenceOutcomes.size()+" preference outcomes: ");
		for (int i=0; i<sentPreferenceOutcomes.size(); i++){
			str = str.concat("\n"+sentPreferenceOutcomes.get(i).getparameterName()+" = "+sentPreferenceOutcomes.get(i).getvalue());
		}
		
		List<IUserIntentAction> sentCAUIIntentOutcomes = info.getSentCAUIIntentOutcomes();
		str = str.concat("\n"+sentCAUIIntentOutcomes.size()+" caui outcomes: ");
		
		for(int i=0; i<sentCAUIIntentOutcomes.size(); i++){
			str = str.concat("\n"+sentCAUIIntentOutcomes.get(i).getparameterName()+" = "+sentCAUIIntentOutcomes.get(i).getvalue());
		}
		
		
		List<CRISTUserAction> sentCRISTIntentOutcomes = info.getSentCRISTIntentOutcomes();
		str = str.concat("\n"+sentCRISTIntentOutcomes.size()+" crist outcomes: ");
		for(int i=0; i<sentCRISTIntentOutcomes.size(); i++){
			str = str.concat("\n"+sentCRISTIntentOutcomes.get(i).getparameterName()+" = "+sentCRISTIntentOutcomes.get(i).getvalue());
		}
		
		str = str.concat("\nOut of the above, sending the following to the decisionMaker: ");
		
		
		str = str.concat("\n"+prefNonOverlapping.size()+" preference type outcomes: ");
		
		for (int i = 0; i< prefNonOverlapping.size(); i++){
			str = str.concat("\n"+prefNonOverlapping.get(i).getparameterName()+" = "+prefNonOverlapping.get(i).getvalue());

		}
		
		
		str = str.concat("\n"+intentNonOverlapping.size()+" user intent type outcomes: ");
		
		for (int i = 0; i< intentNonOverlapping.size(); i++){
			str = str.concat("\n"+intentNonOverlapping.get(i).getparameterName()+" = "+intentNonOverlapping.get(i).getvalue());

		}
		
		this.logging.info(str);
		if (logging.isDebugEnabled()) logging.debug(str);
		
	}

	protected void updateConfidenceLevels(ActionInformation info, FeedbackEvent fEvent) {
		List<IDIANNEOutcome> sentDianneOutcomes = info.getSentDianneOutcomes();
		List<IPreferenceOutcome> sentPreferenceOutcomes = info.getSentPreferenceOutcomes();		
		List<IUserIntentAction> sentCAUIIntentOutcomes = info.getSentCAUIIntentOutcomes();		
		List<CRISTUserAction> sentCRISTIntentOutcomes = info.getSentCRISTIntentOutcomes();

		IAction implementedAction = fEvent.getAction();
		ImplementationInformation existsinDianneList = this.existsinDianneList(sentDianneOutcomes, implementedAction);
		ImplementationInformation existsinPreferenceList = this.existsinPreferenceList(sentPreferenceOutcomes, implementedAction);
		ImplementationInformation existsinCAUIList = this.existsinCAUIList(sentCAUIIntentOutcomes, implementedAction);
		ImplementationInformation existsinCRISTList = existsinCRISTList(sentCRISTIntentOutcomes, implementedAction);

		switch(fEvent.getErrorType()){
		case CONFLICT_RESOLVED:
			//We don't care in this case because another event follows to suggest how it was implemented or not implemented 
			break;
		case IMPLEMENTED:
			//find the sources that gave us the correct action
			if (existsinDianneList.equals(ImplementationInformation.HAS_CORRECT)){
				if (this.dianneConfidenceLevel<100){
					this.dianneConfidenceLevel++;
				}
			}else if (existsinDianneList.equals(ImplementationInformation.HAS_INCORRECT)){
				if (this.dianneConfidenceLevel>2){
					this.dianneConfidenceLevel--;
				}
			}
						
			if (existsinPreferenceList.equals(ImplementationInformation.HAS_CORRECT)){
				if (this.prefMgrConfidenceLevel<100){
					this.prefMgrConfidenceLevel++;
				}
			}else if (existsinPreferenceList.equals(ImplementationInformation.HAS_INCORRECT)){
				if (this.prefMgrConfidenceLevel>2){
					this.prefMgrConfidenceLevel--;
				}
			}
			
			if (existsinCAUIList.equals(ImplementationInformation.HAS_CORRECT)){
				if (this.cauiConfidenceLevel<100){
					this.cauiConfidenceLevel++;
				}
			}else if (existsinCAUIList.equals(ImplementationInformation.HAS_INCORRECT)){
				if (this.cauiConfidenceLevel>2){
					this.cauiConfidenceLevel--;
				}
			}
			if (existsinCRISTList.equals(ImplementationInformation.HAS_CORRECT)){
				if (this.cristConfidenceLevel<100){
					this.cristConfidenceLevel++;
				}
			}else if (existsinCRISTList.equals(ImplementationInformation.HAS_INCORRECT)){
				if (this.cristConfidenceLevel>2){
					this.cristConfidenceLevel--;
				}
			}
			break;
		case SERVICE_DECISION:
			break;
		case SERVICE_UNREACHABLE:
			break;
		case SYSTEM_ERROR:
			break;
		case USER_ABORTED:
			
			if (existsinDianneList.equals(ImplementationInformation.HAS_INCORRECT)){
				if (this.dianneConfidenceLevel<100){
					this.dianneConfidenceLevel++;
				}
			}else if (existsinDianneList.equals(ImplementationInformation.HAS_CORRECT)){
				if (this.dianneConfidenceLevel>2){
					this.dianneConfidenceLevel--;
				}
			}
						
			if (existsinPreferenceList.equals(ImplementationInformation.HAS_INCORRECT)){
				if (this.prefMgrConfidenceLevel<100){
					this.prefMgrConfidenceLevel++;
				}
			}else if (existsinPreferenceList.equals(ImplementationInformation.HAS_CORRECT)){
				if (this.prefMgrConfidenceLevel>2){
					this.prefMgrConfidenceLevel--;
				}
			}
			
			
			if (existsinCAUIList.equals(ImplementationInformation.HAS_INCORRECT)){
				if (this.cauiConfidenceLevel<100){
					this.cauiConfidenceLevel++;
				}
			}else if (existsinCAUIList.equals(ImplementationInformation.HAS_CORRECT)){
				if (this.cauiConfidenceLevel>2){
					this.cauiConfidenceLevel--;
				}
			}
			
			if (existsinCRISTList.equals(ImplementationInformation.HAS_INCORRECT)){
				if (this.cristConfidenceLevel<100){
					this.cristConfidenceLevel++;
				}
			}else if (existsinCRISTList.equals(ImplementationInformation.HAS_CORRECT)){
				if (this.cristConfidenceLevel>2){
					this.cristConfidenceLevel--;
				}
			}
			break;
		case USER_CHOICE:
			//We don't care in this case either because another event follows to suggest how it was implemented or not implemented 
			break;
		default:
			break;
		
		}
		
		
		this.storeConfidenceLevels();
		
		this.actionInformation.remove(info.getUuid());
	}

	
	private IUserIntentAction exists(List<IUserIntentAction> cauiActions, CRISTUserAction crist) {
		for (IUserIntentAction o : cauiActions) {
			if (this.outcomesMatch(o, crist)) {
				return o;
			}
		}
		return null;
	}

	private CRISTUserAction exists(List<CRISTUserAction> cristActions, IUserIntentAction caui) {

		for (CRISTUserAction o : cristActions) {
			if (this.outcomesMatch(o, caui)) {
				return o;
			}
		}

		return null;
	}

	private IDIANNEOutcome exists(List<IDIANNEOutcome> dianneActions, IPreferenceOutcome prefOutcome) {
		for (IDIANNEOutcome d : dianneActions) {
			if (this.outcomesMatch(d, prefOutcome)) {
				return d;
			}
		}
		return null;
	}


	private IPreferenceOutcome exists(List<IPreferenceOutcome> prefActions, IDIANNEOutcome dianneOutcome) {
		for (IPreferenceOutcome p : prefActions) {
			if (this.outcomesMatch(p, dianneOutcome)) {
				return p;
			}
		}
		return null;
	}

	private boolean outcomesMatch(IOutcome outcome1, IOutcome outcome2) {
		if (outcome1.getServiceID().getServiceInstanceIdentifier().equalsIgnoreCase(outcome2.getServiceID().getServiceInstanceIdentifier())) {
			if (outcome1.getparameterName().equalsIgnoreCase(outcome2.getparameterName())) {
				return true;
			}
		}

		return false;
	}
	
	static enum ImplementationInformation {HAS_CORRECT, HAS_INCORRECT, DOESNT_HAVE};

	private ImplementationInformation existsinDianneList(List<IDIANNEOutcome> outcomes, IAction outcome){
		for (IOutcome o : outcomes){
			if (this.outcomesMatch(o, outcome)){
				if ((o.getvalue()).equalsIgnoreCase(outcome.getvalue())){
					return ImplementationInformation.HAS_CORRECT;
				}else{
					return ImplementationInformation.HAS_INCORRECT;
				}
			}
		}
		
		return ImplementationInformation.DOESNT_HAVE;
	}
	

	private ImplementationInformation existsinPreferenceList(List<IPreferenceOutcome> outcomes, IAction outcome){
		for (IOutcome o : outcomes){
			if (this.outcomesMatch(o, outcome)){
				if ((o.getvalue().equalsIgnoreCase(outcome.getvalue()))){
					return ImplementationInformation.HAS_CORRECT;
				}else{
					return ImplementationInformation.HAS_INCORRECT;
				}
			}
		}
		
		return ImplementationInformation.DOESNT_HAVE;
	}
	
	private ImplementationInformation existsinCAUIList(List<IUserIntentAction> outcomes, IAction outcome){
		for (IOutcome o : outcomes){
			if (this.outcomesMatch(o, outcome)){
				if (o.getvalue().equalsIgnoreCase(outcome.getvalue())){
					return ImplementationInformation.HAS_CORRECT;
				}else{
					return ImplementationInformation.HAS_INCORRECT;
				}
			}
		}
		
		return ImplementationInformation.DOESNT_HAVE;
	}
	
	private ImplementationInformation existsinCRISTList(List<CRISTUserAction> outcomes, IAction outcome){
		for (IOutcome o : outcomes){
			if (this.outcomesMatch(o, outcome)){
				if (o.getvalue().equalsIgnoreCase(outcome.getvalue())){
					return ImplementationInformation.HAS_CORRECT;
				}else{
					return ImplementationInformation.HAS_INCORRECT;
				}
			}
		}
		
		return ImplementationInformation.DOESNT_HAVE;
	}
	private boolean outcomesMatch(IOutcome outcome1, IAction action) {
		if (outcome1.getServiceID().getServiceInstanceIdentifier().equalsIgnoreCase(action.getServiceID().getServiceInstanceIdentifier())) {
			if (outcome1.getparameterName().equalsIgnoreCase(action.getparameterName())) {
				return true;
			}
		}

		return false;
	}
	
	
	@Override
	public void handleExternalEvent(CSSEvent arg0) {


	}

}
