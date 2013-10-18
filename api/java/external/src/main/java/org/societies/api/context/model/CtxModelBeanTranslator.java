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
package org.societies.api.context.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.context.model.CommunityCtxEntityBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAssociationIdentifierBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeBondBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxAttributeValueTypeBean;
import org.societies.api.schema.context.model.CtxBondBean;
import org.societies.api.schema.context.model.CtxBondOriginTypeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;
import org.societies.api.schema.context.model.CtxOriginTypeBean;
import org.societies.api.schema.context.model.CtxQualityBean;
import org.societies.api.schema.context.model.IndividualCtxEntityBean;

public final class CtxModelBeanTranslator {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxModelBeanTranslator.class);
	
	/** A constant holding a Not-an-Integer (NaI) value of integer type. */
	public static Integer NaI = Integer.MIN_VALUE;
	
	/** A constant holding a Not-a-Double (NaD) value of double type. */
	public static Double NaD = Double.MIN_VALUE;
	
	/** A constant holding a Not-a-Binary (NaB) value of binary type. */
	public static byte[] NaB = new byte[] { Byte.MIN_VALUE };

	private static CtxModelBeanTranslator instance = new CtxModelBeanTranslator();

	private CtxModelBeanTranslator() {}

	public static synchronized CtxModelBeanTranslator getInstance() {

		return instance;
	}

	public CommunityCtxEntity fromCommunityCtxEntityBean(final CommunityCtxEntityBean commEntityBean) 
			throws MalformedCtxIdentifierException {
		
		if (commEntityBean == null)
			return null;
		
		final CommunityCtxEntity commEntity = new CommunityCtxEntity(
				(CtxEntityIdentifier) fromCtxIdentifierBean(commEntityBean.getId()));
		commEntity.setLastModified(commEntityBean.getLastModified());
		// Handle entity attributes
		for (final CtxAttributeBean attrBean : commEntityBean.getAttributes()){
			commEntity.addAttribute(fromCtxAttributeBean(attrBean));
		}
		// Handle entity associations
		final Set<CtxAssociationIdentifier> assocIds = new HashSet<CtxAssociationIdentifier>();
		for (final CtxAssociationIdentifierBean assocIdBean : commEntityBean.getAssociations()){
			assocIds.add((CtxAssociationIdentifier) fromCtxIdentifierBean(assocIdBean));
		}
		commEntity.setAssociations(assocIds);
		// Handle communities this community is member of
		final Set<CtxEntityIdentifier> communityEntIds = new HashSet<CtxEntityIdentifier>();
		for (final CtxEntityIdentifierBean communityEntIdBean : commEntityBean.getCommunities()) {
			communityEntIds.add((CtxEntityIdentifier) fromCtxIdentifierBean(communityEntIdBean));
		}		
		commEntity.setCommunities(communityEntIds);
		// Handle community members
		final Set<CtxEntityIdentifier> memberEntIds = new HashSet<CtxEntityIdentifier>();
		for (final CtxEntityIdentifierBean memberEntIdBean : commEntityBean.getMembers()) {
			memberEntIds.add((CtxEntityIdentifier) fromCtxIdentifierBean(memberEntIdBean));
		}		
		commEntity.setMembers(memberEntIds);
		// Handle bonds
		for(final CtxBondBean bondBean : commEntityBean.getBonds()){
			commEntity.addBond(fromCtxBondBean(bondBean));
		}

		return commEntity;
	}

	public CommunityCtxEntityBean fromCommunityCtxEntity(final CommunityCtxEntity commEntity) {
		
		if (commEntity == null)
			return null;
		
		final CommunityCtxEntityBean bean = new CommunityCtxEntityBean();
		bean.setId(fromCtxIdentifier(commEntity.getId()));
		bean.setLastModified(commEntity.getLastModified());
		// Handle entity attributes
		final List<CtxAttributeBean> attrIdBeans = new ArrayList<CtxAttributeBean>();
		for (final CtxAttribute attr : commEntity.getAttributes()) {
			attrIdBeans.add(fromCtxAttribute(attr));
		}
		bean.setAttributes(attrIdBeans);
		// Handle entity associations
		final List<CtxAssociationIdentifierBean> assocIdBeans = new ArrayList<CtxAssociationIdentifierBean>();
		for (final CtxAssociationIdentifier assoc : commEntity.getAssociations()) {
			assocIdBeans.add((CtxAssociationIdentifierBean) fromCtxIdentifier(assoc));
		}
		bean.setAssociations(assocIdBeans);
		// Handle communities this community is member of
		final List<CtxEntityIdentifierBean> communityEntIdBeans = new ArrayList<CtxEntityIdentifierBean>();
		for (final CtxEntityIdentifier communityEntId : commEntity.getCommunities()) {
			communityEntIdBeans.add(fromCtxEntityIdentifier(communityEntId));
		}
		bean.setCommunities(communityEntIdBeans);
		// Handle community members
		final List<CtxEntityIdentifierBean> memberEntIdBeans = new ArrayList<CtxEntityIdentifierBean>();
		for (final CtxEntityIdentifier memberEntId : commEntity.getMembers()) {
			memberEntIdBeans.add(fromCtxEntityIdentifier(memberEntId));
		}
		bean.setMembers(memberEntIdBeans);
		// Handle bonds
		final List<CtxBondBean> bondBeansList = new ArrayList<CtxBondBean>();
		for(final CtxBond bond : commEntity.getBonds()) {
			bondBeansList.add(fromCtxBond(bond));
		}
		bean.setBonds(bondBeansList);
		
		return bean;
	}

	private CtxBond fromCtxBondBean(CtxBondBean bondBean){

		CtxAttributeBond bean = null;

		if(bondBean.getModelType().equals(CtxModelType.ATTRIBUTE)){
			bean = this.fromCtxAttributeBondBean((CtxAttributeBondBean)bondBean);
		} else if (bondBean.getModelType().equals(CtxModelType.ASSOCIATION)){
			//	bean = this.fromCtxAssociationBond((CtxAssociationBond)bond);
		}

		return bean;
	}

	private CtxBondBean fromCtxBond(CtxBond bond){

		CtxAttributeBondBean bean = null;

		if(bond.getModelType().equals(CtxModelType.ATTRIBUTE)){
			bean = this.fromCtxAttributeBond((CtxAttributeBond)bond);
		} else if (bond.getModelType().equals(CtxModelType.ASSOCIATION)){
			//	bean = this.fromCtxAssociationBond((CtxAssociationBond)bond);
		}

		return bean;
	}

	/*
	private CtxAssociationBondBean fromCtxAssociationBond(CtxAssociationBond attrBond){

		CtxAssociationBondBean bean = new CtxAssociationBondBean();
	}
	 */

	//TODO fix the following 
	private CtxAttributeBond fromCtxAttributeBondBean(CtxAttributeBondBean attrBondBean){

		CtxAttributeBond attrBond = null;
		/*
		CtxAttributeBond attrBond= new CtxAttributeBond();


		attrBondBean.setMaxValue(attrBond.getMaxValue());
		attrBondBean.setMinValue(attrBond.getMinValue());

		CtxBondOriginType bondOriginType = attrBond.getOriginType();
		attrBondBean.setOriginType(this.fromCtxBondOriginType(bondOriginType));
		attrBondBean.setModelType(ctxModelTypeBeanFromCtxModelType(attrBond.getModelType()));
		attrBondBean.setType(attrBond.getType());
		attrBondBean.setValueType(fromCtxAttributeValueType(attrBond.getValueType()));
		 */	
		return attrBond;
	}

	private CtxAttributeBondBean fromCtxAttributeBond(CtxAttributeBond attrBond){

		final CtxAttributeBondBean attrBondBean = new CtxAttributeBondBean();

		attrBondBean.setMaxValue(attrBond.getMaxValue());
		attrBondBean.setMinValue(attrBond.getMinValue());

		CtxBondOriginType bondOriginType = attrBond.getOriginType();
		attrBondBean.setOriginType(this.fromCtxBondOriginType(bondOriginType));
		attrBondBean.setModelType(ctxModelTypeBeanFromCtxModelType(attrBond.getModelType()));
		attrBondBean.setType(attrBond.getType());
		attrBondBean.setValueType(fromCtxAttributeValueType(attrBond.getValueType()));

		return attrBondBean;
	}

	public IndividualCtxEntityBean fromIndiCtxEntity (IndividualCtxEntity indiEntity) {

		final IndividualCtxEntityBean bean = new IndividualCtxEntityBean();
		bean.setId(fromCtxIdentifier(indiEntity.getId()));
		bean.setLastModified(indiEntity.getLastModified());
		// Handle entity attributes
		final List<CtxAttributeBean> attrIdBeans = new ArrayList<CtxAttributeBean>();
		for (CtxAttribute attr : indiEntity.getAttributes()) {
			attrIdBeans.add(fromCtxAttribute(attr));
		}
		bean.setAttributes(attrIdBeans);
		// Handle entity associations
		final List<CtxAssociationIdentifierBean> assocIdBeans = new ArrayList<CtxAssociationIdentifierBean>();
		for (CtxAssociationIdentifier assoc : indiEntity.getAssociations()) {
			assocIdBeans.add((CtxAssociationIdentifierBean) fromCtxIdentifier(assoc));
		}
		bean.setAssociations(assocIdBeans);
		// Handle communities this individual entity is member of
		final List<CtxEntityIdentifierBean> commListBeans = new ArrayList<CtxEntityIdentifierBean>();
		for (CtxEntityIdentifier entId : indiEntity.getCommunities()) {
			commListBeans.add(fromCtxEntityIdentifier(entId));
		}
		bean.setCommunities(commListBeans);

		return bean;
	}

	public IndividualCtxEntity fromIndiCtxEntityBean (IndividualCtxEntityBean indiEntityBean) throws MalformedCtxIdentifierException {

		final IndividualCtxEntity indiEntity = new IndividualCtxEntity(
				(CtxEntityIdentifier) fromCtxIdentifierBean(indiEntityBean.getId()));
		indiEntity.setLastModified(indiEntityBean.getLastModified());
		// Handle entity attributes
		for (final CtxAttributeBean attrBean : indiEntityBean.getAttributes()){
			indiEntity.addAttribute(fromCtxAttributeBean(attrBean));
		}
		// Handle entity associations
		final Set<CtxAssociationIdentifier> assocIds = new HashSet<CtxAssociationIdentifier>();
		for (final CtxAssociationIdentifierBean assocIdBean : indiEntityBean.getAssociations()){
			assocIds.add((CtxAssociationIdentifier) fromCtxIdentifierBean(assocIdBean));
		}
		indiEntity.setAssociations(assocIds);
		// Handle communities this individual entity is member of
		final Set<CtxEntityIdentifier> communitiesSet = new HashSet<CtxEntityIdentifier>();
		for (final CtxEntityIdentifierBean indiEntIDBean : indiEntityBean.getCommunities()){
			communitiesSet.add((CtxEntityIdentifier) fromCtxIdentifierBean(indiEntIDBean) );
		}
		indiEntity.setCommunities(communitiesSet);
		
		return indiEntity;
	}

	public CtxEntity fromCtxEntityBean(final CtxEntityBean entityBean) 
			throws MalformedCtxIdentifierException {

		final CtxEntity entity = new CtxEntity(
				(CtxEntityIdentifier) fromCtxIdentifierBean(entityBean.getId()));
		entity.setLastModified(entityBean.getLastModified());
		// Handle entity attributes
		for (CtxAttributeBean attrBean : entityBean.getAttributes()){
			entity.addAttribute(fromCtxAttributeBean(attrBean));
		}
		// Handle entity associations
		final Set<CtxAssociationIdentifier> assocIds = new HashSet<CtxAssociationIdentifier>();
		for (final CtxAssociationIdentifierBean assocIdBean : entityBean.getAssociations()) {
			assocIds.add((CtxAssociationIdentifier) fromCtxIdentifierBean(assocIdBean));	
		}
		entity.setAssociations(assocIds);
		
		return entity;	
	}

	public CtxEntityBean fromCtxEntity(CtxEntity entity) {

		final CtxEntityBean bean = new CtxEntityBean();
		bean.setId(fromCtxIdentifier(entity.getId()));
		bean.setLastModified(entity.getLastModified());
		// Handle entity attributes
		final List<CtxAttributeBean> attrIdBeans = new ArrayList<CtxAttributeBean>();
		for (final CtxAttribute attr : entity.getAttributes()) {
			attrIdBeans.add(fromCtxAttribute(attr));
		}
		bean.setAttributes(attrIdBeans);
		// Handle entity associations
		final List<CtxAssociationIdentifierBean> assocIdBeans = new ArrayList<CtxAssociationIdentifierBean>();
		for (final CtxAssociationIdentifier assoc : entity.getAssociations()) {
			assocIdBeans.add((CtxAssociationIdentifierBean) fromCtxIdentifier(assoc));
		}
		bean.setAssociations(assocIdBeans);

		return bean;
	}

	public CtxIdentifierBean fromCtxIdentifier(CtxIdentifier identifier) {

		CtxIdentifierBean ctxIdBean = null;

		if (identifier.getModelType().equals(CtxModelType.ENTITY)) {
			ctxIdBean = new CtxEntityIdentifierBean();
			ctxIdBean.setString(identifier.toString());
			ctxIdBean.setOwnerId(identifier.getOwnerId());
			ctxIdBean.setType(identifier.getType());
			ctxIdBean.setModelType(ctxModelTypeBeanFromCtxModelType(identifier.getModelType()));
			ctxIdBean.setObjectNumber(identifier.getObjectNumber());
		}
		else if (identifier.getModelType().equals(CtxModelType.ATTRIBUTE)) {
			ctxIdBean = new CtxAttributeIdentifierBean(); 
			ctxIdBean.setString(identifier.toString()); 
			ctxIdBean.setOwnerId(identifier.getOwnerId());
			ctxIdBean.setType(identifier.getType());
			ctxIdBean.setModelType(ctxModelTypeBeanFromCtxModelType(identifier.getModelType()));
			ctxIdBean.setObjectNumber(identifier.getObjectNumber());
		}
		else if (identifier.getModelType().equals(CtxModelType.ASSOCIATION)) {
			ctxIdBean = new CtxAssociationIdentifierBean(); 
			ctxIdBean.setString(identifier.toString());
			ctxIdBean.setOwnerId(identifier.getOwnerId());
			ctxIdBean.setType(identifier.getType());
			ctxIdBean.setModelType(ctxModelTypeBeanFromCtxModelType(identifier.getModelType()));
			ctxIdBean.setObjectNumber(identifier.getObjectNumber());
		}

		return ctxIdBean;
	}

	public CtxIdentifier fromCtxIdentifierBean(CtxIdentifierBean identifierBean) 
			throws MalformedCtxIdentifierException {

		return CtxIdentifierFactory.getInstance().fromString(identifierBean.getString());
	}

	public CtxAttributeBean fromCtxAttribute(CtxAttribute attr) {

		CtxAttributeBean bean = new CtxAttributeBean();
		// id
		bean.setId(fromCtxIdentifier(attr.getId()));
		// lastModified
		bean.setLastModified(attr.getLastModified());
		// binaryValue
		if (attr.getBinaryValue() == null)
			bean.setBinaryValue(NaB);
		else
			bean.setBinaryValue(attr.getBinaryValue());
		// doubleValue
		if (attr.getDoubleValue() == null)
			bean.setDoubleValue(NaD);
		else
			bean.setDoubleValue(attr.getDoubleValue());
		bean.setHistoryRecorded(attr.isHistoryRecorded());
		// integerValue
		if (attr.getIntegerValue() == null)
			bean.setIntegerValue(NaI);
		else
			bean.setIntegerValue(attr.getIntegerValue());
		// stringValue
		bean.setStringValue(attr.getStringValue());
		// valueType
		bean.setValueType(fromCtxAttributeValueType(attr.getValueType()));
		// valueMetric
		bean.setValueMetric(attr.getValueMetric());
		// historyRecorded
		bean.setHistoryRecorded(attr.isHistoryRecorded());
		// sourceId
		bean.setSourceId(attr.getSourceId());
		// quality
		bean.setQuality(fromCtxQuality(attr.getQuality()));

		return bean;
	}

	public CtxAttribute fromCtxAttributeBean(CtxAttributeBean bean) 
			throws MalformedCtxIdentifierException {

		// id
		final CtxAttribute object = new CtxAttribute(
				(CtxAttributeIdentifier) fromCtxIdentifierBean(bean.getId()));
		// lastModified
		object.setLastModified(bean.getLastModified());
		// value
		if (bean.getStringValue() != null)
			object.setValue(bean.getStringValue());
		else if (! NaI.equals(bean.getIntegerValue()))
			object.setValue(bean.getIntegerValue());
		else if (! NaD.equals(bean.getDoubleValue()))
			object.setValue(bean.getDoubleValue());
		else if (!Arrays.equals(NaB, bean.getBinaryValue()))
			object.setValue(bean.getBinaryValue());
			
		// valueType
		object.setValueType(fromCtxAttributeValueTypeBean(bean.getValueType()));
		// valueMetric
		object.setValueMetric(bean.getValueMetric());
		// historyRecorded
		object.setHistoryRecorded(bean.isHistoryRecorded());
		// sourceId
		object.setSourceId(bean.getSourceId());
		// quality
		object.getQuality().setLastUpdated(bean.getQuality().getLastUpdated());
		if(bean.getQuality().getOriginType() != null)
			object.getQuality().setOriginType(fromCtxOriginTypeBean(bean.getQuality().getOriginType()));
		if (bean.getQuality().getPrecision() != NaD)
			object.getQuality().setPrecision(bean.getQuality().getPrecision());
		if (bean.getQuality().getUpdateFrequency() != NaD)
			object.getQuality().setUpdateFrequency(bean.getQuality().getUpdateFrequency());

		return object;
	}

	public CtxAssociationBean fromCtxAssociation(CtxAssociation object) {

		final CtxAssociationBean bean = new CtxAssociationBean();
		bean.setId(fromCtxIdentifier(object.getId()));
		bean.setLastModified(object.getLastModified());
		// Handle parent entity
		if (object.getParentEntity() != null)
			bean.setParentEntity(fromCtxEntityIdentifier(object.getParentEntity()));
		// Handle child entities
		final List<CtxEntityIdentifierBean> childEntities = new ArrayList<CtxEntityIdentifierBean>();
		for (CtxEntityIdentifier childEntityId : object.getChildEntities())
			childEntities.add(fromCtxEntityIdentifier(childEntityId));
		bean.setChildEntities(childEntities);

		return bean;
	}

	public CtxAssociation fromCtxAssociationBean(final CtxAssociationBean assocBean) 
			throws MalformedCtxIdentifierException {

		final CtxAssociation assoc = new CtxAssociation(
				(CtxAssociationIdentifier) fromCtxIdentifierBean(assocBean.getId()));
		assoc.setLastModified(assocBean.getLastModified());
		// Handle parent entity
		if (assocBean.getParentEntity() != null)
			assoc.setParentEntity((CtxEntityIdentifier) fromCtxIdentifierBean(assocBean.getParentEntity()));
		// Handle child entities
		for (CtxEntityIdentifierBean childEntityIdBean : assocBean.getChildEntities())
			assoc.addChildEntity((CtxEntityIdentifier) fromCtxIdentifierBean(childEntityIdBean));

		return assoc;
	}

	public CtxModelObjectBean fromCtxModelObject(CtxModelObject object) {

		if (LOG.isDebugEnabled())
			LOG.debug("Creating CtxModelObject bean from instance " + object);
		if (object == null)
			return null;
		
		final CtxModelObjectBean bean;
		try {
			if(object instanceof IndividualCtxEntity)
				bean = this.fromIndiCtxEntity((IndividualCtxEntity) object);
			else if (object instanceof CommunityCtxEntity)
				bean = this.fromCommunityCtxEntity((CommunityCtxEntity) object);
			else if (object instanceof CtxEntity)
				bean = this.fromCtxEntity((CtxEntity) object);
			else if (object instanceof CtxAttribute)
				bean = this.fromCtxAttribute((CtxAttribute) object);
			else if (object instanceof CtxAssociation)
				bean = this.fromCtxAssociation((CtxAssociation) object);
			else
				throw new IllegalArgumentException("Could not create CtxModelObject bean from instance "
						+ object + ": Unsupported CtxModelObject class: " + object.getClass().getName());
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not create CtxModelObject bean from instance "
					+ object + ": " + e.getLocalizedMessage(), e);
		}

		return bean;
	}

	public CtxModelObject fromCtxModelObjectBean(CtxModelObjectBean bean){

		if (LOG.isDebugEnabled())
			LOG.debug("Creating CtxModelObject instance from bean " + bean);
		if (bean == null)
			return null;
		
		final CtxModelObject object;
		try {
			if (bean instanceof IndividualCtxEntityBean)
				object = this.fromIndiCtxEntityBean((IndividualCtxEntityBean) bean);
			else if (bean instanceof CommunityCtxEntityBean)
				object = this.fromCommunityCtxEntityBean((CommunityCtxEntityBean) bean);
			else if (bean instanceof CtxEntityBean)
				object = this.fromCtxEntityBean((CtxEntityBean) bean);
			else if (bean instanceof CtxAttributeBean)
				object = this.fromCtxAttributeBean((CtxAttributeBean) bean);
			else if (bean instanceof CtxAssociationBean)
				object = this.fromCtxAssociationBean((CtxAssociationBean) bean);
			else
				throw new IllegalArgumentException("Could not create CtxModelObject instance from bean "
						+ bean + ": Unsupported bean class: " + bean.getClass().getName());
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not create CtxModelObject instance from bean "
					+ bean + ": " + e.getLocalizedMessage(), e);
		} 

		return object;
	}

	private CtxEntityIdentifierBean fromCtxEntityIdentifier(
			CtxEntityIdentifier object) {

		final CtxEntityIdentifierBean bean = new CtxEntityIdentifierBean();
		bean.setString(object.toString());
		return bean;
	}

	public CtxQualityBean fromCtxQuality(CtxQuality quality) {

		final CtxQualityBean bean = new CtxQualityBean();
		if (quality.getPrecision() == null)
			bean.setPrecision(NaD);
		else
			bean.setPrecision(quality.getPrecision());
		if (quality.getUpdateFrequency() == null)
			bean.setUpdateFrequency(NaD);
		else
			bean.setUpdateFrequency(quality.getUpdateFrequency());
		bean.setOriginType(fromCtxOriginType(quality.getOriginType()));
		bean.setLastUpdated(quality.getLastUpdated());

		return bean;
	}

	public CtxBondOriginTypeBean fromCtxBondOriginType(CtxBondOriginType originType) {

		if (originType == null) 
			return null;
		
		CtxBondOriginTypeBean result = null;
		switch (originType) {
		case MANUALLY_SET: 
			result = CtxBondOriginTypeBean.MANUALLY_SET;
			break;
		case INHERITED: 
			result = CtxBondOriginTypeBean.INHERITED;
			break;
		case DISCOVERED: 
			result = CtxBondOriginTypeBean.DISCOVERED;
			break;
		}

		return result;	
	}


	public CtxBondOriginType fromCtxBondOriginTypeBean(CtxBondOriginTypeBean originType) {

		if (originType == null ) 
			return null;
		
		CtxBondOriginType result = null;
		switch (originType) {
		case MANUALLY_SET: 
			result = CtxBondOriginType.MANUALLY_SET;
			break;
		case INHERITED: 
			result = CtxBondOriginType.INHERITED;
			break;
		case DISCOVERED: 
			result = CtxBondOriginType.DISCOVERED;
			break;
		}

		return result;	
	}

	public CtxOriginTypeBean fromCtxOriginType(final CtxOriginType originType) {

		if(originType == null ) 
			return null;
		
		CtxOriginTypeBean result = null;
		switch (originType) {
		case MANUALLY_SET:
			result = CtxOriginTypeBean.MANUALLY_SET;
			break;
		case INFERRED:
			result = CtxOriginTypeBean.INFERRED;
			break;
		case INHERITED:
			result = CtxOriginTypeBean.INHERITED;
			break;
		case SENSED:
			result = CtxOriginTypeBean.SENSED;
			break;
		}

		return result;	
	}

	public CtxOriginType fromCtxOriginTypeBean(
			CtxOriginTypeBean originTypeBean) {

		if (originTypeBean == null)	
			return null; 
		
		CtxOriginType result = null;
		switch (originTypeBean) {
		case MANUALLY_SET: 
			result = CtxOriginType.MANUALLY_SET;
			break;
		case INFERRED:
			result = CtxOriginType.INFERRED;
			break;
		case INHERITED: 
			result = CtxOriginType.INHERITED;
			break;
		case SENSED:
			result = CtxOriginType.SENSED;
			break;
		}

		return result; 	
	}

	public CtxModelTypeBean ctxModelTypeBeanFromCtxModelType(
			CtxModelType modelType) {

		if (modelType == null)
			return null;
		
		CtxModelTypeBean result = null;
		switch (modelType) {
		case ASSOCIATION:
			result = CtxModelTypeBean.ASSOCIATION;
			break;
		case ATTRIBUTE: 
			result = CtxModelTypeBean.ATTRIBUTE;
			break;
		case ENTITY:
			result = CtxModelTypeBean.ENTITY;
			break;
		}

		return result;	
	}

	public CtxModelType ctxModelTypeFromCtxModelTypeBean(
			CtxModelTypeBean modelTypeBean) {

		if (modelTypeBean == null)
			return null;
		
		CtxModelType result = null;
		switch (modelTypeBean) {
		case ASSOCIATION:
			result = CtxModelType.ASSOCIATION;
			break;
		case ATTRIBUTE:
			result = CtxModelType.ATTRIBUTE;
			break;
		case ENTITY:
			result = CtxModelType.ENTITY;
			break;
		}

		return result;	
	}

	public CtxAttributeValueTypeBean fromCtxAttributeValueType(CtxAttributeValueType valueType) {

		if (valueType == null)
			return null; 

		CtxAttributeValueTypeBean result = null;
		switch (valueType) {
		case BINARY: 
			result = CtxAttributeValueTypeBean.BINARY;
			break;
		case COMPLEX: 
			result = CtxAttributeValueTypeBean.COMPLEX;
			break;
		case DOUBLE:
			result = CtxAttributeValueTypeBean.DOUBLE;
			break;
		case EMPTY: 
			result = CtxAttributeValueTypeBean.EMPTY;
			break;
		case INTEGER:
			result = CtxAttributeValueTypeBean.INTEGER;
			break;
		case STRING:
			result = CtxAttributeValueTypeBean.STRING;
			break;
		}

		return result;
	}

	public CtxAttributeValueType fromCtxAttributeValueTypeBean(CtxAttributeValueTypeBean valueTypeBean) {

		if (valueTypeBean == null)
			return null; 
		
		CtxAttributeValueType result = null;
		switch (valueTypeBean) {
		case BINARY:
			result = CtxAttributeValueType.BINARY;
			break;
		case COMPLEX:
			result = CtxAttributeValueType.COMPLEX;
			break;
		case DOUBLE:
			result = CtxAttributeValueType.DOUBLE;
			break;
		case EMPTY:
			result = CtxAttributeValueType.EMPTY;
			break;
		case INTEGER: 
			result = CtxAttributeValueType.INTEGER;
			break;
		case STRING:
			result = CtxAttributeValueType.STRING;
			break;
		}

		return result;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
}