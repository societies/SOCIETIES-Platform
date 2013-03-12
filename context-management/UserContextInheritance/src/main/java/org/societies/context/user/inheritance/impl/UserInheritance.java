/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druΕΎbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAΓ‡ΓƒO, SA (PTIN), IBM Corp., 
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
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.identity.IIdentity;
import org.societies.context.api.user.inheritance.ConflictResolutionAlgorithm;
import org.societies.context.api.user.inheritance.IUserCtxInheritanceMgr;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.context.model.CtxEntityIdentifier;

/**
 * @author yboul
 */

@Service
public class UserInheritance implements IUserCtxInheritanceMgr{
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(UserInheritance.class);
	
	@Autowired(required=false)
	private ICtxBroker internalCtxBroker;
	
	public UserInheritance() {
		LOG.info(this.getClass() + "UserCtxInheritance instantiated ");
	}

	public CtxAttribute inheritCommunityCtx (CtxEntityIdentifier ctxEntId, CtxEntityIdentifier ctxComId){
		CtxEntity retrievedCSS = null;
		CommunityCtxEntity retrievedCIS = null;
		CtxModelObject cssObj = null;
		CtxModelObject cisObj = null;
		
		ArrayList<CtxEntityIdentifier> cisEntityIdList = new ArrayList<CtxEntityIdentifier>();
		ArrayList<CtxAttribute> cssCisCommonAttributes = new ArrayList<CtxAttribute>();
		ArrayList<CtxAttribute> cssCisUncommonAttributes = new ArrayList<CtxAttribute>();
		
		//apo to given ctxId tou css tha paro ta entity ids ton CIS sta opoia anoikei
		try {
			retrievedCSS =  (CtxEntity) this.internalCtxBroker.retrieve(ctxEntId).get();
			retrievedCIS = (CommunityCtxEntity) this.internalCtxBroker.retrieve(ctxComId).get();
			
			Set<CtxAssociationIdentifier> cssAssociationsIdentifiers = retrievedCSS.getAssociations(CtxAssociationTypes.IS_MEMBER_OF);
			
			if (cssAssociationsIdentifiers.size()!=0){			
				for(CtxAssociationIdentifier ctxAssocId:cssAssociationsIdentifiers){
					CtxAssociation assocObj = (CtxAssociation) this.internalCtxBroker.retrieve(ctxAssocId).get();
					CtxEntityIdentifier cisId = assocObj.getParentEntity();
					cisEntityIdList.add(cisId);
				}
			}
		
			// To css eite tha einai hdh melos enos CIS h oxi. Ean den einai 
			if (cisEntityIdList.size()==0){			
				Set<CtxAttribute> cisAttributes = retrievedCIS.getAttributes();
				Set<CtxAttribute> cssAttributes = retrievedCSS.getAttributes();
				for (CtxAttribute cssAtt:cssAttributes){
					for (CtxAttribute cisAtt:cisAttributes){
						if (cisAtt==cssAtt){
							cssCisCommonAttributes.add(cisAtt);
							
							CtxAttributeIdentifier cssAttId = cssAtt.getId();
							CtxAttributeIdentifier cisAttId = cisAtt.getId();
							
							callQoC(ctxEntId,  ctxComId,  cssAttId, cisAttId);
						}else {
							cssCisUncommonAttributes.add(cisAtt);
						}
					}
				}
			}
			
			//to css den exei common attributes
			if (cssCisCommonAttributes.size()==0){
				for (CtxAttribute ctxAtt:cssCisUncommonAttributes){
					retrievedCSS.addAttribute(ctxAtt);
				}
				//to css exei common attributes
			}else {
				for (CtxAttribute ctxAtt:cssCisCommonAttributes){
				}
			}		
			

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


		return null;

	}
	
	
	
	
	
	
	private void callQoC(CtxEntityIdentifier ctxEntId, CtxEntityIdentifier ctxComId,  CtxAttributeIdentifier cssCtxAttId, CtxAttributeIdentifier cisCtxAttId) {
		// TODO Auto-generated method stub, that will use the QoC to compare the quality of two attributes, one in a css and on in a cis
		try {
			CtxEntity retrievedCSS = (CtxEntity) this.internalCtxBroker.retrieve(ctxEntId).get();
			CtxEntity retrievedCIS = (CtxEntity) this.internalCtxBroker.retrieve(ctxComId).get();
			CtxAttribute retrievedCssAtt =(CtxAttribute) this.internalCtxBroker.retrieveAttribute(cssCtxAttId, false);
			CtxAttribute retrievedCisAtt =(CtxAttribute) this.internalCtxBroker.retrieveAttribute(cisCtxAttId, false);
			
			
			
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

}
