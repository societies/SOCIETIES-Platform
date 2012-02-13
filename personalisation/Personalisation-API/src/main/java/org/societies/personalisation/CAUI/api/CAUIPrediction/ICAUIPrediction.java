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
package org.societies.personalisation.CAUI.api.CAUIPrediction;


import java.util.List;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.personalisation.mgmt.IPersonalisationCallback;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.api.comm.xmpp.datatypes.Identity;

/**
 * @since 0.0.1
 * @author nikosk(ICCS)
 * @version 1.0
 * @created 15-Nov-2011 1:42:10 PM
 */

public interface ICAUIPrediction {

	/**
	 * 
	 * @param bool
	 */
	public void enablePrediction(Boolean bool);

	/**
	 * Allows any service to request a context-based evaluated preference outcome.
	 *  
	 * @param requestor    the DigitalIdentity of the service requesting the outcome
	 * @param ownerID    the DigitalIdentity of the owner of the preferences (i.e. the
	 * user of this service)
	 * @param serviceID    the service identifier of the service requesting the
	 * outcome
	 * @param preferenceName    the name of the preference requested
	 * @return					the outcome in the form of an IAction object
	 */
	public UserIntentAction getCurrentIntentAction(Identity requestor, Identity ownerID, IServiceResourceIdentifier serviceID, String preferenceName);

	
	/**
	 * Predicts next action based on the last performed action
	 * 
	 * @param requestor
	 * @param action
	 * @return predicted action 
	 */
	public void getPrediction(Identity requestor, IAction action, IPersonalisationCallback persCallback); 
		
	/**
	 * Returns a list with the performed predictions.
	 * 
	 */
	
	public List<List<String>> getPredictionHistory();
	
}
