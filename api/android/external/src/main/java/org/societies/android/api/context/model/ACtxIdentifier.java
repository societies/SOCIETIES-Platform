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
package org.societies.android.api.context.model;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This abstract class is used to identify context model objects. It provides
 * methods that return information about the identified model object including:
 * <ul>
 * <li><tt>Scheme</tt>: The URI scheme of all CtxIdentifiers, i.e. 
 * {@link DataIdentifierScheme.CONTEXT CONTEXT}.</li>
 * <li><tt>OwnerId</tt>: A unique identifier of the CSS or CIS where the 
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
public abstract class ACtxIdentifier extends DataIdentifier implements Parcelable {
	
	private static final long serialVersionUID = -9169891916571423259L;
	
	protected static final String SCHEME_DELIM = "://";
	protected static final String DELIM        = "/";
	
	/** The type of the identified context model object. */
	protected transient CtxModelType modelType;
	
	/** The unique number within the CSS/CIS where the identified context model object was initially sensed/collected and stored. */
	protected transient Long objectNumber;
	
	/** The string form of this context identifier. */
	protected volatile String string;    // The only serialisable field
	
	/**
	 * Constructs a context model object identifier by specifying the CSS/CIS ID
	 * where the identified object is stored, as well as, the {@link CtxModelType}
	 * and the unique numeric model object identifier.
	 * 
	 * @param ownerId
	 *            the identifier of the CSS/CIS where the identified context
	 *            model object is stored
	 * @param type
	 *            the semantic tag that characterises the identified context
     *            model object. e.g. "person"
	 * @param objectNumber
	 *            the unique numeric model object identifier
	 */
	ACtxIdentifier(String ownerId, CtxModelType modelType, String type, Long objectNumber) {
		
		super.scheme = DataIdentifierScheme.CONTEXT;
		super.ownerId = ownerId;
		this.modelType = modelType;
		super.type = type;
		this.objectNumber = objectNumber;
	}
	
	/**
	 * Constructs a context model object identifier by parsing the given string. 
	 * 
	 * @throws MalformedCtxIdentifierException
	 *             if the given string cannot be parsed
	 */
	ACtxIdentifier(String str) throws MalformedCtxIdentifierException {
		
		this.parseString(str);
	}
	
	/**
	 * Making class Parcelable
	 */
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
    	out.writeString(ownerId);
    	out.writeString((modelType == null) ? "" : modelType.name());
    	out.writeString(type);
    	out.writeLong(objectNumber);
    }

/*    public static final Parcelable.Creator<ACtxIdentifier> CREATOR
            = new Parcelable.Creator<ACtxIdentifier>() {
        public ACtxIdentifier createFromParcel(Parcel in) {
            return new ACtxIdentifier(in);
        }

        public ACtxIdentifier[] newArray(int size) {
            return new ACtxIdentifier[size];
        }
    };*/
    
    protected ACtxIdentifier(Parcel in) {
    	ownerId = in.readString();

    	try {
    		modelType = CtxModelType.valueOf(in.readString());
    	} catch (IllegalArgumentException x) {
    		modelType = null;
    	}
    	
    	type = in.readString();
    	objectNumber = in.readLong();
    }
	
	/**
	 * Returns a unique identifier of the CSS or CIS where the identified
	 * context model object is stored
     * 
	 * @return a unique identifier of the CSS or CIS where the identified 
	 * context model object is stored
	 * @deprecated As of release 0.0.8 use {@link #getOwnerId()}
	 */
	@Deprecated
	public String getOperatorId() {
		
		return super.getOwnerId();
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
	
	/*
	 * @see org.societies.api.schema.identity.DataIdentifier#getUri()
	 */
	@Override
	public String getUri() {
		
		return this.toUriString();
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
                + ((super.scheme == null) ? 0 : super.scheme.hashCode());
        result = prime * result
                + ((super.ownerId == null) ? 0 : super.ownerId.hashCode());
        result = prime * result
                + ((this.modelType == null) ? 0 : this.modelType.hashCode());
        result = prime * result
                + ((super.type == null) ? 0 : super.type.hashCode());
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
        
        ACtxIdentifier other = (ACtxIdentifier) that;
        if (super.scheme == null) {
            if (other.scheme != null)
                return false;
        } else if (!super.scheme.equals(other.scheme))
            return false;
        if (super.ownerId == null) {
            if (other.ownerId != null)
                return false;
        } else if (!super.ownerId.equals(other.ownerId))
            return false;
        if (this.modelType == null) {
            if (other.modelType != null)
                return false;
        } else if (!this.modelType.equals(other.modelType))
            return false;
        if (super.type == null) {
            if (other.type != null)
                return false;
        } else if (!super.type.equals(other.type))
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
    protected void defineString() {
	}
    
    /**
     * Parses the information contained in the given context identifier string.
     * 
     * @param input
     *            the context identifier string to parse
     * @throws MalformedCtxIdentifierException
     *             if the given string cannot be parsed.             
     */
	protected void parseString(String input) throws MalformedCtxIdentifierException {
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
     *            the object output stream to which this object is to be written
     */
    private void writeObject(ObjectOutputStream os)	throws IOException {
    	
    	this.defineString();        // Initialise the string field
    	os.defaultWriteObject();	// Write the string field only
    }

    /**
     * Reconstructs a CtxIdentifier from the given serial stream.
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