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

package org.societies.context.broker.api.platform;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.societies.context.model.api.CtxAttributeIdentifier;
import org.societies.context.model.api.CtxAttributeValueType;
import org.societies.context.model.api.CtxEntityIdentifier;
import org.societies.context.model.api.CtxIdentifier;
import org.societies.context.model.api.CtxModelObject;
import org.societies.context.model.api.CtxModelType;
import org.societies.context.user.prediction.api.platform.PredictionMethod;




/**
 * ICommunityCtxBroker interface allows to manage user context data. 
 * @author nikosk
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface IUserCtxBroker  extends org.societies.context.broker.api.IUserCtxBroker {

	/**
	 * Creates a CtxAssociation
	 * 
	 * @param type
	 * @param callback
	 * @since 0.0.1
	 */
	public void createAssociation(String type, IUserCtxBrokerCallback callback);

	/**
	 * Creates a CtxAttribute
	 * 
	 * @param scope
	 * @param enum
	 * @param type
	 * @param callback
	 * @since 0.0.1
	 */
	public void createAttribute(CtxEntityIdentifier scope, CtxAttributeValueType enumerate, String type, IUserCtxBrokerCallback callback);

	/**
	 * Creates a CtxEntity
	 * 
	 * @param type
	 * @param callback
	 * @since 0.0.1
	 */
	public void createEntity(String type, IUserCtxBrokerCallback callback);

	/**
	 * Disables context monitoring to Context Database
	 * 
	 * @param type
	 * @since 0.0.1
	 */
	public void disableCtxMonitoring(CtxAttributeValueType type);
	
	/**
	 * Disables context recording to Context History Database
	 * 
	 * @since 0.0.1
	 */
	public void disableCtxRecording();

	/**
	 * Enables context monitoring to Context Database
	 * 
	 * @param type
	 * @since 0.0.1
	 */
	public void  enableCtxMonitoring(CtxAttributeValueType type);
	
	/**
	 * Enables context recording to Context History Database
	 * 
	 * @since 0.0.1
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
	 * Returns the default prediction method used by context prediction component. 
	 * 
	 * @param predMethod
	 * @since 0.0.1
	 */
	public PredictionMethod getDefaultPredictionMethod(PredictionMethod predMethod);

	/**
	 * Returns the prediction method used by context prediction component. 
	 * 
	 * @param predMethod
	 * @since 0.0.1
	 */
	public PredictionMethod  getPredictionMethod(PredictionMethod predMethod);

	/**
	 * Looks up for a list of CtxModelObjects defined by the CtxModelType (CtxEntity,
	 * CtxAttribute, CtxAssociation) of  the specified type.
	 * 
	 * @param modelType
	 * @param type
	 * @param callback
	 * @since 0.0.1
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
	 * @since 0.0.1
	 */
	public void lookupEntities(String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue, IUserCtxBrokerCallback callback);

	/**
	 * Registers the specified EventListener for value modification events of context
	 * attribute(s) with the supplied scope and type.
	 * 
	 * @param scope
	 * @param attrType
	 * @param callback
	 * @since 0.0.1
	 */
	public void registerForUpdates(CtxEntityIdentifier scope, String attrType, IUserCtxBrokerCallback callback);

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param attrId
	 * @param callback
	 * @since 0.0.1
	 */
	public void registerForUpdates(CtxAttributeIdentifier attrId, IUserCtxBrokerCallback callback);

	/**
	 * Removes the specified context model object.
	 * 
	 * @param identifier
	 * @param callback
	 * @since 0.0.1
	 */
	public void remove(CtxIdentifier identifier, IUserCtxBrokerCallback callback);

	/**
	 * Removes context history records defined by type for the specified time period. 
	 * 
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @since 0.0.1
	 */
	public int removeHistory(String type, Date startDate, Date endDate);

	/**
	 * Removes the specified prediction method.
	 * 
	 * @param predMethod
	 * @since 0.0.1
	 */
	public void removePredictionMethod(PredictionMethod predMethod);

	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param identifier
	 * @param callback
	 * @since 0.0.1
	 */
	public void retrieve(CtxIdentifier identifier, IUserCtxBrokerCallback callback);

	/**
	 * Predicts a future context attribute for the specified time.
	 * 
	 * @param attrId
	 * @param date
	 * @param callback
	 * @since 0.0.1
	 */
	public void retrieveFuture(CtxAttributeIdentifier attrId, Date date, IUserCtxBrokerCallback callback);

	/**
	 * Predicts the identified by the modification index future context attribute.
	 * 
	 * @param attrId
	 * @param modificationIndex
	 * @param callback
	 * @since 0.0.1
	 */
	public void retrieveFuture(CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback);

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified modificationIndex.
	 * 
	 * @param attrId
	 * @param modificationIndex
	 * @param callback
	 * @since 0.0.1
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
	 * @since 0.0.1
	 */
	public void retrievePast(CtxAttributeIdentifier attrId, Date startDate, Date endDate, IUserCtxBrokerCallback callback);

	/**
	 * Sets the specified prediction method as default.  
	 * 
	 * @param predMethod
	 * @since 0.0.1
	 */
	public void setDefaultPredictionMethod(PredictionMethod predMethod);

	/**
	 * Sets the specified prediction method in order to be used by the context prediction component.
	 * 
	 * @param predMethod
	 * @param callback
	 * @since 0.0.1
	 */
	public void setPredictionMethod(PredictionMethod predMethod, IUserCtxBrokerCallback callback);

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param attrId
	 * @param callback
	 * @since 0.0.1
	 */
	public void unregisterForUpdates(CtxAttributeIdentifier attrId, IUserCtxBrokerCallback callback);

	/**
	 * Unregisters the specified EventListener for value modification events of
	 * context attribute(s) with the supplied scope and type.
	 * 
	 * @param scope
	 * @param attributeType
	 * @param callback
	 * @since 0.0.1
	 */
	public void unregisterForUpdates(CtxEntityIdentifier scope, String attributeType, IUserCtxBrokerCallback callback);

	/**
	 * Updates a single context model object.
	 * 
	 * @param identifier
	 * @param callback
	 * @since 0.0.1
	 */
	public void update(CtxModelObject identifier, IUserCtxBrokerCallback callback);

}