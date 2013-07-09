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
package org.societies.integration.test.bit.ctx_3pBroker;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.integration.test.userfeedback.UserFeedbackMockResult;
import org.societies.integration.test.userfeedback.UserFeedbackType;

/**
 * 
 *
 * @author nikosk
 *
 */
public class TestLocalCommunityContext {

	private ICtxBroker externalCtxBroker;
	private ICommManager commMgr;
	public ICisManager cisManager;

	private static Logger LOG = LoggerFactory.getLogger(TestLocalCommunityContext.class);

	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;

	private RequestorService requestorService = null;
	private IIdentity serviceIdentity = null;

	private IIdentity cisIdentity = null;

	private ServiceResourceIdentifier myServiceID;

	CtxEntityIdentifier cssOwnerEntityId ;

	@Before
	public void setUp(){

		LOG.info("*** initiallizing " );
		LOG.info("*** " +Test1858.getUserFeedbackMocker());

		Test1858.getUserFeedbackMocker().setEnabled(true);
		Test1858.getUserFeedbackMocker().addReply(UserFeedbackType.ACKNACK, new UserFeedbackMockResult("Allow"));
	}

	@After
	public void tearDown() throws Exception {

		if (this.cisIdentity !=null){
			this.cisManager.deleteCis(this.cisIdentity.getBareJid());
		}
	}


	@Test
	public void Test(){

		this.externalCtxBroker = Test1858.getCtxBroker();
		this.commMgr = Test1858.getCommManager();
		this.cisManager = Test1858.getCisManager();

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

		LOG.info("*** Starting test...");
		
		//create a community entity/attributes
		this.cisIdentity = this.createCIS();

		// lookup/retrieve community entity
		lookupCommunityCreateHistory();

		retrieveAttributeCommInf();
	}

