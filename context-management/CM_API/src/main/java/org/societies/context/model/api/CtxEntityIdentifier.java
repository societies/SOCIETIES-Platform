package org.societies.context.model.api;

public class CtxEntityIdentifier extends CtxIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1550923933016203797L;

	private CtxEntityIdentifier() {}

	/**
	 * 
	 */
	@Override
	public CtxModelType getModelType() {
		return CtxModelType.ENTITY;
	}
}
