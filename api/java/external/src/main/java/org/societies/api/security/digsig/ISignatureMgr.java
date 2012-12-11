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

package org.societies.api.security.digsig;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.societies.api.identity.IIdentity;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;
//import org.w3c.dom.Document;

/**
 * Methods to digitally sign given data and methods to verify given signatures.
 * 
 * @author Mitja Vardjan
 *
 */
@SocietiesExternalInterface(type=SocietiesInterfaceType.PROVIDED)
public interface ISignatureMgr {

	/**
	 * Digitally sign given XML data and embed the signature in the given XML.
	 * 
	 * @param xml The XML String to be signed.
	 * @param xmlNodeId Identifier of the XML node to sign (value of attribute "Id")
	 * @param identity The identity to be used for signature.
	 * @throws DigsigException on any error when parsing XML document, finding the
	 * reference to sign, error with private key, etc.
	 * 
	 * @return XML with embedded signature.
	 */
	public String signXml(String xml, String xmlNodeId, IIdentity identity) throws DigsigException;

	/**
	 * Digitally sign given XML data and embed the signature in the given XML.
	 * 
	 * @param xml The XML Document to be signed.
	 * @param xmlNodeId Identifier of the XML node to sign (value of attribute "Id")
	 * @param identity The identity to be used for signature.
	 * @throws DigsigException on any error when parsing XML document, finding the
	 * reference to sign, error with private key, etc.
	 * 
	 * @return XML with embedded signature.
	 */
	//public Document signXml(Document xml, String xmlNodeId, IIdentity identity) throws DigsigException;
	
	/**
	 * Verify all digital signatures embedded in given XML. Verify also if the
	 * identities used are valid.
	 * 
	 * @param xml The XML containing embedded digital signatures to be verified.
	 * 
	 * @return True if all digital signatures and identities are valid.
	 * False otherwise or if no signatures found.
	 */
	public boolean verifyXml(String xml);

	/**
	 * Digitally sign given data.
	 * 
	 * @param dataToSign The data to sign
	 * @param privateKey The private key to use for signing the data
	 * @return Hex encoded digital signature  
	 * @throws DigsigException If something is wrong with the given key, or
	 * (unlikely) the algorithm cannot process the given data
	 */
	public String sign(byte[] dataToSign, PrivateKey privateKey) throws DigsigException;

	/**
	 * Digitally sign given data.
	 * 
	 * @param dataToSign The data to sign
	 * @param identity The identity to use for signing the data
	 * @return Hex encoded digital signature  
	 * @throws DigsigException If something is wrong with the given identity,
	 * its key, or (unlikely) the algorithm cannot process the given data
	 */
	public String sign(byte[] dataToSign, IIdentity identity) throws DigsigException;
	
	/**
	 * Verify given digital signature against given data.
	 * 
	 * @param data The data that given signature is supposed to correspond to.
	 * @param signature The digital signature to verify
	 * @param publicKey The public key to use for verification
	 * @return True if signature is valid. False if signature or public key is invalid, or
	 * (unlikely) other error occurred.
	 */
	public boolean verify(byte[] data, String signature, PublicKey publicKey);

	/**
	 * Verify given digital signature against given data.
	 * 
	 * @param data The data that given signature is supposed to correspond to.
	 * @param signature The digital signature to verify
	 * @param identity The identity to use for verification
	 * @return True if signature is valid. False if signature, public key or
	 * identity is invalid, or (unlikely) other error occurred.
	 */
	public boolean verify(byte[] data, String signature, IIdentity identity);

	/**
	 * Digitally sign given data.
	 * 
	 * @param dataToSign The data to sign
	 * @param privateKey The private key to use for signing the data
	 * @return Hex encoded digital signature  
	 * @throws DigsigException If something is wrong with the given key, or
	 * (unlikely) the algorithm cannot process the given data
	 */
	public String sign(String dataToSign, PrivateKey privateKey) throws DigsigException;
	
	/**
	 * Digitally sign given data.
	 * 
	 * @param dataToSign The data to sign
	 * @param identity The identity to use for signing the data
	 * @return Hex encoded digital signature  
	 * @throws DigsigException If something is wrong with the given identity,
	 * its key, or (unlikely) the algorithm cannot process the given data
	 */
	public String sign(String dataToSign, IIdentity identity) throws DigsigException;

	/**
	 * Verify given digital signature against given data.
	 * 
	 * @param data The data that given signature is supposed to correspond to.
	 * @param signature The digital signature to verify
	 * @param publicKey The public key to use for verification
	 * @return True if signature is valid. False if signature or public key is invalid, or
	 * (unlikely) other error occurred.
	 */
	public boolean verify(String data, String signature, PublicKey publicKey);
	
	/**
	 * Verify given digital signature against given data.
	 * 
	 * @param data The data that given signature is supposed to correspond to.
	 * @param signature The digital signature to verify
	 * @param publicKey The identity to use for verification
	 * @return True if signature is valid. False if signature, public key or
	 * identity is invalid, or (unlikely) other error occurred.
	 */
	public boolean verify(String data, String signature, IIdentity identity);

	/**
	 * Gets digital certificate for the given identity.
	 * Any identity can be used, our own or identity of any other CSS or CIS.
	 * The certificate includes public key associated with the identity.
	 * 
	 * @param identity The identity to get certificate for
	 * 
	 * @return The certificate, or null if identity not found or no certificate
	 * is associated with the identity
	 */
	@Deprecated
	public X509Certificate getCertificate(IIdentity identity);
	
	/**
	 * Gets private key for the given identity.
	 * If the identity is not one of own identities (of this CSS), then null is returned.
	 * 
	 * @param identity The identity to get private key for
	 * 
	 * @return The private key
	 */
	@Deprecated
	public PrivateKey getPrivateKey(IIdentity identity);
}
