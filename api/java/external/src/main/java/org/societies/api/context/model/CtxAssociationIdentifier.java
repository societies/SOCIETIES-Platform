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

import org.societies.api.schema.identity.DataIdentifierScheme;

/**
 * This class is used to identify context associations. It provides methods that
 * return information about the identified association including:
 * <ul>
 * <li><tt>OwnerId</tt>: A unique identifier of the CSS or CIS where the 
 * identified context association is stored.</li>
 * <li><tt>ModelType</tt>: Describes the type of the identified context model
 * object, i.e. {@link CtxModelType#ASSOCIATION ASSOCIATION}.</li>
 * <li><tt>Type</tt>: A semantic tag that characterises the identified context
 * association, e.g. "isFriendWith".</li>
 * <li><tt>ObjectNumber</tt>: A unique number within the CSS/CIS where the
 * respective context information was initially sensed/collected and stored.</li>
 * </ul>
 * <p>
 * A context association identifier can be represented as a URI formatted
 * String as follows:
 * <pre>
 * &lt;OwnerId&gt;/ASSOCIATION/&lt;Type&gt;/&lt;ObjectNumber&gt;
 * </pre>
 * 
 * @see CtxIdentifier
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public class CtxAssociationIdentifier extends CtxIdentifier {

	private static final long serialVersionUID = -7991875953413583564L;
	
	/**
	 * Creates a context association identifier by specifying the CSS/CIS ID
	 * where the identified context model object is stored, as well as,
	 * the association type and the unique numeric model object identifier.
	 * 
	 * @param ownerId
	 *            the identifier of the CSS/CIS where the identified context
	 *            model object is stored
	 * @param type
	 *            the association type, e.g. "device"
	 * @param objectNumber
	 *            the unique numeric model object identifier
	 */
	public CtxAssociationIdentifier(String ownerId, String type,
			Long objectNumber) {
		
		super(ownerId, CtxModelType.ASSOCIATION, type, objectNumber);
	}
	
	public CtxAssociationIdentifier(String str) throws MalformedCtxIdentifierException {
		
		super(str);
	}

	/**
	 * Formats the string representation of a context association identifier as follows:
	 * <pre> 
	 * ownerId/ASSOCIATION/type/objectNumber
	 * </pre>
	 * 
	 * @see CtxIdentifier#defineString()
	 */
	@Override
	protected void defineString() {

		if (super.string != null) 
			return;

		final StringBuilder sb = new StringBuilder();

		sb.append(super.scheme);
		sb.append("://");
		sb.append(super.ownerId);
		sb.append("/");
		sb.append(CtxModelType.ASSOCIATION);
		sb.append("/");
		sb.append(super.type);
		sb.append("/");
		sb.append(super.objectNumber);

		super.string = sb.toString();
	}

	/**
	 * Parses the string form of a context association identifier as follows:
	 * <pre> 
	 * ownerId/ASSOCIATION/type/objectNumber
	 * </pre>
	 * 
	 * @see CtxIdentifier#parseString(java.lang.String)
	 */
	@Override
	protected void parseString(String input)
			throws MalformedCtxIdentifierException {
		
		super.string = input;

		final int length = input.length();

		final int objectNumberDelim = input.lastIndexOf(CtxIdentifier.DELIM);
		if (objectNumberDelim == -1)
			throw new MalformedCtxIdentifierException("'" + input + "'");
		final String objectNumberStr = input.substring(
				objectNumberDelim + CtxIdentifier.DELIM.length(), length);
		try { 
			super.objectNumber = new Long(objectNumberStr);
		} catch (NumberFormatException nfe) {
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Invalid context association object number", nfe);
		}

		final int typeDelim = input.lastIndexOf(CtxIdentifier.DELIM, objectNumberDelim-1);
		super.type = input.substring(
				typeDelim + CtxIdentifier.DELIM.length(), objectNumberDelim);
		if (super.type.length()==0)
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Context association type cannot be empty");

		final int modelTypeDelim = input.lastIndexOf(CtxIdentifier.DELIM, typeDelim-1);
		if (modelTypeDelim == -1)
			throw new MalformedCtxIdentifierException("'" + input + "'");
		final String modelTypeStr = input.substring(
				modelTypeDelim + CtxIdentifier.DELIM.length(), typeDelim);
		try {
			super.modelType = CtxModelType.valueOf(modelTypeStr);
		} catch (IllegalArgumentException iae) {
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Malformed context model type", iae);
		}
		if (!CtxModelType.ASSOCIATION.equals(super.modelType))
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Expected 'ASSOCIATION' but found '"
					+ super.modelType + "'");

		final int ownerIdDelim = input.lastIndexOf(CtxIdentifier.SCHEME_DELIM, typeDelim-1);
		if (ownerIdDelim == -1)
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Schema delimiter not found");
		super.ownerId = input.substring(
				ownerIdDelim + CtxIdentifier.SCHEME_DELIM.length(), modelTypeDelim);
		if (super.ownerId.length() == 0)
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Owner ID cannot be empty");
		
		final String schemeStr = input.substring(0, ownerIdDelim);
		try {
			super.scheme = DataIdentifierScheme.fromValue(schemeStr);
		} catch (IllegalArgumentException iae) {
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Malformed context identifier scheme: " + schemeStr, iae);
		}
	}
}