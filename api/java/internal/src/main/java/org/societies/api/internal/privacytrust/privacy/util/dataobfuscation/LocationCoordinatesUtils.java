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
package org.societies.api.internal.privacytrust.privacy.util.dataobfuscation;

import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;

/**
 * 
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class LocationCoordinatesUtils {

	public static LocationCoordinates create(double latitude, double longitude, double accuracy) {
		LocationCoordinates data = new LocationCoordinates();
		data.setLatitude(latitude);
		data.setLongitude(longitude);
		data.setAccuracy(accuracy);
		return data;
	}

	public static String toXmlString(LocationCoordinates data) {
		if (null == data) {
			return "<geolocation></geolocation>";
		}
		return "<geolocation>\n" +
				"\t<latitude>" + data.getLatitude() + "</latitude>\n" +
				"\t<longitude>" + data.getLongitude()+ "</longitude>\n" +
				"\t<horizontalAccuracy>" + data.getAccuracy() + "</horizontalAccuracy>\n" +
				"</geolocation>";
	}
	public static String toJsonString(LocationCoordinates data) {
		if (null == data) {
			return "{}";
		}
		return "{\n" +
				"\"latitude\": \""+data.getLatitude()+"\",\n" +
				"\"longitude\": \""+data.getLongitude()+"\",\n" +
				"\"horizontalAccuracy\": \""+data.getAccuracy()+"\"\n" +
				"}";
	}

	public static boolean equal(LocationCoordinates origin, Object obj) {
		// -- Verify reference equality
		if (obj == null) { return false; }
		if (origin == obj) { return true; }
		if (origin.getClass() != obj.getClass()) { return false; }
		// -- Verify obj type
		if (obj instanceof LocationCoordinates) {
			LocationCoordinates other = (LocationCoordinates) obj;
			return (origin.getLatitude() == other.getLatitude()
					&& origin.getLongitude() == other.getLongitude()
					&& origin.getAccuracy() == other.getAccuracy()
					);
		}
		return false;
	}

	public static boolean similar(LocationCoordinates o1, LocationCoordinates o2) {
		return similar(o1, o2, 3);
	}
	public static boolean similar(LocationCoordinates o1, LocationCoordinates o2, int precision) {
		// Equals
		if (o1 == o2 || (null != o1 && o1.equals(o2))) {
			return true;
		}
		// Just to be sure
		if (null == o1 || null == o2) {
			return false;
		}

		// Similar
		return (round(o1.getLatitude(), precision) == round(o2.getLatitude(), precision)
				&& round(o1.getLongitude(), precision) == round(o2.getLongitude(), precision)
				&& round(o1.getAccuracy(), precision) == round(o2.getAccuracy(), precision)
				);
	}

	private static double round(double d, int precision) {
		int temp=(int)((d*Math.pow(10, precision)));
		return (((double)temp)/Math.pow(10, precision));
	}

}
