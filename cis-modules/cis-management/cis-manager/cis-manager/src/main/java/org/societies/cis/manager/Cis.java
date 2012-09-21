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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.CollectionOfElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.PersistedActivityFeed;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
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
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.servicelifecycle.IServiceControlRemote;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryRemote;
import org.societies.api.schema.activityfeed.*;
import org.societies.api.schema.cis.community.*;
import org.societies.api.schema.cis.manager.*;
import org.societies.cis.manager.CisParticipant.MembershipType;
import org.springframework.scheduling.annotation.AsyncResult;

import javax.persistence.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Future;

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
	@Column(name="cis_id")
	private Long id;



// minimun attributes
	@OneToOne(cascade=CascadeType.ALL)
	public CisRecord cisRecord;
	
	//@OneToOne(cascade=CascadeType.ALL)
	@Transient
	public PersistedActivityFeed activityFeed = new PersistedActivityFeed();
	//TODO: should this be persisted?
	@Transient
	private ICommManager CISendpoint;
	@Transient
	IServiceDiscoveryRemote iServDiscRemote = null;
	@Transient
	IServiceControlRemote iServCtrlRemote = null;
	@Transient
	IPrivacyPolicyManager privacyPolicyManager = null;
	
	
	
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
	}



	@Transient
	private IIdentity cisIdentity;
	@Transient
	private PubsubClient psc;
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
				LOG.info("rank set");
				Rule r = new Rule();
				r.setOperation(tokens[2]);
				LOG.info("op set");
				List<String> o = new ArrayList<String>();
				o.add(tokens[3]);
				LOG.info("token set");
				if(tokens.length>4)
					o.add(tokens[4]);
				
				if( (r.setValues(o) && m.setRule(r)) != true)
					LOG.warn("Badly typed criteria on db");
				LOG.info("adding on table");
				cisCriteria.put(tokens[0], m);
				LOG.info("added on table");
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
			
		}else{
			return false;
		}
		
	}

	
	@Column
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
	public IActivityFeed getActivityFeed() {
		return activityFeed;
	}


	private void setActivityFeed(PersistedActivityFeed activityFeed) {
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





	//  constructor of a CIS without a pre-determined ID or host
	public Cis(String cssOwner, String cisName, String cisType, ICISCommunicationMgrFactory ccmFactory
			,IServiceDiscoveryRemote iServDiscRemote,IServiceControlRemote iServCtrlRemote,
			IPrivacyPolicyManager privacyPolicyManager, SessionFactory sessionFactory,
			String description, Hashtable<String, MembershipCriteria> inputCisCriteria) {
		
		this.privacyPolicyManager = privacyPolicyManager;
		
		this.description = description;
		
		this.owner = cssOwner;
		this.cisType = cisType;
		
		this.iServCtrlRemote = iServCtrlRemote;
		this.iServDiscRemote = iServDiscRemote;
		
		membershipCritOnDb= new HashSet<String>();
		
		membersCss = new HashSet<CisParticipant>();
		membersCss.add(new CisParticipant(cssOwner,MembershipType.owner));

		cisCriteria = new Hashtable<String, MembershipCriteria> ();
		
		LOG.info("before adding membership criteria");
		
		// adding membership criteria
		if(inputCisCriteria != null && inputCisCriteria.size() >0){
			Iterator<Map.Entry<String, MembershipCriteria>> it = inputCisCriteria.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, MembershipCriteria> pairs = (Map.Entry<String, MembershipCriteria>)it.next();
		        LOG.info("going to add criteria of attribute" + pairs.getKey());
		        if (this.addCriteriaWithoutDBcall(pairs.getKey(), pairs.getValue()) == false)
		        	LOG.info("Got a false return when trying to add the criteria on the db");// TODO: add an exception here
		        //it.remove(); // avoids a ConcurrentModificationException
		    }
		}

//		m.setMinValue("Edinburgh");
//		m.setMaxValue("Edinburgh");
//		cisCriteria.add(m); // for test purposes only
		
		
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
			this.unregisterCIS();
			LOG.info("could not start comm manager!");
		} 
		LOG.info("CIS listener registered");
		
		
		// TODO: we have to get a proper identity and pwd for the CIS...
		cisRecord = new CisRecord(cisName, cisIdentity.getJid());
		
		LOG.info("CIS creating pub sub service");
		
