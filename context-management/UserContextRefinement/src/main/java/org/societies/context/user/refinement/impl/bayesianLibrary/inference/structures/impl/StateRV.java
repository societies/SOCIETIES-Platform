package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl;


/**
 * Represents the state of a random variable. A state will have a numerical ID and a Name.
 * This numerical ID can be used for the index of a confusion matrix or the identification of the state.
 * @author vera_ma
 *
 */
public class StateRV{

	private int iDState;
	private String nameState;
	
	/**
	 * Creates a new StateRV. 
	 */
	public StateRV(){
		
	}
	
	/**
	 * Constructor where the numerical ID and name are set. 
	 * @param id
	 * @param name
	 */
	public StateRV(int id, String name){
		this.iDState = id;
		this.nameState = name;
	}
	
	/**
	 * Set the ID and name of the state.
	 * @param id: numerical ID.
	 * @param name: name of the state.
	 */
	public void setIDName(int id,String name){
		this.iDState = id;
		this.nameState = name;
	}
	
	/**
	 * Returns the ID of the state.
	 * @return int
	 */
	public int getIDState() {
		return this.iDState;
	}

	/**
	 * Returns the name of the state.
	 * @return String
	 */
	public String getNameState() {
		return this.nameState;
	}

	/**
	 * Set the numerical ID of the state.
	 */
	public void setIDState(int id) {
		this.iDState = id;
	}

	/**
	 * Set the name of the state.
	 * @param n
	 */
	public void setNameState(String n) {
		this.nameState = n;
	}
	
	
}
