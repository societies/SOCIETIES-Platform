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

package org.societies.personalisation.preference.api.UserPreferenceLearning;

import java.util.Date;

import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.IC45Consumer;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */

public interface IC45Learning
{
	/**
	 * This method starts the C4.5 learning process on context history from the date
	 * specified to present for all actions over all identities. (If Date is null, all available history
	 * is retrieved.) It returns output to a call-back method implemented by the
	 * requestor.
	 * 
	 * @param requestor    - an instance of the IC45Consumer to which the output
	 * should be returned
	 * @param startDate    - defines the start date of history to use as input.
	 */
	public void runC45Learning(IC45Consumer requestor, Date startDate);
	
	/**
	 * This method starts the C4.5 learning process on context history from the date
	 * specified to present for the specified parameterName of an IAction from the specified 
	 * service over all identities. (If Date is null, all available history is retrieved.) 
	 * It returns output to a call-back method implemented by the requestor.
	 * 
	 * @param requestor    - an instance of the IC45Consumer to which the output
	 * should be returned
	 * @param date    - defines the start date of history to use as input.
	 * @param serviceId    - the ID of the service related to the actions upon which
	 * learning should run
	 * @param parameterName    - specifies the parameterName (of an IAction) to focus
	 * C4.5 learning on.
	 */
	public void runC45Learning(IC45Consumer requestor, Date date, ServiceResourceIdentifier serviceId, String parameterName);
	
	/**
	 * This method starts the C4.5 learning process on context history from the date
	 * specified to present for all actions for a specific IIdentity. (If Date is
	 * null, all available history is retrieved.) It returns output to a call-back
	 * method implemented by the requestor.
	 * 
	 * @param requestor - an instance of the IC45Consumer to which the output should be returned
	 * @param date - defines the start date of history to use as input.
	 * @param historyOwner - the IIdentity under which learning should run
	 */
	public void runC45Learning(IC45Consumer requestor, Date date, IIdentity historyOwner);
	
	/**
	 * This method starts the C4.5 learning process on context history from the date
	 * specified to present for the specified parameterName of an IAction from the specified
	 * service over the specified IIdentity.(If Date is null, all available history is retrieved.) 
	 * It returns output to a call-back method implemented by the requestor.
	 * 
	 * @param requestor
	 * @param historyOwner
	 * @param date
	 * @param serviceId
	 * @param parameterName
	 */
	public void runC45Learning(IC45Consumer requestor, Date date, IIdentity historyOwner,
    		ServiceResourceIdentifier serviceId, String parameterName);

}