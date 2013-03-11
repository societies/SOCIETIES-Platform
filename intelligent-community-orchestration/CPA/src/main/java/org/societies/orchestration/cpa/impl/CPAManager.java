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

package org.societies.orchestration.cpa.impl;

import org.societies.api.internal.orchestration.ICPA;
import org.societies.api.internal.orchestration.ICisDataCollector;
import org.societies.api.internal.orchestration.ISocialGraph;
import org.societies.api.osgi.event.*;
import org.societies.api.schema.cis.community.Community;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Bjørn Magnus Mathisen
 * Date: 16.09.12
 * Time: 13:51
 */
public class CPAManager extends EventListener implements ICPA {
    private ConcurrentHashMap<String, CPA> cpaMap;
    private ICisDataCollector collector;
    private IEventMgr eventMgr;
    public CPAManager(){
        cpaMap= new ConcurrentHashMap<String, CPA>();
    }

    public void newCis(String cisId) {
        if(cpaMap.containsKey(cisId)) return;
        CPA newCPA = new CPA(collector,cisId);
        Thread t = new Thread(newCPA);
        t.start();
        cpaMap.put(cisId,newCPA);
    }

    public void removedCis(String cisId) {
        if(!cpaMap.containsKey(cisId)) return;
    }
    public void init(){
        this.eventMgr.subscribeInternalEvent(this,new String[]{EventTypes.CIS_CREATION,EventTypes.CIS_DELETION},null);
    }
    public void destroy(){

    }


    public ICisDataCollector getCollector() {
        return collector;
    }

    public void setCollector(ICisDataCollector collector) {
        this.collector = collector;
    }
    public void sendSuggestion(CommunitySuggestionImpl suggestion){
        InternalEvent event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, "newSuggestion", "org/societies/ico/sca", suggestion);
        try {
            getEventMgr().publishInternalEvent(event);
        } catch (EMSException e) {
            e.printStackTrace();
        }
    }

    public IEventMgr getEventMgr() {
        return eventMgr;
    }

    public void setEventMgr(IEventMgr eventMgr) {
        this.eventMgr = eventMgr;
    }

    @Override
    public void handleInternalEvent(InternalEvent event) {
        org.societies.api.schema.cis.community.Community community = null;
        String bareJid = null;
        if(event.geteventType() == EventTypes.CIS_CREATION){
            if(!event.geteventInfo().getClass().equals(org.societies.api.schema.cis.community.Community.class))
                return;
            community = (Community) event.geteventInfo();
            bareJid = community.getCommunityJid();
            this.newCis(bareJid);
        }else if(event.geteventType() == EventTypes.CIS_DELETION){
            if(!event.geteventInfo().getClass().equals(org.societies.api.schema.cis.community.Community.class))
                return;
            community = (Community) event.geteventInfo();
            bareJid = community.getCommunityJid();
            this.removedCis(bareJid);
        }
    }

    @Override
    public void handleExternalEvent(CSSEvent event) {

    }

    @Override
    public List<String> getTrends(String cisId, int n) {
        //TODO: handle cisId null
        if(!cpaMap.containsKey(cisId))
            return null;
        return cpaMap.get(cisId).getTrends(n);
    }

    @Override
    public ISocialGraph getGraph(String cisId) {
        //TODO: handle cisId null
        if(!cpaMap.containsKey(cisId))
            return null;
        return cpaMap.get(cisId).getCPACreationPatterns().getGraph();
    }
}
