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

package org.societies.orchestration.Cscw.api;


/**
 * This interface allows a user to manually or automatically select some friends 
 * or CIS members to follow more closely. The interface can suggest a group of
 * CSSs to follow closely. The user can set some parameters to guide the suggestions
 * and adjust the results. The interface does not do anything to actually follow the 
 * CSSs closely. It only supports the user to sellect who to follow. Other applications
 * or components have to actually do the following. 
 * 
 * @author Babak.Farshchian@sintef.no
 * @version 0.1
 *
 */
public interface IMemberVisibilityManager {
	/**
	 * The three values here are currently used to configure the scores that decide the prioritization.
	 * Initially they are all equal to 5. The value can be from 1 to 10, 10 being highest.
	 * TODO: make this list more generic in order to allow arbitrary number of weight parameters.
	 */
	public int interactionFrequency=5, topicRelevance=5, serviceDataRelevance=5;
	/**
	 * This method is the one that returns the list of CSSs to follow. It uses the
	 * values of the prioritization scores and creates a list of CISs.
	 * TODO: change string to some CSSRecord or other data type.
	 * 
	 * @param cssId ID of the CSS asking for recommendations.
	 * @return list of recommended CSS IDs in form of String array.
	 */
	public String[] getRecommendedPeopleList(String cssId);
	public String[] getRecommendedPeopleList(String cssId, String[] weights);
	public void setWeights(String[] weights);
	public void getWeights(String[] weights);
}
