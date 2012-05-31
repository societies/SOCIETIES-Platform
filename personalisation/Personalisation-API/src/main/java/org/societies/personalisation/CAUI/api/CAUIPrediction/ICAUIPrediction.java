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
package org.societies.personalisation.CAUI.api.CAUIPrediction;


import java.util.List;
import java.util.concurrent.Future;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.identity.IIdentity;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
/**
 * @since 0.0.1
 * @author nikosk(ICCS)
 * @version 1.0
 * @created 15-Nov-2011 1:42:10 PM
 */

public interface ICAUIPrediction {

	/**
	 * This method allows the system to provide predictions 
	 * 
	 * @param bool
	 */
	public void enablePrediction(Boolean bool);

	/**
	 * Allows any service to request a context-based user intent prediction.
	 *  
	 * @param ownerID    the DigitalIdentity of the owner of the user intent model 
	 * @param serviceID  the service identifier of the service requesting the action prediction
	 * @param userActionName    the type of the user action requested
	 * @return					the outcome in the form of an UserIntentAction object
	 */
	public Future<IUserIntentAction> getCurrentIntentAction(IIdentity ownerID, ServiceResourceIdentifier serviceID, String userActionType);
	
	/**
	 * Predicts next user action based on an action update. 
	 *   
	 * @param requestor
	 * @param action
	 * @return predicted action 
	 */
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor, IAction action); 
	
	/**
	 * Predicts next user action based on a context attribute update. 
	 */	   
	public Future<List<IUserIntentAction>> getPrediction(IIdentity requestor, CtxAttribute contextAttribute);
	
	/**
	 * Returns a list with the performed predictions.
	 * @return List
	 */
	public List<List<String>> getPredictionHistory();
	
	/**
	 * Receives the performed action 
	 * 
	 */
	public void receivePredictionFeedback(IAction action);
	
}
