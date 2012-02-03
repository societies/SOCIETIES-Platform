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

package org.societies.api.internal.css.discovery;

import java.util.Collection;
import java.util.List;

/**
 * @author Perumal Kuppuudaiyar
 */
public interface ICssDiscovery {

	/**
	 * Description : This methods sets the domain ID of CSS
	 * @param CSSDomain object
	 * @return if success return true otherwise false
	 */
	public boolean setCssDiscoveryDomain(Object[] CSSDomain);
	/**
	 * Description : Search all the CSS which are online from central CSS registry
	 * @return collection of CSS objects
	 */
	public Collection<Object> searchAllCss();
	/**
	 * Description : Search all the CSS which are online from central CSS registry using CIS group filter
	 * @param cisGroup filter the CSS for this CIS Group
	 * @return collection CSS objects
	 */
	public Collection<Object> searchAllCss(Object[] cisGroup);
	/**
	 * Description : This methods registers for CSS join alert notification for one or more list of CIS group
	 * @param handler notification response handler
	 * @param cisGroupFilter list of CIS group registered for CSS join notification
	 * @return return true if registration message processed successfully, false otherwise
	 */
	public boolean registerForJoinCssAlert(Object handler,
			Object[] cisGroupFilter);
	/**
	 * Description : This methods registers for CSS left alert notification for one or more list of CIS group
	 * @param handler notification response handler
	 * @param cisGroupFilter list of CIS group registered for CSS left notification
	 * @return return true if registration message processed successfully, false otherwise
	 */
	public boolean registerForLeftCssAlert(Object handler,
			Object[] cisGroupFilter);
	/**
	 * Description: This methods unregisters for CSS join alert notification for one or more list of CIS group
	 * @param handler handler notification response handler
	 * @param cisGroupFilter list of CIS group unregistered for CSS join notification
	 * @return return true if registration message processed successfully, false otherwise
	 */
	public boolean unregisterJoinCssAlert(Object handler,
			Object[] cisGroupFilter);
	/**
	 * Description: This methods unregisters for CSS left alert notification for one or more list of CIS group
	 * @param handler handler notification response handler
	 * @param cisGroupFilter list of CIS group unregistered for CSS left notification
	 * @return return true if registration message processed successfully, false otherwise
	 */
	public boolean unregisterLeftCssAlert(Object handler,
			Object[] cisGroupFilter);
	/**
	 * Description : This method search the server for specific CSS using CSS id
	 * @param CSSID of queried CSS
	 * @return CSS object or null based on search result  
	 */
	public Object findCss(Object cssId);
	/**
	 * Description: This method check whether a particular CSS Exists
	 *  @param CSSID of queried CSS
	 * @return true or false based on search result  
	 */
	public boolean isCssExists(Object CssId);
	
	/**
	 * Description : This method returns list of CIS associated with a CSS Id
	 * @param CSSID of CSS to query for CIS membership list
	 * @return list of CIS identification object.
	 */
	public List<Object> getCISMembership(Object CssId);

}
