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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

//import org.societies.cis.mgmt;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeed;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.collaboration.IServiceSharingRecord;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.cis.management.ICis;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.cis.manager.CisParticipant.MembershipType;
import org.societies.identity.IdentityImpl;

import org.societies.api.schema.cis.community.AddResponse;
import org.societies.api.schema.cis.community.GetInfoResponse;
import org.societies.api.schema.cis.community.JoinResponse;
import org.societies.api.schema.cis.community.LeaveResponse;
import org.societies.api.schema.cis.community.SetInfoResponse;
import org.societies.api.schema.cis.community.Who;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.community.ParticipantRole;
import org.societies.api.schema.cis.community.Add;
import org.societies.api.schema.cis.community.Subscription;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.DeleteMemberNotification;
import org.societies.api.schema.cis.manager.DeleteNotification;
import org.societies.api.schema.cis.manager.Notification;
import org.societies.api.schema.cis.manager.SubscribedTo;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Thomas Vilarinho (Sintef)
*/


/**
 * This object corresponds to an owned CIS. The CIS record will be the CIS data which is stored on DataBase
*/

@Entity
@Table(name = "org_societies_cis_manager_Cis")
public class Cis implements IFeatureServer, ICisOwned {
	private static final long serialVersionUID = 1L;
	@Transient
	private final static List<String> NAMESPACES = Collections
			.unmodifiableList( Arrays.asList("http://societies.org/api/schema/cis/manager",
					  		"http://societies.org/api/schema/cis/community"));
			//.singletonList("http://societies.org/api/schema/cis/community");
	@Transient
	private final static List<String> PACKAGES = Collections
			//.singletonList("org.societies.api.schema.cis.community");
	.unmodifiableList( Arrays.asList("org.societies.api.schema.cis.manager",
		"org.societies.api.schema.cis.community"));
	@Transient
	private SessionFactory sessionFactory;
	
	
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}



	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;



// minimun attributes
	@OneToOne(cascade=CascadeType.ALL)
	public CisRecord cisRecord;
	
	@OneToOne(cascade=CascadeType.ALL)
	public ActivityFeed activityFeed;
	//TODO: should this be persisted?
	@Transient
	public Set<IServiceSharingRecord> sharedServices; 
	@Transient
	private ICommManager CISendpoint;
	@Transient
	private IIdentity cisIdentity;
	@Transient
	private PubsubClient psc;
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	public Set<CisParticipant> membersCss; // TODO: this may be implemented in the CommunityManagement bundle. we need to define how they work together
	@Column
	public String cisType;
	
	public String owner;
	
// extra attributes	
	
	@Transient
	public String permaLink; // all those have been moved to the Editor
	
	@Column
	private String password = "none";
	@Column
	private String host = "none";
	

	String description = "";
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Future<IActivityFeed> getCisActivityFeed(){
		return  new AsyncResult<IActivityFeed>(activityFeed);
	}
	
	@Override
	public IActivityFeed getActivityFeed() {
		return activityFeed;
	}


	private void setActivityFeed(ActivityFeed activityFeed) {
		this.activityFeed = activityFeed;
	}


	public Set<IServiceSharingRecord> getSharedServices() {
		return sharedServices;
	}


	public void setSharedServices(Set<IServiceSharingRecord> sharedServices) {
		this.sharedServices = sharedServices;
	}



	private static Logger LOG = LoggerFactory
			.getLogger(Cis.class);	
	
	public Cis(){
		
	}



	// maximum constructor of a CIS without a pre-determined ID or host
	public Cis(String cssOwner, String cisName, String cisType, int mode,ICISCommunicationMgrFactory ccmFactory
	,String permaLink,String password,String host, String description) {
		this(cssOwner, cisName, cisType, mode,ccmFactory);
		this.password = password;
		this.permaLink = permaLink;
		this.host = host;
		this.description = description;
	}

	

	// minimum constructor of a CIS without a pre-determined ID or host
	public Cis(String cssOwner, String cisName, String cisType, int mode,ICISCommunicationMgrFactory ccmFactory) {
		
		this.owner = cssOwner;
		this.cisType = cisType;
		
		sharedServices = new HashSet<IServiceSharingRecord>();
		membersCss = new HashSet<CisParticipant>();
		membersCss.add(new CisParticipant(cssOwner,MembershipType.owner));

		LOG.info("CIS editor created");
		
		try{ 
		CISendpoint = ccmFactory.getNewCommManager();
		} catch  (CommunicationException e) {
			e.printStackTrace();
			LOG.info("could not start comm manager!");
		}
		
		LOG.info("CIS got new comm manager");
		
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
			LOG.info("could not start comm manager!");
		} // TODO unregister??
		LOG.info("CIS listener registered");
		
		
		// TODO: we have to get a proper identity and pwd for the CIS...
		cisRecord = new CisRecord(mode, cisName, cisIdentity.getJid());
		
		LOG.info("CIS creating pub sub service");
		
