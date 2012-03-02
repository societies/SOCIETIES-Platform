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
package org.societies.context.community.estimation.impl;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.mock.EntityIdentifier;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.context.api.community.estimation.EstimationModels;
import org.societies.context.api.community.estimation.ICommunityCtxEstimationMgr;
import org.societies.context.broker.impl.CtxBroker;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yboul 07-Dec-2011 4:15:14 PM
 */
public class CommunityContextEstimation implements ICommunityCtxEstimationMgr{
	
	@Autowired
	private CtxBroker b;
	

	@Override
	public void estimateContext(EstimationModels estimationModel, List<CtxAttribute> list) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void retrieveCurrentCisContext(boolean Current, EntityIdentifier communityID, List<CtxAttribute> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveHistoryCisContext(boolean Current, EntityIdentifier communityID, List<CtxAttribute> list) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateContextModelObject(CtxEntity estimatedContext) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public void estimateContext(EntityIdentifier communityID, List<CtxAttribute> list, Boolean currentDB) throws CtxException{
		// TODO Auto-generated method stub
		
		CtxAttribute a = new CtxAttribute(null);
//		a.getId().getType();
//		a.getIntegerValue();
		
		ArrayList<CtxAttribute> allAttributes = new ArrayList<CtxAttribute>();
		
		ArrayList<CtxEntity> m = retrieveCisMembersWitPredefinedAttr(communityID, list);
		// elegxos gia null h oxi (ta members)
		for (CtxEntity e:m){
			allAttributes.addAll((retrieveMembersAttribute(e, list)));
		}
		
		CalculateAlgorithm(allAttributes);
	}
	
	private ArrayList<CtxEntity> retrieveCisMembersWitPredefinedAttr(EntityIdentifier communityID, List<CtxAttribute> hasTheseAttributes) throws CtxException {
		// TODO Auto-generated method stub
		//b
		//return (ArrayList<CtxEntity>) b.retrieveAdministratingCSS(null, null); na vro tin kanoniki methodo tou broker...
		return null;
	}

	
	private ArrayList<CtxAttribute> retrieveMembersAttribute(CtxEntity member, List<CtxAttribute> hasTheseAttributes) {
		// TODO Auto-generated method stub
		//b
		//return (ArrayList<CtxEntity>) b.retrieveAdministratingCSS(null, null); na vro tin kanoniki methodo tou broker...
		return null;
	}
	
	private void CalculateAlgorithm(ArrayList<CtxAttribute> allAttributes){
		// ti epistrefo san apotelesma kai kat'epektasi ti update kano ston broker????
	}


	public CtxBroker getB() {
		return b;
	}


	public void setB(CtxBroker b) {
		this.b = b;
	}
	
	


}
