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

package org.societies.personalisation.CRISTUserIntentPrediction.test;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.common.api.management.IPersonalisationInternalCallback;

public class CRISTUserIntentPredictionTest {
	
	private ICRISTUserIntentPrediction cristPredictor;
	EntityIdentifier myID;
	private CtxAttribute myCtx;
	IPersonalisationInternalCallback myCallback;
	
	public static void main(){
		System.out.println("Hello Kitty");
	}
	
	public CRISTUserIntentPredictionTest(ICRISTUserIntentPrediction CRISTPredictor){
		this.cristPredictor = CRISTPredictor;
	}
	
	public void initialiseTesting(){
		System.out.println("This is the testing class for CRIST UI Predictioin");
		cristPredictor.getCRISTPrediction(myID, myCtx, myCallback);
	}
	
	public ICRISTUserIntentPrediction getCristPredictor() {
		System.out.println(this.getClass().getName()+"Return CRISTPredictor");
		return cristPredictor;
	}

	public void setPreManager(ICRISTUserIntentPrediction CRISTPredictor) {
		System.out.println(this.getClass().getName()+"GOT CRISTPredictor");
		this.cristPredictor = CRISTPredictor;
	}
}