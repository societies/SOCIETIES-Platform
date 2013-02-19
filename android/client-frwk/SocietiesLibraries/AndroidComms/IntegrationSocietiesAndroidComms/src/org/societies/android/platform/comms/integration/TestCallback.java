package org.societies.android.platform.comms.integration;

import org.societies.android.api.comms.Callback;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class TestCallback implements Callback, Parcelable {
	private static final String LOG_TAG = TestCallback.class.getName();
	
	public TestCallback() {
		super();
	}
	
	@Override
	public void receiveError(String arg0) {
		Log.d(LOG_TAG, "Callback receiveError");
	}

	@Override
	public void receiveItems(String arg0) {
		Log.d(LOG_TAG, "Callback receiveItems");
	}

	@Override
	public void receiveMessage(String arg0) {
		Log.d(LOG_TAG, "Callback receiveMessage");
	}

	@Override
	public void receiveResult(String arg0) {
		Log.d(LOG_TAG, "Callback receiveResult");
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	//Parcelable implementation
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
	}
	
	public static final Parcelable.Creator<TestCallback> CREATOR = new Parcelable.Creator<TestCallback>() {

		public TestCallback createFromParcel(Parcel in) {
			return new TestCallback(in);
		}

		public TestCallback[] newArray(int size) {
			return new TestCallback[size];
		}
		
	};
	
	private TestCallback(Parcel in) {
		super();
	}

}

