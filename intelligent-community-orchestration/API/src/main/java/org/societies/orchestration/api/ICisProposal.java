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
import java.util.HashSet;
import java.util.concurrent.Future;

import org.societies.api.identity.IIdentity;

import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivity;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.identity.InvalidFormatException;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

import org.societies.api.identity.IIdentityManager;

/**
 * This interface represents a CIS proposed to be created, or an existing CIS that is stored within
 * it as an ICis. Used internally within T5.1.
 * This is a convenient way to store and pass around data on a CIS that could be created.
 * For configuration and deletion of CISs, where existing CISs are involved, the ICisProposal should
 * still be used to pass suggestions to Community Lifecycle Management, but whenever an ICisProposal
 * is used to represent a CIS that already exists, use the 'setActualCis' method to have it store
 * the CIS. 
 * 
 * NOTE: if a suggestion is being made to change an existing CIS's attributes, then
 * create an ICisProposal and invoke 'setActualCis' for the CIS as it is now, and create another ICisProposal
 * and don't use setActualCis, but instead set all the attributes on the ICisProposal
 * for the CIS as you want it to become, with the new attributes.
 * 
 *
 */

public class ICisProposal {
	
	private String name;
	private String description;
	
	private Set<String> members;
	private Set<String> administrators;
	
	private String ownerId;
	private String cisType;
	private ArrayList<String> membershipCriteria;
	
	private IActivityFeed activityFeed;
	
	private ICis actualCis;
	private IIdentityManager identityManager;
	
	private ICisProposal parentCis;
	private ICis actualParentCis;
	private ArrayList<ICisProposal> subCiss;
	private ArrayList<ICis> actualSubCiss;
	
	/**
	 * Create a blank ICisProposal, which can then be populated with details that
	 * are proposed for a CIS that may be created.
	 *  
	 */
	public ICisProposal() {
		name = "";
		description = "";
		members = new HashSet<String>();
		administrators = new HashSet<String>();
		ownerId = "";
		cisType = "";
		membershipCriteria = new ArrayList<String>();
		activityFeed = null;
		actualCis = null;
		parentCis = null;
		subCiss = new ArrayList<ICisProposal>();
	}
	
	/**
	 * Get the existing CIS that this proposal datatype is referencing.
	 * 
	 * @return The existing CIS that this proposal datatype is referencing
	 */
	public ICis getActualCis() {
		return actualCis;
	}
	
	/**
	 * Set the existing CIS that this proposal datatype is referencing.
	 * 
	 * @param ICis: the existing CIS that this proposal datatype is referencing.
	 */
	public void setActualCis(ICis actualCis) {
		this.actualCis = actualCis;
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
	public void setName(String name) {
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
	public Set<String> getMemberList() {
		return members;
	}
	
	/**
	 * Set the proposed member list for the CIS proposal.
	 * 
	 * @param Set<IIdentity>: the proposed set of members, each is an IIdentity/"jid", for the CIS proposal 
	 */
	public void setMemberList(Set<String> members) {
		this.members = members;
	}
	
	/**
	 * get list of administrators
	 * 
	 * @return list of administrators of the CIS proposal 
	 * @throws CommunicationException 
	 * @throws InvalidFormatException 
	 */
	public Set<String> getAdministratorList() {
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
			if (role.equals("administrator") && !(administrators.contains(jid))) {
				administrators.add(jid);
				return true;
			}
			else if (role.equals("owner") && !(ownerId.equals(jid))) {
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
			members.remove(jid);
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
	 * NOTE: The parameter type will probably change depending on how T4.5 chooses to represent
	 * this. For now though, use the following syntax for each element of the list:
	 * 
	 * Each element is a string broken into three parts, with "---" between each part.
	 * Part 1) is one of: CONTEXT ATTRIBUTE, CONTEXT ASSOCIATION, ACTIVITY, SERVICE USE
	 * Part 2) is the label type, such as 'address' or 'family', which only applies for context attributes and assocations.
	 * For other datatypes, it doesn't matter if anything is put in part 2) or if it doesn't get a "---"
	 * Part 3) is the value of the data, if this is being passed. So an 'address' might have '15 Redding Street'.
	 * Again this may be left out.
	 * 
	 * So a full String element in the list might be:
	 * 
	 * "CONTEXT ATTRIBUTE---interest---fishing"
	 */
	public void setMembershipCriteria(ArrayList<String> membershipCriteria) {
		this.membershipCriteria = membershipCriteria;
	}
	
	/**
	 * Get the proposed parent CIS for the CIS proposal.
	 * 
	 * @return ICisProposal of the proposed parent CIS, involved in the CIS proposal 
	 */
	public ICisProposal getParentCis() {
		return parentCis;
	}
	
	/**
	 * Get the proposed parent CIS for the CIS proposal, where the proposed parent is a real existing CIS.
	 * 
	 * @return ICis of the proposed parent CIS, involved in the CIS proposal 
	 */
	public ICis getActualParentCis() {
		return actualParentCis;
	}
	
	/**
	 * Set the proposed parent CIS for the CIS proposal.
	 * 
	 * @param ICisProposal of the proposed parent CIS, involved in the CIS proposal
	 */
	public void setParentCis(ICisProposal parentCis) {
		this.parentCis = parentCis;
	}
	
	/**
	 * Set the parent CIS for the CIS proposal, where the parent is a real existing CIS.
	 * 
	 * @param ICis of the potential parent CIS, involved in the CIS proposal
	 */
	public void setParentCis(ICis parentCis) {
		this.actualParentCis = actualParentCis;
	}
	
	/**
	 * Get the proposed sub-CISs for the CIS proposal.
	 * 
	 * @return ArrayList of ICisProposal of the proposed sub-CISs, involved in the CIS proposal 
	 */
	public ArrayList<ICisProposal> getSubCiss() {
		return subCiss;
	}
	
	/**
	 * Set the proposed sub-CISs for the CIS proposal.
	 * 
	 * @param ArrayList of ICisProposal of the proposed sub-CIS, involved in the CIS proposal
	 */
	public void setSubCiss(ArrayList<ICisProposal> subCiss) {
		this.subCiss = subCiss;
	}
	
	/**
	 * Add a proposed sub-CIS for the CIS proposal.
	 * 
	 * @param ICisProposal of the proposed sub-CIS to be added to the CIS proposal
	 */
	public void addSubCis(ICisProposal subCis) {
		subCiss.add(subCis);
	}
	
	/**
	 * Remove a proposed sub-CIS for the CIS proposal.
	 * 
	 * @param ICisProposal of the proposed sub-CIS to be removed from the CIS proposal
	 */
	public void removeSubCis(ICisProposal subCis) {
		subCiss.remove(subCis);
	}
	
	/**
	 * Add a proposed sub-CIS for the CIS proposal, which is already a real existing CIS.
	 * 
	 * @param ICis of the proposed sub-CIS to be added to the CIS proposal
	 */
	public void addSubCis(ICis subCis) {
		actualSubCiss.add(subCis);
	}
	
	/**
	 * Remove a proposed sub-CIS from the CIS proposal, which is a real existing CIS.
	 * 
	 * @param ICisProposal of the proposed sub-CIS to be removed from the CIS proposal
	 */
	public void removeSubCis(ICis subCis) {
		actualSubCiss.remove(subCis);
	}
}
