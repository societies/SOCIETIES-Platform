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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.activity.ActivityFeed;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICis;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;

import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyPolicyTypeConstants;
import org.societies.cis.manager.Cis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;


import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.manager.Communities;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.Create;
import org.societies.api.schema.cis.manager.CisCommunity;
import org.societies.api.schema.cis.manager.Delete;
import org.societies.api.schema.cis.manager.DeleteNotification;
import org.societies.api.schema.cis.manager.SubscribedTo;



// this is the class which manages all the CIS from a CSS
// for the class responsible for editing and managing each CIS instance, consult the CIS

/**
 * @author Thomas Vilarinho (Sintef)
*/

public class CisManager implements ICisManager, IFeatureServer{//, ICommCallback{

	

	Set<Cis> ownedCISs; 
	ICISCommunicationMgrFactory ccmFactory;
	IIdentity cisManagerId;
	ICommManager iCommMgr;
	Set<CisSubscribedImp> subscribedCISs;
	private SessionFactory sessionFactory;
//	IPrivacyPolicyManager polManager;
	

	public void startup(){
		//ActivityFeed ret = null;
	
		
		//ActivityFeed.setSession(session);
		//getting owned CISes
		//Query q = session.createQuery("select o from org_societies_cis_manager_Cis o");
		//this.ownedCISs = (Set<Cis>) q.list();
		//q = session.createQuery("select s from org_societies_cis_manager_CisRecord s");
		//this.subscribedCISs = (Set<CisSubscribedImp>) q.list();
	}

	private final static List<String> NAMESPACES = Collections
			.unmodifiableList( Arrays.asList("http://societies.org/api/schema/cis/manager",
						  		"http://societies.org/api/schema/cis/community"));
			//.singletonList("http://societies.org/api/schema/cis/manager");
	private final static List<String> PACKAGES = Collections
			//.singletonList("org.societies.api.schema.cis.manager");
			.unmodifiableList( Arrays.asList("org.societies.api.schema.cis.manager",
					"org.societies.api.schema.cis.community"));

	private static Logger LOG = LoggerFactory
			.getLogger(CisManager.class);

	public CisManager() {
			this.ownedCISs = new HashSet<Cis>();	
			subscribedCISs = new HashSet<CisSubscribedImp>();
			

	}
	public void init(){
		//ICISCommunicationMgrFactory ccmFactory,ICommManager CSSendpoint
		cisManagerId = iCommMgr.getIdManager().getThisNetworkNode();
		LOG.info("Jid = " + cisManagerId.getBareJid() + ", domain = " + cisManagerId.getDomain() );


			try {
				iCommMgr.register((IFeatureServer)this);
			} catch (CommunicationException e) {
				e.printStackTrace();
			} // TODO unregister??
			
			LOG.info("listener registered");	
			//polManager.inferPrivacyPolicy(PrivacyPolicyTypeConstants.CIS, null);
	}


	
	public ICISCommunicationMgrFactory getCcmFactory() {
		return ccmFactory;
	}



	public void setCcmFactory(ICISCommunicationMgrFactory ccmFactory) {
		this.ccmFactory = ccmFactory;
	}



	public ICommManager getICommMgr() {
		return iCommMgr;
	}



