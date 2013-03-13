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

package org.societies.api.personalisation.mgmt;


import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

import java.util.concurrent.Future;


/**
 * This is the interface of the Personalisation Manager. Services can use this interface
 * to request a preference or intent action from the Personalisation system.
 *
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 14:42:55
 */
@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface IPersonalisationManager {

    /**
     * Allows any service to request an context-based evaluated preference outcome.
     *
     * @param requestor      the DigitalIdentity of the service requesting the outcome
     * @param ownerID        the DigitalIdentity of the owner of the preferences (i.e. the
     *                       user of this service)
     * @param serviceID      the service identifier of the service requesting the
     *                       outcome
     * @param preferenceName the name of the preference requested
     * @return TODO
     * @return the outcome in the form of an IAction object
     */
    public Future<IAction> getIntentAction(Requestor requestor, IIdentity ownerID, ServiceResourceIdentifier serviceID, String preferenceName);

    /**
     * Allows any service to request an context-based evaluated preference outcome.
     *
     * @param requestor      the DigitalIdentity of the service requesting the outcome
     * @param ownerID        the DigitalIdentity of the owner of the preferences (i.e. the
     *                       user of this service)
     * @param serviceType    the type of the service requesting the outcome
     * @param serviceID      the service identifier of the service requesting the
     *                       outcome
     * @param preferenceName the name of the preference requested
     * @return the outcome in the form of an IAction object
     */
    public Future<IAction> getPreference(Requestor requestor, IIdentity ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName);

    /**
     * Allows any service to register itself as a personalisable service with this personalisation manager. The PM will then
     * maintain a list of preferences which can be personalised.
     *
     * @param service The service to be personalised
     * @see IActionConsumer.getPersonalisablePreferences()
     */
    public void registerPersonalisableService(IActionConsumer service);

}
