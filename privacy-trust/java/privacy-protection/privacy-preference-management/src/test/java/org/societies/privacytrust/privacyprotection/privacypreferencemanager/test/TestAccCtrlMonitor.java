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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.test;

import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.INegotiationClient;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementEnvelopeUtils;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementUtils;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.monitoring.accessCtrl.AccCtrlMonitor;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Eliza
 *
 */
public class TestAccCtrlMonitor {

	private static final String ACC_CTRL_PREFERENCE_1 = "accCtrl_preference_1";
	private static final String ACC_CTRL_PREFERENCE_2_AUTO = "accCtrl_preference_2";
	private static final String ACC_CTRL_PREFERENCE_3_AUTO = "accCtrl_preference_3";
	private static final String ACC_CTRL_PREFERENCE_4_AUTO = "accCtrl_preference_4";
	private static final String ACC_CTRL_PREFERENCE_5_AUTO = "accCtrl_preference_5";
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICommManager commsMgr = Mockito.mock(ICommManager.class);
	private IIdentityManager idMgr = Mockito.mock(IIdentityManager.class);
	private ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
	private ITrustBroker trustBroker = Mockito.mock(ITrustBroker.class);
	private IUserFeedback userFeedback = Mockito.mock(IUserFeedback.class);
	private IPrivacyAgreementManager agreementMgr = Mockito.mock(IPrivacyAgreementManager.class);
	private IEventMgr eventManager = Mockito.mock(IEventMgr.class);
	private PrivacyPreferenceManager privPrefMgr;
	private ArrayList<Action> actions;
	private ArrayList<Condition> conditions;
	private Resource resourceWithoutID;
	private AccCtrlMonitor accCtrlMonitor;
	private RequestorCis requestorCis;
	private AgreementEnvelope envelope;
	private RequestorCisBean requestorCisBean;
	private RequestPolicy requestPolicy;
	private ArrayList<ResponseItem> responseItems;
	private MyIdentity userId;
	private CtxEntity userCtxEntity;
	private CtxAttribute locationAttribute;
	private CtxAttribute nameAttribute;
	private CtxAttribute ageAttribute;
	private CtxAttribute statusAttribute;
	private CtxAssociation hasPrivacyPreferences;
	private CtxEntity privacyPreferenceEntity;
	private CtxAttribute accCtrl_1_CtxAttribute;
	private CtxAttribute accCtrl_2_CtxAttribute;
	private CtxAttribute accCtrl_3_CtxAttribute;
	private CtxAttribute accCtrl_4_CtxAttribute;
	private CtxAttribute accCtrl_5_CtxAttribute;
	private CtxAttribute registryCtxAttribute;
	private AccessControlPreferenceDetailsBean accCtrlDetails;
	private Resource resourceWithID;
	private AccessControlPreferenceTreeModel accCtrlmodel;
	private Hashtable<String, Decision> results;
	private IPrivacyDataManagerInternalMock privacyDataManagerInternal;
	
	@Before
	public void setup(){
		this.results = new Hashtable<String, Decision>();
		results.put("home", Decision.PERMIT);
		results.put("work", Decision.DENY);
		this.privPrefMgr  = new PrivacyPreferenceManager();
		this.privPrefMgr.setCommsMgr(commsMgr);
		this.privPrefMgr.setIdMgr(idMgr);
		this.privPrefMgr.setCtxBroker(ctxBroker);
		privacyDataManagerInternal = new IPrivacyDataManagerInternalMock();
		this.privPrefMgr.setprivacyDataManagerInternal(privacyDataManagerInternal);
		this.privPrefMgr.setTrustBroker(trustBroker);
		this.privPrefMgr.setUserFeedback(userFeedback);
		this.privPrefMgr.setAgreementMgr(agreementMgr);
		this.privPrefMgr.setEventMgr(eventManager);
		this.setupRequestor();
		this.setupContext();
		this.setupPolicyDetails();
		this.setupPolicy();
		this.setupEnvelope();
		this.setupDetails();
		this.setupAccCtrlModel();
		this.mockCalls();
		privPrefMgr.initialisePrivacyPreferenceManager();
		accCtrlMonitor = privPrefMgr.getAccCtrlMonitor();

	}
	
	

	