//		PubsubServiceRouter psr = new PubsubServiceRouter(CISendpoint);

		
		LOG.info("CIS pub sub service created");
		
		//this.psc = psc;
		
		LOG.info("CIS autowired PubSubClient");
		// TODO: broadcast its creation to other nodes?
		
		
		activityFeed = ActivityFeed.startUp(this.getCisId()); // this must be called just after the CisRecord has been set

	}
	

	public Set<CisParticipant> getMembersCss() {
		return membersCss;
	}


	public void setMembersCss(Set<CisParticipant> membersCss) {
		this.membersCss = membersCss;
	}
	

	@Override
	public Future<Boolean> addMember(String jid, String role) throws  CommunicationException{
		MembershipType typedRole;
		try{
			typedRole = MembershipType.valueOf(role);
		}catch(IllegalArgumentException e) {
			return new AsyncResult<Boolean>(new Boolean(false)); //the string was not valid
		}
		catch( NullPointerException e) {
			return new AsyncResult<Boolean>(new Boolean(false)); //the string was not valid
		}
		return new AsyncResult<Boolean>(this.addMember(jid, typedRole));
		
	}
	
	
	// internal implementation of the method above
	private boolean addMember(String jid, MembershipType role) throws  CommunicationException{
		
		IIdentity targetCssIdentity;
		try {
			targetCssIdentity = this.CISendpoint.getIdManager().fromJid(jid);
		} catch (InvalidFormatException e) {
			LOG.info("bad jid as input to addMember method");
			e.printStackTrace();
			return false;
		}
		
		LOG.info("add member invoked");
		if (role == null)
			role = MembershipType.participant; // default role is participant
		
		if (membersCss.add(new CisParticipant(jid, role))){
			// should we send a XMPP notification to all the users to say that the new member has been added to the group
			// I thought of that as a way to tell the participants CIS Managers that there is a new participant in that group
			// and the GUI can be updated with that new member
			Stanza sta;
/*			LOG.info("new member added, going to notify community");
			
			// 1) Notifying the added user

			
			CommunityManager cMan = new CommunityManager();
			Notification n = new Notification();
			SubscribedTo s = new SubscribedTo();
			s.setCisJid(this.getCisId());
			s.setCisRole(role.toString());
			n.setSubscribedTo(s);
			cMan.setNotification(n);
			
			LOG.info("finished building notification");


			Stanza sta = new Stanza(targetCssIdentity);
			CISendpoint.sendMessage(sta, cMan);
					
			LOG.info("notification sent to the new user");*/
			
			//2) Sending a notification to all the other users // TODO: probably change this to a pubsub notification
			
			//creating payload
			Participant p = new Participant();
			p.setJid(jid);
			p.setRole( ParticipantRole.fromValue(role.toString())  );
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
				try {
					targetCssIdentity = this.CISendpoint.getIdManager().fromJid(element.getMembersJid());
					sta = new Stanza(targetCssIdentity);
					CISendpoint.sendMessage(sta, c);
				} catch (InvalidFormatException e) {
					e.printStackTrace();
					LOG.warn("bad jid in between members list in the CIS!");
				}//new IdentityImpl(element.getMembersJid());

				
		     }
			LOG.info("notification sents to the existing user");

			return true;
		}else{
			return false;
		}
		
	}


	@Override
	public Future<Boolean> removeMemberFromCIS(String jid) throws  CommunicationException{
		
		IIdentity targetCssIdentity;
		try {
			targetCssIdentity = this.CISendpoint.getIdManager().fromJid(jid);
		} catch (InvalidFormatException e) {
			LOG.warn("bad jid when trying to delete from CIS!");
			e.printStackTrace();
			return new AsyncResult<Boolean>(new Boolean(false));
		}
		
		if (!this.removeMember(jid))
			return new AsyncResult<Boolean>(new Boolean(false));
		// if the user has been removed we must send him back a notification
		
		//2) Sending a notification to all the other users (maybe replace with pubsub later)
		
		CommunityManager cMan = new CommunityManager();
		Notification n = new Notification();
		DeleteMemberNotification s = new DeleteMemberNotification();
		s.setCommunityJid(this.getCisId());
		s.setMemberJid(jid);
		n.setDeleteMemberNotification(s);
		cMan.setNotification(n);
		
		
		Stanza sta = new Stanza(targetCssIdentity);
		CISendpoint.sendMessage(sta, cMan);
		
		return new AsyncResult<Boolean>(new Boolean(true));
		
	}
	
	
	// true if we were able to remove the user
	// false if not
	private boolean removeMember(String jid) throws  CommunicationException{
		
		//TODO: add a check if it is a valid JID
		
		LOG.info("remove member invoked");
		
		if (membersCss.contains(new CisParticipant(jid))){
			LOG.info("user is a participant of the community");
			
			// 1) Removing the user
			if (membersCss.remove( new CisParticipant(jid)) == false)
				return false;
			
			// should we send a notification to the user here?
			
			
			//2) Sending a notification to all the other users (maybe replace with pubsub later)
			
			CommunityManager cMan = new CommunityManager();
			Notification n = new Notification();
			DeleteMemberNotification s = new DeleteMemberNotification();
			s.setCommunityJid(this.getCisId());
			s.setMemberJid(jid);
			n.setDeleteMemberNotification(s);
			cMan.setNotification(n);
			
			LOG.info("finished building notification");

			Set<CisParticipant> se = this.getMembersCss();
			Iterator<CisParticipant> it = se.iterator();
			
			while(it.hasNext()){
				CisParticipant element = it.next();
				LOG.info("sending notification to " + element.getMembersJid());
				IIdentity targetCssIdentity = null;
				try {
					targetCssIdentity = this.CISendpoint.getIdManager().fromJid(element.getMembersJid());
					Stanza sta = new Stanza(targetCssIdentity);
					CISendpoint.sendMessage(sta, cMan);
				} catch (InvalidFormatException e) {
					e.printStackTrace();
					LOG.warn("bad jid in between members list in the CIS!");
				}//new IdentityImpl(element.getMembersJid());

				
		     }
			LOG.info("notification sents to the existing user");
			

			return true;
		}else{
			return false;
		}
		
	}	
	

	// constructor for creating a CIS from a CIS record, maybe the case when we are retrieving data from a database
	// TODO: review as it is almost empty!!
	public Cis(CisRecord cisRecord) {
		
		this.cisRecord = cisRecord; 
		
		//CISendpoint = 	new XCCommunicationMgr(cisRecord.getHost(), cisRecord.getCisId(),cisRecord.getPassword());
		
		
		// TODO: broadcast its creation to other nodes?

	}


	
	// equals comparing only cisRecord

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
		Cis other = (Cis) obj;
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
			LOG.info("community type received");
			Community c = (Community) payload;

			// JOIN
			if (c.getJoin() != null) {
				String jid = "";
				LOG.info("join received");
				String senderjid = stanza.getFrom().getBareJid();
				boolean addresult = false; 
				try {
					addresult = this.addMember(senderjid, MembershipType.participant);
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Community result = new Community();
				
				Participant p = new Participant();
				JoinResponse j = new JoinResponse();
				
				// information sent on the xmpp in case of failure or success
				j.setResult(addresult);
				p.setJid(jid);
				result.setCommunityJid(this.getCisId()); 
								
				if(addresult == true){
					// information sent on the xmpp just in the case of success
					p.setRole( ParticipantRole.fromValue("participant")  );
					result.setCommunityName(this.getName());
					result.setCommunityType(this.cisType);
					result.setMembershipMode(this.getMembershipCriteria());
					result.setOwnerJid(this.getOwnerId());
				}
					
				j.setParticipant(p);
				result.setJoinResponse(j);
					
				return result;

				//return result;
			}
			if (c.getLeave() != null) {
				LOG.info("get leave received");
				Community result = new Community();
				result.setCommunityJid(this.getCisId());
				String jid = stanza.getFrom().getBareJid();
				boolean b = false;
				try{
					b = this.removeMemberFromCIS(jid).get().booleanValue();
				}catch(CommunicationException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				LeaveResponse l = new LeaveResponse();
				l.setResult(b);
				result.setLeaveResponse(l);
				return result;
			}
			if (c.getWho() != null) {
				// WHO
				LOG.info("get who received");
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
				AddResponse ar = new AddResponse();
				result.setCommunityJid(this.getCisId());
				String senderJid = stanza.getFrom().getBareJid();
				Participant p = c.getAdd().getParticipant();
				ar.setParticipant(p);			
				
				
				if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
					//requester is not the owner
					ar.setResult(false);
				}else{
					if(p!= null && p.getJid() != null){
						String role = "";
						if (p.getRole() != null)				
							role = p.getRole().value();
						
						try{
							if(this.addMember(p.getJid(), MembershipType.valueOf(role))){
								ar.setParticipant(p);
								ar.setResult(true);
							}
							else{
								ar.setResult(false);
							}
						}
						catch(Exception e){
							e.printStackTrace();
							ar.setResult(false);
						}
					}					
					
				}
				
				
				return result;
				// END OF ADD
			}

			// get Info
			if (c.getGetInfo()!= null) {
				Community result = new Community();
				GetInfoResponse r = new GetInfoResponse();
				result.setMembershipMode(this.getMembershipCriteria());
				result.setOwnerJid(this.getOwnerId());
				result.setCommunityJid(this.getCisId());
				result.setCommunityName(this.getName());
				result.setCommunityType(this.getCisType());
				result.setDescription(this.getDescription());
				r.setResult(true);
				result.setGetInfoResponse(r);
				return result;

			}				// END OF GET INFO

			// set Info
			// at the moment we limit this to description and type
			if (c.getSetInfo()!= null) {
				Community result = new Community();
				SetInfoResponse r = new SetInfoResponse();
				String senderJid = stanza.getFrom().getBareJid();
				if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
					r.setResult(false);
				}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value
					if( (c.getCommunityType() != null) &&  (!c.getCommunityType().isEmpty()) && 
							(!c.getCommunityType().equals(this.getCisType()))) // if is not empty and is different from current value
						this.setCisType(c.getCommunityType());
					if( (c.getDescription() != null) &&  (!c.getDescription().isEmpty()) && 
							(!c.getDescription().equals(this.getDescription()))) // if is not empty and is different from current value
						this.setDescription(c.getDescription());
					r.setResult(true);						
				}
				
				result.setMembershipMode(this.getMembershipCriteria());
				result.setOwnerJid(this.getOwnerId());
				result.setCommunityJid(this.getCisId());
				result.setCommunityName(this.getName());
				result.setCommunityType(this.getCisType());				
				result.setDescription(this.getDescription());
				return result;

			}				// END OF GET INFO

			
			
		}
		return null;
	}

	
	@Override 
	public Future<Set<ICisParticipant>> getMemberList(){
		Set<ICisParticipant> s = new  HashSet<ICisParticipant>();
		s.addAll(this.getMembersCss());
		return new AsyncResult<Set<ICisParticipant>>(s);
	}
	
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}


	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		
		
	}


	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}


	public String getCisId() {
	
		return this.cisRecord.getCisJid();
	}



	

	//"destructor" class which send a message to all users and closes the connection immediately
	public boolean deleteCIS(){
		boolean ret = true;

		// TODO: do we need to make sure that at this point we are not taking any other XMPP input or api call?

		//**** delete all members and send them a xmpp notification that the community has been deleted
		CommunityManager message = new CommunityManager();
		Notification n = new Notification();
		DeleteNotification d = new DeleteNotification();
		d.setCommunityJid(this.getCisId());
		
		n.setDeleteNotification(d);
		message.setNotification(n);
		Session session = sessionFactory.openSession();
		Set<CisParticipant> s = this.getMembersCss();
		Iterator<CisParticipant> it = s.iterator();
		
		while(it.hasNext()){
			CisParticipant element = it.next();
			
			try {
				// send notification
				LOG.info("sending delete notification to " + element.getMembersJid());
				IIdentity targetCssIdentity = this.CISendpoint.getIdManager().fromJid(element.getMembersJid());//new IdentityImpl(element.getMembersJid());

				LOG.info("iidentity created");
				Stanza sta = new Stanza(targetCssIdentity);
				LOG.info("stanza created");

				this.CISendpoint.sendMessage(sta, message);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// delete user
			it.remove();
	     }
		
		session.close();
		//**** end of delete all members and send them a xmpp notification 
		
		//cisRecord = null; this cant be called as it will be used for comparisson later. I hope the garbage collector can take care of it...
		sharedServices = null; 
		activityFeed = null; // TODO: replace with proper way of destroying it
		
		
		ret = CISendpoint.UnRegisterCommManager();
		if(ret)
			CISendpoint = null;
		else
			LOG.warn("could not unregister CIS");
		//TODO: possibly do something in case we cant close it
		
		//cisIdentity =null;
		PubsubClient psc = null; // TODO: replace with proper way of destroying it

		membersCss = null; 
		
		return ret;
		
	}

	// getters and setters
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	@Override
	public String getName() {
		return this.cisRecord.getCisName();
	}

	@Override
	public String getOwnerId() {
		return this.owner;
	}

	@Override
	public String getCisType() {
		return this.cisType;
	}

	@Override
	public String setCisType(String type) {
		return this.cisType = type;
	}

	@Override
	public int getMembershipCriteria() {
		return this.cisRecord.getMembershipCriteria();
	}
	
	@Override
	public void getInfo(ICisManagerCallback callback){
		LOG.debug("local client call to get info from this CIS");

		
		Community c = new Community();
		c.setCommunityJid(this.getCisId());
		c.setCommunityName(this.getName());
		c.setCommunityType(this.getCisType());
		c.setOwnerJid(this.getOwnerId());
		c.setDescription(this.getDescription());
		c.setGetInfo("");
		
		callback.receiveResult(c);	
	}
	
}
