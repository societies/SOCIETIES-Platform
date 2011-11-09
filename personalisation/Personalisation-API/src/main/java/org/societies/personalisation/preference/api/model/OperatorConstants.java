package org.societies.personalisation.preference.api;

/**
 * Enum class used to define the operators used in the {@link
 * IPreferenceCondition}
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public enum OperatorConstants {

	EQUALS("="), GREATER_THAN(">"), GREATER_OR_EQUAL_THAN(">="),LESS_THAN("<"),LESS_OR_EQUAL_THAN("<=");
	
	private String op;
	OperatorConstants(String op){
		this.op = op;
	}
	
	public OperatorConstants fromString(String str){
		if (str.equalsIgnoreCase("=")){
			return EQUALS;
		}
		
		if (str.equals(">")){
			return GREATER_THAN;
		}
		
		if (str.equals(">=")){
			return GREATER_OR_EQUAL_THAN;
		}
		
		if (str.equals("<")){
			return LESS_THAN;
		}
		
		if (str.equals("<="))
		{
			return LESS_OR_EQUAL_THAN;
		}
		
		return EQUALS;
	}
}