	private void mockCalls() {
		try {
			/**
			 * identity
			 */
			Mockito.when(this.commsMgr.getIdManager()).thenReturn(this.idMgr);
			Mockito.when(this.privPrefMgr.getCommsMgr().getIdManager()).thenReturn(this.idMgr);
			Mockito.when(this.idMgr.fromJid(this.requestorCisBean.getRequestorId())).thenReturn(this.requestorCis.getRequestorId());
			Mockito.when(this.idMgr.fromJid(this.requestorCisBean.getCisRequestorId())).thenReturn(this.requestorCis.getCisRequestorId());
			Mockito.when(idMgr.getThisNetworkNode()).thenReturn((INetworkNode) userId);
			/**
			 * context
			 */
			Mockito.when(ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<List<CtxIdentifier>>(new ArrayList<CtxIdentifier>()));
			List<CtxIdentifier> preferenceEntityList = new ArrayList<CtxIdentifier>();
			preferenceEntityList.add(this.privacyPreferenceEntity.getId());
			Mockito.when(ctxBroker.lookup(CtxModelType.ENTITY, CtxTypes.PRIVACY_PREFERENCE)).thenReturn(new AsyncResult<List<CtxIdentifier>>(preferenceEntityList));
			Mockito.when(ctxBroker.createAttribute(privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_1)).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_1_CtxAttribute));
			Mockito.when(ctxBroker.updateAttribute(Mockito.eq(accCtrl_1_CtxAttribute.getId()), (Serializable) Mockito.anyObject())).thenReturn(new AsyncResult<CtxAttribute>(accCtrl_1_CtxAttribute));
			Mockito.when(ctxBroker.retrieve(accCtrl_1_CtxAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.accCtrl_1_CtxAttribute));
			IndividualCtxEntity weirdPerson = new IndividualCtxEntity(userCtxEntity.getId());
			Mockito.when(ctxBroker.retrieveIndividualEntity(this.userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(weirdPerson));
			Mockito.when(ctxBroker.createAttribute(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY)).thenReturn(new AsyncResult<CtxAttribute>(registryCtxAttribute));
			Mockito.when(ctxBroker.retrieve(this.locationAttribute.getId())).thenReturn(new AsyncResult<CtxModelObject>(this.locationAttribute));
			Mockito.when(agreementMgr.getAgreement(requestorCis)).thenReturn(AgreementEnvelopeUtils.toAgreementEnvelope(envelope, idMgr));
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}






