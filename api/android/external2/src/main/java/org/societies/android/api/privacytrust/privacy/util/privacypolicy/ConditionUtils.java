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

import java.util.List;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ConditionUtils {

	/**
	 * Instantiate a mandatory condition
	 * @param conditionConstant
	 * @param value
	 * @return
	 */
	public static Condition create(ConditionConstants conditionConstant, String value) {
		return create(conditionConstant, value, true);
	}

	public static Condition create(ConditionConstants conditionConstant, String value, boolean optional) {
		Condition condition = new Condition();
		condition.setConditionConstant(conditionConstant);
		condition.setValue(value);
		condition.setOptional(optional);
		return condition;
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

	public static boolean equals(Condition o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		Condition rhs = (Condition) o2;
		return ((o1.getConditionConstant().name().equals(rhs.getConditionConstant().name())
				&& o1.getValue().equals(rhs.getValue())
				&& o1.isOptional() == rhs.isOptional())
				);
	}

	public static boolean equals(List<Condition> o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		List<Condition> rhs = (List<Condition>) o2;
		boolean result = true;
		int i = 0;
		for(Condition o1Element : o1) {
			result &= equals(o1Element, rhs.get(i++));
		}
		return result;
	}
}
