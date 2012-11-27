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
package org.societies.android.privacytrust.policymanagement;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.api.utilities.MissingClientPackageException;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.content.Context;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 * @date 5 déc. 2011
 */
public class PrivacyPolicyManager implements IPrivacyPolicyManager {
	private final static String TAG = PrivacyPolicyManager.class.getSimpleName();

	private PrivacyPolicyManagerRemote privacyPolicyManagerRemote;


	public PrivacyPolicyManager(Context context)  {
		privacyPolicyManagerRemote = new PrivacyPolicyManagerRemote(context);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#getPrivacyPolicy(java.lang.String, org.societies.api.schema.identity.RequestorBean)
	 */
	public void getPrivacyPolicy(String clientPackage, RequestorBean requestor) throws PrivacyException {
		// -- Verify
		if (null == clientPackage || "".equals(clientPackage)) {
			throw new PrivacyException(new MissingClientPackageException());
		}
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}

		privacyPolicyManagerRemote.getPrivacyPolicy(clientPackage, requestor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	public RequestPolicy updatePrivacyPolicy(String clientPackage, RequestPolicy privacyPolicy) throws PrivacyException {
		// -- Verify
		if (null == clientPackage || "".equals(clientPackage)) {
			throw new PrivacyException(new MissingClientPackageException());
		}
		if (null == privacyPolicy) {
			throw new PrivacyException("The privacy policy to update is empty.");
		}
		if (null == privacyPolicy.getRequestor() || null == privacyPolicy.getRequestor().getRequestorId()) {
			throw new PrivacyException("Not enought information to update a privacy policy. Requestor needed.");
		}

		// -- Add
		return privacyPolicyManagerRemote.updatePrivacyPolicy(clientPackage, privacyPolicy);
	}
	public RequestPolicy updatePrivacyPolicy(String clientPackage, String privacyPolicyXml, RequestorBean requestor) throws PrivacyException {
		// -- Verify
		if (null == clientPackage || "".equals(clientPackage)) {
			throw new PrivacyException(new MissingClientPackageException());
		}
		if (null == privacyPolicyXml || "".equals(privacyPolicyXml)) {
			throw new PrivacyException("The XML privacy policy to update is empty.");
		}
		// Retrieve the privacy policy
		RequestPolicy privacyPolicy = fromXmlString(privacyPolicyXml);
		if (null == privacyPolicy) {
			throw new PrivacyException("The XML formatted string of the privacy policy can not be parsed as a privacy policy.");
		}
		// Fill the requestor id
		privacyPolicy.setRequestor(requestor);
		// Create / Store it
		return updatePrivacyPolicy(clientPackage, privacyPolicy);
	}

	public boolean deletePrivacyPolicy(String clientPackage, RequestorBean requestor) throws PrivacyException {
		// -- Verify
		if (null == clientPackage || "".equals(clientPackage)) {
			throw new PrivacyException(new MissingClientPackageException());
		}
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}

		// -- Delete
		return privacyPolicyManagerRemote.deletePrivacyPolicy(clientPackage, requestor);
	}

	public RequestPolicy inferPrivacyPolicy(String clientPackage, int privacyPolicyType, Map configuration) throws PrivacyException {
		// -- Verify
		if (null == clientPackage || "".equals(clientPackage)) {
			throw new PrivacyException(new MissingClientPackageException());
		}
		List<RequestItem> requests = new ArrayList<RequestItem>();
		RequestPolicy privacyPolicy = new RequestPolicy();
		privacyPolicy.setRequestItems(requests);
		return privacyPolicy;
	}

	public String toXmlString(RequestPolicy privacyPolicy) {
		String encoding = "UTF-8";
		// -- Empty Privacy Policy
		if (null == privacyPolicy) {
			return "<?xml version=\"1.0\" encoding=\""+encoding+"\"?><RequestPolicy></RequestPolicy>";
		}

		Serializer serializer = new Persister(); 
		Writer result = new StringWriter();
		try {
			serializer.write(privacyPolicy, result);
		} catch (Exception e) {
			Log.e(TAG, "Can't serialize this privacy policy to an XML string.", e);
			return null;
		}
		return result.toString();
	}

	public RequestPolicy fromXmlString(String privacyPolicy) throws PrivacyException {
		// -- Verify
		// Empty privacy policy
		if (null == privacyPolicy || privacyPolicy.equals("")) {
			Log.d(TAG, "Empty privacy policy. Return a null java object.");
			return null;
		}
		// Fill XML header if necessary
		String encoding = "UTF-8";
		if (!privacyPolicy.startsWith("<?xml")) {
			privacyPolicy = "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\n"+privacyPolicy;
		}
		// If only contains the XML header: empty privacy policy
		if (privacyPolicy.endsWith("?>")) {
			Log.d(TAG, "Empty privacy policy. Return a null java object.");
			return null;
		}

		// -- Convert Xml to Java
		RequestPolicy result = null;
		Serializer serializer = new Persister();       
		Reader reader = new StringReader(privacyPolicy);
		try {
			result = serializer.read(RequestPolicy.class, reader, false);
		} catch (Exception e) {
			Log.e(TAG, "[Error fromXMLString] Can't parse the privacy policy.", e);
		}
		return result;
	}
}
