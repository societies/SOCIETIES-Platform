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

public final class Dbc {
    /**
     * disallow construction
     */
    private Dbc() {
    }

    /**
     * test for a precondition failure.
     * 
     * @param condition
     *            the condition to test
     */
    public static void require(boolean condition) {
        if (!condition) {
            throw new PreconditionException();
        }
    }

    /**
     * test for a precondition failure.
     * 
     * @param message
     *            to be associated with exception
     * @param condition
     *            the condition to test
     */
    public static void require(String message, boolean condition) {
        if (!condition) {
            throw new PreconditionException(message);
        }
    }

    /**
     * test for a postcondition failure.
     * 
     * @param condition
     *            the condition to test
     */
    public static void ensure(boolean condition) {
        if (!condition) {
            throw new PostconditionException();
        }
    }

    /**
     * test for a postcondition failure.
     * 
     * @param message
     *            to be associated with exception
     * @param condition
     *            the condition to test
     */
    public static void ensure(String message, boolean condition) {
        if (!condition) {
            throw new PostconditionException(message);
        }
    }

    /**
     * test for a invariant failure.
     * 
     * @param condition
     *            the condition to test
     */
    public static void invariant(boolean condition) {
        if (!condition) {
            throw new InvariantException();
        }
    }

    /**
     * test for a invariant failure.
     * 
     * @param message
     *            to be associated with exception
     * @param condition
     *            the condition to test
     */
    public static void invariant(String message, boolean condition) {
        if (!condition) {
            throw new InvariantException(message);
        }
    }

    /**
     * general assertion test
     * 
     * @param condition
     *            the condition to test
     */
    public static void assertion(boolean condition) {
        if (!condition) {
            throw new AssertionException();
        }
    }

    /**
     * general assertion test
     * 
     * @param message
     *            to be associated with exception
     * @param condition
     *            the condition to test
     */
    public static void assertion(String message, boolean condition) {
        if (!condition) {
            throw new AssertionException(message);
        }
    }
}
