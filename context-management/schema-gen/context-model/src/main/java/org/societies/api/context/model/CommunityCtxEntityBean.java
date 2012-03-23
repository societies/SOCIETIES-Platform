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
 * This class is used to represent community context entities. A
 * <code>CommunityCtxEntityBean</code> corresponds to a pervasive community (CIS)
 * and is associated with a set of {@link CommunityMemberCtxEntityBean} objects
 * representing its members, i.e. {@link IndividualCtxEntityBean individuals}
 * and/or other {@link CommunityCtxEntityBean communities}. The {@link CtxAttributeBean}
 * class is used in order to describe the community context attributes of a CIS.
 * Common context characteristics shared among community members are described
 * by the {@link CtxBondBean} class. 
 * 
 * @see CtxEntityIdentifierBean
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@XmlType(namespace="http://societies.org/api/schema/context/model", propOrder = {"members", "bonds"})
@XmlAccessorType(XmlAccessType.FIELD)
public class CommunityCtxEntityBean extends CommunityMemberCtxEntityBean {
	
	private static final long serialVersionUID = -8564823052068362334L;
	
	/** The members of this CIS. */
	@XmlElement(required = true, nillable=false)
	private Set<CommunityMemberCtxEntityBean> members = new HashSet<CommunityMemberCtxEntityBean>();
	
	/** The context bond of this CIS. */
	@XmlElement(required = true, nillable=false)
	private Set<CtxBondBean> bonds = new HashSet<CtxBondBean>();

	CommunityCtxEntityBean() {}
	
	public CommunityCtxEntityBean(CtxEntityIdentifierBean id) {
		
		super(id);
	}

	/**
	 * Returns the members of this CIS.
	 *  
	 * @return a set containing the members of this CIS.
	 */
	public Set<CommunityMemberCtxEntityBean> getMembers() {
		return new HashSet<CommunityMemberCtxEntityBean>(this.members);
	}
	
	/**
	 * Adds a member to this CIS.
	 * 
	 * @param member
	 *            the new member of this CIS
	 * @throws NullPointerException if the specified member is <code>null</code>
	 */
	public void addMember(CommunityMemberCtxEntityBean member) {
		
		if (member == null)
			throw new NullPointerException("member can't be null");
		
		this.members.add(member);
	}

	/**
	 * Remove a member from the community
	 * 
	 * @param member
	 *            the member to remove from this CIS
	 * @throws NullPointerException if the specified member is <code>null</code>
	 */
	public void removeMember(CommunityMemberCtxEntityBean member) {
		
		if (member == null)
			throw new NullPointerException("member can't be null");
		
		this.members.remove(member);
	}
	
	/**
	 * Returns the context bonds of this CIS.
	 *  
	 * @return a set containing the context bonds of this CIS.
	 */
	public Set<CtxBondBean> getBonds() {
		
		return new HashSet<CtxBondBean>(this.bonds);
	}
	
	/**
	 * Adds a context bond for this CIS.
	 * 
	 * @param bond
	 *            the context bond to add
	 * @throws NullPointerException if the specified context bond is <code>null</code>
	 */
	public void addBond(CtxBondBean bond) {
		
		if (bond == null)
			throw new NullPointerException("bond can't be null");
		
		this.bonds.add(bond);
	}

	/**
	 * Removes a context bond from this CIS
	 * 
	 * @param bond
	 *            the context bond to remove
	 * @throws NullPointerException if the specified context bond is <code>null</code>
	 */
	public void removeBond(CtxBondBean bond) {
		
		if (bond == null)
			throw new NullPointerException("bond can't be null");
		
		this.bonds.remove(bond);
	}
}