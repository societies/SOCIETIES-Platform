package org.societies.device.common;


/**
 * Some operating system specific details 
 *
 */
public interface OSDetails {
	/*
	 * Description:		returns the OS name (e.g. Andriod / Linux)
	 * 				
	 * @return 			
	 */
	String getName();
	

	/*
	 * Description:		returns the OS version  (e.g. 2.3.6)
	 * 				
	 * @return 			
	 */
	String getVersion();

	/*
	 * Description:		returns specific build (e.g. GRK39F)
	 * 				
	 * @return 			
	 */	
	String getBuild();	
}
