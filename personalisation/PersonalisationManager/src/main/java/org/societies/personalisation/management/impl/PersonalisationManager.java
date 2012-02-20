/**
 * Copyright (c) 2011, SOCIETIES Consortium
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.IFeedbackEvent;
import org.societies.api.personalisation.mgmt.IPersonalisationCallback;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.societies.personalisation.preference.api.UserPreferenceManagement.IUserPreferenceManagement;


public class PersonalisationManager implements IPersonalisationManager, IInternalPersonalisationManager{

	
	//services
	private ICtxBroker ctxBroker;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private IUserPreferenceManagement prefMgr;
	private IDIANNE dianneNetwork;
	private ICAUIPrediction cauiPrediction;
	private ICRISTUserIntentPrediction cristPrediction;
	
	//data structures
	ArrayList<CtxAttributeIdentifier> dianneList;
	ArrayList<CtxAttributeIdentifier> prefMgrList;
	ArrayList<CtxAttributeIdentifier> cauiList;
	ArrayList<CtxAttributeIdentifier> cristList;
	
	/* 
	 * test test test!
	 */
	
	
	public PersonalisationManager(){
		System.out.println(this.getClass().getName()+"HELLO! I'm a brand new service and my interface is: "+this.getClass().getName());

	}
	
	public PersonalisationManager(ICtxBroker broker, IUserPreferenceManagement upm, IDIANNE dianneNetwork, ICAUIPrediction cauiPrediction, ICRISTUserIntentPrediction cristPrediction){
		this.ctxBroker = broker;
		this.prefMgr = upm;
		this.dianneNetwork = dianneNetwork;
		this.cauiPrediction = cauiPrediction;
		this.cristPrediction = cristPrediction;
		
		this.dianneList = new ArrayList<CtxAttributeIdentifier>();
		this.prefMgrList = new ArrayList<CtxAttributeIdentifier>();
		this.cauiList = new ArrayList<CtxAttributeIdentifier>();
		this.cristList = new ArrayList<CtxAttributeIdentifier>();
		
		
	} 

	/*
	 * INITIALISE SERVICES
	 */
	public void initialisePersonalisationManager(){
		if (this.ctxBroker==null){
			System.out.println(this.getClass().getName()+"CtxBroker is null");
		}else{
			System.out.println(this.getClass().getName()+"CtxBroker is NOT null");
		}
		
		
		if (this.prefMgr==null){
			System.out.println(this.getClass().getName()+"UPM is null");
		}else{
			System.out.println(this.getClass().getName()+"UPM is NOT null");
		}
		
		
		System.out.println("Yo!! I'm a brand new service and my interface is: "+this.getClass().getName());
		
	}
	
	public IUserPreferenceManagement getPrefMgr() {
		System.out.println(this.getClass().getName()+"Return UPM");
		return prefMgr;
	}


	public void setPrefMgr(IUserPreferenceManagement upm) {
		System.out.println(this.getClass().getName()+"GOT UPM");
		this.prefMgr = upm;
	}

	public IDIANNE getDianneNetwork() {
		return dianneNetwork;
	}

	public void setDianneNetwork(IDIANNE dianneNetwork) {
		this.dianneNetwork = dianneNetwork;
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

	public ICtxBroker getCtxBroker(){
		System.out.println(this.getClass().getName()+"Return CtxBroker");
		return this.ctxBroker;
	}

	public void setCtxBroker(ICtxBroker broker){
		this.ctxBroker = broker;
	}
	
	
	
	
	/*
	 * IMPLEMENT INTERFACE METHODS
	 */

	
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.common.api.management.IInternalPersonalisationManager#returnFeedback(org.societies.api.internal.personalisation.model.IFeedbackEvent)
	 */
	@Override
	public void returnFeedback(IFeedbackEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.common.api.management.IInternalPersonalisationManager#registerForContextUpdate(org.societies.api.comm.xmpp.datatypes.Identity, org.societies.personalisation.common.api.model.PersonalisationTypes, org.societies.api.context.model.CtxAttributeIdentifier)
	 */
	@Override
	public void registerForContextUpdate(Identity id, PersonalisationTypes type, CtxAttributeIdentifier ctxAttributeId) {
		try {
			this.ctxBroker.registerForUpdates(ctxAttributeId);
			
		} catch (CtxException e) {
			logging.error(e.getMessage());
			e.printStackTrace();
		}
		
		switch (type){
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
		default: return;
		}
		
	}
	
	private void addtoList(CtxAttributeIdentifier ctxId, List<CtxAttributeIdentifier> list){
		for (CtxAttributeIdentifier Id :list){
			if (ctxId.equals(Id)){
				return;
			}
		}
		
		list.add(ctxId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.common.api.management.IInternalPersonalisationManager#getPreference(org.societies.api.comm.xmpp.datatypes.Identity, java.lang.String, org.societies.api.servicelifecycle.model.IServiceResourceIdentifier, java.lang.String, org.societies.api.personalisation.mgmt.IPersonalisationCallback)
	 */
	@Override
	public void getPreference(Identity ownerID, String serviceType, IServiceResourceIdentifier serviceID, String preferenceName, IPersonalisationCallback callback) {
		// TODO Auto-generated method stub
		
	}



	/*
	 * (non-Javadoc)
	 * @see org.societies.api.personalisation.mgmt.IPersonalisationManager#getIntentAction(org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.servicelifecycle.model.IServiceResourceIdentifier, java.lang.String, org.societies.api.personalisation.mgmt.IPersonalisationCallback)
	 */
	@Override
	public void getIntentAction(Identity requestor, Identity ownerID, IServiceResourceIdentifier serviceID, String preferenceName, IPersonalisationCallback callback){
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.personalisation.mgmt.IPersonalisationManager#getPreference(org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.comm.xmpp.datatypes.Identity, java.lang.String, org.societies.api.servicelifecycle.model.IServiceResourceIdentifier, java.lang.String, org.societies.api.personalisation.mgmt.IPersonalisationCallback)
	 */
	@Override
	public void getPreference(Identity requestor, Identity ownerID, String serviceType, IServiceResourceIdentifier serviceID, String preferenceName, IPersonalisationCallback callback){
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.common.api.management.IInternalPersonalisationManager#getIntentAction(org.societies.api.comm.xmpp.datatypes.Identity, org.societies.api.servicelifecycle.model.IServiceResourceIdentifier, java.lang.String, org.societies.api.personalisation.mgmt.IPersonalisationCallback)
	 */
	@Override
	public void getIntentAction(Identity ownerID, IServiceResourceIdentifier serviceID, String preferenceName, IPersonalisationCallback callback){
		
	}
	
	
	public void updateReceived(CtxModelObject ctxAttribute){
		if (ctxAttribute instanceof CtxAttribute){
			InternalPersonalisationCallback callback = new InternalPersonalisationCallback(this);
			CtxAttributeIdentifier ctxId = (CtxAttributeIdentifier) ctxAttribute.getId();
			
			if (this.containsCtxId(ctxId, dianneList)){
				this.dianneNetwork.getOutcome(ctxId.getOperatorId(), (CtxAttribute) ctxAttribute, callback);
				if (this.containsCtxId(ctxId, prefMgrList)){	
					this.prefMgr.getPreferenceOutcome(ctxId.getOperatorId(), ctxAttribute, callback);
					callback.setAskDianne(false);
					callback.setAskPreference(false);
				}else{
					callback.setAskPreference(true);
				}
				
			}else if(this.containsCtxId(ctxId, prefMgrList)){
				this.prefMgr.getPreferenceOutcome(ctxId.getOperatorId(), ctxAttribute, callback);
				if (this.containsCtxId(ctxId, dianneList)){
					this.dianneNetwork.getOutcome(ctxId.getOperatorId(), (CtxAttribute) ctxAttribute, callback);
					callback.setAskDianne(false);
					callback.setAskPreference(false);
				}else{
					callback.setAskDianne(true);
				}
			}else if (this.containsCtxId(ctxId, cauiList)){
				this.cauiPrediction.getPrediction(ctxId.getOperatorId(), ctxAttribute, callback);
				if (this.containsCtxId(ctxId, cristList)){
					this.cristPrediction.getCRISTPrediction(ctxId.getOperatorId(), ctxAttribute, callback);
					callback.setAskCAUI(false);
					callback.setAskCRIST(false);
				}else{
					callback.setAskCRIST(true);
				}
			}else if (this.containsCtxId(ctxId, cristList)){
				this.cristPrediction.getCRISTPrediction(ctxId.getOperatorId(), ctxAttribute, callback);
				if (this.containsCtxId(ctxId, cauiList)){
					this.cauiPrediction.getPrediction(ctxId.getOperatorId(), ctxAttribute, callback);
				}
			}
			
		}
	}
	
	private boolean containsCtxId(CtxAttributeIdentifier ctxId, List<CtxAttributeIdentifier> list){
		for (CtxAttributeIdentifier id:list){
			if (ctxId.equals(id)){
				return true;
			}
		}
		return false;
	}

}
