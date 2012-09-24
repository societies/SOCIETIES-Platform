

/**
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

package org.societies.orchestration.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.orchestration.IDataCollectorSubscriber;
import org.societies.api.schema.activity.Activity;
import org.societies.api.schema.activityfeed.Activityfeed;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;


/**
 * Community Intelligence Orchestration 
 * modified from CtxEventExample
 */
public class CISDataCollector implements Subscriber, IActivityFeedCallback {
	
	/*private ICtxEventMgr ctxEventMgr;*/
	private IIdentity userId;
    private ICommManager iCommMgr;
    private PubsubClient pubsubClient;
    private List<IDataCollectorSubscriber> subscribers;
    private ICisOwned cis;
    private static Logger LOG = LoggerFactory
            .getLogger(CISDataCollector.class);
	public CISDataCollector(IIdentity userEntity) {
		this.userId = userEntity;
        init();
	}
    public void init(){
        subscribers = new ArrayList<IDataCollectorSubscriber>();
/*        initCtxEvent();*/
    }
    public CISDataCollector(ICisOwned cisId){
        this.cis = cisId;
        init();
    }
	/*
	private void initCtxEvent() {

		this.registerListenerByCtxId();
	}*/

/*	private void registerListenerByCtxId() {

		if (this.ctxEventMgr == null) {
			return;
		}
		
		final CtxEntityIdentifier scope = new CtxEntityIdentifier(userId.getIdentifier(), userId.getType().toString(), 1l);
		// *** TODO  ***
		//	Attribute type needs 
		//
		final CtxAttributeIdentifier id = new CtxAttributeIdentifier(scope, "ATTRIBUTE_TYPE", 2l);
		try {
			this.ctxEventMgr.registerChangeListener(new MyCtxChangeEventListener(), new String[] {CtxChangeEventTopic.UPDATED}, id);
		} catch (CtxException ce) {
			
		}
	}*/
	
	private void sendEvent(CtxChangeEvent event) {
		
		// *** TODO  ***
		//
		// republish event onwards to domain Authority
	}
    /*
     * This method will connect the data collector to the CIS pubsub for activity feed updates.
     * @param hostingJID this is the JID of the CIS/CSSManager that is hosting the pubsub service.
     * @param pubsubId this id is per default the same ID as the CIS id, e.g. "cis-4795c379-cd6b-4c4f-b160-af1eb767e786.thomas.local"
     */
    private void connectToPubSub(String hostingJID, String pubSubId){
        List<String> packageList = new ArrayList<String>();
        packageList.add("org.societies.api.schema.activity");
        try {
            pubsubClient.addJaxbPackages(packageList);
        } catch (JAXBException e) {
            LOG.warn("Jaxb exception when trying to add packages to pubsub");
            e.printStackTrace();
        }


        try {
            this.pubsubClient.subscriberSubscribe(iCommMgr.getIdManager().fromJid(hostingJID), pubSubId, this);
        } catch (XMPPError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object item) {
        if(item.getClass().equals(org.societies.api.schema.activity.Activity.class)){
            Activity a = (Activity)item;
            LOG.info("pubsubevent with acitvity " + a.getActor() + " " +a.getVerb()+ " " +a.getTarget());
        }else{
            LOG.info("something weird came on the pubsub");
        }
    }
    final Object syncObj = new Object();
    public List<Object> getHistory(String timePeriod){
        ArrayList<Object> ret = new ArrayList<Object>();
        cis.getActivityFeed().getActivities(timePeriod,this);
        //TODO: handle pubsub updates, while this retrieves the history
        synchronized (syncObj){ //make this call synchronised..
            try{
                syncObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ret.addAll(incomingActHist);
        return ret;
    }
    public List<Object> getAllHistory(){
        String timePeriod = "0 "+Long.toString(System.currentTimeMillis());
        return this.getHistory(timePeriod);
    }

    private List<Activity> incomingActHist;
    @Override
    public void receiveResult(Activityfeed activityFeedObject) {
        incomingActHist =  activityFeedObject.getGetActivitiesResponse().getActivity();
        synchronized (syncObj){
            syncObj.notify();
        }
    }


    public List<?> subscribe(IDataCollectorSubscriber subscriber) {
        List<Object> ret = this.getAllHistory(); //TODO: what happens if we receive update while retrieving history, this may be become a common issue when history is large..
        this.subscribers.add(subscriber);
        return ret;
    }

    private class MyCtxChangeEventListener implements CtxChangeEventListener {

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {
			sendEvent(event);
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {
			sendEvent(event);
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {
			sendEvent(event);
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {
			sendEvent(event);
		}
	}

}