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
package org.societies.android.api.internal.cssmanager;

import java.util.List;

import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Android version of {@link CSSProfile}. Implements Parcelable interface for
 * Android IPC.
 *
 */

public class AndroidCSSRecord extends CssRecord implements Parcelable {
	
	/**
	 * Default constructor
	 */
	public AndroidCSSRecord() {
		super();
	}
	
	public String getDomainServer() {
		return domainServer;
	}
	public void setDomainServer(String domainServer) {
		this.domainServer = domainServer;
	}
	public String getCssHostingLocation() {
		return cssHostingLocation;
	}
	public void setCssHostingLocation(String cssHostingLocation) {
		this.cssHostingLocation = cssHostingLocation;
	}
	public int getEntity() {
		return entity;
	}
	public void setEntity(int entity) {
		this.entity = entity;
	}
	public String getForeName() {
		return foreName;
	}
	public void setForeName(String foreName) {
		this.foreName = foreName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdentityName() {
		return identityName;
	}
	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmailID() {
		return emailID;
	}
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	public String getImID() {
		return imID;
	}
	public void setImID(String imID) {
		this.imID = imID;
	}
	public String getSocialURI() {
		return socialURI;
	}
	public void setSocialURI(String socialURI) {
		this.socialURI = socialURI;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getHomeLocation() {
		return homeLocation;
	}
	public void setHomeLocation(String homeLocation) {
		this.homeLocation = homeLocation;
	}
	public String getCssIdentity() {
		return cssIdentity;
	}
	public void setCssIdentity(String cssIdentity) {
		this.cssIdentity = cssIdentity;
	}
	public AndroidCSSNode[] getCSSNodes() {
		AndroidCSSNode androidNodes [] = new AndroidCSSNode[getCssNodes().size()];
		
		for (int i = 0; i < getCssNodes().size(); i++) {
			androidNodes[i] = (AndroidCSSNode) getCssNodes().get(i);
		}
		return androidNodes;
	}
	public void setCSSNodes(AndroidCSSNode[] androidCSSNodes) {
		getCssNodes().clear();
		for (int i = 0; i < androidCSSNodes.length; i++) {
			getCssNodes().add(androidCSSNodes[i]);
		}
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCssRegistration() {
		return cssRegistration;
	}
	public void setCssRegistration(String cssRegistration) {
		this.cssRegistration = cssRegistration;
	}
	public String getCssInactivation() {
		return cssInactivation;
	}
	public void setCssInactivation(String cssInactivation) {
		this.cssInactivation = cssInactivation;
	}
	public int getCssUpTime() {
		return cssUpTime;
	}
	public void setCssUpTime(int cssUpTime) {
		this.cssUpTime = cssUpTime;
	}
	public AndroidCSSNode[] getArchivedCSSNodes() {
		AndroidCSSNode androidNodes [] = new AndroidCSSNode[getArchiveCSSNodes().size()];
		
		for (int i = 0; i < getArchiveCSSNodes().size(); i++) {
			androidNodes[i] = (AndroidCSSNode) getArchiveCSSNodes().get(i);
		}
		return androidNodes;
	}
	public void setArchiveCSSNodes(AndroidCSSNode[] androidArchiveCSSNodes) {
		getArchiveCSSNodes().clear();
		for (int i = 0; i < androidArchiveCSSNodes.length; i++) {
			getArchiveCSSNodes().add(androidArchiveCSSNodes[i]);
		}
	}
	public int getPresence() {
		return presence;
	}
	public void setPresence(int presence) {
		this.presence = presence;
	}
	/**
	 * Convert a given CssRecord to an AndroidCSSRecord
	 * 
	 * @param cssRecord
	 * @return AndroidCSSRecord
	 */
	public static AndroidCSSRecord convertCssRecord(CssRecord cssRecord) {
		AndroidCSSRecord arecord = new AndroidCSSRecord();

			
		arecord.setArchiveCSSNodes(convertCssNodes(cssRecord.getArchiveCSSNodes()));
		arecord.setCssHostingLocation(cssRecord.getCssHostingLocation());
		arecord.setCssIdentity(cssRecord.getCssIdentity());
		arecord.setCssInactivation(cssRecord.getCssInactivation());
		arecord.setCSSNodes(convertCssNodes(cssRecord.getCssNodes()));
		arecord.setCssRegistration(cssRecord.getCssRegistration());
		arecord.setCssUpTime(cssRecord.getCssUpTime());
		arecord.setDomainServer(cssRecord.getDomainServer());
		arecord.setEmailID(cssRecord.getEmailID());
		arecord.setEntity(cssRecord.getEntity());
		arecord.setForeName(cssRecord.getForeName());
		arecord.setHomeLocation(cssRecord.getHomeLocation());
		arecord.setIdentityName(cssRecord.getIdentityName());
		arecord.setImID(cssRecord.getImID());
		arecord.setName(cssRecord.getName());
		arecord.setPassword(cssRecord.getPassword());
		arecord.setPresence(cssRecord.getPresence());
		arecord.setSex(cssRecord.getSex());
		arecord.setSocialURI(cssRecord.getSocialURI());
		arecord.setStatus(cssRecord.getStatus());
		
		return arecord;
	}

	/**
	 * Convert a List of CssNodes to an array of AndroidCSSNode
	 * @param list
	 * @return
	 */
	public static AndroidCSSNode [] convertCssNodes(List<CssNode> list) {
		AndroidCSSNode acssNodes [] = new AndroidCSSNode[list.size()];
		
		for (int i = 0; i < list.size(); i++) {
			acssNodes[i] = AndroidCSSNode.convertCssNode(list.get(i));
		}
		return acssNodes;
	}
	/**
	 * Private constructor 
	 * Must read from Parcel in exact same sequence and reverse method of writeToParcel
	 * 
	 * @param in parcel
	 */
	private AndroidCSSRecord(Parcel in) {
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
		setCSSNodes(convertParcels(in.readParcelableArray(AndroidCSSNode.class.getClassLoader())));
		setStatus(in.readInt());
		setCssRegistration(in.readString());
		setCssInactivation(in.readString());
		setCssUpTime(in.readInt());
		setSocialURI(in.readString());
		
		setArchiveCSSNodes(convertParcels(in.readParcelableArray(AndroidCSSNode.class.getClassLoader())));
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
		out.writeParcelableArray(getCSSNodes(), 0);
		out.writeInt(getStatus());
		out.writeString(getCssRegistration());
		out.writeString(getCssInactivation());
		out.writeInt(getCssUpTime());
		out.writeString(getSocialURI());
		
		out.writeParcelableArray(getArchivedCSSNodes(), 0);
		out.writeInt(getPresence());

	}
	public static final Parcelable.Creator<AndroidCSSRecord> CREATOR = new Parcelable.Creator<AndroidCSSRecord>() {

		@Override
		public AndroidCSSRecord createFromParcel(Parcel in) {
			return new AndroidCSSRecord(in);
		}

		@Override
		public AndroidCSSRecord[] newArray(int size) {
			return new AndroidCSSRecord [size];
		}
		
	};
	/**
	 * Convert Parcelable array to AndroidCSSNode array
	 * @param parcels
	 * @return AndroidCSSNode array
	 */
	private AndroidCSSNode [] convertParcels(Parcelable parcels []) {
		AndroidCSSNode cssNodes [] = new AndroidCSSNode [parcels.length];
		for (int i = 0; i < parcels.length; i++) {
			cssNodes[i] = (AndroidCSSNode) parcels[i];
		}
		return cssNodes;
	}
}
