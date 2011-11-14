package org.societies.context.model.api;

public class CtxAssociationIdentifier extends CtxIdentifier {

	private static final long serialVersionUID = -7991875953413583564L;

	private CtxAssociationIdentifier() {}

	/**
	 * 
	 */
	@Override
	public CtxModelType getModelType() {
		return CtxModelType.ASSOCIATION;
	}
}
