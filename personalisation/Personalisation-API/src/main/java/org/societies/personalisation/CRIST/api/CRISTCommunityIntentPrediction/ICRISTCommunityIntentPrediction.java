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
package org.societies.personalisation.CRIST.api.CRISTCommunityIntentPrediction;

import java.util.ArrayList;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.model.CRISTCommunityAction;


/**
 * 
 * @author Zhu WANG
 * @version 1.0
 */
public interface ICRISTCommunityIntentPrediction {

	/**
	 * This method will enable the CRIST prediction
	 * 
	 * @param bool		- true to enable and false to disable
	 */
	public void enableCRISTPrediction(boolean bool);
	
	/**
	 * This method will generate CRIST prediction based on the given information "ctxAttribute"
	 * 
	 * @param ctxAttribute		- a set of context
	 */
	public ArrayList<CRISTCommunityAction> getCRISTPrediction(IIdentity entityID, CtxAttribute ctxAttribute);
	
	public ArrayList<CRISTCommunityAction> getCRISTPrediction(IIdentity entityID, IAction action);
	
	/**
	 * This method will return the user's current intent
	 * 
	 *  @param requestor	- the ID of the requestor of the Intent
	 *  @param ownerID		- the ID of the owner of the Intent
	 *  @param serviceID	- the ID of the service related to the actions upon 
	 *  which prediction should perform
	 */
	public CRISTCommunityAction getCurrentUserIntentAction(IIdentity requestor, IIdentity ownerID, ServiceResourceIdentifier serviceID);
	
	/**
	 * This method will send user's feedback about the predicted user intent
	 * 
	 * @param feedbackEvent		- user's feedback
	 */
	public void sendFeedback(FeedbackEvent feedbackEvent);
	
	/**
	 * This method will update the newly generated CRIST User Intent Model
	 * 
	 * @param ctxModelObj	- the new CRIST Model
	 */
	public void updateReceived(CtxModelObject ctxModelObj);
}
