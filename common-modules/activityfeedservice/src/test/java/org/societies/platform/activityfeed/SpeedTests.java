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
package org.societies.platform.activityfeed;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeed;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:META-INF/ActivityFeedTest-context.xml" })
public class SpeedTests extends
AbstractTransactionalJUnit4SpringContextTests implements IActivityFeedCallback {
	
	private static Logger LOG = LoggerFactory.getLogger(SpeedTests.class);
	
	int messages = 1000;
	@Autowired
	private ActivityFeed mFeed;
    @Autowired
	private ActivityFeed pFeed;
	
	private SessionFactory sessionFactory=null;
	private Session session=null;
	
	static {
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
	}
	int feedid=0;
	@Before
	public void setupBefore() throws Exception {
		if(sessionFactory==null){
			sessionFactory = mFeed.getSessionFactory();
		}
//		if(session==null){
//			session = sessionFactory.openSession();
//			mFeed.setSession(session);
//		}
//		if(!session.isOpen())
//			session = sessionFactory.openSession();
		LOG.info("i startup ");
		
	}
	@After
	public void tearDownAfter() throws Exception {
		
		mFeed.clear();
//		session.close();
		mFeed = null;
	}
	@Test
    public void SerialTest(){//this is to avoid any parrallelisation of the tests, which would ruin the point.
        LOG.info("@@@ TESTING SPEED OF MEMORY BASED ACTFEED @@@");
        testOrig();
        LOG.info("@@@ TESTING SPEED OF DISK BASED ACTFEED @@@");
        testPersisted();
    }
	public void testOrig(){
        mFeed.setId("1");
		mFeed.startUp(sessionFactory);
		long start = System.currentTimeMillis();
        addRandomActs(mFeed,this.messages);
        long spent = System.currentTimeMillis() - start;
        LOG.info("Added "+this.messages+" messages in "+spent +" ms: "+((double)this.messages)/((double)spent/1000L)+" msgs/sec");
	}
	public void testPersisted(){
        mFeed.setId("2");
		pFeed.startUp(sessionFactory);
		long start = System.currentTimeMillis();
        addRandomActs(pFeed,this.messages);
        long spent = System.currentTimeMillis() - start;
        LOG.info("Added "+this.messages+" messages in "+spent +" ms: "+((double)this.messages)/((double)spent/1000L)+" msgs/sec");
	}
    private void addRandomActs(IActivityFeed af, int number){
        IActivity newAct=new Activity();
        newAct.setActor("speedtest");
        for(int i=0;i<number;i++)
            af.addActivity(newAct,this);

    }

    @Override
    public void receiveResult(MarshaledActivityFeed activityFeedObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
