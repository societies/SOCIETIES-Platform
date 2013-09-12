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

package org.societies.activity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bjornmagnus
 * Date: 2/8/13
 * Time: 16:06
 */
public class ActivityFeedManager implements IActivityFeedManager {
    //read from DB or created in constructor.
    private List<IActivityFeed> feeds;
    //logger
    private static Logger LOG = LoggerFactory
            .getLogger(ActivityFeedManager.class);

    //these are fetched from spring context..
    private SessionFactory sessionFactory;
    private PubsubClient pubSubClient;
    private ICommManager commManager;

    public ActivityFeedManager(){
        feeds = new ArrayList<IActivityFeed>();
    }
    public ActivityFeedManager(SessionFactory sessionFactory){
        feeds = new ArrayList<IActivityFeed>();
        this.sessionFactory = sessionFactory;
    }
    @Override
    public IActivityFeed getOrCreateFeed(String owner, String feedId, Boolean bPubSub) {
        LOG.debug("In getOrCreateFeed .. ");
        for(IActivityFeed feed : feeds) {
            if(((ActivityFeed)feed).getId().contentEquals(feedId)) {
                if(!((ActivityFeed)feed).getOwner().contentEquals(owner)) {
                    LOG.debug("right feedid but wrong owner");
                    return null;
                }
                return feed;
            }
        }
        LOG.info("did not find feedid creating new..");
        IIdentity identity = null;
        try {
            identity = commManager.getIdManager().fromJid(owner);
        } catch (InvalidFormatException e) {
            LOG.error("Invalid Format of owner jid when creating or getting activityfeed: ", e);
        }
        //not existing, making a new one..
        ActivityFeed ret = new ActivityFeed(feedId,owner);
        if (bPubSub)
        {
        	ret.setPubSubEnabled(true);
        	ret.setPubSubcli(this.pubSubClient);
        }
        
        ret.startUp(this.sessionFactory);
        if (bPubSub) {
        	ret.connectPubSub(identity);
        }

        feeds.add(ret);
        persistNewFeed(ret);
        return ret;
    }

    @Override
    public boolean deleteFeed(String owner, String feedId) {
        Iterator<IActivityFeed> it = feeds.iterator(); IActivityFeed cur;
        while(it.hasNext())    {
            cur = it.next();
            if(((ActivityFeed)cur).getId().contentEquals(feedId)) {
                if(!((ActivityFeed)cur).getOwner().contentEquals(owner)) {
                    return false;
                }
                removeRecord(cur);
                return feeds.remove(cur);
            }
        }
        return false;
    }
    private boolean removeRecord(IActivityFeed feed){
        ActivityFeed deleted = (ActivityFeed)feed;
        Session session = null;
        Transaction t = null;
        try{
            session = sessionFactory.openSession();
            t = session.beginTransaction();
            session.delete(deleted);
            t.commit();
        }catch (Exception e){
            if (t != null) {
                t.rollback();
            }
            LOG.error("Error when trying to delete activityfeed: ",e);
            return false;
        }finally {
            if(session!=null) {
                session.close();
            }
        }
        return true;
    }
    public void init(){
        Session session = getSessionFactory().openSession();
        List<IActivityFeed> tmpFeeds = null;
        try{
        	tmpFeeds = session.createCriteria(ActivityFeed.class).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
        	feeds.addAll(tmpFeeds);
        }catch(Exception e){
            LOG.error("CISManager startup queries failed: ", e);
        }finally{
            if(session!=null) {
                session.close();
            }
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setPubSubClient(PubsubClient pubSubClient) {
        this.pubSubClient = pubSubClient;
        try {
            pubSubClient.addSimpleClasses(Collections
                    .unmodifiableList(Arrays.asList("org.societies.api.schema.activity.MarshaledActivity")));
        } catch (ClassNotFoundException e) {
            LOG.error("Class not found during setting of pubsub: ", e);
        }
    }

    public PubsubClient getPubSubClient() {
        return pubSubClient;
    }

    public ICommManager getCommManager() {
        return commManager;
    }

    public void setCommManager(ICommManager commManager) {
        this.commManager = commManager;
    }
    
    @Override
    public IActivityFeed getRemoteActivityFeedHandler(ICommManager iCommMgr, IIdentity remoteCISid){
    	return new RemoteActivityFeed(iCommMgr,remoteCISid);
    }
    private boolean persistNewFeed(ActivityFeed activityFeed){
        Session session = getSessionFactory().openSession();
        try{
            session.save(activityFeed);
        } catch (Exception e) {
            LOG.error("Error during persisting of new feed: ", e);
            return false;
        }
        return true;
    }
	

}
