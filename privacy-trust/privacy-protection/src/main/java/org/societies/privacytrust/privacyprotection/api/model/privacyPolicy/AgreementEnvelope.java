/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * This class is used after the negotiation succeeds to seal the contract between the negotiating parties. 
 * The envelope contains the negotiation agreement in encrypted format, the user's signature and the public 
 * key with which the provider is able to decrypt the agreement. The AgreementEnvelope object is prepared by
 *  the user's negotiation agent and sent to the provider's negotiation agent.
 * @author Elizabeth
 *
 */
public class AgreementEnvelope implements IAgreementEnvelope, Serializable{

	private IAgreement agreement;
	private byte[] signature;
	private Checksum agreementCheckSum;
	private byte[] publicKey;
	
	AgreementEnvelope(){
		
	}
	public AgreementEnvelope(IAgreement agreement, byte[] publickey, byte[] signature){
		this.agreement = agreement;
		this.signature = signature;
		this.publicKey = publickey;
		this.calculateChecksum();
	}
	
	
	private void calculateChecksum(){
		agreementCheckSum = new CRC32();
		byte[] byteArray;
		try {
			byteArray = getBytes(agreement);
			agreementCheckSum.update(byteArray, 0, byteArray.length);
			//long checksum = checksumEngine.getValue();
			//System.out.println("Checksum: "+checksum);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	  private byte[] getBytes(Object obj) throws java.io.IOException{
	      ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	      ObjectOutputStream oos = new ObjectOutputStream(bos); 
	      oos.writeObject(obj);
	      oos.flush(); 
	      oos.close(); 
	      bos.close();
	      byte [] data = bos.toByteArray();
	      return data;
	  }

	
	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.negotiation.api.platform.IAgreementEnvelope#getAgreement()
	 */
	@Override
	public IAgreement getAgreement() {
		return this.agreement;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.negotiation.api.platform.IAgreementEnvelope#getChecksum()
	 */
	@Override
	public Checksum getChecksum() {
		return this.agreementCheckSum;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.negotiation.api.platform.IAgreementEnvelope#getSignature()
	 */
	@Override
	public byte[] getSignature() {
		return this.signature;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.negotiation.api.platform.IAgreementEnvelope#getPublicKey()
	 */
	@Override
	public Key getPublicKey() {
		return (Key) this.getObject(publicKey);
	}

	public byte[] getPublicKeyInBytes(){
		return this.publicKey;
	}
	public String getPublicKeyAsString(){
		return this.getObject(publicKey).toString();
	}
	  public Object getObject(byte[] bytes){
		  ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		  try {
			ObjectInputStream ois = new ObjectInputStream(bis);
			return ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	  }
}
