/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.privacytrust.privacyprotection.test.dataobfuscation.wrapper;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.internal.mock.DataIdentifier;
import org.societies.privacytrust.privacyprotection.dataobfuscation.DataWrapper;
import org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper.SampleWrapper;

/**
 * @author olivierm
 *
 */
public class SampleWrapperTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.dataobfuscation.DataWrapper#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsNullObject() {
		SampleWrapper obj1 = new SampleWrapper(3);
		SampleWrapper obj2 = null;
		assertFalse(obj1.equals(obj2));
	}
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper.SampleWrapper#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsSameInstance() {
		SampleWrapper obj1 = new SampleWrapper(3);
		assertTrue(obj1.equals(obj1));
	}
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper.SampleWrapper#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsSameObject() {
		SampleWrapper obj1 = new SampleWrapper(3);
		SampleWrapper obj2 = new SampleWrapper(3);
		assertTrue(obj1.equals(obj2));
	}
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper.SampleWrapper#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsDifferentDataWrapper() {
		SampleWrapper obj1 = new SampleWrapper(3);
		SampleWrapper obj2 = new SampleWrapper(3);
		obj2.setDataId(new CtxAttributeIdentifier(new CtxEntityIdentifier(null, null, null), null, null));
		assertFalse(obj1.equals(obj2));
	}
	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper.SampleWrapper#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsDifferentObject() {
		SampleWrapper obj1 = new SampleWrapper(3);
		SampleWrapper obj2 = new SampleWrapper(4);
		obj2.setDataId(new CtxAttributeIdentifier(new CtxEntityIdentifier(null, null, null), null, null));
		assertFalse(obj1.equals(obj2));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper.wrapper.SampleWrapper#getParam1()}.
	 */
	@Test
	public void testGetParam1() {
		double expected = 3;
		SampleWrapper obj1 = new SampleWrapper(3);
		assertEquals(expected, obj1.getParam1());
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper.SampleWrapper#setParam1(int)}.
	 */
	@Test
	public void testSetParam1() {
		double expected = 4;
		SampleWrapper obj1 = new SampleWrapper(3);
		obj1.setParam1(4);
		assertEquals(expected, obj1.getParam1());
	}

}
