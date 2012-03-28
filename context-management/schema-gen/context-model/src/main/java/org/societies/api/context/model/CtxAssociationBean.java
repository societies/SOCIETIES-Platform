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
package org.societies.api.context.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to represent context associations.
 * 
 * @see CtxAssociationIdentifierBean
 * @see CtxEntityBean
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @version 0.0.1
 */
@XmlType(namespace="http://societies.org/api/schema/context/model", propOrder = {"parentEntity", "childEntities"})
@XmlAccessorType(XmlAccessType.FIELD)
public class CtxAssociationBean extends CtxModelObjectBean {

	private static final long serialVersionUID = 4837712964619525572L;
	
	@XmlElement(required = true, nillable=true)
	public CtxEntityIdentifierBean parentEntity;
	
	@XmlElement(required = true, nillable=false)
	public /* final */ Set<CtxEntityIdentifierBean> childEntities = new HashSet<CtxEntityIdentifierBean>();

	CtxAssociationBean() {}
	
	/**
	 * Constructs a CtxAssociationBean with the specified identifier
	 * 
	 * @param id
	 *            the identifier of the newly created cotnext association
	 */
	public CtxAssociationBean(CtxAssociationIdentifierBean id) {
		
		super(id);
	}

	/**
	 * Returns the identifier of this context association.
	 * 
	 * @see CtxAssociationIdentifierBean
	 */
	@Override
	public CtxAssociationIdentifierBean getId() {
		
		return (CtxAssociationIdentifierBean) super.getId();
	}

	/**
	 * Returns the parent entity of this context association or
     * <code>null</code> to indicate an undirected association.
     * 
     * @return the parent entity of this context association
     * @see CtxEntityBean
	 */
	public CtxEntityIdentifierBean getParentEntity() {
		
		return this.parentEntity;
	}
	
	/**
	 * Sets the parent entity of this context association.
     * <p>
     * If a <code>null</code> parameter is specified then the current parent
     * entity is unset and this association becomes undirected.
	 */
	public void setParentEntity(CtxEntityIdentifierBean parentEntity){
		
		this.parentEntity = parentEntity;
	}
	
	/**
	 * Returns a set containing the child entities in this context association.
	 * The method returns an <i>empty</i> set if this association contains no
	 * child entities.
	 *
	 * @return a set containing the child entities in this context association
	 * @see #getEntities(String)
	 */
	public Set<CtxEntityIdentifierBean> getChildEntities() {
		
		return this.getChildEntities(null);
	}
	
	/**
	 * Returns a set containing the child entities in this context association
	 * with the specified type. The method returns an <i>empty</i> set if this
	 * association contains no child entities with the specified type.
     * <p>
	 * Note that the method is equivalent to {@link #getEntities()} if the
	 * specified context entity type is <code>null</code>, i.e. all child
	 * entities contained in this association are returned.
	 *  
     * @param type
     *            the context entity type to match
     * @return the associated context entities with the specified type
     * @see #getEntities() 
	 */
	public Set<CtxEntityIdentifierBean> getChildEntities(String type) {
		
		final Set<CtxEntityIdentifierBean> result = new HashSet<CtxEntityIdentifierBean>();
		
		if (type == null) {
			result.addAll(this.childEntities);
		} else {
			for (final CtxEntityIdentifierBean entity : this.childEntities)
				if (type.equalsIgnoreCase(entity.getType()))
					result.add(entity);
		}
		return result;
	}
	
	/**
	 * Adds the specified entity to this context association.
     * 
     * @param childEntity
     *            the identifier of the child entity to add
     * @throws NullPointerException
     *             if the specified context entity identifier is
     *             <code>null</code>
     * @see CtxEntityIdentifierBean
	 */
	public void addChildEntity(CtxEntityIdentifierBean childEntity) {
		
		if (childEntity == null)
			throw new NullPointerException("childEntity can't be null");
		
		this.childEntities.add(childEntity);
	}

	/**
	 * Removes the specified entity from this context association.
     * 
     * @param childEntity
     *            the identifier of the child entity to remove
     * @throws NullPointerException
     *             if the specified context entity identifier is
     *             <code>null</code>
     * @see CtxEntityIdentifierBean
	 */
	public void removeChildEntity(CtxEntityIdentifierBean childEntity) {
		
		if (childEntity == null)
			throw new NullPointerException("childEntity can't be null");
		
		this.childEntities.remove(childEntity);
	}
	
	/* TODO
	@Override
	public String toString() {
	}*/
}