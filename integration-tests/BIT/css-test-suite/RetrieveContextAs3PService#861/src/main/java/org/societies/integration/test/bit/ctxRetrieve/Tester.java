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
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class Tester {
	
	private IIdentityManager identityManager;
	private ICtxBroker ctxBroker;
	private IIdentity userIdentity;
	private IndividualCtxEntity cssPersonEntity;
	private CtxAttribute nameAttribute;
	private CtxAttribute nameAttribute2;
	private Requestor requestor;
	private IIdentity serviceIdentity;
	private IPrivacyPreferenceManager privPrefMgr;
	private IHelloWorld helloWorld;
	
	public Tester(){
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
	}
	
	@Before
	public void setup(){
		identityManager = Test861.getIdentityManager();
		ctxBroker = Test861.getCtxBroker();
		privPrefMgr = Test861.getPrivPrefMgr();
		helloWorld = Test861.getHelloWorld();
		
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
		this.createPPNPreference1();
		this.createPPNPreference2();
	}
	
	@Test
	public void testRetrieve(){
			this.helloWorld.retrieveCtxAttribute(CtxAttributeTypes.NAME);
			this.helloWorld.displayUserLocation();
	
		
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
			
			Future<CtxAttribute> createAttribute2 = this.ctxBroker.createAttribute(this.cssPersonEntity.getId(), CtxAttributeTypes.NAME);
			
			nameAttribute2 = createAttribute2.get();
			
			nameAttribute2.setStringValue("Walker, Texas Ranger");
			
			
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
	
	
	private void createPPNPreference1(){
		
		Resource resource = new Resource(nameAttribute.getId());
		List<Action> actions = new ArrayList<Action>();
		actions.add(new Action(ActionConstants.CREATE));
		actions.add(new Action(ActionConstants.DELETE));
		actions.add(new Action(ActionConstants.READ));
		actions.add(new Action(ActionConstants.WRITE));
		List<Requestor> requestors = new ArrayList<Requestor>();
		requestors.add(requestor);
		RuleTarget target = new RuleTarget(requestors, resource, actions);
		List<Condition> conditions = new ArrayList<Condition>();
		try {
			PPNPOutcome outcome = new PPNPOutcome(PrivacyOutcomeConstants.ALLOW, target , conditions);
			IPrivacyPreference privacyPreference = new PrivacyPreference(outcome);
			PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(CtxAttributeTypes.NAME, privacyPreference);
			model.setRequestor(requestor);
			model.setAffectedCtxId(nameAttribute.getId());
			PPNPreferenceDetails details = new PPNPreferenceDetails(CtxAttributeTypes.NAME);
			details.setRequestor(requestor);
			this.privPrefMgr.storePPNPreference(details, privacyPreference);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	
	private void createPPNPreference2(){
		Resource resource2 = new Resource(nameAttribute2.getId());
		List<Action> actions2 = new ArrayList<Action>();
		actions2.add(new Action(ActionConstants.CREATE));
		actions2.add(new Action(ActionConstants.DELETE));
		actions2.add(new Action(ActionConstants.READ));
		actions2.add(new Action(ActionConstants.WRITE));
		List<Requestor> requestors2 = new ArrayList<Requestor>();
		requestors2.add(requestor);
		RuleTarget target2 = new RuleTarget(requestors2, resource2, actions2);
		List<Condition> conditions2 = new ArrayList<Condition>();
		try {
			PPNPOutcome outcome2 = new PPNPOutcome(PrivacyOutcomeConstants.BLOCK, target2 , conditions2);
			IPrivacyPreference privacyPreference2 = new PrivacyPreference(outcome2);
			PPNPrivacyPreferenceTreeModel model2 = new PPNPrivacyPreferenceTreeModel(CtxAttributeTypes.NAME, privacyPreference2);
			model2.setRequestor(requestor);
			model2.setAffectedCtxId(nameAttribute2.getId());
			PPNPreferenceDetails details2 = new PPNPreferenceDetails(CtxAttributeTypes.NAME);
			details2.setRequestor(requestor);
			this.privPrefMgr.storePPNPreference(details2, privacyPreference2);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
