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
package org.societies.context.broker.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Used to initialise context data.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
@Service
public class CtxBootLoader {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxBootLoader.class);
	
	/** The Comms Mgr service reference. */
	@SuppressWarnings("unused")
	private ICommManager commMgr;
	
	/** The User Context DB Mgmt service reference. */
	private IUserCtxDBMgr userCtxDBMgr;
	
	@Autowired(required=true)
	CtxBootLoader(ICommManager commMgr, IUserCtxDBMgr userCtxDBMgr) throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		this.commMgr = commMgr;
		this.userCtxDBMgr = userCtxDBMgr;
		
		final INetworkNode localCssNodeId = commMgr.getIdManager().getThisNetworkNode();
		LOG.info("Found local CSS node ID " + localCssNodeId);
		final IIdentity localCssId = commMgr.getIdManager().fromJid(localCssNodeId.getBareJid());
		LOG.info("Found local CSS ID " + localCssId);

		final IndividualCtxEntity cssOwnerEnt = 
				createIndividualEntity(localCssId, CtxEntityTypes.PERSON); // TODO don't hardcode the cssOwner type
		this.createCssNode(cssOwnerEnt, localCssNodeId);
	}
	
	private IndividualCtxEntity createIndividualEntity(final IIdentity cssId,
			final String ownerType) throws CtxBootLoaderException {

		if (cssId == null)
			throw new NullPointerException("cssId can't be null");
		if (ownerType == null)
			throw new NullPointerException("ownerType can't be null"); 

		IndividualCtxEntity cssOwnerEnt = null;

		try {
			LOG.info("Checking if CSS owner context entity " + cssId + " exists...");
			cssOwnerEnt = this.userCtxDBMgr.retrieveIndividualEntity(cssId.getBareJid());
			if (cssOwnerEnt != null) {
				LOG.info("Found CSS owner context entity " + cssOwnerEnt.getId());
			} else {
				cssOwnerEnt = this.userCtxDBMgr.createIndividualEntity(
						cssId.getBareJid(), ownerType); 

				final CtxAttribute cssIdAttr = this.userCtxDBMgr.createAttribute(
						cssOwnerEnt.getId(), CtxAttributeTypes.ID); 

				cssIdAttr.setStringValue(cssId.toString());
				this.userCtxDBMgr.update(cssIdAttr);
				LOG.info("Created CSS owner context entity " + cssOwnerEnt.getId());
			}

			return cssOwnerEnt;

		} catch (Exception e) {
			throw new CtxBootLoaderException("Could not create CSS owner context entity " + cssId
					+ ": " + e.getLocalizedMessage(), e);
		}
	}

	private void createCssNode(final IndividualCtxEntity cssOwnerEnt,
			final INetworkNode cssNodeId) throws CtxBootLoaderException {
		
		if (cssOwnerEnt == null)
			throw new NullPointerException("cssOwnerEnt can't be null");
		if (cssNodeId == null)
			throw new NullPointerException("cssNodeId can't be null");

		try {
			LOG.info("Checking if CSS node context entity " + cssNodeId + " exists...");
			final List<CtxEntityIdentifier> cssNodeEntIds = 
					this.userCtxDBMgr.lookupEntities(CtxEntityTypes.CSS_NODE,
							CtxAttributeTypes.ID, cssNodeId.toString(), cssNodeId.toString());
			if (!cssNodeEntIds.isEmpty()) {
				LOG.info("Found CSS node context entity " + cssNodeEntIds.get(0));
				return;
			}
			
			final CtxAssociation ownsCssNodesAssoc;
			if (cssOwnerEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).isEmpty())
				ownsCssNodesAssoc = this.userCtxDBMgr.createAssociation(
						CtxAssociationTypes.OWNS_CSS_NODES);
			else
				ownsCssNodesAssoc = (CtxAssociation) this.userCtxDBMgr.retrieve(
						cssOwnerEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).iterator().next());
			ownsCssNodesAssoc.setParentEntity(cssOwnerEnt.getId());
			final CtxEntity cssNodeEnt = this.userCtxDBMgr.createEntity(CtxEntityTypes.CSS_NODE);
			ownsCssNodesAssoc.addChildEntity(cssNodeEnt.getId());
			this.userCtxDBMgr.update(ownsCssNodesAssoc);
			final CtxAttribute cssNodeIdAttr = this.userCtxDBMgr.createAttribute(
					cssNodeEnt.getId(), CtxAttributeTypes.ID);
			cssNodeIdAttr.setStringValue(cssNodeId.toString());
			this.userCtxDBMgr.update(cssNodeIdAttr);
			LOG.info("Created CSS node context entity " + cssNodeEnt.getId());

		} catch (Exception e) {
			throw new CtxBootLoaderException("Could not create CSS node context entity " + cssNodeId
					+ ": " + e.getLocalizedMessage(), e);
		}
	}
}