	@Test
	public void testWithPreferences(){
		this.privPrefMgr.storeAccCtrlPreference(accCtrlDetails, accCtrlmodel);
		
		try {
			accCtrlMonitor.handleInternalEvent(this.createSuccessfulNegotiationEvent(this.envelope));
			CtxChangeEvent event = new CtxChangeEvent(this.locationAttribute.getId());
			this.privPrefMgr.getContextCache().getContextCacheUpdater().onModification(event);
			int i = 0; 
			while (this.privacyDataManagerInternal.receivedResponse.isEmpty() && i < 5){
				Thread.sleep(200);
				i++;
			}
			Assert.assertTrue(this.privacyDataManagerInternal.receivedResponse.get(1));
			this.locationAttribute.setStringValue("work");
			this.privPrefMgr.getContextCache().getContextCacheUpdater().onModification(event);
			
			i = 0; 
			while (this.privacyDataManagerInternal.receivedResponse.isEmpty() && i < 5){
				Thread.sleep(200);
				i++;
			}
			Assert.assertTrue(this.privacyDataManagerInternal.receivedResponse.get(2));
			this.locationAttribute.setStringValue("else");
			this.privPrefMgr.getContextCache().getContextCacheUpdater().onModification(event);
			
			i = 0; 
			while (this.privacyDataManagerInternal.receivedResponse.isEmpty() && i < 5){
				Thread.sleep(200);
				i++;
			}
			Assert.assertTrue(this.privacyDataManagerInternal.receivedResponse.get(4));
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Error sending internal event");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private InternalEvent createSuccessfulNegotiationEvent(
			AgreementEnvelope envelope) throws InvalidFormatException {
		NegotiationDetails details = new NegotiationDetails(requestorCis, 0);
		PPNegotiationEvent event = new PPNegotiationEvent(
				AgreementUtils.toAgreement(envelope.getAgreement(), this.idMgr),
				NegotiationStatus.SUCCESSFUL, details);
		InternalEvent iEvent = new InternalEvent(
				EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT, "",
				INegotiationClient.class.getName(), event);
		this.logging.debug("Successfull negotiation Event: \n" + "EventName: "
				+ iEvent.geteventName() + "\nEventSource:"
				+ iEvent.geteventSource() + "\nEventType"
				+ iEvent.geteventType() + "\nEventInfo:"
				+ iEvent.geteventInfo());
		return iEvent;
	}
	
	private void setupRequestor() {
		this.requestorCisBean = new RequestorCisBean();
		IIdentity requestorIdentity = new MyIdentity(IdentityType.CSS, "cisAdmin", "ict-societies.eu");
		
		this.requestorCisBean.setRequestorId(requestorIdentity.getJid());
		IIdentity cisIdentity = new MyIdentity(IdentityType.CIS, "myCis","ict-societies.eu");
		this.requestorCisBean.setCisRequestorId(cisIdentity.getJid());
		
		requestorCis = new RequestorCis(requestorIdentity, cisIdentity);
	}
	
	private void setupPolicyDetails() {
		this.actions = new ArrayList<Action>();
		Action read = new Action();
		read.setActionConstant(ActionConstants.READ);
		Action write = new Action();
		write.setActionConstant(ActionConstants.WRITE);
		Action create = new Action();
		create.setActionConstant(ActionConstants.CREATE);
		Action delete = new Action();
		delete.setActionConstant(ActionConstants.DELETE);
		
		this.actions.add(read);
		this.actions.add(create);
		this.actions.add(write);
		this.actions.add(delete);
		
		
		this.conditions = new ArrayList<Condition>();
		
		Condition shareWithCisAdmin = new Condition();
		shareWithCisAdmin.setConditionConstant(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY);
		shareWithCisAdmin.setValue("Yes");
		shareWithCisAdmin.setOptional(false);
		
		Condition shareWithCisMembers = new Condition();
		shareWithCisMembers.setConditionConstant(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY);
		shareWithCisMembers.setValue("Yes");
		shareWithCisMembers.setOptional(true);
		
		Condition dataRetentionHours = new Condition();
		dataRetentionHours.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
		dataRetentionHours.setValue("24");
		dataRetentionHours.setOptional(false);
		
		conditions.add(dataRetentionHours);
		conditions.add(shareWithCisMembers);
		conditions.add(shareWithCisAdmin);
		
		this.resourceWithoutID = new Resource();
		this.resourceWithoutID.setDataType(CtxAttributeTypes.LOCATION_SYMBOLIC);
		this.resourceWithoutID.setScheme(DataIdentifierScheme.CONTEXT);
		
		this.resourceWithID = new Resource();
		this.resourceWithID.setDataType(CtxAttributeTypes.LOCATION_SYMBOLIC);
		this.resourceWithID.setDataIdUri(this.locationAttribute.getId().getUri());
		this.resourceWithID.setScheme(DataIdentifierScheme.CONTEXT);
	}
	
	private void setupPolicy() {
		this.setupPolicyDetails();
		this.requestPolicy = new RequestPolicy();
		
		this.requestPolicy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.CIS);
		
		List<String> dataTypes = new ArrayList<String>();
		dataTypes.add(CtxAttributeTypes.LOCATION_SYMBOLIC);
		dataTypes.add(CtxAttributeTypes.NAME);
		dataTypes.add(CtxAttributeTypes.AGE);
		dataTypes.add(CtxAttributeTypes.STATUS);
		dataTypes.add("activityfeed");
		List<RequestItem> requestItems = this.createRequestItems(dataTypes);
		this.requestPolicy.setRequestItems(requestItems);
		this.requestPolicy.setRequestor(requestorCisBean);
		
	}
	
