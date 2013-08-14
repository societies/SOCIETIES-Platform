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
package org.societies.integration.test.bit.ctx_Broker;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.context.broker.ICtxBroker;


/**
 * This test creates two CISs:
 * <ol>
 * <li>CIS: allowed attributes = { ACTIVITIES, MOVIES } (<b>public</b> access)</li>
 * <li>CIS: allowed attributes = { ACTIVITIES, MOVIES } (<b>members-only</b> access)</li>
 * </ol>
 *
 * @author nikosk
 */
public class TestLocalCommunityContext {

	private static Logger LOG = LoggerFactory.getLogger(TestLocalCommunityContext.class);
	
	private static final String MEMBERS_ONLY = "SHARE_WITH_CIS_MEMBERS_ONLY";
	
	private static final String PUBLIC = "SHARE_WITH_3RD_PARTIES";
	
	private static final long CIS_CREATION_TIMEOUT = 2000l;
	
	private static final String NON_MEMBER_USER_ID = "nikosk.societies.local";
	
	/** The external Context Broker service reference. */
	private ICtxBroker internalCtxBroker;
	
	/** The CIS Mgr service reference. */
	private ICisManager cisManager;
	
	/** The Comms Mgr service reference. */
	private ICommManager commMgr;
	
	/** The CSS owner ID. */
	private IIdentity userId;
	
	/** The ID of a CSS which is not member of the created CISs. */
	private IIdentity nonMemberId;

	private IIdentity publicCisId;
	private IIdentity membersOnlyCisId;
	
	private ServiceResourceIdentifier serviceId;

	CtxEntityIdentifier cssOwnerEntityId ;

	@Before
	public void setUp() throws Exception {

		LOG.info("*** setUp");
		// obtain service refs
		//this.ctxBroker = Test2119.getCtxBroker();
		this.commMgr = Test2119.getCommManager();
		this.cisManager = Test2119.getCisManager();
		
		// setup test data
		this.userId = this.commMgr.getIdManager().fromJid(
				this.commMgr.getIdManager().getThisNetworkNode().getBareJid());
		LOG.info("*** setUp: userId={}", this.userId);
		
		this.nonMemberId = this.commMgr.getIdManager().fromJid(NON_MEMBER_USER_ID);
		LOG.info("*** setUp: nonMemberId={}", this.nonMemberId);

		this.serviceId = new ServiceResourceIdentifier();
		this.serviceId.setServiceInstanceIdentifier("css://service@societies.org/HelloEarth");
		this.serviceId.setIdentifier(new URI("css://service@societies.org/HelloEarth"));
		LOG.info("*** setUp: serviceId={}", this.serviceId);

		// create CIS1
		this.publicCisId = this.createCis("testCIS1", new String[] {
				CtxAttributeTypes.ACTIVITIES, CtxAttributeTypes.MOVIES }, PUBLIC);
		LOG.info("*** setUp: publicCisId={}", this.publicCisId);
		// create CIS2
		this.membersOnlyCisId = this.createCis("testCIS2", new String[] {
				CtxAttributeTypes.ACTIVITIES, CtxAttributeTypes.MOVIES }, MEMBERS_ONLY);
		LOG.info("*** setUp: membersOnlyCisId={}", this.membersOnlyCisId);
		
		Thread.sleep(CIS_CREATION_TIMEOUT);
	}

	@After
	public void tearDown() throws Exception {

		LOG.info("*** tearDown");
		// reset test data
		this.userId = null;
		this.nonMemberId = null;
		this.serviceId = null;
		
		if (this.publicCisId != null) {
			LOG.info("*** tearDown: Removing publicCisId {}", this.publicCisId);
			this.cisManager.deleteCis(this.publicCisId.getBareJid());
			this.publicCisId = null;
		}
		if (this.membersOnlyCisId !=null) {
			LOG.info("*** tearDown: Removing membersOnlyCis {}", this.membersOnlyCisId);
			this.cisManager.deleteCis(this.membersOnlyCisId.getBareJid());
			this.membersOnlyCisId = null;
		}
		
		// reset service references
		this.internalCtxBroker = null;
		this.commMgr = null;
		this.cisManager = null;
	}

