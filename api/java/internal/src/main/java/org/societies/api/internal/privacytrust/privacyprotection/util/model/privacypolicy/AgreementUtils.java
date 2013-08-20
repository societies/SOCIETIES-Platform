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

import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreement;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.NegotiationAgreement;


/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class AgreementUtils {
	public static NegotiationAgreement toAgreement(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement agreementBean, IIdentityManager identityManager) throws InvalidFormatException
	{
		if (null == agreementBean) {
			return null;
		}
		NegotiationAgreement agreement = new NegotiationAgreement(agreementBean.getRequestedItems());
		agreement.setRequestor(agreementBean.getRequestor());
		if (null != agreementBean.getUserIdentity()) {
			agreement.setUserIdentity(identityManager.fromJid(agreementBean.getUserIdentity()));
		}
		if (null != agreementBean.getUserPublicIdentity()) {
			agreement.setUserPublicIdentity(identityManager.fromJid(agreementBean.getUserPublicIdentity()));
		}
		return agreement;
	}
	public static List<NegotiationAgreement> toAgreements(List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement> agreementBeans, IIdentityManager identityManager) throws InvalidFormatException
	{
		if (null == agreementBeans) {
			return null;
		}
		List<NegotiationAgreement> agreements = new ArrayList<NegotiationAgreement>();
		for(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement agreementBean : agreementBeans) {
			agreements.add(AgreementUtils.toAgreement(agreementBean, identityManager));
		}
		return agreements;
	}

	public static org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement toAgreementBean(IAgreement iAgreement)
	{
		if (null == iAgreement) {
			return null;
		}
		org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement agreementBean = new org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement();
		agreementBean.setRequestor(iAgreement.getRequestor());
		if (null != iAgreement.getUserIdentity()) {
			agreementBean.setUserIdentity(iAgreement.getUserIdentity().getJid());
		}
		if (null != iAgreement.getUserPublicIdentity()) {
			agreementBean.setUserPublicIdentity(iAgreement.getUserPublicIdentity().getJid());
		}
		agreementBean.setRequestedItems(iAgreement.getRequestedItems());
		return agreementBean;
	}
	public static List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement> toAgreementBeans(List<NegotiationAgreement> agreements)
	{
		if (null == agreements) {
			return null;
		}
		List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement> agreementBeans = new ArrayList<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement>();
		for(NegotiationAgreement agreement : agreements) {
			agreementBeans.add(AgreementUtils.toAgreementBean(agreement));
		}
		return agreementBeans;
	}
}
