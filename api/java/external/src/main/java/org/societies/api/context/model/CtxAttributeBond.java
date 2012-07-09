package org.societies.api.context.model;

import java.io.Serializable;
import java.util.Date;

public class CtxAttributeBond extends CtxBond{


	private static final long serialVersionUID = 1L;

	/** The minimum Serializable value of the context bond */
	private Serializable minValue;

	/** The maximum Serializable value of the context bond */
	private Serializable maxValue;


	/**
	 * Constructs a CtxAttributeBond with the specified arguments.
	 * 
	 * @param modelType
	 *          the modelType newly created CtxBond
	 *            
	 * @param type
	 * 			the type newly created CtxBond
	 * 
	 * @param  originType
	 *			the origin type of the newly created CtxBond
	 *            
	 */
	public CtxAttributeBond(String type, CtxBondOriginType originType, CtxAttributeValueType valueType ,Serializable minValue, Serializable  maxValue ) {

		super(CtxModelType.ATTRIBUTE, type, originType);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}


	public Serializable getMinValue(){
		return this.minValue;
	}

	public Serializable getMaxValue(){
		return this.maxValue;
	}

}