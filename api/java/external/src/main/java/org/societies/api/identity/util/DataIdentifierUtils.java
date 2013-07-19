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
package org.societies.api.identity.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;

/**
 * Utility method that helps manipulating DataIdentifier objects
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class DataIdentifierUtils {
	/**
	 * Generate a list of URI: sheme://ownerId/type from these data ids
	 * @param dataIds List of data identifier
	 * @return List of URI string representing these data identifier
	 */
	public static List<String> toUriString(List<DataIdentifier> dataIds) {
		if (null == dataIds || dataIds.size() <= 0) {
			return null;
		}
		List<String> dataIdsString = new ArrayList<String>();
		for(DataIdentifier dataId : dataIds) {
			dataIdsString.add(toUriString(dataId));
		}
		return dataIdsString;
	}

	/**
	 * Generate a URI: sheme://ownerId/type
	 * @param dataId
	 * @return
	 */
	public static String toUriString(DataIdentifier dataId)
	{
		if (dataId instanceof CtxIdentifier) {
			return ((CtxIdentifier) dataId).toUriString();
		}
		return toUriString(dataId.getScheme(), dataId.getOwnerId(), dataId.getType());
	}

	/**
	 * Generate a URI: sheme://ownerid/type
	 * @param scheme
	 * @param ownerId
	 * @param dataType
	 * @return
	 */
	public static String toUriString(DataIdentifierScheme scheme, String ownerId, String dataType) {
		StringBuilder str = new StringBuilder("");
		str.append((scheme != null ? scheme.value()+"://" : "://"));
		str.append((ownerId != null ? ownerId+"/" : "/"));
		str.append((dataType != null ? dataType+"/" : "/"));
		return str.toString();
	}

	/**
	 * Generate a URI: sheme:///type
	 * @param scheme
	 * @param dataType
	 * @return
	 */
	public static String toUriString(DataIdentifierScheme scheme, String dataType) {
		return toUriString(scheme, "", dataType);
	}

	public static boolean equal(DataIdentifier o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		DataIdentifier ro2 = (DataIdentifier) o2;
		String uri1 = DataIdentifierUtils.toUriString(o1);
		String uri2 = DataIdentifierUtils.toUriString(ro2);
		return null != uri1 && uri1.equals(uri2);
	}

	public static boolean equal(DataIdentifier o1, Resource o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		// -- Verify obj type
		String uri1 = DataIdentifierUtils.toUriString(o1);
		String uri2 = ResourceUtils.getDataIdUri(o2);
		return null != uri1 && uri1.equals(uri2);
	}

	/**
	 * scheme + type are equals?
	 */
	public static boolean hasSameType(DataIdentifier id1, DataIdentifier id2) {
		if (null == id1 || null == id2) {
			return false;
		}
		// Scheme equal?
		if (!DataIdentifierSchemeUtils.equal(DataTypeFactory.getScheme(id1), DataTypeFactory.getScheme(id2))) {
			return false;
		}
		// Type equal?
		String type1 = DataTypeFactory.getType(id1);
		String type2 = DataTypeFactory.getType(id2);
		return type1.equals(type2);
	}

	/**
	 * scheme + type are equals, or id1 type is a parent type of id2 type?
	 */
	public static boolean isParentOrSameType(DataIdentifier id1, DataIdentifier id2) {
		if (null == id1 || null == id2) {
			return false;
		}
		// Scheme equal?
		if (!DataIdentifierSchemeUtils.equal(DataTypeFactory.getScheme(id1), DataTypeFactory.getScheme(id2))) {
			return false;
		}
		// Type equal?
		String type1 = DataTypeFactory.getType(id1);
		String type2 = DataTypeFactory.getType(id2);
		Set<String> subTypes1 = (new DataTypeUtils()).getLookableDataTypes(type1);
		return subTypes1.contains(type2);
	}

	/**
	 * To sort a list of data ids by their parent type
	 * E.g. Ids of types NAME_FIRST (leaf), NAME_LAST (leaf), ACTION (root and leaf)  will be sorted as: NAME -> NAME_FIRST, NAME_LAST ; ACTION -> ACTION
	 * E.g. Ids of types NAME (root not leaf), NAME_FIRST (leaf), NAME_LAST (leaf), ACTION (root and leaf) will be sorted as: NAME -> NAME_FIRST, NAME_LAST ; ACTION -> ACTION
	 * E.g. Ids of types NAME (root not leaf), ACTION (root and leaf) will be sorted as: NAME -> null ; ACTION -> ACTION
	 * @param dataIds List of data ids
	 * @return A map of parent types and their related data id (or this parent type if it is also a leaf)
	 */
	public static Map<String, Set<DataIdentifier>> sortByParent(Set<DataIdentifier> dataIds) {
		if (null == dataIds || dataIds.size() <= 0) {
			return null;
		}
		// -- Create the map
		Map<String, Set<DataIdentifier>> sorted = new HashMap<String, Set<DataIdentifier>>();
		DataTypeUtils dataTypeUtils = new DataTypeUtils();
		for(DataIdentifier dataId : dataIds) {
			// Retrieve parent type
			String dataTypeParent = dataTypeUtils.getParent(dataId.getType());
			Set<DataIdentifier> dataTypeGroup = null;
			// Parent type
			if (null == dataTypeParent) {
				dataTypeParent = dataId.getType();
				// Parent & leaf
				if (dataTypeUtils.isLeaf(dataId.getType())) {
					dataTypeGroup = new HashSet<DataIdentifier>();
					dataTypeGroup.add(dataId);
				}
				// Parent with children
				else {
					dataTypeGroup = sorted.get(dataTypeParent);
				}
			}
			// Child
			else {
				dataTypeGroup = sorted.get(dataTypeParent);
				if (null == dataTypeGroup) {
					dataTypeGroup = new HashSet<DataIdentifier>();
				}
				dataTypeGroup.add(dataId);
			}
			sorted.put(dataTypeParent, dataTypeGroup);
		}
		return sorted;
	}

	/**
	 * @throws MalformedCtxIdentifierException 
	 * @see DataIdentifierFactory#fromUri(String)
	 */
	@Deprecated
	public static DataIdentifier fromUri(String dataIdUri) throws MalformedCtxIdentifierException
	{
		return DataIdentifierFactory.fromUri(dataIdUri);
	}
}
