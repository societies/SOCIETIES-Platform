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
package org.societies.api.identity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class RequestorUtilsTest {
	private static Logger LOG = LoggerFactory.getLogger(RequestorUtilsTest.class.getName());

	@Test
	public void testEqual() {
		String testTitle = "Equal";
		LOG.info(testTitle);
		RequestorBean requestorBean1 = null;
		RequestorBean requestorBean2 = null;
		Action notRequestorBean = null;
		// -- Null
		assertTrue("Same null requestorBean should be equal", RequestorUtils.equal(requestorBean1, requestorBean1));
		assertTrue("Null requestorBeans should be equal", RequestorUtils.equal(requestorBean1, requestorBean2));
		assertTrue("Null requestorBean and null object should be equal", RequestorUtils.equal(requestorBean1, notRequestorBean));

		// -- Empty
		requestorBean1 = new RequestorBean();
		requestorBean2 = new RequestorBean();
		notRequestorBean = new Action();
		assertTrue("Same empty requestorBean should be equal", RequestorUtils.equal(requestorBean1, requestorBean1));
		assertTrue("Empty requestorBeans should be equal", RequestorUtils.equal(requestorBean1, requestorBean2));
		assertTrue("Empty requestorBeans should be equal (inverse)", RequestorUtils.equal(requestorBean2, requestorBean1));
		assertFalse("Empty requestorBean and empty object should not be equal", RequestorUtils.equal(requestorBean1, notRequestorBean));

		// -- CSS
		requestorBean1 = RequestorUtils.create("emma.ict-societies.eu");
		assertTrue("Same requestorBean should be equal", RequestorUtils.equal(requestorBean1, requestorBean1));
		assertFalse("Different requestorBeans should be equal", RequestorUtils.equal(requestorBean1, requestorBean2));
		assertFalse("Different requestorBeans should be equal (inverse)", RequestorUtils.equal(requestorBean2, requestorBean1));
		requestorBean2 = RequestorUtils.create("university.ict-societies.eu");
		assertFalse("Different requestorBeans should be equal (2)", RequestorUtils.equal(requestorBean1, requestorBean2));
		assertFalse("Different requestorBeans should be equal (inverse) (2)", RequestorUtils.equal(requestorBean2, requestorBean1));
		requestorBean2 = RequestorUtils.create("emma.ict-societies.eu");
		assertTrue("Equal requestorBeans should be equal", RequestorUtils.equal(requestorBean1, requestorBean2));
		assertTrue("Equal requestorBeans should be equal (inverse)", RequestorUtils.equal(requestorBean2, requestorBean1));
		assertFalse("requestorBean and object should not be equal", RequestorUtils.equal(requestorBean1, notRequestorBean));

		// -- CIS
		requestorBean1 = RequestorUtils.create("emma.ict-societies.eu", "cis-test.ict-societies.local");
		assertTrue("Same requestorBean should be equal", RequestorUtils.equal(requestorBean1, requestorBean1));
		assertFalse("Different requestorBeans should not be equal", RequestorUtils.equal(requestorBean1, requestorBean2));
		assertFalse("Different requestorBeans should not be equal (inverse)", RequestorUtils.equal(requestorBean2, requestorBean1));
		requestorBean2 = RequestorUtils.create("university.ict-societies.eu");
		assertFalse("Different requestorBeans should not be equal (2)", RequestorUtils.equal(requestorBean1, requestorBean2));
		assertFalse("Different requestorBeans should not be equal (inverse) (2)", RequestorUtils.equal(requestorBean2, requestorBean1));
		requestorBean2 = RequestorUtils.create("university.ict-societies.eu", "cis-test.ict-societies.local");
		assertFalse("Different requestorBeans should not be equal (3)", RequestorUtils.equal(requestorBean1, requestorBean2));
		assertFalse("Different requestorBeans should not be equal (inverse) (3)", RequestorUtils.equal(requestorBean2, requestorBean1));
		requestorBean2 = RequestorUtils.create("emma.ict-societies.eu", "cis-test.ict-societies.local");
		assertTrue("Equal requestorBeans should be equal", RequestorUtils.equal(requestorBean1, requestorBean2));
		assertTrue("Equal requestorBeans should be equal (inverse)", RequestorUtils.equal(requestorBean2, requestorBean1));
		assertFalse("requestorBean and object should not be equal", RequestorUtils.equal(requestorBean1, notRequestorBean));

		// -- CIS
		RequestorCisBean requestorCis1 = new RequestorCisBean();
		requestorCis1.setRequestorId("emma.ict-societies.eu");
		requestorCis1.setCisRequestorId("cis-test.ict-societies.local");
		RequestorCisBean requestorCis2 = new RequestorCisBean();
		assertTrue("Same CIS requestorBean should be equal", RequestorUtils.equal(requestorCis1, requestorCis1));
		assertFalse("Different CIS requestorBeans should not be equal", RequestorUtils.equal(requestorCis1, requestorCis2));
		assertFalse("Different CIS requestorBeans should not be equal (inverse)", RequestorUtils.equal(requestorCis2, requestorCis1));
		requestorCis2.setRequestorId("university.ict-societies.eu");
		requestorCis2.setCisRequestorId("cis-test.ict-societies.local");
		assertFalse("Different CIS requestorBeans should not be equal (2)", RequestorUtils.equal(requestorCis1, requestorCis2));
		assertFalse("Different CIS requestorBeans should not be equal (inverse) (2)", RequestorUtils.equal(requestorCis2, requestorCis1));
		requestorCis2.setRequestorId("emma.ict-societies.eu");
		requestorCis2.setCisRequestorId("cis-test.ict-societies.local");
		assertTrue("Same requestorBeans should be equal", RequestorUtils.equal(requestorCis1, requestorCis2));
		assertTrue("Same requestorBeans should be equal (inverse)", RequestorUtils.equal(requestorCis2, requestorCis1));
	}
	
	@Test
	public void testContainable() {
		String testTitle = "requestor is containable?";
		LOG.info("[Test] "+testTitle);
		
		Map<RequestorBean, String> map = new Hashtable<RequestorBean, String>();
		RequestorBean requestor1 = RequestorUtils.create("university.ict-societies.local");
		RequestorBean requestor2 = RequestorUtils.create("emma.ict-societies.local");
		RequestorBean requestor3 = RequestorUtils.create("mario.ict-societies.local");
		map.put(requestor1, "requestor1");
		map.put(requestor2, "requestor2");
		
		assertTrue("Requestor1 should be contained in the map (1/2)", map.containsKey(requestor1));
		assertEquals("Requestor1 should be contained in the map (2/2)", map.get(requestor1), "requestor1");
		
		assertTrue("requestor2 should be contained in the map (1/2)", map.containsKey(requestor2));
		assertEquals("requestor2 should be contained in the map (2/2)", map.get(requestor2), "requestor2");

		assertFalse("requestor3 should not be contained in the map (1/2)", map.containsKey(requestor3));
		assertNull("requestor3 should not be contained in the map (2/2)", map.get(requestor3));
		
	}
}
