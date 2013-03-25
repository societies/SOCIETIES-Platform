/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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
package org.societies.api.personalisation.model;

import java.util.List;

import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;


/**
 * This interface must be implemented by 3rd party services that wish to be personalised
 * proactively by the SOCIETIES platform.
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 13:25:58
 */
@SocietiesExternalInterface(type=SocietiesInterfaceType.REQUIRED)
public interface IActionConsumer {

	/**
	 * 	
	 * This method is used by the User Agent Subsystem to locate the right instance
	 * of a IActionConsumer by filtering them based on their service identifier
	 * @return 		The service has to return its own service identifier
	 */
	public ServiceResourceIdentifier getServiceIdentifier();

	/**
	 * If the service has registered itself with a service type, it should return this
	 * using this method.
	 * @return		The service's type
	 */
	public String getServiceType();

	/**
	 * If the service fits under more than one service type category, it can return a
	 * list of service types using this method.
	 * @return 		A list of service types in String format
	 */
	public List<String> getServiceTypes();

	/**
	 * This method is used by the User Agent subsystem to send actions to be
	 * implemented by the services.
	 * @return		The service should return true if the action was implemented
	 * successfully or false if not.
	 * 
	 * @param userId    The Identity of the user currently using the service 	
	 * @param obj    The IAction object to be implemented
	 */
	public boolean setIAction(IIdentity userId, IAction obj);

    /**
     * Used by the Personalisation Manager, this method should return a list of all preferences which
     * the service implements and would like personalised
     */
    public List<PersonalisablePreferenceIdentifier> getPersonalisablePreferences();

}
