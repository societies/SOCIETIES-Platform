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

import org.societies.context.broker.api.ICommunityCtxBroker;
import org.societies.context.broker.api.ICommunityCtxBrokerCallback;
import org.societies.context.broker.api.IUserCtxBroker;
import org.societies.context.broker.api.IUserCtxBrokerCallback;
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
import org.societies.context.model.api.IndividualCtxEntity;



/*
 * 3p Context Broker Implementation
 * 
 */

public class ContextBroker implements ICommunityCtxBroker, ICommunityCtxBrokerCallback, IUserCtxBroker, IUserCtxBrokerCallback {

	
	//ICommunityCtxBroker methods
	/**
	 * 
	 * @param requester
	 * @param community
	 * @param callback
	 */
	public void retrieveAdministratingCSS(EntityIdentifier requester, CtxEntityIdentifier communityEntId, ICommunityCtxBrokerCallback callback) {
	}

	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param requester
	 * @param community
	 * @param callback
	 */
	public void retrieveBonds(EntityIdentifier requester, CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback) {
	}

	/**
	 * This applies for Community hierarchies. Retrieves the child communities
	 * (subcommunities of CtxEntities) of the specified parent CtxEntity
	 * 
	 * @param requester
	 * @param community
	 * @param callback
	 */
	public void retrieveChildCommunities(EntityIdentifier requester, CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback) {
	}

	/**
	 * Retrievies a list of Individual Context Entities that are members of the
	 * specified community Entity.
	 * 
	 * @param requester
	 * @param community
	 * @param callback
	 */
	public void retrieveCommunityMembers(EntityIdentifier requester, CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback) {
	}

	/**
	 * 
	 * @param requester
	 * @param community
	 * @param callback
	 */
	public void retrieveParentCommunities(EntityIdentifier requester, CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback) {
	}

	
	//ICommunityCtxBrokerCallback methods
	/**
	 * 
	 * @param admCssRetr
	 */
	public void adminCSSRetrieved(CtxEntity admCssRetr) {
	}

	/**
	 * 
	 * @param ctxAttribute
	 */
	public void bondsRetrieved(CtxAttribute ctxAttribute) {
	}

	/**
	 * 
	 * @param childComms
	 */
	public void childCommsRetrieved(List<CtxEntityIdentifier> childComms) {
	}

	/**
	 * 
	 * @param commMembs
	 */
	public void commMembersRetrieved(List <CtxEntityIdentifier> commMembs) {
	}

	/**
	 * 
	 * @param parentComms
	 */
	public void parentCommsRetrieved(List<CtxEntityIdentifier> parentComms) {
	}
	
