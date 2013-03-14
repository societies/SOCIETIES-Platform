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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisRemote;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.logging.IPerformanceMessage;
import org.societies.api.internal.logging.PerformanceMessage;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.util.remote.Util;
import org.societies.api.internal.security.policynegotiator.INegotiation;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.Join;
import org.societies.api.schema.cis.community.JoinResponse;
import org.societies.api.schema.cis.community.Leave;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Qualification;
import org.societies.api.schema.cis.community.WhoRequest;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.cis.manager.AskCisManagerForJoinResponse;
import org.societies.api.schema.cis.manager.AskCisManagerForLeaveResponse;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.Create;
import org.societies.api.schema.cis.manager.Delete;
import org.societies.api.schema.cis.manager.DeleteMemberNotification;
import org.societies.api.schema.cis.manager.ListCrit;
import org.societies.api.schema.cis.manager.ListResponse;
import org.societies.api.schema.identity.RequestorBean;
import org.springframework.scheduling.annotation.AsyncResult;
//import org.societies.api.comm.xmpp.pubsub.PubsubClient;
//import org.societies.api.schema.cis.community.Leave;
//import org.societies.api.schema.cis.community.Leave;


// this is the class which manages all the CIS from a CSS
// for the class responsible for editing and managing each CIS instance, consult the CIS

/**
 * @author Thomas Vilarinho (Sintef)
*/

public class CisManager implements ICisManager, IFeatureServer{//, ICommCallback{

	int nbOfCreatedCIS = 0;
	int nbOfSubscribedCIS = 0;
	int nbOfUnsubscribedCIS = 0;
	int nbOfDeletedCIS = 0;
	
	List<Cis> ownedCISs; 
	ICISCommunicationMgrFactory ccmFactory;
	IIdentity cisManagerId;
	private ICommManager iCommMgr;
	List<CisSubscribedImp> subscribedCISs;
	private SessionFactory sessionFactory;
	ICisDirectoryRemote iCisDirRemote = null;

	private IPrivacyPolicyManager privacyPolicyManager = null;
	private IEventMgr eventMgr = null;
	private ICtxBroker internalCtxBroker = null;

	private INegotiation negotiator;
	private IPrivacyDataManager privacyDataManager;

	//private PubsubClient pubsubClient;
	
	private IUserFeedback iUsrFeedback = null;
	//Autowiring gets and sets
	private boolean privacyPolicyNegotiationIncluded;
    private IActivityFeedManager iActivityFeedManager;

    
    public IActivityFeedManager getiActivityFeedManager() {
		return iActivityFeedManager;
	}

	public void setiActivityFeedManager(IActivityFeedManager iActivityFeedManager) {
		this.iActivityFeedManager = iActivityFeedManager;
	}

	public IUserFeedback getiUsrFeedback() {
		return iUsrFeedback;
	}

	public void setiUsrFeedback(IUserFeedback iUsrFeedback) {
		this.iUsrFeedback = iUsrFeedback;
	}

	
	public INegotiation getNegotiator() {
		return negotiator;
	}

	/*public PubsubClient getPubsubClient() {
		return pubsubClient;
	}

	public void setPubsubClient(PubsubClient pubsubClient) {
		LOG.info("pubsub set on CIS Manager");
		this.pubsubClient = pubsubClient;
		List<String> classList = Collections 
				.unmodifiableList( Arrays.asList("org.societies.api.schema.activity.MarshaledActivity"));
		
    	try {
    		pubsubClient.addSimpleClasses(classList);
		} catch (ClassNotFoundException e1) {
			LOG.warn("error adding classes at pubsub at activityfeed pubsub");
			e1.printStackTrace();
			
		}
	}*/

	public IPrivacyDataManager getPrivacyDataManager() {
		return privacyDataManager;
	}

	public void setNegotiator(INegotiation negotiator) {
		LOG.info("negotiator set");
		this.negotiator = negotiator;
	}
	public IPrivacyPolicyManager getPrivacyPolicyManager() {
		return privacyPolicyManager;
	}
	public ICtxBroker getInternalCtxBroker() {
		return internalCtxBroker;
	}
	public void setInternalCtxBroker(ICtxBroker internalCtxBroker) {
		this.internalCtxBroker = internalCtxBroker;
	}

	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	
	public ICISCommunicationMgrFactory getCcmFactory() {
		return ccmFactory;
	}

	public void setCcmFactory(ICISCommunicationMgrFactory ccmFactory) {
		this.ccmFactory = ccmFactory;
	}


	public ICommManager getICommMgr() {
		return getiCommMgr();
	}

	public void setICommMgr(ICommManager cSSendpoint) {
		setiCommMgr(cSSendpoint);
	}


	public ICisDirectoryRemote getiCisDirRemote() {
		return iCisDirRemote;
	}
	public void setiCisDirRemote(ICisDirectoryRemote iCisDirRemote) {
		this.iCisDirRemote = iCisDirRemote;
	}
	

	
	public void startup(){
		//ActivityFeed ret = null;
	
		Session session = sessionFactory.openSession();
		try{
			this.ownedCISs = session.createCriteria(Cis.class).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
			this.subscribedCISs = session.createCriteria(CisSubscribedImp.class).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

			LOG.info("Nb of subscri CIS is " + this.subscribedCISs.size());
		}catch(Exception e){
			LOG.error("CISManager startup queries failed..");
			e.printStackTrace();
		}finally{
			if(session!=null)
				session.close();
		}
		
		
		
		Iterator<Cis> it = ownedCISs.iterator();
		 
		while(it.hasNext()){
			 Cis element = it.next();
			 element.startAfterDBretrieval(this.getSessionFactory(),this.getCcmFactory(),this.privacyPolicyManager, 
					 this.privacyDataManager,this.iActivityFeedManager);
			 // publish an internal event to notify the restore
			if(this.getEventMgr() != null){
				Community c = new Community();
				element.fillCommmunityXMPPobj(c);
				InternalEvent event = new InternalEvent(EventTypes.CIS_RESTORE, "restore of CIS", element.getCisId(), c);
				try {
					this.getEventMgr().publishInternalEvent(event);
				} catch (EMSException e) {
					LOG.error("error trying to internally publish CREATE event");
					e.printStackTrace();
					
				}
			}

			 
	     }
		
	//	for(Cis cis : ownedCISs){
	//		cis.startAfterDBretrieval(this.getSessionFactory(),this.getCcmFactory());
	//	}
		Iterator<CisSubscribedImp> i = this.subscribedCISs.iterator();
		 
		while(i.hasNext()){
			CisSubscribedImp element = i.next();
			 element.startAfterDBretrieval(this);
	     }
				
	}

