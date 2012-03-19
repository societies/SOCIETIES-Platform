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
package org.societies.personalization.socialprofiler.service;

import java.util.List;

import org.societies.api.internal.sns.ISocialConnector;

public interface Engine {

	/**
	 * this function is used to update the existing network ; it adds 
	 * new users and  new relationships 
	 * groups 
	 * pages, 
	 * @param option
	 * 		permits to configure what will be updated
	 * 			UPDATE_ONLY_STREAM						=200;
				UPDATE_STREAM_AND_USER_INFORMATION		=300;
				UPDATE_STREAM_AND_FANPAGES_AND_GROUPS	=400;
				UPDATE_EVERYTHING						=500;
	 * 
	 */
	public void UpdateNetwork(int option);
	
	/**
	 * this function generates only a part of the network , a cluster , if the network is composed of only a big cluster 
	 * without other clusters or isolated networks then it generates the whole network
	 * 
	 * the method also generates for each facebook user , its groups and fanPages
	 * the necessary condition is that the users belong to the CA platform
	 *  
	 * @param current_id
	 * @param previous_id
	 * @param option
	 * 		option can be 1:FIRST TIME or 2:UPDATE ONLY
	 * 			if option is 1 then this function also generates info but no updates are done
	 * 			if option 2 , the function generates if necessary but also updates
	 */
	public void generate_tree(String current_id, String previous_id,int option);
	
	
	/**
	 * This function allow to fetch data from a new Social Network by the use a specific connector
	 * @param connector of the social network
	 */
	public void linkSocialNetwork(ISocialConnector connector);
	
	/**
	 * Remove the connection from a social network
	 * @param connector
	 */
	public void unlinkSocialNetwork(ISocialConnector connector);
	
	
	public List<ISocialConnector> getSNConnectors();

}