	//IUserCtxBroker methods
	/**
	 * Creates a CtxAssociation
	 * 
	 * @param requester
	 * @param type
	 * @param callback
	 */
	public void createAssociation(EntityIdentifier requester, String type, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Creates a CtxAttribute
	 * 
	 * @param requester
	 * @param scope
	 * @param enum
	 * @param type
	 * @param callback
	 */
	public void createAttribute(EntityIdentifier requester, CtxEntityIdentifier scope, CtxAttributeValueType valueType, String type, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Creates a CtxEntity
	 * 
	 * @param requester
	 * @param type
	 * @param callback
	 */
	public void createEntity(EntityIdentifier requester, String type, IUserCtxBrokerCallback callback){
		
	}

	/**
	 * There are several methods missing that would express the similarity of context
	 * values or objects in a quantifiable form (and not via a sorted list of
	 * most/least similar reference objects/values).
	 * 
	 * @param objectUnderComparison
	 * @param referenceObjects
	 * @param callback
	 */
	public void evaluateSimilarity(Serializable objectUnderComparison, List<Serializable> referenceObjects, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Looks up for a list of CtxModelObjects defined by the CtxModelType (CtxEntity,
	 * CtxAttribute, CtxAssociation) of  the specified type.
	 * 
	 * @param requester
	 * @param modelType
	 * @param type
	 * @param callback
	 */
	public void lookup(EntityIdentifier requester, CtxModelType modelType, String type, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Looks up for a list of CtxEntities of  the specified type, containing the
	 * specified attributes
	 * 
	 * @param requester
	 * @param entityType
	 * @param attribType
	 * @param minAttribValue
	 * @param maxAttribValue
	 * @param callback
	 */
	public void lookupEntities(EntityIdentifier requester, String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Registers the specified EventListener for value modification events of context
	 * attribute(s) with the supplied scope and type.
	 * 
	 * @param requester
	 * @param scope
	 * @param attrType
	 * @param callback
	 */
	public void registerForUpdates(EntityIdentifier requester, CtxEntityIdentifier scope, String attrType, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param requester
	 * @param attrId
	 * @param callback
	 */
	public void registerForUpdates(EntityIdentifier requester, CtxAttributeIdentifier attrId, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Removes the specified context model object.
	 * 
	 * @param requester
	 * @param identifier
	 * @param callback
	 */
	public void remove(EntityIdentifier requester, CtxIdentifier identifier, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param requester
	 * @param identifier
	 * @param callback
	 */
	public void retrieve(EntityIdentifier requester, CtxIdentifier identifier, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Predicts a future context attribute for the specified time.
	 * 
	 * @param requester
	 * @param attrId
	 * @param date
	 * @param callback
	 */
	public void retrieveFuture(EntityIdentifier requester, CtxAttributeIdentifier attrId, Date date, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Predicts the identified by the modification index  future context attribute.
	 * 
	 * @param requester
	 * @param attrId
	 * @param modificationIndex
	 * @param callback
	 */
	public void retrieveFuture(EntityIdentifier requester, CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified modificationIndex.
	 * 
	 * @param requester
	 * @param attrId
	 * @param modificationIndex
	 * @param callback
	 */
	public void retrievePast(EntityIdentifier requester, CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified date and time information.
	 * 
	 * @param requester
	 * @param attrId
	 * @param startDate
	 * @param endDate
	 * @param callback
	 */
	public void retrievePast(EntityIdentifier requester, CtxAttributeIdentifier attrId, Date startDate, Date endDate, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param requester
	 * @param attrId
	 * @param callback
	 */
	public void unregisterForUpdates(EntityIdentifier requester, CtxAttributeIdentifier attrId, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Unregisters the specified EventListener for value modification events of
	 * context attribute(s) with the supplied scope and type.
	 * 
	 * @param requester
	 * @param scope
	 * @param attributeType
	 * @param callback
	 */
	public void unregisterForUpdates(EntityIdentifier requester, CtxEntityIdentifier scope, String attributeType, IUserCtxBrokerCallback callback) {
	}

	/**
	 * Updates a single context model object.
	 * 
	 * @param requester
	 * @param object
	 * @param callback
	 */
	public void update(EntityIdentifier requester, CtxModelObject object, IUserCtxBrokerCallback callback) {
	}

	
	//IUserCtxBrokerCallback
	/**
	 * 
	 * @param c_id
	 * @param reason
	 */
	public void cancel(CtxIdentifier c_id, String reason) {
	}

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxAssociationCreated(CtxAssociation ctxEntity) {
	}

	/**
	 * 
	 * @param ctxAttribute
	 */
	public void ctxAttributeCreated(CtxAttribute ctxAttribute) {
	}

	/**
	 * 
	 * @param list
	 */
	public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
	}

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxEntityCreated(CtxEntity ctxEntity) {
	}

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxIndividualCtxEntityCreated(IndividualCtxEntity ctxEntity) {
	}

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectRemoved(CtxModelObject ctxModelObject) {
	}

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject) {
	}

	/**
	 * 
	 * @param list
	 */
	public void ctxModelObjectsLookedup(List<CtxIdentifier> list) {
	}

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {
	}

	/**
	 * 
	 * @param futCtx
	 */
	public void futureCtxRetrieved(List <CtxAttribute> futCtx) {
	}

	/**
	 * 
	 * @param futCtx
	 */
	public void futureCtxRetrieved(CtxAttribute futCtx) {
	}

	/**
	 * 
	 * @param hoc
	 */
	public void historyCtxRetrieved(CtxHistoryAttribute hoc) {
	}

	/**
	 * 
	 * @param hoc
	 */
	public void historyCtxRetrieved(List<CtxHistoryAttribute> hoc) {
	}

	/**
	 * 
	 * @param c_id
	 */
	public void ok(CtxIdentifier c_id) {
	}

	/**
	 * 
	 * @param list
	 */
	public void ok_list(List<CtxIdentifier> list) {
	}

	/**
	 * 
	 * @param list
	 */
	public void ok_values(List<Object> list) {
	}

	/**
	 * needs further refinement
	 * 
	 * @param results
	 */
	public void similartyResults(List<Object> results) {
	}

	/**
	 * 
	 * @param ctxModelObj
	 */
	public void updateReceived(CtxModelObject ctxModelObj) {
	}
	
}

