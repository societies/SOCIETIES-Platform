/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru≈æbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA√á√ÉO, SA (PTIN), IBM Corp., 
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
package org.societies.api.context.model;

import java.io.Serializable;
import java.util.Date;

import org.societies.api.context.model.util.SerialisationHelper;

/**
 * This class is used to represent context attributes which describe the
 * properties of a {@link CtxEntity}. Multiple <code>CtxAttribute</code>
 * objects can be assigned to an entity. For example, concepts such as the name,
 * the age, and the location of a person entity are described by different
 * attributes. Similarly, attributes describing a device's properties might be
 * the identity, the voltage, and the operational status of the device.
 * Essentially, CtxAttribute objects are used to identify an entity's status in
 * terms of its static and dynamic properties and therefore, capture all context
 * information items that characterise the situation of the owner entity. Note
 * that the containing entity is called the attribute's scope.
 * <p>
 * The value of a <code>CtxAttribute</code> can be set and retrieved using the
 * appropriate setter and getter method. The following value types are supported:
 * <dl>
 * <dt><code>String</code></dt>
 * <dd>Text value.</dd>
 * <dt><code>Integer</code></dt>
 * <dd>Integer value.</dd>
 * <dt><code>Double</code></dt>
 * <dd>Double-precision floating point numeric value.</dd>
 * <dt><code>byte[]</code></dt>
 * <dd>Binary value.</dd>
 * </dl> 
 * The following is an example of a context attribute holding a
 * <code>String</code> value:
 * <pre>
 * // Assuming we have obtained a reference to the context attribute 
 * CtxAttribute nameAttr;
 * // Initialise or update its value 
 * nameAttr.setStringValue(&quot;Sakis Rouvas&quot;);
 * // Retrieve its value 
 * String name = nameAttr.getStringValue();
 * </pre>
 * <p>
 * The <code>CtxAttribute</code> class also provides access to the history flag
 * which controls whether the represented attribute is maintained in the
 * historic context database.
 * 
 * @see CtxAttributeIdentifier
 * @see CtxEntity
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public class CtxAttribute extends CtxModelObject {

	private static final long serialVersionUID = 2885099443175534995L;
	
	/** The text value of this context attribute. */
	private String stringValue;
	
	/** The integer value of this context attribute. */
	private Integer integerValue;
	
	/** The double-precision floating point numeric value of this context attribute.*/
	private Double doubleValue;
	
	/** The binary value of this context attribute. */
	private byte[] binaryValue;
	
	/** The value type of this context attribute */
	private CtxAttributeValueType valueType = CtxAttributeValueType.EMPTY;
	
	/** The metric for the current context attribute value */
	private String valueMetric;
	
	/** The QoC meta-data. */
	private final CtxQuality quality = new CtxQuality(this);
	
	/** The identifier of the context source for the current attribute value. */
	private String sourceId;
	
	/** The history flag of this context attribute. */
	private boolean historyRecorded;
	
	/**
	 * Constructs a CtxAttribute with the specified identifier.
	 * 
	 * @param id
	 *            the identifier of the newly created context attribute
	 */
	public CtxAttribute(CtxAttributeIdentifier id) {
		
		super(id);
	}
	
	/**
	 * Returns the identifier of this context attribute.
	 * 
	 * @return the identifier of this context attribute.
	 */
	@Override
	public CtxAttributeIdentifier getId() {
		
		return (CtxAttributeIdentifier) super.getId();
	}
	
	/**
	 * Returns the identifier of the context entity containing this attribute
	 * 
	 * @return the identifier of the context entity containing this attribute
	 */
	public CtxEntityIdentifier getScope() {
		
		return this.getId().getScope();
	}

	/**
	 * Returns the value of this context attribute or <code>null</code>
	 * if the value is not a String.
	 * 
	 * @return the value of this context attribute or <code>null</code>
	 *         if the value is not a String.
	 * @see #getIntegerValue()
	 * @see #getDoubleValue()
	 * @see #getBinaryValue()
	 * @see #getComplexValue()
	 */
	public String getStringValue() {
		
		return this.stringValue;
	}
	
	/**
	 * Sets the value of this context attribute to the specified String.
	 * 
	 * @param value
	 *            the String value to set
	 * @see #setIntegerValue(Integer)
	 * @see #setDoubleValue(Double)
	 * @see #setBinaryValue(byte[])
	 * @see #setComplexValue(CtxAttributeComplexValue)
	 */
	public void setStringValue(String value) {
		
		this.stringValue = value;
		this.integerValue = null;
		this.doubleValue = null;
		this.binaryValue = null;
		// Update the last update time
		this.quality.setLastUpdated(new Date());
		this.setValueType(CtxAttributeValueType.STRING);
	}

	/**
	 * Returns the value of this context attribute or <code>null</code>
	 * if the value is not an Integer.
	 * 
	 * @return the value of this context attribute or <code>null</code>
	 *         if the value is not an Integer.
	 * @see #getStringValue()
	 * @see #getDoubleValue()
	 * @see #getBinaryValue()
	 * @see #getComplexValue()
	 */
	public Integer getIntegerValue() {
		
		return this.integerValue;
	}
	
	/**
	 * Sets the value of this context attribute to the specified Integer.
	 * 
	 * @param value
	 *            the Integer value to set
	 * @see #setStringValue(String)
	 * @see #setDoubleValue(Double)
	 * @see #setBinaryValue(byte[])
	 * @see #setComplexValue(CtxAttributeComplexValue)
	 */
	public void setIntegerValue(Integer value) {
		this.stringValue = null;
		this.integerValue = value;
		this.doubleValue = null;
		this.binaryValue = null;
		// Update the last update time
		this.quality.setLastUpdated(new Date());
		this.setValueType(CtxAttributeValueType.INTEGER);
	}
	
	/**
	 * Returns the value of this context attribute or <code>null</code>
	 * if the value is not a Double.
	 * 
	 * @return the value of this context attribute or <code>null</code>
	 *         if the value is not a Double.
	 * @see #getStringValue()
	 * @see #getIntegerValue()
	 * @see #getBinaryValue()
	 * @see #getComplexValue()
	 */
	public Double getDoubleValue() {
		
		return this.doubleValue;
	}
	
	/**
	 * Sets the value of this context attribute to the specified Double.
	 * 
	 * @param value
	 *            the Double value to set
	 * @see #setStringValue(String)
	 * @see #setIntegerValue(Integer)
	 * @see #setBinaryValue(byte[])
	 * @see #setComplexValue(CtxAttributeComplexValue)
	 */
	public void setDoubleValue(Double value) {
		
		this.stringValue = null;
		this.integerValue = null;
		this.doubleValue = value;
		this.binaryValue = null;
		// Update the last update time
		this.quality.setLastUpdated(new Date());
		this.setValueType(CtxAttributeValueType.DOUBLE);
	}
	
	/**
	 * Returns the value of this context attribute or <code>null</code>
	 * if the value is not a byte array.
	 * 
	 * @return the value of this context attribute or <code>null</code>
	 *         if the value is not a byte array.
	 * @see #getStringValue()
	 * @see #getIntegerValue()
	 * @see #getDoubleValue()
	 * @see #getComplexValue()
	 */
	public byte[] getBinaryValue() {
		
		return this.binaryValue;
	}
	
	/**
	 * Sets the value of this context attribute to the specified byte array.
	 * 
	 * @param value
	 *            the byte array value to set
	 * @see #setStringValue(String)
	 * @see #setIntegerValue(Integer)
	 * @see #setDoubleValue(Double)
	 * @see #setComplexValue(CtxAttributeComplexValue)
	 */
	public void setBinaryValue(byte[] value) {
		
		this.stringValue = null;
		this.integerValue = null;
		this.doubleValue = null;
		this.binaryValue = value;
		// Update the last update time
		this.quality.setLastUpdated(new Date());
		this.setValueType(CtxAttributeValueType.BINARY);
	}

	/**
	 * Returns the complex value of this context attribute or <code>null</code>
	 * if the value is not of type {@link CtxAttributeComplexValue}.
	 * 
	 * @return the complex value of this context attribute or <code>null</code>
	 *         if the value is not of type {@link CtxAttributeComplexValue}.
	 * @see #getStringValue()
	 * @see #getIntegerValue()
	 * @see #getDoubleValue()
	 * @see #getBinaryValue()
	 */
	public CtxAttributeComplexValue getComplexValue() {
		
		if (this.binaryValue != null) {
			try {
				return (CtxAttributeComplexValue) SerialisationHelper.deserialise(
						this.binaryValue, this.getClass().getClassLoader());
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Sets the complex value of this context attribute to the specified
	 * {@link CtxAttributeComplexValue}.
	 * 
	 * @param value
	 *            the {@link CtxAttributeComplexValue} value to set.
	 * @see #setStringValue(String)
	 * @see #setIntegerValue(Integer)
	 * @see #setDoubleValue(Double)
	 * @see #setBinaryValue(byte[])
	 */
	public void setComplexValue(CtxAttributeComplexValue value) {

		try {
			final byte[] serialisedComplexValue = SerialisationHelper.serialise(value);
			this.setBinaryValue(serialisedComplexValue);
			this.setValueType(CtxAttributeValueType.COMPLEX);
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not serialise complex value '"
					+ value + "': " + e.getLocalizedMessage(), e);
		}
	}
	
	Serializable getValue() {
		
        if (this.stringValue != null)
            return this.stringValue;
        else if (this.integerValue != null)
            return this.integerValue;
        else if (this.doubleValue != null)
            return this.doubleValue;
        else if (this.binaryValue != null)
            return this.binaryValue;
        else
            return null;
    }

	/**
	 * 
	 * @param value
	 * @throws IllegalArgumentException if <code>value</code> is not of type
	 *         <code>String</code>, <code>Integer</code>, <code>Double</code>,
	 *         or <code>byte[]</code> 
	 */
    void setValue(Serializable value) {
    	
        if (value == null || value instanceof String) {
            this.stringValue = (String) value;
            this.integerValue = null;
            this.doubleValue = null;
            this.binaryValue = null;
        } else if (value instanceof Integer) {
        	this.stringValue = null;
            this.integerValue = (Integer) value;
            this.doubleValue = null;
            this.binaryValue = null;
        } else if (value instanceof Double) {
        	this.stringValue = null;
        	this.integerValue = null;
            this.doubleValue= (Double) value;
            this.binaryValue = null;
    	} else if (value instanceof byte[]) {
    		this.stringValue = null;
        	this.integerValue = null;
        	this.doubleValue = null;
            this.binaryValue = (byte[]) value;
    	} else {
            throw new IllegalArgumentException("invalid value type " + value.getClass().getName());
    	}
    }
	
	/**
	 * Returns the value type of this context attribute
	 * 
	 * @return the value type of this context attribute
	 */
	public CtxAttributeValueType getValueType() {
		
		return this.valueType;
	}
	
	/**
	 * Sets the value type of this context attribute
	 * 
	 * @param valueType 
	 *            the value type to set for this context attribute
	 * @see CtxAttributeValueType
	 */
	public void setValueType(CtxAttributeValueType valueType) {
		
		this.valueType = valueType;
	}
	
	/**
	 * Returns the metric for the context attribute value.
	 * 
	 * @return the metric for the context attribute value.
	 */
	public String getValueMetric() {
		
		return this.valueMetric;
	}
	
	/**
	 * Sets the metric for the context attribute value.
	 * 
	 * @param valueMetric
	 *            the metric for the context attribute value to set
	 */
	public void setValueMetric(String valueMetric) {
		
		this.valueMetric = valueMetric;
	}
	
	/**
	 * Returns the Quality of Context (QoC) information associated to this context
	 * attribute.
	 * 
	 * @return the <code>CtxQuality</code> associated to this context
	 *         attribute.
	 * @see CtxQuality
	 */
	public CtxQuality getQuality() {
		
		return this.quality;
	}
		
	/**
	 * Returns the identifier of the context source for the current attribute value.
	 * 
	 * @return the identifier of the context source for the current attribute value.
	 */
	public String getSourceId() {
		
		return this.sourceId;
	}
	
	/**
	 * Sets the identifier of the context source for the current attribute value.
	 * 
	 * @param sourceId
	 *            the identifier of the context source to set.
	 */
	public void setSourceId(String sourceId) {
		
		this.sourceId = sourceId;
	}
	
	/**
	 * Checks if this context attribute is maintained in the context history
	 * repository
	 * 
	 * @return <code>true</code> if this context attribute is maintained in the
	 * context history repository; <code>false</code> otherwise
	 */
	public boolean isHistoryRecorded() {
		
		return this.historyRecorded;
	}
		
	/**
	 * Controls if this context attribute is to be maintained in the context
	 * history repository
	 * 
	 * @param historyRecorded
	 *            set to <code>true</code> if this context attribute is to be
	 *            maintained in the context history repository;
	 *            <code>false</code> otherwise
	 */
	public void setHistoryRecorded(boolean historyRecorded) {
		
		this.historyRecorded = historyRecorded;
	}
	
	/**
	 * Returns a String representation of this context attribute.
     * 
     * @return a String representation of this context attribute.
     * @since 2.0
     */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" {id=");
		sb.append(this.getId());
		sb.append(", lastModified=");
		sb.append(this.getLastModified());
		sb.append(", value=");
		sb.append(this.getValue());
		sb.append("}");
		
		return sb.toString();
	}
}