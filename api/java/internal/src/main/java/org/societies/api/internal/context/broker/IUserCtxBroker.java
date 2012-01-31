/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAΗΓO, SA (PTIN), IBM Corp., 
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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;

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
 * @since 0.0.0
 */
public interface IUserCtxBroker extends org.societies.api.context.broker.IUserCtxBroker {

	/**
	 * Creates a CtxAssociation
	 * 
	 * @param type
	 * @param callback
	 */
	public void createAssociation(String type, IUserCtxBrokerCallback callback);

	/**
	 * Creates a CtxAttribute
	 * 
	 * @param scope
	 * @param enum
	 * @param type
	 * @param callback
	 * @deprecated As of release 0.0.1, replaced by {@link #createAttribute(CtxEntityIdentifier, String, IUserCtxBrokerCallback)}
	 */
	@Deprecated
	public void createAttribute(CtxEntityIdentifier scope, CtxAttributeValueType enumerate, String type, IUserCtxBrokerCallback callback);
	
	/**
	 * Creates a {@link CtxAttribute} of the specified type which is associated to
	 * the identified context entity (scope). The generated attribute is returned
	 * through the {@link IUserCtxBrokerCallback#ctxAttributeCreated} method.
	 * 
	 * @param scope
	 *            the identifier of the context entity to associate with the new
	 *            attribute
	 * @param type
	 *            the type of the context attribute to create
	 * @param callback
	 *            the callback to return the created context attribute
	 * @since 0.0.1
	 */
	public void createAttribute(CtxEntityIdentifier scope, String type, IUserCtxBrokerCallback callback);

	/**
	 * Creates a CtxEntity
	 * 
	 * @param type
	 * @param callback
	 */
	public void createEntity(String type, IUserCtxBrokerCallback callback);

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
	public void  enableCtxMonitoring(CtxAttributeValueType type);

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
	 * @param callback
	 * @since 0.0.1
	 */
	public void evaluateSimilarity(Serializable objectUnderComparison, List<Serializable> referenceObjects, IUserCtxBrokerCallback callback);

	/**
	 * Looks up for a list of CtxModelObjects defined by the CtxModelType (CtxEntity,
	 * CtxAttribute, CtxAssociation) of  the specified type.
	 * 
	 * @param modelType
	 * @param type
	 * @param callback
	 */
	public void lookup(CtxModelType modelType, String type, IUserCtxBrokerCallback callback);

	/**
	 * Looks up for a list of CtxEntities of  the specified type, containing the
	 * specified attributes
	 * 
	 * @param entityType
	 * @param attribType
	 * @param minAttribValue
	 * @param maxAttribValue
	 * @param callback
	 */
	public void lookupEntities(String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue, IUserCtxBrokerCallback callback);

	/**
	 * Registers the specified EventListener for value modification events of context
	 * attribute(s) with the supplied scope and type.
	 * 
	 * @param scope
	 * @param attrType
	 * @param callback
	 */
	public void registerForUpdates(CtxEntityIdentifier scope, String attrType, IUserCtxBrokerCallback callback);

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param attrId
	 * @param callback
	 */
	public void registerForUpdates(CtxAttributeIdentifier attrId, IUserCtxBrokerCallback callback);

	/**
	 * Removes the specified context model object.
	 * 
	 * @param identifier
	 * @param callback
	 */
	public void remove(CtxIdentifier identifier, IUserCtxBrokerCallback callback);

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
	 * @param callback
	 */
	public void retrieve(CtxIdentifier identifier, IUserCtxBrokerCallback callback);

	/**
	 * Predicts a future context attribute for the specified time.
	 * 
	 * @param attrId
	 * @param date
	 * @param callback
	 */
	public void retrieveFuture(CtxAttributeIdentifier attrId, Date date, IUserCtxBrokerCallback callback);

