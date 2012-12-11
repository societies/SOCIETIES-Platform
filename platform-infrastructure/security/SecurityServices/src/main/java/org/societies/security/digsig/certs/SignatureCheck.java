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
package org.societies.security.digsig.certs;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.security.digsig.DigsigException;
import org.societies.security.digsig.util.XmlManipulator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Miroslav Pavleski, Mitja Vardjan
 */
public class SignatureCheck {

	private static Logger LOG = LoggerFactory.getLogger(SignatureCheck.class);
	
	private Document doc;
	private XmlManipulator xml;

	private List<XMLSignature> signatures = new ArrayList<XMLSignature>();
	private boolean signaturesExtracted = false;

	private XMLSignature customerSignature;
	private X509Certificate customerCert;
	private X509Certificate ourCert;

	public SignatureCheck(Document doc, X509Certificate ourCert) {

		LOG.debug("constructor()");
		
		this.doc = doc;
		this.ourCert = ourCert;

		xml = new XmlManipulator();
		xml.setDocument(doc);
	}

	/**
	 * Checks that the provider signature is valid
	 * 
	 * @return
	 * @throws DigsigException
	 */
	public void checkProviderSignature() throws DigsigException {
		Element sopElem = extractSOPElement();

		String sopElemId = sopElem.getAttribute("Id");

		if (sopElemId == null) {
			// <serviceOperaitonPolicy> element must have an Id
			throw new DigsigException("BAD_REQUEST");
		}
		String sopElemRef = "#" + sopElemId;

		// Make sure signatures were extracted
		extractSignatures();

		for (XMLSignature sig : signatures) {
			if (hasReference(sig, sopElemRef)) {

				try {
					KeyInfo keyInfo = sig.getKeyInfo();
					X509Certificate sigCert = keyInfo.getX509Certificate();
					if (sigCert == null) {
						throw new DigsigException("BAD_REQUEST");
					}

					if (sigCert.equals(ourCert)) {
						// This is a provider signature
						boolean result = sig.checkSignatureValue(sigCert
								.getPublicKey());
						if (!result)
							throw new DigsigException("BAD_REQUEST");
						else
							return;
					}
				} catch (DigsigException e) {
					throw e;
				} catch (XMLSignatureException e) {
					throw new DigsigException(e, "BAD_REQUEST");
				} catch (Exception e) {
					throw new DigsigException(e, "INTERNAL_SERVER_ERROR");
				}
			}
		}

		throw new DigsigException("BAD_REQUEST");
	}

	/**
	 * Verifies consumer identity, validates the signature and returns the
	 * selected SOP element
	 * 
	 * @return
	 * @throws DigsigException
	 */
	public Element getReferencedSOP() throws DigsigException {
		
		// make sure the signatures are extracted
		extractSignatures();

		// we'll need this in order to establish SOP correct structure
		Element sopElem = extractSOPElement();

		String sopElemId = sopElem.getAttribute("Id");

		if (sopElemId == null) {
			// <serviceOperaitonPolicy> element must have an Id
			throw new DigsigException("BAD_REQUEST");
		}

		String sopElemRef = "#" + sopElemId;

		for (XMLSignature sig : signatures) {
			if (!hasReference(sig, sopElemRef)) {
				// Signature must have only one reference referencing the
				// selected SOP
				String ref = extractSOPReference(sig);

				// The sig element must have and Id
				String sigId = sig.getElement().getAttribute("Id");
				if (sigId == null)
					throw new DigsigException("BAD_REQUEST");

				String refId = ref.substring(1);
				Node sopNode = xml.getNode(String.format(
						"/societies/serviceOperationPolicy/sop[@Id='%s']",
						refId));
				if (sopNode == null || !(sopNode instanceof Element)) {
					// check that this is actual SOP
					throw new DigsigException("BAD_REQUEST");
				}

				try {
					X509Certificate sigCert = sig.getKeyInfo()
							.getX509Certificate();
					if (sigCert == null)
						throw new DigsigException("BAD_REQUEST");

					// TODO check consumer identity...

					boolean result = sig.checkSignatureValue(sigCert
							.getPublicKey());
					customerSignature = sig;
					customerCert = sigCert;
					if (result)
						return (Element) sopNode;
				} catch (XMLSignatureException e) {
					throw new DigsigException(e, "BAD_REQUEST");
				} catch (Exception e) {
					throw new DigsigException(e, "INTERNAL_SERVER_ERROR");
				}
			}
		}

		throw new DigsigException("BAD_REQUEST");
	}

