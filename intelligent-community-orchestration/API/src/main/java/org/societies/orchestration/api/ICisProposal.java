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
package org.societies.orchestration.api;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Future;

import org.societies.api.identity.IIdentity;

import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivity;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.identity.InvalidFormatException;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * This interface represents a CIS proposed to be created. Used internally within T5.1.
 * This is a convenient way to store and pass around data on a CIS that could be created.
 * For configuration and deletion of CISs, the actual T4.5 ICis datatypes should be used
 * (except in configuration, when new CISs created from merge or split would be represented
 * as ICisProposals).
 *
 */

public class ICisProposal {
	
	private String name;
	private String description;
	
	private Set<IIdentity> members;
	private Set<IIdentity> administrators;
	
	private String ownerId;
	private String cisType;
	private ArrayList<String> membershipCriteria;
	
	private IActivityFeed activityFeed;
	
	
	/**
	 * Create a blank ICisProposal, which can then be populated with details that
	 * are proposed for a CIS that may be created.
	 *  
	 */
	public ICisProposal() {
		name = "";
		description = "";
		members = new HashSet<IIdentity>();
		ownerId = "";
		cisType = "";
		membershipCriteria = new ArrayList<String>();
		activityFeed = null;
	}
	
	/**
	 * Get the proposed name for the CIS proposal.
	 * 
	 * @return String of the name of the CIS that would result from the CIS proposal 
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the proposed name for the CIS proposal.
	 * 
	 * @param String: the proposed name for the CIS proposal 
	 */
	public String setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the proposed description for the CIS proposal.
	 * 
	 * @return String of the description that would be held on a CIS resulting from in the CIS proposal 
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the proposed description for the CIS proposal.
	 * 
	 * @param String: the proposed description for the CIS proposal 
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the interface for working with the ActivityFeed of the CIS
	 * 
	 * 
	 * @param 
	 * @return {@link IActivityFeed} of that CIS
	 */
	public IActivityFeed getCisActivityFeed() {
		return activityFeed;
	}
	
	/**
	 * Set the proposed activity feed for the CIS proposal (probably only usable for when
	 * merging or splitting CISs - the new ones may inherit activities or feeds.
	 * 
	 * @param IActivityFeed: the proposed activity feed for the CIS proposal 
	 */
	public void setCisActivityFeed(IActivityFeed activityFeed) {
		this.activityFeed = activityFeed;
	}
	
	/**
	 * Add an activity to the activity feed of the CIS proposal (would probably only apply
	 * for when the proposal is for a new CIS formed from a split or merge.
	 * 
	 * @param IActivityFeed: the proposed activity feed for the CIS proposal 
	 */
	public void addActivity(IActivity activity) {
		activityFeed.addCisActivity(activity);
	}
	
	/**
	 * get list of members
	 * 
	 * @return list of participants of the CIS proposal 
	 * @throws CommunicationException 
	 * @throws InvalidFormatException 
	 */
	public Set<IIdentity> getMemberList() {
		return members;
	}
	
	/**
	 * Set the proposed member list for the CIS proposal.
	 * 
	 * @param Set<IIdentity>: the proposed set of members, each is an IIdentity/"jid", for the CIS proposal 
	 */
	public void setMemberList(Set<IIdentity> members) {
		this.members = members;
	}
	
	/**
	 * get list of administrators
	 * 
	 * @return list of administrators of the CIS proposal 
	 * @throws CommunicationException 
	 * @throws InvalidFormatException 
	 */
	public Set<IIdentity> getAdministratorList() {
		return administrators;
	}
	
	/**
	 * add a member to the CIS proposal 
	 * 
	 * @param jid is the full jid of the user to be added
	 * @param role of the user. "participant" or "owner" or "administrator"
	 * @return true if it worked and false if the jid was already there
	 * @throws CommunicationException 
	 * @throws InvalidFormatException 
	 */
	public Boolean addMember(String jid, String role) throws  CommunicationException {
		if (members.contains(jid))
			if (role.equals("administrator") && !(administrators.contains(jid)) {
				administrators.add(jid);
				return true;
			}
			else if (role.equals("owner") && !(ownerId.equals(jid)) {
				ownerId = jid;
				return true;
			}
			else return false;
			    
		members.add(jid);
		if (role.equals("owner"))
			ownerId = jid;
		else if (role.equals("administrator"))
			administrators.add(jid);
		return true;
	}

	/**
	 * remove a member from the CIS 
	 * 
	 * @param jid is the full jid of the user to be removed
	 * @return true if it worked and false if the user was not part of the group
	 * @throws CommunicationException 
	 * @throws InvalidFormatException 
	 */
	public Boolean removeMember(String jid) throws  CommunicationException {
		if (members.contains(jid)) {
			members.remove(members.indexOf(jid));
			return true;
		}
		else return false;
	}
	
	/**
	 * Get the proposed owner for the CIS proposal.
	 * 
	 * @return String of the owner that is their IIdentity/"jid", involved in the CIS proposal 
	 */
	public String getOwnerId() {
		return "";
	}
	
	/**
	 * Set the proposed owner for the CIS proposal.
	 * 
	 * @param String: the proposed owner (String form of their IIdentity/"jid") for the CIS proposal 
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	/**
	 * Get the proposed type for the CIS proposal.
	 * 
	 * @return String that is the type involved in the CIS proposal 
	 */
	public String getCisType() {
		return cisType;
	}
	
	/**
	 * Set the proposed type for the CIS proposal.
	 * 
	 * @param String: the proposed type for the CIS proposal 
	 */
	public void setCisType(String type) {
		cisType = type;
	}
	
	/**
	 * Get the proposed membership criteria for the CIS proposal.
	 * 
	 * @return ArrayList of membership criteria, as strings, involved in the CIS proposal 
	 */
	public ArrayList<String> getMembershipCriteria() {
		return membershipCriteria;
	}
	
	/**
	 * Set the proposed membership criteria for the CIS proposal.
	 * 
	 * @param Set of membership criteria, as strings, involved in the CIS proposal 
	 */
	public void setMembershipCriteria(ArrayList<String> membershipCriteria) {
		this.membershipCriteria = membershipCriteria;
	}
	
}
