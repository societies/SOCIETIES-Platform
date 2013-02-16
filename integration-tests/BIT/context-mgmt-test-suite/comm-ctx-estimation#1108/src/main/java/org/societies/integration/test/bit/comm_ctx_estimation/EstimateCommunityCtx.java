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

	private IIdentity cssIDJane; 
	private IIdentity cssIDJohn;

	// run test in jane's container
	private String targetJane = "jane.societies.local";
	private String targetJohn= "john.societies.local";

	//private IndividualCtxEntity john;
	private IndividualCtxEntity jane;

	public ICtxBroker ctxBroker;
	public ICommManager commManager;
	public ICisManager cisManager;

	public void setUp(){

		LOG.info("EstimateCommunityCtx started");

	}

	@Test
	public void TestEstimateCommunityCtx() {
		
		LOG.info("TestCreateCommunityEntities");
		this.ctxBroker=Test1108.getCtxBroker();
		this.commManager= Test1108.getCommManager();
		this.cisManager = Test1108.getCisManager();
		
		LOG.info("Context broker service: "+ this.ctxBroker);
		LOG.info("comm manager service"+ this.commManager);
		//LOG.info("cisManager service"+ Test1108.getCisManager());



		try {
			this.cssIDJane =  this.commManager.getIdManager().fromJid(targetJane);
			this.cssIDJohn =  this.commManager.getIdManager().fromJid(targetJohn);

			// john's interest (remote comm will initiate)
			jane = this.ctxBroker.retrieveIndividualEntity(this.cssIDJane).get();
			List<CtxIdentifier> janeInterestList = this.ctxBroker.lookup(jane.getId(), CtxModelType.ATTRIBUTE,CtxAttributeTypes.INTERESTS).get();
			CtxAttribute janeInterestAttr = null;

			if( janeInterestList.size() == 0){
				janeInterestAttr = this.ctxBroker.createAttribute(jane.getId(), CtxAttributeTypes.INTERESTS).get();
			} else {
				janeInterestAttr = (CtxAttribute) this.ctxBroker.retrieve(janeInterestList.get(0)).get();
			}
			janeInterestAttr.setStringValue("reading,socialnetworking,cinema,sports");
			CtxAttribute janeInterestAttrUpdated = (CtxAttribute) this.ctxBroker.update(janeInterestAttr).get();


			// john's interest (remote comm will initiate)
			LOG.info("john identity : " + this.cssIDJohn.toString());
			CtxEntityIdentifier johnEntityID = this.ctxBroker.retrieveIndividualEntityId(null,this.cssIDJohn).get();

			LOG.info("john entity id : " + johnEntityID.toString());

			//List<CtxIdentifier> johnInterestList = this.ctxBroker.lookup(johnEntityID, CtxModelType.ATTRIBUTE,CtxAttributeTypes.INTERESTS).get();
			List<CtxIdentifier> johnInterestList = this.ctxBroker.lookup(null, this.cssIDJohn,CtxModelType.ATTRIBUTE,CtxAttributeTypes.INTERESTS).get();


			CtxAttribute johnInterestAttr = null;

			if( johnInterestList.size() == 0){
				johnInterestAttr = this.ctxBroker.createAttribute(johnEntityID, CtxAttributeTypes.INTERESTS).get();
			} else {
				johnInterestAttr = (CtxAttribute) this.ctxBroker.retrieve(johnInterestList.get(0)).get();
			}
			johnInterestAttr.setStringValue("cooking,horseRiding,restaurants,cinema");

			CtxAttribute johnInterestAttrUpdated = (CtxAttribute) this.ctxBroker.update(johnInterestAttr).get();

			// create CIS

			IIdentity cisID = this.createCIS();
			// at this point a community Entity should be created in janes container
			// at this point an association should be created in janes container
			LOG.info("wait until community entity is created for cisID"+ cisID  );
			Thread.sleep(40000);

			LOG.info("try to retrieve ctxCommunityEntity of cisID : " + cisID );

			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(cisID).get();
			LOG.info("ctxCommunityEntity id : " + ctxCommunityEntityIdentifier.toString());
			CommunityCtxEntity communityEntity = (CommunityCtxEntity) this.ctxBroker.retrieve(ctxCommunityEntityIdentifier).get();
			LOG.info("ctxCommunityEntity : " + communityEntity);

			LOG.info("ctxCommunityEntity members : " + communityEntity.getMembers());
			LOG.info("ctxCommunityEntity members size : " + communityEntity.getMembers().size());
			
			
			// the following lines will be removed with code adding a css member to the cis
			// adding john to community
			Set<CtxAssociationIdentifier> comAssocIdSet = communityEntity.getAssociations(CtxAssociationTypes.HAS_MEMBERS);
			LOG.info("ctxCommunityEntity members comAssocIdSet : " + comAssocIdSet);
			LOG.info("ctxCommunityEntity members comAssocIdSet size : " + comAssocIdSet.size());

			CtxAssociation hasMembersAssoc = null;

			if(comAssocIdSet != null  ){
				for(CtxAssociationIdentifier assocID : comAssocIdSet){
					hasMembersAssoc = (CtxAssociation) this.ctxBroker.retrieve(assocID).get();	
					LOG.info("hasMembersAssoc getChildEntities: " + hasMembersAssoc.getChildEntities());
					LOG.info("hasMembersAssoc size: " + hasMembersAssoc.getChildEntities().size());
					LOG.info("hasMembersAssoc getParentEntity: " + hasMembersAssoc.getParentEntity());

					hasMembersAssoc.addChildEntity(johnEntityID);
					hasMembersAssoc = (CtxAssociation) this.ctxBroker.update(hasMembersAssoc).get();
				}
			}
		
			CommunityCtxEntity communityEntityUpdated = (CommunityCtxEntity) this.ctxBroker.retrieve(ctxCommunityEntityIdentifier).get();
			LOG.info("Updated ctxCommunityEntity  : " + communityEntityUpdated.getMembers());
			LOG.info("Updated ctxCommunityEntity members : " + communityEntityUpdated.getMembers());

			// the upper lines will be removed with code adding a css member to the cis
			// a community now exists with two members jane (local) and john (remote)
			 
			
			// create a community attribute			
			CtxAttribute communityInterests = this.ctxBroker.createAttribute(communityEntityUpdated.getId(), CtxAttributeTypes.INTERESTS).get();
			LOG.info("**** communityInterests: " + communityInterests.getId());
			
			//estimate the community value and store to context db 
			CtxAttribute communityInterestsValue = this.ctxBroker.estimateCommunityContext(communityEntityUpdated.getId(), communityInterests.getId());
			LOG.info("**** communityInterests id : " + communityInterestsValue.getId());
			LOG.info("**** communityInterests value : " + communityInterestsValue.getStringValue());

			assertEquals("cinema",communityInterestsValue.getStringValue());
			
			
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


	protected  IIdentity createCIS() {

		IIdentity cisID = null;
		try {
			Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> (); 
			LOG.info("*** trying to create cis:");
			ICisOwned cisOwned = this.cisManager.createCis("testCIS", "cisType", cisCriteria, "nice CIS").get();
			LOG.info("*** cis created: "+cisOwned.getCisId());

			LOG.info("*** cisOwned " +cisOwned);
			LOG.info("*** cisOwned.getCisId() " +cisOwned.getCisId());
			String cisIDString  = cisOwned.getCisId();

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