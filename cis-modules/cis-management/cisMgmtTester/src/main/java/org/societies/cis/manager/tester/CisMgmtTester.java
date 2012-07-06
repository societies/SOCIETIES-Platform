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
import org.societies.api.internal.css.management.ICSSManagerCallback;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Thomas Vilarinho (Sintef)
*/


public class CisMgmtTester {

	//private IcisManagerClient cisClient;
	private ICisManager cisClient;
	
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

	public CisMgmtTester(ICisManager cisClient, String targetCisId){

		
		

		
		JoinCallBack icall = new JoinCallBack(cisClient);
		
		
		
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

		/*LOG.info("calling add activity remote");				
		AddActivityCallBack h = new AddActivityCallBack();
		icis.addCisActivity(iActivity, h);
		LOG.info("add activity remote done");*/

		
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
/*		LOG.info("join a remote CIS");
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
				
				
				
/*				iActivity.setTarget("New tgt");
				iActivity.setPublished((System.currentTimeMillis()) + "");

				LOG.info("calling 2nds add activity remote");				
				icis.addCisActivity(iActivity, h);
				LOG.info("add 2nd activity remote done");

				
				
				LOG.info("calling Get Activity Remote");
				GetActivitiesCallBack g = new GetActivitiesCallBack();
				String timePeriod = (System.currentTimeMillis() -60000) + " " + System.currentTimeMillis();
				icis.getActivities(timePeriod, g);
				LOG.info("remote get activity done");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
*/				
			
				
			}

			
			
		}
			

	}
	
	public class AddActivityCallBack implements ICisManagerCallback{
		


		public void receiveResult(Community communityResultObject) {
			if(communityResultObject == null){
				LOG.info("null return on AddActivityCallBack");
				return;
			}
			else{
				if(communityResultObject.getAddActivityResponse().isResult())
					LOG.info("good return on AddActivityCallBack Callback");
				
			}
			
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
				List<org.societies.api.schema.activity.Activity> l = communityResultObject.getGetActivitiesResponse().getActivity();

				int[] memberCheck = {0,0,0};
				
				Iterator<org.societies.api.schema.activity.Activity> it = l.iterator();
				
				while(it.hasNext()){
					org.societies.api.schema.activity.Activity element = it.next();
					LOG.info("actor " + element.getActor() + " target "  + element.getTarget() + " time "  + element.getPublished() );
			     }
				
			}
			
		}


	}

	
}
