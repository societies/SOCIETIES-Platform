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
package org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper;

import org.societies.api.schema.identity.DataIdentifier;

import android.os.Parcel;
import android.os.Parcelable;





/**
 * This data wrapper is an abstraction between obfuscation manager
 * and data models. This is the way for wrapping data to obfuscate them,
 * and filling a type of data (needed to know how obfuscate them) 
 * This wrapper is linked to a specific data obfuscator
 * and know what kind of data is needed to launch the obfuscation. 
 * @author Olivier Maridat (Trialog)
 * @date 14 oct. 2011
 */
public class DataWrapper<E extends Parcelable> implements IDataWrapper<E> {
	/**
	 * ID of the data, useful for persistence
	 */
	private DataIdentifier dataId;
	/**
	 * Data to obfuscate
	 */
	private E data;
	/**
	 * Persistence configuration
	 * Disabled by default 
	 */
	public boolean persistence = false;


	// -- CONSTRUCTOR
	/**
	 * Classical constructor
	 * By default, persistence is disabled  
	 * @param data Data to obfuscate
	 */
	public DataWrapper(E data) {
		this.data = data;
	}
	/**
	 * Persistence constructor
	 * By using this constructor, the persistence will be enabled automatically,
	 * and the unique ID of the data to obfuscate will be used to retrieve obfuscated version of the data.  
	 * @param dataId A unique ID of the data to obfuscate is needed to enable persistence
	 */
	public DataWrapper(DataIdentifier dataId, E data) {
		this(data);
		this.dataId = dataId;
		setPersistenceEnabled(true);
	}
	

	// --- GET/SET
	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper#getDataId()
	 */
	public DataIdentifier getDataId() {
		return dataId;
	}
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper#setDataId(java.lang.String)
	 */
	public void setDataId(DataIdentifier dataId) {
		this.dataId = dataId;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper#getData()
	 */
	public E getData() {
		return data;
	}
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper#setData(java.lang.Object)
	 */
	public void setData(E data) {
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper#isPersistenceEnabled()
	 */
	public boolean isPersistenceEnabled() {
		return persistence;
	}
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper#setPersistenceEnabled(boolean)
	 */
	public void setPersistenceEnabled(boolean persist) {
		persistence = persist;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper#isReadyForObfuscation()
	 */
	public boolean isReadyForObfuscation() {
		if (null != data) {
			return true;
		}
		return false;
	}
	
	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// -- Verify reference equality
		if (obj == this) {
			return true;
		}

		// -- Verify obj type
		if (obj instanceof DataWrapper) {
			DataWrapper other = (DataWrapper) obj;
			return (this.getDataId().equals(other.getDataId()) && this.getData().equals(other.getData()));
		}
		return false;
	}
	
	
	/* ************************
	 * Parcelable Management
	 * ************************ */
	
	public DataWrapper(Parcel in) {
		readFromParcel(in);
	}
	
	/*
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {
		return 0;
	}

	/*
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	public void writeToParcel(Parcel out, int flag) {
		out.writeSerializable(dataId);
		out.writeParcelable(data, flag);
	}
	
	private void readFromParcel(Parcel in) {
		dataId = (DataIdentifier) in.readSerializable();
		data = (E) in.readParcelable(this.getClass().getClassLoader());
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public DataWrapper createFromParcel(Parcel in) {
			return new DataWrapper(in);
		}

		public DataWrapper[] newArray(int size) {
			return new DataWrapper[size];
		}
	};
}
