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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.CollectionOfElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.client.ActivityFeedClient;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.schema.activityfeed.AddActivityResponse;
import org.societies.api.schema.activityfeed.CleanUpActivityFeedResponse;
import org.societies.api.schema.activityfeed.DeleteActivityResponse;
import org.societies.api.schema.activityfeed.GetActivitiesResponse;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.community.AddMemberResponse;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.DeleteMemberResponse;
import org.societies.api.schema.cis.community.GetInfoResponse;
import org.societies.api.schema.cis.community.GetMembershipCriteriaResponse;
import org.societies.api.schema.cis.community.Join;
import org.societies.api.schema.cis.community.JoinResponse;
import org.societies.api.schema.cis.community.LeaveResponse;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.community.ParticipantRole;
import org.societies.api.schema.cis.community.Qualification;
import org.societies.api.schema.cis.community.SetInfoResponse;
import org.societies.api.schema.cis.community.SetMembershipCriteriaResponse;
import org.societies.api.schema.cis.community.WhoResponse;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.DeleteMemberNotification;
import org.societies.api.schema.cis.manager.DeleteNotification;
import org.societies.api.schema.cis.manager.Notification;
import org.societies.api.schema.cis.manager.SubscribedTo;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.cis.manager.CisParticipant.MembershipType;
import org.societies.cis.mgmtClient.CisManagerClient;

