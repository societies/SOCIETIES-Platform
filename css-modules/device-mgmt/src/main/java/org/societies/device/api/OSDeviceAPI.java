package org.societies.device.api;

import org.societies.device.common.OSDetails;
import org.societies.device.common.OSStatistics;

/**
 * 
 * Basic interface for the OSDeviceAPI. 
 * Returns operating system details and statistics. 
 *
 */
public interface OSDeviceAPI {
	
	/*
	 * Description:		Returns the OS details 
	 * 				
	 * @return 			OSDetails	
	 */
	OSDetails getOSDetails();
	
	
	/*
	 * Description:		Returns OS Statistic (e.g. memory and CPU usage)
	 * 				
	 * @return 			OSStatistics	
	 */
	OSStatistics getOSStatistics(); 
}
