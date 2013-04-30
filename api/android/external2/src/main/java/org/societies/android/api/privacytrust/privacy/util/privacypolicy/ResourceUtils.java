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
package org.societies.android.api.privacytrust.privacy.util.privacypolicy;

import java.util.List;

import org.societies.android.api.identity.util.DataIdentifierFactory;
import org.societies.android.api.identity.util.DataIdentifierSchemeUtils;
import org.societies.android.api.identity.util.DataTypeFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;

import android.text.TextUtils;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ResourceUtils {

	public static Resource create(String dataIdUri) {
		Resource resource = new Resource();
		resource.setDataIdUri(dataIdUri);
		DataIdentifier dataId;
		try {
			dataId = DataIdentifierFactory.fromUri(dataIdUri);
			resource.setDataIdUri(dataId.getUri());
			resource.setDataType(dataId.getType());
			resource.setScheme(dataId.getScheme());
		} catch (MalformedCtxIdentifierException e) {
			resource.setDataIdUri(dataIdUri);
		}
		return resource;
	}

	public static Resource create(DataIdentifierScheme dataScheme, String dataType) {
		Resource resource = new Resource();
		resource.setScheme(dataScheme);
		resource.setDataType(dataType);
		return resource;
	}


	public static String getDataIdUri(Resource resource) {
		return ((null == resource.getDataIdUri() || "".equals(resource.getDataIdUri())) ? resource.getScheme()+":///"+resource.getDataType() : resource.getDataIdUri());
	}

	public static String getDataType(Resource resource) {
		// No URI: scheme+type available
		if (null == resource.getDataIdUri() || "".equals(resource.getDataIdUri())) {
			return resource.getDataType();
		}
		// URI available
		return DataTypeFactory.fromUri(resource.getDataIdUri()).getType();
	}

	public static DataIdentifier getDataIdentifier(Resource resource) throws MalformedCtxIdentifierException {
		// No URI: scheme+type available
		if (null == resource.getDataIdUri() || "".equals(resource.getDataIdUri())) {
			return DataIdentifierFactory.fromType(resource.getScheme(), resource.getDataType());
		}
		// URI available
		return DataIdentifierFactory.fromUri(resource.getDataIdUri());
	}


	public static String toXmlString(Resource resource){
		StringBuilder sb = new StringBuilder();
		if (null != resource) {
			sb.append("\n<Resource>\n");
			// URI
			if (null != resource.getDataIdUri()){
				sb.append("\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:resource-id\" DataType=\"org.societies.api.context.model.CtxIdentifier\">\n");
				sb.append("\t\t<AttributeValue>"+resource.getDataIdUri()+"</AttributeValue>\n");
				sb.append("\t</Attribute>\n");
			}
			// Scheme + Type
			if (null != resource.getDataType()){
				sb.append("\t<Attribute AttributeId=\""+resource.getScheme()+"\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">\n");
				sb.append("\t\t<AttributeValue>"+resource.getDataType()+"</AttributeValue>\n");
				sb.append("\t</Attribute>\n");
			}
			sb.append("</Resource>\n");
		}
		return sb.toString();
	}


	public static String toString(Resource resource){
		StringBuilder builder = new StringBuilder();
		builder.append("Resource [");
		if (null != resource) {
			builder.append("getDataIdUri()=");
			if (null==resource.getDataIdUri()){
				builder.append("null");
			}else{
				builder.append(resource.getDataIdUri());
			}
			builder.append(", getDataType()=");
			builder.append(resource.getDataType());
			builder.append(", getScheme()=");
			builder.append(resource.getScheme());
		}
		builder.append("]");
		return builder.toString();
	}

	public static String toString(List<Resource> values){
		StringBuilder sb = new StringBuilder();
		if (null != values) {
			for(Resource entry : values) {
				sb.append(toString(entry));
			}
		}
		return sb.toString();
	}

	public static boolean equal(Resource o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		Resource ro2 = (Resource) o2;
		return (TextUtils.equals(o1.getDataIdUri(), ro2.getDataIdUri())
				&& TextUtils.equals(o1.getDataType(), ro2.getDataType())
				&& DataIdentifierSchemeUtils.equal(o1.getScheme(), ro2.getScheme())
				);
	}
	@Deprecated
	public static boolean equals(Resource o1, Object o2) {
		return equal(o1, o2);
	}

	public static boolean equal(List<Resource> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { return false; }
		// -- Verify obj type
		List<Resource> ro2 = (List<Resource>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(Resource o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}

	public static boolean contain(Resource needle, List<Resource> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(Resource entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}
}