//		PubsubServiceRouter psr = new PubsubServiceRouter(CISendpoint);

		
		LOG.info("CIS pub sub service created");
		
		//this.psc = psc;
		
		LOG.info("CIS autowired PubSubClient");
		// TODO: broadcast its creation to other nodes?
		
		//session = sessionFactory.openSession();
		System.out.println("activityFeed: "+activityFeed);
		activityFeed.startUp(sessionFactory,this.getCisId()); // this must be called just after the CisRecord has been set
		this.sessionFactory = sessionFactory;
        //activityFeed.setSessionFactory(this.sessionFactory);
		this.persist(this);
		
		IActivity iActivity = new org.societies.activity.model.Activity();
		iActivity.setActor(this.getOwnerId());
		iActivity.setObject(cisIdentity.getJid());
		iActivity.setPublished(System.currentTimeMillis()+"");
		iActivity.setVerb("created");
		
		
		activityFeed.addActivity(iActivity);
		
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
			this.unregisterCIS();
		} 
		LOG.info("CIS listener registered");
		
		this.setSessionFactory(sessionFactory);

		//session = sessionFactory.openSession();
		
		LOG.info("building criteria from db");
		cisCriteria = new Hashtable<String, MembershipCriteria> ();
		this.buildCriteriaFromDb();
		LOG.info("done building criteria from db");
		
		
		activityFeed.startUp(sessionFactory,this.getCisId()); // this must be called just after the CisRecord has been set
		activityFeed.getActivities("0 1339689547000");
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
		LOG.info("new member added, going to notify the user");
		IIdentity targetCssIdentity = null;
		try {
			targetCssIdentity = this.CISendpoint.getIdManager().fromJid(jid);
		} catch (InvalidFormatException e) {
			LOG.info("could not send addd notification");
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

		

		// 1) Notifying the added user

		this.nofityAddedUser( jid,  role);	

		
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
			
			//activityFeed notification
			IActivity iActivity = new org.societies.activity.model.Activity();
			iActivity.setActor(jid);
			iActivity.setObject(cisIdentity.getJid());
			iActivity.setPublished(System.currentTimeMillis()+"");
			iActivity.setVerb("joined");
			
			
			activityFeed.addActivity(iActivity);
	
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
			
			
			//activityFeed notification
			IActivity iActivity = new org.societies.activity.model.Activity();
			iActivity.setActor(jid);
			iActivity.setObject(cisIdentity.getJid());
			iActivity.setPublished(System.currentTimeMillis()+"");
			iActivity.setVerb("left");
			
			
			activityFeed.addActivity(iActivity);
			
			

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
		if (payload.getClass().equals(CommunityMethods.class)) {
			LOG.info("community type received");
			CommunityMethods c = (CommunityMethods) payload;

			// JOIN
			if (c.getJoin() != null) {
				//String jid = "";
				LOG.info("join received");
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
							LOG.info("qualification mismatched");
							return result;
						}
							
					}
					else{
						j.setResult(addresult);
						LOG.info("qualification not found");
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

				//return result;
			}
			if (c.getLeave() != null) {
				LOG.info("get leave received");
				CommunityMethods result = new CommunityMethods();
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
				CommunityMethods result = new CommunityMethods();
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
				CommunityMethods result = new CommunityMethods();
				Community com = new Community();
				GetInfoResponse r = new GetInfoResponse();
				this.fillCommmunityXMPPobj(com);
				r.setResult(true);
				r.setCommunity(com);
				result.setGetInfoResponse(r);
				return result;

			}				// END OF GET INFO

			// set Info
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

			}				// END OF GET INFO
			
			// get Membership Criteria
			if (c.getGetMembershipCriteria()!= null) {
				CommunityMethods result = new CommunityMethods();
				GetMembershipCriteriaResponse g = new GetMembershipCriteriaResponse();				
				MembershipCrit m = new MembershipCrit();
				this.fillMembershipCritXMPPobj(m);
				g.setMembershipCrit(m);
				result.setGetMembershipCriteriaResponse(g);
				return result;

			}	
			
			// set Membership Criteria
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

			
			
			// get Activities
			if (c.getGetActivities() != null) {
				LOG.info("get activities called");
				org.societies.api.schema.activityfeed.Activityfeed result = new org.societies.api.schema.activityfeed.Activityfeed();
				GetActivitiesResponse r = new GetActivitiesResponse();
				String senderJid = stanza.getFrom().getBareJid();
				List<IActivity> iActivityList;
				List<org.societies.api.schema.activity.Activity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.Activity>();
				
				//if(!senderJid.equalsIgnoreCase(this.getOwnerId())){//first check if the one requesting the add has the rights
				//	r.setResult(false);
				//}else{
					//if((!c.getCommunityName().isEmpty()) && (!c.getCommunityName().equals(this.getName()))) // if is not empty and is different from current value
					if(c.getGetActivities().getQuery()==null  ||  c.getGetActivities().getQuery().isEmpty())
						iActivityList = activityFeed.getActivities(c.getGetActivities().getTimePeriod());
					else
						iActivityList = activityFeed.getActivities(c.getGetActivities().getQuery(),c.getGetActivities().getTimePeriod());										
				//}
				
					LOG.info("loacl query worked activities called");
					this.activityFeed.iactivToMarshActv(iActivityList, marshalledActivList);

				/*	
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
				*/
					LOG.info("finished the marshling");
				r.setActivity(marshalledActivList);
				result.setGetActivitiesResponse(r);		
				return result;

			}				// END OF get ACTIVITIES
			
			// add Activity

			if (c.getAddActivity() != null) {
				org.societies.api.schema.activityfeed.Activityfeed result = new org.societies.api.schema.activityfeed.Activityfeed();
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

				activityFeed.addActivity(iActivity);
				
				r.setResult(true); //TODO. add a return on the activity feed method
				
				
				result.setAddActivityResponse(r);		
				return result;

			}				// END OF add Activity
			
						
			
			// cleanup activities
			if (c.getCleanUpActivityFeed() != null) {
				org.societies.api.schema.activityfeed.Activityfeed result = new org.societies.api.schema.activityfeed.Activityfeed();
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
		
		
		return null;
	}

	
	@Override 
	public Future<Set<ICisParticipant>> getMemberList(){
		LOG.debug("local get member list WITHOUT CALLBACK called");
		Set<ICisParticipant> s = new  HashSet<ICisParticipant>();
		s.addAll(this.getMembersCss());
		return new AsyncResult<Set<ICisParticipant>>(s);
	}
	
	@Override
	public void getListOfMembers(ICisManagerCallback callback){
		LOG.debug("local get member list WITH CALLBACK called");

		
		CommunityMethods c = new CommunityMethods();
	//	c.setCommunityJid(this.getCisId());
	//	c.setCommunityName(this.getName());
	//	c.setCommunityType(this.getCisType());
	//	c.setOwnerJid(this.getOwnerId());
	//	c.setDescription(this.getDescription());
	//	c.setGetInfo(new GetInfo());
		
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
			if(this.privacyPolicyManager != null)
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
	public void getInfo(ICisManagerCallback callback){
		LOG.debug("local client call to get info from this CIS");

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
			LOG.info("Saving CIS object succeded!");
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
			LOG.info("Deleting object in CisManager succeded!");
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
			LOG.info("Updated CIS object succeded!");
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
		
		// fill criteria
		MembershipCrit m = new MembershipCrit();
		this.fillMembershipCritXMPPobj(m);
		c.setMembershipCrit(m);
		
	} 

	
}
