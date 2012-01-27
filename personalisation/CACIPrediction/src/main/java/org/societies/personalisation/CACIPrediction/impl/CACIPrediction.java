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
package org.societies.personalisation.CACIPrediction.impl;

import java.util.List;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.mock.EntityIdentifier;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;




/**
 * CAUIPrediction
 * 
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CACIPrediction implements ICAUIPrediction{

	@Override
	public void enablePrediction(Boolean bool) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserIntentAction getCurrentIntentAction(EntityIdentifier requestor,
			EntityIdentifier ownerID, IServiceResourceIdentifier serviceID,
			String preferenceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIntentAction getPrediction(EntityIdentifier requestor,
			CtxAttribute ctxAttribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIntentAction getPrediction(EntityIdentifier requestor,
			IAction action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<String>> getPredictionHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	


}
