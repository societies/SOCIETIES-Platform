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
package org.societies.context.source.css.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * This class is used to update the CSS owner context based on CSS record
 * changes.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
@Service
@Lazy(false)
public class CssCtxMonitor extends EventListener {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CssCtxMonitor.class);
	
	private static final String[] EVENT_TYPES = { EventTypes.CSS_RECORD_EVENT,
		EventTypes.CSS_FRIENDED_EVENT, EventTypes.CIS_CREATION, 
		EventTypes.CIS_SUBS, EventTypes.CIS_UNSUBS };
			
	/** The internal Context Broker service. */
	@Autowired(required=true)
	private ICtxBroker ctxBroker;
	
	/** The Event Mgr service. */
	private IEventMgr eventMgr;
	
	/** The Comm Mgr service. */
	private ICommManager commMgr;
	
	/** The executor service. */
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	@Autowired(required=true)
	CssCtxMonitor(IEventMgr eventMgr, ICommManager commMgr) {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		this.eventMgr = eventMgr;
		this.commMgr = commMgr;
		if (LOG.isInfoEnabled())
			LOG.info("Registering for '" + Arrays.asList(EVENT_TYPES) + "' events");
		this.eventMgr.subscribeInternalEvent(this, EVENT_TYPES, null);
		// TODO unsubscribe when stopped?
	}

	/*
	 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
	 */
	@Override
	public void handleExternalEvent(CSSEvent event) {
		
		if (LOG.isWarnEnabled())
			LOG.warn("Received unexpected external '" + event.geteventType() + "' event: " + event);
	}

	/*
	 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
	 */
	@Override
	public void handleInternalEvent(InternalEvent event) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received internal " + event.geteventType() + " event: " + event);
		
		if (EventTypes.CSS_RECORD_EVENT.equals(event.geteventType())) {

			if (!(event.geteventInfo() instanceof CssRecord)) {

				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected event info of type " + CssRecord.class.getName()
						+ " but was " + event.geteventInfo().getClass());
				return;
			}

			final CssRecord cssRecord = (CssRecord) event.geteventInfo();
			this.executorService.execute(new CssRecordUpdateHandler(cssRecord));
			
		} else if (EventTypes.CSS_FRIENDED_EVENT.equals(event.geteventType())) {
			
			if (event.geteventSource() == null || event.geteventSource().isEmpty()) {
				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected non-empty event source "
						+ " but was " + event.geteventSource());
				return;
			}
			if (!(event.geteventInfo() instanceof String)) {

				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected event info of type " + CssRecord.class.getName()
						+ " but was " + event.geteventInfo().getClass());
				return;
			}
			
			this.executorService.execute(new CssFriendedHandler(event.geteventSource(),
					(String) event.geteventInfo()));
			
		} else if (EventTypes.CIS_CREATION.equals(event.geteventType())
				|| EventTypes.CIS_SUBS.equals(event.geteventType())
				|| EventTypes.CIS_UNSUBS.equals(event.geteventType())) {
			
			if (event.geteventSource() == null || event.geteventSource().length() == 0) {

				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected non-null or non-empty event source of type IIdentity JID String"
						+ " but was " + event.geteventSource());
				return;
			}
			
			if (!(event.geteventInfo() instanceof Community)) {

				LOG.error("Could not handle internal " + event.geteventType() + " event: " 
						+ "Expected event info of type " + Community.class.getName()
						+ " but was " + event.geteventInfo().getClass());
				return;
			}

			final Community cisRecord = (Community) event.geteventInfo();
			if (EventTypes.CIS_CREATION.equals(event.geteventType())
					|| EventTypes.CIS_SUBS.equals(event.geteventType()))
				this.executorService.execute(new CssJoinedCisHandler(
						event.geteventSource(), cisRecord.getCommunityJid()));
			else //if (EventTypes.CIS_UNSUBS.equals(event.geteventType()))
				this.executorService.execute(new CssLeftCisHandler(
						event.geteventSource(), cisRecord.getCommunityJid()));
			
		} else {
		
			if (LOG.isWarnEnabled())
				LOG.warn("Received unexpeted event of type '" + event.geteventType() + "'");
		}
	}
	
	private class CssRecordUpdateHandler implements Runnable {
		
		private final CssRecord cssRecord;
		
		private CssRecordUpdateHandler(final CssRecord cssRecord) {
			
			this.cssRecord = cssRecord;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			if (LOG.isInfoEnabled())
				LOG.info("Updated CSS record: " + cssRecord);
			
			final String cssIdStr = cssRecord.getCssIdentity();
			try {
				IIdentity cssId = commMgr.getIdManager().fromJid(cssIdStr);
				CtxEntityIdentifier ownerCtxId = 
						ctxBroker.retrieveIndividualEntity(cssId).get().getId();

				String value;

				// NAME
				value = cssRecord.getName();
				if (value != null && !value.isEmpty())
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.NAME, value);

				// EMAIL
				value = cssRecord.getEmailID();
				if (value != null && !value.isEmpty())
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.EMAIL, value);

				// ADDRESS_HOME_CITY
				value = cssRecord.getHomeLocation();
				if (value != null && !value.isEmpty())
					updateCtxAttribute(ownerCtxId, CtxAttributeTypes.ADDRESS_HOME_CITY, value);

			} catch (InvalidFormatException ife) {

				LOG.error("Invalid CSS IIdentity found in CSS record: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {

				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class CssFriendedHandler implements Runnable {
		
		private final String myCssIdStr;
		private final String newFriendIdStr;
		
		private CssFriendedHandler(final String myCssIdStr, final String newFriendIdStr) {
			
			this.myCssIdStr = myCssIdStr;
			this.newFriendIdStr = newFriendIdStr;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			if (LOG.isInfoEnabled())
				LOG.info("CSS '" + myCssIdStr + "' friended '" + newFriendIdStr + "'");
			
			try {
				final IIdentity myCssId = commMgr.getIdManager().fromJid(myCssIdStr);
				final IndividualCtxEntity myCssEnt = 
						ctxBroker.retrieveIndividualEntity(myCssId).get();

				final IIdentity newFriendId = commMgr.getIdManager().fromJid(newFriendIdStr);
				final CtxEntityIdentifier newFriendEntId =
						ctxBroker.retrieveIndividualEntityId(
								new Requestor(myCssId), newFriendId).get();
				
				final CtxAssociation isFriendsWithAssoc;
				if (myCssEnt.getAssociations(CtxAssociationTypes.IS_FRIENDS_WITH).isEmpty())
					isFriendsWithAssoc = ctxBroker.createAssociation(
							new Requestor(myCssId), myCssId, CtxAssociationTypes.IS_FRIENDS_WITH).get();
				else
					isFriendsWithAssoc = (CtxAssociation) ctxBroker.retrieve(
							myCssEnt.getAssociations(CtxAssociationTypes.IS_FRIENDS_WITH).iterator().next()).get();
				isFriendsWithAssoc.setParentEntity(myCssEnt.getId());
				isFriendsWithAssoc.addChildEntity(newFriendEntId);
				ctxBroker.update(isFriendsWithAssoc);

			} catch (InvalidFormatException ife) {

				LOG.error("Invalid CSS IIdentity found in CSS record: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {

				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class CssJoinedCisHandler implements Runnable {
		
		private final String myCssIdStr;
		
		private final String cisIdStr;
		
		private CssJoinedCisHandler(final String myCssIdStr, final String cisIdStr) {
			
			this.myCssIdStr = myCssIdStr;
			this.cisIdStr = cisIdStr;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			if (LOG.isInfoEnabled())
				LOG.info("CSS '" + this.myCssIdStr + "' joined CIS '" + this.cisIdStr + "'");
			
			try {
				final IIdentity myCssId = commMgr.getIdManager().fromJid(
						this.myCssIdStr);
				final IndividualCtxEntity myCssEnt = 
						ctxBroker.retrieveIndividualEntity(myCssId).get();

				final IIdentity cisId = commMgr.getIdManager().fromJid(this.cisIdStr);

				///////////////////////////////////////////////////////////////
				// The CommunityCtxEntity might not be available right after
				// the CIS_CREATION event: 
				// (1) check if it can be retrieved, otherwise
				// (2) wait for its creation
				///////////////////////////////////////////////////////////////

				// (1) check if the CommunityCtxEntity has already been created
				CtxEntityIdentifier cisEntId =
						ctxBroker.retrieveCommunityEntityId(
								new Requestor(myCssId), cisId).get();
				// (2) if not available, wait until it's created
				// TODO find better way (event-based?)
				if (cisEntId == null) {
					if (LOG.isDebugEnabled())
						LOG.debug("Waiting for the CommunityCtxEntity of CIS '"
								+ cisId + "' to be created");
					int retries = 10;
					while (retries > 0) {
						Thread.sleep(500);
						cisEntId = ctxBroker.retrieveCommunityEntityId(
								new Requestor(myCssId), cisId).get();
						retries--;
						if (cisEntId != null)
							break;
					}
				}
				
				if (cisEntId == null) {
					LOG.error("CommunityCtxEntity of CIS '"	+ cisId + "' is not available!!");
					return;
				}
				
				final CtxAssociation isMemberOfAssoc;
				if (myCssEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).isEmpty())
					isMemberOfAssoc = ctxBroker.createAssociation(
							new Requestor(myCssId), myCssId, CtxAssociationTypes.IS_MEMBER_OF).get();
				else
					isMemberOfAssoc = (CtxAssociation) ctxBroker.retrieve(
							myCssEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().next()).get();
				isMemberOfAssoc.setParentEntity(myCssEnt.getId());
				isMemberOfAssoc.addChildEntity(cisEntId);
				ctxBroker.update(isMemberOfAssoc);

			} catch (InvalidFormatException ife) {

				LOG.error("Invalid CSS/CIS IIdentity: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {

				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class CssLeftCisHandler implements Runnable {
		
		private final String cssIdStr;
		
		private final String cisIdStr;
		
		private CssLeftCisHandler(final String cssIdStr, final String cisIdStr) {
			
			this.cssIdStr = cssIdStr;
			this.cisIdStr = cisIdStr;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			if (LOG.isInfoEnabled())
				LOG.info("CSS '" + this.cssIdStr + "' left CIS '" + this.cisIdStr + "'");
			
			try {
				final IIdentity myCssId = commMgr.getIdManager().fromJid(
						this.cssIdStr);
				final IndividualCtxEntity myCssEnt = 
						ctxBroker.retrieveIndividualEntity(myCssId).get();

				final IIdentity cisId = commMgr.getIdManager().fromJid(cisIdStr);
				final CtxEntityIdentifier cisEntId =
						ctxBroker.retrieveCommunityEntityId(
								new Requestor(myCssId), cisId).get();
				
				final CtxAssociation isMemberOfAssoc;
				if (!myCssEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).isEmpty()) {
					
					isMemberOfAssoc = (CtxAssociation) ctxBroker.retrieve(
							myCssEnt.getAssociations(CtxAssociationTypes.IS_MEMBER_OF).iterator().next()).get();
					isMemberOfAssoc.setParentEntity(myCssEnt.getId());
					isMemberOfAssoc.removeChildEntity(cisEntId);
					ctxBroker.update(isMemberOfAssoc);
				}

			} catch (InvalidFormatException ife) {

				LOG.error("Invalid CSS/CIS IIdentity: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {

				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private void updateCtxAttribute(CtxEntityIdentifier ownerCtxId, 
			String type, String value) throws Exception {

		if (LOG.isDebugEnabled())
			LOG.debug("Updating '" + type + "' of entity " + ownerCtxId + " to '" + value + "'");

		final List<CtxIdentifier> ctxIds = 
				this.ctxBroker.lookup(ownerCtxId, CtxModelType.ATTRIBUTE, type).get();
		final CtxAttribute attr;
		if (!ctxIds.isEmpty())
			attr = (CtxAttribute) this.ctxBroker.retrieve(ctxIds.get(0)).get();
		else
			attr = this.ctxBroker.createAttribute(ownerCtxId, type).get();
			
		attr.setStringValue(value);
		attr.setValueType(CtxAttributeValueType.STRING);
		attr.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		this.ctxBroker.update(attr);
	}
}