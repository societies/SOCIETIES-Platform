/**
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
package org.societies.orchestration.cpa.impl;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import org.societies.api.activity.IActivity;
import org.societies.api.internal.orchestration.ISocialGraph;
import org.societies.api.internal.orchestration.ISocialGraphEdge;
import org.societies.api.internal.orchestration.ISocialGraphVertex;
import org.societies.orchestration.cpa.impl.comparison.ActorComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
public class SocialGraph implements Collection<ISocialGraphVertex>,ISocialGraph {
	private ArrayList<ISocialGraphEdge> edges;
	private ArrayList<ISocialGraphVertex> vertices;
	public SocialGraph(){
		edges = new ArrayList<ISocialGraphEdge>();
		vertices = new ArrayList<ISocialGraphVertex>();
	}
	public List<ISocialGraphEdge> getEdges() {
		return edges;
	}
	public void setEdges(ArrayList<ISocialGraphEdge> edges) {
		this.edges = edges;
	}
	public List<ISocialGraphVertex> getVertices() {
		return vertices;
	}
	public void setVertices(ArrayList<ISocialGraphVertex> vertices) {
		this.vertices = vertices;
	}
	public SocialGraphVertex hasVertex(String name){
		for(ISocialGraphVertex vertex : vertices)
			if(vertex.getName().equalsIgnoreCase(name))
				return (SocialGraphVertex) vertex;
		return null;
	}
	public SocialGraphEdge hasEdge(SocialGraphEdge iedge){
		for(ISocialGraphEdge edge : edges)
			if(edge.equals(iedge))
				return (SocialGraphEdge) edge;
		return null;
	}
	@Override
	public int size(){
		return vertices.size();
	}
	@Override
	public boolean add(ISocialGraphVertex e) {
		return vertices.add(e);
	}
	@Override
	public boolean addAll(Collection<? extends ISocialGraphVertex> c) {
		return vertices.addAll(c);
	}
	@Override
	public void clear() {
		vertices.clear();
	}
	@Override
	public boolean contains(Object o) {
	
		return vertices.contains(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return vertices.containsAll(c);
	}
	@Override
	public boolean isEmpty() {
		return vertices.isEmpty();
	}
	@Override
	public Iterator<ISocialGraphVertex> iterator() {
/*        ArrayList<SocialGraphVertex> ret = new ArrayList<SocialGraphVertex>();
        ret.addAll(this.getVertices());*/
		return  vertices.iterator();
	}
	@Override
	public boolean remove(Object o) {
		return vertices.remove(o);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return vertices.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return vertices.retainAll(c);
	}
	@Override
	public Object[] toArray() {
		return vertices.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return vertices.toArray(a);
	}
	public UndirectedSparseGraph<SocialGraphVertex,SocialGraphEdge> toJung(){
		UndirectedSparseGraph<SocialGraphVertex,SocialGraphEdge> ret = new UndirectedSparseGraph<SocialGraphVertex,SocialGraphEdge>();
		for(ISocialGraphVertex vertex : this.vertices){
			ret.addVertex((SocialGraphVertex) vertex);
		}
		for(ISocialGraphEdge edge : this.edges){
			ret.addEdge((SocialGraphEdge) edge, (SocialGraphVertex)edge.getFrom(), (SocialGraphVertex)edge.getTo());
		}
		return ret;
	}
	public void populateFromNewData(List<IActivity> actDiff , long lastTime, ActorComparator actComp){

		//creating the vertices
		//this make take a while the first time..


		//actDiff = cis.getActivityFeed().getActivities(lastTimeStr+" "+nowStr);
		for(IActivity act : actDiff){
			if(hasVertex(act.getActor()) == null){
				getVertices().add(new SocialGraphVertex(act.getActor()));
			}
			if(hasVertex(act.getTarget()) == null){
				getVertices().add(new SocialGraphVertex(act.getTarget()));
			}
		}
		//creating the edges..
		//this aswell !
		System.out.println("actDiff size:"+actDiff.size()+" getVertices().size():"+getVertices().size());
		int newEdges=0; int hasEdges=0;
		SocialGraphEdge edge = null; SocialGraphEdge searchEdge = null;
		for(ISocialGraphVertex vertex1 : getVertices()){
			for(ISocialGraphVertex vertex2 : getVertices()){
				edge = new SocialGraphEdge((SocialGraphVertex)vertex1,(SocialGraphVertex)vertex2);
				searchEdge = hasEdge(edge);
				if(searchEdge == null){
					newEdges++;
					edge.setWeight(actComp.compare((SocialGraphVertex)vertex1,(SocialGraphVertex)vertex2,actDiff));
					getEdges().add(edge);
				}else{
					hasEdges++;
					searchEdge.addToWeight(actComp.compare((SocialGraphVertex)vertex1,(SocialGraphVertex)vertex2,actDiff));
				}
			}
		}
		System.out.println("newEdges: "+newEdges);
	}
}
