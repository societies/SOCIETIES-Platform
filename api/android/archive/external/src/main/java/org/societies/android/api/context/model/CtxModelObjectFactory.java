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
package org.societies.android.api.context.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.societies.api.context.model.CtxAttributeValueType;

public final class CtxModelObjectFactory {

	private static CtxModelObjectFactory instance = new CtxModelObjectFactory();

	private CtxModelObjectFactory() {}

	public static synchronized CtxModelObjectFactory getInstance() {

		return instance;
	}

	public ACtxEntity createEntity(final ACtxEntityIdentifier id, 
			final Date lastModified, final Set<ACtxAttribute> attributes,
			final Set<ACtxAssociationIdentifier> associations) {
		
		final ACtxEntity result = new ACtxEntity(id);
		result.setLastModified(lastModified);
		
		if (attributes != null && !attributes.isEmpty())
			for (final ACtxAttribute attribute : attributes)
				result.addAttribute(attribute);
		
		if (associations != null)
			for (final ACtxAssociationIdentifier association : associations)
				result.addAssociation(association);
				
		return result;
	}
	
	public AIndividualCtxEntity createIndividualEntity(final ACtxEntityIdentifier id, 
			final Date lastModified, final Set<ACtxAttribute> attributes,
			final Set<ACtxAssociationIdentifier> associations) {
		
		final AIndividualCtxEntity result = new AIndividualCtxEntity(id);
		result.setLastModified(lastModified);
		
		if (attributes != null)
			for (final ACtxAttribute attribute : attributes)
				result.addAttribute(attribute);
		
		if (associations != null)
			for (final ACtxAssociationIdentifier association : associations)
				result.addAssociation(association);
				
		return result;
	}
	
	public ACommunityCtxEntity createCommunityEntity(final ACtxEntityIdentifier id, 
			final Date lastModified, final Set<ACtxAttribute> attributes,
			final Set<ACtxAssociationIdentifier> associations) {
		
		final ACommunityCtxEntity result = new ACommunityCtxEntity(id);
		result.setLastModified(lastModified);
		
		if (attributes != null)
			for (final ACtxAttribute attribute : attributes)
				result.addAttribute(attribute);
		
		if (associations != null)
			for (final ACtxAssociationIdentifier association : associations)
				result.addAssociation(association);
				
		return result;
	}
	
	public ACtxAttribute createAttribute(final ACtxAttributeIdentifier id, 
			final Date lastModified, final Date lastUpdated, 
			final Serializable value) {
		
		final ACtxAttribute result = new ACtxAttribute(id);
		result.setLastModified(lastModified);
		result.setValue(value);
		if (lastUpdated != null)
			result.getQuality().setLastUpdated(lastUpdated);
				
		return result;
	}
	
	public ACtxHistoryAttribute createHistoryAttribute(
			final ACtxAttributeIdentifier id, final Date lastModified,
			final Date lastUpdated,	final String stringValue,
			final Integer integerValue,	final Double doubleValue, 
			final byte[] binaryValue, final CtxAttributeValueType valueType,
			final String valueMetric) {
		
		return new ACtxHistoryAttribute(id,
				lastModified, lastUpdated, stringValue, integerValue,
				doubleValue, binaryValue, valueType, valueMetric);
	}
	
	public ACtxHistoryAttribute createHistoryAttribute(
			final ACtxAttribute attribute) {
		
		return new ACtxHistoryAttribute(attribute.getId(),
				attribute.getLastModified(), 
				attribute.getQuality().getLastUpdated(), 
				attribute.getStringValue(), 
				attribute.getIntegerValue(),
				attribute.getDoubleValue(), 
				attribute.getBinaryValue(), 
				attribute.getValueType(),
				attribute.getValueMetric());
	}
	
	public ACtxAssociation createAssociation(final ACtxAssociationIdentifier id, 
			final Date lastModified, final ACtxEntityIdentifier parentEntity,
			final Set<ACtxEntityIdentifier> childEntities) {
		
		final ACtxAssociation result = new ACtxAssociation(id);
		result.setLastModified(lastModified);
		
		result.setParentEntity(parentEntity);
		
		if (childEntities != null && !childEntities.isEmpty())
			for (final ACtxEntityIdentifier childEntity : childEntities)
				result.addChildEntity(childEntity);
				
		return result;
	}
		
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
}