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
package org.societies.orchestration.cpa.test;

import java.util.HashMap;
import java.util.Set;

import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.schema.activity.Activity;
import org.societies.cis.manager.Cis;

public class CISSimulator {
	private HashMap<String,HashMap<String,Double>> userToUserMap;
	private int messagesperuserperday;
	public CISSimulator(int initUsers, int messagesperuserperday)
	{
		this.messagesperuserperday = messagesperuserperday;
		userToUserMap = new HashMap<String,HashMap<String,Double>>();
		init(initUsers);
	}
	
	public void init(int initUsers){
		String base = "user";
		for(int i = 0;i<initUsers;i++){
			addUser(base+Integer.toString(i+1));
		}
		Set<String> keySet = userToUserMap.keySet();
		Object[] keyArr = keySet.toArray();
		int arrsize = keyArr.length;
		for(int i=0;i<arrsize;i++){
			for(int i2=0;i2<arrsize/2;i2++){
				if(i!=i2)
					setUserToUserRate((String)keyArr[i],(String)keyArr[i2],Math.random());
			}
		}
	}
	/*
	 * 
	 * @param double rate
	 */
	public void setUserToUserRate(String user1, String user2, double rate){
		userToUserMap.get(user1).put(user2, new Double(rate));
		userToUserMap.get(user2).put(user1, new Double(rate));
	}
	public void addUser(String user){
		userToUserMap.put(user, new HashMap<String,Double>());
	}
	public ICisOwned simulate(long days){
		ICisOwned ret = new Cis();
		for(String user : userToUserMap.keySet()){
//			try {
//				ret.addMember(user,"member");
//			} catch (CommunicationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		return ret;
	}
	public Activity makeMessage(String user1, String user2, String message, String published){
		Activity ret = new Activity();
		ret.setActor(user1);
		ret.setObject(message);
		ret.setTarget(user2);
		ret.setPublished(published);
		return ret;
	}
	//test of the test code..
	public static void main(String[] args){
		CISSimulator sim = new CISSimulator(10,10);
		
		
		
	}
}