	private final static List<String> NAMESPACES = Collections
			.unmodifiableList( Arrays.asList("http://societies.org/api/schema/cis/manager",
					"http://societies.org/api/schema/activityfeed",	  		
					"http://societies.org/api/schema/cis/community"));
			//.singletonList("http://societies.org/api/schema/cis/manager");
	private final static List<String> PACKAGES = Collections
		//	.singletonList("org.societies.api.schema.cis.manager");
			.unmodifiableList( Arrays.asList("org.societies.api.schema.cis.manager",
					"org.societies.api.schema.activityfeed",
					"org.societies.api.schema.cis.community"));

	private static Logger LOG = LoggerFactory
			.getLogger(CisManager.class);
	
	private static Logger PERF_LOG = LoggerFactory.getLogger("PerformanceMessage");

	public CisManager() {
			this.ownedCISs = new ArrayList<Cis>();	
			this.subscribedCISs = new ArrayList<CisSubscribedImp>();
			


	}
	
	public void init(){
		
		this.isDepencyInjectionDone(); // TODO: move this to other parts of the code and
		// throw exceptions
		
		while (getiCommMgr().getIdManager() ==null)
			;//just wait untill the XCommanager is ready
		
		cisManagerId = getiCommMgr().getIdManager().getThisNetworkNode();
		LOG.info("Jid = " + cisManagerId.getBareJid() + ", domain = " + cisManagerId.getDomain() );



		try {
			getiCommMgr().register((IFeatureServer) this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		} // TODO unregister??

		LOG.info("listener registered");
		
		// testing to add hard coded context atributtes
		//this.addHardCodedQualifications();
		//polManager.inferPrivacyPolicy(PrivacyPolicyTypeConstants.CIS, null);
		startup();
		LOG.info("CISManager started up with "+this.ownedCISs.size()
				+" owned CISes and "+this.subscribedCISs.size()+" subscribed CISes");
	}





	/**
	 * Create a new CIS for the CSS represented by cssId. Password is needed and is the
	 * same as the CSS password.
	 * After this method is called a CIS is created with mode set to mode.
	 * 
	 * The CSS who creates the CIS will be the owner. Ownership can be changed
	 * later.
	 * 
	 * TODO: define what values mode can have and what each means.
	 * TODO: change the type from String to proper type when CSS ID datatype is defined.
	 *  
	 * @param cssId and cssPassword are to recognise the user
	 * @param cisName is user given name for the CIS, e.g. "Footbal".
	 * @param cisType E.g. "disaster"
	 * TODO define mode better.
	 * @return link to the {@link ICisEditor} representing the new CIS, or 
	 * null if the CIS was not created.
	 */
	
	@Override
	public Future<ICisOwned> createCis(String cisName, String cisType,
			Hashtable<String, MembershipCriteria> cisCriteria,
			String description) {

		String pPolicy = "<RequestPolicy></RequestPolicy>";	
		ICisOwned i = this.localCreateCis(cisName, cisType, description,cisCriteria ,pPolicy);
			return new AsyncResult<ICisOwned>(i);
	}
	
	@Override
	public Future<ICisOwned> createCis(String cisName, String cisType,
			Hashtable<String, MembershipCriteria> cisCriteria,
			String description, String privacyPolicy) {
	

		ICisOwned i = this.localCreateCis(cisName, cisType,  description,cisCriteria,privacyPolicy);
			return new AsyncResult<ICisOwned>(i);
	}
	
	
	
	private Cis getOwnedCisByJid(String jid){
		Iterator<Cis> it = getOwnedCISs().iterator();
		 
		while(it.hasNext()){
			 Cis element = it.next();
			 if (element.getCisRecord().getCisJID().equalsIgnoreCase(jid))
				 return element;
	     }
		return null;
		
	}
	
	
	// local version of the deleteCIS
	private boolean deleteOwnedCis(String cisJid){

		
		boolean ret = false;
		if(getOwnedCISs().contains(new Cis(new CisRecord(cisJid)))){
			Cis cis = this.getOwnedCisByJid(cisJid);
			
			// get community object for later eventing
			Community c = new Community();
			cis.fillCommmunityXMPPobj(c);

			// get CIS ad for later notification
			CisAdvertisementRecord cisAd = new CisAdvertisementRecord();
			MembershipCrit m = new MembershipCrit();
			cis.fillMembershipCritXMPPobj(m);
			cisAd.setMembershipCrit(m);
			cisAd.setName(cis.getName());
			cisAd.setCssownerid(this.cisManagerId.getJid());
			cisAd.setType(cis.getCisType());
			cisAd.setId(cis.getCisId()); // TODO: check if the id or uri needs the jid

			
			ret = cis.deleteCIS();
			ret = iActivityFeedManager.deleteFeed(this.cisManagerId.getJid(), cisJid);
			ret = ret && getOwnedCISs().remove(cis);
			
			if(ret == true && this.getEventMgr() != null){ 
				// if it works we also send an internal event and notify the CIS directory
				
				// sending internal event
				InternalEvent event = new InternalEvent(EventTypes.CIS_DELETION, "deletion of CIS", this.cisManagerId.getBareJid(), c);
				try {
					this.getEventMgr().publishInternalEvent(event);
				} catch (EMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LOG.error("error trying to internally publish CIS DELETE event");
				}
				
				// notifying the directory
				this.iCisDirRemote.deleteCisAdvertisementRecord(cisAd);
				LOG.info("advertisement sent");
				
				// performance log of the delete
				IPerformanceMessage perMess= new PerformanceMessage();
				perMess.setSourceComponent(this.getClass()+"");
				perMess.setD82TestTableName("S50");
				perMess.setTestContext("CISManagement.CisDeletion.Counter");
				perMess.setOperationType("Delete CIS " + cis.getCisId());
				perMess.setPerformanceType(IPerformanceMessage.Quanitative);
				perMess.setPerformanceNameValue((++nbOfDeletedCIS)+"");
				PERF_LOG.trace(perMess.toString());

			}
		}
		
		return ret;
	}
	

	// local version of the createCis
	private ICisOwned localCreateCis(String cisName, String cisType, String description, Hashtable<String, MembershipCriteria> cisCriteria, String privacyPolicy) {

		
		LOG.info("creating a CIS");
		// -- Verification
		// Dependency injection
		if (!isDepencyInjectionDone(0)) {
			LOG.error("[Dependency Injection] CisManager::createCis not ready");
			return null;
		}
		// Parameters
		if ((null == privacyPolicy || "".equals(privacyPolicy))) {
			return null;
		}
				

		Cis cis = new Cis(this.cisManagerId.getBareJid(), cisName, cisType, 
		this.ccmFactory, this.privacyPolicyManager,this.sessionFactory
		,description,cisCriteria,this.iActivityFeedManager);
		cis.setPrivacyDataManager(privacyDataManager); // TODO: possibly move this to the constructor of the cis
		if(cis == null)
			return cis;

		// PRIVACY POLICY CODE

		try {
			IIdentity cssOwnerId = this.cisManagerId;
			IIdentity cisId = getiCommMgr().getIdManager().fromJid(cis.getCisId());
			RequestorCis requestorCis = new RequestorCis(cssOwnerId, cisId);
			if(privacyPolicyManager != null)
				privacyPolicyManager.updatePrivacyPolicy(privacyPolicy, requestorCis);			
		} catch (InvalidFormatException e) {
			LOG.error("CIS or CSS jid came in bad format");
			e.printStackTrace();
			return null;
		} catch (PrivacyException e) {
			LOG.error("The privacy policy can't be stored.", e);
			if (null != cis) {
				cis.deleteCIS();
				//cis.unregisterCIS();
			}
			LOG.error("CIS deleted.");
			e.printStackTrace();
			return null;
		}

		
		
		//
		
		
		// persisting
		//LOG.info("setting sessionfactory for new cis..: "+sessionFactory.hashCode());
		//this.persist(cis);
		//cis.setSessionFactory(sessionFactory);

		
		// advertising the CIS to global CIS directory
		CisAdvertisementRecord cisAd = new CisAdvertisementRecord();
		MembershipCrit m = new MembershipCrit();
		cis.fillMembershipCritXMPPobj(m);
		cisAd.setMembershipCrit(m);
		cisAd.setName(cis.getName());
		cisAd.setCssownerid(this.cisManagerId.getJid());
		cisAd.setType(cis.getCisType());
		cisAd.setId(cis.getCisId()); // TODO: check if the id or uri needs the jid
		this.iCisDirRemote.addCisAdvertisementRecord(cisAd);
		LOG.info("advertisement sent");
		
		// sending internal event
		if(this.getEventMgr() != null){
			Community c = new Community();
			cis.fillCommmunityXMPPobj(c);
			InternalEvent event = new InternalEvent(EventTypes.CIS_CREATION, "creation of CIS", this.cisManagerId.getBareJid(), c);
			try {
				this.getEventMgr().publishInternalEvent(event);
			} catch (EMSException e) {
				LOG.error("error trying to internally publish CREATE event");
				e.printStackTrace();
				
			}
		}
		
		if (getOwnedCISs().add(cis)){
			ICisOwned i = cis;

			
			IPerformanceMessage perMess= new PerformanceMessage();
			perMess.setSourceComponent(this.getClass()+"");
			perMess.setD82TestTableName("S48");
			perMess.setTestContext("CISManagement.CisCreation.Counter");
			perMess.setOperationType("Create CIS " + cis.getCisId());
			perMess.setPerformanceType(IPerformanceMessage.Quanitative);
			perMess.setPerformanceNameValue((++nbOfCreatedCIS)+"");
			PERF_LOG.trace(perMess.toString());
			return i;
		}else{
			return null;
		}
		
	}

	// internal method used to register that the user has subscribed into a CIS
	// it is triggered by the subscription notification on XMPP
	// TODO: review
	public boolean subscribeToCis(CisRecord record) {

		if(! this.subscribedCISs.contains(new Cis(record))){
			CisSubscribedImp csi = new CisSubscribedImp (record, this);
			this.subscribedCISs.add(csi);
			this.persist(csi);
			
			IPerformanceMessage perMess= new PerformanceMessage();
			perMess.setSourceComponent(this.getClass()+"");
			perMess.setD82TestTableName("S49");
			perMess.setTestContext("CISManagement.CisSubscription.Counter");
			perMess.setOperationType("Subscribed in CIS " + record.getCisJID());
			perMess.setPerformanceType(IPerformanceMessage.Quanitative);
			perMess.setPerformanceNameValue((++nbOfSubscribedCIS)+"");
			PERF_LOG.trace(perMess.toString());
			
			// internal eventing
			if(this.getEventMgr() != null){
				Community c = new Community();
				csi.fillCommmunityXMPPobj(c);
				InternalEvent event = new InternalEvent(EventTypes.CIS_SUBS, "subscription of CIS", this.cisManagerId.getBareJid(), c);
				try {
					this.getEventMgr().publishInternalEvent(event);
				} catch (EMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LOG.error("error trying to internally publish SUBS CIS event");
				}
			}
			return true;
		}
		return false;
	}
	
	// internal method used to leave from a CIS
	// this is triggered by the receipt of a confirmation of a leave
	// TODO: review
	public boolean unsubscribeToCis(String cisjid) {

		if(subscribedCISs.contains(new CisSubscribedImp(new CisRecord(cisjid)))){
			
			CisSubscribedImp temp = new CisSubscribedImp(new CisRecord(cisjid));
			temp = subscribedCISs.get(subscribedCISs.indexOf(temp)); // temp now is the real object
			
			// create the object for later eventing
			Community c = new Community();
			temp.fillCommmunityXMPPobj(c);
			
			if(this.subscribedCISs.remove(temp)) {// removing it from the list
				this.deletePersisted(temp); // removing it from the database
				
				IPerformanceMessage perMess= new PerformanceMessage();
				perMess.setSourceComponent(this.getClass()+"");
				perMess.setD82TestTableName("S53");
				perMess.setTestContext("CISManagement.CisUnSubscription.Counter");
				perMess.setOperationType("UnSubscribed in CIS " + c.getCommunityJid());
				perMess.setPerformanceType(IPerformanceMessage.Quanitative);
				perMess.setPerformanceNameValue((++nbOfUnsubscribedCIS)+"");
				PERF_LOG.trace(perMess.toString());
				
				//send the local event
				if(this.getEventMgr() != null){
					InternalEvent event = new InternalEvent(EventTypes.CIS_UNSUBS, "unsubscription of CIS", this.cisManagerId.getBareJid(), c);
					try {
						this.getEventMgr().publishInternalEvent(event);
					} catch (EMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LOG.error("error trying to internally publish UNSUBS CIS event");
					}
				}
				
				return true;
			}
			else{
				return false;
			}
		}else{
			return false;
		}
		

	}



/*
	public List<CisRecord> getOwnedCisList() {
		
		List<CisRecord> l = new ArrayList<CisRecord>();

		Iterator<Cis> it = getOwnedCISs().iterator();
		 
		while(it.hasNext()){
			 Cis element = it.next();
			 l.add(element.getCisRecord());
			 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
	     }
		
		return l;
	}

	public List<CisRecord> getSubscribedCisList() {
		
		List<CisRecord> l = new ArrayList<CisRecord>(this.subscribedCISs);
		return l;
	}*/



	
	
	@Override
	public List<String> getJavaPackages() {
		return  PACKAGES;
	}

	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		// all received IQs contain a community element
		
		LOG.info("get Query received");
		if (payload.getClass().equals(org.societies.api.schema.cis.manager.CommunityManager.class)) {
			CommunityManager c = (CommunityManager) payload;

			if (c.getCreate() != null && c.getCreate().getCommunity() != null) {
				
				// >>>>>>>>>>>>>>>>>>>>>>>>>>>> CREATE CIS >>>>>>>>>>>>>>>>>>>>>>>>>>>>
				LOG.info("create received");
				String senderjid = stanza.getFrom().getBareJid();
				LOG.info("sender JID = " + senderjid);
				
				//TODO: check if the sender is allowed to create a CIS
				Create create = c.getCreate(); 
				//String ownerJid = create.getCommunity().getOwnerJid(); // TODO: owner must be retrieved other way
				//String cisJid = create.getCommunityJid();
				String cisType = create.getCommunity().getCommunityType();
				String cisName = create.getCommunity().getCommunityName();
				String cisDescription;
				if(create.getCommunity().getDescription() != null)
					cisDescription = create.getCommunity().getDescription();
				else
					cisDescription = "";
				//int cisMode = create.getMembershipMode().intValue();

				// TODO: maybe check if the attributes in the criteria are valid attributes (something from CtxAttributeTypes)
				if(cisType != null && cisName != null){
					String pPolicy;
					if(create.getPrivacyPolicy() != null && 
							create.getPrivacyPolicy().isEmpty() == false){
						pPolicy = create.getPrivacyPolicy();
					}else{
						LOG.info("create came with an empty policy");
						pPolicy = "<RequestPolicy></RequestPolicy>";	
					};
					Hashtable<String, MembershipCriteria> h = null;
					MembershipCrit m = create.getCommunity().getMembershipCrit();
					if (m!=null && m.getCriteria() != null && m.getCriteria().size()>0){
						h =new Hashtable<String, MembershipCriteria>();
						
						// populate the hashtable
						for (Criteria crit : m.getCriteria()) {
							MembershipCriteria meb = new MembershipCriteria();
							meb.setRank(crit.getRank());
							Rule r = new Rule();
							if( r.setOperation(crit.getOperator()) == false) {create.setResult(false); return c;}
							ArrayList<String> a = new ArrayList<String>();
							a.add(crit.getValue1());
							if (crit.getValue2() != null && !crit.getValue2().isEmpty()) a.add(crit.getValue2()); 
							if( r.setValues(a) == false) {create.setResult(false); return c;}
							meb.setRule(r);
							h.put(crit.getAttrib(), meb);
						}
					}
					// real create
					Cis icis = (Cis) localCreateCis( cisName, cisType, cisDescription,h,pPolicy);
		
					// sending the response back
					if(icis !=null){
						CommunityManager response = new CommunityManager();
						Create cr = new Create();
						response.setCreate(cr);

						Community comm = new Community();
						icis.fillCommmunityXMPPobj(comm);
						cr.setCommunity(comm);
						cr.setPrivacyPolicy(pPolicy);
						cr.setResult(true);
						return response;
					}else{
						create.setResult(false);
						return c;
					}
				}
				else{
					create.setResult(false);
					LOG.info("missing parameter on the create");
					
					// if one of those parameters did not come, we should return an error
					return c;
				}					
			}
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>> LIST MY CIS >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getList() != null) {
				LOG.info("list received");
				
				ListCrit listingType = ListCrit.OWNED; // default is owned
				ListResponse l = new ListResponse();
				List<Community> comList = new  ArrayList<Community>();
				
				if(c.getList().getListCriteria() !=null)
					listingType = c.getList().getListCriteria();
				
				// GET LIST OF ownedCIS
				if(listingType.equals(ListCrit.OWNED) || listingType.equals(ListCrit.ALL)){
					Iterator<Cis> it = ownedCISs.iterator();
					while(it.hasNext()){
						Cis element = it.next();
						Community community = new Community();
						element.fillCommmunityXMPPobj(community);
						comList.add(community);
				     }
				}
				// GET LIST OF subscribedCIS
				if(listingType.equals(ListCrit.SUBSCRIBED) || listingType.equals(ListCrit.ALL)){
					Iterator<CisSubscribedImp> it = subscribedCISs.iterator();
					while(it.hasNext()){
						CisSubscribedImp element = it.next();
						Community community = new Community();
						element.fillCommmunityXMPPobj(community);
						comList.add(community);						
				     }
				}
				l.setCommunity(comList);				
				return l;
			}			
			// >>>>>>>>>>>>>>>>>>>>>>>>>>>> DELETE CIS >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getDelete() != null) {

				LOG.info("delete CIS received");
				String senderjid = stanza.getFrom().getBareJid();
				LOG.info("sender JID = " + senderjid);
				
				//TODO: check if the sender is allowed to delete a CIS				
				Delete delete = c.getDelete();
				Delete d2 = new Delete();
				
				if(!this.deleteOwnedCis(delete.getCommunityJid()))
					d2.setValue("error"); // TODO: replace for a proper XMPP error message

				c.setDelete(d2);
				return c;
			}
			// >>>>>>>>>>>>>>>>>>>>>>>>>>>> REQUESST TO JOIN FROM A CLIENT >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getAskCisManagerForJoin() != null) {
				CommunityManager response = new CommunityManager();
				AskCisManagerForJoinResponse ar = new AskCisManagerForJoinResponse();
				response.setAskCisManagerForJoinResponse(ar);
				LOG.info("android request for join received in CIS manager");
				String senderjid = stanza.getFrom().getBareJid();
				LOG.info("sender JID = " + senderjid); 
				CisAdvertisementRecord ad = c.getAskCisManagerForJoin().getCisAdv();
				
