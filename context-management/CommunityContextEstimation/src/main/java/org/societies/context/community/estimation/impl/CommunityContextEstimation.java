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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.mock.EntityIdentifier;
import org.societies.context.api.community.estimation.EstimationModels;
import org.societies.context.api.community.estimation.ICommunityCtxEstimationMgr;
import org.societies.context.broker.impl.CtxBroker;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yboul 07-Dec-2011 4:15:14 PM
 * @param <communityMembers>
 */
public class CommunityContextEstimation<communityMembers> implements ICommunityCtxEstimationMgr{
	
	
	//Constructor
	
	public CommunityContextEstimation() {
		// TODO Auto-generated constructor stub
			
	}
	
	//Fields declaretion
	
	@Autowired
	//private InternalCtxBroker b;
	private CtxBroker b;
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
	//@param entityType the type of the entity (e.g. person) as String
	//@param attributeType the attribute we want to make the estimation for (e.g. age)
	
	public void estimateContext(CtxEntityIdentifier communityID, String entityType, String attributeType) 
			throws CtxException, InterruptedException, ExecutionException {
	
		//The getAllCommunityMembers(), uses the retrieveCommunityMembers() that returns a Future<List> with all the members of a given community)
		Future<List<CtxEntityIdentifier>> allMembersListFuture = getAllCommunityMembers(communityID);
		//From Future<List> I get the List with community members of the given Community. So allMembersList will contain a list of (members of Community) contextEntities
		List<CtxEntityIdentifier> allMembersList = allMembersListFuture.get();
		//return list of members of the given type (e.g. person)
		List<CtxAttributeIdentifier> listOfMembersOfGivenType = returnEntitiesWithGivenEntiryType(allMembersList);
		
		returnListOfDesiredAttributes(listOfMembersOfGivenType);
	
		
		// getMembersOfSpecificType(), returns a list with Future objects (from broker)
		Future<List<CtxEntityIdentifier>> membOfSpecificTypeAndAttributeFuture = getMembersOfSpecificTypeAndAttribute(allMembersListFuture, entityType, attributeType);
		
		//From the above future list I get the list with get() in which I have all the persons with the given attribute (e.g. age)
		List<CtxEntityIdentifier> membOfSpecificTypeAndAttribute = membOfSpecificTypeAndAttributeFuture.get();

		
		//getValuesFromMembersAttribute(membOfSpecificTypeAndAttribute, attributeType);
		calculationAlgorithm(membOfSpecificTypeAndAttribute);
	}


	private List<CtxAttributeIdentifier> returnEntitiesWithGivenEntiryType(List<CtxEntityIdentifier> allMembersList) {
	// TODO Auto-generated method stub
		//If the modelType is Entity then put in the listCtxEntityIdentifier this ctxEntityIdentifier.
		//So at the end I will have a list with Entity ctxEntityIdentifiers of the community under discussion
		
	 List<CtxAttributeIdentifier> listCtxEntityIdentifier = new ArrayList<CtxAttributeIdentifier>();
		
		Iterator<CtxEntityIdentifier> membIterator = allMembersList.iterator();
		while (membIterator.hasNext()){
			CtxEntityIdentifier cEI = membIterator.next();
			CtxAttributeIdentifier a = new CtxAttributeIdentifier(cEI, cEI.getType(),cEI.getObjectNumber());
			{
				if
				(cEI.getModelType().ENTITY != null && cEI.getType().equals(entityType)) 
					listCtxEntityIdentifier.add(a);
				else
					System.out.println(cEI.getType());
			}
				
		}
		return listCtxEntityIdentifier;
}
	
	private void returnListOfDesiredAttributes(List<CtxAttributeIdentifier> listOfMembersOfGivenType) {
		// TODO Auto-generated method stub
		// I want to receive the attributes value through the contextIdentifier
		
		ArrayList<CtxAttribute> listOfAttributes = new ArrayList<CtxAttribute>();
		Iterator<CtxAttributeIdentifier> membIterator = listOfMembersOfGivenType.iterator();
		while (membIterator.hasNext()){
			CtxAttributeIdentifier cEI = membIterator.next();
			CtxAttribute ctxAtt;
			CtxModelObject ctxModObj;
			//if 
			//(cEI.getModelType().ATTRIBUTE != null)
				//ctxModObj.
				//listOfAttributes.add(e);
			
		}
	}


	public Future<List<CtxEntityIdentifier>> getAllCommunityMembers(CtxEntityIdentifier communityID) throws CtxException {
		// TODO Auto-generated method stub
		//I want a list with all the CommunityMembers	and I use the methods of ctxBroker
		
		Future<List<CtxEntityIdentifier>> allCommunityMembers = b.retrieveCommunityMembers(null, communityID);
		return allCommunityMembers;
	}
	
	
	private Future<List<CtxEntityIdentifier>> getMembersOfSpecificTypeAndAttribute(Future<List<CtxEntityIdentifier>> allMembersList, String entityType, String attributeType) throws CtxException {
		// TODO Auto-generated method stub
		//Since I got it, somehow, I will iterate in order to put in a new list the members of a given (e.g. "person)"
		//... let say that we end up with a ListWithMembers
		
		Future<List<CtxEntityIdentifier>> membersOfSpecificTypeFuture = b.lookupEntities(null, entityType, attributeType, null, null);
		return membersOfSpecificTypeFuture;

	}
	
	
	
	private ArrayList<CtxAttribute> getValuesFromMembersAttribute(List<CtxEntityIdentifier> membOfSpecificType, String attributeType) {
		// TODO Auto-generated method stub
		//kalytera na gyriso lista apo attributes...
		
		ArrayList<CtxAttribute> res = new ArrayList<CtxAttribute>();
		
		for(CtxEntityIdentifier c:membOfSpecificType){
			//Κλήση σε broker για να κάνει retrieve ενός ctxentity με το συγκεκριμένο identifier θα πρέπει να αντικατασταθεί με μέθοδο του broker
			CtxEntity entity = new CtxEntity(c);
			res.addAll(entity.getAttributes(attributeType));
		}		
		
		//From the new list (with persons) I take the final list with the values of the given attribute
		//TODO
		List<Integer> listWithAttributesValues = new ArrayList<Integer>();
		//return listWithAttributesValues;
		return res;
	}

	private void calculationAlgorithm(List<CtxEntityIdentifier> membOfSpecificTypeAndAttribute){
		
		// ti epistrefo san apotelesma kai kat'epektasi ti update kano ston broker????
//		for (CtxEntityIdentifier i:membOfSpecificTypeAndAttribute){
//			membOfSpecificTypeAndAttribute.re;
//			
//		}
		
	}

	
// Setters and Getters for the private fields ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^//^

	public CtxBroker getB() {                                                                                                  
		return b;																												 
	}																															  

	public void setB(CtxBroker b) {																								
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
