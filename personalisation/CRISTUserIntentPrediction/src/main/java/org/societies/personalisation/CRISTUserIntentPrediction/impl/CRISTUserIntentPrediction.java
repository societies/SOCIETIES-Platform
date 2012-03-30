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

package org.societies.personalisation.CRISTUserIntentPrediction.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
// import javax.annotation.PostConstruct;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxModelObject;
// import org.societies.api.personalisation.model.IAction;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager.ICRISTUserIntentTaskManager;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRIST.api.model.ICRISTUserAction;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;

// @Component
public class CRISTUserIntentPrediction implements ICRISTUserIntentPrediction {

	private IInternalPersonalisationManager persoMgr;
	private ICRISTUserIntentTaskManager cristTaskManager;
	private ICtxBroker ctxBroker;
	
	private IIdentity myId;
	private CtxAttributeIdentifier myCtxId;
	private ServiceResourceIdentifier serviceId;
	private ICRISTUserAction cristOutcome = null; 	

	public CRISTUserIntentPrediction() {
		System.out.println("Hello! I'm the CRIST User Intent Prediction!");
	}
	
	public IInternalPersonalisationManager getPersoMgr() {
		return persoMgr;
	}

	public void setPersoMgr(IInternalPersonalisationManager persoMgr) {
		this.persoMgr = persoMgr;
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	
	public ICRISTUserIntentTaskManager getCristTaskManager(){
		return cristTaskManager;
	}
	
	public void setCristTaskManager(ICRISTUserIntentTaskManager cristTaskManager){
		this.cristTaskManager = cristTaskManager;
	}
	
	public void initialiseCRISTPrediction() {

		if (this.getPersoMgr() == null) {
			System.out
					.println(this.getClass().getName() + "PreManager is null");
		} else {
			System.out.println(this.getClass().getName()
					+ "PreManager is NOT null");
		}

		System.out.println("Yo!! I'm a brand new service and my interface is: "
				+ this.getClass().getName());
		try{
			// TODO
			// this.preManager.registerForContextUpdate(myId, myCtxId);
			System.out.println("CRIST Predictor registered the Context Update Event");
		}catch(Exception e){
			System.err.println("Exception when trying to register the Context Update Event");
			System.err.println(e.toString());
		}
	}
	
	public IInternalPersonalisationManager getPreManager() {
		System.out.println(this.getClass().getName()+" Return InternalPreManager");
		return (IInternalPersonalisationManager) getPersoMgr();
	}

	public void setPreManager(IInternalPersonalisationManager internalPreManager) {
		System.out.println(this.getClass().getName()+" GOT InternalPreManager");
		this.setPersoMgr(internalPreManager);
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#enableCRISTPrediction(boolean)
	 */
	@Override
	public void enableCRISTPrediction(boolean bool) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#sendFeedback(org.societies.api.internal.personalisation.model.FeedbackEvent)
	 */
	@Override
	public void sendFeedback(FeedbackEvent feedbackEvent) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#updateReceived(org.societies.api.context.model.CtxModelObject)
	 */
	@Override
	public void updateReceived(CtxModelObject ctxModelObj) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#getCRISTPrediction(org.societies.api.identity.IIdentity, org.societies.api.context.model.CtxAttribute)
	 */
	@Override
	public Future<List<CRISTUserAction>> getCRISTPrediction(IIdentity entityID,
			CtxAttribute ctxAttribute) {
		// TODO Auto-generated method stub
		List<CRISTUserAction> results = new ArrayList<CRISTUserAction>();
		results = this.cristTaskManager.predictUserIntent(entityID, ctxAttribute);
		
		return new AsyncResult<List<CRISTUserAction>>(results);
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#getCRISTPrediction(org.societies.api.identity.IIdentity, org.societies.api.personalisation.model.IAction)
	 */
	@Override
	public Future<List<CRISTUserAction>> getCRISTPrediction(IIdentity entityID,
			IAction action) {
		// TODO Auto-generated method stub
		List<CRISTUserAction> results = new ArrayList<CRISTUserAction>();
		results = this.cristTaskManager.predictUserIntent(entityID, (CRISTUserAction) action);
		
		return new AsyncResult<List<CRISTUserAction>>(results);
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#getCurrentUserIntentAction(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public Future<CRISTUserAction> getCurrentUserIntentAction(
			IIdentity ownerID, ServiceResourceIdentifier serviceID) {
		// TODO Auto-generated method stub
		
		return null;
	}
}
