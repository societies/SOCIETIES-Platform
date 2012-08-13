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
package org.societies.api.context.source;

import java.io.Serializable;
import java.util.concurrent.Future;

import org.societies.api.context.model.CtxEntity;
import org.societies.api.identity.INetworkNode;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface ICtxSourceMgr {

	/**
	 * Registers a context source with the context source manager. This is necessary
	 * in order to be able to send updates, i.e. new context information.
	 * The return type is a String serving as identifier of this context source 
	 * which is valid until the unregistration. 
	 * 
	 * @param name self-chosen name of the context source
	 * @param contextType type of the context information which shall be provided by the updates
	 * @return identifier to be used in the sendUpdate method
	 */
	public Future<String> register(String name, String contextType);

	/**
	 * Registers a context source with the context source manager. This is necessary
	 * in order to be able to send updates, i.e. new context information.
	 * The return type is a String serving as identifier of this context source 
	 * which is valid until the unregistration. Other than the method above, it can specify
	 * another CtxEntity as data owner. Otherwise the CSS owner would be assumed to be the
	 * data owner.
	 * 
	 * @param contextOwner owner of the context which shall be provided
	 * @param name self-chosen name of the context source
	 * @param contextType type of the context information which shall be provided by the updates
	 * @return identifier to be used in the sendUpdate method
	 */
	public Future<String> register(INetworkNode contextOwner, String name, String contextType);

	/**
	 * Sends modified context information to the CSM which stores it in the context data base.
	 * This is only possible after a registration.
	 * The data thereby can be specified as belonging to a different user than the node-owner.
	 * 
	 * @param identifier retrieved in the register call
	 * @param data actual payload, the context update
	 * @param owner the entity data is referring to 
	 * @return true iff update was received and stored successfully
	 */
	public Future<Boolean> sendUpdate(String identifier, Serializable data, CtxEntity owner);

	/**
	 * Sends modified context information to the CSM which stores it in the context data base.
	 * This is only possible after a registration.
	 * The data thereby can be specified as belonging to a different user than the node-owner.
	 * In addition the three Quality of Context (QoC) properties OriginType, Precision and UpdateFrequency
	 * can be set.
	 * 
	 * @param identifier retrieved in the register call
	 * @param data actual payload, the context update
	 * @param owner the entity data is referring to
	 * @param inferred boolean describing the QoC attribute "inferred", hence if the data attribute was inferred or not @see CtxQuality#getOriginType()
	 * @param precision the precision QoC attribute  @see CtxQuality#getPrecision()
	 * @param frequency frequency of data in Hz @see CtxQuality#getUpdateFrequency()
	 * @return true iff update was received and stored successfully
	 */ 
	public Future<Boolean> sendUpdate(String identifier, Serializable data, CtxEntity owner,
            boolean inferred, double precision, double frequency);

	/**
	 * Sends modified context information to the CSM which stores it in the context data base.
	 * This is only possible after a registration.
	 * 
	 * @param identifier retrieved in the register call
	 * @param data actual payload, the context update
	 * @return true iff update was received and stored successfully
	 */
	public Future<Boolean> sendUpdate(String identifier, Serializable data);

	/**
	 * Counterpart of the register method. unregisters a context soruce.
	 * Afterwards, no updates can be sent any longer.
	 * 
	 * @param identifier
	 * @return true iff unregistration was successful
	 */
	public Future<Boolean> unregister(String identifier);
}
