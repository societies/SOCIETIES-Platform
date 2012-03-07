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

package org.societies.cis.manager;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import java.util.Set;

//import org.societies.cis.mgmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.cis.management.CisActivityFeed;
import org.societies.api.internal.cis.management.CisRecord;
import org.societies.api.internal.cis.management.ICisEditor;
import org.societies.api.internal.cis.management.ICisManager;
import org.societies.api.internal.cis.management.ServiceSharingRecord;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.comm.xmpp.xc.impl.XCCommunicationMgr;
import org.societies.community.Community;
import org.societies.community.Participant;
import org.societies.community.Who;
import org.societies.identity.IdentityImpl;


/**
 * @author Thomas Vilarinho (Sintef)
*/

public class CisEditor implements ICisEditor, IFeatureServer {


	public CisRecord cisRecord;
	public CisActivityFeed cisActivityFeed;
	public Set<ServiceSharingRecord> sharedServices; 
//	public CommunityManagement commMgmt;
	
	
	private final static List<String> NAMESPACES = Collections
			.singletonList("http://societies.org/community");
	private final static List<String> PACKAGES = Collections
			.singletonList("org.societies.community");
	
	private ICommManager CISendpoint;
	
	private IIdentity cisIdentity;

	public String[] membersCss; // TODO: this may be implemented in the CommunityManagement bundle. we need to define how they work together
	
	public static final int MAX_NB_MEMBERS = 100;// TODO: this is temporary, we have to set the memberCss to something more suitable


	private static Logger LOG = LoggerFactory
			.getLogger(CisEditor.class);	
	
	/**
	 * @deprecated  Replaced by constructor which has the new host field
	 */
	
	@Deprecated
	public CisEditor(String ownerCss, String cisId,
			String membershipCriteria, String permaLink, String password) {
		
		cisActivityFeed = new CisActivityFeed();
		sharedServices = new HashSet<ServiceSharingRecord>();
		membersCss = new String[MAX_NB_MEMBERS];
		

		// TODO: broadcast its creation to other nodes?

	}
	
	
	// constructor for creating a CIS from scratch
	public CisEditor(String ownerCss, String cisId,String host,
			String membershipCriteria, String permaLink, String password) {
		
		cisActivityFeed = new CisActivityFeed();
		sharedServices = new HashSet<ServiceSharingRecord>();
		membersCss = new String[MAX_NB_MEMBERS];
		//CISendpoint = 	new XCCommunicationMgr(host, cisId,password);
		
		cisRecord = new CisRecord(cisActivityFeed,ownerCss, membershipCriteria, cisId, permaLink, membersCss,
				password, host, sharedServices);
		

		// TODO: broadcast its creation to other nodes?

	}

	public CisEditor(String ownerCss, String cisId,String host,
			String membershipCriteria, String permaLink, String password,ICISCommunicationMgrFactory ccmFactory) {
		
		cisActivityFeed = new CisActivityFeed();
		sharedServices = new HashSet<ServiceSharingRecord>();
		membersCss = new String[MAX_NB_MEMBERS];

		LOG.info("CIS editor created");
		
		
		cisIdentity = new IdentityImpl(IdentityType.CIS, cisId, host);

		CISendpoint = ccmFactory.getNewCommManager(cisIdentity, password);
				
		LOG.info("CIS endpoint created");
		
		
		
		try {
			CISendpoint.register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		} // TODO unregister??
		
		LOG.info("CIS listener registered");
		
		
		
		cisRecord = new CisRecord(cisActivityFeed,ownerCss, membershipCriteria, cisId, permaLink, membersCss,
				password, host, sharedServices);
		

		// TODO: broadcast its creation to other nodes?

	}

	
	// if just ownerCss and cisId are passed,
	// password will be set to ""
	// membership to "default"
	// permalink to ""
	/**
	 * @deprecated  See if there is really a need for this constructor
	 */
	
	@Deprecated
	public CisEditor(String ownerCss, String cisId) {
		
		membersCss = new String[MAX_NB_MEMBERS];
		cisActivityFeed = new CisActivityFeed();
		sharedServices = new HashSet<ServiceSharingRecord>();
		
		cisRecord = new CisRecord(cisActivityFeed,ownerCss, "default", cisId, "", membersCss,
				"","", sharedServices);
		

		// TODO: broadcast its creation to other nodes?

	}

	// constructor for creating a CIS from a CIS record, maybe the case when we are retrieving data from a database
	// TODO: double check if we should clone the related objects or just copy the reference (as it is now)
	public CisEditor(CisRecord cisRecord) {
		
		this.cisRecord = cisRecord; 
		
		this.cisActivityFeed = this.cisRecord.feed;
		this.sharedServices = this.cisRecord.sharedServices;
		//CISendpoint = 	new XCCommunicationMgr(cisRecord.getHost(), cisRecord.getCisId(),cisRecord.getPassword());
		
		
		// TODO: broadcast its creation to other nodes?

	}

	// index for hash and equals was only the cisRecord
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cisRecord == null) ? 0 : cisRecord.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CisEditor other = (CisEditor) obj;
		if (cisRecord == null) {
			if (other.cisRecord != null)
				return false;
		} else if (!cisRecord.equals(other.cisRecord))
			return false;
		return true;
	}

	public CisRecord getCisRecord() {
		return cisRecord;
	}

	public void setCisRecord(CisRecord cisRecord) {
		this.cisRecord = cisRecord;
	}


	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}



	@Override
	public Object getQuery(Stanza stanza, Object payload) {
		// all received IQs contain a community element
		LOG.info("get Query received");
		if (payload.getClass().equals(Community.class)) {
			Community c = (Community) payload;
			if (c.getJoin() != null) {
				LOG.info("join received");
				String jid = stanza.getFrom().getJid();
//				if (!participants.contains(jid)) {
//					participants.add(jid);
//				}
				// TODO add error cases to schema
				Community result = new Community();
				result.setJoin(""); // null means no element and empty string
									// means empty element
				return result;
			}
			if (c.getLeave() != null) {
				String jid = stanza.getFrom().getJid();
//				if (participants.contains(jid)) {
//					participants.remove(jid);
//				}
				// TODO add error cases to schema
				Community result = new Community();
				result.setLeave(""); // null means no element and empty string
										// means empty element
				return result;
			}
			if (c.getWho() != null) {
				// TODO add error cases to schema
				Community result = new Community();
				Who who = new Who();
/*				for (String jid : participants) {
					Participant p = new Participant();
					p.setJid(jid);
					if (leaders.contains(jid))
						p.setRole("leader");
					else
						p.setRole("participant");
					who.getParticipant().add(p);
				}*/
				result.setWho(who);
				return result;
			}
		}
		return null;
	}

	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}


	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
	
	

    
    
}
