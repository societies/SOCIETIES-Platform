package org.societies.orchestration.cpa.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
public class SocialGraph implements Collection<SocialGraphVertex> {
	private ArrayList<SocialGraphEdge> edges;
	private ArrayList<SocialGraphVertex> vertices;
	public SocialGraph(){
		edges = new ArrayList<SocialGraphEdge>();
		vertices = new ArrayList<SocialGraphVertex>();
	}
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
	@Override
	public int size(){
		return vertices.size();
	}
	@Override
	public boolean add(SocialGraphVertex e) {
		return vertices.add(e);
	}
	@Override
	public boolean addAll(Collection<? extends SocialGraphVertex> c) {
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
	public Iterator<SocialGraphVertex> iterator() {
		return vertices.iterator();
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
		for(SocialGraphVertex vertex : this.vertices){
			ret.addVertex(vertex);
		}
		for(SocialGraphEdge edge : this.edges){
			ret.addEdge(edge, edge.getFrom(), edge.getTo());
		}
		return ret;
	}
}
