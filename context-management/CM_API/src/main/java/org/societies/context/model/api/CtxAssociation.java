package org.societies.context.model.api;

import java.util.HashSet;
import java.util.Set;

public class CtxAssociation extends CtxModelObject {

	private static final long serialVersionUID = 4837712964619525572L;
	
	public CtxEntityIdentifier parentEntity;
	public Set<CtxEntityIdentifier> entities = new HashSet<CtxEntityIdentifier>();

	private CtxAssociation() {}

	/**
	 * 
	 */
	@Override
	public CtxAssociationIdentifier getId() {
		return (CtxAssociationIdentifier) super.getId();
	}

	/**
	 * 
	 * @return
	 */
	public CtxEntityIdentifier getParentEntity() {
		return this.parentEntity;
	}
	
	/**
	 * 
	 * @param parentEntity
	 */
	public void setParentEntity(CtxEntityIdentifier parentEntityId){
		this.parentEntity = parentEntityId;
		if (parentEntityId != null)
			this.entities.add(parentEntityId);
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<CtxEntityIdentifier> getEntities(){
		return this.entities;
	}
	
	/**
	 * 
	 * @param entityId
	 */
	public void addEntity(CtxEntityIdentifier entityId){
		this.entities.add(entityId);
	}

	/**
	 * 
	 * @param entityId
	 */
	public void removeEntity(CtxEntityIdentifier entityId){
		this.entities.remove(entityId);
	}
}