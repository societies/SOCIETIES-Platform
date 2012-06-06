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
package org.societies.api.activity;

import java.util.List;

import org.societies.api.activity.IActivity;
import org.societies.api.cis.management.ICisOwned;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * @author Babak.Farshchian@sintef.no
 *
 */
/**
 * MISSING_ANNOTATION
 * MISSING_JAVADOCS
 */
@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface IActivityFeed {
//	@Deprecated //cannot deprecate at the method signature matches the signature of getActivities(String query, String timePeriod);
//	public List<IActivity> getActivities(String CssId, String timePeriod);
	/**
	 * This method will parse a timeperiod and return a subset of the actitvies
	 *  in this activityfeed that is within the given timeperiod
	 *  
	 * @param {@link String} timeperiod can be: "millisecondssinceepoch millisecondssinceepoch+n" 
	 * @return a @List of {@link IActivity} 
	 * or a empty list if the parameters are wrong or the  timeperiod did not match any activties
	 */
	public List<IActivity> getActivities(String timePeriod);
	@Deprecated
	public List<IActivity> getActivities(String CssId, String query, String timePeriod);
	/**
	 * This method will parse a query and a timeperiod and return a subset of the actitvies
	 *  in this activityfeed that matches the query constraints and is within the given timeperiod
	 *  
	 * @param {@link String} query can be e.g. 'object,contains,"programming"'
	 * @param {@link String} timeperiod can be: "millisecondssinceepoch millisecondssinceepoch+n" 
	 * @return a @List of {@link IActivity} 
	 * or a empty list if the parameters are wrong or the query and/or timeperiod did not match any activties
	 */
	public List<IActivity> getActivities(String query, String timePeriod);
	/**
	 * This method will add a activity and post it on the associated pubsub service.
	 *  
	 * @param {@link IActivity} activity, the activity that will be added.
	 */
	public void addCisActivity(IActivity activity);
	/**
	 * This method will parse a criteria and delete the activities that match the criteria
	 *  
	 * @param {@link String} criteria TODO:define this
	 */
	public void cleanupFeed(String criteria);
}
