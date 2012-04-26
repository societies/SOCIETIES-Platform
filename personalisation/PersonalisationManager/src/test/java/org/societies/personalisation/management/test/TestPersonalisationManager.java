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
package org.societies.personalisation.management.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.decisionmaking.IDecisionMaker;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.societies.personalisation.management.impl.PersonalisationManager;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.springframework.scheduling.annotation.AsyncResult;




public class TestPersonalisationManager {

	ICtxBroker broker = Mockito.mock(ICtxBroker.class);
	IDecisionMaker userAgent = new MockDecisionMaker();
	IDIANNE dianne = Mockito.mock(IDIANNE.class);
	ICAUIPrediction cauiPrediction = Mockito.mock(ICAUIPrediction.class);
	ICRISTUserIntentPrediction cristPrediction = Mockito.mock(ICRISTUserIntentPrediction.class);
	IUserPreferenceConditionMonitor pcm = Mockito.mock(IUserPreferenceConditionMonitor.class);
	IIdentityManager idm = Mockito.mock(IIdentityManager.class);
	private CtxAttributeIdentifier ctxLocationAttributeId;
	private CtxEntityIdentifier ctxEntityId;
	private CtxEntity ctxEntity;
	private MyIdentity mockId;
	private CtxAttribute locationAttribute;
	private Future<List<IPreferenceOutcome>> prefOutcomes;
	private Future<List<IDIANNEOutcome>> dianneOutcomes;
	private Future<List<IUserIntentAction>> cauiOutcomes;
	private Future<List<CRISTUserAction>> cristOutcomes;
	private PersonalisationManager personalisationManager;
	private CtxChangeEvent ctxChangeEvent;
	
	
	@Before
	public void Setup(){
		mockId = new MyIdentity(IdentityType.CSS, "myId", "domain");
		ctxEntityId = new CtxEntityIdentifier(mockId.getJid(), "Person", new Long(1));
		ctxEntity = new CtxEntity(ctxEntityId);	
		ctxLocationAttributeId = new CtxAttributeIdentifier(ctxEntityId, "location", new Long(1));
		locationAttribute = new CtxAttribute(ctxLocationAttributeId);
		locationAttribute.setStringValue("home");
		setupOutcomes();
		ctxChangeEvent = new CtxChangeEvent(ctxLocationAttributeId);
		personalisationManager = new PersonalisationManager();
		personalisationManager.setCauiPrediction(cauiPrediction);
		personalisationManager.setCristPrediction(cristPrediction);
		personalisationManager.setCtxBroker(broker);
		personalisationManager.setDecisionMaker(userAgent);
		personalisationManager.setDianne(dianne);
		personalisationManager.setIdm(idm);
		personalisationManager.setPcm(pcm);
		
		personalisationManager.registerForContextUpdate(mockId, PersonalisationTypes.UserPreference, ctxLocationAttributeId);
		personalisationManager.registerForContextUpdate(mockId, PersonalisationTypes.DIANNE, ctxLocationAttributeId);
		personalisationManager.registerForContextUpdate(mockId, PersonalisationTypes.CAUIIntent, ctxLocationAttributeId);
		personalisationManager.registerForContextUpdate(mockId, PersonalisationTypes.CRISTIntent, ctxLocationAttributeId);
	}
	
	
	


