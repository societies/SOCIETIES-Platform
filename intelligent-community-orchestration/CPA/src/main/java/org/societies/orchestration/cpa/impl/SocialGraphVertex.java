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

import gate.Annotation;
import gate.AnnotationSet;
import org.societies.api.cis.orchestration.model.ISocialGraphEdge;
import org.societies.api.cis.orchestration.model.ISocialGraphVertex;

import java.util.*;

public class SocialGraphVertex implements ISocialGraphVertex {
    private static final int maxLastActs = 10;
	private String name;
	private ArrayList<ISocialGraphEdge> edges;
    public List<String> trends;
    public Queue<String> lastActs;
    private boolean trend = false;
    private long timestamp; //timestamp of last text added.
    //this will contain TERM -> value, e.g. "Person" -> "John"
    private HashMap<String, List<String>> terms;
	public SocialGraphVertex(){
        init();
	}
	public SocialGraphVertex(String name){
		this.name = name;
        init();
	}
    public void init(){
        edges = new ArrayList<ISocialGraphEdge>();
        trends = new ArrayList<String>();
        lastActs = new LinkedList<String>();
        terms = new HashMap<String, List<String>>();
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ISocialGraphEdge> getEdges() {
		return edges;
	}
	public void setEdges(ArrayList<ISocialGraphEdge> edges) {
		this.edges = edges;
	}
	public String toString(){
		return name;
	}
    public boolean hasTrend(String trend){
        for(String s: lastActs)
            if(s.equals(trend))
                return true;
        return false;
    }
    public void addAct(String act){
        lastActs.add(act);
        if(lastActs.size()>maxLastActs)
            lastActs.poll();
    }

    public boolean isTrend() {
        return trend;
    }

    public void setTrend(boolean trend) {
        this.trend = trend;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public void addTerm(String key, List<String> value){
        terms.put(key,value);
    }
    public List<String> getTerm(String key){
        return terms.get(key);
    }
    public HashMap<String, List<String>> getTerms(){
        return terms;
    }
    public void merge(Map<String, AnnotationSet> annotationSetMap){
        boolean found = false;
        List<String> tmpNewTerm = null;
        Iterator<Annotation> annotationIterator;
        for(String key : annotationSetMap.keySet()){
            if(terms.containsKey(key)){ //the key is present in the cache, check if it conaints the same amount of captured terms..
                found = false;
                annotationIterator  = annotationSetMap.get(key).iterator();
                Annotation cur = null;
                while(annotationIterator.hasNext()){
                    cur = annotationIterator.next();
                    if(!terms.get(key).contains(cur.toString()))
                        terms.get(key).add(cur.toString());
                }
            } else  //the key is not present in cache, add to cache
            {
                tmpNewTerm = new ArrayList<String>();
                annotationIterator = annotationSetMap.get(key).iterator();
                while(annotationIterator.hasNext()){
                    tmpNewTerm.add(annotationIterator.next().toString());
                }
                terms.put(key,tmpNewTerm);
            }
        }
    }
}
