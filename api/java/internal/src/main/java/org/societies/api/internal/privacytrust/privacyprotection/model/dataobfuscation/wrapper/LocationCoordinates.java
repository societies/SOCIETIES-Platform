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
package org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.IObfuscable;

/**
 * Describe your class here...
 *
 * @author olivierm
 *
 */
@Deprecated
public class LocationCoordinates extends IObfuscable {
	private double latitude;
	private double longitude;
	private double accuracy;
	
	
	/**
	 * @param latitude
	 * @param longitude
	 */
	public LocationCoordinates(double latitude, double longitude, double accuracy) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
	}
	
	
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the accuracy
	 */
	public double getAccuracy() {
		return accuracy;
	}
	/**
	 * @param accuracy the accuracy to set
	 */
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Geolocation [latitude=" + latitude + ", longitude=" + longitude
				+ ", accuracy=" + accuracy + "]";
	}
	public String toXMLString() {
		return "<geolocation>\n" +
				"\t<latitude>" + latitude + "</latitude>\n" +
				"\t<longitude>" + longitude+ "</longitude>\n" +
				"\t<horizontalAccuracy>" + accuracy + "</horizontalAccuracy>\n" +
			"</geolocation>";
	}
	public String toJSONString() {
		return "{\n" +
				"\"latitude\": \""+latitude+"\",\n" +
				"\"longitude\": \""+longitude+"\",\n" +
				"\"horizontalAccuracy\": \""+accuracy+"\"\n" +
				"}";
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
		if (obj instanceof LocationCoordinates) {
			LocationCoordinates other = (LocationCoordinates) obj;
			return new EqualsBuilder()
			.append(this.getLatitude(), other.getLatitude())
			.append(this.getLongitude(), other.getLongitude())
			.append(this.getAccuracy(), other.getAccuracy())
			.isEquals();
		}
		return false;
	}
}
