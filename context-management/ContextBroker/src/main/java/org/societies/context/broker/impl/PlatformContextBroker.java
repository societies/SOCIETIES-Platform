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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.societies.context.broker.api.ICommunityCtxBrokerCallback;
import org.societies.context.broker.api.platform.ICommunityCtxBroker;
import org.societies.context.broker.api.platform.IUserCtxBroker;
import org.societies.context.broker.api.platform.IUserCtxBrokerCallback;
import org.societies.context.mock.spm.identity.EntityIdentifier;
import org.societies.context.model.api.CtxAssociation;
import org.societies.context.model.api.CtxAttribute;
import org.societies.context.model.api.CtxAttributeIdentifier;
import org.societies.context.model.api.CtxAttributeValueType;
import org.societies.context.model.api.CtxEntity;
import org.societies.context.model.api.CtxEntityIdentifier;
import org.societies.context.model.api.CtxHistoryAttribute;
import org.societies.context.model.api.CtxIdentifier;
import org.societies.context.model.api.CtxModelObject;
import org.societies.context.model.api.CtxModelType;
import org.societies.context.user.prediction.api.platform.PredictionMethod;

/*
 * Platform Context Broker Implementation
 * 
 */

public class PlatformContextBroker implements ICommunityCtxBroker, ICommunityCtxBrokerCallback, IUserCtxBroker, IUserCtxBrokerCallback {

	@Override
	public void retrieveAdministratingCSS(EntityIdentifier requester,
			CtxEntityIdentifier communityEntId,
			ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveBonds(EntityIdentifier requester,
			CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveChildCommunities(EntityIdentifier requester,
			CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveCommunityMembers(EntityIdentifier requester,
			CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveParentCommunities(EntityIdentifier requester,
			CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveAdministratingCSS(
			CtxEntityIdentifier community,
			org.societies.context.broker.api.platform.ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveBonds(
			CtxEntityIdentifier community,
			org.societies.context.broker.api.platform.ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveChildCommunities(
			CtxEntityIdentifier community,
			org.societies.context.broker.api.platform.ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveCommunityMembers(
			CtxEntityIdentifier community,
			org.societies.context.broker.api.platform.ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveParentCommunities(
			CtxEntityIdentifier community,
			org.societies.context.broker.api.platform.ICommunityCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void adminCSSRetrieved(CtxEntity admCssRetr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bondsRetrieved(CtxAttribute ctxAttribute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void childCommsRetrieved(List<CtxEntityIdentifier> childComms) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commMembersRetrieved(List<CtxEntityIdentifier> commMembs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parentCommsRetrieved(List<CtxEntityIdentifier> parentComms) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createAssociation(EntityIdentifier requester, String type,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createAttribute(EntityIdentifier requester,
			CtxEntityIdentifier scope, CtxAttributeValueType valueType,
			String type,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createEntity(EntityIdentifier requester, String type,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evaluateSimilarity(Serializable objectUnderComparison,
			List<Serializable> referenceObjects,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lookup(EntityIdentifier requester, CtxModelType modelType,
			String type,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lookupEntities(EntityIdentifier requester, String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerForUpdates(EntityIdentifier requester,
			CtxEntityIdentifier scope, String attrType,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerForUpdates(EntityIdentifier requester,
			CtxAttributeIdentifier attrId,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(EntityIdentifier requester, CtxIdentifier identifier,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieve(EntityIdentifier requester, CtxIdentifier identifier,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveFuture(EntityIdentifier requester,
			CtxAttributeIdentifier attrId, Date date,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveFuture(EntityIdentifier requester,
			CtxAttributeIdentifier attrId, int modificationIndex,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrievePast(EntityIdentifier requester,
			CtxAttributeIdentifier attrId, int modificationIndex,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrievePast(EntityIdentifier requester,
			CtxAttributeIdentifier attrId, Date startDate, Date endDate,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterForUpdates(EntityIdentifier requester,
			CtxAttributeIdentifier attrId,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterForUpdates(EntityIdentifier requester,
			CtxEntityIdentifier scope, String attributeType,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(EntityIdentifier requester, CtxModelObject object,
			org.societies.context.broker.api.IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel(CtxIdentifier c_id, String reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxAssociationCreated(CtxAssociation ctxEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxAttributeCreated(CtxAttribute ctxAttribute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxEntityCreated(CtxEntity ctxEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxIndividualCtxEntityCreated(CtxEntity ctxEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxModelObjectRemoved(CtxModelObject ctxModelObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxModelObjectsLookedup(List<CtxIdentifier> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void futureCtxRetrieved(List<CtxAttribute> futCtx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void futureCtxRetrieved(CtxAttribute futCtx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historyCtxRetrieved(CtxHistoryAttribute hoc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historyCtxRetrieved(List<CtxHistoryAttribute> hoc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ok(CtxIdentifier c_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ok_list(List<CtxIdentifier> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ok_values(List<Object> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void similartyResults(List<Object> results) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateReceived(CtxModelObject ctxModelObj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createAssociation(String type, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createAttribute(CtxEntityIdentifier scope,
			CtxAttributeValueType enumerate, String type,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createEntity(String type, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disableCtxMonitoring(CtxAttributeValueType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disableCtxRecording() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableCtxMonitoring(CtxAttributeValueType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableCtxRecording() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evaluateSimilarity(Serializable objectUnderComparison,
			List<Serializable> referenceObjects, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PredictionMethod getDefaultPredictionMethod(
			PredictionMethod predMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PredictionMethod getPredictionMethod(PredictionMethod predMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void lookup(CtxModelType modelType, String type,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lookupEntities(String entityType, String attribType,
			Serializable minAttribValue, Serializable maxAttribValue,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerForUpdates(CtxEntityIdentifier scope, String attrType,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerForUpdates(CtxAttributeIdentifier attrId,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(CtxIdentifier identifier, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int removeHistory(String type, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removePredictionMethod(PredictionMethod predMethod) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieve(CtxIdentifier identifier,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveFuture(CtxAttributeIdentifier attrId, Date date,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveFuture(CtxAttributeIdentifier attrId,
			int modificationIndex, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrievePast(CtxAttributeIdentifier attrId,
			int modificationIndex, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrievePast(CtxAttributeIdentifier attrId, Date startDate,
			Date endDate, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultPredictionMethod(PredictionMethod predMethod) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPredictionMethod(PredictionMethod predMethod,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterForUpdates(CtxAttributeIdentifier attrId,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterForUpdates(CtxEntityIdentifier scope,
			String attributeType, IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(CtxModelObject identifier,
			IUserCtxBrokerCallback callback) {
		// TODO Auto-generated method stub
		
	}

}
