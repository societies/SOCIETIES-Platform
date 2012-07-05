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

import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;

/**
 * This interface is used for access control operations and decisions regarding
 * context data. More specifically, the ICtxAccessController is used to decide
 * whether an access to a context resource is to be allowed or denied, based on
 * the privacy policy currently in effect.
 * <p>
 * The {@link #checkPermission} method determines whether the access request
 * indicated by a specified {@link CtxPermission} should be granted or denied. 
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
public interface ICtxAccessController {

	/**
	 * Determines whether the access request indicated by the specified
	 * {@link CtxPermission} should be allowed or denied to the supplied 
	 * requestor, based on the privacy policy currently in effect on the given
	 * target CSS or CIS. This method quietly returns if the access request is
	 * permitted, or throws a CtxAccessControlException otherwise.
	 * 
	 * @param requestor
	 *            the entity requesting the specified permission
	 * @param target
	 *            the target CSS or CIS whose privacy policy to check for the
	 *            requested permission
	 * @param perm
	 *            the requested permission
	 * @throws CtxAccessControlException
	 *             if the specified permission is not permitted, based on the
	 *             current privacy policy
	 * @throws CtxAccessControllerException
	 *             if the specified permission cannot be checked against the
	 *             current privacy policy 
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>
	 */
	public void checkPermission(final Requestor requestor, 
			final IIdentity target, final CtxPermission perm)
			throws CtxAccessControlException, CtxAccessControllerException;
}