//import org.societies.api.schema.cis.community.GetInfo;

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
	@Transient
	private final static List<String> PACKAGES = Collections
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
	@Column(name="cis_id")
	private Long id;

	// minimun attributes
	@OneToOne(cascade=CascadeType.ALL)
	public CisRecord cisRecord;
	
	//@OneToOne(cascade=CascadeType.ALL)
	@Transient
	public IActivityFeed activityFeed = null;
	//TODO: should this be persisted?
	@Transient
	private ICommManager CISendpoint;
	@Transient
	IPrivacyPolicyManager privacyPolicyManager = null;
	@Transient
	IPrivacyDataManager privacyDataManager = null;
	
	public IPrivacyDataManager getPrivacyDataManager() {
		return privacyDataManager;
	}

	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
	}

	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
	}

	@Transient
	private IIdentity cisIdentity;
	//@Transient
	//private PubsubClient psc;
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval=true)
	@JoinTable(
            name="org_societies_cis_manager_Cis_CisParticipant",
            joinColumns = @JoinColumn( name="cis_id"),
            inverseJoinColumns = @JoinColumn( name="cisparticipant_id")
    )
	public Set<CisParticipant> membersCss; // TODO: this may be implemented in the CommunityManagement bundle. we need to define how they work together

	@Column
	public String cisType;
	@Column
	public String owner;
	@Column
	public String description = "";
	@Column
	public String cisName = "";
	
	
	
	//@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval=true)
	//@Transient
	//Set<MembershipCriteriaImp> cisCriteria = null;
	
	@CollectionOfElements(targetElement = java.lang.String.class,fetch=FetchType.EAGER)
	@CollectionTable(name="org_societies_cis_manager_Cis_criteria",joinColumns = @JoinColumn(name = "cis_id"))
	@Column(name="criteria",length=500)
	public Set<String> membershipCritOnDb;// we will store it in the db as "context,rank,operator,value1,value2"

	@Transient
	Hashtable<String, MembershipCriteria> cisCriteria = null;
	
	private Set<String> getMembershipCritOnDb() {
		return membershipCritOnDb;
	}

	private void setMembershipCritOnDb(Set<String> membershipCritOnDb) {
		this.membershipCritOnDb = membershipCritOnDb;
	}

	public boolean checkQualification(HashMap<String,String> qualification){
		for (String cisContext : cisCriteria.keySet()) { // loop through all my criterias
		    if(qualification.containsKey(cisContext)){
		    	MembershipCriteria m = cisCriteria.get(cisContext); // retrieves the context for that criteria
		    	if(m != null){ // if there is a rule we check it
		    		String valueToBeCompared = qualification.get(cisContext);
		    		if (! (m.getRule().checkRule(CtxAttributeValueType.STRING, valueToBeCompared))) //TODO: this CtxAttributeValueType.STRING should be changed!! 
		    			return false;
		    	}
		    }
		    else{// did not have a needed context attribute in its qualification
		    	return false;
		    }
		}
		return true;
	}

	private void buildCriteriaFromDb(){
		for (String s : membershipCritOnDb) { // loop through all my criterias
			LOG.warn("on the loop to build criteria with crit = " + s);
			String[] tokens = s.split(",");
			if(tokens == null || tokens.length < 4){
				LOG.warn("Badly coded criteria on db");
				return;
			}else{
				MembershipCriteria m = new MembershipCriteria();
				m.setRank(Integer.parseInt(tokens[1]));
				LOG.debug("rank set");
				Rule r = new Rule();
				r.setOperation(tokens[2]);
				LOG.debug("op set");
				List<String> o = new ArrayList<String>();
				o.add(tokens[3]);
				LOG.debug("token set");
				if(tokens.length>4)
					o.add(tokens[4]);
				
				if( (r.setValues(o) && m.setRule(r)) != true)
					LOG.warn("Badly typed criteria on db");
				LOG.debug("adding on table");
				cisCriteria.put(tokens[0], m);
				LOG.debug("added on table");
			}
		}
	}
	
	public boolean addCriteria(String contextAtribute, MembershipCriteria m){
		LOG.warn("adding criteria on db");
		
		String s = contextAtribute;
		if(m.getRule() == null || m.getRule().getOperation() == null || m.getRule().getValues() == null
				|| m.getRule().getValues().isEmpty()) return false;
		s +=  "," + m.getRank();
		s +=  "," + m.getRule().getOperation();
		LOG.warn("got operation");
		for(int i=0; i<m.getRule().getValues().size() && i<2; i++){
			s+=  "," + m.getRule().getValues().get(i);
		}
		LOG.warn("calling the list add inside the add criteria and s = " + s);
		membershipCritOnDb.add(s);
		LOG.warn("going to persist");
		this.updatePersisted(this);
		LOG.warn("before putting on the table");
		cisCriteria.put(contextAtribute, m);
		LOG.warn("criteria added on db");
		return true;
	}
	
	
	//to be used only for the constructor
	public boolean addCriteriaWithoutDBcall(String contextAtribute, MembershipCriteria m){
		LOG.warn("adding criteria on db");
		
		String s = contextAtribute;
		if(m.getRule() == null || m.getRule().getOperation() == null || m.getRule().getValues() == null
				|| m.getRule().getValues().isEmpty()) return false;
		s +=  "," + m.getRank();
		s +=  "," + m.getRule().getOperation();
		LOG.warn("got operation");
		for(int i=0; i<m.getRule().getValues().size() && i<2; i++){
			s+=  "," + m.getRule().getValues().get(i);
		}
		LOG.warn("calling the list add inside the add criteria and s = " + s);
		membershipCritOnDb.add(s);
		LOG.warn("going to persist");
		LOG.warn("before putting on the table");
		cisCriteria.put(contextAtribute, m);
		LOG.warn("criteria added on db");
		return true;
	}

	public boolean removeCriteria(String contextAtribute, MembershipCriteria m){
		if(cisCriteria.containsKey(contextAtribute) && cisCriteria.get(contextAtribute).equals(m)){
			//rule is there, lets remove it
			String s = contextAtribute;
			if(m.getRule() == null || m.getRule().getOperation() == null || m.getRule().getValues() == null
					|| m.getRule().getValues().isEmpty()) return false;
			s +=  "," + m.getRank();
			s +=  "," + m.getRule().getOperation();
			for(int i=0; i<m.getRule().getValues().size() && i<2; i++){
				s+=  "," + m.getRule().getValues().get(i);
			}
			if( membershipCritOnDb.remove(s) != true) return false;
			this.updatePersisted(this);
			cisCriteria.remove(contextAtribute); // TODO: maybe inver this from the remove
			return true;
			
		} else {
			return false;
		}
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public IActivityFeed getActivityFeed() {
		return activityFeed;
	}


	private void setActivityFeed(IActivityFeed activityFeed) {
		this.activityFeed = activityFeed;
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

	//  constructor of a CIS without a pre-determined ID or host
	public Cis(String cssOwner, String cisName, String cisType, ICISCommunicationMgrFactory ccmFactory, IPrivacyPolicyManager privacyPolicyManager, 
			   SessionFactory sessionFactory, String description, Hashtable<String, MembershipCriteria> inputCisCriteria,IActivityFeedManager iActivityFeedManager) {

		this.privacyPolicyManager = privacyPolicyManager;
		this.description = description;
		this.owner = cssOwner;
		this.cisType = cisType;
		this.cisName = cisName;
		
		membershipCritOnDb= new HashSet<String>();		
		membersCss = new HashSet<CisParticipant>();
		membersCss.add(new CisParticipant(cssOwner,MembershipType.owner));
		cisCriteria = new Hashtable<String, MembershipCriteria> ();		
		LOG.debug("before adding membership criteria");
		
		// adding membership criteria
		if(inputCisCriteria != null && inputCisCriteria.size() >0){
			Iterator<Map.Entry<String, MembershipCriteria>> it = inputCisCriteria.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, MembershipCriteria> pairs = (Map.Entry<String, MembershipCriteria>)it.next();
		        LOG.debug("going to add criteria of attribute" + pairs.getKey());
		        if (this.addCriteriaWithoutDBcall(pairs.getKey(), pairs.getValue()) == false)
		        	LOG.debug("Got a false return when trying to add the criteria on the db");// TODO: add an exception here
		        //it.remove(); // avoids a ConcurrentModificationException
		    }
		}

		LOG.debug("CIS editor created");
		try{ 
			CISendpoint = ccmFactory.getNewCommManager();
		} catch  (CommunicationException e) {
			e.printStackTrace();
			LOG.debug("could not start comm manager!");
		}
		
		LOG.debug("CIS got new comm manager");
		try {
		cisIdentity = CISendpoint.getIdManager().getThisNetworkNode();//CISendpoint.getIdManager().fromJid(CISendpoint.getIdManager().getThisNetworkNode().getJid());
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		LOG.debug("CIS endpoint created");
		try {
			CISendpoint.register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
			this.unregisterCIS();
			LOG.debug("could not start comm manager!");
		} 
		LOG.debug("CIS listener registered");
	
		cisRecord = new CisRecord(cisName, cisIdentity.getJid(), cssOwner, description, cisType);
		LOG.debug("getting a new activity feed");

		activityFeed = iActivityFeedManager.getOrCreateFeed(cssOwner, cisIdentity.getJid());
		
		// TODO: broadcast its creation to other nodes?
		LOG.debug("activityFeed: "+activityFeed);
		this.sessionFactory = sessionFactory;
        //activityFeed.setSessionFactory(this.sessionFactory);
		this.persist(this);
		
		IActivity iActivity = activityFeed.getEmptyIActivity();
		iActivity.setActor(this.getOwnerId());
		iActivity.setObject(cisIdentity.getJid());
		iActivity.setVerb("created");
		activityFeed.addActivity(iActivity, new   ActivityFeedClient())  ;
	}
		
	public void startAfterDBretrieval(SessionFactory sessionFactory,ICISCommunicationMgrFactory ccmFactory,IPrivacyPolicyManager privacyPolicyManager,
			IPrivacyDataManager	privacyDataManager, IActivityFeedManager iActivityFeedManager){

		
		this.privacyPolicyManager = privacyPolicyManager;
		this.privacyDataManager = privacyDataManager;
		// first Ill try without members
		

		try {
			CISendpoint = ccmFactory.getNewCommManager(this.getCisId());
		} catch (CommunicationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LOG.debug("retrieved COM manager");
	
		try {
		cisIdentity = CISendpoint.getIdManager().getThisNetworkNode();//CISendpoint.getIdManager().fromJid(CISendpoint.getIdManager().getThisNetworkNode().getJid());
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		LOG.debug("CIS endpoint created");
				
		try {
			CISendpoint.register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
			LOG.debug("could not start comm manager!");
			this.unregisterCIS();
		} 
		LOG.debug("CIS listener registered");
		
		this.setSessionFactory(sessionFactory);

		//session = sessionFactory.openSession();
		
		LOG.debug("building criteria from db");
		cisCriteria = new Hashtable<String, MembershipCriteria> ();
		this.buildCriteriaFromDb();
		LOG.debug("done building criteria from db");
		
		// retrieve activity feed
		this.activityFeed = iActivityFeedManager.getOrCreateFeed(this.getOwnerId(), cisIdentity.getJid());
		
		
//		if(null != this.psc){
//			try {
//				LOG.debug("restoring activ feed with pubsub");
//				activityFeed.startUp(sessionFactory,this.getCisId(),this.psc, this.CISendpoint.getIdManager().fromJid(getOwnerId()));
//			} catch (InvalidFormatException e) {
//				// TODO Auto-generated catch block
//				LOG.debug("restoring activ feed without pubsub");
//				e.printStackTrace();
//			} // this must be called just after the CisRecord has been set
//		}
//		else{
//			activityFeed.startUp(sessionFactory,this.getCisId());
//		}
		//activityFeed.getActivities("0 1339689547000");
	}
	

	public Set<CisParticipant> getMembersCss() {
		return membersCss;
	}


	public void setMembersCss(Set<CisParticipant> membersCss) {
		this.membersCss = membersCss;
	}
	
	// notifies cloud node
	private void nofityAddedUser(String jid, String role){
		
		Stanza sta;
		LOG.debug("new member added, going to notify the user");
		IIdentity targetCssIdentity = null;
		try {
			targetCssIdentity = this.CISendpoint.getIdManager().fromJid(jid);
		} catch (InvalidFormatException e) {
			LOG.debug("could not send addd notification");
			e.printStackTrace();
		}		
		
		CommunityManager cMan = new CommunityManager();
		Notification n = new Notification();
		SubscribedTo s = new SubscribedTo();
		Community com = new Community();
		this.fillCommmunityXMPPobj(com);
		s.setRole(role.toString());
		s.setCommunity(com);
		n.setSubscribedTo(s);
		cMan.setNotification(n);
		
		LOG.debug("finished building notification");

		sta = new Stanza(targetCssIdentity);
		try {
			CISendpoint.sendMessage(sta, cMan);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			LOG.debug("problem sending notification to cis");
			e.printStackTrace();
		}
				
		LOG.debug("notification sent to the new user");
	}
	

	@Override
	public boolean addMember(String jid, String role){
		MembershipType typedRole;
		try{
			typedRole = MembershipType.valueOf(role);
		}catch(IllegalArgumentException e) {
			return false; //the string was not valid
		}
		catch( NullPointerException e) {
			return false; //the string was not valid
		}
		boolean ret;
		ret = this.insertMember(jid, typedRole);

		

		// 1) Notifying the added user

		this.nofityAddedUser( jid,  role);	

		
		return ret;
	}
	
	
	// internal implementation of the method above
	private boolean insertMember(String jid, MembershipType role) {
		
		LOG.debug("add member invoked");
		if (role == null)
			role = MembershipType.participant; // default role is participant
		
		if (membersCss.add(new CisParticipant(jid, role))){
			
			//persist in database
			this.updatePersisted(this);
			
			//activityFeed notification
			IActivity iActivity = this.activityFeed.getEmptyIActivity();
			iActivity.setActor(jid);
			iActivity.setObject(cisIdentity.getJid());
			iActivity.setVerb("joined");
			
			// TODO: perhaps, treat dummy return
			activityFeed.addActivity(iActivity,new   ActivityFeedClient()) ;
	
			return true;
		}else{
			return false;
		}
		
	}


	@Override
	public boolean removeMemberFromCIS(String jid) {
		
		IIdentity targetCssIdentity;
		try {
			targetCssIdentity = this.CISendpoint.getIdManager().fromJid(jid);
		} catch (InvalidFormatException e) {
			LOG.warn("bad jid when trying to delete from CIS!");
			e.printStackTrace();
			return false;
		}
		
		if (!this.removeMember(jid))
			return false;
		
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
			LOG.debug("stanza created");
			this.CISendpoint.sendMessage(sta, message);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return true;
		
	}
	
	
	// true if we were able to remove the user
	// false if not
	private boolean removeMember(String jid) {
		
		//TODO: add a check if it is a valid JID
		
		LOG.debug("remove member invoked");

		
		if (membersCss.contains(new CisParticipant(jid))){
			LOG.debug("user is a participant of the community");
			
			// for database update
			
			CisParticipant temp;
			temp = this.getMember(jid);
			
			// 1) Removing the user
			if (membersCss.remove( new CisParticipant(jid)) == false)
				return false;
			
			// updating the database database
			this.updatePersisted(this);
			this.deletePersisted(temp);
			
			
			//activityFeed notification
			IActivity iActivity = activityFeed.getEmptyIActivity();
			iActivity.setActor(jid);
			iActivity.setObject(cisIdentity.getJid());
			iActivity.setVerb("left");
			
			
			activityFeed.addActivity(iActivity,new ActivityFeedClient());
			
			

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
		LOG.debug("get Query received");
		if (payload.getClass().equals(CommunityMethods.class)) {
			LOG.debug("community type received");
			CommunityMethods c = (CommunityMethods) payload;

			//>>>>>>>>>>>>>>>>>>>>>>>>>>>> JOIN >>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getJoin() != null) {
				//String jid = "";
				LOG.debug("join received");
				String senderjid = stanza.getFrom().getBareJid();

				// information sent on the xmpp in case of failure or success
				Community com = new Community();
				CommunityMethods result = new CommunityMethods();
				Participant p = new Participant();
				JoinResponse j = new JoinResponse();
				boolean addresult = false; 
				p.setJid(senderjid);
				this.fillCommmunityXMPPobj(com);
				
				j.setCommunity(com); // THE COMMUNITY MUST BE SET IN THE RESPONSE. THE CALLBACKS ARE COUNTING ON THIS!!
				result.setJoinResponse(j);				
				
				// checking the criteria
				if(this.cisCriteria.size()>0){
					Join join = (Join) c.getJoin();
					if(join.getQualification() != null && join.getQualification().size()>0 ){
						
						// retrieving from marshalled object the qualifications to be checked
						HashMap<String,String> qualification = new HashMap<String,String>();
						for (Qualification q : join.getQualification()) {
							qualification.put(q.getAttrib(), q.getValue());
						}
						
						if (this.checkQualification(qualification) == false){
							j.setResult(addresult);
							LOG.debug("qualification mismatched");
							return result;
						}
					}
					else{
						j.setResult(addresult);
						LOG.debug("qualification not found");
						return result;
					}
				}
				addresult = this.insertMember(senderjid, MembershipType.participant);
				j.setResult(addresult);
				// TODO: add the criteria to the response
				if(addresult == true){
					// information sent on the xmpp just in the case of success
					p.setRole( ParticipantRole.fromValue("participant")  );
				}
				j.setParticipant(p);
				return result;
			}
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>> LEAVE CIS >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getLeave() != null) {
				LOG.debug("get leave received");
				CommunityMethods result = new CommunityMethods();
				String jid = stanza.getFrom().getBareJid();
				boolean b = false;
				b = this.removeMember(jid);
				
				LeaveResponse l = new LeaveResponse();
				l.setCommunityJid(this.getCisId());
				l.setResult(b);
				result.setLeaveResponse(l);
				return result;
			}
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>> LIST MEMBERS >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getWhoRequest() != null) {
				// WHO
				LOG.debug("get who received");
				CommunityMethods result = new CommunityMethods();
				WhoResponse who = new WhoResponse();
				result.setWhoResponse(who);
				who.setResult(false);		
				if(null == c.getWhoRequest().getRequestor())return result; // fails if there is no requestor
				// otherwise we call locally
				Requestor requestor;
				try {
					requestor = RequestorUtils.toRequestor(c.getWhoRequest().getRequestor(),this.CISendpoint.getIdManager());
					CisManagerClient callbac = new CisManagerClient();
					this.getListOfMembers(requestor, callbac);
					CommunityMethods callbackResp = callbac.getComMethObj();
					if (null != callbackResp){
						who.setResult(callbackResp.getWhoResponse().isResult());
						who.setParticipant(callbackResp.getWhoResponse().getParticipant());
					}
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				return result;
			}
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>> ADD MEMBER >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getAddMember() != null) {
				// ADD
				CommunityMethods result = new CommunityMethods();
				AddMemberResponse ar = new AddMemberResponse();
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
							if(this.addMember(p.getJid(), role)){
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
			}
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>> REMOVE MEMBER >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getDeleteMember() != null) {
				// DELETE MEMBER
				CommunityMethods result = new CommunityMethods();
				DeleteMemberResponse dr = new DeleteMemberResponse();
				String senderJid = stanza.getFrom().getBareJid();
				Participant p = c.getDeleteMember().getParticipant();
				dr.setParticipant(p);			
//				if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
					//requester is not the owner
//					dr.setResult(false);
//				}else{
					try{
						dr.setResult(this.removeMemberFromCIS(p.getJid()));
					}
					catch(Exception e){
						e.printStackTrace();
						dr.setResult(false);
					}
//				}
				result.setDeleteMemberResponse(dr);
				return result;
			}
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>> GET INFO >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getGetInfo()!= null) {
				CommunityMethods result = new CommunityMethods();
				Community com = new Community();
				GetInfoResponse r = new GetInfoResponse();
				r.setResult(false);
				r.setCommunity(com);
				RequestorBean rb = c.getGetInfo().getRequestor();
				if(null == rb)return result; // fails if there is no requestor
				// otherwise we call locally
				Requestor requestor;
				try {
					requestor = RequestorUtils.toRequestor(rb,this.CISendpoint.getIdManager());
					CisManagerClient callbac = new CisManagerClient();
					this.getInfo(requestor, callbac);
					CommunityMethods callbackResp = callbac.getComMethObj();
					if (null != callbackResp){
						return callbackResp;
					}
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				return result;
			}
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>> SET INFO >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			// at the moment we limit this to description and type
			if (c.getSetInfo()!= null && c.getSetInfo().getCommunity() != null) {
				CommunityMethods result = new CommunityMethods();
				Community com = new Community();
				SetInfoResponse r = new SetInfoResponse();
				String senderJid = stanza.getFrom().getBareJid();
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value
					Community inputCommunity = c.getSetInfo().getCommunity();
					if( (inputCommunity.getCommunityType() != null) &&  (!inputCommunity.getCommunityType().isEmpty()) && 
							(!inputCommunity.getCommunityType().equals(this.getCisType()))) // if is not empty and is different from current value
						this.setCisType(inputCommunity.getCommunityType());
					if( (inputCommunity.getDescription() != null) &&  (!inputCommunity.getDescription().isEmpty()) && 
							(!inputCommunity.getDescription().equals(this.getDescription()))) // if is not empty and is different from current value
						this.setDescription(inputCommunity.getDescription());
					r.setResult(true);
					// updating at DB
					this.updatePersisted(this);					
				//}
					this.fillCommmunityXMPPobj(com);
					r.setCommunity(com);
					result.setSetInfoResponse(r);
				return result;
			}
			// >>>>>>>>>>>>>>>>>>>>>>>>>>>> GET MEMBERSHIP CRITERIA >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getGetMembershipCriteria()!= null) {
				CommunityMethods result = new CommunityMethods();
				GetMembershipCriteriaResponse g = new GetMembershipCriteriaResponse();				
				MembershipCrit m = new MembershipCrit();
				this.fillMembershipCritXMPPobj(m);
				g.setMembershipCrit(m);
				result.setGetMembershipCriteriaResponse(g);
				return result;
			}	
			// >>>>>>>>>>>>>>>>>>>>>>>>>>>> SET MEMBERSHIP CRITERIA
			if (c.getSetMembershipCriteria()!= null) {
				CommunityMethods result = new CommunityMethods();
				SetMembershipCriteriaResponse r = new SetMembershipCriteriaResponse();
				result.setSetMembershipCriteriaResponse(r);
				// retrieving from marshalled object the incoming criteria
				MembershipCrit m = c.getSetMembershipCriteria().getMembershipCrit();
				if (m!=null && m.getCriteria() != null && m.getCriteria().size()>0){
					
					// populate the hashtable
					for (Criteria crit : m.getCriteria()) {
						MembershipCriteria meb = new MembershipCriteria();
						meb.setRank(crit.getRank());
						Rule rule = new Rule();
						if( rule.setOperation(crit.getOperator()) == false) {r.setResult(false); return result;}
						ArrayList<String> a = new ArrayList<String>();
						a.add(crit.getValue1());
						if (crit.getValue2() != null && !crit.getValue2().isEmpty()) a.add(crit.getValue2()); 
						if( rule.setValues(a) == false) {r.setResult(false); return result;}
						meb.setRule(rule);
						if( this.addCriteria(crit.getAttrib(), meb) == false) {r.setResult(false); return result;}
					}
				}
				r.setResult(true);
				m = new MembershipCrit();
				this.fillMembershipCritXMPPobj(m);
				r.setMembershipCrit(m);
				return result;
			}	
		}
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>> ACTIVITIES MESSAGE BEAN >>>>>>>>>>>>>>>>>>>>>>>>>>>>
		if (payload.getClass().equals(MarshaledActivityFeed.class)) {
			LOG.info("activity feed type received");
            MarshaledActivityFeed c = (MarshaledActivityFeed) payload;

            // >>>>>>>>>>>>>>>>>>>>>>>>>>>> DELETE ACTIVITY >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getDeleteActivity() != null) {
                MarshaledActivityFeed result = new MarshaledActivityFeed();
				DeleteActivityResponse r = new DeleteActivityResponse();
				String senderJid = stanza.getFrom().getBareJid();
				
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value
				IActivity iActivity = activityFeed.getEmptyIActivity();
				iActivity.setActor(c.getDeleteActivity().getMarshaledActivity().getActor());
				iActivity.setObject(c.getDeleteActivity().getMarshaledActivity().getObject());
				iActivity.setTarget(c.getDeleteActivity().getMarshaledActivity().getTarget());
				iActivity.setVerb(c.getDeleteActivity().getMarshaledActivity().getVerb());

				ActivityFeedClient d = new ActivityFeedClient();
				activityFeed.deleteActivity(iActivity,d);
				
				MarshaledActivityFeed m = d.getActivityFeed();
				
				if(null != m && null != m.getDeleteActivityResponse()){
					r.setResult(m.getDeleteActivityResponse().isResult());
				}else{
					LOG.warn("no callback object after immediate call of delecte activity feed");
					r.setResult(false);
				}
				result.setDeleteActivityResponse(r);		
				return result;
			}
			// >>>>>>>>>>>>>>>>>>>>>>>>>>>> GET ACTIVITIES >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getGetActivities() != null) {
				LOG.debug("get activities called");
				org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
				GetActivitiesResponse r = new GetActivitiesResponse();
				String senderJid = stanza.getFrom().getBareJid();
				List<IActivity> iActivityList;
				//List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.MarshaledActivity>();
				
				ActivityFeedClient d = new ActivityFeedClient();
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value
					if(c.getGetActivities().getQuery()==null  ||  c.getGetActivities().getQuery().isEmpty())
						activityFeed.getActivities(c.getGetActivities().getTimePeriod(),d);
					else
						activityFeed.getActivities(c.getGetActivities().getQuery(),c.getGetActivities().getTimePeriod(),d);										
				//}
				
					MarshaledActivityFeed m = d.getActivityFeed();
					
					if(null != m && null != m.getGetActivitiesResponse()){
						r.setMarshaledActivity(m.getGetActivitiesResponse().getMarshaledActivity());
					}else{
						LOG.warn("no callback object after immediate call of get activities");
						// ill set an empty list
						r.setMarshaledActivity(new ArrayList<org.societies.api.schema.activity.MarshaledActivity>());
					}
					
				result.setGetActivitiesResponse(r);		
				return result;
			}
			// >>>>>>>>>>>>>>>>>>>>>>>>>>>> ADD ACTIVITY >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getAddActivity() != null) {
				org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
				AddActivityResponse r = new AddActivityResponse();
				String senderJid = stanza.getFrom().getBareJid();
				
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value
				IActivity iActivity = activityFeed.getEmptyIActivity();
				iActivity.setActor(c.getAddActivity().getMarshaledActivity().getActor());
				iActivity.setObject(c.getAddActivity().getMarshaledActivity().getObject());
				iActivity.setTarget(c.getAddActivity().getMarshaledActivity().getTarget());
				iActivity.setVerb(c.getAddActivity().getMarshaledActivity().getVerb());

				ActivityFeedClient d = new ActivityFeedClient();
				activityFeed.addActivity(iActivity,d);
				
				MarshaledActivityFeed m = d.getActivityFeed();				
				if(null != m && null != m.getAddActivityResponse()){
					r.setResult(m.getAddActivityResponse().isResult());
				}else{
					LOG.warn("no callback object after immediate call of add activity");
					r.setResult(false);
				}
				result.setAddActivityResponse(r);		
				return result;
			}
			// >>>>>>>>>>>>>>>>>>>>>>>>>>>> CLEAN UP ACTIVITIES >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getCleanUpActivityFeed() != null) {
				org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
				CleanUpActivityFeedResponse r = new CleanUpActivityFeedResponse();
				String senderJid = stanza.getFrom().getBareJid();
				
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value

				ActivityFeedClient d = new ActivityFeedClient();
				activityFeed.cleanupFeed(c.getCleanUpActivityFeed().getCriteria(),d);
				
				MarshaledActivityFeed m = d.getActivityFeed();
				
				if(null != m && null != m.getCleanUpActivityFeedResponse()){
					r.setResult(m.getCleanUpActivityFeedResponse().getResult());
				}else{
					LOG.warn("no callback object after immediate call of clean up activity feed");
					r.setResult(0);
				}

				result.setCleanUpActivityFeedResponse(r);		
				return result;
			}
		}	
		return null;
	}

	
	@Override 
	public Set<ICisParticipant> getMemberList(){
		LOG.debug("local get member list WITHOUT CALLBACK called");
		Set<ICisParticipant> s = new  HashSet<ICisParticipant>();
		s.addAll(this.getMembersCss());
		return s;
	}
	
	@Override
	public void getListOfMembers(ICisManagerCallback callback){
		LOG.debug("getListOfMembers: callback");
		LOG.debug("local get member list WITH CALLBACK called");

		CommunityMethods c = new CommunityMethods();
		WhoResponse w = new WhoResponse();
		c.setWhoResponse(w);
		w.setResult(true);
		
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
	
	public void getListOfMembers(Requestor requestor, ICisManagerCallback callback){
		LOG.debug("local get member list WITH CALLBACK called with requestor");

		CommunityMethods c = new CommunityMethods(); // object to be returned in case of failure
		WhoResponse w = new WhoResponse();
		c.setWhoResponse(w);
		w.setResult(false);
		// -- Access control
		if(null != this.privacyDataManager && null != requestor){
			ResponseItem resp = null;
			DataIdentifier dataId = null;
			try {
				dataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS.value() + "://" + this.getCisId() + "/cis-member-list");
				resp = this.privacyDataManager.checkPermission(requestor, dataId, new Action(ActionConstants.READ));
			} catch (MalformedCtxIdentifierException e) {
				LOG.error("The identifier of the requested data is malformed", e);
			} catch (PrivacyException e) {
				LOG.error("Error during access control of this data", e);
			}
			// No permission
			if(null == resp || !Decision.PERMIT.equals(resp.getDecision())){
				LOG.debug("This requestor: "+requestor);
				LOG.debug("doesn't have the permission to retrieve this data: "+dataId);
				callback.receiveResult(c);
				return;
			}
		}
		else{
			LOG.debug("Privacy data manager or requestor is null");
		}
		LOG.debug("permission was granted");
		// -- Retrieve the list of members
		getListOfMembers(callback);
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
		// the remove of the activityFeed will be done by the CIS MANAGER!!
		activityFeed = null;
		this.deletePersisted(this);
		
		// unregistering policy
		IIdentity cssOwnerId;
		try {
			cssOwnerId = this.CISendpoint.getIdManager().fromJid(this.getOwnerId());
			RequestorCis requestorCis = new RequestorCis(cssOwnerId, cisIdentity);	
			if(this.privacyPolicyManager != null)
				this.privacyPolicyManager.deletePrivacyPolicy(requestorCis);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			LOG.debug("bad format in cis owner jid at delete method");
			e1.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			LOG.debug("problem deleting policy");
			e.printStackTrace();
		}		
		
		while(it.hasNext()){
			CisParticipant element = it.next();
			
			try {
				// send notification
				LOG.debug("sending delete notification to " + element.getMembersJid());
				IIdentity targetCssIdentity = this.CISendpoint.getIdManager().fromJid(element.getMembersJid());//new IdentityImpl(element.getMembersJid());

				LOG.debug("iidentity created");
				Stanza sta = new Stanza(targetCssIdentity);
				LOG.debug("stanza created");

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
		
		

		//if(session!=null)
		//	session.close();
		//**** end of delete all members and send them a xmpp notification 
		
		//cisRecord = null; this cant be called as it will be used for comparisson later. I hope the garbage collector can take care of it...
		//activityFeed = null; // TODO: replace with proper way of destroying it
		
		
		ret = CISendpoint.UnRegisterCommManager();
		if(ret)
			CISendpoint = null;
		else
			LOG.warn("could not unregister CIS");
		//TODO: possibly do something in case we cant close it
		

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
		return this.cisRecord.getOwner();
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
	public void getInfo(ICisManagerCallback callback){
		LOG.debug("local client call to get info from this CIS without requestor");

		CommunityMethods result = new CommunityMethods();
		Community c = new Community();
		GetInfoResponse r = new GetInfoResponse();
		r.setResult(true);
		this.fillCommmunityXMPPobj(c);
		result.setGetInfoResponse(r);
		r.setCommunity(c);
		
		callback.receiveResult(result);	
	}
	
	@Override
	public void getInfo(Requestor req, ICisManagerCallback callback){
		LOG.debug("local client call to get info from this CIS with requestor");
		GetInfoResponse r = new GetInfoResponse();
		CommunityMethods result = new CommunityMethods();
		Community c = new Community ();
		r.setCommunity(c);
		result.setGetInfoResponse(r);
		
		CisManagerClient internalCallback = new CisManagerClient();
		
		getListOfMembers(req, internalCallback);
		CommunityMethods internallCabackResult = internalCallback.getComMethObj();
		r.setResult(true);
		this.fillCommmunityXMPPobj(c); // TODO: move the filling of the object to be conditional with privacy
		if(internallCabackResult.getWhoResponse().isResult()){
			c.setParticipant((internallCabackResult.getWhoResponse().getParticipant()));
		}
		
		callback.receiveResult(result);	
	}

	
	@Override
	public void setInfo(Community c, ICisManagerCallback callback) {
		// TODO Auto-generated method stub
		LOG.debug("local client call to set info from this CIS");

		SetInfoResponse r = new SetInfoResponse();

		//check if he is not trying to set things which cant be set
		if( ( (c.getCommunityJid() !=null) && (! c.getCommunityJid().equalsIgnoreCase(this.getCisId()))  ) ||
				(( (c.getCommunityName() !=null)) && (! c.getCommunityName().equals(this.getName()))  ) 
				 //( (!c.getCommunityType().isEmpty()) && (! c.getCommunityJid().equalsIgnoreCase(this.getCisType()))  ) ||
				//|| ( (c.getMembershipMode() != null) && ( c.getMembershipMode() != this.getMembershipCriteria()))
				
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
		CommunityMethods result = new CommunityMethods();		
		Community resp = new Community();
		this.fillCommmunityXMPPobj(resp);
		result.setSetInfoResponse(r);
		r.setCommunity(resp);
		
		callback.receiveResult(result);	
	}

	// session related methods
	private void persist(Object o){
        Session session = this.sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.save(o);
			t.commit();
			LOG.debug("Saving CIS object succeded!");
//			Query q = session.createQuery("select o from Cis aso");
			
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
			LOG.warn("Saving CIS object failed, rolling back");
		}finally{
			if(session!=null){
                session.close();
			}
			
		}
	}
	
	private void deletePersisted(Object o){
        Session session = this.sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.delete(o);
			t.commit();
			LOG.debug("Deleting object in CisManager succeded!");
//			Query q = session.createQuery("select o from Cis aso");
			
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
			LOG.warn("Deleting object in CisManager failed, rolling back");
		}finally{
            if(session!=null){
                session.close();
            }
		}
	}
	
	private void updatePersisted(Object o){
        Session session = this.sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try{
			session.update(o);
			t.commit();
			LOG.debug("Updated CIS object succeded!");
//			Query q = session.createQuery("select o from Cis aso");
			
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
			LOG.warn("Updating CIS object failed, rolling back");
		}finally{
            if(session!=null){
                session.close();
            }
			
		}
	}

	// local method
	public Hashtable<String, MembershipCriteria> getMembershipCriteria() {
		//return this.cisCriteria; // TODO: maybe we should return a copy instead
		//returns a home-made clone
		Hashtable<String, MembershipCriteria> h = new Hashtable<String, MembershipCriteria> ();
		
		for(Map.Entry<String, MembershipCriteria> entry : this.cisCriteria.entrySet()){
			MembershipCriteria orig =  entry.getValue();
			MembershipCriteria clone = new MembershipCriteria();
			clone.setRank(orig.getRank());
			clone.setRule(new Rule(orig.getRule().getOperation(), orig.getRule().getValues()));
			h.put(new String(entry.getKey()),clone );
		}
		return h;		
	}

	@Override
	public void getMembershipCriteria(ICisManagerCallback callback) {
		CommunityMethods result = new CommunityMethods();
		GetMembershipCriteriaResponse g = new GetMembershipCriteriaResponse();
		
		MembershipCrit m = new MembershipCrit();
		this.fillMembershipCritXMPPobj(m);
		g.setMembershipCrit(m);
		result.setGetMembershipCriteriaResponse(g);
		callback.receiveResult(result);
	}

	
	
	
	// internal method for filling up the MembershipCriteria marshalled object
	public void fillMembershipCritXMPPobj(MembershipCrit m){
		List<Criteria> l = new ArrayList<Criteria>();
		
		
		for(Map.Entry<String, MembershipCriteria> entry : this.cisCriteria.entrySet()){
			MembershipCriteria orig =  entry.getValue();
			Criteria c = new Criteria();
			c.setAttrib(entry.getKey());
			c.setOperator(orig.getRule().getOperation());
			c.setRank(orig.getRank());
			c.setValue1(orig.getRule().getValues().get(0));
			if(orig.getRule().getValues().size()==2)
				c.setValue1(orig.getRule().getValues().get(1));
			l.add(c);
		}
		m.setCriteria(l);
	}
		
	
	// internal method for filling up the Community marshalled object	
	public void fillCommmunityXMPPobj(Community c){
		c.setCommunityJid(this.getCisId());
		c.setCommunityName(this.getName());
		c.setCommunityType(this.getCisType());
		c.setOwnerJid(this.getOwnerId());
		c.setDescription(this.getDescription());
	
		/*RequestPolicy p;
		try {
			p = this.privacyPolicyManager.getPrivacyPolicy(new RequestorCis(this.CISendpoint.getIdManager().fromJid(owner) ,this.cisIdentity));
			if (p != null && p.toXMLString().isEmpty()==false){
				c.setPrivacyPolicy("<![CDATA[" + p.toXMLString() + "]]>");
			}
		} catch (PrivacyException e) {
			LOG.warn("Privacy excpetion when getting privacy on fillCommmunityXMPPobj");
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  */
		
		
		// fill criteria
		MembershipCrit m = new MembershipCrit();
		this.fillMembershipCritXMPPobj(m);
		c.setMembershipCrit(m);
		
	} 

	// internal callbacks
	
	
	// callback class to be used for local activity methods only
/*	class DummyActivitiesCall implements IActivityFeedCallback{
		MarshaledActivityFeed feedResponse = null;;
		public DummyActivitiesCall(){//IUserFeedback userFeedback){
			super();
			//this.userFeedback = userFeedback;
		}
		@Override
		public void receiveResult(MarshaledActivityFeed activityFeedObject) {
			feedResponse = activityFeedObject;
		}
		
		public MarshaledActivityFeed getFeedResponse(){
			return feedResponse;
		}
	};*/
	
	
	// subclass for local get list callbacks
	/*private class GetListCallBack implements ICisManagerCallback{
		public boolean done = false;
		public boolean resp = false;
		public List<Participant> l = null;
		
		public GetListCallBack (){super();}
		 
		public void receiveResult(CommunityMethods communityResultObject) {
			if(communityResultObject != null){
				resp = communityResultObject.getWhoResponse().isResult();
				l = communityResultObject.getWhoResponse().getParticipant();
			}
			
			this.done=true; 
			return;
			
		}
		public boolean isDone(){return done;}
		public boolean getResp(){return resp;}
		public List<Participant> getList(){return l;}
	}*/
	
}
