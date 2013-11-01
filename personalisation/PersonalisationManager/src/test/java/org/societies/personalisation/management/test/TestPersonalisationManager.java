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
import java.util.UUID;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
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
import org.societies.api.schema.useragent.decisionmaking.DecisionMakingBean;
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
	IDecisionMaker decisionMaker = Mockito.mock(IDecisionMaker.class);
	IDIANNE dianne = Mockito.mock(IDIANNE.class);
	ICAUIPrediction cauiPrediction = Mockito.mock(ICAUIPrediction.class);
	ICRISTUserIntentPrediction cristPrediction = Mockito.mock(ICRISTUserIntentPrediction.class);
	IUserPreferenceConditionMonitor pcm = Mockito.mock(IUserPreferenceConditionMonitor.class);
	IIdentityManager idm = Mockito.mock(IIdentityManager.class);
	ICommManager commsMgr = Mockito.mock(ICommManager.class);
	private CtxAttributeIdentifier ctxLocationAttributeId;
	private CtxEntityIdentifier ctxEntityId;
	private CtxEntity ctxEntity;
	private MyIdentity mockId;
	private CtxAttribute locationAttribute;
	private List<IPreferenceOutcome> prefOutcomes = new ArrayList<IPreferenceOutcome>();
	private List<IDIANNEOutcome> dianneOutcomes = new ArrayList<IDIANNEOutcome>();
	private List<IUserIntentAction> cauiOutcomes = new ArrayList<IUserIntentAction>();
	private List<CRISTUserAction> cristOutcomes = new ArrayList<CRISTUserAction>();
	private PersonalisationManager personalisationManager;
	private CtxChangeEvent ctxChangeEvent;
	private String uuid = UUID.randomUUID().toString();
	
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
		personalisationManager.setDecisionMaker(decisionMaker);
		personalisationManager.setDianne(dianne);
		//instead of: personalisationManager.setIdm(idm);
		Mockito.when(commsMgr.getIdManager()).thenReturn(idm);
		personalisationManager.setCommsMgr(commsMgr);
		try {
			Mockito.when(idm.fromJid(Mockito.anyString())).thenReturn(mockId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		IDIANNEOutcome dOut = new MockDIANNEOutcome(this.getAServiceID("google", "youtube"), "media", "playback", "friends", 56);
		IDIANNEOutcome dOut2 = new MockDIANNEOutcome(this.getAServiceID("google", "weather"), "news", "volume", "50", 90);
	
		dianneOutcomes.add(dOut);
		showOutcomes("DIANNE", dOut);
		dianneOutcomes.add(dOut2);
		showOutcomes("DIANNE", dOut2);
		
		
		/**
		 * setup preference outcomes
		 */
		PreferenceOutcome pOut = new PreferenceOutcome(this.getAServiceID("google", "youtube"), "media", "volume", "20");
		pOut.setConfidenceLevel(60);
		PreferenceOutcome pOut2 = new PreferenceOutcome(this.getAServiceID("societies", "nearMe"), "connect", "bColour", "#F2F3F4");
		pOut.setConfidenceLevel(100);
		prefOutcomes.add(pOut);
		showOutcomes("PREF" , pOut);
		prefOutcomes.add(pOut2);
		showOutcomes("PREF" , pOut2);	
		/**
		 * setup caui outcomes
		 */
		IUserIntentAction cauiOut = new UserIntentAction(this.getAServiceID("google", "youtube"), "media", "volume", "20", (long) 1);
		cauiOut.setConfidenceLevel(80);
		IUserIntentAction cauiOut2 = new UserIntentAction(this.getAServiceID("google", "nearMe"), "connect", "bColour", "#F2F3F4", (long) 2);
		cauiOut2.setConfidenceLevel(99);

		cauiOutcomes.add(cauiOut);
		showOutcomes("CAUI", cauiOut);
		cauiOutcomes.add(cauiOut2);
		showOutcomes("CAUI", cauiOut2);
		
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
		

		cristOutcomes.add(cristOut);
		showOutcomes("CRIST", cristOut);
		cristOutcomes.add(cristOut2);
		showOutcomes("CRIST", cristOut2);
		
		
		
		
	}
	
	private void showOutcomes(String type, IOutcome outcome){
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Type: "+type+"\n");
		strBuilder.append("Parameter: "+outcome.getparameterName()+"\n");
		strBuilder.append("Value: "+outcome.getvalue()+"\n");
		strBuilder.append("isContextDependent: "+outcome.isContextDependent()+"\n");
		strBuilder.append("isImplementable:" +outcome.isImplementable()+"\n");
		strBuilder.append("isProactive: "+outcome.isProactive()+"\n");
		//JOptionPane.showMessageDialog(null, strBuilder);
		System.out.println(strBuilder);
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
			Mockito.when(pcm.getOutcome(Mockito.eq(mockId), Mockito.eq(locationAttribute), Mockito.anyString())).thenReturn(new AsyncResult<List<IPreferenceOutcome>>(prefOutcomes));
			Mockito.when(dianne.getOutcome(mockId, locationAttribute)).thenReturn(new AsyncResult<List<IDIANNEOutcome>>(dianneOutcomes));
			Mockito.when(cauiPrediction.getPrediction(mockId, locationAttribute)).thenReturn(new AsyncResult<List<IUserIntentAction>>(cauiOutcomes));
			Mockito.when(cristPrediction.getCRISTPrediction(mockId, locationAttribute)).thenReturn(new AsyncResult<List<CRISTUserAction>>(cristOutcomes));
			personalisationManager.onModification(ctxChangeEvent);
			
			ArgumentCaptor<List<IOutcome>> intentArguments = ArgumentCaptor.forClass((Class<List<IOutcome>>)(Class)List.class);
			ArgumentCaptor<List<IOutcome>> preferenceArguments = ArgumentCaptor.forClass((Class<List<IOutcome>>)(Class)List.class);

			//waiting for the thread to finish:
			Thread.sleep(200);
			Mockito.verify(decisionMaker).makeDecision(intentArguments.capture(), preferenceArguments.capture(), Mockito.anyString());
			
			
			List<IOutcome> prefOutcomes = preferenceArguments.getValue();
			List<IOutcome> intentOutcomes = intentArguments.getValue();
			
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
