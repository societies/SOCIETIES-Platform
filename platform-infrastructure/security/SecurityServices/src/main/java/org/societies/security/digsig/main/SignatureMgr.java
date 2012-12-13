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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.societies.api.identity.IIdentity;
import org.societies.api.security.digsig.DigsigException;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.security.digsig.util.DOMHelper;
import org.societies.security.digsig.util.StreamUtil;
import org.societies.security.storage.CertStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Wrapper around {@link DigSig} and {@link XmlDSig}
 * 
 * @author Mitja Vardjan
 */
public class SignatureMgr implements ISignatureMgr {

	private static Logger LOG = LoggerFactory.getLogger(SignatureMgr.class);

	private DigSig digSig = new DigSig();
	private XmlDSig xmlDSig;
	private CertStorage certStorage;

	public SignatureMgr() {
	}
	
	public void init() throws DigsigException {
		
		LOG.info("SignatureMgr()");
		
		X509Certificate cert = certStorage.getOurCert();
		PrivateKey privateKey = certStorage.getOurKey();
		PublicKey publicKey = cert.getPublicKey();
		xmlDSig = new XmlDSig(cert, privateKey);
		 
		LOG.debug("Certificate: {}", cert);
		LOG.debug("Public key: {}", publicKey);
		LOG.debug("Private key: {}", privateKey);
		
		//test();
	}
	
	private void test() throws DigsigException {
		String xml = getResource("PrintService.xml");
		// 1st signature. By the provider, all options are signed.
		xml = xmlDSig.signXml(xml, "Container");
		// 2nd signature. By the requester, the selected option is signed.
		xml = xmlDSig.signXml(xml, "Standard Printing. Costs 0.1$ per A4");
		// 3rd signature. By the provider, requester's signature is signed.
		try {
			Document doc = DOMHelper.parseDocument(StreamUtil.str2stream(xml));
			String requesterSigId = xmlDSig.getRequesterSignatureId(doc);
			LOG.debug("requesterSigId = {}", requesterSigId);
			xml = xmlDSig.signXml(xml, requesterSigId);
		} catch (UnsupportedEncodingException e) {
			LOG.warn("", e);
		} catch (DigsigException e) {
			LOG.warn("", e);
		}
		writeFile("PrintService.signed.xml", xml);
	}
	
	private String getResource(String name) {

		InputStream resource = SignatureMgr.class.getClassLoader().getResourceAsStream(name);
		StringWriter writer = new StringWriter();

		try {
			IOUtils.copy(resource, writer, "UTF-8");
		} catch (IOException e) {
			LOG.error("getResource()", e);
			return null;
		}
		return writer.toString();
	}
	
	private void writeFile(String name, String contents) {

		OutputStream os;
		
		try {
			os = new FileOutputStream(name);
			os.write(contents.getBytes());
		} catch (Exception e) {
			LOG.error("writeFile()", e);
		}
	}
	
	public CertStorage getCertStorage() {
		return certStorage;
	}

	public void setCertStorage(CertStorage certStorage) {
		this.certStorage = certStorage;
	}
	
	@Override
	public String signXml(String xml, String xmlNodeId, IIdentity identity) throws DigsigException {
		
		ArrayList<String> ids = new ArrayList<String>();
		
		ids.add(xmlNodeId);
		
		return xmlDSig.signXml(xml, ids);
	}
	
	
	
	@Override
	public boolean verifyXml(String xml) {
		return xmlDSig.verifyXml(xml);
	}
	
	@Override
	public String sign(byte[] dataToSign, PrivateKey privateKey) throws DigsigException {
		return digSig.sign(dataToSign, privateKey);
	}
	
	@Override
	public String sign(byte[] dataToSign, IIdentity identity) throws DigsigException {
		return digSig.sign(dataToSign, getPrivateKey(identity));
	}
	
	@Override
	public boolean verify(byte[] data, String signature, PublicKey publicKey) {
		return digSig.verify(data, signature, publicKey);
	}
	
	@Override
	public boolean verify(byte[] data, String signature, IIdentity identity) {
		return digSig.verify(data, signature, getPublicKey(identity));
	}
	
	@Override
	public String sign(String dataToSign, PrivateKey privateKey) throws DigsigException {
		return digSig.sign(dataToSign, privateKey);
	}
	
	@Override
	public String sign(String dataToSign, IIdentity identity) throws DigsigException {
		return digSig.sign(dataToSign, getPrivateKey(identity));
	}
	
	@Override
	public boolean verify(String data, String signature, PublicKey publicKey) {
		return digSig.verify(data, signature, publicKey);
	}
	
	@Override
	public boolean verify(String data, String signature, IIdentity identity) {
		return digSig.verify(data, signature, getPublicKey(identity));
	}
	
	@Override
	public X509Certificate getCertificate(IIdentity identity) {
		// FIXME: return the correct result for the given identity
		LOG.warn("The IIdentity parameter is ignored in current implementation. Our own local and only certificate is used.");
		return certStorage.getOurCert();
	}
	
	@Override
	public PrivateKey getPrivateKey(IIdentity identity) {
		// FIXME: return the correct result for the given identity
		LOG.warn("The IIdentity parameter is ignored in current implementation. Our own local and only private key is used.");
		return certStorage.getOurKey();
	}
	
	private PublicKey getPublicKey(IIdentity identity) {
		// FIXME: return the correct result for the given identity
		LOG.warn("The IIdentity parameter is ignored in current implementation. Our own local and only public key is used.");
		
		X509Certificate cert = certStorage.getOurCert();
		
		if (cert == null) {
			LOG.warn("Certificate for {} not found", identity);
			return null;
		}
		return cert.getPublicKey();
	}
}
