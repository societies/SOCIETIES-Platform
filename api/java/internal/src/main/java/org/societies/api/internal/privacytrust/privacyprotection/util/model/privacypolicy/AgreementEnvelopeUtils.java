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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;


/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class AgreementEnvelopeUtils {
	public static AgreementEnvelope toAgreementEnvelope(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope agreementEnvelopeBean, IIdentityManager identityManager) throws InvalidFormatException
	{
		if (null == agreementEnvelopeBean) {
			return null;
		}
		return new AgreementEnvelope(AgreementUtils.toAgreement(agreementEnvelopeBean.getAgreement(), identityManager), agreementEnvelopeBean.getPublicKey(), agreementEnvelopeBean.getSignature());
	}
	public static List<AgreementEnvelope> toAgreementEnvelopes(List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope> agreementEnvelopeBeans, IIdentityManager identityManager) throws InvalidFormatException
	{
		if (null == agreementEnvelopeBeans) {
			return null;
		}
		List<AgreementEnvelope> agreementEnvelopes = new ArrayList<AgreementEnvelope>();
		for(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope agreementEnvelopeBean : agreementEnvelopeBeans) {
			agreementEnvelopes.add(AgreementEnvelopeUtils.toAgreementEnvelope(agreementEnvelopeBean, identityManager));
		}
		return agreementEnvelopes;
	}
	
	public static org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope toAgreementEnvelopeBean(AgreementEnvelope agreementEnvelope)
	{
		if (null == agreementEnvelope) {
			return null;
		}
		org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope agreementEnvelopeBean = new org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope();
		agreementEnvelopeBean.setAgreement(AgreementUtils.toAgreementBean(agreementEnvelope.getAgreement()));
//		if (null != agreementEnvelope.getChecksum()) {
//			agreementEnvelopeBean.setAgreementCheckSum(agreementEnvelope.getChecksum().getValue());
//		}
		agreementEnvelopeBean.setPublicKey(agreementEnvelope.getPublicKeyInBytes());
		agreementEnvelopeBean.setSignature(agreementEnvelope.getSignature());
		return agreementEnvelopeBean;
	}
	public static List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope> toAgreementEnvelopeBeans(List<AgreementEnvelope> agreementEnvelopes)
	{
		if (null == agreementEnvelopes) {
			return null;
		}
		List<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope> agreementEnvelopeBeans = new ArrayList<org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope>();
		for(AgreementEnvelope agreementEnvelope : agreementEnvelopes) {
			agreementEnvelopeBeans.add(AgreementEnvelopeUtils.toAgreementEnvelopeBean(agreementEnvelope));
		}
		return agreementEnvelopeBeans;
	}
	
	public static Checksum calculateChecksum(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope agreement){
		Checksum agreementCheckSum = new CRC32();
		byte[] byteArray;
		try {
			byteArray = getBytes(agreement);
			agreementCheckSum.update(byteArray, 0, byteArray.length);
			return agreementCheckSum;
			//long checksum = checksumEngine.getValue();
			//System.out.println("Checksum: "+checksum);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] getBytes(Object obj) throws java.io.IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos = new ObjectOutputStream(bos); 
		oos.writeObject(obj);
		oos.flush(); 
		oos.close(); 
		bos.close();
		byte [] data = bos.toByteArray();
		return data;
	}


	private static Object getObject(byte[] bytes){
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		try {
			ObjectInputStream ois = new ObjectInputStream(bis);
			return ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static Key getPublicKey(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope agreement) {
		return (Key) getObject(agreement.getPublicKey());
	}
	
	
	public static boolean equals(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope agreement, Object obj) {
		// -- Verify reference equality
		if (obj == agreement) { return true; }
		if (obj == null) { return false; }
		
		if (obj.getClass() != agreement.getClass()) {
			return false;
		}
		
		
		// -- Verify obj type
		org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope rhs = (org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope) obj;
		return new EqualsBuilder()
			.append(agreement.getAgreementCheckSum(), rhs.getAgreementCheckSum())
			.append(agreement.getSignature(), rhs.getSignature())
			.append(agreement.getPublicKey(), agreement.getPublicKey())
			.append(agreement.getAgreement(), agreement.getAgreement())
			.isEquals();
	}
	
}
