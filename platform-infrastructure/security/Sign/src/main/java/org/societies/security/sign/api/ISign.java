package org.societies.security.sign.api;

/**
 * Methods to digitally sign given data and methods to verify given signatures.
 * 
 * @author Mitja Vardjan
 *
 */
public interface ISign {

	/**
	 * Digitally sign given XML data and embed the signature in the given XML.
	 * 
	 * @param xml The XML String to be signed.
	 * 
	 * @param xml The identity to be used for signature.
	 * 
	 * @return XML with embedded signature.
	 */
	public String signXml(String xml, String id);
	
	/**
	 * Verify all digital signatures embedded in given XML. Verify also if the
	 * identities used are valid.
	 * 
	 * @param xml The XML containing embedded digital signatures to be verified.
	 * 
	 * @return True if all digital signatures and identities are valid.
	 * False otherwise or if no signatures found.
	 */
	public boolean verify(String xml);

}
