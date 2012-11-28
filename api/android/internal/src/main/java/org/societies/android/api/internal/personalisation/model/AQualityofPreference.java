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
package org.societies.android.api.internal.personalisation.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class AQualityofPreference implements Parcelable {

	/**
	 * how many times this outcome has been aborted
	 */
	private int abortedCounter;
	/**
	 * when was the last time this outcome was aborted
	 */
	private Date lastAborted;
	/**
	 * when was this outcome last learnt
	 */
	private Date lastModified;
	/**
	 * when was the last time this outcome was successfully implemented
	 */
	private Date lastSuccess;
	/**
	 * how many times this outcome has been successfully implemented
	 */
	private int successCounter;


	public AQualityofPreference(){

	}



	/**
	 * 
	 * @param level
	 */
	public void increaseSuccessCounter(int level){
		this.successCounter +=level;
	}

	public void increaseAbortedCounter(int level) {
		this.abortedCounter +=level;
		
	}
	/**
	 * 
	 * @param lastAborted
	 */
	public void setLastAborted(Date lastAborted){
		this.lastAborted = lastAborted;
	}

	public Date getLastAborted(){
		return this.lastAborted;
	}
	/**
	 * 
	 * @param lastModified
	 */
	public void setLastModified(Date lastModified){
		this.lastModified = lastModified;
	}

	public Date getLastModified(){
		return this.lastModified;
	}
	/**
	 * 
	 * @param lastSuccess
	 */
	public void setLastSuccess(Date lastSuccess){
		this.lastSuccess = lastSuccess;
	}

	
	public Date getLastSuccess(){
		return this.getLastSuccess();
	}
	
	public int getAbortedCounter() {
		// TODO Auto-generated method stub
		return this.abortedCounter;
	}



	public int getSuccessCounter() {
		// TODO Auto-generated method stub
		return this.successCounter;
	}
	
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	private AQualityofPreference(Parcel in){
		super();
		this.lastAborted.setTime(in.readLong());
		this.lastModified.setTime(in.readLong());
		this.lastSuccess.setTime(in.readLong());
		this.abortedCounter = in.readInt();
		this.successCounter = in.readInt();
	}
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(this.lastAborted.getTime());
		out.writeLong(this.lastModified.getTime());
		out.writeLong(this.lastSuccess.getTime());
		out.writeInt(this.abortedCounter);
		out.writeInt(this.successCounter);
		
	}



	public static final Parcelable.Creator<AQualityofPreference> CREATOR = new Parcelable.Creator<AQualityofPreference>() {

        public AQualityofPreference createFromParcel(Parcel in) {
            return new AQualityofPreference(in);
        }

        public AQualityofPreference[] newArray(int size) {
            return new AQualityofPreference[size];
        }

    };




}