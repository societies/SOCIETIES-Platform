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

package org.societies.api.internal.context.broker;

/**
 * This interface provides access to current, past and future context data. The
 * past context refers to the data stored in the context history database. The
 * future context information is provided on the fly based on context
 * prediction methods. The Context Broker also supports distributed context
 * queries; it is a gateway to context data and decides whether the local DB, a
 * remote DB or the Context Inference Management need to be contacted to
 * retrieve the requested context data.
 *
 */
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;

import java.util.concurrent.Future;


public interface ICtxBroker {
	
	/**
	 * Creates a CtxAssociation
	 * 
	 * @param type
	 * @throws CtxException 
	 */
	public Future<CtxAssociation> createAssociation(String type) throws CtxException;

	/**
	 * Creates a {@link CtxAttribute} of the specified type which is associated to
	 * the identified context entity (scope). 
	 * 
	 * @param scope
	 *            the identifier of the context entity to associate with the new
	 *            attribute
	 * @param type
	 *            the type of the context attribute to create
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<CtxAttribute> createAttribute(CtxEntityIdentifier scope, String type) throws CtxException;

	/**
	 * Creates a CtxEntity
	 * 
	 * @param type
	 * @throws CtxException 
	 */
	public Future<CtxEntity> createEntity(String type) throws CtxException;

	/**
	 * Disables context monitoring to Context Database
	 * 
	 * @param type
	 * @throws CtxException 
	 */
	public void disableCtxMonitoring(CtxAttributeValueType type) throws CtxException;

	/**
	 * Disables context recording to Context History Database
	 * @throws CtxException 
	 */
	public void disableCtxRecording() throws CtxException;

	/**
	 * Enables context monitoring to Context Database
	 * 
	 * @param type
	 * @throws CtxException 
	 */
	public void enableCtxMonitoring(CtxAttributeValueType type) throws CtxException;

	/**
	 * Enables context recording to Context History Database
	 * @throws CtxException 
	 * 
	 */
	public void enableCtxRecording() throws CtxException;

	/**
	 * There are several methods missing that would express the similarity of context
	 * values or objects in a quantifiable form (and not via a sorted list of
	 * most/least similar reference objects/values).
	 * 
	 * @param objectUnderComparison
	 * @param referenceObjects
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<List<Object>> evaluateSimilarity(Serializable objectUnderComparison, List<Serializable> referenceObjects) throws CtxException;

	/**
	 * Looks up for a list of CtxModelObjects defined by the CtxModelType (CtxEntity,
	 * CtxAttribute, CtxAssociation) of  the specified type.
	 * 
	 * @param modelType
	 * @param type
	 * @return ctxIdentifier 
	 * @throws CtxException 
	 */
	public Future<List<CtxIdentifier>> lookup(CtxModelType modelType, String type) throws CtxException;

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
	 * Registers the specified EventListener for value modification events of context
	 * attribute(s) with the supplied scope and type.
	 * 
	 * @param scope
	 * @param attrType
	 * @throws CtxException 
	 */
	public void registerForUpdates(CtxEntityIdentifier scope, String attrType) throws CtxException;

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param attrId
	 * @throws CtxException 
	 */
	public void registerForUpdates(CtxAttributeIdentifier attrId) throws CtxException;

	/**
	 * Removes the specified context model object.
	 * 
	 * @param identifier
	 * @throws CtxException 
	 */
	public Future<CtxModelObject> remove(CtxIdentifier identifier) throws CtxException;

	/**
	 * Removes context history records defined by type for the specified time period. 
	 * 
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @throws CtxException 
	 */
	public Future<Integer> removeHistory(String type, Date startDate, Date endDate) throws CtxException;

	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param identifier
	 * @throws CtxException 
	 */
	public Future<CtxModelObject> retrieve(CtxIdentifier identifier) throws CtxException;

	/**
	 * Predicts a future context attribute for the specified time.
	 * 
	 * @param attrId
	 * @param date
	 * @throws CtxException 
	 */
	public Future<List<CtxAttribute>> retrieveFuture(CtxAttributeIdentifier attrId, Date date) throws CtxException;

	/**
	 * Predicts the identified by the modification index future context attribute.
	 * 
	 * @param attrId
	 * @param modificationIndex
	 * @throws CtxException 
	 */
	public Future<List<CtxAttribute>> retrieveFuture(CtxAttributeIdentifier attrId, int modificationIndex) throws CtxException;

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified modificationIndex.
	 * 
	 * @param attrId
	 * @param modificationIndex
	 * @throws CtxException 
	 */
	public Future<List<CtxHistoryAttribute>> retrievePast(CtxAttributeIdentifier attrId, int modificationIndex) throws CtxException;

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified date and time information.
	 * 
	 * @param attrId
	 * @param startDate
	 * @param endDate
	 * @throws CtxException 
	 */
	public Future<List<CtxHistoryAttribute>> retrievePast(CtxAttributeIdentifier attrId, Date startDate, Date endDate) throws CtxException;

	
	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param attrId
	 * @throws CtxException 
	 */
	public void unregisterForUpdates(CtxAttributeIdentifier attrId) throws CtxException;

