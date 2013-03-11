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

package org.societies.integration.test.bit.ctxSNDataMgmt;



import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.context.broker.ICtxBroker;
import org.springframework.scheduling.annotation.AsyncResult;


/**
 * 
 *
 * @author nikosk
 *
 */
public class Tester {

	private ICtxBroker externalCtxBroker;
	private ICommManager commMgr;
	private ISocialData socialData ; 

	private static Logger LOG = LoggerFactory.getLogger(Tester.class);

	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;

	private RequestorService requestorService = null;
	private IIdentity userIdentity = null;
	private IIdentity serviceIdentity = null;
	private ServiceResourceIdentifier myServiceID;


	CtxEntityIdentifier cssOwnerEntityId ;

	public Tester(){

	}

	@Before
	public void setUp(){

	}


	@Test
	public void Test(){

		this.externalCtxBroker = CtxSNDataMgmt.getCtxBroker();
		this.commMgr = CtxSNDataMgmt.getCommManager();
		this.socialData = CtxSNDataMgmt.getSocialData();

		LOG.info("*** " + this.getClass() + " instantiated");

		try {
			this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + this.cssNodeId);

			final String cssOwnerStr = this.cssNodeId.getBareJid();
			this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
			LOG.info("*** cssOwnerId = " + this.cssOwnerId);

			this.serviceIdentity = commMgr.getIdManager().fromJid("nikosk@societies.org");
			myServiceID = new ServiceResourceIdentifier();
			myServiceID.setServiceInstanceIdentifier("css://nikosk@societies.org/HelloEarth");
			myServiceID.setIdentifier(new URI("css://nikosk@societies.org/HelloEarth"));

		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.requestorService = new RequestorService(serviceIdentity, myServiceID);

		LOG.info("*** requestor service = " + this.requestorService);

		LOG.info("*** Starting examples...");


		// TODO createRemoteEntity();
		this.connectToSN();
	
		try {
			Thread.sleep(9000);
			
			this.retrieveSNData();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		
	}


	private void retrieveSNData(){


		CtxEntityIdentifier cssOwnerEntityId;

		try {
			cssOwnerEntityId = this.externalCtxBroker.retrieveIndividualEntityId(this.requestorService, this.cssOwnerId).get();
			LOG.info("*** Retrieved CSS owner context entity id " + cssOwnerEntityId);
			CtxEntity individualEntity = (CtxEntity) this.externalCtxBroker.retrieve(this.requestorService, cssOwnerEntityId).get();

			LOG.info("*** individualEntity " + individualEntity.getId());
			///LOG.info("*** individualEntity " + individualEntity.get);

			List<CtxEntityIdentifier> snEntIDList = this.externalCtxBroker.lookupEntities(this.requestorService, this.cssOwnerId, CtxEntityTypes.SOCIAL_NETWORK, CtxAttributeTypes.TYPE, "facebook", "facebook").get();
			LOG.info("*** individualEntity snEntIDList " + snEntIDList);


			if(!snEntIDList.isEmpty()){
				CtxEntity facebookEntity = (CtxEntity) this.externalCtxBroker.retrieve(this.requestorService, snEntIDList.get(0)).get();

				LOG.info("facebook entity retrieved "+facebookEntity);

				Set<CtxAttribute> attrBooksSet = facebookEntity.getAttributes(CtxAttributeTypes.BOOKS);

				if(!attrBooksSet.isEmpty()) {

					for(CtxAttribute attrBooks: attrBooksSet){
						LOG.info("facebook attribute books retrieved "+attrBooks.getStringValue());	
					}

				}

				Set<CtxAttribute> attrNameSet = facebookEntity.getAttributes(CtxAttributeTypes.NAME);
				if(!attrNameSet.isEmpty()) {
					for(CtxAttribute attrName: attrNameSet){
						LOG.info("facebook attribute name retrieved "+attrName.getStringValue());	
					}

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

	}

	private void connectToSN(){

		LOG.info("Connect to jane societies fake fb account");

		String access_token = "AAAFs43XOj3IBADXaH1hcC65BW8GR8AttMY4FTD6E0TTn1HqBgsqmcVlfMo0sQk6qXrIOqQ23Uyx4ufg323qsLrimmr8E4n4ihxZAHGwZDZD";
		HashMap<String, String> pars = new HashMap<String, String>();
		pars.put(ISocialConnector.AUTH_TOKEN, access_token);

		LOG.info(" access_token " +  access_token);

		ISocialConnector c = this.socialData.createConnector(ISocialConnector.SocialNetwork.Facebook, pars);
		LOG.info("connector added " + c);
	}

}