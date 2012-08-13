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
package org.societies.api.internal.security.policynegotiator;

import java.net.URI;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * Interface for notifying Policy Negotiation on the provider side about any changes
 * in services that are available for sharing to others.
 *
 * @author Mitja Vardjan
 *
 */
public interface INegotiationProviderServiceMgmt {

	/**
	 * Tells Policy Negotiator that a new service is available for sharing to others.
	 * 
	 * @param serviceId ID of the service. The service instance need not exist at this point (TBC).
	 * 
	 * @param slaXml Options for Service Level Agreement (SLA) in XML format. Ignored at the moment.
	 * 
	 * @param clientJarServer Host and port of the server that hosts the JAR file for service client.
	 * If the service does not provide a client, this parameter should be null.
	 * 
	 * @param clientJarFilePath Path of the JAR file for service client.
	 * The path is relative on the server.
	 * If the service does not provide a client, this parameter should be null.
	 * Example: "Calculator.jar" if file path on the server is $VIRGO_HOME/Calculator.jar
	 * 
	 * @throws Negotia
	 */
	public void addService(ServiceResourceIdentifier serviceId, String slaXml, URI clientJarServer,
			String clientJarFilePath)
			throws NegotiationException;

	/**
	 * Tells Policy Negotiator that a new service is available for sharing to others.
	 * 
	 * @param serviceId ID of the service. The service instance need not exist at this point (TBC).
	 * 
	 * @param slaXml Options for Service Level Agreement (SLA) in XML format. Ignored at the moment.
	 * 
	 * @param clientJarServer Location of the JAR file for service client, if the service provides a client.
	 * If the service does not provide a client, this parameter should be null.
	 */
	//public void addService(ServiceResourceIdentifier serviceId, String slaXml, IIdentity clientJarServer);
	
	/**
	 * Tells Policy Negotiator that a service is not available for sharing to others anymore.
	 * 
	 * @param serviceId ID of the service. The service instance need not exist at this point (TBC).
	 */
	public void removeService(ServiceResourceIdentifier serviceId);
}
