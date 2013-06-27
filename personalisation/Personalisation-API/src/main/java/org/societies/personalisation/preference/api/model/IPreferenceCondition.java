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
package org.societies.personalisation.preference.api.model;

import java.io.Serializable;

import org.societies.api.context.model.CtxAttributeIdentifier;


/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:56
 */
public interface IPreferenceCondition extends Serializable {


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
	public CtxAttributeIdentifier getCtxIdentifier();

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
	public void setCtxIdentifier(CtxAttributeIdentifier ctxID);

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