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
package org.societies.api.cis.management;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.identity.InvalidFormatException;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * This interface represents the CISs that are owned by this CSS.
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */


@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface ICisOwned extends ICis {
	
	
	/**
	 * Gets the interface for working with the ActivityFeed of the CIS
	 * 
	 * 
	 * @param 
	 * @return {@link IActivityFeed} of that CIS
	 */
	//public Future<IActivityFeed> getCisActivityFeed();
	
	/**
	 * get list of members
	 * 
	 * @return list of participants of the CIS as {@link ICisParticipant} 
	 * @throws CommunicationException 
	 * @throws InvalidFormatException 
	 */
	public Future<Set<ICisParticipant>> getMemberList();
	
	
	/**
	 * add a member to the CIS 
	 * 
	 * @param jid is the full jid of the user to be added
	 * @param role of the user. At the moment it can be "participant" or "owner"
	 * @return true if it worked and false if the jid was already there
	 * @throws CommunicationException 
	 * @throws InvalidFormatException 
	 */
	public Future<Boolean> addMember(String jid, String role) throws  CommunicationException;

	/**
	 * remove a member from the CIS 
	 * 
	 * @param jid is the full jid of the user to be removed
	 * @return true if it worked and false if the user was not part of the group
	 * @throws CommunicationException 
	 * @throws InvalidFormatException 
	 */
	public Future<Boolean> removeMemberFromCIS(String jid) throws  CommunicationException;
	
	
	// some getters and setters. TODO: change them to return Future so they can work for both remote and local CISs
	// as soon as this is done, we will add the javadoc as well
	public String getOwnerId();
	public String getCisType();
	public String setCisType(String type);
	public String getDescription();
	public void setDescription(String description); 
	

	// TODO: change the visitibility of those methods and add documentation
	public boolean checkQualification(HashMap<String,String> qualification);
	
	public boolean addCriteria(String contextAtribute, MembershipCriteria m);
	public boolean removeCriteria(String contextAtribute, MembershipCriteria m);
}
