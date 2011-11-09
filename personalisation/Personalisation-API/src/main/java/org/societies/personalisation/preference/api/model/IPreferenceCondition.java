package org.societies.personalisation.preference.api;

import java.io.Serializable;

import org.societies.personalisation.common.api.ICtxAttributeIdentifier;

/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:56
 */
public interface IPreferenceCondition extends Serializable {

	/**
	 * Method to compare each field of the conditions
	 * @return		true if they match, false otherwise
	 * 
	 * @param pc    the preference condition to compare this condition to
	 */
	public boolean equals(IPreferenceCondition pc);

	/**
	 * Method to compare each field of the conditions excluding the value field
	 * @return		true if they match, false otherwise
	 * 
	 * @param pc    the preference condition to compare this condition to
	 */
	public boolean equalsIgnoreValue(IPreferenceCondition pc);

	/**
	 * Method to get the context identifier of the attribute included in this
	 * condition
	 * @return	the ICtxIdentifier of the context attribute
	 */
	public ICtxAttributeIdentifier getCtxIdentifier();

	/**
	 * Method to get the context type of the attribute included in this condition i.e.
	 * "symbolic_location" or "activity"
	 * @return		the contextType of the context attribute as String
	 */
	public String getname();

	/**
	 * Method to get the operator {@link #getoperator()} used to compare the value of
	 * this condition with the current value of this context attribute
	 * @return	the operator to compare the value of this condition with the current
	 * value of this context attribute
	 */
	public OperatorConstants getoperator();

	/**
	 * Method to return the type of condition i.e. "TRUST" or "CONTEXT"
	 * @return	the type of condition as String
	 */
	public String getType();

	/**
	 * Method to get the value of the context attribute of this condition
	 * @return	the value of the context attribute as String
	 */
	public String getvalue();

	/**
	 * Method to set the context identifier of the attribute included in this
	 * condition
	 * 
	 * @param ctxID    the ICtxIdentifier of the context attribute
	 */
	public void setCtxIdentifier(ICtxAttributeIdentifier ctxID);

	/**
	 * Method to set the context type of the attribute included in this condition
	 * 
	 * @param name    the context type of the context attribute as String
	 */
	public void setname(String name);

	/**
	 * Method to set the operator used to compare the value of this condition with the
	 * current value of this context attribute
	 * 
	 * @param operator    the operator to compare the value of this condition with the
	 * current value of this context attribute
	 */
	public void setoperator(OperatorConstants operator);

	/**
	 * Method to set the value of the context attribute of this condition i.e. if the
	 * contextType is "symbolic_location", then the value can be "home" or "work"
	 * 
	 * @param newVal    the value of the context attribute of this condition
	 */
	public void setvalue(String newVal);

	/**
	 * Returns a user-friendly representation of this condition
	 * @return this condition as String
	 */
	public String toString();

}