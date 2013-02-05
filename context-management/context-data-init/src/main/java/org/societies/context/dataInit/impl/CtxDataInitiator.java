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
package org.societies.context.dataInit.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * This class is used to add initial context data.
 * 
 * @author <a href="mailto:nikosk@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 * @since 0.4
 */
@Service
public class CtxDataInitiator {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxDataInitiator.class);

	/** The internal BaseUser Broker service. */
	private ICtxBroker ctxBroker;

	/** The Comm Mgr service. */
	private ICommManager commMgr;

	/** The Identity Mgr service. */
	private IIdentityManager idMgr; 

	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;
	private static CtxEntityIdentifier ownerCtxId;

	@Autowired(required=true)
	CtxDataInitiator(ICtxBroker ctxBroker,ICommManager commMgr) {

		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");

		this.commMgr = commMgr;
		this.ctxBroker = ctxBroker;

		this.idMgr = commMgr.getIdManager();


		this.cssOwnerId = this.getLocalIdentity();

		try {

			ownerCtxId = this.ctxBroker.retrieveIndividualEntity(this.cssOwnerId).get().getId();

			if (ownerCtxId.getOwnerId().equals("john.societies.local")){
				BaseUser john = new John();
				addContext(john);
			} else if (ownerCtxId.getOwnerId().equals("jane.societies.local")){
				BaseUser jane = new Jane();
				addContext(jane);
			}

			printCtxAttributes(ownerCtxId);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


	private void addContext(BaseUser user) {

		if (LOG.isInfoEnabled()) // TODO debug
			LOG.info("Updating initial context values");

		String value;

		try {

			// AGE
			value = user.getAge();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.AGE, value);

			// BIRTHDAY
			value = user.getBirthday();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.BIRTHDAY, value);

			// EMAIL
			value = user.getEmail();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.EMAIL, value);

			// FRIENDS
			value = user.getFriends();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.FRIENDS, value);

			// INTERESTS
			value = user.getInterests();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.INTERESTS, value);

			// LANGUAGES
			value = user.getLanguages();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.LANGUAGES, value);

			// LOCATION_COORDINATES
			value = user.getLocationCoordinates();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.LOCATION_COORDINATES, value);

			// LOCATION_SYMBOLIC
			value = user.getLocationSymbolic();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.LOCATION_SYMBOLIC, value);

			// MOVIES
			value = user.getMovies();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.MOVIES, value);

			// NAME
			value = user.getName();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.NAME, value);

			// OCCUPATION
			value = user.getOccupation();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.OCCUPATION, value);

			// POLITICAL_VIEWS
			value = user.getPoliticalViews();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.POLITICAL_VIEWS, value);

			// SEX
			value = user.getSex();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.SEX, value);

			// STATUS
			value = user.getStatus();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.STATUS, value);

			// SKILLS	
			value = user.getSkills();
			if (value != null && !value.isEmpty())
				this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.SKILLS, value);

			
		} catch (Exception e) {
			LOG.info("error when initializing context data: "+ e.getLocalizedMessage());
		}

	}

	private void updateCtxAttribute(CtxEntityIdentifier ownerCtxId, 
			String type, String value) throws Exception {

		if (LOG.isInfoEnabled()) // TODO debug
			LOG.info("Updating '" + type + "' of entity " + ownerCtxId + " to '" + value + "'");

		final List<CtxIdentifier> ctxIds = 
				this.ctxBroker.lookup(ownerCtxId, CtxModelType.ATTRIBUTE, type).get();
		if (!ctxIds.isEmpty()) {
			this.ctxBroker.updateAttribute((CtxAttributeIdentifier) ctxIds.get(0), value);
		} else {
			final CtxAttribute attr = this.ctxBroker.createAttribute(ownerCtxId, type).get();
			this.ctxBroker.updateAttribute(attr.getId(), value);
		}
	}


	private IIdentity getLocalIdentity()  {

		IIdentity cssOwnerId = null;
		INetworkNode cssNodeId = this.idMgr.getThisNetworkNode();
		try {
			cssOwnerId = this.idMgr.fromJid(cssNodeId.getBareJid());
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;	
	}

	private void printCtxAttributes(CtxEntityIdentifier ownerCtxId) throws Exception {
		
		final IndividualCtxEntity entity = (IndividualCtxEntity) this.ctxBroker.retrieve(ownerCtxId).get();
		Set<CtxAttribute> attributes = entity.getAttributes();
		
		LOG.info("CtxEntity :"+entity.getId() );
		for(CtxAttribute attr : attributes){
			LOG.info("CtxAttribute :"+ attr +" value "+ attr.getStringValue());	
		}
		
		
		
	}

}