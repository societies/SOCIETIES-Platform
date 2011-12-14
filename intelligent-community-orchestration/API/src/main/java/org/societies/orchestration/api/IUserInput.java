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

package org.societies.orchestration.api;

import java.util.ArrayList;

/*
 * Descriptions taken from EA in the UserInterface component
 */

public interface IUserInput {
	
	/*
	 * Description: The configureCIS method invokes the configureCIS method 
	 * 				inside the Community Lifecycle Management component 
	 * 				in order to change parameters of a specific CIS.
	 * Parameters: 
	 * 				1) CIS - The CIS descriptor you want to change parameters.
	 * 				2) details - The details you want to change
	 * Returns:
	 * 				* True if the component was able to modify the CIS.
	 *				* False if the component was NOT able to modify the CIS. 
	 * Notes: Error messages must be handled by the User Interface component.
	 */
	
	public boolean configureCIS (Object CIS, Object details);
	
	/*
	 * Description: The createCISs method invokes the createCISs method 
	 * 				inside the Community Lifecycle Management component.
	 * Parameters:
	 * 				1) ArrayList<CIS> - the array list of CISs to create. 
	 * 									It can be one or more CISs at time.
	 * Returns:
	 * 				* The number of CISs that component was able to create. 
	 * 				  If the returned result is 0, no CISs were created.
	 * Notes: Error messages must be handled by the User Interface component.
	 */
	
	public boolean createCISs (ArrayList<Object> CISs);
	
	/*
	 * Description:	The deleteCISs method invokes the deleteCISs method 
	 * 				inside the Community Lifecycle Management component.
	 * Parameters:
	 * 				1) ArrayList<CIS> - the array list of CISs to destroy. 
	 * 									It can be one or more CISs at time.
	 * Returns:
	 * 				* The number of CISs deleted.
	 * Notes:
	 */
	
	public boolean deleteCISs (ArrayList<Object> CISs);
	
	/*
	 * Description: The getCISInfo method invokes the getCISInfo method 
	 * 				inside the Crowd Sourcing Manager component.
	 * Parameters:
	 * 				1) CIS - the CIS descriptor you want to get information.
	 * Returns:
	 * 				* A CIS Entity descriptor.
	 * Notes: Information must be rendered by the User Interface component.
	 */
	
	public Object getCISInfo (Object CIS);
	
	/*
	 * Description: The getMyCISs method returns a list of CISs 
	 * 				where the user is registered.
	 * Parameters:
	 * Returns:
	 * 				* An Array List of configured CISs.
	 * Notes:
	 */
	
	public ArrayList<Object> getMyCISs ();
	
	/*
	 * Description: The getReccomendedCISs method is used when a user wants to 
	 * 				get tips about new CISs based on user interests.
	 * Parameters:
	 * Returns:
	 * 				* An Array List of possible CISs.
	 * Notes: This API is triggered MANUALLY and not automatically.
	 */
	
	public ArrayList<Object> getRecommendedCISs ();
	
	/*
	 * Description: The getUserInfo method invokes the getUserInfo method 
	 * 				inside the Crowd Sourcing Manager component.
	 * Parameters:
	 * Returns:
	 * 				* A CIS Entity descriptor.
	 * Notes: Information must be rendered by the User Interface component.
	 */
	
	public Object getUserInfo (Object CSS);
	
	/*
	 * Description: The searchAvailableCISs represents the API you call 
	 * 				when you need to search on available CIS.
	 * Parameters:
	 * 				1) searchFilter - it consists into search string filters to use 
	 * 								  when searching inside the registry.
	 * Returns:
	 * 				* An Array List of  available CIS matching the criterias 
	 * 				  used in the search filter.
	 * Notes: Information must be rendered by the User Interface component.
	 */
	
	public ArrayList<Object> searchAvailableCISs (String filter);
	
	/*
	 * Description: InviteCSSJoinCIS represents the API you call 
	 * 				when you want to inform external CSS to join 
	 * 				a particular CIS (for example: a just created CIS).
	 * Parameters:
	 * 				1) CSSdescriptor - representing a list of CSSs you want to invite.
	 * 				2) CISdescriptor - representing the CIS you just created.
	 * Returns:
	 * Notes:
	 */
	
	public Object sendInvitations (ArrayList<Object> CSSnodes, Object CIS);
	
	/*
	 * Description: setStatus methods set the user status. 
	 * 				It delegates this function to the T4.5 components.
	 * Parameters:
	 * 				1) status - a string indicated the status or an enumeration of strings.
	 * Returns:
	 * 				* True if status was applied correctly.
	 * 				* False if status was not applied correctly.
	 * Notes:
	 */
	
	public boolean setCSSStatus (String status);
	
	/*
	 * Description: The setDeletedCISsNotification method sets the notifyDeletedCISs variable.
	 * Parameters:
	 * 				1) status - It enables or disables the notifier.
	 * Returns:
	 * 				* True if the variable is set to true.
	 * 				* False if the variable is set to false.
	 * Notes:
	 */
	
	public boolean setDeletedCISsNotification (boolean notifier);
	
	/*
	 * Description: The setIntervalTrigger sets the interval in milliseconds 
	 * 				that a scheduler must be called for triggering events.
	 * Parameters:
	 * 				1) interval - The interval of time before the events start. 
	 * 							  Interval is expressed in milliseconds.
	 * Returns:
	 * 				* True if the method was able to set the correct value.
	 * 				* False if the method was NOT able to set the correct value.
	 * Notes: A scheduler must be designed.
	 */
	
	public boolean setIntervalTrigger (long milliseconds);
	
	/*
	 * Description: GivePermissions represents the API you call 
	 * 				when you want to give permissions to a particular CSS.
	 * Parameters:
	 * 				1) CISdescriptor - representing the CIS you want to change permissions.
	 * 				2) PermissionsDescriptor - represents the permissions to 
	 * 										   set to a specific CIS.
	 * Returns:
	 * 				* True if permissions applied correctly.
	 * 				* False if something went wrong.
	 * Notes:
	 */
	
	public boolean setPermissions (Object CIS, Object permissions);
}
