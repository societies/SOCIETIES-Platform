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
package org.societies.android.api.external.context.broker;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.societies.android.api.external.context.CtxException;
import org.societies.android.api.external.context.event.CtxChangeEventListener;
import org.societies.android.api.external.context.model.CtxAssociation;
import org.societies.android.api.external.context.model.CtxAttribute;
import org.societies.android.api.external.context.model.CtxAttributeIdentifier;
import org.societies.android.api.external.context.model.CtxAttributeValueType;
import org.societies.android.api.external.context.model.CtxBond;
import org.societies.android.api.external.context.model.CtxEntity;
import org.societies.android.api.external.context.model.CtxEntityIdentifier;
import org.societies.android.api.external.context.model.CtxIdentifier;
import org.societies.android.api.external.context.model.CtxModelObject;
import org.societies.android.api.external.context.model.CtxModelType;
import org.societies.android.api.external.context.model.IndividualCtxEntity;

/**
 * This interface provides access to current, past and future context data. The
 * past context refers to the data stored in the context history database. The
 * future context information is provided on the fly based on context
 * prediction methods. The Context Broker also supports distributed context
 * queries; it is a gateway to context data and decides whether the local DB, a
 * remote DB or the Context Inference Management need to be contacted to
 * retrieve the requested context data.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.2
 */
public interface ICtxClientBroker {
 
	/**
	 * Creates a {@link CtxAssociation} with the specified type on the identified
	 * CSS.
	 * 
	 * @param type
	 *            the type of the context association to create
	 * @throws CtxException 
	 */
	public Future<CtxAssociation> createAssociation(String type) throws CtxException;
	
	/**
	 * Creates a {@link CtxAttribute} with the specified type which is associated to
	 * the identified context entity (scope).
	 * 
	 * @param scope
	 *            the identifier of the context entity to associate with the new
	 *            attribute
	 * @param type
	 *            the type of the context attribute to create
	 * @throws CtxException 
	 */
	public Future<CtxAttribute> createAttribute(CtxEntityIdentifier scope, String type) throws CtxException;
	
	/**
	 * Creates a {@link CtxEntity} with the specified type on the identified CSS.
	 * 
	 * @param type
	 *            the type of the context entity to create
	 * @throws CtxException 
	 */
	public Future<CtxEntity> createEntity(String type) throws CtxException;
	
	/**
	 * Disables Context Monitoring for the specified {@link CtxAttributeValueType}.
	 * 
	 * @param type
	 *           the type of the context attribute
	 * @throws CtxException 
	 */
	public void disableCtxMonitoring(CtxAttributeValueType type) throws CtxException;
	
	/**
	 * Enables CContext Monitoring for the specified {@link CtxAttributeValueType}.
	 * 
	 * @param type
	 *            the type of the context attribute
	 * @throws CtxException 
	 */	
	public void enableCtxMonitoring(CtxAttributeValueType type) throws CtxException;
	
	
	/**
	 * Looks up for a list of CtxEntities of  the specified type, containing the
	 * specified attributes
	 * 
	 * @param entityType
	 * @param attribType
	 * @param minAttribValue
	 * @param maxAttribValue
	 * @throws CtxException 
	 */
	public Future<List<CtxEntityIdentifier>> lookupEntities(String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue) throws CtxException;

	/**
	 * Looks up for a list of CtxModelObjects defined by the CtxModelType (CtxEntity,
	 * CtxAttribute, CtxAssociation) of  the specified type.
	 * 
	 * @param modelType
	 * @param type
	 * @throws CtxException 
	 */
	public Future<List<CtxIdentifier>> lookup(CtxModelType modelType, String type) throws CtxException;

