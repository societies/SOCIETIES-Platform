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

package org.societies.context.exampleRemote.broker;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
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
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/*
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author NikosK
 *
 */
@Service
public class ContextAware3pService implements IContextAware3pService{


	private Logger LOG = LoggerFactory.getLogger(this.getClass());

	//services
	private ICommManager commsMgr;
	private IIdentityManager idMgr;
	private ICtxBroker ctxBroker;
	//private IPrivacyPreferenceManager privPrefMgr;

	// identities
	//private RequestorService requestorService;
	private Requestor simpleRequestor;
	//private IIdentity userIdentity;
	private IIdentity serviceIdentity;
	private ServiceResourceIdentifier myServiceID;
	IIdentity johnID;
	

	@Autowired(required=true)
	public ContextAware3pService( ICtxBroker ctxBroker, ICommManager commsMgr){
		
		LOG.info("*** ContextAware3pRemoteService started");

		//services
		this.ctxBroker = ctxBroker;
		this.commsMgr = commsMgr;
		this.idMgr = commsMgr.getIdManager();
		
		LOG.info("ctxBroker: "+this.ctxBroker);
		LOG.info("commsMgr : "+this.commsMgr );
		LOG.info("idMgr : "+this.idMgr );
	
		//identities
		/*
		this.userIdentity = this.idMgr.getThisNetworkNode();
		try {
			this.serviceIdentity = this.idMgr.fromJid("john@societies.org");
			johnID = this.idMgr.fromJid("john@societies.org");
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		myServiceID = new ServiceResourceIdentifier();
		myServiceID.setServiceInstanceIdentifier("css://john@societies.org/ContextAware3pService");
		try {
			myServiceID.setIdentifier(new URI("css://john@societies.org/ContextAware3pService"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//requestorService = new RequestorService(serviceIdentity, myServiceID);
		*/
		simpleRequestor = new Requestor(johnID);
		//LOG.info("userIdentity : "+ userIdentity.getBareJid());
		//LOG.info("requestor service : "+requestorService);
	}


	
	
	@Override
	public CtxAttribute  retrieveRemoteCtxAttribute(IIdentity targetId, CtxAttributeIdentifier attrID){
		
		CtxAttribute result = null;
	/*	
		try {
			result = (CtxAttribute) this.ctxBroker.retrieve(requestorService, attrID).get();
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
		*/
		return result;
	}

	
	@Override
	public List<CtxAttribute> lookupRemoteCtxAttribute(IIdentity targetId, String ctxAttrType){
		List<CtxAttribute> results = null; 
		try {
			//List<CtxIdentifier> attridList = this.ctxBroker.lookup(requestorService, targetId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.BOOKS).get();
			//List<CtxIdentifier> attridList = this.ctxBroker.lookup(requestorService, targetId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.BOOKS).get();
			LOG.info("simple requestor "+ simpleRequestor.getRequestorId().getJid());
			LOG.info("targetId "+ targetId.getJid());
			
			CtxEntity remoteEntity = this.ctxBroker.createEntity(simpleRequestor, targetId, "service").get();
			LOG.info("remote entity"+ remoteEntity);
			
			CtxAttribute remoteAttribute = this.ctxBroker.createAttribute(simpleRequestor, remoteEntity.getId(), "home").get();
				
			LOG.info("remoteAttribute"+ remoteAttribute);
			
			
			List<CtxIdentifier> attridList = this.ctxBroker.lookup(simpleRequestor, targetId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.BOOKS).get();
			
			LOG.info("remote lookup results size "+ attridList.size());
		
			for(CtxIdentifier id : attridList ){
				
				
				LOG.info("remote lookup results id "+ id);
				
			}
			
		
		
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
		return results;
	}
	
	
	
	
	
	
	
}