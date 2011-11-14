package org.societies.privacytrust.privacyprotection.dataobfuscation.wrapper;

import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper;

/**
 * Utilities to instantiate DataWrapper for data obfuscation
 * State: skeleton
 * @author olivierm
 * @date 14 oct. 2011
 */
public class DataWrapperFactory {
	// -- CONTEXT ATTRIBUTE
	/**
	 * To select automatically the relevant DataWrapper
	 * for this ContextAttribute
	 * 
	 * @param contextData
	 * @return A relevant DataWrapper
	 * @throw RuntimeException
	 */
	public static IDataWrapper selectDataWrapper(Object contextData) throws RuntimeException{
		try {
			if (contextData.equals("A Data Type")) {
//				return new [A Data Type]Wrapper(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("No Obfuscator to obfuscate that type of data");
		}
		throw new RuntimeException("No Obfuscator to obfuscate that type of data");
	}
	
//	// -- GEOLOCATION
//	/**
//	 * To get a Ge olocaltionDataWrapper
//	 * The persistence is disabled by default, the obfuscated geolocation will not
//	 * be stored after obfuscation.
//	 * @param latitude Latitude
//	 * @param longitude Longitude
//	 * @param accuracy Accuracy in meters
//	 * @return A GeolocationDataWrapper
//	 */
//	public static GeolocationDataWrapper getGeolocationDataWrapper(double latitude, double longitude, double accuracy) {
//		return new GeolocationDataWrapper(latitude, longitude, accuracy);
//	}
}
