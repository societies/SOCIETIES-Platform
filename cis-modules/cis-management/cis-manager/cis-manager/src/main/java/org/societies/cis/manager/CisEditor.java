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
import java.util.Iterator;
import java.util.List;

import java.util.Set;

//import org.societies.cis.mgmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.collaboration.IServiceSharingRecord;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.cis.management.ICisActivityFeed;
import org.societies.api.cis.management.ICisEditor;
import org.societies.api.cis.management.ICisRecord;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.cis.manager.CisParticipant.MembershipType;
import org.societies.identity.IdentityImpl;

import org.societies.api.schema.cis.community.Join;
import org.societies.api.schema.cis.community.Who;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.community.ParticipantRole;
import org.societies.api.schema.cis.community.Add;
import org.societies.api.schema.cis.community.Subscription;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.SubscribedTo;

/**
 * @author Thomas Vilarinho (Sintef)
*/


/**
 * This objecto corresponds to an owned CIS. The CIS record will be the CIS data which is stored on DataBase
*/

//@Component
public class CisEditor implements ICisEditor, IFeatureServer {


	public CisRecord cisRecord;
	public CisActivityFeed cisActivityFeed;
	public Set<IServiceSharingRecord> sharedServices; 
//	public CommunityManagement commMgmt;
	
	
	private final static List<String> NAMESPACES = Collections
			.singletonList("http://societies.org/api/schema/cis/community");
	private final static List<String> PACKAGES = Collections
			.singletonList("org.societies.api.schema.cis.community");
	
	private ICommManager CISendpoint;
	
	private IIdentity cisIdentity;
	private PubsubClient psc;

	public Set<CisParticipant> membersCss; // TODO: this may be implemented in the CommunityManagement bundle. we need to define how they work together
	


	private static Logger LOG = LoggerFactory
			.getLogger(CisEditor.class);	
	



	// at the moment we are not using this constructor, but just the one below, as that one generates the CIS id for us
	public CisEditor(String ownerCss, String cisId,String host,
			int membershipCriteria, String permaLink, String password,ICISCommunicationMgrFactory ccmFactory) {
		
		cisActivityFeed = new CisActivityFeed();
		sharedServices = new HashSet<IServiceSharingRecord>();
		membersCss = new HashSet<CisParticipant>();
		membersCss.add(new CisParticipant(ownerCss,MembershipType.owner));

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
		
		LOG.info("CIS creating pub sub service");
		
//		PubsubServiceRouter psr = new PubsubServiceRouter(CISendpoint);

		
		LOG.info("CIS pub sub service created");
		
		//this.psc = psc;
		
		LOG.info("CIS autowired PubSubClient");
		// TODO: broadcast its creation to other nodes?

	}

