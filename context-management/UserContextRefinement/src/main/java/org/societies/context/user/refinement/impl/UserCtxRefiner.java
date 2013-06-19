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
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.context.api.user.inference.UserCtxInferenceException;
import org.societies.context.api.user.refinement.IUserCtxRefiner;
//import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.location.UserLocationRefiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.service.ServiceUnavailableException;
import org.springframework.stereotype.Service;

/**
 * UserCtxRefiner Implementation This class implements the interface between the
 * context broker, context inference manager and the Bayesian library
 */
@Service
public class UserCtxRefiner implements IUserCtxRefiner {

	private static Logger LOG = LoggerFactory.getLogger(UserCtxRefiner.class);
	
	@Autowired(required=false)
	private UserLocationRefiner userLocationRefiner;
	
	//private BayesEngine learner = BayesEngine.getInstance();

	//private BayesianInference bayesianInference;

	public UserCtxRefiner() {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		//this.initialise();
	}
/* TODO	
	private void initialise() {
		
		this.bayesianInference = new BayesianInference();
	}
*/	
	/*
	 * @see org.societies.context.api.user.refinement.IUserCtxRefiner#refineOnDemand(org.societies.api.context.model.CtxAttributeIdentifier)
	 */
	@Override
	public CtxAttribute refineOnDemand(CtxAttributeIdentifier attrId) throws UserCtxInferenceException {
	
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("Refining attribute '" + attrId + "'");
		if (CtxAttributeTypes.LOCATION_SYMBOLIC.equals(attrId.getType()))
			try {
				return this.userLocationRefiner.refineOnDemand(attrId);
			} catch (ServiceUnavailableException sue) {
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Service UserLocationRefiner is not available");
			}
		else if( CtxAttributeTypes.LOCATION_COORDINATES.equals(attrId.getType())){
			try {
				return this.userLocationRefiner.refineOnDemandGPSCoords(attrId);
			} catch (ServiceUnavailableException sue) {
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Service UserLocationRefiner is not available");
			}
		}
		else 
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "': Unsupported attribute type: " + attrId.getType());
		// TODO bayesian
		/*
		DAG rule = null;
		
		return (bayesianInference.eval(toRefine, rule)).iterator().next();
		*/
	}
	
	/*
	 * @see org.societies.context.api.user.refinement.IUserCtxRefiner#refineContinuously(org.societies.api.context.model.CtxAttributeIdentifier, double)
	 */
	@Override
	public void refineContinuously(final CtxAttributeIdentifier attrId, double updateFreq) 
			throws UserCtxInferenceException {
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("Refining attribute '" + attrId + "' continuously");
		if (CtxAttributeTypes.LOCATION_SYMBOLIC.equals(attrId.getType()))
			try {
				this.userLocationRefiner.refineContinuously(attrId, updateFreq);
			} catch (ServiceUnavailableException sue) {
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "' continuously: Service UserLocationRefiner is not available");
			}
		else
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "': Unsupported attribute type: " + attrId.getType());
	}
	
	/*
	 * @see org.societies.context.api.user.refinement.IUserCtxRefiner#getInferrableTypes()
	 */
	@Override
	public List<String> getInferrableTypes() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}
}