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
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.PrivacyPermission;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyPermissionUtils {
	public static PrivacyPermission toPrivacyPermission(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission privacyPermissionBean)
	{
		if (null == privacyPermissionBean) {
			return null;
		}
		RequestItem requestItem = RequestItemUtils.toRequestItem(privacyPermissionBean.getRequestItem());
		Decision decision = DecisionUtils.toDecision(privacyPermissionBean.getDecision());
		return new PrivacyPermission(requestItem, decision, privacyPermissionBean.getObfuscationLevel(), privacyPermissionBean.getCreationDate(), privacyPermissionBean.getValidityDuration());
	}
	public static List<PrivacyPermission> toPrivacyPermissions(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission> privacyPermissionBeans)
	{
		if (null == privacyPermissionBeans) {
			return null;
		}
		List<PrivacyPermission> privacyPermissions = new ArrayList<PrivacyPermission>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission privacyPermissionBean : privacyPermissionBeans) {
			privacyPermissions.add(PrivacyPermissionUtils.toPrivacyPermission(privacyPermissionBean));
		}
		return privacyPermissions;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission toPrivacyPermissionBean(PrivacyPermission privacyPermission) throws DatatypeConfigurationException
	{
		if (null == privacyPermission) {
			return null;
		}
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission privacyPermissionBean = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission();
		privacyPermissionBean.setDecision(DecisionUtils.toDecisionBean(privacyPermission.getDecision()));
		privacyPermissionBean.setRequestItem(RequestItemUtils.toRequestItemBean(privacyPermission.getRequestItem()));
		privacyPermissionBean.setObfuscationLevel(privacyPermission.getObfuscationLevel());
		//GregorianCalendar gCalendar = new GregorianCalendar();
		//gCalendar.setTime(privacyPermission.getCreationDate());
		//privacyPermissionBean.setCreationDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar));
		Date now = new Date();
		privacyPermissionBean.setCreationDate(now);
		privacyPermissionBean.setValidityDuration(privacyPermission.getValidityDuration());
		return privacyPermissionBean;
	}
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission> toPrivacyPermissionBeans(List<PrivacyPermission> privacyPermissions) throws DatatypeConfigurationException
	{
		if (null == privacyPermissions) {
			return null;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission> privacyPermissionBeans = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission>();
		for(PrivacyPermission privacyPermission : privacyPermissions) {
			privacyPermissionBeans.add(PrivacyPermissionUtils.toPrivacyPermissionBean(privacyPermission));
		}
		return privacyPermissionBeans;
	}

	/**
	 * To know if this privacy permission is still valid or not
	 * @return True if the privacy permission is still valid
	 */
	public boolean isStillValid(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission privacyPermissionBean) {
		Date now = new Date();
		//return (now.getTime() > (privacyPermissionBean.getCreationDate().toGregorianCalendar().getTimeInMillis()+privacyPermissionBean.getValidityDuration()));
		return (now.getTime() > (privacyPermissionBean.getCreationDate().getTime() + privacyPermissionBean.getValidityDuration()));
	}
	
	@Deprecated
	public static boolean equals(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission o1, Object o2) {
		return equal(o1, o2);
	}
	public static boolean equal(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission rhs = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission) o2;
		return new EqualsBuilder()
		.append(o1.getObfuscationLevel(), rhs.getObfuscationLevel())
		.append(o1.getValidityDuration(), rhs.getValidityDuration())
		.append(o1.getCreationDate(), rhs.getCreationDate())
		.append(o1.getDecision(), rhs.getDecision())
		.append(o1.getRequestItem(), rhs.getRequestItem())
		.append(o1.getRequestor(), rhs.getRequestor())
		.isEquals();
	}
}
