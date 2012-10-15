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
package org.societies.context.community.db.impl.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
 * @since 0.5
 */
@NamedQueries({
	@NamedQuery(
			name = "getCommunityCtxEntityIdByOwnerId",
			query = "select entity.ctxId from CommunityCtxEntityDAO as entity where entity.ctxId.owner_id = :ownerId"
	),
	@NamedQuery(
			name = "getCommunityCtxEntityIdsByType",
			query = "select entity.ctxId from CommunityCtxEntityDAO as entity where entity.ctxId.type = :type"
	),
	@NamedQuery(
			name = "getCommunityCtxEntityIdsByAttrType",
			query = "select distinct entity.ctxId from CommunityCtxEntityDAO as entity inner join entity.attributes as attribute " +
					"where entity.ctxId.type = :entType " +
					"and attribute.ctxId.type = :attrType"
	),
	@NamedQuery(
			name = "getCommunityCtxEntityIdsByAttrStringValue",
			query = "select distinct entity.ctxId from CommunityCtxEntityDAO as entity inner join entity.attributes as attribute " +
					"where entity.ctxId.type = :entType " +
					"and attribute.ctxId.type = :attrType " +
					"and attribute.stringValue between :minAttribValue and :maxAttribValue"
	),
	@NamedQuery(
			name = "getCommunityCtxEntityIdsByAttrIntegerValue",
			query = "select distinct entity.ctxId from CommunityCtxEntityDAO as entity inner join entity.attributes as attribute " +
					"where entity.ctxId.type = :entType " +
					"and attribute.ctxId.type = :attrType " +
					"and attribute.integerValue between :minAttribValue and :maxAttribValue"
	),
	@NamedQuery(
			name = "getCommunityCtxEntityIdsByAttrDoubleValue",
			query = "select distinct entity.ctxId from CommunityCtxEntityDAO as entity inner join entity.attributes as attribute " +
					"where entity.ctxId.type = :entType " +
					"and attribute.ctxId.type = :attrType " +
					"and attribute.doubleValue between :minAttribValue and :maxAttribValue"
	),
	@NamedQuery(
			name = "getCommunityCtxEntityIdsByAttrBinaryValue",
			query = "select distinct entity.ctxId from CommunityCtxEntityDAO as entity inner join entity.attributes as attribute " +
					"where entity.ctxId.type = :entType " +
					"and attribute.ctxId.type = :attrType " +
					"and attribute.binaryValue = :minAttribValue"
	),
	@NamedQuery(
			name = "getCommunityCtxAssociationsByChildEntityId",
			query = "select distinct association from CommunityCtxAssociationDAO as association inner join association.childEntities as childEntity " +
					"where childEntity = :childEntId"
	),
	@NamedQuery(
			name = "getCommunityCtxAssociationsByParentEntityId",
			query = "select distinct association from CommunityCtxAssociationDAO as association " +
					"where association.parentEntity = :parentEntId"
	)
})
@Entity
@org.hibernate.annotations.Entity(
		dynamicUpdate=true
)
@Table(
		name = "org_societies_context_community_entities", 
		uniqueConstraints = { @UniqueConstraint(columnNames = {
				"owner_id", "type" }) }
)
public class CommunityCtxEntityDAO extends CtxModelObjectDAO {
	
	private static final long serialVersionUID = 4804830819205311983L;

	/** The identifier of this entity. */
	@Columns(columns = {
			@Column(name = "owner_id", nullable = false, updatable = false, length = 127),
			@Column(name = "type", nullable = false, updatable = false, length = 63),
			@Column(name = "object_number", nullable = false, updatable = false)
	})
	@Type(type="org.societies.context.community.db.impl.model.hibernate.CtxEntityIdentifierCompositeType")
	private CtxEntityIdentifier ctxId;
	
	@OneToMany(
			cascade = { CascadeType.ALL },
			fetch = FetchType.EAGER,
			mappedBy="entity",
			orphanRemoval = true,
			targetEntity = CommunityCtxAttributeDAO.class
	)
	private Set<CommunityCtxAttributeDAO> attributes = new HashSet<CommunityCtxAttributeDAO>();
	
	@Transient
	private Set<CtxAssociationIdentifier> associations = new HashSet<CtxAssociationIdentifier>();
	
	@Transient
	private Set<CtxEntityIdentifier> communities = new HashSet<CtxEntityIdentifier>();
	
	@Transient
	private Set<CtxEntityIdentifier> members = new HashSet<CtxEntityIdentifier>();

	CommunityCtxEntityDAO() {
		
		super(null);
	}
	
	public CommunityCtxEntityDAO(CtxEntityIdentifier ctxId) {
		
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
	
	public Set<CommunityCtxAttributeDAO> getAttributes() {
		
		return this.attributes;
	}
	
	public void addAttribute(CommunityCtxAttributeDAO attribute) {
		
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
	
	public Set<CtxEntityIdentifier> getCommunities() {
		
		return this.communities;
	}
	
	public void setCommunities(Set<CtxEntityIdentifier> communities) {
		
		this.communities = communities;
	}
	
	public Set<CtxEntityIdentifier> getMembers() {
		
		return this.members;
	}
	
	public void setMembers(Set<CtxEntityIdentifier> members) {
		
		this.members = members;
	}
}