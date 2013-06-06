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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.model.CisAttributeTypes;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.DataTypeFactory;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class DataTypeFactoryTest {
	private static final Logger LOG = LoggerFactory.getLogger(DataTypeFactoryTest.class.getName());

	@Test
	public void testGetScheme() {
		String testTitle = "Get scheme";
		LOG.info("[Test] "+testTitle);
		DataIdentifier id1 = DataIdentifierFactory.fromType(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ADDRESS_HOME_CITY);
		DataIdentifier id2 = null;
		try {
			id2 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://me.ict-societies.eu/"+CisAttributeTypes.MEMBER_LIST);
		} catch (MalformedCtxIdentifierException e) {
			LOG.error("Faillure during data id creation from URI", e);
			fail("Faillure during data id creation from URI: "+e.getMessage());
		}
		DataIdentifier id3 = DataIdentifierFactory.create(DataIdentifierScheme.CIS, "me.ict-societies.eu", CisAttributeTypes.MEMBER_LIST);

		assertEquals("Scheme should be equals", DataIdentifierScheme.CONTEXT, DataTypeFactory.getScheme(id1));
		assertEquals("Scheme should be equals", DataIdentifierScheme.CIS, DataTypeFactory.getScheme(id2));
		assertEquals("Scheme should be equals", DataIdentifierScheme.CIS, DataTypeFactory.getScheme(id3));
	}
	
	@Test
	public void testGetType() {
		String testTitle = "Get type";
		LOG.info("[Test] "+testTitle);
		DataIdentifier id1 = DataIdentifierFactory.fromType(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ADDRESS_HOME_CITY);
		DataIdentifier id2 = null;
		try {
			id2 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://me.ict-societies.eu/"+CisAttributeTypes.MEMBER_LIST);
		} catch (MalformedCtxIdentifierException e) {
			LOG.error("Faillure during data id creation from URI", e);
			fail("Faillure during data id creation from URI: "+e.getMessage());
		}
		DataIdentifier id3 = DataIdentifierFactory.create(DataIdentifierScheme.CIS, "me.ict-societies.eu", CisAttributeTypes.MEMBER_LIST);

		assertEquals("Type should be equals", CtxAttributeTypes.ADDRESS_HOME_CITY, DataTypeFactory.getType(id1));
		assertEquals("Type should be equals", CisAttributeTypes.MEMBER_LIST, DataTypeFactory.getType(id2));
		assertEquals("Type should be equals", CisAttributeTypes.MEMBER_LIST, DataTypeFactory.getType(id3));
		
	}
}
