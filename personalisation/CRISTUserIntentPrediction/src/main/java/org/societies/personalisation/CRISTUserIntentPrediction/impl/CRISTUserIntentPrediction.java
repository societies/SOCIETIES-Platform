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
// import javax.annotation.PostConstruct;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxModelObject;
// import org.societies.api.personalisation.model.IAction;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.mock.EntityIdentifier;
import org.societies.api.mock.ServiceResourceIdentifier;
import org.societies.api.personalisation.model.IAction;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRIST.api.model.ICRISTUserAction;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.management.IPersonalisationInternalCallback;

// @Component
public class CRISTUserIntentPrediction implements ICRISTUserIntentPrediction {

	private IPersonalisationInternalCallback preManager;
	private EntityIdentifier myId;
	private CtxAttributeIdentifier myCtxId;
	private ServiceResourceIdentifier serviceId;
	private ICRISTUserAction cristOutcome = null; 

	public CRISTUserIntentPrediction() {
		System.out.println("Hello! I'm the CRIST User Intent Prediction!");
	}
	
	// @Autowired
	public CRISTUserIntentPrediction(IInternalPersonalisationManager internalPreManager) {
		this.preManager = (IPersonalisationInternalCallback) internalPreManager;
	}
	
	public void initialiseCRISTPrediction() {

		if (this.preManager == null) {
			System.out
					.println(this.getClass().getName() + "PreManager is null");
		} else {
			System.out.println(this.getClass().getName()
					+ "PreManager is NOT null");
		}

		System.out.println("Yo!! I'm a brand new service and my interface is: "
				+ this.getClass().getName());
		try{
			((IInternalPersonalisationManager) this.preManager).registerForContextUpdate(myId, this.getClass().getName(), myCtxId);
			System.out.println("CRIST Predictor registered the Context Update Event");
		}catch(Exception e){
			System.err.println("Exception when trying to register the Context Update Event");
			System.err.println(e.toString());
		}
	}
	
	public IInternalPersonalisationManager getPreManager() {
		System.out.println(this.getClass().getName()+" Return InternalPreManager");
		return (IInternalPersonalisationManager) preManager;
	}

	public void setPreManager(IInternalPersonalisationManager internalPreManager) {
		System.out.println(this.getClass().getName()+" GOT InternalPreManager");
		this.preManager = (IPersonalisationInternalCallback) internalPreManager;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#enableCRISTPrediction(boolean)
	 */
	@Override
	public void enableCRISTPrediction(boolean bool) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#getCRISTPrediction(org.societies.api.mock.EntityIdentifier, org.societies.api.context.model.CtxAttribute, org.societies.personalisation.common.api.management.IPersonalisationInternalCallback)
	 */
	@Override
	public ArrayList<CRISTUserAction> getCRISTPrediction(
			EntityIdentifier entityID, CtxAttribute ctxAttribute,
			IPersonalisationInternalCallback callback) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#getCRISTPrediction(org.societies.api.mock.EntityIdentifier, org.societies.api.personalisation.model.IAction, org.societies.personalisation.common.api.management.IPersonalisationInternalCallback)
	 */
	@Override
	public ArrayList<CRISTUserAction> getCRISTPrediction(
			EntityIdentifier entityID, IAction action,
			IPersonalisationInternalCallback callback) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction#getCurrentUserIntentAction(org.societies.api.mock.EntityIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ServiceResourceIdentifier)
	 */
	@Override
	public CRISTUserAction getCurrentUserIntentAction(
			EntityIdentifier requestor,
			EntityIdentifier ownerID,
			org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ServiceResourceIdentifier serviceID) {
		// TODO Auto-generated method stub
		return null;
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
}
