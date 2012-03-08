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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.mock.EntityIdentifier;
import org.societies.context.api.community.estimation.EstimationModels;
import org.societies.context.api.community.estimation.ICommunityCtxEstimationMgr;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yboul 07-Dec-2011 4:15:14 PM
 */
public class CommunityContextEstimation implements ICommunityCtxEstimationMgr{
	
	
	//Constructor
	
	public CommunityContextEstimation() {
		// TODO Auto-generated constructor stub
			
	}
	
	//Fields declaretion
	
	@Autowired
	private InternalCtxBroker b;
	private CtxEntityIdentifier comId;
	private String entityType;
	private String attributeType;
	

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
	
//	public void estimateContext_John(EntityIdentifier communityID, List<CtxAttribute> list, Boolean currentDB) throws CtxException{
//		// TODO Auto-generated method stub
//		
//	//	CtxAttribute a = new CtxAttribute(null);
////		a.getId().getType();
////		a.getIntegerValue();
//		
//		ArrayList<CtxAttribute> allAttributes = new ArrayList<CtxAttribute>();
//		
//		ArrayList<CtxEntity> m = retrieveCisMembersWitPredefinedAttr_John(communityID, list);
//		// elegxos gia null h oxi (ta members)
//		for (CtxEntity e:m){
//			allAttributes.addAll((retrieveMembersAttribute_John(e, list)));
//		}
//		
//		CalculateAlgorithm(allAttributes);
//		CtxEntityIdentifier community = null;
//		Identity requester = null;
//		b.retrieveCommunityMembers(requester, community);
//		
//	}
//	
//	private ArrayList<CtxEntity> retrieveCisMembersWitPredefinedAttr_John(EntityIdentifier communityID, List<CtxAttribute> hasTheseAttributes) throws CtxException {
//		// TODO Auto-generated method stub
//		//b
//		//return (ArrayList<CtxEntity>) b.retrieveAdministratingCSS(null, null); na vro tin kanoniki methodo tou broker...
//		return null;
//	}
//
//	
//	private ArrayList<CtxAttribute> retrieveMembersAttribute_John(CtxEntity member, List<CtxAttribute> hasTheseAttributes) {
//		// TODO Auto-generated method stub
//		//b
//		//return (ArrayList<CtxEntity>) b.retrieveAdministratingCSS(null, null); na vro tin kanoniki methodo tou broker...
//		return null;
//	}

	
	//******************************************NEW***************************************************************************
	//
	//
	//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
	
	
	// This method will take as inputs
	//@param communityID the id of the community we want to estimate the context
	//@param entityType the type of the entity (e.g. person)
	//@param attributeType the attribute we want to make the estimation for (e.g. age)
	
	public void estimateContext(CtxEntityIdentifier communityID, String entityType, String attributeType) 
			throws CtxException, InterruptedException, ExecutionException {
	
		Future<List<CtxEntityIdentifier>> allMembersList = getAllCommunityMembers(communityID, entityType);
		//Future<List<CtxEntityIdentifier>> membersOfSpecificType = getMembersOfSpecificType(allMembersList, entityType);
		getValuesFromMembersAttribute(allMembersList, attributeType);
	
	}

	public Future<List<CtxEntityIdentifier>> getAllCommunityMembers(CtxEntityIdentifier communityID, String entityType2) throws CtxException {
		// TODO Auto-generated method stub
		//Θέλω μια λίστα με όλα τα CommunityMembers	
		
		return b.retrieveCommunityMembers(communityID);
	}
	
	
	private Future<List<CtxEntityIdentifier>> getMembersOfSpecificType(Future<List<CtxEntityIdentifier>> allMembersList, String entityType) throws CtxException {
		// TODO Auto-generated method stub
		//Εφόσον την πάρω, με κάποιο τρόπο, θα κάνω ένα iterration για να βάλω σε μια νέα λίστα τα μέλη που έχουν το συγκεκριμένο entityType (π.χ. "person)"
		//TODO
		//... και έστω ότι καταλήγουμε στην ListWithMembers
	 return b.lookupEntities(entityType, attributeType, null, null);
	 //return 1;
	 
	 //Future<List<CtxEntityIdentifier>>
	}
	
	
	
	private List<Integer> getValuesFromMembersAttribute(Future<List<CtxEntityIdentifier>> membersOfSpecificType, String attributeType) {
		// TODO Auto-generated method stub
		//kalytera na gyriso lista apo attributes...
		ArrayList<CtxAttribute> res = new ArrayList<CtxAttribute>();
		try {
			for(CtxEntityIdentifier c:membersOfSpecificType.get()){
				//klisi se broker gia retrieve enos ctxentity me to sigekrimen identifier
				//tha prepei na antikatastahei me methodo tou broker
				CtxEntity entity = new CtxEntity(c);
				res.addAll(entity.getAttributes(attributeType));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		//Από την καινούρια λίστα (με persons) πέρνω την τελική λίστα με τα values του συγκεκριμένου attribute
		//TODO
		List<Integer> listWithAttributesValues = new ArrayList<Integer>();
		return listWithAttributesValues;
	}

	private void CalculateAlgorithm(ArrayList<CtxAttribute> allAttributes){
		
		// ti epistrefo san apotelesma kai kat'epektasi ti update kano ston broker????
		
		
	}

	
// Setters and Getters for the private fields ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^//^

	public InternalCtxBroker getB() {                                                                                                  
		return b;																												 
	}																															  

	public void setB(InternalCtxBroker b) {																								
		this.b = b;																												
	}																															


	public CtxEntityIdentifier getComId() {																						
		return comId;																											
	}																															


	public void setComId(CtxEntityIdentifier comId) {
		this.comId = comId;
	}


	public String getEntityType() {
		return entityType;
	}


	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}


	public String getAttributeType() {
		return attributeType;
	}


	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}





}
