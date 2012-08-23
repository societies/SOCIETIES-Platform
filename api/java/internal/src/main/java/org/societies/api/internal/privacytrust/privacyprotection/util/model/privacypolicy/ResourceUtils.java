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
package org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.DataIdentifierFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ResourceUtils {
	public static Resource toResource(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource resourceBean)
	{
		if (null == resourceBean) {
			return null;
		}
		Resource resource = null;
		try {
			// Data id
			if (null != resourceBean.getDataIdUri() && !"".equals(resourceBean.getDataIdUri()))  {
				resource = new Resource(DataIdentifierFactory.fromUri(resourceBean.getDataIdUri()));
			}
			// Dara type
			else if (null != resourceBean.getDataType()) {
				resource = new Resource(resourceBean.getDataType());
			}
			else {
				throw new PrivacyException("The resource id or type can't be null!");
			}
		} catch (MalformedCtxIdentifierException e) {
			return null;
		} catch (PrivacyException e) {
			return null;
		}
		return resource;
	}
	public static List<Resource> toResources(List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource> resourceBeans)
	{
		if (null == resourceBeans) {
			return null;
		}
		List<Resource> resources = new ArrayList<Resource>();
		for(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource resourceBean : resourceBeans) {
			resources.add(ResourceUtils.toResource(resourceBean));
		}
		return resources;
	}
	
	public static org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource toResourceBean(Resource resource)
	{
		if (null == resource) {
			return null;
		}
		org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource resourceBean = new org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource();
		resourceBean.setDataIdUri((null != resource.getDataId() ? resource.getDataId().getUri() : null));
		resourceBean.setDataType(resource.getDataType());
		return resourceBean;
	}
	public static List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource> toResourceBeans(List<Resource> resources)
	{
		if (null == resources) {
			return null;
		}
		List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource> resourceBeans = new ArrayList<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource>();
		for(Resource resource : resources) {
			resourceBeans.add(ResourceUtils.toResourceBean(resource));
		}
		return resourceBeans;
	}
}
