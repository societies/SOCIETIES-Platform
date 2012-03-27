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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to represent context entities. A
 * <code>CtxEntityBean</code> is the core concept upon which the context model is
 * built. It corresponds to an object of the physical or conceptual world. For
 * example an entity could be a person, a device, or a service. The
 * {@link CtxAttributeBean} class is used in order to describe an entity's
 * properties. Concepts such as the name, the age, and the location of a person
 * are described by different context attributes. Relations that may exist among
 * different entities are described by the {@link CtxAssociationBean} class.
 * <p>
 * The <code>CtxEntityBean</code> class provides access to the contained
 * context attributes and the associations this entity is member of.
 * 
 * @see CtxEntityIdentifierBean
 * @see CtxAttributeBean
 * @see CtxAssociationBean
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @version 0.0.1
 */
@XmlType(namespace="http://societies.org/api/schema/context/model", propOrder = {"attributes", "associations"})
@XmlAccessorType(XmlAccessType.FIELD)
public class CtxEntityBean extends CtxModelObjectBean {

	private static final long serialVersionUID = -9180016236230471418L;
	
	@XmlElement(required = true, nillable=false)
	private Set<CtxAttributeBean> attributes = new HashSet<CtxAttributeBean>();
	
	@XmlElement(required = true, nillable=false)
	private Set<CtxAssociationIdentifierBean> associations = new HashSet<CtxAssociationIdentifierBean>();

	CtxEntityBean() {}
	
	/**
	 * Constructs a CtxEntityBean with the specified identifier
	 * 
	 * @param id
	 *            the identifier of the newly created context entity
	 */
	public CtxEntityBean(CtxEntityIdentifierBean id) {
		
		super(id);
	}
	
	/**
	 * Returns the identifier of this context entity.
	 * @see CtxEntityIdentifierBean
	 */
	@Override
	public CtxEntityIdentifierBean getId() {
		
		return (CtxEntityIdentifierBean) super.getId();
	}

	/**
	 * Returns a set of the context attributes contained in this entity. The
     * method returns an <i>empty</i> set if this entity contains no context
     * attributes.
     * 
     * @return a set of the context attributes contained in this entity.
     * @see CtxAttributeBean
     * @see #getAttributes(String)
	 */
	public Set<CtxAttributeBean> getAttributes(){
		
		return this.getAttributes(null);
	}
	
	/**
	 * Returns a set of the context attributes contained in this entity that
	 * have the specified type. The method returns an <i>empty</i> set if this
	 * entity contains no context attributes with the specified type.
	 * <p>
	 * Note that the method is equivalent to {@link #getAttributes()} if the
	 * specified context attribute type is <code>null</code>, i.e. all
	 * attributes contained in this entity are returned.
	 * 
	 * @param type
	 *            the context attribute type to match.
	 * @return a set of the context attributes contained in this entity that
	 *         have the specified type.
	 * @see CtxAttributeBean
	 * @see #getAttributes()
	 */
	public Set<CtxAttributeBean> getAttributes(String type) {
		
		final Set<CtxAttributeBean> result = new HashSet<CtxAttributeBean>();
		
		if (type == null) {
			result.addAll(this.attributes);
		} else {
			for (final CtxAttributeBean attr : this.attributes)
				if (type.equalsIgnoreCase(attr.getType()))
						result.add(attr);
		}
		return result;
	}
	
	/**
	 * Returns a set containing all association identifiers this entity is
	 * member of. The method returns an <i>empty</i> set if this entity is not
	 * member of any association.
	 * 
	 * @return a set containing all association identifiers this entity is member of
	 * @see CtxAssociationIdentifierBean
	 * @see #getAssociations(String)
	 */
	public Set<CtxAssociationIdentifierBean> getAssociations() {
		
		return this.getAssociations(null);
	}
	
	/**
	 * Returns a set containing all association identifiers of the specified
	 * type this entity is member of. The method returns an <i>empty</i> set if
	 * this entity is not member of any association with the specified type.
	 * <p>
	 * Note that the method is equivalent to {@link #getAssociations()} if the
	 * specified context association type is <code>null</code>, i.e. all
	 * associations this entity is member of are returned.
	 * @param type
	 *            the context association type to match
	 * @return a set containing all associations of the specified type this
	 *         entity is member of
	 * @see CtxAssociationIdentifierBean
	 * @see #getAssociations()
	 */
	public Set<CtxAssociationIdentifierBean> getAssociations(String type) {
		
		final Set<CtxAssociationIdentifierBean> result = new HashSet<CtxAssociationIdentifierBean>();
		
		if (type == null) {
			result.addAll(this.associations);
		} else {
			for (final CtxAssociationIdentifierBean assoc : this.associations)
				if (type.equalsIgnoreCase(assoc.getType()))
						result.add(assoc);
		}
		return result;
	}
	
	/**
	 * TO BE REMOVED DO NOT USE
	 */
	@Deprecated
	public void addAttribute(CtxAttributeBean attribute) {
        attributes.add(attribute);
    }

	/* TODO
	@Override
	public String toString() {
	}*/
}