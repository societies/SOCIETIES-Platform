/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
 * ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.context.user.inheritance.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelObjectFactory;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.context.user.inheritance.impl.UserContextInheritanceMgr;
import org.springframework.scheduling.annotation.AsyncResult;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class UserContextInheritanceMgrTest {

	static IndividualCtxEntity indiEntityCSS;
	static CtxEntityIdentifier entityCSSID;

	static CommunityCtxEntity entityCISA;
	static CtxEntityIdentifier entityCISIDA;

	static CtxEntity entityCISB;
	static CtxEntityIdentifier entityCISIDB;

	static CtxEntity entityCISC;
	static CtxEntityIdentifier entityCISIDC;

	static CtxAttribute attrTemperatureCSS;
	static CtxAttributeIdentifier attrTemperatureCSSID;

	static CtxAttribute attrTemperatureCISA;
	static CtxAttributeIdentifier attrTemperatureCISAID;

	static CtxAttribute attrTemperatureCISB;
	static CtxAttributeIdentifier attrTemperatureCISBID;

	static CtxAttribute attrTemperatureCISC;
	static CtxAttributeIdentifier attrTemperatureCISCID;

	static CtxAssociation assoc;
	static CtxAssociationIdentifier assocID;

	static Set<CtxAssociationIdentifier> mockedAssociationIds;

	private IIdentity mockIdentiyCSSID;
	private INetworkNode netNode = Mockito.mock(INetworkNode.class); ;

	ICtxBroker mockctxBroker = Mockito.mock(ICtxBroker.class);
	ICommManager mockcomMngr = Mockito.mock(ICommManager.class);
	IIdentityManager idm = Mockito.mock(IIdentityManager.class);
	IndividualCtxEntity mockedIndividualEntity = Mockito.mock(IndividualCtxEntity.class);
	CtxAssociationTypes mockedCtxAssocTypes = Mockito.mock(CtxAssociationTypes.class);

	IndividualCtxEntity mockedCSS = Mockito.mock(IndividualCtxEntity.class);
	IndividualCtxEntity mCSS = new IndividualCtxEntity(entityCSSID);
	UserContextInheritanceMgr userInheritance;


	private static String cssIdString = "context://john.societies.local/ENTITY/person/31";


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// creating structure:
		// cssA is member of CISA, CISB, CISC
		// CISA, CISB, CISC maintain context attributes of type temperature

		//create individual entity and add temperature attribute to it
		entityCSSID = new CtxEntityIdentifier("context://john.societies.local/ENTITY/person/31");
		attrTemperatureCSSID = new CtxAttributeIdentifier(entityCSSID,CtxAttributeTypes.TEMPERATURE, 1111L );
		attrTemperatureCSS = CtxModelObjectFactory.getInstance().createAttribute(attrTemperatureCSSID, new Date(), new Date(), "hot");
		Set<CtxAttribute> attrSetCSS = new HashSet<CtxAttribute>();
		attrSetCSS.add(attrTemperatureCSS);
		indiEntityCSS = CtxModelObjectFactory.getInstance().createIndividualEntity(entityCSSID, new Date(), attrSetCSS, new HashSet<CtxAssociationIdentifier>(), new HashSet<CtxEntityIdentifier>());


		//create community entity and add temperature attribute to it
		entityCISIDA = new CtxEntityIdentifier("context://cis-1eebff13-9750-404a-8e3b-1a91b79cd5e7.ict-societies.eu/ENTITY/community/32767");
		attrTemperatureCISAID = new CtxAttributeIdentifier(entityCISIDA,CtxAttributeTypes.TEMPERATURE, 122L);
		attrTemperatureCISA = CtxModelObjectFactory.getInstance().createAttribute(attrTemperatureCISAID, new Date(), new Date(), "cold");
		Set<CtxAttribute> attrSetCISA = new HashSet<CtxAttribute>();
		attrSetCISA.add(attrTemperatureCISA);
		entityCISA = CtxModelObjectFactory.getInstance().createCommunityEntity(entityCISIDA, new Date(), attrSetCISA, new HashSet<CtxAssociationIdentifier>(), new HashSet<CtxEntityIdentifier>(), new HashSet<CtxEntityIdentifier>());

		//TODO the same for two more community entities
		entityCISIDB = new CtxEntityIdentifier("context://cis-1eebff13-9750-404a-8e3b-1a91b79cd5e8.ict-societies.eu/ENTITY/community/32768");
		attrTemperatureCISBID = new CtxAttributeIdentifier("context://cis-1eebff13-9750-404a-8e3b-1a91b79cd5e8.ict-societies.eu/ENTITY/community/32768/ATTRIBUTE/temperature/30");
		attrTemperatureCISB = CtxModelObjectFactory.getInstance().createAttribute(attrTemperatureCISBID, new Date(), new Date(), "zzz");
		Set<CtxAttribute> attrSetCISB = new HashSet<CtxAttribute>();
		attrSetCISB.add(attrTemperatureCISB);
		entityCISB = CtxModelObjectFactory.getInstance().createCommunityEntity(entityCISIDB, new Date(), new HashSet(), new HashSet(), new HashSet(), new HashSet());

		entityCISIDC = new CtxEntityIdentifier("context://cis-1eebff13-9750-404a-8e3b-1a91b79cd5e9.ict-societies.eu/ENTITY/community/32769");
		attrTemperatureCISCID = new CtxAttributeIdentifier("context://cis-1eebff13-9750-404a-8e3b-1a91b79cd5e9.ict-societies.eu/ENTITY/community/32769/ATTRIBUTE/temperature/30");
		attrTemperatureCISC = CtxModelObjectFactory.getInstance().createAttribute(attrTemperatureCISCID, new Date(), new Date(), "45");
		Set<CtxAttribute> attrSetCISC = new HashSet<CtxAttribute>();
		attrSetCISC.add(attrTemperatureCISC);
		entityCISC = CtxModelObjectFactory.getInstance().createCommunityEntity(entityCISIDC, new Date(), new HashSet(), new HashSet(), new HashSet(), new HashSet());



		/*
			entityCISIDB = new CtxEntityIdentifier("context://cis-1eebff13-9750-404a-8e3b-1a91b79cd5e8.ict-societies.eu/ENTITY/community/32768");
			entityCISB = CtxModelObjectFactory.getInstance().createCommunityEntity(entityCISIDB, new Date(), new HashSet(), new HashSet(), new HashSet(), new HashSet());
			entityCISIDC = new CtxEntityIdentifier("context://cis-1eebff13-9750-404a-8e3b-1a91b79cd5e9.ict-societies.eu/ENTITY/community/32769");
			entityCISC = CtxModelObjectFactory.getInstance().createCommunityEntity(entityCISIDC, new Date(), new HashSet(), new HashSet(), new HashSet(), new HashSet());
			attrTemperatureCISBID = new CtxAttributeIdentifier("context://cis-1eebff13-9750-404a-8e3b-1a91b79cd5e8.ict-societies.eu/ENTITY/community/32768/ATTRIBUTE/temperature/30");
			attrTemperatureCISB = CtxModelObjectFactory.getInstance().createAttribute(attrTemperatureCISBID, new Date(), new Date(), "zzz");
			attrTemperatureCISCID = new CtxAttributeIdentifier("context://cis-1eebff13-9750-404a-8e3b-1a91b79cd5e9.ict-societies.eu/ENTITY/community/32769/ATTRIBUTE/temperature/30");
			attrTemperatureCISC = CtxModelObjectFactory.getInstance().createAttribute(attrTemperatureCISCID, new Date(), new Date(), "ooo");
		 */
		
		
		assocID = new CtxAssociationIdentifier("context://university.ict-societies.eu/ASSOCIATION/isMemberOf/2");
		//assoc = new CtxAssociation(assocID);
		//assoc.setParentEntity(entityCSSID);
		//assoc.addChildEntity(entityCISIDA);
		//assoc.addChildEntity(entityCISIDB);

		Set<CtxEntityIdentifier> childEntIDs = new HashSet<CtxEntityIdentifier>();
		childEntIDs.add(entityCISIDA);
		childEntIDs.add(entityCISIDB);
		
		assoc = CtxModelObjectFactory.getInstance().createAssociation(assocID, new Date(), entityCSSID, childEntIDs);

		// this will be used as a reply		
		Set<CtxAssociationIdentifier> mockSetOfAssocIds_IS_MEMBER_OF = new HashSet<CtxAssociationIdentifier>();
		mockSetOfAssocIds_IS_MEMBER_OF.add(assocID);
		
		
		mockIdentiyCSSID = new IIdentity() {

			@Override
			public IdentityType getType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getJid() {
				// TODO Auto-generated method stub
				return "context://john.societies.local/ENTITY/person/31";
			}

			@Override
			public String getIdentifier() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getDomain() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getBareJid() {
				// TODO Auto-generated method stub
				return "john@societies.local";
			}
		};

	
		
		

		
		System.out.println("ASSOC ID is: "+assocID);
		System.out.println("parent entity: "+assoc.getParentEntity());
		System.out.println("children entity: "+assoc.getChildEntities());

		System.out.println("before mocking entityCSSID :"+entityCSSID +" entityCSS :"+indiEntityCSS);
		//context://john.societies.local/ENTITY/person/31
		//org.societies.api.context.model.IndividualCtxEntity@77a9138

		System.out.println("cssIdString "+cssIdString +" mockcomMngr "+mockctxBroker);		
		//context://john.societies.local/ENTITY/person/31
		//Mock for ICtxBroker, hashCode: 1535375202

		Mockito.when(mockcomMngr.getIdManager()).thenReturn(idm);
		Mockito.when(idm.getThisNetworkNode()).thenReturn(netNode);
		Mockito.when(netNode.getBareJid()).thenReturn(mockIdentiyCSSID.getJid().toString());
		//Mockito.when(mockctxBroker.lookup(CtxModelType.ATTRIBUTE, "dianneConfidenceLevel")).thenReturn(new AsyncResult(new ArrayList<CtxIdentifier>()));

		Mockito.when(mockcomMngr.getIdManager()).thenReturn(idm);
		Mockito.when(idm.fromJid(cssIdString)).thenReturn(mockIdentiyCSSID);

		System.out.println("mockIdentity is "+mockIdentiyCSSID);
		System.out.println("indiEntity is "+ indiEntityCSS);

		Mockito.when(mockedCSS.getAssociations(CtxAssociationTypes.IS_MEMBER_OF)).thenReturn(mockSetOfAssocIds_IS_MEMBER_OF);
		
		System.out.println("The return size of mockedCSS is "+mockSetOfAssocIds_IS_MEMBER_OF.size());
		System.out.println("The object is "+mockSetOfAssocIds_IS_MEMBER_OF.iterator().next());
		

		System.out.println("mocked Broker:"+mockctxBroker + "   Mocked identity CSSid:" + mockIdentiyCSSID);	
		System.out.println("indiEntityCSS:" + indiEntityCSS.getId());

		Mockito.when(mockctxBroker.retrieveIndividualEntity(mockIdentiyCSSID)).thenReturn(new AsyncResult<IndividualCtxEntity>(indiEntityCSS));
		System.out.println("assoc : "+ assoc.getChildEntities());
		Mockito.when(mockctxBroker.retrieve(attrTemperatureCSSID)).thenReturn(new AsyncResult<CtxModelObject>(attrTemperatureCSS));
		
		
		//Set<CtxAssociationIdentifier> setOfCSSAssocIds = cssEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF);
		//Mockito.when(Mockito.any(CtxEntity.class).getAssociations()).thenReturn(mockSetOfAssocIds_IS_MEMBER_OF);

		System.out.println("0.... the indiEntityCSS from mock is "+ indiEntityCSS);
		//Mockito.when(mockctxBroker.retrieveIndividualEntity(Mockito.any(IIdentity.class)).get()).thenReturn(mockedIndividualEntity);
		//Mockito.when(mockedIndividualEntity.getAssociations()).thenReturn(mockedAssociationIds);

		userInheritance = new UserContextInheritanceMgr();
		userInheritance.setCtxBroker(mockctxBroker);
		userInheritance.setCommMngr(mockcomMngr);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testUserInheritanceMethod1() throws Exception {

		// entities and attributes created.... start testing...
		System.out.println("attributeCSSA "+ attrTemperatureCISA.getStringValue());
		System.out.println("entityCSS "+ indiEntityCSS.getId());

		System.out.println("attrTemperatureCISB :"+attrTemperatureCISB.getStringValue());
		System.out.println("attrTemperatureCISC :"+attrTemperatureCISC.getStringValue());
		System.out.println("entityCSS"+indiEntityCSS.getAttributes().size());

		System.out.println("attrTemperatureCISBID "+ attrTemperatureCISBID);


		assertEquals("context://john.societies.local/ENTITY/person/31",indiEntityCSS.getId().toString());

		IndividualCtxEntity indiEntRetrieved = this.mockctxBroker.retrieveIndividualEntity(mockIdentiyCSSID).get();

		assertEquals(indiEntityCSS.getId().toString(),indiEntRetrieved.getId().toString());
		System.out.println(" indiEntRetrieved attributes : "+indiEntRetrieved.getAttributes(CtxAttributeTypes.TEMPERATURE));
		/// ...
		//System.out.println("USER INH "+ userInheritance.communityInheritance(attrTemperatureCISBID));
		try {
			System.out.println(" START REAL TESTING" );
			System.out.println(" attrTemperatureCSSID: "+ attrTemperatureCSSID );
			
			CtxAttribute a = (CtxAttribute) userInheritance.communityInheritance(attrTemperatureCSSID);
			System.out.println(" result a:" +a);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	//@Test
	public void testUserInheritanceMethod2() {


	}

}