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

import org.junit.Test;
import org.societies.api.context.model.CtxAttributeTypes;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class DataTypeUtilTest {

	@Test
	public void testLoad() {
		DataTypeUtil dataTypeUtil = new DataTypeUtil();
		assertTrue("Should be true", dataTypeUtil.isLeaf(CtxAttributeTypes.ADDRESS_HOME_CITY));
	}
	
	@Test
	public void testIsLeaf() {
		DataTypeUtil dataTypeUtil = new DataTypeUtil();
		assertTrue("NAME_FIRST should be a leaf", dataTypeUtil.isLeaf(CtxAttributeTypes.NAME_FIRST));
		assertTrue("ACTION should be a leaf", dataTypeUtil.isLeaf(CtxAttributeTypes.ACTION));
		assertFalse("NAME should not be a leaf", dataTypeUtil.isLeaf(CtxAttributeTypes.NAME));
		assertTrue("UnknownType should be a leaf", dataTypeUtil.isLeaf("UnknownType"));
	}
	
	@Test
	public void testIsRoot() {
		DataTypeUtil dataTypeUtil = new DataTypeUtil();
		assertFalse("NAME_FIRST should not be a root", dataTypeUtil.isRoot(CtxAttributeTypes.NAME_FIRST));
		assertTrue("ACTION should be a root", dataTypeUtil.isRoot(CtxAttributeTypes.ACTION));
		assertTrue("NAME should not be a root", dataTypeUtil.isRoot(CtxAttributeTypes.NAME));
		assertTrue("UnknownType should be a root", dataTypeUtil.isRoot("UnknownType"));
	}
	
	@Test
	public void testGetParent() {
		DataTypeUtil dataTypeUtil = new DataTypeUtil();
		assertEquals("NAME_FIRST parent should be NAME", CtxAttributeTypes.NAME, dataTypeUtil.getParent(CtxAttributeTypes.NAME_FIRST));
		assertNull("ACTION should not have parent", dataTypeUtil.getParent(CtxAttributeTypes.ACTION));
		assertNull("NAME should not have parent", dataTypeUtil.getParent(CtxAttributeTypes.NAME));
		assertNull("UnknownType should not have parent", dataTypeUtil.getParent("UnknownType"));
	}
	
	@Test
	public void testGetChildren() {
		DataTypeUtil dataTypeUtil = new DataTypeUtil();
		assertNull("NAME_FIRST should not have children", dataTypeUtil.getChildren(CtxAttributeTypes.NAME_FIRST));
		assertNull("ACTION should not have children", dataTypeUtil.getChildren(CtxAttributeTypes.ACTION));
		assertNull("UnknownType should not have children", dataTypeUtil.getChildren("UnknownType"));
		Set<String> actualChildrenList = dataTypeUtil.getChildren(CtxAttributeTypes.NAME);
		Set<String> restrictionChildrenListExpectedActual = dataTypeUtil.getChildren(CtxAttributeTypes.NAME);
		Set<String> expectedChildrenList = new HashSet<String>();
		expectedChildrenList.add(CtxAttributeTypes.NAME_LAST);
		expectedChildrenList.add(CtxAttributeTypes.NAME_FIRST);
		assertNotNull("NAME should have children", actualChildrenList);
		restrictionChildrenListExpectedActual.removeAll(expectedChildrenList); // Remove all element from expectedChildrenList that are in retrieved children list. Result should be an empty list.
		assertTrue("NAME don't have the correct children (expected: "+expectedChildrenList+", but was "+actualChildrenList+")", restrictionChildrenListExpectedActual.isEmpty());
	}
}
