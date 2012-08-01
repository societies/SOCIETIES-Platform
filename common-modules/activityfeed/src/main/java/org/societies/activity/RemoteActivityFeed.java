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


package org.societies.activity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedCallback;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;

import org.societies.api.schema.activity.Activity;
import org.societies.api.schema.activityfeed.Activityfeed;
import org.societies.api.schema.activityfeed.AddActivity;
import org.societies.api.schema.activityfeed.CleanUpActivityFeed;
import org.societies.api.schema.activityfeed.DeleteActivity;
import org.societies.api.schema.activityfeed.GetActivities;
import org.societies.api.schema.cis.community.Community;





public class RemoteActivityFeed implements IActivityFeed {

	private static Logger LOG = LoggerFactory.getLogger(ActivityFeed.class);
	private ICommManager iCommMgr;
	private IIdentity remoteCISid;
	

	
	public RemoteActivityFeed(ICommManager iCommMgr, IIdentity remoteCISid) {
		super();
		this.iCommMgr = iCommMgr;
		this.remoteCISid = remoteCISid;
	}
	


	/*
	public boolean deleteActivity(IActivity activity) {
		LOG.debug("client call to delete activity to a RemoteCIS");
		Activityfeed ac = new Activityfeed();
		DeleteActivity d = new DeleteActivity();
		ac.setDeleteActivity(d);
		Activity a = new Activity();
		d.setActivity(a);
		a.setActor(activity.getActor());
		a.setObject(activity.getObject());
		a.setTarget(activity.getTarget());
		a.setPublished(activity.getPublished());
		a.setVerb(activity.getVerb());
		this.sendXmpp(ac, new delAcCallBack());
				
		return true;
	}*/
	
	class delAcCallBack implements IActivityFeedCallback{

		public delAcCallBack(){
			
		}
		
		@Override
		public void receiveResult(Activityfeed activityFeedObject) {
			// TODO Auto-generated method stub
			
		}
		
	}

	@Override
	public long importActivtyEntries(List<?> activityEntries) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

	public void getActivities(String timePeriod, IActivityFeedCallback c) {
		LOG.debug("client call to get activities, without query, from a RemoteCIS");
		Activityfeed ac = new Activityfeed();
		GetActivities g = new GetActivities();
		g.setTimePeriod(timePeriod);
		ac.setGetActivities(g);
		this.sendXmpp(ac, c);
	}

	public void getActivities(String query,
			String timePeriod, IActivityFeedCallback c) {
		LOG.debug("client call to get activities, with query, from a RemoteCIS");
		Activityfeed ac = new Activityfeed();
		GetActivities g = new GetActivities();
		g.setTimePeriod(timePeriod);
		g.setQuery(query);
		ac.setGetActivities(g);
		this.sendXmpp(ac, c);
	}


	public void addActivity(IActivity activity,IActivityFeedCallback c) {
		LOG.debug("client call to add activity to a RemoteCIS");
		Activityfeed ac = new Activityfeed();
		AddActivity g = new AddActivity();
		Activity a = new Activity();
		a.setActor(activity.getActor());
		a.setObject(activity.getObject());
		a.setTarget(activity.getTarget());
		a.setPublished(activity.getPublished());
		a.setVerb(activity.getVerb());
		g.setActivity(a);
		ac.setAddActivity(g);
		this.sendXmpp(ac, c);
		
	}

	public void cleanupFeed(String criteria,IActivityFeedCallback c) {
		LOG.debug("client call to clean up an activity feed to a RemoteCIS");
		Activityfeed ac = new Activityfeed();
		CleanUpActivityFeed cl = new CleanUpActivityFeed(); 
		cl.setCriteria(criteria);
		ac.setCleanUpActivityFeed(cl);
		this.sendXmpp(ac, c);
	}

	public void deleteActivity(IActivity activity,IActivityFeedCallback c) {
		LOG.debug("client call to delete activity to a RemoteCIS");
		Activityfeed ac = new Activityfeed();
		DeleteActivity d = new DeleteActivity();
		ac.setDeleteActivity(d);
		Activity a = new Activity();
		d.setActivity(a);
		a.setActor(activity.getActor());
		a.setObject(activity.getObject());
		a.setTarget(activity.getTarget());
		a.setPublished(activity.getPublished());
		a.setVerb(activity.getVerb());
		this.sendXmpp(ac, new delAcCallBack());
		
	}
	
	
	
	
	@Override
	public IActivity getEmptyIActivity(){
		org.societies.activity.model.Activity a = new org.societies.activity.model.Activity();
		return a;
	}
	
	
	
	
	
	
	private void sendXmpp(Activityfeed c, IActivityFeedCallback callback){

			Stanza stanza = new Stanza(remoteCISid);
			ActivityFeedCallback commsCallback = new ActivityFeedCallback(
					stanza.getId(), callback);

			try {
				LOG.info("Sending stanza");
				this.iCommMgr.sendIQGet(stanza, c, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

}
