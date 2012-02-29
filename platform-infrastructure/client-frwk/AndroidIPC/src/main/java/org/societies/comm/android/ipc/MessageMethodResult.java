package org.societies.comm.android.ipc;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

public class MessageMethodResult {

	private static final String KEY_ID = "id",								
								KEY_RETURN_VALUE = "returnValue",
								KEY_THROWABLE = "throwable";
	public static final int WHAT = 2;
	
	private String id;
	private Object returnValue;
	private boolean hasThrowable = false;
	private Throwable throwable;
	private Message msg;
	
	public MessageMethodResult(MessageMethodInvocation method, Object returnValue) {
		this.returnValue= returnValue;
		Bundle data = new Bundle();
		data.putParcelable(KEY_RETURN_VALUE, new ReturnValue(returnValue));
		msg = Message.obtain(null, WHAT);
		msg.setData(data);
	}
	
	public MessageMethodResult(MessageMethodInvocation method, Throwable throwable) {
		this.throwable= throwable;
		hasThrowable = true;
		Bundle data = new Bundle();
		data.putSerializable(KEY_THROWABLE, throwable);
		msg = Message.obtain(null, WHAT);
		msg.setData(data);
	}
	
	public MessageMethodResult(Message message) {
		msg = message;
		message.getData().setClassLoader(MessageMethodResult.class.getClassLoader());
		id = message.getData().getString(KEY_ID);
		if(message.getData().containsKey(KEY_THROWABLE)) {
			hasThrowable = true;
			throwable = (Throwable)message.getData().getSerializable(KEY_THROWABLE);
		}
		else
			returnValue = ((ReturnValue)message.getData().getParcelable(KEY_RETURN_VALUE)).returnValue();
	}
	
	public String id() {
		return id;
	}
	
	public boolean hasThrowable() {
		return hasThrowable;
	}
	
	public Throwable throwable() {
		return throwable;
	}
	
	public Object returnValue() {
		return returnValue;
	}
	
	public Message message() {
		return msg;
	}
	
	private static class ReturnValue implements Parcelable {

		private Object returnValue;

		public ReturnValue(Object returnValue) {
			this.returnValue = returnValue;
		}

		public Object returnValue() {
			return returnValue;
		}

		public int describeContents() {
			return 0;
		}

		public void writeToParcel(Parcel out, int flags) {
			out.writeValue(returnValue);
		}

		public static final Parcelable.Creator<ReturnValue> CREATOR
		= new Parcelable.Creator<ReturnValue>() {
			public ReturnValue createFromParcel(Parcel in) {
				return new ReturnValue(in);
			}

			public ReturnValue[] newArray(int size) {
				return new ReturnValue[size];
			}
		};

		private ReturnValue(Parcel in) {
			returnValue = in.readValue(ReturnValue.class.getClassLoader());
		}
	}
}
