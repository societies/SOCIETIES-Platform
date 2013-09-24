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

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.security.digsig.DigsigException;
import org.societies.api.security.digsig.ISignatureMgr;
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

	/**
	 * Key = Relative path in local filesystem, same as Document.getPath()
	 */
	private static HashMap<String, Document> documents = new HashMap<String, Document>();

	private static ISignatureMgr sigMgr;
	private static DocumentDao documentDao;

	public XmlDocumentAccess() {
		
		LOG.info("Constructor");
	}

	public void init() {

		LOG.debug("init()");

		List<Document> documentList = documentDao.getAll();
		
		if (documentList != null) {
			LOG.debug("Loading document list from previous run");
			for (Document r : documentList) {
				documents.put(r.getPath(), r);
				LOG.debug("Loaded document [{}] {}", r.getId(), r.getPath());
			}
		}
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
		
		for (Document r : documents.values()) {
			if (r.getPath().equals(filePath)) {
				LOG.debug("isAuthorized(): file {} found", filePath);
				byte[] certBytes = r.getOwnerCertSerialized();
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
		}
		LOG.debug("isAuthorized(): file {} NOT found", filePath);
		return false;
	}
	
	public static void addDocument(String path, String certStr, byte[] xml, String notificationEndpoint) throws DigsigException {
		
		X509Certificate cert = sigMgr.str2cert(certStr);
		Document doc = new Document(path, cert, xml, notificationEndpoint);
		
		documents.put(doc.getPath(), doc);
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
			merge(doc.getXmlDoc(), xml);
			documentDao.update(doc);
			return true;
		}
		else {
			LOG.warn("Invalid signature. Merging aborted.");
			throw new DigsigException("Invalid signature. Merging aborted.");
		}
	}
	
	private static void merge(byte[] oldXml, byte[] newXml) throws DigsigException {
		// TODO: insert new signatures into old document
	}
}
