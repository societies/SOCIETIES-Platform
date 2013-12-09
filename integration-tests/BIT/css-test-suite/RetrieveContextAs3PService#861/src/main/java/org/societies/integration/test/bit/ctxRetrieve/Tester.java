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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.swing.UIManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
/*import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;*/

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class Tester {
	
	private static final String DEFAULT_FIRST_NAME_VALUE = "Chuck";// Norris"; 
	private static final String DEFAULT_LAST_NAME_VALUE = "Norris";
	private IIdentityManager identityManager;
	private ICtxBroker ctxBroker;
	private IIdentity userIdentity;
	private IndividualCtxEntity cssPersonEntity;
	private CtxAttribute nameAttribute;
	private CtxAttribute symLocAtt;
	
	/** 
	 * The name attribute value, i.e. either {@link #DEFAULT_NAME_VALUE} or the
	 * String value of an existing name attribute. 
	 */
	private String nameAttributeValue;
	private String symLocAttValue;
	private Requestor requestor;
	private IIdentity serviceIdentity;
	//private IPrivacyPreferenceManager privPrefMgr;
	private IHelloWorld helloWorld;
	
	/** The identifiers of the context model objects created in this test. */
	private Set<CtxIdentifier> testCtxIds = new HashSet<CtxIdentifier>(); 
	
	public Tester(){
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
	}
	
	@Before
	public void setup() throws Exception {
		identityManager = Test861.getIdentityManager();
		ctxBroker = Test861.getCtxBroker();
		//privPrefMgr = Test861.getPrivPrefMgr();
		helloWorld = Test861.getHelloWorld();
		
		userIdentity = identityManager.getThisNetworkNode();
		
		Future<IndividualCtxEntity> retrieveIndividualEntity = ctxBroker.retrieveIndividualEntity(userIdentity);
		this.cssPersonEntity = retrieveIndividualEntity.get();
		
		this.setupRequestor();
		this.createOrRetrieveNonInferrableAttribute();
		//this.createPPNPreference1();
		//this.createPPNPreference2();
	}
	
	/*@After
	public void tearDown() throws Exception {
		
		for (final CtxIdentifier ctxId : this.testCtxIds) {
			this.ctxBroker.remove(ctxId);
		}
	}*/
	
	@Test
	public void testRetrieve(){	
		List<String> types = new ArrayList<String>();
		types.add(CtxAttributeTypes.NAME);
		types.add(CtxAttributeTypes.LOCATION_COORDINATES);
			this.helloWorld.retrieveCtxAttribute(types);
			this.helloWorld.displayUserLocation();
	
		
	}
	
	private void setupRequestor() {
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		
		try {
			this.serviceIdentity = this.identityManager.fromJid("test.societies.local.macs.hw.ac.uk");
			this.requestor = new RequestorService(serviceIdentity, serviceId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void createOrRetrieveNonInferrableAttribute() throws Exception {
	
		final Set<CtxAttribute> nameAttrs = 
				this.cssPersonEntity.getAttributes(CtxAttributeTypes.NAME); 
		if (nameAttrs.iterator().hasNext()) {
			this.nameAttribute = nameAttrs.iterator().next();
		} else {
			Future<CtxAttribute> createAttribute = this.ctxBroker.createAttribute(
					this.cssPersonEntity.getId(), CtxAttributeTypes.NAME);
			this.nameAttribute = createAttribute.get();
			this.nameAttribute.setStringValue(DEFAULT_FIRST_NAME_VALUE.concat(" ").concat(DEFAULT_LAST_NAME_VALUE));
			this.nameAttribute = (CtxAttribute) this.ctxBroker.update(
					this.nameAttribute).get();
			// Add to set of context data items to be removed in {@link #tearDown}
			this.testCtxIds.add(this.nameAttribute.getId());
		}
		this.nameAttributeValue = nameAttribute.getStringValue();
		/*final Set<CtxAttribute> nameLastAttrs = 
				this.cssPersonEntity.getAttributes(CtxAttributeTypes.NAME_LAST); 
		if (nameLastAttrs.iterator().hasNext()) {
			this.nameAttribute = nameLastAttrs.iterator().next();
		} else {
			Future<CtxAttribute> createAttribute = this.ctxBroker.createAttribute(
					this.cssPersonEntity.getId(), CtxAttributeTypes.NAME_LAST);
			this.nameAttribute = createAttribute.get();
			this.nameAttribute.setStringValue(DEFAULT_LAST_NAME_VALUE);
			this.nameAttribute = (CtxAttribute) this.ctxBroker.update(
					this.nameAttribute).get();
			// Add to set of context data items to be removed in {@link #tearDown}
			this.testCtxIds.add(this.nameAttribute.getId());
		}
		this.nameAttributeValue.concat(" " + nameAttribute.getStringValue());*/
		
		final Set<CtxAttribute> symLoc = 
				this.cssPersonEntity.getAttributes(CtxAttributeTypes.LOCATION_COORDINATES); 
		if (symLoc.iterator().hasNext()) {
			this.symLocAtt = symLoc.iterator().next();
		} else {
			Future<CtxAttribute> createAttribute = this.ctxBroker.createAttribute(
					this.cssPersonEntity.getId(), CtxAttributeTypes.LOCATION_COORDINATES);
			this.symLocAtt = createAttribute.get();
			this.symLocAtt.setStringValue("(2.3509, 48.8566, 542)");
			this.symLocAtt = (CtxAttribute) this.ctxBroker.update(
					this.symLocAtt).get();
			// Add to set of context data items to be removed in {@link #tearDown}
			this.testCtxIds.add(this.symLocAtt.getId());
		}
		this.symLocAttValue = symLocAtt.getStringValue();
	}
	
/*	private void createPPNPreference1(){
		
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
			model.setAffectedDataId(nameAttribute.getId());
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
			model2.setAffectedDataId(nameAttribute2.getId());
			PPNPreferenceDetails details2 = new PPNPreferenceDetails(CtxAttributeTypes.NAME);
			details2.setRequestor(requestor);
			this.privPrefMgr.storePPNPreference(details2, privacyPreference2);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}*/
}
