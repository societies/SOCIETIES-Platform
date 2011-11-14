package org.societies.context.model.api;

import java.io.Serializable;

public abstract class CtxIdentifier implements Serializable {

	private static final long serialVersionUID = 3552976823045895472L;
	
	private String type;
	private long objectNumber;

	CtxIdentifier() {}

	public abstract CtxModelType getModelType();

	/**
	 *  
	 * @return
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getObjectNumber() {
		return this.objectNumber;
	}
}