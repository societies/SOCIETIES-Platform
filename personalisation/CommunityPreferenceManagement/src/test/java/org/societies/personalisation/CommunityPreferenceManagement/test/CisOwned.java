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
package org.societies.personalisation.CommunityPreferenceManagement.test;

import java.util.HashMap;
import java.util.Set;

import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.identity.RequestorBean;

/**
 * @author Eliza
 *
 */
public class CisOwned implements ICisOwned {

	private final IIdentity cisID;

	public CisOwned(IIdentity cisID){
		this.cisID = cisID;
		
	}
	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICis#getActivityFeed()
	 */
	@Override
	public IActivityFeed getActivityFeed() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICis#getCisId()
	 */
	@Override
	public String getCisId() {
		// TODO Auto-generated method stub
		return this.cisID.getBareJid();
	}


	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICis#getMembershipCriteria(org.societies.api.cis.management.ICisManagerCallback)
	 */
	@Override
	public void getMembershipCriteria(ICisManagerCallback arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICis#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICis#getOwnerId()
	 */
	@Override
	public String getOwnerId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICis#setInfo(org.societies.api.schema.cis.community.Community, org.societies.api.cis.management.ICisManagerCallback)
	 */
	@Override
	public void setInfo(Community arg0, ICisManagerCallback arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisRemote#getInfo(org.societies.api.identity.Requestor, org.societies.api.cis.management.ICisManagerCallback)
	 */
	@Override
	public void getInfo(Requestor arg0, ICisManagerCallback arg1) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void getInfo(RequestorBean arg0, ICisManagerCallback arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisRemote#getListOfMembers(org.societies.api.identity.Requestor, org.societies.api.cis.management.ICisManagerCallback)
	 */
	@Override
	public void getListOfMembers(Requestor arg0, ICisManagerCallback arg1) {
		// TODO Auto-generated method stub

	}
	@Override
	public void getListOfMembers(RequestorBean arg0, ICisManagerCallback arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisOwned#addCriteria(java.lang.String, org.societies.api.cis.attributes.MembershipCriteria)
	 */
	@Override
	public boolean addCriteria(String arg0, MembershipCriteria arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisOwned#addMember(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean addMember(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisOwned#checkQualification(java.util.HashMap)
	 */
	@Override
	public boolean checkQualification(HashMap<String, String> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisOwned#getCisType()
	 */
	@Override
	public String getCisType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisOwned#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisOwned#getMemberList()
	 */
	@Override
	public Set<ICisParticipant> getMemberList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisOwned#removeCriteria(java.lang.String, org.societies.api.cis.attributes.MembershipCriteria)
	 */
	@Override
	public boolean removeCriteria(String arg0, MembershipCriteria arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisOwned#removeMemberFromCIS(java.lang.String)
	 */
	@Override
	public boolean removeMemberFromCIS(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisOwned#setCisType(java.lang.String)
	 */
	@Override
	public String setCisType(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.cis.management.ICisOwned#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String arg0) {
		// TODO Auto-generated method stub

	}

}
