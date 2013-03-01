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
package org.societies.privacytrust.trust.impl.evidence.monitor;

import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
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
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS) 
 * @since 0.4.1
 */
@Service
public class CtxTrustEvidenceMonitor implements CtxChangeEventListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(CtxTrustEvidenceMonitor.class);
	
	@Autowired(required=true)
	private ITrustEvidenceCollector trustEvidenceCollector;
	
	@Autowired(required=false)
	private ICtxBroker ctxBroker;
	
	private final IIdentity ownerId;
	
	private final Set<String> friends = new CopyOnWriteArraySet<String>();
	
	private final Set<String> communities = new CopyOnWriteArraySet<String>();
	
	/** The executor service. */
	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Autowired
	CtxTrustEvidenceMonitor(ITrustNodeMgr trustNodeMgr, ICommManager commMgr) throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		final String ownerIdStr = commMgr.getIdManager().getThisNetworkNode().getBareJid();
		this.ownerId = commMgr.getIdManager().fromJid(ownerIdStr);
	}

	/*
	 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onCreation(CtxChangeEvent event) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received CREATED event " + event);
	}

	/*
	 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onUpdate(CtxChangeEvent event) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received UPDATED event " + event);
	}

	/*
	 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onModification(CtxChangeEvent event) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received MODIFIED event " + event);
		
		if (CtxAssociationTypes.IS_FRIENDS_WITH.equals(event.getId().getType()))
			this.executorService.execute(new UserIsFriendsWithHandler(event.getId()));
		else if (CtxAssociationTypes.IS_MEMBER_OF.equals(event.getId().getType()))
			this.executorService.execute(new UserIsMemberOfHandler(event.getId()));
	}

	/*
	 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
	 */
	@Override
	public void onRemoval(CtxChangeEvent event) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received REMOVED event " + event);
	}
	
	/**
	 * This method is called when the {@link ICtxBroker} service is bound.
	 * 
	 * @param ctxBroker
	 *            the {@link ICtxBroker} service that was bound
	 * @param props
	 *            the set of properties that the {@link ICtxBroker} service
	 *            was registered with
	 */
	void bindCtxBroker(ICtxBroker ctxBroker, Dictionary<Object,Object> props)
			throws Exception {
		
		LOG.info("Binding service reference " + ctxBroker);
		this.executorService.submit(new Initialiser()).get();
	}
	
	/**
	 * This method is called when the {@link ICtxBroker} service is unbound.
	 * 
	 * @param ctxBroker
	 *            the {@link ICtxBroker} service that was unbound
	 * @param props
	 *            the set of properties that the {@link ICtxBroker} service
	 *            was registered with
	 */
	void unbindCtxBroker(ICtxBroker ctxBroker, Dictionary<Object,Object> props) {
		
		LOG.info("Unbinding service reference " + ctxBroker);
	}
	
	private class UserIsFriendsWithHandler implements Runnable {

		private final CtxIdentifier ctxId;
		
		private UserIsFriendsWithHandler(CtxIdentifier ctxId) {
			
			this.ctxId = ctxId;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			try {
				final CtxAssociation isFriendsWith = (CtxAssociation) ctxBroker.retrieve(ctxId).get();
				if (isFriendsWith == null) {
					LOG.error("Could not handle CSS friendship change: "
							+ "Could not retrieve '" + this.ctxId + "'");
					return;
				}
				final Set<String> currentFriends = new HashSet<String>();
				for (final CtxEntityIdentifier cisCtxId : isFriendsWith.getChildEntities())
					currentFriends.add(cisCtxId.getOwnerId());
				// find friends the user is no long member of
				final Set<String> oldFriends = new HashSet<String>(friends);
				oldFriends.removeAll(currentFriends);
				// find new friends the user is member of
				final Set<String> newFriends = new HashSet<String>(currentFriends);
				newFriends.removeAll(friends);
				// update friends the user is member of
				friends.clear();
				friends.addAll(currentFriends);
				
				for (final String oldFriend : oldFriends) {
					
					final TrustedEntityId subjectId = new TrustedEntityId(
							TrustedEntityType.CSS,
							isFriendsWith.getParentEntity().getOwnerId());
					final TrustedEntityId objectId = new TrustedEntityId(
							TrustedEntityType.CSS, 
							oldFriend);
					final TrustEvidenceType type = TrustEvidenceType.UNFRIENDED_USER;
					final Date ts = isFriendsWith.getLastModified();
					if (LOG.isDebugEnabled())
						LOG.debug("Adding direct trust evidence: subjectId="
								+ subjectId + ", objectId=" + objectId 
								+ ", type=" + type + ", ts=" + ts);
					trustEvidenceCollector.addDirectEvidence(
							subjectId, objectId, type, ts, null);
				}

				for (final String newFriend : newFriends) {
					
					final TrustedEntityId subjectId = new TrustedEntityId(
							TrustedEntityType.CSS,
							isFriendsWith.getParentEntity().getOwnerId());
					final TrustedEntityId objectId = new TrustedEntityId(
							TrustedEntityType.CSS, 
							newFriend);
					final TrustEvidenceType type = TrustEvidenceType.FRIENDED_USER;
					final Date ts = isFriendsWith.getLastModified();
					if (LOG.isDebugEnabled())
						LOG.debug("Adding direct trust evidence: subjectId="
								+ subjectId + ", objectId=" + objectId 
								+ ", type=" + type + ", ts=" + ts);
					trustEvidenceCollector.addDirectEvidence(
							subjectId, objectId, type, ts, null);
				}
						
			} catch (Exception e) {
				
				LOG.error("Could not handle CSS friendship change: " 
						+ e.getLocalizedMessage(), e);
			}
		}	
	}
	
	private class UserIsMemberOfHandler implements Runnable {

		private final CtxIdentifier ctxId;
		
		private UserIsMemberOfHandler(CtxIdentifier ctxId) {
			
			this.ctxId = ctxId;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			try {
				final CtxAssociation isMemberOf = 
						(CtxAssociation) ctxBroker.retrieve(ctxId).get();
				if (isMemberOf == null) {
					LOG.error("Could not handle CIS membership change: "
							+ "Could not retrieve '" + this.ctxId + "'");
					return;
				}
				final Set<String> currentCommunities = new HashSet<String>();
				for (final CtxEntityIdentifier cisCtxId : isMemberOf.getChildEntities()) {
					final String communityId = cisCtxId.getOwnerId(); 
					currentCommunities.add(communityId);/*
					if (!communities.contains(communityId)) {
						final CommunityCtxEntity cisEnt = 
								(CommunityCtxEntity) ctxBroker.retrieve(cisCtxId).get();
						if (cisEnt == null) {
							LOG.error("Cannot register for membership changes of CIS '"
									+ communityId + "': Failed to retrieve community context entity");
							continue;
						}
						if (cisEnt.getAssociations(CtxAssociationTypes.HAS_MEMBERS).isEmpty()) {
							LOG.error("Cannot register for membership changes of CIS '"
									+ communityId + "': Failed to access HAS_MEMBERS association of community context entity "
									+ cisEnt.getId());
							continue;
						}
						final CtxAssociationIdentifier hasMembersId = 
								cisEnt.getAssociations(CtxAssociationTypes.HAS_MEMBERS).iterator().next();

						if (LOG.isDebugEnabled())
							LOG.debug("Registering for membership changes of CIS '"
									+ communityId + "' - monitoring HAS_MEMBERS association "
									+ hasMembersId);
						ctxBroker.registerForChanges(
									new CommunityHasMembersHandler(communityId), hasMembersId);
						// TODO
					}*/
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
				
				for (final String oldCommunity : oldCommunities) {
					
					final TrustedEntityId subjectId = new TrustedEntityId(
							TrustedEntityType.CSS,
							isMemberOf.getParentEntity().getOwnerId());
					final TrustedEntityId objectId = new TrustedEntityId(
							TrustedEntityType.CIS, 
							oldCommunity);
					final TrustEvidenceType type = TrustEvidenceType.LEFT_COMMUNITY;
					final Date ts = isMemberOf.getLastModified();
					if (LOG.isDebugEnabled())
						LOG.debug("Adding direct trust evidence: subjectId="
								+ subjectId + ", objectId="	+ objectId 
								+ ", type=" + type + ", ts=" + ts);
					trustEvidenceCollector.addDirectEvidence(
							subjectId, objectId, type, ts, null);
				}

				for (final String newCommunity : newCommunities) {
					
					final TrustedEntityId subjectId = new TrustedEntityId(
							TrustedEntityType.CSS,
							isMemberOf.getParentEntity().getOwnerId());
					final TrustedEntityId objectId = new TrustedEntityId(
							TrustedEntityType.CIS, 
							newCommunity);
					final TrustEvidenceType type = TrustEvidenceType.JOINED_COMMUNITY;
					final Date ts = isMemberOf.getLastModified();
					if (LOG.isDebugEnabled())
						LOG.debug("Adding direct trust evidence: subjectId="
								+ subjectId + ", objectId="	+ objectId 
								+ ", type=" + type + ", ts=" + ts);
					trustEvidenceCollector.addDirectEvidence(
							subjectId, objectId, type, ts, null);
				}
						
			} catch (Exception e) {
				
				LOG.error("Could not handle CIS membership change: " 
						+ e.getLocalizedMessage(), e);
			}
		}	
	}
	
	private class CommunityHasMembersHandler implements CtxChangeEventListener {

		private final String communityId;
		private final Set<String> members = new CopyOnWriteArraySet<String>();
		
		private CommunityHasMembersHandler(final String communityId) {
			
			this.communityId = communityId;
			this.members.add(ownerId.toString());
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {
			
			if (LOG.isDebugEnabled())
				LOG.debug("Received CREATED event " + event);
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {
		
			if (LOG.isDebugEnabled())
				LOG.debug("Received MODIFIED event " + event);
			
			if (event.getId() == null) {
				LOG.error("Could not handle MODIFIED event " + event
						+ ": event.getId can't be null");
				return;
			}
			
			try {
				final CtxAssociation hasMembers = 
						(CtxAssociation) ctxBroker.retrieve(event.getId()).get();
				if (hasMembers == null) {
					LOG.error("Could not handle CIS membership change: "
							+ "Could not retrieve '" + event.getId() + "'");
					return;
				}
				final Set<String> currentMembers = new HashSet<String>();
				for (final CtxEntityIdentifier cisCtxId : hasMembers.getChildEntities())
					currentMembers.add(cisCtxId.getOwnerId());
				// find old members
				final Set<String> oldMembers = new HashSet<String>(this.members);
				oldMembers.removeAll(currentMembers);
				// find new members
				final Set<String> newMembers = new HashSet<String>(currentMembers);
				newMembers.removeAll(this.members);
				// update members
				this.members.clear();
				this.members.addAll(currentMembers);
				
				for (final String oldMember : oldMembers) {
					
					final TrustedEntityId subjectId = new TrustedEntityId(
							TrustedEntityType.CSS,
							oldMember);
					final TrustedEntityId objectId = new TrustedEntityId(
							TrustedEntityType.CIS, 
							this.communityId);
					final TrustEvidenceType type = TrustEvidenceType.LEFT_COMMUNITY;
					final Date ts = hasMembers.getLastModified();
					if (LOG.isDebugEnabled())
						LOG.debug("Adding direct trust evidence: subjectId="
								+ subjectId + ", objectId="	+ objectId 
								+ ", type=" + type + ", ts=" + ts);
					trustEvidenceCollector.addDirectEvidence(
							subjectId, objectId, type, ts, null);
				}

				for (final String newMember : newMembers) {
					
					final TrustedEntityId subjectId = new TrustedEntityId(
							TrustedEntityType.CSS,
							newMember);
					final TrustedEntityId objectId = new TrustedEntityId(
							TrustedEntityType.CIS, 
							this.communityId);
					final TrustEvidenceType type = TrustEvidenceType.JOINED_COMMUNITY;
					final Date ts = hasMembers.getLastModified();
					if (LOG.isDebugEnabled())
						LOG.debug("Adding direct trust evidence: subjectId="
								+ subjectId + ", objectId="	+ objectId 
								+ ", type=" + type + ", ts=" + ts);
					trustEvidenceCollector.addDirectEvidence(
							subjectId, objectId, type, ts, null);
				}
						
			} catch (Exception e) {
				
				LOG.error("Could not handle CIS membership change: " 
						+ e.getLocalizedMessage(), e);
			}
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {
			
			if (LOG.isDebugEnabled())
				LOG.debug("Received REMOVED event " + event);
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {
			
			if (LOG.isDebugEnabled())
				LOG.debug("Received UPDATED event " + event);
		}	
	}
	
	private class Initialiser implements Callable<Void> {
		
		/*
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Void call() throws Exception {
			
			final IndividualCtxEntity ownerEnt = ctxBroker.retrieveIndividualEntity(ownerId).get();
			// init friends set
			if (LOG.isInfoEnabled())
				LOG.info("Acquiring friends of CSS '" + ownerId + "'");
			if (ownerEnt.getAssociations(CtxAssociationTypes.IS_FRIENDS_WITH).iterator().hasNext()) {
				
				final CtxAssociation isFriendsWith = (CtxAssociation) 
						ctxBroker.retrieve(ownerEnt.getAssociations(CtxAssociationTypes.IS_FRIENDS_WITH).iterator().next()).get();
				for (final CtxEntityIdentifier friendEntId : isFriendsWith.childEntities)
					friends.add(friendEntId.getOwnerId());
			}
			// init communitites set
			if (LOG.isInfoEnabled())
				LOG.info("Acquiring communities of CSS '" + ownerId + "'");
			if (ownerEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().hasNext()) {

				final CtxAssociation isMemberOf = (CtxAssociation) 
						ctxBroker.retrieve(ownerEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().next()).get();
				for (final CtxEntityIdentifier friendEntId : isMemberOf.childEntities)
					communities.add(friendEntId.getOwnerId());
			}
		
			if (LOG.isInfoEnabled())
				LOG.info("Registering for context changes related to CSS '"
						+ ownerId + "'");
			ctxBroker.registerForChanges(CtxTrustEvidenceMonitor.this, ownerId);
			
			return null;
		}
	}
}