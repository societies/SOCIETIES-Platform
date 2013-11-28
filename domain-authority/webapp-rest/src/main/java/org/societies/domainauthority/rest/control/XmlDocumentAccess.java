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
package org.societies.domainauthority.rest.control;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.signature.XMLSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.security.digsig.XmlSignature;
import org.societies.api.security.digsig.DigsigException;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.api.security.xml.Xml;
import org.societies.api.security.xml.XmlException;
import org.societies.domainauthority.rest.dao.DocumentDao;
import org.societies.domainauthority.rest.model.Document;
import org.societies.domainauthority.rest.util.RemoteNotification;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class XmlDocumentAccess {

	private static Logger LOG = LoggerFactory.getLogger(XmlDocumentAccess.class);
	
	private static ISignatureMgr sigMgr;
	private static DocumentDao documentDao;

	public XmlDocumentAccess() {
		
		LOG.info("Constructor");
	}

	public void init() {

		LOG.debug("init()");
	}

	public static DocumentDao getDocumentDao() {
		return documentDao;
	}
	public void setDocumentDao(DocumentDao documentDao) {
		XmlDocumentAccess.documentDao = documentDao;
	}

	public static ISignatureMgr getSigMgr() {
		return sigMgr;
	}
	public void setSigMgr(ISignatureMgr sigMgr) {
		LOG.info("setSigMgr()");
		XmlDocumentAccess.sigMgr = sigMgr;
	}
	
	public static boolean isAuthorized(String filePath, String signature) {
		
		LOG.debug("isAuthorized({}, {})", filePath, signature);
		
		if (filePath == null || signature == null) {
			return false;
		}
		
		Document document = documentDao.get(filePath);
		if (document != null) {
			LOG.debug("isAuthorized(): file {} found", filePath);
			byte[] certBytes = document.getOwnerCertSerialized();
			X509Certificate cert;
			try {
				cert = sigMgr.ba2cert(certBytes);
			} catch (DigsigException e) {
				LOG.warn("Could not reconstruct certificate for file {} from {}", filePath, certBytes);
				return false;
			}
			PublicKey publicKey = cert.getPublicKey();
			return sigMgr.verify(filePath, signature, publicKey);
		}
		else {
			LOG.debug("isAuthorized(): file {} NOT found", filePath);
			return false;
		}
	}
	
	public static void addDocument(String path, String certStr, byte[] xml, String notificationEndpoint,
			int minNumSigners) throws DigsigException {
		
		X509Certificate cert = sigMgr.str2cert(certStr);
		Document doc = new Document(path, cert, xml, notificationEndpoint, minNumSigners);
		
		documentDao.save(doc);
	}
	
	/**
	 * Verifies given signature to check if the merge is authorized, then merges given XML document into existing one,
	 * i.e., appends the signatures from given document to existing document with same path identifier.
	 * 
	 * @param path document identifier
	 * @param xml document contents
	 * @param signature signature of path by the original uploader of document
	 * @return True for success, false for error
	 * @throws DigsigException
	 */
	public static boolean mergeDocument(String path, InputStream xml, String signature) throws DigsigException {
		
		Document doc = documentDao.get(path);
		if (doc == null) {
			LOG.warn("Could not retrieve document {} from database. Aborting merge.", path);
			return false;
		}
		byte[] certBytes = doc.getOwnerCertSerialized();
		X509Certificate cert = sigMgr.ba2cert(certBytes);

		if (sigMgr.verify(path, signature, cert.getPublicKey())) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int numInsertedNodes = merge(doc.getXmlDoc(), xml, os);
			if (numInsertedNodes > 0) {
				doc.setXmlDoc(os.toByteArray());
				// Regardless of number of signatures, it is assumed they are from one subject.
				// CN from the certificate should be checked if this is not the case.
				doc.setNumSigners(doc.getNumSigners() + 1);
				documentDao.update(doc);
				LOG.info("XML document merged and stored successfully");
				if (doc.getNumSigners() >= doc.getMinNumSigners()) {
					RemoteNotification.notifyOriginalUploader(doc);
				}
			}
			return true;
		}
		else {
			LOG.warn("Invalid signature. Merging aborted.");
			throw new DigsigException("Invalid signature. Merging aborted.");
		}
	}
	
	private static int merge(byte[] oldXml, InputStream newXml, OutputStream result) throws DigsigException {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();  // TODO: use pipe or something instead
		
		try {
			Xml old = new Xml(new ByteArrayInputStream(oldXml));
			int numInsertedNodes = old.addNodeRecursively(newXml, XmlSignature.XML_SIGNATURE_XPATH);
			LOG.debug("merge: inserted {} new nodes", numInsertedNodes);
			old.toOutputStream(bos);
			InputStream is = new ByteArrayInputStream(bos.toByteArray());
			LOG.debug("merge: verifying signatures");
			int numValidSigs = sigMgr.verifyXml(is).size();
			LOG.debug("merge: number of valid signatures in merged document: {}", numValidSigs);
			old.toOutputStream(result);
			return numInsertedNodes;
		} catch (XmlException e) {
			throw new DigsigException(e);
		}
	}

	/**
	 * Extracts all detached signatures under the document element
	 * 
	 * @return List of signatures in the document
	 * @throws DigsigException
	 */
	public static List<XMLSignature> extractSignatures(org.w3c.dom.Document doc) throws DigsigException {

		if (doc == null || doc.getDocumentElement() == null)
			throw new DigsigException("doc or doc.getDocumentElement() is null");

		NodeList childNodes = doc.getDocumentElement().getChildNodes();
		if (childNodes == null || childNodes.getLength() == 0)
			throw new DigsigException("no child nodes found");

		List<XMLSignature> signatures = new ArrayList<XMLSignature>();
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (!(childNode instanceof Element)) {
				continue;
			}

			Element elem = (Element) childNode;
			String elemName = elem.getLocalName();
			String elemNS = elem.getNamespaceURI();
			if ("http://www.w3.org/2000/09/xmldsig#".equals(elemNS) && "Signature".equals(elemName)) {
				try {
					XMLSignature sig = new XMLSignature(elem, null);
					signatures.add(sig);
					LOG.debug("extractSignatures(): extracted signature {}",
							sig.getElement().getAttribute("Id"));
				} catch (Exception e) {
					throw new DigsigException(e, "could not extract signature");
				}
			}
		}
		return signatures;
	}

	/**
	 * Extracts all detached signatures under the document element
	 * 
	 * @return List of signatures in the document
	 * @throws DigsigException
	 */
	public static List<XMLSignature> extractSignatures(byte[] docBytes) throws DigsigException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		org.w3c.dom.Document doc;
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(new ByteArrayInputStream(docBytes));
		} catch (Exception e) {
			throw new DigsigException(e);
		}
		return extractSignatures(doc);
	}

}
