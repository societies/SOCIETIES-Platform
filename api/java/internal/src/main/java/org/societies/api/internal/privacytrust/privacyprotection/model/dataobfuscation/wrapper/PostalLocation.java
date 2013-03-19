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
 * Postal location
 *
 * @author Olivier Maridat (Trialog)
 *
 */
@Deprecated
public class PostalLocation extends IObfuscable {
	private String logicalName;
	private String streetNumber;
	private String streetName;
	private String district;
	private String town;
	private String postalCode;
	private String department;
	private String region;
	private String country;
	private String continent;
	private String planet;

	/**
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
	 */
	public PostalLocation(String logicalName, String streetNumber,
			String streetName, String district, String town, String postalCode,
			String department, String region, String country, String continent,
			String planet) {
		super();
		this.logicalName = logicalName;
		this.streetNumber = streetNumber;
		this.streetName = streetName;
		this.district = district;
		this.town = town;
		this.postalCode = postalCode;
		this.department = department;
		this.region = region;
		this.country = country;
		this.continent = continent;
		this.planet = planet;
	}
	/**
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
	 */
	public PostalLocation(String logicalName, String streetNumber,
			String streetName, String district, String town, String postalCode,
			String department, String region, String country, String continent) {
		this(logicalName, streetNumber, streetName, district, town, postalCode, department, region, country, continent, "Earth");
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
		if (obj instanceof PostalLocation) {
			PostalLocation other = (PostalLocation) obj;
			return new EqualsBuilder()
			.append(this.getLogicalName(), other.getLogicalName())
			.append(this.getStreetNumber(), other.getStreetNumber())
			.append(this.getStreetName(), other.getStreetName())
			.append(this.getDistrict(), other.getDistrict())
			.append(this.getTown(), other.getTown())
			.append(this.getPostalCode(), other.getPostalCode())
			.append(this.getDepartment(), other.getDepartment())
			.append(this.getRegion(), other.getRegion())
			.append(this.getCountry(), other.getCountry())
			.append(this.getContinent(), other.getContinent())
			.append(this.getPlanet(), other.getPlanet())
			.isEquals();
		}
		return false;
	}
	/**
	 * @return the logicalName
	 */
	public String getLogicalName() {
		return logicalName;
	}
	/**
	 * @param logicalName the logicalName to set
	 */
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	/**
	 * @return the streetNumber
	 */
	public String getStreetNumber() {
		return streetNumber;
	}
	/**
	 * @param streetNumber the streetNumber to set
	 */
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	/**
	 * @return the streetName
	 */
	public String getStreetName() {
		return streetName;
	}
	/**
	 * @param streetName the streetName to set
	 */
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	/**
	 * @return the district
	 */
	public String getDistrict() {
		return district;
	}
	/**
	 * @param district the district to set
	 */
	public void setDistrict(String district) {
		this.district = district;
	}
	/**
	 * @return the town
	 */
	public String getTown() {
		return town;
	}
	/**
	 * @param town the town to set
	 */
	public void setTown(String town) {
		this.town = town;
	}
	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}
	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}
	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}
	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}
	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return the continent
	 */
	public String getContinent() {
		return continent;
	}
	/**
	 * @param continent the continent to set
	 */
	public void setContinent(String continent) {
		this.continent = continent;
	}
	/**
	 * @return the planet
	 */
	public String getPlanet() {
		return planet;
	}
	/**
	 * @param planet the planet to set
	 */
	public void setPlanet(String planet) {
		this.planet = planet;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PostalLocation [logicalName=" + logicalName + ", streetNumber="
				+ streetNumber + ", streetName=" + streetName + ", district="
				+ district + ", town=" + town + ", postalCode=" + postalCode
				+ ", department=" + department + ", region=" + region
				+ ", country=" + country + ", continent=" + continent
				+ ", planet=" + planet + "]";
	}
}
