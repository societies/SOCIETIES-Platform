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

package org.societies.personalisation.management.impl;

import java.util.List;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;

public class ContextEventCallback implements IUserCtxBrokerCallback{

	private final PersonalisationManager pm;

	public ContextEventCallback(PersonalisationManager pm){
		this.pm = pm;
		
	}

	@Override
	public void cancel(CtxIdentifier arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxAssociationCreated(CtxAssociation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxAttributeCreated(CtxAttribute arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxEntitiesLookedup(List<CtxEntityIdentifier> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxEntityCreated(CtxEntity arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxIndividualCtxEntityCreated(CtxEntity arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxModelObjectRemoved(CtxModelObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxModelObjectRetrieved(CtxModelObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxModelObjectUpdated(CtxModelObject attribute) {
		this.pm.updateReceived(attribute);
		
	}

	@Override
	public void ctxModelObjectsLookedup(List<CtxIdentifier> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void futureCtxRetrieved(List<CtxAttribute> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void futureCtxRetrieved(CtxAttribute arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historyCtxRetrieved(CtxHistoryAttribute arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historyCtxRetrieved(List<CtxHistoryAttribute> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ok(CtxIdentifier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ok_list(List<CtxIdentifier> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ok_values(List<Object> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void similartyResults(List<Object> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateReceived(CtxModelObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxHistoryTuplesRemoved(Boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxHistoryTuplesRetrieved(List<CtxAttributeIdentifier> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxHistoryTuplesSet(Boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxHistoryTuplesUpdated(List<CtxAttributeIdentifier> arg0) {
		// TODO Auto-generated method stub
		
	}
}
