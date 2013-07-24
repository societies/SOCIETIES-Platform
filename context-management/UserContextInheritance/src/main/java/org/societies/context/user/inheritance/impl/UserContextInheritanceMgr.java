/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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
package org.societies.context.user.inheritance.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.context.api.user.inheritance.ConflictResolutionAlgorithm;
import org.societies.context.api.user.inheritance.IUserCtxInheritanceMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserContextInheritanceMgr implements IUserCtxInheritanceMgr {

	private static final Logger LOG = LoggerFactory.getLogger(UserContextInheritanceMgr.class);
	private ICtxBroker ctxBroker;
	private ICommManager commMngr;

	@Autowired (required=true)
	public UserContextInheritanceMgr(ICtxBroker ctxBroker, ICommManager commMngr ) throws Exception {	
		if (LOG.isDebugEnabled()){
			LOG.info(this.getClass() + "instantiated ");
		}
		this.ctxBroker = ctxBroker;
		this.commMngr = commMngr;
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public ICommManager getCommMngr() {
		return commMngr;
	}

	public void setCommMngr(ICommManager commMngr) {
		this.commMngr = commMngr;
	}

	public UserContextInheritanceMgr() throws Exception {	
		if (LOG.isDebugEnabled()){
			LOG.info(this.getClass() + "instantiated ");
		}
	}

	public List<String>  inferTypes() {
		
		final List<String> inferrableTypes = new CopyOnWriteArrayList<String>();
		inferrableTypes.add(CtxAttributeTypes.TEMPERATURE);
		inferrableTypes.add(CtxAttributeTypes.INTERESTS);
		inferrableTypes.add(CtxAttributeTypes.BOOKS);
		inferrableTypes.add(CtxAttributeTypes.ACTIVITIES);
		inferrableTypes.add(CtxAttributeTypes.OCCUPATION);
		inferrableTypes.add(CtxAttributeTypes.LOCATION_SYMBOLIC);
		inferrableTypes.add(CtxAttributeTypes.TEMPERATURE);
		
		return inferrableTypes;
	}

	/**
	 *
	 * 
	 * @param 
	 * @param 
	 * @param attrType
	 *            
	 * @throws CtxException 
	 *             if the unregistration process fails
	 * 
	 * @since 0.0.3
	 */
	@Override
	public CtxAttribute compareQoC(CtxAttribute ctxAtt1, CtxAttribute ctxAtt2) {

		long freshnessOfFirstAttribute = ctxAtt1.getQuality().getFreshness();
		long freshnessOfSecondAttribute = ctxAtt2.getQuality().getFreshness();

		double base=0.0;
		if (freshnessOfFirstAttribute < freshnessOfSecondAttribute) {
			base=freshnessOfFirstAttribute;
		}else{
			base=freshnessOfSecondAttribute;
		}
		System.out.println("Base is "+base);
		double qoc1 = 50*(freshnessOfFirstAttribute/base) + 50*freshnessOfFirstAttribute;
		double qoc2 = 50*(freshnessOfSecondAttribute/base) + 50*freshnessOfSecondAttribute;

		if (qoc1>=qoc2){
			System.out.println("The returned att1 is " + ctxAtt1 + " " + ctxAtt1.getStringValue());
			return ctxAtt1;
		}else{
			System.out.println("The returned att2 is " + ctxAtt2+ " " + ctxAtt2.getStringValue());
			return ctxAtt2;
		}				

	}

	@Override
	public void getCIS(IIdentity arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getContextAttribute(CtxAttributeIdentifier arg0,
			CtxAttributeValueType arg1, IIdentity arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void inheritContextAttribute(CtxAttributeIdentifier arg0,
			CtxAttributeValueType arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resolveConflicts(ConflictResolutionAlgorithm arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public CtxAttribute communityInheritance(CtxAttributeIdentifier ctxAttrId) {
		LOG.info("in the main method");
		CtxAttribute retAttribute = null;
		List<CtxIdentifier> cisCtxAttributeIdList = new ArrayList<CtxIdentifier>();
		List<CtxIdentifier> totalListOfCisCtxAttributeId = new ArrayList<CtxIdentifier>();
		ArrayList<CtxAttribute> listWithCtxAttributeObjs = new ArrayList<CtxAttribute>();
		ArrayList<CtxAssociation> setOfCtxAssociationsObj = new ArrayList<CtxAssociation>();	
		Set<CtxAssociationIdentifier> setOfCSSAssocIds = new HashSet<CtxAssociationIdentifier>();
		Set<CtxEntityIdentifier> assocChildId = new HashSet<CtxEntityIdentifier>();

		 
		
		//Given the css entity, fetch a set of association ids, type "isMemberOf"
		String cssIdString = commMngr.getIdManager().getThisNetworkNode().getBareJid();
		IIdentity ownerId;
		try {
			ownerId = commMngr.getIdManager().fromJid(cssIdString);
			IndividualCtxEntity cssEntity = this.ctxBroker.retrieveIndividualEntity(ownerId).get();
			setOfCSSAssocIds = cssEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF);
			CtxAttribute a = this.ctxBroker.retrieveAttribute(ctxAttrId, false).get();
			String attributeType = a.getType();
			for (CtxAssociationIdentifier cssAssocId:setOfCSSAssocIds){			
				CtxAssociation assocObj = (CtxAssociation) ctxBroker.retrieve(cssAssocId).get();
				setOfCtxAssociationsObj.add(assocObj);
			}
			//from the association objects, get the CIS ids (getParent method) (for each .getChildEntities get the entities where the getOwnerId = jid of the CIS (by using the comm manager))

			for (CtxAssociation assocObj:setOfCtxAssociationsObj){		
				 assocChildId = assocObj.getChildEntities();
				LOG.info("The assocParentId is :"+assocChildId); //Ta 10 CIS, p.x. context://cis-84e13f1b-6e7a-4e3b-b184-22c19f2372f6.ict-societies.eu/ENTITY/community/32774
			}
			
			//use the  lookup (requestor, targerid, attribute, attId.getType()) to retrieve a set of att ids
			
			for (CtxEntityIdentifier cisIdentifier:assocChildId){
				cisCtxAttributeIdList = this.ctxBroker.lookup(new Requestor(ownerId), cisIdentifier, CtxModelType.ATTRIBUTE, attributeType).get();
				totalListOfCisCtxAttributeId.addAll(cisCtxAttributeIdList);
				}
			LOG.info("the total list of CIS Attributes is "+totalListOfCisCtxAttributeId.size());
			
			// through the broker, retrieve the attribute objects
			//for (CtxIdentifier attId:cisCtxAttributeIdList){
			for (CtxIdentifier attId:totalListOfCisCtxAttributeId){
				CtxAttribute attrEntity = (CtxAttribute) ctxBroker.retrieve(attId).get();
				listWithCtxAttributeObjs.add(attrEntity);	
			}
			
			//if the attributes are more than one, then run the compareQoC method
			if (listWithCtxAttributeObjs.size() >= 2) {
				CtxAttribute currAtt = listWithCtxAttributeObjs.get(0);
				for (int i=1; i<listWithCtxAttributeObjs.size(); i++){
					retAttribute = (CtxAttribute)compareQoC(currAtt, listWithCtxAttributeObjs.get(i));
					currAtt=retAttribute;
				}

			}else{
				retAttribute =listWithCtxAttributeObjs.get(0);
				
			}

		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retAttribute;
	}
}
