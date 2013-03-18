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
package org.societies.android.api.identity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.societies.api.schema.identity.DataTypeDescription;

/**
 * Utility method that helps manipulating data types and provides a hierarchy to SOCIETIES data
 * 
 * @author Olivier Maridat (Trialog)
 */
public class DataTypeUtils {
	private static Map<String, Set<String>> dataTypeHierarchy;
	private static Map<String, String> dataTypeAntiHierarchy;
	private static Map<String, DataTypeDescription> dataTypeDescription;
	private static boolean loaded = false;


	public DataTypeUtils() {
		if (!loaded) {
			dataTypeHierarchy = new HashMap<String, Set<String>>();
			dataTypeAntiHierarchy = new HashMap<String, String>();
			dataTypeDescription = new HashMap<String, DataTypeDescription>();

			// -- Load hierarchy
			loadDataHierarchy();

			// -- Load type description
			loadDataTypeDescription();

			loaded = true;
		}
	}


	/**
	 * To know if a data type is a leaf (external node) in the hierarchy or not
	 * E.g. true for NAME_FIRST (leaf) and ACTION (root without children), but false for NAME (root with children)
	 * @param dataType Data type name
	 * @return True if the data type is a leaf, false otherwise
	 */
	public boolean isLeaf(String dataType) {
		// In the anti-hierarchy (i.e. children of someone)
		// Or nor in the hierarchy, neither in the anti-hierarchy (i.e. not referenced type: root and leaf by default)
		return  (dataTypeAntiHierarchy.containsKey(dataType)
				|| (!dataTypeHierarchy.containsKey(dataType) && !dataTypeAntiHierarchy.containsKey(dataType)));
	}

	/**
	 * To know if a data type is a root (internal node at the top level) in the hierarchy or not
	 * E.g. true for NAME (root with children) and ACTION (root without children), but false for NAME_FIRST (leaf)
	 * @param dataType Data type name
	 * @return True if the data type is a root, false otherwise
	 */
	public boolean isRoot(String dataType) {
		// In the hierarchy (i.e. has got children)
		// Or nor in the hierarchy, neither in the anti-hierarchy (i.e. not referenced type: root and leaf by default)
		return (dataTypeHierarchy.containsKey(dataType)
				|| (!dataTypeHierarchy.containsKey(dataType) && !dataTypeAntiHierarchy.containsKey(dataType)));
	}

	/**
	 * To retrieve the relevant lookable data types in the data hierarchy: itself or its children if any
	 * E.g. children of NAME (root with children) are: NAME_FIRST, NAME_LAST,
	 * E.g. children of ACTION (root) are: ACTION,
	 * E.g. children of NAME_FIRST (leaf) are: NAME_FIRST
	 * @param dataType Data type name
	 * @return The list of children data type if dataType is a root type with children, or a list containing only dataType if it is a leaf or a root without children
	 */
	public Set<String> getLookableDataTypes(String dataType) {
		// Retrieve children
		Set<String> children = getChildren(dataType);
		// If no result: means no children, return at least the used dataType
		if (null == children) {
			children = new HashSet<String>();
			children.add(dataType);
		}
		return children;
	}

	/**
	 * To retrieve the children data types of a data type in the data hierarchy
	 * E.g. children of NAME (root with children) are: NAME_FIRST, NAME_LAST,
	 * E.g. children of ACTION (root) are: null,
	 * E.g. children of NAME_FIRST (leaf) are: null
	 * @param dataType Data type name
	 * @return The list of children data type if dataType is a root type with children, or null if dataType is a leaf or a root without children
	 */
	public Set<String> getChildren(String dataType) {
		// Leaf: no children
		if (isLeaf(dataType)) {
			return null;
		}
		// Otherwise: return the children
		Set<String> children = dataTypeHierarchy.get(dataType);
		// Robustness: return null if it is empty for some reason (but if data hiearchy is configured correctly, it should not)
		return children.isEmpty() ? null : children;
	}

	/**
	 * To retrieve the parent data type of a data type in the data hierarchy
	 * E.g. parent of NAME (root with children) is: null,
	 * E.g. parent of ACTION (root) is: null,
	 * E.g. parent of NAME_FIRST (leaf) is: NAME
	 * @param dataType Data type name
	 * @return The parent data type if dataType is a leaf, or null if dataType is a root
	 */
	public String getParent(String dataType) {
		// Not root: there is a parent
		if (!isRoot(dataType)) {
			return dataTypeAntiHierarchy.get(dataType);
		}
		// No parent
		return null;
	}

	/**
	 * To retrieve the friendly description of a data type
	 * If no existing friendly description is retrieved, the data type is sanitized and returned
	 * @param dataType Data type name
	 * @return This data type friendly name and description
	 */
	public DataTypeDescription getFriendlyDescription(String dataType) {
		DataTypeDescription description = null;
		// Retrieve the stored description
		if (dataTypeDescription.containsKey(dataType)) {
			description = dataTypeDescription.get(dataType);
		}
		// No description available, or retrieved description null: compute one as good as possible
		if (null == description) {
			description = DataTypeDescriptionUtils.create(dataType); 
		}
		return description;
	}

