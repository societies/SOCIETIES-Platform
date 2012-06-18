package org.societies.orchestration.cpa.impl;

public class SocialGraphEdge {
	private double weight;
	private SocialGraphVertex from;
	private SocialGraphVertex to;
	public SocialGraphEdge(){
		weight = 0;
		from = null; to = null;
	}
	public SocialGraphEdge(SocialGraphVertex from, SocialGraphVertex to){
		this.from = from; this.to = to;
	}
	public SocialGraphEdge(SocialGraphVertex from, SocialGraphVertex to, double weight){
		this.weight = weight;
		this.from = from; this.to = to;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public SocialGraphVertex getFrom() {
		return from;
	}
	public void setFrom(SocialGraphVertex from) {
		this.from = from;
	}
	public SocialGraphVertex getTo() {
		return to;
	}
	public void setTo(SocialGraphVertex to) {
		this.to = to;
	}
	@Override
	public boolean equals(Object o){
		if(!o.getClass().equals(SocialGraphEdge.class))
			return false;
		SocialGraphEdge e = (SocialGraphEdge)o;
		long l = e.getFrom().hashCode()+e.getTo().hashCode();
		long l2 = this.getFrom().hashCode()+this.getTo().hashCode();
		return l==l2;
	}
	public void addToWeight(double dw){
		weight += dw;
	}
}
