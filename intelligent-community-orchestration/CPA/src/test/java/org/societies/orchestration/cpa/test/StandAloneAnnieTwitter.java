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

package org.societies.orchestration.cpa.test;

/**
 * Created with IntelliJ IDEA.
 * User: Bjørn Magnus Mathisen
 * Date: 10.10.12
 * Time: 19:11
 */


import gate.*;
import gate.corpora.RepositioningInfo;
import gate.creole.ANNIEConstants;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.Out;
import org.societies.api.activity.IActivity;
import org.societies.orchestration.cpa.test.util.Tweet2011Extractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * This class illustrates how to use ANNIE as a sausage machine
 * in another application - put ingredients in one end (URLs pointing
 * to documents) and get sausages (e.g. Named Entities) out the
 * other end.
 * <P><B>NOTE:</B><BR>
 * For simplicity's sake, we don't do any exception handling.
 */
public class StandAloneAnnieTwitter {

    /** The Corpus Pipeline application to contain ANNIE */
    private SerialAnalyserController annieController;

    /**
     * Initialise the ANNIE system. This creates a "corpus pipeline"
     * application that can be used to run sets of documents through
     * the extraction system.
     */
    public void initAnnie() throws GateException {
        Out.prln("Initialising ANNIE...");

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
    } // initAnnie()

    /** Tell ANNIE's controller about the corpus you want to run on */
    public void setCorpus(Corpus corpus) {
        annieController.setCorpus(corpus);
    } // setCorpus

    /** Run ANNIE */
    public void execute() throws GateException {
        Out.prln("Running ANNIE...");
        annieController.execute();
        Out.prln("...ANNIE complete");
    } // execute()

    /**
     * Run from the command-line, with a list of URLs as argument.
     * <P><B>NOTE:</B><BR>
     * This code will run with all the documents in memory - if you
     * want to unload each from memory after use, add code to store
     * the corpus in a DataStore.
     */
    public static void main(String args[])
            throws GateException, IOException {
        // initialise the GATE library
        Out.prln("Initialising GATE...");
        File f = new File("./src/test/resources");
        Out.println("path:"+f.getAbsolutePath());
        Gate.setPluginsHome(f);
        Gate.setGateHome(new File("./src/test/resources"));
        Gate.init();


        // Load ANNIE plugin
        File gateHome = Gate.getGateHome();
        File pluginsHome = new File("./src/test/resources/", "plugins");
        Gate.getCreoleRegister().registerDirectories(new File(pluginsHome, "/ANNIE").toURL());
        Out.prln("...GATE initialised");

        // initialise ANNIE (this may take several minutes)
        StandAloneAnnieTwitter annie = new StandAloneAnnieTwitter();
        annie.initAnnie();

        // create a GATE corpus and add a document for each command-line
        // argument
        Corpus corpus = (Corpus) Factory.createResource("gate.corpora.CorpusImpl");
        List<IActivity> tweetActs = Tweet2011Extractor.actsFromGzJson(args[0]);
        String wholeContent="";
        for(IActivity act : tweetActs) {
                             wholeContent += act.getObject()+". ";
        } // for each of args
        FeatureMap params = Factory.newFeatureMap();
        params.put("stringContent", wholeContent);
        params.put("preserveOriginalContent", new Boolean(true));
        params.put("collectRepositioningInfo", new Boolean(true));
        Document tdoc = (Document)
                Factory.createResource("gate.corpora.DocumentImpl", params);
        corpus.add(tdoc);
        // tell the pipeline about the corpus and run it
        annie.setCorpus(corpus);
        annie.execute();
        Out.println("adding: \""+wholeContent+"\" to corpus");
        // for each document, get an XML document with the
        // person and location names added
        Iterator iter = corpus.iterator();
        int count = 0;
        String startTagPart_1 = "<span GateID=\"";
        String startTagPart_2 = "\" title=\"";
        String startTagPart_3 = "\" style=\"background:Red;\">";
        String endTag = "</span>";
        for(String s : tdoc.getNamedAnnotationSets().keySet())
        {
            Out.println("annotation key: "+s);
        }
        while(iter.hasNext()) {
            Out.println("new iter..");
            Document doc = (Document) iter.next();
            Map<String, AnnotationSet> res = doc.getNamedAnnotationSets();
            if(res == null )
                continue;
            List<String> tmpNewTerm = null;
            Iterator<Annotation> annotationIterator;
            for(String key : res.keySet()){

                    annotationIterator  = res.get(key).iterator();
                    Annotation cur = null;
                while(annotationIterator.hasNext()){
                    cur = annotationIterator.next();
                    Out.println("cur: "+cur.toString());
                }
            }
            // do something usefull with the XML here!
//      Out.prln("'"+xmlDocument+"'");
        } // for each doc
    } // main

    /**
     *
     */
    public static class SortedAnnotationList extends Vector {
        public SortedAnnotationList() {
            super();
        } // SortedAnnotationList

        public boolean addSortedExclusive(Annotation annot) {
            Annotation currAnot = null;

            // overlapping check
            for (int i=0; i<size(); ++i) {
                currAnot = (Annotation) get(i);
                if(annot.overlaps(currAnot)) {
                    return false;
                } // if
            } // for

            long annotStart = annot.getStartNode().getOffset().longValue();
            long currStart;
            // insert
            for (int i=0; i < size(); ++i) {
                currAnot = (Annotation) get(i);
                currStart = currAnot.getStartNode().getOffset().longValue();
                if(annotStart < currStart) {
                    insertElementAt(annot, i);
                    /*
                    Out.prln("Insert start: "+annotStart+" at position: "+i+" size="+size());
                    Out.prln("Current start: "+currStart);
                    */
                    return true;
                } // if
            } // for

            int size = size();
            insertElementAt(annot, size);
//Out.prln("Insert start: "+annotStart+" at size position: "+size);
            return true;
        } // addSorted
    } // SortedAnnotationList
} // class StandAloneAnnie
