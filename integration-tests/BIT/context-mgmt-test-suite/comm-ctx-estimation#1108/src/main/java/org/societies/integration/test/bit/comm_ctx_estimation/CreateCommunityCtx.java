package org.societies.integration.test.bit.comm_ctx_estimation;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
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
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;


public class CreateCommunityCtx {

	private static Logger LOG = LoggerFactory.getLogger(Test1108.class);

	private IIdentity cssIDUniversity; 
	private IIdentity cssIDEmma;

	//private CommunityCtxEntity communityEntity;
	private String targetUniversity = "University.ict-societies.eu";
	private String targetEmma = "emma.ict-societies.eu";
	
	private IndividualCtxEntity University;
	private IndividualCtxEntity Emma;

	private CtxAttributeIdentifier communityAttrTemperatureId = null;
	private CtxAttributeIdentifier communityAttrInterestsId = null;
	private CtxEntityIdentifier communityCtxEntityID = null;

	public ICtxBroker ctxBroker;
	public ICisManager cisManager;
	public ICommManager commManager;
	//private INetworkNode cssNodeId;

	//String cssPassword = "password.ict-societies.eu";

	public void setUp(){

		LOG.info("CreateCommunityCtx started");

	}

	@Test
	public void TestCreateCommunityEntities() {

		LOG.info("TestCreateCommunityEntities");
		this.ctxBroker=Test1108.getCtxBroker();
		this.commManager= Test1108.getCommManager();
		this.cisManager = Test1108.getCisManager();
		
		LOG.info("Context broker service: "+ this.ctxBroker);
		LOG.info("comm manager service"+ this.commManager);
		LOG.info("cisManager service"+ this.cisManager);

		CommunityCtxEntity communityEntity = null;

		try {
			this.cssIDUniversity = this.commManager.getIdManager().fromJid(targetUniversity);
			this.cssIDEmma = this.commManager.getIdManager().fromJid(targetEmma);
			
			University = this.ctxBroker.retrieveIndividualEntity(this.cssIDUniversity).get();
			List<CtxIdentifier> UniversityInterestList = this.ctxBroker.lookup(University.getId(), CtxModelType.ATTRIBUTE, CtxAttributeTypes.INTERESTS).get();
			CtxAttribute UniversityInterestAttr = null;
			
			if (UniversityInterestList.size() ==0){
				UniversityInterestAttr = this.ctxBroker.createAttribute(University.getId(), CtxAttributeTypes.INTERESTS).get();
			} else {
				UniversityInterestAttr = (CtxAttribute) this.ctxBroker.retrieve(UniversityInterestList.get(0)).get();
			}
			UniversityInterestAttr.setStringValue("reading,socialnetworking,cinema,sports");
			CtxAttribute UniversityInterestsAttrUpdated = (CtxAttribute) this.ctxBroker.update(UniversityInterestAttr).get();
			
			// emma's interest (remote comm will initiate)
			LOG.info("emma identity : " + this.cssIDEmma.toString());
			CtxEntityIdentifier emmaEntityID = this.ctxBroker.retrieveIndividualEntityId(null, this.cssIDEmma).get();
			LOG.info("emma entity id : " + emmaEntityID.toString());
			//List<CtxIdentifier> emmaInterestList = this.ctxBroker.lookup(emmaEntityID, CtxModelType.ATTRIBUTE,CtxAttributeTypes.INTERESTS).get();
			List<CtxIdentifier> emmaInterestList = this.ctxBroker.lookup(null, this.cssIDEmma, CtxModelType.ATTRIBUTE,CtxAttributeTypes.INTERESTS).get();
			
			CtxAttribute emmaInterestAttr = null;
			
			if( emmaInterestList.size() == 0){
				emmaInterestAttr = this.ctxBroker.createAttribute(emmaEntityID, CtxAttributeTypes.INTERESTS).get();
			} else {
				emmaInterestAttr = (CtxAttribute) this.ctxBroker.retrieve(emmaInterestList.get(0)).get();
			}
			emmaInterestAttr.setStringValue("cooking,horseRiding,restaurants,cinema");

			CtxAttribute emmaInterestAttrUpdated = (CtxAttribute) this.ctxBroker.update(emmaInterestAttr).get();
			
			//Create CIS
			
			IIdentity cisID = this.createCIS();
			LOG.info("wait until community entity is created for cisID"+ cisID  );
			Thread.sleep(20000);
			
			LOG.info("try to retrieve ctxCommunityEntity of cisID : " + cisID );
			
//			//IIdentity cisID = Test1108.getCommManager().getIdManager().fromJid("cis-05ecbe3d-9577-445d-a652-a3ea2beeb7f2.ict-societies.eu");
//			//IIdentity cisID = ('cis-05ecbe3d-9577-445d-a652-a3ea2beeb7f2.ict-societies.eu');
//			//LOG.info("#@#@# Cis id "+cisID);
//			LOG.info("#@#@# Test id "+cisID); //cis-05ecbe3d-9577-445d-a652-a3ea2beeb7f2.ict-societies.eu
//			//communityEntity = Test1108.getCtxBroker().createCommunityEntity(cisID).get();
//			communityEntity = Test1108.getCtxBroker().createCommunityEntity(cisID).get();
//			LOG.info("gCommunity Entity Created");
//			LOG.info("g00");		
			
			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(cisID).get();
			LOG.info("ctxCommunityEntity id : " + ctxCommunityEntityIdentifier.toString());
			
			communityEntity = (CommunityCtxEntity) this.ctxBroker.retrieve(ctxCommunityEntityIdentifier).get();
			LOG.info("ctxCommunityEntity members : " + communityEntity.getMembers());
			
			// only university is member of the cis community, manually add emma
			// this should be removed
			
			if (communityEntity.getMembers().size()==1){
				Set<CtxAssociationIdentifier> comAssocIdSet = communityEntity.getAssociations(CtxAssociationTypes.HAS_MEMBERS);
				CtxAssociation hasMembersAssoc = null;
				if (comAssocIdSet.size() >0){
					for (CtxAssociationIdentifier assocID:comAssocIdSet){
						hasMembersAssoc = (CtxAssociation) this.ctxBroker.retrieve(assocID).get();
					}
					hasMembersAssoc.addChildEntity(emmaEntityID);
					hasMembersAssoc = (CtxAssociation) this.ctxBroker.update(hasMembersAssoc).get(); 
				}
			}
			
			CommunityCtxEntity communityEntityUpdated = (CommunityCtxEntity) this.ctxBroker.retrieve(ctxCommunityEntityIdentifier).get();
			LOG.info("Updated ctxCommunityEntity members : " + communityEntityUpdated.getMembers());
			
			CtxAttribute communityInterests = this.ctxBroker.createAttribute(communityEntityUpdated.getId(), CtxAttributeTypes.INTERESTS).get();
			LOG.info("**** communityInterests: " + communityInterests.getId());
			
			CtxAttribute communityInterestsValue = this.ctxBroker.estimateCommunityContext(communityEntityUpdated.getId(), communityInterests.getId());
			LOG.info("**** communityInterests id : " + communityInterestsValue.getId());
			LOG.info("**** communityInterests value : " + communityInterestsValue.getStringValue());
			

		
			retrieveCommunityEntities();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.info("interrupted exception");
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.info("execution exception");
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.info("invalid format exception");
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

	protected  IIdentity createCIS() {

		IIdentity cisID = null;

		try {
			Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> ();
			LOG.info("*** trying to create cis : ");
			ICisOwned cisOwned = this.cisManager.createCis("testCIS", "cisType", cisCriteria, "nice CIS").get();
			LOG.info("*** cis created: "+cisOwned.getCisId());
			
			cisOwned = cisManager.createCis("testCIS", "cisType", cisCriteria, "nice CIS").get();
			LOG.info("*** cisOwned " +cisOwned);
			LOG.info("*** cisOwned.getCisId() " +cisOwned.getCisId());
			String cisIDString  = cisOwned.getCisId();
			
			cisID = this.commManager.getIdManager().fromJid(cisIDString);
			
//			MembershipCriteria m = new MembershipCriteria();
//			try{
//				Rule r = new Rule("equals",new ArrayList<String>(Arrays.asList("married")));
//				m.setRule(r);
//				cisCriteria.put(CtxAttributeTypes.STATUS, m);
//				r = new Rule("equals",new ArrayList<String>(Arrays.asList("Brazil")));
//				m.setRule(r);
//				cisCriteria.put(CtxAttributeTypes.ADDRESS_HOME_COUNTRY, m);
//			}catch(InvalidParameterException e){
//				// TODO: treat expection
//				e.printStackTrace();
//			}


		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
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