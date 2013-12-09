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
package org.societies.integration.test.bit.ctxRetrieve;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class Tester2 {
	
	private IIdentityManager identityManager;
	private ICtxBroker ctxBroker;
	private IIdentity userIdentity;
	private IndividualCtxEntity cssPersonEntity;
	private CtxAttribute nameAttribute;
	private CtxAttribute nameAttribute2;
	private Requestor requestor;
	private IIdentity serviceIdentity;
	private IHelloWorld helloWorld;
	private IPrivacyDataManager privacyDataManager;
	public Tester2(){
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
	}
	
	@Before
	public void setup(){
		identityManager = Test861.getIdentityManager();
		ctxBroker = Test861.getCtxBroker();
		helloWorld = Test861.getHelloWorld();
		privacyDataManager = Test861.getPrivDataMgr();
		userIdentity = identityManager.getThisNetworkNode();
		
		
		try {
			Future<IndividualCtxEntity> retrieveIndividualEntity = ctxBroker.retrieveIndividualEntity(userIdentity);
			this.cssPersonEntity = retrieveIndividualEntity.get();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.setupRequestor();
		this.createNonInferrableAttribute();

	}
	
	@Test
	public void testRetrieve(){
		List<String> atts = new ArrayList<String>();
		atts.add(CtxAttributeTypes.NAME_FIRST);
		atts.add(CtxAttributeTypes.NAME_LAST);
		atts.add(CtxAttributeTypes.LOCATION_SYMBOLIC);
			List<CtxAttribute> attrs = this.helloWorld.retrieveCtxAttribute(atts);
			
			//this.helloWorld.displayUserLocation();
	
			try {
				List<ResponseItem> rItems = this.privacyDataManager.checkPermission(requestor, this.nameAttribute.getId(), new Action(ActionConstants.READ));
				List<ResponseItem> rItems2 = this.privacyDataManager.checkPermission(requestor, this.nameAttribute2.getId(), new Action(ActionConstants.READ));
				
				
				if (rItems != null && rItems.size() > 0 && Decision.DENY.equals(rItems.get(0).getDecision())){
					for (CtxAttribute attr: attrs){
						if (attr.getStringValue().equalsIgnoreCase("Chuck Norris")){
							TestCase.fail("HelloWorld service got access to an attribute it shouldn't have");
						}
					}
				}
				
				if (rItems != null && rItems.size() > 0 && Decision.PERMIT.equals(rItems.get(0).getDecision())){
					boolean foundAttribute = false;
					for (CtxAttribute attr: attrs){
						if (attr.getStringValue().equalsIgnoreCase("Chuck Norris")){
							foundAttribute = true;
						}
					}
					
					if(!foundAttribute){
						TestCase.fail("HelloWorld service did not get access to an attribute to which access was allowed");
					}
				}
				
				
				
				if (rItems2 != null && rItems2.size() > 0 && rItems2.get(0).getDecision().equals(Decision.DENY)){
					for (CtxAttribute attr: attrs){
						if (attr.getStringValue().equalsIgnoreCase("Walker, Texas Ranger")){
							TestCase.fail("HelloWorld service got access to an attribute it shouldn't have");
						}
					}
				}
				
				if (rItems2 != null && rItems2.size() > 0 && rItems2.get(0).getDecision().equals(Decision.PERMIT)){
					boolean foundAttribute = false;
					for (CtxAttribute attr: attrs){
						if (attr.getStringValue().equalsIgnoreCase("Walker, Texas Ranger")){
							foundAttribute = true;
						}
					}
					
					if (!foundAttribute){
						TestCase.fail("HelloWorld service did not get access to an attribute to which access was allowed");
					}
				}
			} catch (PrivacyException e) {
				TestCase.fail("Error during data access:"+e.getMessage());
			}
		
	}
	
	private void setupRequestor() {
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		
		try {
			this.serviceIdentity = this.identityManager.fromJid("eliza@societies.org");
			this.requestor = new RequestorService(serviceIdentity, serviceId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void createNonInferrableAttribute(){
		try {
			Future<CtxAttribute> createAttribute = this.ctxBroker.createAttribute(this.cssPersonEntity.getId(), CtxAttributeTypes.NAME);
			
			nameAttribute = createAttribute.get();
			
			nameAttribute.setStringValue("Chuck Norris");
			
			this.ctxBroker.update(nameAttribute);
			Future<CtxAttribute> createAttribute2 = this.ctxBroker.createAttribute(this.cssPersonEntity.getId(), CtxAttributeTypes.NAME);
			
			nameAttribute2 = createAttribute2.get();
			
			nameAttribute2.setStringValue("Walker, Texas Ranger");
			this.ctxBroker.update(nameAttribute2);
			
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
