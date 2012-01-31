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

import org.societies.api.mock.EntityIdentifier;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import java.util.concurrent.Future;


public interface ICtxBroker {
	
	/**
	 * Creates a CtxAssociation
	 * 
	 * @param type
	 */
	public Future<CtxAssociation> createAssociation(String type);

	/**
	 * Creates a {@link CtxAttribute} of the specified type which is associated to
	 * the identified context entity (scope). 
	 * 
	 * @param scope
	 *            the identifier of the context entity to associate with the new
	 *            attribute
	 * @param type
	 *            the type of the context attribute to create
	 * @since 0.0.1
	 */
	public Future<CtxAttribute> createAttribute(CtxEntityIdentifier scope, String type);

	/**
	 * Creates a CtxEntity
	 * 
	 * @param type
	 */
	public Future<CtxEntity> createEntity(String type);

	/**
	 * Disables context monitoring to Context Database
	 * 
	 * @param type
	 */
	public void disableCtxMonitoring(CtxAttributeValueType type);

	/**
	 * Disables context recording to Context History Database
	 */
	public void disableCtxRecording();

	/**
	 * Enables context monitoring to Context Database
	 * 
	 * @param type
	 */
	public void enableCtxMonitoring(CtxAttributeValueType type);

	/**
	 * Enables context recording to Context History Database
	 * 
	 */
	public void enableCtxRecording();

	/**
	 * There are several methods missing that would express the similarity of context
	 * values or objects in a quantifiable form (and not via a sorted list of
	 * most/least similar reference objects/values).
	 * 
	 * @param objectUnderComparison
	 * @param referenceObjects
	 * @since 0.0.1
	 */
	public Future<List<Object>> evaluateSimilarity(Serializable objectUnderComparison, List<Serializable> referenceObjects);

	/**
	 * Looks up for a list of CtxModelObjects defined by the CtxModelType (CtxEntity,
	 * CtxAttribute, CtxAssociation) of  the specified type.
	 * 
	 * @param modelType
	 * @param type
	 */
	public Future<List<CtxModelObject>> lookup(CtxModelType modelType, String type);

	/**
	 * Looks up for a list of CtxEntities of  the specified type, containing the
	 * specified attributes
	 * 
	 * @param entityType
	 * @param attribType
	 * @param minAttribValue
	 * @param maxAttribValue
	 */
	public Future<List<CtxEntities>> lookupEntities(String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue);

	/**
	 * Registers the specified EventListener for value modification events of context
	 * attribute(s) with the supplied scope and type.
	 * 
	 * @param scope
	 * @param attrType
	 */
	public Future<List<Object>> registerForUpdates(CtxEntityIdentifier scope, String attrType);

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param attrId
	 */
	public Future<List<Object>> registerForUpdates(CtxAttributeIdentifier attrId);

	/**
	 * Removes the specified context model object.
	 * 
	 * @param identifier
	 */
	public Future<CtxModelObject> remove(CtxIdentifier identifier);

	/**
	 * Removes context history records defined by type for the specified time period. 
	 * 
	 * @param type
	 * @param startDate
	 * @param endDate
	 */
	public int removeHistory(String type, Date startDate, Date endDate);

	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param identifier
	 */
	public Future<CtxModelObject> retrieve(CtxIdentifier identifier);

	/**
	 * Predicts a future context attribute for the specified time.
	 * 
	 * @param attrId
	 * @param date
	 */
	public Future<List<CtxAttribute>> retrieveFuture(CtxAttributeIdentifier attrId, Date date);

	/**
	 * Predicts the identified by the modification index future context attribute.
	 * 
	 * @param attrId
	 * @param modificationIndex
	 */
	public Future<List<CtxAttribute>> retrieveFuture(CtxAttributeIdentifier attrId, int modificationIndex);

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified modificationIndex.
	 * 
	 * @param attrId
	 * @param modificationIndex
	 */
	public Future<List<CtxHistoryAttribute>> retrievePast(CtxAttributeIdentifier attrId, int modificationIndex);

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified date and time information.
	 * 
	 * @param attrId
	 * @param startDate
	 * @param endDate
	 */
	public Future<List<CtxHistoryAttribute>> retrievePast(CtxAttributeIdentifier attrId, Date startDate, Date endDate);

	
	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param attrId
	 */
	public void unregisterForUpdates(CtxAttributeIdentifier attrId);

	/**
	 * Unregisters the specified EventListener for value modification events of
	 * context attribute(s) with the supplied scope and type.
	 * 
	 * @param scope
	 * @param attributeType
	 */
	public Future<List<Object>> unregisterForUpdates(CtxEntityIdentifier scope, String attributeType);

	/**
	 * Updates a single context model object.
	 * 
	 * @param identifier
	 */
	public Future<CtxModelObject> update(CtxModelObject identifier);

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
	 * @throws NullPointerException if the specified context attribute identifier
	 *            is <code>null</code>
	 * @throws IllegalArgumentException if the type of the specified context
	 *            attribute value is not valid (supported value types are defined
	 *            in {@link org.societies.api.context.model.CtxAttributeValueType})
	 * @since 0.0.1
	 */
	public Future<CtxAttribute> updateAttribute(CtxAttributeIdentifier attributeId, Serializable value);

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
	 * @throws NullPointerException if the specified context attribute identifier
	 *            is <code>null</code>
	 * @throws IllegalArgumentException if the type of the specified context
	 *            attribute value is not valid (supported value types are defined
	 *            in {@link org.societies.api.context.model.CtxAttributeValueType})
	 * @since 0.0.1
	 */
	public Future<CtxAttribute> updateAttribute(CtxAttributeIdentifier attributeId, Serializable value,
			String valueMetric);

	/**
	 * This method allows to set a primary context attribute that will be stored in context History Database
	 * upon value update along with a list of other context attributes. 
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @since 0.0.1
	 */
	public Future<Boolean> setCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds);

	/**
	 * This method allows to get the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @since 0.0.1
	 */
	public Future<List<CtxAttributeIdentifier>> getCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds);

	/**
	 * This method allows to update the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 *  
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @since 0.0.1
	 */
	public Future<List<CtxAttributeIdentifier>> updateCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds);

	/**
	 * This method allows to remove the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @since 0.0.1
	 */
	public Future<Boolean> removeCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds);
	
	
	/**
	 * This method retrieves the CSS that is assigned with the community administration role.
	 * @param community
	 * @since 0.0.1
	 */
	public Future<CtxEntity> retrieveAdministratingCSS(CtxEntityIdentifier community);

	/**
	 * Retrieves the context attribute(s) that act as a bond for the community of
	 * entities. The community is specified by the CtxEntityIdentifier.
	 * 
	 * @param community
	 * @since 0.0.1
	 */
	public Future<CtxAttribute> retrieveBonds(CtxEntityIdentifier community);

	/**
	 * This applies for Community hierarchies. Retrieves the child communities
	 * (sub-communities of CtxEntities) of the specified parent CtxEntity.
	 * 
	 * @param community
	 * @since 0.0.1
	 */
	public Future<List<CtxEntityIdentifier>> retrieveChildCommunities(CtxEntityIdentifier community);

	/**
	 * Retrieves a list of Individual Context Entities that are members of the
	 * specified community Entity.
	 * 
	 * @param community
	 * @since 0.0.1
	 */
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(CtxEntityIdentifier community);

	/**
	 * This applies for Community hierarchies. Retrieves the parent communities
	 * of the specified CtxEntity.
	 * 
	 * @param community
	 * @since 0.0.1
	 */
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(CtxEntityIdentifier community);
	

}
