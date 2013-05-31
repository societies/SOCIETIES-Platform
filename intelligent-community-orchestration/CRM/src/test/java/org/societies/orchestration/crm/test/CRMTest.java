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
package org.societies.orchestration.crm.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.orchestration.model.FilterOperators;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.cis.directory.client.CisDirectoryRemoteClient;
import org.societies.orchestration.crm.CommunityRecommendationManager;
import org.societies.orchestration.crm.Filter;

/**
 * Unit tests for Community Recommendation Manager
 *
 * @author Chris Lima
 *
 */
public class CRMTest {

	//TODO: SIMILAR operator test is disable.
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CommunityRecommendationManager.class);
	
	/** Random reference */
	private static final Random r = new Random(System.currentTimeMillis());

	private CommunityRecommendationManager CRM;

	private int limit;

	@Before
	public void doBefore() throws Exception
	{
		LOG.info("setUp");
		CisDirectoryRemoteClient mockCallback = new CisDirectoryRemoteClient();
		mockCallback.setResultList(getMockCisAdvertisementRecord(10));
		CRM = new CommunityRecommendationManager(mockCallback, new MockCisDirectoryRemote(), new MockCommManager());
	}

	@Test
	public void testGetCISAdvsRecommendationPrimaryFilter() {

		Filter primaryfilter[]  =  { 
				new Filter(CtxAttributeTypes.LOCATION_SYMBOLIC, "Dublin", FilterOperators.EQUAL),
				new Filter(CtxAttributeTypes.AGE, "10", FilterOperators.GREATER_OR_EQUAL),
				new Filter(CtxAttributeTypes.SEX, "male", FilterOperators.EQUAL),
//				new Filter(CtxAttributeTypes.INTERESTS, "soccer player", FilterOperators.SIMILAR)
		};

		List<CisAdvertisementRecord> recommendationList = CRM.getCISAdvResults(limit, primaryfilter, null);
		for (CisAdvertisementRecord adv: recommendationList){
			LOG.info(adv.getName());
		}
	}
	
	@Test
	public void testGetCISAdvsRecommendationSecondaryFilter() {

		Filter secondaryfilter[]  =  { 
				new Filter(CtxAttributeTypes.LOCATION_SYMBOLIC, "Tramore", FilterOperators.NOT_EQUAL),
				new Filter(CtxAttributeTypes.AGE, "56", FilterOperators.LESS_OR_EQUAL),
				new Filter(CtxAttributeTypes.SEX, "female", FilterOperators.EQUAL),
//				new Filter(CtxAttributeTypes.INTERESTS, "sports", FilterOperators.SIMILAR)
		};

		List<CisAdvertisementRecord> recommendationList = CRM.getCISAdvResults(limit, null, secondaryfilter);
		for (CisAdvertisementRecord adv: recommendationList){
			LOG.info(adv.getName());
		}
	}

	@Test
	public void testGetRecommendationsPrimaryFilter() {

		Filter primaryfilter[]  =  { 
				new Filter(CtxAttributeTypes.LOCATION_SYMBOLIC, "Dublin", FilterOperators.EQUAL),
				new Filter(CtxAttributeTypes.AGE, "10", FilterOperators.GREATER_OR_EQUAL),
				new Filter(CtxAttributeTypes.SEX, "male", FilterOperators.EQUAL),
//				new Filter(CtxAttributeTypes.INTERESTS, "soccer player", FilterOperators.SIMILAR)
		};

		List<IIdentity> recommendationList = CRM.getResults(limit, primaryfilter, null);
		
		List<IIdentity> mockIDList = new ArrayList<IIdentity>();
		for (IIdentity id: recommendationList){
			LOG.info("IIdentity Secondary Filter: "+id.getJid());
			mockIDList.add(new MockIdentity(id.getJid()));
		}
	}
	@Test
	public void testGetRecommendationsSecondaryFilter() {

		Filter secondaryfilter[]  =  { 
				new Filter(CtxAttributeTypes.LOCATION_SYMBOLIC, "Tramore", FilterOperators.NOT_EQUAL),
				new Filter(CtxAttributeTypes.AGE, "56", FilterOperators.LESS_OR_EQUAL),
				new Filter(CtxAttributeTypes.SEX, "female", FilterOperators.EQUAL),
//				new Filter(CtxAttributeTypes.INTERESTS, "sports", FilterOperators.SIMILAR)
		};

		List<IIdentity> recommendationList = CRM.getResults(limit, null, secondaryfilter);
		
		List<IIdentity> mockIDList = new ArrayList<IIdentity>();
		for (IIdentity id: recommendationList){
			LOG.info("IIdentity Secondary Filter: "+id.getJid());
			mockIDList.add(new MockIdentity(id.getJid()));
		}
	}
	
	/**
	 * @return 
	 * @return
	 * @see org.societies.orchestration.crm.CommunityRecommendationManager#getLimit()
	 */
	@Test
	public void testLimit() {
		Assert.assertEquals(10, CRM.getLimit());
	}
	
	@Test
	public void testNullFilters() {
		Assert.assertEquals(new ArrayList<IIdentity>(), CRM.getResults(limit, null, null));
	}
	
	
	@Test
	public void testNullFiltersCISAdv() {
		Assert.assertEquals(new ArrayList<IIdentity>(), CRM.getCISAdvResults(limit, null, null));
	}
	
	@Test
	public void testEmptyFilter() {
		Assert.assertEquals(11, CRM.getResults(limit, new Filter[0], null).size());
	}
	
	@Test
	public void testEmptyFilterCISAdv() {
		Assert.assertEquals(11, CRM.getCISAdvResults(limit, null, new Filter[0]).size());
	}

	private static List<CisAdvertisementRecord> getMockCisAdvertisementRecord(int numberOfCisAdv) {
		List<CisAdvertisementRecord> cisAdvertsList = new ArrayList<CisAdvertisementRecord>();
		for (int i = 0;  i <= numberOfCisAdv; i++) {
			CisAdvertisementRecord cisAdv = new CisAdvertisementRecord();
			MembershipCrit memberCriteria = new MembershipCrit();
			List<Criteria> criteriaList = new ArrayList<Criteria>();
			//Two random criteria for the test
			criteriaList.add(getRandomCriteria());
			criteriaList.add(getRandomCriteria());
			criteriaList.add(getRandomCriteria());
			memberCriteria.setCriteria(criteriaList);
			
			//Setting CisAdvertisementRecord
			cisAdv.setName("CIS-"+r.nextInt(Integer.MAX_VALUE) + 1);
			cisAdv.setCssownerid("chris.societies.local");
			cisAdv.setId(r.nextInt(Integer.MAX_VALUE) + 1+".societies.local");
			cisAdv.setMembershipCrit(memberCriteria);
			cisAdvertsList.add(cisAdv);
		}
		return cisAdvertsList;
	}

	/**
	 * @return
	 */
	private static Criteria getRandomCriteria() {
		Criteria criteria1 = new Criteria();
		criteria1.setAttrib(CtxAttributeTypes.AGE);
		criteria1.setValue1("22");
		
		Criteria criteria2 = new Criteria();
		criteria2.setAttrib(CtxAttributeTypes.AGE);
		criteria2.setValue1("40");
		
		Criteria criteria3 = new Criteria();
		criteria3.setAttrib(CtxAttributeTypes.LOCATION_SYMBOLIC);
		criteria3.setValue1("Dublin");
		
		Criteria criteria4 = new Criteria();
		criteria4.setAttrib(CtxAttributeTypes.LOCATION_SYMBOLIC);
		criteria4.setValue1("Tramore");
		
		Criteria criteria5 = new Criteria();
		criteria5.setAttrib(CtxAttributeTypes.LOCATION_SYMBOLIC);
		criteria5.setValue1("Brazil");
		
		Criteria criteria6 = new Criteria();
		criteria6.setAttrib(CtxAttributeTypes.SEX);
		criteria6.setValue1("male");

		Criteria criteria7 = new Criteria();
		criteria7.setAttrib(CtxAttributeTypes.SEX);
		criteria7.setValue1("female");
		
		Criteria criteria8 = new Criteria();
		criteria8.setAttrib(CtxAttributeTypes.INTERESTS);
		criteria8.setValue1("Drama movies");
		
		Criteria criteria9 = new Criteria();
		criteria9.setAttrib(CtxAttributeTypes.INTERESTS);
		criteria9.setValue1("Football team");
		
		Criteria criteria10 = new Criteria();
		criteria10.setAttrib(CtxAttributeTypes.INTERESTS);
		criteria10.setValue1("Drums");
		
		final Criteria[] randomCriteria={criteria1, criteria2, criteria3, criteria4, criteria5, criteria6, criteria7, criteria8, criteria9, criteria10};
		Criteria criteriaResult = randomCriteria[r.nextInt(10)];
		LOG.info("Mock Criteria attribute: "+criteriaResult.getAttrib()+" with value: "+criteriaResult.getValue1());
		return criteriaResult;
	}

}