	/**
	 * <ol>
	 * <li>Member performs CRUD on Public CIS</li>
	 * <li>Member performs CRUD on Members-Only CIS</li>
	 * </ol>
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testMemberCRUD() throws Exception {
		
		LOG.info("*** testMemberCRUD: START");
		
		final RequestorService requestorService =
				new RequestorService(this.userId, this.serviceId);
		
		// verify CRUD on public CIS by member CSS/3P
		final CtxEntityIdentifier publicCisEntId = 
				this.internalCtxBroker.retrieveCommunityEntityId(requestorService, this.publicCisId).get();
		LOG.info("*** testMemberCRUD: publicCisEntId={}", publicCisEntId);
		assertNotNull(publicCisEntId);
		
		final CtxAttribute publicActivitiesAttr =	this.internalCtxBroker.createAttribute(
				requestorService, publicCisEntId, CtxAttributeTypes.ACTIVITIES).get();
		assertNotNull(publicActivitiesAttr);
		assertNull(publicActivitiesAttr.getStringValue());
		
		publicActivitiesAttr.setStringValue("activity1,activity2");
		final CtxAttribute publicActivitiesAttrUpdated = (CtxAttribute) 
				this.internalCtxBroker.update(requestorService, publicActivitiesAttr).get();
		assertNotNull(publicActivitiesAttrUpdated);
		assertNotNull(publicActivitiesAttrUpdated.getStringValue());
		assertEquals("activity1,activity2", publicActivitiesAttrUpdated.getStringValue());
		LOG.info("*** testMemberCRUD: updated activities: {}", publicActivitiesAttrUpdated.getStringValue());

		final CtxAttribute publicActivitiesAttrRetrieved = (CtxAttribute)
				this.internalCtxBroker.retrieve(requestorService, publicActivitiesAttr.getId()).get();
		assertNotNull(publicActivitiesAttrRetrieved);
		assertNotNull(publicActivitiesAttrRetrieved.getStringValue());
		assertEquals("activity1,activity2", publicActivitiesAttrRetrieved.getStringValue());
		
		// verify CRUD on members-only CIS by member CSS/3P
		final CtxEntityIdentifier membersOnlyCisEntId = 
				this.internalCtxBroker.retrieveCommunityEntityId(requestorService, this.membersOnlyCisId).get();
		LOG.info("*** testMemberCRUD: membersOnlyCisEntId={}", membersOnlyCisEntId);
		assertNotNull(membersOnlyCisEntId);
		
		final CtxAttribute membersOnlyActivitiesAttr =	this.internalCtxBroker.createAttribute(
				requestorService, membersOnlyCisEntId, CtxAttributeTypes.ACTIVITIES).get();
		assertNotNull(membersOnlyActivitiesAttr);
		assertNull(membersOnlyActivitiesAttr.getStringValue());
		
		membersOnlyActivitiesAttr.setStringValue("activity1,activity2");
		final CtxAttribute membersOnlyActivitiesAttrUpdated = (CtxAttribute) 
				this.internalCtxBroker.update(requestorService, membersOnlyActivitiesAttr).get();
		assertNotNull(membersOnlyActivitiesAttrUpdated);
		assertNotNull(membersOnlyActivitiesAttrUpdated.getStringValue());
		assertEquals("activity1,activity2", membersOnlyActivitiesAttrUpdated.getStringValue());
		LOG.info("*** testMemberCRUD: updated activities: {}", membersOnlyActivitiesAttrUpdated.getStringValue());

		final CtxAttribute membersOnlyActivitiesAttrRetrieved = (CtxAttribute)
				this.internalCtxBroker.retrieve(requestorService, membersOnlyActivitiesAttr.getId()).get();
		assertNotNull(membersOnlyActivitiesAttrRetrieved);
		assertNotNull(membersOnlyActivitiesAttrRetrieved.getStringValue());
		assertEquals("activity1,activity2", membersOnlyActivitiesAttrRetrieved.getStringValue());
			
		LOG.info("*** testMemberCRUD: END");
	}
	
	/**
	 * <ol>
	 * <li>Non-member performs CRUD on Public CIS</li>
	 * <li>Non-member performs CRUD on Members-Only CIS</li>
	 * </ol>
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testPublicCRUD() throws Exception {
		
		LOG.info("*** testPublicCRUD: START");
		
		final RequestorService requestorService =
				new RequestorService(this.nonMemberId, this.serviceId);
		
		// verify CRUD on public CIS by non-member CSS/3P
		final CtxEntityIdentifier publicCisEntId = 
				this.internalCtxBroker.retrieveCommunityEntityId(requestorService, this.publicCisId).get();
		LOG.info("*** testPublicCRUD: publicCisEntId={}", publicCisEntId);
		assertNotNull(publicCisEntId);
		
		final CtxAttribute publicActivitiesAttr =	this.internalCtxBroker.createAttribute(
				requestorService, publicCisEntId, CtxAttributeTypes.ACTIVITIES).get();
		assertNotNull(publicActivitiesAttr);
		assertNull(publicActivitiesAttr.getStringValue());
		
		publicActivitiesAttr.setStringValue("activity1,activity2");
		final CtxAttribute publicActivitiesAttrUpdated = (CtxAttribute) 
				this.internalCtxBroker.update(requestorService, publicActivitiesAttr).get();
		assertNotNull(publicActivitiesAttrUpdated);
		assertNotNull(publicActivitiesAttrUpdated.getStringValue());
		assertEquals("activity1,activity2", publicActivitiesAttrUpdated.getStringValue());
		LOG.info("*** testPublicCRUD: updated activities: {}", publicActivitiesAttrUpdated.getStringValue());

		final CtxAttribute publicActivitiesAttrRetrieved = (CtxAttribute)
				this.internalCtxBroker.retrieve(requestorService, publicActivitiesAttr.getId()).get();
		assertNotNull(publicActivitiesAttrRetrieved);
		assertNotNull(publicActivitiesAttrRetrieved.getStringValue());
		assertEquals("activity1,activity2", publicActivitiesAttrRetrieved.getStringValue());
		
		// verify CRUD on members-only CIS by non-member CSS/3P
		final CtxEntityIdentifier membersOnlyCisEntId = 
				this.internalCtxBroker.retrieveCommunityEntityId(requestorService, this.membersOnlyCisId).get();
		LOG.info("*** testPublicCRUD: membersOnlyCisEntId={}", membersOnlyCisEntId);
		assertNotNull(membersOnlyCisEntId);
		
		final CtxAttribute membersOnlyActivitiesAttr =	this.internalCtxBroker.createAttribute(
				requestorService, membersOnlyCisEntId, CtxAttributeTypes.ACTIVITIES).get();
		assertNotNull(membersOnlyActivitiesAttr);
		assertNull(membersOnlyActivitiesAttr.getStringValue());
		
		membersOnlyActivitiesAttr.setStringValue("activity1,activity2");
		boolean accessControlExceptionCaught = false;
		try {
			this.internalCtxBroker.update(requestorService, membersOnlyActivitiesAttr).get();
		} catch (CtxAccessControlException cace) {
			accessControlExceptionCaught = true;
		}
		assertTrue(accessControlExceptionCaught);

		accessControlExceptionCaught = false;
		try {
			this.internalCtxBroker.retrieve(requestorService, membersOnlyActivitiesAttr.getId()).get();
		} catch (CtxAccessControlException cace) {
			accessControlExceptionCaught = true;
		}
		assertTrue(accessControlExceptionCaught);
			
		LOG.info("*** testPublicCRUD: END");
	}
	
	/*
	 * this test will retrieve a community attribute that triggers inference
	 * and a community attribute that doesn't trigger inference 
	 *
	@Test
	public void retrieveAttributeCommInf(){
		
		final RequestorService requestorService =
				new RequestorService(this.userId, this.serviceId);

		LOG.info("starting test retrieveAttributeCommInf ");
		try {
			CtxEntityIdentifier commEntityId = this.ctxBroker.retrieveCommunityEntityId(requestorService, this.publicCisId).get();

			CtxAttribute activitiesAttr = this.ctxBroker.createAttribute(requestorService, commEntityId, CtxAttributeTypes.ACTIVITIES).get();
			activitiesAttr.setStringValue("activity1,activity2");
			CtxAttribute activitiesAttrUpdated = (CtxAttribute) this.ctxBroker.update(requestorService,activitiesAttr ).get();
			LOG.info(" update activities: "+activitiesAttrUpdated.getComplexValue().getPairs());
			LOG.info(" retrieved activities getFreshness 1: "+activitiesAttrUpdated.getQuality().getFreshness());


			CtxAttribute moviesAttr = this.ctxBroker.createAttribute(requestorService, commEntityId, CtxAttributeTypes.MOVIES).get();
			moviesAttr.setStringValue("movie1,movie2");
			CtxAttribute moviesAttrUpdated = (CtxAttribute) this.ctxBroker.update(requestorService,moviesAttr ).get();
			LOG.info(" update movies: "+moviesAttrUpdated.getComplexValue().getPairs());
			LOG.info(" retrieved movies getFreshness 1: "+moviesAttrUpdated.getQuality().getFreshness());

			Thread.sleep(5000);

			// if community ctx estimator is called a complex value type should be present
			LOG.info(" trigger community inference test" );
			CtxAttribute attrActivitiesRetrieved = (CtxAttribute) this.ctxBroker.retrieve(requestorService,activitiesAttr.getId() ).get();
			LOG.info(" retrieved activities: "+attrActivitiesRetrieved.getComplexValue().getPairs());
			LOG.info(" retrieved activities getFreshness 2: "+attrActivitiesRetrieved.getQuality().getFreshness());
			LOG.info(" retrieved activities Freshness should be the same: "+attrActivitiesRetrieved.getQuality().getFreshness()+" "+activitiesAttrUpdated.getQuality().getFreshness());
			LOG.info(" retrieved activities Freshness should be the same: "+attrActivitiesRetrieved.getQuality().getLastUpdated()+" "+activitiesAttrUpdated.getQuality().getLastUpdated());
			assertEquals(attrActivitiesRetrieved.getQuality().getLastUpdated(), activitiesAttrUpdated.getQuality().getLastUpdated());

			CtxAttribute attrMoviesRetrieved = (CtxAttribute) this.ctxBroker.retrieve(requestorService,moviesAttr.getId()).get();
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
	
	@Test
	public void lookupCommunityCreateHistory(){
		
		LOG.info("starting test lookupCommunityCreateHistory ");
		
		CommunityCtxEntity communityEntity = null;
		Date startDate = new Date();

		try {
			Thread.sleep(9000);
			CtxEntityIdentifier commEntityId1 = this.ctxBroker.retrieveCommunityEntityId(this.requestorService, this.cisId1).get();
			CtxEntityIdentifier commEntityId2 = this.ctxBroker.retrieveCommunityEntityId(this.requestorService, this.cisId2).get();
			//CommunityCtxEntity commEntity = (CommunityCtxEntity) this.externalCtxBroker.retrieve(this.requestorService, commEntityId).get();
			//LOG.info("commEntity : " +commEntity );
			assertNotNull(commEntityId1);
			assertNotNull(commEntityId2);
			
			LOG.info("commEntityId 1: " +commEntityId1 );
			LOG.info("commEntityId 2: " +commEntityId2 );

			CtxAttribute commAttr1 =  this.ctxBroker.createAttribute(this.requestorService, commEntityId1, CtxAttributeTypes.EMAIL).get();
			commAttr1.setStringValue("communityemail1");
			this.ctxBroker.update(this.requestorService, commAttr1);
			Thread.sleep(4000);

			CtxAttribute commAttr2 =  this.ctxBroker.createAttribute(this.requestorService, commEntityId2, CtxAttributeTypes.EMAIL).get();
			commAttr2.setStringValue("communityemail2");
			this.ctxBroker.update(this.requestorService, commAttr2);
			Thread.sleep(4000);

			
			LOG.info("community this.cisIdentity 1: " +this.cisId1 );
			List<CtxIdentifier> resultsEnt1 = this.ctxBroker.lookup(this.requestorService, this.cisId1, CtxModelType.ENTITY, CtxEntityTypes.COMMUNITY).get();
			List<CtxIdentifier> resultsAttr1 = this.ctxBroker.lookup(this.requestorService, this.cisId1, CtxModelType.ATTRIBUTE, CtxAttributeTypes.EMAIL).get();

			String value1 = "";
			LOG.info("community this.cisIdentity resultsEnt1 : " +resultsEnt1 );
			LOG.info("community this.cisIdentity resultsAttr1 : " +resultsAttr1 );
			if(resultsAttr1.size() > 0){
				CtxAttribute commEmailAttr1 = (CtxAttribute) this.ctxBroker.retrieve(this.requestorService,resultsAttr1.get(0)).get();	
				value1 = commEmailAttr1.getStringValue();	
				LOG.info("community this.cisIdentity value: " +value1 );
			}
			assertEquals("communityemail1", value1);


			LOG.info("community this.cisIdentity 2 : " +this.cisId2 );
			List<CtxIdentifier> resultsEnt2 = this.ctxBroker.lookup(this.requestorService, this.cisId2, CtxModelType.ENTITY, CtxEntityTypes.COMMUNITY).get();
			List<CtxIdentifier> resultsAttr2 = this.ctxBroker.lookup(this.requestorService, this.cisId2, CtxModelType.ATTRIBUTE, CtxAttributeTypes.EMAIL).get();

			String value2 = "";
			LOG.info("community this.cisIdentity resultsEnt: " +resultsEnt2 );
			LOG.info("community this.cisIdentity resultsAttr: " +resultsAttr2 );
			if(resultsAttr2.size() > 0){
				CtxAttribute commEmailAttr2 = (CtxAttribute) this.ctxBroker.retrieve(this.requestorService,resultsAttr2.get(0)).get();	
				value2 = commEmailAttr2.getStringValue();	
				LOG.info("community this.cisIdentity value: " +value2 );
			}
			assertEquals("communityemail2", value2);

			// create community attributes
			// update community attributes , stored in history
			// retrieve community attributes

			LOG.info(" interestCommAttr commEntityId : " +commEntityId1 );

			CtxAttribute interestCommAttr = this.ctxBroker.createAttribute(this.requestorService, commEntityId1, CtxAttributeTypes.INTERESTS).get();
			LOG.info(" interestCommAttr interestCommAttr : " +interestCommAttr.getId());

			interestCommAttr.setHistoryRecorded(true);
			interestCommAttr.setStringValue("aa,bb,cc");
			CtxAttribute interestCommAttr1 = (CtxAttribute) this.ctxBroker.update(this.requestorService, interestCommAttr).get();
			LOG.info(" interestCommAttr interestCommAttr 1: " +interestCommAttr1.getId());
			Thread.sleep(1000);
			interestCommAttr1.setStringValue("aa,bb,cc,dd");
			CtxAttribute interestCommAttr2 = (CtxAttribute) this.ctxBroker.update(this.requestorService, interestCommAttr1).get();

			Thread.sleep(1000);
			interestCommAttr2.setStringValue("aa,bb,cc,dd,ee");
			CtxAttribute interestCommAttr3 = (CtxAttribute) this.ctxBroker.update(this.requestorService, interestCommAttr2).get();

			Thread.sleep(1000);
			Date endDate = new Date();
			LOG.info("startDate  : " + startDate);
			LOG.info("endDate  : " + endDate);

			List<CtxHistoryAttribute> historyList = this.ctxBroker.retrieveHistory(this.requestorService,interestCommAttr.getId(), startDate, endDate).get();
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
*/
	/**
	 * 
	 * @param name
	 * @param attrTypes
	 * @param mode {@link #MEMBERS_ONLY} or {@link TestLocalCommunityContext#PUBLIC}
	 * @return
	 * @throws Exception
	 */
	private IIdentity createCis(String name, String[] attrTypes, String mode) throws Exception {

		final Hashtable<String, MembershipCriteria> cisCriteria = 
				new Hashtable<String, MembershipCriteria> ();
		LOG.info("*** createCis: name='{}'", name);
		final ICisOwned cisOwned = 
				this.cisManager.createCis(name, "cisType", cisCriteria, "nice CIS",
						this.createPrivacyPolicy(attrTypes, mode)).get();
		final String cisIdStr = cisOwned.getCisId();

		return this.commMgr.getIdManager().fromJid(cisIdStr);
	}
	
