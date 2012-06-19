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
package org.societies.privacytrust.trust.api.engine;

import java.util.Set;

import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.model.ITrust;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;

/**
 * This interface provides methods to evaluate the trustworthiness of 
 * {@link ITrustedEntity} objects.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.3
 */
public interface ITrustEngine {

	/**
	 * Evaluates the trustworthiness of the specified {@link ITrustedCss} based
	 * on the supplied set of {@link ITrustEvidence}.
	 *  
	 * @param css
	 *            the {@link ITrustedCss} whose trust value to evaluate
	 * @param evidenceSet
	 *            the set of {@link ITrustEvidence} based on which to evaluate
	 *            the trust value
	 * @return the evaluated {@link ITrust} of the specified CSS 
	 * @throws TrustEngineException
	 *            if there is an error during trust evaluation
	 * @throw NullPointerException
	 *            if any of the specified parameters is <code>null</code>
	 */
	public ITrust evaluate(final ITrustedCss css, 
			final Set<ITrustEvidence> evidenceSet) throws TrustEngineException;
	
	/**
	 * Evaluates the trustworthiness of the specified {@link ITrustedCis} based
	 * on the supplied set of {@link ITrustEvidence}.
	 *  
	 * @param cis
	 *            the {@link ITrustedCis} whose trust value to evaluate
	 * @param evidenceSet
	 *            the set of {@link ITrustEvidence} based on which to evaluate
	 *            the trust value
	 * @return the evaluated {@link ITrust} of the specified CIS 
	 * @throws TrustEngineException
	 *            if there is an error during trust evaluation
	 * @throw NullPointerException
	 *            if any of the specified parameters is <code>null</code>
	 */
	public ITrust evaluate(final ITrustedCis cis, 
			final Set<ITrustEvidence> evidenceSet) throws TrustEngineException;
	
	/**
	 * Evaluates the trustworthiness of the specified {@link ITrustedService}
	 * based on the supplied set of {@link ITrustEvidence}.
	 *  
	 * @param service
	 *            the {@link ITrustedService} whose trust value to evaluate
	 * @param evidenceSet
	 *            the set of {@link ITrustEvidence} based on which to evaluate
	 *            the trust value
	 * @return the evaluated {@link ITrust} of the specified service 
	 * @throws TrustEngineException
	 *            if there is an error during trust evaluation
	 * @throw NullPointerException
	 *            if any of the specified parameters is <code>null</code>
	 */
	public ITrust evaluate(final ITrustedService service, 
			final Set<ITrustEvidence> evidenceSet) throws TrustEngineException;
}