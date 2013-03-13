/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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

package org.societies.api.personalisation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;



/**
 * This interface is used to represent a user action. 
 * @author Elizabeth
 *
 */
@SocietiesExternalInterface(type=SocietiesInterfaceType.REQUIRED)
public interface IAction extends Serializable{
	/**
	 * 
	 * @return the value of this action (i.e. if the action is volume then the value would be an int from 0 to 100
	 */
	public String getvalue();
	
	/**
	 * 
	 * @return the name of the action (i.e. volume)
	 */
	public String getparameterName();
	
	/**
	 * 
	 * @return any other names this action might also be called
	 */
	public ArrayList<String> getparameterNames();
	
	/**
	 * @return the identifier of the service to which this action is applied to
	 */
	public ServiceResourceIdentifier getServiceID();
	
	/**
	 * 
	 * @return the type of service this action can be applied to
	 */
	public String getServiceType();
	
	/**
	 * 
	 * @return a list of alternative types of service this action can be applied to.
	 */
	public List<String> getServiceTypes();
	
	/**
	 * @param id	the identifier of the service this action is applied to
	 */
	public void setServiceID(ServiceResourceIdentifier id);
	
	/**
	 * 
	 * @param type 	the type of service this action is applied to
	 */
	public void setServiceType(String type);
	
	/**
	 * 
	 * @param types		a list of alternative types this action can be applied to
	 */
	public void setServiceTypes(List<String> types);
	

	/**
	 * Indicates if this action can be implemented or is only used as a conditional action for 
	 * triggering User Intent sequences. These types of actions are created by the UAM and represent 
	 * actions such as joined/left CIS, started/stopped service etc. Other such functions of the 
	 * platform may be added in the future
	 * @return	true if the action can be implemented, false if not. 
	 */
	public boolean isImplementable();
	
	/**
	 * Indicates whether this action should be implemented proactively (by the DecisionMaker) or not. 
	 * The user will have the ability to make this change manually using the preference GUI (T6.5 webapp Profile Settings).
	 * 3p services can also indicate if this action should be implemented when they create actions and send them to 
	 * the UAM component.  
	 * @return	true if the action should be proactively implemented, false if not. 
	 */
	public boolean isProactive();
	
	/**
	 * Indicates whether this action should be learnt or stored as a static preference. 3p services can create 
	 * static preferences (such as configuration settings) that do not depend on changing context. The UAM makes sure not to 
	 * store this action in the context history and avoid learning on this.  
	 * @return	true if the action should be context dependent, false if it shouldn't. Note that this will not return false 
	 * if the action does not currently have context conditions attached but was created as contextDependent 
	 */
	public boolean isContextDependent();
	
	
	
}

