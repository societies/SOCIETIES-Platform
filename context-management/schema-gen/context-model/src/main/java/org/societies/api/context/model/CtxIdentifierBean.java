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
package org.societies.api.context.model;

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
 * object, i.e. is one of the following: {@link CtxModelTypeBean#ENTITY ENTITY},
 * {@link CtxModelTypeBean#ATTRIBUTE ATTRIBUTE}, or {@link CtxModelTypeBean#ASSOCIATION
 * ASSOCIATION}.</li>
 * <li><tt>Type</tt>: A semantic tag that characterises the identified context
 * model object. e.g. "person".</li>
 * <li><tt>ObjectNumber</tt>: A unique number within the CSS/CIS where the
 * respective context information was initially sensed/collected and stored.</li>
 * </ul>
 * 
 * @see CtxModelTypeBean
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@XmlType(namespace="http://societies.org/api/schema/context/model")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CtxIdentifierBean implements Serializable {

	private static final long serialVersionUID = 3552976823045895472L;
	
	/** The unique identifier of the CSS or CIS where the identified context model object is stored.*/
	protected transient String operatorId;
	
	/** The type of the identified context model object. */
	protected transient CtxModelTypeBean modelType;
	
	/** The semantic tag that characterises the identified context model object. */
	protected transient String type;
	
	/** The unique number within the CSS/CIS where the identified context model object was initially sensed/collected and stored. */
	protected transient Long objectNumber;
	
	/** The string form of this context identifier. */
	@XmlElement(required = true, nillable=false)
	protected volatile String string;    // The only serialisable field

	CtxIdentifierBean() {}
	
	/**
	 * Constructs a context model object identifier by specifying the CSS/CIS ID
	 * where the identified object is stored, as well as, the {@link CtxModelTypeBean}
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
	CtxIdentifierBean(String operatorId, CtxModelTypeBean modelType, String type, Long objectNumber) {
		
		this.operatorId = operatorId;
		this.modelType = modelType;
		this.type = type;
		this.objectNumber = objectNumber;
	}
	
	/**
	 * Constructs a context model object identifier by parsing the given string. 
	 * 
	 * @throws MalformedCtxIdentifierException
	 *             if the given string cannot be parsed
	 */
	CtxIdentifierBean(String str) throws MalformedCtxIdentifierException {
		
		this.parseString(str);
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
	 * @see CtxModelTypeBean
	 */
	public CtxModelTypeBean getModelType() {
		
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
		
		this.defineString();
		
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
        
        CtxIdentifierBean other = (CtxIdentifierBean) that;
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
     * Formats the string representation of this context identifier.
     */
    protected abstract void defineString();
    
    /**
     * Parses the information contained in the given context identifier string.
     * 
     * @param input
     *            the context identifier string to parse
     * @throws MalformedCtxIdentifierException
     *             if the given string cannot be parsed.             
     */
	protected abstract void parseString(String input) throws MalformedCtxIdentifierException;
	
	/**
     * Writes the contents of this CtxIdentifierBean to the given object output stream.
     * <p> 
     * The only serialisable field of a CtxIdentifierBean instance is its 
     * {@link #string} field. That field is given a value, if it does not have
     * one already, and then the {@link java.io.ObjectOutputStream#defaultWriteObject()}
     * method of the given object-output stream is invoked.
     *
     * @param os
     *            the object output stream to which this object is to be written
     */
    private void writeObject(ObjectOutputStream os)	throws IOException {
    	
    	this.defineString();        // Initialise the string field
    	os.defaultWriteObject();	// Write the string field only
    }

    /**
     * Reconstructs a CtxIdentifierBean from the given serial stream.
     * <p> 
     * The {@link java.io.ObjectInputStream#defaultReadObject()} method is
     * invoked to read the value of the <tt>string</tt> field. The result is
     * then parsed in the usual way.
     *
     * @param is
     *            the object input stream from which this object is being read
     */
    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
	
    	is.defaultReadObject();     // Read the string field only
    	try {
    		this.parseString(this.string);
    	} catch (MalformedCtxIdentifierException mcie) {
    		IOException ioe = new InvalidObjectException("Invalid context identifier");
    		ioe.initCause(mcie);
    		throw ioe;
    	}
    }
}