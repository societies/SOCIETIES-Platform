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
package org.societies.api.internal.privacytrust.privacy.util.dataobfuscation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.IObfuscable;
import org.societies.api.schema.identity.DataIdentifierScheme;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class DataWrapperUtils {

	public static org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper create(String dataType, IObfuscable data) {
		org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper dataWrapper = new org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper();
		dataWrapper.setDataType(dataType);
		dataWrapper.setData(data);
		return dataWrapper;
	}


	public static DataWrapper toDataWrapper(org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper dataWrapperBean)
	{
		if (null == dataWrapperBean) {
			return null;
		}
		return new DataWrapper(DataIdentifierFactory.fromType(DataIdentifierScheme.CONTEXT, dataWrapperBean.getDataType()), dataWrapperBean.getData());
	}
	public static List<DataWrapper> toDataWrappers(List<org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper> dataWrapperBeans)
	{
		if (null == dataWrapperBeans) {
			return null;
		}
		List<DataWrapper> dataWrappers = new ArrayList<DataWrapper>();
		for(org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper dataWrapperBean : dataWrapperBeans) {
			dataWrappers.add(DataWrapperUtils.toDataWrapper(dataWrapperBean));
		}
		return dataWrappers;
	}

	public static org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper toDataWrapperBean(IDataWrapper dataWrapper)
	{
		if (null == dataWrapper) {
			return null;
		}
		org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper dataWrapperBean = new org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper();
		dataWrapperBean.setDataType(dataWrapper.getDataId().getType());
		dataWrapperBean.setData(dataWrapper.getData());
		return dataWrapperBean;
	}
	public static List<org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper> toDataWrapperBeans(List<DataWrapper> dataWrappers)
	{
		if (null == dataWrappers) {
			return null;
		}
		List<org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper> dataWrapperBeans = new ArrayList<org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper>();
		for(DataWrapper dataWrapper : dataWrappers) {
			dataWrapperBeans.add(DataWrapperUtils.toDataWrapperBean(dataWrapper));
		}
		return dataWrapperBeans;
	}

	public static boolean equals(org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper o1, Object o2) {
		// -- Verify reference equality
		if (o2 == null) { return false; }
		if (o1 == o2) { return true; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper rhs = (org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper) o2;
		return new EqualsBuilder()
		.append(o1.getDataType(), rhs.getDataType())
		.append(o1.getData(), rhs.getData())
		.isEquals();
	}
}
