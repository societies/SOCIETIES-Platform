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
package org.societies.personalisation.SNDataExtractor.api.extractor;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.SNDataExtractor.api.extractor.mock.SNConnector;



public interface ISNDataExtractor  {
	
	/**
	 * This method will add a new USER connection between an user (new or already provisioned) 
	 * and the social network, by the specific Social Network Connector provided by T4.6
	 * @param ownerId the DigitalIdentity of the user (entity)
	 * @param 
	 */
	public SNConnector doConnectionToSN(IIdentity entityId, String connectorType);
	
	
	/**
	 * This method will provide the preferences from the SN of an entity User.
	 * @param ownerId the DigitalIdentity of the user (entity)
	 */
	public IOutcome getPreferences(IIdentity entityId);
	
	/**
	 * This method will provide the raw data from a specific social network 
	 * @param ownerId the DigitalIdentity of the user (entity)
	 * @parem connector the Social Network connector
     * @param serviceId	the service identifier of the service requesting the outcome
	 */
	public String getSocialPreferences(IIdentity entityId, SNConnector connector, ServiceResourceIdentifier serviceId);
	
	/**
	 * This method will remove the entity User from the graph and its connection with the social network
	 * @param ownerId the DigitalIdentity of the user (entity)
	 * @parem connector the Social Network connector
     * @param serviceId	the service identifier of the service requesting the outcome
	 */
	public boolean removeConnection(IIdentity entityId, SNConnector connector);
	
	
	/**
	 * This method will clear the User HISTORY to reset start the preference analisys from the beginning
	 * @param ownerId the DigitalIdentity of the user (entity)
	 * @parem connector the Social Network connector
	 */
	public void resetPreferences(IIdentity entityId, SNConnector connector);
	
		
}
