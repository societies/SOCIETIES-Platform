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

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import org.societies.api.context.model.CtxEntityIdentifier;

/**
 * Describe your class here...
 *
 * @author pkosmides
 *
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
					"where attribute.ctxId.type = :attrType"
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
	)
})
@Entity
@org.hibernate.annotations.Entity(
		dynamicUpdate=true
)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("CommunityCtxEntity")
public class CommunityCtxEntityDAO extends CommunityCtxEntityBaseDAO {
	
	private static final long serialVersionUID = -4415679433140566711L;

	CommunityCtxEntityDAO() {
		
		super();
	}
	
	public CommunityCtxEntityDAO(CtxEntityIdentifier ctxId) {
		
		super(ctxId);
	}	

	@Transient
	private Set<CtxEntityIdentifier> communities = new HashSet<CtxEntityIdentifier>();
	
	@Transient
	private Set<CtxEntityIdentifier> members = new HashSet<CtxEntityIdentifier>();


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