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
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.security.digsig.XmlSignature;
import org.societies.api.security.digsig.DigsigException;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.api.security.xml.Xml;
import org.societies.api.security.xml.XmlException;
import org.societies.domainauthority.rest.dao.DocumentDao;
import org.societies.domainauthority.rest.model.Document;

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
	
	public static void addDocument(String path, String certStr, byte[] xml, String notificationEndpoint) throws DigsigException {
		
		X509Certificate cert = sigMgr.str2cert(certStr);
		Document doc = new Document(path, cert, xml, notificationEndpoint);
		
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
	public static boolean mergeDocument(String path, byte[] xml, String signature) throws DigsigException {
		
		Document doc = documentDao.get(path);
		if (doc == null) {
			LOG.warn("Could not retrieve document {} from database. Aborting merge.", path);
			return false;
		}
		byte[] certBytes = doc.getOwnerCertSerialized();
		X509Certificate cert = sigMgr.ba2cert(certBytes);

		if (sigMgr.verify(path, signature, cert.getPublicKey())) {
			byte[] merged = merge(doc.getXmlDoc(), xml);
			doc.setXmlDoc(merged);
			documentDao.update(doc);
			LOG.info("XML document merged and stored successfully");
			return true;
		}
		else {
			LOG.warn("Invalid signature. Merging aborted.");
			throw new DigsigException("Invalid signature. Merging aborted.");
		}
	}
	
	private static byte[] merge(byte[] oldXml, byte[] newXml) throws DigsigException {
		
		ByteArrayInputStream oldIs = new ByteArrayInputStream(oldXml);
		ByteArrayInputStream newIs = new ByteArrayInputStream(newXml);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Xml old;
		
		try {
			old = new Xml(oldIs);
			int numInsertedNodes = old.addNodeRecursively(newIs, XmlSignature.XML_SIGNATURE_XPATH);
			LOG.debug("merge: inserted {} new nodes", numInsertedNodes);
			old.toOutputStream(os);
			return os.toByteArray();
		} catch (XmlException e) {
			throw new DigsigException(e);
		}
	}
}
