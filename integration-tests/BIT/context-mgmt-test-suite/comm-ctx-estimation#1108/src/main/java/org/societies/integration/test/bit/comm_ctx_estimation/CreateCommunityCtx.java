package org.societies.integration.test.bit.comm_ctx_estimation;

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
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;


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

	private CtxAttributeIdentifier communityAttrInterestsId = null;
	private CtxEntityIdentifier communityCtxEntityID = null;


	private INetworkNode cssNodeId;
	private ICisManager cisManager;
	
	/*
	private String privacyPolicyWithoutRequestor  = "<RequestPolicy>" +
			"<Target>" +
			"<Resource>" +
			"<Attribute AttributeId=\"contextType\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">" +
			"<AttributeValue>fdsfsf</AttributeValue>" +
			"</Attribute>" +
			"</Resource>" +
			"<Action>" +
			"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants\">" +
			"<AttributeValue>WRITE</AttributeValue>" +
			"</Attribute>" +
			"<optional>false</optional>" +
			"</Action>" +
			"<Condition>" +
			"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants\">" +
			"<AttributeValue DataType=\"SHARE_WITH_3RD_PARTIES\">dfsdf</AttributeValue>" +
			"</Attribute>" +
			"<optional>true</optional>" +
			"</Condition>" +
			"<Condition>" +
			"<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" DataType=\"org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants\">" +
			"<AttributeValue DataType=\"DATA_RETENTION_IN_MINUTES\">412</AttributeValue>" +
			"</Attribute>" +
			"<optional>true</optional>" +
			"</Condition>" +
			"<optional>false</optional>" +
			"</Target>" +
			"</RequestPolicy>";
*/
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
			Test1108.getCtxBroker().update(individualAttr1);

			this.cssID2 =  Test1108.getCommManager().getIdManager().fromJid("coo@societies.local ");
			this.indiEnt2 = Test1108.getCtxBroker().createIndividualEntity(this.cssID2, CtxEntityTypes.PERSON).get();
			CtxAttribute individualAttr2 = Test1108.getCtxBroker().createAttribute(this.indiEnt2.getId() , CtxAttributeTypes.INTERESTS).get();
			individualAttr2.setStringValue("cooking,horseRiding,restaurants,cinema");

			this.cssID3 =  Test1108.getCommManager().getIdManager().fromJid("doo@societies.local ");
			this.indiEnt3 = Test1108.getCtxBroker().createIndividualEntity(this.cssID3, CtxEntityTypes.PERSON).get();
			CtxAttribute individualAttr3 = Test1108.getCtxBroker().createAttribute(this.indiEnt3.getId() , CtxAttributeTypes.INTERESTS).get();
			individualAttr3.setStringValue("cooking,horseRiding,socialnetworking,restaurants,cinema");

			communityEntity.addMember(this.indiEnt1.getId());
			communityEntity.addMember(this.indiEnt2.getId());
			communityEntity.addMember(this.indiEnt3.getId());

			Test1108.getCtxBroker().update(communityEntity);

			 CtxAttribute ctxAttr = Test1108.getCtxBroker().createAttribute(communityEntity.getId(),CtxAttributeTypes.INTERESTS).get();
		
			 this.communityAttrInterestsId = ctxAttr.getId();
			 this.communityCtxEntityID = communityEntity.getId();
			 
			 LOG.info("TestRetrieveCommunityEntities "+ctxAttr.getId() );
			 LOG.info("TestRetrieveCommunityEntities   this.communityAttrInterestsId "+ this.communityAttrInterestsId );
			 
			 
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
			CtxAttribute communityAttr1 = (CtxAttribute) Test1108.getCtxBroker().retrieveAttribute(this.communityAttrInterestsId, false).get();

			LOG.info(" communityAttr  "+communityAttr1.getId()); 
			LOG.info(" communityAttr  value "+ communityAttr1.getStringValue()+ " should be null");
	
			estimatedCommunityAttribute = Test1108.getCtxBroker().estimateCommunityContext(this.communityCtxEntityID, this.communityAttrInterestsId);
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
		ICisOwned cisOwned ;
	
		try {
			this.cssNodeId = Test1108.getCommManager().getIdManager().getThisNetworkNode();
			LOG.info("*** cssNodeId = " + this.cssNodeId);
			final String cssOwnerStr = this.cssNodeId.getBareJid();
			cssOwnerId = Test1108.getCommManager().getIdManager().fromJid(cssOwnerStr);
		
			Hashtable<String,MembershipCriteria> cisCriteria = new Hashtable<String,MembershipCriteria>();
			
			cisOwned = cisManager.createCis("testCIS", "cisType", cisCriteria, "nice CIS").get();
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