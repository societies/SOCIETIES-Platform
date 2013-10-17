package org.societies.personalisation.CACIDiscovery.impl;

import java.util.Set;
//import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.springframework.beans.factory.annotation.Autowired;



public class CACICommunityMembershipChangeMonitor implements CtxChangeEventListener{

	private static final Logger LOG = LoggerFactory.getLogger(CACICommunityMembershipChangeMonitor.class);
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private ICtxBroker ctxBroker;
	private final IIdentity ownerId;
	private ICommManager commMgr;
	//private final Set<String> communities = new CopyOnWriteArraySet<String>();


	@Autowired (required=true)
	public CACICommunityMembershipChangeMonitor(ICtxBroker ctxBrok, ICommManager commMngr) throws Exception{
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
		if (LOG.isDebugEnabled())
			LOG.debug("Registering for context changes related to CSS is_member_of association '"
					+ ownerId + "'");
		ctxBroker.registerForChanges(this, isMemberCISsID);


		/*
		CtxAssociation ownsCISsAssoc = (CtxAssociation) ctxBrok.retrieve(isMemberCISsID).get();
		Set<CtxEntityIdentifier> communitiesEntitiesId = ownsCISsAssoc.getChildEntities();

		for (CtxEntityIdentifier comEntityId:communitiesEntitiesId){
			String cisIdStr = comEntityId.getOwnerId();
			IIdentity cisId = commMngr.getIdManager().fromJid(cisIdStr);
			this.registerMembershipChanges(cisId);
			communities.add(cisIdStr);
		}
		if (LOG.isDebugEnabled())
			LOG.debug("communities=" + communities);	 
*/
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
		if (CtxAssociationTypes.IS_MEMBER_OF.equals(event.getId().getType())) // TODO change OWNS_COMMUNITIES
			if (LOG.isDebugEnabled()) LOG.debug("joined cis event received : event.getId(): "+ event.getId() + " --- event.getSource():"+ event.getSource());
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
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
		}
	}

	
	private class CommunityHasMembersHandler implements Runnable {
		private final CtxEntityIdentifier communityEntId;

		private CommunityHasMembersHandler(final CtxEntityIdentifier communityEntId) {
			this.communityEntId = communityEntId;
		}

		@Override
		public void run() {
			if (LOG.isDebugEnabled())
				LOG.debug("Retrieving community Entity '" + communityEntId
						+ "' to handle membership change of CIS '" + communityEntId.getOwnerId() + "'");

			try {
				CommunityCtxEntity communityEnt = (CommunityCtxEntity) ctxBroker.retrieve(this.communityEntId).get();
				if (communityEnt == null) {
					LOG.error("Could not handle membership change of CIS '"
							+ this.communityEntId.getOwnerId() + "': '"
							+ communityEnt + "' is null");
					return;
				}

			} catch (Exception e) {
				LOG.error("Could not handle membership change of CIS '"
						//+ this.listener.communityId + "': "	
						+ e.getLocalizedMessage(), e);			}
		}		

	}


}
