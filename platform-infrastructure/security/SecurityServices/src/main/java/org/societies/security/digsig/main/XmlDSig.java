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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.security.digsig.certs.SignatureCheck;
import org.societies.security.storage.CertStorage;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @author Mitja Vardjan, based on code by Miroslav Pavleski
 * 
 */
public class XmlDSig {

	private static Logger LOG = LoggerFactory.getLogger(XmlDSig.class);

	private CertStorage certStorage;

	private DocumentBuilderFactory dbf;
	private DocumentBuilder docBuilder;
	private DOMImplementationImpl domImpl;
	private LSSerializer serializer;

	public XmlDSig(CertStorage certStorage) {
		this.certStorage = certStorage;

		try {
			if (!Init.isInitialized()) {
				Init.init();
			}

			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);

			docBuilder = dbf.newDocumentBuilder();

			domImpl = new DOMImplementationImpl();

			serializer = domImpl.createLSSerializer();
			DOMConfiguration config = serializer.getDomConfig();
			config.setParameter("comments", new Boolean(true));
		} catch (Exception e) {
			LOG.error("SignActivity", "Failed to initialize", e);
			return;
		}

	}

	public String signXml(String xml, String xmlNodeId, IIdentity identity) {

		LOG.debug("signXml(..., {}, {})", xmlNodeId, identity);

		return xml; // FIXME
	}

	public Document signXml(Document doc, String xmlNodeId, IIdentity identity) {

		LOG.debug("signXml(..., {}, {})", xmlNodeId, identity);
		SignatureCheck check = new SignatureCheck(doc, certStorage);

		try {
			XMLSignature finalSig = new XMLSignature(doc, null,
					XMLSignature.ALGO_ID_SIGNATURE_RSA);
			doc.getDocumentElement().appendChild(finalSig.getElement());

			Transforms transforms = new Transforms(doc);
			// Also must use c14n
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);

			String customerSigId = check.getCustomerSignature().getElement().getAttribute("Id");

			finalSig.addDocument("#" + customerSigId, transforms,
					MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);
			finalSig.addKeyInfo(certStorage.getOurCert());
			finalSig.sign(certStorage.getOurKey());
		} catch (Exception e) {
			LOG.warn("signXml()", e);
		}

		return doc;
	}

	/**
	 * Transform XML from byte[] to {@link Document}
	 * 
	 * @param xml
	 *            The XML in form of byte array
	 * @return XML {@link Document} or null on error
	 */
	private Document byteArray2doc(byte[] xml) {

		Document doc = null;

		try {
			doc = docBuilder.parse(new ByteArrayInputStream(xml));
		} catch (SAXException e) {
			LOG.warn("byteArray2doc(" + xml + ")", e);
		} catch (IOException e) {
			LOG.warn("byteArray2doc(" + xml + ")", e);
		}

		return doc;
	}

	/**
	 * Transform XML from {@link Document} to byte[]
	 * 
	 * @param xml
	 *            The XML in form of {@link Document}
	 * @return XML byte array or null on error
	 */
	private byte[] doc2byteArray(Document doc) {

		LSOutput domOutput = domImpl.createLSOutput();
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		domOutput.setByteStream(output);
		domOutput.setEncoding("UTF-8");
		serializer.write(doc, domOutput);

		return output.toByteArray();
	}

	public Document signXml(Document doc, ArrayList<String> idsToSign) {

		X509Certificate cert;
		Key key;
		XMLSignature sig;
		
		LOG.debug("signXml({}, {})", doc, idsToSign);
		
		cert = certStorage.getOurCert();
		key = certStorage.getOurKey();
		if (cert == null || key == null) {
			LOG.error("SignActivity", "Retrieved empty identity from storage!");
			return null;
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
			LOG.error("SignActivity", "Failed while signing!", e);
			return null;
		}
	}

	public boolean verifyXml(String xml) {
		LOG.debug("verifyXml()");
		return true; // FIXME
	}
}
