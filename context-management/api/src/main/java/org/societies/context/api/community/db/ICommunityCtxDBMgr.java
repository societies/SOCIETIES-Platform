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
package org.societies.context.api.community.db;

import java.util.List;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;

/**
 * ICommunityCtxDBMgr platform interface. This interface provides access to community context database. 
 * 
 * @author nlia
 * @created 12-Nov-2011 7:15:14 PM
 */
public interface ICommunityCtxDBMgr {

	/**
	 * Creates a community Context Attribute
	 * 
	 * @param scope
	 * @param enum
	 * @param type
	 */
	public CtxAttribute createCommunityAttribute(CtxEntityIdentifier scope, String type) throws CtxException;

	/**
	  * Creates a community Context Entity. 
	  * @param cisId
	  * @throws CtxException 
	  * @since 0.2
	  */
	public CommunityCtxEntity createCommunityEntity(IIdentity cisId) throws CtxException;

	////////////////////////////
	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param identifier
	 */
	public CtxModelObject retrieve(CtxEntityIdentifier identifier) throws CtxException;
	
	
	/**
	  * Retrieves the specidied community Context Entity. 
	  * @param ctxId
	  * @throws CtxException 
	  * @since 0.2
	  */
	public CommunityCtxEntity retrieveCommunityEntity(CtxEntityIdentifier ctxId) throws CtxException;

	/**
	  * Updates a community Context Entity. 
	  * @param entity
	  * @throws CtxException 
	  * @since 0.2
	  */
	public CommunityCtxEntity updateCommunityEntity(CommunityCtxEntity entity) throws CtxException;
	
	/**
	  * Removes the specidied community Context Entity. 
	  * @param ctxId
	  * @throws CtxException 
	  * @since 0.2
	  */
	public CommunityCtxEntity removeCommunityEntity(CtxEntityIdentifier ctxId) throws CtxException;

	/**
	  * This method retrieves the CSS that is assigned with the community administration role.
	  * @param communityId
	  * @throws CtxException 
	  * @since 0.2
	  */
	public IndividualCtxEntity retrieveAdministratingCss(CtxEntityIdentifier communityId) throws CtxException;
	
	
	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param community identifier
	 * @throws CtxException
	 * @since 0.0.1
	 */
	public CtxBond retrieveBonds(CtxEntityIdentifier community) throws CtxException;

	/**
	  * Retrieves the sub-communities of the specified community entity.
	  *  
	  * @param communityId
	  * @throws CtxException 
	  * @since 0.2
	  */
	public List<CtxEntityIdentifier> retrieveSubCommunities(CtxEntityIdentifier communityId) throws CtxException;

	
	/**
	 * This applies for Community hierarchies. Retrieves the child communities
	 * (subcommunities of CtxEntities) of the specified parent CtxEntity
	 * 
	 * @param community identifier
	 * @throws CtxException
	 * @since 0.0.1
	 */
	public List<CtxEntityIdentifier> retrieveChildCommunities(CtxEntityIdentifier community) throws CtxException;

	/**
	 * Retrieves a list of Individual Context Entities that are members of the
	 * specified community Entity.
	 * 
	 * @param community
	 * @throws CtxException
	 * @since 0.0.1
	 */
	public List<CtxEntityIdentifier> retrieveCommunityMembers(CtxEntityIdentifier community) throws CtxException;

	/**
	 * Retrieves communities characterized as parent for the community specified by the Community CtxEntityIdentifier  
	 * 
	 * @param community
	 * @throws CtxException
	 * @since 0.0.1
	 */
	public List<CtxEntityIdentifier> retrieveParentCommunities(CtxEntityIdentifier community) throws CtxException;

}