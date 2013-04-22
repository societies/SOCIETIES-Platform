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
package org.societies.context.activity.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * This class is used to update user/community context based on CSS/CIS related
 * events (e.g. new CSS connections, CIS membership changes, etc).
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
@Service
@Lazy(false)
public class CtxActivityFeed {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxActivityFeed.class);
	
	private static final List<String> CONTEXT_TYPES = Collections.unmodifiableList(
			Arrays.asList(CtxAttributeTypes.LOCATION_SYMBOLIC));

	/** The internal Context Broker service. */
	private ICtxBroker ctxBroker;
	
	/** The CSS activity feed. */
	private IActivityFeed cssActivityFeed;
	
	private final String cssActivityFeedId;

	@Autowired(required=true)
	CtxActivityFeed(ICtxBroker ctxBroker, 
			IActivityFeedManager activityFeedMgr,
			ICommManager commMgr) throws Exception {

		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");

		this.ctxBroker = ctxBroker;		

		try {
			if (LOG.isInfoEnabled())
				LOG.info("Obtaining reference to CSS Activity Feed");
			this.cssActivityFeedId = commMgr.getIdManager().getThisNetworkNode().toString(); 
			this.cssActivityFeed = activityFeedMgr.getOrCreateFeed(
					cssActivityFeedId, cssActivityFeedId, false);

			final CtxEntityIdentifier cssOwnerEntId = ctxBroker.retrieveIndividualEntity(
					commMgr.getIdManager().getCloudNode()).get().getId();
			if (LOG.isInfoEnabled())
				LOG.info("Registering CSS context change listener for types '" + CONTEXT_TYPES + "'");
			// Specify null attribute type to catch *all* attribute changes under the
			// CSS owner entity
			ctxBroker.registerForChanges(new CssCtxChangeListener(), cssOwnerEntId, null);
			
		} catch (Exception e) {

			LOG.error("Could not instantiate " + this.getClass()
					+ "': " + e.getLocalizedMessage(), e);
			throw e;
		}
	}

	private class CssCtxChangeListener implements CtxChangeEventListener {

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {
			
			if (LOG.isTraceEnabled())
				LOG.trace("Received CREATED event " + event);
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {
			
			if (LOG.isTraceEnabled())
				LOG.trace("Received MODIFIED event " + event);
			
			if (!(event.getId() instanceof CtxAttributeIdentifier)) {
				LOG.error("Received unexpected event for object '" + event.getId() 
						+ "': Not a context attribute!");
				return;
			}
			
			final CtxAttributeIdentifier attrId = (CtxAttributeIdentifier) event.getId();
			if (CONTEXT_TYPES.contains(attrId.getType())) {
				try {
					final String activity;
					final CtxAttribute attr = (CtxAttribute) ctxBroker.retrieve(attrId).get();
					if (CtxAttributeTypes.LOCATION_SYMBOLIC.equals(attr.getType())) {
						activity = "User location changed to " + attr.getStringValue();
					} else {
						activity = attr.getType() + " changed to " + attr.getStringValue();
					}
					if (LOG.isDebugEnabled())
						LOG.debug("Adding activity '" + activity + "'");
					addCssActivity(activity);
					
				} catch (Exception e) {
					LOG.error("Failed to retrieve modified context attribute " + attrId
							+ ": " + e.getLocalizedMessage(), e);
					return;
				}
			} else {
				if (LOG.isDebugEnabled())
					LOG.debug("Ignoring event for context attribute of type " + attrId.getType());
			}
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {
			
			if (LOG.isTraceEnabled())
				LOG.trace("Received REMOVED event " + event);
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {
			
			if (LOG.isTraceEnabled())
				LOG.trace("Received UPDATED event " + event);
		}
	}
	
	private void addCssActivity(final String action){

	    final IActivity activity = this.cssActivityFeed.getEmptyIActivity();
	    activity.setActor(this.cssActivityFeedId);
	    activity.setObject(this.cssActivityFeedId);
	    activity.setVerb(action);

	    this.cssActivityFeed.addActivity(activity);
	}
}