	// constructor of a CIS without a pre-determined ID or host
	public CisEditor(String cssOwner, String cisName, String cisType, int mode,ICISCommunicationMgrFactory ccmFactory) {
		
		cisActivityFeed = new CisActivityFeed();
		sharedServices = new HashSet<IServiceSharingRecord>();
		membersCss = new HashSet<CisParticipant>();
		membersCss.add(new CisParticipant(cssOwner,MembershipType.owner));

		LOG.info("CIS editor created");
		
		CISendpoint = ccmFactory.getNewCommManager();
		
		try {
		cisIdentity = CISendpoint.getIdManager().getThisNetworkNode();//CISendpoint.getIdManager().fromJid(CISendpoint.getIdManager().getThisNetworkNode().getJid());
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		LOG.info("CIS endpoint created");
		
		
		
		try {
			CISendpoint.register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		} // TODO unregister??
		
		LOG.info("CIS listener registered");
		
		
		// TODO: we have to get a proper identity and pwd for the CIS...
		cisRecord = new CisRecord(cisActivityFeed,cssOwner, mode, cisIdentity.getJid(), "", membersCss,
				cisIdentity.getDomain(), sharedServices,cisType,cisName);
		
		LOG.info("CIS creating pub sub service");
		
//		PubsubServiceRouter psr = new PubsubServiceRouter(CISendpoint);

		
		LOG.info("CIS pub sub service created");
		
		//this.psc = psc;
		
		LOG.info("CIS autowired PubSubClient");
		// TODO: broadcast its creation to other nodes?

	}
	

	public Set<CisParticipant> getMembersCss() {
		return membersCss;
	}


	public void setMembersCss(Set<CisParticipant> membersCss) {
		this.membersCss = membersCss;
	}
	

	/**
	 * add a member to the CIS and at the same time send the XMPP notification to all the other users that this user has been added to the community
	 * and send a notification to that user
	 * 
	 * @param jid is the full jid of the user
	 * @param role, if the role is null, the member will be set as a participant
	 * @return true if it worked and false if the jid was already there
	 * @throws CommunicationException 
	 */
	public boolean addMember(String jid, MembershipType role) throws  CommunicationException{
		
		
		LOG.info("add member invoked");
		if (role == null)
			role = MembershipType.participant; // default role is participant
		
		if (membersCss.add(new CisParticipant(jid, role))){
			// should we send a XMPP notification to all the users to say that the new member has been added to the group
			// I thought of that as a way to tell the participants CIS Managers that there is a new participant in that group
			// and the GUI can be updated with that new member
			LOG.info("new member added, going to notify community");
			
			// 1) Notifying the added user

			
			CommunityManager cMan = new CommunityManager();
			SubscribedTo s = (SubscribedTo) cMan.getSubscribedTo();
			s.setCisJid(jid);
			s.setCisRole(role.toString());
			cMan.setSubscribedTo(s);
			
			Participant p = new Participant();
			p.setJid(this.getCisId());
			p.setRole( ParticipantRole.fromValue(role.toString())  );
			IIdentity targetCssIdentity = new IdentityImpl(jid);
			Stanza sta = new Stanza(targetCssIdentity);
			CISendpoint.sendMessage(sta, cMan);
					
			
			//2) Sending a notification to all the other users // TODO: probably change this to a thread that process a queue or similar
			
			//creating payload
			Community c = new Community();
			Who w = new Who();
			w.getParticipant().add(p);// p has been set on the 1st message
			c.setWho(w);
			// sending to all members
			
			Set<CisParticipant> se = this.getMembersCss();
			Iterator<CisParticipant> it = se.iterator();
			
			while(it.hasNext()){
				CisParticipant element = it.next();
				LOG.info("sending notification to " + element.getMembersJid());
				targetCssIdentity = new IdentityImpl(element.getMembersJid());
				sta = new Stanza(targetCssIdentity);
				CISendpoint.sendMessage(sta, c);
				
		     }

			return true;
		}else{
			return false;
		}
		
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
		} else if (cisRecord.equals(other.cisRecord) == false)
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

			// JOIN
			if (c.getJoin() != null) {
				String jid = "";
				LOG.info("join received");
				String senderjid = stanza.getFrom().getBareJid();
				boolean addresult = false; 
				try{
					if(c.getJoin().getParticipant() != null){
						jid = c.getJoin().getParticipant().getJid();
						addresult = this.addMember(jid, MembershipType.participant);
					}
						
				}catch(CommunicationException e){
					e.printStackTrace();
				}
				Community result = new Community();
				if(addresult == false){
					result.setJoin(new Join());
				}
				else{
					
					Participant p = new Participant();
					p.setJid(jid);
					p.setRole( ParticipantRole.fromValue("participant")  );
					Subscription sub = new Subscription();
					sub.setParticipant(p);
					result.setSubscription(sub);
					
				}
				return result;

				//return result;
			}
			if (c.getLeave() != null) {
				String jid = stanza.getFrom().getBareJid();
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
				// WHO
				Community result = new Community();
				Who who = new Who();
				this.getMembersCss();
		
				
				Set<CisParticipant> s = this.getMembersCss();
				Iterator<CisParticipant> it = s.iterator();
				
				while(it.hasNext()){
					CisParticipant element = it.next();
					Participant p = new Participant();
					p.setJid(element.getMembersJid());
					p.setRole( ParticipantRole.fromValue(element.getMtype().toString())   );
					who.getParticipant().add(p);
			     }
				
				result.setWho(who);
				return result;
				// END OF WHO
			}
			if (c.getAdd() != null) {
				// ADD
				Community result = new Community();
				Add a = new Add();
				result.setAdd(a);

				//TODO: possibly check that the sender is the owner of the CSS
				Participant p = c.getAdd().getParticipant();
				if(p!= null && p.getJid() != null){
					String role = "";
					if (p.getRole() != null)				
						role = p.getRole().value();
					
					try{
						if(this.addMember(p.getJid(), MembershipType.valueOf(role))){
							a.setParticipant(p);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				return result;
				// END OF ADD
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

	@Override
	public ICisActivityFeed getActivityFeed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCisId() {
	
		return this.cisRecord.getCisId();
	}

	@Override
	public Boolean update(String arg0, ICisRecord arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

    
    
}
