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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.context.community.db.impl.model.hibernate.CtxEntityIdentifierType;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
@NamedQueries({
	@NamedQuery(
			name = "getCommunityCtxAssociationIdsByType",
			query = "select association.ctxId from CommunityCtxAssociationDAO as association where association.ctxId.type = :type"
	),
	@NamedQuery(
			name = "getCommunityCtxAssociationIdsByOwnerIdAndType",
			query = "select association.ctxId from CommunityCtxAssociationDAO as association " + 
					"where association.ctxId.owner_id = :ownerId and association.ctxId.type = :type"
	),
	@NamedQuery(
			name = "getCommunityCtxAssociationIdsByParentEntityId",
			query = "select distinct association.ctxId from CommunityCtxAssociationDAO as association " +
					"where association.parentEntity = :parentEntId"
	),
	@NamedQuery(
			name = "getCommunityCtxAssociationIdsByChildEntityId",
			query = "select distinct association.ctxId from CommunityCtxAssociationDAO as association inner join association.childEntities as childEntity " +
					"where childEntity = :childEntId"
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
	),
	@NamedQuery(
			name = "getCommunityCtxAssociationIdsByChildEntityIdAndType",
			query = "select distinct association.ctxId from CommunityCtxAssociationDAO as association inner join association.childEntities as childEntity " +
					"where childEntity = :childEntId and association.ctxId.type = :type"
	),
	@NamedQuery(
			name = "getCommunityCtxAssociationsByParentEntityIdAndType",
			query = "select distinct association.ctxId from CommunityCtxAssociationDAO as association " +
					"where association.parentEntity = :parentEntId and association.ctxId.type = :type"
	)
})
@Entity
@org.hibernate.annotations.Entity(
		dynamicUpdate=true
)
@Table(
		name = "org_societies_context_community_associations", 
		uniqueConstraints = { @UniqueConstraint(columnNames = {
				"owner_id", "type", "object_number" }) }
)
public class CommunityCtxAssociationDAO extends CtxModelObjectDAO {
	
	private static final long serialVersionUID = -8672603349607617492L;

	/** The identifier of this association. */
	@Columns(columns = {
			@Column(name = "owner_id", nullable = false, updatable = false, length = 127),
			@Column(name = "type", nullable = false, updatable = false, length = 63),
			@Column(name = "object_number", nullable = false, updatable = false)
	})
	@Type(type="org.societies.context.community.db.impl.model.hibernate.CtxAssociationIdentifierCompositeType")
	private CtxAssociationIdentifier ctxId;
	
	@Column(name = "parent_entity", nullable = true, updatable = true)
	@Type(type="org.societies.context.community.db.impl.model.hibernate.CtxEntityIdentifierType")
	private CtxEntityIdentifier parentEntity;
	
	@CollectionOfElements(fetch = FetchType.EAGER, targetElement = CtxEntityIdentifierType.class)
	@JoinTable(name="org_societies_context_community_assoc_entities", joinColumns = @JoinColumn(name="association_id"))
	@Column(name="ctx_ent_id")
	private Set<CtxEntityIdentifier> childEntities = new HashSet<CtxEntityIdentifier>();

	CommunityCtxAssociationDAO() {
		
		super(null);
	}
	
	public CommunityCtxAssociationDAO(CtxAssociationIdentifier ctxId) {
		
		super(ctxId.toString());
		this.ctxId = ctxId;
	}
	
	/*
	 * @see org.societies.context.user.db.impl.model.CtxModelObjectDAO#getId()
	 */
	@Override
	public CtxAssociationIdentifier getId() {
		
		return this.ctxId;
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
	 * Sets the parent entity of this context association.
     * <p>
     * If a <code>null</code> parameter is specified then the current parent
     * entity is unset and this association becomes undirected.
	 */
	public void setParentEntity(CtxEntityIdentifier parentEntity){
		
		this.parentEntity = parentEntity;
	}
	
	public Set<CtxEntityIdentifier> getChildEntities() {
		
		return this.childEntities;
	}
	
	public void setChildEntities(final Set<CtxEntityIdentifier> childEntities) {
		
		this.childEntities = childEntities;
	}
}