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

import java.util.ArrayList;

import org.societies.api.context.model.CtxAttributeValueType;

public class Rule {
        
    //could be an enumeration with its own API.
    //private ArrayList<String> operations;

    private OperationType operation;

	public enum OperationType {
		   equals, greaterThan, lessThan, range, differentFrom;		
		   
		   static public boolean isValid(String aName) {
			   OperationType[] oTypes = OperationType.values();
		       for (OperationType oType : oTypes)
		           if (oType.name().equals(aName))
		               return true;
		       return false;
		   }
	}
    
    private ArrayList<Object> values;

    //Non-T4.5 components need to be able to create a rule
    public Rule() {

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

    public ArrayList<Object> getValues() {
        return values;
    }

    public boolean setValues(ArrayList<Object> values) {
        if ((!operation.equals(OperationType.range)) && (values.size() == 1)) this.values = values;
        else if ((operation.equals(OperationType.range)) && (values.size() == 2)) this.values = values;
        else return false;
        return true;
    }
    
    public boolean checkRule(CtxAttributeValueType t, Object value){
		switch (t){
		case STRING:
			String v = (String) value;
			switch (this.operation){
			case equals:
				if(v.equals((String) this.values.get(0))) return true;
				else return false;
			case differentFrom:
				if(!v.equals((String) this.values.get(0))) return true;
				else return false;
			case lessThan:
			case greaterThan:
			case range:
				return false; // invalid rule TODO: print a warning
			}
				
			break;			//end of String check

		case INTEGER:
			int i = ((Integer) value).intValue();
			switch (this.operation){
			case equals:
				if(i == ((Integer)this.values.get(0)).intValue()   ) return true;
				else return false;
			case differentFrom:
				if(i != ((Integer)this.values.get(0)).intValue()  ) return true;
				else return false;
			case lessThan:
				if(i < ((Integer)this.values.get(0)).intValue()  ) return true;
				else return false;
			case greaterThan:
				if(i > ((Integer)this.values.get(0)).intValue()  ) return true;
				else return false;
			case range:
				if((i > ((Integer)this.values.get(0)).intValue()  ) && (i < ((Integer)this.values.get(1)).intValue()  ))  return true;
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
    
}
