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
package org.societies.orchestration.cpa.test;

import org.hibernate.SessionFactory;
import org.societies.activity.ActivityFeed;
import org.societies.activity.PersistedActivityFeed;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.schema.activityfeed.Activityfeed;
import org.societies.orchestration.cpa.impl.CPACreationPatterns;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CISSimulator implements IActivityFeedCallback {
	private HashMap<String,HashMap<String,Double>> userToUserMap;
	private int messagesperuserperday;
	private int users;
	@Autowired
	private PersistedActivityFeed actFeed;
	@Autowired
	private SessionFactory sessionFactory;
	private int maxActs = 2000;
	public int getMaxActs() {
		return maxActs;
	}

	public void setMaxActs(int maxActs) {
		this.maxActs = maxActs;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public CISSimulator(int initUsers, int messagesperuserperday)
	{
		this.messagesperuserperday = messagesperuserperday;
		userToUserMap = new HashMap<String,HashMap<String,Double>>();
		init(initUsers);
	}
	
	public ActivityFeed getActFeed() {
		return actFeed;
	}

	public void setActFeed(PersistedActivityFeed actFeed) {
		this.actFeed = actFeed;
	}

	public void init(int initUsers){
		String base = "user";
		for(int i = 0;i<initUsers;i++){
			addUser(base+Integer.toString(i+1));
		}
		Set<String> keySet = userToUserMap.keySet();
		Object[] keyArr = keySet.toArray();
		int arrsize = keyArr.length;
		for(int i=0;i<arrsize;i++){
			for(int i2=0;i2<arrsize;i2++){
				if(i!=i2){
					System.out.println("adding connection from "+(String)keyArr[i]+" to "+(String)keyArr[i2]);
					setUserToUserRate((String)keyArr[i],(String)keyArr[i2],Math.abs(Math.random()-0.31d));
				}
			}
		}
		this.users = initUsers;
	}
	/*
	 * 
	 * @param double rate
	 */
	public void setUserToUserRate(String user1, String user2, double rate){
		userToUserMap.get(user1).put(user2, new Double(rate));
		userToUserMap.get(user2).put(user1, new Double(rate));
	}
	public void addUser(String user){
		userToUserMap.put(user, new HashMap<String,Double>());
	}
	public ICisOwned simulate(long days){
		ICISSimulated ret = new ICISSimulated();
		ret.setFeed(actFeed);
		for(String user : userToUserMap.keySet()){
			try {
				ret.addMember(user,"member");
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//sample every "minute"
		long daysGone=0;
		List<String> usersList = ret.getUsers();
		Activity act = null; String user1, user2;
		long timecounter = System.currentTimeMillis()-(daysGone*24L*3600L*1000L);
		long msgCounter = 0;
		while(daysGone<days){
			for(int i=0;i<(24*60);i++){
				for(int u1=0;u1<users;u1++){
					for(int u2=0;u2<users;u2++){
						if(u1==u2)
							continue;
						
						user1=usersList.get(u1);
						user2=usersList.get(u2);
						if(Math.random()>this.userToUserMap.get(user1).get(user2)){
							System.out.println("msgCounter: "+ (++msgCounter) + " maxActs: "+maxActs+" count: "+((ActivityFeed)ret.getFeed()).count());
							ret.getActivityFeed().addActivity(makeMessage(user1,user2,"message",Long.toString((long)(Math.random()*(24L*3600L*1000L)))),this); //add message to random time of this day given probabilities in the table..
						}
						if(msgCounter > this.maxActs){
							break;
						}
					}
					if(msgCounter > this.maxActs){
						break;
					}
					
				}
				if(msgCounter > this.maxActs){
					break;
				}
				
			}
			if(msgCounter > this.maxActs){
				break;
			}
			timecounter += (24L*3600L*1000L);
		}
		System.out.println("rethash: "+ret.hashCode()+" ret.getFeed(): "+ret.getFeed()+ " ret.getFeed().count(): "+((ActivityFeed)ret.getFeed()).count());
		return ret;
	}
	public Activity makeMessage(String user1, String user2, String message, String published){
		Activity ret = new Activity();
		ret.setActor(user1);
		ret.setObject(message);
		ret.setTarget(user2);
		ret.setPublished(published);
		return ret;
	}
	//test of the test code..
	public static void main(String[] args){
		CISSimulator sim = new CISSimulator(10,10);
		
        ApplicationContextLoader loader = new ApplicationContextLoader();
        loader.load(sim, "SimTest-context.xml");
		sim.getActFeed().setSession(sim.getSessionFactory().openSession());
        sim.simulate(1);
        sim.maxActs = 2000;
        
        CPACreationPatterns cpa = new CPACreationPatterns();
        cpa.init();
        cpa.analyze(sim.getActFeed().getActivities("0"+Long.toString(System.currentTimeMillis()+100000000L)));
        
	}

    @Override
    public void receiveResult(Activityfeed activityFeedObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
