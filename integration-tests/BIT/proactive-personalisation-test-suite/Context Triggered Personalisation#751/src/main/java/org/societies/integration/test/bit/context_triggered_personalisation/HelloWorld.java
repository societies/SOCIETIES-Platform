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
package org.societies.integration.test.bit.context_triggered_personalisation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.societies.api.identity.IIdentity;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class HelloWorld implements IHelloWorld, IActionConsumer{

	private ServiceResourceIdentifier myServiceID;
	private IUserActionMonitor uam; 
	Hashtable<IIdentity, String> colours;
	Hashtable<IIdentity, String> volumes;
	
	public HelloWorld(){
		colours = new Hashtable<IIdentity, String>();
		volumes = new Hashtable<IIdentity, String>();
		myServiceID = new ServiceResourceIdentifier();
		myServiceID.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
	}
	@Override
	public ServiceResourceIdentifier getServiceIdentifier() {
		return myServiceID;
	}

	@Override
	public String getServiceType() {
		return "HelloWorld";
	}

	@Override
	public List<String> getServiceTypes() {
		return new ArrayList<String>();
	}

	@Override
	public boolean setIAction(IIdentity userId, IAction action) {
		System.out.println("Received action: "+action.toString());
		if (action.getparameterName().equalsIgnoreCase("bgColour")){
			this.colours.put(userId, action.getvalue());
		}else if (action.getparameterName().equalsIgnoreCase("volume")){
			this.volumes.put(userId, action.getvalue());
		}
		return true;
	}
	@Override
	public void setBackgroundColour(IIdentity userId, String colour) {
		IAction action = new Action(this.myServiceID, "HelloWorld", "bgColour", colour);
		this.uam.monitor(userId, action);
	}
	@Override
	public void setVolume(IIdentity userId, String volume) {
		
		IAction action = new Action(this.myServiceID, "HelloWorld", "volume", volume);
		this.uam.monitor(userId, action);
	}
	/**
	 * @return the uam
	 */
	public IUserActionMonitor getUam() {
		return uam;
	}
	/**
	 * @param uam the uam to set
	 */
	public void setUam(IUserActionMonitor uam) {
		this.uam = uam;
	}
	@Override
	public String getBackgroundColour(IIdentity userId) {
		if (this.colours.containsKey(userId)){
			return this.colours.get(userId);
		}
		
		return "";
	}
	@Override
	public String getVolume(IIdentity userId) {
		if (this.volumes.containsKey(userId)){
			return this.volumes.get(userId);
		}
		
		return "";
		
	}

}
