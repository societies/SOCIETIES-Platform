package org.societies.privacytrust.trust.api.model;

import java.io.Serializable;

/**
 * This abstract class is used to represent the trustworthiness of TrustedEntities.
 * The DirectTrust, IndirectTrust and UserPerceivedTrust classes are concrete
 * implementations of this class used to model the direct, indirect and user-
 * perceived trust in an entity, respectively.
 */
public abstract class Trust implements Serializable {

	private static final long serialVersionUID = 3965922195661451444L;
	
	private double value;

	/**
	 * 
	 * @return
	 */
	public double getValue(){
		return this.value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(double value){
		this.value = value;
	}
}