	/*
	 * this test will retrieve a community attribute that triggers inference
	 * and a community attribute that doesn't trigger inference 
	 */
	private void retrieveAttributeCommInf(){

		try {
			CtxEntityIdentifier commEntityId = this.externalCtxBroker.retrieveCommunityEntityId(this.requestorService, this.cisIdentity).get();

			CtxAttribute activitiesAttr = this.externalCtxBroker.createAttribute(this.requestorService, commEntityId, CtxAttributeTypes.ACTIVITIES).get();
			activitiesAttr.setStringValue("activity1,activity2");
			CtxAttribute activitiesAttrUpdated = (CtxAttribute) this.externalCtxBroker.update(this.requestorService,activitiesAttr ).get();
			LOG.info(" update activities: "+activitiesAttrUpdated.getComplexValue().getPairs());
			LOG.info(" retrieved activities getFreshness 1: "+activitiesAttrUpdated.getQuality().getFreshness());
			
			
			CtxAttribute moviesAttr = this.externalCtxBroker.createAttribute(this.requestorService, commEntityId, CtxAttributeTypes.MOVIES).get();
			moviesAttr.setStringValue("movie1,movie2");
			CtxAttribute moviesAttrUpdated = (CtxAttribute) this.externalCtxBroker.update(this.requestorService,moviesAttr ).get();
			LOG.info(" update movies: "+moviesAttrUpdated.getComplexValue().getPairs());
			LOG.info(" retrieved movies getFreshness 1: "+moviesAttrUpdated.getQuality().getFreshness());
			
			Thread.sleep(5000);
			
			// if community ctx estimator is called a complex value type should be present
			LOG.info(" trigger community inference test" );
			CtxAttribute attrActivitiesRetrieved = (CtxAttribute) this.externalCtxBroker.retrieve(requestorService,activitiesAttr.getId() ).get();
			LOG.info(" retrieved activities: "+attrActivitiesRetrieved.getComplexValue().getPairs());
			LOG.info(" retrieved activities getFreshness 2: "+attrActivitiesRetrieved.getQuality().getFreshness());
			LOG.info(" retrieved activities Freshness should be the same: "+attrActivitiesRetrieved.getQuality().getFreshness()+" "+activitiesAttrUpdated.getQuality().getFreshness());
			LOG.info(" retrieved activities Freshness should be the same: "+attrActivitiesRetrieved.getQuality().getLastUpdated()+" "+activitiesAttrUpdated.getQuality().getLastUpdated());
			assertEquals(attrActivitiesRetrieved.getQuality().getLastUpdated(), activitiesAttrUpdated.getQuality().getLastUpdated());
			
			CtxAttribute attrMoviesRetrieved = (CtxAttribute) this.externalCtxBroker.retrieve(requestorService,moviesAttr.getId()).get();
			LOG.info(" retrieved movies getFreshness 2 : "+attrMoviesRetrieved.getQuality().getFreshness());
			LOG.info(" retrieved movies: "+attrMoviesRetrieved.getComplexValue().getPairs());
			LOG.info(" retrieved movies Freshness should NOT be the same: "+attrMoviesRetrieved.getQuality().getFreshness()+" "+moviesAttrUpdated.getQuality().getFreshness());
			LOG.info(" retrieved movies Freshness should NOT be the same (last updated): "+attrMoviesRetrieved.getQuality().getLastUpdated()+" "+moviesAttrUpdated.getQuality().getLastUpdated());
			assertNotSame(attrMoviesRetrieved.getQuality().getLastUpdated(), moviesAttrUpdated.getQuality().getLastUpdated());
			
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

	private void lookupCommunityCreateHistory(){

		CommunityCtxEntity communityEntity = null;
		Date startDate = new Date();

		try {
			Thread.sleep(9000);
			CtxEntityIdentifier commEntityId = this.externalCtxBroker.retrieveCommunityEntityId(this.requestorService, this.cisIdentity).get();
			//CommunityCtxEntity commEntity = (CommunityCtxEntity) this.externalCtxBroker.retrieve(this.requestorService, commEntityId).get();
			//LOG.info("commEntity : " +commEntity );
			assertNotNull(commEntityId);

			LOG.info("commEntityId : " +commEntityId );

			CtxAttribute commAttr =  this.externalCtxBroker.createAttribute(this.requestorService, commEntityId, CtxAttributeTypes.EMAIL).get();
			commAttr.setStringValue("communityemail");
			this.externalCtxBroker.update(this.requestorService, commAttr);
			Thread.sleep(4000);

			LOG.info("community this.cisIdentity : " +this.cisIdentity );
			List<CtxIdentifier> resultsEnt = this.externalCtxBroker.lookup(this.requestorService, this.cisIdentity, CtxModelType.ENTITY, CtxEntityTypes.COMMUNITY).get();
			List<CtxIdentifier> resultsAttr = this.externalCtxBroker.lookup(this.requestorService, this.cisIdentity, CtxModelType.ATTRIBUTE, CtxAttributeTypes.EMAIL).get();

			String value = "";
			LOG.info("community this.cisIdentity resultsEnt: " +resultsEnt );
			LOG.info("community this.cisIdentity resultsAttr: " +resultsAttr );
			if(resultsAttr.size() > 0){
				CtxAttribute commEmailAttr = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService,resultsAttr.get(0)).get();	
				value = commEmailAttr.getStringValue();	
				LOG.info("community this.cisIdentity value: " +value );
			}
			assertEquals("communityemail", value);


			// create community attributes
			// update community attributes , stored in history
			// retrieve community attributes

			LOG.info(" interestCommAttr commEntityId : " +commEntityId );

			CtxAttribute interestCommAttr = this.externalCtxBroker.createAttribute(this.requestorService, commEntityId, CtxAttributeTypes.INTERESTS).get();
			LOG.info(" interestCommAttr interestCommAttr : " +interestCommAttr.getId());

			interestCommAttr.setHistoryRecorded(true);
			interestCommAttr.setStringValue("aa,bb,cc");
			CtxAttribute interestCommAttr1 = (CtxAttribute) this.externalCtxBroker.update(this.requestorService, interestCommAttr).get();
			LOG.info(" interestCommAttr interestCommAttr 1: " +interestCommAttr1.getId());
			Thread.sleep(1000);
			interestCommAttr1.setStringValue("aa,bb,cc,dd");
			CtxAttribute interestCommAttr2 = (CtxAttribute) this.externalCtxBroker.update(this.requestorService, interestCommAttr1).get();

			Thread.sleep(1000);
			interestCommAttr2.setStringValue("aa,bb,cc,dd,ee");
			CtxAttribute interestCommAttr3 = (CtxAttribute) this.externalCtxBroker.update(this.requestorService, interestCommAttr2).get();

			Thread.sleep(1000);
			Date endDate = new Date();
			LOG.info("startDate  : " + startDate);
			LOG.info("endDate  : " + endDate);

			List<CtxHistoryAttribute> historyList = this.externalCtxBroker.retrieveHistory(this.requestorService,interestCommAttr.getId(), startDate, endDate).get();
			LOG.info("historyList  : " + historyList);
			assertEquals(3, historyList.size());

			LOG.info("TEST SUCCESSFUL  : ");

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

	protected IIdentity createCIS() {

		IIdentity cisID = null;
		try {
			Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> ();
			LOG.info("*** trying to create cis:");
			ICisOwned cisOwned = this.cisManager.createCis("testCIS", "cisType", cisCriteria, "nice CIS").get();
			LOG.info("*** cis created: "+cisOwned.getCisId());

			LOG.info("*** cisOwned " +cisOwned);
			LOG.info("*** cisOwned.getCisId() " +cisOwned.getCisId());
			String cisIDString = cisOwned.getCisId();

			cisID = this.commMgr.getIdManager().fromJid(cisIDString);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cisID;
	}
}