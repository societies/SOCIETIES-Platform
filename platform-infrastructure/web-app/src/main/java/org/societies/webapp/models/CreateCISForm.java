package org.societies.webapp.models;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.societies.api.cis.attributes.Rule;
import org.springframework.util.AutoPopulatingList;

@Deprecated // No longer used after move from JSP to JSF
public class CreateCISForm {

	@NotNull
	private String cisName;
	private String cisDescription;
	private String cisType;
	
	private String dummyAtr;
	private String dummyOperation;
	private String dummyValue;
	private String dummyValue2;
	
	private AutoPopulatingList<MembershipCriteriaForm> ruleArray = new AutoPopulatingList<MembershipCriteriaForm>(MembershipCriteriaForm.class);
	
	
	
	public AutoPopulatingList<MembershipCriteriaForm> getRuleArray() {
		return ruleArray;
	}

	public void setRuleArray(AutoPopulatingList<MembershipCriteriaForm> ruleArray) {
		this.ruleArray = ruleArray;
	}

	public CreateCISForm() {
		// TODO Auto-generated constructor stub

	}

	public String getCisName() {
		return cisName;
	}

	public void setCisName(String cisName) {
		this.cisName = cisName;
	}

	public String getCisDescription() {
		return cisDescription;
	}

	public void setCisDescription(String cisDescription) {
		this.cisDescription = cisDescription;
	}

	public String getCisType() {
		return cisType;
	}

	public void setCisType(String cisType) {
		this.cisType = cisType;
	}

	public String getDummyAtr() {
		return dummyAtr;
	}

	public void setDummyAtr(String dummyAtr) {
		this.dummyAtr = dummyAtr;
	}

	public String getDummyOperation() {
		return dummyOperation;
	}

	public void setDummyOperation(String dummyOperation) {
		this.dummyOperation = dummyOperation;
	}

	public String getDummyValue() {
		return dummyValue;
	}

	public void setDummyValue(String dummyValue) {
		this.dummyValue = dummyValue;
	}

	public String getDummyValue2() {
		return dummyValue2;
	}

	public void setDummyValue2(String dummyValue2) {
		this.dummyValue2 = dummyValue2;
	}
	

}
