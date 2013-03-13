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
package org.societies.integration.test.bit.ctx_personalisation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.personalisation.model.PersonalisablePreferenceIdentifier;
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
	String bgColour;
	String volume;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public HelloWorld(){
		bgColour = "";
		volume = "";
		myServiceID = new ServiceResourceIdentifier();
		myServiceID.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		try {
			myServiceID.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		log("Received action: \nParameter:"+action.getparameterName()+"\nValue: "+action.getvalue());
		if (action.getparameterName().equalsIgnoreCase("bgColour")){
			this.bgColour = action.getvalue();
			log("Personalised bgColour with: "+action.getvalue());
		}else if (action.getparameterName().equalsIgnoreCase("volume")){
			this.volume = action.getvalue();
			log("Personalised volume with: "+action.getvalue());
		}
		return true;
	}
	@Override
	public void setBackgroundColour(IIdentity userId, String colour) {
		IAction action = new Action(this.myServiceID, "HelloWorld", "bgColour", colour);
		log("sending action to uam: "+action.toString());
		this.uam.monitor(userId, action);
	}
	@Override
	public void setVolume(IIdentity userId, String volume) {
		
		IAction action = new Action(this.myServiceID, "HelloWorld", "volume", volume);
		log("sending action to uam: "+action.toString());
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
		return this.bgColour;
	}
	
	@Override
	public String getVolume(IIdentity userId) {
		return this.volume;
		
	}
	private void log(String msg){
		logging.debug(this.getClass().getName()+": "+msg);
	}
	@Override
	public List<PersonalisablePreferenceIdentifier> getPersonalisablePreferences() {
		return new ArrayList<PersonalisablePreferenceIdentifier>();
	}
}
