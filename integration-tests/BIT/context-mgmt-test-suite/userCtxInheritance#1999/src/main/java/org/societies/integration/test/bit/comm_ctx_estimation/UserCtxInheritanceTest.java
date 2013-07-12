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

public class UserCtxInheritanceTest {

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

		LOG.info("EstimateCommunityCtx started");

	}

	@Test
	public void TestEstimateCommunityCtx() {

		LOG.info("TestCreateCommunityEntities");
		this.ctxBroker=Test1999.getCtxBroker();
		this.commManager= Test1999.getCommManager();
		this.cisManager = Test1999.getCisManager();

		LOG.info("Context broker service: "+ this.ctxBroker);
		LOG.info("comm manager service"+ this.commManager);

		try {
			this.cssIDUniversity =  this.commManager.getIdManager().fromJid(targetUniversity);
			this.cssIDEmma =  this.commManager.getIdManager().fromJid(targetEmma);

			// setting uni attributes 

			// university interests 
			LOG.info("university's identity : " + this.cssIDUniversity.toString());
			//CtxEntityIdentifier universityEntityID = this.ctxBroker.retrieveIndividualEntityId(null, this.cssIDUniversity).get();

			LOG.info("Creating the temperature attribute for University");
			CtxAttribute temperatureUniversity = updateIntegerAttr(this.cssIDUniversity, CtxAttributeTypes.TEMPERATURE, 10 );
			assertEquals(temperatureUniversity.getType(), CtxAttributeTypes.TEMPERATURE);
			LOG.info("university's temperature created : " + temperatureUniversity.getId());

			LOG.info("Creating the interests attribute for University");
			CtxAttribute interestsUniversitys = updateStringAttr(this.cssIDUniversity, CtxAttributeTypes.INTERESTS,"reading,socialnetworking,cinema,sports" );
			assertEquals(interestsUniversitys.getType(), CtxAttributeTypes.INTERESTS);
			LOG.info("university's Interests created : " + interestsUniversitys.getId());
			
			CtxAttribute languagesUniversity = updateStringAttr(this.cssIDUniversity, CtxAttributeTypes.LANGUAGES, "English,Spanish,French" );
			assertEquals(languagesUniversity.getType(), CtxAttributeTypes.LANGUAGES);
			LOG.info("university's Languages created : " + languagesUniversity.getId());

			CtxAttribute locationUniversity = updateStringAttr(this.cssIDUniversity, CtxAttributeTypes.LOCATION_SYMBOLIC,"zoneA" );
			assertEquals(locationUniversity.getType(), CtxAttributeTypes.LOCATION_SYMBOLIC);
			LOG.info("university's location created : " + locationUniversity.getId());

			//String s = "125.6,127.7";
			/*CtxAttribute locationCoordUniversity = updateStringAttr(this.cssIDUniversity, CtxAttributeTypes.LOCATION_COORDINATES, "125.6,127.7" );
			//assertEquals(locationCoordUniversity.getType(), CtxAttributeTypes.LOCATION_COORDINATES);
			LOG.info("university's locationCoordinated created : " + locationCoordUniversity.getId());*/


			// E-M-M-A's interest (remote comm will be initiated)
			LOG.info("emma's identity : " + this.cssIDEmma.toString());
			LOG.info("Creating the temperature attribute for Emma");
			CtxAttribute temperatureEmma = updateIntegerAttr(this.cssIDEmma, CtxAttributeTypes.TEMPERATURE, 20 );
			assertEquals(temperatureEmma.getType(), CtxAttributeTypes.TEMPERATURE);
			LOG.info("emma's temperature created : " + temperatureEmma.getId());

			LOG.info("Creating the interests attribute for Emma");
			CtxAttribute interestsEmma =  updateStringAttr(this.cssIDEmma,CtxAttributeTypes.INTERESTS,"cooking,horseRiding,restaurants,cinema" );
			assertEquals(interestsEmma.getType(), CtxAttributeTypes.INTERESTS);
			LOG.info("emmas's INTERESTS created : " + interestsEmma.getId());

			CtxAttribute languagesEmma = updateStringAttr(this.cssIDEmma, CtxAttributeTypes.LANGUAGES, "English,German,Greek" );
			assertEquals(languagesEmma.getType(), CtxAttributeTypes.LANGUAGES);
			LOG.info("Emma's Languages created : " + languagesEmma.getId());

			CtxAttribute locationEmma  = updateStringAttr(this.cssIDEmma,CtxAttributeTypes.LOCATION_SYMBOLIC,"zoneA" );
			assertEquals(locationEmma.getType(), CtxAttributeTypes.LOCATION_SYMBOLIC);
			LOG.info("emmas's location created : " + locationEmma.getId());

			/*CtxAttribute locationCoordEmma = updateStringAttr(this.cssIDEmma, CtxAttributeTypes.LOCATION_COORDINATES,"10.5,60.5" );
			assertEquals(locationCoordEmma.getType(), CtxAttributeTypes.LOCATION_COORDINATES);
			LOG.info("Emma's location created : " + locationCoordEmma.getId());*/

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

			this.ctxBroker.createAttribute(communityEntity.getId(), CtxAttributeTypes.INTERESTS);
			this.ctxBroker.createAttribute(communityEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC);
			this.ctxBroker.createAttribute(communityEntity.getId(), CtxAttributeTypes.TEMPERATURE);
			this.ctxBroker.createAttribute(communityEntity.getId(), CtxAttributeTypes.LANGUAGES);
			//this.ctxBroker.createAttribute(communityEntity.getId(), CtxAttributeTypes.LOCATION_COORDINATES);


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

					CtxEntityIdentifier uniEntityID = this.ctxBroker.retrieveIndividualEntityId(null,this.cssIDUniversity).get();
					hasMembersAssoc.addChildEntity(uniEntityID);
					hasMembersAssoc = (CtxAssociation) this.ctxBroker.update(hasMembersAssoc).get();
				}
			}

			CommunityCtxEntity communityEntityUpdated = (CommunityCtxEntity) this.ctxBroker.retrieve(ctxCommunityEntityIdentifier).get();
			LOG.info("Updated ctxCommunityEntity : " + communityEntityUpdated.getMembers());
			LOG.info("Updated ctxCommunityEntity members : " + communityEntityUpdated.getMembers());
			// the upper lines will be removed with code adding a css member to the cis
			// a community now exists with two members university (local) and emma (remote)


			//test estimations 

			fetchCommunityValue(communityEntityUpdated.getId(), CtxAttributeTypes.INTERESTS);
			fetchCommunityValue(communityEntityUpdated.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC);
			fetchCommunityValue(communityEntityUpdated.getId(), CtxAttributeTypes.TEMPERATURE);
			fetchCommunityValue(communityEntityUpdated.getId(), CtxAttributeTypes.LANGUAGES);
			//fetchCommunityValue(communityEntityUpdated.getId(), CtxAttributeTypes.LOCATION_COORDINATES);

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


	private CtxAttribute updateStringAttr(IIdentity identity, String attributeType, String value){
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
				//attribute = (CtxAttribute) this.ctxBroker.retrieveAttribute((CtxAttributeIdentifier) attributeList.get(0), false).get();
				attribute = (CtxAttribute) this.ctxBroker.retrieve(attributeList.get(0)).get();
			}
			attribute.setStringValue(value);
			attributeUpdated = (CtxAttribute) this.ctxBroker.update(attribute).get();
			
			LOG.info("The attribute to be returned is "+ attributeUpdated);
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


	private CtxAttribute updateIntegerAttr(IIdentity identity, String attributeType, Integer value){
		CtxAttribute attributeUpdated = null;
		List<CtxIdentifier> attributeList;
		//List<CtxIdentifier> emmaInterestList = this.ctxBroker.lookup(null, this.cssIDEmma,CtxModelType.ATTRIBUTE,CtxAttributeTypes.INTERESTS).get();
		try {
			attributeList = this.ctxBroker.lookup(identity, CtxModelType.ATTRIBUTE,attributeType).get();
			CtxAttribute attribute = null;
			LOG.info("the attributeList size is aaaaa:"+attributeList.size());
			if( attributeList.size() == 0){

				CtxEntityIdentifier entityID = this.ctxBroker.retrieveIndividualEntityId(null, identity).get();
				attribute = this.ctxBroker.createAttribute(entityID, attributeType).get();

			} else {
				LOG.info("The attribute is :" + attributeList.get(0));
				attribute = (CtxAttribute) this.ctxBroker.retrieve(attributeList.get(0)).get();
				//attribute = (CtxAttribute) this.ctxBroker.retrieveAttribute((CtxAttributeIdentifier)attributeList.get(0), false).get();
				LOG.info("The attribute 2 is :" + attribute);
			}
			attribute.setIntegerValue(value);
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




	public void fetchCommunityValue(CtxEntityIdentifier communityEntityId, String ctxAttributeType){

		//String estimatedValue = null;

		LOG.info("**** lookup "+ ctxAttributeType); 
		List<CtxIdentifier> communityAttrList;
		try {
			communityAttrList = this.ctxBroker.lookup(communityEntityId, CtxModelType.ATTRIBUTE, ctxAttributeType).get();

			LOG.info("**** lookup community attribute results : " +communityAttrList);

			if(communityAttrList.size() > 0 ) {
				CtxAttributeIdentifier communityAttrId = (CtxAttributeIdentifier) communityAttrList.get(0);	

				LOG.info("**** INITIATE ESTIMATION ");
				//CtxAttribute communityAttribute = (CtxAttribute) this.ctxBroker.retrieve(communityAttrId).get();
				CtxAttribute communityAttribute = (CtxAttribute) this.ctxBroker.retrieveAttribute(communityAttrId, true).get();

				LOG.info("**** communityInterests id : " + communityAttribute.getId());
				LOG.info("  ******************** string outcome pairs...........  " + communityAttribute.getComplexValue().getPairs());
				LOG.info("  ******************** numeric outcome average...........  " + communityAttribute.getComplexValue().getAverage());
				LOG.info("  ******************** numeric outcome median...........  " + communityAttribute.getComplexValue().getMedian());
				LOG.info("  ******************** numeric outcome mode...........  " + communityAttribute.getComplexValue().getMode());
				LOG.info("  ******************** numeric outcome max...........  " + communityAttribute.getComplexValue().getRangeMax());
				LOG.info("  ******************** numeric outcome min...........  " + communityAttribute.getComplexValue().getRangeMin());

				//LOG.info("**** communityInterests value : " + communityInterestsAttribute.getStringValue());
				//estimatedValue = communityInterestsAttribute.getStringValue();
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



	protected IIdentity createCIS() {

		IIdentity cisID = null;
		try {
			Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> ();
			LOG.info("*** trying to create cis:");
			ICisOwned cisOwned = this.cisManager.createCis("testCIS5", "cisType", cisCriteria, "nice CIS").get();
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
