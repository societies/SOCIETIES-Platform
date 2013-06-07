package org.societies.personalisation.CACIDiscovery.impl;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.springframework.beans.factory.annotation.Autowired;



public class CACICommunityMembershipChangeMonitor implements CtxChangeEventListener{

	private static final Logger LOG = LoggerFactory.getLogger(CACICommunityMembershipChangeMonitor.class);
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private ICtxBroker ctxBroker;
	private final IIdentity ownerId;
	private ICommManager commMgr;
	private final Set<String> communities = new CopyOnWriteArraySet<String>();

	
	@Autowired (required=true)
	public CACICommunityMembershipChangeMonitor(ICtxBroker ctxBrok, ICommManager commMngr) throws Exception{
		this.commMgr = commMngr;
		this.ctxBroker = ctxBrok;

		//fetch my CSS ID
		final String ownerIdStr = commMgr.getIdManager().getThisNetworkNode().getBareJid();
		this.ownerId = commMngr.getIdManager().fromJid(ownerIdStr);

		//fetch my CSS Entity
		IndividualCtxEntity ownerEnt = ctxBrok.retrieveIndividualEntity(this.ownerId).get();

		//fetch OWNS communities
		Set<CtxAssociationIdentifier> ownsCISsSet = ownerEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF);
		// TODO The "IS_MEMBER_OF will change in "OWNS_COMMUNITIES"

		if (ownsCISsSet.isEmpty()){
			LOG.error("Could not initialise: ownsCISs association is null");
			throw new IllegalStateException("Could not initialise: ownsCISs association is null");
		}

		CtxAssociationIdentifier ownsCISsID = ownsCISsSet.iterator().next();
		if (LOG.isInfoEnabled())
			LOG.info("Registering for context changes related to CSS OWNS_COMMUNITIES association '"
					+ ownerId + "'");
		ctxBroker.registerForChanges(this, ownsCISsID);

		CtxAssociation ownsCISsAssoc = (CtxAssociation) ctxBrok.retrieve(ownsCISsID).get();
		Set<CtxEntityIdentifier> communitiesEntitiesId = ownsCISsAssoc.getChildEntities();

		for (CtxEntityIdentifier comEntityId:communitiesEntitiesId){
			String cisIdStr = comEntityId.getOwnerId();
			IIdentity cisId = commMngr.getIdManager().fromJid(cisIdStr);
			this.registerMembershipChanges(cisId);
			communities.add(cisIdStr);
		}
		if (LOG.isDebugEnabled())
			LOG.debug("communities=" + communities);	 
	}

	//end of constructor

	private void registerMembershipChanges(IIdentity cisId) {

		CtxEntityIdentifier communityEntId;

		try {
			communityEntId = ctxBroker.retrieveCommunityEntityId(new Requestor(ownerId),cisId).get();
			final CommunityCtxEntity communityEnt = (CommunityCtxEntity) ctxBroker.retrieve(communityEntId).get();

			if (communityEnt == null) {
				LOG.error("Failed to register for membership changes of CIS '" + communityEntId.getOwnerId()
						+ "': Community context entity is null");
				return;
			}

			CtxAssociationIdentifier hasMembersId = communityEnt.getAssociations(CtxAssociationTypes.HAS_MEMBERS).iterator().next();

			if (LOG.isInfoEnabled())
				LOG.info("Registering for membership changes of CIS '" + communityEntId.getOwnerId() + "'");

			ctxBroker.registerForChanges(new CommunityHasMembersListener(communityEntId), hasMembersId);


		} catch (Exception e) {
			// TODO: handle exception
			LOG.error("Could not register for membership changes for community: "+ cisId +" "+ e.getLocalizedMessage(),e);
		}

	}
	private class CommunityHasMembersListener implements CtxChangeEventListener {
		private final CtxEntityIdentifier communityId;

		private CommunityHasMembersListener(final CtxEntityIdentifier communityId) {

			this.communityId = communityId;
		}

		@Override
		public void onCreation(CtxChangeEvent event) {
			if (LOG.isDebugEnabled())
				LOG.debug("Received CREATED event " + event);			
		}

		@Override
		public void onUpdate(CtxChangeEvent event) {
			if (LOG.isDebugEnabled())
				LOG.debug("Received UPDATED event " + event);			
		}

		@Override
		public void onModification(CtxChangeEvent event) {
			if (LOG.isDebugEnabled())
				LOG.debug("Received MODIFIED event " + event);

			if (event.getId() == null) {
				LOG.error("Could not handle MODIFIED event " + event
						+ ": event.getId can't be null");
				return;
			}
			executorService.execute(new CommunityHasMembersHandler(communityId));
		}

		@Override
		public void onRemoval(CtxChangeEvent event) {
			if (LOG.isDebugEnabled())
				LOG.debug("Received REMOVED event " + event);			
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
		if (CtxAssociationTypes.IS_MEMBER_OF.equals(event.getId().getType())) // TODO change OWNS_COMMUNITIES
			this.executorService.execute(new UserOwnsCommunitiesHandler(event.getId()));

	}

	@Override
	public void onRemoval(CtxChangeEvent event) {
		// TODO Auto-generated method stub

	}

	private class UserOwnsCommunitiesHandler implements Runnable {

		private final CtxIdentifier ctxId;
		private UserOwnsCommunitiesHandler(CtxIdentifier ctxId) {

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