	/**
	 * 
	 * @param attrTypes
	 * @param mode {@link #MEMBERS_ONLY} or {@link TestLocalCommunityContext#PUBLIC}
	 * @return
	 */
	private String createPrivacyPolicy(String[] attrTypes, String mode) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<RequestPolicy>"
				+ "<Target>"
				+ "<Resource>"
				+ "<Attribute AttributeId=\"cis\""
				+ " DataType=\"http://www.w3.org/2001/XMLSchema#string\">"
				+ "<AttributeValue>cis-member-list</AttributeValue>"
				+ "</Attribute>"
				+ "</Resource>"
				+ "<Action>"
				+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\"" 
				+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">"
				+ "<AttributeValue>READ</AttributeValue>"
				+ "</Attribute>"
				+ "<optional>false</optional>"
				+ "</Action>"
				+ "<Action>"
				+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\"" 
				+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">"
				+ "<AttributeValue>CREATE</AttributeValue>"
				+ "</Attribute>"
				+ "<optional>false</optional>"
				+ "</Action>"
				+ "<Condition>"
				+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\"" 
				+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
				+ "<AttributeValue DataType=\"RIGHT_TO_OPTOUT\">1</AttributeValue>"
				+ "</Attribute>"
				+ "<optional>false</optional>"
				+ "</Condition>"
				+ "<Condition>"
				+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\"" 
				+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
				+ "<AttributeValue DataType=\"STORE_IN_SECURE_STORAGE\">1</AttributeValue>"
				+ "</Attribute>"
				+ "<optional>false</optional>"
				+ "</Condition>"
				+ "<Condition>"
				+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\"" 
				+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
				+ "<AttributeValue DataType=\"SHARE_WITH_CIS_MEMBERS_ONLY\">1</AttributeValue>"
				+ "</Attribute>"
				+ "<optional>false</optional>"
				+ "</Condition>"
				+ "<optional>false</optional>"
				+ "</Target>");
				for (final String attrType : attrTypes) {
					sb.append("<Target>"
							+ "<Resource>"
							+ "<Attribute AttributeId=\"context\""
							+ " DataType=\"http://www.w3.org/2001/XMLSchema#string\">"
							+ "<AttributeValue>" + attrType + "</AttributeValue>"
							+ "</Attribute>"
							+ "</Resource>"
							+ "<Action>"
							+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\"" 
							+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">"
							+ "<AttributeValue>CREATE</AttributeValue>"
							+ "</Attribute>"
							+ "<optional>false</optional>"
							+ "</Action>"
							+ "<Action>"
							+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\"" 
							+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">"
							+ "<AttributeValue>READ</AttributeValue>"
							+ "</Attribute>"
							+ "<optional>false</optional>"
							+ "</Action>"
							+ "<Action>"
							+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\"" 
							+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">"
							+ "<AttributeValue>WRITE</AttributeValue>"
							+ "</Attribute>"
							+ "<optional>false</optional>"
							+ "</Action>"
							+ "<Condition>"
							+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\"" 
							+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
							+ "<AttributeValue DataType=\"RIGHT_TO_OPTOUT\">1</AttributeValue>"
							+ "</Attribute>"
							+ "<optional>false</optional>"
							+ "</Condition>"
							+ "<Condition>"
							+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\"" 
							+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
							+ "<AttributeValue DataType=\"STORE_IN_SECURE_STORAGE\">1</AttributeValue>"
							+ "</Attribute>"
							+ "<optional>false</optional>"
							+ "</Condition>"
							+ "<Condition>"
							+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\"" 
							+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
							+ "<AttributeValue DataType=\"" + mode + "\">1</AttributeValue>"
							+ "</Attribute>"
							+ "<optional>false</optional>"
							+ "</Condition>"
							+ "<optional>false</optional>"
							+ "</Target>");
				}
				sb.append("</RequestPolicy>");
		
		return sb.toString();
	}
}