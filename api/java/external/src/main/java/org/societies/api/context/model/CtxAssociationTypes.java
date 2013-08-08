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

/**
 * This class defines common {@link CtxAssociation context association} types.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.7
 */
public class CtxAssociationTypes {
	
	/**
	 * Undirected context association.
	 * 
	 * @since 0.0.8
	 */
	public static final String ARE_FAMILY = "areFamily";
	
	/**
	 * Undirected context association.
	 * 
	 * @since 0.0.8
	 */
	public static final String ARE_WORK_COLLEAGUES = "areWorkColleagues";
	
	/**
	 * Directed context association between a {@link CommunityCtxEntity} 
	 * (parent entity) and the {@link CommunityMemberCtxEntity 
	 * CommunityMemberCtxEntities} (child entities) which are members of this
	 * community.
	 * 
	 *  @since 0.5
	 */
	public static final String HAS_MEMBERS = "hasMembers";
	
	/**
	 * Directed context association.
	 * 
	 * @deprecated As of 0.0.8, use the {@link #HAS_PARAMETERS} type instead
	 */
	@Deprecated
	public static final String HAS_PARAMETER = "hasParameter";
	
	/**
	 * Directed context association.
	 * 
	 * @since 0.0.8
	 */
	public static final String HAS_PARAMETERS = "hasParameters";
	
	/**
	 * Directed context association.
	 */
	public static final String HAS_PRIVACY_POLICIES = "hasPrivacyPolicies";
	
	/**
	 * Has privacy policy agreement
	 * To be moved to internal API
	 */
	@Deprecated
	public static final String HAS_PRIVACY_POLICY_AGREEMENTS = "hasPrivacyPolicyAgreements";
	
	/**
	 * Directed context association between an {@link IndividualCtxEntity} 
	 * (parent entity) and the {@link CommunityCtxEntity CommunityCtxEntities}
	 * (child entities) this individual is the administrator of. More 
	 * specifically, each IndividualCtxEntity must have exactly <i>one</i> 
	 * association of this type which may have zero or more child entities
	 * representing the CISs this individual administrates.  
	 * 
	 *  @since 1.2
	 */
	public static final String IS_ADMIN_OF = "isAdminOf";
	
	/**
	 * Directed context association.
	 */
	public static final String IS_CONNECTED_TO_SNS = "isConnectedToSns";
	
	/**
	 * Directed context association between an {@link IndividualCtxEntity} 
	 * (parent entity) and the {@link IndividualCtxEntity IndividualCtxEntities}
	 * (child entities) this individual is friends with.
	 * 
	 * @since 0.5
	 */
	public static final String IS_FRIENDS_WITH = "isFriendsWith";
	
	/**
	 * Directed context association between an {@link IndividualCtxEntity} 
	 * (parent entity) and the {@link CommunityCtxEntity CommunityCtxEntities}
	 * (child entities) this individual is member of.
	 * 
	 *  @since 0.5
	 */
	public static final String IS_MEMBER_OF = "isMemberOf";
	
	/**
	 * Directed context association.
	 */
	public static final String OWNS_SERVICES = "ownsServices";
	
	/**
	 * Directed context association between an {@link IndividualCtxEntity} and
	 * the {@link CtxEntityTypes#CSS_NODE} entities it owns.
	 * 
	 * @since 0.5
	 */
	public static final String OWNS_CSS_NODES = "ownsCssNodes";
	
	/**
	 * Directed context association.
	 */
	public static final String SHARES_SERVICES = "sharesServices";
			
	/**
	 * @deprecated As of 0.0.8, use the {@link #USES_SERVICES} type instead.
	 */
	@Deprecated
	public static final String USES_SERVICE = "usesService";
	
	/**
	 * Directed context association.
	 * 
	 * @since 0.0.8
	 */
	public static final String USES_DEVICES = "usesDevices";
	
	/**
	 * Directed context association.
	 * 
	 * @since 0.0.8
	 */
	public static final String USES_SERVICES = "usesServices";

}