	/**
	 * To retrieve the friendly description of a data types
	 * @param dataTypeList List of data type logical name
	 * @return List of data type friendly names and descriptions
	 */
	public List<DataTypeDescription> getFriendlyDescription(List<String> dataTypeList) {
		List<DataTypeDescription> friendlyNameList = new ArrayList<DataTypeDescription>();
		for(String dataType : dataTypeList) {
			friendlyNameList.add(getFriendlyDescription(dataType));
		}
		return friendlyNameList;
	}


	// -- Private methods

	/**
	 * Load the whole Societies data hierarchy
	 * @return True if success
	 */
	private boolean loadDataHierarchy() {
//		Set<String> postAddressHomeChildren = new HashSet<String>();
//		postAddressHomeChildren.add(CtxAttributeTypes.ADDRESS_HOME_STREET_NUMBER);
//		postAddressHomeChildren.add(CtxAttributeTypes.ADDRESS_HOME_STREET_NAME);
//		postAddressHomeChildren.add(CtxAttributeTypes.ADDRESS_HOME_CITY);
//		postAddressHomeChildren.add(CtxAttributeTypes.ADDRESS_HOME_COUNTRY);
//		dataTypeHierarchy.put("ADDRESS_HOME", postAddressHomeChildren);
//		dataTypeAntiHierarchy.put(CtxAttributeTypes.ADDRESS_HOME_STREET_NUMBER, "ADDRESS_HOME");
//		dataTypeAntiHierarchy.put(CtxAttributeTypes.ADDRESS_HOME_STREET_NAME, "ADDRESS_HOME");
//		dataTypeAntiHierarchy.put(CtxAttributeTypes.ADDRESS_HOME_CITY, "ADDRESS_HOME");
//		dataTypeAntiHierarchy.put(CtxAttributeTypes.ADDRESS_HOME_COUNTRY, "ADDRESS_HOME");
//
//		Set<String> postAddressWorkChildren = new HashSet<String>();
//		postAddressWorkChildren.add(CtxAttributeTypes.ADDRESS_WORK_STREET_NUMBER);
//		postAddressWorkChildren.add(CtxAttributeTypes.ADDRESS_WORK_STREET_NAME);
//		postAddressWorkChildren.add(CtxAttributeTypes.ADDRESS_WORK_CITY);
//		postAddressWorkChildren.add(CtxAttributeTypes.ADDRESS_WORK_COUNTRY);
//		dataTypeHierarchy.put("ADDRESS_WORK", postAddressWorkChildren);
//		dataTypeAntiHierarchy.put(CtxAttributeTypes.ADDRESS_WORK_STREET_NUMBER, "ADDRESS_WORK");
//		dataTypeAntiHierarchy.put(CtxAttributeTypes.ADDRESS_WORK_STREET_NAME, "ADDRESS_WORK");
//		dataTypeAntiHierarchy.put(CtxAttributeTypes.ADDRESS_WORK_CITY, "ADDRESS_WORK");
//		dataTypeAntiHierarchy.put(CtxAttributeTypes.ADDRESS_WORK_COUNTRY, "ADDRESS_WORK");
//
//		Set<String> nameChildren = new HashSet<String>();
//		nameChildren.add(CtxAttributeTypes.NAME_FIRST);
//		nameChildren.add(CtxAttributeTypes.NAME_LAST);
//		dataTypeHierarchy.put(CtxAttributeTypes.NAME, nameChildren);
//		dataTypeAntiHierarchy.put(CtxAttributeTypes.NAME_FIRST, CtxAttributeTypes.NAME);
//		dataTypeAntiHierarchy.put(CtxAttributeTypes.NAME_LAST, CtxAttributeTypes.NAME);
		return true;
	}

	/**
	 * Load friendly description for whole Societies data
	 * @return True if success
	 */
	private boolean loadDataTypeDescription() {
//		addDataTypeDescription(CtxAttributeTypes.ABOUT, "Information about this entity");
//		addDataTypeDescription(CtxAttributeTypes.ACTION, "Action done by this entity");
//		addDataTypeDescription(CtxAttributeTypes.AGE, "Age of this entity");
//		addDataTypeDescription(CtxAttributeTypes.BOOKS, "Favorite books", "Favorite books of this entity");
		return true;
	}

	private void addDataTypeDescription(String dataTypeIdentifier, String dataTypeFriendlyName, String dataTypeFriendlyDescription) {
		dataTypeDescription.put(dataTypeIdentifier, DataTypeDescriptionUtils.create(dataTypeIdentifier, dataTypeFriendlyName, dataTypeFriendlyDescription));
	}

	private void addDataTypeDescription(String dataTypeIdentifier, String dataTypeFriendlyDescription) {
		dataTypeDescription.put(dataTypeIdentifier, DataTypeDescriptionUtils.create(dataTypeIdentifier, null, dataTypeFriendlyDescription));
	}
}
