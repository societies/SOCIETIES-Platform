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
package org.societies.integration.test.bit.assessment;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * Describe your class here...
 *
 * @author mitjav
 *
 */
public class CtxBrokerInternalHelper {
	
	private static Logger LOG = LoggerFactory.getLogger(CtxBrokerInternalHelper.class);

	private ICtxBroker ctxBrokerInternal;
	
	private CtxEntityIdentifier ctxEntityIdentifier;
	private CtxIdentifier ctxAttributeStringIdentifier;


	public CtxBrokerInternalHelper(ICtxBroker ctxBrokerInternal) {
		this.ctxBrokerInternal = ctxBrokerInternal;
	}
	
	public void retrieveCssOperator() {
		
		LOG.info("*** retrieveCssOperator");
		
		try {
			IndividualCtxEntity operator = ctxBrokerInternal.retrieveCssOperator().get();
			LOG.info("*** operator context entity id: " + operator.getId());
		} catch (Exception e) {
			
			LOG.error("*** CM sucks: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * At this point a CtxEntity of type "Device" is created with an attribute
	 * of type "DeviceID" with a string value "device1234".
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void createContext() throws CtxException, InterruptedException, ExecutionException {

		LOG.info("*** createContext");

		//create ctxEntity of type "Device"
		Future<CtxEntity> futureEnt;
		futureEnt = ctxBrokerInternal.createEntity("Device");
		CtxEntity ctxEntity = (CtxEntity) futureEnt.get();

		//get the context identifier of the created entity (to be used at the next step)
		this.ctxEntityIdentifier = ctxEntity.getId();

		//create ctxAttribute with a String value that it is assigned to the previously created ctxEntity
		Future<CtxAttribute> futureCtxAttrString = ctxBrokerInternal.createAttribute(this.ctxEntityIdentifier, "DeviceID");
		// get the object of the created CtxAttribute
		CtxAttribute ctxAttributeString = (CtxAttribute) futureCtxAttrString.get();

		// by setting this flag to true the CtxAttribute values will be stored to Context History Database upon update
		ctxAttributeString.setHistoryRecorded(true);

		// set a string value to CtxAttribute
		ctxAttributeString.setStringValue("device1234");

		// with this update the attribute is stored in Context DB
		Future<CtxModelObject> futureAttrUpdated = ctxBrokerInternal.update(ctxAttributeString);

		// get the updated CtxAttribute object and identifier (to be used later for retrieval purposes)
		ctxAttributeString = (CtxAttribute) futureAttrUpdated.get();
		this.ctxAttributeStringIdentifier = ctxAttributeString.getId();
	}
	
	/**
	 * This method demonstrates how to retrieve context data from the context database
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void retrieveContext() throws CtxException, InterruptedException, ExecutionException {

		LOG.info("*** retrieveContext");

		// if the CtxEntityID or CtxAttributeID is known the retrieval is performed by using the ctxBroker.retrieve(CtxIdentifier) method

		// retrieve ctxEntity
		// This retrieval is performed based on the known CtxEntity identifier
		// Retrieve is also possible to be performed based on the type of the CtxEntity. This will be demonstrated in a later example.
		Future<CtxModelObject> ctxEntityRetrievedFuture = ctxBrokerInternal.retrieve(this.ctxEntityIdentifier);
		CtxEntity retrievedCtxEntity = (CtxEntity) ctxEntityRetrievedFuture.get();

		LOG.info("Retrieved ctxEntity id " +retrievedCtxEntity.getId()+ " of type: "+retrievedCtxEntity.getType());

		// retrieve the CtxAttribute contained in the CtxEntity with the string value
		// again the retrieval is based on an known identifier, it is possible to retrieve it based on type.This will be demonstrated in a later example.
		Future<CtxModelObject> ctxAttributeRetrievedStringFuture = ctxBrokerInternal.retrieve(this.ctxAttributeStringIdentifier);
		CtxAttribute retrievedCtxAttribute = (CtxAttribute) ctxAttributeRetrievedStringFuture.get();
		LOG.info("Retrieved ctxAttribute id " +retrievedCtxAttribute.getId()+ " and value: "+retrievedCtxAttribute.getStringValue());
	}
}
