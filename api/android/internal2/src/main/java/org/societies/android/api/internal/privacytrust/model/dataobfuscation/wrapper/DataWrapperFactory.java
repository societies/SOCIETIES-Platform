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

import org.societies.api.internal.schema.privacytrust.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.model.dataobfuscation.LocationCoordinates;
import org.societies.api.internal.schema.privacytrust.model.dataobfuscation.Name;
import org.societies.api.internal.schema.privacytrust.model.dataobfuscation.PostalLocation;
import org.societies.api.internal.schema.privacytrust.model.dataobfuscation.Status;
import org.societies.api.internal.schema.privacytrust.model.dataobfuscation.Temperature;




/**
 * Utilities to instantiate DataWrapper for data obfuscation
 * @author Olivier Maridat (Trialog)
 * @date 14 oct. 2011
 */
public class DataWrapperFactory {
	// -- CONTEXT ATTRIBUTE

	
	// -- GEOLOCATION
	
	/**
	 * To get a LocationCoordinatesWrapper
	 * The persistence is disabled by default, the obfuscated geolocation will not
	 * be stored after obfuscation.
	 * @param latitude Latitude
	 * @param longitude Longitude
	 * @param accuracy Accuracy in meters
	 * @return A LocationCoordinatesWrapper
	 */
	public static DataWrapper getLocationCoordinatesWrapper(double latitude, double longitude, double accuracy) {
		LocationCoordinates data = new LocationCoordinates();
		data.setLatitude(latitude);
		data.setLongitude(longitude);
		data.setAccuracy(accuracy);
		DataWrapper wrapper = new DataWrapper();
		wrapper.setData(data);
		return wrapper;
	}
	
	/**
	 * To get the Postal location wrapper
	 * @param logicalName
	 * @param streetNumber
	 * @param streetName
	 * @param district
	 * @param town
	 * @param postalCode
	 * @param department
	 * @param region
	 * @param country
	 * @param continent
	 * @param planet
	 * @return
	 */
	public static DataWrapper getPostalLocationWrapper(String logicalName, String streetNumber,
			String streetName, String district, String town, String postalCode,
			String department, String region, String country, String continent,
			String planet) {
		PostalLocation data = new PostalLocation();
		data.setLogicalName(logicalName);
		data.setStreetNumber(streetNumber);
		data.setStreetName(streetName);
		data.setDistrict(district);
		data.setTown(town);
		data.setPostalCode(postalCode);
		data.setDepartment(department);
		data.setRegion(region);
		data.setCountry(country);
		data.setContinent(continent);
		data.setPlanet(planet);
		DataWrapper wrapper = new DataWrapper();
		wrapper.setData(data);
		return wrapper;
	}


	// -- NAME
	
	/**
	 * To get a NameWrapper
	 * The persistence is disabled by default, the obfuscated name will not
	 * @param firstName
	 * @param lastName
	 * @return the NameWrapper
	 */
	public static DataWrapper getNameWrapper(String firstName, String lastName) {
		Name data = new Name();
		data.setFirstName(firstName);
		data.setLastName(lastName);
		DataWrapper wrapper = new DataWrapper();
		wrapper.setData(data);
		return wrapper;
	}

	
	// -- TEMPERATURE
	
	/**
	 * To get a TemperatureWrapper
	 * The persistence is disabled by default
	 * @param degree Degree in number value
	 * @return the Temperature wrapper
	 */
	public static DataWrapper getTemperatureWrapper(double degree) {
		Temperature data = new Temperature();
		data.setDegree(degree);
		DataWrapper wrapper = new DataWrapper();
		wrapper.setData(data);
		return wrapper;
	}
	/**
	 * To get a TemperatureWrapper
	 * The persistence is disabled by default
	 * @param degree Degree in string value @see {@value org.societies.android.api.internal.privacytrust.model.dataobfuscation.Temperature#heatString}
	 * @return the Temperature wrapper
	 */
	public static DataWrapper getTemperatureWrapper(String degree) {
		Temperature data = new Temperature();
		data.setDegreeString(degree);
		DataWrapper wrapper = new DataWrapper();
		wrapper.setData(data);
		return wrapper;
	}
	
	
	// -- ACTION
//	
//	/**
//	 * To get a ActivityWrapper
//	 * @param actor
//	 * @param verb
//	 * @param object
//	 * @return
//	 */
//	public static IDataWrapper<Activity> getActivityWrapper(String actor, String verb, String object) {
//		Activity data = new Activity();
//		data.setActor(actor);
//		data.setVerb(verb);
//		data.setObject(object);
//		return new DataWrapper<Activity>(data);
//	}
	
	
	// -- STATUS
	
	/**
	 * 
	 * @param status
	 * @return
	 */
	public static DataWrapper getStatusWrapper(String status) {
		Status data = new Status();
		data.setStatus(status);
		DataWrapper wrapper = new DataWrapper();
		wrapper.setData(data);
		return wrapper;
	}
}
