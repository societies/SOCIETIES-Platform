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
package local.test.dummy.classes;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import local.test.dummy.interfaces.ICisDirectory;
import local.test.dummy.interfaces.ICisAdvertisementRecord;

/**
 * Describe your class here...
 *
 * @author John
 *
 */
public class CisDirectory implements ICisDirectory {
	
	private ICisAdvertisementRecord[] myCisAdvertisementRecord = new ICisAdvertisementRecord[100];
	private String uri;
	
	public CisDirectory(){
		directorySetUp();
	}
	
	
	@Override
	public ICisAdvertisementRecord[] searchByName(String cisName){
		return (ICisAdvertisementRecord[]) myCisAdvertisementRecord;
	}
	
	@Override
	public String getURI(){
		return this.uri;
	}

	@Override
	public void addCisAdvertisementRecord(ICisAdvertisementRecord cisAdvert){
		myCisAdvertisementRecord[myCisAdvertisementRecord.length +1] = cisAdvert;
	}
	
	//void deleteCisAdvertisementRecord(CisAdvertisementRecord cisAdvert);
	//void updateCisAdvertisementRecord(CisAdvertisementRecord oldCisAdvert,CisAdvertisementRecord updatedCisAdvert);

	@SuppressWarnings("null")
	private void directorySetUp(){
		CisAdvertisementRecord car = new CisAdvertisementRecord();
		car.setDescription("CIS 0");
		car.setName("CIS 0");
		MembershipCriteria mc = new MembershipCriteria();
		HashMap<String, MembershipCriteria> mcl = new HashMap<String, MembershipCriteria>();
		//
		Rule r = new Rule();
		r.setAtt("Location");
		r.setOperation("equals");
		List<String> v  = new ArrayList<String>();
		v.add("50");
		r.setValues(v);
		mc.setRule(r);
		//
		mcl.put("Location", mc);
		mcl.put("place", mc);
		car.setMembershipCriteria(mcl);
		myCisAdvertisementRecord[0] = car;
		//
		CisAdvertisementRecord car1 = new CisAdvertisementRecord();
		car1.setDescription("CIS 1");
		car1.setName("CIS 1");
		MembershipCriteria mc1 = new MembershipCriteria();
		HashMap<String, MembershipCriteria> mcl1 = new HashMap<String, MembershipCriteria>();
		//
		Rule r1 = new Rule();
		r1.setAtt("Location");
		r1.setOperation("equals");
		List<String> v1  = new ArrayList<String>();
		v1.add("50");
		r.setValues(v1);
		mc1.setRule(r1);
		//
		mcl1.put("Location", mc1);
		mcl1.put("place", mc1);
		car.setMembershipCriteria(mcl1);
		myCisAdvertisementRecord[1] = car1;
		//
		CisAdvertisementRecord car2 = new CisAdvertisementRecord();
		car2.setDescription("CIS 2");
		car2.setName("CIS 2");
		MembershipCriteria mc2 = new MembershipCriteria();
		HashMap<String, MembershipCriteria> mcl2 = new HashMap<String, MembershipCriteria>();
		//
		Rule r2 = new Rule();
		r2.setAtt("Location");
		r2.setOperation("equals");
		List<String> v2  = new ArrayList<String>();
		v2.add("50");
		r2.setValues(v2);
		mc2.setRule(r2);
		//
		mcl2.put("Location", mc2);
		mcl2.put("place", mc2);
		car.setMembershipCriteria(mcl2);
		myCisAdvertisementRecord[2] = car2;
		//
		
	}
	
}
