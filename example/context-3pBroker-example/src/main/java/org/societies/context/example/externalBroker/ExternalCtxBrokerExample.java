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
package org.societies.context.example.externalBroker;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeBond;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueMetrics;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBondOriginType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class ExternalCtxBrokerExample 	{

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(ExternalCtxBrokerExample.class);

	/** The 3P Context Broker service reference. */
	private ICtxBroker externalCtxBroker;
	private ICommManager commMgrService;
	private org.societies.api.internal.context.broker.ICtxBroker internalCtxBroker;
	private IIdentity cssOwnerId;
	private INetworkNode cssNodeId;
	private ICisOwned cisOwned  = null;
	private IIdentity cisID;


	//	private IIdentity cisID;
	private IIdentity cssID1; 
	private IIdentity cssID2;
	private IIdentity cssID3;

	private IndividualCtxEntity indiEnt1;
	private IndividualCtxEntity indiEnt2;
	private IndividualCtxEntity indiEnt3;

	private static IndividualCtxEntity owner = null;
	private static CtxEntity deviceCtxEntity;
	private static CtxAssociation usesServiceAssoc;

	private static CtxAttributeIdentifier weightAttrIdentifier = null;
	private static CtxAttributeIdentifier ctxAttributeDeviceIDIdentifier = null;

	private Requestor requestor = null;
	private IIdentity remoteTargetCss;

	private IContextAware3pService ca3pService;


	@Autowired(required=true)
	public ExternalCtxBrokerExample(ICtxBroker externalCtxBroker,org.societies.api.internal.context.broker.ICtxBroker internalCtxBroker , ICommManager commMgr,ICisManager cisManager, IContextAware3pService ca3pService) throws InvalidFormatException {

		LOG.info("*** " + this.getClass() + " instantiated");

		LOG.info("*** ca3pService : " + this.ca3pService + " instantiated");

		this.externalCtxBroker = externalCtxBroker;
		this.internalCtxBroker = internalCtxBroker;
		this.commMgrService = commMgr;
		this.ca3pService = ca3pService;


		this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
		LOG.info("*** cssNodeId = " + this.cssNodeId);

		final String cssOwnerStr = this.cssNodeId.getBareJid();
		this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
		LOG.info("*** cssOwnerId = " + this.cssOwnerId);

		this.requestor = new Requestor(this.cssOwnerId);
		LOG.info("*** requestor = " + this.requestor);

		Hashtable<String,MembershipCriteria> cisCriteria = new Hashtable<String,MembershipCriteria>();
		try {
			cisOwned = cisManager.createCis("testCIS", "cisType", cisCriteria, "nice CIS").get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("*** cisOwned " +cisOwned);
		LOG.info("*** cisOwned.getCisId() " +cisOwned.getCisId());
		String cisIDString  = cisOwned.getCisId();

		this.cisID = commMgr.getIdManager().fromJid(cisIDString);

		LOG.info("*** Starting individual context examples...");

		ca3pService.retrieveIndividualEntityId();
		ca3pService.createCtxEntityWithCtxAttributes();
		
		//access control is not working for entities yet
		//ca3pService.retrieveCtxAttributeBasedOnEntity();

		ca3pService.lookupAndRetrieveCtxAttributes();


		LOG.info("*** Starting community context examples...");
		// creation of communities is only allowed by platform services
		this.createCommunityCtxEnt();
		
		ca3pService.retrievceLookupCommunityEntAttributes(this.cisID);
	}


	private void createCommunityCtxEnt(){

		CommunityCtxEntity communityEntity;
		try {
			communityEntity = internalCtxBroker.createCommunityEntity(this.cisID).get();
			this.cssID1 =  this.commMgrService.getIdManager().fromJid("Aris@societies.local ");
			this.indiEnt1 = this.internalCtxBroker.createIndividualEntity(this.cssID1, CtxEntityTypes.PERSON).get();
			CtxAttribute individualAttr1 = this.internalCtxBroker.createAttribute(this.indiEnt1.getId() , CtxAttributeTypes.INTERESTS).get();
			individualAttr1.setStringValue("reading,socialnetworking,cinema,sports");
			this.internalCtxBroker.update(individualAttr1);

			this.cssID2 =  this.commMgrService.getIdManager().fromJid("Babis@societies.local ");
			this.indiEnt2 = this.internalCtxBroker.createIndividualEntity(this.cssID2, CtxEntityTypes.PERSON).get();
			CtxAttribute individualAttr2 = this.internalCtxBroker.createAttribute(this.indiEnt2.getId() , CtxAttributeTypes.INTERESTS).get();
			individualAttr2.setStringValue("cooking,horseRiding,restaurants,cinema");

			this.cssID3 =  this.commMgrService.getIdManager().fromJid("Chrisa@societies.local ");
			this.indiEnt3 = this.internalCtxBroker.createIndividualEntity(this.cssID3, CtxEntityTypes.PERSON).get();
			CtxAttribute individualAttr3 = this.internalCtxBroker.createAttribute(this.indiEnt3.getId() , CtxAttributeTypes.INTERESTS).get();
			individualAttr3.setStringValue("cooking,horseRiding,socialnetworking,restaurants,cinema");

			communityEntity.addMember(this.indiEnt1.getId());
			communityEntity.addMember(this.indiEnt2.getId());
			communityEntity.addMember(this.indiEnt3.getId());

			CtxAttributeBond attributeLocationBond = new CtxAttributeBond(CtxAttributeTypes.LOCATION_SYMBOLIC, CtxBondOriginType.MANUALLY_SET);
			attributeLocationBond.setMinValue("home");
			attributeLocationBond.setMaxValue("home");
			attributeLocationBond.setValueType(CtxAttributeValueType.STRING);
			LOG.info("locationBond created : " + attributeLocationBond.toString());
			CtxAttributeBond attributeAgeBond = new CtxAttributeBond(CtxAttributeTypes.WEIGHT, CtxBondOriginType.MANUALLY_SET);

			attributeLocationBond.setValueType(CtxAttributeValueType.INTEGER);
			attributeAgeBond.setMinValue(new Integer(18));
			attributeAgeBond.setMinValue(new Integer(20));

			communityEntity.addBond(attributeLocationBond);
			communityEntity.addBond(attributeAgeBond);


			this.internalCtxBroker.update(communityEntity);

			CtxAttribute ctxAttr = this.internalCtxBroker.createAttribute(communityEntity.getId(),CtxAttributeTypes.INTERESTS).get();
			ctxAttr.setStringValue("reading,socialnetworking,sports,cooking,horseRiding,cinema,restaurants");
			ctxAttr.setValueType(CtxAttributeValueType.STRING);
			ctxAttr.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);

			this.internalCtxBroker.update(ctxAttr).get();


		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	


	
	
	

	
	
}