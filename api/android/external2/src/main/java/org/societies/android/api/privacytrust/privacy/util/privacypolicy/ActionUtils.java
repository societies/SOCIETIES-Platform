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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;


/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ActionUtils {
	public static Map<String, String> map2FriendlyName;
	
	/**
	 * Create a mandatory action
	 * 
	 * @param actionConstant
	 * @return
	 */
	public static Action create(ActionConstants actionConstant) {
		return create(actionConstant, false);
	}

	public static Action create(ActionConstants actionConstant, boolean optional) {
		Action action = new Action();
		action.setActionConstant(actionConstant);
		action.setOptional(optional);
		return action;
	}

	/**
	 * Create a list of mandatory actions
	 * 
	 * @param actionConstants Array of actions
	 * @return List of mandatory actions
	 */
	public static List<Action> createList(ActionConstants... actionConstants) {
		List<Action> actions = new ArrayList<Action>();
		for (ActionConstants actionConstant : actionConstants) {
			actions.add(create(actionConstant));
		}
		return actions;
	}


	public static String toXmlString(Action action){
		StringBuilder sb = new StringBuilder();
		if (null != action) {
			sb.append("\n<Action>\n");
			sb.append("\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\""+action.getActionConstant().getClass().getName()+"\">\n");
			sb.append("\t\t<AttributeValue>"+action.getActionConstant().name()+"</AttributeValue>\n");
			sb.append("\t</Attribute>\n");
			sb.append("\t<optional>"+action.isOptional()+"</optional>\n");
			sb.append("</Action>");
		}
		return sb.toString();
	}

	public static String toXmlString(List<Action> actions){
		StringBuilder sb = new StringBuilder();
		if (null != actions) {
			for(Action action : actions) {
				sb.append(toXmlString(action));
			}
		}
		return sb.toString();
	}

	public static String toString(Action action){
		StringBuilder builder = new StringBuilder();
		if (null != action) {
			builder.append("Action [getActionConstant()=");
			builder.append(action.getActionConstant());
			builder.append(", isOptional()=");
			builder.append(action.isOptional());
			builder.append("]");
		}
		return builder.toString();
	}

	public static String toString(List<Action> actions){
		StringBuilder sb = new StringBuilder();
		if (null != actions) {
			for(Action action : actions) {
				sb.append(toString(action));
			}
		}
		return sb.toString();
	}
	

	/**
	 * To retrieve the friendly names
	 * @param haystack List of actions
	 * @return List of action friendly names
	 */
	public List<String> getFriendlyName(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> haystack) {
		List<String> friendlyNameList = new ArrayList<String>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action entry : haystack) {
			friendlyNameList.add(getFriendlyName(entry));
		}
		return friendlyNameList;
	}
	
	/**
	 * Return a friendly description of a list of actions
	 * @param haystack
	 * @return "action1, action2 and action3"
	 */
	public String getFriendlyDescription(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> haystack) {
		return getFriendlyDescription(haystack, false);
	}
	/**
	 * Return a friendly description of a list of actions
	 * @param haystack
	 * @param displayOptionString To display " (optional)" after an option action
	 * @return "action1, action2 and action3"
	 */
	public String getFriendlyDescription(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> haystack, boolean displayOptionalString) {
		if (null == haystack || haystack.size() <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		int size = haystack.size();
		for (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action entry : haystack) {
			sb.append(getFriendlyName(entry));
			if (displayOptionalString && entry.isOptional()) {
				sb.append(" (optional)");
			}
			if (i != (size-1)) {
				if (i == (size-2)) {
					sb.append(" and ");
				}
				else {
					sb.append(", ");
				}
			}
			i++;
		}
		return sb.toString();
	}

	/**
	 * To retrieve the friendly name
	 * @param entry Action
	 * @return Action friendly name
	 */
	public static String getFriendlyName(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action entry) {
		if (null != entry && null != entry.getActionConstant()) {
			return "";
		}
		if (null == map2FriendlyName || map2FriendlyName.size() <= 0) {
			map2FriendlyName = new HashMap<String, String>();
			map2FriendlyName.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.READ.name(), "access");
			map2FriendlyName.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.WRITE.name(), "update");
			map2FriendlyName.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.CREATE.name(), "create");
			map2FriendlyName.put(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants.DELETE.name(), "delete");
		}
		if (map2FriendlyName.containsKey(entry.getActionConstant().name())) {
			return map2FriendlyName.get(entry.getActionConstant().name());
		}
		return entry.getActionConstant().name();
	}

	public static boolean equal(Action o1, Object o2, boolean dontCheckOptional) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		Action ro2 = (Action) o2;
		return (ActionConstantsUtils.equal(o1.getActionConstant(), ro2.getActionConstant()))
				&& (dontCheckOptional || o1.isOptional() == ro2.isOptional());
	}
	@Deprecated
	public static boolean equals(Action o1, Object o2) {
		return equal(o1, o2);
	}
	public static boolean equal(Action o1, Object o2) {
		return equal(o1, o2, false);
	}

	public static boolean equal(List<Action> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		List<Action> ro2 = (List<Action>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(Action o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}
	@Deprecated
	public static boolean equals(List<Action> o1, Object o2) {
		return equal(o1, o2);
	}

	public static boolean contain(Action needle, List<Action> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(Action entry : haystack) {
			if (equal(needle, entry)) {
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
	public static boolean containAllMandotory(List<Action> needles, List<Action> haystack) {
		if (null == haystack || haystack.size() <= 0) {
			return true;
		}
		for (Action entry : haystack){
			if (entry.isOptional()) {
				continue;
			}
			if (!contain(entry, needles)) {
				return false;
			}
		}
		return true;
	}


	@Deprecated
	public static List<Action> fromFormattedString(String actionsString) {
		List<Action> actions = new ArrayList<Action>();
		if (null != actionsString && !"".equals(actionsString)) {
			int pos = 0, end;
			// Loop over actions
			while ((end = actionsString.indexOf('/', pos)) >= 0) {
				String actionString = actionsString.substring(pos, end);
				int positionOptional = actionString.indexOf(':');
				Action action = new Action();
				action.setActionConstant(ActionConstants.fromValue(actionString.substring(0, positionOptional)));
				action.setOptional("false".equals(actionString.substring(positionOptional+1, actionString.length())) ? false : true);
				actions.add(action);
				pos = end + 1;
			}
		}
		return actions;
	}
	@Deprecated
	public static String toFormattedString(List<Action> actions) {
		StringBuilder sb = new StringBuilder();
		if (null != actions) {
			for(int i=0; i<actions.size(); i++) {
				sb.append(actions.get(i).getActionConstant().name()+":"+(actions.get(i).isOptional() ? "true" : "false")+"/");
			}
		}
		return sb.toString();
	}
}
