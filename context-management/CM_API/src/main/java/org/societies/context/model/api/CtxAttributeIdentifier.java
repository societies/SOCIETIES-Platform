package org.societies.context.model.api;

public class CtxAttributeIdentifier extends CtxIdentifier {
	
	private static final long serialVersionUID = -282171829285239788L;
	
	private CtxEntityIdentifier scope;

	private CtxAttributeIdentifier() {}
	
	public CtxEntityIdentifier getScope() {
		return this.scope;
	}

	/**
	 * 
	 */
	@Override
	public CtxModelType getModelType() {
		return CtxModelType.ATTRIBUTE;
	}
}
