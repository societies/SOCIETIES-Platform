package org.societies.integration.test.bit.comm_ctx_estimation;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

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
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;


import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;


public class CreateCommunityCtx {

	private static Logger LOG = LoggerFactory.getLogger(Test1108.class);
	

	//	private IIdentity cisID;
	private IIdentity cssID1; 
	private IIdentity cssID2;
	private IIdentity cssID3;


	//private CommunityCtxEntity communityEntity;
	
	private IndividualCtxEntity indiEnt1;
	private IndividualCtxEntity indiEnt2;
	private IndividualCtxEntity indiEnt3;

	private CtxAttributeIdentifier communityAttrTemperatureId = null;
	private CtxAttributeIdentifier communityAttrInterestsId = null;
	private CtxEntityIdentifier communityCtxEntityID = null;


	private INetworkNode cssNodeId;
	private ICisManager cisManager;
	
		String cssPassword = "password.societies.local";

	public void setUp(){

		LOG.info("CreateCommunityCtx started");
	//	LOG.info("Context broker service: "+ Test1108.getCtxBroker());
	//	LOG.info("comm manager service"+ Test1108.getCommManager());
	//	LOG.info("cisManager service"+ Test1108.getCisManager());

	}


	@Test
	public void TestCreateCommunityEntities() {

		LOG.info("TestCreateCommunityEntities");

		LOG.info("Context broker service: "+ Test1108.getCtxBroker());
		LOG.info("comm manager service"+ Test1108.getCommManager());
		LOG.info("cisManager service"+ Test1108.getCisManager());

		CommunityCtxEntity communityEntity = null;

		try {
			IIdentity cisID = createCISid();
			LOG.info("Cis id "+cisID);

			communityEntity = Test1108.getCtxBroker().createCommunityEntity(cisID).get();

			this.cssID1 =  Test1108.getCommManager().getIdManager().fromJid("boo@societies.local ");
			this.indiEnt1 = Test1108.getCtxBroker().createIndividualEntity(this.cssID1, CtxEntityTypes.PERSON).get();

			CtxAttribute individualAttr1 = Test1108.getCtxBroker().createAttribute(this.indiEnt1.getId() , CtxAttributeTypes.INTERESTS).get();
			individualAttr1.setStringValue("reading,socialnetworking,cinema,sports");
			individualAttr1.setValueType(CtxAttributeValueType.STRING);

			CtxAttribute individualAttr2 = Test1108.getCtxBroker().createAttribute(this.indiEnt1.getId() , CtxAttributeTypes.TEMPERATURE).get();
			individualAttr2.setValueType(CtxAttributeValueType.INTEGER);
			individualAttr2.setIntegerValue(25);

			Test1108.getCtxBroker().update(individualAttr1);
			Test1108.getCtxBroker().update(individualAttr2);

			this.cssID2 =  Test1108.getCommManager().getIdManager().fromJid("coo@societies.local ");
			this.indiEnt2 = Test1108.getCtxBroker().createIndividualEntity(this.cssID2, CtxEntityTypes.PERSON).get();

			CtxAttribute individualAttr3 = Test1108.getCtxBroker().createAttribute(this.indiEnt2.getId() , CtxAttributeTypes.INTERESTS).get();
			individualAttr3.setStringValue("cooking,horseRiding,restaurants,cinema");
			individualAttr3.setValueType(CtxAttributeValueType.STRING);

			CtxAttribute individualAttr4 = Test1108.getCtxBroker().createAttribute(this.indiEnt2.getId() , CtxAttributeTypes.TEMPERATURE).get();
			individualAttr4.setIntegerValue(27);
			individualAttr4.setValueType(CtxAttributeValueType.INTEGER);

			Test1108.getCtxBroker().update(individualAttr3);
			Test1108.getCtxBroker().update(individualAttr4);

			this.cssID3 =  Test1108.getCommManager().getIdManager().fromJid("doo@societies.local ");
			this.indiEnt3 = Test1108.getCtxBroker().createIndividualEntity(this.cssID3, CtxEntityTypes.PERSON).get();

			CtxAttribute individualAttr5 = Test1108.getCtxBroker().createAttribute(this.indiEnt3.getId() , CtxAttributeTypes.INTERESTS).get();
			individualAttr5.setStringValue("cooking,horseRiding,socialnetworking,restaurants,cinema");
			individualAttr5.setValueType(CtxAttributeValueType.STRING);

			CtxAttribute individualAttr6 = Test1108.getCtxBroker().createAttribute(this.indiEnt3.getId() , CtxAttributeTypes.TEMPERATURE).get();
			individualAttr6.setIntegerValue(27);
			individualAttr6.setValueType(CtxAttributeValueType.INTEGER);

			Test1108.getCtxBroker().update(individualAttr5);
			Test1108.getCtxBroker().update(individualAttr6);

			communityEntity.addMember(this.indiEnt1.getId());
			communityEntity.addMember(this.indiEnt2.getId());
			communityEntity.addMember(this.indiEnt3.getId());

			Test1108.getCtxBroker().update(communityEntity);

			CtxAttribute ctxAttr = Test1108.getCtxBroker().createAttribute(communityEntity.getId(), CtxAttributeTypes.TEMPERATURE).get();
			CtxAttribute ctxAttr1 = Test1108.getCtxBroker().createAttribute(communityEntity.getId(), CtxAttributeTypes.INTERESTS).get();

			this.communityAttrTemperatureId = ctxAttr.getId();
			this.communityCtxEntityID = communityEntity.getId();
			this.communityAttrInterestsId = ctxAttr1.getId();

			//this.communityAttrTemperatureId = ctxAttr1.getId();

			// LOG.info("TestRetrieveCommunityEntities "+ctxAttr.getId() );
			LOG.info("TestRetrieveCommunityEntities   this.communityAttrTemperatureId "+ this.communityAttrTemperatureId );
			LOG.info("TestRetrieveCommunityEntities   this.communityInterestsId "+ this.communityAttrInterestsId);


			retrieveCommunityEntities();
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


	
	public void retrieveCommunityEntities() {

		LOG.info("TestRetrieveCommunityEntities");
		CtxAttribute estimatedCommunityAttribute;
		
		try {
			// at this point communityAttrInterests is created and assigned to communityEntity but has a null value 
			LOG.info("communityAttrInterestsId " + this.communityAttrInterestsId);
			LOG.info("communityAttrInterestsId getOwnerId " + this.communityAttrInterestsId.getOwnerId());
			CtxAttribute communityTempr = (CtxAttribute) Test1108.getCtxBroker().retrieveAttribute(this.communityAttrTemperatureId, false).get();
			CtxAttribute communityInter = (CtxAttribute) Test1108.getCtxBroker().retrieveAttribute(this.communityAttrInterestsId, false).get();
			LOG.info(" communityAttr  "+communityTempr.getId()); 
			LOG.info(" communityAttr  value "+ communityInter.getStringValue()+ " should be null");
	
			LOG.info(" The estimation for the community Temperature begins, ");
			estimatedCommunityAttribute = Test1108.getCtxBroker().estimateCommunityContext(this.communityCtxEntityID, this.communityAttrTemperatureId);
			LOG.info(" estimatedCommunityAttribute getString ID:  "+estimatedCommunityAttribute.getId()+" should not be null ");
			LOG.info(" estimatedCommunityAttribute getString value:  "+estimatedCommunityAttribute.getDoubleValue() +" should not be null ");
			
			LOG.info(" The estimation for the community Interests begins, ");
			estimatedCommunityAttribute = Test1108.getCtxBroker().estimateCommunityContext(this.communityCtxEntityID, this.communityAttrInterestsId);
			LOG.info(" estimatedCommunityAttribute getString ID:  "+estimatedCommunityAttribute.getId()+" should not be null ");
			LOG.info(" estimatedCommunityAttribute getString value:  "+estimatedCommunityAttribute.getStringValue() +" should not be null ");
			
			// TODO second version of the test
			//retrieving attribute with flag true should initiate the inference process.
			//inference process will assign a community context value to community attribute
					
			//CtxAttribute communityAttr2 = (CtxAttribute) Test1108.getCtxBroker().retrieveAttribute(this.communityAttrInterestsId, true).get();
			//LOG.info(" trigger inference ");
			//LOG.info(" communityAttr  "+communityAttr2.getId()); 
			//LOG.info(" communityAttr  value "+ communityAttr2.getStringValue());
		
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
	
	// helper classes
	
	protected  IIdentity createCISid() {

		IIdentity cisID = null;
		IIdentity cssOwnerId = null;
		ICisOwned cisOwned ;
	
		try {
			this.cssNodeId = Test1108.getCommManager().getIdManager().getThisNetworkNode();
			LOG.info("*** cssNodeId = " + this.cssNodeId);
			final String cssOwnerStr = this.cssNodeId.getBareJid();
			cssOwnerId = Test1108.getCommManager().getIdManager().fromJid(cssOwnerStr);
		
			//Hashtable<String,MembershipCriteria> cisCriteria = new Hashtable<String,MembershipCriteria>();
		
			
			Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> (); 
			MembershipCriteria m = new MembershipCriteria();
			try{
				Rule r = new Rule("equals",new ArrayList<String>(Arrays.asList("married")));
				m.setRule(r);
				cisCriteria.put(CtxAttributeTypes.STATUS, m);
				r = new Rule("equals",new ArrayList<String>(Arrays.asList("Brazil")));
				m.setRule(r);
				cisCriteria.put(CtxAttributeTypes.ADDRESS_HOME_COUNTRY, m);
			}catch(InvalidParameterException e){
				// TODO: treat expection
				e.printStackTrace();
			}
			
			LOG.info("*** trying to create cis : ");
			cisOwned = Test1108.getCisManager().createCis("CIS_NAME", "password.societies.local" , cisCriteria,"stringArg3").get();
			
			LOG.info("*** cis created: "+cisOwned.getCisId());
			
			//LOG.info("*** cis list: "+cisManager.getCisList());
			//			cisManager.createCis(arg0, arg1, arg2, arg3)
			
			 //CisOwned ciss =  (cisManager.createCis(cssOwnerStr, "password.societies.local","cisNAme", null , TEST_CIS_MODE)).get();
			
			
			//.createCis("testCIS", "cisType", cisCriteria, "nice CIS").get();
			
			//cisOwned = cisManager.getOwnedCis(arg0);
			LOG.info("*** cisOwned " +cisOwned);
			String cisIDString  = cisOwned.getCisId();
			LOG.info("*** cisOwned.getCisId() " +cisIDString);
			cisID = Test1108.getCommManager().getIdManager().fromJid(cisIDString);
			
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cisID;
	}
}