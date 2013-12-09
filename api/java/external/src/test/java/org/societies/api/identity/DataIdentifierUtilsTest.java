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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.model.CisAttributeTypes;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class DataIdentifierUtilsTest {
	private static final Logger LOG = LoggerFactory.getLogger(DataIdentifierUtilsTest.class.getName());

	@Test
	public void testHasSameType() {
		String testTitle = "Get scheme";
		LOG.info("[Test] "+testTitle);
		DataIdentifier id1 = DataIdentifierFactory.fromType(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ADDRESS_HOME_CITY);
		DataIdentifier id2 = DataIdentifierFactory.create(DataIdentifierScheme.CONTEXT, "me.ict-societies.eu", CtxAttributeTypes.ADDRESS_HOME_CITY);
		DataIdentifier id3 = DataIdentifierFactory.fromType(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.BIRTHDAY);
		DataIdentifier id4 = null;
		try {
			id4 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://me.ict-societies.eu/"+CisAttributeTypes.MEMBER_LIST);
		} catch (MalformedCtxIdentifierException e) {
			LOG.error("Faillure during data id creation from URI", e);
			fail("Faillure during data id creation from URI: "+e.getMessage());
		}
		DataIdentifier id5 = DataIdentifierFactory.create(DataIdentifierScheme.CIS, "me.ict-societies.eu", CisAttributeTypes.MEMBER_LIST);
		DataIdentifier id6 = DataIdentifierFactory.create(DataIdentifierScheme.CIS, "you.ict-societies.eu", CisAttributeTypes.MEMBER_LIST);
		DataIdentifier id7 = DataIdentifierFactory.create(DataIdentifierScheme.CIS, "me.ict-societies.eu", CisAttributeTypes.CIS_LIST);
		DataIdentifier id8 = DataIdentifierFactory.fromType(DataIdentifierScheme.CIS, CtxAttributeTypes.ADDRESS_HOME_CITY);
		SimpleDataIdentifier id9 = new SimpleDataIdentifier();
		id9.setUri(DataIdentifierScheme.CIS+"://me.ict-societies.eu/"+CisAttributeTypes.MEMBER_LIST);
		LOG.info(id9.getUri());
		DataIdentifier id10 = new SimpleDataIdentifier();
		DataIdentifier id11 = null;

		// Context Scheme
		assertTrue("Type should be equals", DataIdentifierUtils.hasSameType(id1, id2));
		assertTrue("Type should be equals (inverse)", DataIdentifierUtils.hasSameType(id2, id1));
		assertFalse("Type should not be equals (1/4)", DataIdentifierUtils.hasSameType(id1, id3));
		assertFalse("Type should not be equals (inverse) (2/4)", DataIdentifierUtils.hasSameType(id3, id1));
		assertFalse("Type should not be equals (3/4)", DataIdentifierUtils.hasSameType(id2, id3));
		assertFalse("Type should not be equals (inverse) (4/4)", DataIdentifierUtils.hasSameType(id3, id2));

		// Cis scheme
		assertTrue("Type should be equals", DataIdentifierUtils.hasSameType(id4, id5));
		assertTrue("Type should be equals (inverse)", DataIdentifierUtils.hasSameType(id5, id4));
		assertTrue("Type should be equals even if owner id is different", DataIdentifierUtils.hasSameType(id5, id6));
		assertTrue("Type should be equals even if owner id is different (inverse)", DataIdentifierUtils.hasSameType(id6, id5));
		assertFalse("Type should not be equals (1/2)", DataIdentifierUtils.hasSameType(id5, id7));
		assertFalse("Type should not be equals (inverse) (2/2)", DataIdentifierUtils.hasSameType(id7, id6));

		assertTrue("Type should be equals (even if only URI is defined) (1/4)", DataIdentifierUtils.hasSameType(id4, id9));
		assertTrue("Type should be equals (even if only URI is defined) (inverse) (2/4)", DataIdentifierUtils.hasSameType(id9, id4));
		assertTrue("Type should be equals (even if only URI is defined) (3/4)", DataIdentifierUtils.hasSameType(id5, id9));
		assertTrue("Type should be equals (even if only URI is defined) (inverse) (4/4)", DataIdentifierUtils.hasSameType(id9, id5));

		// Mix
		assertFalse("Type should not be equals (1/4)", DataIdentifierUtils.hasSameType(id1, id4));
		assertFalse("Type should not be equals (inverse) (2/4)", DataIdentifierUtils.hasSameType(id4, id1));
		assertFalse("Type should not be equals (3/4)", DataIdentifierUtils.hasSameType(id3, id5));
		assertFalse("Type should not be equals (inverse) (4/4)", DataIdentifierUtils.hasSameType(id5, id3));
		// Same type but different scheme
		assertFalse("Type should not be equals (4/6)", DataIdentifierUtils.hasSameType(id1, id8));
		assertFalse("Type should not be equals (inverse) (6/6)", DataIdentifierUtils.hasSameType(id8, id1));

		// Limit
		assertFalse("Type should not be equals if one is null (1/4)", DataIdentifierUtils.hasSameType(id1, id11));
		assertFalse("Type should not be equals if one is null (inverse) (2/4)", DataIdentifierUtils.hasSameType(id11, id1));
		assertFalse("Type should not be equals if one is empty (3/4)", DataIdentifierUtils.hasSameType(id1, id10));
		assertFalse("Type should not be equals if one is empty (inverse) (4/4)", DataIdentifierUtils.hasSameType(id10, id1));
		assertFalse("Type should not be equals if one is empty (5/6)", DataIdentifierUtils.hasSameType(id9, id10));
		assertFalse("Type should not be equals if one is empty (inverse) (6/6)", DataIdentifierUtils.hasSameType(id10, id9));
	}

	@Test
	public void testIsParentType() {
		String testTitle = "Get scheme";
		LOG.info("[Test] "+testTitle);
		DataIdentifier id0 = DataIdentifierFactory.fromType(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ADDRESS_HOME);
		DataIdentifier id1 = DataIdentifierFactory.fromType(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.ADDRESS_HOME_CITY);
		DataIdentifier id2 = DataIdentifierFactory.create(DataIdentifierScheme.CONTEXT, "me.ict-societies.eu", CtxAttributeTypes.ADDRESS_HOME_CITY);
		DataIdentifier id3 = DataIdentifierFactory.fromType(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.BIRTHDAY);
		DataIdentifier id4 = null;
		try {
			id4 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://me.ict-societies.eu/"+CisAttributeTypes.MEMBER_LIST);
		} catch (MalformedCtxIdentifierException e) {
			LOG.error("Faillure during data id creation from URI", e);
			fail("Faillure during data id creation from URI: "+e.getMessage());
		}
		DataIdentifier id5 = DataIdentifierFactory.create(DataIdentifierScheme.CIS, "me.ict-societies.eu", CisAttributeTypes.MEMBER_LIST);
		DataIdentifier id6 = DataIdentifierFactory.create(DataIdentifierScheme.CIS, "you.ict-societies.eu", CisAttributeTypes.MEMBER_LIST);
		DataIdentifier id7 = DataIdentifierFactory.create(DataIdentifierScheme.CIS, "me.ict-societies.eu", CisAttributeTypes.CIS_LIST);
		DataIdentifier id8 = DataIdentifierFactory.fromType(DataIdentifierScheme.CIS, CtxAttributeTypes.ADDRESS_HOME_CITY);
		SimpleDataIdentifier id9 = new SimpleDataIdentifier();
		id9.setUri(DataIdentifierScheme.CIS+"://me.ict-societies.eu/"+CisAttributeTypes.MEMBER_LIST);
		LOG.info(id9.getUri());
		DataIdentifier id10 = new SimpleDataIdentifier();
		DataIdentifier id11 = null;

		// Context Scheme
		assertTrue("Type 2 should be children of type 1", DataIdentifierUtils.isParentOrSameType(id0, id1));
		assertFalse("Type 1 should not be a children of type 2", DataIdentifierUtils.isParentOrSameType(id1, id0));
		assertTrue("Type should be equals", DataIdentifierUtils.isParentOrSameType(id1, id2));
		assertTrue("Type should be equals (inverse)", DataIdentifierUtils.isParentOrSameType(id2, id1));
		assertFalse("Type should not be equals (1/4)", DataIdentifierUtils.isParentOrSameType(id1, id3));
		assertFalse("Type should not be equals (inverse) (2/4)", DataIdentifierUtils.isParentOrSameType(id3, id1));
		assertFalse("Type should not be equals (3/4)", DataIdentifierUtils.isParentOrSameType(id2, id3));
		assertFalse("Type should not be equals (inverse) (4/4)", DataIdentifierUtils.isParentOrSameType(id3, id2));

		// Cis scheme
		assertTrue("Type should be equals", DataIdentifierUtils.isParentOrSameType(id4, id5));
		assertTrue("Type should be equals (inverse)", DataIdentifierUtils.isParentOrSameType(id5, id4));
		assertTrue("Type should be equals even if owner id is different", DataIdentifierUtils.isParentOrSameType(id5, id6));
		assertTrue("Type should be equals even if owner id is different (inverse)", DataIdentifierUtils.isParentOrSameType(id6, id5));
		assertFalse("Type should not be equals (1/2)", DataIdentifierUtils.isParentOrSameType(id5, id7));
		assertFalse("Type should not be equals (inverse) (2/2)", DataIdentifierUtils.isParentOrSameType(id7, id6));

		assertTrue("Type should be equals (even if only URI is defined) (1/4)", DataIdentifierUtils.isParentOrSameType(id4, id9));
		assertTrue("Type should be equals (even if only URI is defined) (inverse) (2/4)", DataIdentifierUtils.isParentOrSameType(id9, id4));
		assertTrue("Type should be equals (even if only URI is defined) (3/4)", DataIdentifierUtils.isParentOrSameType(id5, id9));
		assertTrue("Type should be equals (even if only URI is defined) (inverse) (4/4)", DataIdentifierUtils.isParentOrSameType(id9, id5));

		// Mix
		assertFalse("Type should not be equals (1/4)", DataIdentifierUtils.isParentOrSameType(id1, id4));
		assertFalse("Type should not be equals (inverse) (2/4)", DataIdentifierUtils.isParentOrSameType(id4, id1));
		assertFalse("Type should not be equals (3/4)", DataIdentifierUtils.isParentOrSameType(id3, id5));
		assertFalse("Type should not be equals (inverse) (4/4)", DataIdentifierUtils.isParentOrSameType(id5, id3));
		// Same type but different scheme
		assertFalse("Type should not be equals (4/6)", DataIdentifierUtils.isParentOrSameType(id1, id8));
		assertFalse("Type should not be equals (inverse) (6/6)", DataIdentifierUtils.isParentOrSameType(id8, id1));

		// Limit
		assertFalse("Type should not be equals if one is null (1/4)", DataIdentifierUtils.isParentOrSameType(id1, id11));
		assertFalse("Type should not be equals if one is null (inverse) (2/4)", DataIdentifierUtils.isParentOrSameType(id11, id1));
		assertFalse("Type should not be equals if one is empty (3/4)", DataIdentifierUtils.isParentOrSameType(id1, id10));
		assertFalse("Type should not be equals if one is empty (inverse) (4/4)", DataIdentifierUtils.isParentOrSameType(id10, id1));
		assertFalse("Type should not be equals if one is empty (5/6)", DataIdentifierUtils.isParentOrSameType(id9, id10));
		assertFalse("Type should not be equals if one is empty (inverse) (6/6)", DataIdentifierUtils.isParentOrSameType(id10, id9));
	}


	@Test
	public void testSortByParent() {
		Set<DataIdentifier> nameList = new HashSet<DataIdentifier>();
		Set<DataIdentifier> actionList = new HashSet<DataIdentifier>();
		DataIdentifier nameId = null;
		DataIdentifier actionId = null;
		try {
			nameId = DataIdentifierFactory.create(DataIdentifierScheme.CONTEXT, "fooCss", CtxAttributeTypes.NAME);
			nameList.add(DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://fooCss/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.NAME_FIRST+"/33"));
			nameList.add(DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://fooCss/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.NAME_LAST+"/38"));
			actionId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://fooCss/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.ACTION+"/42");
			actionList.add(actionId);
		} catch (MalformedCtxIdentifierException e) {
			LOG.error("Faillure during data id creation from URI", e);
			fail("Faillure during data id creation from URI: "+e);
		}
		// - List1
		// Parameters
		Set<DataIdentifier> list1 = new HashSet<DataIdentifier>();
		list1.addAll(nameList);
		list1.add(actionId);
		// Expected
		Map<String, Set<DataIdentifier>> expectedMap1 = new HashMap<String, Set<DataIdentifier>>();
		expectedMap1.put(nameId.getType(), nameList);
		expectedMap1.put(actionId.getType(), actionList);
		assertEquals("NAME_FIRST (leaf), NAME_LAST (leaf), ACTION (root and leaf)  should be sorted as: NAME -> NAME_FIRST, NAME_LAST ; ACTION -> ACTION", expectedMap1, DataIdentifierUtils.sortByParent(list1));

		// - List2
		// Parameters
		Set<DataIdentifier> list2 = new HashSet<DataIdentifier>();
		list2.addAll(nameList);
		list2.add(nameId);
		list2.add(actionId);
		assertEquals("NAME (root not leaf), NAME_FIRST (leaf), NAME_LAST (leaf), ACTION (root and leaf) should be sorted as: NAME -> NAME_FIRST, NAME_LAST ; ACTION -> ACTION", expectedMap1, DataIdentifierUtils.sortByParent(list2));

		// - List3
		// Parameters
		Set<DataIdentifier> list3 = new HashSet<DataIdentifier>();
		list3.add(nameId);
		list3.add(actionId);
		// Expected
		Map<String, Set<DataIdentifier>> expectedMap3 = new HashMap<String, Set<DataIdentifier>>();
		expectedMap3.put(nameId.getType(), null);
		expectedMap3.put(actionId.getType(), actionList);
		assertEquals("NAME (root not leaf), ACTION (root and leaf) should be sorted as: NAME -> null ; ACTION -> ACTION", expectedMap3, DataIdentifierUtils.sortByParent(list3));
	}
}
