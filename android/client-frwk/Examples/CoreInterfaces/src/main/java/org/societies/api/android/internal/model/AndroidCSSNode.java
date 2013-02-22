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



import org.societies.api.schema.cssmanagement.CssNode;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Android version of {@link CSSNode}. Implements Parcelable interface for
 * Android IPC.
 *
 */
public class AndroidCSSNode extends CssNode implements Parcelable {
	/**
	 * Default Constructor
	 */
	public AndroidCSSNode() {
		super();
	}

	public void setIdentity (String identity) {
		this.identity = identity;
	}
	
	public String getIdentity() {
		return this.identity;
	}
	
	public void setStatus (int status) {
		this.status = status;
	}
	public int getStatus() {
		return this.status;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
	
	/**
	 * Convert CssNode to AndroidCSSNode 
	 * @param cssNode
	 * 
	 * @return AndroidCSSNode
	 */
	public static AndroidCSSNode convertCssNode(CssNode cssNode) {
		AndroidCSSNode aNode = new AndroidCSSNode();
		
		aNode.setIdentity(cssNode.getIdentity());
		aNode.setStatus(cssNode.getStatus());
		aNode.setType(cssNode.getType());
		
		return aNode;
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
			out.writeString(getIdentity());
			out.writeInt(getType());
			out.writeInt(getStatus());
	}
	
	public static final Parcelable.Creator<AndroidCSSNode> CREATOR = new Parcelable.Creator<AndroidCSSNode>() {

		@Override
		public AndroidCSSNode createFromParcel(Parcel in) {
			return new AndroidCSSNode(in);
		}

		@Override
		public AndroidCSSNode[] newArray(int size) {
			return new AndroidCSSNode[size];
		}
		
	};
	
	private AndroidCSSNode(Parcel in) {
		super();
		setIdentity(in.readString());
		setType(in.readInt());
		setStatus(in.readInt());
	}
}
