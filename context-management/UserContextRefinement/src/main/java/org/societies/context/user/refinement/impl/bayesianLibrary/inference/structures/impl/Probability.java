package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fran_ko
 *
 */
public class Probability implements Cloneable, Serializable{
	private String[] stateInstances;
	private double prob;
	private static Logger logger = LoggerFactory.getLogger(Probability.class);

	public Probability(String[] states, double p)
	{
		stateInstances = states;
		prob = p;
	}
	
	public String[] getStates(){
		return stateInstances;
	}
	
	public double getProbability(){
		return prob;
	}
	
	public String toString()
	{
		String ergebnis = "";
		for(int i=0; i<stateInstances.length;i++){
			String s = (String)stateInstances[i];
			ergebnis += s + "\t\t";
		}
		ergebnis+="|\t" + prob;
		
		return ergebnis;
	}

	/**
	 * @param d
	 */
	public void multiplyProbability(double d) {
		prob*=d;
		if (prob == Double.NaN) System.err.println("NaN in "+stateInstances);
	}
	
	/**
	 * @param d
	 */
	public void setProbability(double d) {
		prob=d;		
	}
	
	public Object clone(){
		try{
			Probability neu = (Probability)super.clone();
			return neu;
		}
		catch (CloneNotSupportedException e){
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}
	
	public void setStates(String[] input){
		this.stateInstances=input;
	}
}
