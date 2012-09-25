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
package org.societies.integration.test.bit.cpa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisOwned;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;


/**
 * @author  Bjørn Magnus based on work from Rafik and Olivier
 */
public class NominalTestCase {
    private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class.getSimpleName());
    public static Integer testCaseNumber;

    private String privacyPolicyWithoutRequestor;
    private String cssId;
    private List<String> cisIds;
    private String cssPassword;
    private String cisName;
    private String cisDescription;
    private String cisType;
    private int numCIS = 6;
    private Hashtable<String, MembershipCriteria> cisMembershipCriteria;


    @Before
    public void setUp() {
        LOG.info("[#"+testCaseNumber+"] setUp");
        cisIds = new ArrayList<String>();
        cssId = TestCase1096.commManager.getIdManager().getThisNetworkNode().getJid();
        cssPassword = "password.societies.local";
        cisName = "CisTest";
        cisDescription = "CIS to Test CIS Creation";
        cisType = "trialog";
        cisMembershipCriteria = new Hashtable<String, MembershipCriteria>();
    }

    @After
    public void tearDown() {
        LOG.info("[#"+testCaseNumber+"] tearDown");
        if (null != cisIds && !"".equals(cisIds)) {
            for( String cisId : cisIds )
                TestCase1096.cisManager.deleteCis(cisId);
        }
    }


    @Test
    public void testCreateCises() {
        for(int i = 0 ; i < this.numCIS ; i++){
            String cisId;
            String testTitle = "testCreateCisWithoutPrivacyPolicyCreation: ";
            LOG.info("[#"+testCaseNumber+"] "+testTitle);

            // Create CIS
            LOG.info("############## CREATED CIS with Id:"+cssId+" ("+cssPassword+")");
            Future<ICisOwned> futureCis = TestCase1096.cisManager.createCis(cisName, cisType, cisMembershipCriteria, cisDescription);
            ICisOwned newCis = null;
            assertNotNull("Future new CIS is null", futureCis);

            // Retrieve future CIS
            try {
                newCis = futureCis.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException e)  {
                LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
                fail("[Error InterruptedException] "+testTitle);
            } catch (ExecutionException e) {
                LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
                fail("[Error ExecutionException] "+testTitle);
            } catch (TimeoutException e) {
                LOG.error("[Error "+e.getLocalizedMessage()+"] "+testTitle, e);
                fail("[Error TimeoutException] "+testTitle);
            }
            assertNotNull("New CIS is null", newCis);

            // Retrieve CIS id
            cisId = newCis.getCisId();
            cisIds.add(cisId);
            assertNotNull("New CIS id is null", cisIds);

            // Check if the CIS is on the CIS Management registry
            ICis cisRetrieved =  TestCase1096.cisManager.getCis(cisId);
            assertNotNull("New CIS is not stored", cisRetrieved);
            assertEquals("New CIS and retrived CIS should be the same but are not", newCis, cisRetrieved);
        }
        LOG.info("Starting simulation and injection of fake activities..");



        LOG.info("cisIds.size(): "+cisIds.size());
        assert(cisIds.size()==this.numCIS);
    }

}
