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
package org.societies.personalisation.UserPreferenceManagement.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.cis.PreMerger;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Eliza
 *
 */
public class TestPreMerger {

	private IIdentity userId;
	private IIdentity otherUserId;
	
	private ICtxBroker broker;
	private PreMerger preMerger;
	private IndividualCtxEntity ctxEntity;
	private CtxAttribute ctxLocationAttribute;
	private IndividualCtxEntity otherCtxEntity;
	private CtxAttribute otherCtxLocationAttribute;
	private IPreference rootPreference;
	private ServiceResourceIdentifier serviceID;

	@Before
	public void setup(){
		userId = new MockIdentity(IdentityType.CSS, "myId", "domain");
		otherUserId = new MockIdentity(IdentityType.CSS, "otherId", "domain");
		setupContext();
		setupPreference();
		broker = Mockito.mock(ICtxBroker.class);
		
		
		try {
			Mockito.when(this.broker.retrieveIndividualEntity(userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(this.ctxEntity));
			List<CtxIdentifier> attList = new ArrayList<CtxIdentifier>();
			attList.add(this.ctxLocationAttribute.getId());
			Mockito.when(this.broker.lookup(this.ctxEntity.getId(), CtxModelType.ATTRIBUTE, this.ctxLocationAttribute.getType())).thenReturn(new AsyncResult<List<CtxIdentifier>>(attList));
			Mockito.when(this.broker.lookup(this.ctxEntity.getId(), CtxModelType.ATTRIBUTE, CtxAttributeTypes.STATUS)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		preMerger = new PreMerger(broker, userId);
	}
	
	
	
	@Test
	public void preMergerTest(){
		IPreference replaceCtxIdentifiers = preMerger.replaceCtxIdentifiers(rootPreference);
		
		
		
		Assert.assertNotNull(replaceCtxIdentifiers);
		Assert.assertNull(replaceCtxIdentifiers.getUserObject());
		Assert.assertEquals(2, replaceCtxIdentifiers.getDepth());
		
		Enumeration<IPreference> depthFirstEnumeration = replaceCtxIdentifiers.depthFirstEnumeration();
		
		while(depthFirstEnumeration.hasMoreElements()){
			IPreference nextElement = depthFirstEnumeration.nextElement();
			if (nextElement.isLeaf()){
				if (nextElement.getUserObject() instanceof IPreferenceCondition){
					Assert.fail("leaf node contains a condition.");
				}
			}else{
				if (nextElement.getUserObject() instanceof IOutcome){
					Assert.fail("branch node contains an outcome.");
				}
			}
		}
	}
	
	
	
	
	private void setupPreference() {
		
		this.serviceID = new ServiceResourceIdentifier();
		try {
			this.serviceID.setIdentifier(new URI("css://mycss.com/MediaPlayer"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serviceID.setServiceInstanceIdentifier("MediaPlayer");
		String preferenceName = "volume";
		PreferenceTreeNode preference = new PreferenceTreeNode(new ContextPreferenceCondition(this.otherCtxLocationAttribute.getId(), OperatorConstants.EQUALS, "work", CtxAttributeTypes.LOCATION_COORDINATES));
		PreferenceTreeNode nodeNullCondition = new PreferenceTreeNode(new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "home", null));
		nodeNullCondition.add(new PreferenceTreeNode(new PreferenceOutcome(serviceID, "", preferenceName, "100")));
		preference.add(nodeNullCondition);
		
		PreferenceTreeNode nodeNonNullCondition = new PreferenceTreeNode(new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "something", CtxAttributeTypes.STATUS));
		nodeNonNullCondition.add(new PreferenceTreeNode(new PreferenceOutcome(serviceID, "", preferenceName, "50")));
		preference.add(nodeNonNullCondition);
		
		
		preference.add(new PreferenceTreeNode(new PreferenceOutcome(serviceID, "", preferenceName, "0")));
		
		rootPreference = new PreferenceTreeNode();
		rootPreference.add(preference);
		
		rootPreference.add(new PreferenceTreeNode(new PreferenceOutcome(serviceID, "", preferenceName, "20")));
		
		PreferenceTreeNode preferenceTreeNode = new PreferenceTreeNode(new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "something", CtxAttributeTypes.STATUS));
		PreferenceTreeNode preferenceTreeNode2 = new PreferenceTreeNode(new ContextPreferenceCondition(this.otherCtxLocationAttribute.getId(), OperatorConstants.EQUALS, "park", CtxAttributeTypes.LOCATION_SYMBOLIC));
		preferenceTreeNode.add(preferenceTreeNode2);
		rootPreference.add(preferenceTreeNode);
	}



	private void setupContext() {
		
		CtxEntityIdentifier ctxEntityId = new CtxEntityIdentifier(userId.getJid(), CtxEntityTypes.PERSON, new Long(1));
		ctxEntity = new IndividualCtxEntity(ctxEntityId);
		
		CtxEntityIdentifier otherCtxEntityId = new CtxEntityIdentifier(otherUserId.getJid(), CtxEntityTypes.PERSON, new Long(2));
		otherCtxEntity = new IndividualCtxEntity(otherCtxEntityId);
		
		CtxAttributeIdentifier ctxLocationAttributeId = new CtxAttributeIdentifier(otherCtxEntityId, CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		ctxLocationAttribute = new CtxAttribute(ctxLocationAttributeId);
		
		CtxAttributeIdentifier otherCtxLocationAttributeId = new CtxAttributeIdentifier(otherCtxEntityId, CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		otherCtxLocationAttribute = new CtxAttribute(otherCtxLocationAttributeId);
		
		
		
	}




	
}
