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

package org.societies.orchestration.cpa.impl.comparison;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import gate.*;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import gate.creole.ANNIEConstants;
import gate.creole.ExecutionException;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.Out;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.orchestration.cpa.impl.SocialGraphVertex;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User:  bjornmagnus.mathisen@sintef.no
 * Date: 12/5/12
 * Time: 1:32 PM
 */
public class ContentComparator implements ActorComparator {

    private URL pluginURL;
    private int annotationsDone = 0;
    private int fruitfulannotationsDone = 0;
    private static MaxentTagger tagger = new MaxentTagger(
            "english-left3words-distsim.tagger");
    private static Logger LOG = LoggerFactory
            .getLogger(ContentComparator.class);

    public ContentComparator(){
            initPOS();
    }
    public void initPOS(){

    }
    @Override
    public double compare(SocialGraphVertex member1, SocialGraphVertex member2, List<IActivity> activityDiff)  {
        double ret = 0;

        String member1Text = "";
        String member2Text = "";
        long m1lastTimeStamp = 0;
        long m2lastTimeStamp = 0;
        for(IActivity act: activityDiff){
            if(contains(member1,act)){
                //add new link (or add weight to an old link)
                member1Text += ". "+act.getObject();
                if(Long.parseLong(act.getPublished())>m1lastTimeStamp)
                    m1lastTimeStamp = Long.parseLong(act.getPublished());
            }
            if( contains(member2,act) ) {
                member2Text += ". "+act.getObject();
                if(Long.parseLong(act.getPublished())>m2lastTimeStamp)
                    m2lastTimeStamp = Long.parseLong(act.getPublished());
            }

        }
        if(member1Text.length() == 0 || member1Text.length() == 0)
            return 0;

        Map<String, Integer> m1annotations = null;
        long start=0,timespent=0;
        if(m1lastTimeStamp>member1.getTimestamp()){
            member1.merge(getAnnotations(member1Text)); //cache the extraction
            member1.setTimestamp(m1lastTimeStamp);
            m1annotations = member1.getTerms();
            annotationsDone++;


        } else {
            m1annotations = member1.getTerms();
        }

        Map<String, Integer> m2annotations = null;
        if(m2lastTimeStamp>member2.getTimestamp()){
            member2.merge(getAnnotations(member2Text)); //cache the extraction
            member2.setTimestamp(m2lastTimeStamp);
            m2annotations = member2.getTerms();
            annotationsDone++;
        } else {
            m2annotations = member2.getTerms();
        }
        int m1count = 0, m2count=0 , jointcount=0;
        for(String key : m1annotations.keySet()){
            if(m2annotations.containsKey(key)){
                m1count = m1annotations.get(key); m2count = m2annotations.get(key);
                if(m1count>m2count)
                    ret += m2count;
                else
                    ret += m1count;
            }
        }
        //look for ..
        return ret;
    }
    public Map<String,Integer> getAnnotations(String inp){
        Map<String,Integer> ret = new HashMap<String,Integer>();
        HashSet<String> hashSet = new HashSet<String>();
        String deUrlifyTmp[] = inp.split(" ");
        inp = "";
        for(String s : deUrlifyTmp){
            if(s.contains("http")){
                hashSet.add(s);
            }else{
                inp += " "+s;
            }
        }
        inp = inp.trim();
        inp = inp.replaceAll("\'", "");
        String sample = inp.replaceAll("\\W", " ");
        if(sample.trim().length() == 0 ) //just whitespaces.
            return ret;
        // The tagged string
        String tagged = null;
        try{
            tagged = tagger.tagTokenizedString(sample);
        }catch (IndexOutOfBoundsException e){
            LOG.error("IndexOutOfBoundsException on sample : \""+sample+"\"",e);
        }
        if(tagged == null)
            return ret;

        // Output the result
        String[] x = tagged.split(" ");

        ArrayList<String> list = new ArrayList<String>();

        for(int i=0; i<x.length; i++)
        {
            if (x[i].substring(x[i].lastIndexOf("_")+1).startsWith("N"))
            {
                if(x[i].split("_")[0].length()!=0)
                    list.add(x[i].split("_")[0]);
            }
        }
        for(String keyWord : list){
            if(ret.containsKey(keyWord)){
                ret.put(keyWord,ret.get(keyWord)+1);
            } else
                ret.put(keyWord,1);
        }
        return ret;
    }
    public boolean contains(SocialGraphVertex participant, IActivity act){
        if(act.getActor()!=null && act.getActor().contains(participant.getName()))
            return true;
        if(act.getObject()!=null &&  act.getObject().contains(participant.getName()))
            return true;
        if(act.getTarget()!=null && act.getTarget().contains(participant.getName()))
            return true;
        return false;
    }
}
