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

import gate.*;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import gate.creole.ANNIEConstants;
import gate.creole.ExecutionException;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.Out;
import org.societies.api.activity.IActivity;
import org.societies.orchestration.cpa.impl.SocialGraphVertex;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: epic
 * Date: 12/4/12
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContentComparator implements ActorComparator {

    private SerialAnalyserController annieController;
    private URL pluginURL;
    public ContentComparator(){
        try {
            initAnnie();
        } catch (GateException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public void initAnnie() throws GateException {
        Out.prln("Initialising ANNIE...");
        Gate.setPluginsHome(new File("."));
        Gate.init();
        File gateHome = Gate.getGateHome();
        pluginURL =  ContentComparator.class.getClassLoader().getResource("plugins/ANNIE/");
        File pluginsHome = new File(".", "plugins");
/*        try {*/
            Gate.getCreoleRegister().registerDirectories(pluginURL);
            //Gate.getCreoleRegister().registerDirectories(new File(pluginsHome, "/ANNIE").toURL());
/*        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
        Out.prln("...GATE initialised");



        // create a serial analyser controller to run ANNIE with
        annieController =
                (SerialAnalyserController) Factory.createResource(
                        "gate.creole.SerialAnalyserController", Factory.newFeatureMap(),
                        Factory.newFeatureMap(), "ANNIE_" + Gate.genSym()
                );

        // load each PR as defined in ANNIEConstants
        for(int i = 0; i < ANNIEConstants.PR_NAMES.length; i++) {
            FeatureMap params = Factory.newFeatureMap(); // use default parameters
            ProcessingResource pr = (ProcessingResource)
                    Factory.createResource(ANNIEConstants.PR_NAMES[i], params);

            // add the PR to the pipeline controller
            annieController.add(pr);
        } // for each ANNIE PR

        Out.prln("...ANNIE loaded");
    }
    public Corpus makeCorpus(String str) throws GateException {
        DocumentContentImpl content = new DocumentContentImpl(str);
        Document doc = new DocumentImpl();
        doc.setContent(content);
        Corpus corpus = (Corpus) Factory.createResource("gate.corpora.CorpusImpl");
        FeatureMap params = Factory.newFeatureMap();
        params.put("preserveOriginalContent", new Boolean(true));
        params.put("collectRepositioningInfo", new Boolean(true));
        corpus.add(doc);
        return corpus;
    }
    @Override
    public double compare(SocialGraphVertex member1, SocialGraphVertex member2, List<IActivity> activityDiff)  {
        double ret = 0;

        String member1Text = "";  int m1count = 0;
        String member2Text = "";  int m2count = 0;
        for(IActivity act: activityDiff){
            if(contains(member1,act)){
                m1count ++;
                //add new link (or add weight to an old link)
                member1Text += ". "+act.getObject();
            }
            else if( contains(member2,act) ) {
                m2count ++;
                member2Text += ". "+act.getObject();
            }

        }
        if(member1Text.length() == 0 || member1Text.length() == 0)
            return 0;
        System.out.println("comparing two members m1 totaltextlength: "+member1Text.length()+" numberof: "+m1count+ " ratio: "+((double)member1Text.length())/((double)m1count));
        System.out.println("comparing two members m2 totaltextlength: "+member2Text.length()+" numberof: "+m2count+ " ratio: "+((double)member2Text.length())/((double)m2count));
        long start = System.currentTimeMillis();
        Map<String,AnnotationSet> m1annotations = getAnnotations(member1Text);
        long timespent = (System.currentTimeMillis()-start);
        System.out.println("annotating "+member1Text.length()+" time spent: "+timespent+" per char: "+((double)(System.currentTimeMillis()-start))/((double)member1Text.length()));
        start = System.currentTimeMillis();
        Map<String,AnnotationSet> m2annotations = getAnnotations(member2Text);
        timespent = (System.currentTimeMillis()-start);
        System.out.println("annotating "+member2Text.length()+" time spent: "+timespent+" per char: "+((double)(System.currentTimeMillis()-start))/((double)member2Text.length()));

        //check if the two members have talked about the same locations.
        AnnotationSet l1 = m1annotations.get("Location");
        AnnotationSet l2 = m2annotations.get("Location");
        Iterator<Annotation> it = null;
        if(l1 != null && l2!=null){
            it = l1.iterator();
            while(it.hasNext()){
                if(l2.contains(it.next()))
                    ret++;
            }
        }
        //check if the two members have talked about the same persons.
        AnnotationSet p1 = m1annotations.get("Person");
        AnnotationSet p2 = m2annotations.get("Person");
        if(p1!=null && p2!=null){
            it = p1.iterator();
            while(it.hasNext()){
                if(p2.contains(it.next()))
                    ret++;
            }
        }
        //look for ..
        System.out.println("ret: "+ret);
        return ret;
    }
    private Map<String,AnnotationSet> getAnnotations(String str){
        System.out.println("trying to annotate str of size: "+str.length());
        Map<String,AnnotationSet> ret = new HashMap<String,AnnotationSet>();
        Corpus corp = null;
        try {
            corp = this.makeCorpus(str);
        } catch (GateException e) {
            e.printStackTrace();
        }

        annieController.setCorpus(corp);
        try {
            annieController.execute();
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Document doc = null;
        if(corp.size()>0){
            doc = corp.get(0);
            System.out.println("corp size: "+corp.size());
        }else{
            System.out.println("corp size 0 ret:"+ret);
            return ret;
        }
        ret = doc.getNamedAnnotationSets();
        if(ret == null){
            System.out.println("ret is null after getnamedannotation...");
            ret = new HashMap<String, AnnotationSet>();
        }else
            System.out.println("found something!: "+ret.values().iterator().next().toString());
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
