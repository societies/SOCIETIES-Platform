package org.societies.personalisation.DIANNE.impl.model;

import java.util.Iterator;

public class OutcomeNode extends Node
{
	double potential;
	double gradient;
	
	public OutcomeNode(int ID, String groupName, String nodeName)
	{
		super(ID, groupName, nodeName);
		potential = 0.0;
	}
	
	public void calculatePotential()
	{
		double tmpPotential = 0.0;
		
		Iterator<Synapse> synapses_it = synapses.iterator();
		while(synapses_it.hasNext())
		{
			Synapse nextSynapse = (Synapse)synapses_it.next();
			if(nextSynapse.getPreNode().getActive())  //the context pre-node is active
			{
				tmpPotential = tmpPotential+nextSynapse.getWeight();
			}
		}
		
		//apply squashing function to tmpPotential
		potential = squash(tmpPotential);
	}
	
	/*
	 * Squashing function method
	 */
	private double squash(double tmpPotential)
	{		
		return gradient*tmpPotential;
	}
	
	/*
	 * Boosting methods
	 */
	public void boost(double boost_value)
	{
		//update synapses by boost_value
		System.out.println("Outcome node "+nodeName+" boosting by "+boost_value);
		double delta_sw = calculateDeltaSW(boost_value);
		System.out.println("Delta_sw = "+delta_sw);
		updateWeights(delta_sw);
	}
	
	private double calculateDeltaSW(double boost_value)
	{
		//divide boost_value by the number of synapses with preNode=on
		int count = 0;
		Iterator<Synapse> synapses_it = synapses.iterator();
		while(synapses_it.hasNext())
		{
			Synapse nextSynapse = (Synapse)synapses_it.next();
			if(nextSynapse.getPreNode().active) //check if preNode is active
			{
				count++;
			}
		}
		System.out.println("no. of active synapses = "+count);
		return boost_value/count;
	}
	
	private void updateWeights(double update_value)
	{
		Iterator<Synapse> synapses_it = synapses.iterator();
		while(synapses_it.hasNext())
		{
			Synapse nextSynapse = (Synapse)synapses_it.next();
			if(nextSynapse.getPreNode().active) //check if preNode is active
			{
				double weight = nextSynapse.getWeight();
				double weight_prime = weight+update_value;
				nextSynapse.setWeight(weight_prime);
			}
		}
	}
	
	/*
	 * Setter methods
	 */
	public void initialiseGradient(double gradient)
	{
		this.gradient = gradient;
	}
	
	public void setGradient(double gradient)
	{
		this.gradient = gradient;
		calculatePotential();
	}
	
	public void setPotential(double potential)
	{
		this.potential = potential;
	}
	
	/*
	 * Getter methods
	 */
	public double getPotential()
	{
		return potential;
	}
}
