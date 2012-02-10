package org.societies.api.android.internal.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CSSDevice implements Parcelable {
	/**
	 * Enum for device status types
	 */
	public enum deviceStatus {Available, Unavailable, Hibernating};
	/**
	 * Enum for device node types
	 */
	public enum nodeType {Android, Cloud, Rich};
	
	/**
	 * unique within context of CSS
	 */
	private String identity = null;
	
	/**
	 * status of device
	 */
	private String status = null;
	/**
	 * node type of device
	 */
	private String nodeType = null;
	
	public CSSDevice(String identity, String status, String nodeType) {
		this.identity = identity;
		this.status = status;
		this.nodeType = nodeType;
	}


	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
	/**
	 * Parcelable implementation
	 */
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
			out.writeString(identity);
			out.writeString(nodeType);
			out.writeString(status);
	}
	
	public static final Parcelable.Creator<CSSDevice> CREATOR = new Parcelable.Creator<CSSDevice>() {

		@Override
		public CSSDevice createFromParcel(Parcel in) {
			return new CSSDevice(in);
		}

		@Override
		public CSSDevice[] newArray(int size) {
			return new CSSDevice[size];
		}
		
	};
	
	private CSSDevice(Parcel in) {
		identity = in.readString();
		nodeType = in.readString();
		status = in.readString();
	}
}
