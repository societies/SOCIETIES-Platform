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
import java.util.Iterator;
import java.util.LinkedHashSet;
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
	
	private final Set<CtxAttributeIdentifier> continuouslyRefinedAttrIds = 
				new CopyOnWriteArraySet<CtxAttributeIdentifier>();
	
	private final ExecutorService executorService =
			Executors.newSingleThreadExecutor();
	
	static Comparator<CtxAttribute> LocationSymbolicComparator =
			new Comparator<CtxAttribute>() {

		@Override
		public int compare(CtxAttribute attr1, CtxAttribute attr2) {

			if (attr1 == null && attr2 == null) 
				return 0; 
		    // we want null values first 
		    if (attr1 != null && attr2 == null) 
		    	return +1; 
		    if (attr1 == null && attr2 != null) 
		    	return -1;
		    
			final long now = new Date().getTime();
			final boolean isAttr1Fresh = this.isFresh(attr1, now); 
			final boolean isAttr2Fresh = this.isFresh(attr2, now);

			if (isAttr1Fresh && isAttr2Fresh) { // both attributes are fresh
				
				if (LOG.isDebugEnabled())
					LOG.debug("attr1 and attr2 fresh");

				if (attr1.getSourceId() == null && attr2.getSourceId() != null)
					return -1;
				else if (attr1.getSourceId() != null && attr2.getSourceId() == null)
					return +1;
				else if (attr1.getSourceId() == null && attr2.getSourceId() == null)
					return attr1.getQuality().getLastUpdated().compareTo(attr2.getQuality().getLastUpdated());
				else if (attr1.getSourceId().contains(CtxSourceNames.PZ)
						&& attr2.getSourceId().contains(CtxSourceNames.RFID))
					return -1;
				else if ((attr1.getSourceId().contains(CtxSourceNames.PZ)
								&& attr2.getSourceId().contains(CtxSourceNames.PZ))
						|| (attr1.getSourceId().contains(CtxSourceNames.RFID)
								&& attr2.getSourceId().contains(CtxSourceNames.RFID)))
					return attr1.getQuality().getLastUpdated().compareTo(attr2.getQuality().getLastUpdated());
				else if (attr1.getSourceId().contains(CtxSourceNames.RFID)
						&& attr2.getSourceId().contains(CtxSourceNames.PZ))
					return +1;
				else if (attr2.getSourceId().contains(CtxSourceNames.PZ) 
						|| attr2.getSourceId().contains(CtxSourceNames.RFID))
					return -1;
				else if (attr1.getSourceId().contains(CtxSourceNames.PZ)
						|| attr1.getSourceId().contains(CtxSourceNames.RFID))
					return +1;
				else 
					return attr1.getQuality().getLastUpdated().compareTo(attr2.getQuality().getLastUpdated());
			
			} else if (isAttr1Fresh) { // a1 is fresh
				
				if (LOG.isDebugEnabled())
					LOG.debug("attr1 fresh");

				return +1;

			} else if (isAttr2Fresh) { // a2 is fresh
				
				if (LOG.isDebugEnabled())
					LOG.debug("attr2 fresh");

				return -1;

			} else { // none of the attributes is fresh
				
				if (LOG.isDebugEnabled())
					LOG.debug("attr1 and attr2 NOT fresh");

				return attr1.getQuality().getLastUpdated().compareTo(attr2.getQuality().getLastUpdated());
			}
		}
		
		private boolean isFresh(final CtxAttribute attr, final long now) {
			
			final long timeSinceLastUpdate = now - attr.getQuality().getLastUpdated().getTime();
			final Double timeBetweenUpdates = (attr.getQuality().getUpdateFrequency() != null)
					? (1d / attr.getQuality().getUpdateFrequency()) * 1000d : null;
			return (timeBetweenUpdates != null)
					? timeBetweenUpdates > timeSinceLastUpdate : true;
		}
	};
	
	UserLocationRefiner() {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}

	public CtxAttribute refineOnDemandGPSCoords(final CtxAttributeIdentifier attrId)
			throws UserCtxInferenceException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Refining attribute '" + attrId + "' on-demand");
		if (!CtxAttributeTypes.LOCATION_COORDINATES.equals(attrId.getType()))
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "': Unsupported attribute type: " + attrId.getType());
		
		// TODO add real inference
		CtxAttribute refinedAttr ;
		try {
			refinedAttr = this.internalCtxBroker.retrieveAttribute(attrId, false).get();
		} catch (Exception e) {
			
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "': " + e.getLocalizedMessage(), e);
		} 
		
		return refinedAttr;
	}
	
	public CtxAttribute refineOnDemand(final CtxAttributeIdentifier attrId) 
			throws UserCtxInferenceException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Refining attribute '" + attrId + "' on-demand");
		if (!CtxAttributeTypes.LOCATION_SYMBOLIC.equals(attrId.getType()))
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "': Unsupported attribute type: " + attrId.getType());
		
		final CtxEntityIdentifier ownerEntId = attrId.getScope();
		if (LOG.isDebugEnabled())
			LOG.debug("ownerEntId=" + ownerEntId);
		try {
			final CtxEntity ownerEnt = (CtxEntity) this.internalCtxBroker.retrieve(ownerEntId).get();
			if (ownerEnt == null)
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Owner entity '" + ownerEnt +  "' does not exist");
			if (ownerEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).isEmpty())
				return null; // Cannot refine without OWNS_CSS_NODES association
			final CtxAssociationIdentifier ownsCssNodesAssocId = 
					ownerEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).iterator().next();
			if (LOG.isDebugEnabled())
				LOG.debug("ownsCssNodesAssocId=" + ownsCssNodesAssocId);
			final CtxAssociation ownsCssNodesAssoc = (CtxAssociation) 
					this.internalCtxBroker.retrieve(ownsCssNodesAssocId).get();
			if (ownsCssNodesAssoc == null)
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Association '" + ownsCssNodesAssocId +  "' does not exist");
			if (!ownerEntId.equals(ownsCssNodesAssoc.getParentEntity()))
				return null; // Cannot refine if the CSS owner entity is not the parent of the OWNS_CSS_NODES association
			if (ownsCssNodesAssoc.getChildEntities().isEmpty())
				return null; // Cannot refine without CSS_NODE entities
			// TODO check User Interaction Device node
			final Set<CtxAttribute> inputAttrs = new LinkedHashSet<CtxAttribute>();
			final Iterator<CtxEntityIdentifier> nodeIterator = ownsCssNodesAssoc.getChildEntities().iterator(); 
			while (nodeIterator.hasNext()) {
				final CtxEntityIdentifier cssNodeEntId = nodeIterator.next();
				if (LOG.isDebugEnabled())
					LOG.debug("cssNodeEntId=" + cssNodeEntId);
				final CtxEntity cssNodeEnt = (CtxEntity) 
						this.internalCtxBroker.retrieve(cssNodeEntId).get();
				if (cssNodeEnt == null) {
					LOG.warn("Could not refine attribute '"	+ attrId + "': Entity '" 
							+ cssNodeEntId +  "' does not exist");
					continue;
				}
				inputAttrs.addAll(cssNodeEnt.getAttributes(attrId.getType()));
			}
			if (LOG.isDebugEnabled())
				LOG.debug("inputAttrs.size()=" + inputAttrs.size());
			if (inputAttrs.isEmpty())
				return null; // Cannot refine without attributes of the specified type under the CSS_NODE entity
			final SortedSet<CtxAttribute> sortedInputAttrs = 
					new TreeSet<CtxAttribute>(LocationSymbolicComparator);
			sortedInputAttrs.addAll(inputAttrs);
			final CtxAttribute optimalInputAttr = sortedInputAttrs.last();
			if (LOG.isDebugEnabled())
				LOG.debug("optimalInputAttr=" + optimalInputAttr.getId());
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
		
		if (LOG.isDebugEnabled())
			LOG.debug("Refining attribute " + attrId + " continuously");
		if (!CtxAttributeTypes.LOCATION_SYMBOLIC.equals(attrId.getType()))
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "': Unsupported attribute type: " + attrId.getType());
		if (this.continuouslyRefinedAttrIds.contains(attrId)) {
			if (LOG.isDebugEnabled())
				LOG.debug("Attribute " + attrId + " already continuously inferred");
			return;
		}
		final CtxEntityIdentifier ownerEntId = attrId.getScope();
		if (LOG.isDebugEnabled())
			LOG.debug("ownerEntId=" + ownerEntId);
		try {
			final CtxEntity ownerEnt = (CtxEntity) this.internalCtxBroker.retrieve(ownerEntId).get();
			if (ownerEnt == null)
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Owner entity '" + ownerEnt +  "' does not exist");
			if (ownerEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).isEmpty())
				return; // Cannot refine without OWNS_CSS_NODES association
			final CtxAssociationIdentifier ownsCssNodesAssocId = 
					ownerEnt.getAssociations(CtxAssociationTypes.OWNS_CSS_NODES).iterator().next();
			if (LOG.isDebugEnabled())
				LOG.debug("ownsCssNodesAssocId=" + ownsCssNodesAssocId);
			final CtxAssociation ownsCssNodesAssoc = (CtxAssociation) 
					this.internalCtxBroker.retrieve(ownsCssNodesAssocId).get();
			if (ownsCssNodesAssoc == null)
				throw new UserCtxInferenceException("Could not refine attribute '"
						+ attrId + "': Association '" + ownsCssNodesAssocId +  "' does not exist");
			if (ownsCssNodesAssoc.getChildEntities().isEmpty())
				return; // Cannot refine without CSS_NODE entities
			if (!ownerEntId.equals(ownsCssNodesAssoc.getParentEntity()))
				return; // Should not refine attributes under the CSS_NODE entity
			final Iterator<CtxEntityIdentifier> nodeIterator = ownsCssNodesAssoc.getChildEntities().iterator(); 
			while (nodeIterator.hasNext()) {
				final CtxEntityIdentifier cssNodeEntId = nodeIterator.next();
				if (LOG.isDebugEnabled())
					LOG.debug("cssNodeEntId=" + cssNodeEntId);
				this.internalCtxBroker.registerForChanges(
						new LocationSymbolicChangeListener(attrId), cssNodeEntId, attrId.getType());
			}
			
			if (LOG.isDebugEnabled())
				LOG.debug("Adding " + attrId + " to set of continuously inferred attributes");
			this.continuouslyRefinedAttrIds.add(attrId);
			
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
			
			if (LOG.isDebugEnabled())
				LOG.debug("LocationSymbolicChangeListener received event " + event);
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
			
			if (LOG.isDebugEnabled())
				LOG.debug("Handling LOCATION_SYMBOLIC update to refine attribute " + this.attrId);
			try {
				final CtxAttribute refinedAttr = refineOnDemand(this.attrId);
				if (LOG.isDebugEnabled())
					LOG.debug("Refined attribute " + refinedAttr);
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