	public void setICommMgr(ICommManager cSSendpoint) {
		iCommMgr = cSSendpoint;
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
	 * @param mode membership type, e.g 1= read-only.
	 * TODO define mode better.
	 * @return link to the {@link ICisEditor} representing the new CIS, or 
	 * null if the CIS was not created.
	 */
	
	
	@Override
	public Future<ICisOwned> createCis(String cssId, String cssPassword, String cisName, String cisType, int mode) {
			ICisOwned i = this.localCreateCis(cssId, cssPassword, cisName, cisType, mode);
			return new AsyncResult<ICisOwned>(i);
		
	}
	
	
	
	private Cis getOwnedCisByJid(String jid){
		Iterator<Cis> it = getOwnedCISs().iterator();
		 
		while(it.hasNext()){
			 Cis element = it.next();
			 if (element.getCisRecord().getCisJid().equals(jid))
				 return element;
	     }
		return null;
		
	}
	
	
	// local version of the deleteCIS
	private boolean deleteOwnedCis(String cssId, String cssPassword, String cisJid){
		// TODO: how do we check fo the cssID/pwd?
		
		boolean ret = false;
		if(getOwnedCISs().contains(new Cis(new CisRecord(cisJid)))){
			Cis cis = this.getOwnedCisByJid(cisJid);
			ret = cis.deleteCIS();
			ret = ret && getOwnedCISs().remove(cis);
		}
		
		return ret;
	}
	

	
	
	
	// local version of the createCis
	private ICisOwned localCreateCis(String cssId, String cssPassword, String cisName, String cisType, int mode) {
		// TODO: how do we check fo the cssID/pwd?
		//if(cssId.equals(this.CSSendpoint.getIdManager().getThisNetworkNode().getJid()) == false){ // if the cssID does not match with the host owner
		//	LOG.info("cssID does not match with the host owner");
		//	return null;
		//}
		// TODO: review this logic as maybe I should probably check if it exists before creating
		
		Cis cis = new  Cis(cssId, cisName, cisType, mode,this.ccmFactory);
		this.persist(cis);
		if (getOwnedCISs().add(cis)){
			ICisOwned i = cis;
			return i;
		}else{
			return null;
		}
		
	}

	// internal method used to register that the user has subscribed into a CIS
	// it is triggered by the subscription notification on XMPP
	// TODO: review
	public boolean subscribeToCis(CisRecord i) {

		this.subscribedCISs.add(new CisSubscribedImp (new CisRecord(i.getCisJid()),this));
		return true;
		
	}
	
	// internal method used to leave from a CIS
	// this is triggered by the receipt of a confirmation of a leave
	// TODO: review
	public boolean unsubscribeToCis(String cisjid) {

		if(subscribedCISs.contains(new CisSubscribedImp(new CisRecord(cisjid)))){
			return subscribedCISs.remove(new CisSubscribedImp(new CisRecord(cisjid)));
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

			if (c.getCreate() != null) {
				
				// CREATE CIS
				LOG.info("create received");
				String senderjid = stanza.getFrom().getBareJid();
				LOG.info("sender JID = " + senderjid);
				
				//TODO: check if the sender is allowed to create a CIS
				
				Create create = c.getCreate();
				
				String ownerJid = create.getOwnerJid();
				String cisJid = create.getCommunityJid();
				String cisPassword = create.getCommunityPassword();
				String ownerPassword = create.getOwnerPassword();
				String cisType = create.getCommunityType();
				String cisName = create.getCommunityName();
				//int cisMode = create.getMembershipMode().intValue();

				if(ownerJid != null && ownerPassword != null && cisType != null && cisName != null &&  create.getMembershipMode()!= null){
					int cisMode = create.getMembershipMode().intValue();

					ICisOwned icis = localCreateCis(ownerJid, ownerPassword, cisName, cisType, cisMode);

					
					create.setCommunityJid(icis.getCisId());
					LOG.info("CIS with self assigned ID Created!!");
					return c;  
				}else{
				
				LOG.info("missing parameter on the create");
				// if one of those parameters did not come, we should return an error
				return new CommunityManager();
				}
				// END OF CREATE CIS					

			}
			if (c.getList() != null) {
				LOG.info("list received");
				
				String listingType = "owned"; // default is owned
				if(c.getList().getListCriteria() !=null)
					listingType = c.getList().getListCriteria();
								
				
				Communities com = new Communities();
				
				if(listingType.equals("owned") || listingType.equals("all")){
				// GET LIST CODE of ownedCIS
					
					Iterator<Cis> it = ownedCISs.iterator();
					
					while(it.hasNext()){
						CisRecord element = it.next().getCisRecord();
						CisCommunity community = new CisCommunity();
						community.setCommunityJid(element.getCisJid());
						com.getCisCommunity().add(community);
						 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
				     }
				}

				// GET LIST CODE of subscribedCIS
				if(listingType.equals("subscribed") || listingType.equals("all")){
					//List<CisRecord> li = this.getSubscribedCisList();
					Iterator<CisSubscribedImp> it = subscribedCISs.iterator();
					
					while(it.hasNext()){
						CisSubscribedImp element = it.next();
						CisCommunity community = new CisCommunity();
						community.setCommunityJid(element.getCisId());
						com.getCisCommunity().add(community);
						 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
				     }
				}
				
				return com;

			}
				// END OF LIST
				
			// DELETE CIS
			if (c.getDelete() != null) {

				LOG.info("delete CIS received");
				String senderjid = stanza.getFrom().getBareJid();
				LOG.info("sender JID = " + senderjid);
				
				//TODO: check if the sender is allowed to delete a CIS
				
				Delete delete = c.getDelete();
				Delete d2 = new Delete();
				
				if(!this.deleteOwnedCis(senderjid, "", delete.getCommunityJid()))
					d2.setValue("error"); // TODO: replace for a proper XMPP error message

				c.setDelete(d2);
				return c;
			}
			// END OF DELETE
				

			if (c.getConfigure() != null) {
				LOG.info("configure received");
				return c;
			}

			
		}
		return null;

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

			// treating getSubscribedTo notifications
			if (c.getNotification().getSubscribedTo()!= null) {
				LOG.info("subscribedTo received");
				SubscribedTo s = (SubscribedTo) c.getNotification().getSubscribedTo();
				CisRecord r = new CisRecord(s.getCisJid());
				this.subscribeToCis(r);
				return;
			}
			
			// treating delete notifications
			if (c.getNotification().getDeleteNotification() != null) {
				LOG.info("delete notification received");
				DeleteNotification d = (DeleteNotification) c.getNotification().getDeleteNotification();
				if (!this.subscribedCISs.remove(new CisRecord(d.getCommunityJid())))
					LOG.info("CIS is not part of the list of subscribed CISs");
				return;
			}
		}
		if (payload.getClass().equals(Community.class)) {

			Community c = (Community) payload;

			// treating new member notifications
			if (c.getWho() != null) {
				LOG.info("new member joined a CIS notification received");
				// TODO: do something? or maybe remove those notifications
				return;
			}
			

		}
		
		
	}



	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean deleteCis(String cssId, String cssPassword, String cisId) {
		// TODO Auto-generated method stub
		return 	this.deleteOwnedCis(cssId, cssPassword, cisId);
	}

