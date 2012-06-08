package org.societies.orchestration.cpa.impl;

import java.util.ArrayList;

public class SocialGraphVertex {
	private String name;
	private ArrayList<SocialGraphEdge> edges;
	public SocialGraphVertex(){
		
	}
	public SocialGraphVertex(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<SocialGraphEdge> getEdges() {
		return edges;
	}
	public void setEdges(ArrayList<SocialGraphEdge> edges) {
		this.edges = edges;
	}

}
