package org.societies.personalisation.CAUIPrediction.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.springframework.beans.factory.annotation.Autowired;


public class CommunityJoinMonitor implements CtxChangeEventListener{


	private static final Logger LOG = LoggerFactory.getLogger(CommunityJoinMonitor.class);
	private ExecutorService executorService = Executors.newSingleThreadExecutor();


	private ICtxBroker ctxBroker;
	private final IIdentity ownerId;
	private ICommManager commMgr;
	
	/// check community estimation communities state 
	//private final Set<String> communities = new CopyOnWriteArraySet<String>();
	private final Set<String> communities = new CopyOnWriteArraySet<String>();

	//@Autowired (required=true)
	public CommunityJoinMonitor(ICtxBroker ctxBrok, ICommManager commMngr) throws Exception{
		this.commMgr = commMngr;
		this.ctxBroker = ctxBrok;

		//fetch my CSS ID
		final String ownerIdStr = commMgr.getIdManager().getThisNetworkNode().getBareJid();
		this.ownerId = commMngr.getIdManager().fromJid(ownerIdStr);

		//fetch my CSS Entity
		IndividualCtxEntity ownerEnt = ctxBrok.retrieveIndividualEntity(this.ownerId).get();

		//fetch is_Member_of association
		Set<CtxAssociationIdentifier> isMemberOfCISsSet = ownerEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF);


		if (isMemberOfCISsSet.isEmpty()){
			LOG.error("Could not initialise: is_member_of_CISs association is null");
			throw new IllegalStateException("Could not initialise: is_member_of_CISs association is null");
		}

		CtxAssociationIdentifier isMemberCISsID = isMemberOfCISsSet.iterator().next();
		if (LOG.isInfoEnabled())
			LOG.debug("Registering for context changes related to CSS is_member_of association '"
					+ ownerId + "'");
		ctxBroker.registerForChanges(this, isMemberCISsID);

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
		if (CtxAssociationTypes.IS_MEMBER_OF.equals(event.getId().getType())) 
			LOG.debug("joined cis event received : event.getId(): "+ event.getId() + " --- event.getSource():"+ event.getSource());
		this.executorService.execute(new CssJoinedCommunityHandler(event.getId()));

	}

	@Override
	public void onRemoval(CtxChangeEvent event) {
		// TODO Auto-generated method stub

	}



	private class CssJoinedCommunityHandler implements Runnable {

		private final CtxIdentifier ctxId;

		private CssJoinedCommunityHandler(CtxIdentifier ctxId) {

			this.ctxId = ctxId;
			LOG.debug("CssJoinedCommunityHandler  1 "+ this.ctxId );
			//CssJoinedCommunityHandler  1 context://university.ict-societies.eu/ASSOCIATION/isMemberOf/2 

		}

		@Override
		public void run() {
			//LOG.info("CssJoinedCommunityHandler  2 "+ this.ctxId );
			CtxEntityIdentifier cssEntId = null;
			try {
				CtxAssociation isMemberOfAssoc = (CtxAssociation) ctxBroker.retrieve(this.ctxId).get();
				LOG.debug("CssJoinedCommunityHandler  3 "+ isMemberOfAssoc.getId() );
				cssEntId = isMemberOfAssoc.getParentEntity();

				Set<CtxEntityIdentifier> cisEntIdSet = isMemberOfAssoc.getChildEntities();

				if(!cisEntIdSet.isEmpty() ){

					//TODO keep state of old/current communities
					// REMOVE for loop
					for(CtxEntityIdentifier cisEntityID : cisEntIdSet){

						LOG.debug("CssJoinedCommunityHandler  4 cisEntityID "+ cisEntityID );
						// register for new caci model update events
						
						ctxBroker.registerForChanges(new CACIModelEventHandler(), cisEntityID , CtxAttributeTypes.CACI_MODEL);

						if (LOG.isInfoEnabled())
							LOG.info("Registering for context changes related to CIS CACI_Model ctx attribute '");
					}
				}
			} catch (Exception e) {
				LOG.error(" Exception while trying to register for context changes related to CIS CACI_Model ctx attribute of cis: '" +cssEntId +"' "+e.getLocalizedMessage() );
				e.printStackTrace();
			}
		}
	}



	private class CACIModelEventHandler implements CtxChangeEventListener{


		CACIModelEventHandler(){
			LOG.debug("inside CACIModelEventHandler ------------------");
		}

		@Override
		public void onCreation(CtxChangeEvent event) {
					LOG.info("inside CACIModelEventHandler ------------------ onCreation "+event.getId());

		}

		@Override
		public void onUpdate(CtxChangeEvent event) {
				LOG.info("inside CACIModelEventHandler ------------------ onUpdate "+event.getId());
			//
		}

		@Override
		public void onModification(CtxChangeEvent event) {
				LOG.info("inside CACIModelEventHandler ------------------ onModification ");

			if (CtxAttributeTypes.CACI_MODEL.equals(event.getId().getType())) 
				LOG.debug("update of caci model received  : event.getId(): "+ event.getId() + " --- event.getSource():"+ event.getSource());
			CtxIdentifier attributeCaciID = event.getId();

			if( attributeCaciID != null ){

				try {
					// retrieve caciModelAttr from CIS ctx DB
					CtxAttribute caciModelAttrRemote = (CtxAttribute) ctxBroker.retrieveAttribute((CtxAttributeIdentifier) attributeCaciID,false).get();

					LOG.debug("*** remote caci attribute retrieved " + caciModelAttrRemote);
					LOG.debug("***onModification 1 caciModelAttrRemote= " + caciModelAttrRemote.getId());


					//store caciModel to local CSS ctx DB
					CtxAttribute caciModelAttrLocal = null;
					List<CtxIdentifier> caciModelAttrLocalList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL).get();
					LOG.debug("***onModification 2 caciModelAttrLocalList= " + caciModelAttrLocalList.size());
					IIdentity localcssID = getOwnerId();
					CtxEntityIdentifier entityID = ctxBroker.retrieveIndividualEntityId(null, localcssID).get();

					//	LOG.info("***onModification 3 entityID= " + entityID.toString());
					if( caciModelAttrLocalList.isEmpty()){
					//	caciModelAttrLocal = ctxBroker.createAttribute(entityID, CtxAttributeTypes.CACI_MODEL).get();
						LOG.debug("***onModification 4 ctxBroker.createAttribute= null ");
					} else {
						CtxAttributeIdentifier attrID = (CtxAttributeIdentifier) caciModelAttrLocalList.get(0);
						caciModelAttrLocal = (CtxAttribute) ctxBroker.retrieveAttribute(attrID, false).get();
						LOG.debug("***onModification 5 ctxBroker.retrieveAttribute= " + caciModelAttrLocal.getId());
					}
					
					if(caciModelAttrRemote.getBinaryValue() != null){
						UserIntentModelData newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(caciModelAttrRemote.getBinaryValue(), this.getClass().getClassLoader());

						byte[] binaryModel = SerialisationHelper.serialise(newUIModelData); 
						caciModelAttrLocal.setBinaryValue(binaryModel);
						ctxBroker.update(caciModelAttrLocal);
						LOG.debug("*** model  stored in = " + caciModelAttrLocal.getId());
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
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}

		@Override
		public void onRemoval(CtxChangeEvent event) {
			// TODO Auto-generated method stub

		}

		private IIdentity getOwnerId(){

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