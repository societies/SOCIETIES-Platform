/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.context.model.api;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to represent context associations.
 * 
 * @see CtxAssociationIdentifier
 * @see CtxEntity
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @version 0.0.1
 */
public class CtxAssociation extends CtxModelObject {

	private static final long serialVersionUID = 4837712964619525572L;
	
	public CtxEntityIdentifier parentEntity;
	public Set<CtxEntityIdentifier> entities = new HashSet<CtxEntityIdentifier>();

	CtxAssociation() {}

	/**
	 * Returns the identifier of this context association.
	 * 
	 * @see CtxAssociationIdentifier
	 */
	@Override
	public CtxAssociationIdentifier getId() {
		return (CtxAssociationIdentifier) super.getId();
	}

	/**
	 * Returns the parent entity of this context association or
     * <code>null</code> to indicate an undirected association.
     * 
     * @return the parent entity of this context association
     * @see CtxEntity
	 */
	public CtxEntityIdentifier getParentEntity() {
		return this.parentEntity;
	}
	
	/**
	 * Sets the parent entity of this context association. The method also adds
     * the specified context entity to this association if it is not already a
     * member. 
     * <p>
     * If a <code>null</code> parameter is specified then the current
     * parent entity is unset but not removed from this association. Effectively,
     * this association becomes undirected.
     * 
	 * @param parentEntity
	 *            the identifier of the context entity to set as parent
	 */
	public void setParentEntity(CtxEntityIdentifier parentEntityId){
		this.parentEntity = parentEntityId;
		if (parentEntityId != null)
			this.entities.add(parentEntityId);
	}
	
	/**
	 * Returns a set containing the entities in this context association. The method
	 * returns an <i>empty</i> set if this association contains no context entities.
	 *
	 * @return a set containing the entities in this context association
	 * @see #getEntities(String)
	 */
	public Set<CtxEntityIdentifier> getEntities() {
		return this.getEntities(null);
	}
	
	/**
	 * Returns a set containing the entities in this context association with the
	 * specified type. The method returns an <i>empty</i> set if this association
	 * contains no context entities with the specified type.
     * <p>
	 * Note that the method is equivalent to {@link #getEntities()} if the
	 * specified context entity type is <code>null</code>, i.e. all
	 * entities contained in this association are returned.
	 *  
     * @param type
     *            the context entity type to match
     * @return the associated context entities with the specified type
     * @see #getEntities() 
	 */
	public Set<CtxEntityIdentifier> getEntities(String type) {
		final Set<CtxEntityIdentifier> result = new HashSet<CtxEntityIdentifier>();
		
		if (type == null) {
			result.addAll(this.entities);
		} else {
			for (final CtxEntityIdentifier entity : this.entities)
				if (type.equalsIgnoreCase(entity.getType()))
					result.add(entity);
		}
		return result;
	}
	
	/**
	 * Adds the specified entity to this context association
     * 
     * @param entityId
     *            the identifier of the context entity to add
     * @throws NullPointerException
     *             if the specified context entity identifier is
     *             <code>null</code>
     * @see CtxEntityIdentifier
	 */
	public void addEntity(CtxEntityIdentifier entityId) {
		if (entityId == null)
			throw new NullPointerException("entityId can't be null");
		
		this.entities.add(entityId);
	}

	/**
	 * Removes the specified entity from this context association. As a side
     * effect, if the entity to be removed is parent in this association then
     * it is set to <code>null</code> after this operation.
     * 
     * @param entityId
     *            the identifier of the context entity to remove
     * @throws NullPointerException
     *             if the specified context entity identifier is
     *             <code>null</code>
     * @see CtxEntityIdentifier
	 */
	public void removeEntity(CtxEntityIdentifier entityId) {
		if (entityId == null)
			throw new NullPointerException("entityId can't be null");
		
		this.entities.remove(entityId);
	}
	
	/* TODO
	@Override
	public String toString() {
	}*/
}