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
package org.societies.security.digsig.main;

import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.security.digsig.DigsigException;
import org.societies.security.digsig.certs.SignatureCheck;
import org.societies.security.digsig.util.XmlManipulator;
import org.societies.security.storage.CertStorage;
import org.w3c.dom.Document;

/**
 * 
 * 
 * @author Mitja Vardjan, based on code by Miroslav Pavleski
 * 
 */
public class XmlDSig {

	private static Logger LOG = LoggerFactory.getLogger(XmlDSig.class);

	private CertStorage certStorage;

	public XmlDSig(CertStorage certStorage) {
		this.certStorage = certStorage;

		try {
			if (!Init.isInitialized()) {
				Init.init();
			}
		} catch (Exception e) {
			LOG.error("XmlDSig", "Failed to initialize", e);
			return;
		}

	}

	public String signXml(String xml, String xmlNodeId, IIdentity identity) {

		LOG.debug("signXml(..., {}, {})", xmlNodeId, identity);

		return xml; // FIXME
	}

	// TODO: move to PolicyNegotiator
	public String getRequesterSignatureId(Document doc) {

		SignatureCheck check = new SignatureCheck(doc, certStorage);
		String sigId = check.getCustomerSignature().getElement().getAttribute("Id");

		return sigId;
	}

	public Document signXml(Document doc, ArrayList<String> idsToSign) throws DigsigException {

		X509Certificate cert;
		Key key;
		XMLSignature sig;
		
		LOG.debug("signXml({}, {})", doc, idsToSign);
		
		cert = certStorage.getOurCert();
		key = certStorage.getOurKey();
		if (cert == null || key == null) {
			LOG.error("signXml: cert and key must not be null: cert = {}, key = {}", cert, key);
			throw new DigsigException("Cert or key is null");
		}

		try {
			sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_RSA);

			doc.getDocumentElement().appendChild(sig.getElement());

			Transforms transforms = new Transforms(doc);
			// Also must use c14n
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);

			for (String id : idsToSign) {
				LOG.debug("signXml(): adding URI of the resource to be signed: \"{}\"", id);
				sig.addDocument("#" + id, transforms,
						MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);
			}

			sig.addKeyInfo(cert);
			sig.sign(key);
			sig.getElement().setAttribute("Id",	"Signature-" + UUID.randomUUID().toString());
			return doc;
		} catch (Exception e) {
			LOG.error("signXml", "Failed while signing!", e);
			throw new DigsigException(e);
		}
	}

	public String signXml(String xml, ArrayList<String> idsToSign) throws DigsigException {
		
		Document doc;
		String str;
		XmlManipulator xmlManipulator = new XmlManipulator();
		
		xmlManipulator.load(xml);
		doc = xmlManipulator.getDocument();
		
		doc = signXml(doc, idsToSign);

		xmlManipulator = new XmlManipulator();
		xmlManipulator.setDocument(doc);
		str = xmlManipulator.getDocumentAsString();

		return str;
	}

	public boolean verifyXml(String xml) {
		LOG.debug("verifyXml()");
		return true; // FIXME
	}
}
