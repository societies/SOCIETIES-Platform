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

package org.societies.personalisation.dianne;

import java.util.HashMap;

import org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE;
import org.societies.personalisation.common.api.management.IPersonalisationInternalCallback;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.mock.EntityIdentifier;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.api.internal.personalisation.model.IOutcome;

public class DIANNE implements IDIANNE{

	private HashMap<EntityIdentifier, NetworkRunner> networks;
	//IInternalUserActionMonitor uaMonitor;
	//IUserCtxBroker ctxBroker;

	public DIANNE(){
		networks = new HashMap<EntityIdentifier, NetworkRunner>();
	}

	@Override
	public void getOutcome(EntityIdentifier ownerId,
			IServiceResourceIdentifier serviceId, 
			String preferenceName, 
			IPersonalisationInternalCallback callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void getOutcome(EntityIdentifier ownerId,
			CtxAttribute attribute, 
			IPersonalisationInternalCallback callback) {
		// Context update received!!!
		if(networks.containsKey(ownerId)){
			
		}else{
			NetworkRunner newNetwork = new NetworkRunner();
		}
	}
	
	@Override
	public void getOutcome(EntityIdentifier ownerId,
			IAction action,
			IPersonalisationInternalCallback callback){
		// Action update received!!!
	}

	@Override
	public void enableDIANNELearning(EntityIdentifier ownerId) {
		System.out.println("Enabling incremental learning for identity: "+ ownerId);
		if(networks.containsKey(ownerId)){
			NetworkRunner network = networks.get(ownerId);
			network.play();
		}else{
			System.out.println("No networks exist for this identity");
		}
	}

	@Override
	public void disableDIANNELearning(EntityIdentifier ownerId) {
		System.out.println("Disabling incremental learning for identity: "+ ownerId);	
		if(networks.containsKey(ownerId)){
			NetworkRunner network = networks.get(ownerId);
			network.pause();
		}else{
			System.out.println("No networks exist for this identity");
		}
	}
	
	
	public void initialiseDIANNELearning(){
		System.out.println("DIANNE initialised!!");
		//register for action updates from PersonalisationMgr
		
		//register for context updates from PersonalisationMgr
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE#getOutcome(org.societies.api.mock.EntityIdentifier, org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String)
	 * DEPRECATED
	 */
	@Override
	public IOutcome getOutcome(EntityIdentifier arg0,
			IServiceResourceIdentifier arg1, String arg2) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE#getOutcome(org.societies.api.mock.EntityIdentifier, org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String, org.societies.api.context.model.CtxAttribute)
	 * DEPRECATED
	 */
	@Override
	public IOutcome getOutcome(EntityIdentifier arg0,
			IServiceResourceIdentifier arg1, String arg2, CtxAttribute arg3) {
		return null;
	}
}
