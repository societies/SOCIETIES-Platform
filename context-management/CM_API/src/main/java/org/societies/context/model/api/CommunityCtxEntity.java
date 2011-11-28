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
 * This class is used to represent community context entities. A
 * <code>CommunityCtxEntity</code> is the core concept upon which the context model is
 * built. It corresponds to an object of the physical or conceptual world. For
 * example an entity could be a person, a device, or a service. The
 * {@link CtxAttribute} class is used in order to describe an entity's
 * properties. Concepts such as the name, the age, and the location of a person
 * are described by different context attributes. Relations that may exist among
 * different entities are described by the {@link CtxAssociation} class.
 * <p>
 * The <code>CtxEntity</code> class provides access to the contained
 * context attributes and the associations this entity is member of.
 * 
 * @see CtxEntityIdentifier
 * @see CtxAttribute
 * @see CtxAssociation
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public class CommunityCtxEntity extends CommunityMemberCtxEntity {
	
	private static final long serialVersionUID = -8564823052068362334L;
	
	public Set<CommunityMemberCtxEntity> members = new HashSet<CommunityMemberCtxEntity>();

	private CommunityCtxEntity() {}

	/**
	 * Returns the members of this CommunityCtxEntity
	 *  
	 * @return
	 */
	public Set<CommunityMemberCtxEntity> getMembers() {
		return new HashSet<CommunityMemberCtxEntity>(this.members);
	}
	
	/**
	 * Add a member to the community.
	 * 
	 * @param member
	 */
	public void addMember(CommunityMemberCtxEntity member) {
		this.members.add(member);
	}

	/**
	 * Remove a member from the community
	 * 
	 * @param member
	 */
	public void removeMember(CommunityMemberCtxEntity member) {
		this.members.remove(member);
	}
}