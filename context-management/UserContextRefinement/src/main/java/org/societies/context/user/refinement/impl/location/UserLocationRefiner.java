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
package org.societies.context.user.refinement.impl.location;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.source.CtxSourceNames;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.context.api.user.inference.UserCtxInferenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
@Service
public class UserLocationRefiner {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(UserLocationRefiner.class);
	
	@Autowired(required=true)
	private ICtxBroker internalCtxBroker;
	
	private final Set<CtxAttributeIdentifier> continuoulsyRefinedAttrIds = 
				new CopyOnWriteArraySet<CtxAttributeIdentifier>();
	
	private final ExecutorService executorService =
			Executors.newSingleThreadExecutor();
	
	static Comparator<CtxAttribute> LocationSymbolicComparator =
			new Comparator<CtxAttribute>() {

		@Override
		public int compare(CtxAttribute a1, CtxAttribute a2) {

			final double now = new Date().getTime();

			final double timeSinceLastUpdate1 = now - a1.getLastModified().getTime();
			final Double timeBetweenUpdates1 = (a1.getQuality().getUpdateFrequency() != null)
					? (1d / a1.getQuality().getUpdateFrequency()) * 1000d : null;
			final boolean isFresh1 = (timeBetweenUpdates1 != null)
					? timeBetweenUpdates1 > timeSinceLastUpdate1 : true; 

			final double timeSinceLastUpdate2 = now - a2.getLastModified().getTime();
			final Double timeBetweenUpdates2 = (a2.getQuality().getUpdateFrequency() != null)
					? (1d / a2.getQuality().getUpdateFrequency()) * 1000d : null;
			final boolean isFresh2 = (timeBetweenUpdates1 != null)
					? timeBetweenUpdates2 > timeSinceLastUpdate2 : true;

			if (isFresh1 && isFresh2) { // both attributes are fresh
				
				if (LOG.isInfoEnabled()) // TODO DEBUG
					LOG.info("a1 and a2 fresh");

				if (a1.getSourceId().contains(CtxSourceNames.PZ)
						&& a2.getSourceId().contains(CtxSourceNames.RFID))
					return -1;
				else if ((a1.getSourceId().contains(CtxSourceNames.PZ)
								&& a2.getSourceId().contains(CtxSourceNames.PZ))
						|| (a1.getSourceId().contains(CtxSourceNames.RFID)
								&& a2.getSourceId().contains(CtxSourceNames.RFID)))
					return a1.getQuality().getLastUpdated().compareTo(a2.getQuality().getLastUpdated());
				else if (a1.getSourceId().contains(CtxSourceNames.RFID)
						&& a2.getSourceId().contains(CtxSourceNames.PZ))
					return +1;
				else if (a2.getSourceId().contains(CtxSourceNames.PZ) 
						|| a2.getSourceId().contains(CtxSourceNames.RFID))
					return -1;
				else if (a1.getSourceId().contains(CtxSourceNames.PZ)
						|| a1.getSourceId().contains(CtxSourceNames.RFID))
					return +1;
				else 
					return a1.getQuality().getLastUpdated().compareTo(a2.getQuality().getLastUpdated());
			
			} else if (isFresh1) { // a1 is fresh
				
				if (LOG.isInfoEnabled()) // TODO DEBUG
					LOG.info("a1 fresh");

				return +1;

			} else if (isFresh2) { // a2 is fresh
				
				if (LOG.isInfoEnabled()) // TODO DEBUG
					LOG.info("a2 fresh");

				return -1;

			} else { // none of the attributes is fresh
				
				if (LOG.isInfoEnabled()) // TODO DEBUG
					LOG.info("a1 and a2 NOT fresh");

				return a1.getQuality().getLastUpdated().compareTo(a2.getQuality().getLastUpdated());
			}
		}
	};
	
	UserLocationRefiner() {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}

	public CtxAttribute refineOnDemand(final CtxAttributeIdentifier attrId) 
			throws UserCtxInferenceException {
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("Refining attribute " + attrId);
		if (!CtxAttributeTypes.LOCATION_SYMBOLIC.equals(attrId.getType()))
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "': Unsupported attribute type: " + attrId.getType());
		
