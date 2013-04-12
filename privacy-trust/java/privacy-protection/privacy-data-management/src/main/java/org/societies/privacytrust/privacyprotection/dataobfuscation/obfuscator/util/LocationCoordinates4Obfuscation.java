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
package org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.util;

import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;


/**
 * Internal version of LocationCoordinates wrapper. It contains intermediate values
 * useful to obfuscate a location
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class LocationCoordinates4Obfuscation extends LocationCoordinates {
	private double obfuscationLevel;
	private int obfuscationAlgorithm;
	private double shiftDirection;
	private double shiftDistance;
	private double shiftAlpha;
	
	
	/**
	 * @param latitude
	 * @param longitude
	 * @param accuracy
	 */
	public LocationCoordinates4Obfuscation(double latitude, double longitude, double accuracy) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
	}
	
	public LocationCoordinates toLocationCoordinates() {
		LocationCoordinates res = new LocationCoordinates();
		res.setLatitude(latitude);
		res.setLongitude(longitude);
		res.setAccuracy(accuracy);
		return res;
	}
	
	
	/**
	 * @return the obfuscationLevel
	 */
	public double getObfuscationLevel() {
		return obfuscationLevel;
	}
	/**
	 * @param obfuscationLevel the obfuscationLevel to set
	 */
	public void setObfuscationLevel(double obfuscationLevel) {
		this.obfuscationLevel = obfuscationLevel;
	}
	/**
	 * @return the obfuscationAlgorithm
	 */
	public int getObfuscationAlgorithm() {
		return obfuscationAlgorithm;
	}
	/**
	 * @param obfuscationAlgorithm the obfuscationAlgorithm to set
	 */
	public void setObfuscationAlgorithm(int obfuscationAlgorithm) {
		this.obfuscationAlgorithm = obfuscationAlgorithm;
	}
	/**
	 * @return the shiftDirection
	 */
	public double getShiftDirection() {
		return shiftDirection;
	}
	/**
	 * @param shiftDirection the shiftDirection to set
	 */
	public void setShiftDirection(double shiftDirection) {
		this.shiftDirection = shiftDirection;
	}
	/**
	 * @return the shiftDistance
	 */
	public double getShiftDistance() {
		return shiftDistance;
	}
	/**
	 * @param shiftDistance the shiftDistance to set
	 */
	public void setShiftDistance(double shiftDistance) {
		this.shiftDistance = shiftDistance;
	}
	/**
	 * @return the shiftAlpha
	 */
	public double getShiftAlpha() {
		return shiftAlpha;
	}
	/**
	 * @param shiftAlpha the shiftAlpha to set
	 */
	public void setShiftAlpha(double shiftAlpha) {
		this.shiftAlpha = shiftAlpha;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Geolocation [latitude=" + getLatitude() + ", longitude=" + getLongitude()
				+ ", horizontalAccuracy=" + getAccuracy()
				+ ", obfuscationLevel=" + obfuscationLevel
				+ ", obfuscationAlgorithm=" + obfuscationAlgorithm + "]";
	}
	public String toXMLString() {
		return "<geolocation>\n" +
				"\t<latitude>" + getLatitude() + "</latitude>\n" +
				"\t<longitude>" + getLongitude()+ "</longitude>\n" +
				"\t<horizontalAccuracy>" + getAccuracy() + "</horizontalAccuracy>\n" +
				"\t<obfuscationLevel>" + obfuscationLevel + "</obfuscationLevel>\n" +
				"\t<obfuscationAlgorithm>" + obfuscationAlgorithm + "</obfuscationAlgorithm>\n" +
			"</geolocation>";
	}
	public String toJSONString() {
		return "{\n" +
				"\"latitude\": \""+getLatitude()+"\"\n" +
				"\"longitude\": \""+getLongitude()+"\"\n" +
				"\"horizontalAccuracy\": \""+getAccuracy()+"\"\n" +
				"\"obfuscationLevel\": \""+obfuscationLevel+"\"\n" +
				"\"obfuscationAlgorithm\": \""+obfuscationAlgorithm+"\"\n" +
				"}";
	}
}
