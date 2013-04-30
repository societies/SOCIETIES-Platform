package org.societies.webapp.models;

import org.societies.api.cis.attributes.Rule.OperationType;

@Deprecated // No longer used after move from JSP to JSF
public class MembershipCriteriaForm {
	
	String attribute;
	
	String operator;
	
	String value1;
	
	String value2;
	
	boolean deleted = false;
	
	
	

	public MembershipCriteriaForm() {
		super();
	}

	public MembershipCriteriaForm(String attribute, String operator,
			String value1, String value2, boolean v) {
		super();
		this.attribute = attribute;
		this.operator = operator;
		this.value1 = value1;
		this.value2 = value2;
		this.deleted = v;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean visible) {
		this.deleted = visible;
	}
	
	
	
}
