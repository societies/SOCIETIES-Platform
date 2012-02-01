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

package org.societies.personalisation.socialprofiler.datamodel;


import org.neo4j.graphdb.Node;


public class GroupImpl implements Group, NodeProperties{

	private final Node underlyingNode;
	
	/**
	 * constructor of group
	 * @param underlyingNode
	 * 			Node
	 */			
	public GroupImpl(Node underlyingNode) {
		super();
		this.underlyingNode = underlyingNode;
	}

	/**
	 * returns the underlyong node of the group
	 * @return	Node underlyingNode
	 */
	public Node getUnderlyingNode() {
		return underlyingNode;
	}

	//@Override
	public String getName() {
		return (String) underlyingNode.getProperty( NAME_PROPERTY );
	}

	//@Override
	public void setName(String name) {
		underlyingNode.setProperty( NAME_PROPERTY, name );
	}

	//@Override
	public String getCreator() {
		return (String) underlyingNode.getProperty( CREATOR_PROPERTY );
	}

	//@Override
	public String getDescription() {
		return (String) underlyingNode.getProperty( DESCRIPTION_PROPERTY );
	}

	//@Override
	public String getGroupSubType() {
		return (String) underlyingNode.getProperty( SUBTYPE_PROPERTY );
	}

	//@Override
	public String getGroupType() {
		return (String) underlyingNode.getProperty( TYPE_PROPERTY );
	}

	//@Override
	public String getRealName() {
		return (String) underlyingNode.getProperty( REAL_NAME_PROPERTY );
	}

	//@Override
	public String getUpdateTime() {
		return (String) underlyingNode.getProperty( UPDATETIME_PROPERTY );
	}

	//@Override
	public void setCreator(String creator) {
		underlyingNode.setProperty(CREATOR_PROPERTY, creator );
	}

	//@Override
	public void setDescription(String description) {
		underlyingNode.setProperty( DESCRIPTION_PROPERTY, description );
	}

	//@Override
	public void setGroupSubType(String groupSubType) {
		underlyingNode.setProperty( SUBTYPE_PROPERTY, groupSubType );
	}

	//@Override
	public void setGroupType(String groupType) {
		underlyingNode.setProperty( TYPE_PROPERTY, groupType );
	}

	//@Override
	public void setRealName(String realName) {
		underlyingNode.setProperty( REAL_NAME_PROPERTY, realName );
	}

	//@Override
	public void setUpdateTime(String updateTime) {
		underlyingNode.setProperty( UPDATETIME_PROPERTY, updateTime );
	}

}
