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
package org.societies.context.broker.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.context.broker.api.security.CtxPermission;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
public class CtxPermissionTest {
	
	private static final String CTX_ENTITY_ID_STR = "myCSS/ENTITY/person/3";
	private static final String CTX_ATTRIBUTE_ID_STR = "myCSS/ENTITY/person/3/ATTRIBUTE/Name/6";
	private static final String CTX_ASSOCIATION_ID_STR = "myCSS/ASSOCIATION/hasFriends/9";
	
	private static CtxEntityIdentifier ctxEntityId;
    private static CtxAttributeIdentifier ctxAttributeId;    
    private static CtxAssociationIdentifier ctxAssociationId;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		ctxEntityId = new CtxEntityIdentifier(CTX_ENTITY_ID_STR);
		ctxAttributeId = new CtxAttributeIdentifier(CTX_ATTRIBUTE_ID_STR);
		ctxAssociationId = new CtxAssociationIdentifier(CTX_ASSOCIATION_ID_STR);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		ctxEntityId = null;
		ctxAttributeId = null;
		ctxAssociationId = null;
	}

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
	 * Test method for {@link org.societies.context.broker.api.security.CtxPermission#getResource()}.
	 */
	@Test
	public void testGetResource() {
		
		final CtxPermission readPerm = new CtxPermission(ctxAttributeId, "read");
		assertEquals(ctxAttributeId, readPerm.getResource());
        assertEquals(ctxAttributeId.toString(), readPerm.getName());
	}
	
	/**
     * Test method for
     * {@link org.societies.context.broker.api.security.CtxPermission#getActions()}.
     */
    @Test
    public void testGetValidActions() {
    	
        final CtxPermission readPerm = new CtxPermission(ctxAttributeId, "read");
        assertEquals("read", readPerm.getActions());
        final CtxPermission writePerm = new CtxPermission(ctxAttributeId, "write");
        assertEquals("write", writePerm.getActions());
        final CtxPermission createPerm = new CtxPermission(ctxAttributeId, "create");
        assertEquals("create", createPerm.getActions());
        final CtxPermission deletePerm = new CtxPermission(ctxAttributeId, "delete");
        assertEquals("delete", deletePerm.getActions());
        final CtxPermission readWritePerm = new CtxPermission(ctxAttributeId,
                "write,read");
        assertEquals("read,write", readWritePerm.getActions());
        final CtxPermission noPerm1 = new CtxPermission(ctxAssociationId, null);
        assertEquals("", noPerm1.getActions());
        final CtxPermission noPerm2 = new CtxPermission(ctxEntityId, "");
        assertEquals("", noPerm2.getActions());
    }

    /**
     * Test method for
     * {@link org.societies.context.broker.api.security.CtxPermission#getActions()}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetMalformedActions() {
    	
        new CtxPermission(ctxAttributeId, "read,write,eat");
    }

	/**
	 * Test method for {@link org.societies.context.broker.api.security.CtxPermission#implies(java.security.Permission)}.
	 */
	@Test
	public void testImpliesPermission() {
		
		// Context entity id has READ, WRITE, checking against the same entity
        // id with a subset of the permitted actions, i.e. READ
        final CtxPermission entReadWritePerm = new CtxPermission(ctxEntityId,
                "read,write");
        final CtxPermission entReadPerm = new CtxPermission(ctxEntityId, "read");
        assertTrue(entReadWritePerm.implies(entReadPerm));
        // Assert the reverse fails
        assertFalse(entReadPerm.implies(entReadWritePerm));

        // Context entity id has READ, WRITE, checking against a scoped context
        // attribute id with a subset of the permitted actions, i.e. READ
        final CtxPermission attrReadPerm = new CtxPermission(ctxAttributeId, "read");
        assertTrue(entReadWritePerm.implies(attrReadPerm));

        // Context entity id has READ, checking against a scoped context
        // attribute id with a different action, i.e. WRITE
        final CtxPermission attrWritePerm = new CtxPermission(ctxAttributeId, "write");
        assertFalse(entReadPerm.implies(attrWritePerm));
	}

	/**
	 * Test method for {@link org.societies.context.broker.api.security.CtxPermission#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		
		// Same ctx id and actions
        final CtxPermission perm1 = new CtxPermission(ctxAttributeId, "read,write");
        final CtxPermission perm2 = new CtxPermission(ctxAttributeId, "write,read");
        assertEquals(perm1.hashCode(), perm2.hashCode());
        assertTrue(perm1.equals(perm2));

        // Same ctx id but different actions
        final CtxPermission perm3 = new CtxPermission(ctxAttributeId, "read");
        assertFalse(perm1.equals(perm3));

        // Same actions but different ctx id
        final CtxPermission perm4 = new CtxPermission(ctxEntityId, "read");
        assertFalse(perm3.equals(perm4));
	}
}