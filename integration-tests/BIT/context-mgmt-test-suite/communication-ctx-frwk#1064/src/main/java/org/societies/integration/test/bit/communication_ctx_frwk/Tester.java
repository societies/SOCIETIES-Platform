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

package org.societies.integration.test.bit.communication_ctx_frwk;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;


/**
 * Utility class that creates mock actions
 *
 * @author Nikos
 *
 */
public class Tester {

	private static Logger LOG = LoggerFactory.getLogger(Test1064.class);
	Requestor requestor = null;


	// run test in university's container
	private String targetEmma= "emma.ict-societies.eu";


	public Tester(){
		LOG.info("*** " + this.getClass() + " starting");
	}

	@Before
	public void setUp(){

	}


	@Test
	public void Test(){

		LOG.info("*** REMOTE CM TEST STARTING ***");
		LOG.info("*** " + this.getClass() + " instantiated");
		LOG.info("*** ctxBroker service :"+Test1064.getCtxBroker());

		try {								
			INetworkNode cssNodeId = Test1064.getCommManager().getIdManager().getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = Test1064.getCommManager().getIdManager().fromJid(cssOwnerStr);
			this.requestor = new Requestor(cssOwnerId);
			LOG.info("*** requestor = " + this.requestor);

			IIdentity cssIDEmma =  Test1064.getCommManager().getIdManager().fromJid(targetEmma);
			CtxEntity entityEmmaDevice = Test1064.getCtxBroker().createEntity(requestor, cssIDEmma, CtxEntityTypes.DEVICE).get();

			LOG.info("entity DEVICE created based on 3p broker "+entityEmmaDevice.getId());
			assertNotNull(entityEmmaDevice.getId());	
			assertEquals("device", entityEmmaDevice.getType().toLowerCase());
			
			CtxAttribute attrEmmaTemperature = Test1064.getCtxBroker().createAttribute(requestor, entityEmmaDevice.getId(), CtxAttributeTypes.TEMPERATURE).get();
			LOG.info("Attribute TEMPERATURE created in remote container "+attrEmmaTemperature.getId());
			assertNotNull(attrEmmaTemperature.getId());	

			
		//test binary
			MockBlobClass blob = new MockBlobClass(999);
			byte[] blobBytes;

				try {
					blobBytes = SerialisationHelper.serialise(blob);
					CtxAttribute ctxAttrBinary = Test1064.getCtxBroker().createAttribute(requestor, entityEmmaDevice.getId(), CtxAttributeTypes.ACTIVITIES).get();
					ctxAttrBinary.setBinaryValue(blobBytes);

					Test1064.getCtxBroker().update(requestor, ctxAttrBinary);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			

			// entity and attribute created

			// perform remote look up and retrieve
			// ENTITY
			List<CtxIdentifier> entityList = Test1064.getCtxBroker().lookup(requestor, cssIDEmma,  CtxModelType.ENTITY, CtxEntityTypes.DEVICE).get();
			LOG.info("remote entity list ids:" +entityList);
			assertNotNull(entityList);
			
			CtxEntity entityDevRetrieved = (CtxEntity) Test1064.getCtxBroker().retrieve(requestor, entityList.get(0)).get();
			LOG.info("remote entity id:" +entityDevRetrieved.getId());
			assertNotNull(entityDevRetrieved.getId());

			// ATTRIBUTE
			//List<CtxIdentifier> attrList = Test1064.getCtxBroker().lookup(requestor, entityDevRetrieved.getId(), CtxModelType.ATTRIBUTE ,CtxAttributeTypes.TEMPERATURE).get();
			List<CtxIdentifier> attrList = Test1064.getCtxBroker().lookup(requestor, cssIDEmma, CtxModelType.ATTRIBUTE ,CtxAttributeTypes.TEMPERATURE).get();
			LOG.info("remote attribute list ids:" +attrList);
			//assertEquals(1,attrList.size());

			CtxAttribute remoteAttrTemp = (CtxAttribute) Test1064.getCtxBroker().retrieve(requestor,attrList.get(0)).get();
			LOG.info("remote CtxAttribute id:" +remoteAttrTemp.getId());
			assertNotNull(remoteAttrTemp.getId());
			assertEquals("temperature", remoteAttrTemp.getType().toLowerCase());
			
			
			//List<CtxIdentifier> attrListAction = Test1064.getCtxBroker().lookup(requestor, entityDevRetrieved.getId(), CtxModelType.ATTRIBUTE ,CtxAttributeTypes.ACTION).get();
			List<CtxIdentifier> attrListAction = Test1064.getCtxBroker().lookup(requestor, cssIDEmma, CtxModelType.ATTRIBUTE ,CtxAttributeTypes.ACTIVITIES).get();
			CtxAttribute remoteAttrAction = (CtxAttribute) Test1064.getCtxBroker().retrieve(requestor,attrListAction.get(0)).get();
			LOG.info("remote CtxAttribute id:" +remoteAttrAction.getId());
			assertNotNull(remoteAttrAction.getId());
			assertEquals("activities", remoteAttrAction.getType().toLowerCase());
			LOG.info("remote CtxAttribute binary value:" +remoteAttrAction.getBinaryValue() +"  -- "+this.getClass().getClassLoader() );
			
			MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(remoteAttrAction.getBinaryValue(), this.getClass().getClassLoader());
			assertNotNull(retrievedBlob);
			

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}