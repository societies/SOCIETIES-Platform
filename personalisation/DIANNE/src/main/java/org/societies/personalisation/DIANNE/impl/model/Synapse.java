package org.societies.personalisation.DIANNE.impl.model;

public class Synapse 
{
	String id;
	ContextNode preNode;
	OutcomeNode postNode;
	double weight;

	public Synapse(String id, ContextNode preNode, OutcomeNode postNode)
	{
		this.id = id;
		this.preNode = preNode;
		this.postNode = postNode;
		weight = 0.0;
	}

	public void updateWeight()
	{
		if(preNode.getActive() && postNode.getActive()) //both on - increment
		{
			weight = weight+1.0;
		}else if(preNode.getActive() && !postNode.getActive()) //context on, outcome off - decrement
		{
			weight = weight-1.0;
		}
	}

	/*
	 * Getter methods
	 */
	public String getId()
	{
		return id;
	}

	public ContextNode getPreNode()
	{
		return preNode;
	}

	public OutcomeNode getPostNode()
	{
		return postNode;
	}

	public double getWeight()
	{
		return weight;
	}

	/*
	 * Setter methods
	 */
	public void setWeight(double weight)
	{
		this.weight = weight;
	}
}
