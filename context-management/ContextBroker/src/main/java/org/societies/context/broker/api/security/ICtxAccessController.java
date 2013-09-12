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
package org.societies.context.broker.api.security;

import java.util.List;

import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.identity.Requestor;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;

/**
 * This interface is used for access control operations and decisions regarding
 * context data. More specifically, the ICtxAccessController is used to decide
 * whether an access to a context resource is to be allowed or denied, based on
 * the privacy preferences/policy currently in effect. 
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
public interface ICtxAccessController {

	/**
	 * Determines whether the specified access request to the identified 
	 * context model object should be allowed or denied for the supplied
	 * requestor. The type of the access request is indicated by the specified
	 * {@link ActionConstants}. Note that this method quietly returns if the
	 * access request is permitted, or throws a {@link CtxAccessControlException}
	 * otherwise.
	 * 
	 * @param requestor
	 *            the entity requesting access to the specified context model
	 *            object.
	 * @param ctxId
	 *            the context model object whose access is being requested.
	 * @param actionConst
	 *            the type of the access request, e.g. {@link ActionConstants#READ}.
	 * @throws CtxAccessControlException
	 *             if the specified access request is not permitted.
	 * @throws CtxAccessControllerException
	 *             if a permission for the specified access request cannot be
	 *             determined.
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>.
	 * @since 2.0
	 */
	public void checkPermission(final Requestor requestor, 
			final CtxIdentifier ctxId, final ActionConstants actionConst)
			throws CtxAccessControlException, CtxAccessControllerException;
	
	/**
	 * Determines whether the specified access request to the identified 
	 * context model object(s) should be allowed or denied for the supplied
	 * requestor. The type of the access request is indicated by the specified
	 * {@link ActionConstants}. The method returns a list of the context 
	 * identifiers for which the specified access request is allowed. Note that
	 * if the supplied requestor is denied access to all of the identified
	 * context model objects, the method throws a {@link CtxAccessControlException}.
	 * 
	 * @param requestor
	 *            the entity requesting access to the specified context model
	 *            object.
	 * @param ctxIdList
	 *            the list of context model objects whose access is being 
	 *            requested.
	 * @param actionConst
	 *            the type of the access request, e.g. {@link ActionConstants#READ}.
	 * @return a list of the context identifiers for which the specified access
	 *             request is allowed.
	 * @throws CtxAccessControlException
	 *             if the specified access request is not permitted.
	 * @throws CtxAccessControllerException
	 *             if a permission for the specified access request cannot be
	 *             determined.
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>.
	 * @since 2.0
	 */
	public List<CtxIdentifier> checkPermission(final Requestor requestor, 
			final List<? extends CtxIdentifier> ctxIdList, 
			final ActionConstants actionConst) throws 
			CtxAccessControlException, CtxAccessControllerException;
	
	/**
	 * Obfuscates the specified context model object. The requestor on whose
	 * behalf to perform the obfuscation must also be specified. The method
	 * returns the obfuscated context model object.
	 * 
	 * @param requestor
	 *            the requestor on whose behalf to perform the obfuscation.
	 * @param ctxModelObject
	 *            the context model object to obfuscate.
	 * @return the obfuscated context model object.
	 * @throws CtxAccessControllerException
	 *             if there is a problem performing the obfuscation.
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>.
	 * @since 2.0
	 */
	public CtxModelObject obfuscate(final Requestor requestor, 
			final CtxModelObject ctxModelObject) 
					throws CtxAccessControllerException;
	
	/**
	 * Obfuscates the specified list of context model objects. The requestor on
	 * whose behalf to perform the obfuscation must also be specified. The 
	 * method returns a list containing the obfuscated context model objects.
	 * 
	 * @param requestor
	 *            the requestor on whose behalf to perform the obfuscation.
	 * @param ctxModelObjectList
	 *            the list of context model objects to obfuscate.
	 * @return a list containing the obfuscated context model objects.
	 * @throws CtxAccessControllerException
	 *             if there is a problem performing the obfuscation.
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>.
	 * @since 2.0
	 */
	public List<CtxModelObject> obfuscate(final Requestor requestor, 
			final List<CtxModelObject> ctxModelObjectList) 
					throws CtxAccessControllerException;
}