/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.utilities.DBC;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestDbc {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    // Assertion test cases
    @Test(expected = AssertionException.class)
    public void testAssertionWithoutMessage() throws Exception {
        Dbc.assertion(false);
    }

    @Test(expected = AssertionException.class)
    public void testAssertionWithMessage() throws Exception {
        Dbc.assertion("some error message", false);
    }

    @Test
    public void testAssertionWithoutMessageTrue() throws Exception {
        Dbc.assertion(true);
    }

    @Test
    public void testAssertionWithMessageTrue() throws Exception {
        Dbc.assertion("some error message", true);
    }

    // Invariant test cases
    @Test(expected = InvariantException.class)
    public void testInvariantWithoutMessage() throws Exception {
        Dbc.invariant(false);
    }

    @Test(expected = InvariantException.class)
    public void testInvariantWithMessage() throws Exception {
        Dbc.invariant("some error message", false);
    }

    @Test
    public void testInvariantWithoutMessageTrue() throws Exception {
        Dbc.invariant(true);
    }

    @Test
    public void testInvariantWithMessageTrue() throws Exception {
        Dbc.invariant("some error message", true);
    }

    // Postcondition test cases
    @Test(expected = PostconditionException.class)
    public void testPostWithoutMessage() throws Exception {
        Dbc.ensure(false);
    }

    @Test(expected = PostconditionException.class)
    public void testPostWithMessage() throws Exception {
        Dbc.ensure("some error message", false);
    }

    @Test
    public void testPostWithoutMessageTrue() throws Exception {
        Dbc.ensure(true);
    }

    @Test
    public void testPostWithMessageTrue() throws Exception {
        Dbc.ensure("some error message", true);
    }

    // Precondition test cases
    @Test(expected = PreconditionException.class)
    public void testPreWithoutMessage() throws Exception {
        Dbc.require(false);
    }

    @Test(expected = PreconditionException.class)
    public void testPreWithMessage() throws Exception {
        Dbc.require("some error message", false);
    }

    @Test
    public void testPreWithoutMessageTrue() throws Exception {
        Dbc.require(true);
    }

    @Test
    public void testPreWithMessageTrue() throws Exception {
        Dbc.require("some error message", true);
    }

}
