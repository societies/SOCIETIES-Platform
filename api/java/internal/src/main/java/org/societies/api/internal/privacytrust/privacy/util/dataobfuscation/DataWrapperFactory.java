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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.identity.util.DataTypeUtils;
import org.societies.api.internal.privacytrust.privacy.model.dataobfuscation.ObfuscatorInfo;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Name;


/**
 * Utility methods to instantiate DataWrapper for data obfuscation
 * 
 * @author Olivier Maridat (Trialog)
 * @created 14 oct. 2011
 * @updated 04 jul. 2013
 */
public class DataWrapperFactory {
	//	private static Logger LOG = LoggerFactory.getLogger(DataWrapperFactory.class.getName());

	public static final String NOT_OBFUSCABLE_TYPE = "NOT_OBFUCSABLE";


	// -- CONTEXT ATTRIBUTE

	/**
	 * Sort a list of ctx data by their near parent obfuscable type.
	 * If a data is not obfuscable, it is sorted as {@link #NOT_OBFUSCABLE_TYPE}.
	 * E.g. NAME_FIRST (obfuscable, NAME), NAME_LAST (obfuscable, NAME), LOCATION_COORDINATES (obfuscable, LOCATION_COORDINATES) and ACTION (not obfuscable) will be sorted as: NAME -> (NAME_FIRST, NAME_LAST), LOCATION_COORDINATES -> (LOCATION_CORDINATES), NOT_OBFUSCABLE -> (ACTION)
	 * 
	 * @param ctxDataList The list of ctx attributes to sort by obfuscable types
	 * @return A map of obfucsable types and their related list of CtxModelObject, plus potentially a field {@link #NOT_OBFUSCABLE_TYPE} with the related list of not obfuscable CtxModelObject
	 */
	public static Map<String, List<CtxModelObject>> sortByObfuscability(List<CtxModelObject> ctxDataList) {
		if (null == ctxDataList || ctxDataList.size() <= 0) {
			//			LOG.error("Can't sort null values");
			return null;
		}
		String notObfuscableType = "NOT_OBFUCSABLE";
		DataTypeUtils dataTypeUtils = new DataTypeUtils();
		ObfuscatorInfoFactory obfuscatorInfoFactory = new ObfuscatorInfoFactory();
		Map<String, List<CtxModelObject>> sorted = new HashMap<String, List<CtxModelObject>>();
		for(CtxModelObject data : ctxDataList) {
			List<CtxModelObject> dataTypeGroup = null;
			String dataType = data.getId().getType();
			// -- Directly an obfuscable type ?
			ObfuscatorInfo obfuscatorInfo = obfuscatorInfoFactory.getObfuscatorInfo(dataType);
			if (null != obfuscatorInfo && obfuscatorInfo.isObfuscable()) {
				dataTypeGroup = new ArrayList<CtxModelObject>();
				dataTypeGroup.add(data);
				sorted.put(dataType, dataTypeGroup);
				continue;
			}
			// -- Parent obfuscable?
			// Retrieve parent type
			String dataTypeParent = dataTypeUtils.getParent(dataType);
			// Already a parent type
			if (null == dataTypeParent) {
				dataTypeParent = dataType;
			}
			// Is obfuscable ?
			obfuscatorInfo = obfuscatorInfoFactory.getObfuscatorInfo(dataTypeParent);
			if (null == obfuscatorInfo || !obfuscatorInfo.isObfuscable()) {
				dataTypeParent = notObfuscableType;
			}
			// Retrieve this group or create it
			dataTypeGroup = sorted.get(dataTypeParent);
			if (null == dataTypeGroup) {
				dataTypeGroup = new ArrayList<CtxModelObject>();
			}
			// Add data
			dataTypeGroup.add(data);
			sorted.put(dataTypeParent, dataTypeGroup);
		}
		return sorted;
	}

