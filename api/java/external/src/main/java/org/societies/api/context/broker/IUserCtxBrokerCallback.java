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
package org.societies.api.context.broker;

import java.util.List;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.IndividualCtxEntity;

public interface IUserCtxBrokerCallback {
	
	/**
	 * 
	 * @param c_id
	 * @param reason
	 */
	public void cancel(CtxIdentifier c_id, String reason);

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxAssociationCreated(CtxAssociation ctxEntity);

	/**
	 * 
	 * @param ctxAttribute
	 */
	public void ctxAttributeCreated(CtxAttribute ctxAttribute);

	/**
	 * 
	 * @param list
	 */
	public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list);

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxEntityCreated(CtxEntity ctxEntity);

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxIndividualCtxEntityCreated(IndividualCtxEntity ctxEntity);

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectRemoved(CtxModelObject ctxModelObject);

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject);

	/**
	 * 
	 * @param list
	 */
	public void ctxModelObjectsLookedup(List<CtxIdentifier> list);

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectUpdated(CtxModelObject ctxModelObject);

	/**
	 * 
	 * @param futCtx
	 */
	public void futureCtxRetrieved(List <CtxAttribute> futCtx);

	/**
	 * 
	 * @param futCtx
	 */
	public void futureCtxRetrieved(CtxAttribute futCtx);

	/**
	 * 
	 * @param hoc
	 */
	public void historyCtxRetrieved(CtxHistoryAttribute hoc);

	/**
	 * 
	 * @param hoc
	 */
	public void historyCtxRetrieved(List<CtxHistoryAttribute> hoc);

	/**
	 * 
	 * @param c_id
	 */
	public void ok(CtxIdentifier c_id);

	/**
	 * 
	 * @param list
	 */
	public void ok_list(List<CtxIdentifier> list);

	/**
	 * 
	 * @param list
	 */
	public void ok_values(List<Object> list);

	/**
	 * needs further refinement
	 * 
	 * @param results
	 */
	public void similartyResults(List<Object> results);

	/**
	 * 
	 * @param ctxModelObj
	 */
	public void updateReceived(CtxModelObject ctxModelObj);
}