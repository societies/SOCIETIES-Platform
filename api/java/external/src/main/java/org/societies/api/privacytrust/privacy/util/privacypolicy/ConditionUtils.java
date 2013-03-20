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
package org.societies.api.privacytrust.privacy.util.privacypolicy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;

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
	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition create(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants conditionConstant, String value) {
		return create(conditionConstant, value, true);
	}
	
	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition create(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants conditionConstant, String value, boolean optional) {
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition condition = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition();
		condition.setConditionConstant(conditionConstant);
		condition.setValue(value);
		condition.setOptional(optional);
		return condition;
	}
	
	public static Condition toCondition(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition conditionBean)
	{
		if (null == conditionBean) {
			return null;
		}
		ConditionConstants conditionConstant = ConditionConstants.valueOf(conditionBean.getConditionConstant().name());
		return new Condition(conditionConstant, conditionBean.getValue(), conditionBean.isOptional());
	}
	public static List<Condition> toConditions(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition> conditionBeans)
	{
		if (null == conditionBeans) {
			return null;
		}
		List<Condition> conditions = new ArrayList<Condition>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition conditionBean : conditionBeans) {
			conditions.add(ConditionUtils.toCondition(conditionBean));
		}
		return conditions;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition toConditionBean(Condition condition)
	{
		if (null == condition) {
			return null;
		}
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition conditionBean = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition();
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants conditionConstant = org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants.valueOf(condition.getConditionName().name());
		conditionBean.setConditionConstant(conditionConstant);
		conditionBean.setValue(condition.getValueAsString());
		conditionBean.setOptional(condition.isOptional());
		return conditionBean;
	}
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition> toConditionBeans(List<Condition> conditions)
	{
		if (null == conditions) {
			return null;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition> conditionBeans = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition>();
		for(Condition condition : conditions) {
			conditionBeans.add(ConditionUtils.toConditionBean(condition));
		}
		return conditionBeans;
	}

	public static boolean contains(ConditionConstants conditionToCheck, List<Condition> conditions) {
		if (null == conditions || conditions.size() <= 0 || null == conditionToCheck) {
			return false;
		}
		for(Condition condition : conditions) {
			if (condition.getConditionName().equals(conditionToCheck)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(Condition conditionToCheck, List<Condition> conditions) {
		if (null == conditions || conditions.size() <= 0 || null == conditionToCheck) {
			return false;
		}
		for(Condition condition : conditions) {
			if (condition.getConditionName().equals(conditionToCheck.getConditionName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(List<Condition> conditionsToCheck, List<Condition> conditions) {
		if (null == conditions || conditions.size() <= 0 || null == conditionsToCheck || conditionsToCheck.size() <= 0 || conditionsToCheck.size() < conditionsToCheck.size()) {
			return false;
		}
		for(Condition conditionToCheck : conditionsToCheck) {
			if (!contains(conditionToCheck, conditions)) {
				return false;
			}
		}
		return true;
	}

	public static boolean containsOr(List<Condition> conditionsToCheck, List<Condition> conditions) {
		if (null == conditions || conditions.size() <= 0 || null == conditionsToCheck || conditionsToCheck.size() <= 0 || conditionsToCheck.size() < conditionsToCheck.size()) {
			return false;
		}
		for(Condition conditionToCheck : conditionsToCheck) {
			if (contains(conditionToCheck, conditions)) {
				return true;
			}
		}
		return false;
	}

	public static String toXmlString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition condition){
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

	public static String toXmlString(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition> conditions){
		StringBuilder sb = new StringBuilder();
		if (null != conditions) {
			for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition condition : conditions) {
				sb.append(toXmlString(condition));
			}
		}
		return sb.toString();
	}

	public static boolean equals(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition rhs = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition) o2;
		return new EqualsBuilder()
		.append(o1.getConditionConstant().name(), rhs.getConditionConstant().name())
		.append(o1.getValue(), rhs.getValue())
		.append(o1.isOptional(), rhs.isOptional())
		.isEquals();
	}
	
	public static String toString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition condition){
		
			StringBuilder builder = new StringBuilder();
			builder.append("Condition [getConditionConstant()=");
			builder.append(condition.getConditionConstant());
			builder.append(", isOptional()=");
			builder.append(condition.isOptional());
			builder.append(", getValue()=");
			builder.append(condition.getValue());
			builder.append("]");
			return builder.toString();
		

	}
}
