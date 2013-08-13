/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru≈æbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA√á√ÉO, SA (PTIN), IBM Corp., 
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
package org.societies.android.api.context;

import java.io.Serializable;
import java.util.List;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;
import org.societies.api.schema.identity.RequestorBean;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:pkosmidis@cn.ntua.gr">Pavlos Kosmides</a> (ICCS)
 * @since 1.1
 */
public interface ICtxClientHelper extends ICoreSocietiesServices{

	public CtxEntityBean createEntity(final RequestorBean requestor, 
			final String targetCss, final String type, final ICtxClientCallback callback) throws CtxException;
	
	public CtxAttributeBean createAttribute(final RequestorBean requestor, 
			final CtxEntityIdentifierBean scope, 
			final String type, 
			final ICtxClientCallback callback) throws CtxException;

	public CtxAssociationBean createAssociation(final RequestorBean requestor, 
			final String targetCss, final String type, final ICtxClientCallback callback) throws CtxException;
	
	public List<CtxIdentifierBean> lookup(final RequestorBean requestor,
			final String target, final CtxModelTypeBean modelType,
			final String type, final ICtxClientCallback callback) throws CtxException;
	
	 public List<CtxIdentifierBean> lookup(final RequestorBean requestor, 
			 final CtxEntityIdentifierBean entityId, final CtxModelTypeBean modelType,
			 final String type, final ICtxClientCallback callback) throws CtxException;

	public List<CtxEntityIdentifierBean> lookupEntities(final RequestorBean requestor, 
			final String targetCss, final String entityType, 
			final String attribType,final Serializable minAttribValue,
			final Serializable maxAttribValue, final ICtxClientCallback callback) throws CtxException;
	
	public CtxModelObjectBean remove(final RequestorBean requestor, 
			final CtxIdentifierBean identifier, final ICtxClientCallback callback) throws CtxException;

	public CtxModelObjectBean retrieve(final RequestorBean requestor, 
			final CtxIdentifierBean identifier, final ICtxClientCallback callback) throws CtxException;
	
	public CtxEntityIdentifierBean retrieveIndividualEntityId(final RequestorBean requestor, 
			final String cssId, final ICtxClientCallback callback) throws CtxException;
	
	public CtxEntityIdentifierBean retrieveCommunityEntityId(final RequestorBean requestor, 
			final String cisId, final ICtxClientCallback callback) throws CtxException;

	public CtxModelObjectBean update(final RequestorBean requestor,
			final CtxModelObjectBean object, final ICtxClientCallback callback) throws CtxException;
}
