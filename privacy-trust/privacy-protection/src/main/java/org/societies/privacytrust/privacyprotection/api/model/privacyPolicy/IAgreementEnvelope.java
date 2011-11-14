package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;

import java.security.Key;
import java.util.zip.Checksum;

/**
 * @author Elizabeth
 *
 */
public interface IAgreementEnvelope {
	
	public IAgreement getAgreement();
	
	public Checksum getChecksum();
	
	public byte[] getSignature();
	
	public Key getPublicKey();

	public String getPublicKeyAsString();
	
	public byte[] getPublicKeyInBytes();
}
