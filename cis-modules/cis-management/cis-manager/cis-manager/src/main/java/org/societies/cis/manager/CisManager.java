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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.cis.management.ICisEditor;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisRecord;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;

import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.cis.manager.CisEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import org.societies.api.schema.cis.manager.Community;
import org.societies.api.schema.cis.manager.Communities;
import org.societies.api.schema.cis.manager.Create;



// this is the class which manages all the CIS from a CSS
// for the class responsible for editing and managing each CIS instance, consult the CISEditor

/**
 * @author Thomas Vilarinho (Sintef)
*/
@Component
public class CisManager implements ICisManager, IFeatureServer{

	Set<CisEditor> ownedCISs; 
	ICISCommunicationMgrFactory ccmFactory;
	IIdentity cisManagerId;
	ICommManager CSSendpoint;
	Set<CisRecord> subscribedCISs;
	
	private final static List<String> NAMESPACES = Collections
			.singletonList("http://societies.org/api/schema/cis/manager");
	private final static List<String> PACKAGES = Collections
			.singletonList("org.societies.api.schema.cis.manager");
	

	private static Logger LOG = LoggerFactory
			.getLogger(CisManager.class);

	@Autowired
	public CisManager(ICISCommunicationMgrFactory ccmFactory,ICommManager CSSendpoint) {
		this.CSSendpoint = CSSendpoint;
		this.ccmFactory = ccmFactory;

		cisManagerId = CSSendpoint.getIdManager().getThisNetworkNode();
		LOG.info("Jid = " + cisManagerId.getBareJid() + ", domain = " + cisManagerId.getDomain() );


			try {
				CSSendpoint.register(this);
			} catch (CommunicationException e) {
				e.printStackTrace();
			} // TODO unregister??
			
			LOG.info("listener registered");

			ownedCISs = new HashSet<CisEditor>();	
			subscribedCISs = new HashSet<CisRecord>();

	}

	/** Deprecated, has been replaced by the function bellow
	 * 
	 * Create a CIS Editor with default settings and returns a CIS Record 
	 * This function should generate automatically the jid for the CIS and the pwd
	 * 
	 * @param  creatorCssId  bareJid of the user creating the CIS
	 * @param  cisname 		 term used by the user to map the CIS. needs to be matched with the real
	 * 						 cis XMPP credentials in the database
	 * @return      CisRecord
	 * 
	 */
	@Deprecated
	public CisRecord createCis(String creatorCssId, String cisname) {
		// TODO: create and identity for the CIS and map it in the database with the cisname
		// cisName = randon unused JID;
		// cisId_pwd = random password;
		String cisId = "cis1";
		String host = this.CSSendpoint.getIdManager().getThisNetworkNode().getDomain();
		String password = "password.thomas.local";

		return this.createCis(creatorCssId, cisId, host, password);
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
	public ICisEditor createCis(String cssId, String cssPassword, String cisName, String cisType, int mode) {
		// TODO: how do we check fo the cssID/pwd?
		if(cssId.equals(this.CSSendpoint.getIdManager().getThisNetworkNode().getJid()) == false){ // if the cssID does not match with the host owner
			LOG.info("cssID does not match with the host owner");
			return null;
		}
		// TODO: review this logic as maybe I should probably check if it exists before creating
		CisEditor cis = new  CisEditor(cssId, cisName, cisType, mode,this.ccmFactory);		
		if (ownedCISs.add(cis))
			return cis;
		else
			return null;
		
	}

	
	/**
	 * Create a CIS Editor with default settings and returns a CIS Record 
	 * Function to be called from the XMPP or to be used by the 
	 *  public method CisRecord createCis(String creatorCssId, String cisName) 
	 * 
	 * @param  creatorCssId  bareJid of the user creating the CIS
	 * @param  cisName 		 jid to be given to the CIS
	 * @param  host 		 jid to be given to the CIS
	 * @password  host 		 jid to be given to the CIS
	 * @return      CisRecord
	 * 
	 */
	
	private CisRecord createCis(String creatorCssId, String cisId, String host, String password) {
		//TODO: check if 
		// cIs already exist in the database or if this is a new CIS
		CisEditor cis = new  CisEditor(creatorCssId,
				cisId,host,0,"",password,this.ccmFactory);
		if (ownedCISs.add(cis))
			return cis.getCisRecord();
		else
			return null;
	}



	public List<CisRecord> getOwnedCisList() {
		
		List<CisRecord> l = new ArrayList<CisRecord>();

		Iterator<CisEditor> it = ownedCISs.iterator();
		 
		while(it.hasNext()){
			 CisEditor element = it.next();
			 l.add(element.getCisRecord());
			 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
	     }
		
		return l;
	}

	public List<CisRecord> getSubscribedCisList() {
		
		List<CisRecord> l = new ArrayList<CisRecord>(this.subscribedCISs);
		return l;
	}

	
	
	@Override
	public List<String> getJavaPackages() {
		return  PACKAGES;
	}

	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		// all received IQs contain a community element
		LOG.info("get Query received");
		if (payload.getClass().equals(Community.class)) {
			Community c = (Community) payload;

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
				LOG.info("CIS to be created with " + ownerJid + " " + cisJid + " "+ cisPassword + " ");
				if(cisPassword != null && ownerJid != null && cisJid != null ){
					CisRecord cisR = this.createCis(ownerJid,
							cisJid,this.CSSendpoint.getIdManager().getThisNetworkNode().getDomain(), cisPassword);
					
					LOG.info("CIS Created!!");
					return create;
					
				}
				else{
					if(ownerJid != null && ownerPassword != null && cisType != null && cisName != null &&  create.getMembershipMode()!= null){
						int cisMode = create.getMembershipMode().intValue();
						ICisEditor icis = createCis(ownerJid, ownerPassword, cisName, cisType, cisMode);
						
						create.setCommunityJid(icis.getCisId());
						LOG.info("CIS with self assigned ID Created!!");
						return create;  
					}
					
					LOG.info("missing parameter on the create");
					// if one of those parameters did not come, we should return an error
					return create;
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
					List<CisRecord> l = this.getOwnedCisList();
					Iterator<CisRecord> it = l.iterator();
					
					while(it.hasNext()){
						CisRecord element = it.next();
						Community community = new Community();
						community.setCommunityJid(element.getCisJID());
						com.getCommunity().add(community);
						 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
				     }
				}

				// GET LIST CODE of subscribedCIS
				if(listingType.equals("subscribed") || listingType.equals("all")){
					List<CisRecord> li = this.getOwnedCisList();
					Iterator<CisRecord> it = li.iterator();
					
					while(it.hasNext()){
						CisRecord element = it.next();
						Community community = new Community();
						community.setCommunityJid(element.getCisJID());
						com.getCommunity().add(community);
						 //LOG.info("CIS with id " + element.getCisRecord().getCisId());
				     }
				}
			
				
				
				return com;

			}
			if (c.getConfigure() != null) {
				LOG.info("configure received");
				return c;
			}
			if (c.getDelete() != null) {
				LOG.info("delete received");
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
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Boolean deleteCis(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ICisRecord[] getCisList(ICisRecord arg0) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Boolean requestNewCisOwner(String arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub
		return null;
	}







	@Override
	public ICisRecord getCis(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	


}
