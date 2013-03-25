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

import org.apache.shindig.social.opensocial.model.Person;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeed;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.orchestration.cpa.impl.CPACreationPatterns;
import org.societies.platform.FacebookConn.impl.FacebookConnectorImpl;
import org.societies.platform.socialdata.SocialData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: bjornmagnus
 * Date: 1/15/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = { "classpath:CPAUnitTest-context.xml"})

public class FBTest {
    private static final Logger logger = LoggerFactory.getLogger(FBTest.class);

    private ISocialConnector connector = null;
    //access_token, expires: 5183998 (?)
    private SocialData socialData;
    private String token = "";
    @Autowired
    private ActivityFeed actFeed;
    //@Qualifier(value = "testSF")
    @Autowired
    private SessionFactory sessionFactory;
    public static void main(String[] args){

        if(args.length < 2){
            System.out.println("not enough arguments (got "+args.length+" needs 2), supply token");
            System.exit(1);
        }   else { System.out.println("params 0: "+args[0]+" length: "+args.length);    }
        FBTest fb = new FBTest();
        ApplicationContextLoader loader = new ApplicationContextLoader();
        loader.load(fb, "SimTest-context.xml");
        fb.init(args[0],args[1]);
        fb.analyze();
    }
    public void init(String token, String accountName) {



        socialData  = new SocialData();
        connector = new FacebookConnectorImpl(token, "bjornmagnus@me.com");
        logger.info("Connector name: " + connector.getConnectorName());
        logger.info("Connector id: " + connector.getID());
        actFeed.setSessionFactory(this.sessionFactory);
        try {
            socialData.addSocialConnector(connector);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        socialData.updateSocialData();
        logger.info("SocialData ready in memory");


        List<?> profiles = null;
        profiles =  socialData.getSocialProfiles();
        assertNotNull("Profiles are null", profiles);
        Iterator it = profiles.iterator();
        int index =1;
        while(it.hasNext()){
            Person p = (Person) it.next();
            logger.info("--- Profile "+index +" ID:" +p.getId() + " -->"+p.getName().getFormatted() );
            index++;
        }
        List<?> activities = socialData.getSocialActivity();
        this.actFeed.setId("1");
        this.actFeed.importActivityEntries(activities);


    }
    public void analyze(){
        final ArrayList<IActivity>  actDiff = new ArrayList<IActivity>();
        final CPACreationPatterns cpa = new CPACreationPatterns();
        class GetActFeedCB implements IActivityFeedCallback {

            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                System.out.println("in receiveresult: "+activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size());
                for(MarshaledActivity act : activityFeedObject.getGetActivitiesResponse().getMarshaledActivity())  {
                    actDiff.add(new Activity(act));
                }
                cpa.init();
                cpa.analyze(actDiff);
            }
        }
        GetActFeedCB dummyFeedback = new GetActFeedCB();
        actFeed.getActivities("0 " + Long.toString(System.currentTimeMillis() + 100000L), 10000, dummyFeedback);
    }
    public Activity makeMessage(String user1, String user2, String message, String published){
        Activity ret = new Activity();
        ret.setActor(user1);
        ret.setObject(message);
        ret.setTarget(user2);
        ret.setPublished(published);
        return ret;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