				if(ad == null){
					ar.setStatus("error");
					return response;
				}
				else{
					JoinCallBackToAndroid jCallback = new JoinCallBackToAndroid(stanza.getFrom(), this.getiCommMgr(),ad.getId());
					this.joinRemoteCIS(ad, jCallback);// the real return will come in the callback
					
					ar.setStatus("pending");
					return response;
				}
			}
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>> REQUEST TO LEAVE FROM A CLIENT >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getAskCisManagerForLeave() != null) {
				CommunityManager response = new CommunityManager();
				AskCisManagerForLeaveResponse lr = new AskCisManagerForLeaveResponse();
				response.setAskCisManagerForLeaveResponse(lr);
				LOG.info("android request for leave received in CIS manager");
				String senderjid = stanza.getFrom().getBareJid();
				LOG.info("sender JID = " + senderjid); 
				
				if(null == c.getAskCisManagerForLeave().getTargetCisJid() || c.getAskCisManagerForLeave().getTargetCisJid().isEmpty()){
					lr.setStatus("error");
					return response;
				}
				else{
					LeaveCallBackToAndroid jCallback = new LeaveCallBackToAndroid(stanza.getFrom(), this.getiCommMgr(),c.getAskCisManagerForLeave().getTargetCisJid());
					this.leaveRemoteCIS(c.getAskCisManagerForLeave().getTargetCisJid(), jCallback);
					
					lr.setStatus("pending");
					return response;
				}
			}
			// >>>>>>>>>>>>>>>>>>>>>>>>>>>> CONFIGURE MY CIS >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getConfigure() != null) {
				LOG.info("configure received");
				return c;
			}
		}
		return null;

	}

    public ICommManager getiCommMgr() {
        return iCommMgr;
    }

    public void setiCommMgr(ICommManager iCommMgr) {
        this.iCommMgr = iCommMgr;
    }

    public class JoinCallBack implements ICisManagerCallback{
    	JoinResponse resp;
	
		public JoinCallBack(JoinResponse resp){
			this.resp = resp;
		}
	
		@Override
		public void receiveResult(CommunityMethods communityResultObject) {
			if(communityResultObject == null || communityResultObject.getJoinResponse() == null){
				LOG.info("null return on JoinCallBack");
				resp.setResult(false);
			}
			else{
				LOG.info("Result Status: joined CIS " + communityResultObject.getJoinResponse().isResult());
				resp = communityResultObject.getJoinResponse();
			}
		}
	}

	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		LOG.info("message received with class, id, from: " + payload.getClass() + " , " + stanza.getId() + " , " + stanza.getFrom().getBareJid());
		if (payload.getClass().equals(org.societies.api.schema.cis.manager.CommunityManager.class)) {

			CommunityManager c = (CommunityManager) payload;

			// >>>>>>>>>>>>>>>>>>>>>>>>>>>>treating getSubscribedTo notifications >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getNotification().getSubscribedTo()!= null) {
				LOG.info("subscribedTo received");
				this.subscribeToCis(new CisRecord(c.getNotification().getSubscribedTo().getCommunity().getCommunityName(), 
												  c.getNotification().getSubscribedTo().getCommunity().getCommunityJid(),
												  c.getNotification().getSubscribedTo().getCommunity().getOwnerJid(),
												  c.getNotification().getSubscribedTo().getCommunity().getDescription(),
												  c.getNotification().getSubscribedTo().getCommunity().getCommunityType()
												  ));
				return;
			}
			
			// >>>>>>>>>>>>>>>>>>>>>>>>>>>>treating delete CIS notifications >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getNotification().getDeleteNotification() != null) {
				LOG.info("delete notification received");
				this.unsubscribeToCis(stanza.getFrom().getBareJid());
