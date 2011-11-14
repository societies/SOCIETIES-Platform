package org.societies.context.model.api;

import java.io.Serializable;

public class CtxAttribute extends CtxModelObject {

	private static final long serialVersionUID = 2885099443175534995L;
	
	private Serializable value;
	private CtxAttributeValueType valueType;
	private CtxQuality quality;

	private CtxAttribute() {}

	/**
	 * 
	 */
	@Override
	public CtxAttributeIdentifier getId() {
		return (CtxAttributeIdentifier) super.getId();
	}

	/**
	 * 
	 * @return
	 */
	public Serializable getValue(){
		return this.value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(Serializable value){
		this.value = value;
	}
	
	/**
	 * 
	 * @return
	 */
	public CtxAttributeValueType getValueType(){
		return this.valueType;
	}
	
	/**
	 * 
	 * @return
	 */
	public CtxQuality getQuality() {
		return this.quality;
	}
}