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

import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.schema.identity.DataIdentifierScheme;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is used to identify context entities. It provides methods that
 * return information about the identified entity including:
 * <ul>
 * <li><tt>OwnerId</tt>: A unique identifier of the CSS or CIS where the 
 * identified context entity is stored.</li>
 * <li><tt>ModelType</tt>: Describes the type of the identified context model
 * object, i.e. {@link CtxModelType#ENTITY ENTITY}.</li>
 * <li><tt>Type</tt>: A semantic tag that characterises the identified context
 * entity. e.g. "person".</li>
 * <li><tt>ObjectNumber</tt>: A unique number within the CSS/CIS where the
 * respective context information was initially sensed/collected and stored.</li>
 * </ul>
 * <p>
 * A context entity identifier can be represented as a URI formatted String as
 * follows:
 * <pre>
 * &lt;OwnerId&gt;/ENTITY/&lt;Type&gt;/&lt;ObjectNumber&gt;
 * </pre>
 * 
 * @see ACtxIdentifier
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public class ACtxEntityIdentifier extends ACtxIdentifier {
	
	private static final long serialVersionUID = -7948766616331030324L;

	/**
	 * Creates a context entity identifier by specifying the CSS/CIS ID
	 * where the identified context model object is stored, as well as,
	 * the entity type and the unique numeric model object identifier.
	 * 
	 * @param ownerId
	 *            the identifier of the CSS/CIS where the identified context
	 *            model object is stored
	 * @param type
	 *            the entity type, e.g. "device"
	 * @param objectNumber
	 *            the unique numeric model object identifier
	 */
	public ACtxEntityIdentifier(String ownerId, String type, 
			Long objectNumber) {
		
		super(ownerId, CtxModelType.ENTITY, type, objectNumber);
	}
	
	public ACtxEntityIdentifier(String str) throws MalformedCtxIdentifierException {
		
		super(str);
	}

	/**
	 * Making class Parcelable
	 */
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
    	super.writeToParcel(out, flags);
    }

    public static final Parcelable.Creator<ACtxEntityIdentifier> CREATOR
            = new Parcelable.Creator<ACtxEntityIdentifier>() {
        public ACtxEntityIdentifier createFromParcel(Parcel in) {
            return new ACtxEntityIdentifier(in);
        }

        public ACtxEntityIdentifier[] newArray(int size) {
            return new ACtxEntityIdentifier[size];
        }
    };
    
    private ACtxEntityIdentifier(Parcel in) {
    	super(in);

    }

    /** 
	 * Formats the string representation of a context entity identifier as follows:
	 * <pre> 
	 * ownerId/ENTITY/type/objectNumber
	 * </pre>
	 * 
	 * @see ACtxIdentifier#defineString()
	 */
	@Override
	protected void defineString() {

		if (super.string != null) 
			return;

		final StringBuilder sb = new StringBuilder();

		sb.append(super.scheme);
		sb.append(ACtxIdentifier.SCHEME_DELIM);
		sb.append(super.ownerId);
		sb.append(ACtxIdentifier.DELIM);
		sb.append(CtxModelType.ENTITY);
		sb.append(ACtxIdentifier.DELIM);
		sb.append(super.type);
		sb.append(ACtxIdentifier.DELIM);
		sb.append(super.objectNumber);

		super.string = sb.toString();
	}

	/**
	 * Parses the string form of a context entity identifier as follows:
	 * <pre> 
	 * ownerId/ENTITY/type/objectNumber
	 * </pre>
	 * 
	 * @see ACtxIdentifier#parseString(java.lang.String)
	 */
	@Override
	protected void parseString(String input)
			throws MalformedCtxIdentifierException {
	
/*		super.string = input;

		final int length = input.length();

		final int objectNumberDelim = input.lastIndexOf(ACtxIdentifier.DELIM);
		if (objectNumberDelim == -1)
			throw new MalformedCtxIdentifierException("'" + input + "'");
		final String objectNumberStr = input.substring(
				objectNumberDelim + ACtxIdentifier.DELIM.length(), length);
		try { 
			super.objectNumber = new Long(objectNumberStr);
		} catch (NumberFormatException nfe) {
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Invalid context entity object number", nfe);
		}

		final int typeDelim = input.lastIndexOf(ACtxIdentifier.DELIM, objectNumberDelim-1);
		super.type = input.substring(
				typeDelim + ACtxIdentifier.DELIM.length(), objectNumberDelim);
		if (super.type.length() == 0)
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Context entity type cannot be empty");

		final int modelTypeDelim = input.lastIndexOf(ACtxIdentifier.DELIM, typeDelim-1);
		if (modelTypeDelim == -1)
			throw new MalformedCtxIdentifierException("'" + input + "'");
		final String modelTypeStr = input.substring(
				modelTypeDelim + ACtxIdentifier.DELIM.length(), typeDelim);
		try {
			super.modelType = CtxModelType.valueOf(modelTypeStr);
		} catch (IllegalArgumentException iae) {
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Malformed context model type", iae);
		}
		if (!CtxModelType.ENTITY.equals(super.modelType))
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Expected 'ENTITY' but found '"
					+ super.modelType + "'");

		final int ownerIdDelim = input.lastIndexOf(ACtxIdentifier.SCHEME_DELIM, typeDelim-1);
		if (ownerIdDelim == -1)
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Schema delimiter not found");
		super.ownerId = input.substring(
				ownerIdDelim + ACtxIdentifier.SCHEME_DELIM.length(), modelTypeDelim);
		if (super.ownerId.length() == 0)
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Owner ID cannot be empty");
		
		final String schemeStr = input.substring(0, ownerIdDelim);
		try {
			super.scheme = DataIdentifierScheme.fromValue(schemeStr);
		} catch (IllegalArgumentException iae) {
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Malformed context identifier scheme: " + schemeStr, iae);
		} */
	}
}