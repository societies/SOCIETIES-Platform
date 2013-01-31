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
package org.societies.android.api.privacytrust.trust.model;

import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is a <code>Parcelable</code> wrapper around {@link TrustEvidenceType}
 * which is used to characterise the type of trust evidence.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public final class ATrustEvidenceType implements Parcelable {
	
	/** The {@link TrustEvidenceType} wrapped in this <code>ATrustEvidenceType</code>. */
	private final TrustEvidenceType type;
	
	public static final Parcelable.Creator<ATrustEvidenceType> CREATOR =
			new Parcelable.Creator<ATrustEvidenceType>() {

				/**
				 * Creates a <code>ATrustEvidenceType</code> from the given
				 * {@link Parcel}.
				 * 
				 * @throws IllegalArgumentException 
				 *            if a <code>ATrustEvidenceType</code> cannot be
				 *            created from <code>source</code>.
				 * @see ATrustEvidenceType#writeToParcel(Parcel, int)
				 */
				public ATrustEvidenceType createFromParcel(Parcel source) {
					
					final String typeName = source.readString();
					final TrustEvidenceType type = TrustEvidenceType.valueOf(typeName);
					return new ATrustEvidenceType(type);
				}

				public ATrustEvidenceType[] newArray(int size) {
					
					return new ATrustEvidenceType[size];
				}
		};
	
	/**
	 * Creates a <code>ATrustEvidenceType</code> instance from the given 
	 * {@link TrustEvidenceType}.
	 * 
	 * @throws NullPointerException
	 *             if <code>type</code> is <code>null</code>.
	 */
	public ATrustEvidenceType(final TrustEvidenceType type) {
		
		if (type == null)
			throw new NullPointerException("type can't be null");
		
		this.type = type;
	}

	/**
	 * Returns the {@link TrustEvidenceType} wrapped in this 
	 * <code>ATrustEvidenceType</code>.
	 * 
	 * @return the {@link TrustEvidenceType} wrapped in this 
	 *         <code>ATrustEvidenceType</code>.
	 */
	public TrustEvidenceType getType() {
		
		return this.type;
	}
	
	/*
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {
		
		return 0;
	}

	/*
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(this.type.name());
	}
	
	/**
	 * Returns a String representation of this <code>ATrustedEntityId</code>.
	 * 
	 * @return a String representation of this <code>ATrustedEntityId</code>.
	 */
	@Override
	public String toString() {
		
		return this.type.toString();
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		return this.type.hashCode();
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object that) {
		
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (this.getClass() != that.getClass())
			return false;
		
		ATrustEvidenceType other = (ATrustEvidenceType) that;
		return (this.type.equals(other.type));
	}
}