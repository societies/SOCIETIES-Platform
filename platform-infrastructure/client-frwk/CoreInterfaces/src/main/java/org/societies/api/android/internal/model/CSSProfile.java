package org.societies.api.android.internal.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CSSProfile implements Parcelable {
	/**
	 * Enum for CSS status
	 */
	enum cssStatus {Active, Inactive};
	/**
	 * Enum for entity types
	 */
	enum entityType {Person, Organisation};
	/**
	 * Enum for gender types
	 */
	enum genderType {Male, Female, Unspecified};
	/**
	 * Enum for presence types
	 */
	enum presenceType {Available, DoNotDisturb, Offline, Away, ExtendedAway };
	
	
	/**
	 * is the CSS a person or organisation ?
	 */
	String entityType = null;
	/**
	 * used for personal CSS
	 */
	String foreName = null;
	/**
	 * used for personal surname or organisation's name
	 */
	String name = null;
	/**
	 * CSS name
	 * Will be required to be unique
	 */
	String identityName = null;
	/**
	 * password 
	 * Will be required to one way encrypted e.g. SHA-1
	 */
	String password = null;
	/**
	 * e-mail account
	 */
	String emailID = null;
	/**
	 * Instant messaging ID
	 */
	String imID = null;
	/**
	 * Social Network URI
	 */
	String socialURI = null;
	/**
	 * Gender of person
	 */
	String sex = null;
	/**
	 * Home or default location
	 */
	String homeLocation = null;
	
	/**
	 * CSS UID 
	 */
	String cssIdentity = null;
	/**
	 * Current list of device IDs that constitute a CSS
	 */
	CSSDevice cssDevices[] = null;
	/**
	 * Status of CSS
	 */
	String cssStatus = null;
	/**
	 * Date of CSS registration
	 */
	String cssRegistration = null;
	/**
	 * Date of CSS inactivation
	 */
	String cssInactivation = null;
	/**
	 * Number of minutes that the CSS has been logged in
	 */
	int cssUpTime = 0;
	
//	/**
//	 * List of CIS that the CSS has participated in
//	 * TODO Requires CIS generic data type
//	 */
//	CSSDevice encounteredCIS[] = null;
	/**
	 * Array of devices that have participated in the CSS
	 */
	CSSDevice archiveCSSDevices[] = null;
	/**
	 * Presence status user
	 */
	String presenceStatus = null;
	
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
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
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
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
	public CSSDevice[] getCssDevices() {
		return cssDevices;
	}
	public void setCssDevices(CSSDevice cssDevices[]) {
		this.cssDevices = cssDevices;
	}
	public String getCssStatus() {
		return cssStatus;
	}
	public void setCssStatus(String cssStatus) {
		this.cssStatus = cssStatus;
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
//	public List getEncounteredCIS() {
//		return encounteredCIS;
//	}
//	public void setEncounteredCIS(List encounteredCIS) {
//		this.encounteredCIS = encounteredCIS;
//	}
	public CSSDevice [] getArchiveCSSDevices() {
		return archiveCSSDevices;
	}
	public void setArchiveCSSDevices(CSSDevice archiveCSSDevices[]) {
		this.archiveCSSDevices = archiveCSSDevices;
	}
	public String getPresenceStatus() {
		return presenceStatus;
	}
	public void setPresenceStatus(String presenceStatus) {
		this.presenceStatus = presenceStatus;
	}
	
	/**
	 * Private constructor 
	 * Must read from Parcel in exact same sequence and reverse method of writeToParcel
	 * 
	 * @param in parcel
	 */
	private CSSProfile(Parcel in) {
		entityType = in.readString();
		foreName = in.readString();
		name = in.readString();
		identityName = in.readString();
		password = in.readString();
		emailID = in.readString();
		imID = in.readString();
		sex = in.readString();
		homeLocation = in.readString();
		cssIdentity = in.readString();
		cssDevices = (CSSDevice[]) in.readParcelableArray(CSSDevice.class.getClassLoader());
		cssStatus = in.readString();
		cssRegistration = in.readString();
		cssInactivation = in.readString();
		int cssUpTime = in.readInt();
		
//		CSSDevice encounteredCIS[] = null;
		archiveCSSDevices = (CSSDevice[]) in.readParcelableArray(CSSDevice.class.getClassLoader());
		presenceStatus = in.readString();

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
		out.writeString(entityType);
		out.writeString(foreName);
		out.writeString(name);
		out.writeString(identityName);
		out.writeString(password);
		out.writeString(emailID);
		out.writeString(imID);
		out.writeString(sex);
		out.writeString(homeLocation);
		out.writeString(cssIdentity);
		out.writeParcelableArray(cssDevices, 0);
		out.writeString(cssStatus);
		out.writeString(cssRegistration);
		out.writeString(cssInactivation);
		out.writeInt(cssUpTime);
		
//		CSSDevice encounteredCIS[] = null;
		out.writeParcelableArray(archiveCSSDevices, 0);
		out.writeString(presenceStatus);

	}
	public static final Parcelable.Creator<CSSProfile> CREATOR = new Parcelable.Creator<CSSProfile>() {

		@Override
		public CSSProfile createFromParcel(Parcel in) {
			return new CSSProfile(in);
		}

		@Override
		public CSSProfile[] newArray(int size) {
			return new CSSProfile [size];
		}
		
	};
}
