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
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.orchestration.api.ICis;
import org.societies.orchestration.api.ICisProposal;

import java.util.ArrayList;
import java.util.List;

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
	private long lastTime = 0L;
	private SocialGraph graph = new SocialGraph();
	public List<ICisProposal> analyze(List<ICisOwned> cises){
		ArrayList<ICisProposal> ret = new ArrayList<ICisProposal>();
		ArrayList<IActivity> actDiff = new ArrayList<IActivity>();
		String lastTimeStr = Long.toString(lastTime);
		String nowStr = Long.toString(System.currentTimeMillis());
		//1. make a graph of interactions, the weight on the links indicates level of interaction, 0 is none. 
		//2. segment the graph nodes according to weights. suggest
		for(ICisOwned icis : cises){
			actDiff.addAll(icis.getActivityFeed().getActivities(lastTimeStr+" "+nowStr)); //getting the diff.
		}
		//creating the vertices
		//this make take a while the first time..
		for(IActivity act : actDiff){
			if(graph.hasVertex(act.getActor()) == null){
				graph.getVertices().add(new SocialGraphVertex(act.getActor()));
			}
			if(graph.hasVertex(act.getTarget()) == null){
				graph.getVertices().add(new SocialGraphVertex(act.getTarget()));
			}
		}
		//creating the edges..
		//this aswell !
		SocialGraphEdge edge = null; SocialGraphEdge searchEdge = null;
		for(SocialGraphVertex vertex1 : graph.getVertices()){
			for(SocialGraphVertex vertex2 : graph.getVertices()){
				edge = new SocialGraphEdge(vertex1,vertex2);
				searchEdge = graph.hasEdge(edge);
				if(searchEdge == null){
					edge.setWeight(cooperation(vertex1,vertex2,actDiff));
					graph.getEdges().add(edge);
				}else
					searchEdge.addToWeight(cooperation(vertex1,vertex2,actDiff));
			}
		}
		//TADA, the social graph should be created, phew
		//Now for the NP-complete analysis..
		
		return ret;
		
	}
	//activityDiff should be a diff, the social graph should be persistant.
	public double cooperation(SocialGraphVertex member1,SocialGraphVertex member2, List<IActivity> activityDiff){
		double ret = 0;
		for(IActivity act: activityDiff){
			if(contains(member1,act) && contains(member2,act)){
				//add new link (or add weight to an old link)
				ret += 1.0;
			}
				
		}
		
		return ret;
	}
	public boolean contains(SocialGraphVertex participant, IActivity act){
		if(act.getActor().contains(participant.getName()))
			return true;
		if(act.getObject().contains(participant.getName()))
			return true;
		if(act.getTarget().contains(participant.getName()))
			return true;
		return false;
	}
	public void init(){}
	public CPACreationPatterns(){}
}