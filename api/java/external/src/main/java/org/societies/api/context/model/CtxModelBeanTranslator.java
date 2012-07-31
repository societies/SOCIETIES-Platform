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
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAssociationIdentifierBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxAttributeValueTypeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;
import org.societies.api.schema.context.model.CtxOriginTypeBean;
import org.societies.api.schema.context.model.CtxQualityBean;

public final class CtxModelBeanTranslator {
	
	private static CtxModelBeanTranslator instance = new CtxModelBeanTranslator();
	
	private CtxModelBeanTranslator() {}
	
	public static synchronized CtxModelBeanTranslator getInstance() {
		
		return instance;
	}
	
	public CtxEntityBean fromCtxEntity(CtxEntity entity) throws DatatypeConfigurationException{
		
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
	
	public CtxEntity fromCtxEntityBean(CtxEntityBean entityBean) throws DatatypeConfigurationException, MalformedCtxIdentifierException{
		
		CtxEntity entity = new CtxEntity(
				(CtxEntityIdentifier) fromCtxIdentifierBean(entityBean.getId()));
		entity.setLastModified(XMLGregorianCalendarToDate(entityBean.getLastModified()));
		// Handle entity attributes
		for (CtxAttributeBean attrBean : entityBean.getAttributes())
			entity.addAttribute(fromCtxAttributeBean(attrBean));
		// Handle entity association IDs
		for (CtxAssociationIdentifierBean assocIdBean : entityBean.getAssociations())
			entity.addAssociation((CtxAssociationIdentifier) fromCtxIdentifierBean(assocIdBean));
		
		return entity;	
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
		
		CtxAttributeBean bean=new CtxAttributeBean();
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
		bean.setQuality(CtxQualityBeanFromCtxQuality(attr.getQuality()));
		
		return bean;
		
	}
	
	public CtxAttribute fromCtxAttributeBean(CtxAttributeBean attrBean) throws DatatypeConfigurationException, MalformedCtxIdentifierException {
		
		CtxAttribute attribute = new CtxAttribute(
				(CtxAttributeIdentifier) fromCtxIdentifierBean(attrBean.getId()));
		attribute.setLastModified(XMLGregorianCalendarToDate(attrBean.getLastModified()));
		// Handle value
		attribute.setBinaryValue(attrBean.getBinaryValue());
		attribute.setDoubleValue(attrBean.getDoubleValue());
		attribute.setIntegerValue(attrBean.getIntegerValue());
		attribute.setStringValue(attrBean.getStringValue());
		// Handle value meta-data
		attribute.setValueMetric(attrBean.getValueMetric());
		attribute.setValueType(fromCtxAttributeValueTypeBean(attrBean.getValueType()));
		// Handle other params
		attribute.setHistoryRecorded(attrBean.isHistoryRecorded());
		attribute.setSourceId(attrBean.getSourceId());
		// Handle QoC
		attribute.getQuality().setLastUpdated(
				XMLGregorianCalendarToDate(attrBean.getQuality().getLastUpdated()));
		attribute.getQuality().setOriginType(
				fromCtxOriginTypeBean(attrBean.getQuality().getOriginType()));
		attribute.getQuality().setPrecision(attrBean.getQuality().getPrecision());
		attribute.getQuality().setUpdateFrequency(attrBean.getQuality().getUpdateFrequency());
		
		return attribute;
		
	}
	
	public CtxAssociationBean fromCtxAssociation(CtxAssociation assoc) throws DatatypeConfigurationException {
		
		CtxAssociationBean bean=new CtxAssociationBean();
		//bean.setChildEntities(assoc.getChildEntities());
		bean.setLastModified(DateToXMLGregorianCalendar(assoc.getLastModified()));
		bean.setId(fromCtxIdentifier(assoc.getId()));
		
		List<CtxEntityIdentifierBean> childEntities = new ArrayList<CtxEntityIdentifierBean>();
		for (CtxEntityIdentifier child : assoc.getChildEntities()) {
			childEntities.add((CtxEntityIdentifierBean) fromCtxIdentifier(child));
		}
		bean.setChildEntities(childEntities);
		bean.setParentEntity(parentEntityBeanFromParentEntity(assoc.getParentEntity()));
		
		return bean;
		
	}
	
	public CtxAssociation fromCtxAssociationBean(CtxAssociationBean assocBean) throws DatatypeConfigurationException, MalformedCtxIdentifierException {
		
		CtxAssociation assoc = new CtxAssociation(
				(CtxAssociationIdentifier) fromCtxIdentifierBean(assocBean.getId()));
		assoc.setLastModified(XMLGregorianCalendarToDate(assocBean.getLastModified()));
		// Handle parent entity
		assoc.setParentEntity((CtxEntityIdentifier) fromCtxIdentifierBean(assocBean.getParentEntity()));
		// Handle child entities
		for (CtxEntityIdentifierBean childEntityIdBean : assocBean.getChildEntities())
			assoc.addChildEntity((CtxEntityIdentifier) fromCtxIdentifierBean(childEntityIdBean));
		
		return assoc;
	}
	
	public CtxIdentifierBean fromCtxModelObject(CtxModelObject object) {
		
		CtxIdentifierBean bean=new CtxEntityIdentifierBean();
		bean.setString(object.toString());
		return bean;
		
	}
	
	private CtxEntityIdentifierBean parentEntityBeanFromParentEntity(
			CtxEntityIdentifier parentEntity) {
		
		CtxEntityIdentifierBean bean = new CtxEntityIdentifierBean();
		bean.setString(parentEntity.toString());
		return bean;
	}

	public CtxQualityBean CtxQualityBeanFromCtxQuality(CtxQuality quality) throws DatatypeConfigurationException {
		
		CtxQualityBean bean=new CtxQualityBean();
		bean.setPrecision(quality.getPrecision());
		bean.setUpdateFrequency(quality.getUpdateFrequency());
		bean.setOriginType(fromCtxOriginType(quality.getOriginType()));
		bean.setLastUpdated(DateToXMLGregorianCalendar(quality.getLastUpdated()));
		
		return bean;
	}

	public CtxOriginTypeBean fromCtxOriginType(
			CtxOriginType originType) {
		
		return CtxOriginTypeBean.valueOf(originType.toString());	
	}
	
	public CtxOriginType fromCtxOriginTypeBean(
			CtxOriginTypeBean originTypeBean) {
		
		return CtxOriginType.valueOf(originTypeBean.toString());	
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
