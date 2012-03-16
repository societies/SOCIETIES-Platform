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
package org.societies.personalisation.CAUIPrediction.impl;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.personalisation.mgmt.IPersonalisationCallback;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.management.IPersonalisationInternalCallback;


/**
 * CAUIPrediction
 * 
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUIPrediction implements ICAUIPrediction{

	
	private IInternalPersonalisationManager persoMgr;
	private ICtxBroker ctxBroker;


	public ICtxBroker getCtxBroker() {
		System.out.println(this.getClass().getName()+": Return ctxBroker");

		return ctxBroker;
	}


	public void setCtxBroker(ICtxBroker ctxBroker) {
		System.out.println(this.getClass().getName()+": Got ctxBroker");

		this.ctxBroker = ctxBroker;
	}

	public IInternalPersonalisationManager getPersoMgr() {
		System.out.println(this.getClass().getName()+": Return persoMgr");
		return persoMgr;
	}


	public void setPersoMgr(IInternalPersonalisationManager persoMgr) {
		System.out.println(this.getClass().getName()+": Got persoMgr");
		this.persoMgr = persoMgr;
	}

	// constructor
	public void initialiseCAUIPrediction(){
			
	}
	
	CAUIPrediction(){
		
	}
	
	@Override
	public void enablePrediction(Boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IUserIntentAction getCurrentIntentAction(IIdentity arg0,
			ServiceResourceIdentifier arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getPrediction(IIdentity arg0, IAction arg1,
			IPersonalisationInternalCallback arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getPrediction(IIdentity arg0, CtxAttribute arg1,
			IPersonalisationInternalCallback arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<List<String>> getPredictionHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
