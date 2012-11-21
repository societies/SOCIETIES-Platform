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
package org.societies.context.user.db.impl.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelObjectFactory;
import org.societies.api.context.model.IndividualCtxEntity;

public final class CtxModelDAOTranslator {

	private static CtxModelDAOTranslator instance = new CtxModelDAOTranslator();

	private CtxModelDAOTranslator() {}

	public static synchronized CtxModelDAOTranslator getInstance() {

		return instance;
	}

	public CtxEntityDAO fromCtxEntity(CtxEntity mo) {

		final CtxEntityDAO dao = new CtxEntityDAO(mo.getId());
		dao.setLastModified(mo.getLastModified());

		for (final CtxAttribute attribute : mo.getAttributes()) {
			dao.addAttribute(this.fromCtxAttribute(attribute));
		}
		
		return dao;
	}

	public CtxEntity fromCtxEntityDAO(CtxEntityDAO dao) {

		final Set<CtxAttribute> attributes = new HashSet<CtxAttribute>();
		for (final CtxAttributeDAO attributeDAO : dao.getAttributes())
			attributes.add(this.fromCtxAttributeDAO(attributeDAO));
		
		final CtxEntity mo = CtxModelObjectFactory.getInstance()
				.createEntity(dao.getId(), dao.getLastModified(), attributes,
						dao.getAssociations());

		return mo;
	}
	
	public IndividualCtxEntityDAO fromIndividualCtxEntity(IndividualCtxEntity mo) {

		final IndividualCtxEntityDAO dao = new IndividualCtxEntityDAO(mo.getId());
		dao.setLastModified(mo.getLastModified());

		for (final CtxAttribute attribute : mo.getAttributes()) {
			dao.addAttribute(this.fromCtxAttribute(attribute));
		}
		
		return dao;
	}

	public IndividualCtxEntity fromIndividualCtxEntityDAO(IndividualCtxEntityDAO dao) {

		final Set<CtxAttribute> attributes = new HashSet<CtxAttribute>();
		for (final CtxAttributeDAO attributeDAO : dao.getAttributes())
			attributes.add(this.fromCtxAttributeDAO(attributeDAO));
		
		final IndividualCtxEntity mo = CtxModelObjectFactory.getInstance()
				.createIndividualEntity(dao.getId(), dao.getLastModified(),
						attributes, dao.getAssociations(), dao.getCommunities());
		
		return mo;
	}

	public CtxAttributeDAO fromCtxAttribute(CtxAttribute mo) {
		
		final CtxAttributeDAO dao = new CtxAttributeDAO(mo.getId());
		dao.setQuality(new CtxQualityDAO(mo.getId()));
		dao.setLastModified(mo.getLastModified());
		dao.setHistoryRecorded(mo.isHistoryRecorded());
		dao.setStringValue(mo.getStringValue());
		dao.setIntegerValue(mo.getIntegerValue());
		dao.setDoubleValue(mo.getDoubleValue());
		dao.setBinaryValue(mo.getBinaryValue());
		dao.setValueType(mo.getValueType());	
		dao.setValueMetric(mo.getValueMetric());
		dao.setSourceId(mo.getSourceId());
		dao.getQuality().setLastUpdated(mo.getQuality().getLastUpdated());
		dao.getQuality().setOriginType(mo.getQuality().getOriginType());
		dao.getQuality().setPrecision(mo.getQuality().getPrecision());
		dao.getQuality().setUpdateFrequency(mo.getQuality().getUpdateFrequency());
		
		return dao;
	}

	public CtxAttribute fromCtxAttributeDAO(CtxAttributeDAO dao) {
		
		final Serializable value;
		if (dao.getStringValue() != null)
			value = dao.getStringValue();
		else if (dao.getIntegerValue() != null)
			value = dao.getIntegerValue();
		else if (dao.getDoubleValue() != null)
			value = dao.getDoubleValue();
		else if (dao.getBinaryValue() != null)
			value = dao.getBinaryValue();
		else 
			value = null;
		final CtxAttribute mo = CtxModelObjectFactory.getInstance()
				.createAttribute(dao.getId(), dao.getLastModified(),
						dao.getQuality().getLastUpdated(), value);
		
		mo.setHistoryRecorded(dao.isHistoryRecorded()); 
		mo.setValueType(dao.getValueType());
		mo.setValueMetric(dao.getValueMetric());
		mo.setSourceId(dao.getSourceId());
		mo.getQuality().setOriginType(dao.getQuality().getOriginType());
		mo.getQuality().setPrecision(dao.getQuality().getPrecision());
		mo.getQuality().setUpdateFrequency(dao.getQuality().getUpdateFrequency());
		
		return mo;
	}

	public CtxAssociationDAO fromCtxAssociation(CtxAssociation mo) {

		CtxAssociationDAO dao = new CtxAssociationDAO(mo.getId());
		dao.setLastModified(mo.getLastModified());
		// Set parent entity
		dao.setParentEntity(mo.getParentEntity());
		// Set child entities
		dao.setChildEntities(mo.getChildEntities());

		return dao;
	}

	public CtxAssociation fromCtxAssociationDAO(CtxAssociationDAO dao) {

		return CtxModelObjectFactory.getInstance()
			.createAssociation(dao.getId(), dao.getLastModified(),
				dao.getParentEntity(), dao.getChildEntities());		
	}

	public CtxModelObjectDAO fromCtxModelObject(CtxModelObject mo) {

		final CtxModelObjectDAO dao;
		
		if (mo instanceof CtxEntity)
			dao = this.fromCtxEntity((CtxEntity) mo);
		else if (mo instanceof IndividualCtxEntity)
			dao = this.fromIndividualCtxEntity((IndividualCtxEntity) mo);
		else if (mo instanceof CtxAttribute) 
			dao = this.fromCtxAttribute((CtxAttribute) mo);
		else if (mo instanceof CtxAssociation)
			dao = this.fromCtxAssociation((CtxAssociation) mo);
		else
			throw new IllegalArgumentException("unsupported mo type " + mo.getClass().getName());

		return dao;
	}

	public CtxModelObject fromCtxModelObjectDAO(CtxModelObjectDAO dao){

		final CtxModelObject mo;
		
		if (dao instanceof IndividualCtxEntityDAO)
			mo = this.fromIndividualCtxEntityDAO((IndividualCtxEntityDAO) dao);
		else if (dao instanceof CtxEntityDAO)
			mo = this.fromCtxEntityDAO((CtxEntityDAO) dao);
		else if (dao instanceof CtxAttributeDAO)
			mo = this.fromCtxAttributeDAO((CtxAttributeDAO) dao);
		else if (dao instanceof CtxAssociationDAO)
			mo = this.fromCtxAssociationDAO((CtxAssociationDAO) dao);
		else
			throw new IllegalArgumentException("unsupported dao type " + dao.getClass().getName());
		
		return mo;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
}