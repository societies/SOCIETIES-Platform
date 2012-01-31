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

import java.util.List;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;



/**
 * This interface represents a callback interface for the IUserCtxBroker interface.
 * 
 * @author nikosk
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface IUserCtxBrokerCallback {

	/**
	 * Generic callback method
	 * 
	 * @param c_id
	 * @param reason
	 * @since 0.0.1
	 */
	public void cancel(CtxIdentifier c_id, String reason);

	/**
	 * The callback of IUserCtxBroker method
	 * createAssociation(String type, IUserCtxBrokerCallback callback)
	 * 
	 * @param ctxEntity
	 * @since 0.0.1
	 */
	public void ctxAssociationCreated(CtxAssociation ctxEntity);

	/**
	 * The callback of IUserCtxBroker method
	 * createAttribute(CtxEntityIdentifier scope, CtxAttributeValueType enumerate, String type, IUserCtxBrokerCallback callback)
	 * 
	 * @param ctxAttribute
	 * @since 0.0.1
	 */
	public void ctxAttributeCreated(CtxAttribute ctxAttribute);

	/**
	 * The callback of IUserCtxBroker method
	 * createAttribute(CtxEntityIdentifier scope, CtxAttributeValueType enumerate, String type, IUserCtxBrokerCallback callback)
	 *
	 * @param list
	 * @since 0.0.1
	 */
	public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list);

	/**
	 * The callback of IUserCtxBroker method
	 * createEntity(String type, IUserCtxBrokerCallback callback) 
	 * 
	 * @param ctxEntity
	 * @since 0.0.1
	 */
	public void ctxEntityCreated(CtxEntity ctxEntity);

	/**
	 * The callback of IUserCtxBroker method
	 * createEntity(String type, IUserCtxBrokerCallback callback) 
	 * 
	 * @param ctxEntity
	 * @since 0.0.1
	 */
	public void ctxIndividualCtxEntityCreated(CtxEntity ctxEntity);

	/**
	 * The callback of IUserCtxBroker method
	 * remove(CtxIdentifier identifier, IUserCtxBrokerCallback callback)
	 * 
	 * @param ctxModelObject
	 * @since 0.0.1
	 */
	public void ctxModelObjectRemoved(CtxModelObject ctxModelObject);

	/**
	 * The callback of IUserCtxBroker method
	 * retrieve(CtxIdentifier identifier, IUserCtxBrokerCallback callback)
	 * 
	 * @param ctxModelObject
	 * @since 0.0.1
	 */
	public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject);

	/**
	 * The callback of IUserCtxBroker method
	 * lookup(CtxModelType modelType, String type, IUserCtxBrokerCallback callback)
	 * 
	 * @param list
	 * @since 0.0.1
	 */
	public void ctxModelObjectsLookedup(List<CtxIdentifier> list);

	/**
	 * The callback of IUserCtxBroker method
	 * update(CtxModelObject identifier, IUserCtxBrokerCallback callback)
	 * 
	 * @param ctxModelObject
	 * @since 0.0.1
	 */
	public void ctxModelObjectUpdated(CtxModelObject ctxModelObject);

	/**
	 * The callback of IUserCtxBroker method
	 * retrieveFuture(CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback)
	 * and retrieveFuture(CtxAttributeIdentifier attrId, Date date, IUserCtxBrokerCallback callback)
	 * 
	 * @param futCtx
	 * @since 0.0.1
	 */
	public void futureCtxRetrieved(List <CtxAttribute> futCtx);

	/**
	 * The callback of IUserCtxBroker method
	 * retrieveFuture(CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback)
	 * and retrieveFuture(CtxAttributeIdentifier attrId, Date date, IUserCtxBrokerCallback callback)
	 * 
	 * @param futCtx
	 * @since 0.0.1
	 */
	public void futureCtxRetrieved(CtxAttribute futCtx);

	/**
	 * The callback of IUserCtxBroker method
	 * retrievePast(CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback)
	 * 
	 * @param hoc
	 * @since 0.0.1
	 */
	public void historyCtxRetrieved(CtxHistoryAttribute hoc);

	/**
	 * The callback of IUserCtxBroker method
	 * retrievePast(CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback)
	 * 
	 * @param hoc
	 * @since 0.0.1
	 */
	public void historyCtxRetrieved(List<CtxHistoryAttribute> hoc);

	/**
	 * Generic call back method
	 * 
	 * @param c_id
	 * @since 0.0.1
	 */
	public void ok(CtxIdentifier c_id);

	/**
	 * Generic call back method
	 * 
	 * @param list
	 * @since 0.0.1
	 */
	public void ok_list(List<CtxIdentifier> list);

	/**
	 * Generic call back method
	 * 
	 * @param list
	 * @since 0.0.1
	 */
	public void ok_values(List<Object> list);

	/**
	 * The callback of IUserCtxBroker method
	 * evaluateSimilarity(Serializable objectUnderComparison, List<Serializable> referenceObjects, IUserCtxBrokerCallback callback)
	 * 
	 * @param results
	 * @since 0.0.1
	 */
	public void similartyResults(List<Object> results);

	/**
	 * The callback of IUserCtxBroker method
	 * update(CtxModelObject identifier, IUserCtxBrokerCallback callback)
	 * 
	 * @param ctxModelObj
	 * @since 0.0.1
	 */
	public void updateReceived(CtxModelObject ctxModelObj);
	
	
	/**
	 * The callback of IUserCtxBroker method
	 * setCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
	 *		List<CtxAttributeIdentifier> listOfEscortingAttributeIds,IUserCtxBrokerCallback callback)
	 * 
	 * @param flag 
	 * @since 0.0.1
	 */
	public void ctxHistoryTuplesSet(Boolean flag);

	/**
	 * The callback of IUserCtxBroker method
	 * getCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
	 *		List<CtxAttributeIdentifier> listOfEscortingAttributeIds,IUserCtxBrokerCallback callback)
	 * 
	 * @param listOfEscortingAttributeIds 
	 * @since 0.0.1
	 */
	public void ctxHistoryTuplesRetrieved(List<CtxAttributeIdentifier> listOfEscortingAttributeIds);

	/**
	 * The callback of IUserCtxBroker method
	 * updateCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
	 *		List<CtxAttributeIdentifier> listOfEscortingAttributeIds,IUserCtxBrokerCallback callback)
	 *  
	 * @param listOfEscortingAttributeIds
	 * @since 0.0.1
	 */
	public void ctxHistoryTuplesUpdated (List<CtxAttributeIdentifier> listOfEscortingAttributeIds);

	/**
	 * The callback of IUserCtxBroker method
	 * removeCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
	 *		List<CtxAttributeIdentifier> listOfEscortingAttributeIds,IUserCtxBrokerCallback callback)
	 * 
	 * @param flag 
	 * @since 0.0.1
	 */
	public void ctxHistoryTuplesRemoved(Boolean flag);

}