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
package org.societies.api.privacytrust.privacy.util.privacypolicy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

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
public class ResourceUtilsTest {
	private static Logger LOG = LoggerFactory.getLogger(ResourceUtilsTest.class.getName());
	
	@Test
	public void testGetDataIdentifier() {
		String testTitle = "GetDataIdentifier";
		LOG.info(testTitle);
		DataIdentifier actualDataId = null;
		// -- Context data: scheme + type
		try {
			actualDataId = ResourceUtils.getDataIdentifier(ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ABOUT));
			assertNotNull("Data id should not be null", actualDataId);
			assertEquals("Data id should be equals to: "+DataIdentifierScheme.CONTEXT.name(), DataIdentifierScheme.CONTEXT.name(), actualDataId.getScheme().name());
			assertNotNull("Data id scheme should not be null", actualDataId.getScheme());
			assertNotNull("Data id type should not be null", actualDataId.getType());
			assertEquals("Data id type should be equals to: "+CtxAttributeTypes.ABOUT, CtxAttributeTypes.ABOUT, actualDataId.getType());
		} catch (MalformedCtxIdentifierException e) {
			fail("MalformedCtxIdentifierException");
			e.printStackTrace();
		}

		// -- CIS data: scheme + type
		String typeCisMemberList = "cis-member-list";
		try {
			actualDataId = ResourceUtils.getDataIdentifier(ResourceUtils.create(DataIdentifierScheme.CIS, typeCisMemberList));
			assertNotNull("Data id should not be null", actualDataId);
			assertEquals("Data id should be equals to: "+DataIdentifierScheme.CIS.name(), DataIdentifierScheme.CIS.name(), actualDataId.getScheme().name());
			assertNotNull("Data id scheme should not be null", actualDataId.getScheme());
			assertNotNull("Data id type should not be null", actualDataId.getType());
			assertEquals("Data id type should be equals to: "+typeCisMemberList, typeCisMemberList, actualDataId.getType());
		} catch (MalformedCtxIdentifierException e) {
			fail("MalformedCtxIdentifierException");
			e.printStackTrace();
		}

		// -- CIS data: uri
		try {
			actualDataId = ResourceUtils.getDataIdentifier(ResourceUtils.create(DataIdentifierUtils.toUriString(DataIdentifierScheme.CIS, typeCisMemberList)));
			assertNotNull("Data id should not be null", actualDataId);
			assertEquals("Data id should be equals to: "+DataIdentifierScheme.CIS.name(), DataIdentifierScheme.CIS.name(), actualDataId.getScheme().name());
			assertNotNull("Data id scheme should not be null", actualDataId.getScheme());
			assertNotNull("Data id type should not be null", actualDataId.getType());
			assertEquals("Data id type should be equals to: "+typeCisMemberList, typeCisMemberList, actualDataId.getType());
		} catch (MalformedCtxIdentifierException e) {
			fail("MalformedCtxIdentifierException");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testEqual() {
		String testTitle = "Equal";
		LOG.info(testTitle);
		Resource resource1 = null;
		Resource resource2 = null;
		Action notResource = null;
		// -- Null Privacy Policy
		assertTrue("Same null resource should be equal", ResourceUtils.equal(resource1, resource1));
		assertTrue("Null resources should be equal", ResourceUtils.equal(resource1, resource2));
		assertTrue("Null resource and null object should be equal", ResourceUtils.equal(resource1, notResource));
		// -- Empty Privacy Policy
		resource1 = new Resource();
		resource2 = new Resource();
		notResource = new Action();
		assertTrue("Same empty resource should be equal", ResourceUtils.equal(resource1, resource1));
		assertTrue("Empty resources should be equal", ResourceUtils.equal(resource1, resource2));
		assertTrue("Empty resources should be equal (inverse)", ResourceUtils.equal(resource2, resource1));
		assertFalse("Empty resource and empty object should not be equal", ResourceUtils.equal(resource1, notResource));
		// -- Privacy Policy
		List<Condition> conditions1 = new ArrayList<Condition>();
		conditions1.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
		List<Condition> conditions2 = new ArrayList<Condition>();
		conditions2.add(ConditionUtils.create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "Yes"));
		conditions2.add(ConditionUtils.create(ConditionConstants.MAY_BE_INFERRED, "Yes"));
		resource1 = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION);
		assertTrue("Same resource should be equal", ResourceUtils.equal(resource1, resource1));
		assertFalse("Different resources should be equal", ResourceUtils.equal(resource1, resource2));
		assertFalse("Different resources should be equal (inverse)", ResourceUtils.equal(resource2, resource1));
		resource2 = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ABOUT);
		assertFalse("Different resources should be equal (2)", ResourceUtils.equal(resource1, resource2));
		assertFalse("Different resources should be equal (inverse) (2)", ResourceUtils.equal(resource2, resource1));
		resource2 = ResourceUtils.create(DataIdentifierScheme.CONTEXT+"://emma.ict-societies.eu/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.ACTION+"/13");
		assertFalse("Different resources should be equal (2)", ResourceUtils.equal(resource1, resource2));
		assertFalse("Different resources should be equal (inverse) (2)", ResourceUtils.equal(resource2, resource1));
		resource2 = ResourceUtils.create(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ACTION);
		assertTrue("Equal resources should be equal", ResourceUtils.equal(resource1, resource2));
		assertTrue("Equal resources should be equal (inverse)", ResourceUtils.equal(resource2, resource1));
		assertFalse("resource and object should not be equal", ResourceUtils.equal(resource1, notResource));
	}
}