	private List<RequestItem> createRequestItems(List<String> dataTypes){
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		for (String type : dataTypes){
			RequestItem item = new RequestItem();
			item.setActions(actions);
			item.setConditions(conditions);
			item.setOptional(false);
			Resource resource = new Resource();
			resource.setDataType(type);
			if (type.equalsIgnoreCase("activityfeed")){
				resource.setScheme(DataIdentifierScheme.CIS);
			}else{
				resource.setScheme(DataIdentifierScheme.CONTEXT);
			}
			item.setResource(resource);
			requestItems.add(item);
		}
		this.responseItems = new ArrayList<ResponseItem>();
		
		for (int i=0; i<requestItems.size(); i++){
			RequestItem item = requestItems.get(i);
			ResponseItem respItem = new ResponseItem();
			respItem.setRequestItem(item);
			
			if (i%2==0){
				respItem.setDecision(Decision.DENY);
			}else{
				respItem.setDecision(Decision.PERMIT);
			}
			this.responseItems.add(respItem);			
		}

		return requestItems;
	}
	
	private void setupEnvelope() {
		this.envelope = new AgreementEnvelope();
		Agreement agreement = new Agreement();
		
		agreement.setRequestedItems(this.responseItems);
		agreement.setRequestor(requestorCisBean);
		
		this.envelope.setAgreement(agreement);
		KeyPair keypair = generateKeys();

		try {
			this.envelope.setPublicKey(SerialisationHelper.serialise(keypair.getPublic()));
			this.envelope.setSignature(signAgreement(agreement, keypair));
			org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope envelopeObj = new org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope(AgreementUtils.toAgreement(agreement, idMgr), envelope.getPublicKey(), envelope.getSignature());
			//JOptionPane.showMessageDialog(null, "bean: "+envelope.getPublicKey()+" / obj: "+envelopeObj.getPublicKeyInBytes());
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private KeyPair generateKeys(){
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");

			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			kpg.initialize(1024,random);
			return kpg.generateKeyPair();

		} 
		catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] signAgreement(Agreement agreement, KeyPair keypair){

		this.generateKeys();
		if (keypair==null){
			this.logging.debug("Unable to generate Keys for signing the Agreement object");
			return null;
		}

		try {
			Signature dsa = Signature.getInstance("SHA1withDSA");
			dsa.initSign(keypair.getPrivate());
			byte[] byteArray = this.getBytes(agreement);

			dsa.update(byteArray);
			return dsa.sign();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private byte[] getBytes(Object obj) throws java.io.IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos = new ObjectOutputStream(bos); 
		oos.writeObject(obj);
		oos.flush(); 
		oos.close(); 
		bos.close();
		byte [] data = bos.toByteArray();
		return data;
	}
	private void setupContext() {
		this.userId = new MyIdentity(IdentityType.CSS, "xcmanager","societies.local");
		CtxEntityIdentifier ctxId = new CtxEntityIdentifier(userId.getJid(), "Person", new Long(1));
		this.userCtxEntity = new CtxEntity(ctxId);
		CtxAttributeIdentifier id = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(1));
		this.locationAttribute = new CtxAttribute(id);
		this.locationAttribute.setStringValue("home");
		this.locationAttribute.setValueType(CtxAttributeValueType.STRING);
		this.locationAttribute.getQuality().setOriginType(CtxOriginType.SENSED);
		
		CtxAttributeIdentifier nameId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.NAME, new Long(1));
		this.nameAttribute = new CtxAttribute(nameId);
		this.nameAttribute.setValueType(CtxAttributeValueType.STRING);
		this.nameAttribute.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		
		CtxAttributeIdentifier ageId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.AGE, new Long(1));
		this.ageAttribute = new CtxAttribute(ageId);
		this.ageAttribute.setValueType(CtxAttributeValueType.STRING);
		this.ageAttribute.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		
		CtxAttributeIdentifier statusId = new CtxAttributeIdentifier(userCtxEntity.getId(), CtxAttributeTypes.STATUS, new Long(1));
		this.statusAttribute = new CtxAttribute(statusId);
		this.statusAttribute.setValueType(CtxAttributeValueType.STRING);
		this.statusAttribute.getQuality().setOriginType(CtxOriginType.INFERRED);
		
		
		hasPrivacyPreferences = new CtxAssociation(new CtxAssociationIdentifier(userId.getJid(), CtxTypes.HAS_PRIVACY_PREFERENCES, new Long(3)));
		CtxEntityIdentifier preferenceEntityId_ = new CtxEntityIdentifier(userId.getJid(), CtxTypes.PRIVACY_PREFERENCE, new Long(2));

