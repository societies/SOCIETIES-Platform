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
package org.societies.android.api.cis.management;

import org.societies.api.schema.activity.Activity;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class AActivity extends Activity implements Parcelable {

	private static final long serialVersionUID = 2942344377976155227L;

	public AActivity() {
		super();
	}
	
	/* @see android.os.Parcelable#describeContents() */
	public int describeContents() {
		return 0;
	}

	/* @see android.os.Parcelable#writeToParcel(android.os.Parcel, int) */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getActor());
		dest.writeString(this.getObject());
		dest.writeString(this.getPublished());
		dest.writeString(this.getTarget());
		dest.writeString(this.getVerb());		
	}
	
	private AActivity(Parcel in) {
		super();
		this.setActor(in.readString());
		this.setObject(in.readString());
		this.setPublished(in.readString());
		this.setTarget(in.readString());
		this.setVerb(in.readString());
	}
	
	public static final Parcelable.Creator<AActivity> CREATOR = new Parcelable.Creator<AActivity>() {
		public AActivity createFromParcel(Parcel in) {
			return new AActivity(in);
		}

		public AActivity[] newArray(int size) {
			return new AActivity[size];
		}
	};

	public static AActivity convertActivity(Activity activity) {
		AActivity aact = new AActivity();
		aact.setActor(activity.getActor());
		aact.setObject(activity.getObject());
		aact.setPublished(activity.getPublished());
		aact.setTarget(activity.getTarget());
		aact.setVerb(activity.getVerb());
		
		return aact;
	}
	
	public static Activity convertAActivity(Activity aactivity) {
		Activity act = new Activity();
		act.setActor(aactivity.getActor());
		act.setObject(aactivity.getObject());
		act.setPublished(aactivity.getPublished());
		act.setTarget(aactivity.getTarget());
		act.setVerb(aactivity.getVerb());
		
		return act;
	}

	
}
