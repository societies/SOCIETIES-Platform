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
package org.societies.integration.test.bit.activityfeedct;

/**
 * The test case 1244 aims to test the privacy policy management
 * real usage, using the context broker and the communication
 * framework.
 * 
 * @author Olivier Maridat (Trialog)
 *
 */
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.integration.test.IntegrationTestCase;


public class TestCase109612 extends IntegrationTestCase {
    private static Logger LOG = LoggerFactory.getLogger(TestCase109612.class.getSimpleName());

    public static ICommManager commManager;
    public static ICisManager cisManager;
    public static ICisDirectoryRemote cisDirectory;


    public TestCase109612() {
        // Call the super constructor
        // with test case number
        // and test case classes to run
        super(109612, new Class[]{ActivityFeedManagerRemoteTest.class});
        ActivityFeedManagerRemoteTest.testCaseNumber = this.testCaseNumber;
    }

    /* -- Dependency injection --- */
    public void setCommManager(ICommManager commManager) {
        this.commManager = commManager;
        LOG.info("[#"+testCaseNumber+"] [DependencyInjection] ICommManager injected");
    }
    public void setCisManager(ICisManager cisManager) {
        this.cisManager = cisManager;
        LOG.debug("#"+testCaseNumber+"] [DependencyInjection] ICisManager injected");
    }
    public void setCisDirectory(ICisDirectoryRemote cisDirectory) {
        this.cisDirectory = cisDirectory;
        LOG.debug("#"+testCaseNumber+"] [DependencyInjection] cisDirectory injected");
    }



}