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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.RemoteActivityFeed;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.activity.Activity;
import org.societies.api.schema.cis.community.Community;
//import org.societies.api.schema.cis.community.GetInfo;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.GetInfo;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.community.ParticipantRole;
import org.societies.api.schema.cis.community.SetInfo;
//import org.societies.api.schema.cis.community.SetInfo;
import org.societies.api.schema.cis.community.Who;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Thomas Vilarinho (Sintef)
*/

@Entity
@Table(name = "org_societies_cis_manager_CisSubscribedImp")
public class CisSubscribedImp implements ICis {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Transient
	private static Logger LOG = LoggerFactory
			.getLogger(CisSubscribedImp.class);
	
	@OneToOne(cascade=CascadeType.ALL)
	private CisRecord cisRecord;
	@Transient
	private CisManager cisManag = null;
	
	public CisRecord getCisRecord() {
		return cisRecord;
	}

	@Transient
	private IActivityFeed iactivityFeed = null;
	
	public void setCisRecord(CisRecord cisRecord) {
		this.cisRecord = cisRecord;
	}

	public CisSubscribedImp() {
	}
	
	public CisSubscribedImp(CisRecord cisRecord, CisManager cisManag) {
		super();
		this.cisRecord = cisRecord;
		this.cisManag =cisManag;
		try {
			this.iactivityFeed = new RemoteActivityFeed(cisManag.iCommMgr, cisManag.iCommMgr.getIdManager().fromJid(cisRecord.cisJID));
		} catch (InvalidFormatException e) {
			LOG.debug("Wrong format of CIS jid in cisRecord");
			e.printStackTrace();
		}
	}
	
	// constructor to be used just for "equals" check
	public CisSubscribedImp(CisRecord cisRecord) {
		super();
		this.cisRecord = cisRecord;
	}

	@Override
	public String getCisId() {
		return this.cisRecord.getCisJID();
	}

	@Override
	public String getName() {
		return this.cisRecord.getCisName();
	}

	
	public void startAfterDBretrieval(CisManager cisManag){
		this.cisManag = cisManag;
		try {
			this.iactivityFeed = new RemoteActivityFeed(cisManag.iCommMgr, cisManag.iCommMgr.getIdManager().fromJid(this.cisRecord.cisJID));
		} catch (InvalidFormatException e) {
			LOG.debug("Wrong format of CIS jid in cisRecord");
			e.printStackTrace();
		}

	}
	
	
	
	
	@Override
	public void getInfo(ICisManagerCallback callback){
		LOG.debug("client call to get info from a RemoteCIS");


		IIdentity toIdentity;
		try {
			toIdentity = this.cisManag.iCommMgr.getIdManager().fromJid(this.getCisId());
			Stanza stanza = new Stanza(toIdentity);
			CisManagerClientCallback commsCallback = new CisManagerClientCallback(
					stanza.getId(), callback, this.cisManag);

			CommunityMethods c = new CommunityMethods();
			c.setGetInfo( new GetInfo());
			try {
				LOG.info("Sending stanza with get info");
				this.cisManag.iCommMgr.sendIQGet(stanza, c, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			LOG.info("Problem with the input jid when trying to send the get info");
			e1.printStackTrace();
		}	
	}

	@Override
	public void setInfo(Community c, ICisManagerCallback callback){
		LOG.debug("client call to get info from a RemoteCIS");

		// TODO: add input treating

		IIdentity toIdentity;
		try {
			toIdentity = this.cisManag.iCommMgr.getIdManager().fromJid(this.getCisId());
			Stanza stanza = new Stanza(toIdentity);
			CisManagerClientCallback commsCallback = new CisManagerClientCallback(
					stanza.getId(), callback, this.cisManag);

			CommunityMethods com = new CommunityMethods();
			SetInfo s = new SetInfo();
			s.setCommunity(c);
			com.setSetInfo(s);
		
			try {
				LOG.info("Sending stanza with set info");
				this.cisManag.iCommMgr.sendIQGet(stanza, com, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			LOG.info("Problem with the input jid when trying to send the set info");
			e1.printStackTrace();
		}	
	}
	
	
	@Override
	public void getListOfMembers(ICisManagerCallback callback){
		
		LOG.info("client call to get list of members from a RemoteCIS");


		IIdentity toIdentity;
		try {
			toIdentity = this.cisManag.iCommMgr.getIdManager().fromJid(this.getCisId());
			LOG.info("identity ok");
			Stanza stanza = new Stanza(toIdentity);
			LOG.info("stanza done");
			CisManagerClientCallback commsCallback = new CisManagerClientCallback(
					stanza.getId(), callback, this.cisManag);

			LOG.info("callback");
			CommunityMethods c = new CommunityMethods();
			Who w = new Who();
			c.setWho(w);
			try {
				LOG.info("Sending stanza with who");
				this.cisManag.iCommMgr.sendIQGet(stanza, c, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			LOG.info("Problem with the input jid when trying to send the who");
			e1.printStackTrace();
		}	

	}

	
	//Overriding hash and equals to compare cisRecord only

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
		CisSubscribedImp other = (CisSubscribedImp) obj;
		if (cisRecord == null) {
			if (other.cisRecord != null)
				return false;
		} else if (!cisRecord.equals(other.cisRecord))
			return false;
		return true;
	}
	
	
	
	
	private void sendXmpp(Community c,ICisManagerCallback callback){
		IIdentity toIdentity;
		try {
			toIdentity = this.cisManag.iCommMgr.getIdManager().fromJid(this.getCisId());
			Stanza stanza = new Stanza(toIdentity);
			CisManagerClientCallback commsCallback = new CisManagerClientCallback(
					stanza.getId(), callback, this.cisManag);

			try {
				LOG.info("Sending stanza");
				this.cisManag.iCommMgr.sendIQGet(stanza, c, commsCallback);
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			LOG.info("Problem with the input jid when trying to send");
			e1.printStackTrace();
		}	
	}
	
	
	public Future<IActivityFeed> getCisActivityFeed(){
		IIdentity remoteCISid;
		IActivityFeed i = null;
		try {
			remoteCISid = this.cisManag.iCommMgr.getIdManager().fromJid(this.getCisId());
			i = new RemoteActivityFeed(this.cisManag.iCommMgr,remoteCISid);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new AsyncResult<IActivityFeed>(i);
	}

	@Override
	public void getMembershipCriteria(ICisManagerCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IActivityFeed getActivityFeed() {
		return this.iactivityFeed;
	}
	
	// internal method for filling up the Community marshalled object	
	public void fillCommmunityXMPPobj(Community c){
		c.setCommunityJid(this.getCisId());
		c.setCommunityName(this.getName());
		
	} 

}
