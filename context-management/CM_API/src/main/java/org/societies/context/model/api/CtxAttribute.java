package org.societies.context.model.api;

import java.io.Serializable;

public class CtxAttribute extends CtxModelObject {

	private static final long serialVersionUID = 2885099443175534995L;
	
	private Serializable value;
	private CtxAttributeValueType valueType;
	private String valueMetric;
	private CtxQuality quality;
	private String sourceId;

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
	
	public String getValueMetric() {
		return this.valueMetric;
	}
	
	public void setValueMetric(String valueMetric) {
		this.valueMetric = valueMetric;
	}
	
	/**
	 * Returns the Quality of Context (QoC) information associated to this context
	 * attribute.
	 * 
	 * @return the <code>CtxQuality</code> associated to this context
	 *         attribute.
	 * @see CtxQuality
	 */
	public CtxQuality getQuality() {
		return this.quality;
	}
	
	/**
	 * Returns the identifier of the context source.
	 * 
	 * @return the identifier of the context source.
	 */
	public String getSourceId() {
		return this.sourceId;
	}
	
	/**
	 * Sets the identifier of the context source.
	 * 
	 * @param sourceId
	 *            the identifier of the context source to set.
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
}