package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.io.Serializable;

import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyConditionConstants;

/**
 * This class represents a context condition. It is a name-value pair in which the name represents a type in 
 * the personal data of the user such as location or activity and the value represents one of the values that 
 * this type can take (e.g. name = location, value = home). 
 * @author Elizabeth
 *
 */
public class ContextPreferenceCondition implements IPrivacyPreferenceCondition, Serializable{

	private ICtxAttributeIdentifier ctxID;
	private String value;
	private PrivacyConditionConstants myConditionType;

	private OperatorConstants operator;
	public ContextPreferenceCondition(ICtxAttributeIdentifier ctxIdentifier, OperatorConstants op, String val){
		this.ctxID = ctxIdentifier;
		this.operator = op;
		this.value = val;
		this.myConditionType = PrivacyConditionConstants.CONTEXT;
	}
	
	public ICtxAttributeIdentifier getCtxIdentifier(){
		return this.ctxID;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the operator
	 */
	public OperatorConstants getOperator() {
		return operator;
	}
	
	public String toString(){
		return this.ctxID.getType()+" "+this.operator+" "+this.value;
	}
	
	public boolean equals(IPrivacyPreferenceCondition ippc){
		if (!(ippc instanceof ContextPreferenceCondition)){
			return false;
		}
		ContextPreferenceCondition cpc = (ContextPreferenceCondition) ippc;
		if (!(this.ctxID.equals(cpc.getCtxIdentifier()))){
			return false;
		}
		if (!(this.operator.equals(cpc.getOperator()))){
			return false;
		}
		if (!(this.value.toLowerCase().equals(cpc.getValue().toLowerCase()))){
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyPreferenceCondition#getType()
	 */
	@Override
	public PrivacyConditionConstants getType() {
		return PrivacyConditionConstants.CONTEXT;
	}
	
}
