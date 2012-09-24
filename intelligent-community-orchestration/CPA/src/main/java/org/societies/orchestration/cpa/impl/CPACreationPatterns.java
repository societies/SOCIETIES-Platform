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

package org.societies.orchestration.cpa.impl;

import org.societies.api.activity.IActivity;
import org.societies.api.internal.orchestration.ICommunitySuggestion;
import org.societies.orchestration.api.impl.CommunitySuggestionImpl;
import org.societies.orchestration.cpa.impl.comparison.ActorComparator;
import org.societies.orchestration.cpa.impl.comparison.SimpleCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This
 * 
 * 
 * @author Bjørn Magnus Mathisen, based on work from Fraser Blackmun
 * @version 0
 * 
 */

public class CPACreationPatterns
{
	public SocialGraph getGraph() {
		return graph;
	}
	public void setGraph(SocialGraph graph) {
		this.graph = graph;
	}
	private long lastTime = 0L;
	private SocialGraph graph = new SocialGraph();
	private int numEdgesToRemove = 4;
	private GraphAnalyser analyser;
	private static final String JUNGBETWEENNESS = "jungbetweenness";
	private Set<Set<SocialGraphVertex>> clusterSets ;
	public CPACreationPatterns(){}
	public void init(){
		//TODO: read config
		String analyserprop = JUNGBETWEENNESS;
		if(analyserprop.contains(JUNGBETWEENNESS)){
			analyser = new JungBetweennessAnalyser(5);
		}
	}
	private ActorComparator actComp = null;
	public List<ICommunitySuggestion> analyze(List<IActivity> actDiff){
		ArrayList<ICommunitySuggestion> ret = new ArrayList<ICommunitySuggestion>();
		// = new ArrayList<IActivity>();
		String lastTimeStr = Long.toString(lastTime);
		String nowStr = Long.toString(System.currentTimeMillis());


		actComp = new SimpleCounter();
		//1. make a graph of interactions, the weight on the links indicates level of interaction, 0 is none. 
		graph.populateFromNewData(actDiff, lastTime, actComp);
		//2. segment the graph nodes according to weights. suggest
		
		//TADA, the social graph should be created, phew
		//Now for the analysis..
		clusterSets = analyser.cluster(graph);
		for(Set<SocialGraphVertex> set : clusterSets){
            CommunitySuggestionImpl prop = new CommunitySuggestionImpl();
			for(SocialGraphVertex member : set){
				prop.addMember(member.getName());//TODO: role?
			}
			ret.add(prop);
		}
		return ret;
		
	}
	//activityDiff should be a diff, the social graph should be persistant.
//	public double cooperation(SocialGraphVertex member1,SocialGraphVertex member2, List<IActivity> activityDiff){
//
//	}
	public Set<Set<SocialGraphVertex>> getClusterSets() {
		return clusterSets;
	}
	public void setClusterSets(Set<Set<SocialGraphVertex>> clusterSets) {
		this.clusterSets = clusterSets;
	}

}