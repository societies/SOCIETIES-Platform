package org.societies.integration.test.bit.comm_ctx_estimation;

import static org.junit.Assert.*;

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
import org.societies.api.context.model.CtxAssociationTypes;
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

public class EstimateCommunityCtx {

	private static Logger LOG = LoggerFactory.getLogger(EstimateCommunityCtx.class);

	private IIdentity cssIDUniversity;
	private IIdentity cssIDEmma;

	// run test in university's container
	private String targetUniversity = "university.ict-societies.eu";
	private String targetEmma= "emma.ict-societies.eu";

	//private IndividualCtxEntity emma;
	private IndividualCtxEntity university;

	public ICtxBroker ctxBroker;
	public ICommManager commManager;
	public ICisManager cisManager;

	public void setUp(){

		LOG.info("EstimateUserInheritance started");
		
	}

	@Test
	public void TestUserInheritance() {

		LOG.info("TestUserInheritance");
		this.ctxBroker=Test1108.getCtxBroker();
		this.commManager= Test1108.getCommManager();
		this.cisManager = Test1108.getCisManager();

		LOG.info("Context broker service: "+ this.ctxBroker);
		LOG.info("comm manager service"+ this.commManager);
		//LOG.info("cisManager service"+ Test1108.getCisManager());


		try {
			this.cssIDUniversity =  this.commManager.getIdManager().fromJid(targetUniversity);
			this.cssIDEmma =  this.commManager.getIdManager().fromJid(targetEmma);

			// university's interests 
			LOG.info("university's identity : " + this.cssIDUniversity.toString());
			//CtxEntityIdentifier universityEntityID = this.ctxBroker.retrieveIndividualEntityId(null, this.cssIDUniversity).get();

			CtxAttribute interestsUniversitys = updateIndividualAttribute(this.cssIDUniversity, CtxAttributeTypes.INTERESTS,"reading,socialnetworking,cinema,sports" );
			assertEquals(interestsUniversitys.getType(), CtxAttributeTypes.INTERESTS);
			LOG.info("university's interests created : " + interestsUniversitys.getId());

			CtxAttribute locationUniversity = updateIndividualAttribute(this.cssIDUniversity, CtxAttributeTypes.LOCATION_SYMBOLIC,"zoneA" );
			assertEquals(locationUniversity.getType(), CtxAttributeTypes.LOCATION_SYMBOLIC);
			LOG.info("university's location created : " + locationUniversity.getId());

			// emma's interest (remote comm will be initiated)
			LOG.info("emma's identity : " + this.cssIDEmma.toString());

			CtxAttribute interestsEmma =  updateIndividualAttribute(this.cssIDEmma,CtxAttributeTypes.INTERESTS,"cooking,horseRiding,restaurants,cinema" );
			assertEquals(interestsEmma.getType(), CtxAttributeTypes.INTERESTS);
			LOG.info("emmas's interest created : " + interestsEmma.getId());

			CtxAttribute locationEmma  = updateIndividualAttribute(this.cssIDEmma,CtxAttributeTypes.LOCATION_SYMBOLIC,"zoneA" );
			assertEquals(locationEmma.getType(), CtxAttributeTypes.LOCATION_SYMBOLIC);
			LOG.info("emmas's location created : " + locationEmma.getId());


			// create CIS
			IIdentity cisID = this.createCIS();
			// at this point a community Entity should be created in universitys container
			// at this point an association should be created in universitys container
			LOG.info("wait until community entity and attributes are created for cisID"+ cisID  );
			Thread.sleep(40000);
			
			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(cisID).get();
			LOG.info("ctxCommunityEntity id : " + ctxCommunityEntityIdentifier.toString());
			CommunityCtxEntity communityEntity = (CommunityCtxEntity) this.ctxBroker.retrieve(ctxCommunityEntityIdentifier).get();
			LOG.info("ctxCommunityEntity : " + communityEntity);

			LOG.info("ctxCommunityEntity members : " + communityEntity.getMembers());
			LOG.info("ctxCommunityEntity members size : " + communityEntity.getMembers().size());


			// the following lines will be removed with code adding a css member to the cis
			// adding emma to community
			Set<CtxAssociationIdentifier> comAssocIdSet = communityEntity.getAssociations(CtxAssociationTypes.HAS_MEMBERS);
			LOG.info("ctxCommunityEntity members comAssocIdSet : " + comAssocIdSet);
			LOG.info("ctxCommunityEntity members comAssocIdSet size : " + comAssocIdSet.size());

			CtxAssociation hasMembersAssoc = null;	

			if(comAssocIdSet != null ){
				for(CtxAssociationIdentifier assocID : comAssocIdSet){
					hasMembersAssoc = (CtxAssociation) this.ctxBroker.retrieve(assocID).get();	
					LOG.info("hasMembersAssoc getChildEntities: " + hasMembersAssoc.getChildEntities());
					LOG.info("hasMembersAssoc size: " + hasMembersAssoc.getChildEntities().size());
					LOG.info("hasMembersAssoc getParentEntity: " + hasMembersAssoc.getParentEntity());

					CtxEntityIdentifier emmaEntityID = this.ctxBroker.retrieveIndividualEntityId(null,this.cssIDEmma).get();
					hasMembersAssoc.addChildEntity(emmaEntityID);
					hasMembersAssoc = (CtxAssociation) this.ctxBroker.update(hasMembersAssoc).get();
				}
			}

			CommunityCtxEntity communityEntityUpdated = (CommunityCtxEntity) this.ctxBroker.retrieve(ctxCommunityEntityIdentifier).get();
			LOG.info("Updated ctxCommunityEntity : " + communityEntityUpdated.getMembers());
			LOG.info("Updated ctxCommunityEntity members : " + communityEntityUpdated.getMembers());

			// the upper lines will be removed with code adding a css member to the cis
			// a community now exists with two members university (local) and emma (remote)
			String interestValue = fetchCommunityValue(communityEntityUpdated.getId(), CtxAttributeTypes.INTERESTS);
			assertEquals("cinema", interestValue);	

			// String locationSymbolicValue = fetchCommunityValue(communityEntityUpdated.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC);
			// assertEquals("zoneA", locationSymbolicValue);



		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	

	private CtxAttribute updateIndividualAttribute(IIdentity identity, String attributeType, String value){
	
		CtxAttribute attributeUpdated = null;
		List<CtxIdentifier> attributeList;
		//List<CtxIdentifier> emmaInterestList = this.ctxBroker.lookup(null, this.cssIDEmma,CtxModelType.ATTRIBUTE,CtxAttributeTypes.INTERESTS).get();

		
		try {
			
			
			attributeList = this.ctxBroker.lookup(identity, CtxModelType.ATTRIBUTE,attributeType).get();
		
			CtxAttribute attribute = null;

			if( attributeList.size() == 0){
				
				CtxEntityIdentifier entityID = this.ctxBroker.retrieveIndividualEntityId(null, identity).get();
				attribute = this.ctxBroker.createAttribute(entityID, attributeType).get();
			
			} else {
				attribute = (CtxAttribute) this.ctxBroker.retrieve(attributeList.get(0)).get();
			}
			attribute.setStringValue(value);
			
			attributeUpdated = (CtxAttribute) this.ctxBroker.update(attribute).get();
		
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
	
		return attributeUpdated;
	}
	
	
	


	public String fetchCommunityValue(CtxEntityIdentifier communityEntityId, String ctxAttributeType){

		String estimatedValue = null;

		LOG.info("**** lookup communityInterests: "); 
		List<CtxIdentifier> communityInterestsList;
		try {
			communityInterestsList = this.ctxBroker.lookup(communityEntityId, CtxModelType.ATTRIBUTE, ctxAttributeType).get();

			LOG.info("**** lookup community attribute results : " +communityInterestsList);

			if(communityInterestsList.size() > 0 ) {
				CtxAttributeIdentifier communityAttrId = (CtxAttributeIdentifier) communityInterestsList.get(0);	

				CtxAttribute communityInterestsAttribute = (CtxAttribute) this.ctxBroker.retrieve(communityAttrId).get();

				LOG.info("**** communityInterests id : " + communityInterestsAttribute.getId());
				LOG.info("**** communityInterests value : " + communityInterestsAttribute.getStringValue());
				estimatedValue = communityInterestsAttribute.getStringValue();
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
		
		return estimatedValue;
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

cisID = this.commManager.getIdManager().fromJid(cisIDString);

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