	@Override
	public List<ICis> getCisList(){
		
		// add subscribed CIS to the list to be returned
		List<ICis> l = new ArrayList<ICis>();
		l.addAll(subscribedCISs);

		
		/*// add owned CIS to the list to be returned
		List<ICis> l2 = new ArrayList<ICis>();

		Iterator<Cis> it = getOwnedCISs().iterator();
		 
		while(it.hasNext()){
			 Cis element = it.next();
			 l2.add(element);
			 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
	     }*/
		l.addAll(ownedCISs);
		
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
	public ICis[] getCisList(ICis arg0) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean requestNewCisOwner(String arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub
		return false;
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
	public ICis getCis(String cssId, String cisId) {
		
		// first we check it on the owned CISs		
		Iterator<Cis> it = getOwnedCISs().iterator();
		while(it.hasNext()){
			 Cis element = it.next();
			 if (element.getCisId().equals(cisId))
				 return element;
	     }
		
		// then we check on the subscribed CISs
		Iterator<CisSubscribedImp> iterator = this.subscribedCISs.iterator();
		while(iterator.hasNext()){
			CisSubscribedImp element = iterator.next();
			 if (element.getCisId().equals(cisId))
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
			 if (element.getCisId().equals(cisId))
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
		}catch(Exception e){
			t.rollback();
			LOG.warn("Saving CIS object failed, rolling back");
		}finally{
			if(session!=null)
				session.close();
		}
	}
	
	public  SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		LOG.info("in setsessionfactory!! sessionFactory is: "+sessionFactory);
		ActivityFeed.setStaticSessionFactory(sessionFactory);
	}

	

	// getters and setters
	
	
	public Set<Cis> getOwnedCISs() {
		return ownedCISs;
	}

	public Set<CisSubscribedImp> getSubscribedCISs() {
		return subscribedCISs;
	}


// client methods
	
	@Override
	public void joinRemoteCIS(String cisId, ICisManagerCallback callback) {
		
		LOG.debug("client call to join a RemoteCIS");


		IIdentity toIdentity;
		try {
			toIdentity = this.iCommMgr.getIdManager().fromJid(cisId);
			Stanza stanza = new Stanza(toIdentity);
			CisManagerClientCallback commsCallback = new CisManagerClientCallback(
					stanza.getId(), callback, this);

			Community c = new Community();

			c.setJoin("");
			try {
				LOG.info("Sending stanza with join");
				this.iCommMgr.sendIQGet(stanza, c, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			LOG.info("Problem with the input jid when trying to send the join");
			e1.printStackTrace();
		}
	}

	@Override
	public void leaveRemoteCIS(String cisId, ICisManagerCallback callback){
		LOG.debug("client call to leave a RemoteCIS");


		IIdentity toIdentity;
		try {
			toIdentity = this.iCommMgr.getIdManager().fromJid(cisId);
			Stanza stanza = new Stanza(toIdentity);
			CisManagerClientCallback commsCallback = new CisManagerClientCallback(
					stanza.getId(), callback, this);

			Community c = new Community();

			c.setLeave("");
			try {
				LOG.info("Sending stanza with leave");
				this.iCommMgr.sendIQGet(stanza, c, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			LOG.info("Problem with the input jid when trying to send the join");
			e1.printStackTrace();
		}
	}



}