	public XMLSignature getCustomerSignature() throws DigsigException {
		if (customerSignature == null) {
			getReferencedSOP();
		}
		return customerSignature;
	}

	public X509Certificate getCustomerCert() {
		return customerCert;
	}

	/**
	 * Extracts all detached signatures under the document element
	 * 
	 * @return true if successful
	 * @throws DigsigException
	 */
	private void extractSignatures() throws DigsigException {
		if (signaturesExtracted)
			return;

		if (doc == null || doc.getDocumentElement() == null)
			throw new DigsigException("BAD_REQUEST");

		NodeList childNodes = doc.getDocumentElement().getChildNodes();
		if (childNodes == null || childNodes.getLength() == 0)
			throw new DigsigException("BAD_REQUEST");

		int numServiceOperationPolicyNodes = 0;

		// This loop ensures that all subelements to the document element are
		// either serviceOperationPolicy or ds:Signature
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (!(childNode instanceof Element))
				continue;

			Element elem = (Element) childNode;
			String elemName = elem.getLocalName();
			String elemNS = elem.getNamespaceURI();
			if ("http://www.w3.org/2000/09/xmldsig#".equals(elemNS)
					&& "Signature".equals(elemName)) {
				try {
					XMLSignature sig = new XMLSignature(elem, null);
					signatures.add(sig);
				} catch (Exception e) {
					throw new DigsigException(e, "BAD_REQUEST");
				}
			} else if ("serviceOperationPolicy".equals(elemName)
					&& elemNS == null) {
				numServiceOperationPolicyNodes++;
				if (numServiceOperationPolicyNodes > 1)
					throw new DigsigException("BAD_REQUEST");
			} else {
				throw new DigsigException("BAD_REQUEST");
			}
		}

		signaturesExtracted = true;
	}

	private Element extractSOPElement() throws DigsigException {
		Node node = xml.getNode("/societies/serviceOperationPolicy");
		if (node == null)
			throw new DigsigException("BAD_REQUEST");
		if (!(node instanceof Element))
			throw new DigsigException("BAD_REQUEST");

		return (Element) node;
	}

	/**
	 * Checks if given XML Digital Signature references an Id element
	 * 
	 * @param sig
	 * @param ref
	 * @return
	 */
	private static boolean hasReference(XMLSignature sig, String ref) {
		SignedInfo si = sig.getSignedInfo();
		if (si == null || si.getElement() == null)
			return false;

		// Loop through all reference nodes
		NodeList list = si.getElement().getChildNodes();
		if (list == null || list.getLength() == 0)
			return false;

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node == null || !(node instanceof Element))
				continue;
			Element elem = (Element) node;

			if ("http://www.w3.org/2000/09/xmldsig#".equals(elem
					.getNamespaceURI())
					&& "Reference".equals(elem.getLocalName())) {

				if (ref.equals(elem.getAttribute("URI")))
					return true;
			}

		}

		return false;
	}

	private static String extractSOPReference(XMLSignature sig) {
		SignedInfo si = sig.getSignedInfo();
		if (si == null || si.getElement() == null)
			return null;

		// Loop through all reference nodes
		NodeList list = si.getElement().getChildNodes();
		if (list == null) {
			// only one reference
			return null;
		}

		int refElements = 0;
		String result = null;

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node == null || !(node instanceof Element))
				continue;
			Element elem = (Element) node;

			if ("http://www.w3.org/2000/09/xmldsig#".equals(elem
					.getNamespaceURI())
					&& "Reference".equals(elem.getLocalName())) {

				// count elements
				refElements++;
				if (refElements > 1) {
					// if more than one it is an error
					return null;
				}
				result = elem.getAttribute("URI");
			}
		}

		return result;
	}
}
