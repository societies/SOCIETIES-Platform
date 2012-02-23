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
package org.societies.personalisation.CRIST.api.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.societies.api.internal.personalisation.model.IOutcome;

public interface ICRISTCommunitySituation extends IOutcome{
	/**
	 * This method will link the given ICRISTCommunityAction list to the current
	 * situation along with the corresponding transition probabilities
	 * 
	 * @param communityActions
	 */
	public void addActions(
			HashMap<ICRISTCommunityAction, Double> communityActions);

	/**
	 * This method will link the given ICRISTCommunityTask list to the current
	 * situation along with the corresponding transition probabilities
	 * 
	 * @param communityTasks
	 */
	public void addTasks(
			HashMap<ICRISTCommunityTask, Double> communityTasks);

	/**
	 * This method will return the related actions of the current situation
	 * 
	 * @return
	 */
	public HashMap<ICRISTCommunityAction, Double> getSituatioinActions();

	/**
	 * This method will return the related context information of the current
	 * situation
	 * 
	 * @return
	 */
	public Map<String, Serializable> getSituationContext();

	/**
	 * This method will return the ID of the current situation
	 * 
	 * @return
	 */
	public String getSituationID();

	/**
	 * This method will return the related tasks of the current situation
	 * 
	 * @return
	 */
	public HashMap<ICRISTCommunityTask, Double> getSituationTasks();

	/**
	 * This method will assign the current situation context with the given
	 * situationContext
	 * 
	 * @param situationContext
	 */
	public void setSituationContext(Map<String, Serializable> situationContext);

	/**
	 * This method will return the contents of this task in a String mode
	 */
	public String toString();
}