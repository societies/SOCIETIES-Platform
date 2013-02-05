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
package org.societies.android.api.internal.servicemonitor;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class InstalledAppInfo implements Parcelable {

	private String applicationName="";
	private String packageName="";
	private String applicationDescription="";
	private String versionName="";
	private int versionCode = 0;
	private String iconAsB64string="";

	public InstalledAppInfo() {
		super();
	}
	
	/**@return the applicationName */
	public String getApplicationName() {
		return applicationName;
	}

	/**@param applicationName the applicationName to set */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**@return the packageName*/
	public String getPackageName() {
		return packageName;
	}

	/**@param packageName the packageName to set*/
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**@return the applicationDescription*/
	public String getApplicationDescription() {
		return applicationDescription;
	}

	/**@param applicationDescription the applicationDescription to set*/
	public void setApplicationDescription(String applicationDescription) {
		this.applicationDescription = applicationDescription;
	}

	/**@return the versionName*/
	public String getVersionName() {
		return versionName;
	}

	/**@param versionName the versionName to set*/
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	/**@return the versionCode*/
	public int getVersionCode() {
		return versionCode;
	}

	/**@param versionCode the versionCode to set*/
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	
	/**@return the iconAsB64string */
	public String getIconAsB64string() {
		return iconAsB64string;
	}

	/**@param iconAsB64string the iconAsB64string to set*/
	public void setIconAsB64string(String iconAsB64string) {
		this.iconAsB64string = iconAsB64string;
	}

	@Override
	public String toString() {
		return (applicationName + "\t" + packageName + "\t" + applicationDescription + "\t" + versionName + "\t" + versionCode + "\n");// + iconAsB64string);
	}

	/* @see android.os.Parcelable#describeContents() */
	public int describeContents() {
		return 0;
	}

	/* @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)*/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getApplicationName());
		dest.writeString(this.getApplicationDescription());
		dest.writeString(this.getPackageName());
		dest.writeString(this.getVersionName());
		dest.writeInt(this.getVersionCode());
		dest.writeString(this.getIconAsB64string());
	}
	
	private InstalledAppInfo(Parcel in) {
		super();
		this.setApplicationName(in.readString());
		this.setApplicationDescription(in.readString());
		this.setPackageName(in.readString());
		this.setVersionName(in.readString());
		this.setVersionCode(in.readInt());
		this.setIconAsB64string(in.readString());
	}
	
	public static final Parcelable.Creator<InstalledAppInfo> CREATOR = new Parcelable.Creator<InstalledAppInfo>() {
		public InstalledAppInfo createFromParcel(Parcel in) {
			return new InstalledAppInfo(in);
		}

		public InstalledAppInfo[] newArray(int size) {
			return new InstalledAppInfo[size];
		}
	};
}
