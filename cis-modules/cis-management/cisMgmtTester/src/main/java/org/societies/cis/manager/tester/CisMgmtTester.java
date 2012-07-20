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

package org.societies.cis.manager.tester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.css.management.ICSSManagerCallback;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.Create;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Thomas Vilarinho (Sintef)
*/


public class CisMgmtTester {

	//private IcisManagerClient cisClient;
	//private ICisManager cisClient;
	private ICommManager iCommMgr;
	
	private static Logger LOG = LoggerFactory
			.getLogger(CisMgmtTester.class);
	
	private String targetCisId = null;
	
	int join = 0;
	
	static int busy = 0;
	
	public static int getBusy() {
		return busy;
	}

	public static void setBusy(int busy) {
		CisMgmtTester.busy = busy;
	}

	public CisMgmtTester(ICommManager iCommMgr, String targetCisId){

		this.iCommMgr = iCommMgr;
		this.targetCisId = targetCisId;
		IIdentity toIdentity;
		try {
			toIdentity =iCommMgr.getIdManager().fromJid(targetCisId);
			Stanza stanza = new Stanza(toIdentity);
			
			CommunityManager c = new CommunityManager();
			Create cr = new Create();
			c.setCreate(cr);
			Community com = new Community();
			cr.setCommunity(com);
			com.setCommunityType("futebol");
			com.setCommunityName("fla");
			com.setDescription("grupo do fla");
			
			MembershipCrit m = new MembershipCrit();
			List<Criteria> l = new ArrayList<Criteria>();
			m.setCriteria(l);
			Criteria crit = new Criteria();
			crit.setAttrib("status");
			crit.setOperator("equals");
			//crit.setRank(0);
			crit.setValue1("married");
			l.add(crit);
			
			com.setMembershipCrit(m);
			
			try {
				LOG.info("Sending stanza");
				this.iCommMgr.sendIQGet(stanza, c, new TestIcomCallBack());
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e1) {
			LOG.info("Problem with the input jid when trying to send");
			e1.printStackTrace();
		}	
		
		
		
/*		JoinCallBack icall = new JoinCallBack(cisClient);
		
		
		
		LOG.info("starting CIS MGMT tester");
		this.cisClient = cisClient;
		LOG.info("got autowired reference, target cisId is " + targetCisId);

		ICis icis = cisClient.getCis(targetCisId);

		if(icis == null){
			LOG.info("could not retrieve CIS");
		}
		
		IActivity iActivity = new org.societies.activity.model.Activity();
		iActivity.setActor("act");
		iActivity.setObject("obj");
		iActivity.setTarget("tgt");
		iActivity.setPublished((System.currentTimeMillis() -55) + "");
		iActivity.setVerb("verb");

		LOG.info("calling add activity remote");				
		AddActivityCallBack h = new AddActivityCallBack();
		icis.addCisActivity(iActivity, h);
		LOG.info("add activity remote done");

		
		LOG.info("del activity remote");
		try {
			IActivityFeed iac =  icis.getCisActivityFeed().get();
			iac.deleteActivity(iActivity);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
				
		LOG.info("del activity remote done");
		LOG.info("join a remote CIS");
		this.cisClient.joinRemoteCIS(targetCisId, icall);
		LOG.info("join sent");
*/
		

		
		
	}
	
	public class JoinCallBack implements ICisManagerCallback{
		
		ICisManager cisClient;
		
		public JoinCallBack(ICisManager cisClient){
			this.cisClient = cisClient;
		}

		public void receiveResult(Community communityResultObject) {
			 
			if(communityResultObject == null){
				LOG.info("null return on JoinCallBack");
				return;
			}
			else{
				LOG.info("good return on JoinCallBack");
				LOG.info("Result Status: joined CIS " + communityResultObject.getCommunityJid());
				join = 1;
				ICis icis = cisClient.getCis(communityResultObject.getCommunityJid());
				
				
				/*					IActivity iActivity = new org.societies.activity.model.Activity();
				iActivity.setActor("act");
				iActivity.setObject("obj");
				iActivity.setTarget("tgt");
				iActivity.setPublished((System.currentTimeMillis() -55) + "");
				iActivity.setVerb("verb");

				LOG.info("calling add activity remote");				
							AddActivityCallBack h = new AddActivityCallBack();
				icis.addCisActivity(iActivity, h);
				LOG.info("add activity remote done");
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					IActivityFeed iac = icis.getCisActivityFeed().get();
					iac.deleteActivity(iActivity);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
*/				
				
				
	
				
			}

			
			
		}
			

	}
	
	public class TestIcomCallBack implements ICommCallback{
		
		private final  List<String> NAMESPACES = Collections
				.unmodifiableList( Arrays.asList("http://societies.org/api/schema/cis/manager",
//									"http://societies.org/api/schema/activity",
							  		"http://societies.org/api/schema/cis/community"));
				//.singletonList("http://societies.org/api/schema/cis/manager");
		private final  List<String> PACKAGES = Collections
				//.singletonList("org.societies.api.schema.cis.manager");
				.unmodifiableList( Arrays.asList("org.societies.api.schema.cis.manager",
//						"org.societies.api.schema.activity",
						"org.societies.api.schema.cis.community"));
		
		@Override
		public List<String> getXMLNamespaces() {
			
			return this.NAMESPACES;
		}

		@Override
		public List<String> getJavaPackages() {
			// TODO Auto-generated method stub
			return this.PACKAGES;
		}

		@Override
		public void receiveResult(Stanza stanza, Object payload) {
			LOG.info(payload.toString());
			
		}

		@Override
		public void receiveError(Stanza stanza, XMPPError error) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void receiveItems(Stanza stanza, String node, List<String> items) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void receiveMessage(Stanza stanza, Object payload) {
			LOG.info(payload.toString());
			
		}


	}
	
	public class GetActivitiesCallBack  implements ICisManagerCallback{
		
		
	

		public void receiveResult(Community communityResultObject) {
			if(communityResultObject == null){
				LOG.info("null return on GetListMembersCallBack");
				return;
			}
			else{
				LOG.info("good return on GetActivitiesCallBack  Callback");
				LOG.info("Result Status: GetListMembersCallBack from CIS " + communityResultObject.getCommunityJid());
			/*	List<org.societies.api.schema.activity.Activity> l = communityResultObject.getGetActivitiesResponse().getActivity();

				int[] memberCheck = {0,0,0};
				
				Iterator<org.societies.api.schema.activity.Activity> it = l.iterator();
				
				while(it.hasNext()){
					org.societies.api.schema.activity.Activity element = it.next();
					LOG.info("actor " + element.getActor() + " target "  + element.getTarget() + " time "  + element.getPublished() );
			     }*/
				
			}
			
		}


	}

	
}
