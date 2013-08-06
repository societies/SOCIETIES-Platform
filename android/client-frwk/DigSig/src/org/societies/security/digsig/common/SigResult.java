package org.societies.security.digsig.common;


import android.os.Parcel;
import android.os.Parcelable;

public class SigResult implements Parcelable {
	byte[] cert;
	int sigStatus;
	int trustStatus;		
	
	public static Parcelable.Creator<SigResult> CREATOR = new Creator<SigResult>() {
		
		@Override
		public SigResult[] newArray(int size) {
			return new SigResult[size];
		}
		
		@Override
		public SigResult createFromParcel(Parcel in) {
			SigResult result = new SigResult();
			result.cert = in.createByteArray();
			result.sigStatus = in.readInt();
			result.trustStatus = in.readInt();
							
			return result;	
		}			
	};			
	
	public byte[] getCert() {
		return cert;
	}

	public void setCert(byte[] cert) {
		this.cert = cert;
	}

	public int getSigStatus() {
		return sigStatus;
	}

	public void setSigStatus(int sigStatus) {
		this.sigStatus = sigStatus;
	}

	public int getTrustStatus() {
		return trustStatus;
	}

	public void setTrustStatus(int trustStatus) {
		this.trustStatus = trustStatus;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByteArray(cert);
		dest.writeInt(sigStatus);
		dest.writeInt(trustStatus);				
	}	
}