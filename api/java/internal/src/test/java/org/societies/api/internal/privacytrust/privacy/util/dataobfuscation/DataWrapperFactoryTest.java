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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Name;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;

public class DataWrapperFactoryTest {
	// Values
	private String ownerId = "fooCss";
	private String firstnameStr = "Olivier";
	private String lastnameStr = "Maridat";
	private double latitude = 45.255;
	private double longitude = 2.45;
	private double accuracy = 100.5;
	// Ids
	private DataIdentifier nameId;
	private DataIdentifier firstnameId = null;
	private DataIdentifier lastnameId = null;
	private DataIdentifier actionId = null;
	private DataIdentifier locationCoordinatesId = null;
	// Data
	private List<CtxModelObject> ctxDataListNameCopy;
	private List<CtxModelObject> ctxDataListName;
	private List<CtxModelObject> ctxDataListAction;
	private List<CtxModelObject> ctxDataListLocationCoordinates;
	private CtxAttribute firstname;
	private CtxAttribute lastname;
	private CtxAttribute firstnameCopy;
	private CtxAttribute lastnameCopy;
	private CtxAttribute action;
	private CtxAttribute locationCoordinates;


	@Before
	public void setUp() {
		// Generate Ids
		try {
			nameId = DataIdentifierFactory.create(DataIdentifierScheme.CONTEXT, ownerId, CtxAttributeTypes.NAME);
			firstnameId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+ownerId+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.NAME_FIRST+"/33");
			lastnameId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+ownerId+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.NAME_LAST+"/38");
			actionId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+ownerId+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.ACTION+"/42");
			locationCoordinatesId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+ownerId+"/ENTITY/person/1/ATTRIBUTE/"+CtxAttributeTypes.LOCATION_COORDINATES+"/76");
		} catch (MalformedCtxIdentifierException e) {
			fail("Faillure during data id creation from URI: "+e);
		}
		// Create list of CtxModelObject lists
		ctxDataListNameCopy = new ArrayList<CtxModelObject>();
		firstnameCopy = new CtxAttribute((CtxAttributeIdentifier) firstnameId);
		firstnameCopy.setStringValue(firstnameStr);
		firstnameCopy.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		firstnameCopy.setValueType(CtxAttributeValueType.STRING);
		ctxDataListNameCopy.add(firstnameCopy);
		lastnameCopy = new CtxAttribute((CtxAttributeIdentifier) lastnameId);
		lastnameCopy.setStringValue(lastnameStr);
		lastnameCopy.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		lastnameCopy.setValueType(CtxAttributeValueType.STRING);
		ctxDataListNameCopy.add(lastnameCopy);
		
		ctxDataListName = new ArrayList<CtxModelObject>();
		firstname = new CtxAttribute((CtxAttributeIdentifier) firstnameId);
		firstname.setStringValue(firstnameStr);
		firstname.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		firstname.setValueType(CtxAttributeValueType.STRING);
		ctxDataListName.add(firstname);
		lastname = new CtxAttribute((CtxAttributeIdentifier) lastnameId);
		lastname.setStringValue(lastnameStr);
		lastname.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		lastname.setValueType(CtxAttributeValueType.STRING);
		ctxDataListName.add(lastname);

		ctxDataListAction = new ArrayList<CtxModelObject>();
		action = new CtxAttribute((CtxAttributeIdentifier) actionId);
		action.setStringValue("Do this !");
		action.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		action.setValueType(CtxAttributeValueType.STRING);
		ctxDataListAction.add(action);

		ctxDataListLocationCoordinates = new ArrayList<CtxModelObject>();
		locationCoordinates = new CtxAttribute((CtxAttributeIdentifier) locationCoordinatesId);
		locationCoordinates.setStringValue(latitude+","+longitude);
		locationCoordinates.getQuality().setOriginType(CtxOriginType.MANUALLY_SET);
		locationCoordinates.getQuality().setPrecision(accuracy);
		action.setValueType(CtxAttributeValueType.STRING);
		ctxDataListLocationCoordinates.add(locationCoordinates);
	}


	@Test
	public void testGetRetrieveWrapperFromCtx() {
		// -- Name
		// Generate
		DataWrapper dataWrapper1 = DataWrapperFactory.getDataWrapper(CtxAttributeTypes.NAME, ctxDataListName);
		assertNotNull("Generated wrapper should not be null", dataWrapper1);
		assertEquals("First name should be "+firstname, firstnameStr, ((Name)dataWrapper1.getData()).getFirstName());
		assertEquals("Last name should be "+lastname, lastnameStr, ((Name)dataWrapper1.getData()).getLastName());
		assertEquals("Data wrapper should have a correct type", CtxAttributeTypes.NAME, dataWrapper1.getDataType());
		DataWrapper dataWrapper1b = DataWrapperFactory.getDataWrapper(CtxAttributeTypes.NAME, ctxDataListLocationCoordinates);
		assertNotNull("Generated wrapper should not be null", dataWrapper1b);
		assertEquals("First name should be empty", "", ((Name)dataWrapper1b.getData()).getFirstName());
		assertEquals("Last name should be empty", "", ((Name)dataWrapper1b.getData()).getLastName());

		// Retrieve
		int expectedRetrievedListSize = 2;
		List<CtxModelObject> ctxDataListNameRetrieved = DataWrapperFactory.retrieveData(dataWrapper1, ctxDataListName);
		assertNotNull("Retrieved data should not be null", ctxDataListNameRetrieved);
		assertTrue(expectedRetrievedListSize+" data should be retrieved", ctxDataListNameRetrieved.size() == expectedRetrievedListSize);
		assertTrue("firstname should in the retrieved list", ctxDataListNameRetrieved.contains(firstname));
		assertTrue("lastname should in the retrieved list", ctxDataListNameRetrieved.contains(lastname));
		assertFalse("locationCoordinates should not be in the retrieved list", ctxDataListNameRetrieved.contains(locationCoordinates));
		assertFalse("action should not be in the retrieved list", ctxDataListNameRetrieved.contains(action));
		assertEquals("action data list should still be equals to its copy", ctxDataListNameCopy, ctxDataListNameRetrieved);


		// -- Action
		DataWrapper dataWrapper2 = DataWrapperFactory.getDataWrapper(CtxAttributeTypes.ACTION, ctxDataListAction);
		assertNull("Generated wrapper should be null", dataWrapper2);
		DataWrapper dataWrapper2b = DataWrapperFactory.getDataWrapper(DataWrapperFactory.NOT_OBFUSCABLE_TYPE, ctxDataListAction);
		assertNull("Generated wrapper should be null", dataWrapper2b);


		// -- Location coordinates
		DataWrapper dataWrapper3 = DataWrapperFactory.getDataWrapper(CtxAttributeTypes.LOCATION_COORDINATES, ctxDataListLocationCoordinates);
		assertNotNull("Generated wrapper should not be null", dataWrapper3);
		assertTrue("Latitude should be "+latitude+" but is "+((LocationCoordinates)dataWrapper3.getData()).getLatitude(), latitude == ((LocationCoordinates)dataWrapper3.getData()).getLatitude());
		assertTrue("Longitude should be "+longitude+" but is "+((LocationCoordinates)dataWrapper3.getData()).getLongitude(), longitude == ((LocationCoordinates)dataWrapper3.getData()).getLongitude());
		assertTrue("Accuracy should be "+accuracy+" but is "+((LocationCoordinates)dataWrapper3.getData()).getAccuracy(), accuracy == ((LocationCoordinates)dataWrapper3.getData()).getAccuracy());
		assertEquals("Data wrapper should have a correct type", CtxAttributeTypes.LOCATION_COORDINATES, dataWrapper3.getDataType());

		// Retrieve
		expectedRetrievedListSize = 1;
		List<CtxModelObject> ctxDataListLocationCoordinatesRetrieved = DataWrapperFactory.retrieveData(dataWrapper3, ctxDataListLocationCoordinates);
		assertNotNull("Retrieved data should not be null", ctxDataListLocationCoordinatesRetrieved);
		assertTrue(expectedRetrievedListSize+" data should be retrieved", ctxDataListLocationCoordinatesRetrieved.size() == expectedRetrievedListSize);
		assertTrue("locationCoordinates should in the retrieved list", ctxDataListLocationCoordinatesRetrieved.contains(locationCoordinates));
		assertFalse("action should not be in the retrieved list", ctxDataListLocationCoordinatesRetrieved.contains(action));
		assertFalse("firstname should not be in the retrieved list", ctxDataListLocationCoordinatesRetrieved.contains(firstname));
		assertFalse("lastname should not be in the retrieved list", ctxDataListLocationCoordinatesRetrieved.contains(lastname));
	}

	@Test
	public void testGetRetrieveNameWrapper() {
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
	public void testGetRetrieveLocationCoordinatesWrapper() {
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

	@Test
	public void testSortByObfuscability() {
		// Null or empty
		Map<String, List<CtxModelObject>> ctxDataListRetrieved1 = DataWrapperFactory.sortByObfuscability(null);
		Map<String, List<CtxModelObject>> ctxDataListRetrieved2 = DataWrapperFactory.sortByObfuscability(new ArrayList<CtxModelObject>());
		assertNull("Retrieved data list should be null", ctxDataListRetrieved1);
		assertNull("Retrieved data list should be null", ctxDataListRetrieved2);

		// -- Name
		Map<String, List<CtxModelObject>> ctxDataListNameRetrieved = DataWrapperFactory.sortByObfuscability(ctxDataListName);
		int expectedRetrievedListSize = 1;
		assertNotNull("Retrieved data list should not be null", ctxDataListNameRetrieved);
		assertTrue(expectedRetrievedListSize+" data should be retrieved but was "+ctxDataListNameRetrieved.size(), ctxDataListNameRetrieved.size() == expectedRetrievedListSize);
		assertFalse("No unobfuscable data should be retrieved "+ctxDataListNameRetrieved, ctxDataListNameRetrieved.containsKey(DataWrapperFactory.NOT_OBFUSCABLE_TYPE));
		assertTrue("Obfuscable data group should be used", ctxDataListNameRetrieved.containsKey(CtxAttributeTypes.NAME));
		assertEquals("A correct list should be retrieved", ctxDataListName, ctxDataListNameRetrieved.get(CtxAttributeTypes.NAME));
		assertEquals("A correct list should be retrieved (even copy)", ctxDataListNameCopy, ctxDataListNameRetrieved.get(CtxAttributeTypes.NAME));

		// -- Action
		Map<String, List<CtxModelObject>> ctxDataListActionRetrieved = DataWrapperFactory.sortByObfuscability(ctxDataListAction);
		expectedRetrievedListSize = 1;
		assertNotNull("Retrieved action list should not be null", ctxDataListActionRetrieved);
		assertTrue(expectedRetrievedListSize+" data should be retrieved but was "+ctxDataListActionRetrieved.size(), ctxDataListActionRetrieved.size() == expectedRetrievedListSize);
		assertTrue("No obfuscable data should be retrieved "+ctxDataListActionRetrieved, ctxDataListActionRetrieved.containsKey(DataWrapperFactory.NOT_OBFUSCABLE_TYPE));
		assertEquals("A correct list should be retrieved", ctxDataListAction, ctxDataListActionRetrieved.get(DataWrapperFactory.NOT_OBFUSCABLE_TYPE));

		// -- Location Coordinates
		Map<String, List<CtxModelObject>> ctxDataListLocationCoordinatesRetrieved = DataWrapperFactory.sortByObfuscability(ctxDataListLocationCoordinates);
		expectedRetrievedListSize = 1;
		assertNotNull("Retrieved data list should not be null", ctxDataListLocationCoordinatesRetrieved);
		assertTrue(expectedRetrievedListSize+" data should be retrieved but was "+ctxDataListLocationCoordinatesRetrieved.size(), ctxDataListLocationCoordinatesRetrieved.size() == expectedRetrievedListSize);
		assertFalse("No unobfuscable data should be retrieved "+ctxDataListLocationCoordinatesRetrieved, ctxDataListLocationCoordinatesRetrieved.containsKey(DataWrapperFactory.NOT_OBFUSCABLE_TYPE));
		assertTrue("Obfuscable data group should be used", ctxDataListLocationCoordinatesRetrieved.containsKey(CtxAttributeTypes.LOCATION_COORDINATES));
		assertEquals("A correct list should be retrieved", ctxDataListLocationCoordinates, ctxDataListLocationCoordinatesRetrieved.get(CtxAttributeTypes.LOCATION_COORDINATES));

		// -- Le Grand Mix
		List<CtxModelObject> ctxDataList = new ArrayList<CtxModelObject>();
		ctxDataList.addAll(ctxDataListName);
		ctxDataList.addAll(ctxDataListAction);
		ctxDataList.addAll(ctxDataListLocationCoordinates);
		Map<String, List<CtxModelObject>> ctxDataListMixRetrieved = DataWrapperFactory.sortByObfuscability(ctxDataList);
		expectedRetrievedListSize = 3;
		assertNotNull("Retrieved data list should not be null", ctxDataListMixRetrieved);
		assertTrue(expectedRetrievedListSize+" data should be retrieved but was "+ctxDataListMixRetrieved.size(), ctxDataListMixRetrieved.size() == expectedRetrievedListSize);
		assertTrue("Unobfuscable data should be retrieved "+ctxDataListMixRetrieved, ctxDataListMixRetrieved.containsKey(DataWrapperFactory.NOT_OBFUSCABLE_TYPE));
		assertEquals("Actions are unobfuscable", ctxDataListAction, ctxDataListActionRetrieved.get(DataWrapperFactory.NOT_OBFUSCABLE_TYPE));
		assertTrue("Location coordinates Obfuscable data group should be retrieved", ctxDataListMixRetrieved.containsKey(CtxAttributeTypes.LOCATION_COORDINATES));
		assertEquals("Locations coordinates are obfuscable", ctxDataListLocationCoordinates, ctxDataListMixRetrieved.get(CtxAttributeTypes.LOCATION_COORDINATES));
		assertTrue("Location coordinates Obfuscable data group should be retrieved", ctxDataListMixRetrieved.containsKey(CtxAttributeTypes.NAME));
		assertEquals("Name are obfuscable", ctxDataListName, ctxDataListMixRetrieved.get(CtxAttributeTypes.NAME));

	}
}