	/**
	 * Predicts the identified by the modification index future context attribute.
	 * 
	 * @param attrId
	 * @param modificationIndex
	 * @param callback
	 */
	public void retrieveFuture(CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback);

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified modificationIndex.
	 * 
	 * @param attrId
	 * @param modificationIndex
	 * @param callback
	 */
	public void retrievePast(CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback);

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified date and time information.
	 * 
	 * @param attrId
	 * @param startDate
	 * @param endDate
	 * @param callback
	 */
	public void retrievePast(CtxAttributeIdentifier attrId, Date startDate, Date endDate, IUserCtxBrokerCallback callback);

	
	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param attrId
	 * @param callback
	 */
	public void unregisterForUpdates(CtxAttributeIdentifier attrId, IUserCtxBrokerCallback callback);

	/**
	 * Unregisters the specified EventListener for value modification events of
	 * context attribute(s) with the supplied scope and type.
	 * 
	 * @param scope
	 * @param attributeType
	 * @param callback
	 */
	public void unregisterForUpdates(CtxEntityIdentifier scope, String attributeType, IUserCtxBrokerCallback callback);

	/**
	 * Updates a single context model object.
	 * 
	 * @param identifier
	 * @param callback
	 */
	public void update(CtxModelObject identifier, IUserCtxBrokerCallback callback);

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
	 * Note that the updated <code>CtxAttribute</code> is returned through the
	 * {@link IUserCtxBrokerCallback#ctxModelObjectUpdated} method. If the
	 * update operation is not successfull then the callback method returns
	 * <code>null</code>.
	 * <p>
	 * This method is equivalent to calling {@link #updateAttribute(CtxAttributeIdentifier,
	 * Serializable, String, IUserCtxBrokerCallback)} specifying a <code>null</code>
	 * valueMetric parameter. 
	 * 
	 * @param attributeId
	 *            the identifier of the attribute to be updated
	 * @param value
	 *            the value to be set for the identified context attribute
	 * @param callback
	 *            the callback to return the updated context attribute
	 * @throws NullPointerException if the specified context attribute identifier
	 *            is <code>null</code>
	 * @throws IllegalArgumentException if the type of the specified context
	 *            attribute value is not valid (supported value types are defined
	 *            in {@link org.societies.api.context.model.CtxAttributeValueType})
	 * @since 0.0.1
	 */
	public void updateAttribute(CtxAttributeIdentifier attributeId, Serializable value,
			IUserCtxBrokerCallback callback);

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
	 * Note that the updated <code>CtxAttribute</code> is returned through the
	 * {@link IUserCtxBrokerCallback#ctxModelObjectUpdated} method. If the
	 * update operation is not successfull then the callback method returns
	 * <code>null</code>.
	 * 
	 * @param attributeId
	 *            the identifier of the attribute to be updated
	 * @param value
	 *            the value to be set for the identified context attribute
	 * @param valueMetric
	 *            the value metric to be set for the identified context attribute
	 * @param callback
	 *            the callback to return the updated context attribute
	 * @throws NullPointerException if the specified context attribute identifier
	 *            is <code>null</code>
	 * @throws IllegalArgumentException if the type of the specified context
	 *            attribute value is not valid (supported value types are defined
	 *            in {@link org.societies.api.context.model.CtxAttributeValueType})
	 * @since 0.0.1
	 */
	public void updateAttribute(CtxAttributeIdentifier attributeId, Serializable value,
			String valueMetric, IUserCtxBrokerCallback callback);

	/**
	 * This method allows to set a primary context attribute that will be stored in context History Database
	 * upon value update along with a list of other context attributes. 
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @param callback
	 * @since 0.0.1
	 */
	public void setCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds,IUserCtxBrokerCallback callback);

	/**
	 * This method allows to get the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @param callback
	 * @since 0.0.1
	 */
	public void getCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds,IUserCtxBrokerCallback callback);

	/**
	 * This method allows to update the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 *  
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @param callback
	 * @since 0.0.1
	 */
	public void updateCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds,IUserCtxBrokerCallback callback);

	/**
	 * This method allows to remove the list of the context attribute identifiers that consist the snapshot that is stored
	 * in context history database along with the a primary context attribute.  
	 * 
	 * @param primaryAttrIdentifier
	 * @param listOfEscortingAttributeIds
	 * @param callback
	 * @since 0.0.1
	 */
	public void removeCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds,
			IUserCtxBrokerCallback callback);
}