	/**
	 * Registers the specified {@link CtxChangeEventListener} for changes
	 * related to the context model object referenced by the specified identifier.
	 * 
	 * @param listener
	 *            the listener to register for context changes 
	 * @param ctxId
	 *            the identifier of the context model object whose change
	 *            events to register for
	 * @throws CtxException if the registration process fails
	 * @throws NullPointerException if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void registerForChanges(final CtxChangeEventListener listener, 
     final CtxIdentifier ctxId) throws CtxException;
	
	/**
	 * Unregisters the specified {@link CtxChangeEventListener} from changes
	 * related to the context model object referenced by the specified identifier.
	 * 
	 * @param listener
	 *            the listener to unregister from context changes 
	 * @param ctxId
	 *            the identifier of the context model object whose change
	 *            events to unregister from
	 * @throws CtxException if the unregistration process fails
	 * @throws NullPointerException if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void unregisterFromChanges(final CtxChangeEventListener listener, 
     final CtxIdentifier ctxId) throws CtxException;   
	
	/**
	 * Registers the specified {@link CtxChangeEventListener} for changes
	 * related to the context attribute(s) with the supplied scope and type.
	 * 
	 * @param listener
	 *            the listener to register for context changes
	 * @param scope
	 *            the scope of the context attribute(s) whose change events to
	 *            register for 
	 * @param attrType
	 *            the type of the context attribute(s) whose change events to
	 *            register for
	 * @throws CtxException if the registration process fails
	 * @throws NullPointerException if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void registerForChanges(final CtxChangeEventListener listener,
     final CtxEntityIdentifier scope, String attrType) throws CtxException;
	
	/**
	 * Unregisters the specified {@link CtxChangeEventListener} from changes
	 * related to the context attribute(s) with the supplied scope and type.
	 * 
	 * @param listener
	 *            the listener to unregister from context changes
	 * @param scope
	 *            the scope of the context attribute(s) whose change events to
	 *            unregister from 
	 * @param attrType
	 *            the type of the context attribute(s) whose change events to
	 *            unregister from
	 * @throws CtxException if the unregistration process fails
	 * @throws NullPointerException if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void unregisterFromChanges(final CtxChangeEventListener listener,
     final CtxEntityIdentifier scope, String attrType) throws CtxException;
	
	/**
	 * Removes the specified context model object.
	 * 
	 * @param identifier
	 * @throws CtxException 
	 */
	public Future<CtxModelObject> remove(CtxIdentifier identifier) throws CtxException;
	
	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param identifier
	 * @throws CtxException 
	 */
	public Future<CtxModelObject> retrieve(CtxIdentifier identifier) throws CtxException;
	
	/**
	 * Updates a single context model object.
	 * 
	 * @param identifier
	 * @throws CtxException 
	 */
	public Future<CtxModelObject> update(CtxModelObject identifier) throws CtxException;
	
	/**
	 * Updates the specified attribute.
	 * 
	 * @param attributeId
	 * @param value
	 * @throws CtxException 
	 */
	public Future<CtxAttribute> updateAttribute(CtxAttributeIdentifier attributeId, Serializable value) throws CtxException;
	
	/**
	 * Updates the specified attribute.
	 * 
	 * @param attributeId
	 * @param value
	 * @param valueMetric
	 * @throws CtxException 
	 */
	public Future<CtxAttribute> updateAttribute(CtxAttributeIdentifier attributeId, Serializable value,
  String valueMetric) throws CtxException;

	//community
	
	/**
	 * 
	 * @param community
	 * @throws CtxException 
	 */
	public Future<IndividualCtxEntity> retrieveAdministratingCSS(CtxEntityIdentifier community) throws CtxException;
  
	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param community
	 * @throws CtxException 
	 */
	public Future<Set<CtxBond>> retrieveBonds(CtxEntityIdentifier community) throws CtxException;
  
	/**
	 * Retrieves the sub-communities of the specified community Entity.
	 * 
	 * @param community
	 * @throws CtxException 
	 */
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(CtxEntityIdentifier community) throws CtxException;
  
	/**
     * Retrieves a list of Individual Context Entities that are members of the specified community Entity 
	 * (individuals or subcommunities).
	 * 
	 * @param community
	 * @throws CtxException 
	 */
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(CtxEntityIdentifier community) throws CtxException;

  //history
	/**
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException
	 */
	public Future<Boolean> setHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
     List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;
 
	/**
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException
	 */
	public Future<List<CtxAttributeIdentifier>> getHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
     List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;
  
	/**
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException
	 */
	public Future<List<CtxAttributeIdentifier>> updateHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
     List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;

	/**
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException
	 */
	public Future<Boolean> removeHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
     List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;
  
	/**
	 * 
	 * @throws CtxException
	 */
	public void enableCtxRecording() throws CtxException;
   
	/**
	 * 
	 * @throws CtxException
	 */
	public void disableCtxRecording() throws CtxException;
}