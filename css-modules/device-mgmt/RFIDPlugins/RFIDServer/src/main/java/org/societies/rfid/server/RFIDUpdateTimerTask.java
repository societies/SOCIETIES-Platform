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
package org.societies.rfid.server;

import java.util.Date;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.rfid.server.api.IRfidServer;


public class RFIDUpdateTimerTask extends TimerTask{


	private String tagNumber;
	//private IRfidClient rfidClient;
	private IRfidServer rfidServer;
	private String symLoc;
	private String userJid;
	private boolean readyToSend = true;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private Date timeStamp;
	private long updateInterval = 5000;
	
	public RFIDUpdateTimerTask(IRfidServer server, String tagNumber, String symLoc, String userID){
		//this.rfidClient = client;
		this.rfidServer = server;
		this.tagNumber = tagNumber;
		this.symLoc = symLoc;
		this.userJid = userID;
		this.timeStamp = new Date();
	}
	
	@Override
	public void run() {
		if(logging.isDebugEnabled()) logging.debug("trexo trexo !!! "+this.tagNumber);
		Date currentTimeStamp = new Date();
		//if current time is more than 5s after the last update timestamp 
		if ((currentTimeStamp.getTime()-updateInterval)>this.timeStamp.getTime()){
			if(logging.isDebugEnabled()) logging.debug("Location updates no longer sent");
			return;
		}
		
		
			this.rfidServer.sendRemoteUpdate(this.tagNumber, this.symLoc.trim());//(this.userJid, this.symLoc.trim(), this.tagNumber);	
			if(logging.isDebugEnabled()) logging.debug("Sent remote Symbolic Location update message [value:"+this.symLoc+"]");
	
		
	}

	public void setTagNumber(String tagNumber) {
		this.tagNumber = tagNumber;
	}
	public String getTagNumber() {
		return tagNumber;
	}

	public String getSymLoc() {
		return symLoc;
	}

	public void setSymLoc(String newLocation) {
		if(logging.isDebugEnabled()) logging.debug("setting new symloc: "+newLocation+" for: "+this.tagNumber);
		this.timeStamp = new Date();
		this.symLoc = newLocation;
	}

	public String getUserJid() {
		return userJid;
	}

	public void setUserJid(String userJid) {
		this.userJid = userJid;
	}

}
