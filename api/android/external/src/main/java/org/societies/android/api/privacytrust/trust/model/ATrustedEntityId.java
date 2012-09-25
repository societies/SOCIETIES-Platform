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

import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is a <code>Parcelable</code> wrapper around {@link TrustedEntityId}
 * which is used to uniquely identify trusted CSSs, CISs or services.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
public final class ATrustedEntityId implements Parcelable {
	
	/** The {@link TrustedEntityId} wrapped in this <code>ATrustedEntityId</code>. */
	private final TrustedEntityId teid;
	
	public static final Parcelable.Creator<ATrustedEntityId> CREATOR =
			new Parcelable.Creator<ATrustedEntityId>() {

				/**
				 * Creates a <code>ATrustedEntityId</code> from the given
				 * {@link Parcel}.
				 * 
				 * @throws IllegalArgumentException 
				 *            if a <code>ATrustedEntityId</code> cannot be
				 *            created from <code>source</code>.
				 * @see ATrustedEntityId#writeToParcel(Parcel, int)
				 */
				public ATrustedEntityId createFromParcel(Parcel source) {
					
					final String str = source.readString();
					TrustedEntityId teid = null;
					try {
						teid = new TrustedEntityId(str);
					} catch (MalformedTrustedEntityIdException mteie) {
						
						throw new IllegalArgumentException(
								mteie.getLocalizedMessage(), mteie);
					}
					return new ATrustedEntityId(teid);
				}

				public ATrustedEntityId[] newArray(int size) {
					
					return new ATrustedEntityId[size];
				}
		};
	
	/**
	 * Creates a <code>ATrustedEntityId</code> instance from the given 
	 * {@link TrustedEntityId}.
	 * 
	 * @throws NullPointerException
	 *             if <code>teid</code> is <code>null</code>.
	 */
	public ATrustedEntityId(final TrustedEntityId teid) {
		
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		
		this.teid = teid;
	}
	
	/**
	 * Creates a <code>ATrustedEntityId</code> instance from the given 
	 * {@link TrustedEntityId} String representation.
	 * 
	 * @throws NullPointerException 
	 *            if <code>str</code> is <code>null</code>.
	 * @throws MalformedTrustedEntityIdException
	 *            if <code>str</code> is not formatted correctly.
	 */
	public ATrustedEntityId(final String str) throws MalformedTrustedEntityIdException {
		
		this(new TrustedEntityId(str));
	}

	/**
	 * Returns the {@link TrustedEntityId} wrapped in this 
	 * <code>ATrustedEntityId</code>.
	 * 
	 * @return the {@link TrustedEntityId} wrapped in this 
	 *         <code>ATrustedEntityId</code>.
	 */
	public TrustedEntityId getTeid() {
		
		return this.teid;
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
		
		dest.writeString(this.teid.toString());
	}
	
	/**
	 * Returns a String representation of this <code>ATrustedEntityId</code>.
	 * 
	 * @return a String representation of this <code>ATrustedEntityId</code>.
	 */
	@Override
	public String toString() {
		
		return this.teid.toString();
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		return this.teid.hashCode();
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
		
		ATrustedEntityId other = (ATrustedEntityId) that;
		return (this.teid.equals(other.teid));
	}
}