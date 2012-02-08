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
package org.societies.context.userHistory.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;



public class UserContextHistoryManagement implements IUserCtxHistoryMgr {

	private final LinkedHashMap<Long,List> hocObjects;

	public UserContextHistoryManagement(){
		this.hocObjects =  new LinkedHashMap<Long,List>();
	}



	public void storeHoCAttribute(CtxAttribute ctxAttribute,Date date){
		CtxHistoryAttribute hocAttr = new CtxHistoryAttribute(ctxAttribute);
		Long i = HistoryCtxModelObjectNumberGenerator.getNextValue();
		List<Serializable> hocObject = new ArrayList();
		hocObject.add(0,hocAttr.getId());
		hocObject.add(1,hocAttr);
		hocObject.add(2,date);
		this.hocObjects.put(i,hocObject);	
	}


	@Override
	public void disableCtxRecording() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableCtxRecording() {
		// TODO Auto-generated method stub

	}

	public List<CtxHistoryAttribute> retrieveHistory(CtxAttributeIdentifier attrId) {

		
		List<CtxHistoryAttribute> results = new ArrayList<CtxHistoryAttribute>();
		for(Long l : this.hocObjects.keySet()){
			
			List tempRes = this.hocObjects.get(l);
			CtxAttributeIdentifier ctxAttrid = (CtxAttributeIdentifier) tempRes.get(0);
			if (ctxAttrid.equals(attrId)){
				
				results.add((CtxHistoryAttribute) tempRes.get(1));
			}
		}

		return results;
	}


	/*
	public List<CtxHistoryAttribute> retrieveHistory(CtxAttributeIdentifier attrId) {

		List<CtxHistoryAttribute> results = new ArrayList<CtxHistoryAttribute>();
		for(CtxHistoryAttribute ctxHistoryAttribute : this.hocObjects.keySet()){
			if (ctxHistoryAttribute.getId().equals(attrId)){
				results.add(ctxHistoryAttribute);
			}
		}

		return results;
	}
	 */
	@Override
	public List<CtxHistoryAttribute> retrieveHistory(CtxAttributeIdentifier attrId,
			Date startDate, Date endDate) {

		List<CtxHistoryAttribute> results = new ArrayList<CtxHistoryAttribute>();
		results = retrieveHistory(attrId);

		//callback.historyRetrievedDate(results);
		return results;
	}

	@Override
	public int removeCtxHistory(CtxAttribute arg0, Date arg1, Date arg2)
			throws CtxException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeHistory(String arg0, Date arg1, Date arg2)
			throws CtxException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<CtxHistoryAttribute> retrieveHistory(
			CtxAttributeIdentifier arg0, int arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	
	//*************************************
	//  Tuple management
	//*************************************
	
	@Override
	public List<CtxAttributeIdentifier> getCtxHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Boolean removeCtxHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> retrieveHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1,
			Date arg2, Date arg3) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setCtxHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxAttributeIdentifier> updateCtxHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

}
