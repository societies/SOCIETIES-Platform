package org.societies.personalisation.CAUIPrediction.impl;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;


public class CommunityJoinMonitor implements CtxChangeEventListener{


	private static final Logger LOG = LoggerFactory.getLogger(CommunityJoinMonitor.class);
	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	private ICtxBroker ctxBroker;
	private final IIdentity ownerId;
	private ICommManager commMgr;

	/// check community estimation communities state 
	//private final Set<String> communities = new CopyOnWriteArraySet<String>();
	private final Set<CtxEntityIdentifier> communities = new CopyOnWriteArraySet<CtxEntityIdentifier>();


	public CommunityJoinMonitor(ICtxBroker ctxBrok, ICommManager commMngr) throws Exception{
		this.commMgr = commMngr;
		this.ctxBroker = ctxBrok;

		//fetch my CSS ID
		final String ownerIdStr = commMgr.getIdManager().getThisNetworkNode().getBareJid();
		this.ownerId = commMngr.getIdManager().fromJid(ownerIdStr);

		//fetch my CSS Entity
		//IndividualCtxEntity ownerEnt = this.ctxBroker.retrieveIndividualEntity(this.ownerId).get();

		CtxEntityIdentifier indiEntID = this.ctxBroker.retrieveIndividualEntityId(null, this.ownerId).get();
		List<CtxIdentifier> isMemberOfCtxIDList =  this.ctxBroker.lookup(indiEntID, CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();

		if(!isMemberOfCtxIDList.isEmpty() ){
			CtxAssociation isMemberOfAssoc = (CtxAssociation) this.ctxBroker.retrieve(isMemberOfCtxIDList.get(0)).get();	

			if (LOG.isDebugEnabled())
				LOG.debug("Registering for context changes related to CSS is_member_of association '"
						+ ownerId + "'");

			ctxBroker.registerForChanges(this, isMemberOfAssoc.getId());

		} else {
			LOG.error("Could not initialise: is_member_of_CISs association is null");
			throw new IllegalStateException("Could not initialise: is_member_of_CISs association is null");
		}
	}

	@Override
	public void onCreation(CtxChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(CtxChangeEvent event) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onModification(CtxChangeEvent event) {
		if (CtxAssociationTypes.IS_MEMBER_OF.equals(event.getId().getType())){

			if (LOG.isDebugEnabled())
				LOG.debug("joined cis event received : event.getId(): "+ event.getId() + " --- event.getSource():"+ event.getSource());
			CtxAssociationIdentifier assocID = (CtxAssociationIdentifier) event.getId(); 

			this.executorService.execute(new CssJoinedCommunityHandler(assocID));
		}
	}

	@Override
	public void onRemoval(CtxChangeEvent event) {
		// TODO Auto-generated method stub

	}



	private class CssJoinedCommunityHandler implements Runnable {

		private final CtxAssociationIdentifier ctxAssocId;

		private CssJoinedCommunityHandler(CtxAssociationIdentifier ctxAssocId) {

			this.ctxAssocId = ctxAssocId;
			if (LOG.isDebugEnabled())
				LOG.debug("CssJoinedCommunityHandler  1 "+ this.ctxAssocId );
			//CssJoinedCommunityHandler  1 context://university.ict-societies.eu/ASSOCIATION/isMemberOf/2 
		}

		@Override
		public void run() {
			//LOG.info("CssJoinedCommunityHandler  2 "+ this.ctxId );
			CtxEntityIdentifier cssEntId = null;
			try {
				final CtxAssociation isMemberOfAssoc = (CtxAssociation) ctxBroker.retrieve(this.ctxAssocId).get();

				if (isMemberOfAssoc == null) {
					LOG.error("Could not handle CIS membership change: "
							+ "Could not retrieve '" + this.ctxAssocId + "'");
					return;
				}

				if (LOG.isDebugEnabled())
					LOG.debug("CssJoinedCommunityHandler  3 "+ isMemberOfAssoc.getId() );
				final Set<CtxEntityIdentifier> currentCommunities = new HashSet<CtxEntityIdentifier>();

				for (final CtxEntityIdentifier cisCtxId : isMemberOfAssoc.getChildEntities()) {
					//final String communityId = cisCtxId.getOwnerId(); 
					currentCommunities.add(cisCtxId);
				}

				// find communities the user is no long member of
				final Set<CtxEntityIdentifier> oldCommunities = new HashSet<CtxEntityIdentifier>(communities);
				oldCommunities.removeAll(currentCommunities);
				if (LOG.isDebugEnabled())
					LOG.debug("CSS ctx Id " + isMemberOfAssoc.getParentEntity() + ", oldCommunities=" + oldCommunities);

				// find new communities the user is member of
				final Set<CtxEntityIdentifier> newCommunities = new HashSet<CtxEntityIdentifier>(currentCommunities);
				newCommunities.removeAll(communities);
				if (LOG.isDebugEnabled())
					LOG.debug("CSS ctx Id " + isMemberOfAssoc.getParentEntity() + ", newCommunities=" + newCommunities);

				// update communities the user is member of
				communities.clear();
				communities.addAll(currentCommunities);

				for (final CtxEntityIdentifier oldCommunity : oldCommunities){
					ctxBroker.unregisterFromChanges(new CACIModelEventHandler(), oldCommunity, CtxAttributeTypes.CACI_MODEL);
					if (LOG.isDebugEnabled())
						LOG.debug("UnRegistering for context changes related to CIS CACI_Model ctx attribute  for "+oldCommunity );
				}

				for (final CtxEntityIdentifier newCommunity : newCommunities) {
					ctxBroker.registerForChanges(new CACIModelEventHandler(), newCommunity , CtxAttributeTypes.CACI_MODEL);
					if (LOG.isDebugEnabled())
						LOG.debug("Registering for context changes related to CIS CACI_Model ctx attribute  for "+newCommunity );
				}

			} catch (Exception e) {
				LOG.error(" Exception while trying to register for context changes related to CIS CACI_Model ctx attribute of cis: '" +cssEntId +"' "+e.getLocalizedMessage() );
				e.printStackTrace();
			}
		}
	}




	private class CACIModelEventHandler implements CtxChangeEventListener{


		CACIModelEventHandler(){
			
		}

		@Override
		public void onCreation(CtxChangeEvent event) {
			

		}

		@Override
		public void onUpdate(CtxChangeEvent event) {
			
		}

		@Override
		public void onModification(CtxChangeEvent event) {
			
			if (CtxAttributeTypes.CACI_MODEL.equals(event.getId().getType())) 
				LOG.debug("update of caci model received  : event.getId(): "+ event.getId() + " --- event.getSource():"+ event.getSource());
			CtxIdentifier attributeCaciID = event.getId();

			if( attributeCaciID != null ){

				try {
					// retrieve caciModelAttr from CIS ctx DB
					CtxAttribute caciModelAttrRemote = (CtxAttribute) ctxBroker.retrieve(attributeCaciID).get();

					//store caciModel to local CSS ctx DB
					CtxAttribute caciModelAttrLocal = null;

					final String ownerIdStr = commMgr.getIdManager().getThisNetworkNode().getBareJid();
					IIdentity ownerId = commMgr.getIdManager().fromJid(ownerIdStr);
					CtxEntityIdentifier indiEntID = ctxBroker.retrieveIndividualEntityId(null, ownerId).get();
					
					// use local user entity id 
					List<CtxIdentifier> caciModelAttrLocalList = ctxBroker.lookup(indiEntID, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL).get();
					if (LOG.isDebugEnabled())
						LOG.debug("***onModification 2 caciModelAttrLocalList= " + caciModelAttrLocalList.size());
					
					//	LOG.info("***onModification 3 entityID= " + entityID.toString());
					if( caciModelAttrLocalList.isEmpty()){
						//	caciModelAttrLocal = ctxBroker.createAttribute(entityID, CtxAttributeTypes.CACI_MODEL).get();
						if (LOG.isDebugEnabled())
							LOG.debug("***onModification 4 ctxBroker.createAttribute= null ");
					} else {
						CtxAttributeIdentifier attrID = (CtxAttributeIdentifier) caciModelAttrLocalList.get(0);
						caciModelAttrLocal = (CtxAttribute) ctxBroker.retrieveAttribute(attrID, false).get();
						if (LOG.isDebugEnabled())
							LOG.debug("***onModification 5 ctxBroker.retrieveAttribute= " + caciModelAttrLocal.getId());
					}

					if(caciModelAttrRemote.getBinaryValue() != null){
						UserIntentModelData newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(caciModelAttrRemote.getBinaryValue(), this.getClass().getClassLoader());

						byte[] binaryModel = SerialisationHelper.serialise(newUIModelData); 
						caciModelAttrLocal.setBinaryValue(binaryModel);
						ctxBroker.update(caciModelAttrLocal);
						if (LOG.isDebugEnabled())
							LOG.debug("*** model  stored in = " + caciModelAttrLocal.getId());
					}

				} catch (Exception e) {
					LOG.error("Exception when handling modification event for new remote CACI model "+e.getLocalizedMessage());
					e.printStackTrace();
				} 
			}
		}

		@Override
		public void onRemoval(CtxChangeEvent event) {
			// TODO Auto-generated method stub

		}

		public IIdentity getOwnerId(){

			IIdentity cssOwnerId = null;
			try {
				final INetworkNode cssNodeId = commMgr.getIdManager().getThisNetworkNode();
				//LOG.info("*** cssNodeId = " + cssNodeId);
				final String cssOwnerStr = cssNodeId.getBareJid();
				cssOwnerId =  commMgr.getIdManager().fromJid(cssOwnerStr);
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return cssOwnerId;
		}
	}

}