	private void setupOutcomes() {
		/**
		 * setup dianne outcomes
		 */
		IDIANNEOutcome dOut = new MockDIANNEOutcome(this.getAServiceID("google", "youtube"), "media", "volume", "20", 56);
		IDIANNEOutcome dOut2 = new MockDIANNEOutcome(this.getAServiceID("google", "weather"), "news", "volume", "50", 90);
		ArrayList<IDIANNEOutcome> diannes = new ArrayList<IDIANNEOutcome>();
		diannes.add(dOut);
		diannes.add(dOut2);
		this.dianneOutcomes = new AsyncResult<List<IDIANNEOutcome>>(diannes);
		
		
		/**
		 * setup preference outcomes
		 */
		PreferenceOutcome pOut = new PreferenceOutcome(this.getAServiceID("google", "youtube"), "media", "volume", "20");
		pOut.setConfidenceLevel(60);
		PreferenceOutcome pOut2 = new PreferenceOutcome(this.getAServiceID("societies", "nearMe"), "connect", "bColour", "#F2F3F4");
		pOut.setConfidenceLevel(100);
		ArrayList<IPreferenceOutcome> prefs = new ArrayList<IPreferenceOutcome>();
		prefs.add(pOut);
		prefs.add(pOut2);
		this.prefOutcomes = new AsyncResult<List<IPreferenceOutcome>>(prefs);
		
		/**
		 * setup caui outcomes
		 */
		IUserIntentAction cauiOut = new UserIntentAction(this.getAServiceID("google", "youtube"), "media", "volume", "20", (long) 1);
		cauiOut.setConfidenceLevel(80);
		IUserIntentAction cauiOut2 = new UserIntentAction(this.getAServiceID("google", "nearMe"), "connect", "bColour", "#F2F3F4", (long) 2);
		cauiOut2.setConfidenceLevel(99);
		ArrayList<IUserIntentAction> cauis = new ArrayList<IUserIntentAction>();
		cauis.add(cauiOut);
		cauis.add(cauiOut2);
		this.cauiOutcomes = new AsyncResult<List<IUserIntentAction>>(cauis);
		
		
		/**
		 * setup crist outcomes
		 */
		CRISTUserAction cristOut = new CRISTUserAction();
		cristOut.setServiceID(this.getAServiceID("google", "youtube"));
		cristOut.setparameterName("volume");
		cristOut.setServiceType("media");
		cristOut.setvalue("80");
		cristOut.setConfidenceLevel(50);
		
		CRISTUserAction cristOut2 = new CRISTUserAction();
		cristOut2.setServiceID(this.getAServiceID("microsoft", "hotmail"));
		cristOut2.setServiceType("mail");
		cristOut2.setparameterName("accessType");
		cristOut2.setvalue("pop");
		cristOut2.setConfidenceLevel(99);
		
		ArrayList<CRISTUserAction> crists = new ArrayList<CRISTUserAction>();
		crists.add(cristOut);
		crists.add(cristOut2);
		this.cristOutcomes = new AsyncResult<List<CRISTUserAction>>(crists);
		
		
		
		
	}

	private ServiceResourceIdentifier getAServiceID(String css, String serviceName){
		ServiceResourceIdentifier sID = new ServiceResourceIdentifier();
		sID.setServiceInstanceIdentifier("css://"+css+"/"+serviceName);
		
		return sID;
	}

	@Test
	public void testContextEventReceivedNoConflicts(){
		try {
			Mockito.when(broker.lookup(CtxModelType.ATTRIBUTE, "dianneConfidenceLevel")).thenReturn(new AsyncResult(new ArrayList<CtxIdentifier>()));
			Mockito.when(broker.lookup(CtxModelType.ATTRIBUTE, "prefMgrConfidenceLevel")).thenReturn(new AsyncResult(new ArrayList<CtxIdentifier>()));
			Mockito.when(broker.lookup(CtxModelType.ATTRIBUTE, "cauiConfidenceLevel")).thenReturn(new AsyncResult(new ArrayList<CtxIdentifier>()));
			Mockito.when(broker.lookup(CtxModelType.ATTRIBUTE, "cristConfidenceLevel")).thenReturn(new AsyncResult(new ArrayList<CtxIdentifier>()));
			Mockito.when(broker.retrieve(ctxLocationAttributeId)).thenReturn(new AsyncResult(locationAttribute));
			Mockito.when(idm.fromJid(ctxLocationAttributeId.getOperatorId())).thenReturn(mockId);
			Mockito.when(pcm.getOutcome(mockId, locationAttribute)).thenReturn(prefOutcomes);
			Mockito.when(dianne.getOutcome(mockId, locationAttribute)).thenReturn(dianneOutcomes);
			Mockito.when(cauiPrediction.getPrediction(mockId, locationAttribute)).thenReturn(cauiOutcomes);
			Mockito.when(cristPrediction.getCRISTPrediction(mockId, locationAttribute)).thenReturn(cristOutcomes);
			personalisationManager.onModification(ctxChangeEvent);
			
			List<IOutcome> prefOutcomes = ((MockDecisionMaker) userAgent).getPreferences();
			List<IOutcome> intentOutcomes = ((MockDecisionMaker) userAgent).getIntent();
			
			for (IOutcome prefOutcome : prefOutcomes){
				System.out.println(prefOutcome.toString());
			}
			
			for (IOutcome intentOutcome : intentOutcomes){
				System.out.println(intentOutcome.toString());
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
