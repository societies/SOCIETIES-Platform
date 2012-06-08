package org.societies.orchestration.cpa.impl;

import java.util.ArrayList;

public class SocialGraph {
	private ArrayList<SocialGraphEdge> edges;
	private ArrayList<SocialGraphVertex> vertices;
	public ArrayList<SocialGraphEdge> getEdges() {
		return edges;
	}
	public void setEdges(ArrayList<SocialGraphEdge> edges) {
		this.edges = edges;
	}
	public ArrayList<SocialGraphVertex> getVertices() {
		return vertices;
	}
	public void setVertices(ArrayList<SocialGraphVertex> vertices) {
		this.vertices = vertices;
	}
	public SocialGraphVertex hasVertex(String name){
		for(SocialGraphVertex vertex : vertices)
			if(vertex.getName().equalsIgnoreCase(name))
				return vertex;
		return null;
	}
	public SocialGraphEdge hasEdge(SocialGraphEdge iedge){
		for(SocialGraphEdge edge : edges)
			if(edge.equals(iedge))
				return edge;
		return null;
	}
}
