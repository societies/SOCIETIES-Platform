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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.util.DataTypeUtils;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class DataTypeUtilTest {
	private static DataTypeUtils dataTypeUtil;

	@BeforeClass
	public static void tearUpClass() {
		dataTypeUtil = new DataTypeUtils();
	}


	@Test
	public void testLoad() {
		DataTypeUtils dataTypeUtil = new DataTypeUtils();
		assertTrue("Should be true", dataTypeUtil.isLeaf(CtxAttributeTypes.ADDRESS_HOME_CITY));
	}

	@Test
	public void testIsLeaf() {
		assertTrue("NAME_FIRST should be a leaf", dataTypeUtil.isLeaf(CtxAttributeTypes.NAME_FIRST));
		assertTrue("ACTION should be a leaf", dataTypeUtil.isLeaf(CtxAttributeTypes.ACTION));
		assertFalse("NAME should not be a leaf", dataTypeUtil.isLeaf(CtxAttributeTypes.NAME));
		assertTrue("UnknownType should be a leaf", dataTypeUtil.isLeaf("UnknownType"));

		assertTrue("leaf2 should be a leaf", dataTypeUtil.isLeaf("leaf2"));
		assertTrue("leaf1 should be a leaf", dataTypeUtil.isLeaf("leaf1"));
		assertFalse("middle should not be a leaf", dataTypeUtil.isLeaf("middle"));
		assertFalse("root should not be a leaf", dataTypeUtil.isLeaf("root"));
	}

	@Test
	public void testIsRoot() {
		assertFalse("NAME_FIRST should not be a root", dataTypeUtil.isRoot(CtxAttributeTypes.NAME_FIRST));
		assertTrue("ACTION should be a root", dataTypeUtil.isRoot(CtxAttributeTypes.ACTION));
		assertTrue("NAME should not be a root", dataTypeUtil.isRoot(CtxAttributeTypes.NAME));
		assertTrue("UnknownType should be a root", dataTypeUtil.isRoot("UnknownType"));

		assertFalse("leaf2 should not be a root", dataTypeUtil.isRoot("leaf2"));
		assertFalse("leaf1 should not be a root", dataTypeUtil.isRoot("leaf1"));
		assertFalse("middle should not be a root", dataTypeUtil.isRoot("middle"));
		assertTrue("root should be a root", dataTypeUtil.isRoot("root"));
	}

	@Test
	public void testGetParent() {
		assertEquals("NAME_FIRST parent should be NAME", CtxAttributeTypes.NAME, dataTypeUtil.getParent(CtxAttributeTypes.NAME_FIRST));
		assertNull("ACTION should not have parent", dataTypeUtil.getParent(CtxAttributeTypes.ACTION));
		assertNull("NAME should not have parent", dataTypeUtil.getParent(CtxAttributeTypes.NAME));
		assertNull("UnknownType should not have parent", dataTypeUtil.getParent("UnknownType"));

		assertEquals("leaf2 parent should be middle", "middle", dataTypeUtil.getParent("leaf2"));
		assertEquals("leaf1 should be root", "root", dataTypeUtil.getParent("leaf1"));
		assertEquals("middle should be root", "root", dataTypeUtil.getParent("middle"));
		assertNull("root should not be a leaf", dataTypeUtil.getParent("root"));
	}

	@Test
	public void testGetChildren() {
		// Name FIRST
		assertNull("NAME_FIRST should not have children", dataTypeUtil.getChildren(CtxAttributeTypes.NAME_FIRST));
		// ACtion
		assertNull("ACTION should not have children", dataTypeUtil.getChildren(CtxAttributeTypes.ACTION));
		// Unkwon
		assertNull("UnknownType should not have children", dataTypeUtil.getChildren("UnknownType"));
		// NAME
		Set<String> actualChildrenList = dataTypeUtil.getChildren(CtxAttributeTypes.NAME);
		Set<String> expectedChildrenList = new HashSet<String>();
		expectedChildrenList.add(CtxAttributeTypes.NAME_LAST);
		expectedChildrenList.add(CtxAttributeTypes.NAME_FIRST);
		assertNotNull("NAME should have children", actualChildrenList);
		assertTrue("NAME don't have the correct children (expected: "+expectedChildrenList+", but was "+actualChildrenList+")", actualChildrenList.containsAll(expectedChildrenList));
		// leaf2
		assertNull("leaf2 should not have children", dataTypeUtil.getChildren("leaf2"));
		// leaf1
		assertNull("leaf1 should not have children", dataTypeUtil.getChildren("leaf1"));
		// middle
		Set<String> actualmiddleChildrenList = dataTypeUtil.getChildren("middle");
		Set<String> expectedmiddleChildrenList = new HashSet<String>();
		expectedmiddleChildrenList.add("leaf2");
		assertNotNull("middle should have children", actualmiddleChildrenList);
		assertTrue("middle don't have the correct children (expected: "+expectedmiddleChildrenList+", but was "+actualmiddleChildrenList+")", actualmiddleChildrenList.containsAll(expectedmiddleChildrenList));
		// root
		Set<String> actualrootChildrenList = dataTypeUtil.getChildren("root");
		Set<String> expectedrootChildrenList = new HashSet<String>();
		expectedrootChildrenList.add("leaf1");
		expectedrootChildrenList.add("middle");
		assertNotNull("root should have children", actualrootChildrenList);
		assertTrue("root don't have the correct children (expected: "+expectedrootChildrenList+", but was "+actualrootChildrenList+")", actualrootChildrenList.containsAll(expectedrootChildrenList));
	}
	
	@Test
	public void testGetChildrenRecursive() {
		Set<String> test1 = dataTypeUtil.getChildren(CtxAttributeTypes.NAME);
		assertNotNull("1: NAME should have children", test1);
		Set<String> test2 = dataTypeUtil.getChildren(CtxAttributeTypes.NAME);
		assertNotNull("2: NAME should have children", test2);
		Set<String> test3 = dataTypeUtil.getChildren(CtxAttributeTypes.NAME, true);
		assertNotNull("3: NAME should have children", test3);
		
		// Namefirst
		assertNull("NAME_FIRST should not have children", dataTypeUtil.getChildren(CtxAttributeTypes.NAME_FIRST, true));
		// Action
		assertNull("ACTION should not have children", dataTypeUtil.getChildren(CtxAttributeTypes.ACTION, true));
		// Unknown
		assertNull("UnknownType should not have children", dataTypeUtil.getChildren("UnknownType", true));
		// Name
		Set<String> actualChildrenList = dataTypeUtil.getChildren(CtxAttributeTypes.NAME, true);
		Set<String> expectedChildrenList = new HashSet<String>();
		expectedChildrenList.add(CtxAttributeTypes.NAME_LAST);
		expectedChildrenList.add(CtxAttributeTypes.NAME_FIRST);
		assertNotNull("NAME should have children (exected: "+expectedChildrenList+" but was "+actualChildrenList+")", actualChildrenList);
		assertTrue("NAME don't have the correct children (expected: "+expectedChildrenList+", but was "+actualChildrenList+")", actualChildrenList.containsAll(expectedChildrenList));
		// leaf2
		assertNull("leaf2 should not have children", dataTypeUtil.getChildren("leaf2", true));
		// leaf1
		assertNull("leaf1 should not have children", dataTypeUtil.getChildren("leaf1", true));
		// middle
		Set<String> actualmiddleChildrenList = dataTypeUtil.getChildren("middle", true);
		Set<String> expectedmiddleChildrenList = new HashSet<String>();
		expectedmiddleChildrenList.add("leaf2");
		assertNotNull("middle should have children", actualmiddleChildrenList);
		assertTrue("middle don't have the correct children (expected: "+expectedmiddleChildrenList+", but was "+actualmiddleChildrenList+")", actualmiddleChildrenList.containsAll(expectedmiddleChildrenList));
		// root
		Set<String> actualrootChildrenList = dataTypeUtil.getChildren("root", true);
		Set<String> expectedrootChildrenList = new HashSet<String>();
		expectedrootChildrenList.add("leaf1");
		expectedrootChildrenList.add("leaf2");
		assertNotNull("root should have children", actualrootChildrenList);
		assertTrue("root don't have the correct children (expected: "+expectedrootChildrenList+", but was "+actualrootChildrenList+")", actualrootChildrenList.containsAll(expectedrootChildrenList));
	}

	@Test
	public void testGetLookableDataTypes() {
		Set<String> test1 = dataTypeUtil.getChildren(CtxAttributeTypes.NAME);
		Set<String> test2 = dataTypeUtil.getChildren(CtxAttributeTypes.NAME);
		assertNotNull("1: NAME should have children", test1);
		assertNotNull("2: NAME should have children", test2);
		
		// NAME_FIRST
		Set<String> expectedNameFirstChildrenList = new HashSet<String>();
		expectedNameFirstChildrenList.add(CtxAttributeTypes.NAME_FIRST);
		assertEquals("NAME_FIRST don't have the correct lookable data types", expectedNameFirstChildrenList, dataTypeUtil.getLookableDataTypes(CtxAttributeTypes.NAME_FIRST));
		// ACTION
		Set<String> expectedActionChildrenList = new HashSet<String>();
		expectedActionChildrenList.add(CtxAttributeTypes.ACTION);
		assertEquals("ACTION don't have the correct lookable data types", expectedActionChildrenList, dataTypeUtil.getLookableDataTypes(CtxAttributeTypes.ACTION));
		// UnknownType
		Set<String> expectedUnknownTypeChildrenList = new HashSet<String>();
		expectedUnknownTypeChildrenList.add("UnknownType");
		assertEquals("UnknownType don't have the correct lookable data types", expectedUnknownTypeChildrenList, dataTypeUtil.getLookableDataTypes("UnknownType"));
		// NAME
		Set<String> actualChildrenList = dataTypeUtil.getLookableDataTypes(CtxAttributeTypes.NAME);
		Set<String> expectedChildrenList = new HashSet<String>();
		expectedChildrenList.add(CtxAttributeTypes.NAME_LAST);
		expectedChildrenList.add(CtxAttributeTypes.NAME_FIRST);
		assertNotNull("NAME should have lookable data types", actualChildrenList);
		assertTrue("NAME don't have the correct lookable data types (expected: "+expectedChildrenList+", but was "+actualChildrenList+")", actualChildrenList.containsAll(expectedChildrenList));
		// leaf2
		Set<String> expectedleaf2FirstChildrenList = new HashSet<String>();
		expectedleaf2FirstChildrenList.add("leaf2");
		assertEquals("leaf2 don't have the correct lookable data types", expectedleaf2FirstChildrenList, dataTypeUtil.getLookableDataTypes("leaf2"));
		// leaf1
		Set<String> expectedleaf1FirstChildrenList = new HashSet<String>();
		expectedleaf1FirstChildrenList.add("leaf1");
		assertEquals("leaf1 don't have the correct lookable data types", expectedleaf1FirstChildrenList, dataTypeUtil.getLookableDataTypes("leaf1"));
		// middle
		Set<String> actualmiddleChildrenList = dataTypeUtil.getLookableDataTypes("middle");
		Set<String> expectedmiddleChildrenList = new HashSet<String>();
		expectedmiddleChildrenList.add("leaf2");
		assertNotNull("middle should have children", actualmiddleChildrenList);
		assertTrue("middle don't have the correct children (expected: "+expectedmiddleChildrenList+", but was "+actualmiddleChildrenList+")",
				actualmiddleChildrenList.containsAll(expectedmiddleChildrenList));
		// root
		Set<String> actualrootChildrenList = dataTypeUtil.getLookableDataTypes("root");
		Set<String> expectedrootChildrenList = new HashSet<String>();
		expectedrootChildrenList.add("leaf1");
		expectedrootChildrenList.add("leaf2");
		assertNotNull("root should have children", actualrootChildrenList);
		assertTrue("root don't have the correct children (expected: "+expectedrootChildrenList+", but was "+actualrootChildrenList+")",
				actualrootChildrenList.containsAll(expectedrootChildrenList));
	}
}
