/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.api.android.internal.model;

import org.societies.api.internal.css.management.CSSProfile;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Android version of {@link CSSProfile}. Implements Parcelable interface for
 * Android IPC.
 *
 */

public class AndroidCSSProfile extends CSSProfile implements Parcelable {
	
	/**
	 * Default contructor
	 */
	public AndroidCSSProfile() {
		super();
	}
	/**
	 * Private constructor 
	 * Must read from Parcel in exact same sequence and reverse method of writeToParcel
	 * 
	 * @param in parcel
	 */
	private AndroidCSSProfile(Parcel in) {
		setDomainServer(in.readString());
		setCssHostingLocation(in.readString());
		setEntity(in.readInt());
		setForeName(in.readString());
		setName(in.readString());
		setIdentityName(in.readString());
		setPassword(in.readString());
		setEmailID(in.readString());
		setImID(in.readString());
		setSex(in.readInt());
		setHomeLocation(in.readString());
		setCssIdentity(in.readString());
		setCssNodes((AndroidCSSNode[]) in.readParcelableArray(AndroidCSSNode.class.getClassLoader()));
		setStatus(in.readInt());
		setCssRegistration(in.readString());
		setCssInactivation(in.readString());
		setCssUpTime(in.readInt());
		
//		CSSDevice encounteredCIS[] = null;
		setArchiveCSSNodes((AndroidCSSNode[]) in.readParcelableArray(AndroidCSSNode.class.getClassLoader()));
		setPresence(in.readInt());

	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	/**
	 * properties must be written in exact sequence as private constructor
	 */
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(getDomainServer());
		out.writeString(getCssHostingLocation());
		out.writeInt(getEntity());
		out.writeString(getForeName());
		out.writeString(getName());
		out.writeString(getIdentityName());
		out.writeString(getPassword());
		out.writeString(getEmailID());
		out.writeString(getImID());
		out.writeInt(getSex());
		out.writeString(getHomeLocation());
		out.writeString(getCssIdentity());
		out.writeParcelableArray((AndroidCSSNode[])getCssNodes(), 0);
		out.writeInt(getStatus());
		out.writeString(getCssRegistration());
		out.writeString(getCssInactivation());
		out.writeInt(getCssUpTime());
		
//		CSSDevice encounteredCIS[] = null;
		out.writeParcelableArray((AndroidCSSNode[]) getArchiveCSSNodes(), 0);
		out.writeInt(getPresence());

	}
	public static final Parcelable.Creator<AndroidCSSProfile> CREATOR = new Parcelable.Creator<AndroidCSSProfile>() {

		@Override
		public AndroidCSSProfile createFromParcel(Parcel in) {
			return new AndroidCSSProfile(in);
		}

		@Override
		public AndroidCSSProfile[] newArray(int size) {
			return new AndroidCSSProfile [size];
		}
		
	};
}
