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

package org.societies.orchestration.cpa.impl.comparison.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: epic
 * Date: 11/9/12
 * Time: 12:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class TrendStats {
    private int count = 1;
    private int age=0;
    public static int maxAge = 5;
    public static int trendThreshold = 4;
    private boolean trend = false;
    private String trendText;
    private SortedSet<Long> mentions;
    protected static Logger LOG = LoggerFactory.getLogger(TrendStats.class);
    public TrendStats(long timestamp){

        mentions = new TreeSet<Long>();
        mentions.add(timestamp);
    }
    public boolean tooOld(){
        if(!trend && (age-count)>maxAge )   //TODO: test if is this a good trick
            return true;

        age++;
        return false;
    }
    public void increment(Long time)
    {
/*        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date(time));
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        long hour = c.getTime().getTime();
        if(histoGram.containsKey(hour))
            histoGram.put(hour,histoGram.get(hour)+1);
        else
            histoGram.put(hour,1);*/
        count++;
        int before = mentions.size();
        mentions.add(time);
        if(count>=trendThreshold)
            trend = true;
    }
    public int getCount(){
        return count;
    }

    public boolean isTrend() {
        return trend;
    }

    public void setTrend(boolean trend) {
        this.trend = trend;
    }

    public String getTrendText() {
        return trendText;
    }

    public void setTrendText(String trendText) {
        this.trendText = trendText;
    }
    public HashMap<Long,Integer> getHistoGram(){
        //LOG.info("making histogram for trend word: \""+trendText+"\" count: "+count+ " mentions size: "+mentions.size());
        HashMap<Long,Integer> histoGram = new HashMap<Long, Integer>();
        int steps = 10;
        //if this trend is REALLY small return nonsensical histogram
        if(mentions.size() == 0){
            return histoGram;
        }
        else if(mentions.size()==1){
            histoGram.put(mentions.iterator().next().longValue(),1);
            return histoGram;
        }

        //if the trend is small adjust window..
        if(mentions.size()<100 && mentions.size()>9) //too small for 10 steps..
            steps = mentions.size()/3;
        else if(steps<9)
            steps = 1;

        long first = mentions.first();
        long last = mentions.last();
        long interval = (last-first)/steps;
        //LOG.info("making histogram with interval: "+(double)interval/1000.0d+" seconds");
        Iterator<Long> it = mentions.iterator();
        long start = 0 , stop=0;
        Long cur = null;
        for(int i=0;(first+(interval*i))<last && it.hasNext();i++){

            start = (first+(interval*(i)));
            stop = (first+(interval*(i+1)));
            do {
                cur = it.next();
                if(cur >= start
                        && cur < stop ){
                    if(histoGram.containsKey(start))
                        histoGram.put(start,histoGram.get(start)+1);
                    else
                        histoGram.put(start,1);
                }
            } while(it.hasNext() && cur < stop);
        }

        return histoGram;
    }

}
