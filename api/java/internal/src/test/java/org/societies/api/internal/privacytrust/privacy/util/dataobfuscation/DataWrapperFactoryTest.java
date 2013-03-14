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

import static org.junit.Assert.*;

import org.junit.Test;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Name;

public class DataWrapperFactoryTest {
	
	@Test
	public void testGetNameWrapper() {
		String firstname = "Olivier";
		String lastname = "Maridat";
		// -- Generate
		DataWrapper dataWrapper = DataWrapperFactory.getNameWrapper(firstname, lastname);
		assertNotNull("Generated wrapper should not be null", dataWrapper);
		assertEquals("First name should be "+firstname, firstname, ((Name)dataWrapper.getData()).getFirstName());
		assertEquals("Last name should be "+lastname, lastname, ((Name)dataWrapper.getData()).getLastName());
		
		// -- Retrieve
		Name retrievedName = DataWrapperFactory.retrieveName(dataWrapper);
		assertNotNull("Retrieved name should not be null", retrievedName);
		assertEquals("First name should be "+firstname, firstname,retrievedName.getFirstName());
		assertEquals("Last name should be "+lastname, lastname, retrievedName.getLastName());
		
		// Compare
		assertTrue("Generated and retrieved should be equal", NameUtils.equals(retrievedName, ((Name)dataWrapper.getData())));
	}

	@Test
	public void testGetLocationCoordinatesWrapper() {
		double latitude = 2.8;
		double longitude = 45.3;
		double accuracy = 542.0;
		// -- Generate
		DataWrapper dataWrapper = DataWrapperFactory.getLocationCoordinatesWrapper(latitude, longitude, accuracy);
		assertNotNull("Generated wrapper should not be null", dataWrapper);
		assertTrue("Latitude should be "+latitude, latitude == ((LocationCoordinates)dataWrapper.getData()).getLatitude());
		assertTrue("Longitude should be "+longitude, longitude == ((LocationCoordinates)dataWrapper.getData()).getLongitude());
		assertTrue("Accuracy should be "+accuracy, accuracy == ((LocationCoordinates)dataWrapper.getData()).getAccuracy());
		
		// -- Retrieve
		LocationCoordinates retrievedName = DataWrapperFactory.retrieveLocationCoordinates(dataWrapper);
		assertNotNull("Retrieved location should not be null", retrievedName);
		assertTrue("Latitude should be "+latitude, latitude == retrievedName.getLatitude());
		assertTrue("Longitude should be "+longitude, longitude == retrievedName.getLongitude());
		assertTrue("Accuracy should be "+accuracy, accuracy == retrievedName.getAccuracy());
		
		// Compare
		assertTrue("Generated and retrieved should be equal", LocationCoordinatesUtils.equal(retrievedName, ((LocationCoordinates)dataWrapper.getData())));
		assertTrue("Generated and retrieved should be similar", LocationCoordinatesUtils.similar(retrievedName, ((LocationCoordinates)dataWrapper.getData())));
	}
}
