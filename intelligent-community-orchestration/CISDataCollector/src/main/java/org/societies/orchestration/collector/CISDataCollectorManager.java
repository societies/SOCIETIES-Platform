/*
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

package org.societies.orchestration.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.internal.orchestration.ICisDataCollector;
import org.societies.api.internal.orchestration.IDataCollectorSubscriber;
import org.societies.api.osgi.event.*;
import org.societies.api.schema.cis.community.Community;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bjørn Magnus Mathisen
 * Date: 16.09.12
 * Time: 12:19
 */
public class CISDataCollectorManager extends EventListener implements ICisDataCollector {

    private static Logger LOG = LoggerFactory
            .getLogger(CISDataCollectorManager.class);

    private ICisManager cisManager;
    private HashMap<String,CISDataCollector> collectors = new HashMap<String,CISDataCollector>();
    private IEventMgr eventMgr;
    private Object mtx = new Object();
    public void init(){
        List<ICisOwned> cisOwnedList = getCisManager().getListOfOwnedCis();
        for(ICisOwned cis : cisOwnedList)
            collectors.put(cis.getCisId(),new CISDataCollector(cis));
        this.eventMgr.subscribeInternalEvent(this,new String[]{EventTypes.CIS_CREATION,EventTypes.CIS_DELETION},null);
    }
    public void destroy(){

    }

    @Override
    public List<?> subscribe(String cisId, IDataCollectorSubscriber subscriber) {
        synchronized (mtx) {
            if(collectors.containsKey(cisId)){
                return collectors.get(cisId).subscribe(subscriber);
            }
            //the following code should rarely run.. (all CISes should be in the list given notification from CISManager)
            ICisOwned cis = getCisManager().getOwnedCis(cisId);
            if(cis==null) return null;
            CISDataCollector newCollector = new CISDataCollector(cis);
            collectors.put(cisId,newCollector);
            return newCollector.subscribe(subscriber);
        }

    }

    public void newCis(String cisId) {
        LOG.info("in newCIS in cis data collector");
        synchronized (mtx){
            if(collectors.containsKey(cisId)) return;
            ICisOwned cis = getCisManager().getOwnedCis(cisId);
            if(cis==null) return;
            collectors.put(cisId,new CISDataCollector(cis));
        }
    }

    public void removalOfCIS(String cisId) {
        if(collectors.containsKey(cisId)){
            collectors.remove(cisId);
            //TODO: should this bit also notify the CPA of the removal, or should this be handled elsewhere?
        }
    }

    public ICisManager getCisManager() {
        return cisManager;
    }

    public void setCisManager(ICisManager cisManager) {
        this.cisManager = cisManager;
    }

    @Override
    public void handleInternalEvent(InternalEvent event) {
        String bareJid = null;
        org.societies.api.schema.cis.community.Community community = null;
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
            this.removalOfCIS(bareJid);
        }
    }

    @Override
    public void handleExternalEvent(CSSEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public IEventMgr getEventMgr() {
        return eventMgr;
    }

    public void setEventMgr(IEventMgr eventMgr) {
        this.eventMgr = eventMgr;
    }
}
