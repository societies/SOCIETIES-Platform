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
package org.societies.api.schema.context.model;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

//import org.societies.api.identity.IIdentity;

/**
 * This abstract class is used to identify context model objects. It provides
 * methods that return information about the identified model object including:
 * <ul>
 * <li><tt>OperatorId</tt>: A unique identifier of the CSS or CIS where the 
 * identified context model object is stored.</li>
 * <li><tt>ModelType</tt>: Describes the type of the identified context model
 * object, i.e. is one of the following: {@link CtxModelType#ENTITY ENTITY},
 * {@link CtxModelType#ATTRIBUTE ATTRIBUTE}, or {@link CtxModelType#ASSOCIATION
 * ASSOCIATION}.</li>
 * <li><tt>Type</tt>: A semantic tag that characterises the identified context
 * model object. e.g. "person".</li>
 * <li><tt>ObjectNumber</tt>: A unique number within the CSS/CIS where the
 * respective context information was initially sensed/collected and stored.</li>
 * </ul>
 * 
 * @see CtxModelType
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@XmlType(namespace="http://societies.org/api/schema/context/model")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CtxIdentifier implements Serializable {

	private static final long serialVersionUID = 3552976823045895472L;
	
	/** The unique identifier of the CSS or CIS where the identified context model object is stored.*/
	private transient String operatorId;
	
	/** The type of the identified context model object. */
	private transient CtxModelType modelType;
	
	/** The semantic tag that characterises the identified context model object. */
	private transient String type;
	
	/** The unique number within the CSS/CIS where the identified context model object was initially sensed/collected and stored. */
	private transient Long objectNumber;
	
	/** The string form of this context identifier. */
	@XmlElement(required = true, nillable=false)
	private volatile String string;    // The only serialisable field

	CtxIdentifier() {}
	
	/**
	 * Creates a context model object identifier by specifying the CSS/CIS ID
	 * where the identified object is stored, as well as, the {@link CtxModelType}
	 * and the unique numeric model object identifier.
	 * 
	 * @param operatorId
	 *            the identifier of the CSS/CIS where the identified context
	 *            model object is stored
	 * @param type
	 *            the semantic tag that characterises the identified context
     *            model object. e.g. "person"
	 * @param objectNumber
	 *            the unique numeric model object identifier
	 */
	CtxIdentifier(String operatorId, CtxModelType modelType, String type, Long objectNumber) {
		
		this.operatorId = operatorId;
		this.modelType = modelType;
		this.type = type;
		this.objectNumber = objectNumber;
	}

	CtxIdentifier(String str) throws MalformedCtxIdentifierException {
		
		this.parse(str);
	}
	
	/**
	 * Returns a unique identifier of the CSS or CIS where the identified
	 * context model object is stored
     * 
	 * @return a unique identifier of the CSS or CIS where the identified 
	 * context model object is stored
	 */
	public String getOperatorId() {
		
		return this.operatorId;
	}
	
	/**
	 * Returns the type of the identified context model object
	 * 
	 * @return the type of the identified context model object
	 * @see CtxModelType
	 */
	public CtxModelType getModelType() {
		
		return this.modelType;
	}

	/**
	 * Returns the semantic tag (e.g. "person") that characterises the
	 * identified context model object
     * 
     * @return the semantic tag of the identified context model object 
	 */
	public String getType() {
		
		return this.type;
	}
	
	/**
	 * Returns the numeric part of this context model object identifier
     * 
     * @return the numeric part of this context model object identifier
	 */
	public Long getObjectNumber() {
		
		return this.objectNumber;
	}
	
	/**
	 * Returns a URI formatted String representation of this context model
	 * object identifier
	 * 
	 * @return a URI formatted String representation of this context model
	 * object identifier
	 */
	public String toUriString() {
		
		return this.toString();
	}
	
	/**
	 * Returns a String representation of this context model object identifier
	 * 
	 * @return a String representation of this context model object identifier
	 */
	@Override
	public String toString() {
		
		this.initString();
		
		return this.string;
	}
	
	/**
     * @see java.lang.Object#hashCode()
     * @since 0.0.2
     */
    @Override
    public int hashCode() {
    	
        final int prime = 31;
        int result = 1;
        
        result = prime * result
                + ((this.operatorId == null) ? 0 : this.operatorId.hashCode());
        result = prime * result
                + ((this.modelType == null) ? 0 : this.modelType.hashCode());
        result = prime * result
                + ((this.type == null) ? 0 : this.type.hashCode());
        result = prime * result
                + ((this.objectNumber == null) ? 0 : this.objectNumber.hashCode());
        
        return result;
    }
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @since 0.0.2
	 */
	@Override
    public boolean equals(Object that) {
		
        if (this == that)
            return true;
        if (that == null)
            return false;
        if (this.getClass() != that.getClass())
            return false;
        
        CtxIdentifier other = (CtxIdentifier) that;
        if (this.operatorId == null) {
            if (other.operatorId != null)
                return false;
        } else if (!this.operatorId.equals(other.operatorId))
            return false;
        if (this.modelType == null) {
            if (other.modelType != null)
                return false;
        } else if (!this.modelType.equals(other.modelType))
            return false;
        if (this.type == null) {
            if (other.type != null)
                return false;
        } else if (!this.type.equals(other.type))
            return false;
        if (this.objectNumber == null) {
            if (other.objectNumber != null)
                return false;
        } else if (!this.objectNumber.equals(other.objectNumber))
            return false;
        
        return true;
    }
	
	/**
     * Writes the contents of this CtxIdentifier to the given object output stream.
     * <p> 
     * The only serialisable field of a CtxIdentifier instance is its 
     * {@link #string} field. That field is given a value, if it does not have
     * one already, and then the {@link java.io.ObjectOutputStream#defaultWriteObject()}
     * method of the given object-output stream is invoked.
     *
     * @param os
     *            The object output stream to which this object is to be written
     */
    private void writeObject(ObjectOutputStream os)	throws IOException {
    	
    	this.initString();          // Initialise the string field
    	os.defaultWriteObject();	// Write the string field only!
    }

    /**
     * Reconstructs a CtxIdentifier from the given serial stream.
     * <p> 
     * The {@link java.io.ObjectInputStream#defaultReadObject()} method is
     * invoked to read the value of the <tt>string</tt> field. The result is
     * then parsed in the usual way.
     *
     * @param is
     *            The object input stream from which this object is being read
     */
    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
	
    	is.defaultReadObject();     // Read the string field only!
    	try {
    		this.parse(this.string);
    	} catch (MalformedCtxIdentifierException mcie) {
    		IOException ioe = new InvalidObjectException("Invalid Context Identifier");
    		ioe.initCause(mcie);
    		throw ioe;
    	}
    }
	
    // <operatorId>/<modelType>/<type>/<objectNumber>
    private void initString() {
		
		if (this.string != null) 
			return;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.operatorId);
		sb.append("/");
		sb.append(this.modelType);
		sb.append("/");
		sb.append(this.type);
		sb.append("/");
		sb.append(this.objectNumber);
		
		this.string = sb.toString();
	}
    
    // <operatorId>/<modelType>/<type>/<objectNumber>
	private void parse(String input) throws MalformedCtxIdentifierException {
		
		this.string = input;

		final int length = input.length();

		final int objectNumberDelim = input.lastIndexOf("/");
		if (objectNumberDelim == -1)
			throw new MalformedCtxIdentifierException("'" + input + "'");
		final String objectNumberStr = input.substring(objectNumberDelim+1, length);
		try { 
			objectNumber = new Long(objectNumberStr);
		} catch (NumberFormatException nfe) {
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Invalid context model object number", nfe);
		}

		final int typeDelim = input.lastIndexOf("/", objectNumberDelim-1);
		type = input.substring(typeDelim+1, objectNumberDelim);
		if (type.isEmpty())
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Context type cannot be empty");

		final int modelTypeDelim = input.lastIndexOf("/", typeDelim-1);
		if (modelTypeDelim == -1)
			throw new MalformedCtxIdentifierException("'" + input + "'");
		final String modelTypeStr = input.substring(modelTypeDelim+1, typeDelim);
		try {
			modelType = CtxModelType.valueOf(modelTypeStr);
		} catch (IllegalArgumentException iae) {
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Malformed context model type", iae);
		}

		operatorId = input.substring(0, modelTypeDelim);
		if (operatorId.isEmpty())
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Operator ID cannot be empty");
	}
}