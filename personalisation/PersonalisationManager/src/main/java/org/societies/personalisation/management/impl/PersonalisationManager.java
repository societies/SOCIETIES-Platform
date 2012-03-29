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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.IFeedbackEvent;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.decisionmaking.IDecisionMaker;
import org.societies.api.personalisation.mgmt.IPersonalisationCallback;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;

public class PersonalisationManager implements IPersonalisationManager,
		IInternalPersonalisationManager, CtxChangeEventListener {

	// services
	private ICtxBroker ctxBroker;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private IUserPreferenceConditionMonitor pcm;
	private IDIANNE dianne;
	private ICAUIPrediction cauiPrediction;
	private ICRISTUserIntentPrediction cristPrediction;
	private IIdentityManager idm;
	private IDecisionMaker decisionMaker;

	// data structures
	ArrayList<CtxAttributeIdentifier> dianneList;
	ArrayList<CtxAttributeIdentifier> prefMgrList;
	ArrayList<CtxAttributeIdentifier> cauiList;
	ArrayList<CtxAttributeIdentifier> cristList;

	/*
	 * test test test!
	 */

	public PersonalisationManager() {
		System.out.println(this.getClass().getName()
				+ "HELLO! I'm a brand new service and my interface is: "
				+ this.getClass().getName());

	}

	/*
	 * INITIALISE SERVICES
	 */
	public void initialisePersonalisationManager(ICtxBroker ctxBroker,
			IUserPreferenceConditionMonitor pcm) {
		this.ctxBroker = ctxBroker;
		this.pcm = pcm;
		/*
		 * this.dianne = dianne; this.cauiPrediction = cauiPrediction;
		 * this.cristPrediction = cristPrediction;
		 */

		this.dianneList = new ArrayList<CtxAttributeIdentifier>();
		this.prefMgrList = new ArrayList<CtxAttributeIdentifier>();
		this.cauiList = new ArrayList<CtxAttributeIdentifier>();
		this.cristList = new ArrayList<CtxAttributeIdentifier>();
		if (this.ctxBroker == null) {
			System.out.println(this.getClass().getName() + "CtxBroker is null");
		} else {
			System.out.println(this.getClass().getName()
					+ "CtxBroker is NOT null");
		}

		if (this.pcm == null) {
			System.out.println(this.getClass().getName() + "PCM is null");
		} else {
			System.out.println(this.getClass().getName() + "PCM is NOT null");
		}

		System.out
				.println("Full init. Yo!! I'm a brand new service and my interface is: "
						+ this.getClass().getName());

	}

	public void initialisePersonalisationManager(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;

		/*
		 * this.dianne = dianne; this.cauiPrediction = cauiPrediction;
		 * this.cristPrediction = cristPrediction;
		 */

		this.dianneList = new ArrayList<CtxAttributeIdentifier>();
		this.prefMgrList = new ArrayList<CtxAttributeIdentifier>();
		this.cauiList = new ArrayList<CtxAttributeIdentifier>();
		this.cristList = new ArrayList<CtxAttributeIdentifier>();
		if (this.ctxBroker == null) {
			System.out.println(this.getClass().getName() + "CtxBroker is null");
		} else {
			System.out.println(this.getClass().getName()
					+ "CtxBroker is NOT null");
		}

		/*
		 * if (this.pcm==null){
		 * System.out.println(this.getClass().getName()+"PCM is null"); }else{
		 * System.out.println(this.getClass().getName()+"PCM is NOT null"); }
		 */

		System.out
				.println("Ctx init. Yo!! I'm a brand new service and my interface is: "
						+ this.getClass().getName());

	}

	public void initialisePersonalisationManager() {

		this.dianneList = new ArrayList<CtxAttributeIdentifier>();
		this.prefMgrList = new ArrayList<CtxAttributeIdentifier>();
		this.cauiList = new ArrayList<CtxAttributeIdentifier>();
		this.cristList = new ArrayList<CtxAttributeIdentifier>();
		System.out
				.println("Empty init. Yo!! I'm a brand new service and my interface is: "
						+ this.getClass().getName());

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
		System.out.println(this.getClass().getName() + "Return PCM");
		return pcm;
	}

	public void setPcm(IUserPreferenceConditionMonitor pcm) {
		System.out.println(this.getClass().getName() + "GOT PCM");
		this.pcm = pcm;
	}

	public IDIANNE getDianne() {
		System.out.println(this.getClass().getName() + "Return DIANNE");
		return dianne;
	}

	public void setDianne(IDIANNE dianne) {
		System.out.println(this.getClass().getName() + "GOT DIANNE");
		this.dianne = dianne;
	}

	public ICtxBroker getCtxBroker() {
		System.out.println(this.getClass().getName() + "Return CtxBroker");
		return this.ctxBroker;
	}

	public void setCtxBroker(ICtxBroker broker) {
		System.out.println(this.getClass().getName() + "GOT CtxBroker");
		this.ctxBroker = broker;
	}

	/*
	 * IMPLEMENT INTERFACE METHODS
	 */

	public IIdentityManager getIdm() {
		return idm;
	}

	public void setIdm(IIdentityManager idm) {
		this.idm = idm;
	}

	public IDecisionMaker getDecisionMaker() {
		return decisionMaker;
	}

	public void setDecisionMaker(IDecisionMaker decisionMaker) {
		this.decisionMaker = decisionMaker;
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
		// TODO Auto-generated method stub

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
			this.ctxBroker.registerForChanges(this, ctxAttributeId);

		} catch (CtxException e) {
			logging.error(e.getMessage());
			e.printStackTrace();
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
	public void getPreference(IIdentity ownerID, String serviceType,
			ServiceResourceIdentifier serviceID, String preferenceName,
			IPersonalisationCallback callback) {
		// TODO Auto-generated method stub

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
	public void getIntentAction(IIdentity requestor, IIdentity ownerID,
			ServiceResourceIdentifier serviceID, String preferenceName,
			IPersonalisationCallback callback) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.personalisation.mgmt.IPersonalisationManager#getPreference
	 * (org.societies.api.comm.xmpp.datatypes.IIdentity,
	 * org.societies.api.comm.xmpp.datatypes.IIdentity, java.lang.String,
	 * org.societies.api.servicelifecycle.model.ServiceResourceIdentifier,
	 * java.lang.String,
	 * org.societies.api.personalisation.mgmt.IPersonalisationCallback)
	 */
	@Override
	public void getPreference(IIdentity requestor, IIdentity ownerID,
			String serviceType, ServiceResourceIdentifier serviceID,
			String preferenceName, IPersonalisationCallback callback) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.common.api.management.
	 * IInternalPersonalisationManager
	 * #getIntentAction(org.societies.api.comm.xmpp.datatypes.IIdentity,
	 * org.societies.api.servicelifecycle.model.ServiceResourceIdentifier,
	 * java.lang.String,
	 * org.societies.api.personalisation.mgmt.IPersonalisationCallback)
	 */
	@Override
	public void getIntentAction(IIdentity ownerID,
			ServiceResourceIdentifier serviceID, String preferenceName,
			IPersonalisationCallback callback) {

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
		// TODO Auto-generated method stub

	}

	@Override
	public void onModification(CtxChangeEvent event) {
		CtxIdentifier ctxIdentifier = event.getId();
	
		try {
			Future<CtxModelObject> futureAttribute = this.ctxBroker.retrieve(ctxIdentifier);

			CtxAttribute ctxAttribute = (CtxAttribute) futureAttribute.get();

			
			if (ctxAttribute instanceof CtxAttribute) {
				CtxAttributeIdentifier ctxId = (CtxAttributeIdentifier) ctxAttribute.getId();
				IIdentity userId = this.idm.fromJid(ctxId.getOperatorId());
				List<IOutcome> preferenceOutcomes = this.processPreferences(userId, ctxAttribute);
				List<IOutcome> intentOutcomes = this.processIntent(userId, ctxAttribute);
				this.decisionMaker.makeDecision(intentOutcomes, preferenceOutcomes);
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<IOutcome> processIntent(IIdentity userId,	CtxAttribute ctxAttribute) {
		List<IOutcome> results = new ArrayList<IOutcome>();
		CtxAttributeIdentifier ctxId = ctxAttribute.getId();
		

		try {
			if (this.containsCtxId(ctxId, cauiList)) {
				Future<List<IUserIntentAction>> futureCauiActions = this.cauiPrediction.getPrediction(userId, ctxAttribute);
				if (this.containsCtxId(ctxId, cristList)) {
					Future<List<CRISTUserAction>> futureCristActions = this.cristPrediction.getCRISTPrediction(userId, ctxAttribute);
					return this.compareIntentConflicts(futureCauiActions.get(), futureCristActions.get());
				}else{
					List<IUserIntentAction> cauiActions = futureCauiActions.get();
					
					for (IUserIntentAction cauiAction : cauiActions){
						CRISTUserAction cristAction = this.cristPrediction.getCurrentUserIntentAction(userId, cauiAction.getServiceID()).get();
						if (cristAction.getvalue().equalsIgnoreCase(cauiAction.getvalue())){
							results.add(cauiAction);
						}else{
							results.add(this.resolveIntentConflicts(cristAction, cauiAction));
						}
					}
				}
			}else if (this.containsCtxId(ctxId, cristList)){
				Future<List<CRISTUserAction>> futureCristActions = this.cristPrediction.getCRISTPrediction(userId, ctxAttribute);
				if (this.containsCtxId(ctxId, cauiList)){
					Future<List<IUserIntentAction>> futureCauiActions = this.cauiPrediction.getPrediction(userId, ctxAttribute);
					return this.compareIntentConflicts(futureCauiActions.get(), futureCristActions.get());
				}else{
					List<CRISTUserAction> cristActions = futureCristActions.get();
					
					for (CRISTUserAction cristAction : cristActions){
						IUserIntentAction cauiAction = this.cauiPrediction.getCurrentIntentAction(userId, cristAction.getServiceID(), cristAction.getparameterName()).get();
						if (cauiAction.getvalue().equalsIgnoreCase(cristAction.getvalue())){
							results.add(cristAction);
						}else{
							results.add(this.resolveIntentConflicts(cristAction, cauiAction));
						}
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;
	}





	private List<IOutcome> processPreferences(IIdentity userId, CtxAttribute ctxAttribute) {
		List<IOutcome> results = new ArrayList<IOutcome>();
		/*
		 * List<IPreferenceOutcome> pcmResults = new
		 * ArrayList<IPreferenceOutcome>(); List<IDIANNEOutcome> dianneResults =
		 * new ArrayList<IDIANNEOutcome>();
		 */

		try {
			CtxAttributeIdentifier ctxId = (CtxAttributeIdentifier) ctxAttribute.getId();
			
			if (this.containsCtxId(ctxId, dianneList)) {
				Future<List<IDIANNEOutcome>> futureDianneOutcomes = this.dianne.getOutcome(userId, (CtxAttribute) ctxAttribute);
				if (this.containsCtxId(ctxId, prefMgrList)) {
					Future<List<IPreferenceOutcome>> futurePrefOutcomes = this.pcm.getOutcome(userId, ctxAttribute);
					return this.comparePreferenceConflicts(futureDianneOutcomes.get(),futurePrefOutcomes.get());
				} else {
					List<IDIANNEOutcome> dianneOutcomes;

					dianneOutcomes = futureDianneOutcomes.get();
					
					for (IDIANNEOutcome dOut : dianneOutcomes) {
						IPreferenceOutcome pOut = this.pcm.getOutcome(userId, dOut.getServiceID(), dOut.getparameterName()).get();
						if (pOut.getvalue().equalsIgnoreCase(dOut.getvalue())) {
							results.add(dOut);
						} else {
							results.add(this.resolvePreferenceConflicts(dOut, pOut));
						}
					}
				}
			} else if (this.containsCtxId(ctxId, prefMgrList)) {
				Future<List<IPreferenceOutcome>> futurePcmOutcomes = this.pcm.getOutcome(userId, ctxAttribute);
				if (this.containsCtxId(ctxId, dianneList)){
					Future<List<IDIANNEOutcome>> futureDianneOutcomes = this.dianne.getOutcome(userId, (CtxAttribute) ctxAttribute);
					return this.comparePreferenceConflicts(futureDianneOutcomes.get(), futurePcmOutcomes.get());
				}else{
					List<IPreferenceOutcome> pcmOutcomes = futurePcmOutcomes.get();
					for (IPreferenceOutcome pOut:pcmOutcomes){
						IDIANNEOutcome dOut = this.dianne.getOutcome(userId, pOut.getServiceID(), pOut.getparameterName()).get().get(0);
						if (dOut.getvalue().equalsIgnoreCase(pOut.getvalue())){
							results.add(pOut);
						}else{
							results.add(this.resolvePreferenceConflicts(dOut, pOut));
						}
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	
	private List<IOutcome> comparePreferenceConflicts(List<IDIANNEOutcome> dOuts, List<IPreferenceOutcome> pOuts){
		
		
		return new ArrayList<IOutcome>();
	}

	private List<IOutcome> compareIntentConflicts(List<IUserIntentAction> caui, List<CRISTUserAction> crist) {
		// TODO Auto-generated method stub
		return null;
	}
	private IOutcome resolvePreferenceConflicts(IDIANNEOutcome dOut, IPreferenceOutcome pOut){
		return null;
	}

	private IOutcome resolveIntentConflicts(CRISTUserAction cristAction, IUserIntentAction cauiAction) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onRemoval(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

}
