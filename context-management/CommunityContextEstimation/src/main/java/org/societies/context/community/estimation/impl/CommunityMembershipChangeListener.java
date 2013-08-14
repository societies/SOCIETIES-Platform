/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.context.community.estimation.impl;
import java.util.HashSet;
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
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.springframework.beans.factory.annotation.Autowired;

public class CommunityMembershipChangeListener implements CtxChangeEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(CommunityMembershipChangeListener.class);
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	private ICtxBroker ctxBroker;
	private final IIdentity ownerId;
	private ICommManager commMgr;
	private final Set<String> communities = new CopyOnWriteArraySet<String>();

	@Autowired (required=true)
	public CommunityMembershipChangeListener(ICtxBroker ctxBrok, ICommManager commMngr) throws Exception {
		this.commMgr = commMngr;
		this.ctxBroker = ctxBrok;

		// fetch my CSS ID
		final String ownerIdStr = commMgr.getIdManager().getThisNetworkNode().getBareJid();
		this.ownerId = commMgr.getIdManager().fromJid(ownerIdStr);
		// fetch my CSS entity
		IndividualCtxEntity ownerEnt = ctxBroker.retrieveIndividualEntity(this.ownerId).get();
		// fetch OWNS_COMMUNITIES assoc
		Set<CtxAssociationIdentifier> ownsCISsSet = ownerEnt.getAssociations(CtxAssociationTypes.IS_ADMIN_OF);
		// TODO The "IS_MEMBER_OF will change in "OWNS_COMMUNITIES"

		if (ownsCISsSet.isEmpty()){
			LOG.error("Could not initialise: ownsCISs association is null");
			throw new IllegalStateException("Could not initialise: ownsCISs association is null");
		}
		CtxAssociationIdentifier ownsCISsId = ownsCISsSet.iterator().next();
		if (LOG.isInfoEnabled())
			LOG.info("Registering for context changes related to CSS OWNS_COMMUNITIES association '"
					+ ownerId + "'");
		ctxBroker.registerForChanges(this, ownsCISsId);

		CtxAssociation ownsCISsAssoc = (CtxAssociation) ctxBroker.retrieve(ownsCISsId).get();
		Set<CtxEntityIdentifier> communitiesEntitiesSet = ownsCISsAssoc.getChildEntities();
		for (CtxEntityIdentifier comEntityId:communitiesEntitiesSet){
			String cisIdStr = comEntityId.getOwnerId();
			IIdentity cisId = commMngr.getIdManager().fromJid(cisIdStr);			
			this.registerMembershipChanges(cisId);
			communities.add(comEntityId.getOwnerId());
		}

		if (LOG.isDebugEnabled()){
			LOG.debug("communities=" + communities);
		}

	}

	private void registerMembershipChanges(IIdentity cisId){

		CtxEntityIdentifier communityEntId;
		CommunityCtxEntity communityEnt = null;
		try {
			communityEntId = ctxBroker.retrieveCommunityEntityId(new Requestor(ownerId),cisId).get();

			if (communityEntId == null) {
				LOG.error("Failed to register for membership changes of CIS '" + cisId
						+ "': Community context entity identifier is null");
				return;
			}

			if (LOG.isDebugEnabled()){
				LOG.debug("Retrieving community context entity identified as " + communityEntId);
				communityEnt = (CommunityCtxEntity) ctxBroker.retrieve(communityEntId).get();
				if (communityEnt == null) {
					LOG.error("Failed to register for membership changes of CIS '" + communityEntId.getOwnerId()
							+ "': Community context entity is null");
					return;
				}
			}

			CtxAssociationIdentifier hasMembersId = communityEnt.getAssociations(CtxAssociationTypes.HAS_MEMBERS).iterator().next();
			if (LOG.isInfoEnabled())
				LOG.info("Registering for membership changes of CIS '" + communityEntId.getOwnerId() + "'");

			ctxBroker.registerForChanges(new CommunityHasMembersListener(communityEntId), hasMembersId);

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

	private class CommunityHasMembersListener implements CtxChangeEventListener {

		private final CtxEntityIdentifier communityId;
		private CommunityHasMembersListener(final CtxEntityIdentifier communityId) {

			this.communityId = communityId;
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */

		@Override
		public void onCreation(CtxChangeEvent arg0) {
			// TODO Auto-generated method stub

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

		@Override
		public void onUpdate(CtxChangeEvent event) {
			if (LOG.isDebugEnabled())
				LOG.debug("Received UPDATED event " + event);			
		}

	}

	private class CommunityHasMembersHandler implements Runnable{

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
				final CommunityCtxEntity communityEnt = (CommunityCtxEntity) ctxBroker.retrieve(this.communityEntId).get();
				if (communityEnt == null) {
					LOG.error("Could not handle membership change of CIS '"
							+ this.communityEntId.getOwnerId() + "': '"
							+ communityEnt + "' is null");
					return;
				}	


				//	for (CtxAttribute)	// Call estimateCommunityCtx			
				Set<CtxAttribute> cisAttributesSet = communityEnt.getAttributes();
				for (CtxAttribute cisAtt:cisAttributesSet){
					CommunityContextEstimation cce = new CommunityContextEstimation();
					cce.estimateCommunityCtx(communityEntId, cisAtt.getId());
				}


			} catch (Exception e) {

				LOG.error("Could not handle membership change of CIS '"
						//+ this.listener.communityId + "': "	
						+ e.getLocalizedMessage(), e);
			}			
		}

	}

	@Override
	public void onCreation(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onModification(CtxChangeEvent event) {
		if (CtxAssociationTypes.IS_ADMIN_OF.equals(event.getId().getType())) // TODO change OWNS_COMMUNITIES
			this.executorService.execute(new UserOwnsCommunitiesHandler(event.getId()));
	}

	@Override
	public void onRemoval(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub

	}
	private class UserOwnsCommunitiesHandler implements Runnable {

		private final CtxIdentifier ctxId;

		private UserOwnsCommunitiesHandler(CtxIdentifier ctxId) {

			this.ctxId = ctxId;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			try {
				final CtxAssociation isMemberOf = (CtxAssociation) ctxBroker.retrieve(ctxId).get();
				if (isMemberOf == null) {
					LOG.error("Could not handle CIS membership change: "
							+ "Could not retrieve '" + this.ctxId + "'");
					return;
				}
				final Set<String> currentCommunities = new HashSet<String>();
				for (final CtxEntityIdentifier cisCtxId : isMemberOf.getChildEntities()) {
					final String communityId = cisCtxId.getOwnerId(); 
					currentCommunities.add(communityId);
				}

				// find communities the user is no long member of
				final Set<String> oldCommunities = new HashSet<String>(communities);
				oldCommunities.removeAll(currentCommunities);
				if (LOG.isDebugEnabled())
					LOG.debug("CSS ctx Id " + isMemberOf.getParentEntity() + ", oldCommunities=" + oldCommunities);

				// find new communities the user is member of
				final Set<String> newCommunities = new HashSet<String>(currentCommunities);
				newCommunities.removeAll(communities);
				if (LOG.isDebugEnabled())
					LOG.debug("CSS ctx Id " + isMemberOf.getParentEntity() + ", newCommunities=" + newCommunities);

				// update communities the user is member of
				communities.clear();
				communities.addAll(currentCommunities);

				for (final String oldCommunity : oldCommunities){
					// TODO unregisterMembershipChanges(cisId);
					IIdentity cisId = commMgr.getIdManager().fromJid(oldCommunity);
				//	this.unregisterMembershipChanges(cisId);
					ctxBroker.unregisterFromChanges(new CommunityMembershipChangeListener(ctxBroker,commMgr), cisId);
				}

				for (final String newCommunity : newCommunities) {
					IIdentity cisId=null;
					try {
						cisId = commMgr.getIdManager().fromJid(newCommunity);
					} catch (InvalidFormatException e) {
						// TODO Auto-generated catch block
						LOG.error("Could not create CIS Iidentity from jid"+ e.getLocalizedMessage(), e);
						continue;
					}
					registerMembershipChanges(cisId);
				}

			} catch (Exception e) {

				LOG.error("Could not handle CIS membership change: " 
						+ e.getLocalizedMessage(), e);
			}
		}

/*		private void unregisterMembershipChanges(IIdentity cisId) {
			CtxEntityIdentifier communityEntId;
			CommunityCtxEntity communityEnt = null;

			try {
				communityEntId = ctxBroker.retrieveCommunityEntityId(new Requestor(ownerId),cisId).get();

				if (communityEntId == null) {
					LOG.error("Failed to register for membership changes of CIS '" + cisId
							+ "': Community context entity identifier is null");
					return;
				}

				if (LOG.isDebugEnabled()){
					LOG.debug("Retrieving community context entity identified as " + communityEntId);
					communityEnt = (CommunityCtxEntity) ctxBroker.retrieve(communityEntId).get();
					if (communityEnt == null) {
						LOG.error("Failed to register for membership changes of CIS '" + communityEntId.getOwnerId()
								+ "': Community context entity is null");
						return;
					}
				}

				CtxAssociationIdentifier hasMembersId = communityEnt.getAssociations(CtxAssociationTypes.HAS_MEMBERS).iterator().next();
				if (LOG.isInfoEnabled())
					LOG.info("Unregistering from membership changes of CIS '" + communityEntId.getOwnerId() + "'");

				ctxBroker.unregisterFromChanges(new unregisterCommunityHasMemeberListener(communityEntId), hasMembersId);


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
		}*/

/*		private class unregisterCommunityHasMemeberListener implements CtxChangeEventListener{

			private final CtxEntityIdentifier communityId;

			private unregisterCommunityHasMemeberListener(final CtxEntityIdentifier communityId){

				this.communityId = communityId;
			}

			@Override
			public void onCreation(CtxChangeEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onModification(CtxChangeEvent event) {
				// TODO Auto-generated method stub
				if(CtxAssociationTypes.IS_FRIENDS_WITH.equals(event.getId())){
					executorService.execute(new unregisterCommunityHasMemeberHandler(event.getId()));	
				}
				
			}

			@Override
			public void onRemoval(CtxChangeEvent event) {
				// TODO Auto-generated method stub
				//if(CtxAttributeTypes.if event.getId().getType()
			}

			@Override
			public void onUpdate(CtxChangeEvent event) {
				// TODO Auto-generated method stub

			}

			private class unregisterCommunityHasMemeberHandler implements Runnable{
				private CtxIdentifier ctxId; 
				
				public unregisterCommunityHasMemeberHandler(CtxIdentifier id) {
					this.ctxId = id;
				}

				@Override
				public void run() {
					// TODO Auto-generated method stub

					try {
						
						CommunityCtxEntity comEntity = (CommunityCtxEntity) ctxBroker.retrieve(ctxId).get();
						Set<CtxAssociationIdentifier> isMemberOfAssoc = comEntity.getAssociations(CtxAssociationTypes.IS_MEMBER_OF);
						for(CtxAssociationIdentifier isMemOfAss:isMemberOfAssoc){
							try {
								ctxBroker.unregisterFromChanges(new CommunityMembershipChangeListener(ctxBroker,commMgr), isMemOfAss);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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

			}

		}*/
	}

}
