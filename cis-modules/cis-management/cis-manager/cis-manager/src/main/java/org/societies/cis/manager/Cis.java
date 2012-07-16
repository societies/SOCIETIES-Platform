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
import java.util.ArrayList;
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
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeed;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.cis.management.ICis;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.servicelifecycle.IServiceControlRemote;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryRemote;
import org.societies.cis.manager.CisParticipant.MembershipType;
import org.societies.identity.IdentityImpl;

import org.societies.api.schema.activity.Activity;
import org.societies.api.schema.activityfeed.Activityfeed;
import org.societies.api.schema.activityfeed.DeleteActivityResponse;
import org.societies.api.schema.cis.community.AddActivityResponse;
import org.societies.api.schema.cis.community.AddMemberResponse;
import org.societies.api.schema.cis.community.CleanUpActivityFeedResponse;
import org.societies.api.schema.cis.community.DeleteMemberResponse;
import org.societies.api.schema.cis.community.GetActivitiesResponse;
import org.societies.api.schema.cis.community.GetInfo;
import org.societies.api.schema.cis.community.GetInfoResponse;
import org.societies.api.schema.cis.community.JoinResponse;
import org.societies.api.schema.cis.community.LeaveResponse;
import org.societies.api.schema.cis.community.SetInfoResponse;
import org.societies.api.schema.cis.community.Who;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.community.ParticipantRole;
import org.societies.api.schema.cis.community.AddMember;
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
							"http://societies.org/api/schema/activityfeed",
					  		"http://societies.org/api/schema/cis/community"));
	//		.singletonList("http://societies.org/api/schema/cis/community");
	@Transient
	private final static List<String> PACKAGES = Collections
			//.singletonList("org.societies.api.schema.cis.community");
	.unmodifiableList( Arrays.asList("org.societies.api.schema.cis.manager",
			"org.societies.api.schema.activityfeed",
		"org.societies.api.schema.cis.community"));
	@Transient
	private SessionFactory sessionFactory;
	@Transient
	private Session session;
	
	
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
	
	//@OneToOne(cascade=CascadeType.ALL)
	@Transient
	public ActivityFeed activityFeed = new ActivityFeed();
	//TODO: should this be persisted?
	@Transient
	private ICommManager CISendpoint;
	@Transient
	IServiceDiscoveryRemote iServDiscRemote;
	@Transient
	IServiceControlRemote iServCtrlRemote;
	@Transient
	IPrivacyPolicyManager privacyPolicyManager;
	
	@Transient
	private IIdentity cisIdentity;
	@Transient
	private PubsubClient psc;
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval=true)
	public Set<CisParticipant> membersCss; // TODO: this may be implemented in the CommunityManagement bundle. we need to define how they work together
	@Column
	public String cisType;
	
	public String owner;
	