	/**
	 * Unregisters the specified EventListener for value modification events of
	 * context attribute(s) with the supplied scope and type.
	 * 
	 * @param scope
	 * @param attributeType
	 * @throws CtxException 
	 */
	public void unregisterForUpdates(CtxEntityIdentifier scope, String attributeType) throws CtxException;

	/**
	 * Updates a single context model object.
	 * 
	 * @param identifier
	 * @throws CtxException 
	 */
	public Future<CtxModelObject> update(CtxModelObject identifier) throws CtxException;

	/**
	 * Updates the {@link CtxAttribute} identified by the specified {@link CtxAttributeIdentifier}
	 * using the supplied value.
	 * <p>
	 * The following value types are supported:
	 * <dl>
	 * <dt><code>String</code></dt>
	 * <dd>Text value.</dd>
	 * <dt><code>Integer</code></dt>
	 * <dd>Integer value.</dd>
	 * <dt><code>Double</code></dt>
	 * <dd>Double-precision floating point numeric value.</dd>
	 * <dt><code>byte[]</code></dt>
	 * <dd>Binary value.</dd>
	 * </dl>
	 * @param attributeId
	 *            the identifier of the attribute to be updated
	 * @param value
	 *            the value to be set for the identified context attribute
	 * @throws CtxException 
	 * @throws NullPointerException if the specified context attribute identifier
	 *            is <code>null</code>
	 * @throws IllegalArgumentException if the type of the specified context
	 *            attribute value is not valid (supported value types are defined
	 *            in {@link org.societies.api.context.model.CtxAttributeValueType})
	 * @since 0.0.1
	 */
	public Future<CtxAttribute> updateAttribute(CtxAttributeIdentifier attributeId, Serializable value) throws CtxException;

	/**
	 * Updates the {@link CtxAttribute} identified by the specified {@link CtxAttributeIdentifier}
	 * using the supplied value. The value metric can also be specified.
	 * <p>
	 * The following value types are supported:
	 * <dl>
	 * <dt><code>String</code></dt>
	 * <dd>Text value.</dd>
	 * <dt><code>Integer</code></dt>
	 * <dd>Integer value.</dd>
	 * <dt><code>Double</code></dt>
	 * <dd>Double-precision floating point numeric value.</dd>
	 * <dt><code>byte[]</code></dt>
	 * <dd>Binary value.</dd>
	 * </dl>
	 * @param attributeId
	 *            the identifier of the attribute to be updated
	 * @param value
	 *            the value to be set for the identified context attribute
	 * @param valueMetric
	 *            the value metric to be set for the identified context attribute
	 * @throws CtxException 
	 * @throws NullPointerException if the specified context attribute identifier
	 *            is <code>null</code>
	 * @throws IllegalArgumentException if the type of the specified context
	 *            attribute value is not valid (supported value types are defined
	 *            in {@link org.societies.api.context.model.CtxAttributeValueType})
	 * @since 0.0.1
	 */
	public Future<CtxAttribute> updateAttribute(CtxAttributeIdentifier attributeId, Serializable value,
			String valueMetric) throws CtxException;

	/**
	 * This method allows to set a primary context attribute that will be stored in context History Database
	 * upon value update along with a list of other context attributes. 
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<Boolean> setCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;

	/**
	 * This method allows to get the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<List<CtxAttributeIdentifier>> getCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;

	/**
	 * This method allows to update the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 *  
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<List<CtxAttributeIdentifier>> updateCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;

	/**
	 * This method allows to remove the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<Boolean> removeCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException;
	
	
	/**
	 * This method retrieves the CSS that is assigned with the community administration role.
	 * @param community
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<IndividualCtxEntity> retrieveAdministratingCSS(CtxEntityIdentifier community) throws CtxException;

	/**
	 * Retrieves the context attribute(s) that act as a bond for the community of
	 * entities. The community is specified by the CtxEntityIdentifier.
	 * 
	 * @param community
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<Set<CtxBond>> retrieveBonds(CtxEntityIdentifier community) throws CtxException;

	/**
	 * Retrieves the sub-communities of the specified community Entity.
	 *  
	 * @param community
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(CtxEntityIdentifier community) throws CtxException;

	/**
	 * Retrieves a list of Individual Context Entities that are members of the specified community Entity 
	 * (individuals or subcommunities).
	 * 
	 * @param community
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(CtxEntityIdentifier community) throws CtxException;

	/**
	 * This applies for Community hierarchies. Retrieves the parent communities
	 * of the specified CtxEntity.
	 * 
	 * @param community
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(CtxEntityIdentifier community) throws CtxException;
	

}
