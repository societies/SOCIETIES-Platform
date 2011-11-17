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
package org.societies.personalisation.preference.api.CommunityPreferenceLearning;

import java.util.Date;
import java.util.List;

import org.societies.personalisation.common.api.model.IAction;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.UserPreferenceLearning.IC45Output;



/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 15:23:28
 */
public interface ICommunityPreferenceMerger {

	/**
	 * 
	 * @param date
	 * @param serviceId
	 * @param action
	 */
	public void explicitlyTriggerLearning(Date date, ServiceResourceIdentifier serviceId, IAction action);

	/**
	 * (non-Javadoc)
	 * @see org.personalsmartspace.lm.mining.c45.api.platform.
	 * IC45Consumer#handleC45Output(java.util.List)
	 * 
	 * @param list
	 */
	public void handleC45Output(List<IC45Output> list);

}