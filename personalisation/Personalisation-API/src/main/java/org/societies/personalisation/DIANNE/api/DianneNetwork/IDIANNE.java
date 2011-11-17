/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.personalisation.DIANNE.api.DianneNetwork;

import org.societies.personalisation.common.api.model.IOutcome;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ContextAttribute;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */

public interface IDIANNE {
	
	/**
	 * This method will return the current value of the DIANNE preference
	 * @param ownerId	the DigitalIdentity of the owner of the preferences
	 * @param serviceId	the service identifier of the service requesting the outcome
	 * @param preferenceName	the name of the preference being requested
	 */
	public IOutcome getOutcome(EntityIdentifier ownerId, ServiceResourceIdentifier serviceId, String preferenceName);
	
	/**
	 * This method will return the current value of the DIANNE preference given the new context update
	 * @param ownerId	the DigitalIdentity of the owner of the preference
	 * @param serviceId	the service identifier of the service requesting the outcome
	 * @param preferenceName	the name of the preference being requested
	 * @param attribute		the context attribute update to implement in the DIANNE before retrieval
	 */
	public IOutcome getOutcome(EntityIdentifier ownerId, ServiceResourceIdentifier serviceId, String preferenceName, ContextAttribute attribute);
	
	/**
	 * This method will start DIANNE learning
	 */
	public void enableDIANNELearning();
	
	/**
	 * This method will stop DIANNE learning
	 */
	public void disableDIANNELearning();

}
