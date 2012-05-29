package org.societies.android.privacytrust.datamanagement.accessor;





public class DAOException extends Exception {
	private static final long serialVersionUID = 5278061773292533932L;
	private static final String codeException = "DAO";
	
	/** 
	 * Crée une nouvelle instance de DAOException 
	 * @param addPrivacypermission Code de la commande ayant déclenchée l'exception 
	 * @param codeResponse Code de l'erreur 
	 */  
	public DAOException(String addPrivacypermission, String codeResponse) {  
		super(codeException+Constants.CODE_SEPARATOR+addPrivacypermission+Constants.CODE_SEPARATOR+codeResponse);  
	}  
	
	/** 
	 * Crée une nouvelle instance de DAOException 
	 * @param addPrivacypermission Code de la commande ayant déclenchée l'exception 
	 * @param codeResponse Code de l'erreur 
	 * @param throwable Cause
	 */  
	public DAOException(String addPrivacypermission, String codeResponse, Throwable throwable) {  
		super(codeException+Constants.CODE_SEPARATOR+addPrivacypermission+Constants.CODE_SEPARATOR+codeResponse, throwable);  
	}  
}
