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
import java.util.List;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ActionUtils {
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
	public static String toFormattedString(List<Action> actions) {
		StringBuilder sb = new StringBuilder();
		if (null != actions) {
			for(int i=0; i<actions.size(); i++) {
				sb.append(actions.get(i).getActionConstant().name()+":"+(actions.get(i).isOptional() ? "true" : "false")+"/");
			}
		}
		return sb.toString();
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

	public static boolean equals(Action o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		Action rhs = (Action) o2;
		return (o1.getActionConstant().name().equals(rhs.getActionConstant().name())
				&& o1.isOptional() == rhs.isOptional());
	}

	public static boolean equals(List<Action> o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		List<Action> rhs = (List<Action>) o2;
		boolean result = true;
		int i = 0;
		for(Action o1Action : o1) {
			result &= equals(o1Action, rhs.get(i++));
		}
		return result;
	}
}
