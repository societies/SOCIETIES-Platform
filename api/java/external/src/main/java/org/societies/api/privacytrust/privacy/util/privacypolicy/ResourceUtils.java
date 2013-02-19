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
package org.societies.api.privacytrust.privacy.util.privacypolicy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.DataIdentifierFactory;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ResourceUtils {
	public static Resource toResource(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resourceBean)
	{
		if (null == resourceBean) {
			return null;
		}
		Resource resource = null;
		try {
			// Data id
			if (null != resourceBean.getDataIdUri() && !"".equals(resourceBean.getDataIdUri()) && null!=resourceBean.getScheme())  {
				resource = new Resource(DataIdentifierFactory.fromUri(resourceBean.getDataIdUri()));
			}
			// Data type
			else if (null != resourceBean.getDataType() && null!=resourceBean.getScheme()) {
				resource = new Resource(resourceBean.getScheme(), resourceBean.getDataType());
			}
			else {
				throw new PrivacyException("The resource id or type and DataIdentifierScheme can't be null!");
			}
		} catch (MalformedCtxIdentifierException e) {
			return null;
		} catch (PrivacyException e) {
			e.printStackTrace();
			return null;
		}
		return resource;
	}
	public static List<Resource> toResources(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource> resourceBeans)
	{
		if (null == resourceBeans) {
			return null;
		}
		List<Resource> resources = new ArrayList<Resource>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resourceBean : resourceBeans) {
			resources.add(ResourceUtils.toResource(resourceBean));
		}
		return resources;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource toResourceBean(Resource resource)
	{
		try
		{
			if (null == resource) {
				return null;
			}
			org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resourceBean = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource();
			resourceBean.setDataIdUri((null != resource.getDataId() ? resource.getDataId().getUri() : null));
			resourceBean.setDataType(resource.getDataType());
			if (resource.getScheme()==null){
				throw new PrivacyException("The DataIdentifierScheme cannot be null!");
			}
			resourceBean.setScheme(resource.getScheme());
			return resourceBean;
		}catch(PrivacyException e){
			e.printStackTrace();
			return null;
		}
	}
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource> toResourceBeans(List<Resource> resources)
	{
		if (null == resources) {
			return null;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource> resourceBeans = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource>();
		for(Resource resource : resources) {
			resourceBeans.add(ResourceUtils.toResourceBean(resource));
		}
		return resourceBeans;
	}

	public static String getDataIdUri(
			org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource) {
		return ((null == resource.getDataIdUri() || "".equals(resource.getDataIdUri())) ? resource.getScheme()+":///"+resource.getDataType() : resource.getDataIdUri());
	}


	public static String toXmlString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource){
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
				sb.append("\t<Attribute AttributeId=\""+resource.getScheme().name()+"\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">\n");
				sb.append("\t\t<AttributeValue>"+resource.getDataType()+"</AttributeValue>\n");
				sb.append("\t</Attribute>\n");
			}
			sb.append("</Resource>\n");
		}
		return sb.toString();
	}

	public static boolean equals(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource rhs = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource) o2;
		return new EqualsBuilder()
		.append(o1.getDataIdUri(), rhs.getDataIdUri())
		.append(o1.getDataType(), rhs.getDataType())
		.append(o1.getScheme(), rhs.getScheme())
		.isEquals();
	}
}
