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
package org.societies.context.user.db.impl.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
@NamedQueries({
	@NamedQuery(
			name = "getCtxEntityIdsByType",
			query = "select entity.ctxId from CtxEntityDAO as entity where entity.ctxId.type = :type"
	),
	@NamedQuery(
			name = "getCtxEntityIdsByIdAndType",
			query = "select entity.ctxId from CtxEntityDAO as entity where entity.ctxId = :entityId and entity.ctxId.type = :type"
	),
	@NamedQuery(
			name = "getCtxEntityIdsByOwnerIdAndType",
			query = "select entity.ctxId from CtxEntityDAO as entity where entity.ctxId.type = :type and entity.ctxId.owner_id = :ownerId"
	),
	@NamedQuery(
			name = "getCtxEntityIdsByOwnerId",
			query = "select entity.ctxId from CtxEntityDAO as entity where entity.ctxId.owner_id = :ownerId"
	),
	@NamedQuery(
			name = "getCtxEntityIdsByAttrType",
			query = "select distinct entity.ctxId from CtxEntityDAO as entity inner join entity.attributes as attribute " +
					"where entity.ctxId.type = :entType " +
					"and attribute.ctxId.type = :attrType"
	),
	@NamedQuery(
			name = "getCtxEntityIdsByAttrStringValue",
			query = "select distinct entity.ctxId from CtxEntityDAO as entity inner join entity.attributes as attribute " +
					"where entity.ctxId.type = :entType " +
					"and attribute.ctxId.type = :attrType " +
					"and attribute.stringValue between :minAttribValue and :maxAttribValue"
	),
	@NamedQuery(
			name = "getCtxEntityIdsByAttrIntegerValue",
			query = "select distinct entity.ctxId from CtxEntityDAO as entity inner join entity.attributes as attribute " +
					"where entity.ctxId.type = :entType " +
					"and attribute.ctxId.type = :attrType " +
					"and attribute.integerValue between :minAttribValue and :maxAttribValue"
	),
	@NamedQuery(
			name = "getCtxEntityIdsByAttrDoubleValue",
			query = "select distinct entity.ctxId from CtxEntityDAO as entity inner join entity.attributes as attribute " +
					"where entity.ctxId.type = :entType " +
					"and attribute.ctxId.type = :attrType " +
					"and attribute.doubleValue between :minAttribValue and :maxAttribValue"
	),
	@NamedQuery(
			name = "getCtxEntityIdsByAttrBinaryValue",
			query = "select distinct entity.ctxId from CtxEntityDAO as entity inner join entity.attributes as attribute " +
					"where entity.ctxId.type = :entType " +
					"and attribute.ctxId.type = :attrType " +
					"and attribute.binaryValue = :minAttribValue"
	)
})
@Entity
@org.hibernate.annotations.Entity(
		dynamicUpdate=true
)
@Table(
		name = "org_societies_context_entities", 
		uniqueConstraints = { @UniqueConstraint(columnNames = {
				"owner_id", "type", "object_number" }) }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("CtxEntity")
public class CtxEntityDAO extends CtxModelObjectDAO {
	
	private static final long serialVersionUID = 4804830819205311983L;

	/** The identifier of this entity. */
	@Columns(columns = {
			@Column(name = "owner_id", nullable = false, updatable = false, length = 127),
			@Column(name = "type", nullable = false, updatable = false, length = 63),
			@Column(name = "object_number", nullable = false, updatable = false)
	})
	@Type(type="org.societies.context.user.db.impl.model.hibernate.CtxEntityIdentifierCompositeType")
	private CtxEntityIdentifier ctxId;
	
	@OneToMany(
			cascade = { CascadeType.ALL },
			fetch = FetchType.EAGER,
			mappedBy="entity",
			orphanRemoval = true,
			targetEntity = CtxAttributeDAO.class
	)
	private Set<CtxAttributeDAO> attributes = new HashSet<CtxAttributeDAO>();
	
	@Transient
	private Set<CtxAssociationIdentifier> associations = new HashSet<CtxAssociationIdentifier>();

	CtxEntityDAO() {
		
		super(null);
	}
	
	public CtxEntityDAO(CtxEntityIdentifier ctxId) {
		
		super(ctxId.toString());
		this.ctxId = ctxId;
	}
	
	/*
	 * @see org.societies.context.user.db.impl.model.CtxModelObjectDAO#getId()
	 */
	@Override
	public CtxEntityIdentifier getId() {
		
		return this.ctxId;
	}
	
	public Set<CtxAttributeDAO> getAttributes() {
		
		return this.attributes;
	}
	
	public void addAttribute(CtxAttributeDAO attribute) {
		
		if (!this.attributes.contains(attribute))
			this.attributes.add(attribute);
		
		if (!this.equals(attribute.getEntity()))
			attribute.setEntity(this);
	}
	
	public Set<CtxAssociationIdentifier> getAssociations() {
		
		return this.associations;
	}
	
	public void setAssociations(Set<CtxAssociationIdentifier> associations) {
		
		this.associations = associations;
	}
}