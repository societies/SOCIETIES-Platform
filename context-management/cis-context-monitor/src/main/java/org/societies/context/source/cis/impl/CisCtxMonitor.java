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
package org.societies.context.source.cis.impl;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * This class is used to update CIS context data based on CIS life-cycle events.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
@Service
@Lazy(false)
public class CisCtxMonitor extends EventListener {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CisCtxMonitor.class);
	
	private static final String[] EVENT_TYPES = { EventTypes.CIS_CREATION, EventTypes.CIS_DELETION };
			
	/** The internal Context Broker service. */
	@Autowired(required=true)
	private ICtxBroker ctxBroker;
	
	/** The Event Mgr service reference. */
	private IEventMgr eventMgr;
	
	/** The Comm Mgr service reference. */
	private ICommManager commMgr;
	
	/** The executor service. */
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	@Autowired(required=true)
	CisCtxMonitor(IEventMgr eventMgr, ICommManager commMgr) {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		this.eventMgr = eventMgr;
		this.commMgr = commMgr;
		if (LOG.isInfoEnabled())
			LOG.info("Registering for '" + Arrays.asList(EVENT_TYPES) + "' events");
		this.eventMgr.subscribeInternalEvent(this, EVENT_TYPES, null);
		// TODO unsubscribe when stopped
	}

	/*
	 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
	 */
	@Override
	public void handleExternalEvent(CSSEvent event) {
		
		LOG.warn("Received unexpected external '" + event.geteventType() + "' event: " + event);
	}

	/*
	 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
	 */
	@Override
	public void handleInternalEvent(InternalEvent event) {
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("Received internal " + event.geteventType() + " event: " + event);
		
		if (!(event.geteventInfo() instanceof Community)) {
			
			LOG.error("Could not handle internal " + event.geteventType() + " event: " 
					+ "Expected event info of type " + Community.class.getName()
					+ " but was " + event.geteventInfo().getClass());
			return;
		}
		
		if (EventTypes.CIS_CREATION.equals(event.geteventType())) {
			this.executorService.execute(
					new CisCreationCtxHandler((Community) event.geteventInfo()));
		} else if (EventTypes.CIS_DELETION.equals(event.geteventType())) {
			this.executorService.execute(
					new CisRemovalCtxHandler((Community) event.geteventInfo()));
		} else {
			LOG.error("Could not handle internal event: " 
					+ "Unexpected event type " + event.geteventType());
			return;
		}
	}
	
	private class CisCreationCtxHandler implements Runnable {
		
		private Community cis;
		
		private CisCreationCtxHandler(Community cis) {
			
			this.cis = cis;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			try {
				final String cisIdStr = this.cis.getCommunityJid();
				final IIdentity cisId = commMgr.getIdManager().fromJid(cisIdStr);
				final CommunityCtxEntity cisEntity = ctxBroker.createCommunityEntity(cisId).get();
				
				String value;
				
				// NAME
				value = this.cis.getCommunityName();
				if (value != null && !value.isEmpty())
					updateCtxAttribute(cisEntity.getId(), CtxAttributeTypes.NAME, value);
				
				// ABOUT
				value = this.cis.getDescription();
				if (value != null && !value.isEmpty())
					updateCtxAttribute(cisEntity.getId(), CtxAttributeTypes.ABOUT, value);
				
				final String cisOwnerIdStr = cis.getOwnerJid();
				final IIdentity cisOwnerId = commMgr.getIdManager().fromJid(cisOwnerIdStr);
				final IndividualCtxEntity cisOwnerEntity = ctxBroker.retrieveIndividualEntity(cisOwnerId).get();
				if (cisOwnerEntity == null) {
					LOG.error("Could not retrieve IndividualCtxEntity for CIS creator " + cisOwnerId);
					return;
				}
					
				if (LOG.isInfoEnabled()) // TODO DEBUG
					LOG.info("Adding member " + cisOwnerEntity.getId() + " to community " + cisEntity.getId());
				final CtxAssociationIdentifier hasMembersAssocId = 
						cisEntity.getAssociations(CtxAssociationTypes.HAS_MEMBERS).iterator().next();
				final CtxAssociation hasMembersAssoc = 
						(CtxAssociation) ctxBroker.retrieve(hasMembersAssocId).get();
				hasMembersAssoc.addChildEntity(cisOwnerEntity.getId());
				ctxBroker.update(hasMembersAssoc);
				// TODO owning CSS ?
				// TODO administrating CSS ?
				// TODO membership criteria / bonds
				
			} catch (InvalidFormatException ife) {
				
				LOG.error("Invalid IIdentity found in CIS record: " 
						+ ife.getLocalizedMessage(), ife);
			} catch (Exception e) {
				
				LOG.error("Failed to access context data: " 
						+ e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class CisRemovalCtxHandler implements Runnable {
		
		@SuppressWarnings("unused")
		private Community cis;
		
		private CisRemovalCtxHandler(Community cis) {
			
			this.cis = cis;
		}

		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			// TODO Auto-generated method stub
		}
	}
	
	private void updateCtxAttribute(CtxEntityIdentifier ownerCtxId, 
			String type, String value) throws Exception {

		if (LOG.isDebugEnabled())
			LOG.debug("Updating '" + type + "' of entity " + ownerCtxId + " to '" + value + "'");
		/*
		final List<CtxIdentifier> ctxIds = 
				this.ctxBroker.lookup(ownerCtxId, CtxModelType.ATTRIBUTE, type).get();
		final CtxAttribute attr;
		if (!ctxIds.isEmpty())
			attr = (CtxAttribute) this.ctxBroker.retrieve(ctxIds.get(0));
		else*/
		final CtxAttribute attr = this.ctxBroker.createAttribute(ownerCtxId, type).get();
			
		attr.setStringValue(value);
		attr.setValueType(CtxAttributeValueType.STRING);
		attr.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		this.ctxBroker.update(attr);
	}
}