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
