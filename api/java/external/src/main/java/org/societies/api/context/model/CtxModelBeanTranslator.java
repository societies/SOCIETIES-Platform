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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAssociationIdentifierBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxAttributeValueTypeBean;
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

	private static CtxModelBeanTranslator instance = new CtxModelBeanTranslator();

	private CtxModelBeanTranslator() {}

	public static synchronized CtxModelBeanTranslator getInstance() {

		return instance;
	}

	public IndividualCtxEntityBean fromIndiCtxEntity (IndividualCtxEntity indiEntity) throws DatatypeConfigurationException {

		IndividualCtxEntityBean bean=new IndividualCtxEntityBean();
		bean.setId(fromCtxIdentifier(indiEntity.getId()));

		XMLGregorianCalendar lastModifiedXML = this.DateToXMLGregorianCalendar(indiEntity.getLastModified());
		bean.setLastModified(lastModifiedXML);

		List<CtxAssociationIdentifierBean> assocIdBeans = new ArrayList<CtxAssociationIdentifierBean>();
		for (CtxAssociationIdentifier assoc : indiEntity.getAssociations()) {
			assocIdBeans.add((CtxAssociationIdentifierBean) fromCtxIdentifier(assoc));
		}
		bean.setAssociations(assocIdBeans);

		List<CtxAttributeBean> attrIdBeans = new ArrayList<CtxAttributeBean>();
		for (CtxAttribute attr : indiEntity.getAttributes()) {
			attrIdBeans.add(fromCtxAttribute(attr));
		}
		bean.setAttributes(attrIdBeans);
		//indiEntity.getCommunities()

		List<CtxEntityIdentifierBean> commListBeans = new ArrayList<CtxEntityIdentifierBean>();
		for (CtxEntityIdentifier entId : indiEntity.getCommunities()) {
			commListBeans.add(fromCtxEntityIdentifier(entId));
		}

		bean.setCommunities(commListBeans);

		return bean;
	}


	public IndividualCtxEntity fromIndiCtxEntityBean (IndividualCtxEntityBean indiEntityBean) throws MalformedCtxIdentifierException, DatatypeConfigurationException {

		IndividualCtxEntity indiEntity = new IndividualCtxEntity(
				(CtxEntityIdentifier) fromCtxIdentifierBean(indiEntityBean.getId()));

		indiEntity.setLastModified(XMLGregorianCalendarToDate(indiEntityBean.getLastModified()));
		// Handle entity attributes
		for (CtxAttributeBean attrBean : indiEntityBean.getAttributes()){
			indiEntity.addAttribute(fromCtxAttributeBean(attrBean));
		}

		// Handle entity association IDs
		final Set<CtxAssociationIdentifier> assocIds = new HashSet<CtxAssociationIdentifier>();
		for (CtxAssociationIdentifierBean assocIdBean : indiEntityBean.getAssociations()){
			assocIds.add((CtxAssociationIdentifier) fromCtxIdentifierBean(assocIdBean));
		}
		indiEntity.setAssociations(assocIds);

		Set<CtxEntityIdentifier> communitiesSet = new HashSet<CtxEntityIdentifier>();
		for(CtxEntityIdentifierBean indiEntIDBean : indiEntityBean.getCommunities()){
			communitiesSet.add((CtxEntityIdentifier) fromCtxIdentifierBean(indiEntIDBean) );
		}
		indiEntity.setCommunities(communitiesSet);

		return indiEntity;
	}


	public CtxEntity fromCtxEntityBean(CtxEntityBean entityBean) throws DatatypeConfigurationException, MalformedCtxIdentifierException{

		CtxEntity entity = new CtxEntity(
				(CtxEntityIdentifier) fromCtxIdentifierBean(entityBean.getId()));
		entity.setLastModified(XMLGregorianCalendarToDate(entityBean.getLastModified()));
		// Handle entity attributes
		for (CtxAttributeBean attrBean : entityBean.getAttributes()){
			entity.addAttribute(fromCtxAttributeBean(attrBean));
		}

		// Handle entity association IDs
		final Set<CtxAssociationIdentifier> assocIds = new HashSet<CtxAssociationIdentifier>();
		for (CtxAssociationIdentifierBean assocIdBean : entityBean.getAssociations()){
			assocIds.add((CtxAssociationIdentifier) fromCtxIdentifierBean(assocIdBean));
			entity.setAssociations(assocIds);
		}

		return entity;	
	}


	public CtxEntityBean fromCtxEntity(CtxEntity entity) throws DatatypeConfigurationException {

		CtxEntityBean bean=new CtxEntityBean();
		bean.setId(fromCtxIdentifier(entity.getId()));

		XMLGregorianCalendar lastModifiedXML = this.DateToXMLGregorianCalendar(entity.getLastModified());
		bean.setLastModified(lastModifiedXML);

		List<CtxAssociationIdentifierBean> assocIdBeans = new ArrayList<CtxAssociationIdentifierBean>();
		for (CtxAssociationIdentifier assoc : entity.getAssociations()) {
			assocIdBeans.add((CtxAssociationIdentifierBean) fromCtxIdentifier(assoc));
		}
		bean.setAssociations(assocIdBeans);

		List<CtxAttributeBean> attrIdBeans = new ArrayList<CtxAttributeBean>();
		for (CtxAttribute attr : entity.getAttributes()) {
			attrIdBeans.add(fromCtxAttribute(attr));
		}
		bean.setAttributes(attrIdBeans);

		return bean;
	}


	public CtxIdentifierBean fromCtxIdentifier(CtxIdentifier identifier) {

		CtxIdentifierBean ctxIdBean = null;

		if (identifier.getModelType().equals(CtxModelType.ENTITY)) {
			ctxIdBean = new CtxEntityIdentifierBean();
			ctxIdBean.setString(identifier.toString());
		}
		else if (identifier.getModelType().equals(CtxModelType.ATTRIBUTE)) {
			ctxIdBean = new CtxAttributeIdentifierBean(); 
			ctxIdBean.setString(identifier.toString()); 
		}
		else if (identifier.getModelType().equals(CtxModelType.ASSOCIATION)) {
			ctxIdBean = new CtxAssociationIdentifierBean(); 
			ctxIdBean.setString(identifier.toString());
		}

		return ctxIdBean;
	}

	public CtxIdentifier fromCtxIdentifierBean(CtxIdentifierBean identifierBean) throws MalformedCtxIdentifierException {

		return CtxIdentifierFactory.getInstance().fromString(identifierBean.getString());
	}

	public CtxAttributeBean fromCtxAttribute(CtxAttribute attr) throws DatatypeConfigurationException {

		CtxAttributeBean bean = new CtxAttributeBean();
		bean.setBinaryValue(attr.getBinaryValue());
		bean.setDoubleValue(attr.getDoubleValue());
		bean.setHistoryRecorded(attr.isHistoryRecorded());
		bean.setId(fromCtxIdentifier(attr.getId()));
		bean.setIntegerValue(attr.getIntegerValue());
		bean.setLastModified(DateToXMLGregorianCalendar(attr.getLastModified()));
		bean.setSourceId(attr.getSourceId());
		bean.setStringValue(attr.getStringValue());
		bean.setValueMetric(attr.getValueMetric());
		bean.setValueType(fromCtxAttributeValueType(attr.getValueType()));
		bean.setSourceId(attr.getSourceId());
		bean.setQuality(fromCtxQuality(attr.getQuality()));

		return bean;
	}

	public CtxAttribute fromCtxAttributeBean(CtxAttributeBean bean) 
			throws DatatypeConfigurationException, MalformedCtxIdentifierException {

		final CtxAttribute object = new CtxAttribute(
				(CtxAttributeIdentifier) fromCtxIdentifierBean(bean.getId()));
		object.setLastModified(XMLGregorianCalendarToDate(bean.getLastModified()));
		// Handle value
		if (bean.getStringValue() != null)
			object.setValue(bean.getStringValue());
		else if (bean.getIntegerValue() != null)
			object.setValue(bean.getIntegerValue());
		else if (bean.getDoubleValue() != null)
			object.setValue(bean.getDoubleValue());
		else if (bean.getBinaryValue() != null)
			object.setValue(bean.getBinaryValue());
		// Handle value meta-data
		object.setValueType(fromCtxAttributeValueTypeBean(bean.getValueType()));
		object.setValueMetric(bean.getValueMetric());
		// Handle other params
		object.setHistoryRecorded(bean.isHistoryRecorded());
		object.setSourceId(bean.getSourceId());
		// Handle QoC
		object.getQuality().setLastUpdated(
				XMLGregorianCalendarToDate(bean.getQuality().getLastUpdated()));
		if(bean.getQuality().getOriginType() != null ){
			object.getQuality().setOriginType(fromCtxOriginTypeBean(bean.getQuality().getOriginType()));
		}
		object.getQuality().setPrecision(bean.getQuality().getPrecision());
		object.getQuality().setUpdateFrequency(bean.getQuality().getUpdateFrequency());

		return object;
	}

	public CtxAssociationBean fromCtxAssociation(CtxAssociation object) throws DatatypeConfigurationException {

		CtxAssociationBean bean = new CtxAssociationBean();
		bean.setId(fromCtxIdentifier(object.getId()));
		bean.setLastModified(DateToXMLGregorianCalendar(object.getLastModified()));
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

	public CtxAssociation fromCtxAssociationBean(CtxAssociationBean assocBean) throws DatatypeConfigurationException, MalformedCtxIdentifierException {

		CtxAssociation assoc = new CtxAssociation(
				(CtxAssociationIdentifier) fromCtxIdentifierBean(assocBean.getId()));
		assoc.setLastModified(XMLGregorianCalendarToDate(assocBean.getLastModified()));
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

		final CtxModelObjectBean bean;
		try {
			if(object instanceof IndividualCtxEntity){
				bean = this.fromIndiCtxEntity((IndividualCtxEntity) object);
			}				
			else if (object instanceof CtxEntity){
				bean = this.fromCtxEntity((CtxEntity) object);
			}else if (object instanceof CtxAttribute)
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

		final CtxModelObject object;
		try {
			if(bean instanceof IndividualCtxEntityBean)
				object = this.fromIndiCtxEntityBean((IndividualCtxEntityBean) bean);
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

	public CtxQualityBean fromCtxQuality(CtxQuality quality) throws DatatypeConfigurationException {

		CtxQualityBean bean=new CtxQualityBean();
		bean.setPrecision(quality.getPrecision());
		bean.setUpdateFrequency(quality.getUpdateFrequency());
		bean.setOriginType(fromCtxOriginType(quality.getOriginType()));
		bean.setLastUpdated(DateToXMLGregorianCalendar(quality.getLastUpdated()));

		return bean;
	}

	public CtxOriginTypeBean fromCtxOriginType(
			CtxOriginType originType) {

		CtxOriginTypeBean result = null;

		if(originType != null ) result = CtxOriginTypeBean.valueOf(originType.toString());	

		return result;	
	}

	public CtxOriginType fromCtxOriginTypeBean(
			CtxOriginTypeBean originTypeBean) {

		CtxOriginType result = null;
		if (originTypeBean != null)	result = CtxOriginType.valueOf(originTypeBean.toString());

		return result; 	
	}

	public CtxModelTypeBean CtxModelTypeBeanFromCtxModelType(
			CtxModelType modelType) {

		return CtxModelTypeBean.valueOf(modelType.toString());	
	}

	public CtxModelType CtxModelTypeFromCtxModelTypeBean(
			CtxModelTypeBean modelTypeBean) {

		return CtxModelType.valueOf(modelTypeBean.toString());	
	}

	public CtxAttributeValueTypeBean fromCtxAttributeValueType(CtxAttributeValueType valueType) {

		return CtxAttributeValueTypeBean.valueOf(valueType.toString());	
	}

	public CtxAttributeValueType fromCtxAttributeValueTypeBean(CtxAttributeValueTypeBean valueTypeBean) {

		return CtxAttributeValueType.valueOf(valueTypeBean.toString());	
	}

	public XMLGregorianCalendar DateToXMLGregorianCalendar(Date myDate) throws DatatypeConfigurationException {

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(myDate);
		XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		return xmlDate;
	}

	public Date XMLGregorianCalendarToDate(XMLGregorianCalendar xml) throws DatatypeConfigurationException {

		Date dt = xml.toGregorianCalendar().getTime();
		return dt;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
}