	/**
	 * Generate a data wrapper from a bunch of ctx data "ctxDataList" from the same obfuscable type
	 * Only LOCATION_COORDINATES and NAME are covered at the moment.
	 * 
	 * @param obfuscableType Type of the obfuscation. Every ctx data types to obfuscate are child of this type, or are this type
	 * @param ctxDataList List of ctx attribute to obfuscate together
	 * @return A wrapper containing the ctx data values, or null if no data wrapper are retrieved
	 */
	public static DataWrapper getDataWrapper(String obfuscableType, List<CtxModelObject> ctxDataList) {
		if (null == obfuscableType || null == ctxDataList || ctxDataList.size() <= 0) {
			//			LOG.error("Can't find a data wrapper from null values");
			return null;
		}

		DataWrapper dataWrapper = null;
		if (CtxAttributeTypes.LOCATION_COORDINATES.equals(obfuscableType)) {
			dataWrapper = getLocationCoordinatesWrapper(ctxDataList);
		}
		else if (CtxAttributeTypes.NAME.equals(obfuscableType)) {
			dataWrapper = getNameWrapper(ctxDataList);
		}
		return dataWrapper;
	}
	/**
	 * Retrieve the list of Context data from the obfuscated wrapper
	 * Only LOCATION_COORDINATES and NAME are covered at the moment.
	 * 
	 * @param obfuscableType Type of the obfuscation. Every ctx data types to obfuscate are child of this type, or are this type
	 * @param obfuscatedDataWrapper Wrapper of the obfuscated data to retrieve
	 * @param originaCtxDataList Original list of ctx attribute, not yet obfuscated. They will used to generate the list of obfuscated ctx attributes
	 * @return A list of obfuscated ctx attributes, same size as the original list but potentially in a different order. Null if null parameters
	 */
	public static List<CtxModelObject> retrieveData(DataWrapper obfuscatedDataWrapper, List<CtxModelObject> originalCtxDataList) {
		List<CtxModelObject> data = null;
		if (null == obfuscatedDataWrapper || null == originalCtxDataList || originalCtxDataList.size() <= 0) {
			//			LOG.error("Can't retrieve data from null values");
			return data;
		}
		// Retrieve data depending of their types
		if (CtxAttributeTypes.LOCATION_COORDINATES.equals(obfuscatedDataWrapper.getDataType())) {
			data = retrieveLocationCoordinates(obfuscatedDataWrapper, originalCtxDataList);
		}
		else if (CtxAttributeTypes.NAME.equals(obfuscatedDataWrapper.getDataType())) {
			data = retrieveName(obfuscatedDataWrapper, originalCtxDataList);
		}
		return data;
	}


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
		String dataType = CtxAttributeTypes.LOCATION_COORDINATES;
		LocationCoordinates data = LocationCoordinatesUtils.create(latitude, longitude, accuracy);
		return DataWrapperUtils.create(dataType, data);
	}
	/**
	 * Duplication of {@link #getLocationCoordinatesWrapper(double, double, double)} for Context usage.
	 */
	public static DataWrapper getLocationCoordinatesWrapper(List<CtxModelObject> ctxDataList) {
		double latitude = 0.0;
		double longitude = 0.0;
		double accuracy = 0.0;
		for(CtxModelObject data : ctxDataList) {
			if (null == data || null == data.getId() || !(data instanceof CtxAttribute) || !CtxAttributeTypes.LOCATION_COORDINATES.equals(data.getId().getType())) {
				continue;
			}
			CtxAttribute attribute = (CtxAttribute)data;
			if (null == attribute.getStringValue()) {
				break;
			}
			String[] rawValue = attribute.getStringValue().split(",");
			if (rawValue.length != 2) {
				break;
			}
			try {
				latitude = Double.valueOf(rawValue[0]);
				longitude = Double.valueOf(rawValue[1]);
			}
			catch(NumberFormatException e) {
				latitude = 0.0;
				longitude = 0.0;
			}
			if (null != attribute.getQuality()) {
				accuracy = attribute.getQuality().getPrecision();
			}
			break;
		}
		return getLocationCoordinatesWrapper(latitude, longitude, accuracy);
	}
	public static LocationCoordinates retrieveLocationCoordinates(DataWrapper dataWrapper) {
		String dataType = CtxAttributeTypes.LOCATION_COORDINATES;
		if (null == dataWrapper || !dataType.equals(dataWrapper.getDataType())) {
			return null;
		}
		return (LocationCoordinates) dataWrapper.getData();
	}
	public static List<CtxModelObject> retrieveLocationCoordinates(DataWrapper obfuscatedDataWrapper, List<CtxModelObject> originalCtxDataList) {
		LocationCoordinates obfuscatedData = retrieveLocationCoordinates(obfuscatedDataWrapper);
		List<CtxModelObject> obfuscatedCtxDataList = new ArrayList<CtxModelObject>();
		if (null == obfuscatedData || null == originalCtxDataList || originalCtxDataList.isEmpty()) {
			return obfuscatedCtxDataList;
		}
		for (CtxModelObject data : originalCtxDataList) {
			if (null == data || null == data.getId() || !(data instanceof CtxAttribute) || !CtxAttributeTypes.LOCATION_COORDINATES.equals(data.getId().getType())) {
				continue;
			}
			// Copy
			CtxAttribute attribute = ((CtxAttribute)data);
			CtxAttribute newCtxObject = new CtxAttribute((CtxAttributeIdentifier)data.getId());
			newCtxObject.setHistoryRecorded(attribute.isHistoryRecorded());
			newCtxObject.setSourceId(attribute.getSourceId());
			newCtxObject.setValueMetric(attribute.getValueMetric());
			newCtxObject.setValueType(attribute.getValueType());
			// Update
			newCtxObject.setStringValue(obfuscatedData.getLatitude()+","+obfuscatedData.getLongitude());
			newCtxObject.getQuality().setPrecision(obfuscatedData.getAccuracy());
			newCtxObject.getQuality().setOriginType(CtxOriginType.INFERRED);
			obfuscatedCtxDataList.add(newCtxObject);
			break;
		}
		return obfuscatedCtxDataList;
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
		String dataType = CtxAttributeTypes.NAME;
		Name data = NameUtils.create(firstName, lastName);
		return DataWrapperUtils.create(dataType, data);
	}
	/**
	 * Duplication of {@link #getNameWrapper(String, String)} for Context usage.
	 */
	public static DataWrapper getNameWrapper(List<CtxModelObject> ctxDataList) {
		String firstname = "";
		String lastname = "";
		int found = 0;
		for(CtxModelObject data : ctxDataList) {
			if (!(data instanceof CtxAttribute)) {
				continue;
			}
			if (CtxAttributeTypes.NAME_FIRST.equals(data.getId().getType())) {
				found++;
				firstname =  ((CtxAttribute)data).getStringValue();
			}
			if (CtxAttributeTypes.NAME_LAST.equals(data.getId().getType())) {
				found++;
				lastname =  ((CtxAttribute)data).getStringValue();
			}
			if (found >= 2)
				break;
		}
		return getNameWrapper(firstname, lastname);
	}
	public static Name retrieveName(DataWrapper dataWrapper) {
		String dataType = CtxAttributeTypes.NAME;
		if (null == dataWrapper || !dataType.equals(dataWrapper.getDataType())) {
			return null;
		}
		return (Name) dataWrapper.getData();
	}
	public static List<CtxModelObject> retrieveName(DataWrapper obfuscatedDataWrapper, List<CtxModelObject> originalCtxDataList) {
		List<CtxModelObject> obfuscatedCtxDataList = new ArrayList<CtxModelObject>();
		Name obfuscatedData = retrieveName(obfuscatedDataWrapper);
		if (null == obfuscatedData || null == originalCtxDataList || originalCtxDataList.isEmpty()) {
			return obfuscatedCtxDataList;
		}
		int found = 0;
		for (CtxModelObject data : originalCtxDataList) {
			if (null == data || null == data.getId() || !(data instanceof CtxAttribute)) {
				continue;
			}
			if (CtxAttributeTypes.NAME_FIRST.equals(data.getId().getType())
					|| CtxAttributeTypes.NAME_LAST.equals(data.getId().getType())) {
				found++;
				// Copy
				CtxAttribute attribute = ((CtxAttribute)data);
				CtxAttribute newCtxObject = new CtxAttribute((CtxAttributeIdentifier)data.getId());
				newCtxObject.setHistoryRecorded(attribute.isHistoryRecorded());
				newCtxObject.setSourceId(attribute.getSourceId());
				newCtxObject.setValueMetric(attribute.getValueMetric());
				newCtxObject.setValueType(attribute.getValueType());
				// Update
				if (CtxAttributeTypes.NAME_LAST.equals(data.getId().getType())) {
					newCtxObject.setStringValue(obfuscatedData.getLastName());
				}
				else {
					newCtxObject.setStringValue(obfuscatedData.getFirstName());

				}
				newCtxObject.getQuality().setOriginType(CtxOriginType.INFERRED);
				obfuscatedCtxDataList.add(newCtxObject);
			}
			if (found >= 2)
				break;
		}
		return obfuscatedCtxDataList;
	}
}
