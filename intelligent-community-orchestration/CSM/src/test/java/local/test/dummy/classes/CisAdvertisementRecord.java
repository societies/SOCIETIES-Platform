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

import java.util.HashMap;

import local.test.dummy.interfaces.ICisAdvertisementRecord;



/**
 * Describe your class here...
 *
 * @author John
 *
 */
public class CisAdvertisementRecord implements ICisAdvertisementRecord {
	
	private String name;
	private String ID;
	private String uri;
	private String password;
	private String type;
	private String description;
	private HashMap<String, MembershipCriteria> mc;
	
	public CisAdvertisementRecord(){
		this.name = "";
		this.ID = "";
		this.uri = "";
		password = "";
		this.type = "";
		this.description = "";
		this.mc = new HashMap<String, MembershipCriteria>();
	}
	
	@Override
	public String getName(){
		return this.name;
	}
	
	@Override
	public void setName(String name){
		this.name = name;
	}

	@Override
	public String getId(){
		return this.ID;
	}

	@Override
	public String getUri(){
		return this.uri;
	}
	
	@Override
	public void setUri(String uri){
		this.uri = uri;
	}
	
	@Override
	public String getPassword(){
		return this.password;
	}
		
	@Override
	public void setPassword(String password){
		this.password = password;
	}
	
	@Override
	public String getType(){
		return this.type;
	}
	
	@Override
	public String setType(String type){
		this.type = type;
		return type;
	}

	@Override
	public String getDescription(){
		return this.description;
	}
	
    @Override
	public boolean setDescription(String description){
    	this.description = description;
    	return true;
    }
    
 	public HashMap<String, MembershipCriteria> getMembershipCriteria(){
    	return mc;
    }
    
    public boolean setMembershipCriteria(HashMap<String, MembershipCriteria> membershipCriteria){
    	mc.putAll(membershipCriteria);
    	return true;
    }


}
