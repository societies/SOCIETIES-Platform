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
	
	//public List<IActivity> getActivities(String timePeriod);
	//@Deprecated
	//public List<IActivity> getActivities(String CssId, String query, String timePeriod);
	/**
	 * This method will parse a query and a timeperiod and return a subset of the actitvies
	 *  in this activityfeed that matches the query constraints and is within the given timeperiod
	 *  
	 * @param {@link String} is defined as such (as per {@link http://opensocial-resources.googlecode.com/svn/spec/2.0.1/Core-API-Server.xml#Request-Parameter-FilterBy-Value} but with one addition):
	 * The Query String should be a JSON String that is structured as follows:
     * { ... "filterBy" : "name", "filterOp" : "startsWith", "filterValue" : "John" ... }@
	 * Thus it needs to contain the keys (and their corresponding values) "filterBy", "filterOp" and "filterValue" (the last one can have a empty value given certain filterOps, see below)
	 * 
     * Filter operators:
     * 
     * The operation to use when filtering a collection by a field specified in 'filterBy', defaults to "contains". Valid values:
     * contains Return elements where filterValue appears somewhere in the element's filterBy field value.
     * equals Return elements where filterValue exactly matches the element's filterBy field value.
     * startsWith Return elements where filterValue exactly matches the first N characters of the element's filterBy field value, where N is the length of the filterValue string.
     * present Return elements where the element's filterBy field value is not empty or null.
     * isNull The exact opposite of "present", NOTE: this is in addition to the opensocial specification.

	 * The last two filterOperators does not need to have a value for the "filterValue" values.
	 * @param {@link String} timeperiod can be: "millisecondssinceepoch millisecondssinceepoch+n" 
	 * @return a @List of {@link IActivity} 
	 * or a empty list if the parameters are wrong or the query and/or timeperiod did not match any activties
	 */
	//public List<IActivity> getActivities(String query, String timePeriod);
	/**
	 * This method will add a activity and post it on the associated pubsub service.
	 *  
	 * @param {@link IActivity} activity, the activity that will be added.
	 */
//	public void addCisActivity(IActivity activity);
	/**
	 * This method will parse a criteria and delete the activities that match the criteria
	 *  
	 * @param {@link String} criteria which has the same definition as the query of "getActivities(String query.."
	 * @return {@link int} number of deleted activities.
	 */
	//public int cleanupFeed(String criteria);
	/**
	 * 
	 * 
	 * @param {@link IAcitivty} activity the activity that should be deleted.
	 * @return {@link boolean} true if the the activity was found and deleted, false if not.
	 */
	//public boolean deleteActivity(IActivity activity);
	/**
	 * 
	 * @param List of ActivityEntry as used by the social data connector, and implemented by shindig.
	 * @return long number of entries successfully imported
	 */
	public long importActivtyEntries(List<?> activityEntries);
	
	
	public void getActivities(String timePeriod, IActivityFeedCallback c);
	public void getActivities(String query,
			String timePeriod, IActivityFeedCallback c) ;

	public void addActivity(IActivity activity,IActivityFeedCallback c) ;

	public void cleanupFeed(String criteria,IActivityFeedCallback c);
	public void deleteActivity(IActivity activity,IActivityFeedCallback c);
	
	public IActivity getEmptyIActivity();
	
}
