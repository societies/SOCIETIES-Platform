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
package org.societies.orchestration.cpa.impl.comparison.trident;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import org.societies.activity.model.Activity;
import storm.trident.operation.TridentCollector;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bjornmagnus.mathisen@sintef.no
 * Date: 5/16/13
 * Time: 15:09
 */
public class ActivitySpout implements storm.trident.spout.IBatchSpout {
    private List<Activity> acts=null;
    public ActivitySpout(ArrayList<Activity> startActs){
        acts = new ArrayList<Activity>();
        acts.addAll(startActs);
    }
    public void pushActivities(List<Activity> newActs){
        acts.addAll(newActs);
    }

    @Override
    public void open(Map map, TopologyContext topologyContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void emitBatch(long l, TridentCollector tridentCollector) {
        ArrayList emitList = new ArrayList();
        //make sure emitlist is
        Iterator<Activity> it = acts.iterator();
        while(!acts.isEmpty() && emitList.size()<l){ //TODO: hmm seems to easy
            emitList.add(it.next());
            it.remove();
        }
        tridentCollector.emit(emitList);
    }

    @Override
    public void ack(long l) {
        //TODO: what should this do...?
    }

    @Override
    public void close() {
        //TODO: what should this do...? I think this is moot for my spout
    }

    @Override
    public Map getComponentConfiguration() {
        return new HashMap();
    }

    @Override
    public Fields getOutputFields() {
        //Fields ret = new Fields();
        return new Fields("text");  //To change body of implemented methods use File | Settings | File Templates.
    }
}
