/*
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp.,
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

package org.societies.platform.activityfeed;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeed;
import org.societies.activity.ActivityFeedManager;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: bjornmagnus
 * Date: 2/11/13
 * Time: 20:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/ActivityFeedManagerTest-context.xml"})
public class ActivityFeedManagerTest {
    private static Logger LOG = LoggerFactory
            .getLogger(ActivityFeedManagerTest.class);
    //@Autowired
    private ActivityFeedManager activityFeedManagerUnderTest;
    private IActivityFeedManager iActivityFeedManagerUnderTest;

    @Autowired
    private SessionFactory sessionFactory;
    private ICISCommunicationMgrFactory mockCcmFactory;
    private ICommManager mockCSSendpoint = mock(ICommManager.class);
    private IIdentityManager mockIdentityManager;
    private IIdentity mockIdentity;
    private String mockJid = "mockJid";

    @BeforeClass
    public void setupBeforeClass() throws InvalidFormatException {

        when(mockCSSendpoint.getIdManager()).thenReturn(mockIdentityManager);
        when(mockIdentityManager.fromJid(mockJid)).thenReturn(mockIdentity);
        activityFeedManagerUnderTest.setCommManager(mockIdentity);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Test
    public void testConstructor(){
        activityFeedManagerUnderTest = new ActivityFeedManager();
        this.setMockingOn();
    }
    @Test
    public void testGetNewActivityFeed(){

    }
    @Test
    public void testGetOldActivityFeed(){

    }
    @Test
    public void testGetNotMyOwnActivityFeed(){

    }
    @Test
    public void testDeleteOwnActivityFeed(){

    }
    @Test
    public void testDeleteNotMyOwnActivityFeed(){

    }
    @Test
    public void testDeleteNonExistentActivityFeed(){

    }
    public ICommManager getMockCSSendpoint() {
        return mockCSSendpoint;
    }

    public void setMockCSSendpoint(ICommManager mockCSSendpoint) {
        this.mockCSSendpoint = mockCSSendpoint;
    }
}
