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
package org.societies.android.api.privacytrust.privacy.util.privacypolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

import android.text.TextUtils;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ConditionUtils {
	public static Map<String, String> map2FriendlyName;

	/**
	 * Instantiate a mandatory condition
	 * @param conditionConstant
	 * @param value
	 * @return
	 */
	public static Condition create(ConditionConstants conditionConstant, String value) {
		return create(conditionConstant, value, false);
	}

	public static Condition create(ConditionConstants conditionConstant, String value, boolean optional) {
		Condition condition = new Condition();
		condition.setConditionConstant(conditionConstant);
		condition.setValue(value);
		condition.setOptional(optional);
		return condition;
	}

	public static Condition createPrivate() {
		return create(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY, "1");
	}

	public static Condition createMembersOnly() {
		return create(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY, "1");
	}

	public static Condition createPublic() {
		return create(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1");
	}


	public static String toXmlString(Condition condition){
		StringBuilder sb = new StringBuilder();
		if (null != condition) {
			sb.append("\n<Condition>\n");
			sb.append("\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\" DataType=\""+condition.getConditionConstant().getClass().getName()+"\">\n");
			sb.append("\t\t<AttributeValue DataType=\""+condition.getConditionConstant().name()+"\">"+condition.getValue()+"</AttributeValue>\n");
			sb.append("\t</Attribute>\n");
			sb.append("\t<optional>"+condition.isOptional()+"</optional>\n");
			sb.append("</Condition>");
		}
		return sb.toString();
	}

	public static String toXmlString(List<Condition> conditions){
		StringBuilder sb = new StringBuilder();
		if (null != conditions) {
			for(Condition condition : conditions) {
				sb.append(toXmlString(condition));
			}
		}
		return sb.toString();
	}

	public static String toString(Condition condition){
		StringBuilder builder = new StringBuilder();
		builder.append("Condition [getConditionConstant()=");
		if (null != condition) {
			builder.append(condition.getConditionConstant());
			builder.append(", isOptional()=");
			builder.append(condition.isOptional());
			builder.append(", getValue()=");
			builder.append(condition.getValue());
		}
		builder.append("]");
		return builder.toString();
	}

	public static String toString(List<Condition> conditions){
		StringBuilder sb = new StringBuilder();
		if (null != conditions) {
			for(Condition condition : conditions) {
				sb.append(toString(condition));
			}
		}
		return sb.toString();
	}

	/**
	 * To retrieve the friendly names
	 * @param haystack List of conditions
	 * @return List of condition friendly names
	 */
	public List<String> getFriendlyName(List<Condition> haystack) {
		List<String> friendlyNameList = new ArrayList<String>();
		if (null != haystack && haystack.size() > 0) {
			// Sort
			Collections.sort(haystack, new ConditionComparator());
			// Retrieve friendly names
			for(Condition entry : haystack) {
				friendlyNameList.add(getFriendlyName(entry));
			}
		}
		return friendlyNameList;
	}

	/**
	 * To retrieve the friendly name
	 * @param entry condition
	 * @return condition friendly name
	 */
	public static String getFriendlyName(Condition entry) {
		if (null != entry && null != entry.getConditionConstant()) {
			return "";
		}
		if (null == map2FriendlyName || map2FriendlyName.size() <= 0) {
			map2FriendlyName = new HashMap<String, String>();
			map2FriendlyName.put(ConditionConstants.SHARE_WITH_3RD_PARTIES.name(), "Shared with the world");
			map2FriendlyName.put(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY.name(), "Shared with community members");
			map2FriendlyName.put(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY.name(), "Not shared");
			map2FriendlyName.put(ConditionConstants.MAY_BE_INFERRED.name(), "Warning, this data may be inferred");
			map2FriendlyName.put(ConditionConstants.DATA_RETENTION_IN_SECONDS.name(), "Data retention in seconds");
			map2FriendlyName.put(ConditionConstants.DATA_RETENTION_IN_MINUTES.name(), "Data retention in minutes");
			map2FriendlyName.put(ConditionConstants.DATA_RETENTION_IN_HOURS.name(), "Data retention in hours");
			map2FriendlyName.put(ConditionConstants.RIGHT_TO_OPTOUT.name(), "Right to optout");
			map2FriendlyName.put(ConditionConstants.STORE_IN_SECURE_STORAGE.name(), "Stored in a secure storage");
			map2FriendlyName.put(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA.name(), "Right to access held data");
			map2FriendlyName.put(ConditionConstants.RIGHT_TO_CORRECT_INCORRECT_DATA.name(), "Right to correct invalid data");
		}
		if (map2FriendlyName.containsKey(entry.getConditionConstant().name())) {
			return map2FriendlyName.get(entry.getConditionConstant().name());
		}
		return entry.getConditionConstant().name();
	}
	public static boolean equal(Condition o1, Object o2, boolean dontCheckOptional) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		Condition ro2 = (Condition) o2;
		return (ConditionConstantsUtils.equal(o1.getConditionConstant(), ro2.getConditionConstant())
				&& (TextUtils.equals(o1.getValue(), ro2.getValue()))
				&& (dontCheckOptional || o1.isOptional() == ro2.isOptional())
				);
	}
	@Deprecated
	public static boolean equals(Condition o1, Object o2) {
		return equal(o1, o2);
	}
	public static boolean equal(Condition o1, Object o2) {
		return equal(o1, o2, false);
	}

	public static boolean equal(List<Condition> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { return false; }
		// -- Verify obj type
		List<Condition> ro2 = (List<Condition>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(Condition o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}


	public static class ConditionComparator implements Comparator<Condition> { 
		@Override
		public int compare(
				Condition o1,
				Condition o2) {
			if (equal(o1, o2)) {
				return 0;
			}
			if (null == o1) {
				return 1;
			}
			if (null == o2) {
				return -1;
			}
			return o1.getConditionConstant().name().compareToIgnoreCase(o2.getConditionConstant().name());
		}
	}

	public static boolean contain(Condition conditionToCheck, List<Condition> conditions) {
		if (null == conditions || conditions.size() <= 0 || null == conditionToCheck) {
			return false;
		}
		for(Condition condition : conditions) {
			if (equal(conditionToCheck, condition)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * All mandatory requested elements of the haystack are in the needles list
	 * The needle list may contain other elements
	 * @param needles
	 * @param haystack
	 * @return
	 */
	public static boolean containAllMandotory(List<Condition> needles, List<Condition> haystack) {
		if (null == haystack || haystack.size() <= 0) {
			return true;
		}
		for (Condition entry : haystack){
			if (entry.isOptional()) {
				continue;
			}
			if (!contain(entry, needles)) {
				return false;
			}
		}
		return true;
	}
}
