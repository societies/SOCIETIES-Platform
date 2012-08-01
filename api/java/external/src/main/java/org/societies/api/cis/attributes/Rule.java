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
package org.societies.api.cis.attributes;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.societies.api.context.model.CtxAttributeValueType;

public class Rule{
        
    //could be an enumeration with its own API.
    //private ArrayList<String> operations;

    private OperationType operation;

	public enum OperationType {
		   equals, greaterThan, lessThan, range, differentFrom;		
		   
		   static public boolean isValid(String aName) {
			   OperationType[] oTypes = OperationType.values();
		       for (OperationType oType : oTypes)
		           if (oType.name().equalsIgnoreCase(aName))
		               return true;
		       return false;
		   }
	}
    
    private List<String> values;

    //Non-T4.5 components need to be able to create a rule
    public Rule() {

    }
    
    public Rule(String operation, List<String> values) throws InvalidParameterException {
    	if(this.setOperation(operation) == false) throw new InvalidParameterException("Operation invalid");
    	List<String> newStrs = new ArrayList<String>(values);
    	if(this.setValues(newStrs) == false) throw new InvalidParameterException("Value list invalid");

    }

	public String getOperation() {
        return operation.name();
    }
	public boolean setOperation(String operation) {
        if (OperationType.isValid(operation)) {
            this.operation = OperationType.valueOf(operation);
            return true;
        }
        else return false;
    }

    public List<String> getValues() {
        return values;
    }

    public boolean setValues(List<String> values) {
        if ((!operation.equals(OperationType.range)) && (values.size() == 1)) this.values = values;
        else if ((operation.equals(OperationType.range)) && (values.size() == 2)) this.values = values;
        else return false;
        return true;
    }
    
    public boolean checkRule(CtxAttributeValueType t, String value){
		switch (t){
		case STRING:
			String v = value;
			switch (this.operation){
			case equals:
				if(v.equalsIgnoreCase(this.values.get(0))) return true;
				else return false;
			case differentFrom:
				if(!v.equalsIgnoreCase(this.values.get(0))) return true;
				else return false;
			case lessThan:
			case greaterThan:
			case range:
				return false; // invalid rule TODO: print a warning
			}
				
			break;			//end of String check

		case INTEGER:
			int i = Integer.valueOf(value);
			switch (this.operation){
			case equals:
				if(i == (Integer.valueOf(this.values.get(0))   )) return true;
				else return false;
			case differentFrom:
				if(i != (Integer.valueOf(this.values.get(0))   ))  return true;
				else return false;
			case lessThan:
				if(i < (Integer.valueOf(this.values.get(0))   ))  return true;
				else return false;
			case greaterThan:
				if(i > (Integer.valueOf(this.values.get(0))   ))  return true;
				else return false;
			case range:
				if((i > (Integer.valueOf(this.values.get(0))   )) && (i < (Integer.valueOf(this.values.get(1))   )))  return true;
				else return false;
			}
			
			break;			//end of Integer check
		case DOUBLE:
		case BINARY:
		case EMPTY:
		default:
		}
    	return false;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rule other = (Rule) obj;
		if (operation != other.operation)
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
    
    
    
}
