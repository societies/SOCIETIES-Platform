/*
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp.,
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

package org.societies.orchestration.cpa.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.societies.activity.ActivityFeed;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.identity.RequestorBean;

import org.societies.api.identity.Requestor;

public class ICISSimulated implements ICisOwned {
	private ArrayList<String> members;
	public ArrayList<String> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<String> members) {
		this.members = members;
	}

	public ActivityFeed getFeed() {
		return feed;
	}

	public void setFeed(ActivityFeed feed) {
		this.feed = feed;
	}
	private ActivityFeed feed=null;
	public ICISSimulated() {
		members = new ArrayList<String>();
		feed = new ActivityFeed();
	}

	@Override
	public String getCisId() {
		// TODO Auto-generated method stub
		return "1";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "name";
	}

    @Override
    public void getMembershipCriteria(ICisManagerCallback callback) {
    }

  

    @Override
    public void getInfo(Requestor req,ICisManagerCallback callback){
		// TODO Auto-generated method stub

	}
    
    @Override
    public void getInfo(RequestorBean req,ICisManagerCallback callback){
		// TODO Auto-generated method stub

	}
    

	@Override
	public void getListOfMembers(Requestor requestor, ICisManagerCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getListOfMembers(RequestorBean requestor, ICisManagerCallback callback) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void setInfo(Community c, ICisManagerCallback callback) {
		// TODO Auto-generated method stub

	}

//	@Override
//	public void addActivity(IActivity activity, ICisManagerCallback callback) {
//		this.feed.addActivity(activity);
//
//	}
//
//	@Override
//	public void getActivities(String timePeriod, ICisManagerCallback callback) {
//		//nope
//
//	}


	@Override
	public Set<ICisParticipant> getMemberList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addMember(String jid, String role) {
		return this.members.add(jid);
	}

	@Override
	public boolean removeMemberFromCIS(String jid){
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getOwnerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCisType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setCisType(String type) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub

	}

    @Override
    public boolean checkQualification(HashMap<String, String> qualification) {
        return false;
    }

    @Override
    public boolean addCriteria(String contextAtribute, MembershipCriteria m) {
        return false;
    }

    @Override
    public boolean removeCriteria(String contextAtribute, MembershipCriteria m) {
        return false;
    }

    @Override
	public IActivityFeed getActivityFeed() {
		// TODO Auto-generated method stub
		return feed;
	}
	public List<String> getUsers(){
		return this.members;
	}
}
