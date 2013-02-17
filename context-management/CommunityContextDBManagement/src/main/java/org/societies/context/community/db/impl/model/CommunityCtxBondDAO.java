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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.societies.api.context.model.CtxBond;

/**
 * @author <a href="mailto:pkosmidis@cn.ntua.gr">Pavlos Kosmides</a> (ICCS)
 * @see CtxBond
 * @since 1.0
 */
@NamedQueries({
	@NamedQuery(
			name = "getCtxBondsByCommunityCtxEntityId",
			query = "select ctxbond.bond from CommunityCtxBondDAO as ctxbond where ctxbond.entity = :ctxId"
	),
	@NamedQuery(
			name = "getCommunityCtxEntitysBonds",
			query = "select distinct ctxbond from CommunityCtxBondDAO as ctxbond where ctxbond.entity = :ctxId"
	)
})
@Entity
@org.hibernate.annotations.Entity(
		dynamicUpdate=true
)
@Table(
		name = "org_societies_context_community_bonds",
		uniqueConstraints = { @UniqueConstraint(columnNames = {
						"entity_id", "bond_model_type", "bond_type", "bond_origin_type" }) }
)
public class CommunityCtxBondDAO implements Serializable {

	private static final long serialVersionUID = -1248264471007054358L;

	@Id @GeneratedValue
	private Long id;
	  
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = CommunityCtxEntityBaseDAO.class)
	@JoinColumn(
			name = "entity_id",
			nullable = false,
			updatable = false
	)
	private CommunityCtxEntityBaseDAO entity;
	
	/** The bond of this entity. */
	@Enumerated(EnumType.STRING)
	@Columns(columns = {
			@Column(name = "bond_model_type", nullable = true, updatable = true),
			@Column(name = "bond_type", nullable = true, updatable = true),
			@Column(name = "bond_origin_type", nullable = true, updatable = true)
	})
	@Type(type="org.societies.context.community.db.impl.model.hibernate.CtxBondCompositeType")
	private CtxBond bond;

	public CommunityCtxBondDAO() { }
	
	/**
	 * Constructs a <code>CommunityCtxBondDAO</code> object for the specified community
	 * context entity.
	 * 
	 * @param entity
	 * the community context entity that has this bond.
	 *
	 */
	
	public CommunityCtxBondDAO(CtxBond bond) {
		
		this.bond = bond;
		
	}
	
	public Long getId() {
		
		return this.id;
		
	}
	
	public CommunityCtxEntityBaseDAO getEntity() {
		
		return this.entity;
		
	}

	public void setEntity(CommunityCtxEntityBaseDAO communityCtxEntityBaseDAO) {
		
		this.entity = communityCtxEntityBaseDAO;
		
	}
}