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
package org.societies.context.broker.impl.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.util.SerialisationHelper;

public class CtxBrokerUtils {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxBrokerUtils.class);
	
    /**
     * String representation of binary large object
     */
    public static final String BLOB_STRING = "_BLOB_";
    
    /**
     * Returns an attribute value as a String.
     * 
     * @param attributeValue    The attribute value.
     * 
     * @return A String representation of the attribute value.
     */
    public static String attributeValueAsString(Serializable attributeValue) {

        if (null == attributeValue) {
            return "null";
        }

        if (attributeValue instanceof String || attributeValue instanceof Integer || attributeValue instanceof Double) {
            return attributeValue.toString();
        }

        return BLOB_STRING;
    }

    public static CtxAttributeValueType findAttributeValueType(Serializable value) {
		if (value == null)
			return CtxAttributeValueType.EMPTY;
		else if (value instanceof String)
			return CtxAttributeValueType.STRING;
		else if (value instanceof Integer)
			return CtxAttributeValueType.INTEGER;
		else if (value instanceof Double)
			return CtxAttributeValueType.DOUBLE;
		else if (value instanceof byte[])
			return CtxAttributeValueType.BINARY;
		else
			throw new IllegalArgumentException(value + ": Invalid value type");
	}

    public static Boolean compareAttributeValues(CtxAttribute attribute, Serializable value){

		Boolean areEqual = false;
		if (value instanceof String ) {
			if (attribute.getStringValue()!=null) {
				String valueStr = attribute.getStringValue();
				if(valueStr.equalsIgnoreCase(value.toString())) return true;             			
			}
		} else if (value instanceof Integer) {
			if(attribute.getIntegerValue()!=null) {
				Integer valueInt = attribute.getIntegerValue();
				if(valueInt.equals((Integer)value)) return true;  
			}
		} else if (value instanceof Double) {
			if(attribute.getDoubleValue()!=null) {
				Double valueDouble = attribute.getDoubleValue();
				if(valueDouble.equals((Double) value)) return true;             			
			}
		} else {
			byte[] valueBytes;
			byte[] attributeValueBytes;
			try {
				valueBytes = attribute.getBinaryValue();
				attributeValueBytes = SerialisationHelper.serialise(value);
				if (Arrays.equals(valueBytes, attributeValueBytes)) {
					areEqual = true;
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}                		
		}
		return areEqual;
	}
        
    public static Boolean hasValue(CtxAttribute ctxAttribute){
    	Boolean hasValue = false; 
    	
    	if(ctxAttribute.getStringValue() != null) return true;
    	if(ctxAttribute.getIntegerValue() != null) return true;
    	if(ctxAttribute.getDoubleValue() != null) return true;
    	if(ctxAttribute.getBinaryValue() != null) return true;	
    	
    	return hasValue;
    }
}