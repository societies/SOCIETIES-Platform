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

package org.societies.api.css.devicemgmt.display;

import java.io.File;
import java.net.URL;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public interface IDisplayDriver {

	/**
	 * This method should be called by any service that wants to be displayed in the public displays in HWU. The display portal can execute jars and exe files and 
	 * also load webpages. Therefore, if a service has a web interface that can be called using a browser, then the URL of the webpage should be
	 * provided.  
	 * 
	 * @param serviceID 	the serviceID of the service registering
	 * @param serviceName	the user-friendly name of the service registering 
	 * @param executableLocation	the location of the executable on a web server or the URL of the webpage
	 */
	public void registerDisplayableService(ServiceResourceIdentifier serviceID, String serviceName, URL executableLocation);
	
	
	/**
	 * Allows a service to send a message to the user through the public display. The message will be displayed only if the 
	 * user is near the screen and has permission to use it.  
	 * @param serviceName	the user-friendly name of the service 
	 * @param text			the text to be displayed. HTML is also allowed. For images use absolute URLs
	 */
	public void sendNotification(String serviceName, String text);
	
	/**
	 * Allows a service to display an image file on the public display. The image will be displayed only if the user is near 
	 * the screen and has permission to use it. 
	 * @param serviceName	the user-friendly name of the service
	 * @param pathToFile	the location of the file to send to be displayed (local)
	 */
	public void displayImage(String serviceName, String pathToFile);
	
	/**
	 * Allows a service to display an image file on the public display. The image will be displayed only if the user is near 
	 * the screen and has permission to use it. 
	 * @param serviceName	the user-friendly name of the service
	 * @param remoteImageLocation	the location of the file to send to be displayed (remote)
	 */
	public void displayImage(String serviceName, URL remoteImageLocation);
	
	
	
}
