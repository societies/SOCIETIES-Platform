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

package org.societies.orchestration.cpa.test;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeedManager;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.orchestration.ICisDataCollector;
import org.societies.orchestration.cpa.impl.CPA;
import org.societies.orchestration.cpa.test.util.SentenceExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: bjornmagnus
 * Date: 5/3/13
 * Time: 10:24
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:CPAUnitTest-context.xml"})
public class CPAMockTester {
    public static final String TREND_1 = "trend1";
    public static final String NON_TREND = "non_trend";
    public static final String CIS_ID = "cpaid";
    public static final long sleepTime = 100;
    private static Logger LOG = LoggerFactory
            .getLogger(CPAMockTester.class);
    //@Autowired
    private ActivityFeedManager activityFeedManagerUnderTest;
    private IActivityFeedManager iActivityFeedManagerUnderTest;
    private static CPA cpa  = null;
    private Thread thread = null;

    @Autowired
    private SessionFactory sessionFactory;
    private ICISCommunicationMgrFactory mockCcmFactory;
    //mocks
    private static ICisDataCollector mockCollector = mock(ICisDataCollector.class);

    @BeforeClass
    public static void setupBeforeClass() throws InvalidFormatException, CommunicationException, XMPPError {
        cpa = new CPA(mockCollector,CIS_ID);
        when(mockCollector.subscribe(CIS_ID,cpa)).thenReturn(new ArrayList());



    }

    public void startThread(){
        thread = new Thread(cpa);
        thread.start();
    }
    public void stopThread(){
        thread = null;
    }


    @Before
    public void beforeTest(){
        cpa.init();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    Activity a1 = new Activity();
    Activity a2 = new Activity();
    Activity a3 = new Activity();
    Activity a4 = new Activity();
    Activity pa1 = new Activity();
    Activity pa2 = new Activity();
    Activity pa3 = new Activity();
    Activity pa4 = new Activity();
    long t = 0;
    public void makeActs(){
        t = System.currentTimeMillis();
        a1.setTime(t+1);
        System.out.println("adding a1 with t: "+a1.getTime());
        a1.setPublished(Long.toString(t+1));
        a1.setActor("user1");
        a1.setTarget("user2");
        a1.setObject(TREND_1);


        a2.setTime(t+2);
        System.out.println("adding a2 with t: "+a2.getTime());
        a2.setPublished(Long.toString(t+2));
        a2.setActor("user5");
        a2.setTarget("user3");
        a2.setObject(TREND_1);


        a3.setTime(t+3);
        System.out.println("adding a3 with t: "+a3.getTime());
        a3.setPublished(Long.toString(t+3));
        a3.setActor("user5");
        a3.setTarget("user17");
        a3.setObject(TREND_1);


        a4.setTime(t+4);
        System.out.println("adding a4 with t: "+a4.getTime());
        a4.setPublished(Long.toString(t+4));
        a4.setActor("user17");
        a4.setTarget("user9");
        a4.setObject(TREND_1);

        pa1.setTime(t-1);
        pa1.setPublished(Long.toString(t-1));
        pa1.setActor("user1");
        pa1.setTarget("user2");
        pa1.setObject(NON_TREND+"1");


        pa2.setTime(t-2);
        pa2.setPublished(Long.toString(t-2));
        pa2.setActor("user5");
        pa2.setTarget("user3");
        pa2.setObject(NON_TREND+"2");


        pa3.setTime(t-3);
        pa3.setPublished(Long.toString(t-3));
        pa3.setActor("user5");
        pa3.setTarget("user17");
        pa3.setObject(NON_TREND+"3");


        pa4.setTime(t-4);
        pa4.setPublished(Long.toString(t-4));
        pa4.setActor("user17");
        pa4.setTarget("user9");
        pa4.setObject(NON_TREND+"3");

    }

    @Test
    public void simpleTrendTest(){

        ArrayList list = new ArrayList();

        makeActs();
        list.addAll(Arrays.asList(a1, a2, a3, a4, pa1, pa2, pa3, pa4)) ;
        cpa.receiveNewData(list);
        LOG.info("inserted trends into cpa object, sleeping " + sleepTime + " ms while cpa thinks..");
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }

        List<String> trends = cpa.getTrends(2);
        for(String trend : trends)
            LOG.info("trend: "+trend);
    }

    @Test
    public void reutersTest(){
        SentenceExtractor extractor = null;
        ArrayList list = new ArrayList();
        try {
            extractor = new SentenceExtractor(CISSimulator.class.getClassLoader().getResource("reuters21578content.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //String sentence = extractor.getSentences(20,1)[0];
        String tmpSentence = "";
        int maxMsgs = 100;
        int msgCounter = 0;
        while(msgCounter++<maxMsgs){
            tmpSentence = extractor.getSentences(msgCounter,1)[0];
            if(tmpSentence.length()>254)
                tmpSentence = tmpSentence.substring(0,253);
            LOG.info("inserting setence: \""+tmpSentence+"\"");
            list.add(makeMessage("from","two",tmpSentence,"0"));

        }
        cpa.receiveNewData(list);
        this.startThread();
        LOG.info("inserted acts ("+list.size()+") into cpa object, sleeping " + sleepTime + " ms while cpa thinks..");
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
        List<String> trends = cpa.getTrends(2);
        for(String trend : trends)
            LOG.info("trend: "+trend);
    }

    public static Activity makeMessage(String user1, String user2, String message, String published){
        Activity ret = new Activity();
        ret.setActor(user1);
        ret.setObject(message);
        ret.setTarget(user2);
        ret.setPublished(published);
        return ret;
    }
}
