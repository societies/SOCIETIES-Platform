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
package org.societies.context.user.refinement.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.context.api.user.refinement.IUserCtxRefiner;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.springframework.stereotype.Service;

/**
 * UserCtxRefiner Implementation This class implements the interface between the
 * context broker, context inference manager and the Bayesian library
 */
@Service
public class UserCtxRefiner implements IUserCtxRefiner {

	private BayesEngine learner = BayesEngine.getInstance();

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private BayesianInference bayesianInference;


	public UserCtxRefiner() {
		initialise();
	}
		
	public void initialise(){
		this.bayesianInference = new BayesianInference();
		logger.info("{}", "CSM started");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.context.api.user.refinement.IUserCtxRefiner#refineContext
	 * (org.societies.api.context.model.CtxAttributeIdentifier)
	 */
	@Override
	public CtxAttribute refineContext(CtxAttributeIdentifier arg0) {
		
		CtxAttribute toRefine = null;
		//TODO retrieve CtxAttribute to modify from its identifier
		
		DAG rule = null;
		
		return (bayesianInference.eval(toRefine, rule)).iterator().next();
		
		

	}


	/* (non-Javadoc)
	 * @see org.societies.context.api.user.refinement.IUserCtxRefiner#getInferableTypes()
	 */
	@Override
	public List<String> getInferableTypes() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.context.api.user.refinement.IUserCtxRefiner#inferContextContinuously
	 * (org.societies.api.context.model.CtxAttributeIdentifier, double)
	 */
	public void inferContextContinuously(CtxAttributeIdentifier id, double updateFreq){
		//TODO
	}

}