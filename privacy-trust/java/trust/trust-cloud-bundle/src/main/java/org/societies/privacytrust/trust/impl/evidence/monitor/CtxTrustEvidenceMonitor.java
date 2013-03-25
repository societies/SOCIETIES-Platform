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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustNodeMgr;
import org.societies.privacytrust.trust.api.evidence.repo.ITrustEvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is used to acquire trust evidence based on the CSS owner's direct
 * interactions with other CSSs, the CISs they're member of, and their
 * installed services. More specifically, it adds {@link DirectTrustEvidence}
 * to the Trust Evidence Repository by monitoring the following context change
 * events:
 * <ol>
 * <li>CSS owner uses a service</li>
 * <li>CSS owner (un)friends another CSS.</li>
 * <li>CSS owner joins/leaves a CIS.</li>
 * <li>Membership changes in a CIS the CSS owner is member of.</li>
 * </ol>
 * 
 * The generated pieces of Direct Trust Evidence are then processed by the
 * Direct Trust Engine in order to (re)evaluate the direct trust on the
 * referenced entities, i.e. CSS, CISs or services, on behalf of the CSS owner.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS) 
 * @since 0.4.1
 */
@Service
public class CtxTrustEvidenceMonitor implements CtxChangeEventListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(CtxTrustEvidenceMonitor.class);
	
	/** The time to wait between registration attempts for membership changes (in seconds) */
	private static final long WAIT = 60l;
	
	@Autowired(required=true)
	private ITrustEvidenceCollector trustEvidenceCollector;
	
	@Autowired(required=true)
	private ITrustEvidenceRepository trustEvidenceRepository; 
	
	@Autowired(required=false)
	private ICtxBroker ctxBroker;
	
	private ICommManager commMgr;
	
	private final IIdentity ownerId;
	
	private final Set<String> friends = new CopyOnWriteArraySet<String>();
	
	private final Set<String> communities = new CopyOnWriteArraySet<String>();
	
	private final Set<String> unmonitoredCommunities = new CopyOnWriteArraySet<String>();
	
	/** The executor service. */
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	/** The scheduled executor service. */
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Autowired
	CtxTrustEvidenceMonitor(ITrustNodeMgr trustNodeMgr, ICommManager commMgr) throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		this.commMgr = commMgr;
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
		
		if (CtxAttributeTypes.LAST_ACTION.equals(event.getId().getType()))
			this.executorService.execute(new UserLastActionHandler(event.getId()));
		else if (CtxAssociationTypes.IS_FRIENDS_WITH.equals(event.getId().getType()))
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
	
	private class UserLastActionHandler implements Runnable {

		private final CtxIdentifier ctxId;
		
		private UserLastActionHandler(CtxIdentifier ctxId) {
			
			this.ctxId = ctxId;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			try {
				final CtxAttribute lastActionAttr = (CtxAttribute) ctxBroker.retrieve(ctxId).get();
				if (lastActionAttr == null) {
					LOG.error("Could not handle CSS last action: "
							+ "Could not retrieve '" + this.ctxId + "'");
					return;
				}
				if (lastActionAttr.getBinaryValue() == null) {
					LOG.error("Could not handle CSS last action: "
							+ "LAST_ACTION attribute value is null");
					return;
				}
				final IAction lastAction = (IAction) SerialisationHelper.deserialise(
						lastActionAttr.getBinaryValue(), this.getClass().getClassLoader());
				if (LOG.isDebugEnabled())
					LOG.debug("lastAction=" + lastAction);
				final String userId = lastActionAttr.getId().getOwnerId();
				final String serviceId = lastAction.getServiceID().getIdentifier().toString();
				final Date ts = lastActionAttr.getLastModified();
				addServiceEvidence(userId, serviceId, ts);
						
			} catch (Exception e) {
				
				LOG.error("Could not handle CSS last action: " 
						+ e.getLocalizedMessage(), e);
			}
		}	
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
				
				final String userId = isFriendsWith.getParentEntity().getOwnerId();
				final Date ts = isFriendsWith.getLastModified();
				
				for (final String oldFriend : oldFriends)
					addFriendshipEvidence(userId, oldFriend, TrustEvidenceType.UNFRIENDED_USER, ts);

				for (final String newFriend : newFriends)
					addFriendshipEvidence(userId, newFriend, TrustEvidenceType.FRIENDED_USER, ts);
						
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
				
				final String memberId = isMemberOf.getParentEntity().getOwnerId(); 
				final Date ts = isMemberOf.getLastModified();
				
				for (final String oldCommunity : oldCommunities)
					addMembershipEvidence(memberId, oldCommunity, 
							TrustEvidenceType.LEFT_COMMUNITY, ts);
					
				for (final String newCommunity : newCommunities) {
					addMembershipEvidence(memberId, newCommunity, 
							TrustEvidenceType.JOINED_COMMUNITY, ts);
					unmonitoredCommunities.add(newCommunity);
				}
						
			} catch (Exception e) {
				
				LOG.error("Could not handle CIS membership change: " 
						+ e.getLocalizedMessage(), e);
			}
		}	
	}
	
	private class CommunityHasMembersListener implements CtxChangeEventListener {

		private final String communityId;
		private final Set<String> members = new CopyOnWriteArraySet<String>();
		
		private CommunityHasMembersListener(final String communityId, final Set<String> members) {
			
			this.communityId = communityId;
			this.members.addAll(members);
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
			
			executorService.execute(new CommunityHasMembersHandler(event.getId(), this));
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
	
	private class CommunityHasMembersHandler implements Runnable {

		private final CtxIdentifier hasMembersAssocId;
		private final CommunityHasMembersListener listener;
		
		private CommunityHasMembersHandler(final CtxIdentifier hasMembersAssocId,
				final CommunityHasMembersListener listener) {
			
			this.hasMembersAssocId = hasMembersAssocId;
			this.listener = listener;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
		
			if (LOG.isDebugEnabled())
				LOG.debug("Retrieving HAS_MEMBERS association '" + this.hasMembersAssocId
						+ "' to handle membership change of CIS '" + this.listener.communityId + "'");
			try {
				final CtxAssociation hasMembersAssoc = 
						(CtxAssociation) ctxBroker.retrieve(this.hasMembersAssocId).get();
				if (hasMembersAssoc == null) {
					LOG.error("Could not handle membership change of CIS '"
							+ this.listener.communityId + "': '"
							+ this.hasMembersAssocId + "' is null");
					return;
				}
				final Set<String> currentMembers = new HashSet<String>();
				for (final CtxEntityIdentifier cisCtxId : hasMembersAssoc.getChildEntities())
					currentMembers.add(cisCtxId.getOwnerId());
				// find old members
				final Set<String> oldMembers = new HashSet<String>(this.listener.members);
				oldMembers.removeAll(currentMembers);
				// find new members
				final Set<String> newMembers = new HashSet<String>(currentMembers);
				newMembers.removeAll(this.listener.members);
				// update members
				this.listener.members.clear();
				this.listener.members.addAll(currentMembers);
				
				final Date ts = hasMembersAssoc.getLastModified();
				
				for (final String oldMember : oldMembers)
					addMembershipEvidence(oldMember, this.listener.communityId,
							TrustEvidenceType.LEFT_COMMUNITY, ts);
				
				for (final String newMember : newMembers)
					addMembershipEvidence(newMember, this.listener.communityId,
							TrustEvidenceType.JOINED_COMMUNITY, ts);
						
			} catch (Exception e) {
				
				LOG.error("Could not handle membership change of CIS '"
						+ this.listener.communityId + "': "	
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	/**
	 * Runs periodically in the background to perform various maintenance
	 * tasks.
	 */
	private class MaintenanceDaemon implements Runnable {

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			if (LOG.isDebugEnabled())
				LOG.debug("Communities to register for membership changes: " + unmonitoredCommunities);
			for (final String communityId : new HashSet<String>(unmonitoredCommunities)) {

				try {
					registerForMembershipChanges(communityId);
					unmonitoredCommunities.remove(communityId);
				} catch (Exception e) {
					LOG.warn("Failed to register for membership changes of CIS '" + communityId 
							+ "': " + e.getLocalizedMessage() + ". Will re-attempt to register in "
							+ WAIT + " seconds...");
				}
			} // for each communityId ends
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
				for (final CtxEntityIdentifier friendEntId : isFriendsWith.getChildEntities())
					friends.add(friendEntId.getOwnerId());
			}
			if (LOG.isDebugEnabled())
				LOG.debug("friends=" + friends);
			// init communitites set
			if (LOG.isInfoEnabled())
				LOG.info("Acquiring communities of CSS '" + ownerId + "'");
			if (ownerEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().hasNext()) {

				final CtxAssociation isMemberOf = (CtxAssociation) 
						ctxBroker.retrieve(ownerEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().next()).get();
				for (final CtxEntityIdentifier communityEntId : isMemberOf.getChildEntities()) {
					communities.add(communityEntId.getOwnerId());
					unmonitoredCommunities.add(communityEntId.getOwnerId());
				}
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("communities=" + communities);
				LOG.debug("unmonitoredCommunities=" + unmonitoredCommunities);
			}
			scheduler.scheduleWithFixedDelay(new MaintenanceDaemon(),
					WAIT, WAIT, TimeUnit.SECONDS);
		
			if (LOG.isInfoEnabled())
				LOG.info("Registering for context changes related to CSS '"
						+ ownerId + "'");
			ctxBroker.registerForChanges(CtxTrustEvidenceMonitor.this, ownerId);
			
			return null;
		}
	}
	
	private void addServiceEvidence(final String userId, 
			final String serviceId,	final Date ts) throws TrustException {
		
		final TrustedEntityId subjectId = new TrustedEntityId(
				TrustedEntityType.CSS,
				userId);
		final TrustedEntityId objectId = new TrustedEntityId(
				TrustedEntityType.SVC, 
				serviceId);
		
		final TrustEvidenceType type = TrustEvidenceType.USED_SERVICE;
		if (LOG.isDebugEnabled())
			LOG.debug("Adding direct trust evidence: subjectId="
					+ subjectId + ", objectId="	+ objectId 
					+ ", type=" + type + ", ts=" + ts);
		trustEvidenceCollector.addDirectEvidence(
				subjectId, objectId, type, ts, null);
	}
	
	private void addFriendshipEvidence(final String userId, 
			final String friendId, final TrustEvidenceType type,
			final Date ts) throws TrustException {
		
		if (!TrustEvidenceType.FRIENDED_USER.equals(type) 
				&& !TrustEvidenceType.UNFRIENDED_USER.equals(type))
			throw new IllegalArgumentException("Illegal evidence type " + type);
		
		final TrustedEntityId subjectId = new TrustedEntityId(
				TrustedEntityType.CSS,
				userId);
		final TrustedEntityId objectId = new TrustedEntityId(
				TrustedEntityType.CSS, 
				friendId);
		
		if (LOG.isDebugEnabled())
			LOG.debug("Adding direct trust evidence: subjectId="
					+ subjectId + ", objectId="	+ objectId 
					+ ", type=" + type + ", ts=" + ts);
		trustEvidenceCollector.addDirectEvidence(
				subjectId, objectId, type, ts, null);
	}
	
	private void addMembershipEvidence(final String memberId, 
			final String communityId, final TrustEvidenceType type,
			final Date ts) throws TrustException {
		
		if (!TrustEvidenceType.JOINED_COMMUNITY.equals(type) 
				&& !TrustEvidenceType.LEFT_COMMUNITY.equals(type))
			throw new IllegalArgumentException("Illegal evidence type " + type);
		
		final TrustedEntityId subjectId = new TrustedEntityId(
				TrustedEntityType.CSS,
				memberId);
		final TrustedEntityId objectId = new TrustedEntityId(
				TrustedEntityType.CIS, 
				communityId);
		
		if (LOG.isDebugEnabled())
			LOG.debug("Adding direct trust evidence: subjectId="
					+ subjectId + ", objectId="	+ objectId 
					+ ", type=" + type + ", ts=" + ts);
		trustEvidenceCollector.addDirectEvidence(
				subjectId, objectId, type, ts, null);
	}
	
	private void registerForMembershipChanges(final String communityId) throws Exception {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving community context entity identifier of CIS '" + communityId + "'");
		final CtxEntityIdentifier communityEntId = ctxBroker.retrieveCommunityEntityId(
				new Requestor(ownerId), commMgr.getIdManager().fromJid(communityId)).get();
		if (communityEntId == null) {
			LOG.error("Failed to register for membership changes of CIS '" + communityId
					+ "': Community context entity identifier is null");
			return;
		}
		if (LOG.isDebugEnabled())
			LOG.debug("Retrieving community context entity identified as " + communityEntId);
		final CommunityCtxEntity communityEnt = 
				(CommunityCtxEntity) ctxBroker.retrieve(communityEntId).get();
		if (communityEnt == null) {
			LOG.error("Failed to register for membership changes of CIS '" + communityEntId.getOwnerId()
					+ "': Community context entity is null");
			return;
		}
		CtxAssociationIdentifier hasMembersId = null;
		final Set<String> members = new HashSet<String>();
		for (final CtxAssociationIdentifier foundHasMembersId : communityEnt.getAssociations(CtxAssociationTypes.HAS_MEMBERS)) {
			final CtxAssociation foundHasMembers =
					(CtxAssociation) ctxBroker.retrieve(foundHasMembersId).get();
			if (foundHasMembers != null && communityEntId.equals(foundHasMembers.getParentEntity())) {
				hasMembersId = foundHasMembersId;
				for (final CtxEntityIdentifier memberEntId : foundHasMembers.getChildEntities()) {
					final String memberId = memberEntId.getOwnerId();
					if (LOG.isDebugEnabled())
						LOG.debug("Checking existing evidence about '" + memberId + "' being member of community '" + communityId + "'");
					if (trustEvidenceRepository.retrieveDirectEvidence(
							new TrustedEntityId(TrustedEntityType.CSS, memberId), 
							new TrustedEntityId(TrustedEntityType.CIS, communityId),
							TrustEvidenceType.JOINED_COMMUNITY, null, null).isEmpty())
						addMembershipEvidence(memberId, communityId, TrustEvidenceType.JOINED_COMMUNITY, new Date());
					members.add(memberId);
				}
				break;
			}
		}
		if (hasMembersId == null) {
			LOG.error("Failed to register for membership changes of CIS '" + communityEntId.getOwnerId()
					+ "': HAS_MEMBERS association not found");
			return;
		}
		if (LOG.isInfoEnabled())
			LOG.info("Registering for membership changes of CIS '" + communityEntId.getOwnerId() + "'");
		if (LOG.isDebugEnabled())
			LOG.debug("hasMembersId=" + hasMembersId + ", members=" + members);
		ctxBroker.registerForChanges(new CommunityHasMembersListener(
				communityEntId.getOwnerId(), members), hasMembersId);
	}
}