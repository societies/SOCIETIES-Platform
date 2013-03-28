package org.societies.context.user.inheritance.impl;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeBond;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.context.api.user.inheritance.ConflictResolutionAlgorithm;
import org.societies.context.api.user.inheritance.IUserCtxInheritanceMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yboul
 */

@Service
public class UserInheritance implements IUserCtxInheritanceMgr{
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(UserInheritance.class);
	
	@Autowired(required=false)
	private ICtxBroker internalCtxBroker;
	private IUserFeedback userFeedback;
	
	public UserInheritance() {
		LOG.info(this.getClass() + "UserCtxInheritance instantiated ");
	}
	public ArrayList<CtxAttributeBond> getInherittedAttributes (CtxEntityIdentifier cssIdentifier, CtxEntityIdentifier cisIdentifier){
		CtxEntity retrievedCSS = null;
		CommunityCtxEntity retrievedCIS = null;
		
		ArrayList<CtxEntityIdentifier> cisEntityIdList = new ArrayList<CtxEntityIdentifier>();
		ArrayList<CtxAttribute> cssCisCommonAttributes = new ArrayList<CtxAttribute>();
		ArrayList<CtxAttribute> cssCisUncommonAttributes = new ArrayList<CtxAttribute>();
		ArrayList<CtxAttributeBond> cssAttributesForInheritance = new ArrayList<CtxAttributeBond>();
		ArrayList<CtxAttributeBond> cssConflictedAttributesForInheritance = new ArrayList<CtxAttributeBond>();
		//given the css' ctxId get the entity ids of the CIS that this css is member of
	
		try {
			retrievedCSS =  (CtxEntity) this.internalCtxBroker.retrieve(cssIdentifier).get();
			retrievedCIS = (CommunityCtxEntity) this.internalCtxBroker.retrieve(cisIdentifier).get();
			
			Set<CtxAssociationIdentifier> cssAssociationsIdentifiers = retrievedCSS.getAssociations(CtxAssociationTypes.IS_MEMBER_OF);
			
			Set<CtxBond> cisBondsSet = retrievedCIS.getBonds();
			Set<CtxAttribute> cisAttributeSet = retrievedCIS.getAttributes();
			Set<CtxAttribute> cssAttributesSet = retrievedCSS.getAttributes();
			
			if (cisBondsSet.size()!=0 && cssAttributesSet.size()!=0){
				for(CtxBond ctxBond:cisBondsSet){
					
					if (ctxBond instanceof CtxAttributeBond){
						CtxAttributeBond attrBond = (CtxAttributeBond) ctxBond;
						for (CtxAttribute ctxAttribute:cssAttributesSet){
							if (ctxBond.getType() == ctxAttribute.getType() && ctxAttribute.getStringValue()==null){ //&& attrBond.getMinValue()==attrBond.getMaxValue()){
								cssAttributesForInheritance.add(attrBond);
							}
							else{
								cssAttributesForInheritance.add(attrBond);
							}
					}
				
					}
				}
								
				
				
				
			}
			if (cssAssociationsIdentifiers.size()!=0){			
				for(CtxAssociationIdentifier ctxAssocId:cssAssociationsIdentifiers){
					CtxAssociation assocObj = (CtxAssociation) this.internalCtxBroker.retrieve(ctxAssocId).get();
					CtxEntityIdentifier cisId = assocObj.getParentEntity();
					cisEntityIdList.add(cisId);
				}
			}
		
			// If css not yet member of a cis
			if (cisEntityIdList.size()==0){			
				Set<CtxAttribute> cisAttributes = retrievedCIS.getAttributes();
				Set<CtxAttribute> cssAttributes = retrievedCSS.getAttributes();
				for (CtxAttribute cssAtt:cssAttributes){
					for (CtxAttribute cisAtt:cisAttributes){
						if (cisAtt==cssAtt){
							cssCisCommonAttributes.add(cisAtt);
							
							CtxAttributeIdentifier cssAttId = cssAtt.getId();
							CtxAttributeIdentifier cisAttId = cisAtt.getId();
							
							//callQoC(cssId,  cisId,  cssAttId, cisAttId);
						}else {
							cssCisUncommonAttributes.add(cisAtt);
						}
					}
				}
			}
			
			//if css has no common attributes with the cis			
				
				if (cssCisUncommonAttributes.size()!=0){
					
					String proposalText = "Which attribute do you want to update? ";
					String[] options = new String[cssCisCommonAttributes.size()];
					options = (String[]) cssCisUncommonAttributes.toArray();
					
					List<String> feedback = userFeedback.getExplicitFB(ExpProposalType.CHECKBOXLIST, new ExpProposalContent(proposalText, options)).get();
					
					for (String select:feedback){
						for (CtxAttribute ctxAtt:cssCisUncommonAttributes){
							if (ctxAtt.getStringValue()==select){
								CtxAttribute ca = (CtxAttribute) this.internalCtxBroker.createAttribute(cssIdentifier, select).get();
								ca.setStringValue(ctxAtt.getStringValue().toString());
								ca.setValueType(CtxAttributeValueType.STRING);
								ca = (CtxAttribute) this.internalCtxBroker.update(ca).get();
								LOG.info("the aatribute that has been updated is "+ca.getStringValue());
								
							}
						}
					}

					//if css and cis have common attributes
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


		return cssAttributesForInheritance;

	}
	
	public ArrayList<CtxAttributeBond> removeInheritedUserCtx (CtxEntityIdentifier cssIdentifier, CtxEntityIdentifier cisIdentifier){
		try {
			CtxEntity retrievedCSS = (CtxEntity) this.internalCtxBroker.retrieve(cssIdentifier).get();
			CommunityCtxEntity retrievedCIS = (CommunityCtxEntity) this.internalCtxBroker.retrieve(cisIdentifier).get();
			
			Set<CtxBond> cisBondsSet = retrievedCIS.getBonds();
			Set<CtxAttribute> cssAttributesSet = retrievedCSS.getAttributes();
			
			
			
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


	
	
	
/*	private void callQoC(CtxEntityIdentifier ctxEntId, CtxEntityIdentifier ctxComId,  CtxAttributeIdentifier cssCtxAttId, CtxAttributeIdentifier cisCtxAttId) {
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
		
	}*/



}