/*				DeleteNotification d = (DeleteNotification) c.getNotification().getDeleteNotification();
				if(!this.subscribedCISs.contains(new CisRecord(d.getCommunityJid()))){
					LOG.info("CIS is not part of the list of subscribed CISs");
				}
				else{
					CisSubscribedImp temp = new CisSubscribedImp(new CisRecord(d.getCommunityJid()));
					temp = subscribedCISs.get(subscribedCISs.indexOf(temp)); // temp now is the real object

					
					this.subscribedCISs.remove(temp);// removing it from the list
					this.deletePersisted(temp); // removing it from the database
				}
				return;*/
			}
			
			// >>>>>>>>>>>>>>>>>>>>>>>>>>>>treating deleteMember notifications >>>>>>>>>>>>>>>>>>>>>>>>>>>>
			if (c.getNotification().getDeleteMemberNotification() != null) {
				LOG.info("delete member notification received");
				DeleteMemberNotification d = (DeleteMemberNotification) c.getNotification().getDeleteMemberNotification();
				if(d.getMemberJid() != this.cisManagerId.getBareJid()){
					LOG.warn("delete member notification had a different member than me...");
				}
				if(!this.subscribedCISs.contains(new CisRecord(d.getCommunityJid()))){
					LOG.info("CIS is not part of the list of subscribed CISs");
				}
				else{
					CisSubscribedImp temp = new CisSubscribedImp(new CisRecord(d.getCommunityJid()));
					temp = subscribedCISs.get(subscribedCISs.indexOf(temp)); // temp now is the real object

					
					this.subscribedCISs.remove(temp);// removing it from the list
					this.deletePersisted(temp); // removing it from the database
				}
				return;
			}
		}
	}

	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Deprecated
	public boolean deleteCis(String cssId, String cssPassword, String cisId){
		return false;
	}

	@Override
	public boolean deleteCis(String cisId) {
		// TODO Auto-generated method stub
		return 	this.deleteOwnedCis(cisId);
	}

	@Override
	public List<ICis> getCisList(){
		
		// add subscribed CIS to the list to be returned
		List<ICis> l = new ArrayList<ICis>();
		l.addAll(subscribedCISs);
		l.addAll(ownedCISs);
		return l;
	}
	
	@Override
	public List<ICis> searchCisByName(String name){
		// add subscribed CIS to the list to be returned
		List<ICis> l = new ArrayList<ICis>();
		Iterator<Cis> it = getOwnedCISs().iterator();
		 
		while(it.hasNext()){
			 Cis element = it.next();
			 if(element.getName().contains(name))
			 l.add(element);
			 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
	     }
		
		Iterator<CisSubscribedImp> it2 = this.getSubscribedCISs().iterator();
		while(it2.hasNext()){
			CisSubscribedImp element = it2.next();
			 if(element.getName().contains(name))
			 l.add(element);
			 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
	     }
		
		return l;
		
	}
	
	@Override
	public List<ICisOwned> getListOfOwnedCis(){
		// add subscribed CIS to the list to be returned
		List<ICisOwned> l = new ArrayList<ICisOwned>();
		l.addAll(ownedCISs);		
		return l;
	}

	@Override
	public List<ICis> getRemoteCis(){
		List<ICis> l = new ArrayList<ICis>();
		l.addAll(subscribedCISs);
		return l;
	}
	
	@Override
	public List<ICisOwned> searchCisByMember(IIdentity css) throws InterruptedException, ExecutionException{
		List<ICisOwned> l = new ArrayList<ICisOwned>();
		for (ICisOwned temp : this.ownedCISs) {
			if(temp.getMemberList().contains(new CisParticipant(css.getBareJid())))
				l.add(temp);
		}
		return l;
	}

	/**
	 * Get a CIS Record with the ID cisId.
	 * 
	 * TODO: Check the return value. Should be something more meaningful.
	 * 
	 * @param cisId The ID (jabber ID) of the CIS to get.
	 * @return the CISRecord with the ID cisID, or null if no such CIS exists.
	 */
	@Override
	public ICis getCis(String cisId) {
		
		// first we check it on the owned CISs		
		Iterator<Cis> it = getOwnedCISs().iterator();
		while(it.hasNext()){
			 Cis element = it.next();
			 if (element.getCisId().equalsIgnoreCase(cisId))
				 return element;
	     }
		
		// then we check on the subscribed CISs
		Iterator<CisSubscribedImp> iterator = this.subscribedCISs.iterator();
		while(iterator.hasNext()){
			CisSubscribedImp element = iterator.next();
			 if (element.getCisId().equalsIgnoreCase(cisId))
				 return element;
	     }
		
		return null;
	}

	@Override
	public ICisOwned getOwnedCis(String cisId) {
		// first we check it on the owned CISs		
		Iterator<Cis> it = getOwnedCISs().iterator();
		while(it.hasNext()){
			 Cis element = it.next();
			 if (element.getCisId().equalsIgnoreCase(cisId))
				 return element;
	     }
		
		return null;
	}
	
	// session related methods
	private void persist(Object o){
		Session session = sessionFactory.openSession();
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
				//session = sessionFactory.openSession();
				//LOG.info("checkquery returns: "+session.createCriteria(Cis.class).list().size()+" hits ");
				//session.close();
			}
			
		}
	}
	
	private void updatePersisted(Object o){
		Session session = sessionFactory.openSession();
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
				//session = sessionFactory.openSession();
				//LOG.info("checkquery returns: "+session.createCriteria(Cis.class).list().size()+" hits ");
				//session.close();
			}
			
		}
	}
	
	private void deletePersisted(Object o){
		Session session = sessionFactory.openSession();
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
				//session = sessionFactory.openSession();
				//LOG.info("checkquery returns: "+session.createCriteria(Cis.class).list().size()+" hits ");
				//session.close();
			}
		}
	}
	
	public  SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		LOG.info("in setsessionfactory!! sessionFactory is: "+sessionFactory);
		//ActivityFeed.setStaticSessionFactory(sessionFactory);
		for(Cis cis : ownedCISs)
			cis.setSessionFactory(sessionFactory);
	}

	// getters and setters
	public List<Cis> getOwnedCISs() {
		return ownedCISs;
	}

	public List<CisSubscribedImp> getSubscribedCISs() {
		return subscribedCISs;
	}


	// internal method that adds the necessary qualifications into the join message
	private void getQualificationsForJoin(CisAdvertisementRecord adv,Join j){
		LOG.debug("getting qualifications for join");
		
		List<Qualification> lq = new ArrayList<Qualification>();

		if(internalCtxBroker !=null){ // check if it has been wired
			
			// Internal check of qualifications
			if(adv.getMembershipCrit()!=null && adv.getMembershipCrit().getCriteria() !=null 
					&& adv.getMembershipCrit().getCriteria().size()>0){
				// if there is some memb criteria on the CIS we need to send our qualifications
				CtxEntityIdentifier memberCssEntityId;
				try {
					memberCssEntityId = this.internalCtxBroker.retrieveIndividualEntity(this.getICommMgr().getIdManager().getThisNetworkNode()).get().getId();
				} catch (Exception e) {
					LOG.debug("exception retrieving 1st data from internal CTX broker");
					e.printStackTrace();
					return;
				}
				
				List<Criteria> l = adv.getMembershipCrit().getCriteria();
				for (Criteria temp : l) { // for each criteria
					List<CtxIdentifier> ctxIds;
					try {
						ctxIds = this.internalCtxBroker.lookup(memberCssEntityId, CtxModelType.ATTRIBUTE,temp.getAttrib()).get();
					} catch (Exception e) {
						LOG.debug("exception retrieving 2nd data from internal CTX broker");
						e.printStackTrace();
						return;
					}
					if (ctxIds!=null && !ctxIds.isEmpty()) {
						  LOG.debug("qualification found ");
						  CtxIdentifier ctxId = ctxIds.get(0);
						  // retrieve the attribute
						  CtxAttribute attribute;
						try {
							attribute = (CtxAttribute) this.internalCtxBroker.retrieve(ctxId).get();
							LOG.debug("qualification is " + attribute.getType());
						} catch (Exception e) {
							LOG.debug("exception retrieving 3rd data from internal CTX broker");
							e.printStackTrace();
							return;
						}
						  if (attribute != null){
							  // TODO: at the moment we are not checking the criteria here, but just building
							  // the qualification because it was wanted by privace that just the attribute is
							  // revealed on Advertisement
							  Qualification q = new Qualification();
							  q.setAttrib(temp.getAttrib());
							  q.setValue(attribute.getStringValue());
							  lq.add(q);
							  LOG.debug("qualification value " + attribute.getStringValue());
						  }
					}
				}
				
			}
		}//end of if(internalCtxBroker !=null){
		
		// End of qualification retrieaval
		if (lq.size()>0)
			j.setQualification(lq);
	}
	
	// TODO just for test purposes, delete later
	// set the user as a protestant from Paris =D
	private void addHardCodedQualifications(){
		
		if(internalCtxBroker !=null){ // check if it has been wired
			
			LOG.info("going to add hard coded qualifications");
			CtxEntityIdentifier memberCssEntityId;
			try {
				memberCssEntityId = this.internalCtxBroker.retrieveIndividualEntity(this.getICommMgr().getIdManager().getThisNetworkNode()).get().getId();
				
				// first social status
	            LOG.info("memberCssEntityId:"+memberCssEntityId.hashCode());
	            LOG.info("this.internalCtxBroker.lookup(memberCssEntityId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.RELIGIOUS_VIEWS): "+this.internalCtxBroker.lookup(memberCssEntityId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.RELIGIOUS_VIEWS));
				List<CtxIdentifier> ctxIds = this.internalCtxBroker.lookup(memberCssEntityId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.RELIGIOUS_VIEWS).get();			
	
				if(ctxIds!= null && ctxIds.isEmpty() == false){
					CtxAttribute ctAtb1 = ((CtxAttribute) this.internalCtxBroker.retrieve(ctxIds.get(0)).get());
					LOG.info("Already existing status equals to " + ctAtb1.getStringValue() );
				}else{
					LOG.info("non existing social status, gonna create");
					CtxAttribute ctAtb1 = this.internalCtxBroker.createAttribute(memberCssEntityId, CtxAttributeTypes.RELIGIOUS_VIEWS).get();
					ctAtb1.setStringValue("protestant");
					ctAtb1.setValueType(CtxAttributeValueType.STRING);
					this.internalCtxBroker.update(ctAtb1);
				}
	
				List<CtxIdentifier> ctxIds2 = this.internalCtxBroker.lookup(memberCssEntityId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.ADDRESS_HOME_CITY).get();
				if(ctxIds2!= null && ctxIds2.isEmpty() == false){
					LOG.info("Already existing status equals to " + ((CtxAttribute) this.internalCtxBroker.retrieve(ctxIds2.get(0)).get()).getStringValue() );
				}else{
					LOG.info("non existing city, gonna create");
					CtxAttribute ctAtb1 = this.internalCtxBroker.createAttribute(memberCssEntityId, CtxAttributeTypes.ADDRESS_HOME_CITY).get();
					ctAtb1.setStringValue("Paris");
					ctAtb1.setValueType(CtxAttributeValueType.STRING);
					this.internalCtxBroker.update(ctAtb1);
				}
	
				
			} catch (Exception e) {
				LOG.debug("exception retrieving 1st data from internal CTX broker");
				e.printStackTrace();
				return;
			}
		}
		
	}
	
	
	// client methods
	@Override
	public void getListOfMembers(Requestor req, IIdentity targetcis,
			ICisManagerCallback callback) {
		// TODO Auto-generated method stub
		
		LOG.debug("local get member list WITH CALLBACK called");

		
		CommunityMethods c = new CommunityMethods();
		
		WhoRequest w = new WhoRequest();
		c.setWhoRequest(w);
		RequestorBean reqB = Util.createRequestorBean(req);
		w.setRequestor(reqB);
		
		//TODO: add a privacy call?
		this.sendXmpp(c, targetcis.getBareJid(), callback);		
		
	}
	
	public ICisRemote getHandlerToRemoteCis(String cisId){
		CisSubscribedImp i = new CisSubscribedImp();
		CisRecord r = new CisRecord(cisId);
		i.setCisRecord(r);
		i.setCisManag(this);
		return i;
	}
	
	@Override
	public void joinRemoteCIS(CisAdvertisementRecord adv, ICisManagerCallback callback) {
		
		LOG.debug("client call to join a RemoteCIS");
		Join j = new Join();
		boolean error = false;
		
		// TODO: maybe to already a check here
		this.getQualificationsForJoin(adv,j);
		

		LOG.info("going to start the negotiation");

		try {
			negotiator.startNegotiation(new RequestorCis(this.getiCommMgr().getIdManager().fromJid(adv.getCssownerid()) , this.getiCommMgr().getIdManager().fromJid(adv.getId())),
					new INegCallBack(this,j,adv.getId(),callback));
		}
		catch (InvalidFormatException e) {
			LOG.error("[Negotiation] Error during the instantiation of IIdentitys from "+adv.getCssownerid()+" or "+adv.getId(), e);
			error = true;
		}
		catch (Exception e) {
			LOG.error("[Negotiation] Error during negotiation", e);
			error = true;
		}
		if(true == error){
			CommunityMethods result = new CommunityMethods();		
			Community com = new Community();
			com.setCommunityJid(adv.getId());
			JoinResponse jr = new JoinResponse();
			jr.setResult(false);
			jr.setCommunity(com);
			result.setJoinResponse(jr);			
			callback.receiveResult(result);
			return;
		}
		
		LOG.debug("negotiator has been called, Ill proceed with the join");
		
	}
	
	class INegCallBack implements INegotiationCallback{
		
		CisManager cisMgm = null;
		Join j = null;
		String targetJid = null;
		ICisManagerCallback callback = null;

		
		public INegCallBack (CisManager cisMgm, Join j, String targetJid, ICisManagerCallback callback){
			this.cisMgm = cisMgm;
			this.j = j;
			this.targetJid = targetJid;
			this.callback = callback;
		}
		
		@Override
		public void onNegotiationError(String msg) {
			this.returnFail();
			
		}
		
		private void returnFail(){
			LOG.debug("privacy negotiation error");
			
			if(null != iUsrFeedback){
				iUsrFeedback.showNotification("privacy negotiation errorn when joining" + targetJid);
			}
			
			CommunityMethods result = new CommunityMethods();		
			Community com = new Community();
			com.setCommunityJid(targetJid);
			JoinResponse jr = new JoinResponse();
			jr.setResult(false);
			jr.setCommunity(com);
			result.setJoinResponse(jr);			
			callback.receiveResult(result);
		}

		@Override
		public void onNegotiationComplete(String agreementKey, List<URI> fileUris) {
			if(agreementKey!=null && !agreementKey.isEmpty()){
				// -- Sending join
				IIdentity toIdentity;
				try {
					toIdentity = cisMgm.getiCommMgr().getIdManager().fromJid(targetJid);
					Stanza stanza = new Stanza(toIdentity);
					CisManagerClientCallback commsCallback = new CisManagerClientCallback(
							stanza.getId(), callback, cisMgm);

					CommunityMethods c = new CommunityMethods();

					c.setJoin(j);

					try {
						LOG.info("Sending stanza with join");
						cisMgm.getiCommMgr().sendIQGet(stanza, c, commsCallback);
					} catch (CommunicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (InvalidFormatException e1) {
					LOG.info("Problem with the input jid when trying to send the join");
					e1.printStackTrace();
				}
			}
			else{
				this.returnFail();
			}
				
		}
	}
	

	@Override
	public void leaveRemoteCIS(String cisId, ICisManagerCallback callback){
		LOG.debug("client call to leave a RemoteCIS");


		CommunityMethods c = new CommunityMethods();
		c.setLeave(new Leave());
		this.sendXmpp(c, cisId, callback);
		
	}

	
	private void sendXmpp(CommunityMethods c, String targetJid, ICisManagerCallback callback){
		IIdentity toIdentity;
		try {
			toIdentity = this.getiCommMgr().getIdManager().fromJid(targetJid);
			Stanza stanza = new Stanza(toIdentity);
			CisManagerClientCallback commsCallback = new CisManagerClientCallback(
					stanza.getId(), callback, this);

			try {
				LOG.info("Sending stanza");
				this.getiCommMgr().sendIQGet(stanza, c, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			LOG.info("Problem with the input jid when trying to send");
			e1.printStackTrace();
		}	
	}
	
	public void UnRegisterCisManager(){
		// unregister all its CISs
		for(Cis c : ownedCISs ){
			c.unregisterCIS();
		}
		
	}

	
	/* ***********************************
	 *         Dependency Injection      *
	 *************************************/
	
	/**
	 * @param privacyPolicyManager the privacyPolicyManager to set
	 */
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		if(this.getListOfOwnedCis() != null && this.getListOfOwnedCis().size()>0){
			LOG.info("[Dependency Injection] IPrivacyPolicyManager injected in CISs");
			for (int i=0; i< this.getListOfOwnedCis().size(); i++) {
				Cis c = (Cis)this.getListOfOwnedCis().get(i);
				c.setPrivacyPolicyManager(privacyPolicyManager);
			}
		}
		
		LOG.info("[Dependency Injection] IPrivacyPolicyManager injected");
	}
	
	
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
		
		if(this.getListOfOwnedCis() != null && this.getListOfOwnedCis().size()>0){
			LOG.info("[Dependency Injection] IPrivacyDataManager injected in CISs");
			for (int i=0; i< this.getListOfOwnedCis().size(); i++) {
				Cis c = (Cis)this.getListOfOwnedCis().get(i);
				c.setPrivacyDataManager(privacyDataManager);
			}
		}
		
		LOG.info("[Dependency Injection] IPrivacyDataManager injected");
	}
	
	
	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		if (null == getiCommMgr()) {
			LOG.info("[Dependency Injection] Missing ICommManager");
			return false;
		}
		if (null == getiCommMgr().getIdManager()) {
			LOG.info("[Dependency Injection] Missing IIdentityManager");
			return false;
		}
		if (null == ccmFactory) {
			LOG.info("[Dependency Injection] Missing ICISCommunicationMgrFactory");
			return false;
		}
		if (null == sessionFactory) {
			LOG.info("[Dependency Injection] Missing SessionFactory");
			return false;
		}

		
		if (level >= 1) {
			if (null == iActivityFeedManager) {
				LOG.info("[Dependency Injection] Missing ActivityFeedManager");
				return false;
			}
			if (null == iCisDirRemote) {
				LOG.info("[Dependency Injection] Missing ICisDirectoryRemote");
				return false;
			}
			
			if (null == privacyPolicyManager) {
				LOG.info("[Dependency Injection] Missing IPrivacyPolicyManager");
				return false;
			}
			if (null == privacyDataManager) {
				LOG.info("[Dependency Injection] Missing IPrivacyDataManager");
				return false;
			}

			if (null == negotiator) {
				LOG.info("[Dependency Injection] Missing INegotiation");
				return false;
			}

			if (null == internalCtxBroker) {
				LOG.info("[Dependency Injection] Missing Context Broker");
				return false;
			}
			if (null == eventMgr) {
				LOG.info("[Dependency Injection] Missing Event Manager");
				return false;
			}
			if (null == iUsrFeedback) {
				LOG.info("[Dependency Injection] Missing Usr Feedback");
				return false;
			}
			
		}
		return true;
	}

	public boolean isPrivacyPolicyNegotiationIncluded() {
		return privacyPolicyNegotiationIncluded;
	}

	public void setPrivacyPolicyNegotiationIncluded(
			boolean privacyPolicyNegotiationIncluded) {
		this.privacyPolicyNegotiationIncluded = privacyPolicyNegotiationIncluded;
	}



}