		final CtxEntityIdentifier ownerEntId = attrId.getScope();
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("ownerEntId=" + ownerEntId);
		try {
			final CtxEntity ownerEnt = (CtxEntity) this.internalCtxBroker.retrieve(ownerEntId).get();
			if (ownerEnt == null)
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Owner entity '" + ownerEnt +  "' does not exist");
			if (ownerEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).isEmpty())
				return null; // Cannot refine without OWNS_CSS_NODES association
			final CtxAssociationIdentifier ownsCssNodesAssocId = 
					ownerEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).iterator().next();
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("ownsCssNodesAssocId=" + ownsCssNodesAssocId);
			final CtxAssociation ownsCssNodesAssoc = (CtxAssociation) 
					this.internalCtxBroker.retrieve(ownsCssNodesAssocId).get();
			if (ownsCssNodesAssoc == null)
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Association '" + ownsCssNodesAssocId +  "' does not exist");
			if (ownsCssNodesAssoc.getChildEntities().isEmpty())
				return null; // Cannot refine without CSS_NODE entities
			// TODO select User Interaction Node; pick first for now
			final CtxEntityIdentifier cssNodeEntId = ownsCssNodesAssoc.getChildEntities().iterator().next();
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("cssNodeEntId=" + cssNodeEntId);
			final CtxEntity cssNodeEnt = (CtxEntity) 
					this.internalCtxBroker.retrieve(cssNodeEntId).get();
			if (cssNodeEnt == null)
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Entity '" + cssNodeEntId +  "' does not exist");
			final Set<CtxAttribute> inputAttrs = cssNodeEnt.getAttributes(attrId.getType());
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("inputAttrs.size()=" + inputAttrs.size());
			if (inputAttrs.isEmpty())
				return null; // Cannot refine without attributes of the specified type under the CSS_NODE entity
			final SortedSet<CtxAttribute> sortedInputAttrs = 
					new TreeSet<CtxAttribute>(LocationSymbolicComparator);
			sortedInputAttrs.addAll(inputAttrs);
			final CtxAttribute optimalInputAttr = sortedInputAttrs.last();
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("optimalInputAttr=" + optimalInputAttr.getId());
			final CtxAttribute refinedAttr = 
					this.internalCtxBroker.retrieveAttribute(attrId, false).get();
			refinedAttr.setStringValue(optimalInputAttr.getStringValue());
			refinedAttr.setValueType(CtxAttributeValueType.STRING);
			refinedAttr.setSourceId("UserLocationRefiner");
			refinedAttr.getQuality().setOriginType(CtxOriginType.INFERRED);
			if (optimalInputAttr.getQuality().getUpdateFrequency() != null)
				refinedAttr.getQuality().setUpdateFrequency(optimalInputAttr.getQuality().getUpdateFrequency());
			
			return refinedAttr;
			
		} catch (Exception e) {
			
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "': " + e.getLocalizedMessage(), e);
		}
	}
	
	public void refineContinuously(final CtxAttributeIdentifier attrId, final Double updateFrequency) 
			throws UserCtxInferenceException {
		
		// TODO handle updateFrequency
		
		if (LOG.isInfoEnabled()) // DEBUG
			LOG.info("Refining attribute " + attrId + " continuously");
		if (!CtxAttributeTypes.LOCATION_SYMBOLIC.equals(attrId.getType()))
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "': Unsupported attribute type: " + attrId.getType());
		if (this.continuoulsyRefinedAttrIds.contains(attrId)) {
			if (LOG.isInfoEnabled()) // DEBUG
				LOG.info("Attribute " + attrId + " already continuously inferred");
			return;
		}
		final CtxEntityIdentifier ownerEntId = attrId.getScope();
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("ownerEntId=" + ownerEntId);
		try {
			final CtxEntity ownerEnt = (CtxEntity) this.internalCtxBroker.retrieve(ownerEntId).get();
			if (ownerEnt == null)
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Owner entity '" + ownerEnt +  "' does not exist");
			if (ownerEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).isEmpty())
				return; // Cannot refine without OWNS_CSS_NODES association
			final CtxAssociationIdentifier ownsCssNodesAssocId = 
					ownerEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).iterator().next();
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("ownsCssNodesAssocId=" + ownsCssNodesAssocId);
			final CtxAssociation ownsCssNodesAssoc = (CtxAssociation) 
					this.internalCtxBroker.retrieve(ownsCssNodesAssocId).get();
			if (ownsCssNodesAssoc == null)
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Association '" + ownsCssNodesAssocId +  "' does not exist");
			if (ownsCssNodesAssoc.getChildEntities().isEmpty())
				return; // Cannot refine without CSS_NODE entities
			// TODO select User Interaction Node; pick first for now
			final CtxEntityIdentifier cssNodeEntId = ownsCssNodesAssoc.getChildEntities().iterator().next();
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("cssNodeEntId=" + cssNodeEntId);
			this.internalCtxBroker.registerForChanges(
					new LocationSymbolicChangeListener(attrId), cssNodeEntId, attrId.getType());
			if (LOG.isInfoEnabled()) // DEBUG
				LOG.info("Adding " + attrId + " to set of continuously inferred attributes");
			this.continuoulsyRefinedAttrIds.add(attrId);
			
		} catch (Exception e) {
			
			throw new UserCtxInferenceException("Could not continuoysly refine attribute '"
					+ attrId + "': " + e.getLocalizedMessage(), e);
		}
	}
	
	private class LocationSymbolicChangeListener implements CtxChangeEventListener {
		
		private final CtxAttributeIdentifier attrId;
		
		private LocationSymbolicChangeListener (final CtxAttributeIdentifier attrId) {
			
			this.attrId = attrId;
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {
			// TODO Auto-generated method stub
			
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {
			// TODO Auto-generated method stub
			
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {
			
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("Received event " + event);
			executorService.execute(new LocationSymbolicChangeHandler(this.attrId));
		}
	}
	
	private class LocationSymbolicChangeHandler implements Runnable {

		private final CtxAttributeIdentifier attrId;
		
		private LocationSymbolicChangeHandler(final CtxAttributeIdentifier attrId) {
			
			this.attrId = attrId;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("Handling LOCATION_SYMBOLIC update to refine attribute " + this.attrId);
			try {
				final CtxAttribute refinedAttr = refineOnDemand(this.attrId);
				if (LOG.isInfoEnabled()) // TODO DEBUG
					LOG.info("Refined attribute " + refinedAttr);
				// TODO send refinedAttr to UserCtxInferenceMgr
				if (refinedAttr != null)
					internalCtxBroker.update(refinedAttr);
			} catch (Exception e) {
				
				LOG.error("Could not handle LOCATION_SYMBOLIC update to refine attribute " 
						+ this.attrId + ": " + e.getLocalizedMessage(), e);
			}
		}
	}
}