		this.privacyPreferenceEntity = new CtxEntity(preferenceEntityId_);
		
		this.accCtrl_1_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_1, new Long(5)));
		this.accCtrl_2_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_2_AUTO, new Long(5)));
		this.accCtrl_3_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_3_AUTO, new Long(5)));
		this.accCtrl_4_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_4_AUTO, new Long(5)));
		this.accCtrl_5_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_5_AUTO, new Long(5)));
		this.registryCtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(userCtxEntity.getId(), CtxTypes.PRIVACY_PREFERENCE_REGISTRY, new Long(1)));
		//this.accCtrl_2_CtxAttribute = new CtxAttribute(new CtxAttributeIdentifier(this.privacyPreferenceEntity.getId(), ACC_CTRL_PREFERENCE_2, new Long(6)));
	}
	
	private  void setupDetails(){
		
		Action action = new Action();
		action.setActionConstant(ActionConstants.READ);
		this.accCtrlDetails  = new AccessControlPreferenceDetailsBean();
		this.accCtrlDetails.setAction(action);
		this.accCtrlDetails.setRequestor(requestorCisBean);
		this.accCtrlDetails.setResource(resourceWithID);
		
	}
	
	private void setupAccCtrlModel() {
		AccessControlOutcome outcomeAllow  = new AccessControlOutcome(PrivacyOutcomeConstantsBean.ALLOW);
		AccessControlOutcome outcomeBlock = new AccessControlOutcome(PrivacyOutcomeConstantsBean.BLOCK);
		
		IPrivacyPreference preferenceAllow = new PrivacyPreference(outcomeAllow);
		IPrivacyPreference preferenceBlock = new PrivacyPreference(outcomeBlock);
		
		
		for (Condition cond : conditions){
			IPrivacyPreference conditionPreference = new PrivacyPreference(new PrivacyCondition(cond));
			conditionPreference.add(preferenceAllow);
			preferenceAllow = conditionPreference;
		}
		
		
		for (Condition cond : conditions){
			Condition copiedCondition = new Condition();
			copiedCondition.setConditionConstant(cond.getConditionConstant());
			copiedCondition.setOptional(cond.isOptional());
			copiedCondition.setValue(cond.getValue());
			IPrivacyPreference conditionPreference = new PrivacyPreference(new PrivacyCondition(copiedCondition));
/*			if (cond.getConditionConstant().equals(ConditionConstants.DATA_RETENTION_IN_HOURS)){
				((PrivacyCondition) conditionPreference.getCondition()).getCondition().setValue("12");
			}else{
				((PrivacyCondition) conditionPreference.getCondition()).getCondition().setValue("No");
			}*/
			conditionPreference.add(preferenceBlock);
			preferenceBlock = conditionPreference;
		}
		
		
		
		
		ContextPreferenceCondition contextPreferenceConditionHome = new ContextPreferenceCondition(locationAttribute.getId(), OperatorConstants.EQUALS, "home");
		ContextPreferenceCondition contextPreferenceConditionWork = new ContextPreferenceCondition(locationAttribute.getId(), OperatorConstants.EQUALS, "work");
		
		IPrivacyPreference contextPrivacyPreferenceHome = new PrivacyPreference(contextPreferenceConditionHome);
		contextPrivacyPreferenceHome.add(preferenceAllow);
		
		IPrivacyPreference contextPrivacyPreferenceWork = new PrivacyPreference(contextPreferenceConditionWork);
		contextPrivacyPreferenceWork.add(preferenceBlock);
		
		
		IPrivacyPreference privacyPreference = new PrivacyPreference();
		privacyPreference.add(contextPrivacyPreferenceWork);
		privacyPreference.add(contextPrivacyPreferenceHome);
		System.out.println(((PrivacyPreference) privacyPreference).toString());
		//JOptionPane.showMessageDialog(null, ((PrivacyPreference) privacyPreference).toString());
		this.accCtrlmodel = new AccessControlPreferenceTreeModel(accCtrlDetails, privacyPreference);
		
	}
	
	public class IPrivacyDataManagerInternalMock implements IPrivacyDataManagerInternal{

		private Hashtable<Integer, Boolean> receivedResponse;
		public IPrivacyDataManagerInternalMock(){
			receivedResponse = new Hashtable<Integer, Boolean>();
		}
		@Override
		public List<org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem> getPermissions(
				Requestor requestor, DataIdentifier dataId)
				throws PrivacyException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<ResponseItem> getPermissions(
				Requestor requestor,
				DataIdentifier dataId,
				List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions)
				throws PrivacyException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean updatePermission(
				Requestor requestor,
				DataIdentifier dataId,
				List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions,
				org.societies.api.privacytrust.privacy.model.privacypolicy.Decision permission)
				throws PrivacyException {
			//JOptionPane.showMessageDialog(null, "Updated permission - detail\npermission: "+permission+"\nsymloc value: "+locationAttribute.getStringValue());
			
			Assert.assertEquals(permission.toString().toLowerCase(), results.get(locationAttribute.getStringValue()).toString().toLowerCase());
			this.receivedResponse.clear();
			if(locationAttribute.getStringValue().equals("home")){
				receivedResponse.put(1, true);
			}else if (locationAttribute.getStringValue().equals("work")){
				receivedResponse.put(2, true);
			}
				
			
			return false;
		}

		@Override
		public boolean updatePermission(
				Requestor requestor,
				org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem permission)
				throws PrivacyException {
			//JOptionPane.showMessageDialog(null, "Updated permission- responseItem\npermission: "+permission.getDecision()+"\nsymloc value: "+locationAttribute.getStringValue());
			Assert.assertEquals(permission.toString().toLowerCase(), results.get(locationAttribute.getStringValue()).toString().toLowerCase());
			this.receivedResponse.clear();
			if(locationAttribute.getStringValue().equals("home")){
				receivedResponse.put(1, true);
			}else if (locationAttribute.getStringValue().equals("work")){
				receivedResponse.put(2, true);
			}
			return false;
		}

		@Override
		public boolean deletePermissions(Requestor requestor,
				DataIdentifier dataId) throws PrivacyException {
			//JOptionPane.showMessageDialog(null, "Deleted permission - req/dataId" );
			
			this.receivedResponse.clear();
			receivedResponse.put(3, true);
			
			return false;
		}

		@Override
		public boolean deletePermission(
				Requestor requestor,
				DataIdentifier dataId,
				List<org.societies.api.privacytrust.privacy.model.privacypolicy.Action> actions)
				throws PrivacyException {
			//JOptionPane.showMessageDialog(null, "Deleted permission - req/dataId/actions" );
			this.receivedResponse.clear();
			receivedResponse.put(4, true);
			return false;
		}
		@Override
		public boolean updatePermissions(Requestor requestor,
				List<ResponseItem> permissions) throws PrivacyException {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public List<ResponseItem> getPermissions(RequestorBean requestor,
				List<DataIdentifier> dataIds, List<Action> actions)
				throws PrivacyException {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public List<ResponseItem> getPermissions(RequestorBean requestor,
				DataIdentifier dataId, List<Action> actions)
				throws PrivacyException {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public List<ResponseItem> getPermissions(RequestorBean requestor,
				DataIdentifier dataId) throws PrivacyException {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public boolean updatePermission(RequestorBean requestor,
				DataIdentifier dataId, List<Action> actions, Decision permission)
				throws PrivacyException {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean deletePermissions(RequestorBean requestor,
				DataIdentifier dataId) throws PrivacyException {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean deletePermissions(RequestorBean requestor,
				DataIdentifier dataId, List<Action> actions)
				throws PrivacyException {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean updatePermissions(RequestorBean requestor,
				List<DataIdentifier> dataIds, List<Action> actions,
				List<Decision> decisions) throws PrivacyException {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean updatePermission(RequestorBean requestor,
				ResponseItem permission) throws PrivacyException {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean updatePermissions(RequestorBean requestor,
				List<ResponseItem> permissions) throws PrivacyException {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean updatePermissions(RequestorBean requestor,
				List<DataIdentifier> dataIds, List<Action> actions,
				Decision decision) throws PrivacyException {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