// extra attributes	
	
	@Transient
	public String permaLink; // all those have been moved to the Editor
	
	@Transient
	private String password = "none";
	@Transient
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

	public IServiceDiscoveryRemote getiServDiscRemote() {
		return iServDiscRemote;
	}

	public void setiServDiscRemote(IServiceDiscoveryRemote iServDiscRemote) {
		this.iServDiscRemote = iServDiscRemote;
	}

	public IServiceControlRemote getiServCtrlRemote() {
		return iServCtrlRemote;
	}

	public void setiServCtrlRemote(IServiceControlRemote iServCtrlRemote) {
		this.iServCtrlRemote = iServCtrlRemote;
	}

	


	private static Logger LOG = LoggerFactory
			.getLogger(Cis.class);	
	
	public Cis(){
		
	}

	// internal method
	public CisParticipant getMember(String cssJid){	
		Set<CisParticipant> se = this.getMembersCss();
		Iterator<CisParticipant> it = se.iterator();
		
		while(it.hasNext()){
			CisParticipant element = it.next();
			if (element.getMembersJid().equals(cssJid))
					return element;
		}
		return null;
		
	}


	// maximum constructor of a CIS without a pre-determined ID or host
	public Cis(String cssOwner, String cisName, String cisType, int mode,ICISCommunicationMgrFactory ccmFactory
	,String permaLink,String password,String host, String description,	IServiceDiscoveryRemote iServDiscRemote,IServiceControlRemote iServCtrlRemote,IPrivacyPolicyManager privacyPolicyManager, SessionFactory sessionFactory) {
		this(cssOwner, cisName, cisType, mode,ccmFactory,iServDiscRemote,iServCtrlRemote,privacyPolicyManager,sessionFactory);
		this.password = password;
		this.permaLink = permaLink;
		this.host = host;
		this.description = description;

	}

	

	// minimum constructor of a CIS without a pre-determined ID or host
	public Cis(String cssOwner, String cisName, String cisType, int mode,ICISCommunicationMgrFactory ccmFactory
			,IServiceDiscoveryRemote iServDiscRemote,IServiceControlRemote iServCtrlRemote,IPrivacyPolicyManager privacyPolicyManager, SessionFactory sessionFactory) {
		
		this.privacyPolicyManager = privacyPolicyManager;
		
		this.owner = cssOwner;
		this.cisType = cisType;
		
		this.iServCtrlRemote = iServCtrlRemote;
		this.iServDiscRemote = iServDiscRemote;
		
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
			iServCtrlRemote.registerCISEndpoint(CISendpoint);
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
		
		session = sessionFactory.openSession();
		System.out.println("activityFeed: "+activityFeed);
		activityFeed.startUp(session,this.getCisId()); // this must be called just after the CisRecord has been set
		
		this.persist(this);
		//activityFeed.getActivities("0 1339689547000");

	}
	
	public void startAfterDBretrieval(SessionFactory sessionFactory,ICISCommunicationMgrFactory ccmFactory,IPrivacyPolicyManager privacyPolicyManager){
		
		
		this.privacyPolicyManager = privacyPolicyManager;
		// first Ill try without members

		try {
			CISendpoint = ccmFactory.getNewCommManager(this.getCisId());
		} catch (CommunicationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LOG.info("retrieved COM manager");
	
		try {
		cisIdentity = CISendpoint.getIdManager().getThisNetworkNode();//CISendpoint.getIdManager().fromJid(CISendpoint.getIdManager().getThisNetworkNode().getJid());
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		LOG.info("CIS endpoint created");
				
		try {
			CISendpoint.register(this);
			//iServCtrlRemote.registerCISEndpoint(CISendpoint);
//			CISendpoint.register((IFeatureServer) iServCtrlRemote);
//			CISendpoint.register((IFeatureServer) iServDiscRemote);
		} catch (CommunicationException e) {
			e.printStackTrace();
			LOG.info("could not start comm manager!");
		} // TODO unregister??
		LOG.info("CIS listener registered");
		
		this.setSessionFactory(sessionFactory);

		session = sessionFactory.openSession();
		activityFeed.startUp(session,this.getCisId()); // this must be called just after the CisRecord has been set
		activityFeed.getActivities("0 1339689547000");
	}
	

	public Set<CisParticipant> getMembersCss() {
		return membersCss;
	}


	public void setMembersCss(Set<CisParticipant> membersCss) {
		this.membersCss = membersCss;
	}
	

	@Override
	public Future<Boolean> addMember(String jid, String role){
		MembershipType typedRole;
		try{
			typedRole = MembershipType.valueOf(role);
		}catch(IllegalArgumentException e) {
			return new AsyncResult<Boolean>(new Boolean(false)); //the string was not valid
		}
		catch( NullPointerException e) {
			return new AsyncResult<Boolean>(new Boolean(false)); //the string was not valid
		}
		boolean ret;
		ret = this.insertMember(jid, typedRole);

		
		Stanza sta;
		LOG.info("new member added, going to notify the user");
		IIdentity targetCssIdentity = null;
		try {
			targetCssIdentity = this.CISendpoint.getIdManager().fromJid(jid);
		} catch (InvalidFormatException e) {
			LOG.info("could not send addd notification");
			e.printStackTrace();
		}		
		// 1) Notifying the added user

		
		CommunityManager cMan = new CommunityManager();
		Notification n = new Notification();
		SubscribedTo s = new SubscribedTo();
		s.setCisJid(this.getCisId());
		s.setRole(role.toString());
		n.setSubscribedTo(s);
		cMan.setNotification(n);
		
		LOG.info("finished building notification");


		sta = new Stanza(targetCssIdentity);
		try {
			CISendpoint.sendMessage(sta, cMan);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			LOG.info("problem sending notification to cis");
			e.printStackTrace();
		}
				
		LOG.info("notification sent to the new user");		

		
		return new AsyncResult<Boolean>(new Boolean(ret));
	}
	
	
	// internal implementation of the method above
	private boolean insertMember(String jid, MembershipType role) {
		

		
		LOG.info("add member invoked");
		if (role == null)
			role = MembershipType.participant; // default role is participant
		
		if (membersCss.add(new CisParticipant(jid, role))){
			
			//persist in database
			this.updatePersisted(this);
			
			// should we send a XMPP notification to all the users to say that the new member has been added to the group
			// I thought of that as a way to tell the participants CIS Managers that there is a new participant in that group
			// and the GUI can be updated with that new member

			
			//2) Sending a notification to all the other users // TODO: probably change this to a pubsub notification
			
			//creating payload
/*			Participant p = new Participant();
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
			*/
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
		
		// 2) Notification to deleted user here
		
		
		CommunityManager message = new CommunityManager();
		Notification n = new Notification();
		DeleteMemberNotification d = new DeleteMemberNotification();
		d.setCommunityJid(this.getCisId());
		d.setMemberJid(jid);
		
		n.setDeleteMemberNotification(d);
		message.setNotification(n);

		try {
			targetCssIdentity = this.CISendpoint.getIdManager().fromJid(jid);
			Stanza sta = new Stanza(targetCssIdentity);			
			LOG.info("stanza created");
			this.CISendpoint.sendMessage(sta, message);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new AsyncResult<Boolean>(new Boolean(true));
		
	}
	
	
	// true if we were able to remove the user
	// false if not
	private boolean removeMember(String jid) throws  CommunicationException{
		
		//TODO: add a check if it is a valid JID
		
		LOG.info("remove member invoked");

		
		if (membersCss.contains(new CisParticipant(jid))){
			LOG.info("user is a participant of the community");
			
			// for database update
			
			CisParticipant temp;
			temp = this.getMember(jid);
			
			// 1) Removing the user
			if (membersCss.remove( new CisParticipant(jid)) == false)
				return false;
			
			// updating the database database
			this.updatePersisted(this);
			this.deletePersisted(temp);
			
			
	
			
			
			//3) Sending a notification to all the other users (maybe replace with pubsub later)
			
/*			CommunityManager cMan = new CommunityManager();
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
			LOG.info("notification sents to the existing user");*/
			

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
				addresult = this.insertMember(senderjid, MembershipType.participant);
				
				
				Community result = new Community();
				
				Participant p = new Participant();
				JoinResponse j = new JoinResponse();
				
				// information sent on the xmpp in case of failure or success
				j.setResult(addresult);
				p.setJid(jid);
				result.setCommunityJid(this.getCisId()); 
				result.setCommunityName(this.getName());
				result.setCommunityType(this.cisType);
				result.setMembershipMode(this.getMembershipCriteria());
								
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
					b = this.removeMember(jid);
				}catch(CommunicationException e){
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
			if (c.getAddMember() != null) {
				// ADD
				Community result = new Community();
				AddMemberResponse ar = new AddMemberResponse();
				result.setCommunityJid(this.getCisId());
				String senderJid = stanza.getFrom().getBareJid();
				Participant p = c.getAddMember().getParticipant();
				ar.setParticipant(p);			
				
				
//				if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
					//requester is not the owner
//					ar.setResult(false);
//				}else{
					if(p!= null && p.getJid() != null){
						String role = "";
						if (p.getRole() != null)				
							role = p.getRole().value();
						
						try{
							if(this.addMember(p.getJid(), role).get()){
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
					
//				}
				
				result.setAddMemberResponse(ar);
				return result;
				// END OF ADD
			}

			if (c.getDeleteMember() != null) {
				// DELETE MEMBER
				Community result = new Community();
				DeleteMemberResponse dr = new DeleteMemberResponse();
				result.setCommunityJid(this.getCisId());
				String senderJid = stanza.getFrom().getBareJid();
				Participant p = c.getDeleteMember().getParticipant();
				dr.setParticipant(p);			
				
				
//				if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
					//requester is not the owner
//					dr.setResult(false);
//				}else{
					try{
						dr.setResult(this.removeMemberFromCIS(p.getJid()).get());
					}
					catch(Exception e){
						e.printStackTrace();
						dr.setResult(false);
					}
					
//				}
				
				result.setDeleteMemberResponse(dr);
				return result;
				// END OF DELETE MEMBER
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
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value
					if( (c.getCommunityType() != null) &&  (!c.getCommunityType().isEmpty()) && 
							(!c.getCommunityType().equals(this.getCisType()))) // if is not empty and is different from current value
						this.setCisType(c.getCommunityType());
					if( (c.getDescription() != null) &&  (!c.getDescription().isEmpty()) && 
							(!c.getDescription().equals(this.getDescription()))) // if is not empty and is different from current value
						this.setDescription(c.getDescription());
					r.setResult(true);	
					
					// updating at DB
					this.updatePersisted(this);
					
				//}
				
				result.setMembershipMode(this.getMembershipCriteria());
				result.setOwnerJid(this.getOwnerId());
				result.setCommunityJid(this.getCisId());
				result.setCommunityName(this.getName());
				result.setCommunityType(this.getCisType());				
				result.setDescription(this.getDescription());
				return result;

			}				// END OF GET INFO

			
			// get Activities
			if (c.getGetActivities() != null) {
				Community result = new Community();
				GetActivitiesResponse r = new GetActivitiesResponse();
				String senderJid = stanza.getFrom().getBareJid();
				List<IActivity> iActivityList;
				List<org.societies.api.schema.activity.Activity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.Activity>();
				
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value
					if(c.getGetActivities().getQuery()!=null  &&  c.getGetActivities().getQuery().isEmpty())
						iActivityList = activityFeed.getActivities(c.getGetActivities().getTimePeriod());
					else
						iActivityList = activityFeed.getActivities(c.getGetActivities().getQuery(),c.getGetActivities().getTimePeriod());										
				//}
				

				Iterator<IActivity> it = iActivityList.iterator();
				
				while(it.hasNext()){
					IActivity element = it.next();
					org.societies.api.schema.activity.Activity a = new org.societies.api.schema.activity.Activity();
					a.setActor(element.getActor());
					a.setObject(a.getObject());
					a.setPublished(a.getPublished());
					a.setVerb(a.getVerb());
					marshalledActivList.add(a);
			     }
				
				r.setActivity(marshalledActivList);
				result.setGetActivitiesResponse(r);		
				return result;

			}				// END OF get ACTIVITIES
			
			// add Activity

			if (c.getAddActivity() != null) {
				Community result = new Community();
				AddActivityResponse r = new AddActivityResponse();
				String senderJid = stanza.getFrom().getBareJid();
				
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value
				IActivity iActivity = new org.societies.activity.model.Activity();
				iActivity.setActor(c.getAddActivity().getActivity().getActor());
				iActivity.setObject(c.getAddActivity().getActivity().getObject());
				iActivity.setTarget(c.getAddActivity().getActivity().getTarget());
				iActivity.setPublished(c.getAddActivity().getActivity().getPublished());
				iActivity.setVerb(c.getAddActivity().getActivity().getVerb());

				activityFeed.addCisActivity(iActivity);
				
				r.setResult(true); //TODO. add a return on the activity feed method
				
				
				result.setAddActivityResponse(r);		
				return result;

			}				// END OF add Activity
			
						
			
			// cleanup activities
			if (c.getCleanUpActivityFeed() != null) {
				Community result = new Community();
				CleanUpActivityFeedResponse r = new CleanUpActivityFeedResponse();
				String senderJid = stanza.getFrom().getBareJid();
				
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value

				
				r.setResult(activityFeed.cleanupFeed(c.getCleanUpActivityFeed().getCriteria())); //TODO. add a return on the activity feed method
				
				
				result.setCleanUpActivityFeedResponse(r);		
				return result;

			}				// END OF cleanup activities
			
		}
		if (payload.getClass().equals(Activityfeed.class)) {
			LOG.info("activity feed type received");
			Activityfeed c = (Activityfeed) payload;
			
			// delete Activity

			if (c.getDeleteActivity() != null) {
				Activityfeed result = new Activityfeed();
				DeleteActivityResponse r = new DeleteActivityResponse();
				String senderJid = stanza.getFrom().getBareJid();
				
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value
				IActivity iActivity = new org.societies.activity.model.Activity();
				iActivity.setActor(c.getDeleteActivity().getActivity().getActor());
				iActivity.setObject(c.getDeleteActivity().getActivity().getObject());
				iActivity.setTarget(c.getDeleteActivity().getActivity().getTarget());
				iActivity.setPublished(c.getDeleteActivity().getActivity().getPublished());
				iActivity.setVerb(c.getDeleteActivity().getActivity().getVerb());

				r.setResult(activityFeed.deleteActivity(iActivity));

				result.setDeleteActivityResponse(r);		
				return result;

			}				// END OF delete Activity
			
		}
		
		
		return null;
	}

	
	@Override 
	public Future<Set<ICisParticipant>> getMemberList(){
		LOG.debug("local get member list WITH CALLBACK called");
		Set<ICisParticipant> s = new  HashSet<ICisParticipant>();
		s.addAll(this.getMembersCss());
		return new AsyncResult<Set<ICisParticipant>>(s);
	}
	
	@Override
	public void getListOfMembers(ICisManagerCallback callback){
		LOG.debug("local get member list WITHOUT CALLBACK called");

		
		Community c = new Community();
		c.setCommunityJid(this.getCisId());
		c.setCommunityName(this.getName());
		c.setCommunityType(this.getCisType());
		c.setOwnerJid(this.getOwnerId());
		c.setDescription(this.getDescription());
		c.setGetInfo(new GetInfo());
		
		Who w = new Who();
		c.setWho(w);
		
		Set<CisParticipant> s = this.getMembersCss();
		Iterator<CisParticipant> it = s.iterator();
		
		List<Participant> l = new  ArrayList<Participant>();
		while(it.hasNext()){
			CisParticipant element = it.next();
			Participant p = new Participant();
			p.setJid(element.getMembersJid());
			p.setRole( ParticipantRole.fromValue(element.getMtype().toString())   );
			l.add(p);
	     }
		
		w.setParticipant(l);
		
		callback.receiveResult(c);	
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
	
		return this.cisRecord.getCisJID();
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
		Set<CisParticipant> s = this.getMembersCss();
		Iterator<CisParticipant> it = s.iterator();

		// deleting from DB
		activityFeed.clear();
		activityFeed = null;
		this.deletePersisted(this);
		
		// unregistering policy
		IIdentity cssOwnerId;
		try {
			cssOwnerId = this.CISendpoint.getIdManager().fromJid(this.getOwnerId());
			RequestorCis requestorCis = new RequestorCis(cssOwnerId, cisIdentity);	
			this.privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			LOG.info("bad format in cis owner jid at delete method");
			e1.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			LOG.info("problem deleting policy");
			e.printStackTrace();
		}		
		
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
		
		

		if(session!=null)
			session.close();
		//**** end of delete all members and send them a xmpp notification 
		
		//cisRecord = null; this cant be called as it will be used for comparisson later. I hope the garbage collector can take care of it...
		//activityFeed = null; // TODO: replace with proper way of destroying it
		
		
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

	public boolean unregisterCIS(){
		boolean ret = CISendpoint.UnRegisterCommManager();
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
		GetInfoResponse r = new GetInfoResponse();
		r.setResult(true);
		c.setCommunityJid(this.getCisId());
		c.setCommunityName(this.getName());
		c.setCommunityType(this.getCisType());
		c.setOwnerJid(this.getOwnerId());
		c.setMembershipMode(this.getMembershipCriteria());
		c.setDescription(this.getDescription());
		c.setGetInfoResponse(r);
		
		callback.receiveResult(c);	
	}

	
	@Override
	public void setInfo(Community c, ICisManagerCallback callback) {
		// TODO Auto-generated method stub
		LOG.debug("local client call to set info from this CIS");

		SetInfoResponse r = new SetInfoResponse();

		//check if he is not trying to set things which cant be set
		if( ( (c.getCommunityJid() !=null) && (! c.getCommunityJid().equalsIgnoreCase(this.getCisId()))  ) ||
				(( (c.getCommunityName() !=null)) && (! c.getCommunityName().equalsIgnoreCase(this.getName()))  ) ||
				 //( (!c.getCommunityType().isEmpty()) && (! c.getCommunityJid().equalsIgnoreCase(this.getCisType()))  ) ||
				 ( (c.getMembershipMode() != null) && ( c.getMembershipMode() != this.getMembershipCriteria())  )
				
				){
			r.setResult(false);
			
		}
		else{
			r.setResult(true);
			if(c.getDescription() != null &&  !c.getDescription().isEmpty())
				this.description = c.getDescription();
			if(c.getCommunityType() != null &&  !c.getCommunityType().isEmpty())
				this.cisType = c.getCommunityType();
			
			// commit in database
			this.updatePersisted(this);
			
		}
				
		Community resp = new Community();
		resp.setCommunityJid(this.getCisId());
		resp.setCommunityName(this.getName());
		resp.setCommunityType(this.getCisType());
		resp.setMembershipMode(this.getMembershipCriteria());
		resp.setOwnerJid(this.getOwnerId());
		resp.setDescription(this.getDescription());
		resp.setSetInfoResponse(r);
		
		callback.receiveResult(resp);	
	}

	
	
	// session related methods

	private void persist(Object o){
		Transaction t = session.beginTransaction();
		try{
			session.save(o);
			t.commit();
			LOG.info("Saving CIS object succeded!");
//			Query q = session.createQuery("select o from Cis aso");
			
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
			LOG.warn("Saving CIS object failed, rolling back");
		}finally{
			if(session!=null){
			}
			
		}
	}
	
	
	private void deletePersisted(Object o){
		Transaction t = session.beginTransaction();
		try{
			session.delete(o);
			t.commit();
			LOG.info("Deleting object in CisManager succeded!");
//			Query q = session.createQuery("select o from Cis aso");
			
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
			LOG.warn("Deleting object in CisManager failed, rolling back");
		}finally{
		}
	}
	
	private void updatePersisted(Object o){
		Transaction t = session.beginTransaction();
		try{
			session.update(o);
			t.commit();
			LOG.info("Updated CIS object succeded!");
//			Query q = session.createQuery("select o from Cis aso");
			
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
			LOG.warn("Updating CIS object failed, rolling back");
		}finally{
			
		}
	}
	
	
	// TODO
	@Override
	public void addCisActivity(IActivity activity,ICisManagerCallback callback){
		
			Community result = new Community();
			AddActivityResponse r = new AddActivityResponse();

			activityFeed.addCisActivity(activity);
			
			r.setResult(true); //TODO. add a return on the activity feed method
			
			
			result.setAddActivityResponse(r);		
			callback.receiveResult(result);
		
	}
	
	@Override
	public void getActivities(String timePeriod,ICisManagerCallback callback){
		Community result = new Community();
		GetActivitiesResponse r = new GetActivitiesResponse();
		List<IActivity> iActivityList;
		List<org.societies.api.schema.activity.Activity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.Activity>();
		
		iActivityList = activityFeed.getActivities(timePeriod);
		

		Iterator<IActivity> it = iActivityList.iterator();
		
		while(it.hasNext()){
			IActivity element = it.next();
			org.societies.api.schema.activity.Activity a = new org.societies.api.schema.activity.Activity();
			a.setActor(element.getActor());
			a.setObject(a.getObject());
			a.setPublished(a.getPublished());
			a.setVerb(a.getVerb());
			marshalledActivList.add(a);
	     }
		
		r.setActivity(marshalledActivList);
		result.setGetActivitiesResponse(r);		
		
		callback.receiveResult(result);
				
	}
	
	
}
