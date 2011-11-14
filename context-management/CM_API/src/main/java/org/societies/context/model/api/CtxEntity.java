package org.societies.context.model.api;

import java.util.Set;

public class CtxEntity extends CtxModelObject {

	private static final long serialVersionUID = -9180016236230471418L;
	
	private Set<CtxAttribute> attributes;
	private Set<CtxAssociationIdentifier> associationIds;

	CtxEntity() {}
	
	/**
	 * 
	 */
	@Override
	public CtxEntityIdentifier getId() {
		return (CtxEntityIdentifier) super.getId();
	}

	/**
	 * 
	 * @return
	 */
	public Set<CtxAttribute> getAttributes(){
		return this.attributes;
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<CtxAssociationIdentifier> getAssociationIds(){
		return this.associationIds;
	}
}